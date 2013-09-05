package code;

public class AddressBusImpl implements AddressBus {
	private int address = -1;
	
	private UpdateListener updateListener;
	
	@Override
	public void put(int address) {
		this.address = address; //Fire an update event every time bus is written to
		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, this.display());
		updateListener.handleUpDateEvent(updateEvent);
	}
	
	
	@Override
	public int read() {
		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, ""); //Reset address bus display when read by another module
		updateListener.handleUpDateEvent(updateEvent);
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
	
}

