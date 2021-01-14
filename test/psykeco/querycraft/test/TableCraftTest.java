package psykeco.querycraft.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import psykeco.querycraft.TableCraft;
import psykeco.querycraft.sql.SQLTableCraft;


class TableCraftTest {
	
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
				  "create table `TestDB`.`Entita` ("
					+ "chiave INT,"
					+ "campo TEXT,"
					+ "anotherCampo NVARCHAR(676),"
	
					+ "primary key(chiave,anotherCampo)"
				+ ")"
		;
		
		TableCraft s = new SQLTableCraft().DB("TestDB").
				table(Entita.class).primary("chiave").primary("anotherCampo");
		
		assertEquals(expected,s.create().trim());
	}
	
	@Test
	void testDrop () {
		String expected =
			"drop table if exists `TestDB`.`Entita`"
		;
		
		TableCraft s = new SQLTableCraft().DB("TestDB").
				table(Entita.class).primary("chiave");
		
		assertEquals(expected,s.drop().trim());
	}
	
	@Test
	void testSelect () {
		String expected =
			  "select * "
			+ "from information_schema "
			+ "where table_schema='TestDB' "
			+ "and table_name='Entita'"
		;
		
		TableCraft s = new SQLTableCraft().DB("TestDB").
				table(Entita.class).primary("chiave");
		
		assertEquals(expected,s.select().trim());
	}
	
	@Test
	void testInsertData () {
		String expected =
			  "insert into `TestDB`.`Entita` ( `chiave`,`campo`) "
			+ "values (123,'un campo generico')"
		;
		
		Entita ins=new Entita();
		ins.setCampo("un campo generico");
		ins.setChiave(123);
		
		TableCraft s = new SQLTableCraft().DB("TestDB").
				table(Entita.class).primary("chiave");
		
		assertEquals(expected,s.insertData(ins).craft().trim());
	}
	
	@Test
	void testSelectData () {
		String expected =
				  "select * "
				+ "from `TestDB`.`Entita` "
				+ "where 1=1 AND `campo`='un campo generico'";
		;
		
		Entita ins=new Entita();
		ins.setCampo("un campo generico");
		
		TableCraft s = new SQLTableCraft().DB("TestDB").
				table(Entita.class).primary("chiave");
		
		assertEquals(expected,s.selectData(ins).craft().trim());
	}
	
	@Test
	void testDeleteData () {
		String expected =
				  "delete from `TestDB`.`Entita` "
				+ "where 1=1 AND `campo`='un campo generico'";
		;
		
		Entita ins=new Entita();
		ins.setCampo("un campo generico");
		
		TableCraft s = new SQLTableCraft().DB("TestDB").
				table(Entita.class).primary("chiave");
		
		assertEquals(expected,s.deleteData(ins).craft().trim());
	}
	
	@Test
	void testUpdateData () {
		String expected =
				  "update `TestDB`.`Entita` "
				+ "set `campo`='un campo generico',`anotherCampo`='Another campo generico'  "
				+ "where 1=1 AND `chiave`=123"
				
		;
		
		Entita ins=new Entita();
		ins.setChiave(123);
		ins.setCampo("un campo generico");
		ins.setAnotherCampo("Another campo generico");
		
		TableCraft s = new SQLTableCraft().DB("TestDB").
				table(Entita.class).primary("chiave");
		
		assertEquals(expected,s.updateData(ins).craft().trim());
	}

}
