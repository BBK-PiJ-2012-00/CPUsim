package code;

/*
 * Interface for general purpose registers.  General purpose registers are implemented as 
 * an array of type Data. General purpose registers are true general purpose; they are not
 * restricted to hold only Operands, allowing more flexibility.
 */

public interface RegisterFile {
	
	/*
	 * A method for writing to a general purpose register.
	 * 
	 * @param int index the reference to the specific register in the array of
	 * registers.
	 * @param Data data the value to be written to the specified register.
	 */
	public void write(int index, Data data);
	
	
	/*
	 * A method for reading the contents of a general purpose register.
	 * 
	 * @param int index the reference to the register to be read from.
	 */
	public Data read(int index);
	
	
	/*
	 * A method for registering an event listener object with the RegisterFile,
	 * for GUI display purposes. Every time the contents of a RegisterFile index is updated,
	 * an update event is created and handled by the listener to change the GUI
	 * display accordingly.
	 * 
	 * @param UpdateListener listener the listener object to handle update events.
	 */
	public void registerListener(UpdateListener listener);
	
	
	


}
