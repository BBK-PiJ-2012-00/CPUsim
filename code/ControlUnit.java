package code;

/*
 * The primary class of the simulator, representing the CPU's control unit.
 */

/*
 * Interface between MBR/MAR and system bus; in reality, the MBR would link directly to data line, 
 * MAR directly to address line. This would make execution more complicated; the registers would have
 * to be synchronized to ensure that the address in the MAR corresponds to data in MBR.
 * 
 * But this could be achieved by careful structuring of control unit methods? It would be even more complex
 * for pipelined operation to keep system bus synchronized.  
 * 
 * Control unit can access contents of MAR/MBR, and pass these values as parameters to the system bus to start with.
 */

public interface ControlUnit {
	

//	
	//Stages represented by methods
	
	public void instructionFetch(); 
		//Code for instruction fetch stage
	
	public void instructionDecode(); 
	
	public void instructionExecute();
	
	public void instructionStore(); //Not required in every cycle
		
	

	
	
	
	

}
