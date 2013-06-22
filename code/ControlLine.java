package code;

public class ControlLine implements BusControlLine {
	private static ControlLine instance = null; //Reference to the one instance of this class
	private BusLine addrLine;
	private BusLine dataLine;
	public boolean inUse; //To ensure a write is followed by a read operation.
	
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
	
	synchronized public boolean writeToBus(int addr, int data) { //An atomic operation, to preserve data integrity
		if (!inUse) {
			inUse = true;
			addrLine.put(addr);
			dataLine.put(data);
			return true;
		}
		return false;
	}
	
	public int readAddressLine() {
		return addrLine.read();
	}
	
	
	public int readDataLine() {
		return dataLine.read();
	}
	
	public boolean isInUse() {
		return inUse;
	}

	
	
	
	

}
