package code;

/*
 * This may not need to be abstract; pipelined version can extend this class, implement runnable and add
 * a forward() method.
 */

public abstract class FetchDecodeStage implements Runnable {
	private BusController systemBus;
	
	private MemoryAddressRegister mar;
	private MemoryBufferRegister mbr;	
	
	private InstructionRegister ir;
	private ProgramCounter pc;
	//private RegisterFile genRegisters;
	
	private int opcodeValue; //This is fetched by control unit to pass to next stage.
	
	private UpdateListener updateListener; //Update event listener
	
	
	public FetchDecodeStage(BusController systemBus, MemoryAddressRegister mar, MemoryBufferRegister mbr,
			InstructionRegister ir, ProgramCounter pc) {
		this.systemBus = systemBus;
		
		this.mar = mar;
		this.mbr = mbr;
		
		this.ir = ir;
		this.pc = pc;
	}
	
	
	public void instructionFetch() {
		this.fireUpdate("\n** INSTRUCTION FETCH/DECODE STAGE ** \n");
		ir.clear(); //Clear previous instruction from display
		mar.write(pc.getValue()); //Write address value in PC to MAR.
		
		this.fireUpdate("Memory address from PC placed into MAR \n");
		
//		try {
//			wait();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		systemBus.transferToMemory(mar.read(), null); //Transfer address from MAR to system bus, prompting read
		this.fireUpdate("Load contents of memory address " + mar.read() + " into MBR \n");
		
//		try {
//			wait();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		

		
		//A Data item should now be in MBR
		ir.loadIR((Instruction) mbr.read()); //Cast required as mbr holds type data, IR type Instruction; May need to handle exception
		this.fireUpdate("Load contents of MBR into IR \n");
		
//		try {
//			wait();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		mar.write(-1);//Reset MAR. Repositioned here for user clarity; mem. addr. remains in MAR until instr. in IR.
		
		
		mbr.write(null); //Clear MBR to reflect that instruction has moved to IR (should it be reset earlier, to better reflect
		//movement?)
		
	}
	//Fetch ends with instruction being loaded into IR.
	
	
	public int instructionDecode() { //Returns int value of opcode
		Instruction instr = ir.read();
		int opcodeValue = instr.getOpcode().getValue(); //Gets instruction opcode as int value
		pc.incrementPC(); //Increment PC; done here so that with pipelining, the next instruction can be fetched at this point
		this.fireUpdate("PC incremented by 1 (ready for next instruction fetch) \n");
		
//		try {
//			wait();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
		
		
		//pc.display();
		return opcodeValue;
		
		//this.instructionExecute(opcodeValue);
		//Add interim references to operand locations, to pass to execute stage?
		
	}
	
	public synchronized void run() { //Synchronized to enable step execution
		this.instructionFetch();
		opcodeValue = this.instructionDecode();
	}
	
	public abstract void forward(); //For forwarding execution to the next stage (for pipelining)
	
	public int getOpcodeValue() {
		return this.opcodeValue;
	}
	
	private void fireUpdate(String update) {
		ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(this, update);
		updateListener.handleUpDateEvent(updateEvent);		
	}
	
	public void registerListener(UpdateListener listener) {
		this.updateListener = listener;
	}
	
}
