package code;

public abstract class FetchDecodeStage extends Stage {
//	private BusController systemBus;
//	
//	private MemoryAddressRegister mar;
//	private MemoryBufferRegister mbr;	
//	
//	private InstructionRegister ir;
//	private ProgramCounter pc;
	
	
	private int opcodeValue; //This is accessed by control unit to pass to next stage.
	
	private UpdateListener updateListener; //Update event listener
	
	//private boolean isWaiting;
	
	
	public FetchDecodeStage(BusController systemBus, InstructionRegister ir, ProgramCounter pc, RegisterFile genRegisters,
			Register statusRegister, MemoryBufferRegister mbr, MemoryAddressRegister mar) {
		
		super(systemBus, ir, pc, genRegisters, statusRegister, mbr, mar);
		
//		this.systemBus = systemBus;
//		
//		
//		
//		this.mar = mar;
//		this.mbr = mbr;
//		
//		this.ir = ir;
//		this.pc = pc;
	}
	
	
//	public void instructionFetch() {
//		this.fireUpdate("\n** INSTRUCTION FETCH/DECODE STAGE ** \n");
//		getIR().clear(); //Clear previous instruction from display
//		getMAR().write(getPC().getValue()); //Write address value in PC to MAR.
//		
//		this.fireUpdate("Memory address from PC placed into MAR \n");
//		
////		isWaiting = true;
////		try {
////			wait();
////		} catch (InterruptedException e) {
////			e.printStackTrace();
////			isWaiting = false;
////			return; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
////		}
////		isWaiting = false;
//		
//		
//		//Transfer address from MAR to system bus, prompting read
//		boolean successfulTransfer = getSystemBus().transferToMemory(getMAR().read(), null); 
//		if (!successfulTransfer) { 
//			//If SwingWorker is cancelled and thread of execution is interrupted, successfulTransfer will be false and the
//			//method should not execute any further
//			return;
//		}
//		
//		System.out.println("Reentered f/d stage from bus: successful transfer = " + successfulTransfer);
//		
//		this.fireUpdate("Load contents of memory address " + getMAR().read() + " into MBR \n");
//		
//		
////		isWaiting = true;
////		try {
////			wait();
////		} catch (InterruptedException e) {
////			e.printStackTrace();
////			isWaiting = false;
////			return; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
////		}
////		isWaiting = false;	
//		
//		
//		//A Data item should now be in MBR
//		getIR().loadIR((Instruction) getMBR().read()); //Cast required as mbr holds type data, IR type Instruction; May need to handle exception
//		this.fireUpdate("Load contents of MBR into IR \n");
//		
////		isWaiting = true;
////		try {
////			wait();
////		} catch (InterruptedException e) {
////			e.printStackTrace();
////			isWaiting = false;
////			return; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
////		}
////		isWaiting = false;
//		
//		getMAR().write(-1);//Reset MAR. Repositioned here for user clarity; mem. addr. remains in MAR until instr. in IR.		
//		getMBR().write(null); //Clear MBR to reflect that instruction has moved to IR
//		
//	}
//	//Fetch ends with instruction being loaded into IR.
//	
//	
//	public int instructionDecode() { //Returns int value of opcode
//		Instruction instr = getIR().read();
//		int opcodeValue = instr.getOpcode().getValue(); //Gets instruction opcode as int value
//		getPC().incrementPC(); //Increment PC; done here so that with pipelining, the next instruction can be fetched at this point
//		this.fireUpdate("PC incremented by 1 (ready for next instruction fetch) \n");
////		
////		isWaiting = true;
////		try {
////			wait();
////		} catch (InterruptedException e) {
////			e.printStackTrace();
////			isWaiting = false;
////			return -1; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called). -1 signals to control
////			//unit to stop execution.
////		}
////		isWaiting = false;		
//		
//		return opcodeValue;
//		
//	}
	
//	@Override
//	public synchronized void run() { //Synchronized to enable step execution
//		this.instructionFetch();
//		opcodeValue = this.instructionDecode();
//	}
	
	public abstract boolean instructionFetch();
	
	public abstract int instructionDecode();
	
	public int getOpcodeValue() {
		return this.opcodeValue;
	}
	
	public void setOpcodeValue(int opcodeValue) {
		this.opcodeValue = opcodeValue;
	}
	
	@Override
	protected abstract void fireUpdate(String update); 
	
	public void registerListener(UpdateListener listener) {
		this.updateListener = listener;
	}
	
	public UpdateListener getUpdateListener() {
		return this.updateListener;
	}
	
	
	
	/*
	 * This boolean flag allows the GUI's SwingWorker thread to determine whether the thread is
	 * waiting on this object. If so, notify() will be called on this object (and not otherwise).
	 */
//	public boolean isWaiting() {
//		return isWaiting;
//	}
	
	/*
	 * This method is used when assembly program execution is reset/restarted midway through;
	 * if isWaiting has just been set to false and the worker thread is cancelled, when it comes to
	 * stepping through execution after the restart, the "Step" button won't work as it uses the status
	 * of isWaiting to determine whether to resume execution or not.
	 */
//	public void setWaitStatus(boolean status) {
//		isWaiting = status;
//	}
	
	
//	public BusController getSystemBus() {
//		return this.systemBus;
//	}
//	
//	public MemoryAddressRegister getMAR() {
//		return this.getMAR();
//	}
//	
//	public MemoryBufferRegister getMBR() {
//		return this.mbr;
//	}
//	
//	public InstructionRegister getIR() {
//		return this.ir;
//	}
//	
//	public ProgramCounter getPC() {
//		return this.pc;
//	}
	
//	public UpdateListener getUpdateListener() {
//		return this.updateListener;
//	}
	
}
