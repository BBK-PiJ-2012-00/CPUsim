package code;

public class StandardFetchDecodeStage extends FetchDecodeStage {
	
	//private int opcodeValue; //This is accessed by control unit to pass to next stage.
	
	private UpdateListener updateListener; //Update event listener
	
	//private boolean isWaiting;
	

	public StandardFetchDecodeStage(BusController systemBus, InstructionRegister ir, ProgramCounter pc,
			RegisterFile genRegisters, Register statusRegister, MemoryBufferRegister mbr, MemoryAddressRegister mar) {
		
		super(systemBus, ir, pc, genRegisters, statusRegister, mbr, mar);
	}	
	
	//Could call accessMemory() to avoid duplicate code; precede with the titled update
	@Override
	public boolean instructionFetch() {
		fireUpdate("\n** INSTRUCTION FETCH/DECODE STAGE ** \n");	
		boolean successful = accessMemory(true, false, false);
		return successful;
//		getIR().clear(); //Clear previous instruction from display
//		getMAR().write(getPC().getValue()); //Write address value in PC to MAR.
//		
//		this.fireUpdate("Memory address from PC placed into MAR \n");
//		
//		setWaitStatus(true);
//		try {
//			wait();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//			setWaitStatus(false);
//			return; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called).
//		}
//		setWaitStatus(false);
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
		
	}
	//Fetch ends with instruction being loaded into IR.
	
	
	public int instructionDecode() { //Returns int value of opcode
		Instruction instr = getIR().read();
		int opcodeValue = instr.getOpcode().getValue(); //Gets instruction opcode as int value
		getPC().incrementPC(); //Increment PC; done here so that with pipelining, the next instruction can be fetched at this point
		this.fireUpdate("PC incremented by 1 (ready for next instruction fetch) \n");
		
		setWaitStatus(true);
		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
			setWaitStatus(false);
			return -1; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called). -1 signals to control
			//unit to stop execution.
		}
		setWaitStatus(false);		
		
		return opcodeValue;
		
	}
	
	@Override
	public synchronized void run() { //Synchronized to enable step execution
		System.out.println("In f/d stage run.");
		if (this.instructionFetch() == false) { //Returns false if interrupted during fetch
			setOpcodeValue(-1); //Signals to control unit to stop execution
			return; //Cancel execution if interrupted during fetch
		}
		setOpcodeValue(this.instructionDecode());
	}
	
	@Override
	protected void fireUpdate(String update) {
		System.out.println("in fire update");
		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, update);
		System.out.println("Update Event created:" + updateEvent);
		updateListener.handleUpDateEvent(updateEvent);		
	}
	
	public void registerListener(UpdateListener listener) {
		this.updateListener = listener;
	}
	
//	public int getOpcodeValue() {
//		return this.opcodeValue;
//	}
	
//	public void setOpcodeValue(int opcodeValue) {
//		this.opcodeValue = opcodeValue;
//	}
	
//	protected void fireUpdate(String update) {
//		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, update);
//		getUpdateListener().handleUpDateEvent(updateEvent);		
//	}
	
//	public void registerListener(UpdateListener listener) {
//		this.updateListener = listener;
//	}
	
	
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
//		return getSystemBus();
//	}
//	
//	public MemoryAddressRegister getMAR() {
//		return getMAR();
//	}
//	
//	public MemoryBufferRegister getMBR() {
//		return getMBR();
//	}
//	
//	public InstructionRegister getIR() {
//		return getIR();
//	}
//	
//	public ProgramCounter getPC() {
//		return getPC();
//	}
	
//	public UpdateListener getUpdateListener() {
//		return this.updateListener;
//	}




	

}
