package code;

/*
 * An interface to represent operands; IntegerOperand, FloatingPointOperand and potentially more. 
 * This helps manage and structure the data used in the simulator; all data extends Data at the top of the hierarchy,
 * with Instruction and Operand being on the second tier.  This avoids data in main memory being represented as type
 * Object, which would require casting before use.
 */

public interface Operand extends Data {

}
