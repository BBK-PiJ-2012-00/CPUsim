package code;

import java.util.EventObject;

/*
 * A class to represent events within the back end program code of the simulator.  These events
 * are passed to the relevant event listener so that the GUI can be updated in line with the activity
 * of the back end simulator program code. The fields are used to store information about the events so 
 * that the relevant components of the GUI can be updated accordingly.
 */
public class ModuleUpdateEvent extends EventObject {
	private static final long serialVersionUID = 663648502104832825L; //For serialization (unused)
	
	private String update; //The String with the update value
	private int register; //For general purpose registers
	
	private Operand op1; //op1 is the first ALU operand (to be sent for display on GUI)
	private Operand op2; //op2 is the second ALU operand
	private Operand result; //the result of the ALU operation (these are all type Operand for correct display on GUI)
	private String aluUnit; //Which unit (i.e. adder) did the event originate from
	
	private boolean controlLineUpdate; //True if the source intends to set control line GUI display
	private int activityMonitorReference; //Value 0 or 1 (write back stage doesn't use system bus) to determine which
				//activity monitor sould display the update on the GUI in pipelined mode.
	private boolean pipeliningEnabled; //For update from control line, set to true if constructor ModuleUpdateEvent(Object, 
									//boolean, int, String) is called, indicating pipelined mode.

	public ModuleUpdateEvent(Object source, String update) {
		super(source);
		this.update = update;
	}
	
	/*
	 * For general purpose and IR register events (register references the specific general purpose/ IR register).
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
	
	/*
	 * In pipelined mode, the activity monitor that should be updated needs to be specified for system
	 * bus transfers; both the fetch/decode stage and execute stages use the system bus and the system bus
	 * updates should be directed to display in the monitor belonging to the stage that is using the system bus.
	 */
	public ModuleUpdateEvent(Object source, boolean controlLineUpdate, int activityMonitorRef, String update) {
		super(source);
		this.controlLineUpdate = controlLineUpdate;
		this.activityMonitorReference = activityMonitorRef;
		this.update = update;
		this.pipeliningEnabled = true;
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
	
	public int getActivityMonitorReference() {
		return this.activityMonitorReference;
	}
	
	public boolean pipeliningEnabled() {
		return this.pipeliningEnabled;
	}

}
