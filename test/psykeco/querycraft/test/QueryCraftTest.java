package psykeco.querycraft.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import psykeco.querycraft.QueryCraft;
import psykeco.querycraft.sql.SQLDeleteCraft;
import psykeco.querycraft.sql.SQLInsertCraft;
import psykeco.querycraft.sql.SQLSelectCraft;

class QueryCraftTest {

	@Test
	void testSelect () {
		String expected =
			"SELECT `campo`,`chiave` "
			+ "FROM `TestDB`.`TestTable` "
			+ "WHERE 1=1 AND "
			+ 	"`colonna 2`=5 "
			+ 	"AND `colonna 1`='stringa 1'"
		;
		
		QueryCraft s = new SQLSelectCraft().DB("TestDB").
				table("TestTable")
				.entry("chiave",null)
				.entry("campo",null)
				.filter("colonna 1", "stringa 1")
				.filter("colonna 2",5);
		
		assertEquals(expected,s.craft().trim());
	}
	
	@Test
	void testInsert () {
		String expected =
			"INSERT INTO `TestDB`.`TestTable` "
			+ 	"( `chiave`,`campo`) "
			+ "VALUES "
			+ 	"(5,'questo e un test')"
		;
		
		QueryCraft s = new SQLInsertCraft().DB("TestDB").
				table("TestTable")
				.entry("chiave",5)
				.entry("campo","questo e un test");
		
		assertEquals(expected,s.craft().trim());
	}

	
	@Test
	void testDelete () {
		String expected =
			"DELETE FROM `TestDB`.`TestTable` "
			+ "WHERE 1=1 "
				+ "AND `colonna 2`=5 "
				+ "AND `colonna 1`='stringa 1'"
		;
		
		QueryCraft s = new SQLDeleteCraft().DB("TestDB").
				table("TestTable")
				.filter("colonna 1", "stringa 1")
				.filter("colonna 2",5);
		
		assertEquals(expected,s.craft().trim());
	}


}
