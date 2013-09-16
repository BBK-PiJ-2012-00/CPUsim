package code;

import java.awt.Color;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.filechooser.FileFilter;


@SuppressWarnings("serial")
public class CPUframe extends JFrame {
	
	private ControlUnit controlUnit;
	private MainMemory memory;
	private Assembler assembler;
	private Loader loader;
	private BusController systemBusController;
	
	private boolean pipeliningEnabled;
	
	private File currentAssemblyFile; //To hold a reference to the current assembly program to facilitate restarting the program.
	
	private SwingWorker<Void, Void> executionWorker; //Reference to SwingWorker thread, allowing for it to be cancelled.
	
	private JFileChooser fileChooser;
	
	//The GUI window is divided into four panels, arranged as columns
	private JPanel panel1;
	private JPanel panel2;
	private JPanel panel3;
	private JPanel panel4;
	
	private JPanel registerPanel;
		
	private JTextArea memoryContentArea;
	private JTextArea assemblyProgramArea; 
	
	private JTextArea activityArea; //For standard and pipelined modes	
	private JTextArea activityArea1; //For pipelined mode (execute stage activity monitor)
	private JTextArea activityArea2; //For pipelined mode (write back stage activity monitor)
	
	private JTextField pcField;
	private JTextField[] irRegisters; //Index 0 used for standard IR, all three for IRfile in pipelined mode.
	private JTextField conditionField;
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
	
	private JButton fileOpenButton;
	private JButton modeSwitchButton;
	
	private Border magentaLine; //Coloured borders for MAR, MBR, address and data bus displays.
	private Border cyanLine;
	
	
	public CPUframe() {	
		this.setTitle("CPUsim");
		
		CPUbuilder cpuBuilder = new CPUbuilder(false); //Create CPU components, standard mode is default
		
		this.controlUnit = cpuBuilder.getControlUnit();
		this.memory = cpuBuilder.getMemoryModule();
		this.loader = cpuBuilder.getLoader();
		this.systemBusController = cpuBuilder.getBusController();	
		
		createComponents();				
	}
	
	
	
	private void createComponents() {	
		
		//Arrange panels horizontally
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS)); 
		
		//Draw the four panels and their contents
		drawPanel1();
		drawPanel2();
		drawPanel3();
		drawPanel4();
	}
	

		
	/*
	 * Panel 1: assembler and activity monitor
	 */
	public void drawPanel1() {
		
		panel1 = new JPanel();
		panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
		panel1.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
		panel1.setMinimumSize(new Dimension(340, 800));


		/*
		 * Assembler display in panel 1
		 */
		JPanel assemblerBufferPanel = new JPanel(); //To make border equal to controlPanel above
		JPanel assemblerContentPanel = new JPanel();
		
		if (!pipeliningEnabled) {
			assemblyProgramArea = new JTextArea(20, 32);
		}
		else { //Slightly smaller assembly program area for pipelined mode as more content must fit in window
			assemblyProgramArea = new JTextArea(17, 32); 
		}
		
		assemblyProgramArea.setEditable(false);
		assemblyProgramArea.setCaretPosition(0);
		
		JScrollPane assemblerScroller = new JScrollPane(assemblyProgramArea);		
		assemblerBufferPanel.add(assemblerScroller);		
		assemblerContentPanel.add(assemblerBufferPanel);
		
		JPanel assemblerPanel = new JPanel();		
		assemblerPanel.add(assemblerContentPanel);			
		assemblerContentPanel.setBorder(BorderFactory.createTitledBorder(" Assembly Program "));
		
		panel1.add(assemblerPanel);		
	
		
		/*
		 * Activity Monitor display in panel1
		 */
		JPanel activityBufferPanel = new JPanel(); //To make border equal to controlPanel and assembly above
		JPanel activityContentPanel = new JPanel();
		
		if (!pipeliningEnabled) { //One large activity monitor in standard mode
			activityArea = new JTextArea(18, 32);
			activityArea.setEditable(false);
			JScrollPane activityScroller = new JScrollPane(activityArea);
			
			activityBufferPanel.add(activityScroller);			
			activityContentPanel.add(activityBufferPanel);
			
			JPanel activityPanel = new JPanel();
			activityPanel.add(activityContentPanel);			
			
			activityContentPanel.setBorder(BorderFactory.createTitledBorder(" Activity Monitor "));
			
			panel1.add(activityPanel);
		
		}
		
		else { //Pipelining enabled, create three smaller activity monitors specific to each stage
			
			JPanel activityBufferPanel1 = new JPanel();
			JPanel activityBufferPanel2 = new JPanel();
			
			activityContentPanel.setLayout(new BoxLayout(activityContentPanel, BoxLayout.Y_AXIS));
			
			//Activity area for fetch/decode stage
			activityArea = new JTextArea(7, 32);
			activityArea.setEditable(false);		
			JScrollPane activityScroller = new JScrollPane(activityArea);
			
			//Activity area for execute stage
			activityArea1 = new JTextArea(7, 32);
			activityArea1.setEditable(false);
			JScrollPane activityScroller1 = new JScrollPane(activityArea1);
			
			//Activity area for writeBackStage stage
			activityArea2 = new JTextArea(3, 32);
			activityArea2.setEditable(false);
			JScrollPane activityScroller2 = new JScrollPane(activityArea2);			
			
			
			activityBufferPanel.add(activityScroller);
			activityBufferPanel1.add(activityScroller1);
			activityBufferPanel2.add(activityScroller2);
			
			activityBufferPanel.setBorder(BorderFactory.createTitledBorder(" Fetch/Decode Stage Activity "));
			activityBufferPanel1.setBorder(BorderFactory.createTitledBorder(" Execute Stage Activity "));
			activityBufferPanel2.setBorder(BorderFactory.createTitledBorder(" Write Back Stage Activity "));
			
			activityContentPanel.add(activityBufferPanel);
			activityContentPanel.add(activityBufferPanel1);
			activityContentPanel.add(activityBufferPanel2);
			
			JPanel activityPanel = new JPanel();
			activityPanel.add(activityContentPanel);
			
			panel1.add(activityPanel);
			
		}
		
		controlUnit.getFetchDecodeStage().registerListener(new UpdateListener(this)); //Register a listener with FD stage
		controlUnit.getExecuteStage().registerListener(new UpdateListener(this)); //Register a listener with Ex. stage
		controlUnit.getWriteBackStage().registerListener(new UpdateListener(this)); //Register a listener with WB stage		
		
		
		this.getContentPane().add(panel1);		
	}
	
		
		
	/*
	 * Panel 2 contains CPU and ALU displays.	
	 */
	public void drawPanel2() {
		
		panel2 = new JPanel();
		panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
		
		registerPanel = new JPanel();
		registerPanel.setMaximumSize(new Dimension(400, 400));
		registerPanel.setAlignmentX(CENTER_ALIGNMENT);
		registerPanel.setAlignmentY(TOP_ALIGNMENT);
		registerPanel.setBorder(BorderFactory.createTitledBorder(" CPU Registers "));
		registerPanel.setLayout(new BoxLayout(registerPanel, BoxLayout.Y_AXIS));		
				
		JPanel topRegisterPanel = new JPanel();
		topRegisterPanel.setLayout(new BoxLayout(topRegisterPanel, BoxLayout.X_AXIS));
		
		JPanel controlRegistersPanel1 = new JPanel();		
		controlRegistersPanel1.setLayout(new BoxLayout(controlRegistersPanel1, BoxLayout.Y_AXIS));
		
		pcField = new JTextField(9);		
		pcField.setEditable(false);
		pcField.setAlignmentX(CENTER_ALIGNMENT);
		pcField.setAlignmentY(CENTER_ALIGNMENT);
		JPanel pcPanel = new JPanel();
		pcPanel.setMaximumSize(new Dimension(155, 60));
		pcPanel.add(pcField);
		pcPanel.setBorder(BorderFactory.createTitledBorder(" PC "));
		
		controlUnit.getPC().registerListener(new UpdateListener(this));
		
		JPanel irPanel = new JPanel();
		
		if (!pipeliningEnabled) { //In standard mode, just a single IR
			irRegisters = new JTextField[1];
			irRegisters[0] = new JTextField(8);
			irRegisters[0].setEditable(false);
			irPanel.setMaximumSize(new Dimension(155, 60));
			irPanel.add(irRegisters[0]);
			irPanel.setBorder(BorderFactory.createTitledBorder(" IR "));
		}
		
		else { //Pipelined mode requires an IRfile
			irPanel.setLayout(new BoxLayout(irPanel, BoxLayout.Y_AXIS));			
			irRegisters = new JTextField[3];
			
			JPanel ir0Panel = new JPanel();
			ir0Panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 2, 0));
			irRegisters[0] = new JTextField(9);
			irRegisters[0].setEditable(false);
			JLabel ir0Label = new JLabel("F/D  ");
			ir0Panel.setMaximumSize(new Dimension(155, 50));
			ir0Panel.setLayout(new BoxLayout(ir0Panel, BoxLayout.X_AXIS));
			ir0Panel.add(ir0Label);
			ir0Panel.add(irRegisters[0]);
			
			JPanel ir1Panel = new JPanel();
			irRegisters[1] = new JTextField(9);
			irRegisters[1].setEditable(false);
			JLabel ir1Label = new JLabel("Ex.   ");
			ir1Panel.setMaximumSize(new Dimension(155, 50));
			ir1Panel.setLayout(new BoxLayout(ir1Panel, BoxLayout.X_AXIS));
			ir1Panel.add(ir1Label);
			ir1Panel.add(irRegisters[1]);
			
			JPanel ir2Panel = new JPanel();
			ir2Panel.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 2));
			irRegisters[2] = new JTextField(9);
			irRegisters[2].setEditable(false);
			JLabel ir2Label = new JLabel("WB   ");
			ir2Panel.setMaximumSize(new Dimension(155, 50));
			ir2Panel.setLayout(new BoxLayout(ir2Panel, BoxLayout.X_AXIS));
			ir2Panel.add(ir2Label);
			ir2Panel.add(irRegisters[2]);			
			
			
			irPanel.setBorder(BorderFactory.createTitledBorder(" IR File "));
			irPanel.setMaximumSize(new Dimension(155, 115));
			
			
			irPanel.add(ir0Panel);
			irPanel.add(ir1Panel);
			irPanel.add(ir2Panel);
			
		}
		
		controlUnit.getIR().registerListener(new UpdateListener(this));
		
		//Add PC and IR displays
		controlRegistersPanel1.add(pcPanel);
		controlRegistersPanel1.add(irPanel);
		controlRegistersPanel1.setBorder(BorderFactory.createEmptyBorder(0, 10, 30, 20));
		
		//MAR, MBR and CC displays
		JPanel controlRegistersPanel2 = new JPanel();
		controlRegistersPanel2.setLayout(new BoxLayout(controlRegistersPanel2, BoxLayout.Y_AXIS));
		
		marField = new JTextField(8);
		marField.setEditable(false);		
		JPanel marPanel = new JPanel();
		marPanel.setMaximumSize(new Dimension(150, 60));
		marPanel.add(marField);
		cyanLine = BorderFactory.createLineBorder(Color.blue);
		marPanel.setBorder(BorderFactory.createTitledBorder(cyanLine, " MAR "));
		controlUnit.getMAR().registerListener(new UpdateListener(this));
		
		mbrField = new JTextField(8);
		mbrField.setEditable(false);
		JPanel mbrPanel = new JPanel();
		mbrPanel.setMaximumSize(new Dimension(150, 60));
		mbrPanel.add(mbrField);
		magentaLine = BorderFactory.createLineBorder(Color.magenta);
		mbrPanel.setBorder(BorderFactory.createTitledBorder(magentaLine, " MBR "));
		controlUnit.getMBR().registerListener(new UpdateListener(this));
		
		conditionField = new JTextField(8);
		conditionField.setEditable(false);
		JPanel conditionPanel = new JPanel();
		conditionPanel.setMaximumSize(new Dimension(150, 60));
		conditionPanel.add(conditionField);
		conditionPanel.setBorder(BorderFactory.createTitledBorder(" CC "));
		
		controlUnit.getConditionCodeRegister().registerListener(new UpdateListener(this));
		
		controlRegistersPanel2.add(marPanel);
		controlRegistersPanel2.add(mbrPanel);
		controlRegistersPanel2.add(conditionPanel);
		controlRegistersPanel2.setBorder(BorderFactory.createEmptyBorder(0, 15, 30, 0));
		
		topRegisterPanel.add(controlRegistersPanel1);
		topRegisterPanel.add(controlRegistersPanel2);
		
		registerPanel.add(topRegisterPanel);
		
		
		//General purpose registers
		JPanel generalPurposePanel = new JPanel();
		generalPurposePanel.setLayout(new BoxLayout(generalPurposePanel, BoxLayout.X_AXIS));
		generalPurposePanel.setMaximumSize(new Dimension(400, 300));
		generalPurposePanel.setBorder(BorderFactory.createTitledBorder(" General Purpose Registers "));
		
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
		
		controlUnit.getRegisters().registerListener(new UpdateListener(this));
		
		
		/*
		 * ALU display
		 */		
		JPanel aluPanel = new JPanel(); //Panel to encase the two subpanels
		aluPanel.setLayout(new BoxLayout(aluPanel, BoxLayout.X_AXIS));
		aluPanel.setBorder(BorderFactory.createTitledBorder(" ALU "));
		
		JPanel aluSubPanel1 = new JPanel();
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
		
		JPanel aluAdderPanel = new JPanel();
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
		
		JPanel aluSubtractorPanel = new JPanel();
		aluSubtractorPanel.setLayout(new BoxLayout(aluSubtractorPanel, BoxLayout.Y_AXIS));
		aluSubtractorPanel.add(subOperand1Panel);
		aluSubtractorPanel.add(subOperand2Panel);
		aluSubtractorPanel.add(subResultPanel);
		aluSubtractorPanel.setBorder(BorderFactory.createTitledBorder("Sub. Unit"));
		
		aluSubPanel1.add(aluAdderPanel);
		aluSubPanel1.add(aluSubtractorPanel);
		
		
		/*
		 * For aluSubPanel2, containing mul and div units
		 */
		
		JPanel aluSubPanel2 = new JPanel();
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
		
		JPanel aluDivPanel = new JPanel();
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
		
		JPanel aluMulPanel = new JPanel();
		aluMulPanel.setLayout(new BoxLayout(aluMulPanel, BoxLayout.Y_AXIS));
		aluMulPanel.add(mulOperand1Panel);
		aluMulPanel.add(mulOperand2Panel);
		aluMulPanel.add(mulResultPanel);
		aluMulPanel.setBorder(BorderFactory.createTitledBorder("Mul. Unit"));
		
		aluSubPanel2.add(aluDivPanel);
		aluSubPanel2.add(aluMulPanel);
		
		aluPanel.add(aluSubPanel1);
		aluPanel.add(aluSubPanel2);
		aluPanel.setPreferredSize(new Dimension(400, 290));
		aluPanel.setMaximumSize(new Dimension(400, 290));
		
		JPanel bufferPanel = new JPanel(); //To separate registers from ALU with some empty space
		bufferPanel.setPreferredSize(new Dimension(400, 35));
		bufferPanel.setMaximumSize(new Dimension(400, 35));
		
		panel2.add(bufferPanel);		
		panel2.add(aluPanel);		
		
		panel2.setBorder(BorderFactory.createEmptyBorder(15, 5, 20, 5));
		
		ALU.registerListener(new UpdateListener(this));
		
		this.getContentPane().add(panel2);
		
	}
		
	
	//Panel 3 contains system bus and control button panel
	public void drawPanel3() {
		panel3 = new JPanel();
		
		JPanel systemBusPanel = new JPanel();
		systemBusPanel.setLayout(new BoxLayout(systemBusPanel, BoxLayout.Y_AXIS));//Add bus panels vertically
		
		JPanel addressBusPanel = new JPanel();
		addressBusPanel.setPreferredSize(new Dimension(175, 60));
		addressBusPanel.setBorder(BorderFactory.createTitledBorder(cyanLine, "Address Bus"));
		addressBusField = new JTextField(8);
		addressBusField.setEditable(false);
		addressBusPanel.add(addressBusField);
		systemBusPanel.add(addressBusPanel);
		
		systemBusController.accessControlLine().getAddressBus().registerListener(new UpdateListener(this));
		
		JPanel dataBusPanel = new JPanel();
		dataBusPanel.setPreferredSize(new Dimension(175, 60));
		dataBusPanel.setBorder(BorderFactory.createTitledBorder(magentaLine, "Data Bus"));
		dataBusField = new JTextField(8);
		dataBusField.setEditable(false);
		dataBusPanel.add(dataBusField);
		systemBusPanel.add(dataBusPanel);
		
		systemBusController.accessControlLine().getDataBus().registerListener(new UpdateListener(this));
		
		JPanel controlLinePanel = new JPanel();
		controlLinePanel.setPreferredSize(new Dimension(175, 60));
		controlLinePanel.setBorder(BorderFactory.createTitledBorder("Control Line"));
		controlLineField = new JTextField(8);
		controlLineField.setEditable(false);
		controlLinePanel.add(controlLineField);
		systemBusPanel.add(controlLinePanel);
		
		systemBusController.accessControlLine().registerListener(new UpdateListener(this));
		
		systemBusPanel.setBorder(BorderFactory.createTitledBorder(" System Bus "));
		
		
		panel3.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 10));
		
		
		panel3.add(systemBusPanel);
		
		JPanel panel3BufferPanel = new JPanel();
		panel3BufferPanel.setPreferredSize(new Dimension(150, 90));
		panel3.add(panel3BufferPanel); //To separate system bus and control panel displays
		
		
		/*
		 * Control panel
		 */
		JPanel controlPanel = new JPanel(); //Container panel for all control buttons
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
		controlPanel.setMaximumSize(new Dimension(200, 250));
		
		JPanel subControlPanel1 = new JPanel();
		subControlPanel1.setLayout(new BoxLayout(subControlPanel1, BoxLayout.X_AXIS));
		subControlPanel1.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		
		JPanel subControlPanel2 = new JPanel();
		subControlPanel2.setLayout(new BoxLayout(subControlPanel2, BoxLayout.X_AXIS));
		
		JPanel subControlPanel3 = new JPanel();
		subControlPanel3.setLayout(new BoxLayout(subControlPanel3, BoxLayout.X_AXIS));
		subControlPanel3.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		
		JPanel subControlPanel4 = new JPanel();
		subControlPanel4.setLayout(new BoxLayout(subControlPanel4, BoxLayout.X_AXIS));
		
		JPanel subControlPanel5 = new JPanel();
		subControlPanel5.setLayout(new BoxLayout(subControlPanel5, BoxLayout.X_AXIS));
		subControlPanel5.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		
		fileOpenButton = new JButton("Select Assembly File");
		fileOpenButton.addActionListener(new FileOpenListener());
		subControlPanel1.add(fileOpenButton);
		controlPanel.add(subControlPanel1);		
		
		fileChooser = new JFileChooser("src/assemblyPrograms");	
		
		
		JButton executeButton = new JButton("Execute Program");
		executeButton.addActionListener(new ExecuteListener());
		subControlPanel2.add(executeButton);
		controlPanel.add(subControlPanel2);
		
		JButton stepButton = new JButton("Step");
		stepButton.addActionListener(new StepExecutionListener());
		subControlPanel3.add(stepButton);
		controlPanel.add(subControlPanel3);

		
		//Adjust mode switch button labels depending on mode of execution
		if (!pipeliningEnabled) {
			modeSwitchButton = new JButton("Pipelined Mode");
		}
		else {
			modeSwitchButton = new JButton("Standard Mode");
		}
		modeSwitchButton.addActionListener(new ModeSwitchListener());
		subControlPanel4.add(modeSwitchButton);	
		controlPanel.add(subControlPanel4);
	
		
		JButton resetButton = new JButton("Reset");
		resetButton.addActionListener(new ResetListener());
		subControlPanel4.add(resetButton);
		subControlPanel5.add(resetButton);	
		
		JButton helpButton = new JButton("Help!");
		helpButton.addActionListener(new HelpButtonListener());		
		subControlPanel4.add(helpButton);
		subControlPanel5.add(helpButton);	
		
		controlPanel.add(subControlPanel5);
		
		//Border formatting for button control panel
		Border controlPanelLine = BorderFactory.createLineBorder(Color.BLUE, 3);
		Font controlPanelFont = new Font("default", Font.ITALIC, 15);
		controlPanel.setBorder(BorderFactory.createTitledBorder(controlPanelLine, "* Control Panel *",0, 0, controlPanelFont));		
		
	
		panel3.add(controlPanel);
		panel3.setPreferredSize(new Dimension(220, 800));
		panel3.setMaximumSize(new Dimension(220, 800));
		
		
		JPanel imagePanel = new JPanel();
		ImageIcon logo = new ImageIcon("src/cpuSimLogoMagentaSmall.jpg");
		JLabel logoLabel = new JLabel("", logo, JLabel.CENTER);
		imagePanel.add(logoLabel);
		panel3.add(imagePanel);
		
		this.getContentPane().add(panel3);
		
	}
	
	
	//Panel 4 contains main memory display
	public void drawPanel4() {
		
		panel4 = new JPanel();
		panel4.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
			
		JPanel memoryContentsPanel = new JPanel(); //A panel to store the scrollpane and text area for memory display
		memoryContentsPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
		
		memoryContentArea = new JTextArea(35, 12);
		memoryContentArea.setText(memory.display()); //Text area for memory display
		memoryContentArea.setCaretPosition(0);
		memoryContentArea.setEditable(false); //Memory display is not editable
		
		JScrollPane scroller = new JScrollPane(memoryContentArea);		
		
		memoryContentsPanel.add(scroller); //Add text area to memory panel
	
		UpdateListener memoryListener = new UpdateListener(this);
		memory.registerListener(memoryListener);

		JPanel memoryPanel = new JPanel(); //Main panel for all memory related items
		memoryPanel.setLayout(new BoxLayout(memoryPanel, BoxLayout.Y_AXIS));
		memoryPanel.add(memoryContentsPanel);
		
		memoryPanel.setBorder(BorderFactory.createTitledBorder(" Main Memory "));
		
		panel4.add(memoryPanel);
		
		this.getContentPane().add(panel4);
	
	}
	


	
	/*
	 * Event listening inner classes
	 */

	class ExecutionWorker extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() throws Exception {
			try {
				controlUnit.activate();	
			}
			catch (NullPointerException e) {
				JOptionPane.showMessageDialog(null, "Assembly program logic error! Please ensure program logic is sound.");
			}
			catch (ClassCastException e) {
				JOptionPane.showMessageDialog(null, "Assembly program logic error! Please ensure program logic is sound.");
			}
				
			return null;
		}
		
	}
	
	
	class ExecuteListener implements ActionListener { //Execute Program button

		@Override
		public void actionPerformed(ActionEvent e) {
			if (currentAssemblyFile != null) { //There must be a file to execute
				
				if (executionWorker == null) { //Only create worker thread if the current one is null (to avoid several threads).;
					executionWorker = new ExecutionWorker();
					executionWorker.execute();
					
				}
				else {
				//	System.out.println("Execution worker isn't null, so nothing created.");
				}
				
			}
			else {
				JLabel noFileErrorMessage = new JLabel("Please select an assembly file first!");
				JOptionPane.showMessageDialog(null, noFileErrorMessage, "Error!", JOptionPane.WARNING_MESSAGE);
			}
			
		}
		
	}
	
	class ResetListener implements ActionListener { //Reset button
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			try {
				executionWorker.cancel(true); //Old worker thread needs terminating
				executionWorker = null;
				
				//Clear fields				
				memory.clearMemory();
	            activityArea.setText("");
	            controlUnit.clearRegisters(); 
	            systemBusController.accessControlLine().clear();
	            ALU.clearFields();
	            if (pipeliningEnabled) {
	            	activityArea1.setText("");
	            	activityArea2.setText("");
	            	controlUnit.getExecuteStage().setActive(false); //Signal SwingWorker to stop if blocked on accessMemory()
	            }
				
	           
	            assembler = new AssemblerImpl(loader);
	            assembler.selectFile(currentAssemblyFile); //Reopen previously selected assembly file
	    		assembler.assembleCode();
	    		assembler.loadToLoader();
	    		assembler.getLoader().loadToMemory();
	    		memoryContentArea.setCaretPosition(0); //Scrolls text area to top
	    		
	    		assemblyProgramArea.setText(assembler.display());
	    		assemblyProgramArea.setCaretPosition(0);
			}
			catch (NullPointerException nfe) { //If reset is pressed not during execution
				JOptionPane.showMessageDialog(null, "Please select an assembly file first!", "!", JOptionPane.WARNING_MESSAGE);
			}
		}
	}
	
	
	
	class FileOpenListener implements ActionListener {  //Select Assembly File button
		
		@Override
		public void actionPerformed(ActionEvent e) {
		    //Handle open button action.
		    if (e.getSource() == fileOpenButton) {
	
		    	//Restricts file chooser to accepting text files and directories only
		    	fileChooser.setAcceptAllFileFilterUsed(false);
		    	FileFilter txtFilter = new TextFileFilter();
		    	fileChooser.setFileFilter(txtFilter);	
		    	fileChooser.setCurrentDirectory(new File("./assemblyPrograms"));
		    	
		        int returnVal = fileChooser.showOpenDialog(fileOpenButton);

		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            currentAssemblyFile = fileChooser.getSelectedFile();		           
	            
		            memory.clearMemory();
		            activityArea.setText("");
		            ALU.clearFields();
		            if (pipeliningEnabled) { //Clear additional activity monitors
		            	activityArea1.setText("");
		            	activityArea2.setText("");
		            }
		            controlUnit.clearRegisters();
		           
		            assembler = new AssemblerImpl(loader);
		            assembler.selectFile(currentAssemblyFile);
		    		boolean fileAcceptable = assembler.assembleCode();
		    		
		    		if (fileAcceptable) {	 //Only load if the file is found / valid assembly code    		
			    		assembler.loadToLoader();
			    		assembler.getLoader().loadToMemory();
			    		memoryContentArea.setText(memory.display()); //update memory display on opening file
			    		memoryContentArea.setCaretPosition(0); //Scrolls text area to top
			    		
			    		assemblyProgramArea.setText(assembler.display());
			    		assemblyProgramArea.setCaretPosition(0);
			    		
			    		if (executionWorker != null) { //Cancel and nullify execution worker if it's active
				    		executionWorker.cancel(true);
				    		executionWorker = null;
			    		}
		    		}
		    		else {
		    			currentAssemblyFile = null; //Prevent execution from being allowed if file not found	    			
		    		}
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
			
			//Notify every object which has a waiting thread
			else {
				if (systemBusController.accessControlLine().isWaiting()) {
					synchronized(systemBusController.accessControlLine()) {
						systemBusController.accessControlLine().notifyAll();
					}
				}
				if (controlUnit.getFetchDecodeStage().isWaiting()) {
					synchronized(controlUnit.getFetchDecodeStage()) {
						controlUnit.getFetchDecodeStage().notifyAll();
					}
				}
				if (controlUnit.getExecuteStage().isWaiting()) {
					synchronized(controlUnit.getExecuteStage()) {
						controlUnit.getExecuteStage().notifyAll();
					}
				}
				if (controlUnit.getWriteBackStage().isWaiting()) {
					synchronized(controlUnit.getWriteBackStage()) {
						controlUnit.getWriteBackStage().notifyAll();
					}
				}
			}
		}
		
	}
	
	
	class HelpButtonListener implements ActionListener { //Help button

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
//		         FileReader fileReader = new FileReader("src/assemblyPrograms/CPUsim User Manual.txt");
				FileReader fileReader = new FileReader("./assemblyPrograms/CPUsim User Manual.txt");
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
	
	
	class ModeSwitchListener implements ActionListener { //Mode switch button

		@Override
		public void actionPerformed(ActionEvent e) {
			int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to switch execution mode?" +
					"\nAny assembly language progam progress will be lost.", "Switch Modes?", JOptionPane.OK_CANCEL_OPTION);
			if (option == JOptionPane.OK_OPTION) { //If ok is pressed
				
				if (!pipeliningEnabled) {
					pipeliningEnabled = true;			
					
					CPUbuilder cpuBuilder = new CPUbuilder(true); //Create pipelined components
					
					//Reassign component fields
					CPUframe.this.controlUnit = cpuBuilder.getControlUnit(); //Get pipelined control unit
					CPUframe.this.memory = cpuBuilder.getMemoryModule();
					CPUframe.this.loader = cpuBuilder.getLoader();
					CPUframe.this.systemBusController = cpuBuilder.getBusController();	
					CPUframe.this.executionWorker = null;
				
					//Remove panels for standard mode and redraw for pipelined mode (now that pipeliningEnabled is true)
					getContentPane().remove(panel1);
					getContentPane().remove(panel2);
					getContentPane().remove(panel3);
					getContentPane().remove(panel4);
					drawPanel1();
					drawPanel2();
					drawPanel3();
					drawPanel4();
					revalidate();
					
				}
				
				else { //Switching back to standard from pipelining mode
					
					pipeliningEnabled = false;
					
					CPUbuilder cpuBuilder = new CPUbuilder(false); //Create standard components
					
					//Reassign component fields
					CPUframe.this.controlUnit = cpuBuilder.getControlUnit(); //Get standard control unit
					CPUframe.this.memory = cpuBuilder.getMemoryModule();
					CPUframe.this.loader = cpuBuilder.getLoader();
					CPUframe.this.systemBusController = cpuBuilder.getBusController();	
					CPUframe.this.executionWorker = null;
					
					//Redraw components for standard mode
					getContentPane().remove(panel1);
					getContentPane().remove(panel2);
					getContentPane().remove(panel3);
					getContentPane().remove(panel4);
					drawPanel1();
					drawPanel2();
					drawPanel3();
					drawPanel4();
					revalidate();					
					
				}				
			}
			
		}		
	}
		
	
	/*
	 * Below are getter methods for the display fields of the GUI, to which acess is required
	 * by the UpdateListener class.
	 */
	
	public JTextField getPCfield() {
		return this.pcField;
	}


	public JTextField getIRfield(int index) {
		return this.irRegisters[index];
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
	
	public JTextField getConditionCodeField() {
		return this.conditionField;
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
	
	public JTextArea getActivityMonitor1() { //For pipelined execute stage updates
		return this.activityArea1;
	}
	
	public JTextArea getActivityMonitor2() { //For pipelined write back stage updates
		return this.activityArea2;
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
	
	protected void setPipeliningEnabled(boolean enabled) { //For use by TestFrame subclass during unit testing
		pipeliningEnabled = enabled;
	}

	

}
