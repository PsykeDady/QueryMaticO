package psykeco.querymatico.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import psykeco.querymatico.SelectMaticO;
import psykeco.querymatico.sql.SQLSelectMaticO;

class AggregateTest {

	@Test
	void groupByTest() {
		String expected=""
			+ "SELECT `id` "
			+ "FROM `MyApp`.`Test` "
			+ "WHERE 1=1 "
				+ "AND `descrizione`='test test' "
			+ "GROUP BY `nome`"
		;
		
		SelectMaticO scf= new SQLSelectMaticO().DB("MyApp").table("Test")
				.entry("id").filter("descrizione", "test test").groupBy("nome");
		
		assertEquals(expected, scf.build());
	}
	
	@Test
	void orderByTest() {
		String expected=""
			+ "SELECT `id` "
			+ "FROM `MyApp`.`Test` "
			+ "WHERE 1=1 AND `descrizione`='test test' "
			+ "ORDER BY `nome` ASC"
		;
		
		SelectMaticO scf= new SQLSelectMaticO().DB("MyApp").table("Test")
				.entry("id").filter("descrizione", "test test").orderBy("nome");
		
		assertEquals(expected, scf.build());
	}
	
	@Test
	void orderByDescTest() {
		String expected=""
			+ "SELECT `id` "
			+ "FROM `MyApp`.`Test` "
			+ "WHERE 1=1 AND `descrizione`='test test' "
			+ "ORDER BY `nome` DESC"
		;
		
		SelectMaticO scf= new SQLSelectMaticO().DB("MyApp").table("Test")
				.entry("id").filter("descrizione", "test test").orderBy("nome",false);
		
		assertEquals(expected, scf.build());
	}
	
	@Test
	void countTest() {
		String expected=""
				+ "SELECT `id`,COUNT(*) "
				+ "FROM `MyApp`.`Test` "
				+ "WHERE 1=1 AND "
					+ "`descrizione`='test test'"
		;
		
		SelectMaticO scf= new SQLSelectMaticO().DB("MyApp").table("Test")
				.entry("id").filter("descrizione", "test test").count(null);
		
		assertEquals(expected, scf.build());
	}
	
	@Test
	void distinctTest() {
		String expected=""
			+ "SELECT `nome`,DISTINCT(`id`) "
			+ "FROM `MyApp`.`Test` "
			+ "WHERE 1=1 AND `descrizione`='test test'"
		;
		
		SelectMaticO scf= new SQLSelectMaticO().DB("MyApp").table("Test")
				.entry("nome").filter("descrizione", "test test").distinct("id");
		
		assertEquals(expected, scf.build());
	}
	
	@Test
	void countDistinctTest() {
		String expected=""
			+ "SELECT `id`,COUNT(DISTINCT(`nome`)) "
			+ "FROM `MyApp`.`Test` "
			+ "WHERE 1=1 AND `descrizione`='test test'"
		;
		
		SelectMaticO scf= new SQLSelectMaticO().DB("MyApp").table("Test")
				.entry("id").filter("descrizione", "test test").count("nome").distinct("nome");
		
		assertEquals(expected, scf.build());
	}

}
