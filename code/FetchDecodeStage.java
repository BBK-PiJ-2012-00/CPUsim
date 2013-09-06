package code;

/*
 * This may not need to be abstract; pipelined version can extend this class, implement runnable and add
 * a forward() method.
 */

public abstract class FetchDecodeStage implements Runnable {
	private BusController systemBus;
	
	private MemoryAddressRegister mar;
	private MemoryBufferRegister mbr;	
	
	private InstructionRegister ir;
	private ProgramCounter pc;
	
	
	private int opcodeValue; //This is fetched by control unit to pass to next stage.
	
	private UpdateListener updateListener; //Update event listener
	
	private boolean isWaiting;
	
	
	public FetchDecodeStage(BusController systemBus, MemoryAddressRegister mar, MemoryBufferRegister mbr,
			InstructionRegister ir, ProgramCounter pc) {
		this.systemBus = systemBus;
		
		this.mar = mar;
		this.mbr = mbr;
		
		this.ir = ir;
		this.pc = pc;
	}
	
	
	public void instructionFetch() {
		this.fireUpdate("\n** INSTRUCTION FETCH/DECODE STAGE ** \n");
		ir.clear(); //Clear previous instruction from display
		mar.write(pc.getValue()); //Write address value in PC to MAR.
		
		this.fireUpdate("Memory address from PC placed into MAR \n");
		
		isWaiting = true;
		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
			isWaiting = false;
			return; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
		}
		isWaiting = false;
		
		
		//Transfer address from MAR to system bus, prompting read
		boolean successfulTransfer = systemBus.transferToMemory(mar.read(), null); 
		if (!successfulTransfer) { 
			//If SwingWorker is cancelled and thread of execution is interrupted, successfulTransfer will be false and the
			//method should not execute any further
			return;
		}
		
		System.out.println("Reentered f/d stage from bus: successful transfer = " + successfulTransfer);
		
		this.fireUpdate("Load contents of memory address " + mar.read() + " into MBR \n");
		
		
		isWaiting = true;
		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
			isWaiting = false;
			return; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
		}
		isWaiting = false;	
		
		
		//A Data item should now be in MBR
		ir.loadIR((Instruction) mbr.read()); //Cast required as mbr holds type data, IR type Instruction; May need to handle exception
		this.fireUpdate("Load contents of MBR into IR \n");
		
		isWaiting = true;
		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
			isWaiting = false;
			return; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
		}
		isWaiting = false;
		
		mar.write(-1);//Reset MAR. Repositioned here for user clarity; mem. addr. remains in MAR until instr. in IR.		
		mbr.write(null); //Clear MBR to reflect that instruction has moved to IR (should it be reset earlier, to better reflect
		//movement?)
		
	}
	//Fetch ends with instruction being loaded into IR.
	
	
	public int instructionDecode() { //Returns int value of opcode
		Instruction instr = ir.read();
		int opcodeValue = instr.getOpcode().getValue(); //Gets instruction opcode as int value
		pc.incrementPC(); //Increment PC; done here so that with pipelining, the next instruction can be fetched at this point
		this.fireUpdate("PC incremented by 1 (ready for next instruction fetch) \n");
		
		isWaiting = true;
		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
			isWaiting = false;
			return -1; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called). -1 signals to control
			//unit to stop execution.
		}
		isWaiting = false;		
		
		return opcodeValue;
		
	}
	
	public synchronized void run() { //Synchronized to enable step execution
		this.instructionFetch();
		opcodeValue = this.instructionDecode();
	}
	
	//Returns true once opcode forwarded to next stage
	public abstract boolean forward(); //For forwarding execution to the next stage (for pipelining)
	
	public int getOpcodeValue() {
		return this.opcodeValue;
	}
	
	private void fireUpdate(String update) {
		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, update);
		updateListener.handleUpDateEvent(updateEvent);		
	}
	
	public void registerListener(UpdateListener listener) {
		this.updateListener = listener;
	}
	
	
	/*
	 * This boolean flag allows the GUI's SwingWorker thread to determine whether the thread is
	 * waiting on this object. If so, notify() will be called on this object (and not otherwise).
	 */
	public boolean isWaiting() {
		return isWaiting;
	}
	
	/*
	 * This method is used when assembly program execution is reset/restarted midway through;
	 * if isWaiting has just been set to false and the worker thread is cancelled, when it comes to
	 * stepping through execution after the restart, the "Step" button won't work as it uses the status
	 * of isWaiting to determine whether to resume execution or not.
	 */
	public void resetWaitStatus() {
		isWaiting = false;
	}
	
}
