package code;

import javax.swing.SwingUtilities;

/*
 * Class to represent Memory Address Register. This is a singleton to ensure only one instance.  See MBR
 * class for motivation.
 */
public class MAR implements MemoryAddressRegister {
	private UpdateListener updateListener;
	
	private int registerContents;
	
	
	public void registerListener(UpdateListener listener) {
		this.updateListener = listener;
	}
	
	/*
	 * 	 * (non-Javadoc)
	 * @see code.MemoryAddressRegister#write(int)
	 * Writing to MAR should prompt one of two things: transfer of the contents to PC,
	 * or activation of system bus -> control unit can have micro-methods which handle
	 * data flow. These can then be incorporated major methods representing stages.
	 * Control unit is responsible for coordination.
	 */
	@Override
	public void write(int address) {
		registerContents = address;
//		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, display());
//		updateListener.handleUpDateEvent(updateEvent);
		fireUpdate(display());
		
	}
	
	@Override
	public int read() {
		return registerContents;
	}
	
	public String display() {
		String marDisplay = "";
		if (registerContents == -1) {
			marDisplay += "";
		}
		else {
			marDisplay += this.registerContents;
		}
		return marDisplay;
	}



	//GUI events should be handled from EDT
	//This adds the update event to the EDT thread. Need to test this works on the GUI
	@Override
	public void fireUpdate(final String update) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(MAR.this, update);
				MAR.this.updateListener.handleUpDateEvent(updateEvent);	
			}
		});
	}
	
}
