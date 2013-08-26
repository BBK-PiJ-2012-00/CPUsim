package code;

import java.util.ArrayList;
import java.util.List;

/*
 * Initially, the Loader simply loads program code into memory and doesn't perform any address resolution,
 * as addressing is initially absolute and not symbolic.
 */
public class LoaderImpl implements Loader {
	private MainMemory memory = MemoryModule.getInstance();
	
	private Data[] programCode; //An array to represent the program to be loaded into memory
	
	public void load(Data[] assembledCode) { //For loading code from assmebler to loader
		this.programCode = assembledCode;
	}
	
	public void loadToMemory() {
		for (Data d : programCode) {
			System.out.println(d.toString());
		}
		//this.programCode = codeBuffer.toArray(new Data[codeBuffer.size()]); //Convert codeBuffer to standard array
		memory.resetPointer(); //Set memory pointer back to 0
		memory.loadMemory(programCode);		
	}
	
	public Data[] getProgramCode() {
		return this.programCode;
	}
	
	public void clear() {
		this.programCode = null;
	}
	

}
