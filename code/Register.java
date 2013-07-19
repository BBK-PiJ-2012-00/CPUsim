package code;

public interface Register {
	
	
	
	//For methods common to all control registers; may not be the case that they have much in common!
	
	/*
	 * MBR needs to hold type Data; it holds all data types that come into CPU from memory, and 
	 * 	data that goes out to memory.
	 * MAR holds integers only; address indexes.
	 * 
	 * PC has an increment method, set, read.
	 * 
	 * General purpose registers will be a register file of 16 registers to begin with
	 * MBR as a register file? Or just the one slot? One slot would be simpler.
	 */
	
	public int read();
	
	public int write(Data data);
	
	

}
