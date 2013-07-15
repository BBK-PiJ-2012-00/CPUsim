package code;

/*
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
