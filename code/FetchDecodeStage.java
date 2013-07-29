package code;

/*
 * This may not need to be abstract; pipelined version can extend this class, implement runnable and add
 * a forward() method.
 */

public abstract class FetchDecodeStage {
	private BusController systemBus;
	
	private MemoryAddressRegister mar;
	private MemoryBufferRegister mbr;	
	
	private InstructionRegister ir;
	private ProgramCounter pc;
	//private RegisterFile genRegisters;
	
	
	public FetchDecodeStage(InstructionRegister ir, ProgramCounter pc) {
		systemBus = SystemBusController.getInstance();
		
		mar = MAR.getInstance();
		mbr = MBR.getInstance();
		
		this.ir = ir;
		this.pc = pc;
		//this.genRegisters = genRegisters;
	}
	
	
	public void instructionFetch() {
		mar.write(pc.getValue()); //Write address value in PC to MAR.
		systemBus.transferToMemory(mar.read(), null); //Transfer address from MAR to system bus, prompting read
		mar.write(0);//Reset MAR (but is it more confusing to place 0 there, as 0 is valid memory address?
		
		//A Data item should now be in MBR
		ir.loadIR((Instruction) mbr.read()); //Cast required as mbr holds type data, IR type Instruction; May need to handle exception
		
		mbr.write(null); //Clear MBR to reflect that instruction has moved to IR (should it be reset earlier, to better reflect
		//movement?)
		
		//this.instructionDecode();
	}
	//Fetch ends with instruction being loaded into IR.
	
	
	//Should this also retrieve references to operand locations? (and pass them as parameters to instructionExecute())
	public int instructionDecode() { //Returns int value of opcode
		Instruction instr = ir.read();
		int opcodeValue = instr.getOpcode().getValue(); //Gets instruction opcode as int value
		pc.incrementPC(); //Increment PC; done here so that with pipelining, the next instruction can be fetched at this point
		
		return opcodeValue;
		
		//this.instructionExecute(opcodeValue);
		//Add interim references to operand locations, to pass to execute stage?
		
	}
	
	public abstract void forward(); //For fowarding execution to the next stage (this will vary depending on mode of operation)
	
	
}