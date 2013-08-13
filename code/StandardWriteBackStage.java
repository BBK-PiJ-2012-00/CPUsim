package code;

public class StandardWriteBackStage extends WriteBackStage {

	public StandardWriteBackStage(InstructionRegister ir, RegisterFile genRegisters) {
		super(ir, genRegisters);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void receive(Operand result) {
		//this.instructionWriteBack(result);
		this.setResult(result);
	}
	

}
