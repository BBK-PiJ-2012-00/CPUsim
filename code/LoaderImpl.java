package code;

/*
 * The Loader simply loads program code into memory.
 */
public class LoaderImpl implements Loader {
	private MainMemory memory; //Reference to main memory	
	private Data[] programCode; //An array to represent the program to be loaded into memory
	
	
	public LoaderImpl(MainMemory memory) { //Inject a memory reference (done in CPUbuilder).
		this.memory = memory;
	}
	
	
	@Override
	public void load(Data[] assembledCode) { //For loading code from assembler to loader
		this.programCode = assembledCode;
	}
	
	
	@Override
	public void loadToMemory() {
		memory.resetPointer(); //Set memory pointer back to 0
		memory.loadMemory(programCode);	
	}
	
	
	@Override
	public Data[] getProgramCode() {
		return this.programCode;
	}
	
	
	@Override
	public void clear() {
		this.programCode = null;
	}
	

}
