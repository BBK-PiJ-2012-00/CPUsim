package code;

public class PC implements ProgramCounter {
	private int nextInstructionPointer; //address of the next instruction to be fetched
	private UpdateListener updateListener;
	
	public int getValue() {
		return nextInstructionPointer;
	}
	
	public void incrementPC() {
		nextInstructionPointer++;
		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, "" + nextInstructionPointer);
		updateListener.handleUpDateEvent(updateEvent);
	}
	
	public void setPC(int address) { //Used to set PC pointer
		this.nextInstructionPointer = address;
		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, "" + nextInstructionPointer);
		updateListener.handleUpDateEvent(updateEvent);
	}
	
	public String display() {
		String display = "";
		display += nextInstructionPointer;
		return display;
	}
	
	public void registerListener(UpdateListener listener) {
		this.updateListener = listener;
	}
	

}
