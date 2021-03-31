package psykeco.querymatico.test.syntax;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import psykeco.querymatico.QueryMaticO;
import psykeco.querymatico.TableMaticO;
import psykeco.querymatico.sql.SQLDeleteMaticO;
import psykeco.querymatico.sql.SQLInsertMaticO;
import psykeco.querymatico.sql.SQLSelectMaticO;
import psykeco.querymatico.sql.SQLTableMaticO;

class CopyTest {

	private static class Entita{
		@SuppressWarnings("unused")
		private Integer chiave;
		@SuppressWarnings("unused")
		private String campo;
		@SuppressWarnings("unused")
		private String anotherCampo;
		public void setChiave(int chiave) {
			this.chiave = chiave;
		}
		public void setCampo(String campo) {
			this.campo = campo;
		}
		public void setAnotherCampo(String anotherCampo) {
			this.anotherCampo = anotherCampo;
		}
	}

	@Test
	void testCopyTable () {
		String expected =
				  "CREATE TABLE `TestDB`.`Entita` ("
					+ "chiave INT,"
					+ "campo TEXT,"
					+ "anotherCampo NVARCHAR(676),"
	
					+ "PRIMARY KEY(chiave,anotherCampo)"
				+ ")"
		;
		
		TableMaticO s = new SQLTableMaticO().DB("TestDB").
				table(Entita.class).primary("chiave").primary("anotherCampo");
		TableMaticO s2= s.copy().DB("DBTest");
		assertEquals(expected,s.create().trim());
		assertEquals(expected.replace("TestDB", "DBTest"),s2.create().trim());
		assertNotEquals(s, s2);
	}
	
	@Test
	void testCopySelect () {
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
		
		QueryMaticO s2= s.copy().DB("DBTest");
		
		assertEquals(expected,s.build().trim());
		assertEquals(expected.replace("TestDB", "DBTest"),s2.build().trim());
		assertNotEquals(s, s2);
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
		
		QueryMaticO s2= s.copy().DB("DBTest");
		
		assertEquals(expected,s.build().trim());
		assertEquals(expected.replace("TestDB", "DBTest"),s2.build().trim());
		assertNotEquals(s, s2);
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
		
		QueryMaticO s2= s.copy().DB("DBTest");
		
		assertEquals(expected,s.build().trim());
		assertEquals(expected.replace("TestDB", "DBTest"),s2.build().trim());
		assertNotEquals(s, s2);
	}

}
