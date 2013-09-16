package code;

public class StandardWriteBackStage extends WriteBackStage {

	public StandardWriteBackStage(BusController systemBus, InstructionRegister ir, ProgramCounter pc, RegisterFile genRegisters,
			Register rCC, MemoryBufferRegister mbr, MemoryAddressRegister mar) {
		
		super(systemBus, ir, pc, genRegisters, rCC, mbr, mar);
	}

	@Override
	public void receive(Operand result) {
		setResult(result); //Set result field in WriteBackStage superclass
	}
	
	@Override
	public void instructionWriteBack(Operand result) { //Not required in every cycle
		//It is implicit in the nature of arithmetic instructions that the result is stored in the register
		//referenced in the first field of the instruction after the opcode (field1)
		getGenRegisters().write(getIR().read().getField1(), result);
	}
	
	
	@Override
	public synchronized void run() { //Calls instructionWriteBack method, using the result field of superclass 
		instructionWriteBack(getResult());
	}

	
	

}
