package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import code.Data;

public class RegisterFile16Test {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}
	
//	@Override
//	public void write(int index, Data data) {
//		if (index > -1 && index < 16) { //Ensure valid index (0-15)
//			generalPurposeRegisters[index] = data;
//		}
//		//Otherwise do nothing (throw exception?)
//	}
//	@Override
//	public Data read(int index) {
//		if (index < -1 && index < 16) { //Ensure valid index (0-15)
//			return generalPurposeRegisters[index];
//		}
//		//Throw exception?
//		return null;
//	}

}
