package code;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class SimulatorLauncher {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		SimulatorLauncher simLauncher = new SimulatorLauncher();
		simLauncher.run();
		
	}
	
	private void run() {
		
		JFrame frame = new CPUframe();
		

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack(); //Adjusts size automatically
		frame.setVisible(true);
		
		

		
		
	}

}
