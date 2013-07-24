package code;

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
