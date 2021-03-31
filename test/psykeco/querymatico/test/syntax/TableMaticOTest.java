package psykeco.querymatico.test.syntax;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import psykeco.querymatico.TableMaticO;
import psykeco.querymatico.sql.SQLTableMaticO;


class TableMaticOTest {
	
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
	void testCreate () {
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
		assertEquals(expected,s.create().trim());
	}
	
	@Test
	void testDrop () {
		String expected =
			"DROP TABLE IF EXISTS `TestDB`.`Entita`"
		;
		
		TableMaticO s = new SQLTableMaticO().DB("TestDB").
				table(Entita.class).primary("chiave");
		
		assertEquals(expected,s.drop().trim());
	}
	
	@Test
	void testSelect () {
		String expected =
			  "SELECT * "
			+ "FROM `information_schema`.`Tables` "
			+ "WHERE 1=1 AND `table_schema`='TestDB' "
			  + "AND `table_name`='Entita'";
		;
		
		TableMaticO s = new SQLTableMaticO().DB("TestDB").
				table(Entita.class).primary("chiave");
		
		assertEquals(expected,s.exists().trim());
	}
	
	@Test
	void testInsertData () {
		String expected =
			  "INSERT INTO `TestDB`.`Entita` ( `chiave`,`campo`) "
			+ "VALUES (123,'un campo generico')"
		;
		
		Entita ins=new Entita();
		ins.setCampo("un campo generico");
		ins.setChiave(123);
		
		TableMaticO s = new SQLTableMaticO().DB("TestDB").
				table(Entita.class).primary("chiave");
		
		assertEquals(expected,s.insertData(ins).build());
	}
	
	@Test
	void testSelectData () {
		String expected =
				  "SELECT * "
				+ "FROM `TestDB`.`Entita` "
				+ "WHERE 1=1 AND `campo`='un campo generico'";
		;
		
		Entita ins=new Entita();
		ins.setCampo("un campo generico");
		
		TableMaticO s = new SQLTableMaticO().DB("TestDB").
				table(Entita.class).primary("chiave");
		
		assertEquals(expected,s.selectData(ins).build());
	}
	
	@Test
	void testSelectAll () {
		String expected =
				  "SELECT * "
				+ "FROM `TestDB`.`Entita` "
				+ "WHERE 1=1"
		;
		
		
		TableMaticO s = new SQLTableMaticO().DB("TestDB").
				table(Entita.class).primary("chiave");
		
		assertEquals(expected,s.selectData(null).build());
	}
	
	@Test
	void testCountData () {
		String expected =
				  "SELECT COUNT(*) "
				+ "FROM `TestDB`.`Entita` "
				+ "WHERE 1=1 AND `campo`='un campo generico'";
		;
		
		Entita ins=new Entita();
		ins.setCampo("un campo generico");
		
		TableMaticO s = new SQLTableMaticO().DB("TestDB").
				table(Entita.class).primary("chiave");
		assertEquals(expected,s.countData(ins).build());
	}
	
	@Test
	void testDeleteData () {
		String expected =
				  "DELETE FROM `TestDB`.`Entita` "
				+ "WHERE 1=1 AND `campo`='un campo generico'";
		;
		
		Entita ins=new Entita();
		ins.setCampo("un campo generico");
		
		TableMaticO s = new SQLTableMaticO().DB("TestDB").
				table(Entita.class).primary("chiave");
		
		assertEquals(expected,s.deleteData(ins).build());
	}
	
	@Test
	void testUpdateData () {
		String expected =
				  "UPDATE `TestDB`.`Entita` "
				+ "SET `campo`='un campo generico',`anotherCampo`='Another campo generico' "
				+ "WHERE 1=1 AND `chiave`=123"
				
		;
		
		Entita ins=new Entita();
		ins.setChiave(123);
		ins.setCampo("un campo generico");
		ins.setAnotherCampo("Another campo generico");
		
		TableMaticO s = new SQLTableMaticO().DB("TestDB").
				table(Entita.class).primary("chiave");
		
		assertEquals(expected,s.updateData(ins).build());
	}

}
