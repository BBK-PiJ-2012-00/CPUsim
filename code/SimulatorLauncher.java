package code;


import javax.swing.JFrame;

public class SimulatorLauncher {
	//This class launches the program

	
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
