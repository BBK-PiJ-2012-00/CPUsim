package code;

/* 
 * An interface for the address bus component of the system bus. The address line holds an address value
 * as an integer when the address is a location in main memory. The address line holds a value of -1 when the 
 * system bus is making a transfer from main memory to the CPU, as all data is delivered into the Memory Buffer
 * Register.
 */
public interface AddressBus {
	
	/*
	 * A method to put a main memory address location on the address line.
	 * 
	 * @param int address the memory address destination of the bus transfer.
	 */
	public void put(int address);
	
	/*
	 * A method for reading an address from the address line of the bus.
	 * 
	 * @return int the address on the address line.
	 */
	public int read();

	/*
	 * Returns a String representation of AddressBus contents for GUI display.
	 * 
	 * @return String the String representation of the AddressBus contents.
	 */
	public String display();

	/*
	 * A method for registering an event listener object with the address bus,
	 * for GUI display purposes. Every time the contents of this line is updated,
	 * an update event is created and handled by the listener to change the GUI
	 * display accordingly.
	 * 
	 * @param UpdateListener listener the listener object to handle update events.
	 */
	public void registerListener(UpdateListener listener);		

}
