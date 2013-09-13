package code;

/*
 * The primary class of the simulator, representing the CPU's control unit.
 */

public interface ControlUnit {
	
	/*
	 * This method is called from CPUframe when the user clicks "Execute Program".
	 * It initiates the instruction cycle in both pipelined and standard mode, and 
	 * manages the cycle in standard mode (i.e. exits when a HALT instruction is executed,
	 * bringing the cycle to an end, and quits early should an interrupt be detected in one
	 * of the stages (the result of "reset" being clicked on the GUI). 
	 */
	public void activate(); //Initiate instruction cycle execution
	
	public InstructionRegister getIR();//To access IR; for updating GUI display
	
	public ProgramCounter getPC(); //To access PC; for updating GUI display
	
	public RegisterFile getRegisters(); //To access general purpose registers, for updating GUI display.
	
	public Register getConditionCodeRegister(); //To access status register, updating GUI display.
	
	public MemoryBufferRegister getMBR(); //Access MBR for updating GUI display
	
	public MemoryAddressRegister getMAR(); //Access MAR for updating GUI display
	
	public FetchDecodeStage getFetchDecodeStage();
	
	public ExecuteStage getExecuteStage();

	public WriteBackStage getWriteBackStage();
	
	public void clearRegisters();

	

}
