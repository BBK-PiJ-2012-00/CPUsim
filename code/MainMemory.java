package code;

public interface MainMemory {
	
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
	

}
