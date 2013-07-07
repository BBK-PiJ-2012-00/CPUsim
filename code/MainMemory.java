package code;

public interface MainMemory {
	
	//Has reference to memory port(s) -> one for input, one for output, or perhaps several for pipelining
	//Ports can deal with interface to bus
	
	//Memory addresses themselves are represented as indexes on an array(list)
	//Simple array --> no ordering, no moving items up when items are deleted, no growth: fixed size
	
	//Ports are perhaps an added complication? Each module simply has one BusControlLine reference
	
	//Accessor/mutator methods can be synchronized to deal with concurrency issues brought about by
	//pipelining
	
	

}
