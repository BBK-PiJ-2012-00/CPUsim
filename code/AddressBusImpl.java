package code;

import javax.swing.SwingUtilities;

public class AddressBusImpl implements AddressBus {
	private int address = -1;
	
	private UpdateListener updateListener;
	
	@Override
	public void put(int address) {
		this.address = address; //Fire an update event every time bus is written to
//		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, this.display());
//		updateListener.handleUpDateEvent(updateEvent);
		fireUpdate(this.display());
	}
	
	
	@Override
	public int read() {
//		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, ""); //Reset address bus display when read by another module
//		updateListener.handleUpDateEvent(updateEvent);
		fireUpdate("");
		return this.address;
	}
	
	
	@Override
	public void registerListener(UpdateListener listener) {
		this.updateListener = listener;
	}
	
	
	@Override
	public String display() {
		String displayString = "";
		if (this.address == -1) {
			return displayString; //If address line holds -1, signifies transfer from memory to MBR (no address).
		}
		displayString += this.address;
		return displayString;
	}
	
	//GUI events should be handled from EDT
	//This adds the update event to the EDT thread. Need to test this works on the GUI
	@Override
	public void fireUpdate(final String update) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(AddressBusImpl.this, update);
				AddressBusImpl.this.updateListener.handleUpDateEvent(updateEvent);	
			}
		});
	}
	
}

