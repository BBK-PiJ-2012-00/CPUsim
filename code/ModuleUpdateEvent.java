package code;

import java.util.EventObject;

/*
 * A class to represent events within the back end program code of the simulator.  These events
 * are passed to the relevant event listener so that the GUI can be updated in line with the activity
 * of the back end simulator program code. The fields are used to store information about the events so 
 * that the relevant components of the GUI can be updated accordingly.
 */
public class ModuleUpdateEvent extends EventObject {
	private String update; //The String with the update value
	private int register; //For general purpose registers
	
	private int op1; //op1 is the first ALU operand (to be sent for display on GUI)
	private int op2; //op2 is the second ALU operand
	private int result; //the result of the ALU operation
	private String aluUnit; //Which unit (i.e. adder) did the event originate from

	public ModuleUpdateEvent(Object source, String update) {
		super(source);
		this.update = update;
	}
	
	public ModuleUpdateEvent(Object source, int register, String update) {
		super(source);
		this.update = update;
		this.register = register;
	}
	
	/*
	 * A constructor for ALU-triggered events.
	 */
	public ModuleUpdateEvent(Object source, String aluUnit, int op1, int op2, int result) {
		super(source);
		this.aluUnit = aluUnit;
		this.op1 = op1;
		this.op2 = op2;
		this.result = result;
	}
	
	public String getUpdate() {
		return this.update;
	}
	
	public int getRegisterReference() {
		return this.register;
	}

}
