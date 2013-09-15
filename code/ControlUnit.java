package code;

/*
 * The primary class of the simulator, representing the CPU's control unit.
 */

public interface ControlUnit {
	
	/*
	 * This method is called from CPUframe when the user clicks "Execute Program".
	 * It initiates the instruction cycle (by calling the private launch() method in 
	 * both pipelined and standard mode, and manages the cycle in standard mode 
	 * (i.e. exits when a HALT instruction is executed, bringing the cycle to an end, 
	 * and quits early should an interrupt be detected in one
	 * of the stages (the result of "reset" being clicked on the GUI). 
	 */
	public void activate(); //Initiate instruction cycle execution
	
	
	/*
	 * @return InstructionRegister the InstructionRegister field.
	 */
	public InstructionRegister getIR();//To access IR; for updating GUI display
	
	
	/*
	 * @return ProgramCounter the ProgramCounter field.
	 */
	public ProgramCounter getPC(); //To access PC; for updating GUI display
	
	
	/*
	 * @return RegisterFile the RegisterFile (general purpose registers) field. 
	 */
	public RegisterFile getRegisters(); //To access general purpose registers, for updating GUI display.
	
	
	/*
	 * @return Register the condition code register field.
	 */
	public Register getConditionCodeRegister(); //To access status register, updating GUI display.
	
	
	/*
	 * @return MemoryBufferRegister the MBR field.
	 */
	public MemoryBufferRegister getMBR(); //Access MBR for updating GUI display
	
	
	/*
	 * @return MemoryAddressRegister the MAR field.
	 */
	public MemoryAddressRegister getMAR(); //Access MAR for updating GUI display
	
	
	/*
	 * @return FetchDecodeStage the FetchDecodeStage field (pipelined or standard).
	 */	
	public FetchDecodeStage getFetchDecodeStage();
	
	
	/*
	 * @return ExecuteStage the ExecuteStage field (pipelined or standard).
	 */
	public ExecuteStage getExecuteStage();

	
	/*
	 * @return WriteBackStage the WriteBackStage field (pipelined or standard).
	 */
	public WriteBackStage getWriteBackStage();
	
	/*
	 * A method for resetting the contents of all CPU registers in the event of a 
	 * reset operation.
	 */
	public void clearRegisters();

	

}
