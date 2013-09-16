package code;

/*
 * Imitates the function of a real-life loader module; "machine language" code is produced by the assembler, which the loader then
 * writes into main memory.
 * 
 * The assembler parses lines of a text file containing assembly code into an array of Instructions for the loader to deal with.
 */

public interface Loader {
	
	/*
	 * Called by the assembler when it loads an assembled assembly language
	 * program to the loader.
	 * 
	 * @param Data[] an array containing the assembled instructions and operands.
	 */
	public void load(Data[] assembledCode);
	
	
	/*
	 * Loads the assembled program into main memory.
	 */
	public void loadToMemory();
	
	
	/*
	 * A getter method for the program code array.
	 * 
	 * @return Data[] the assembled program code stored in an array.
	 */
	public Data[] getProgramCode();
	
	
	/*
	 * Used for clearing the array of program code prior to passing another
	 * assembly program to the loader.
	 */
	public void clear();
	

}
