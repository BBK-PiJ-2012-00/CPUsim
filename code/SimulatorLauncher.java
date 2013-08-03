package code;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class SimulatorLauncher {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		SimulatorLauncher simLauncher = new SimulatorLauncher();
		simLauncher.run();
		
	}
	
	private void run() {
		//Create assembler, loader -> initiate loading of a text file via assembler
		ControlUnit controlUnit = new ControlUnitImpl(false);
		MainMemory memory = MemoryModule.getInstance();
		
		JLabel testLabel = new JLabel(((MemoryModule) memory).display());
		
		JFrame frame = new JFrame("CPUsim");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(testLabel, BorderLayout.WEST);
		frame.pack();
		frame.setVisible(true);
		
	}

}
