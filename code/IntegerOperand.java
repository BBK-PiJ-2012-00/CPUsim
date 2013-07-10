package code;

/*
* A wrapper class to hold integer operands.  Implements data to allow for storage in main memory.
* This helps manage and structure the data used in the simulator; all data extends Data at the top of the hierarchy,
* with Instruction and Operands being on the second tier.  This avoids data in main memory being represented as type
* Object, which is less specific. Casting isn't eliminated, but this approach is more belt and braces than using Object.
*/
public class IntegerOperand implements Data {
	private int operand;
	
	public IntegerOperand(int operand) {
		this.operand = operand;
	}
	
	public int getInteger() {
		return operand;
	}

	@Override
	public boolean isInstruction() {
		return false;
	}

	@Override
	public boolean isInteger() {
		return true;
	}

}

/*
 * !!! Can have an Operand interface that specifies convert to int, and convert to double. Ints as doubles are simple, and doubles
 * as ints simply lose their fraction. Can use if statement and isInteger() to check it's an integer.
 * 
 * Or, memory can have methods: readInteger(), readInstruction()
 * 
 */
