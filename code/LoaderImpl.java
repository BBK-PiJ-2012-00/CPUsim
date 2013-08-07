package code;

import java.util.ArrayList;
import java.util.List;

/*
 * Initially, the Loader simply loads program code into memory and doesn't perform any address resolution,
 * as addressing is initially absolute and not symbolic.
 */
public class LoaderImpl implements Loader {
	private MainMemory memory = MemoryModule.getInstance();
	//private List<Data> codeBuffer = new ArrayList<Data>(); //To buffer code coming in from memory (amount not known in advance)
	//ArrayList grows as required, making it more suitable than an array
	private Data[] programCode; //An array to represent the program to be loaded into memory
	
	public void load(Data[] assembledCode) { //For loading code from assmebler to loader
		this.programCode = assembledCode;
	}
	
	public void loadToMemory() {
		for (Data d : programCode) {
			System.out.println(d.toString());
		}
		//this.programCode = codeBuffer.toArray(new Data[codeBuffer.size()]); //Convert codeBuffer to standard array
		memory.loadMemory(programCode);		
	}
	
	public Data[] getProgramCode() {
		return this.programCode;
	}
	

}
