package code;

/*
 * The primary class of the simulator, representing the CPU's control unit.
 */

public interface ControlUnit {
	
	
	//Stages represented by methods
	
	public void instructionFetch(); 
		//Code for instruction fetch stage
	
	public void instructionDecode(); 
	
	public void instructionExecute(int opcode); //int opcode is decoded opcode passed from decode()
	
	public void instructionStore(); //Not required in every cycle
		
	

	
	
	
	

}
