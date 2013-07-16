package code;

/*
 * An interface for the address line component of the System Bus. The address line holds an address value
 * as an integer when the address is a location in main memory. The address line holds no value when the 
 * System Bus is making a transfer from main memory to the CPU, as all data is delivered into the Memory Buffer
 * Register.
 */
public interface AddressLine {
	
	/*
	 * A method to put a main memory address location on the address line.
	 * 
	 * @param int address the memory address destination of the bus transfer.
	 */
	public void put(int address);
	
//	/*
//	 * A method used to signify a transfer from memory back to MBR of CPU. 
//	 * May not be required long term. Sets address to -1, which is not a valid
//	 * main memory address location. 
//	 */
//	public void put();
	
	/*
	 * A method for reading an address from the address line of the bus.
	 * 
	 * @return int the address on the address line.
	 */
	public int read();		

}
