package code;

/*
 * A class to represent a status register, generally for use with conditional branch instructions. User-visible,
 * can be referenced as r16 in assembly code.
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
