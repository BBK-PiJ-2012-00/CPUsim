package code;

/*
 * 
 * DataLine pending change to DataBus; typically, a data line in a parallel channel carries one bit,
 * and a data bus width of 32 lines is used to transmit a 32-bit word. Collectively these lines are called
 * a data bus.  DataBus would more accurately describe the function of this class; the DataBus class
 * can be interpreted as containing 32 data lines, enabling it to carry 32-bit words of data.  Thus, the data
 * field of this class representing the contents of the data bus is limited to a maximum value of 2^32.
 * 
 * An interface to represent a System Bus data line. Data lines carry data of type Data between main memory and 
 * the CPU. Type Data is required because data lines will carry both Instructions as well as other formats, such as
 * integers and possibly floating point in the future.
 */


public interface DataLine {

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
	
}
