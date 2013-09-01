package code;

public class DataBusImpl implements DataBus {
	private Data data;
	
	private UpdateListener updateListener;

	@Override
	public void put(Data value) {
		data = value;
		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, display());
		updateListener.handleUpDateEvent(updateEvent);
	}

	@Override
	public Data read() {
		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, ""); //Reset data bus display after another module reads from it
		updateListener.handleUpDateEvent(updateEvent);
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


}
