package code;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
 * The super class of all stages. Contains all relevant register references and other fields
 * common to all stages.  Most importantly, this class contains accessMemory(), used to make
 * use of the MAR, MBR and system bus thread-safe. Thus, all code for interacting with the system
 * bus from both the F/D stage and Ex. stage is encapsulated within.
 */
public abstract class Stage implements Runnable {
	
	private BusController systemBus;
	
	private MemoryAddressRegister mar;
	private MemoryBufferRegister mbr;	
	
	private InstructionRegister ir;
	private ProgramCounter pc;
	private RegisterFile genRegisters;
	private Register rCC;

	private static Lock lock;
	
	private boolean active;
	private boolean pipelineFlush;
	private boolean isWaiting;
	

	public Stage(BusController systemBus, InstructionRegister ir, ProgramCounter pc, RegisterFile genRegisters,
			Register rCC, MemoryBufferRegister mbr, MemoryAddressRegister mar) {
		
		this.systemBus = systemBus;
		
		this.mar = mar;
		this.mbr = mbr;
		
		this.ir = ir;
		this.pc = pc;
		this.genRegisters = genRegisters;
		this.rCC = rCC;
		
		lock = new ReentrantLock();		
	}
	
	//Static getter for the lock.
	public static Lock getLock() {
		return lock;
	}
	
	
	/*
	 * Contains code to fetch instructions or operands from memory,
	 * protecting integrity of MAR and MBR registers during pipelined mode.
	 * 
	 * isInstructionFetch is used to signify if the operation is an instruction fetch, with
	 * the other boolean parameters being used in the same way. This ensures the correct block
	 * of code is excuted by the subclass using the method.
	 * 
	 * Load/Store code is always executed by SwingWorker thread, as this runs in the execute stage
	 * during pipelined mode and through all stages in standard mode. It spawns two additional threads
	 * in pipelined mode to operate in the F/D and WB stages.
	 */
	public synchronized boolean accessMemory(boolean isInstructionFetch, boolean isOperandLoad, boolean isOperandStore,
			boolean isPipelined) {
		
		//For activity monitor updates in pipelined mode.
		String operation = "";
		
		if (isPipelined) {		
			
			if (isInstructionFetch) {
				operation = "instruction fetch";
			}
			if (isOperandLoad) {
				operation = "LOAD";
			}
			if (isOperandStore) {
				operation = "STORE";

			}
			if (((ReentrantLock) lock).isLocked()) {
				System.out.println(getClass() + " lock is locked, should fire update.");
				fireUpdate("> Pipeline delay: Waiting to acquire use of MAR, MBR and \nsystem bus to complete " + 
						operation + " operation.\n" );
			}
		}
		
	
		//Attempt to get lock to allow progression into body of accessMemory() method	
		try {
			lock.lockInterruptibly();
			//Interrupt signals reset operation; cancel execution (note isWaiting is NOT set here, as the thread is not at a wait()).
		} catch (InterruptedException e1) { 
			//e1.printStackTrace();	
			active = false;
			return false;
		}


		if (isInstructionFetch) {
			//Instruction fetch code; accessed in pipelined mode by a separate thread running in the F/D stage,
			//therefore it is necessary to regularly poll for its interrupted status so that in the event that
			//"reset" is clicked on the GUI (triggering SwingWorker thread running in the execute stage to issue
			//an interrupt to the threads running in the F/D and WB stages), or in the event of a pipeline
			// flush (which is also signalled by an interrupt from the execute stage), this thread can return to 
			//its run() method and terminate naturally without executing further code.
			//Note the isInterrupted() polling blocks are not used when the wait() statements are active but are important
			//for testing when the wait() statements are not used.
			
			
			if (!isPipelined) {
				getIR().clear(); //Clear previous instruction from display, standard mode only
			}
			
			if (Thread.currentThread().isInterrupted()) { //In event of pipeline flush from execute stage
				return false;
			}
			
			System.out.println ("PC value is: " + getPC().getValue() + " before wait 1");
			
			//Additional wait for clarity in pipelined mode, as PC incremented at different point to pipelined mode
			if (isPipelined && pc.getValue() != 0) { //Don't wait on first instruction fetch (causes gap)
				setWaitStatus(true);
				try {
					wait();
				} catch (InterruptedException e) {
					//e.printStackTrace();
					active = false;
					setWaitStatus(false);
					return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
				}
				setWaitStatus(false);
			}
		
			
			getMAR().write(getPC().getValue()); //Write address value in PC to MAR.
			
			if (Thread.currentThread().isInterrupted()) { //In event of pipeline flush from execute stage
				return false;
			}
			
			fireUpdate("> Memory address from PC placed into MAR \n");
			
			setWaitStatus(true);
			try {
				wait();
			} catch (InterruptedException e) {
				//e.printStackTrace();
				active = false;
				setWaitStatus(false);
				return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
			}
			setWaitStatus(false);
			
			
			if (Thread.currentThread().isInterrupted()) { //In event of pipeline flush from execute stage
				return false;
			}
			
			//Transfer address from MAR to system bus, prompting read
			if (isPipelined) { //Caller only used for pipelined mode
				getSystemBus().setCaller(this); //Register object reference with system bus to enable updates to relevant 
				//activity monitor
			}
			boolean successfulTransfer = getSystemBus().transferToMemory(getMAR().read(), null); 
			getSystemBus().setCaller(null); //Reset caller reference when done to avoid interference for next caller.
			if (!successfulTransfer) { 
				//If SwingWorker is cancelled and thread of execution is interrupted, successfulTransfer will be false and the
				//method should not execute any further
				return false;
			}
 
			
			if (Thread.currentThread().isInterrupted()) { //In event of pipeline flush from execute stage
				return false;
			}
			
			this.fireUpdate("> Contents of memory address " + getMAR().read() + " loaded into MBR \n");
			
			
			setWaitStatus(true);
			try {
				wait();
			} catch (InterruptedException e) {
				//e.printStackTrace();
				active = false;
				setWaitStatus(false);
				return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
			}
			setWaitStatus(false);
			
			if (Thread.currentThread().isInterrupted()) { //In event of pipeline flush from execute stage
				return false;
			}
			
			
			
			//A Data item should now be in MBR
			getIR().loadIR((Instruction) getMBR().read()); //Cast required as mbr holds type data, IR type Instruction; cast exception
															//caught in CPUframe
			this.fireUpdate("> Contents of MBR loaded into IR \n");
			
			if (Thread.currentThread().isInterrupted()) { //In event of pipeline flush from execute stage
				return false;
			}
			
			
			if (!isPipelined) { //Incrementing PC is next step in standard mode (not pipelined mode), so a wait() is necessary
				setWaitStatus(true);
				try {
					wait();
				} catch (InterruptedException e) {
					//e.printStackTrace();
					active = false;
					setWaitStatus(false);
					return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
				}
				setWaitStatus(false);
			}
			
			getMAR().write(-1);//Reset MAR. Repositioned here for user clarity; mem. addr. remains in MAR until instr. in IR.		
			getMBR().write(null); //Clear MBR to reflect that instruction has moved to IR
		
		}
		
		
		/*
		 * LOAD instruction execution
		 */		
		else if (isOperandLoad) {
			
			fireUpdate("> Executing LOAD instruction; memory address " +	ir.read(1).getField1() + 
					"\nplaced into MAR to initiate operand fetch \n");
			mar.write(ir.read(1).getField1()); //Load mar with source address of instruction in IR
			//Request a read from memory via system bus after next wait(), with address contained in mar
			
			setWaitStatus(true);
			try {
				wait();
			} catch (InterruptedException e) {
				//e.printStackTrace();
				active = false;
				setWaitStatus(false);
				return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
			}
			setWaitStatus(false);
			
			
			if (isPipelined) { //Caller only used for pipelined mode
				getSystemBus().setCaller(this); //Register object reference with system bus to enable updates to relevant 
				//activity monitor
			}
			boolean successfulTransfer = getSystemBus().transferToMemory(mar.read(), null);
			getSystemBus().setCaller(null);
			if (!successfulTransfer) {
				active = false;
				return false;
			}
			this.fireUpdate("> Operand " + mbr.read().toString() + " loaded from address " + ir.read(1).getField1() + 
					" into MBR\n");
			
			
			setWaitStatus(true);
			try {
				wait();
			} catch (InterruptedException e) {
				//e.printStackTrace();
				active = false;
				setWaitStatus(false);
				return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
			}
			setWaitStatus(false);
			
			mar.write(-1); //Reset MAR
			
			
			if (ir.read(1).getField2() == 16) { //ConditionCodeRegister reference
				getCC().write((Operand) mbr.read()); //Write operand in getMBR() to condition/status register
				
				this.fireUpdate("> Loaded operand " + mbr.read() + " into condition code register\n");
				
				setWaitStatus(true);
				try {
					wait();
				} catch (InterruptedException e) {
				//	e.printStackTrace();
					active = false;
					setWaitStatus(false);
					return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
				}
				setWaitStatus(false);
			}
			
			
			else {
			
				//Transfer data in mbr to destination field in instruction in ir (field2).
				getGenRegisters().write(ir.read(1).getField2(), mbr.read());//getField2() gives reg. destination index, mbr.read()
					//gives the operand to be moved from mbr to genRegisters at index given in getField2().
				this.fireUpdate("> Operand " + mbr.read() + " loaded into r" + ir.read(1).getField2() + "\n");
				
				setWaitStatus(true);
				try {
					wait();
				} catch (InterruptedException e) {
					//e.printStackTrace();
					active = false;
					setWaitStatus(false);
					return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
				}
				setWaitStatus(false);
			}
			
			
			mbr.write(null); //Reset MBR
			
		}
		
		
		/*
		 * STORE instruction execution.
		 */
		else if (isOperandStore) {

						
			this.fireUpdate("> Executing STORE instruction; destination memory \naddress " + getIR().read(1).getField2() + 
				" placed into MAR \n");
			getMAR().write(getIR().read(1).getField2()); //Load mar with destination (memory address)
			
			setWaitStatus(true);
			try {
				wait();
			} catch (InterruptedException e) {
				//e.printStackTrace();
				active = false;
				setWaitStatus(false);
				return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
			}
			setWaitStatus(false);
			
			getMBR().write(getGenRegisters().read(getIR().read(1).getField1())); //Write to mbr the data held in 
			//getGenRegisters() at index given by field1(source) of instruction held in IR.
			this.fireUpdate("> Operand " + getGenRegisters().read(getIR().read(1).getField1()) + " loaded from r" + 
					getIR().read(1).getField1() + " into MBR\n");
			
			setWaitStatus(true);
			try {
				wait();
			} catch (InterruptedException e) {
			//	e.printStackTrace();
				active = false;
				setWaitStatus(false);
				return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
			}
			setWaitStatus(false);
			
			//Transfer contents of mbr to address specified in mar
			if (isPipelined) { //Caller only used for pipelined mode
				getSystemBus().setCaller(this); //Register object reference with system bus to enable updates to relevant 
				//activity monitor
			}
			boolean successfulTransfer = getSystemBus().transferToMemory(getMAR().read(), getMBR().read()); 
			getSystemBus().setCaller(null);
			if (!successfulTransfer) {
				active = false;
				return false;
			}
			
			this.fireUpdate("> Operand " + getGenRegisters().read(getIR().read(1).getField1()) + " stored in memory address " +
					getIR().read(1).getField2() + "\n");
			
			setWaitStatus(true);
			try {
				wait();
			} catch (InterruptedException e) {
			//	e.printStackTrace();
				active = false;
				setWaitStatus(false);
				return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
			}
			setWaitStatus(false);
		
		}
		
		lock.unlock(); //Release lock upon leaving accessMemory()
	
		return true;
	}
	
	
	public abstract void run();
	
	protected abstract void fireUpdate(String update);
	
	
	/*
	 * Denotes that the instruction cycle is active; is set to false upon a HALT,
	 * or if reset is clicked, cancelling execution.
	 * 
	 * @return boolean the status of the boolean active field.
	 */
	public boolean isActive() {
		return this.active;
	}
	
	
	/*
	 * Set the active field.
	 * 
	 * @param the value to be taken on by active.
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
	
	
	/*
	 * There must be a way to differentiate between interrupts generated by pipelining flushing
	 * and those generated by the user clicking reset; in the former case, execution must continue,
	 * with the fetch/decode stage simply resetting itself, in the latter case execution should terminate
	 * altogether.
	 * 
	 * @param boolean flush the value to be taken on by the pipelineFlush field.	 * 
	 */
	public void setPipelineFlush(boolean flush) {
		this.pipelineFlush = flush;
	}
	
	
	/*
	 * Is set to true in the event of a pipeline flush caused
	 * by execution of a branch instruction.
	 * 
	 * @return boolean the status of the pipelineFlush indicator field.
	 */
	public boolean isPipelineFlush() {
		return this.pipelineFlush;
	}
	
	
	
	/*
	 * This boolean flag allows the GUI's event dispatch thread to determine whether the SwingWorker
	 * thread is waiting on this object. If so, notify() will be called on this object (and not otherwise).
	 * 
	 * @return boolean the status of isWaiting.
	 */
	public boolean isWaiting() {
		return this.isWaiting;
	}
	
	
	/*
	 * This method is used when assembly program execution is reset/restarted midway through;
	 * if isWaiting has just been set to false and the worker thread is cancelled, when it comes to
	 * stepping through execution after the restart, the "Step" button won't work as it uses the status
	 * of isWaiting to determine whether to resume execution or not.
	 * 
	 * @param boolean waiting the value to be taken on by the isWaiting field.
	 */
	public void setWaitStatus(boolean waiting) {
		this.isWaiting = waiting;
	}
	
	
	/*
	 * The following are getter methods for Stage fields.
	 */
	
	public InstructionRegister getIR() {
		return this.ir;
	}
	
	public ProgramCounter getPC() {
		return this.pc;
	}
	
	public RegisterFile getGenRegisters() {
		return this.genRegisters;
	}
	
	public BusController getSystemBus() {
		return this.systemBus;
	}
	
	public MemoryAddressRegister getMAR() {
		return this.mar;
	}
	
	public MemoryBufferRegister getMBR() {
		return this.mbr;
	}
	
	public Register getCC() {
		return this.rCC;
	}

}
