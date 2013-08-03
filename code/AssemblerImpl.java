package code;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AssemblerImpl implements Assembler {
	private List<Data> programCode; //The assembly language program to be passed to the loader (arraylist expands to fit)
	//Reference to file
	private String fileReference;
	
	
	public AssemblerImpl() {
		this.programCode = new ArrayList<Data>();
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
	            System.out.println(line); //For testing
	        }
	    } 
	    catch (FileNotFoundException ex) {
	    	ex.printStackTrace();
	    }	    
	    finally {
	    	if (s != null) {
	    		s.close();
	        }
	    }
	}
	
	
	//Check code once translated; ensure that a HALT instruction is the last instruction (if one is missing
	//in the translated code, insert one by default).
	

}
