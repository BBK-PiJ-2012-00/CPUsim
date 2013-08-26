package code;

public class StandardFetchDecodeStage extends FetchDecodeStage {

	public StandardFetchDecodeStage(BusController systemBus, MemoryAddressRegister mar, MemoryBufferRegister mbr,
			InstructionRegister ir, ProgramCounter pc) {
		super(systemBus, mar, mbr, ir, pc);
	}

	@Override
	public void forward() {
		//Probably not required for standard mode of operation
	}

}
