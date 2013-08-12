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
	}

}
