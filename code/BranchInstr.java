package code;


/*
 * A class to represent branch instructions.
 */

public class BranchInstr extends Instruction {
	private int branchTarget; //Branch target field
	private int register; //Field for use by BRE and BRNE opcodes only (register refers to the register whose contents is to
	//be compared to the control register (rCC) to determine whether the branch is to be taken or not.
	
	
	public BranchInstr(Opcode opcode, int branchTarget) { //For BR and BRZ instructions
		super(opcode);
		if (this.OPCODE.getValue() < 8 || this.OPCODE.getValue() > 9) { //Validate opcode
			throw new IllegalStateException("Illegal opcode for this instruction format!");
		}
		if (branchTarget > 99 || branchTarget < 0) { //100 memory addresses (0-99).
			throw new IllegalStateException("Memory addresss out of bounds!");
		}
		this.branchTarget = branchTarget;
		this.register = -1; //-1 to reflect that this field is unused (-1 refers to nothing)
	}
	
	
	public BranchInstr(Opcode opcode) { //For SKZ branch instructions, which have no required fields other than opcode
		super(opcode);
		if (this.OPCODE.getValue() != 10) {
			throw new IllegalStateException("Illegal opcode for this instruction format!");
		}
		this.branchTarget = -1; //Branch target field unused so set to default value
		this.register = -1; //-1 to reflect that this field is unused (-1 refers to nothing)
	}
	
	
	public BranchInstr(Opcode opcode, int branchTarget, int register) { //Constructor for BRE / BRNE opcodes which require 2nd field
		super(opcode);
		if (this.OPCODE.getValue() < 11 || this.OPCODE.getValue() > 12) {
			throw new IllegalStateException("Illegal opcode for this instruction format!");
		}
		//First field: branch target for if branch is taken
		if (branchTarget > 99 || branchTarget < 0) { //100 memory addresses (0-99).
			throw new IllegalStateException("Memory addresss out of bounds!");
		}
		//Second field: the register whose contents should be compared to rCC contents to determine if branch is taken.
		if (register < 0 || register > 15) { //16 general purpose registers available (0-15).
			throw new IllegalStateException("Register address out of bounds!");
		}
		this.branchTarget = branchTarget;
		this.register = register;
	}
	

	@Override
	public int getField1() {
		return branchTarget;
	}

	
	@Override
	public int getField2() {
		return register; //register will equal -1 for all branch instructions other than BRE/BRNE
	}

}
