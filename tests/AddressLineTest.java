package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import code.AddressLineImpl;
import code.AddressLine;

public class AddressLineTest {
	AddressLine aLine = new AddressLineImpl();
	
	@Test
	public void readTest() {
		assertEquals(aLine.read(), -1); //Should be 0 as address field is uninitialised and has default value of -1
	}
	
	@Test
	public void parameterizedPutTest() {
		aLine.put(10);
		int expected = 10;
		int output = aLine.read();
		assertEquals(expected, output);
	}
	

}
