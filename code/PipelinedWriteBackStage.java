package code;

import java.util.concurrent.BlockingQueue;

import javax.swing.SwingUtilities;

public class PipelinedWriteBackStage extends WriteBackStage {
	
	private BlockingQueue<Instruction> executeToWriteQueue; //This queue referenced by execute stage, too


	public PipelinedWriteBackStage(BusController systemBus, InstructionRegister ir, ProgramCounter pc, RegisterFile genRegisters,
			Register statusRegister, MemoryBufferRegister mbr, MemoryAddressRegister mar,
			BlockingQueue<Instruction> executeToWriteQueue) {
		
		super(systemBus, ir, pc, genRegisters, statusRegister, mbr, mar);
		this.executeToWriteQueue = executeToWriteQueue;
	}

	
	
	@Override
	public void receive(Operand result) { //Called by execute stage to supply operand for write back
		setResult(result); //Set result field of WriteBackStage super class

	}
	
	@Override
	public void instructionWriteBack(Operand result) { 
		//It is implicit in the nature of arithmetic instructions that the result is stored in the register
		//referenced in the first field of the instruction after the opcode (field1)
		getGenRegisters().write(getIR().read(2).getField1(), result);
		
		fireUpdate("> Result operand " + result + " written to r" + getIR().read(2).getField1() + " from ALU\n");
		
		setWaitStatus(true);
		try {
			wait();
		}
		catch (InterruptedException e) { //If reset is clicked on GUI, swing worker thread in ex. stage will trigger interrupt
			//e.printStackTrace();	
			setWaitStatus(false);
			Thread.currentThread().interrupt();  //Interrupts itself, prompting eventual termination
			return; 
		}
		setWaitStatus(false);
	}
	

	/*
	 * The thread running in this stage, like the thread running in the F/D Stage, can
	 * be interrupted at any point by the SwingWorker thread running in the Ex. Stage.
	 * An interrupt causes this thread to cease executing meaningful code and to exit
	 * as quickly as possible, effectively terminating itself.
	 */
	@Override
	public synchronized void run() {
		setActive(true);
		while (isActive()) {
			try {
				fireUpdate("> Waiting to receive operand from Ex. Stage.\n");
				Instruction instr = executeToWriteQueue.take();
				fireUpdate("> Received operand " + getResult() + " from Ex. Stage.\n");
				
				
				if (Thread.currentThread().isInterrupted()) { //Polling is for use without wait() statements (for testing)
					setActive(false);
					return;
				}
				
				getIR().loadIR(2, instr); //Load instruction into last IR index
				
				if (Thread.currentThread().isInterrupted()) { //Polling is for use without wait() statements (for testing)
					setActive(false);
					return;
				}
				
				instructionWriteBack(getResult()); //Get result from result field in WriteBackStage
				if (Thread.currentThread().isInterrupted()) { 
					setActive(false); //Signal to ex. stage that interrupt has been issued (Ex. checks status of WB's active).
					return;
				}
				getIR().clear(2); //Reset IR once write back operation complete.
			} catch (InterruptedException e) {
				setActive(false);
				//e.printStackTrace();
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
