package code;

public enum Opcode {
	
	LOAD(1), STORE(2), MOVE(3),
	ADD(4), SUB(5), DIV(6), MUL(7),
	BR(8), BRZ(9), BRE(10), BRNE(11),
	SK(12), ISZ(13);
	
	private int value;
	
	private Opcode(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}

}
