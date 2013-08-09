package code;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


public class CPUframe extends JFrame {
	private ControlUnit controlUnit;
	private MainMemory memory;
	private Assembler assembler;
	
	private static final int FRAME_WIDTH = 700;
	private static final int FRAME_HEIGHT = 400;
	
	private JFileChooser fileChooser;
	
	private JPanel controlPanel;
	private JPanel memoryPanel;
	private JPanel registerPanel;
	
	private JLabel memoryLabel;
	private JLabel MARlabel;
	private JLabel MBRlabel;
	private JLabel StatusRegisterlabel;
	private JLabel PClabel;
	private JLabel generalPurposeLabel;
	
	private JButton executeButton;
	private JButton resetButton;
	private JButton fileOpenButton;

	private JScrollPane scroller;
	
	public CPUframe(Assembler assembler, ControlUnit controlUnit, MainMemory memory) {
		this.assembler = assembler;
		this.controlUnit = controlUnit;
		this.memory = memory;
				
		createComponents();
		
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
	}
	
	private void createComponents() {
		controlPanel = new JPanel();
		memoryPanel = new JPanel(); 
		memoryLabel = new JLabel(((MemoryModule) MemoryModule.getInstance()).display());
		memoryPanel.add(memoryLabel);
		registerPanel = new JPanel();
	
		scroller = new JScrollPane(memoryPanel);		
		this.getContentPane().add(scroller, BorderLayout.EAST); 
		
		MARlabel = new JLabel(MAR.getInstance().display());
		registerPanel.add(MARlabel);

		
		executeButton = new JButton("Execute Program");
		executeButton.addActionListener(new ExecuteListener());
		controlPanel.add(executeButton);
		resetButton = new JButton("Reset");
		resetButton.addActionListener(new ResetListener());
		
		fileOpenButton = new JButton("Select Assembly File");
		fileOpenButton.addActionListener(new FileOpenListener());
		controlPanel.add(fileOpenButton);
		
		
		controlPanel.add(resetButton);
		this.getContentPane().add(registerPanel);
		this.getContentPane().add(controlPanel);
		
		fileChooser = new JFileChooser("src/assemblyPrograms");		
		//int returnVal = fileChooser.showOpenDialog(this); //Opens file chooser on program launch
				
		
		
	}
	
	class ExecuteListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			controlUnit.activate();
			memoryLabel.setText(memory.display());
			
		}
		
	}
	
	class ResetListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			memory.clearMemory();
			memoryLabel.setText(memory.display());
		}
	}
	
	class FileOpenListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
		    //Handle open button action.
		    if (e.getSource() == fileOpenButton) {
		        int returnVal = fileChooser.showOpenDialog(fileOpenButton);

		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fileChooser.getSelectedFile();
		            
		            assembler.selectFile(file);
		    		assembler.assembleCode();
		    		assembler.loadToLoader();
		    		((AssemblerImpl) assembler).getLoader().loadToMemory();
		    		memoryLabel.setText(memory.display()); //update memory display on opening file
		            
//		            //This is where a real application would open the file.
//		            log.append("Opening: " + file.getName() + "." + newline);
		        } //else {
//		            log.append("Open command cancelled by user." + newline);
//		        }
		   }
		}
	}
	

}
