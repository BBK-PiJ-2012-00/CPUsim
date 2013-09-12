package code;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public abstract class Stage implements Runnable {
	
	private BusController systemBus;
	
	private MemoryAddressRegister mar;
	private MemoryBufferRegister mbr;	
	
	private InstructionRegister ir;
	private ProgramCounter pc;
	private RegisterFile genRegisters;
	private Register statusRegister;

	private static Lock lock;
	
	private boolean active;
	private boolean pipelineFlush;
	private boolean isWaiting;
	

	public Stage(BusController systemBus, InstructionRegister ir, ProgramCounter pc, RegisterFile genRegisters,
			Register statusRegister, MemoryBufferRegister mbr, MemoryAddressRegister mar) {
		
		this.systemBus = systemBus;
		
		this.mar = mar;
		this.mbr = mbr;
		
		this.ir = ir;
		this.pc = pc;
		this.genRegisters = genRegisters;
		this.statusRegister = statusRegister;
		
		lock = new ReentrantLock();
		
	
		
	}
	
	public static Lock getLock() {
		return lock;
	}
	
	
	/*
	 * Contains code to fetch instructions or operands from memory,
	 * protecting integrity of MAR and MBR registers during pipelined mode.
	 * 
	 * isInstructionFetch is used to signify if the operation is an instruction fetch
	 * or operand fetch.
	 */
	public synchronized boolean accessMemory(boolean isInstructionFetch, boolean isOperandLoad, boolean isOperandStore,
			boolean isPipelined) {
		
		
		String operation = "";
		
		if (isPipelined) {
			
			
			if (isInstructionFetch) {
				operation = "instruction fetch";
				//stage = "F/D Stage";
			}
			if (isOperandLoad) {
				operation = "LOAD";
			//	stage = "Ex. Stage";
			}
			if (isOperandStore) {
				operation = "STORE";
			//	stage = "Ex. Stage";
			}
//			if (pc.getValue() != 0) { //Prevent display of update below on first instruction fetch, as no wait will take place
			if (((ReentrantLock) lock).isLocked()) {
				System.out.println(getClass() + " lock is locked, should fire update.");
				fireUpdate("> Pipeline delay: Waiting to acquire use of MAR, MBR and \nsystem bus to complete " + 
						operation + " operation.\n" );
			}
		}
		
	
		//Attempt to get lock to allow progression into body of accessMemory() method	
		try {
			lock.lockInterruptibly();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.println(getClass() + " Caught at lockInterruptibly.");
			//properLock.unlock();
			active = false;
			return false;
		}


		if (isInstructionFetch) {
			//Instruction fetch code; accessed in pipelined mode by a separate thread running in the F/D stage,
			//therefore it is necessary to regularly poll for its interrupted status so that in the event that
			//"reset" is clicked on the GUI (triggering SwingWorker thread running in the execute stage to issue
			//an interrupt to the threads running in the F/D stage and WB stage), or in the event of a pipeline
			// flush (which is also signalled by an interrupt from the execute stage), this thread can return to 
			//its run() method and terminate naturally without executing further code.
			
			
			if (!isPipelined) {
				getIR().clear(); //Clear previous instruction from display, standard mode only
			}
			
			if (Thread.currentThread().isInterrupted()) { //In event of pipeline flush from execute stage
				return false;
			}
			
			System.out.println ("PC value is: " + getPC().getValue() + " before wait 1");
			
			//Additional wait for clarity in pipelined mode, as PC incremented at different point (but don't wait on
			//first instruction fetch as this causes an initial GUI gap).
			if (isPipelined && pc.getValue() != 0) {
				setWaitStatus(true);
				try {
					System.out.println("wait 1");
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					active = false;
					setWaitStatus(false);
					return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
				}
				setWaitStatus(false);
			}
			System.out.println("left wait 1");
			
			//When branch is taken: FD is held at above wait while PC is set by branch, so when this then leaves the wait
			//and reads the PC, it's set to the new value when really we'd like it to be set to the old one to better
			//demonstrate the pipeline flush.... it then is interrupted during wait2, and starts again, effectively repeating
			//the placing mem addr from pc to mar.
			
			getMAR().write(getPC().getValue()); //Write address value in PC to MAR.
			
			if (Thread.currentThread().isInterrupted()) { //In event of pipeline flush from execute stage
				return false;
			}
			
			fireUpdate("> Memory address from PC placed into MAR \n");
			
			setWaitStatus(true);
			try {
				System.out.println("wait 2");
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
				active = false;
				setWaitStatus(false);
				return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
			}
			setWaitStatus(false);
			System.out.println("left wait 2");
			
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
				e.printStackTrace();
				active = false;
				setWaitStatus(false);
				return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
			}
			setWaitStatus(false);
			
			if (Thread.currentThread().isInterrupted()) { //In event of pipeline flush from execute stage
				System.out.println("Entering interrupted block 5");
				return false;
			}
			
			
			
			//System.out.println("Ln102, attempting to cast: " + getMBR().read());
			//A Data item should now be in MBR
			getIR().loadIR((Instruction) getMBR().read()); //Cast required as mbr holds type data, IR type Instruction; May need to handle exception
			System.out.println("Instruction: " + getMBR().read());
			this.fireUpdate("> Contents of MBR loaded into IR \n");
			
			if (Thread.currentThread().isInterrupted()) { //In event of pipeline flush from execute stage
				//System.out.println("Entering interrupted block 6");
//				if (pipelineFlush) {
//					fireUpdate("** PIPELINE FLUSH ** \nFetch/decode of " + ir.read(0).toString() + " abandoned.");
//				}
				return false;
			}
			
			
			if (!isPipelined) { //Incrementing PC is next step in standard mode (not pipeliend mode), so a wait() is necessary
				setWaitStatus(true);
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					active = false;
					setWaitStatus(false);
	//				if (pipelineFlush) {
	//					fireUpdate("** PIPELINE FLUSH ** \nFetch/decode of " + ir.read(0).toString() + " abandoned.");
	//				}
					return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
				}
				setWaitStatus(false);
			}
			
			getMAR().write(-1);//Reset MAR. Repositioned here for user clarity; mem. addr. remains in MAR until instr. in IR.		
			getMBR().write(null); //Clear MBR to reflect that instruction has moved to IR (should it be reset earlier, to 
			//better reflect movement?)
			
			//fireUpdate("Leaving Fetch block\n");
		}
		
		/*
		 * LOAD instruction execution
		 */		
		else if (isOperandLoad) {
			
			//fireUpdate("Into isOperandLoad.\n");
			//fireUpdate("Info: IR1 holding: " + ir.read(1).toString());
			//System.out.println("IN ACCESS MEMORY METHOD: LOAD operation");
			
			fireUpdate("> Executing LOAD instruction; memory address " +	ir.read(1).getField1() + 
					"\nplaced into MAR to initiate operand fetch \n");
			mar.write(ir.read(1).getField1()); //Load mar with source address of instruction in IR
			//Request a read from memory via system bus, with address contained in mar
			
			setWaitStatus(true);
			try {
				System.out.println("E: about to wait after loading MAR with source address of instruction.");
				wait();
				System.out.println("E: just left that wait.");
			} catch (InterruptedException e) {
				e.printStackTrace();
				active = false;
				setWaitStatus(false);
				//properLock.unlock();
				return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
			}
			setWaitStatus(false);
			
			System.out.println("Instruction is: " + ir.read(1).toString());
			System.out.println("Value on MAR at ln46:" + mar.read());
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
				e.printStackTrace();
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
					e.printStackTrace();
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
					e.printStackTrace();
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
			
			if (!active) { //Swing worker ignores cancel if blocked on lock object
				return false;
			}
			
			//System.out.println("IN ACCESS MEMORY METHOD: STORE operation.");
			
			this.fireUpdate("> Executing STORE instruction; destination memory \naddress " + getIR().read(1).getField2() + 
				" placed into MAR \n");
			getMAR().write(getIR().read(1).getField2()); //Load mar with destination (memory address)
			
			setWaitStatus(true);
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
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
				e.printStackTrace();
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
				e.printStackTrace();
				active = false;
				setWaitStatus(false);
				return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
			}
			setWaitStatus(false);
		
		}
		
	//}
		lock.unlock();
	
		return true;
	}
	
	
	public abstract void run();
	
	protected abstract void fireUpdate(String update);
	
	//public abstract void setPipelineFlush(boolean isFlush);
	
	public boolean isActive() {
		return this.active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public boolean isPipelineFlush() {
		return this.pipelineFlush;
	}
	
	/*
	 * There must be a way to differentiate between interrupts generated by pipelining flushing
	 * and those generated by the user clicking reset; in the former case, execution must continue,
	 * with the fetch/decode stage simply resetting itself, in the latter case execution should terminate
	 * altogether.
	 * 
	 */
	public void setPipelineFlush(boolean flush) {
		this.pipelineFlush = flush;
	}
	
	/*
	 * This boolean flag allows the GUI's SwingWorker thread to determine whether the thread is
	 * waiting on this object. If so, notify() will be called on this object (and not otherwise).
	 */
	public boolean isWaiting() {
		return this.isWaiting;
	}
	
	/*
	 * This method is used when assembly program execution is reset/restarted midway through;
	 * if isWaiting has just been set to false and the worker thread is cancelled, when it comes to
	 * stepping through execution after the restart, the "Step" button won't work as it uses the status
	 * of isWaiting to determine whether to resume execution or not.
	 */
	public void setWaitStatus(boolean waiting) {
		this.isWaiting = waiting;
	}
	
	
	
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
		return this.statusRegister;
	}

}
