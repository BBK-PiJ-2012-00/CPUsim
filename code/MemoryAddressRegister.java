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
	
	public String display();

	public void registerListener(UpdateListener registerListener);
	
	public void fireUpdate(String update);
}

