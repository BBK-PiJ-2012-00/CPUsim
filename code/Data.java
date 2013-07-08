package code;

/*
 * Main memory is implemented as an array of type Data. Instructions implement data, as does anything else
 * that is stored in main memory.
 */
public interface Data {
	
	public boolean isInstruction();
	
	public boolean isInteger();

}
