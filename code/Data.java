package code;

/*
 * Main memory is implemented as an array of type Data. Instructions and Operands implement Data, thus
 * allowing them to be stored in memory.
 */
public interface Data {
	
	public boolean isInstruction();
	
	public boolean isInteger();
	
	/*
	 * Not currently in use but floating point operands could be introduced at some point.
	 */
	public boolean isFloatingPoint();

}
