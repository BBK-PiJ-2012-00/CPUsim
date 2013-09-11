package code;

/*
 * An interface for a System Bus control line. 
 */

public interface ControlLine {

	
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
	 * 
	 * @param int address the address to be written to the address line.
	 * @param Data data the data to be written to the data line.
	 * @return boolean true if the write is successful, false otherwise.
	 */
	public boolean writeToBus(int address, Data data);
	
	/*
	 * A method that prompts the data line of the system bus to load its value into the CPU's memory 
	 * buffer register (MBR), as the final stage of a memory read operation.
	 * 
	 * @return boolean true if the method completed successfully, false otherwise.
	 */
	public boolean deliverToMBR(); //Prompts dataLine to load its value into MBR, completing memory read operation
	
	
	/*
	 * A method that prompts either the first phase of a memory read, or the final stage of a memory write.
	 * For the first stage of a memory read, the address value on the address line is passed (delivered) to main memory, 
	 * so that the value held at that address may be loaded onto the address line (during the second stage of
	 * a memory read). For the final stage of a memory write, the  data line is prompted to load its value into 
	 * the memory location specified by the address line. These two operations are differentiated by the boolean
	 * parameter passed into the method when it is called.
	 * 
	 * @param boolean isRead if the operation is to be a memory read, the value is true, otherwise false. 
	 * @return boolean returns true if the method completed successfully, false otherwise.
	 */
	public boolean deliverToMemory(boolean isRead); //Prompts dataLine to load value into memory, completing memory write operation

	public void registerMemoryModule(MainMemory memory);

	public AddressBus getAddressBus();

	public DataBus getDataBus();

	public boolean isWaiting();

	public void fireActivityUpdate(String update);
	
	public void registerListener(UpdateListener listener);

	public void fireOperationUpdate(String update);

	public void resetWaitStatus();
	
	public void clear();

		
	
	
	/*
	 * Bus is an object used to pass data and addresses - there should be multiple address/data
	 * lines, and corresponding control lines to allow pipelined access.
	 * Race condition problem:
	 * There must also be a way of ensuring that when one module has placed something on the bus, the other module must
	 * perform a read before the bus can be written to again... so there should perhaps be an inUse boolean flag to control
	 * this.
	 * What gets written must be read before anyone can write to the bus again.
	 */


	
	

}