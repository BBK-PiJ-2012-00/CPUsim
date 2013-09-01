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
	private List<String> instructionArray; //For intermediate stage where programString is split into two, instructions being stored
	private List<String> operandArray; //in instructionArray, operands being stored in operandArray (as Strings)
		
	private Map<String, Integer> lookupTable; //For associating labels with relative addresses	
	
	private int operandAddressPointer;
	
	
	public AssemblerImpl(Loader loader) {
		this.programString = new ArrayList<String>();
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

	    try {
	        s = new Scanner(new BufferedReader(new FileReader(fileReference)));
	
	        while (s.hasNextLine()) {	        	
	        	line = s.nextLine();
	        	if (line.length() != 0) { //Don't add blank lines to the array
	        		// System.out.println(line); //For testing
		            programString.add(line);
	        	}	           
	        }
	    } 
	    catch (FileNotFoundException ex) {
	    	ex.printStackTrace();
	    	this.fileReference = null;
	    	JOptionPane.showMessageDialog(null, "File not found!", "Error!", JOptionPane.WARNING_MESSAGE);
	    }	    
	    finally {
	    	if (s != null) {
	    		s.close();
	        }
	    }
	}
	
	/*
	 * (non-Javadoc)
	 * @see code.Assembler#separateOperands()
	 * 
	 * Set to miss header line ONLY if headers are present!! (i.e. if < is first non-blank character)
	 */
	@Override
	public void separateOperands() {
		operandArray = new ArrayList<String>();
		instructionArray = new ArrayList<String>();
		for (int i = 0; i < programString.size(); i++) { //Start at 1 to miss out header line
			if (programString.get(i).contains("DATA")) { //Only operand declarations contain this String sequence
				operandArray.add(programString.get(i));
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
		//All operand part arrays will contain 3 parts: symbol, DATA declaration, and operand value
		String symbol = operandParts.get(0).substring(0, operandParts.get(0).length() -1); //Trim semicolon from symbol
		lookupTable.put(symbol, operandAddressPointer); //Map symbol to address		
		
		String operandString = operandParts.get(2); //Operand value as String
		int operandValue = Integer.parseInt(operandString);
		//Handle exception for illegal operands!
		Data operand = new OperandImpl(operandValue);
		
		return operand;		
	}
	
	
	@Override
	public List<String> splitCodeLine(String line) {
		Scanner sc;
		Pattern delimiterPattern = Pattern.compile("[\\,]?[\\s]+"); //splits a String on one or more whitespaces, or a comma
		//followed by a whitespace -> this separates each line of assembly code into bits for processing into instructions.
		
		List<String> splitLine = new ArrayList<String>(); //Array to hold one line of code, split up into parts

		if (line.contains("#")) { //If the line of code contains a comment, remove the comment part
			String[] halvedLine = line.split("[\\#]");
			line = halvedLine[0]; 
		}
		//What if a line is a comment? I.e. only a comment on that line and nothing else?
		
		sc = new Scanner(line);
		sc.useDelimiter(delimiterPattern);
		while (sc.hasNext()) { //Add each part of an instruction/declaration to splitLine
			splitLine.add(sc.next());
		}
		sc.close();			

		return splitLine;		
	}
	
	public void mapInstructionLabel(List<String> instructionParts, int lineNum) {
		if (instructionParts.get(0).endsWith(":")) {
			String label = instructionParts.get(0).substring(0, instructionParts.get(0).length() - 1); //Trim colon from end
			lookupTable.put(label, lineNum);
		}
		return;
	}
	
	
	@Override
	public boolean assembleCode() {
		this.readAssemblyFile();
		if (this.fileReference != null) { //Only assemble if file successfully opened
			//Operands assembled first so their symbolic references can be mapped to actual addresses
			this.separateOperands();
					
			//Assemble the operands (represented as Strings) to real Operands and put them in programCode
			for (int i = 0; i < operandArray.size(); i++) {
				List<String> lineComponents = this.splitCodeLine(operandArray.get(i)); //Break line of code into parts
				
				Data operand = this.assembleOperand(lineComponents);
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
			
			//String destinationString = instructionParts.get(i+2).substring(1); //Trim leading 'r' off
			String destinationString = instructionParts.get(2).substring(1); //Trim leading 'r' off
			//register source to leave an integer reference
			
			int destination;
			
			if (destinationString.equals("CC")) { //A load instruction may reference condition code/status register
				destination = 16;
			}
			
			else {
				//Error handling for register destination reference
				try {
					destination = Integer.parseInt(destinationString);
					if (destination < 0 || destination > 16) { //Illegal register reference
						throw new IllegalStateException("Invalid register reference in LOAD/BRE/BRNE instruction");
					}
				}
				catch (NumberFormatException nfe) {
					JOptionPane.showMessageDialog(null, "Assembly program syntax error: Illegal register destination\n" +
							"reference in LOAD/BRE/BRNE instruction!", "Assembly Program Error", JOptionPane.WARNING_MESSAGE);
					return null; //Prevent further parsing
				}
				catch (IllegalStateException ise) {
					JOptionPane.showMessageDialog(null, "Assembly program syntax error: Illegal register destination\n" +
							"reference in " + opcode + " instruction! Register references should\n be between r0 to r16 (or " +
							"rCC as the destination register of LOAD or MOVE instructions).",
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
			
			String sourceString = instructionParts.get(1).substring(1);//Trim leading 'r' off
			int source;
			try {
				//register source to leave an integer reference
				source = Integer.parseInt(sourceString);	
				if (source < 0 || source > 15) { //rCC not included in store instructions, hence 15 is upper bound
					throw new IllegalStateException("Invalid register reference in STORE instruction");
				}
			}
			catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(null, "Assembly program syntax error: Illegal register destination\n" +
						"reference in STORE instruction!", "Assembly Program Error", JOptionPane.WARNING_MESSAGE);
				return null; //Prevent further parsing
			}
			catch (IllegalStateException ise) {
				JOptionPane.showMessageDialog(null, "Assembly program syntax error: Illegal register destination\n" +
						"reference in STORE instruction! Register references should\nbe between r0 to r16.",
						"Assembly Program Error", JOptionPane.WARNING_MESSAGE);
				return null; //Prevent further parsing
			}
			
			data = new TransferInstr(Opcode.STORE, source, destination);
			return data;
		}
		
		//Register to register opcodes all follow this block (all register-register)
		else if (instructionParts.get(0).equals("MOVE") || instructionParts.get(0).equals("ADD") || 
				instructionParts.get(0).equals("SUB") || instructionParts.get(0).equals("DIV") ||
				instructionParts.get(0).equals("MUL")) {
			
			String opcode = instructionParts.get(0);
							
			//Register source, register destination
			String sourceString = instructionParts.get(1).substring(1);
			int source;
			try {
				source = Integer.parseInt(sourceString);
				if (source < 0 || source > 16) { //rCC references allowed in MOVE instruction (ArithmeticInstr catches error)
					throw new IllegalStateException("Illegal register reference");
				}
			}
			catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(null, "Assembly program syntax error: Illegal register destination\n" +
						"reference in " + opcode + " instruction!", "Assembly Program Error", JOptionPane.WARNING_MESSAGE);
				return null; //Prevent further parsing
			}
			catch (IllegalStateException ise) {
				JOptionPane.showMessageDialog(null, "Assembly program syntax error: Illegal register " +
						"reference in " + opcode + " instruction! Register \nreferences should be between r0 to r16 (or rCC " +
						"as the register destination for \nLOAD or MOVE instructions).", "Assembly Program Error", 
						JOptionPane.WARNING_MESSAGE);
				return null; //Prevent further parsing
			}
			
			int destination;
			
			String destinationString = instructionParts.get(2).substring(1); //Trim leading 'r' off
			if (destinationString.equals("CC")) { //A load instruction may reference condition code/status register
				destination = 16;
			}
			else {
				destination = Integer.parseInt(destinationString);
			}	
			//register source to leave an integer reference
			//int destination = Integer.parseInt(destinationString);
			
			if (opcode.equals("MOVE")) {
				data = new TransferInstr(Opcode.MOVE, source, destination);
				return data;
			}
			
			if (opcode.equals("ADD")) {
				data = new ArithmeticInstr(Opcode.ADD, source, destination);
				return data;
			}
			
			if (opcode.equals("SUB")) {
				data = new ArithmeticInstr(Opcode.SUB, source, destination);
				return data;
			}
			
			if (opcode.equals("DIV")) {
				data = new ArithmeticInstr(Opcode.DIV, source, destination);
				return data;
			}
			
			if (opcode.equals("MUL")) {
				data = new ArithmeticInstr(Opcode.MUL, source, destination);
				return data;
			}				
		}
		
		//These opcodes have the same instruction format; just a symbolic memory address
		else if (instructionParts.get(0).equals("BR") || instructionParts.get(0).equals("BRZ")) {
			String opcode = instructionParts.get(0);
			System.out.println(lookupTable);
			int destination;
			
			try {
				destination = lookupTable.get(instructionParts.get(1)); //Get value for symbolic memory address ref.
			}
			catch (NullPointerException npe) {
				JOptionPane.showMessageDialog(null, "Assembly program syntax error: Label reference \"" + 
						instructionParts.get(1) + "\" has not been declared.", "Assembly Program Error", JOptionPane.WARNING_MESSAGE);
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
			
			if (opcode.equals("SKZ")) {
				data = new BranchInstr(Opcode.SKZ);
			}
			else {
				data = new HaltInstr(Opcode.HALT);
			}
			return data;
		}
			
		else { //Opcode not found
			JOptionPane.showMessageDialog(null, "Assembly program syntax error: invalid opcode encountered:\n   \"" +
					instructionParts.get(0) + "\" is not recognised. \nPlease ensure all instruction opcodes are valid.", 
					"Assembly Program Error", JOptionPane.WARNING_MESSAGE);
			return null; //Prevents further parsing

		}		
		return data;
	}
	
	@Override
	public void loadToLoader() {
		loader.clear(); //has the effect of resetting the loader each time, ensuring no remnant data from the last use
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
	
	public Loader getLoader() { //Returns reference to loader, for testing
		return this.loader;
	}

	
	/*
	 * For GUI display
	 */
	@Override
	public String display() { //Displays assembly language program
		String displayString = "    <Label>  <Instruction/Operand>  <#Comment>\n\n";
		for (int i = 0; i < programString.size(); i++) {

			if (i < 10) { //Line number formatting
				displayString += "0" + i + "| " + programString.get(i) + "\n"; 
				//Add blank line between instruction/operand declarations
				if (!programString.get(i).contains("DATA") && programString.get(i + 1).contains("DATA")) {
					displayString += "\n";
				}
			}
			else {
				displayString += i + "| " + programString.get(i) + "\n";
				//Add blank line between instruction/operand declarations
				if (!programString.get(i).contains("DATA") && programString.get(i+1).contains("DATA")) { 
					displayString += "\n";
				}
			}
		}		

		return displayString;
	}


	
	

/*
 * It will be simpler from an assembly point of view to make data declarations for storing variables
 * to memory first, and then have the program code following.  This allows variables to be mapped
 * to an address first, so that they may then be referred to in program code.
 * 
 * Better than this would be the following: keep structure of assembly text file as it is, but after
 * each line has been added to the initial array list of Strings, go through the array and add any lines
 * containing DATA to another array, and everything else to yet another one.  This effectively separates
 * instructions from operand declarations. Then, the operands can be dealt with first so that their symbolic
 * references can be added to the lookupTable, so that when instructions are created, the operand references
 * can be looked up and resolved to a memory address value.
 */
	
/*
 * Assembler reads file of format below. Each line is read into a String, which is stored in an array list.
 * Therefore, the index of each string forms its line number, which also serves as a relative address (i.e. the 
 * code at line 0 is the start address, the code at line 1 is the start address + 1 etc).  If a label is present,
 * it should be stored in a HashMap as a key (so that when it is looked up, the line number value is returned)
 * along with the line number of the label as the value. So for example, in the code
 * below, when line 0 is read from the array list for parsing into an instruction or operand, a label will be detected.
 * Thus, L1 (string) will be stored in the hash map as a key, with the line number 0 being stored as the value.  When
 * L1 is later referred to in, say, a branch instruction. the hash map can be consulted so that the label can be
 * translated to a relative address (0 in this case), and this address can be placed in the instruction field instead
 * of the label.
 */
//	<Label>  <Instruction>
//	L1:      LOAD r0, var1  #Presence of a label before an instruction indicates it will likely be a branch target
//	         LOAD r1, var2
//	         ADD r2, r1
//	         STORE r1, var3
//	         BRZ L1 #Branch to L1, which is the LOAD r0, r1 instruction
//	         HALT
//
//	var1:    DATA 9
//	var2:    DATA 13
//	var3:    DATA 0 #The target location of a STORE instr. can be declared and initialised to 0
	         
	
	
	//Check code once translated; ensure that a HALT instruction is the last instruction (if one is missing
	//in the translated code, insert one by default).
	

}
