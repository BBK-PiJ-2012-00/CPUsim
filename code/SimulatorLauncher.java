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
		//Create assembler, loader -> initiate loading of a text file via assembler
		ControlUnit controlUnit = new ControlUnitImpl(false);
		MainMemory memory = MemoryModule.getInstance();
		
		//Assembler assembler = new AssemblerImpl();
//		assembler.selectFile("src/assemblyPrograms/assemblerTestProgram2");
//		assembler.assembleCode();
//		assembler.loadToLoader();
//		((AssemblerImpl) assembler).getLoader().loadToMemory();
		
		JFrame frame = new CPUframe();
		
//		JLabel testLabel = new JLabel(((MemoryModule) memory).display());
//		JLabel testLabelPC = new JLabel(controlUnit.getPC().display());
//		
//		//JTextArea memoryArea = new JTextArea(10, 50);
//		//memoryArea.add(testLabel);
//		JScrollPane memoryScrollPane = new JScrollPane(testLabel);
//		//JPanel memoryPanel = new JPanel();
//		//memoryPanel.add(memoryScrollPane);
//		//memoryPanel.setSize(100, 300);
//		
////				setPreferredSize(new Dimension(450, 110));
////				...
////				add(scrollPane, BorderLayout.CENTER);
////)
//		
//		JFrame frame = new JFrame("CPUsim");
		//frame.setSize(800, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.getContentPane().add(memoryScrollPane, BorderLayout.EAST);
//		frame.getContentPane().add(testLabelPC, BorderLayout.WEST);
		frame.pack(); //Adjusts size automatically
		frame.setVisible(true);
		
		
//		controlUnit.activate();
//		
//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		testLabelPC.setText(controlUnit.getPC().display());
//		testLabel.setText(((MemoryModule) memory).display());
		
		
		
	}

}
