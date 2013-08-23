package code;

/*
 * Interface for general purpose registers.  General purpose registers are implemented as 
 * an array of type Data. General purpose registers are true general purpose; they are not
 * restricted to hold only data or only addresses, allowing more flexibility and simplicity.
 */

public interface RegisterFile {
	
	/*
	 * A design consideration is whether to implement true general purpose registers: any general
	 * purpose register can contain the operand for any opcode.
	 * Or whether general purpose registers should be separated into data registers and address registers.
	 * Data registers can hold only data, address registers addresses.  
	 * Simpler to have true-general purpose, especially mostly register-register operations take place.  More
	 * flexibility.
	 * 
	 * Condition code registers: set as result of operations. Some of these would be necessary: 2 to begin with,
	 * can increase if necessary.  Often a control register (as opposed to user-visible; not alterable by
	 * programmer).  
	 * PSW not necessary; simulator does not implement interrupts or switch modes.  
	 * If method calls are incorporated into assembly language, may need a control register/stack pointer to point to block
	 * to memory location where PC is to be reset to after method call (CA: 511).  
	 */
	
	/*
	 * A method for writing to a general purpose register.
	 * 
	 * @param int index the reference to the specific register in the array of
	 * registers.
	 * @param Data data the value to be written to the specified register.
	 */
	public void write(int index, Data data);
	
	/*
	 * A method for reading the contents of a general purpose register.
	 * 
	 * @param int index the reference to the register to be read from.
	 */
	public Data read(int index);
	
	public void registerListener(UpdateListener listener);
	


}
