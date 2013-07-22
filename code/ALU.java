package code;

/*
 * Class to represent arithmetic & logic unit. Static methods representing
 * addition unit, multiplication unit etc.
 */
public class ALU {
	
	public static Operand AdditionUnit1(Operand operand1, Operand operand2) {
		int op1 = operand1.unwrapInteger();
		int op2 = operand2.unwrapInteger();
		int sum = op1 + op2;
		if (sum > 2147483647 || sum < -2147483648) { //Max/min signed integer for 32-bit machine -> would cause overflow
			//Throw exception
			return null;
		}
		Operand result = new OperandImpl(sum);
		return result;
	}

}
