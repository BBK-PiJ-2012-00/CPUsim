package code;

/*
 * Class to represent Memory Address Register.
 */
public class MAR implements MemoryAddressRegister {
	private int registerContents;
	
	@Override
	public void write(int address) {
		registerContents = address;
	}
	
	@Override
	public int read() {
		return registerContents;
	}

}
