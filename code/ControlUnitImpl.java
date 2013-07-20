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
	
	public void execute() {
		InstructionCycleStage fetchStage = new FetchDecodeStage();

	}
	
	public class FetchDecodeStage implements InstructionCycleStage {
		
		public void run() {
			this.instructionFetch();			
		}
		
		
		private void instructionFetch() {
			mar.write(pc.getValue()); //Write address value in PC to MAR.
			systemBus.transferToMemory(mar.read(), null); //Transfer address from MAR to system bus, prompting read
			//A Data item should now be in MBR
			ir.loadIR(mbr.read()); //what about data loaded into
			//MBR that is data (operand) as opposed to instruction? Is this loaded straight to a register?
			//http://comminfo.rutgers.edu/~muresan/201_JavaProg/11CPU/Lecture11.pdf
			//have methods to represent storeExecuteCycle, loadExecuteCycle etc, depending on decode of fetched instruction
			//This is instruction fetch -> only covers fetching of an instruction, not its execution; fetching data from memory
			//will be part of executing a LOAD instruction, which occurs in execute. Decode determines nature of instruction; 
			//instructionDecode() method.
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
