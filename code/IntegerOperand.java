package code;

public class IntegerOperand implements Operand {
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
