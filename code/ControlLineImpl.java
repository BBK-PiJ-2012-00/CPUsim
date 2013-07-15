package code;

public class ControlLineImpl implements ControlLine {
	private AddressLine addressLine;
	private DataLine dataLine;
	public boolean inUse; //To ensure a write is followed by a read operation.
	
	private MainMemory memory;
	
	//References to CPU and memory
	
	
	public ControlLineImpl() {
		addressLine = new AddressLineImpl();
		dataLine = new DataLineImpl();
		memory = MemoryModule.getInstance();
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
		if (address == -1) { //Indicates transfer from memory to CPU (memory read)
			addressLine.put();
			dataLine.put(data);
			//Need to invoke memory to read the bus, as memory sits idle
			return memory.notify(address);
		}
		addressLine.put(address);
		dataLine.put(data);
		return memory.notify(address, data);		
	}
	
	
	/*
	 * The following methods may not be required
	 *
	 */
	
	
//	public int readAddressLine() {
//		return addrLine.read();
//	}
//	
//	
//	public int readDataLine() {
//		return dataLine.read();
//	}
//	
	public boolean isInUse() {
		return inUse;
	}

}
