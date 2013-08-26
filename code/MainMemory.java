package code;

/*
 * An interface to represent main memory. Main memory is implemented as an array of 100 slots, each
 * with the capacity for values up to 2^32 (to reflect the fact that the simulator is 32-bit). 
 * 
 */

public interface MainMemory {
	
	public Data accessAddress(int index); //For testing purposes; pending deletion. 
	
	/*
	 * A method used by the Loader class for the purpose of writing instructions into memory before
	 * program execution begins.
	 * 
	 * @param Instruction instr the instruction to be written into memory.
	 * @param int index the memory location where the instruction is to be written.
	 */
	public void writeInstruction(Instruction instr, int index); //For use by Loader to write instructions into memory		
	
	/*
	 * A method invoked by the system bus for writing data to memory; it prompts memory to receive data
	 * from the system bus and write it to the address given as as a method parameter.
	 * 
	 *  @param int address the address the contents of the data line is to be written to.
	 *  @param Data data the data to be written from the data line to memory.
	 *  @return boolean true if the write is successful, false otherwise.
	 */
	public boolean notifyWrite(int address, Data data); //Method to prompt memory to receive data from system bus (write)
		//No checking of address being empty or not; up to programmer
		
	
	/*
	 * A method invoked by the system bus to prompt a memory read. Data is 
	 * read from the memory address specified by the system bus, and that data 
	 * is then placed on the system bus for transfer to the CPU.
	 * 
	 * @param int address the address of the data to be read.
	 * @return boolean true if the read is successful, false otherwise.
	 */
	public boolean notifyRead(int address); //Absence of data indicates requested memory read as opposed to write (as in reality)
		//Must return data to system bus for transfer back to cpu
	
	

	public int getPointer(); 
	
	public void resetPointer();
	
	public void clearMemory(); //To reset memory contents when loading a new program
		 //Resets each non-null address to null, effectively clearing memor contents
		
	
	public void loadMemory(Data[] programCode);
	
	public void registerListener(UpdateListener listener);
	
	public String display();
	
	public void registerBusController(BusController systemBusController);
	

}
