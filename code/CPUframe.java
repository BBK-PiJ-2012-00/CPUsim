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
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;




public class CPUframe extends JFrame {
	
	private ControlUnit controlUnit;
	private MainMemory memory;
	private Assembler assembler;
	private Loader loader;
	private BusController systemBusController;
	
	private File currentAssemblyFile; //To hold a reference to the current assembly program to facilitate restarting the program.
	
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
	
	private JPanel activityContentPanel;
	private JPanel activityPanel;
	private JScrollPane activityScroller;
	
	
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
	
	private JPanel systemBusPanel;
	private JPanel controlLinePanel;
	private JPanel addressBusPanel;
	private JPanel dataBusPanel;
	
	
	private JTextArea memoryContentArea;
	private JTextArea assemblyProgramArea;
	
	private JTextArea activityArea;
	
	private JTextField pcField;
	private JTextField irField;
	private JTextField statusField;
	private JTextField marField;
	private JTextField mbrField;
	
	private JTextField[] genRegisters = new JTextField[16];	
	
	private JTextField addOperand1;
	private JTextField addOperand2;
	private JTextField addResult;
	
	private JTextField subOperand1;
	private JTextField subOperand2;
	private JTextField subResult;
	
	private JTextField divOperand1;
	private JTextField divOperand2;
	private JTextField divResult;
	
	private JTextField mulOperand1;
	private JTextField mulOperand2;
	private JTextField mulResult;
	
	private JTextField controlLineField;
	private JTextField addressBusField;
	private JTextField dataBusField;
	
	private JButton executeButton;
	private JButton resetButton;
	private JButton fileOpenButton;
	private JButton helpButton;
	private JButton modeSwitchButton;
	private JButton stepButton;

	private JScrollPane scroller;
	private JScrollPane assemblerScroller;
	
	public CPUframe() {
		//this.assembler = assembler;
		//this.controlUnit = new ControlUnitImpl(false);
		//this.memory = MemoryModule.getInstance();
		
		CPUbuilder cpuBuilder = new CPUbuilder(false);
		
		this.controlUnit = cpuBuilder.getControlUnit();
		this.memory = cpuBuilder.getMemoryModule();
		this.loader = cpuBuilder.getLoader();
		this.systemBusController = cpuBuilder.getBusController();	
		
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
	
		UpdateListener memoryListener = new UpdateListener(this);
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
		
		helpButton = new JButton("Help!");
		helpButton.addActionListener(new HelpButtonListener());
		
		subControlPanel2.add(helpButton);
		
		modeSwitchButton = new JButton("Pipelined Mode");
		subControlPanel2.add(modeSwitchButton);
		
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
		 * Activity Monitor display in panel1
		 */
		JPanel activityBufferPanel = new JPanel(); //To make border equal to controlPanel and assembly above
		activityContentPanel = new JPanel();
		activityArea = new JTextArea(15, 30);
		activityArea.setEditable(false);
		
		activityScroller = new JScrollPane(activityArea);
		
		activityBufferPanel.add(activityScroller);
		
		activityContentPanel.add(activityBufferPanel);
		
		activityPanel = new JPanel();
		activityPanel.add(activityContentPanel);			
		
		activityContentPanel.setBorder(BorderFactory.createTitledBorder(" Activity Monitor "));
		activityPanel.setAlignmentX(LEFT_ALIGNMENT);
		
		controlUnit.getFetchDecodeStage().registerListener(new UpdateListener(this)); //Register a listener with FD stage
		controlUnit.getExecuteStage().registerListener(new UpdateListener(this)); //Register a listener with Ex. stage
		
		panel1.add(activityPanel);
		
		
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
		//registerPanel.setLayout(new BoxLayout(registerPanel, BoxLayout.X_AXIS));
		
		JPanel topRegisterPanel = new JPanel();
		topRegisterPanel.setLayout(new BoxLayout(topRegisterPanel, BoxLayout.X_AXIS));
		
		controlRegistersPanel1 = new JPanel();		
		//controlRegistersPanel1.setLayout(new BoxLayout(controlRegistersPanel1, BoxLayout.X_AXIS));
		controlRegistersPanel1.setLayout(new BoxLayout(controlRegistersPanel1, BoxLayout.Y_AXIS));
		
		pcField = new JTextField(6);		
		pcField.setEditable(false);
		pcField.setAlignmentX(CENTER_ALIGNMENT);
		pcField.setAlignmentY(CENTER_ALIGNMENT);
		pcPanel = new JPanel();
		pcPanel.setMaximumSize(new Dimension(125, 60));
		pcPanel.add(pcField);
		pcPanel.setBorder(BorderFactory.createTitledBorder(" PC "));
		
		UpdateListener registerListener = new UpdateListener(this);
		controlUnit.getPC().registerListener(registerListener);
		
		irField = new JTextField(8);
		irField.setEditable(false);
		irPanel = new JPanel();
		irPanel.setMaximumSize(new Dimension(150, 90));
		irPanel.add(irField);
		irPanel.setBorder(BorderFactory.createTitledBorder(" IR "));
		
		controlUnit.getIR().registerListener(new UpdateListener(this));
		
		statusField = new JTextField(6);
		statusField.setEditable(false);
		statusPanel = new JPanel();
		statusPanel.setMaximumSize(new Dimension(125, 60));
		//statusPanel.setSize(5, 5);
		statusPanel.add(statusField);
		statusPanel.setBorder(BorderFactory.createTitledBorder(" CC "));
		
		controlUnit.getStatusRegister().registerListener(new UpdateListener(this));
		
		controlRegistersPanel1.add(pcPanel);
		//controlRegistersPanel1.add(irPanel);
		controlRegistersPanel1.add(statusPanel);
		controlRegistersPanel1.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 20));
		//registerPanel.add(controlRegistersPanel1);
		
		
		controlRegistersPanel2 = new JPanel();
		//controlRegistersPanel2.setLayout(new BoxLayout(controlRegistersPanel2, BoxLayout.X_AXIS));
		controlRegistersPanel2.setLayout(new BoxLayout(controlRegistersPanel2, BoxLayout.Y_AXIS));
		
		marField = new JTextField(8);
		marField.setEditable(false);		
		marPanel = new JPanel();
		marPanel.setMaximumSize(new Dimension(150, 60));
		marPanel.add(marField);
		Border cyanLine = BorderFactory.createLineBorder(Color.cyan);
		marPanel.setBorder(BorderFactory.createTitledBorder(cyanLine, " MAR "));
		controlUnit.getMAR().registerListener(registerListener);
		
		mbrField = new JTextField(8);
		mbrField.setEditable(false);
		mbrPanel = new JPanel();
		mbrPanel.setMaximumSize(new Dimension(150, 60));
		mbrPanel.add(mbrField);
		Border magentaLine = BorderFactory.createLineBorder(Color.magenta);
		mbrPanel.setBorder(BorderFactory.createTitledBorder(magentaLine, " MBR "));
		controlUnit.getMBR().registerListener(registerListener);
		
		controlRegistersPanel2.add(marPanel);
		controlRegistersPanel2.add(mbrPanel);
		controlRegistersPanel2.add(irPanel);
		controlRegistersPanel2.setBorder(BorderFactory.createEmptyBorder(0, 15, 30, 0));
		
		topRegisterPanel.add(controlRegistersPanel1);
		topRegisterPanel.add(controlRegistersPanel2);
		//topRegisterPanel.setPreferredSize(new Dimension(500, 600));
		
		//registerPanel.add(controlRegistersPanel2);
		registerPanel.add(topRegisterPanel);
		//registerPanel.setMinimumSize(new Dimension(10000, 1000));
		
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
		
		aluPanel = new JPanel(); //Panel to encase the two subpanels
		aluPanel.setLayout(new BoxLayout(aluPanel, BoxLayout.X_AXIS));
		aluPanel.setBorder(BorderFactory.createTitledBorder(" ALU "));
		
		aluSubPanel1 = new JPanel();
		aluSubPanel1.setLayout(new BoxLayout(aluSubPanel1, BoxLayout.Y_AXIS));
		
		JLabel addOperand1Label = new JLabel("Operand 1:");
		addOperand1 = new JTextField(4);
		addOperand1.setEditable(false);
		JPanel adderOperand1Panel = new JPanel();
		adderOperand1Panel.setLayout(new BoxLayout(adderOperand1Panel, BoxLayout.X_AXIS));
		adderOperand1Panel.add(addOperand1Label);
		adderOperand1Panel.add(addOperand1);

		
		JLabel addOperand2Label = new JLabel("Operand 2:");
		addOperand2 = new JTextField(4);
		//addOperand2.setMaximumSize(new Dimension(20, 10));
		addOperand2.setEditable(false);
		JPanel adderOperand2Panel = new JPanel();
		adderOperand2Panel.setLayout(new BoxLayout(adderOperand2Panel, BoxLayout.X_AXIS));
		adderOperand2Panel.add(addOperand2Label);
		adderOperand2Panel.add(addOperand2);
		
		JLabel addResultLabel = new JLabel("Result:       ");
		addResult = new JTextField(4);
		addResult.setEditable(false);
		JPanel adderResultPanel = new JPanel();
		adderResultPanel.setLayout(new BoxLayout(adderResultPanel, BoxLayout.X_AXIS));
		adderResultPanel.add(addResultLabel);
		adderResultPanel.add(addResult);		
		
		aluAdderPanel = new JPanel();
		aluAdderPanel.setLayout(new BoxLayout(aluAdderPanel, BoxLayout.Y_AXIS));
		aluAdderPanel.add(adderOperand1Panel);
		aluAdderPanel.add(adderOperand2Panel);
		aluAdderPanel.add(adderResultPanel);
		aluAdderPanel.setBorder(BorderFactory.createTitledBorder("Add. Unit"));
		
		aluSubPanel1.add(aluAdderPanel);
		
		/*
		 * Sub unit
		 */
		JLabel subOperand1Label = new JLabel("Operand 1:");
		subOperand1 = new JTextField(4);
		subOperand1.setEditable(false);
		JPanel subOperand1Panel = new JPanel();
		subOperand1Panel.setLayout(new BoxLayout(subOperand1Panel, BoxLayout.X_AXIS));
		subOperand1Panel.add(subOperand1Label);
		subOperand1Panel.add(subOperand1);

		
		JLabel subOperand2Label = new JLabel("Operand 2:");
		subOperand2 = new JTextField(4);
		//addOperand2.setMaximumSize(new Dimension(20, 10));
		subOperand2.setEditable(false);
		JPanel subOperand2Panel = new JPanel();
		subOperand2Panel.setLayout(new BoxLayout(subOperand2Panel, BoxLayout.X_AXIS));
		subOperand2Panel.add(subOperand2Label);
		subOperand2Panel.add(subOperand2);
		
		JLabel subResultLabel = new JLabel("Result:       ");
		subResult = new JTextField(4);
		subResult.setEditable(false);
		JPanel subResultPanel = new JPanel();
		subResultPanel.setLayout(new BoxLayout(subResultPanel, BoxLayout.X_AXIS));
		subResultPanel.add(subResultLabel);
		subResultPanel.add(subResult);		
		
		aluSubtractorPanel = new JPanel();
		aluSubtractorPanel.setLayout(new BoxLayout(aluSubtractorPanel, BoxLayout.Y_AXIS));
		aluSubtractorPanel.add(subOperand1Panel);
		aluSubtractorPanel.add(subOperand2Panel);
		aluSubtractorPanel.add(subResultPanel);
		aluSubtractorPanel.setBorder(BorderFactory.createTitledBorder("Sub. Unit"));
		
		aluSubPanel1.add(aluAdderPanel);
		aluSubPanel1.add(aluSubtractorPanel);
		//aluSubPanel1.setMaximumSize(new Dimension(100, 125));
		//aluSubPanel1.setMinimumSize(new Dimension(100, 125));
		
		
		
		/*
		 * For aluSubPanel2, containing mul and div units
		 */
		
		aluSubPanel2 = new JPanel();
		aluSubPanel2.setLayout(new BoxLayout(aluSubPanel2, BoxLayout.Y_AXIS));			

		JLabel divOperand1Label = new JLabel("Operand 1:");
		divOperand1 = new JTextField(4);
		divOperand1.setEditable(false);
		JPanel divOperand1Panel = new JPanel();
		divOperand1Panel.setLayout(new BoxLayout(divOperand1Panel, BoxLayout.X_AXIS));
		divOperand1Panel.add(divOperand1Label);
		divOperand1Panel.add(divOperand1);

		
		JLabel divOperand2Label = new JLabel("Operand 2:");
		divOperand2 = new JTextField(4);
		//addOperand2.setMaximumSize(new Dimension(20, 10));
		divOperand2.setEditable(false);
		JPanel divOperand2Panel = new JPanel();
		divOperand2Panel.setLayout(new BoxLayout(divOperand2Panel, BoxLayout.X_AXIS));
		divOperand2Panel.add(divOperand2Label);
		divOperand2Panel.add(divOperand2);
		
		JLabel divResultLabel = new JLabel("Result:       ");
		divResult = new JTextField(4);
		divResult.setEditable(false);
		JPanel divResultPanel = new JPanel();
		divResultPanel.setLayout(new BoxLayout(divResultPanel, BoxLayout.X_AXIS));
		divResultPanel.add(divResultLabel);
		divResultPanel.add(divResult);		
		
		aluDivPanel = new JPanel();
		aluDivPanel.setLayout(new BoxLayout(aluDivPanel, BoxLayout.Y_AXIS));
		aluDivPanel.add(divOperand1Panel);
		aluDivPanel.add(divOperand2Panel);
		aluDivPanel.add(divResultPanel);
		aluDivPanel.setBorder(BorderFactory.createTitledBorder("Div. Unit"));
		
		
		/*
		 * Mul unit
		 */
		JLabel mulOperand1Label = new JLabel("Operand 1:");
		mulOperand1 = new JTextField(4);
		mulOperand1.setEditable(false);
		JPanel mulOperand1Panel = new JPanel();
		mulOperand1Panel.setLayout(new BoxLayout(mulOperand1Panel, BoxLayout.X_AXIS));
		mulOperand1Panel.add(mulOperand1Label);
		mulOperand1Panel.add(mulOperand1);

		
		JLabel mulOperand2Label = new JLabel("Operand 2:");
		mulOperand2 = new JTextField(4);
		//addOperand2.setMaximumSize(new Dimension(20, 10));
		mulOperand2.setEditable(false);
		JPanel mulOperand2Panel = new JPanel();
		mulOperand2Panel.setLayout(new BoxLayout(mulOperand2Panel, BoxLayout.X_AXIS));
		mulOperand2Panel.add(mulOperand2Label);
		mulOperand2Panel.add(mulOperand2);
		
		JLabel mulResultLabel = new JLabel("Result:       ");
		mulResult = new JTextField(4);
		mulResult.setEditable(false);
		JPanel mulResultPanel = new JPanel();
		mulResultPanel.setLayout(new BoxLayout(mulResultPanel, BoxLayout.X_AXIS));
		mulResultPanel.add(mulResultLabel);
		mulResultPanel.add(mulResult);		
		
		aluMulPanel = new JPanel();
		aluMulPanel.setLayout(new BoxLayout(aluMulPanel, BoxLayout.Y_AXIS));
		aluMulPanel.add(mulOperand1Panel);
		aluMulPanel.add(mulOperand2Panel);
		aluMulPanel.add(mulResultPanel);
		aluMulPanel.setBorder(BorderFactory.createTitledBorder("Mul. Unit"));
		
		aluSubPanel2.add(aluDivPanel);
		aluSubPanel2.add(aluMulPanel);
		//aluSubPanel2.setMaximumSize(new Dimension(100, 125));
		//aluSubPanel2.setMinimumSize(new Dimension(100, 125));
		
		aluPanel.add(aluSubPanel1);
		aluPanel.add(aluSubPanel2);
		aluPanel.setPreferredSize(new Dimension(395, 290));
		
		JPanel bufferPanel = new JPanel(); //To separate registers from ALU with some empty space
		bufferPanel.setPreferredSize(new Dimension(395, 20));
		panel2.add(bufferPanel);
		
		panel2.add(aluPanel);
		panel2.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5)); 
		
		ALU.registerListener(new UpdateListener(this));
		
		
		/*
		 * System Bus
		 */
//		private JPanel systemBusPanel;
//		private JPanel controlLinePanel;
//		private JPanel addressBusPanel;
//		private JPanel dataBusPanel;
		
		systemBusPanel = new JPanel();
		systemBusPanel.setLayout(new BoxLayout(systemBusPanel, BoxLayout.Y_AXIS));//Add bus panels vertically
		
		addressBusPanel = new JPanel();
		addressBusPanel.setPreferredSize(new Dimension(175, 60));
		//addressBusPanel.setBackground(Color.blue);
		//Border cyanLine = BorderFactory.createLineBorder(Color.cyan);	
		addressBusPanel.setBorder(BorderFactory.createTitledBorder(cyanLine, "Address Bus"));
		//Border addressBusBorder = BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Address Bus");
		//addressBusPanel.setBorder(addressBusBorder);
		addressBusField = new JTextField(8);
		addressBusField.setEditable(false);
		addressBusPanel.add(addressBusField);
		systemBusPanel.add(addressBusPanel);
		
		systemBusController.accessControlLine().getAddressBus().registerListener(new UpdateListener(this));
		
		dataBusPanel = new JPanel();
		dataBusPanel.setPreferredSize(new Dimension(175, 60));
		//dataBusPanel.setBackground(Color.magenta);
		//Border magentaLine = BorderFactory.createLineBorder(Color.magenta);
		dataBusPanel.setBorder(BorderFactory.createTitledBorder(magentaLine, "Data Bus"));
		dataBusField = new JTextField(8);
		dataBusField.setEditable(false);
		dataBusPanel.add(dataBusField);
		systemBusPanel.add(dataBusPanel);
		
		systemBusController.accessControlLine().getDataBus().registerListener(new UpdateListener(this));
		
		controlLinePanel = new JPanel();
		controlLinePanel.setPreferredSize(new Dimension(175, 60));
		controlLinePanel.setBorder(BorderFactory.createTitledBorder("Control Line"));
		controlLineField = new JTextField(8);
		controlLineField.setEditable(false);
		controlLinePanel.add(controlLineField);
		systemBusPanel.add(controlLinePanel);
		
		systemBusController.accessControlLine().registerListener(new UpdateListener(this));
		
		systemBusPanel.setBorder(BorderFactory.createTitledBorder("System Bus"));
		
		
		panel3.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 10));
		panel3.add(systemBusPanel);

		
		this.getContentPane().add(panel1); //Leftmost panel
		panel1.setBackground(Color.cyan);
		this.getContentPane().add(panel2);
		panel2.setBackground(Color.DARK_GRAY);
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
			//executionWorker.cancel(true); //Terminate the thread.
		}
		
	}
	
	
	class ExecuteListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (currentAssemblyFile != null) { //There must be a file to execute
				
				if (executionWorker == null) { //Only create worker thread if the current one is null (to avoid several threads).;
					executionWorker = new ExecutionWorker();
					executionWorker.execute();
				}
				
			}
			else {
				JLabel noFileErrorMessage = new JLabel("Please select an assembly file first!");
				JOptionPane.showMessageDialog(null, noFileErrorMessage, "Error!", JOptionPane.WARNING_MESSAGE);
			}
			
		}
		
	}
	
	class ResetListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
//			memory.clearMemory();
//			memoryContentArea.setText(memory.display());
//			memoryContentArea.setCaretPosition(0);
			
			executionWorker.cancel(true); //Old worker thread needs terminating
			memory.clearMemory();
            activityArea.setText("");
            controlUnit.clearRegisters();
           // controlUnit.resetStages();
            
            
           
            assembler = new AssemblerImpl(loader);
            assembler.selectFile(currentAssemblyFile); //Reopen previously selected assembly file
    		assembler.assembleCode();
    		assembler.loadToLoader();
    		assembler.getLoader().loadToMemory();
    		memoryContentArea.setText(memory.display()); //update memory display on opening file
    		memoryContentArea.setCaretPosition(0); //Scrolls text area to top
    		
    		assemblyProgramArea.setText(assembler.display());
    		assemblyProgramArea.setCaretPosition(0);		
		}
	}
	
	
	/*
	 * Clear fields upon selecting a file so that the user can run as many programs in succession
	 * as desired.
	 */
	class FileOpenListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
		    //Handle open button action.
		    if (e.getSource() == fileOpenButton) {
		        int returnVal = fileChooser.showOpenDialog(fileOpenButton);

		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            currentAssemblyFile = fileChooser.getSelectedFile();
		            
		            memory.clearMemory();
		            activityArea.setText("");
		            controlUnit.clearRegisters();
		           
		            assembler = new AssemblerImpl(loader);
		            assembler.selectFile(currentAssemblyFile);
		    		assembler.assembleCode();
		    		assembler.loadToLoader();
		    		assembler.getLoader().loadToMemory();
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
			if (currentAssemblyFile == null) { //To alert user to fact that they should select a file first
				JLabel noFileErrorMessage = new JLabel("Please select an assembly file first!");
				JOptionPane.showMessageDialog(null, noFileErrorMessage, "Error!", JOptionPane.WARNING_MESSAGE);
			}
			else if (systemBusController.accessControlLine().isWaiting()) {
				synchronized(systemBusController.accessControlLine()) {
					systemBusController.accessControlLine().notify();
					//System.out.println("bus notify called");
				}
			}
			else if (controlUnit.getFetchDecodeStage().isWaiting()) {
				synchronized(controlUnit.getFetchDecodeStage()) {
					controlUnit.getFetchDecodeStage().notify();
					//System.out.println("f/d notify called");
				}
			}
			else if (controlUnit.getExecuteStage().isWaiting()) {
				synchronized(controlUnit.getExecuteStage()) {
					controlUnit.getExecuteStage().notify();
					//System.out.println("ex notify called");
				}
			}	
		}
		
	}
	
	
	class HelpButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
		         FileReader fileReader = new FileReader("src/assemblyPrograms/CPUsim User Manual.txt");
		         JTextArea helpPane = new JTextArea();
		         helpPane.setEditable(false);
		         helpPane.setLineWrap(true); //Wrap lines to fit display area
		         helpPane.setWrapStyleWord(true); //Wrap lines on word boundaries
		         helpPane.read(fileReader, null); //Read the text file
		         
		         fileReader.close();
		         JScrollPane helpScroller = new JScrollPane(helpPane);
		         helpScroller.setPreferredSize(new Dimension(600, 600));
		      
		         JOptionPane.showMessageDialog(null, helpScroller, "User Manual", JOptionPane.INFORMATION_MESSAGE);
            }
            catch (IOException iox) {
                System.err.println(iox);
             }
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



	public JTextField getMARfield() {
		return this.marField;
	}
	
	public JTextField getMBRfield() {
		return this.mbrField;
	}
	
	public JTextField getGenPurposeRegister(int index) {
		return this.genRegisters[index];
	}
	
	public JTextField getStatusRegisterField() {
		return this.statusField;
	}
	
	/*
	 * Rather than 12 individual getters for ALU text fields, it is more concise to
	 * add the fields to arrays for handling by UpdateListener. 
	 */
	
	public JTextField[] getAddFields() {
		JTextField[] addFields = new JTextField[3];
		addFields[0] = addOperand1;
		addFields[1] = addOperand2;
		addFields[2] = addResult;
		
		return addFields;
	}
	
	public JTextField[] getSubFields() {
		JTextField[] subFields = new JTextField[3];
		subFields[0] = subOperand1;
		subFields[1] = subOperand2;
		subFields[2] = subResult;
		
		return subFields;
	}
	
	public JTextField[] getDivFields() {
		JTextField[] divFields = new JTextField[3];
		divFields[0] = divOperand1;
		divFields[1] = divOperand2;
		divFields[2] = divResult;
		
		return divFields;
	}
	
	public JTextField[] getMulFields() {
		JTextField[] mulFields = new JTextField[3];
		mulFields[0] = mulOperand1;
		mulFields[1] = mulOperand2;
		mulFields[2] = mulResult;
		
		return mulFields;
	}
	
	public JTextField[] getAllAluFields() { //Used for reset (called by UpdateListener)
		JTextField[] aluFields = new JTextField[12];
		aluFields[0] = addOperand1;
		aluFields[1] = addOperand2;
		aluFields[2] = addResult;
		aluFields[3] = subOperand1;
		aluFields[4] = subOperand2;
		aluFields[5] = subResult;
		aluFields[6] = divOperand1;
		aluFields[7] = divOperand2;
		aluFields[8] = divResult;
		aluFields[9] = mulOperand1;
		aluFields[10] = mulOperand2;
		aluFields[11] = mulResult;
		
		return aluFields;
	}
	
	public JTextArea getActivityMonitor() {		
		return this.activityArea;		
	}
	
	public JTextField getAddressBusField() {
		return this.addressBusField;
	}



	public JTextField getDataBusField() {
		return this.dataBusField;
	}



	public JTextField getControlLineField() {
		return this.controlLineField;
	}

	
	

		
		
	
	

}
