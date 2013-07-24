package code;

/*
 * HALT instruction signals the end of a program. It has no fields, therefore its getField() methods
 * return -1.
 */
public class HaltInstr extends Instruction {
	
	public HaltInstr(Opcode opcode) {
		super(opcode);
		if (this.OPCODE.getValue() != 13) { //HALT instruction can only accept HALT opcode.
			throw new IllegalStateException("Invalid opcode for this instruction type!");
		}
	}

	@Override
	public int getField1() {
		return -1;
	}

	@Override
	public int getField2() {
		return -1;
	}

}
