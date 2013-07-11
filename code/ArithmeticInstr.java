package code;

/*
 * TO DO:
 * 		Constructor: restrict field values to conform to no. of registers in CPU
 * 		Should Instruction be a superclass as opposed to interface? Can hold opcode field, and the methods from Data
 * 			ArithmeticInstr etc can add their own relevant fields (i.e. registerDest vs source), and their own instructions.
 *			The toString() methods are the same for arithmetic and transfer; both have two fields besides opcode, so branch
 *			instructions could override these methods. Could insert if statements to effect of if opcode is greater/less than 
 *			certain values to obtain instruction type, print this or that.  All data method bodies are common to all instructions.
 *			Avoids code duplication with same benefits of Instruction interface.
 */

/*
 * Class to represent arithmetic instructions; arithmetic instructions are register-register, using direct register addressing.
 * e.g. ADD r1, r2: add contents of r2 to contents of r1 and store in r1.
 * 
 */

public class ArithmeticInstr extends Instruction {
	private int registerDest; //Register where result of operation should be stored.
	private int registerInput; //Register where the input operand is stored (the operand to be added/subtracted etc to the contents of registerDest).
	
	public ArithmeticInstr(Opcode opcode, int registerDest, int registerInput) {
		super(opcode);
		if (this.OPCODE.getValue() < 4 || this.OPCODE.getValue() > 7) {
			throw new IllegalStateException("Invalid opcode for this instruction type.");//Ensures opcode supplied to constructor is an arithmetic opcode
		}
		//Insert code here to handle validation of registerDest/registerInput fields; should not be larger than no. of registers in CPU.
		this.registerDest = registerDest;
		this.registerInput = registerInput;
	}


	@Override
	public int getField1() {
		return registerDest;
	}


	@Override
	public int getField2() {
		return registerInput;
	}	

}
