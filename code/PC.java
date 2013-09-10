package code;

import javax.swing.SwingUtilities;

public class PC implements ProgramCounter {
	private int nextInstructionPointer; //address of the next instruction to be fetched
	private UpdateListener updateListener;
	
	public int getValue() {
		return nextInstructionPointer;
	}
	
	public void incrementPC() {
		nextInstructionPointer++;
//		System.out.println("PC incremented to address: " + nextInstructionPointer);
//		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, "" + nextInstructionPointer);
//		updateListener.handleUpDateEvent(updateEvent);
		fireUpdate("" + nextInstructionPointer);
	}
	
	public void setPC(int address) { //Used to set PC pointer
		this.nextInstructionPointer = address;
		//System.out.println("PC set to address " + address);
//		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, "" + nextInstructionPointer);
//		updateListener.handleUpDateEvent(updateEvent);
		fireUpdate("" + nextInstructionPointer);
	}
	
	public String display() {
		String display = "";
		display += nextInstructionPointer;
		return display;
	}
	
	public void registerListener(UpdateListener listener) {
		this.updateListener = listener;
	}
	
	//GUI events should be handled from EDT
	//This adds the update event to the EDT thread. Need to test this works on the GUI
	@Override
	public void fireUpdate(final String update) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(PC.this, update);
				PC.this.updateListener.handleUpDateEvent(updateEvent);	
			}
		});
	}
	

}
