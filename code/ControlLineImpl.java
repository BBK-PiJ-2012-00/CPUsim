package code;

public class ControlLineImpl implements ControlLine {
	private AddressLine addressLine;
	private DataLine dataLine;
	public boolean inUse; //To ensure a write is followed by a read operation.
	
	//References to CPU and memory
	
	
	public ControlLineImpl() {
		addressLine = new AddressLineImpl();
		dataLine = new DataLineImpl();
	}
	
	/* 
	 * ControlLine implements only a single writeToBus() method, purposefully not
	 * differentiating between transfers from CPU to memory, and memory to CPU. This is
	 * because two separate methods for these two operations would add complexity to
	 * the concurrency issues surrounding the use of the SystemBus when operating in
	 * pipelined mode. It is important that all bus data's integrity is maintained,
	 * and that two threads are prevented from accessing this method at the same time.
	 * 
	 * An address value of -1 is used to signify a transfer from main memory to CPU, as
	 * -1 is a non-existent memory address. Any other value (0 or greater) signifies a
	 * transfer from CPU to the memory location specified.
	 */
	synchronized public boolean writeToBus(int address, Data data) {
		if (address == -1) { //Indicates transfer from memory to CPU
			addressLine.put();
			dataLine.put(data);
			//Need to invoke memory to read the bus, as memory sits idle
			//return Memory.alert() -> a method on memory that returns a boolean value
		}
		addressLine.put(address);
		dataLine.put(data);
		//return Memory.alert();		
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
//	synchronized public boolean writeToBus(boolean cpuIssue, boolean memoryIssue, int addr, int data) { //An atomic operation, to preserve data integrity
//		if (!inUse) {
//			if (cpuIssue) { //this means CPU has issued the write and memory should read from the bus
//				inUse = true;
//				addrLine.put(addr);//The memory address where the data is to be stored
//				dataLine.put(data);
//				//call method on memory to read from bus?
//			}
//			inUse = true;
//			addrLine.put(addr);
//			dataLine.put(data);
//			return true;
//		}
//		return false;
//	}
	
	
	/*
	 * The following methods may not be required
	 *
	 */
	
	
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
