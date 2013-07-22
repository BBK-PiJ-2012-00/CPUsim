package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import code.AddressBusImpl;
import code.AddressBus;

public class AddressBusTest {
	AddressBus aBus = new AddressBusImpl();
	
	@Test
	public void readTest() {
		assertEquals(aBus.read(), -1); //Should be 0 as address field is uninitialised and has default value of -1
	}
	
	@Test
	public void parameterizedPutTest() {
		aBus.put(10);
		int expected = 10;
		int output = aBus.read();
		assertEquals(expected, output);
	}
	

}
