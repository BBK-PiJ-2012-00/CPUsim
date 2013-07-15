package code;

public class MBR {
	private Data registerContents;
	
	public void write(Data data) {
		//Size is restricted in Instruction/Operand classes
		registerContents = data;
	}
	
	public Data read() { //May need to employ readAsInt(), readAsInstr() etc, if necessary
		return registerContents;
	}

}
