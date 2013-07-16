package code;

/*
 * TO DO: sort out concurrency issues surrounding bus and memory read operations. Should a read be atomic?
 * Or should the bus be able to be used in between? This would create confusion pedagogically; thus, all read
 * and write operations should be atomic and commandeer the bus for the duration of the operation.
 */

public class MemoryModule implements MainMemory {
	private static MainMemory memoryModule = null; //Ensures singleton
	
	//Array full of null values to start with - should it be initialised to hold 0s?
	private final Data[] MEMORY; //Array representing main memory itself/
	private int pointer; //Points to next available location for storage
	
	private Bus systemBus;//Reference to system bus
	
	private MemoryModule() { //Memory is a singleton to prevent duplicates and inconsistency
		//systemBus = SystemBus.getInstance(); //This causes stack overflow error; SystemBus creates new controlLine,
		//which in turn creates new memory module, which creates a new systemBus... until call stack overflows.
		MEMORY = new Data[100];
		pointer = 0;
	}
	
	public synchronized static MainMemory getInstance() {
		if (memoryModule == null) {
			memoryModule = new MemoryModule();
		}
		return memoryModule;
	}
	
	
	public void writeInstruction(Instruction instr, int index) { //For use by Loader to write instructions into memory
		MEMORY[index] = instr;  
	}
	
	
	public boolean notify(int address, Data data) { //Method to prompt memory to receive data from system bus (write)
		//No checking of address being empty or not; up to programmer
		if (address < 100 && address >= 0) {
			MEMORY[address] = data;
			return true;
		}
		//Throw an exception or simply return false?
		return false;
	}
	
	public boolean notify(int address) { //Absence of data indicates requested memory read as opposed to write (as in reality)
		Data dataRead;
		if (address < 100 && address >=0) {
			if (MEMORY[address] == null) {
				dataRead = new OperandImpl(0); //If the contents of address is null, read as 0 (as in reality)				
			}
			else {
				dataRead = MEMORY[address];
			}
			systemBus = SystemBus.getInstance(); //Putting this here breaks awkward chain, and is of no consequence 
			//as there is only ever one system bus instance with global access point.
			
			//As this is all performed by the same thread, there should be no issue!!
			//Don't issue this line here!! Return now, perform transferToCPU from control line?
			//It may be an idea to have the methods memoryRead() and memoryWrite() in SystemBus instead,
			//which would allow both to be atomic and encapsulate all actions pertaining to those operations.
			//Will the below line be executable? Bus is waiting on completion of this method!
			systemBus.transferToCPU(dataRead);//Transfers read data to system bus, subsequently to CPU
			return true;
		}
		//Throw exception if address invalid? This would disrupt program flow (possibly)
		return false;
	}
	

}
