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
	 * This method takes a line of code and splits it into an array list of Strings, which can then be passed
	 * to another method for interpretation (i.e. so that instructions or operands can be created). For example
	 * a line reading: LOAD var1, r0 would be broken up into an array list of size 3, each index holding one word
	 * from the line of code. This simplifies the interpretation of a line of code.
	 * 
	 * @param String line the line of code to be broken up into components.
	 * @return List<String> the line of code broken up into an array list.
	 */
	public List<String> splitCodeLine(String line);
	
	public List<String> getProgramString();
	
	public void assembleCode();
	
	/*
	 * This method assembles the operandArray field into Data (Operand) types, and stores them
	 * in the programCode array. They are stored at an index that exceeds what will be the last
	 * address of the instruction code so that operands appear in memory at the end of the program.
	 * 
	 * @param List<String> operandParts represents one line of program code broken up into an array for
	 * easier interpretation.
	 * 
	 * @return Data the line of program code assembled into type Data.
	 */
	public Data assembleOperand(List<String> operandParts);
	
	public Data assembleInstruction(List<String> instructionParts);
	
	public void mapInstructionLabel(List<String> lineParts, int lineNum);
	
	public void loadToLoader();
	
	/*
	 * This method splits the assembly program stored as an array list of Strings in programString into
	 * two distinct array lists (also of type String). The idea is that operand declarations are stored
	 * separately from instructions, so that they may have their symbolic memory references mapped to 
	 * real memory addresses before instructions are assembled. This means that when instructions come to be
	 * interpreted and assembled, the symbolic references to operands can be resolved using the lookupTable, to
	 * which symbolic operand references are added and mapped to real memory addresses.
	 */
	public void separateOperands();
	
	public List<String> getInstructionArray();
	
	public List<String> getOperandArray();
	
	public Map<String, Integer> getLookupTable();

	public Data[] getProgramCode();

}
