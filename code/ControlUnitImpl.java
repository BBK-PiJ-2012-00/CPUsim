package code;

public class ControlUnitImpl implements ControlUnit {
	private BusController systemBus = SystemBusController.getInstance();
	
	private ProgramCounter pc;
	
	private IR ir;
	
	private MemoryBufferRegister mbr;
	private MemoryAddressRegister mar;
	
	private RegisterFile genRegisters;
	
	public ControlUnitImpl() {
		systemBus = SystemBusController.getInstance();
		mbr = MBR.getInstance();
		mar = MAR.getInstance();
		genRegisters = new RegisterFile16();
		pc = new PC();
		ir = new IR();
	}
	
	public void go() {
		InstructionCycleStage fetchStage = new FetchDecodeStage();

	}
	
	public class FetchDecodeStage implements InstructionCycleStage {
		
		public void run() {
			this.instructionFetch();			
		}
		
		
		private void instructionFetch() {
			mar.write(pc.getValue());
		}
		
	}
	
	//Stages represented by methods --> these methods should be embedded within objects of type Stage
	//This allows for pipelining to be more easily implemented later.
	
	public void instructionFetch() {
		//Code for instruction fetch stage
	}
	
	public void instructionDecode() {
		
	}
	
	public void instructionExecute() {
		//Can call other, private methods depending on instruction opcode
		//case switch statement: if arithmetic, call arithmeticInstrExecute(), etc
	}
	
	public void instructionStore() { //Not required in every cycle
		
	}
	


}
