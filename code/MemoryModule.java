package code;

public class MemoryModule implements MainMemory {
	private Data[] memoryContents = new Data[100]; //Array representing main memory itself
	private int pointer; //Points to next available location
	
	
	@Override
	public Instruction readInstruction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int readInteger() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double readFloatingPoint() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void writeInstruction(Instruction instr, int index) { //unclear if index should be explicitly specified, or done using 
																//pointer field
		
	}

}
