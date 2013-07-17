package code;

public class MBR {
	private Data registerContents;
	
	public boolean write(Data data) { //Successful write returns true
		//Size is restricted in Instruction/Operand classes
		registerContents = data;
		if (registerContents == null) {
			return false;
		}
		return true;
	}
	
	public Data read() { //May need to employ readAsInt(), readAsInstr() etc, if necessary
		return registerContents;
	}

}
