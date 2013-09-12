package code;

import javax.swing.SwingUtilities;

public class AddressBusImpl implements AddressBus {
	private int address = -1;	
	private UpdateListener updateListener;
	
	@Override
	public void put(int address) {
		this.address = address;
		fireUpdate(this.display()); //Fire an update event every time bus is written to
	}
	
	
	@Override
	public int read() {
		fireUpdate(""); //Reset address bus display when read by another module
		return this.address;
	}
	
	
	@Override
	public void registerListener(UpdateListener listener) {
		this.updateListener = listener;
	}
	
	
	/*
	 * Returns a String representation of AddressBus contents for GUI display.
	 */
	private String display() {
		String displayString = "";
		if (this.address == -1) {
			return displayString; //If address line holds -1, signifies transfer from memory to MBR (no address).
		}
		displayString += this.address;
		return displayString;
	}
	
	
	//GUI events should be handled from EDT (Event Dispatch Thread) only.
	//This adds the update event to the EDT thread.	
	private void fireUpdate(final String update) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(AddressBusImpl.this, update);
				AddressBusImpl.this.updateListener.handleUpDateEvent(updateEvent);	
			}
		});
	}
	
}

