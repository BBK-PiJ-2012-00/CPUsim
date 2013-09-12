package code;

/*
 * Class to represent arithmetic instructions; arithmetic instructions are register-register, using direct register addressing.
 * e.g. ADD r1, r2: add contents of r2 to contents of r1 and store in r1.
 * 
 */

public class ArithmeticInstr extends Instruction {
	private int registerDest;//Register where input operand is originally stored, and where the result of operation should be stored
	private int registerInput; //Register where the input operand is stored
	
	
	public ArithmeticInstr(Opcode opcode, int registerDest, int registerInput) {
		super(opcode);
		if (this.OPCODE.getValue() < 4 || this.OPCODE.getValue() > 7) {
			//Ensures opcode supplied to constructor is an arithmetic opcode
			throw new IllegalStateException("Invalid opcode for this instruction type.");
		}
		//Restrictions on register reference fields of instruction; must comply with number of general purpose registers in CPU
		if (registerDest < 0 || registerDest > 15) { //0-15 general purpose
			throw new IllegalStateException("Invalid register reference; register references must be between 0 and 15 inclusive " +
					"\n(rCC not applicable to arithmetic instructions");
		}
		if (registerInput < 0 || registerInput > 15) {
			throw new IllegalStateException("Invalid register reference; register referencesmust be between 0 and 15 inclusive " +
					"\n(rCC not applicable to arithmetic instructions).");
		}
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
