package code;

public class ControlLine implements BusControlLine {
	private static ControlLine instance = null; //Reference to the one instance of this class
	private BusLine addrLine;
	private BusLine dataLine;
	
	/*
	 * The program must have one and only one System Bus so that all modules reference the exact same bus line objects.
	 * (Singleton) 
	 */
	private ControlLine() {
		addrLine = new AddressLine();
		dataLine = new DataLine();
	}
	
	public static ControlLine getInstance() { //May need to be synchronized depending on concurrency
		if (instance == null) {
			instance = new ControlLine();
		}
		return instance;
	}

	public void setAddressLine(int addr) {
		addrLine.put(addr);		
	}
	
	public int readAddressLine() {
		return addrLine.read();
	}
	
	public void setDataLine(int data) {
		dataLine.put(data);
	}
	
	public int readDataLine() {
		return dataLine.read();
	}
	
	
	
	

}
