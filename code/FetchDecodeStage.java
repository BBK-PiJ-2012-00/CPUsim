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
			Register rCC, MemoryBufferRegister mbr, MemoryAddressRegister mar) {
		
		super(systemBus, ir, pc, genRegisters, rCC, mbr, mar);

	}
	
	
	/*
	 * Standard and pipelined stages will implement this slightly differently, hence it
	 * is abstract (both version must have this method, though).
	 * 
	 * @return boolean if the stage is interrupted (or flushed in pipelined mode), the
	 * course of action is to stop executing and return false (this is implemented in an
	 * error handling block for InterruptedExceptions), signalling to the run() method code
	 * that it should terminate execution.
	 */
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
