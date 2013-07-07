package code;

/*
 * Instructions are 32 bits in length.  Representation is initiall in hexadecimal, but to ensure integrity,
 * hexadecimal values for the instruction fields must not exceed the maximum value possible for a given length of bits.
 * Opcodes currently occupy the first 4 bits of an instruction, permitting no more than 16 opcodes for the instruction set.
 * Whilst the simulator would work fine if instruction fields were allowed to overflow their bit allocation, it would not
 * be very realistic. 
 */

public interface Instruction {
	
	/*
	 * @return Opcode returns opcode field of instruction
	 */
	public Opcode getOpcode();
	
	/*
	 * Returns hexadecimal machine code representation of instruction on screen.
	 * 
	 * @return String hexadecimal machine code representation of instruction
	 */
	public String toMachineString();

}

/*
 * Format of the instruction in terms of allocation of bits determines max no. of opcodes, as well as max
 * values for fields representing memory/register addresses. Safeguards should be in place in assembler to
 * ensure values don't cause overflow.
 */



