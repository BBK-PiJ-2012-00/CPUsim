package code;

/*
 * TO DO: sort out concurrency issues surrounding bus and memory read operations. Should a read be atomic?
 * Or should the bus be able to be used in between? This would create confusion pedagogically; thus, all read
 * and write operations should be atomic and commandeer the bus for the duration of the operation.
 */

public class MemoryModule implements MainMemory {
	private static MainMemory memoryModule = null; //Keeps track of singleton
	
	//Array full of null values to start with - should it be initialised to hold 0s?
	private final Data[] MEMORY; //Array representing main memory itself/
	private int pointer; //Points to next available location for storage	
	private BusController systemBusController; //Reference to system bus
	
	
	
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
	
	public Data accessAddress(int index) { //Primarily for testing purposes, not for use by program.
		return MEMORY[index];
	}
	
	//May take an array as a parameter, depdening on Loader implementation
	public void writeInstruction(Instruction instr, int index) { //For use by Loader to write instructions into memory
		MEMORY[index] = instr; 
		//As this is only used by loader, it should take the address of the first instruction loaded into memory
		//and transfer this address via the address bus to the MAR, and on to the PC to begin execution.
		//A method can be called to do this; transferFirstInstruction().
		//When a user selects a program to load, this should prompt the sim to the point where the PC is loaded
		//but is waiting to begin execution.
		//So it simply causes MAR to set the PC, and then the sim waits for user to press 'start'.
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
			systemBusController = SystemBusController.getInstance(); //Putting this here breaks awkward creation chain, and is of no consequence 
			//as there is only ever one system bus instance with global access point.
			
			//As this is all performed by the same thread, there should be no issue!!
			//Don't issue this line here!! Return now, perform transferToCPU from control line?
			//It may be an idea to have the methods memoryRead() and memoryWrite() in SystemBus instead,
			//which would allow both to be atomic and encapsulate all actions pertaining to those operations.
			//Will the below line be executable? Bus is waiting on completion of this method!
			systemBusController.transferToCPU(dataRead);//Transfers read data to system bus, subsequently to CPU
			return true;
		}
		//Throw exception if address invalid? This would disrupt program flow (possibly)
		return false;
	}
	

}
