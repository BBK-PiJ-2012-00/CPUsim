package code;

/*
 * A wrapper class for integers and floating point values; enables them to be stored in main memory.
 * 
 * Allows a degree of realism; in reality, values are stored in memory as a sequence of bits. Read one way, the bits form an
 * instruction, read another and they can form a (un)signed integer or floating point value. The Operand class allows integers
 * and floating point values to coexist in main memory, and also allow such values to be treated as either integers or floating
 * point values. Operands could be implemented as IntegerOperand, FloatingPointOperand etc, but this adds more redundant code and
 * unnecessary complexity.
 */
public interface Operand extends Data {
		
	public int unwrapInteger();
	
	public double unwrapFloatingPoint();	

	@Override
	public boolean isInstruction();

	@Override
	public boolean isInteger();		
	
	public boolean isFloatingPoint();
		

}
