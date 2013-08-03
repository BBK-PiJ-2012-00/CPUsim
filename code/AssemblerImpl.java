package code;

import java.util.ArrayList;
import java.util.List;

public class AssemblerImpl implements Assembler {
	private List<Data> programCode; //The assembly language program to be passed to the loader (arraylist expands to fit)
	
	
	
	public AssemblerImpl() {
		this.programCode = new ArrayList<Data>();
	}
	
	//Check code once translated; ensure that a HALT instruction is the last instruction (if one is missing
	//in the translated code, insert one by default).
	

}
