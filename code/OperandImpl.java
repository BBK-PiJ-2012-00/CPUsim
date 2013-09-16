package code;

public class OperandImpl implements Operand {
	private int intOperand; //Stores integer value
	private double floatingPointOperand; //Stores floating-point value
	private boolean isInteger;
	private boolean isFloatingPoint;
	
	
	public OperandImpl(int intOperand) {
		this.intOperand = intOperand; //If integer passed as parameter, set isInteger to true and isFloatingPoint to false
		isInteger = true;
		isFloatingPoint = false;		
	}
	
	public OperandImpl(double floatingPointOperand) {//If double passed as parameter, set isFloating point to true, isInteger to false
		this.floatingPointOperand = floatingPointOperand;
		isInteger = false;
		isFloatingPoint = true;
	}
	
	
	public int unwrapInteger() {		
		if (!isInteger) { //If not integer, return floatingPointOperand cast to int (fraction part lost)
			int castIntOperand = (int) floatingPointOperand; //Create temporary value so as not to overwrite true value
			return castIntOperand;
		}
		return intOperand;		
	}
	
	
	public double unwrapFloatingPoint() {		
		if (!isFloatingPoint) { //If not floating point, return intOperand as floating point value (fraction will be 0).
			double castFloatingPointOperand = (double) intOperand;
			return castFloatingPointOperand;		
		}
		return floatingPointOperand;
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
			return "#" + intOperand; //Return int as String 	
		}
		if (isFloatingPoint) {
			return floatingPointOperand + "";
		}
		return null;
	}
	

}
