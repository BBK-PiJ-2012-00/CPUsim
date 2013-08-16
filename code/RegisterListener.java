package code;

import java.util.EventListener;

import javax.swing.JFrame;
import javax.swing.JTextField;

public class RegisterListener implements EventListener {
	private CPUframe frame; //Reference to main window frame object
	
	public RegisterListener(CPUframe frame) {
		this.frame = frame;
	}
		
	public void handleUpDateEvent(ModuleUpdateEvent e) {
		if (e.getSource() instanceof ProgramCounter) {
			frame.getPCfield().setText(e.getUpdate());
		}
		
		else if (e.getSource() instanceof InstructionRegister) {
			frame.getIRfield().setText(e.getUpdate());
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
		
		else if ((e.getSource() instanceof FetchDecodeStage) || (e.getSource() instanceof ExecuteStage) ||
				(e.getSource() instanceof WriteBackStage)) {
			frame.getActivityMonitor().append(e.getUpdate()); //Append new update to existing text
			frame.getActivityMonitor().setCaretPosition(frame.getActivityMonitor().getDocument().getLength());
		}
		
		/*
		 * As a dummy object is used for ALU updates (ALU uses static methods and cannot be instantiated, so a dummy
		 * Object instance is used) so this if statement must come last to avoid use by any other instances.
		 */
		else if (e.getSource() instanceof Object) { //ALU (static, therefore no object instantiation; dummy object used)
			if (e.getAluUnit().equals("")) { //indicates reset operation
				for (int i = 0; i < 12; i ++) {
					frame.getAllAluFields()[i].setText("");				
				}
			}
			if (e.getAluUnit().equals("add")) { //addition unit update
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
