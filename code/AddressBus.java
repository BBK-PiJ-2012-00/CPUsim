package code;

/*
 * AddressLine now called AddressBus; typically, an address line in a parallel channel carries one bit,
 * and an address bus width of 32 lines is used to transmit a 32-bit word. Collectively these lines are called
 * an address bus.  AddressBus would more accurately describe the function of this class; the AddressBus class
 * can be interpreted as containing 32 address lines, enabling it to carry 32-bit addresses.  Thus, the address
 * field of this class representing the contents of the address bus is limited to a maximum value of 2^32.
 * 
 * An interface for the address line component of the System Bus. The address line holds an address value
 * as an integer when the address is a location in main memory. The address line holds no value when the 
 * System Bus is making a transfer from main memory to the CPU, as all data is delivered into the Memory Buffer
 * Register.
 */
public interface AddressBus {
	
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

	public String display();

	public void registerListener(UpdateListener listener);		

}
