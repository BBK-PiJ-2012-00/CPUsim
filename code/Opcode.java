package code;

/*
 * An enum representing instruction opcode values.
 */
public enum Opcode {
	
	LOAD(1), STORE(2), MOVE(3),
	ADD(4), SUB(5), DIV(6), MUL(7),
	BR(8), BRZ(9), SKZ(10), BRE(11), BRNE(12), 
	HALT(13);
	
	private int value; //Associates an integer value with each opcode
	
	private Opcode(int value) { //A constructor allowing the value field to be set
		this.value = value;
	}
	
	public int getValue() {  //Returns the integer value associated with each opcode
		return this.value;
	}

}
