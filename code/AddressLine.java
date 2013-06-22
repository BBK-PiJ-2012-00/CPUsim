package code;

public class AddressLine implements BusLine {
	private int address;
	
	public void put(int address) {
		//Check address validity
		this.address = address;
	}
	
	public int read() {
		return this.address;
	}

}
