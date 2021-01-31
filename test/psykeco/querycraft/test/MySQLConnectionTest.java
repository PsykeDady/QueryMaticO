package psykeco.querycraft.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import psykeco.querycraft.DBCraft;
import psykeco.querycraft.QueryCraft;
import psykeco.querycraft.TableCraft;
import psykeco.querycraft.sql.MySqlConnection;
import psykeco.querycraft.sql.SQLConnectionCraft;
import psykeco.querycraft.sql.SQLDBCraft;
import psykeco.querycraft.sql.SQLTableCraft;

class MySQLConnectionTest {
	
	class Ciao{
		int id;
		String nome;
	}

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
		
		expected="jdbc:mysql://localhost:3306?useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
		actual=s.craft();
		System.out.println(actual);
		assertEquals(expected, actual);
		
		assertFalse(MySqlConnection.existConnection());
		
		MySqlConnection.createConnection(s);
		MySqlConnection m=new MySqlConnection();
		
		assertTrue(MySqlConnection.existConnection());
		
		List<String>listdb=m.listDB();
		
		assertTrue(listdb.contains("information_schema"));
		
		assertTrue(listdb.contains("performance_schema"));
		
		for (String x: listdb) {
			System.out.println(x);
		}
		
		StringBuilder sb=new StringBuilder();
		
		for(int i =33;i<127;i++) if( (i<'a' || i>'z') && (i<'A' || i>'Z') )sb.append((char)i);
		String nomedb="DB"+QueryCraft.validateBase(sb.toString());
		DBCraft db =new SQLDBCraft().DB(nomedb);
		System.out.println(db.create());
		m.exec(db.create());
		System.out.println(m.getErrMsg());
		
		sb.append((char)9);
		sb.append((char)10);
		sb.append("ciao");
		
		TableCraft tc= new SQLTableCraft().DB(nomedb).table(Ciao.class);
		m.exec(tc.create());
		System.out.println(m.getErrMsg());
		
		Ciao c=new Ciao();
		c.id=1;
		c.nome=QueryCraft.validateValue(sb.toString());
		
		System.out.println(tc.insertData(c).craft());
		m.exec(tc.insertData(c).craft());
		System.out.println(m.getErrMsg());
		
		
		System.out.println("#### FINE   MYSQLCONNECTION TEST ####");
	}

}
