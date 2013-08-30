package code;

/*
 * The primary class of the simulator, representing the CPU's control unit.
 */

public interface ControlUnit {
	
	
	//Stages of the instruction cycle are now represented by the Stage classes, removing the need for these
	//methods to be present in the control unit itself.
	
	//public void instructionFetch(); 
		//Code for instruction fetch stage
	
	//public int instructionDecode(); //Returns opcode value (numeric)
	
	//public void instructionExecute(int opcode); //int opcode is decoded opcode passed from decode()
	
	//public void instructionWriteBack(Operand result); //Only required for arithemtic instructions; store the result
	
	public void activate(); //Initiate instruction cycle execution
	
	public InstructionRegister getIR();//To access IR; for updating GUI display
	
	public ProgramCounter getPC(); //To access PC; for updating GUI display
	
	public RegisterFile getRegisters(); //To access general purpose registers, for updating GUI display.
	
	public Register getStatusRegister(); //To access status register, updating GUI display.
	
	public MemoryBufferRegister getMBR(); //Access MBR for updating GUI display
	
	public MemoryAddressRegister getMAR(); //Access MAR for updating GUI display
	
	public FetchDecodeStage getFetchDecodeStage();
	
	public ExecuteStage getExecuteStage();

	public WriteBackStage getWriteBackStage();
	
	public void clearRegisters();
	
	public void resetStages();

		
	

	
	
	
	

}
