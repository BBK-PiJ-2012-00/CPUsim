package code;

public class IR {
	Instruction contents; //Instruction in the instruction register
	
	public void loadIR(Instruction instr) {
		this.contents = instr;
	}
	
	//code for decoding instruction and executing it: for arithmetic, this involves passing it to ALU

}
