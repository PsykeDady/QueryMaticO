package psykeco.querymatico.test.syntax;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import psykeco.querymatico.QueryMaticO;
import psykeco.querymatico.sql.SQLDeleteMaticO;
import psykeco.querymatico.sql.SQLInsertMaticO;
import psykeco.querymatico.sql.SQLSelectMaticO;

class QueryMaticOTest {

	@Test
	void testSelect () {
		String expected =
			"SELECT `campo`,`chiave` "
			+ "FROM `TestDB`.`TestTable` "
			+ "WHERE 1=1 AND "
			+ 	"`colonna 2`=5 "
			+ 	"AND `colonna 1`='stringa 1'"
		;
		
		QueryMaticO s = new SQLSelectMaticO().DB("TestDB").
				table("TestTable")
				.entry("chiave",null)
				.entry("campo",null)
				.filter("colonna 1", "stringa 1")
				.filter("colonna 2",5);
		
		assertEquals(expected,s.build().trim());
	}
	
	@Test
	void testInsert () {
		String expected =
			"INSERT INTO `TestDB`.`TestTable` "
			+ 	"( `chiave`,`campo`) "
			+ "VALUES "
			+ 	"(5,'questo e un test')"
		;
		
		QueryMaticO s = new SQLInsertMaticO().DB("TestDB").
				table("TestTable")
				.entry("chiave",5)
				.entry("campo","questo e un test");
		
		assertEquals(expected,s.build().trim());
	}

	
	@Test
	void testDelete () {
		String expected =
			"DELETE FROM `TestDB`.`TestTable` "
			+ "WHERE 1=1 "
				+ "AND `colonna 2`=5 "
				+ "AND `colonna 1`='stringa 1'"
		;
		
		QueryMaticO s = new SQLDeleteMaticO().DB("TestDB").
				table("TestTable")
				.filter("colonna 1", "stringa 1")
				.filter("colonna 2",5);
		
		assertEquals(expected,s.build().trim());
	}


}
