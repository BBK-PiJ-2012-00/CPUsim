package code;

public abstract class WriteBackStage extends Stage {
	//private InstructionRegister ir;
	//private RegisterFile genRegisters;
	private Operand result;
	private UpdateListener updateListener;
	
	
	public WriteBackStage(BusController systemBus, InstructionRegister ir, ProgramCounter pc, RegisterFile genRegisters,
			Register statusRegister, MemoryBufferRegister mbr, MemoryAddressRegister mar) {
		
		super(systemBus, ir, pc, genRegisters, statusRegister, mbr, mar);
	}
	
	public Operand getResult() {
		return this.result;
	}
	
	public void setResult(Operand result) {
		this.result = result;
	}
		
	
	//Writing of results to register file
	public abstract void instructionWriteBack(Operand result);
	
	public abstract void receive(Operand result); //To receive operand reference from previous stage (pipelined and standard modes
										//will implement this differently)
		
	@Override
	public abstract void run();

	public void registerListener(UpdateListener listener) {
		this.updateListener = listener;		
	}
	
	public UpdateListener getUpdateListener() {
		return this.updateListener;
	}
	
	@Override
	protected void fireUpdate(String update) {
		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, update);
		updateListener.handleUpDateEvent(updateEvent);
	}
}
