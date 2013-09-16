package code;

/*
 * An interface to represent main memory. Main memory is implemented as an array of 100 slots of
 * type Data. 
 */

public interface MainMemory {
	
	public Data accessAddress(int index); //For testing purposes; pending deletion. 
	
	
	/*
	 * A method invoked by the system bus for writing data to memory; it prompts memory to receive data
	 * from the system bus and write it to the address given as as a method parameter.
	 * 
	 *  @param int address the address the contents of the data line is to be written to.
	 *  @param Data data the data to be written from the data line to memory.
	 *  @return boolean true if the write is successful, false otherwise.
	 */
	public boolean notifyWrite(int address, Data data);
		
		
	
	/*
	 * A method invoked by the system bus to prompt a memory read. Data is 
	 * read from the memory address specified by the system bus, and that data 
	 * is then placed on the system bus for transfer to the CPU.
	 * 
	 * @param int address the address of the data to be read.
	 * @return boolean true if the read is successful, false otherwise.
	 */
	public boolean notifyRead(int address);	
	
	
	/*
	 * The pointer (represented as an integer) points the the next available
	 * storage slot in memory. This resets the pointer to 0.
	 */
	public void resetPointer();
	
	
	/*
	 * @return int the pointer field.
	 */
	public int getPointer(); 
	
	
	/*
	 * To reset memory contents before loading a new program.
	 */
	public void clearMemory(); 
	
	
	/*
	 * Called by the loader to load assembled program code into memory.
	 * 
	 * @param Data[] the assembled program code saved into an array.
	 */
	public void loadMemory(Data[] programCode);
	
	
	/*
	 * A method for registering an event listener object with the memory module,
	 * for GUI display purposes. Every time the contents of memory is updated,
	 * an update event is created and handled by the listener to change the GUI
	 * display accordingly.
	 * 
	 * @param UpdateListener listener the listener object to handle update events.
	 */
	public void registerListener(UpdateListener listener);
	
	/*
	 * Renders memory contents into a String fit for GUI display. This method is
	 * public for this class unlike many others because it is called by the CPU
	 * frame for a better "empty" memory display consisting of empty addresses.
	 * 
	 * @return String contents of memory rendered for GUI display.
	 */
	public String display();
	
	
	/*
	 * A method called by the CPUbuilder class for register the bus controller
	 * object with memory; memory requires access to this object to enable it
	 * to use the system bus.
	 * 
	 * @param BusController the bus controller reference.
	 */
	public void registerBusController(BusController systemBusController);
	
	

}
