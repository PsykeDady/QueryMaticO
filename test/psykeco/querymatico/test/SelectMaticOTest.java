package psykeco.querymatico.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import psykeco.querymatico.SelectMaticO;
import psykeco.querymatico.sql.SQLSelectMaticO;

class SelectMaticOTest {
	
	@Test
	void testSelectMaticO() {
		SelectMaticO s=(SelectMaticO) new SQLSelectMaticO().DB("TestDB").table("Entita").alias("e").
				entry("id").entry("nome").filter("INTERO", 123).filter("STRINGA", "ciao");
		
		String expected=""
				+ "SELECT `e`.`id`,`e`.`nome` "
				+ "FROM `TestDB`.`Entita` `e` "
				+ "WHERE 1=1 "
					+ "AND `e`.`STRINGA`='ciao' "
					+ "AND `e`.`INTERO`=123" 
		;
		
		assertEquals(expected, s.build() );
	}
	
	@Test
	void testSelectMaticOJoin() {
		SelectMaticO j=(SelectMaticO) new SQLSelectMaticO().DB("TestDB").table("Entita").alias("e").
				entry("id").entry("nome").filter("INTERO", 123).filter("STRINGA", "ciao");
		
		SelectMaticO s=(SelectMaticO) new SQLSelectMaticO().DB("TestDB").table("Ciao").alias("c").
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
		assertEquals(expected, s.build() );
	}

}
