package code;

/*
 * Interface for general purpose registers.  General purpose registers are implemented as 
 * an array of type Data.
 */

public interface RegisterFile {
	
	public void write(int index, Data data);
	
	public Data read(int index);
	


}
