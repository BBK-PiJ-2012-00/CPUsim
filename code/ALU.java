package code;

/*
 * Class to represent arithmetic & logic unit. Static methods representing
 * addition unit, multiplication unit etc.
 */
public class ALU {
	private static UpdateListener updateListener;
	
	private ALU() { //To prevent instantiation; all methods are static (as ALU is like a utility class)
		super();
	}
	
	public static Operand AdditionUnit(Operand operand1, Operand operand2) {
		int op1 = operand1.unwrapInteger();
		int op2 = operand2.unwrapInteger();
		int sum = op1 + op2;
		Operand result = new OperandImpl(sum);
		
		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(null, "add", op1, op2, result);
		updateListener.handleUpDateEvent(updateEvent);		
		return result;
	}
	
	public static Operand SubtractionUnit(Operand operand1, Operand operand2) {
		int op1 = operand1.unwrapInteger();
		int op2 = operand2.unwrapInteger();
		int intResult = op1 - op2;
		Operand result = new OperandImpl(intResult);
		
		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(null, "sub", op1, op2, result);
		updateListener.handleUpDateEvent(updateEvent);
		return result;
	}
	
	public static Operand DivisionUnit(Operand operand1, Operand operand2) {
		int op1 = operand1.unwrapInteger();
		int op2 = operand2.unwrapInteger();
		int intResult = op1 / op2;
		Operand result = new OperandImpl(intResult);
		
		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(null, "div", op1, op2, result);
		updateListener.handleUpDateEvent(updateEvent);
		return result;
	}
	
	public static Operand MultiplicationUnit(Operand operand1, Operand operand2) {
		int op1 = operand1.unwrapInteger();
		int op2 = operand2.unwrapInteger();
		int intResult = op1 * op2;		
		Operand result = new OperandImpl(intResult);
		
		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(null, "mul", op1, op2, result);
		updateListener.handleUpDateEvent(updateEvent);
		return result;
	}
	
	public static void registerListener(UpdateListener listener) {
		updateListener = listener;
	}
	
	public static void clearFields() { //For resetting GUI fields after alu operation
		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(null, "", 0, 0, null);
		updateListener.handleUpDateEvent(updateEvent);
	}
	


}
