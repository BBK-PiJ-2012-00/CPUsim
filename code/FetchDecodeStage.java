package code;

/*
 * A stage object representing the fetch/decode stage of the instruction cycle. Is subclassed
 * by the pipelined and standard variations.
 */
public abstract class FetchDecodeStage extends Stage {	
	private int opcodeValue; //This is accessed by control unit to pass to next stage in standard mode.	
	private UpdateListener updateListener; 	
	
	
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
	
	
	/*
	 * Contains the code for the decoding of instructions fetched from memory and that are being
	 * held in the IR.
	 * 
	 * @return int has a value of -1 in the event of a reset (causing an interrupt) or pipeline
	 * flush. Otherwise is the true value of the decoded instruction's opcode.
	 */
	public abstract int instructionDecode();
	
	
	/*
	 * @return int the opcodeValue field, representing the numerical value of an instruction's
	 * opcode.
	 */
	public int getOpcodeValue() {
		return this.opcodeValue;
	}
	
	
	/*
	 * @param int opcodeValue the value to be taken on by the opcodeValue field.
	 */
	public void setOpcodeValue(int opcodeValue) {
		this.opcodeValue = opcodeValue;
	}
	

	
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
	 * @return UpdateListener the updateListener field.
	 */
	public UpdateListener getUpdateListener() {
		return this.updateListener;
	}
	
}
