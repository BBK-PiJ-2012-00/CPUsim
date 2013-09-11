package code;

import javax.swing.SwingUtilities;

public class DataBusImpl implements DataBus {
	private Data data;
	
	private UpdateListener updateListener;

	@Override
	public void put(Data value) {
		data = value;
//		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, display());
//		updateListener.handleUpDateEvent(updateEvent);
		fireUpdate(display());
	}

	@Override
	public Data read() {
//		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, ""); //Reset data bus display after another module reads from it
//		updateListener.handleUpDateEvent(updateEvent);
		fireUpdate("");
		return data;
	}
	
	@Override
	public void registerListener(UpdateListener listener) {
		this.updateListener = listener;
	}
	
	@Override
	public String display() {
		String displayString = "";
		if (data == null) {
			return displayString;
		}
		displayString += data.toString();
		return displayString;
	}
	
	//GUI events should be handled from EDT
	//This adds the update event to the EDT thread. Need to test this works on the GUI
	@Override
	public void fireUpdate(final String update) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(DataBusImpl.this, update);
				DataBusImpl.this.updateListener.handleUpDateEvent(updateEvent);	
			}
		});
	}


}
