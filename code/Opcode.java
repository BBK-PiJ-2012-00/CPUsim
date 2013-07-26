package code;

public enum Opcode {
	
	LOAD(1), STORE(2), MOVE(3),
	ADD(4), SUB(5), DIV(6), MUL(7),
	BR(8), BRZ(9), SKZ(10), BRE(11), BRNE(12), 
	HALT(13);
	
	private int value;
	
	private Opcode(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}

}
