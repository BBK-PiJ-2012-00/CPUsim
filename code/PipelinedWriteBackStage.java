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
		
		fireUpdate("\n** WRITE BACK STAGE **\n");//Simpler to place this here than within writeBackStage object
		fireUpdate("Result operand " + result + " written to r" + getIR().read(2).getField1() + " from ALU\n");
	}

	@Override
	public void run() {
		setActive(true);
		while (isActive()) {
			try {
				System.out.println("Attempting take...");
				Instruction instr = executeToWriteQueue.take();
				System.out.println("Taken instruction: " + instr.toString());
				
				if (Thread.currentThread().isInterrupted()) {
					setActive(false);
					return;
				}
				
				getIR().loadIR(2, instr); //Load instruction into last IR index
				System.out.println("IR 2: " + getIR().read(2).toString());
				
				if (Thread.currentThread().isInterrupted()) {
					setActive(false);
					return;
				}
				
				instructionWriteBack(getResult()); //Get result from result field in WriteBackStage
				getIR().clear(2); //Reset IR once write back operation complete.
				System.out.println("Cleared.");
			} catch (InterruptedException e) {
				setActive(false);
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
