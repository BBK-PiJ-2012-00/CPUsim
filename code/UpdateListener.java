package code;

import java.util.EventListener;


/*
 * An event listener class to handle GUI update events.
 */
public class UpdateListener implements EventListener {
	private CPUframe frame; //Reference to main window frame object
	
	public UpdateListener(CPUframe frame) {
		this.frame = frame;
	}
		
	/*
	 * The event handling code; sets fields on the CPUframe window in response to
	 * ModuleUpdateEvents being created by simulator components.
	 */
	public void handleUpDateEvent(ModuleUpdateEvent e) {
		
		if (e.getSource() instanceof ProgramCounter) {
			frame.getPCfield().setText(e.getUpdate());
		}
		
		else if (e.getSource() instanceof InstructionRegister) {
			frame.getIRfield(e.getRegisterReference()).setText(e.getUpdate());
		}
		
		else if (e.getSource() instanceof MainMemory) {
			frame.getMemoryField().setText(e.getUpdate());
			frame.getMemoryField().setCaretPosition(0);
		}
		
		else if (e.getSource() instanceof MemoryAddressRegister) {
			frame.getMARfield().setText(e.getUpdate());
		}
		
		else if (e.getSource() instanceof MemoryBufferRegister) {
			frame.getMBRfield().setText(e.getUpdate());
		}
		
		else if (e.getSource() instanceof RegisterFile) {
			frame.getGenPurposeRegister(e.getRegisterReference()).setText(e.getUpdate());
		}
		
		else if (e.getSource() instanceof ConditionCodeRegister) {
			frame.getConditionCodeField().setText(e.getUpdate());
		}
		
		else if (e.getSource() instanceof AddressBus) {
			frame.getAddressBusField().setText(e.getUpdate());
		}
		
		else if (e.getSource() instanceof DataBus) {
			frame.getDataBusField().setText(e.getUpdate());
		}
		
		else if (e.getSource() instanceof ControlLine) {
			if (e.isControlLineUpdate()) { //Boolean to differentiate between control line field update and activity monitor update
				frame.getControlLineField().setText(e.getUpdate());
			}
			else if (!e.pipeliningEnabled()) { //Standard execution, only one activity monitor to update
				frame.getActivityMonitor().append(e.getUpdate()); //Append new update to existing text
				frame.getActivityMonitor().setCaretPosition(frame.getActivityMonitor().getDocument().getLength());
			}
			else if (e.pipeliningEnabled()) {//Pipelined execution, 2 activity monitors to choose from (WB stage never uses bus).
				if (e.getActivityMonitorReference() == 0) { //F/D stage activity monitor
					frame.getActivityMonitor().append(e.getUpdate()); 
					frame.getActivityMonitor().setCaretPosition(frame.getActivityMonitor().getDocument().getLength());					
				}
				else if (e.getActivityMonitorReference() == 1) { //Ex. stage activity monitor
					frame.getActivityMonitor1().append(e.getUpdate());
					frame.getActivityMonitor1().setCaretPosition(frame.getActivityMonitor1().getDocument().getLength());
				}
			}
		}
		
		//Handles updates for pipelined and standard f/d stages, and standard execute and write back stages.
		else if ((e.getSource() instanceof FetchDecodeStage) || (e.getSource() instanceof StandardExecuteStage) ||
				(e.getSource() instanceof StandardWriteBackStage)) {
			frame.getActivityMonitor().append(e.getUpdate()); //Append new update to existing text
			frame.getActivityMonitor().setCaretPosition(frame.getActivityMonitor().getDocument().getLength());
		}
		
		else if (e.getSource() instanceof PipelinedExecuteStage) { //Write to execute stage activity monitor (pipelined)
			frame.getActivityMonitor1().append(e.getUpdate()); //Append new update to existing text
			frame.getActivityMonitor1().setCaretPosition(frame.getActivityMonitor1().getDocument().getLength());
		}
		
		else if (e.getSource() instanceof PipelinedWriteBackStage) { //Write to write back activity monitor (pipelined)
			frame.getActivityMonitor2().append(e.getUpdate());
			frame.getActivityMonitor2().setCaretPosition(frame.getActivityMonitor2().getDocument().getLength());
		}
		
		/*
		 * As a dummy object is used for ALU updates (ALU uses static methods and cannot be instantiated) so this if 
		 * statement must come last to avoid use by any other instances.
		 */
		else if (e.getSource() instanceof Object) { //ALU (static, therefore no object instantiation; dummy object used)
			
			if (e.getAluUnit().equals("")) { //indicates reset operation
				for (int i = 0; i < 12; i ++) {
					frame.getAllAluFields()[i].setText("");				
				}
			}
			else if (e.getAluUnit().equals("add")) { //addition unit update
				frame.getAddFields()[0].setText(e.getOp1().toString());
				frame.getAddFields()[1].setText(e.getOp2().toString());
				frame.getAddFields()[2].setText(e.getResult().toString());
			}
			else if (e.getAluUnit().equals("sub")) { //sub unit update
				frame.getSubFields()[0].setText(e.getOp1().toString());
				frame.getSubFields()[1].setText(e.getOp2().toString());
				frame.getSubFields()[2].setText(e.getResult().toString());
			}
			else if (e.getAluUnit().equals("div")) { //div unit update
				frame.getDivFields()[0].setText(e.getOp1().toString());
				frame.getDivFields()[1].setText(e.getOp2().toString());
				frame.getDivFields()[2].setText(e.getResult().toString());
			}
			else if (e.getAluUnit().equals("mul")) { //sub unit update
				frame.getMulFields()[0].setText(e.getOp1().toString());
				frame.getMulFields()[1].setText(e.getOp2().toString());
				frame.getMulFields()[2].setText(e.getResult().toString());
			}	
		}
	}

}
