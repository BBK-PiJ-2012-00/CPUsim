package code;

public class StandardExecuteStage extends ExecuteStage {

	public StandardExecuteStage(InstructionRegister ir, ProgramCounter pc,
			RegisterFile genRegisters, Register statusRegister,
			WriteBackStage writeBackStage) {
		super(ir, pc, genRegisters, statusRegister, writeBackStage);
		// TODO Auto-generated constructor stub
	}
	
//	@Override
//	public void receive(int opcode) {
//		this.instructionExecute(opcode);
//	}

	@Override
	public void forward(Operand result) {
		this.getWriteBackStage().receive(result);
	}

}
