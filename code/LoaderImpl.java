package code;

import java.util.ArrayList;
import java.util.List;

public class LoaderImpl implements Loader {
	private MainMemory memory = MemoryModule.getInstance();
	private List<Data> codeBuffer = new ArrayList<Data>(); //To buffer code coming in from memory (amount not known in advance)
	//ArrayList grows as required, making it more suitable than an array
	private Data[] programCode; //An array to represent the program to be loaded into memory
	
	public void load(Data dataItem) { //For loading code from assmebler to loader
		codeBuffer.add(dataItem);
	}
	
	public void loadToMemory(Data[] programCode) {
		this.programCode = programCode;
		memory.loadMemory(programCode);
	}
	
	

}
