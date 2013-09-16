package code;

/*
 * Interface for the program counter register of the CPU.
 */

public interface ProgramCounter {
	
	/*
	 * @return int the current address referenced by the PC.
	 */
	public int getValue();
	
	
	/*
	 * Increments the address held in the PC by one, to fetch
	 * the next sequential instruction from memory.
	 */
	public void incrementPC();
	
	
	/*
	 * A method used to set the value of the PC; used when a program is 
	 * first loaded into memory, and when branch instructions are executed.
	 */
	public void setPC(int address);
	
	
	/*
	 * A method for registering an event listener object with the PC,
	 * for GUI display purposes. Every time the contents of the PC is updated,
	 * an update event is created and handled by the listener to change the GUI
	 * display accordingly.
	 * 
	 * @param UpdateListener listener the listener object to handle update events.
	 */
	public void registerListener(UpdateListener listener);
	
	
}
