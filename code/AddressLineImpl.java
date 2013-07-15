package code;

public class AddressLineImpl implements AddressLine {
	private int address;
	
	@Override
	public void put(int address) {
		//Check address validity?
		this.address = address;
	}
	
	@Override
	public void put() {
		this.address = -1; //-1 not a main memory address; used for transfers to MBR of CPU.
	}
	
	@Override
	public int read() {
		return this.address;
	}	
}
