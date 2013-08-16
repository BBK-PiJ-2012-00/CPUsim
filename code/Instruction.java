package code;

/*
 * All instructions extend the abstract Instruction class. This allows for code reuse, reduces redundant code that would be
 * brought about were Instruction implemented as an interface, and ensures all instructions implement the same key methods.
 * 
 * Instructions are 32 bits in length.  Representation is initially in hexadecimal, but to ensure integrity,
 * hexadecimal values for the instruction fields must not exceed the maximum value possible for a given length of bits.
 * Opcodes occupy the first 4 bits of an instruction, permitting no more than 16 opcodes for the instruction set.
 * Whilst the simulator would work fine if instruction fields were allowed to overflow their bit allocation, it would not
 * be very realistic.  The same applies to other fields within an instruction. 
 */

public abstract class Instruction implements Data {	
	protected final Opcode OPCODE; 
	
	protected Instruction(Opcode opcode) {
		this.OPCODE = opcode;
	}
	
	protected Instruction() { //default constructor, required by abstract class
		OPCODE = null;
	}
	
	@Override
	public boolean isInstruction() {
		return true;
	}

	@Override
	public boolean isInteger() {
		return false;
	}

	@Override
	public boolean isFloatingPoint() {
		return false;
	}
	
	/*
	 * Returns the opcode of the instruction.
	 * 
	 * @return Opcode the opcode field of the instruction.
	 */
	public Opcode getOpcode() {
		return this.OPCODE;
	}
	
	/*
	 * Returns instruction in String format, for printing on screen. Opcode 
	 * is in mnemonic form. 
	 */
	public String toString() {
		if (OPCODE.getValue() == 13 || OPCODE.getValue() == 10) { //For HALT/SKZ instructions
			return OPCODE.toString();
		}
		if (OPCODE.getValue() > 7 && OPCODE.getValue() < 10) { //For branch instructions which only have one instruction field besides opcode
			return OPCODE.toString() + " " + this.getField1();
		}
		if (OPCODE.getValue() > 2 && OPCODE.getValue() < 8) { //Arithmetic and MOVE instructions
			if (this.getField2() == 16) { //Display condition code register as rCC rather than r16
				return OPCODE.toString() + " r" + this.getField1() + " rCC";
			}
			return OPCODE.toString() + " r" + this.getField1() + " r" + this.getField2();
		}
		if ((OPCODE.getValue() > 10 && OPCODE.getValue() < 13) || OPCODE.getValue() == 1) { //BRE/BRNE/LOAD instructions
			if (this.getField2() == 16) { //Display condition code register as rCC rather than r16
				return OPCODE.toString() + " r" + this.getField1() + " rCC";
			}
			return OPCODE.toString() + " " + this.getField1() + " r" + this.getField2();
		}
		return OPCODE.toString() + " r" + this.getField1() + " " + this.getField2(); //STORE
	}
	
	
	/*
	 * Returns instruction in String format, in hexadecimal representation.
	 * 
	 * @return String hexadecimal representation of instruction.
	 */
	public String toMachineString() {
		String machineString;
		if (OPCODE.getValue() == 13 || OPCODE.getValue() == 10) { //For HALT and SKZ instructions
			return OPCODE.toString();
		}
		if (OPCODE.getValue() > 7 && OPCODE.getValue() < 10) { //For branch instructions with only one field
			machineString = OPCODE.getValue() + " " + Integer.toHexString(this.getField1()).toUpperCase();
			return machineString;
		}
		machineString = OPCODE.getValue() + " " + Integer.toHexString(this.getField1()).toUpperCase() + " " + 
				Integer.toHexString(this.getField2()).toUpperCase();
		return machineString;
	}
	
//	@Override
//	public boolean equals(Object o) {
//		if (o == null) {
//			return false;
//		}
//		if (!(o instanceof Instruction)) {
//			return false;
//		}
//		
//		Instruction i = (Instruction) o;
//		if (i.OPCODE.equals(this.OPCODE) && i.getField1() == this.getField1() && i.getField2() == this.getField2()) {
//			return true;
//		}
//		return false;
//	}
	
	/*
	 * Returns the first instruction field. For data transfer instructions, this is the source location (14 bits);
	 * for arithmetic instructions, the register whose contents is an operand in the arithmetic operation, as well
	 * as the location where the result of the operation is to be stored (14 bits); for 
	 * branch instructions, the branch target (28 bits).
	 * 
	 * @return int the integer representation of the first field of the instruction.
	 */
	public abstract int getField1(); //All instructions have at least one field
	
	/*
	 * Returns the second field of an instruction. For branch instructions, the return value will be -1 as these instructions
	 * have only one field (the branch target), and memory addresses start at 0 (thus -1 is unused, and an invalid address). 
	 * For data transfer instructions, this field is the destination location; for arithmetic instructions, this is the 
	 * register location where one of the operands to the arithmetic operation is stored.
	 * 
	 * @return int the integer representation of the second instruction field.
	 */
	public abstract int getField2(); //Branch instructions don't have this: returns -1.

}

/*
 * Format of the instruction in terms of allocation of bits determines max no. of opcodes, as well as max
 * values for fields representing memory/register addresses. Safeguards should be in place in assembler to
 * ensure values don't cause overflow.
 */



