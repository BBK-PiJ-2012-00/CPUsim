package code;

import java.util.concurrent.BlockingQueue;

public class PipelinedWriteBackStage extends WriteBackStage {
	
	private BlockingQueue<Instruction> executeToWriteQueue;

	public PipelinedWriteBackStage(BusController systemBus, InstructionRegister ir, ProgramCounter pc, RegisterFile genRegisters,
			Register statusRegister, MemoryBufferRegister mbr, MemoryAddressRegister mar,
			BlockingQueue<Instruction> executeToWriteQueue) {
		
		super(systemBus, ir, pc, genRegisters, statusRegister, mbr, mar);
		this.executeToWriteQueue = executeToWriteQueue;
	}

	
	
	@Override
	public void receive(Operand result) { //Called by execute stage to supply operand for writeback
		setResult(result); //Set result field of WriteBackStage super class

	}
	
	@Override
	public void instructionWriteBack(Operand result) { 
		//It is implicit in the nature of arithmetic instructions that the result is stored in the register
		//referenced in the first field of the instruction after the opcode (field1)
		getGenRegisters().write(getIR().read(2).getField1(), result);
	}

	@Override
	public void run() {
		while (isActive()) {
			try {
				Instruction instr = executeToWriteQueue.take();
				getIR().loadIR(2, instr); //Load instruction into last IR index
				instructionWriteBack(getResult()); //Get result from result field in WriteBackStage
				getIR().clear(2); //Reset IR once write back operation complete.
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return; //Stop executing if interrupted (signals a reset or HALT).
			}
		}
		return;

	}
	
	//What about making active boolean static? This would mean all stages are accessing the same one.



	@Override
	protected void fireUpdate(String update) {
		// TODO Auto-generated method stub
		
	}

}
