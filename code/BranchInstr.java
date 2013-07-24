package code;

/*
 * TO DO:
 * 		Place restrictions on branch target field in constructor depending on memory address size
 * 		Adjust toString methods in Instruction superclass to take into account that BRE and BRNE do have two fields
 */

public class BranchInstr extends Instruction {
	private int branchTarget; //28-bit branch target field
	private int register; //Field for use by BRE and BRNE opcodes only (register references the register whose contents is to
	//be compared to a control register to determine whether the branch is to be taken or not.
	
	public BranchInstr(Opcode opcode, int branchTarget) {
		super(opcode);
		if (this.OPCODE.getValue() < 8 || this.OPCODE.getValue() > 9) {
			throw new IllegalStateException("Illegal opcode for this instruction format!");
		}
		if (branchTarget > 99 || branchTarget < 0) { //100 memory addresses at present (0-99).
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
		this.branchTarget = -1; //field unused
		this.register = -1; //-1 to reflect that this field is unused (-1 refers to nothing)
	}
	
	public BranchInstr(Opcode opcode, int branchTarget, int register) { //Constructor for BRE / BRNE opcodes which require 2nd field
		super(opcode);
		if (this.OPCODE.getValue() < 11 || this.OPCODE.getValue() > 12) {
			throw new IllegalStateException("Illegal opcode for this instruction format!");
		}
		if (branchTarget > 99 || branchTarget < 0) { //100 memory addresses at present (0-99).
			throw new IllegalStateException("Memory addresss out of bounds!");
		}
		if (register < 0 || register > 15) { //16 general purpose registers available, 
			throw new IllegalStateException("Register address out of bounds!");
		}
		this.branchTarget = branchTarget;
		this.register = register;
	}
	

	@Override
	public int getField1() {
		return branchTarget;
	}

	//Field 2 may be useful BRE and BRNE instructions; field2 can refer to a general purpose register to be compared to
	//a control register (say, r15 or separate control register) by default
	@Override
	public int getField2() {
		return register; //register will equal -1 for all branch instructions other than BRE/BRNE
	}

}
