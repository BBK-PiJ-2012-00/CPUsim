package code;

public class AddressBusImpl implements AddressBus {
	private int address = -1;
	
	private UpdateListener updateListener;
	
	@Override
	public void put(int address) {
		//Check address validity?
		this.address = address; //Fire an update event every time bus is written to
		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, this.display());
	}
	
	/*
	 * Pending deletion; not required as default address is now set to -1, signalling that CPU is destination of transfer.
	 */
//	@Override
//	public void put() {
//		this.address = -1; //-1 not a main memory address; used for transfers to MBR of CPU. Cannot leave as default 0, as 0 is
//		//a valid memory address.  Set default to -1 instead!
//	}
	
	@Override
	public int read() {
		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, ""); //Reset address bus display when read by another module
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
