package code;

/*
 * TO DO:
 * 		Place restrictions on branch target field in constructor depending on memory address size
 */

public class BranchInstr extends Instruction {
	private int branchTarget; //28-bit branch target field
	
	public BranchInstr(Opcode opcode, int branchTarget) {
		super(opcode);
		if (this.OPCODE.getValue() < 8 || this.OPCODE.getValue() > 13) {
			throw new IllegalStateException("Illegal opcode for this instruction format!");
		}
		if (branchTarget > 99) { //100 memory addresses at present (0-99).
			throw new IllegalStateException("Memory addresss out of bounds!");
		}
		this.branchTarget = branchTarget;
	}

	@Override
	public int getField1() {
		return branchTarget;
	}

	@Override
	public int getField2() {
		return -1; //Branch instructions have only one field.
	}

}
