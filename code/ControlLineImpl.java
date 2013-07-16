package code;

public class ControlLineImpl implements ControlLine {
	private AddressLine addressLine;
	private DataLine dataLine;
	public boolean inUse; //To ensure a write is followed by a read operation.
	
	private MainMemory memory;
	
	private MBR mockMBR = new MBR(); //Instantiation and reference will be handled properly later; mock MBR for testing.	
	//References to CPU (MBR/MAR) and memory
	
	
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
	synchronized public boolean writeToBus(int address, Data data) { //This method now is responsibly only for writing to bus, with accessing lines
		//delegated to deliverTo...() methods below. Better encapsulation and separation of concerns, as well as better use of bus lines.
		if (address == -1) { //Indicates transfer from memory to CPU (memory read)
			//addressLine.put(); //addressLine.put() can perhaps be got rid of -> if address field in AddressLine is null/0, this signifies delivery
			//to CPU as opposed to memory (and is true to reality).
			dataLine.put(data); //This is functionally redundant, but will be useful for GUI animation of bus lines
			//Need to invoke memory to read the bus, as memory sits idle
			return this.deliverToMBR(); //Complete read operation. 
		}
		//Memory write code:
		addressLine.put(address); //functionally redundant!
		dataLine.put(data);
		return this.deliverToMemory();		
	}
	
	private boolean deliverToMBR() { //Prompts dataLine to load its value into MBR, completing memory read operation
		return mockMBR.write(dataLine.read());		
	}
	
	private boolean deliverToMemory() { //Prompts dataLine to load value into memory, completing memory write operation
		return memory.notify(addressLine.read(), dataLine.read());
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
