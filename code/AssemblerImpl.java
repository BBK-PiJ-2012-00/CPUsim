package code;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

public class AssemblerImpl implements Assembler {
	private Data[] programCode; //The assembly language program to be passed to the loader 
	
	private File fileReference; //Reference to the text file containing assembly program
	
	private Loader loader; //Reference to loader module
	
	private List<String> programString; //Holds program code read from file as array list of Strings
	private List<String> displayProgram; //Holds program code in format better suited for display purposes (retains spaces)
	private List<String> leadingComments; //To store initial comments at start of file (those not embedded in code)
	private List<String> instructionArray; //For intermediate stage where programString is split into two, instructions being stored
	private List<String> operandArray; //in instructionArray, operands being stored in operandArray (as Strings)
		
	private Map<String, Integer> lookupTable; //For associating labels with memory addresses	
	
	private int operandAddressPointer;//Used to reference the final instruction line address (usually HALT) so that
									 //operands can be stored after this point.
	
	
	public AssemblerImpl(Loader loader) {
		this.programString = new ArrayList<String>();
		this.displayProgram = new ArrayList<String>();
		this.leadingComments = new ArrayList<String>();
		this.lookupTable = new HashMap<String, Integer>();
		this.loader = loader;
	}
	
	
	@Override
	public void selectFile(File fileName) { //For selecting assembly program file
		this.fileReference = fileName;
	}
	
	
	@Override
	public void readAssemblyFile() {
		String line;
		Scanner s = null;
		boolean leadingCommentsFinished = false; //To signal end of any leading comments

	    try {
	        s = new Scanner(new BufferedReader(new FileReader(fileReference)));
	
	        while (s.hasNextLine()) {	        	
	        	line = s.nextLine();
	        	if (!leadingCommentsFinished && line.length() != 0) {
	        		if (line.startsWith("#")) {
	        			leadingComments.add(line); //Add any comments at beginning of file to separate list for better display        			
	        		}
	        		else {
	        			leadingCommentsFinished = true;
	        		}
	        	}
	        	if (leadingCommentsFinished) { //Don't add leading comments to programString
	        		if (line.matches("[\\s]*")) { //Do not add lines consisting of only blank spaces to programString
	        			displayProgram.add(line); //Ok to add blank lines to the list of code for GUI display
	        		}
	        		else {
	        			programString.add(line);
	        			displayProgram.add(line);
	        		}
	        	
	        	}
	        }
	    } 
	    catch (FileNotFoundException ex) {
	    	this.fileReference = null;
	    	JOptionPane.showMessageDialog(null, "File not found!", "Error!", JOptionPane.WARNING_MESSAGE);
	    }	    
	    finally {
	    	if (s != null) {
	    		s.close();
	    	}
	    }
	}
	
	
	@Override
	public void separateOperands() {
		operandArray = new ArrayList<String>();
		instructionArray = new ArrayList<String>();
		for (int i = 0; i < programString.size(); i++) { 
			if (programString.get(i).contains("DATA")) { //Only operand declarations contain this String sequence
				operandArray.add(programString.get(i));
			}
			else if (programString.get(i).trim().startsWith("#")) { //Detect comments embedded between lines of code
				//Do nothing; don't add comment lines to instruction or operand arrays
			}
			else {
				instructionArray.add(programString.get(i));
			}
		}
		programCode = new Data[instructionArray.size() + operandArray.size()];
		operandAddressPointer = instructionArray.size(); //Set operand address pointer to a location that will come immediately
														//after the last instruction (deduced by size of instructionArray).
	}
	
	
	@Override
	public Data assembleOperand(List<String> operandParts) {
		//All operand part arrays will contain 3 parts: label, DATA declaration, and operand value
		String label = operandParts.get(0).substring(0, operandParts.get(0).length() -1); //Trim semicolon from label
		lookupTable.put(label, operandAddressPointer); //Map label to address		
		
		String operandString = operandParts.get(2); //Operand value as String
		int operandValue;
		try {
			operandValue = Integer.parseInt(operandString); //Attempt to parse String to int
		}
		catch (NumberFormatException nfe) {
			JOptionPane.showMessageDialog(null, "Assembly program syntax error: Invalid operand declaration \"" + operandString +
					"\".", "Assembly Program Error", JOptionPane.WARNING_MESSAGE);
			return null; //Return null in the event of an error; prevents further parsing of file and flags error to user
		}
		
		Data operand = new OperandImpl(operandValue); //Create the operand
		
		return operand;		
	}
	
	
	@Override
	public List<String> splitCodeLine(String line) {
		Scanner sc;
		Pattern delimiterPattern = Pattern.compile("[\\,]?[\\s]+"); //splits a String on one or more white spaces, or a comma
		//followed by a whitespace -> this separates each line of assembly code into bits for processing into instructions.
		
		List<String> splitLine = new ArrayList<String>(); //Array to hold one line of code, split up into parts

		if (line.contains("#")) { //If the line of code contains a comment, remove the comment part
			String[] halvedLine = line.split("[\\#]"); //Split the line on the comment delimiter
			line = halvedLine[0]; 
		}
		
		sc = new Scanner(line);
		sc.useDelimiter(delimiterPattern);
		while (sc.hasNext()) { //Add each part of an instruction/declaration to splitLine
			splitLine.add(sc.next());
		}
		sc.close();			

		return splitLine;		
	}
	
	
	@Override
	public void mapInstructionLabel(List<String> instructionParts, int lineNum) {
		if (instructionParts.get(0).endsWith(":")) { //Ending with ":" indicates presence of a label, else do nothing
			String label = instructionParts.get(0).substring(0, instructionParts.get(0).length() - 1); //Trim colon from end
			lookupTable.put(label, lineNum);
		}
		return;
	}
	
	
	@Override
	public boolean assembleCode() {
		this.readAssemblyFile();
		if (this.fileReference != null) { //Only assemble if file successfully opened
			//Operands are assembled first so their symbolic references can be mapped to actual addresses
			this.separateOperands();
					
			//Assemble the operands (represented as Strings) to real Operands and put them in programCode
			for (int i = 0; i < operandArray.size(); i++) {
				List<String> lineComponents = this.splitCodeLine(operandArray.get(i)); //Break line of code into parts
				
				Data operand = this.assembleOperand(lineComponents);
				if (operand == null) { //Indicates an error in assembling an operand
					return false; //Signals error to GUI code and prevents loading of invalid assembly file
				}
				programCode[operandAddressPointer] = operand; //Add the operand to the data array, at specified address
				operandAddressPointer++; //Increment so that the next operand will be stored in the next consecutive address
			}
			
			/*
			 * Instruction labels (if present) must be mapped prior to assembling instructions. 
			 */
			for (int i = 0; i < instructionArray.size(); i++) {
				List<String> lineComponents = this.splitCodeLine(instructionArray.get(i));
				this.mapInstructionLabel(lineComponents, i); 		
			}			
	
			//Assemble the instructions represented as Strings into type Instruction, and put into programCode array
			for (int i = 0; i < instructionArray.size(); i++) { 	
				List<String> lineComponents = this.splitCodeLine(instructionArray.get(i)); //Break a line of code into parts
				
				Data instruction = this.assembleInstruction(lineComponents); //Create an instruction/operand from the line components
				if (instruction == null) { //Only happens if an error occurs in instruction parsing
					return false; //Signals an error to GUI code, prevents loading of assembly file
				}
				programCode[i] = instruction; //Add the instruction/operand to an array list, to be later passed into memory
			}
			
			return true;
		}
		
		return false; //If file not found		
	}
	
	
	@Override
	public Data assembleInstruction(List<String> instructionParts) {
		Data data = null;
		
		if (instructionParts.get(0).endsWith(":")) { //Indicates presence of a label
			instructionParts.remove(0); //Remove label (already added to the lookup table)
		}		

		//This means a memory source and register destination are specified (these opcodes all follow same format)
		if (instructionParts.get(0).equals("LOAD") || instructionParts.get(0).equals("BRE") ||
				instructionParts.get(0).equals("BRNE")) { 
			
			String opcode = instructionParts.get(0);
			String destinationString;
			int destination;
			
			//Ensure the instruction has two fields
			try {
				destinationString = instructionParts.get(2).substring(1); //Trim leading 'r' off			
			}
			catch (IndexOutOfBoundsException iob) { //Occurs if less than 2 arguments specified
				JOptionPane.showMessageDialog(null, "Assembly program syntax error: Ensure that\n" + instructionParts.get(0) +
						" instructions have two arguments/fields.", "Assembly Program Error", JOptionPane.WARNING_MESSAGE);
				return null; //Prevent further parsing
			}
			
			if (destinationString.equals("CC")) { //A load instruction may reference condition code/status register
				destination = 16;
			}
			
			else {
				//Error handling for register destination reference
				try {
					destination = Integer.parseInt(destinationString);
					if (destination < 0 || destination > 16) { //Illegal register reference
						throw new IllegalStateException("Invalid register reference in " + opcode + " instruction");
					}
				}
				catch (NumberFormatException nfe) {
					JOptionPane.showMessageDialog(null, "Assembly program syntax error: Illegal register\n" +
							"reference \"" + instructionParts.get(2) + "\" in " + opcode + " instruction!", 
							"Assembly Program Error", JOptionPane.WARNING_MESSAGE);
					return null; //Prevent further parsing
				}
				catch (IllegalStateException ise) {
					JOptionPane.showMessageDialog(null, "Assembly program syntax error: Illegal register\n" +
							"reference \"" + instructionParts.get(2) + "\" in " + opcode + " instruction! Register " +
							"references should\n be between r0 to r15 (or rCC as the destination register of LOAD or MOVE instructions).",
							"Assembly Program Error", JOptionPane.WARNING_MESSAGE);
					return null; //Prevent further parsing
				}
				
			}				
			
			int source;
			try {
				source = lookupTable.get(instructionParts.get(1)); //Memory addresses are always symbolic
			}
			catch (NullPointerException npe) {
				JOptionPane.showMessageDialog(null, "Assembly program syntax error: Label reference \"" + 
						instructionParts.get(1) + "\" has not been declared.", "Assembly Program Error", JOptionPane.WARNING_MESSAGE);
				return null; //Prevent further parsing
			}
			
			if (opcode.equals("LOAD")) {
				data = new TransferInstr(Opcode.LOAD, source, destination);
				return data;
			}
			
			if (opcode.equals("BRE")) {
				data = new BranchInstr(Opcode.BRE, source, destination);
				return data;
			}
			
			if (opcode.equals("BRNE")) {
				data = new BranchInstr(Opcode.BRNE, source, destination);
				return data;
			}			
			
		}
		
		
		else if (instructionParts.get(0).equals("STORE")) { //This means a register source and memory destination are specified
			int destination;
			
			try {
				destination = lookupTable.get(instructionParts.get(2)); //Look up symbolic destination
			}
			catch (NullPointerException npe) {
				JOptionPane.showMessageDialog(null, "Assembly program syntax error: Label reference \"" + 
						instructionParts.get(2) + "\" has not been declared.", "Assembly Program Error", JOptionPane.WARNING_MESSAGE);
				return null; //Prevent further parsing
			}
			catch (IndexOutOfBoundsException iob) { //Occurs if less than 2 arguments specified
				JOptionPane.showMessageDialog(null, "Assembly program syntax error: Ensure that\n" + instructionParts.get(0) +
						" instructions have two arguments/fields.", "Assembly Program Error", JOptionPane.WARNING_MESSAGE);
				return null; //Prevent further parsing
			}
			
			String sourceString;
			try {
				sourceString = instructionParts.get(1).substring(1);//Trim leading 'r' off
			}
			catch (IndexOutOfBoundsException iob) { //Occurs if less than 2 arguments specified
				JOptionPane.showMessageDialog(null, "Assembly program syntax error: Ensure that\n" + instructionParts.get(0) +
						" instructions have two arguments/fields.", "Assembly Program Error", JOptionPane.WARNING_MESSAGE);
				return null; //Prevent further parsing
			}
			
			int source;
			
			try {
				source = Integer.parseInt(sourceString);	
				if (source < 0 || source > 15) { //rCC not included in store instructions, hence 15 is upper bound
					throw new IllegalStateException("Invalid register reference in STORE instruction");
				}
			}
			catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(null, "Assembly program syntax error: Illegal register reference\n" +
						"\"" + instructionParts.get(1) + "\" in STORE instruction!", "Assembly Program Error", 
						JOptionPane.WARNING_MESSAGE);
				return null; //Prevent further parsing
			}
			catch (IllegalStateException ise) {
				JOptionPane.showMessageDialog(null, "Assembly program syntax error: Illegal register reference\n" +
						"\"" + instructionParts.get(1) + "\" in STORE instruction! Register references should\nbe between r0 and r15.",
						"Assembly Program Error", JOptionPane.WARNING_MESSAGE);
				return null; //Prevent further parsing
			}
			
			data = new TransferInstr(Opcode.STORE, source, destination);
			return data;
		}
		
		//Register to register operations all follow this block
		else if (instructionParts.get(0).equals("MOVE") || instructionParts.get(0).equals("ADD") || 
				instructionParts.get(0).equals("SUB") || instructionParts.get(0).equals("DIV") ||
				instructionParts.get(0).equals("MUL")) {
			
			String opcode = instructionParts.get(0);
			String sourceString;				
			//Register source, register destination
			
			//Ensure instruction has right number of fields
			try {
				sourceString = instructionParts.get(1).substring(1);
			}
			catch (IndexOutOfBoundsException iob) {
				JOptionPane.showMessageDialog(null, "Assembly program syntax error: Ensure that\n" + instructionParts.get(0) +
						" instructions have two arguments/fields.", "Assembly Program Error", JOptionPane.WARNING_MESSAGE);
				return null; //Prevent further parsing
			}
			int source;
			
			try {
				source = Integer.parseInt(sourceString);
				if (source < 0 || source > 16) { //rCC references allowed in MOVE instruction (ArithmeticInstr catches error)
					throw new IllegalStateException("Illegal register reference");
				}
			}
			catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(null, "Assembly program syntax error: Illegal register\n" +
						"reference \"" + instructionParts.get(1) + "\" in " + opcode + " instruction!", 
						"Assembly Program Error", JOptionPane.WARNING_MESSAGE);
				return null; //Prevent further parsing
			}
			catch (IllegalStateException ise) {
				JOptionPane.showMessageDialog(null, "Assembly program syntax error: Illegal register " +
						"reference \"" + instructionParts.get(1) + "\" in " + opcode + " instruction! Register \nreferences should " +
						"be between r0 to r15 (or rCC as the register destination for \nLOAD or MOVE instructions).",
						"Assembly Program Error", 
						JOptionPane.WARNING_MESSAGE);
				return null; //Prevent further parsing
			}
			catch (IndexOutOfBoundsException iob) {
				JOptionPane.showMessageDialog(null, "Assembly program syntax error: Ensure that\n" + instructionParts.get(0) +
						" instructions have two arguments/fields.", "Assembly Program Error", JOptionPane.WARNING_MESSAGE);
				return null; //Prevent further parsing
			}
			
			int destination;
			String destinationString;
			
			//Ensure there is a destination field
			try {			
				destinationString = instructionParts.get(2).substring(1); //Trim leading 'r' off
			}
			catch (IndexOutOfBoundsException iob) {
				JOptionPane.showMessageDialog(null, "Assembly program syntax error: Ensure that\n" + instructionParts.get(0) +
						" instructions have two arguments/fields.", "Assembly Program Error", JOptionPane.WARNING_MESSAGE);
				return null; //Prevent further parsing
			}
			
			if (destinationString.equals("CC")) { //A load instruction may reference condition code/status register
				destination = 16;
			}
			
			else {
				try {
					destination = Integer.parseInt(destinationString);
					if (destination < 0 || destination > 16) {
						throw new IllegalStateException("Illegal register reference");
					}
				}
				catch (NumberFormatException nfe) {
					JOptionPane.showMessageDialog(null, "Assembly program syntax error: Illegal register\n" +
							"reference \"" + instructionParts.get(2) + "\" in " + opcode + " instruction!", 
							"Assembly Program Error", JOptionPane.WARNING_MESSAGE);
					return null; //Prevent further parsing
				}
				catch (IllegalStateException ise) {
					JOptionPane.showMessageDialog(null, "Assembly program syntax error: Illegal register " +
							"reference \"" + instructionParts.get(2) + "\" in " + opcode + " instruction! Register \nreferences should " +
							"be between r0 to r15 (or rCC as the register destination for \nLOAD or MOVE instructions).",
							"Assembly Program Error", 
							JOptionPane.WARNING_MESSAGE);
					return null; //Prevent further parsing
				}
			}	
			
			if (opcode.equals("MOVE")) {
				data = new TransferInstr(Opcode.MOVE, source, destination);
				return data;
			}
			
			
			if (opcode.equals("ADD")) {
				try {
					data = new ArithmeticInstr(Opcode.ADD, source, destination);
				}
				catch (IllegalStateException ise) { //May be thrown by ArithmeticInstr if rCC referenced (not allowed)
					JOptionPane.showMessageDialog(null, ise.getMessage(), "Assembly Program Error", JOptionPane.WARNING_MESSAGE);
					return null; //Prevent further parsing
				}
				return data;
			}
			
			if (opcode.equals("SUB")) {
				try {
					data = new ArithmeticInstr(Opcode.SUB, source, destination);
				}
				catch (IllegalStateException ise) {
					JOptionPane.showMessageDialog(null, ise.getMessage(), "Assembly Program Error", JOptionPane.WARNING_MESSAGE);
					return null; //Prevent further parsing
				}
				return data;
			}
			
			if (opcode.equals("DIV")) {
				try {
					data = new ArithmeticInstr(Opcode.DIV, source, destination);
				}
				catch (IllegalStateException ise) {
					JOptionPane.showMessageDialog(null, ise.getMessage(), "Assembly Program Error", JOptionPane.WARNING_MESSAGE);
					return null; //Prevent further parsing
				}
				return data;
			}
			
			if (opcode.equals("MUL")) {
				try {
					data = new ArithmeticInstr(Opcode.MUL, source, destination);
				}
				catch (IllegalStateException ise) {
					JOptionPane.showMessageDialog(null, ise.getMessage(), "Assembly Program Error", JOptionPane.WARNING_MESSAGE);
					return null; //Prevent further parsing
				}
				return data;
			}				
		}
		
		//These opcodes have the same instruction format; just a symbolic memory address
		else if (instructionParts.get(0).equals("BR") || instructionParts.get(0).equals("BRZ")) {
			String opcode = instructionParts.get(0);
			
			if (instructionParts.size() > 2) {
				JOptionPane.showMessageDialog(null, "Assembly program syntax error: " + opcode + " insructions \n" +
						" should have one target field.", "Assembly Program Error", JOptionPane.WARNING_MESSAGE);
				return null; //Prevent assembly
			}
			
			int destination;
			
			try {
				destination = lookupTable.get(instructionParts.get(1)); //Get value for symbolic memory address ref.
			}
			catch (NullPointerException npe) {
				JOptionPane.showMessageDialog(null, "Assembly program syntax error: Label reference \"" + 
						instructionParts.get(1) + "\" in " + opcode + "\ninstruction has not been declared.", 
						"Assembly Program Error", JOptionPane.WARNING_MESSAGE);
				return null; //Prevent further parsing
			}
			catch (IndexOutOfBoundsException iob) { //Occurs if less than 2 arguments specified
				JOptionPane.showMessageDialog(null, "Assembly program syntax error: Ensure that\n" + instructionParts.get(0) +
						" instructions have a target field.", "Assembly Program Error", JOptionPane.WARNING_MESSAGE);
				return null; //Prevent further parsing
			}
			
			if (opcode.equals("BR")) {
				data = new BranchInstr(Opcode.BR, destination);
			}
			else {
				data = new BranchInstr(Opcode.BRZ, destination);
			}
			return data;
		}
		
		else if (instructionParts.get(0).equals("SKZ") || instructionParts.get(0).equals("HALT")) {
			
			String opcode = instructionParts.get(0);
			
			if (instructionParts.size() > 1) {
				JOptionPane.showMessageDialog(null, "Assembly program syntax error: " + opcode + " insructions \n" +
						" should not have any reference/target fields.", "Assembly Program Error", JOptionPane.WARNING_MESSAGE);
				return null; //Prevent assembly
			}
			
			if (opcode.equals("SKZ")) {
				data = new BranchInstr(Opcode.SKZ);
			}
			else {
				data = new HaltInstr(Opcode.HALT);
			}
			return data;
		}
			
		else { //Opcode not found
			JOptionPane.showMessageDialog(null, "Assembly program syntax error: invalid opcode encountered:\n        \"" +
					instructionParts.get(0) + "\" is not recognised. \nPlease ensure all instruction opcodes are valid " +
					"and that all operand \ndeclarations are of the format <Label>: DATA <integer>", 
					"Assembly Program Error", JOptionPane.WARNING_MESSAGE);
			return null; //Prevents further parsing

		}		
		return data;
	}
	
	@Override
	public void loadToLoader() {
		loader.clear(); //Has the effect of resetting the loader each time, ensuring no remnant data from the last use
		loader.load(this.programCode);
	}
	
	@Override
	public List<String> getProgramString() {
		return this.programString;
	}
	
	@Override
	public List<String> getOperandArray() {
		return this.operandArray;
	}
	
	@Override
	public List<String> getInstructionArray() {
		return this.instructionArray;
	}


	@Override
	public Map<String, Integer> getLookupTable() {
		return this.lookupTable;
	}
	
	@Override
	public Data[] getProgramCode() {
		return this.programCode;
	}
	
	@Override
	public List<String> getLeadingComments() {
		return this.leadingComments;
	}
	
	@Override
	public Loader getLoader() { //Returns reference to loader, for testing
		return this.loader;
	}

	
	
	/*
	 * For GUI display
	 */
	@Override
	public String display() { //Displays assembly language program with line numbers
		String displayString = "     <Label>:  <Instruction/Operand>  <#Comment>\n\n";
		
		if (leadingComments.size() > 0) { //Display leading comments, if there are any
			for (int i = 0; i < leadingComments.size(); i++) {
				displayString += leadingComments.get(i) + "\n";
			}
			displayString += "\n"; //Add extra space for clarity
		}
		
		int lineReference = 0; //For display of line numbers, including blank lines
		for (int i = 0; i < displayProgram.size(); i++) {
			if (lineReference < 10) { //Line number formatting
				displayString += "0" + lineReference + "|  " + displayProgram.get(i) + "\n";		
				lineReference++;
			}
			else {
				displayString += lineReference + "|  " + displayProgram.get(i) + "\n";
				lineReference++;
			}
		}

		return displayString;
	}


}
