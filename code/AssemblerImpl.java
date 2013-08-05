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
	
	
	
	public AssemblerImpl() {
		this.programCode = new ArrayList<Data>();
		this.programString = new ArrayList<String>();
		this.lookupTable = new HashMap<String, Integer>();
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
	           // System.out.println(line); //For testing
	            programString.add(line);
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
	
	public List<String> getProgramString() {
		return this.programString;
	}
	
	public List<String> splitCodeLine(String line) {
		Scanner sc;
		Pattern delimiterPattern = Pattern.compile("[\\,]?[\\s]+"); //splits a String on one or more whitespaces, or a comma
		//followed by a whitespace -> this separates each line of assembly code into bits for processing into instructions.
		
		List<String> splitLine = new ArrayList<String>(); //Array to hold one line of code, split up into parts

		//for (int i = 1; i < programString.size(); i++) { //Start at 1 to avoid header line
			//lineArray = delimiterPattern.split(line);
			//String line = programString.get(i);
		if (line.contains("#")) { //If the line of code contains a comment, remove the comment part
			String[] halvedLine = line.split("[\\#]");
			line = halvedLine[0]; //Second half of the line always contains the comment, so this can be dropped
//					for(int j = 0; j < lineParts.length; j++) {
//						System.out.println(lineParts[j]);
//					}
		}
			sc = new Scanner(line);
			sc.useDelimiter(delimiterPattern);
			while (sc.hasNext()) { //Add each part of an instruction/declaration to splitLine
				splitLine.add(sc.next());
			}
			sc.close();
			
		//}
		for (String str : splitLine) {
			System.out.println(str);
		}
		return splitLine;
		
	}
	
	public void parseCode() {
		for (int i = 1; i < programString.size(); i++) { //Start at 1 to skip header line of assembly program
			List<String> lineComponents = this.splitCodeLine(programString.get(i)); //Break a line of code into parts
			
		}
		
		
		Scanner sc;
		Pattern delimiterPattern = Pattern.compile("[\\,]?[\\s]+"); //splits a String on one or more whitespaces, or a comma
		//followed by a whitespace -> this separates each line of assembly code into bits for processing into instructions.
		
		List<String> splitLine = new ArrayList<String>(); //Array to hold one line of code, split up into parts

		for (int i = 1; i < programString.size(); i++) { //Start at 1 to avoid header line
			//lineArray = delimiterPattern.split(line);
			String line = programString.get(i);
				if (line.contains("#")) { //If the line of code contains a comment, remove the comment part
					String[] halvedLine = line.split("[\\#]");
					line = halvedLine[0]; //Second half of the line always contains the comment, so this can be dropped
//					for(int j = 0; j < lineParts.length; j++) {
//						System.out.println(lineParts[j]);
//					}
				}
			sc = new Scanner(line);
			sc.useDelimiter(delimiterPattern);
			while (sc.hasNext()) { //Add each part of an instruction/declaration to splitLine
				splitLine.add(sc.next());
			}
			
		}
		for (String str : splitLine) {
			System.out.println(str);
		}
	}
	
	
	public Data createData(List<String> splitData) {
		for (int i = 0; i < splitData.size(); i++) { //Go through the list of instruction/data parts
			if (splitData.get(i).endsWith(":")) { //Indicates a label (i.e. L1: LOAD....
				
			}
		}
		
		return line;
	}
	

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
