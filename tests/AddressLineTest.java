package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import code.AddressLineImpl;
import code.AddressLine;

public class AddressLineTest {
	AddressLine aLine = new AddressLineImpl();
	
	@Test
	public void readTest() {
		assertEquals(aLine.read(), 0); //Should be 0 as address field is uninitialised and int has default value of 0
	}
	
	@Test
	public void parameterizedPutTest() {
		aLine.put(10);
		int expected = 10;
		int output = aLine.read();
		assertEquals(expected, output);
	}
	

}
