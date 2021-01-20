package psykeco.querycraft.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import psykeco.querycraft.sql.MySqlConnection;
import psykeco.querycraft.sql.SQLConnectionCraft;

class MySQLConnectionTest {

	@Test
	void test() {
		System.out.println("#### INIZIO MYSQLCONNECTION TEST ####");
		
		String expected="";
		String actual  ="";
		
		SQLConnectionCraft s=new SQLConnectionCraft();
		File pskf=new File("psk");
		System.out.println(pskf.getAbsolutePath());
		String psk="";
		try(Scanner sc=new Scanner(pskf)){
			psk=sc.nextLine();
		} catch(Exception e) {}
		s.psk(psk);
		
		expected="jdbc:mysql://localhost:3306";
		actual=s.craft();
		System.out.println(actual);
		assertEquals(expected, actual);
		
		assertFalse(MySqlConnection.statoConnessione());
		
		MySqlConnection.createConnection(s);
		MySqlConnection m=new MySqlConnection();
		
		assertTrue(MySqlConnection.statoConnessione());
		
		List<String>listdb=m.listDB();
		
		assertTrue(listdb.contains("information_schema"));
		
		assertTrue(listdb.contains("performance_schema"));
		
		for (String x: listdb) {
			System.out.println(x);
		}
		System.out.println("#### FINE   MYSQLCONNECTION TEST ####");
	}

}
