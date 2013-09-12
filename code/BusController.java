package code;

/*
 * An interface for the system bus controller of the simulator. It seems wise to encapsulate the three bus lines 
 * in a system bus controller class, meaning that lines must be accessed via this class and not directly, ensuring
 * a higher degree of data integrity.  Also allows for later addition of more lines to improve performance without
 * having to alter code elsewhere (i.e. to choose which address line and which data line etc).
 * 
 * A SystemBusController class also simplifies use of the system bus; rather than having to access control lines directly and 
 * separately, the SystemBusController class handles all interaction with the lines and can implement any safeguards necessary.
 */
public interface BusController {
		
	/*
	 * A method to transfer data from CPU to memory via the SystemBus.
	 * 
	 * @param int memoryAddress the memory destination.
	 * @param Data data the data to be transferred.
	 * @return true if transfer successful, false otherwise.
	 */
	public boolean transferToMemory(int memoryAddress, Data data);
	
	
	/*
	 * A method to transfer data from memory to CPU via the SystemBus.
	 * 
	 * @param Data data the data to be transferred.
	 * @return true if transfer successful, false otherwise.
	 */	 
	 boolean transferToCPU(Data data);
	 
	 /*
	  * Allows access to the control line, necessary for when
	  * components are instantiated and references need to be passed.
	  * 
	  * @return ControlLine control line reference.
	  */
	 public ControlLine accessControlLine(); //For GUI updates
	 
	 /*
	  * For pipelined execution; on the pipelined GUI there are three activity
	  * monitor displays and it is necessary to deliver the system bus GUI updates
	  * to the relevant one. For example, if the system bus is used to fetch an
	  * instruction, the update should show on the F/D Stage's activity monitor.
	  * If an operand is fetched or written to/from memory via the system bus, this
	  * should show up on the Execution Stage's activity monitor. By identifying the
	  * calling class, the updates can be channelled accordingly. After finishing 
	  * with the system bus, the caller class should pass a null reference to this
	  * method to avoid any interference when used by another Stage object.
	  * 
	  * This method isn't used in standard execution mode as there is no need (only
	  * one activity monitor display).
	  * 
	  * The method passes the Stage reference to the control line, as that is where
	  * GUI updates for the system bus originate from.
	  * 
	  * @param Stage callingStage a reference to the object using the system bus.
	  */
	 public void setCaller(Stage callingStage);

}
