package code;

/*
 * An interface to represent the data bus of the system bus.  The data bus carries data of type Data between
 * main memory and the CPU, coordinated by the control line.  Type Data is required because data lines will carry 
 * both Instructions and operands.
 */
public interface DataBus {

	/*
	 * A method used to put data on the data line.
	 * 
	 * @param Data value the data of type Data to be put on the data line.
	 */
	public void put(Data value);

	/*
	 * A method used to read data from the data line.
	 * 
	 * @return Data the data read from the line.
	 */
	public Data read();

	
	/*
	 * A method for registering an event listener object with the data bus,
	 * for GUI display purposes. Every time the contents of the data bus is updated,
	 * an update event is created and handled by the listener to change the GUI
	 * display accordingly.
	 * 
	 * @param UpdateListener listener the listener object to handle update events.
	 */
	public void registerListener(UpdateListener listener);
	
}
