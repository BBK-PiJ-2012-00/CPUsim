package code;

import java.util.concurrent.BlockingQueue;

public class PipelinedFetchDecodeStage extends FetchDecodeStage {
	private BlockingQueue<Integer> fetchToExecuteQueue;

	public PipelinedFetchDecodeStage(BusController systemBus,
			MemoryAddressRegister mar, MemoryBufferRegister mbr,
			InstructionRegister ir, ProgramCounter pc, BlockingQueue<Integer> fetchToExecuteQueue) {
		
		super(systemBus, mar, mbr, ir, pc);
		this.fetchToExecuteQueue = fetchToExecuteQueue;

	}

	@Override
	public boolean forward() {
		Integer opcode = this.getOpcodeValue(); //Get opcode value from superclass to pass to queue
		try {
			fetchToExecuteQueue.put(opcode);
			return true;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}

}
