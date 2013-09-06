package code;

public class StandardExecuteStage extends ExecuteStage {

	public StandardExecuteStage(BusController systemBus, InstructionRegister ir, ProgramCounter pc,	RegisterFile genRegisters,
			Register statusRegister, WriteBackStage writeBackStage, MemoryBufferRegister mbr, MemoryAddressRegister mar) {
		super(systemBus, ir, pc, genRegisters, statusRegister, writeBackStage, mbr, mar);
	}
	
//	@Override
//	public void receive(int opcode) {
//		this.instructionExecute(opcode);
//	}

	@Override
	public boolean forward(Operand result) {
		this.getWriteBackStage().receive(result);
		this.getWriteBackStage().run();
		return true;
	}

}
