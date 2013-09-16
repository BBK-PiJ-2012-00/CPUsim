package code;

import javax.swing.SwingUtilities;

/*
 * Class to represent Memory Address Register
 */
public class MAR implements MemoryAddressRegister {
	private UpdateListener updateListener;	
	private int registerContents;
	
	
	public void registerListener(UpdateListener listener) {
		this.updateListener = listener;
	}
	
	
	
	@Override
	public void write(int address) {
		registerContents = address;
		fireUpdate(display());		
	}
	
	
	@Override
	public int read() {
		return registerContents;
	}
	
	
	/*
	 * Converts value on MAR to String format for GUI display.
	 */
	private String display() {
		String marDisplay = "";
		if (registerContents == -1) {
			marDisplay += "";
		}
		else {
			marDisplay += this.registerContents;
		}
		return marDisplay;
	}



	//GUI events should be handled from EDT only.
	//This adds the update event to the EDT thread
	private void fireUpdate(final String update) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(MAR.this, update);
				MAR.this.updateListener.handleUpDateEvent(updateEvent);	
			}
		});
	}
	
}
