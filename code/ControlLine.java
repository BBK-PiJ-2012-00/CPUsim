package code;

/*
 * An interface for a System Bus control line. The system bus can only be used from a Stage's
 * synchronized accessMemory() method, ensuring no race conditions or lost updates. 
 */

public interface ControlLine {

	
	/* 
	 * ControlLine implements only a single writeToBus() method, purposefully not
	 * differentiating between transfers from CPU to memory, as this is true to reality
	 * and also simplifies the control line.  It is important that all bus data's integrity 
	 * is maintained, and that two threads are prevented from accessing this method at the 
	 * same time, hence the method is synchronized. Synchronizing this method is also 
	 * necessary for the wait() statements to be implemented (these allow the user to step 
	 * through execution one step at a time on the GUI), as a thread held at
	 * a wait() statement can only be notified (using notify()) by another thread if the other
	 * thread holds the lock on the object. The thread held at the wait() statement releases
	 * the lock while waiting, making it available for another thread (which will be the SwingWorker
	 * thread activated from the GUI) to notify() it.  
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
	public boolean deliverToMBR(); 
	
	
	/*
	 * A method that prompts either the first phase of a memory read, or the final stage of a memory write.
	 * For the first stage of a memory read, the address value on the address bus is passed (delivered) to main memory, 
	 * so that the value held at that address may be loaded onto the data bus (during the second stage of
	 * a memory read). For the final stage of a memory write, the  data bus is prompted to load its value into 
	 * the memory location specified by the address bus. These two operations are differentiated by the boolean
	 * parameter passed into the method when it is called (determined by writeToBus()).
	 * 
	 * @param boolean isRead if the operation is to be a memory read, the value is true, otherwise false. 
	 * @return boolean returns true if the method completed successfully, false otherwise.
	 */
	public boolean deliverToMemory(boolean isRead);

	
	/*
	 * Passes the control line a reference to main memory.
	 * 
	 * @param MainMemory memory a reference to main memory.
	 */
	public void registerMemoryModule(MainMemory memory);

	
	/*
	 * Accessor for address bus reference. Address bus is encapsulated
	 * within control line. Useful for registering event listener with
	 * address bus.
	 * 
	 * @return AddressBus the control line's address bus reference.
	 */
	public AddressBus getAddressBus();

	
	/*
	 * Accessor for data bus reference. Data bus is encapsulated
	 * within control line. Useful for registering event listener with
	 * data bus.
	 * 
	 * @return DataBus the control line's data bus reference.
	 */
	public DataBus getDataBus();

	
	/*
	 * The isWaiting boolean flag is used to coordinate the step-through
	 * execution of an assembly program. When the thread of execution running through the
	 * control line code encounters a wait() statement, its isWaiting flag is
	 * set to true just prior to this. This enables the EDT thread on the GUI to issue a notify()
	 * to the waiting thread when the user clicks "step" on the GUI, prompting the thread to continue
	 * executing until the next wait(). isWaiting is set to false once the wait() statement has been
	 * passed.
	 * 
	 * @return boolean the status of the isWaiting flag (true if waiting, false if not).
	 */
	public boolean isWaiting();

	
	/*
	 * Registers an UpdateListener with the class, which is used to update the system
	 * bus display on the GUI.
	 * 
	 * @param UpdateLisetner listener the listener passed to the control line from the GUI.
	 */
	public void registerListener(UpdateListener listener);
	

	
	/*
	 * To clear system bus lines in event of the SwingWorker thread being cancelled, which happens
	 * if the user clicks "reset" on the GUI display, cancelling assembly program execution and
	 * resetting the display. Also called in the event of a pipeline flush, in which case if the system bus
	 * is being used by the fetch/decode stage, the thread is interrupted by the SwingWorker thread active
	 * in the Execute Stage and the display of the system bus components should be reset.
	 */
	public void clear();

	
	
	/*
	 * This method is called by the setCaller() method on the BusController, enabling the Stage object which is
	 * acquiring the use of the system bus to pass the control line a reference to itself.
	 * For pipelined execution only; on the pipelined GUI there are three activity monitor displays and it is 
	 * necessary to deliver the system bus GUI updates to the relevant one. For example, if the system bus 
	 * is used to fetch an instruction, the update should show on the F/D Stage's activity monitor.
	 * If an operand is fetched or written to/from memory via the system bus, this should show up on the 
	 * Execution Stage's activity monitor. By identifying the calling class, the updates can be channelled 
	 * accordingly. After finishing with the system bus, the caller class should pass a null reference to this
	 * method to avoid any interference when used by another Stage object.
	 * 
	 * This method isn't used in standard execution mode as there is no need (only
	 * one activity monitor display).
	 * 
	 * @param Stage callingStage a reference to the object using the system bus.
	 */	  
	public void setCaller(Stage caller);


}