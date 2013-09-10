package code;

import javax.swing.SwingUtilities;

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
		
//		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(null, "add", op1, op2, result);
//		updateListener.handleUpDateEvent(updateEvent);	
		fireUpdate("add", op1, op2, result);
		return result;
	}
	
	public static Operand SubtractionUnit(Operand operand1, Operand operand2) {
		int op1 = operand1.unwrapInteger();
		int op2 = operand2.unwrapInteger();
		int intResult = op1 - op2;
		Operand result = new OperandImpl(intResult);
		
//		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(null, "sub", op1, op2, result);
//		updateListener.handleUpDateEvent(updateEvent);
		fireUpdate("sub", op1, op2, result);
		return result;
	}
	
	public static Operand DivisionUnit(Operand operand1, Operand operand2) {
		int op1 = operand1.unwrapInteger();
		int op2 = operand2.unwrapInteger();
		int intResult = op1 / op2;
		Operand result = new OperandImpl(intResult);
		
//		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(null, "div", op1, op2, result);
//		updateListener.handleUpDateEvent(updateEvent);
		fireUpdate("div", op1, op2, result);
		return result;
	}
	
	public static Operand MultiplicationUnit(Operand operand1, Operand operand2) {
		int op1 = operand1.unwrapInteger();
		int op2 = operand2.unwrapInteger();
		int intResult = op1 * op2;		
		Operand result = new OperandImpl(intResult);
		
//		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(null, "mul", op1, op2, result);
//		updateListener.handleUpDateEvent(updateEvent);
		fireUpdate("mul", op1, op2, result);
		return result;
	}
	
	public static void registerListener(UpdateListener listener) {
		updateListener = listener;
	}
	
	public static void clearFields() { //For resetting GUI fields after alu operation
//		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(null, "", 0, 0, null);
//		updateListener.handleUpDateEvent(updateEvent);
		fireUpdate("", 0, 0, null);
	}
	
	//GUI events should be handled from EDT
	//This adds the update event to the EDT thread. Need to test this works on the GUI
	public static void fireUpdate(final String update, final int op1, final int op2, final Operand result) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(null, update, op1, op2, result);
				updateListener.handleUpDateEvent(updateEvent);	
			}
		});
	}
	


}
