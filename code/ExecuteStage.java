package code;

/*
 * Standard and pipelined execute stages are subclasses of this class.
 */
public abstract class ExecuteStage extends Stage {
	private WriteBackStage writeBackStage;
	private int opcode;	
	private UpdateListener updateListener;
	
	
	public ExecuteStage(BusController systemBus, InstructionRegister ir, ProgramCounter pc, RegisterFile genRegisters,
			Register rCC, MemoryBufferRegister mbr, MemoryAddressRegister mar, WriteBackStage writeBackStage) {
		
		super(systemBus, ir, pc, genRegisters, rCC, mbr, mar);
		
		this.writeBackStage = writeBackStage;
	}
	
	/*
	 * Contains code for the execution of instructions. There are some differences between pipelined and standard,
	 * hence this method is abstract.
	 * 
	 * @param int opcode the opcode of the instruction to be executed, obtained the the F/D stage.
	 */
	public abstract boolean instructionExecute(int opcode); 
	
	
	/*
	 * This class requires a reference to the write back stage in both execution modes so that the results of
	 * ALU operations can be passed to it.
	 * 
	 * @return WriteBackStage the write back stage reference.
	 */
	public WriteBackStage getWriteBackStage() {
		return this.writeBackStage;
	} 
	
	
	public abstract void run(); //Must be overridden in subclasses
	
	
	/*
	 * Sets the opcode field, so the opcode may be referred back to.
	 * 
	 * @param int opcode the opcode of the currently executing instruction.
	 */
	public void setOpcodeValue(int opcode) {
		this.opcode = opcode;
	}
	
	public int getOpcodeValue() {
		return this.opcode;
	}
	
	/*
	 * A method for forwarding an operand to the write back stage; forwarding differs
	 * between execution modes, with pipelined mode incorporating the use of a BlockingQueue.
	 * 
	 * @param Operand result the result to be passed to the write back stage.
	 */
	public abstract boolean forward(Operand result);
	

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
	 * To enable subclasses access to the update listener field.
	 */
	protected UpdateListener getUpdateListener() {
		return this.updateListener;
	}
	

}
