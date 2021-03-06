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
		
		fireUpdate("add", op1, op2, result);
		return result;
	}
	
	public static Operand SubtractionUnit(Operand operand1, Operand operand2) {
		int op1 = operand1.unwrapInteger();
		int op2 = operand2.unwrapInteger();
		int intResult = op1 - op2;
		Operand result = new OperandImpl(intResult);
		
		fireUpdate("sub", op1, op2, result);
		return result;
	}
	
	public static Operand DivisionUnit(Operand operand1, Operand operand2) {
		int op1 = operand1.unwrapInteger();
		int op2 = operand2.unwrapInteger();
		int intResult = op1 / op2;
		Operand result = new OperandImpl(intResult);
		
		fireUpdate("div", op1, op2, result);
		return result;
	}
	
	public static Operand MultiplicationUnit(Operand operand1, Operand operand2) {
		int op1 = operand1.unwrapInteger();
		int op2 = operand2.unwrapInteger();
		int intResult = op1 * op2;		
		Operand result = new OperandImpl(intResult);
		
		fireUpdate("mul", op1, op2, result);
		return result;
	}
	
	
	public static void registerListener(UpdateListener listener) {
		updateListener = listener;
	}
	
	
	public static void clearFields() { //For resetting GUI fields after alu operation
		fireUpdate("", 0, 0, null);
	}
	
	
	//GUI events should be handled from Event Dispatch Thread (EDT) only.
	//This adds the update event to the EDT thread.
	private static void fireUpdate(final String update, final int op1, final int op2, final Operand result) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(null, update, op1, op2, result);
				updateListener.handleUpDateEvent(updateEvent);	
			}
		});
	}
	


}
