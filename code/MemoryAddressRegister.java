package code;

/*
 * A class to represent the memory address register of the CPU, which is used to specify addresses in main memory.
 */
public interface MemoryAddressRegister {
	
	/*
	 * A method for writing an integer address into the MAR.
	 * 
	 * @param int address the address to be written.
	 */
	public void write(int address);
	
	/*
	 * A method for reading the contents of the MAR.
	 * 
	 * @return int the address contained in the MAR.
	 */
	public int read();
	

	/*
	 * A method for registering an event listener object with the MAR,
	 * for GUI display purposes. Every time the contents of the MAR is updated,
	 * an update event is created and handled by the listener to change the GUI
	 * display accordingly.
	 * 
	 * @param UpdateListener listener the listener object to handle update events.
	 */
	public void registerListener(UpdateListener registerListener);
	
}

