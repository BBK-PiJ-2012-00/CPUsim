package code;

/*
 * An interface for a CPU register which holds an Operand value. Implemented by ConditionCodeRegister.
 */
public interface Register {		
	
	
	/*
	 * Returns the contents of the register.
	 * 
	 * @return Operand the register contents.
	 */
	public Operand read();
	
	
	/*
	 * Writes an Operand to the register.
	 * 
	 * @param Operand operand the operand to be written.
	 */
	public void write(Operand operand);
	
	
	/*
	 * A method for registering an event listener object with the register,
	 * for GUI display purposes. Every time the contents of the register is updated,
	 * an update event is created and handled by the listener to change the GUI
	 * display accordingly.
	 * 
	 * @param UpdateListener listener the listener object to handle update events.
	 */
	public void registerListener(UpdateListener listener);
	
	
}
