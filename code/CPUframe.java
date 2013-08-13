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
	
	private SwingWorker<Void, Void> executionWorker;
	
	
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
	
	private JPanel aluPanel;
	private JPanel aluSubPanel1;
	private JPanel aluSubPanel2;
	private JPanel aluAdderPanel;
	private JPanel aluSubtractorPanel;
	private JPanel aluDivPanel;
	private JPanel aluMulPanel;
	
	
	private JTextArea memoryContentArea;
	private JTextArea assemblyProgramArea;
	
	private JTextField pcField;
	private JTextField irField;
	private JTextField statusField;
	private JTextField marField;
	private JTextField mbrField;
	
	private JTextField[] genRegisters = new JTextField[16];	

	
	private JButton executeButton;
	private JButton resetButton;
	private JButton fileOpenButton;
	private JButton stepButton;

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
	
		RegisterListener memoryListener = new RegisterListener(this);
		memory.registerListener(memoryListener);

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
		//subControlPanel1.setBorder(BorderFactory.createTitledBorder(" Control Panel "));
		
		subControlPanel2 = new JPanel();
		subControlPanel2.setLayout(new BoxLayout(subControlPanel2, BoxLayout.X_AXIS));
		
		controlPanel.add(subControlPanel1);
		controlPanel.add(subControlPanel2);
		
		fileOpenButton = new JButton("Select Assembly File");
		fileOpenButton.addActionListener(new FileOpenListener());
		subControlPanel1.add(fileOpenButton);		
		
		fileChooser = new JFileChooser("src/assemblyPrograms");	
		
		executeButton = new JButton("Execute Program");
		executeButton.addActionListener(new ExecuteListener());
		subControlPanel1.add(executeButton);
		
		stepButton = new JButton("Step");
		stepButton.setAlignmentX(LEFT_ALIGNMENT);
		stepButton.addActionListener(new StepExecutionListener());
		
		subControlPanel2.add(stepButton);
		
		controlPanel.setBorder(BorderFactory.createTitledBorder( " Control Panel "));
		
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
		
		irField = new JTextField(8);
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
		MAR.getInstance().registerListener(registerListener);
		
		mbrField = new JTextField(8);
		mbrField.setEditable(false);
		mbrPanel = new JPanel();
		mbrPanel.setMaximumSize(new Dimension(150, 60));
		mbrPanel.add(mbrField);
		mbrPanel.setBorder(BorderFactory.createTitledBorder(" MBR "));
		MBR.getInstance().registerListener(registerListener);
		
		controlRegistersPanel2.add(marPanel);
		controlRegistersPanel2.add(mbrPanel);
		
		
		registerPanel.add(controlRegistersPanel2);
		
		/*
		 * General purpose registers.
		 */
		
		generalPurposePanel = new JPanel();
		generalPurposePanel.setLayout(new BoxLayout(generalPurposePanel, BoxLayout.X_AXIS));
		generalPurposePanel.setMaximumSize(new Dimension(400, 300));
		generalPurposePanel.setBorder(BorderFactory.createTitledBorder("General Purpose Registers"));
		
		JPanel individualPanel1 = new JPanel();
		individualPanel1.setLayout(new BoxLayout(individualPanel1, BoxLayout.Y_AXIS));
		
		JLabel r0label = new JLabel("r0");
		genRegisters[0] = new JTextField(4);
		genRegisters[0].setEditable(false);
		JPanel genReg0panel = new JPanel();
		genReg0panel.add(r0label);
		genReg0panel.add(genRegisters[0]);
		individualPanel1.add(genReg0panel);
		
		JLabel r1label = new JLabel("r1");
		genRegisters[1] = new JTextField(4);
		genRegisters[1].setEditable(false);
		JPanel genReg1panel = new JPanel();
		genReg1panel.add(r1label);
		genReg1panel.add(genRegisters[1]);
		individualPanel1.add(genReg1panel);
		
		JLabel r2label = new JLabel("r2");
		genRegisters[2] = new JTextField(4);
		genRegisters[2].setEditable(false);
		JPanel genReg2panel = new JPanel();
		genReg2panel.add(r2label);
		genReg2panel.add(genRegisters[2]);
		individualPanel1.add(genReg2panel);
		
		JLabel r3label = new JLabel("r3");
		genRegisters[3] = new JTextField(4);
		genRegisters[3].setEditable(false);
		JPanel genReg3panel = new JPanel();
		genReg3panel.add(r3label);
		genReg3panel.add(genRegisters[3]);
		individualPanel1.add(genReg3panel);
		
		
//		/*
//		 * Automated gen register creation.
//		 */
//		for (int i = 0; i < 4; i++) {
//			
//			JLabel regLabel = new JLabel("0" + i);
//			JTextField genRegister = new JTextField(4);
//			genRegister.setEditable(false);
//			JPanel regPanel = new JPanel();
//			genRegister.setEditable(false);
//			regPanel.add(regLabel);
//			regPanel.add(genRegister);
//			individualPanel1.add(regPanel);
//			
//		}
		generalPurposePanel.add(individualPanel1);
		
		JPanel individualPanel2 = new JPanel();
		individualPanel2.setLayout(new BoxLayout(individualPanel2, BoxLayout.Y_AXIS));
		
		JLabel r4label = new JLabel("r4");
		genRegisters[4] = new JTextField(4);
		genRegisters[4].setEditable(false);
		JPanel genReg4panel = new JPanel();
		genReg4panel.add(r4label);
		genReg4panel.add(genRegisters[4]);
		individualPanel2.add(genReg4panel);
		
		JLabel r5label = new JLabel("r5");
		genRegisters[5] = new JTextField(4);
		genRegisters[5].setEditable(false);
		JPanel genReg5panel = new JPanel();
		genReg5panel.add(r5label);
		genReg5panel.add(genRegisters[5]);
		individualPanel2.add(genReg5panel);
		
		JLabel r6label = new JLabel("r6");
		genRegisters[6] = new JTextField(4);
		genRegisters[6].setEditable(false);
		JPanel genReg6panel = new JPanel();
		genReg6panel.add(r6label);
		genReg6panel.add(genRegisters[6]);
		individualPanel2.add(genReg6panel);
		
		JLabel r7label = new JLabel("r7");
		genRegisters[7] = new JTextField(4);
		genRegisters[7].setEditable(false);
		JPanel genReg7panel = new JPanel();
		genReg7panel.add(r7label);
		genReg7panel.add(genRegisters[7]);
		individualPanel2.add(genReg7panel);
		
		
////		for (int i = 4; i < 8; i++) {
////			
////			JLabel regLabel = new JLabel("0" + i);
////			JTextField genRegister = new JTextField(4);
////			genRegister.setEditable(false);
////			JPanel regPanel = new JPanel();
////			genRegister.setEditable(false);
////			regPanel.add(regLabel);
////			regPanel.add(genRegister);
////			individualPanel2.add(regPanel);
////			
////		}
		
		generalPurposePanel.add(individualPanel2);
		
		JPanel individualPanel3 = new JPanel();
		individualPanel3.setLayout(new BoxLayout(individualPanel3, BoxLayout.Y_AXIS));
		
		JLabel r8label = new JLabel("r8");
		genRegisters[8] = new JTextField(4);
		genRegisters[8].setEditable(false);
		JPanel genReg8panel = new JPanel();
		genReg8panel.add(r8label);
		genReg8panel.add(genRegisters[8]);
		individualPanel3.add(genReg8panel);
		
		JLabel r9label = new JLabel("r9");
		genRegisters[9] = new JTextField(4);
		genRegisters[9].setEditable(false);
		JPanel genReg9panel = new JPanel();
		genReg9panel.add(r9label);
		genReg9panel.add(genRegisters[9]);
		individualPanel3.add(genReg9panel);
		
		JLabel r10label = new JLabel("r10");
		genRegisters[10] = new JTextField(4);
		genRegisters[10].setEditable(false);
		JPanel genReg10panel = new JPanel();
		genReg10panel.add(r10label);
		genReg10panel.add(genRegisters[10]);
		individualPanel3.add(genReg10panel);
		
		JLabel r11label = new JLabel("r11");
		genRegisters[11] = new JTextField(4);
		genRegisters[11].setEditable(false);
		JPanel genReg11panel = new JPanel();
		genReg11panel.add(r11label);
		genReg11panel.add(genRegisters[11]);
		individualPanel3.add(genReg11panel);
		
////		for (int i = 8; i < 12; i++) {
////			JLabel regLabel;
////			if (i < 10) {
////				regLabel = new JLabel("0" + i);
////			}
////			else {
////				regLabel = new JLabel("" + i);
////			}
////			JTextField genRegister = new JTextField(4);
////			JPanel regPanel = new JPanel();
////			genRegister.setEditable(false);
////			regPanel.add(regLabel);
////			regPanel.add(genRegister);
////			individualPanel3.add(regPanel);
////			
////		}
		
		generalPurposePanel.add(individualPanel3);
		
		JPanel individualPanel4 = new JPanel();
		individualPanel4.setLayout(new BoxLayout(individualPanel4, BoxLayout.Y_AXIS));
		
		JLabel r12label = new JLabel("r12");
		genRegisters[12] = new JTextField(4);
		genRegisters[12].setEditable(false);
		JPanel genReg12panel = new JPanel();
		genReg12panel.add(r12label);
		genReg12panel.add(genRegisters[12]);
		individualPanel4.add(genReg12panel);
		
		JLabel r13label = new JLabel("r13");
		genRegisters[13] = new JTextField(4);
		genRegisters[13].setEditable(false);
		JPanel genReg13panel = new JPanel();
		genReg13panel.add(r13label);
		genReg13panel.add(genRegisters[13]);
		individualPanel4.add(genReg13panel);
		
		JLabel r14label = new JLabel("r14");
		genRegisters[14] = new JTextField(4);
		genRegisters[14].setEditable(false);
		JPanel genReg14panel = new JPanel();
		genReg14panel.add(r14label);
		genReg14panel.add(genRegisters[14]);
		individualPanel4.add(genReg14panel);
		
		JLabel r15label = new JLabel("r15");
		genRegisters[15] = new JTextField(4);
		genRegisters[15].setEditable(false);
		JPanel genReg15panel = new JPanel();
		genReg15panel.add(r15label);
		genReg15panel.add(genRegisters[15]);
		individualPanel4.add(genReg15panel);
		
		generalPurposePanel.add(individualPanel4);
		
		
		
		registerPanel.add(generalPurposePanel);
		
		panel2.add(registerPanel);
		
		controlUnit.getRegisters().registerListener(registerListener);
		
		/*
		 * ALU display
		 */
		
//		private JPanel aluPanel;
//		private JPanel aluSubPanel1;
//		private JPanel aluSubPanel2;
//		private JPanel aluAdderPanel;
//		private JPanel aluSubtractorPanel;
//		private JPanel aluDivPanel;
//		private JPanel aluMulPanel;
		
		aluPanel = new JPanel(); //Panel to encase the two subpanels
		aluPanel.setLayout(new BoxLayout(aluPanel, BoxLayout.Y_AXIS));
		aluPanel.setBorder(BorderFactory.createTitledBorder(" ALU "));
		
		
		
		

		
		this.getContentPane().add(panel1); //Leftmost panel
		panel1.setBackground(Color.cyan);
		this.getContentPane().add(panel2);
		this.getContentPane().add(panel3);
		panel3.setBackground(Color.orange);
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
			//executionWorker = new ExecutionWorker().execute();
			executionWorker = new ExecutionWorker();
			executionWorker.execute();
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
	
	class StepExecutionListener implements ActionListener { //Implements step execution (notifies worker thread)

		@Override
		public void actionPerformed(ActionEvent e) {
			synchronized(controlUnit.getFetchDecodeStage()) {
				controlUnit.getFetchDecodeStage().notify();
			}
			synchronized(controlUnit.getExecuteStage()) {
				controlUnit.getExecuteStage().notify();
			}
//			synchronized(controlUnit.getWriteBackStage()) {
//				controlUnit.getWriteBackStage().notify();
//			}
		}
		
	}
	
	public JTextField getPCfield() {
		return this.pcField;
	}



	public JTextComponent getIRfield() {
		return this.irField;
	}
	
	public JTextArea getMemoryField() {
		return this.memoryContentArea;
	}



	public JTextComponent getMARfield() {
		return this.marField;
	}
	
	public JTextComponent getMBRfield() {
		return this.mbrField;
	}
	
	public JTextComponent getGenPurposeRegister(int index) {
		return this.genRegisters[index];
	}
	
	
	

		
		
	
	

}
