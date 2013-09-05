package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import code.AddressBusImpl;
import code.AddressBus;
import code.UpdateListener;

public class AddressBusTest {
	AddressBus aBus;
	
	@Before
	public void setUp() {
		aBus = new AddressBusImpl();
		aBus.registerListener(new UpdateListener(new TestFrame())); //Dummy UpdateListener to prevent null pointer exceptions		
	}
	
	
	@Test
	public void readTest() {
		assertEquals(aBus.read(), -1); //Should be 0 as address field is uninitialised and has default value of -1
	}
	
	@Test
	public void putTest() {
		aBus.put(10);
		int expected = 10;
		int output = aBus.read();
		assertEquals(expected, output);
	}
	

}
