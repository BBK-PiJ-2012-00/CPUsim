package code;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;


public class CPUframe extends JFrame {
	private ControlUnit controlUnit;
	private MainMemory memory;
	private Assembler assembler;
	
	private static final int FRAME_WIDTH = 1000;
	private static final int FRAME_HEIGHT = 1000;
	
	private JFileChooser fileChooser;
	
	private JPanel panel1;
	private JPanel panel2;
	private JPanel panel3;
	private JPanel panel4;
	
	private JPanel memoryPanel;
	private JPanel memoryContentsPanel;	
	
	private JPanel controlPanel;
	private JPanel subControlPanel1; //panel to contain open and execute buttons (horizontal layout)
	private JPanel subControlPanel2; //Additional panel for any additional buttons
	
	private JPanel assemblerPanel;
	private JPanel assemblerContentPanel;
	
	
	private JPanel registerPanel;
	private JPanel marPanel;
	private JPanel mbrPanel;
	private JPanel irPanel;
	private JPanel pcPanel;
	private JPanel statusPanel;
	
	//private JLabel memoryLabel;
	//private JLabel titleLabel;
	
	private JTextArea memoryContentArea;
	private JTextArea assemblyProgramArea;
	
	private JLabel MARlabel;
	private JLabel MBRlabel;
	private JLabel StatusRegisterlabel;
	private JLabel PClabel;
	private JLabel generalPurposeLabel;
	
	private JButton executeButton;
	private JButton resetButton;
	private JButton fileOpenButton;

	private JScrollPane scroller;
	private JScrollPane assemblerScroller;
	
	public CPUframe(ControlUnit controlUnit, MainMemory memory) {
		//this.assembler = assembler;
		this.controlUnit = controlUnit;
		this.memory = memory;
		
		//setSize(FRAME_WIDTH, FRAME_HEIGHT);
				
		createComponents();
		
		
	}
	
	private void createComponents() {
		
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS)); //Arrange panels horizontally
		
		panel1 = new JPanel();
		panel2 = new JPanel();
		panel3 = new JPanel();
		panel4 = new JPanel();
		
		
		
		/*
		 * Code for memory display.
		 *
		 */		
		memoryContentsPanel = new JPanel(); //A panel to store the scrollpane and text area for memory display
		
		memoryContentArea = new JTextArea(35, 12);
		memoryContentArea.setText(memory.display()); //Text area for memory display
		memoryContentArea.setCaretPosition(0);
		memoryContentArea.setEditable(false); //Memory display is not editable
		
		scroller = new JScrollPane(memoryContentArea);		
		
		memoryContentsPanel.add(scroller); //Add text area to memory panel
	
		

		//scroller.setBorder(BorderFactory.createTitledBorder(" Main Memory "));
		//memoryPanel.add(scroller);
		
		memoryPanel = new JPanel(); //Main panel for all memory related items
		memoryPanel.setLayout(new BoxLayout(memoryPanel, BoxLayout.Y_AXIS));
		memoryPanel.add(memoryContentsPanel);
		
		resetButton = new JButton("Reset");
		resetButton.addActionListener(new ResetListener());
		resetButton.setAlignmentX(CENTER_ALIGNMENT);
		memoryPanel.add(resetButton);
		
		memoryPanel.setBorder(BorderFactory.createTitledBorder(" Main Memory "));
		
		panel4.add(memoryPanel);
		//panel4.setBorder(BorderFactory.createEmptyBorder());
		
		
		/*
		 * Panel 1: control buttons, assembler and activity monitor
		 */
		
		panel1 = new JPanel();
		panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
		
		controlPanel = new JPanel(); //Container panel for all control buttons
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
		controlPanel.setAlignmentX(LEFT_ALIGNMENT);
		
		subControlPanel1 = new JPanel();
		subControlPanel1.setLayout(new BoxLayout(subControlPanel1, BoxLayout.X_AXIS));
		subControlPanel1.setBorder(BorderFactory.createTitledBorder(" Control Panel "));
		
		controlPanel.add(subControlPanel1);
		//controlPanel.add(subControlPanel2);
		
		fileOpenButton = new JButton("Select Assembly File");
		fileOpenButton.addActionListener(new FileOpenListener());
		subControlPanel1.add(fileOpenButton);		
		
		fileChooser = new JFileChooser("src/assemblyPrograms");	
		
		executeButton = new JButton("Execute Program");
		executeButton.addActionListener(new ExecuteListener());
		subControlPanel1.add(executeButton);			
		
		panel1.add(controlPanel);
	
		/*
		 * Assembler display in panel 1
		 */
		assemblerPanel = new JPanel();
		assemblerPanel.setAlignmentX(LEFT_ALIGNMENT);
		assemblerContentPanel = new JPanel();
		assemblerContentPanel.setLayout(new BoxLayout(assemblerContentPanel, BoxLayout.X_AXIS));
		assemblerContentPanel.setAlignmentX(LEFT_ALIGNMENT);
		assemblyProgramArea = new JTextArea(15, 30);
		assemblyProgramArea.setEditable(false);
		
		assemblerScroller = new JScrollPane(assemblyProgramArea);
		assemblerScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		assemblerContentPanel.add(assemblerScroller);
		assemblerContentPanel.setBorder(BorderFactory.createTitledBorder(" Assembly Program "));
		
		assemblerPanel.add(assemblerContentPanel);
		
		
		panel1.add(assemblerPanel);		
		//Assembly program panel isn't fixed to the left like control panel is; why is this?
		
		
		
		
		
		registerPanel = new JPanel();
		registerPanel.setBorder(BorderFactory.createTitledBorder("CPU Registers"));
		
		MARlabel = new JLabel(MAR.getInstance().display());
		marPanel = new JPanel();
		marPanel.add(MARlabel);
		marPanel.setBorder(BorderFactory.createTitledBorder(" MAR "));
		
		MBRlabel = new JLabel(MBR.getInstance().display());
		mbrPanel = new JPanel();
		mbrPanel.add(MARlabel);
		mbrPanel.setBorder(BorderFactory.createTitledBorder(" MBR "));
		
		
		
		registerPanel.add(marPanel);
		registerPanel.add(mbrPanel);
		
		
//		registerPanel.add(MARlabel);
//		
//		MBRlabel = new JLabel(MBR.getInstance().display());
//		registerPanel.add(MBRlabel);

		
			
		//int returnVal = fileChooser.showOpenDialog(this); //Opens file chooser on program launch
		
		this.getContentPane().add(panel1); //Leftmost panel
		this.getContentPane().add(panel2);
		this.getContentPane().add(panel3);
		this.getContentPane().add(panel4); //Rightmost panel
		
		
	}
	
	class ExecuteListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			controlUnit.activate();
			memoryContentArea.setText(memory.display());
			memoryContentArea.setCaretPosition(0); //Scrolls to top of memory for better view
			
		}
		
	}
	
	class ResetListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			memory.clearMemory();
			memoryContentArea.setText(memory.display());
			memoryContentArea.setCaretPosition(0);
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
		            
		            memory.clearMemory();
		           
		            assembler = new AssemblerImpl();
		            assembler.selectFile(file);
		    		assembler.assembleCode();
		    		assembler.loadToLoader();
		    		((AssemblerImpl) assembler).getLoader().loadToMemory();
		    		memoryContentArea.setText(memory.display()); //update memory display on opening file
		    		memoryContentArea.setCaretPosition(0); //Scrolls text area to top
		    		
		    		assemblyProgramArea.setText(assembler.display());
		    		assemblyProgramArea.setCaretPosition(0);
		    		
//		    		BufferedReader in = null;
//		    		try {
//		    		    in = new BufferedReader(new FileReader(file));
//		    		    String line;
//		    		    while ((line = in.readLine()) != null) {
//		    		        assemblyProgramArea.append(line);
//		    		        assemblyProgramArea.setCaretPosition(0);
//		    		    }
//		    		} catch (IOException ioEx) {
//		    		} finally {
//		    		    try { in.close(); } catch (Exception ex) { }
		    		
		    		
		    		
		            
//		            //This is where a real application would open the file.
//		            log.append("Opening: " + file.getName() + "." + newline);
		        } //else {
//		            log.append("Open command cancelled by user." + newline);
//		        }
		   }
		}
		
	}
	

}
