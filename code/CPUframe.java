package code;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


public class CPUframe extends JFrame {
	private ControlUnit controlUnit;
	private MainMemory memory;
	
	private static final int FRAME_WIDTH = 700;
	private static final int FRAME_HEIGHT = 400;
	
	private JPanel controlPanel;
	private JPanel memoryPanel;
	private JLabel memoryLabel;
	private JButton executeButton;
	private JButton resetButton;
	private JScrollPane scroller;
	
	public CPUframe(ControlUnit controlUnit, MainMemory memory) {
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
	
		scroller = new JScrollPane(memoryPanel);		
		this.getContentPane().add(scroller, BorderLayout.EAST); 
		
		executeButton = new JButton("Execute Program");
		executeButton.addActionListener(new ExecuteListener());
		controlPanel.add(executeButton);
		resetButton = new JButton("Reset");
		resetButton.addActionListener(new ResetListener());
		controlPanel.add(resetButton);
		this.getContentPane().add(controlPanel);
		
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

}
