package code;

/*
 * An interface for the system bus controller of the simulator. It seems wise to encapsulate the (initially) three bus lines 
 * in a system bus class, meaning that lines must be accessed via this class and not directly, ensuring
 * a higher degree of data integrity.  Also allows for later addition of more lines to improve performance without
 * having to alter code elsewhere (i.e. to choose which address line and which data line etc).
 * 
 * A SystemBusController class also simplifies use of the system bus; rather than having to access control lines directly and 
 * separately, the SystemBusController class handles all interaction with the lines and can implement any safeguards necessary.
 */
public interface BusController {
		
	/*
	 * A method to transfer data from CPU to memory via the SystemBus.
	 * 
	 * @param int memoryAddress the memory destination.
	 * @param Data data the data to be transferred.
	 * @return true if transfer successful, false otherwise.
	 */
	public boolean transferToMemory(int memoryAddress, Data data);
	
	
	/*
	 * A method to transfer data from memory to CPU via the SystemBus.
	 * 
	 * @param Data data the data to be transferred.
	 * @return true if transfer successful, false otherwise.
	 */	 
	 boolean transferToCPU(Data data);

}
