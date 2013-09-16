package code;

import javax.swing.SwingUtilities;

public class StandardFetchDecodeStage extends FetchDecodeStage {
	
	
	public StandardFetchDecodeStage(BusController systemBus, InstructionRegister ir, ProgramCounter pc,
			RegisterFile genRegisters, Register rCC, MemoryBufferRegister mbr, MemoryAddressRegister mar) {
		
		super(systemBus, ir, pc, genRegisters, rCC, mbr, mar);
	}	
	
	
	@Override
	public boolean instructionFetch() {
		fireUpdate("\n** INSTRUCTION FETCH/DECODE STAGE ** \n");	
		boolean successful = accessMemory(true, false, false, false);//Final false to reflect standard mode
		return successful;	//Returns false if interrupt occurs, true otherwise	
	}
	//Fetch ends with instruction being loaded into IR.
	
	
	public int instructionDecode() { //Returns int value of opcode
		Instruction instr = getIR().read();
		int opcodeValue = instr.getOpcode().getValue(); //Gets instruction opcode as int value
		getPC().incrementPC(); //Increment PC; done here so that with pipelining, the next instruction can be fetched at this point
		this.fireUpdate("> PC incremented by 1 (ready for next instruction fetch) \n");
		
		setWaitStatus(true);
		try {
			wait();
		} catch (InterruptedException e) {
			//e.printStackTrace();
			setWaitStatus(false);
			return -1; //Do not continue execution if interrupted (SwingWorker.cancel(true) is called). -1 signals to control
			//unit to stop execution.
		}
		setWaitStatus(false);		
		
		return opcodeValue;
		
	}
	
	@Override
	public synchronized void run() { //Synchronized to enable step execution
		if (this.instructionFetch() == false) { //Returns false if interrupted during fetch
			setOpcodeValue(-1); 
			return; //Cancel execution if interrupted during fetch
		}
		setOpcodeValue(this.instructionDecode());
	}
	
	@Override
	public void fireUpdate(final String update) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			    ModuleUpdateEvent updateEvent = new ModuleUpdateEvent(StandardFetchDecodeStage.this, update);
				StandardFetchDecodeStage.this.getUpdateListener().handleUpDateEvent(updateEvent);	
			}
		});
	}

}
