package code;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class AssemblerImpl implements Assembler {
	private List<Data> programCode; //The assembly language program to be passed to the loader (arraylist expands to fit)
	private List<String> programString; //Holds program code as string array list, for parsing into data array
	//Reference to file
	private String fileReference;
	private Map<String, Integer> lookupTable; //For associating labels with relative addresses
	private Loader loader;
	
	private List<String> instructionArray; //For intermediate stage where programString is split into two, instructions being stored
	private List<String> operandArray; //in instructionArray, operands being stored in operandArray (as Strings)
	private int operandAddressPointer;
	
	
	public AssemblerImpl() {
		this.programCode = new ArrayList<Data>();
		this.programString = new ArrayList<String>();
		this.lookupTable = new HashMap<String, Integer>();
		this.loader = new LoaderImpl();
	}
	
	
	public void selectFile(String fileName) { //For selecting assembly program file
		this.fileReference = fileName;
	}
	
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
	    	//Display pop-up error
	    }	    
	    finally {
	    	if (s != null) {
	    		s.close();
	        }
	    }
	}
	
	
	/*
	 * This method splits the assembly program stored as an array of Strings in programString into
	 * two distinct array lists (also of type String). The idea is that operand declarations are stored
	 * separately from instructions, so that they may have their symbolic memory references mapped to 
	 * real memory addresses before instructions are assembled. This means that when instructions come to be
	 * interpreted and assembled, the symbolic references to operands can be resolved using the lookupTable, to
	 * which symbolic operand references are added and mapped to real memory addresses.
	 */
	public void separateOperands() {
		operandArray = new ArrayList<String>();
		instructionArray = new ArrayList<String>();
		for (int i = 1; i < programString.size(); i++) { //Start at 1 to miss out header line
			if (programString.get(i).contains("DATA")) { //Only operand declarations contain this String sequence
				operandArray.add(programString.get(i));
			}
			else {
				instructionArray.add(programString.get(i));
			}
		}
		operandAddressPointer = instructionArray.size(); //Set operand address pointer to a location that will come immediately
														//after the last instruction (deduced by size of instructionArray).
	}
	
	/*
	 * This method assembles the operandArray into proper Data (Operand) types, and stores them
	 * in the program code array. They are stored at an index that exceeds what will be the last
	 * address of the instruction code so that operands appear in memory at the end of the program.
	 */
	public Data assembleOperand(List<String> operandParts) {
		//All operand part arrays will contain 3 parts: symbol, DATA declaration, and operand value
		String symbol = operandParts.get(0).substring(0, operandParts.get(0).length() -1); //Trim semicolon from symbol
		lookupTable.put(symbol, operandAddressPointer); //Map symbol to address		
		
		String operandString = operandParts.get(2); //Operand value as String
		int operandValue = Integer.parseInt(operandString);
		Data operand = new OperandImpl(operandValue);
		
		return operand;		
	}
	
	
	public List<String> getProgramString() {
		return this.programString;
	}
	
	/*
	 * Method takes a line of code and splits it into an array list of Strings, which can then be passed
	 * to another method for interpretation (i.e. so that instructions or operands can be created).
	 */
	public List<String> splitCodeLine(String line) {
		Scanner sc;
		Pattern delimiterPattern = Pattern.compile("[\\,]?[\\s]+"); //splits a String on one or more whitespaces, or a comma
		//followed by a whitespace -> this separates each line of assembly code into bits for processing into instructions.
		
		List<String> splitLine = new ArrayList<String>(); //Array to hold one line of code, split up into parts

		if (line.contains("#")) { //If the line of code contains a comment, remove the comment part
			String[] halvedLine = line.split("[\\#]");
			line = halvedLine[0]; 
		}
		
		sc = new Scanner(line);
		sc.useDelimiter(delimiterPattern);
		while (sc.hasNext()) { //Add each part of an instruction/declaration to splitLine
			splitLine.add(sc.next());
		}
		sc.close();			
	
		for (String str : splitLine) {
			System.out.println(str);
		}
		return splitLine;		
	}
	
	public void assembleCode() {
		//Operands assembled first so their symbolic references can be mapped to actual addresses
		for (int i = 0; i < operandArray.size(); i++) {
			List<String> lineComponents = this.splitCodeLine(operandArray.get(i)); //Break line of code into parts
			Data operand = this.assembleOperand(lineComponents);
			programCode.set(operandAddressPointer, operand); //Add the operand to the data array, at specified address
			operandAddressPointer++; //Increment so that the next operand will be stored in the next consecutive address
		}
		
//		for (int i = 1; i < programString.size(); i++) { //Start at 1 to skip header line of assembly program
//			List<String> lineComponents = this.splitCodeLine(programString.get(i)); //Break a line of code into parts
//			Data machineCodeLine = this.createData(lineComponents, i-1); //Create an instruction/operand from the line components
//			//i-1 gives the line number of the line of code, useful for mapping labels
//			programCode.add(machineCodeLine); //Add the instruction/operand to an array list, to be later passed into memory
//		}
		
	}
	
	@Override
	public Data assembleInstruction(List<String> instructionParts, int lineNum) {
		Data data = null;
		for (int i = 0; i < instructionParts.size(); i++) { //Go through the list of instruction/data parts
			System.out.println("Entered for-loop: i = " + i);
			if (instructionParts.get(i).endsWith(":")) { //Indicates a label (i.e. L1: LOAD....
				String label = instructionParts.get(i).substring(0, instructionParts.get(i).length() - 2); //Trim the colon off the end
				lookupTable.put(label, lineNum); //Map the label to the line number of the code
			}
			
			if (instructionParts.get(i).equals("LOAD")) { //This means a memory source and register destination are specified
				String sourceString = instructionParts.get(i+1).substring(1); //Trim leading 'r' off
				//register source to leave an integer reference
				int source = Integer.parseInt(sourceString);
				
				String destinationString = instructionParts.get(i+2).substring(1, instructionParts.get(i+2).length() - 1);//Trim brackets off
				//memory address reference, leaving an integer
				int destination = Integer.parseInt(destinationString);
				
				data = new TransferInstr(Opcode.LOAD, source, destination);
				return data;
			}
			
			else if (instructionParts.get(i) == "STORE") { //This means a register source and memory destination are specified
				String sourceString = instructionParts.get(i+1).substring(1, (instructionParts.get(i+1).length() - 2));//Trim brackets off
				//memory address reference, leaving an integer
				int source = Integer.parseInt(sourceString);
				
				String destinationString = instructionParts.get(i+1).substring(1, (instructionParts.get(i+1).length() - 1)); //Trim leading 'r' off
				//register destination to leave an integer reference
				int destination = Integer.parseInt(destinationString);
				
				
				data = new TransferInstr(Opcode.LOAD, source, destination);
				return data;
			}
		}
		
		return data;
	}
	
	public void loadToLoader(Data[] programCode) {
		
	}
	
	public List<String> getOperandArray() {
		return this.operandArray;
	}
	
	public List<String> getInstructionArray() {
		return this.instructionArray;
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
