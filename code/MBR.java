package code;

import javax.swing.SwingUtilities;

/*
 * This class is now a singleton (there can only be once instance, with a global access point). Because this class
 * is referenced by the ControlLine class, as well as the ControlUnit class, it is important that only one instance
 * of this class can exist (it has already occurred during testing that two separate instances co-existed, causing 
 * data inegrity issues). As the ControlLine is accessible only via the BusController, it would mean that the MBR
 * would have to be instantiated in the ControlUnit first, and then passed as a constructor parameter to the BusController,
 * and then to the ControlUnit class.  As the BusController has no need for an MBR reference, this causes added complication.
 * Thus, a singleton would allow both the ControlUnit and the ControlLine access, without worrying about duplicate objects
 * and passing objects through classes which need not have any knowledge of the MBR.  The same goes for the MAR class.
 */

public class MBR implements MemoryBufferRegister {
	private Data registerContents;
	private UpdateListener updateListener;
	
	
	@Override
	public synchronized boolean write(Data data) { //Successful write returns true
		//Size is restricted in Instruction/Operand classes
		registerContents = data;
//		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, display());
//		updateListener.handleUpDateEvent(updateEvent);
		fireUpdate(display());
		if (registerContents == null) {
			return false;
		}
		return true;
	}
	
	@Override
	public synchronized Data read() { 
		return registerContents;
	}
	
	public String display() {
		String mbrDisplay = "";
		if (registerContents == null) {
			mbrDisplay += "";
		}
		else {
			mbrDisplay += "  " + this.registerContents.toString();
		}
		return mbrDisplay;
	}

	@Override
	public void registerListener(UpdateListener listener) {
		this.updateListener = listener;
		
	}
	
	//GUI events should be handled from EDT
	//This adds the update event to the EDT thread. Need to test this works on the GUI
	@Override
	public void fireUpdate(final String update) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(MBR.this, update);
				MBR.this.updateListener.handleUpDateEvent(updateEvent);	
			}
		});
	}



}
