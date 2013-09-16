package code;

/*
 * This class is subclassed by the pipelined and standard write back stage objects. The write back
 * stage is for the writing of ALU operation results back to general purpose registers.
 */
public abstract class WriteBackStage extends Stage {
	private Operand result; //Holds the result of the ALU operation
	private UpdateListener updateListener;
	
	
	public WriteBackStage(BusController systemBus, InstructionRegister ir, ProgramCounter pc, RegisterFile genRegisters,
			Register statusRegister, MemoryBufferRegister mbr, MemoryAddressRegister mar) {
		
		super(systemBus, ir, pc, genRegisters, statusRegister, mbr, mar);
	}
	
	
	/*
	 * @return the result field.
	 */
	public Operand getResult() {
		return this.result;
	}
	
	
	/*
	 * @param Operand result the value to be taken by the result field.
	 */
	public void setResult(Operand result) {
		this.result = result;
	}
		
	
	/*
	 * This method is implemented differently by standard and pipelined modes.  It handles
	 * the writing back of ALU operation results to general purpose registers. In pipelined mode,
	 * this is done by an additional thread spawned by the SwingWorker thread which runs in the execute
	 * stage. In standard mode, this is done by the SwingWorker thread itself.
	 * 
	 * @param Operand result the result to be written back to a general purpose register.
	 */
	public abstract void instructionWriteBack(Operand result);
	
	
	/*
	 * A method the prompts the write back stage to receive the operand result.
	 * 
	 * @param Operand result the received result from the execute stage.
	 */
	public abstract void receive(Operand result);
	
	
	/*
	 * A method for registering an event listener object with the stage object,
	 * for GUI display purposes. Every time an activity monitor comment is generated,
	 * an update event is created and handled by the listener to change the GUI
	 * display accordingly.
	 * 
	 * @param UpdateListener listener the listener object to handle update events.
	 */
	public void registerListener(UpdateListener listener) {
		this.updateListener = listener;		
	}
	
	
	/*
	 * @return the updateListener field.
	 */
	public UpdateListener getUpdateListener() {
		return this.updateListener;
	}
	
	
	@Override
	protected void fireUpdate(String update) {
		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, update);
		updateListener.handleUpDateEvent(updateEvent);
	}
}
