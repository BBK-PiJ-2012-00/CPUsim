package code;

/*
 * The primary class of the simulator, representing the CPU's control unit.
 */

public interface ControlUnit {
	
	
	//Stages represented by methods
	
	public void instructionFetch(); 
		//Code for instruction fetch stage
	
	public int instructionDecode(); //Returns opcode value (numeric)
	
	public void instructionExecute(int opcode); //int opcode is decoded opcode passed from decode()
	
	public void instructionWriteBack(Operand result); //Only required for arithemtic instructions; store the result
	
	public InstructionRegister getIR();//To access IR; for testing purposes
	
	public ProgramCounter getPC(); //To access PC; for testing purposes
	
	public RegisterFile getRegisters(); //To access general purpose registers, for testing.
	
	public Register getStatusRegister(); //To access status register, for testing.
		
	

	
	
	
	

}
