package code;


/*
 * A class to represent the Memory Buffer Register of the CPU. This register interacts with the control line(s) of 
 * the system bus to both send and receive data to and from main memory.
 */
public interface MemoryBufferRegister {
	
	/*
	 * A method to write a value of type Data to the MBR. 
	 * 
	 * @return boolean a successful write returns true. An unsuccessful write is determined by
	 * register contents being equal to null, in which case false is returned.
	 * @param Data data the data to be written to the register.
	 */
	public boolean write(Data data); 
	
	
	/*
	 * A method that reads (and returns) the contents of the MBR.
	 * 
	 * @return Data the data contents of the MBR.
	 */
	public Data read();

}
