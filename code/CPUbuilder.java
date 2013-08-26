package code;

/*
 * CPU components can be instantiated ONLY via this class; could keep a static counter to ensure
 * no accidental duplicates!
 */
public class CPUbuilder {
	private MainMemory memory;
	
	private BusController systemBusController; //Still responsible for creating bus lines; these aren't used anywhere else
	private ControlLine controlLine;
	
	private MemoryBufferRegister mbr;
	private MemoryAddressRegister mar;
	
	private ControlUnit controlUnit;
	
	private Loader loader; //Needs to be here as this holds a memory reference; important to pass around the ONE reference!
	
	public CPUbuilder(boolean pipelined) {
		createComponents(pipelined);
	}
	
	public void createComponents(boolean pipelined) {
		memory = MemoryModule.getInstance(); //To be replaced by new	
		//Memory needs a reference to the BusController
		
		//systemBusController = SystemBusController.getInstance();
		
		mbr = MBR.getInstance();
		mar = MAR.getInstance();
		
		controlLine = new ControlLineImpl(mbr);
		controlLine.registerMemoryModule(memory);
		systemBusController = new SystemBusController(controlLine);
		
		if (pipelined) {
			controlUnit = new ControlUnitImpl(pipelined, mbr);
		}
		else {
			controlUnit = new ControlUnitImpl(false, mbr);
		}	
		
		loader = new LoaderImpl(memory);

		
	}
	
	public void buildCPU() {
		
	}
	
	public MainMemory getMemoryModule() {
		return this.memory;
	}
	
	public BusController getBusController() {
		return this.systemBusController;
	}
	
	public Loader getLoader() {
		return this.loader;
	}

}
