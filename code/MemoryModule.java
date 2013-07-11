package code;

public class MemoryModule implements MainMemory {
	private Data[] memoryContents = new Data[100]; //Array representing main memory itself
	private int pointer; //Points to next available location
	
	
	@Override
	public Instruction readInstruction(int index) {
		if (memoryContents[index].isInstruction()) { //Check contents of address is of type Instruction
			Instruction instr = (Instruction) memoryContents[index]; //Cast to instruction required; stored as type Data
			return instr;
		}
		//Perhaps throw exception if not of type instruction?
		return null;
	}

	@Override
	public int readInteger(int index) {
		if (memoryContents[index].isInteger()) {
			Operand integer = (Operand) memoryContents[index];
			return integer.unwrapInteger(); //Unwraps the integer from Operand wrapper class
		}
		return 0;
	}

	@Override
	public double readFloatingPoint() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void writeInstruction(Instruction instr, int index) { //index will be specified by destination field in TransferInstr
		
	}
	
	public void writeInteger

}
