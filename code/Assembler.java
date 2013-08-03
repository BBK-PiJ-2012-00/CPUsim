package code;

/*
 * Reads a text file containing assembly language. Can initially read into an array of Strings, from which an array of Instructions is
 * then created.
 */

public interface Assembler {
	
	public void selectFile(String fileName);
	
	public void readAssemblyFile();
}
