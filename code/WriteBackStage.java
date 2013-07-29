package code;

public abstract class WriteBackStage {
	private InstructionRegister ir;
	private RegisterFile genRegisters;
	
	
	public WriteBackStage(InstructionRegister ir, RegisterFile genRegisters) {
		this.ir = ir;
		this.genRegisters = genRegisters;
	}
		
	
	//Writing of results to register file
	public void instructionWriteBack(Operand result) { //Not required in every cycle
		//It is implicit in the nature of arithmetic instructions that the result is stored in the register
		//referenced in the first field of the instruction after the opcode (field1)
		genRegisters.write(ir.read().getField1(), result);
	}
	
	public abstract void receive(Operand result); //To receive operand reference from previous stage (pipelined and standard modes
										//will implement this differently)
		

}
