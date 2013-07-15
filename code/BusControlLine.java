package code;

/*
 * Synchronous, clock-based coordination of the Bus would result in a more complicated than necessary design, so
 * it makes sense to adopt an asynchronous, handshaking-protocol method of sharing the Bus between the CPU and memory.
 * Using the Observer pattern would again introduce more complexity; proposed is the use of methods such as memoryWrite / 
 * memoryRead. Memory is idle, waiting
 * for requests from the processor.  The processor requests a read and then waits for data to arrive.
 * 
 * Processor issues read -> memory locates address and places contents on bus -> sends back to processor: processor
 * is idle waiting for the results of the read.  Memory invokes a sendToCPU() method to signal returning data. 
 */

public interface BusControlLine {
	
	
	/*
	 * To enforce data integrity during pipelined operation, the use of the bus is restricted to ensure write operations 
	 * are followed y read operations by the receiver module.  Setting the address and data lines must be an
	 * atomic operation for this reason.  If the bus is not already in use the method will return
	 * true to reflect a successful write, otherwise it returns false.  
	 * 
	 * @param int addr the address to be written to the address line.
	 * @param int data the data to be written to the data line.
	 * @return boolean if the write is successful, returns true; otherwise, false.
	 */
	public boolean writeToBus(int addr, int data); //This may not work -> addr and data come from MAR/MBR: separate objects
	//This is where a port might come in -> MBR/MAR pass data to the port seperately, which then interacts with bus
	
	public int readAddressLine();
	
	public int readDataLine();
	
	/*
	 * This pertains to a boolean inUse field in the ControlLine class; it is important to control the use of the
	 * System Bus lines.  When one module has written to the bus lines, the information must be read by the other module
	 * before anything subsequent can be written to the bus line.  The boolean flag should facilitate this.
	 */
	public boolean isInUse();

	/*
	 * Think about concurrency! Bus is an object used to pass data and addresses - there should be multiple address/data
	 * lines, and corresponding control lines to allow pipelined access.
	 * Race condition problem:
	 * There must also be a way of ensuring that when one module has placed something on the bus, the other module must
	 * perform a read before the bus can be written to again... so there should perhaps be an inUse boolean flag to control
	 * this.
	 * What gets written must be read before anyone can write to the bus again.
	 */
}
