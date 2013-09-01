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
	
	private ControlUnit controlUnit;
	
	private Loader loader; //Needs to be here as this holds a memory reference; important to pass around the ONE reference!
	
	public CPUbuilder(boolean pipelined) {
		createComponents(pipelined);
	}
	
	public void createComponents(boolean pipelined) {
		
		memory = new MemoryModule(); 
		
		mbr = new MBR();
		
		controlLine = new ControlLineImpl(mbr);
		controlLine.registerMemoryModule(memory);
		systemBusController = new SystemBusController(controlLine);
		
		memory.registerBusController(systemBusController);
		
		if (pipelined) {
			controlUnit = new ControlUnitImpl(pipelined, mbr, systemBusController);
		}
		else {
			controlUnit = new ControlUnitImpl(false, mbr, systemBusController);
		}	
		
		loader = new LoaderImpl(memory);

		
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
	
	public ControlUnit getControlUnit() {
		return this.controlUnit;
	}

}
