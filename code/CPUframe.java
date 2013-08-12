package code;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.EventListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;
import javax.swing.text.JTextComponent;




public class CPUframe extends JFrame {
	
	private ControlUnit controlUnit;
	private MainMemory memory;
	private Assembler assembler;
	
	
	private static final int FRAME_WIDTH = 1800;
	private static final int FRAME_HEIGHT = 1600;
	
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
	
	
	private JPanel registerPanel; //A panel to hold all cpu registers
	private JPanel controlRegistersPanel1; //A panel to hold one row of control registers
	private JPanel controlRegistersPanel2;
	private JPanel generalPurposePanel; // A panel to group the general purpsoe registers
	private JPanel marPanel;
	private JPanel mbrPanel;
	private JPanel irPanel;
	private JPanel pcPanel;
	private JPanel statusPanel;
	
	//private JLabel memoryLabel;
	//private JLabel titleLabel;
	
	private JTextArea memoryContentArea;
	private JTextArea assemblyProgramArea;
	
	private JTextField pcField;
	private JTextField irField;
	private JTextField statusField;
	private JTextField marField;
	private JTextField mbrField;
	
	

	
	private JButton executeButton;
	private JButton resetButton;
	private JButton fileOpenButton;

	private JScrollPane scroller;
	private JScrollPane assemblerScroller;
	
	public CPUframe() {
		//this.assembler = assembler;
		this.controlUnit =  new ControlUnitImpl(false);
		this.memory = MemoryModule.getInstance();
		
		//setSize(FRAME_WIDTH, FRAME_HEIGHT);
				
		createComponents();
		
		
	}
	
	
	
	private void createComponents() {
		
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS)); //Arrange panels horizontally
		
		//panel1 = new JPanel();
		//panel1.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));	
		//panel2 = new JPanel();
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
		panel1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		//panel1.setBackground(Color.cyan);
		panel1.setMaximumSize(new Dimension(0, 1000)); //Use Box to keep things rigid
		
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
		JPanel assemblerBufferPanel = new JPanel(); //To make border equal to controlPanel above
		assemblerContentPanel = new JPanel();
		assemblyProgramArea = new JTextArea(15, 30);
		assemblyProgramArea.setEditable(false);
		assemblyProgramArea.setCaretPosition(0);
		
		assemblerScroller = new JScrollPane(assemblyProgramArea);
		
		assemblerBufferPanel.add(assemblerScroller);
		
		assemblerContentPanel.add(assemblerBufferPanel);
		
		assemblerPanel = new JPanel();
		//assemblerPanel.setLayout(new BoxLayout(assemblerPanel, BoxLayout.Y_AXIS));
		assemblerPanel.add(assemblerContentPanel);		
		assemblerPanel.setAlignmentX(LEFT_ALIGNMENT);
		assemblerPanel.setAlignmentY(BOTTOM_ALIGNMENT);
		
		
		assemblerContentPanel.setBorder(BorderFactory.createTitledBorder(" Assembly Program "));
		
		panel1.add(assemblerPanel);		
		//Assembly program panel isn't fixed size; things move when frame resied; panels not fixed size.
		
		/*
		 * CPU registers
		 */
		
		
		panel2 = new JPanel();
		panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
		
		registerPanel = new JPanel();
		registerPanel.setMaximumSize(new Dimension(400, 400));
		registerPanel.setAlignmentX(CENTER_ALIGNMENT);
		registerPanel.setAlignmentY(TOP_ALIGNMENT);
		//registerPanel.setMaximumSize(new Dimension(20, 20));
		registerPanel.setBorder(BorderFactory.createTitledBorder("CPU Registers"));
		registerPanel.setLayout(new BoxLayout(registerPanel, BoxLayout.Y_AXIS));
		
		controlRegistersPanel1 = new JPanel();		
		controlRegistersPanel1.setLayout(new BoxLayout(controlRegistersPanel1, BoxLayout.X_AXIS));
		
		pcField = new JTextField(4);		
		pcField.setEditable(false);
		pcField.setAlignmentX(CENTER_ALIGNMENT);
		pcField.setAlignmentY(CENTER_ALIGNMENT);
		pcPanel = new JPanel();
		pcPanel.setMaximumSize(new Dimension(75, 60));
		pcPanel.add(pcField);
		pcPanel.setBorder(BorderFactory.createTitledBorder(" PC "));
		
		RegisterListener registerListener = new RegisterListener(this);
		controlUnit.getPC().registerListener(registerListener);
		
		irField = new JTextField(10);
		irField.setEditable(false);
		irPanel = new JPanel();
		irPanel.setMaximumSize(new Dimension(150, 60));
		irPanel.add(irField);
		irPanel.setBorder(BorderFactory.createTitledBorder(" IR "));
		
		controlUnit.getIR().registerListener(registerListener);
		
		statusField = new JTextField(4);
		statusField.setEditable(false);
		statusPanel = new JPanel();
		statusPanel.setMaximumSize(new Dimension(75, 60));
		//statusPanel.setSize(5, 5);
		statusPanel.add(statusField);
		statusPanel.setBorder(BorderFactory.createTitledBorder(" CC "));
		
		controlRegistersPanel1.add(pcPanel);
		controlRegistersPanel1.add(irPanel);
		controlRegistersPanel1.add(statusPanel);
		registerPanel.add(controlRegistersPanel1);
				
		
		controlRegistersPanel2 = new JPanel();
		controlRegistersPanel2.setLayout(new BoxLayout(controlRegistersPanel2, BoxLayout.X_AXIS));
		
		marField = new JTextField(4);
		marField.setEditable(false);
		marPanel = new JPanel();
		marPanel.setMaximumSize(new Dimension(75, 60));
		marPanel.add(marField);
		marPanel.setBorder(BorderFactory.createTitledBorder(" MAR "));
		
		mbrField = new JTextField(4);
		mbrField.setEditable(false);
		mbrPanel = new JPanel();
		mbrPanel.setMaximumSize(new Dimension(75, 60));
		mbrPanel.add(mbrField);
		mbrPanel.setBorder(BorderFactory.createTitledBorder(" MBR "));
		
		controlRegistersPanel2.add(marPanel);
		controlRegistersPanel2.add(mbrPanel);
		
		
		registerPanel.add(controlRegistersPanel2);
		
		generalPurposePanel = new JPanel();
		generalPurposePanel.setLayout(new BoxLayout(generalPurposePanel, BoxLayout.X_AXIS));
		generalPurposePanel.setMaximumSize(new Dimension(400, 300));
		generalPurposePanel.setBorder(BorderFactory.createTitledBorder("General Purpose Registers"));
		
		JPanel individualPanel1 = new JPanel();
		individualPanel1.setLayout(new BoxLayout(individualPanel1, BoxLayout.Y_AXIS));
		
		for (int i = 0; i < 4; i++) {
			
			JLabel regLabel = new JLabel("0" + i);
			JTextField genRegister = new JTextField(4);
			genRegister.setEditable(false);
			JPanel regPanel = new JPanel();
			genRegister.setEditable(false);
			regPanel.add(regLabel);
			regPanel.add(genRegister);
			individualPanel1.add(regPanel);
			
		}
		generalPurposePanel.add(individualPanel1);
		
		JPanel individualPanel2 = new JPanel();
		individualPanel2.setLayout(new BoxLayout(individualPanel2, BoxLayout.Y_AXIS));
		
		for (int i = 4; i < 8; i++) {
			
			JLabel regLabel = new JLabel("0" + i);
			JTextField genRegister = new JTextField(4);
			genRegister.setEditable(false);
			JPanel regPanel = new JPanel();
			genRegister.setEditable(false);
			regPanel.add(regLabel);
			regPanel.add(genRegister);
			individualPanel2.add(regPanel);
			
		}
		generalPurposePanel.add(individualPanel2);
		
		JPanel individualPanel3 = new JPanel();
		individualPanel3.setLayout(new BoxLayout(individualPanel3, BoxLayout.Y_AXIS));
		
		for (int i = 8; i < 12; i++) {
			JLabel regLabel;
			if (i < 10) {
				regLabel = new JLabel("0" + i);
			}
			else {
				regLabel = new JLabel("" + i);
			}
			JTextField genRegister = new JTextField(4);
			JPanel regPanel = new JPanel();
			genRegister.setEditable(false);
			regPanel.add(regLabel);
			regPanel.add(genRegister);
			individualPanel3.add(regPanel);
			
		}
		generalPurposePanel.add(individualPanel3);
		
		JPanel individualPanel4 = new JPanel();
		individualPanel4.setLayout(new BoxLayout(individualPanel4, BoxLayout.Y_AXIS));
		
		for (int i = 12; i < 16; i++) {
			JLabel regLabel = new JLabel("" + i);
			JTextField genRegister = new JTextField(4);
			JPanel regPanel = new JPanel();
			genRegister.setEditable(false);
			regPanel.add(regLabel);
			regPanel.add(genRegister);
			individualPanel4.add(regPanel);
			
		}
		generalPurposePanel.add(individualPanel4);
		
		
		
		registerPanel.add(generalPurposePanel);
		
		panel2.add(registerPanel);
		
		/*
		 * ALU display
		 */
		

		
		this.getContentPane().add(panel1); //Leftmost panel
		this.getContentPane().add(panel2);
		this.getContentPane().add(panel3);
		this.getContentPane().add(panel4); //Rightmost panel
		
		
	}
	
//	class MeaningOfLifeFinder extends SwingWorker<String, Object> {
//	       @Override
//	       public String doInBackground() {
//	           return findTheMeaningOfLife();
//	       }
//
//	       @Override
//	       protected void done() {
//	           try {
//	               label.setText(get());
//	           } catch (Exception ignore) {
//	           }
//	       }
//	   }
//
//	   (new MeaningOfLifeFinder()).execute();
//	
	class ExecutionWorker extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() throws Exception {
			controlUnit.activate();			
			return null;
		}
		
		@Override
		protected void done() {
			memoryContentArea.setText(memory.display());
			memoryContentArea.setCaretPosition(0);
		}
		
	}
	
	
	class ExecuteListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			new ExecutionWorker().execute();
//			controlUnit.activate();
//			memoryContentArea.setText(memory.display());
//			memoryContentArea.setCaretPosition(0); //Scrolls to top of memory for better view
			
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
		            
		        }
		   }
		}		
	}
	
	public JTextField getPCfield() {
		return this.pcField;
	}



	public JTextComponent getIRfield() {
		return this.irField;
	}
	
	

		
		
	
	

}
