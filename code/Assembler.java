package code;

import java.util.List;
import java.util.Map;

/*
 * Reads a text file containing assembly language, assembles the code and passed it the the loader.
 */

public interface Assembler {
	
	/*
	 * A method for referencing a text file containing the assembly program.
	 * 
	 * @param String fileName the path to the text file.
	 */
	public void selectFile(String fileName);
	
	/*
	 * Reads the text file passed as a reference via selectFile(), and places the code line by line
	 * into the programString array list.
	 */
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
	
	/*
	 * There is an error if a label is referenced that has not yet been added to the lookup table; instructionArray
	 * needs to be cycled through first, with any labels being mapped before the code can be assembled.
	 */
	/*
	 * assembleCode() prompts the conversion of an assembly file into a Data array containing the program code,
	 * ready for passing to the loader. Operands are first mapped from symbolic to physical memory addresses before
	 * being assembled and added to the programCode array. Instruction labels are also mapped to memory addresses, before
	 * instructions themselves are assembled and added to programCode. This method relies on many of the other methods
	 * of the assembler to do this.
	 */	
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
	
	
	/*
	 * Assembles the program instructions held in instructionArray as Strings to objects of type Data
	 * (Instruction).  
	 * 
	 * @param List<String> instructionParts one line of code representing an instruction broken into
	 * an array for easier interpretation.
	 * @return Data the line of code representing an instruction assembled into type Data.
	 */
	public Data assembleInstruction(List<String> instructionParts);
	
	/*
	 * A method for mapping instruction labels to memory addresses. The memory addresses are
	 * absolute but could also be used as an offset from a base address, if the loader implements
	 * relocatable addressing as an extension.  
	 * 
	 * @param List<String> lineParts a line of code broken up into an array, for simpler interpretation.
	 * @param int lineNum the line number of the line of code, which is used as an address reference.
	 */
	public void mapInstructionLabel(List<String> lineParts, int lineNum);
	
	
	/*
	 * Loads programCode containing the program code into the loader.
	 */
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
