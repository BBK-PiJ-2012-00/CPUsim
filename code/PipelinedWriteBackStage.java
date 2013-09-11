package code;

import java.util.concurrent.BlockingQueue;

import javax.swing.SwingUtilities;

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
		
		fireUpdate("Result operand " + result + " written to r" + getIR().read(2).getField1() + " from ALU\n");
		
		setWaitStatus(true);
		try {
			wait();
		}
		catch (InterruptedException e) { //If reset is clicked on GUI, swing worker thread in ex. stage will trigger interrupt
			e.printStackTrace();		//and stop this thread from running
			setWaitStatus(false);
			Thread.currentThread().interrupt(); 
			return; 
		}
		setWaitStatus(false);
	}

	@Override
	public synchronized void run() {
		setActive(true);
		while (isActive()) {
			try {
				fireUpdate("> Waiting to receive operand from Ex. Stage.\n");
				System.out.println("Attempting take...");
				Instruction instr = executeToWriteQueue.take();
				fireUpdate("> Received operand " + getResult() + " from Ex. Stage.\n");
				
				
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
				if (Thread.currentThread().isInterrupted()) { 
					setActive(false); //Signal to ex. stage that interrupt has been issued (Ex. checks status of WB's active).
					return;
				}
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
	
	@Override
	public void fireUpdate(final String update) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(PipelinedWriteBackStage.this, update);
				PipelinedWriteBackStage.this.getUpdateListener().handleUpDateEvent(updateEvent);	
			}
		});
	}




	
	

}
