package psykeco.querycraft.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import psykeco.querycraft.QueryCraft;
import psykeco.querycraft.SelectCraft;
import psykeco.querycraft.sql.SQLSelectCraft;

class SelectCraftTest {

//	@Test
//	void testsSelectCraft() {
//		SelectCraft s=(SelectCraft) new SQLSelectCraft().DB("TestDB").table("Entita").alias("e").
//				entry("id").entry("nome").filter("INTERO", 123).filter("STRINGA", "ciao");
//		
//		String expected="`e`.`id`,`e`.`nome`";
//		
//		assertEquals(expected, s.selectCraft().trim() );
//	}
//	
//	@Test
//	void testFromCraft() {
//		SelectCraft s=(SelectCraft) new SQLSelectCraft().DB("TestDB").table("Entita").alias("e").
//				entry("id").entry("nome").filter("INTERO", 123).filter("STRINGA", "ciao");
//		
//		String expected="`TestDB`.`Entita` `e`";
//		
//		assertEquals(expected, s.fromCraft().trim() );
//	}
//	
//	@Test
//	void testWhereCraft() {
//		SelectCraft s=(SelectCraft) new SQLSelectCraft().DB("TestDB").table("Entita").alias("e").
//				entry("id").entry("nome").filter("INTERO", 123).filter("STRINGA", "ciao");
//		
//		String expected="AND `e`.`STRINGA`='ciao' AND `e`.`INTERO`=123";
//		
//		assertEquals(expected, s.whereCraft().trim() );
//	}
//	
//	@Test
//	void testsSelectCraftJoin() {
//		SelectCraft j=(SelectCraft) new SQLSelectCraft().DB("TestDB").table("Entita").alias("e").
//				entry("id").entry("nome").filter("INTERO", 123).filter("STRINGA", "ciao");
//		
//		SelectCraft s=(SelectCraft) new SQLSelectCraft().DB("TestDB").table("Ciao").alias("c").
//				entry("id").entry("saluto").filter("VMOBILE", 21.3f).filter("CAMPO", "we");
//		
//		s.join(j).joinFilter("saluto", "nome");
//		
//		String expected="`c`.`id`,`c`.`saluto`, `e`.`id`,`e`.`nome`";
//		
//		assertEquals(expected, s.selectCraft().trim() );
//	}
//	
//	@Test
//	void testFromCraftJoin() {
//		SelectCraft j=(SelectCraft) new SQLSelectCraft().DB("TestDB").table("Entita").alias("e").
//				entry("id").entry("nome").filter("INTERO", 123).filter("STRINGA", "ciao");
//		
//		SelectCraft s=(SelectCraft) new SQLSelectCraft().DB("TestDB").table("Ciao").alias("c").
//				entry("id").entry("saluto").filter("VMOBILE", 21.3f).filter("CAMPO", "we");
//		
//		s.join(j).joinFilter("saluto", "nome");
//		
//		String expected="`TestDB`.`Ciao` `c`, `TestDB`.`Entita` `e`";
//		
//		assertEquals(expected, s.fromCraft().trim() );
//	}
//	
//	@Test
//	void testWhereCraftJoin() {
//		SelectCraft j=(SelectCraft) new SQLSelectCraft().DB("TestDB").table("Entita").alias("e").
//				entry("id").entry("nome").filter("INTERO", 123).filter("STRINGA", "ciao");
//		
//		SelectCraft s=(SelectCraft) new SQLSelectCraft().DB("TestDB").table("Ciao").alias("c").
//				entry("id").entry("saluto").filter("VMOBILE", 21.3f).filter("CAMPO", "we");
//		
//		s.join(j).joinFilter("saluto", "nome");
//		
//		String expected=""
//				+ "AND `c`.`CAMPO`='we' "
//				+ "AND `c`.`VMOBILE`=21.3 , "
//				+ "AND `e`.`STRINGA`='ciao' "
//				+ "AND `e`.`INTERO`=123 "
//				+ "AND `c`.`saluto`=`e`.`nome`";
//		
//		assertEquals(expected, s.whereCraft().trim() );
//	}
	
	// ^ righe sopra commentate perché i metodi son stati definiti protected, scommentare se si ha necessita' di testare i metodi singoli, dopo averli ovviamente resi public
	
	
	@Test
	void testSelectCraft() {
		SelectCraft s=(SelectCraft) new SQLSelectCraft().DB("TestDB").table("Entita").alias("e").
				entry("id").entry("nome").filter("INTERO", 123).filter("STRINGA", "ciao");
		
		String expected=""
				+ "SELECT `e`.`id`,`e`.`nome` "
				+ "FROM `TestDB`.`Entita` `e` "
				+ "WHERE 1=1 "
					+ "AND `e`.`STRINGA`='ciao' "
					+ "AND `e`.`INTERO`=123" 
		;
		
		assertEquals(expected, s.craft() );
	}
	
	@Test
	void testSelectCraftJoin() {
		SelectCraft j=(SelectCraft) new SQLSelectCraft().DB("TestDB").table("Entita").alias("e").
				entry("id").entry("nome").filter("INTERO", 123).filter("STRINGA", "ciao");
		
		SelectCraft s=(SelectCraft) new SQLSelectCraft().DB("TestDB").table("Ciao").alias("c").
				entry("id").entry("saluto").filter("VMOBILE", 21.3f).filter("CAMPO", "we");
		
		s.join(j).joinFilter("saluto", "nome");
		
		String expected=""
				+ "SELECT `c`.`id`,`c`.`saluto`,`e`.`id`,`e`.`nome` "
				+ "FROM `TestDB`.`Ciao` `c`, `TestDB`.`Entita` `e` "
				+ "WHERE 1=1 "
					+ "AND `c`.`CAMPO`='we' "
					+ "AND `c`.`VMOBILE`=21.3 "
					+ "AND `e`.`STRINGA`='ciao' "
					+ "AND `e`.`INTERO`=123 "
					+ "AND `c`.`saluto`=`e`.`nome`";
		System.out.println(expected);
		assertEquals(expected, s.craft() );
	}

}
