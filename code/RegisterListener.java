package code;

import java.util.EventListener;

import javax.swing.JFrame;

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
	}

}
