package code;

/*
 * Main memory is implemented as an array of type Data. Instructions implement data, as does anything else
 * that is stored in main memory.
 * 
 * While main memory is an array of type Data, things declared as Operand and Instruction can be stored within (but
 * come out as type Data?)
 */
public interface Data {
	
	public boolean isInstruction();
	
	public boolean isInteger();
	
	public boolean isFloatingPoint();

}
