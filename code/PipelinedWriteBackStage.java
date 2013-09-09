package code;

import java.util.concurrent.BlockingQueue;

public class PipelinedWriteBackStage extends WriteBackStage {
	
	private BlockingQueue<Instruction> executeToWriteQueue;

	public PipelinedWriteBackStage(InstructionRegister ir, RegisterFile genRegisters,
			BlockingQueue<Instruction> executeToWriteQueue) {
		
		super(ir, genRegisters);
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
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return; //Stop executing if interrupted (signals a reset or HALT).
			}
		}

	}



	@Override
	protected void fireUpdate(String update) {
		// TODO Auto-generated method stub
		
	}

}
