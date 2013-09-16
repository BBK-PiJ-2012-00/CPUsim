package code;

/*
 * A wrapper class for integers (and floating point values); enables them to be stored in main memory.
 * 
 * Allows a degree of realism; in reality, values are stored in memory as a sequence of bits. Read one way, the bits form an
 * instruction, read another and they can form a (un)signed integer or floating point value. The Operand class allows integers
 * and floating point values to coexist in main memory, and also allows such values to be treated as either integers or floating
 * point values.
 */
public interface Operand extends Data {
		
	

	/*
	 * Unwraps an integer value stored in the field of the Operand.
	 * 
	 * @return int the wrapped integer value.
	 */
	public int unwrapInteger();
	
	
	/*
	 * Unwraps a floating point value stored in the field of the Operand.
	 * 
	 * @double the wrapped floating-point value.
	 */
	public double unwrapFloatingPoint();	

	
	@Override
	public boolean isInstruction(); //Returns false for Operand types

	
	@Override
	public boolean isInteger();	
	
	
	/*
	 * Not currently in use/
	 */
	@Override
	public boolean isFloatingPoint();
		

}

