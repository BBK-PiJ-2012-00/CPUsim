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
	public synchronized boolean accessMemory(boolean isInstructionFetch, boolean isOperandLoad, boolean isOperandStore) {
		
		String operation = "";
		String stage = "";
		//if (pc.getValue() != 0) { //Not good enough!
		if (isInstructionFetch) {
			operation = "instruction fetch";
			stage = "F/D Stage";
		}
		if (isOperandLoad) {
			operation = "LOAD";
			stage = "Ex. Stage";
		}
		if (isOperandStore) {
			operation = "STORE";
			stage = "Ex. Stage";
		}
		fireUpdate("> Waiting to acquire use of MAR, MBR and system bus to \ncomplete " + operation + " operation.\n" );
	//	}
		
		
		
		//System.out.println(getClass() + " waiting to acquire lock.");
	
			
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
	//	System.out.println(getClass() + " just acquired lock.");
		
		fireUpdate("> Exclusive use of MAR, MBR and system bus acquired by\n" + stage + " for " + operation + " operation.\n");
	
		if (isInstructionFetch) {
			//Instruction fetch code; accessed in pipelined mode by a separate thread running in the F/D stage,
			//therefore it is necessary to regularly poll for its interrupted status so that in the event that
			//"reset" is clicked on the GUI (triggering SwingWorker thread running in the execute stage to issue
			//an interrupt to the threads running in the F/D stage and WB stage), or in the event of a pipeline
			// flush (which is also signalled by an interrupt from the execute stage), this thread can return to 
			//its run() method and terminate naturally without executing further code.
			
			//System.out.println("IN ACCESS MEMORY METHOD: Fetch Operation");
			
			//this.fireUpdate("\n** INSTRUCTION FETCH/DECODE STAGE ** \n");
			//getIR().clear(); //Clear previous instruction from display++++++++ SORT FOR STANDaRD
			
			if (Thread.currentThread().isInterrupted()) { //In event of pipeline flush from execute stage
				//System.out.println("Entering interrupted block 1");
//				if (pipelineFlush) {
//					fireUpdate("** PIPELINE FLUSH ** \nFetch/decode of " + ir.read(0).toString() + " abandoned.");
//				}
				//pipelineFlush = true;
				//properLock.unlock();
				return false;
			}
			
			getMAR().write(getPC().getValue()); //Write address value in PC to MAR.
			
			if (Thread.currentThread().isInterrupted()) { //In event of pipeline flush from execute stage
			//	System.out.println("Entering interrupted block 2");
//				if (pipelineFlush) {
//					fireUpdate("** PIPELINE FLUSH ** \nFetch/decode of " + ir.read(0).toString() + " abandoned.");
//				}
				return false;
			}
			
			fireUpdate("> Memory address from PC placed into MAR \n");
			
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
				//System.out.println("FD interrupted during wait 1");
				//properLock.unlock();
				return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
			}
			setWaitStatus(false);
			
			if (Thread.currentThread().isInterrupted()) { //In event of pipeline flush from execute stage
			//	System.out.println("Entering interrupted block 3");
//				if (pipelineFlush) {
//					fireUpdate("** PIPELINE FLUSH ** \nFetch/decode of " + ir.read(0).toString() + " abandoned.");
//				}
				//pipelineFlush = true;
			//	properLock.unlock();
				return false;
			}
			
			//Transfer address from MAR to system bus, prompting read
			boolean successfulTransfer = getSystemBus().transferToMemory(getMAR().read(), null); 
			if (!successfulTransfer) { 
//				if (pipelineFlush) {
//					fireUpdate("** PIPELINE FLUSH ** \nFetch/decode of " + ir.read(0).toString() + " abandoned.");
//				}
				//If SwingWorker is cancelled and thread of execution is interrupted, successfulTransfer will be false and the
				//method should not execute any further
			//	properLock.unlock();
				return false;
			}
			//Flushing during system bus operation? 
			
			if (Thread.currentThread().isInterrupted()) { //In event of pipeline flush from execute stage
				System.out.println("Entering interrupted block 4");
//				if (pipelineFlush) {
//					fireUpdate("** PIPELINE FLUSH ** \nFetch/decode of " + ir.read(0).toString() + " abandoned.");
//				}
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
//				if (pipelineFlush) {
//					fireUpdate("** PIPELINE FLUSH ** \nFetch/decode of " + ir.read(0).toString() + " abandoned.");
//				}
				return false; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
			}
			setWaitStatus(false);
			
			if (Thread.currentThread().isInterrupted()) { //In event of pipeline flush from execute stage
				System.out.println("Entering interrupted block 5");
//				if (pipelineFlush) {
//					fireUpdate("** PIPELINE FLUSH ** \nFetch/decode of " + ir.read(0).toString() + " abandoned.");
//				}
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
			boolean successfulTransfer = getSystemBus().transferToMemory(mar.read(), null);
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
			getSystemBus().transferToMemory(getMAR().read(), getMBR().read()); 
			
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
