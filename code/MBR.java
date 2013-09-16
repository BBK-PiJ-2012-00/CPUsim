package code;

import javax.swing.SwingUtilities;


public class MBR implements MemoryBufferRegister {
	private Data registerContents;
	private UpdateListener updateListener;
	
	
	@Override
	public boolean write(Data data) {
		registerContents = data;
		fireUpdate(display());
		if (registerContents == null) {
			return false;
		}
		return true;
	}
	
	
	@Override
	public Data read() { 
		return registerContents;
	}
	
	/*
	 * For GUI display purposes; displays MBR contents in String
	 * format.
	 */
	private String display() {
		String mbrDisplay = "";
		if (registerContents == null) {
			mbrDisplay += "";
		}
		else {
			mbrDisplay += this.registerContents.toString();
		}
		return mbrDisplay;
	}
	

	@Override
	public void registerListener(UpdateListener listener) {
		this.updateListener = listener;
		
	}
	
	
	//GUI events should be handled from EDT only.
	//This adds the update event to the EDT thread.
	private void fireUpdate(final String update) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(MBR.this, update);
				MBR.this.updateListener.handleUpDateEvent(updateEvent);	
			}
		});
	}



}
