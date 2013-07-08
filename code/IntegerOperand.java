package code;

/*
* A wrapper class to hold integer operands.  Implements data to allow for storage in main memory.
* This helps manage and structure the data used in the simulator; all data extends Data at the top of the hierarchy,
* with Instruction and Operands being on the second tier.  This avoids data in main memory being represented as type
* Object, which is less specific. Casting isn't eliminated, but this approach is more belt and braces than using Object.
*/
public class IntegerOperand implements Data {
	private int operand;
	
	

	@Override
	public boolean isInstruction() {
		return false;
	}

	@Override
	public boolean isInteger() {
		return true;
	}

}
