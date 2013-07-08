package code;

/*
 * An interface to represent operands; IntegerOperand, FloatingPointOperand and potentially more. 
 * This helps manage and structure the data used in the simulator; all data extends Data at the top of the hierarchy,
 * with Instruction and Operand being on the second tier.  This avoids data in main memory being represented as type
 * Object, which is less specific. Casting isn't eliminated, but this approach is more belt and braces than using Object.
 */

public interface Operand extends Data {
	
	public

}
