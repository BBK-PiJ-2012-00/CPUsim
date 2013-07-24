package code;

/*
 * A class to represent a status register, generally for use with conditional branch instructions. User-visible,
 * can be referenced as r16 in assembly code.
 * 
 * This register may automatically be set as the result of certain operations? Easier to set it manually with explicit
 * LOAD instruction.  Can be incremented/decremented using arithmetic operations.
 */
public class StatusRegister implements Register {
	private Operand contents;

	@Override
	public Operand read() {
		return this.contents;
	}

	@Override
	public void write(Operand operand) {
		this.contents = operand;
	}

}
