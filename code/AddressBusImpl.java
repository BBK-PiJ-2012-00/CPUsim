package code;

public class AddressBusImpl implements AddressBus {
	private int address = -1;
	
	@Override
	public void put(int address) {
		//Check address validity?
		this.address = address;
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
		return this.address;
	}	
}
