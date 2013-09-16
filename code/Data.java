package code;

/*
 * Main memory is implemented as an array of type Data. Instructions and Operands implement Data, thus
 * allowing them to be stored in memory.
 */
public interface Data {
	
	/*
	 * Returns true if the object referred to the by Data interface is of type
	 * Instruction.
	 * 
	 * @return boolean true if Instruction, false otherwise.
	 */
	public boolean isInstruction();
	
	
	/*
	 * Returns true if the object referred to the by Data interface is of type
	 * Operand, and that operand wraps an integer.
	 * 
	 * @return boolean true if the Operand wraps an integer, false otherwise.
	 */
	public boolean isInteger();
	
	
	/*
	 * Not currently in use but floating point operands could be introduced at some point.
	 */
	public boolean isFloatingPoint();

}
