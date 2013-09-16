package code;

/*
 * All instructions extend this abstract Instruction class. This allows for code reuse, reduces redundant code that would be
 * brought about were Instruction implemented as an interface, and ensures all instructions implement the same key methods.
 * 
 * Instructions are 32 bits in length.  Representation is primarily in decimal for the purposes of this simulator,
 * with the toMachineString() method allowing for a hexadecimal representation.
 * Opcodes occupy the first 4 bits of an instruction, permitting no more than 16 opcodes for the instruction set.
 * Whilst the simulator would work fine if instruction fields were allowed to overflow their bit allocation, it would not
 * be very realistic.  The same applies to other fields within an instruction. 
 * 
 * While the assembler implements a lot of error handling with regard to instruction instantiation, error handling
 * is also implemented in the Instruction subclasses.
 */

public abstract class Instruction implements Data {	
	protected final Opcode OPCODE; //protected to allow access by subclasses.
	
	
	protected Instruction(Opcode opcode) {
		this.OPCODE = opcode;
	}
	
	
	@Override
	public boolean isInstruction() {
		return true;
	}

	@Override
	public boolean isInteger() {
		return false;
	}

	
	/*
	 * Not currently in use.
	 */
	@Override
	public boolean isFloatingPoint() {
		return false;
	}
	
	
	/*
	 * Returns the opcode of the instruction.
	 */
	public Opcode getOpcode() {
		return this.OPCODE;
	}
	
	
	/*
	 * Returns instruction in String format, for printing on screen. Opcode 
	 * is in mnemonic form. 
	 */
	public String toString() {
		if (OPCODE.getValue() == 13 || OPCODE.getValue() == 10) { //For HALT/SKZ instructions (both have no fields)
			return OPCODE.toString();
		}
		
		//For branch instructions which only have one instruction field besides opcode
		if (OPCODE.getValue() > 7 && OPCODE.getValue() < 10) {
			return OPCODE.toString() + " " + this.getField1();
		}
		
		//Arithmetic and MOVE instructions
		if (OPCODE.getValue() > 2 && OPCODE.getValue() < 8) { 
			if (this.getField1() == 16) { //Display condition code register as rCC rather than r16
				return OPCODE.toString() + " rCC" + " r" + this.getField1();
			}
			if (this.getField2() == 16) { //Display condition code register as rCC rather than r16
				return OPCODE.toString() + " r" + this.getField1() + " rCC";
			}
			return OPCODE.toString() + " r" + this.getField1() + " r" + this.getField2();
		}
		
		//BRE/BRNE/LOAD instructions
		if ((OPCODE.getValue() > 10 && OPCODE.getValue() < 13) || OPCODE.getValue() == 1) { 
			if (this.getField2() == 16) { //Display condition code register as rCC rather than r16
				return OPCODE.toString() + " " + this.getField1() + " rCC";
			}
			return OPCODE.toString() + " " + this.getField1() + " r" + this.getField2();
		}
		return OPCODE.toString() + " r" + this.getField1() + " " + this.getField2(); //STORE
	}
	
	
	/*
	 * Returns instruction in String format, in hexadecimal representation (not currently used).
	 */
	public String toMachineString() {
		String machineString;
		if (OPCODE.getValue() == 13 || OPCODE.getValue() == 10) { //For HALT and SKZ instructions with no fields
			return OPCODE.toString();
		}
		
		if (OPCODE.getValue() > 7 && OPCODE.getValue() < 10) { //For branch instructions with only one field
			machineString = OPCODE.getValue() + " " + Integer.toHexString(this.getField1()).toUpperCase();
			return machineString;
		}
		
		//For all those instructions with two fields (Transfer and Arithmetic).
		machineString = OPCODE.getValue() + " " + Integer.toHexString(this.getField1()).toUpperCase() + " " + 
				Integer.toHexString(this.getField2()).toUpperCase();
		return machineString;
	}

	
	/*
	 * Returns the first instruction field. For data transfer instructions, this is the source location (14 bits);
	 * for arithmetic instructions, the register whose contents is an operand in the arithmetic operation, as well
	 * as the location where the result of the operation is to be stored (14 bits); for 
	 * branch instructions BR, BRZ, BRE, BRNE, this field is the branch target (28 bits). For SKZ and HALT instructions,
	 * this returns -1 as these instructions do not have any fields (-1 is a default value that doesn't refer to a memory
	 * address or register).
	 * 
	 * @return int the integer representation of the first field of the instruction.
	 */
	public abstract int getField1();
	
	
	/*
	 * Returns the second field of an instruction. For branch instructions BR, BRZ and SKZ the return value will be -1 as 
	 * these instructions have only one field (the branch target). For BRE/BRNE instructions, this field refers to a 
	 * general purpose register who contents is to be compared to the contents of the condition code register (rCC).
	 * For data transfer instructions, this field is the destination location; for arithmetic instructions, this is the 
	 * register location where one of the operands to the arithmetic operation is stored.
	 * 
	 * @return int the integer representation of the second instruction field.
	 */
	public abstract int getField2(); //Branch instructions don't have this: returns -1.

}




