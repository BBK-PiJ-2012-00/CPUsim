package code;

import javax.swing.SwingUtilities;

public class DataBusImpl implements DataBus {
	private Data data;
	
	private UpdateListener updateListener;

	@Override
	public void put(Data value) {
		data = value;
		fireUpdate(display());
	}
	

	@Override
	public Data read() {
		fireUpdate("");
		return data;
	}
	
	
	@Override
	public void registerListener(UpdateListener listener) {
		this.updateListener = listener;
	}
	
	
	/*
	 * Used for formatting data field for GUI display.
	 */
	private String display() {
		String displayString = "";
		if (data == null) {
			return displayString;
		}
		displayString += data.toString();
		return displayString;
	}
	
	
	//GUI events should be handled from EDT only.
	//This adds the update event to the EDT thread.
	private void fireUpdate(final String update) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(DataBusImpl.this, update);
				DataBusImpl.this.updateListener.handleUpDateEvent(updateEvent);	
			}
		});
	}


}
