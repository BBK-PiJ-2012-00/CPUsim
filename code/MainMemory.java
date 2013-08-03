package code;

/*
 * An interface to represent main memory. Main memory is implemented as an array of 100 slots, each
 * with the capacity for values up to 2^32 (to reflect the fact that the simulator is 32-bit). 
 * 
 * Main memory is in this case a singleton, despite that in reality memory modules can be swapped in and out.
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
	 * A method for use by the system bus; it prompts a memory write.
	 * 
	 *  @param int address the address the contents of the data line is to be written to.
	 *  @param Data data the data to be written from the data line to memory.
	 *  @return boolean true if the write is successful, false otherwise.
	 */
	public boolean notify(int address, Data data); //Method to prompt memory to receive data from system bus (write)
		//No checking of address being empty or not; up to programmer
		
	
	/*
	 * A method for use by the system bus; it prompts a memory read. The data read from a specified
	 * address in memory is then placed on the system bus for transfer to the CPU.
	 * 
	 * @param int address the address of the data to be read.
	 * @return boolean true if the read is successful, false otherwise.
	 */
	public boolean notify(int address); //Absence of data indicates requested memory read as opposed to write (as in reality)
		//Must return data to system bus for transfer back to cpu
	
	

	public int getPointer(); 
	
	public void resetPointer();
	
	public void clearMemory(); //To reset memory contents when loading a new program
		 //Resets each non-null address to null, effectively clearing memor contents
		
	
	public void loadMemory(Data[] programCode);
		
	
	//Has reference to memory port(s) -> one for input, one for output, or perhaps several for pipelining
	//Ports can deal with interface to bus
	
	//Memory addresses themselves are represented as indexes on an array(list)
	//Simple array --> no ordering, no moving items up when items are deleted, no growth: fixed size
	
	//Accessor/mutator methods can be synchronized to deal with concurrency issues brought about by
	//pipelining
	
	
	
	/*
	 * A wrapper class (Operand) is employed for all other data types (i.e. data that isn't an instructions): This
	 * avoids using an array of Object, which would entail casting and no security with regard
	 * to what can be added to the array.
	 */
	
	
	
	

}
