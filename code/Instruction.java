package code;

public interface Instruction {
	
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



