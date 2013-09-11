package code;

import javax.swing.SwingWorker;

public abstract class Stage implements Runnable {
	
	private BusController systemBus;
	
	private MemoryAddressRegister mar;
	private MemoryBufferRegister mbr;	
	
	private InstructionRegister ir;
	private ProgramCounter pc;
	private RegisterFile genRegisters;
	private Register statusRegister;
	
	private static Object lock; //Static object that is used to synchronize accessMemory() method
												//across fetch/decode and execute stage objects.
	
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
		
		lock = new Object();
		
	
		
	}
	
	
	/*
	 * Contains code to fetch instructions or operands from memory,
	 * protecting integrity of MAR and MBR registers during pipelined mode.
	 * 
	 * isInstructionFetch is used to signify if the operation is an instruction fetch
	 * or operand fetch.
	 */
	public synchronized boolean accessMemory(boolean isInstructionFetch, boolean isOperandLoad, boolean isOperandStore) {
		
		if (pc.getValue() != 0) { //Not good enough!
			String operation = "";
			if (isInstructionFetch) {
				operation = "instruction fetch";
			}
			if (isOperandLoad) {
				operation = "LOAD";
			}
			if (isOperandStore) {
				operation = "STORE";
			}
			fireUpdate("Pipeline delay: Waiting to acquire use of system bus \nto complete " + operation + " operation.\n" );
		}
		
		
		//What to do if interrupted while waiting for lock?? SwingWorker is blocked at that point.
		
		//setWaitStatus(true);
		System.out.println(getClass() + " waiting to acquire lock.");
		synchronized(lock) {
			System.out.println(getClass() + " just acquired lock.");
			
			//setWaitStatus(false);
			
			
		
			if (isInstructionFetch) {
				System.out.println("IN ACCESS MEMORY METHOD: Fetch Operation");
				
				System.out.println("Entering instructionFetch()");
				//this.fireUpdate("\n** INSTRUCTION FETCH/DECODE STAGE ** \n");
				//getIR().clear(); //Clear previous instruction from display
				
				if (Thread.currentThread().isInterrupted()) { //In event of pipeline flush from execute stage
					System.out.println("Entering interrupted block 1");
					fireUpdate("**Branch taken in execute stage; pipeline flush. Current instruction \nfetch/decode abandoned.");
					//pipelineFlush = true;
					return false;
				}
				
				getMAR().write(getPC().getValue()); //Write address value in PC to MAR.
				
				if (Thread.currentThread().isInterrupted()) { //In event of pipeline flush from execute stage
					fireUpdate("**Branch taken in execute stage; pipeline flush. Current instruction \nfetch/decode abandoned.");
					//pipelineFlush = true;
					return false;
				}
				
				fireUpdate("Memory address from PC placed into MAR \n");
				
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
					fireUpdate("Branch taken in execute stage; pipeline flush. Current instruction \nfetch/decode abandoned.");
					//pipelineFlush = true;
					return false;
				}
				
				//Transfer address from MAR to system bus, prompting read
				boolean successfulTransfer = getSystemBus().transferToMemory(getMAR().read(), null); 
				if (!successfulTransfer) { 
					//If SwingWorker is cancelled and thread of execution is interrupted, successfulTransfer will be false and the
					//method should not execute any further
					return false;
				}
				//Flushing during system bus operation? 
				
				if (Thread.currentThread().isInterrupted()) { //In event of pipeline flush from execute stage
					fireUpdate("Branch taken in execute stage; pipeline flush. Current instruction \nfetch/decode abandoned.");
					//pipelineFlush = true;
					return false;
				}
				
				this.fireUpdate("Load contents of memory address " + getMAR().read() + " into MBR \n");
				
				
				
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
					fireUpdate("**Branch taken in execute stage; pipeline flush. Current instruction \nfetch/decode abandoned.");
					//pipelineFlush = true;
					return false;
				}
				
				
				
				System.out.println("Ln102, attempting to cast: " + getMBR().read());
				//A Data item should now be in MBR
				getIR().loadIR((Instruction) getMBR().read()); //Cast required as mbr holds type data, IR type Instruction; May need to handle exception
				System.out.println("Instruction: " + getMBR().read());
				this.fireUpdate("Load contents of MBR into IR \n");
				
				if (Thread.currentThread().isInterrupted()) { //In event of pipeline flush from execute stage
					fireUpdate("**Branch taken in execute stage; pipeline flush. Current instruction \nfetch/decode abandoned.\n");
					//pipelineFlush = true;
					return false;
				}
				
				fireUpdate("About to enter wait\n");
				
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
				
				getMAR().write(-1);//Reset MAR. Repositioned here for user clarity; mem. addr. remains in MAR until instr. in IR.		
				getMBR().write(null); //Clear MBR to reflect that instruction has moved to IR (should it be reset earlier, to 
				//better reflect movement?)
				
				fireUpdate("Leaving Fetch block\n");
			}
			
			/*
			 * LOAD instruction execution
			 */		
			else if (isOperandLoad) {
				//Swing worker needs to be stopped here if cancelled while waiting to get lock on a LOAD
				fireUpdate("Into isOperandLoad.\n");
				fireUpdate("Info: IR1 holding: " + ir.read(1).toString());
				System.out.println("IN ACCESS MEMORY METHOD: LOAD operation");
				
				fireUpdate("Executing LOAD instruction; memory address " +	ir.read(1).getField1() + 
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
					return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
				}
				setWaitStatus(false);
				
				System.out.println("Instruction is: " + ir.read(1).toString());
				System.out.println("Value on MAR at ln46:" + mar.read());
				boolean successfulTransfer = getSystemBus().transferToMemory(mar.read(), null);
				if (!successfulTransfer) {
					active = false;
					return false;
				}
				this.fireUpdate("Operand " + mbr.read().toString() + " loaded from address " + ir.read(1).getField1() + 
						" into MBR\n");
				
				setWaitStatus(true);
				System.out.println ("About to wait");
				try {
					wait();
					System.out.println("Out of wait.");
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
					
					this.fireUpdate("Loaded operand " + mbr.read() + " into condition code register\n");
					
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
				
				
				
				//Transfer data in mbr to destination field in instruction in ir (field2).
				getGenRegisters().write(ir.read(1).getField2(), mbr.read());//getField2() gives reg. destination index, mbr.read()
				//gives the operand to be moved from mbr to genRegisters at index given in getField2().
				this.fireUpdate("Operand " + mbr.read() + " loaded into r" + ir.read(1).getField2() + "\n");
				
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
				
				
				mbr.write(null); //Reset MBR
				
			}
			
			
			/*
			 * STORE instruction execution.
			 */
			else if (isOperandStore) {
				
				System.out.println("IN ACCESS MEMORY METHOD: STORE operation.");
				
				this.fireUpdate("Executing STORE instruction; destination memory \naddress " + getIR().read(1).getField2() + 
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
				this.fireUpdate("Operand " + getGenRegisters().read(getIR().read(1).getField1()) + " loaded from r" + 
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
				getSystemBus().transferToMemory(getMAR().read(), getMBR().read()); 
				
				this.fireUpdate("Operand " + getGenRegisters().read(getIR().read(1).getField1()) + " stored in memory address " +
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
			fireUpdate("Early lock release\n");
			return true;
		}
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
		System.out.println(this.getClass() + ": waiting set to " + waiting);
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
