package code;

/*
 * An interface to represent main memory. Main memory is implemented as an array of 100 slots, each
 * with the capacity for values up to 2^32 (to reflect the fact that the simulator is 32-bit). 
 * 
 * Main memory is in this case a singleton, despite that in reality memory modules can be swapped in and out.
 */

public interface MainMemory {
	
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
		
	
	//Has reference to memory port(s) -> one for input, one for output, or perhaps several for pipelining
	//Ports can deal with interface to bus
	
	//Memory addresses themselves are represented as indexes on an array(list)
	//Simple array --> no ordering, no moving items up when items are deleted, no growth: fixed size
	
	//Ports are perhaps an added complication? Each module simply has one BusControlLine reference
	
	//Accessor/mutator methods can be synchronized to deal with concurrency issues brought about by
	//pipelining
	
	//An int[] array as originally planned could be problematic; Instructions are to be stored in the array,
	//as are ints to be used as variables in programs.  It would be complex to translate an instruction to
	//an int to place in memory -> fields would become ambiguous as representation is not binary.
	//One possible solution is to have an array of MemoryStorable items, which implement that interface.
	//Another is to have a Data[] array, where a Data class is used to encapsulate thigns to be stored in 
	//memory; Data class would have two fields; int and Instruction, one of which will always be null.  It
	//then has a simple isInstruction() or isInt() method, to indicate what the data stored is; an int or
	//an instruction.  Instructions will be stored sequentially, with instance variables as ints being stored
	//"away" from the sequential instructions.
	//The other alternative is to have main memory as an array of type String, and the Strings representing 
	//the data can be manipulated accordingly.
	//Technically anything should be able to be stored in main memory, so Object might be appropriate.
	
	/*
	 * A wrapper class can be employed for all other data types: IntegerWrapper, or IntegerOperand, and this can
	 * implement Data.  The avoids using an array of Object, which would entail casting and no security with regard
	 * to what can be added to the array.
	 */
	
	/*
	 * Includes a cast from type Data to type Instruction.
	 * 
	 * @param int index the address of the instruction to be read from memory.
	 * @return Instruction the instruction read from the specified address.
	 */
	//public Instruction readInstruction(int index);
	
	/*
	 * Stored Operand type is accessed from location specified by index parameter; this is cast from Data
	 * to Operand, which is then unwrapped to return the integer.
	 * 
	 * @param int index the memory location of the operand.
	 * @return the integer stored at the specified location.
	 */
	//public int readInteger(int index);
	
	//public double readFloatingPoint();
	

}
