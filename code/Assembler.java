package code;

import java.util.List;
import java.util.Map;

/*
 * Reads a text file containing assembly language. Can initially read into an array of Strings, from which an array of Instructions is
 * then created.
 */

public interface Assembler {
	
	public void selectFile(String fileName);
	
	public void readAssemblyFile();
	
	/*
	 * Method takes a line of code and splits it into an array list of Strings, which can then be passed
	 * to another method for interpretation (i.e. so that instructions or operands can be created).
	 */
	public List<String> splitCodeLine(String line);
	
	public List<String> getProgramString();
	
	public void assembleCode();
	
	public Data assembleOperand(List<String> operandParts);
	
	public Data assembleInstruction(List<String> instructionParts, int lineNum);
	
	public void loadToLoader(Data[] programCode);
	
	public void separateOperands();
	
	public List<String> getInstructionArray();
	
	public List<String> getOperandArray();
	
	public Map<String, Integer> getLookupTable();

}
