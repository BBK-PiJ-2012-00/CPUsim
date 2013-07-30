package code;

public class OperandImpl implements Operand {
	private int intOperand;
	private double floatingPointOperand;
	private boolean isInteger;
	private boolean isFloatingPoint;
	
	public OperandImpl(int intOperand) {
		//Restrict size 2,147,483,647 and -2,147,483,648
		this.intOperand = intOperand;
		isInteger = true;
		isFloatingPoint = false;		
	}
	
	public OperandImpl(double floatingPointOperand) {
		this.floatingPointOperand = floatingPointOperand;
		isInteger = false;
		isFloatingPoint = true;
	}
	
	public int unwrapInteger() {
		try {
			if (!isInteger) {
				throw new IllegalStateException("Warning: The wrapped operand is not an integer."); //Not really illegal state...
			}
			return intOperand;
		}
		catch (IllegalStateException e) {
			e.getMessage();
		}
		//return 0; //If this method is called on a non-integer operand, returns 0
		intOperand = (int) floatingPointOperand;
		return intOperand; //returns floatingPointOperand as an integer (fraction part lost).
	}
	
	public double unwrapFloatingPoint() {
		try {
			if (!isFloatingPoint) {
				throw new IllegalStateException("Warning: The wrapped operand is not a floating point value.");
			}
			return floatingPointOperand;
		}
		catch (IllegalStateException e) {
			e.getMessage();
		}
		//return 0.0;
		floatingPointOperand = (double) intOperand;
		return floatingPointOperand; //returns intOperand as a floating point value (fraction part will equal 0).
		
	}
	

	@Override
	public boolean isInstruction() {
		return false;
	}

	@Override
	public boolean isInteger() {
		return isInteger;
	}
	
	@Override
	public boolean isFloatingPoint() {
		return isFloatingPoint;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof Operand)) {
			return false;
		}
		Operand operand = (Operand) o;
		if (this.unwrapInteger() == operand.unwrapInteger()) {
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() { //Operand is just a wrapper class for integers, so it makes sense to return the same hashCode
		Integer i = (Integer) this.unwrapInteger();
		return i.hashCode();
	}
	
	
	@Override
	public String toString() {
		if (isInteger) {
			return intOperand + ""; //Return int as String 	
		}
		if (isFloatingPoint) {
			return floatingPointOperand + "";
		}
		return null;
	}
	

}
