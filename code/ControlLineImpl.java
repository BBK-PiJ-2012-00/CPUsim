package code;

public class ControlLineImpl implements ControlLine {
	private AddressLine addressLine;
	private DataLine dataLine;
	//public boolean inUse; //To ensure a write is followed by a read operation.
	
	private MainMemory memory;
	
	private MemoryBufferRegister mockMBR = new MBR(); //Instantiation and reference will be handled properly later; mock MBR for testing.	
	//References to CPU (MBR/MAR) and memory
	
	public Data accessMockMBR() { //For testing purposes only
		return mockMBR.read();
	}
	
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
		if (address == -1) { //Indicates transfer from memory to CPU (2nd phase of memory read; delivery from memory to MBR)
			//no need to place address on address bus -> if address field in AddressLine is null/0, this signifies delivery
			//to CPU as opposed to memory (and is true to reality).
			dataLine.put(data); //This is functionally redundant, but will be useful for GUI animation of bus lines
			return this.deliverToMBR(); //Complete read operation. 
		}
		else if (data == null) { //Signifies first phase of a read; MAR places address on address line, prompting memory to
			//place contents of the address on the address line onto data line for return to MBR.
			addressLine.put(address);
			return this.deliverToMemory(true);
		}
		//Memory write code:
		addressLine.put(address);
		dataLine.put(data);
		return this.deliverToMemory(false);	//False -> not a read operation (write operation)	
	}
	
	public boolean deliverToMBR() { //Prompts dataLine to load its value into MBR, completing memory read operation
		//System.out.println("in deliverToMBR(); dataLine value written to MBR will be: " + dataLine.read().toString());
		return mockMBR.write(dataLine.read());		
	}
	
	
	
	public boolean deliverToMemory(boolean isRead) { //Prompts dataLine to load value into memory, completing memory write operation
		if (isRead) {
			return memory.notify(addressLine.read());
		}
		return memory.notify(addressLine.read(), dataLine.read());
	}

}
