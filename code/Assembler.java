package code;

import java.io.File;
import java.util.List;
import java.util.Map;

/*
 * A class to represent an assembler.  It reads a text file containing assembly language, assembles 
 * the code and passes it the the loader module.
 */

public interface Assembler {
	
	/*
	 * A method for referencing a text file containing the assembly program.
	 * 
	 * @param String fileName the path to the text file.
	 */
	public void selectFile(File fileName);
	
	/*
	 * Reads the text file passed as a reference via selectFile(), and places the code line by line
	 * into the programString, displayProgram and leadingComments array lists. Blank lines are omitted
	 * using a regular expression when adding lines to programString, as it is from this array that 
	 * code is assembled. The other two array lists are for GUI display purposes.
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
	
	
	
	/*
	 * assembleCode() prompts the conversion of an assembly file into a Data array (programCode field) containing 
	 * the program code, ready for passing to the loader. Operands are first mapped from symbolic to physical memory 
	 * addresses before being assembled and added to the programCode array. Instruction labels are also mapped to memory 
	 * addresses, before instructions themselves are assembled and added to programCode. This method invokes many of the 
	 * other methods of the assembler to do this.  If any syntax errors in the assembly program are encountered, this method 
	 * returns false, signalling to the GUI code that the file open operation should be cancelled. A pop up warning is
	 * also displayed to the user, alerting them to specific errors in the assembly program file.
	 * 
	 * @return true if file found and assembled without errors, false otherwise.
	 */	
	public boolean assembleCode();
	
	
	/*
	 * This method assembles the operandArray field into Data (Operand) types, and stores them
	 * in the programCode array. They are stored at an index that exceeds what will be the last
	 * address of the instruction code so that operands appear in memory at the end of the program.
	 * Returns null if assembly of an instruction was unsuccessful due to invalid assembly
	 * program syntax. 
	 * 
	 * @param List<String> operandParts represents one line of program code broken up into an array for
	 * easier interpretation. Only lines of code containing the "DATA" keyword are added to operandParts.
	 * 
	 * @return Data the line of program code assembled into type Data.
	 */
	public Data assembleOperand(List<String> operandParts);
	
	
	/*
	 * Assembles the program instructions held in instructionArray as Strings to objects of type Data
	 * (Instruction).  Returns null if assembly of an instruction was unsuccessful due to invalid assembly
	 * program syntax. 
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
	 * @param List<String> lineParts a line of code broken up into an array list, for simpler interpretation.
	 * @param int lineNum the line number of the line of code, which is used as an address reference.
	 */
	public void mapInstructionLabel(List<String> lineParts, int lineNum);
	
	
	/*
	 * Loads programCode containing the program code into the loader. It clears the loader before
	 * loading to ensure no old assembly code remnants are present.
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
	
	
	/*
	 * A getter method for the programString array list field, which holds the assembly language
	 * code read from a text file.
	 * 
	 * @return List<String> the programString field.
	 */
	public List<String> getProgramString();
	
	/*
	 * A getter method for the instructionArray field, which holds the instruction declarations
	 * of an assembly program (no operand declarations).
	 * 
	 * @return List<String> the instructionArray field.
	 */
	public List<String> getInstructionArray();
	
	
	/*
	 * A getter method for the operandArray field, which holds the operand declarations
	 * of an assembly program (no instruction declarations).
	 * 
	 * @return List<String> the operandArray field.
	 */
	public List<String> getOperandArray();
	
	
	/*
	 * A getter method used to access the lookupTable field, which maps symbolic addresses
	 * to physical memory addresses.
	 * 
	 * @return Map<String, Integer> the lookupTable field.
	 */
	public Map<String, Integer> getLookupTable();

	
	/*
	 * A getter method for accessing the programCode Data array, which contains the assembly code
	 * ready to be passed to the loader, for passing to memory.  
	 * 
	 * @return Data[] the assembly program code as a Data array.
	 */
	public Data[] getProgramCode();	
	
	
	/*
	 * A getter method for accessing the loader.
	 * 
	 * @return Loader the loader module to which assembled code is passed for transfer to memory.
	 */
	public Loader getLoader();

	
	/*
	 * A getter method for accessing the leadingComments field, an array list which holds any comments
	 * present at the beginning of an assembly language file. These are separated from the program code
	 * during assembly but should still appear on the GUI display when the file is opened. Separating them
	 * also allows for the leading comments to be displayed outside of line numbers, which makes for better
	 * readability as display room on the GUI is tight.
	 * 
	 * @return List<String> the leadingComments field.
	 */
	public List<String> getLeadingComments();
	
	/*
	 * Renders the assembly code read from the text file containing it in a format suitable for display
	 * on the GUI.  Adds headers and line numbers.
	 * 
	 * @return String the assembly code in String format.
	 */
	public String display();

}
