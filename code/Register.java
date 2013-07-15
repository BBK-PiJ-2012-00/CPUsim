package code;

public interface Register {
	
	//For methods common to all registers
	//This should perhaps be an abstract class, as all registers have a contents field
	//There are then General Purpose registers
	//Or, leave this as an interface, and have abstract classes at next level down
	
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
	
	public int write();
	
	

}
