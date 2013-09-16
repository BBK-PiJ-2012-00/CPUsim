package tests;

import code.CPUframe;

@SuppressWarnings("serial")
public class TestFrame extends CPUframe {
	
	public TestFrame() {
		//Do nothing; a dummy object for testing
	}
	
	public TestFrame(boolean pipelined) { //For IRfile/IR; created as non-pipelined by default, meaning single IR created
		//When testing IRfile, this causes index out of bounds exceptions. Solved by setting pipelining to true and redrawing
		//the second panel, which will prompt instantiation of the extra JTextFields required for the IRfile.
		setPipeliningEnabled(true);
		drawPanel2();

	}

}
