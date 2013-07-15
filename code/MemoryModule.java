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
	
	
	
	
//	@Override
//	public Instruction readInstruction(int index) {
//		if (memoryContents[index].isInstruction()) { //Check contents of address is of type Instruction
//			Instruction instr = (Instruction) memoryContents[index]; //Cast to instruction required; stored as type Data
//			return instr;
//		}
//		//Perhaps throw exception if not of type instruction?
//		return null;
//	}

//	@Override
//	public int readInteger(int index) { //Index arrives via system bus address line
//		if (memoryContents[index].isInteger()) {
//			Operand integer = (Operand) memoryContents[index];
//			return integer.unwrapInteger(); //Unwraps the integer from Operand wrapper class
//		}
//		return 0;
//	}

//	@Override
//	public double readFloatingPoint() {
//		// TODO Auto-generated method stub
//		return 0;
//	}
	
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
		//Must return data to system bus for transfer back to cpu
		Data dataRead;
		if (address < 100 && address >=0) {
			if (MEMORY[address] == null) {
				dataRead = new OperandImpl(0); //If the contents of address is null, read as 0 (as in reality)				
			}
			else {
				dataRead = MEMORY[address];
			}
			systemBus.transferToCPU(dataRead);
			return true;
		}
		//Throw exception if address invalid? This would disrupt program flow (possibly)
		return false;
	}
	

}
