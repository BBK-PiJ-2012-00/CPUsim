package code;

public class ControlLineImpl implements ControlLine {
	private static ControlLine instance = null; //Reference to the one instance of this class
	private BusLine addrLine;
	private BusLine dataLine;
	public boolean inUse; //To ensure a write is followed by a read operation.
	
	//References to CPU and memory
	
	
	/*
	 * The program must have one and only one System Bus so that all modules reference the exact same bus line objects.
	 * (Singleton) --> singleton now implemented at higher level; one System Bus class with potential for multiple lines.
	 */
	public ControlLineImpl() {
		addrLine = new AddressLine();
		dataLine = new DataLine();
	}
	
	public static ControlLine getInstance() { //May need to be synchronized depending on concurrency
		if (instance == null) {
			instance = new ControlLineImpl();
		}
		return instance;
	}
	
	/*
	 * Should this method also invoke the memory to read the bus? A write to the bus by the CPU should be
	 * followed by memory reading from the bus, and vice versa. 
	 * 
	 *  CPUissue/memoryIssue booleans; both cannot be true or false at same time. Simplifies bus; two seperate
	 *  methods would require more coordination (synchronization) issues. 
	 * (non-Javadoc)
	 * @see code.BusControlLine#writeToBus(int, int)
	 */
	synchronized public boolean writeToBus(boolean cpuIssue, boolean memoryIssue, int addr, int data) { //An atomic operation, to preserve data integrity
		if (!inUse) {
			if (cpuIssue) { //this means CPU has issued the write and memory should read from the bus
				inUse = true;
				addrLine.put(addr);//The memory address where the data is to be stored
				dataLine.put(data);
				//call method on memory to read from bus?
			}
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
