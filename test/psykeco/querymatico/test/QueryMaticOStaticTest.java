package psykeco.querymatico.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static psykeco.querymatico.sql.utility.SQLClassParser.validateBase;
import static psykeco.querymatico.sql.utility.SQLClassParser.validateValue;

import org.junit.jupiter.api.Test;

class QueryMaticOStaticTest {

	@Test
	void validateBaseTest() {
		String expected="l``ammore e l``odio";
		String input="l`ammore e l`odio";
		String actual=validateBase(input);
		assertEquals(expected, actual);
	}
	
	@Test
	void validateValueTest() {
		String expected="NOI SIAM L''ESERCITOO.		 L''ESERCITO DEL SURF.";
		String input="NOI SIAM L'ESERCITOO.		 L'ESERCITO DEL SURF.";
		String actual=validateValue(input);
		assertEquals(expected, actual);
	}
	
	@Test
	void validateBaseTest0() {
		String input="l`ammore e"+((char)0)+" l`odio";
		System.out.println(input);
		String actual=validateBase(input);
		assertNull(actual);
	}
	
	@Test
	void validateBaseTest8() {
		String input="l`ammore e"+((char)8)+" l`odio";
		System.out.println(input);
		String actual=validateBase(input);
		assertNull(actual);
	}
	
	@Test
	void validateBaseTest13() {
		String input="l`ammore e"+((char)13)+" l`odio";
		System.out.println(input);
		String actual=validateBase(input);
		assertNull(actual);
	}
	
	@Test
	void validateBaseTest26() {
		String input="l`ammore e"+((char)26)+" l`odio";
		System.out.println(input);
		String actual=validateBase(input);
		assertNull(actual);
	}
	
	@Test
	void validateBaseTest92() {
		String input="l`ammore e"+((char)92)+" l`odio";
		String expected="l``ammore e"+((char)92)+" l``odio";
		System.out.println(input);
		String actual=validateBase(input);
		assertEquals(expected, actual);
	}
	
	@Test
	void validateValueTest0() {
		String input="NOI SIAM L'ESERCITO"+((char)0)+" 	.\nL'ESERCITO DEL SURF.";
		System.out.println(input);
		String actual=validateValue(input);
		assertNull(actual);
	}
	
	@Test
	void validateValueTest8() {
		String input="NOI SIAM L'ESERCITO"+((char)8)+" 	.\nL'ESERCITO DEL SURF.";
		System.out.println(input);
		String actual=validateValue(input);
		assertNull(actual);
	}
	
	@Test
	void validateValueTest26() {
		String input="NOI SIAM L'ESERCITO"+((char)26)+" 	.\nL'ESERCITO DEL SURF.";
		System.out.println(input);
		String actual=validateValue(input);
		assertNull(actual);
	}
	
	@Test
	void validateValueTest92() {
		String input="NOI SIAM L'ESERCITO"+((char)92)+" 	.\nL'ESERCITO DEL SURF.";
		String expected="NOI SIAM L''ESERCITO"+((char)92)+" 	.\n" + 
				"L''ESERCITO DEL SURF.";
		System.out.println(input);
		String actual=validateValue(input);
		assertEquals(expected, actual);
	}
	
	@Test 
	void validateDuplicateBase() {
		String expected="l``ammore e l``odio";
		String input="l``ammore e l``odio";
		String actual=validateBase(input);
		assertEquals(expected, actual);
	}
	
	@Test 
	void validateDuplicateValue() {
		String expected="NOI SIAM L''ESERCITOO.		 L''ESERCITO DEL SURF.";
		String input="NOI SIAM L''ESERCITOO.		 L''ESERCITO DEL SURF.";
		String actual=validateValue(input);
		assertEquals(expected, actual);
	}
	
	@Test 
	void validateENDBase() {
		String expected="l``ammore e l``odio``";
		String input="l``ammore e l``odio`";
		String actual=validateBase(input);
		assertEquals(expected, actual);
	}
	
	@Test 
	void validateENDValue() {
		String expected="NOI SIAM L''ESERCITOO.		 L''ESERCITO DEL SURF.''";
		String input="NOI SIAM L''ESERCITOO.		 L''ESERCITO DEL SURF.'";
		String actual=validateValue(input);
		assertEquals(expected, actual);
	}

}
