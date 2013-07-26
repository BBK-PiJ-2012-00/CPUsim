package code;

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
	}

}
