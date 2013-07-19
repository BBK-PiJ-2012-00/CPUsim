package code;

public class PC implements ProgramCounter {
	int nextInstructionPointer; //address of the next instruction to be fetched
	
	public int getValue() {
		return nextInstructionPointer;
	}
	
	public void incrementPC() {
		nextInstructionPointer++;
	}
	
	public void setPC(int address) { //Used to set PC pointer
		this.nextInstructionPointer = address;
	}
	
	

}
