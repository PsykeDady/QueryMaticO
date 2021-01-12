package psykeco.querycraft.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import psykeco.querycraft.TableCraft;
import psykeco.querycraft.sql.SQLTableCraft;


class TableCraftTest {
	
	private static class Entita{
		private Integer chiave;
		private String campo;
		public int getChiave() {
			return chiave;
		}
		public void setChiave(int chiave) {
			this.chiave = chiave;
		}
		public String getCampo() {
			return campo;
		}
		public void setCampo(String campo) {
			this.campo = campo;
		}
	}

	@Test
	void testCreate () {
		String expected =
			"create table `TestDB`.`Entita` ("
				+ "chiave INT primary key,"
				+ "campo NVARCHAR(32766)"
			+ ")"
		;
		
		TableCraft s = new SQLTableCraft().DB("TestDB").
				table(Entita.class).primary("chiave");
		
		assertEquals(expected,s.create().trim());
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

}
