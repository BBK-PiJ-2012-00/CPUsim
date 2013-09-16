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
		fireUpdate("" + nextInstructionPointer);
	}
	
	
	
	public void setPC(int address) { //Used to set PC pointer
		this.nextInstructionPointer = address;
		fireUpdate("" + nextInstructionPointer);
	}
	
	
	
	public void registerListener(UpdateListener listener) {
		this.updateListener = listener;
	}
	
	
	
	//GUI events should be handled from EDT only.
	//This adds the update event to the EDT thread.
	private void fireUpdate(final String update) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(PC.this, update);
				PC.this.updateListener.handleUpDateEvent(updateEvent);	
			}
		});
	}
	

}
