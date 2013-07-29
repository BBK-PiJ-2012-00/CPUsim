package code;

public class StandardFetchDecodeStage extends FetchDecodeStage {

	public StandardFetchDecodeStage(InstructionRegister ir, ProgramCounter pc,
			RegisterFile genRegisters) {
		super(ir, pc, genRegisters);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void forward() {
		//Probably not required for standard mode of operation
	}

}
