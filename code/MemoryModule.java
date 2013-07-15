package code;

public class MemoryModule implements MainMemory {
	private Data[] memoryContents = new Data[100]; //Array representing main memory itself
	private int pointer = 0; //Points to next available location for storage
	
	private SystemBus systemBus = SystemBus.getInstance(); //Reference to system bus
		
	
	
	
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
		memoryContents[index] = instr;  
	}
	
	
	public boolean notify(int address, Data data) { //Method to prompt memory to receive data from system bus (write)
		//No checking of address being empty or not; up to programmer
		if (address < 100 && address >= 0) {
			memoryContents[address] = data;
			return true;
		}
		//Throw an exception or simply return false?
		return false;
	}
	
	public boolean notify(int address) { //Absence of data indicates requested memory read as opposed to write (as in reality)
		//Must return data to system bus for transfer back to cpu
		if (address < 100 && address >=0) {
			Data dataRead = memoryContents[address];
			systemBus.transferToCPU(dataRead);
			return true;
		}
		//Throw exception if address invalid? This would disrupt program flow (possibly)
		return false;
	}
	

}
