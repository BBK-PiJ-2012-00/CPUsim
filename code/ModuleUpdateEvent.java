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
	
	private Operand op1; //op1 is the first ALU operand (to be sent for display on GUI)
	private Operand op2; //op2 is the second ALU operand
	private Operand result; //the result of the ALU operation (these are all type Operand for correct display on GUI)
	private String aluUnit; //Which unit (i.e. adder) did the event originate from
	
	private boolean controlLineUpdate; //True if the source intends to set control line GUI display

	public ModuleUpdateEvent(Object source, String update) {
		super(source);
		this.update = update;
	}
	
	/*
	 * For general purpose register events (register references the specific general purpose register).
	 */
	public ModuleUpdateEvent(Object source, int register, String update) {
		super(source);
		this.update = update;
		this.register = register;
	}
	
	/*
	 * A constructor for ALU-triggered events.
	 */
	public ModuleUpdateEvent(Object source, String aluUnit, int op1, int op2, Operand result) {
		super(new Object()); //will be null as ALU is a static utility class (but using null creates problems, hence dummy Obj)
		this.aluUnit = aluUnit;
		this.op1 = new OperandImpl(op1);
		this.op2 = new OperandImpl(op2);
		this.result = result;
	}
	
	/*
	 * A constructor for updates to the control line GUI display; the control line updates both
	 * this as well as the activity monitor, so it is important to distinguish between updates
	 * intended for one or the other.
	 */
	public ModuleUpdateEvent(Object source, boolean controlLineUpdate, String update) {
		super(source);
		this.controlLineUpdate = controlLineUpdate;
		this.update = update;		
	}
	
	public String getUpdate() {
		return this.update;
	}
	
	public int getRegisterReference() {
		return this.register;
	}
	
	public String getAluUnit() {
		return this.aluUnit;
	}
	
	public Operand getResult() {
		return this.result;
	}
	
	public Operand getOp1() {
		return this.op1;
	}
	
	public Operand getOp2() {
		return this.op2;
	}
	
	public boolean isControlLineUpdate() {
		return this.controlLineUpdate;
	}

}
