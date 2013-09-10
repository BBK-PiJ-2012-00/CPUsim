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
	
	public void registerListener(UpdateListener listener);
	
	public String display();
	
	public void fireUpdate(String update);

}
