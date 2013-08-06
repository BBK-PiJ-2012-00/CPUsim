package code;

/*
 * Imitates the function of a real-life loader module; machine language code is produced by the assembler, which the loader then
 * writes into main memory.
 * 
 * The assembler parses lines of a text file containing assembly code into an array of Instructions for the loader to deal with.
 */

public interface Loader {
	
	public void load(Data[] assembledCode);
	
	public void loadToMemory();

}
