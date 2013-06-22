package code;

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
	public boolean writeToBus(int addr, int data);

		
	/*
	 * To enforce data integrity during pipelined operation, use of the bus is restricted to ensure write operations are 
	 * followed by read operations by the receiver module.  If the bus is not already in use, the method will return true
	 * to reflect a successful write, otherwise it returns false.
	 * 
	 * @return boolean if the write is successful, true; otherwise, false.
	 * @param int addr the address to be put on the address line.
	 */
	public boolean setAddressLine(int addr);
	
	public int readAddressLine();
	
	/*
	 * To enforce data integrity during pipelined operation, use of the bus is restricted to ensure write operations are 
	 * followed by read operations by the receiver module.  If the bus is not already in use, the method will return true
	 * to reflect a successful write, otherwise it returns false.
	 * 
	 * @return boolean if the write is successful, true; otherwise, false.
	 * @param int data the data to be put on the data line.
	 */
	public boolean setDataLine(int data);
	
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
