package code;

public class MemoryModule implements MainMemory {
	private static MainMemory memoryModule = null; //Ensures singleton
	
	//Array full of null values to start with - should it be initialised to hold 0s?
	private final Data[] MEMORY; //Array representing main memory itself/
	private int pointer; //Points to next available location for storage
	
	private SystemBus systemBus; //Reference to system bus
	
	private MemoryModule() { //Memory is a singleton to prevent duplicates and inconsistency
		systemBus = SystemBus.getInstance();
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
			//Will the below line be executable? Bus is waiting on completion of this method!
			systemBus.transferToCPU(dataRead);//Transfers read data to system bus, subsequently to CPU
			return true;
		}
		//Throw exception if address invalid? This would disrupt program flow (possibly)
		return false;
	}
	

}
