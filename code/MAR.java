package code;

/*
 * Class to represent Memory Address Register.
 */
public class MAR {
	private int registerContents;
	
	public void write(int address) {
		registerContents = address;
	}
	
	public int read() {
		return registerContents;
	}

}
