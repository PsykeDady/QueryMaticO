package psykeco.querymatico.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static psykeco.querymatico.sql.utility.SQLClassParser.validateBase;
import static psykeco.querymatico.sql.utility.SQLClassParser.validateValue;

import java.io.File;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import psykeco.querymatico.DBMaticO;
import psykeco.querymatico.TableMaticO;
import psykeco.querymatico.sql.SQLConnectionMaticO;
import psykeco.querymatico.sql.SQLDBMaticO;
import psykeco.querymatico.sql.SQLTableMaticO;
import psykeco.querymatico.sql.runners.MySqlConnection;

class MySQLConnectionTest {
	
	class Ciao{
		int id;
		String nome;
	}

	@Test
	@Order(1)
	void test() {
		System.out.println("#### INIZIO MYSQLCONNECTION TEST ####");
		
		String expected="";
		String actual  ="";
		
		SQLConnectionMaticO s=new SQLConnectionMaticO();
		File pskf=new File("psk");
		System.out.println(pskf.getAbsolutePath());
		String psk="";
		try(Scanner sc=new Scanner(pskf)){
			psk=sc.nextLine();
		} catch(Exception e) {}
		s.psk(psk);
		
		expected="jdbc:mysql://localhost:3306?useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
		actual=s.build();
		System.out.println(actual);
		assertEquals(expected, actual);

		assertFalse(MySqlConnection.existConnection());
		
		MySqlConnection.createConnection(s);
		MySqlConnection m=new MySqlConnection();
		
		assertTrue(MySqlConnection.existConnection());
		StringBuilder sb=new StringBuilder();
		
		for(int i =33;i<127;i++) if( (i<'a' || i>'z') && (i<'A' || i>'Z') )sb.append((char)i);
		String nomedb="DB"+validateBase(sb.toString());
		DBMaticO db =new SQLDBMaticO().DB(nomedb);
		System.out.println(db.create());
		m.exec(db.create());
		System.out.println(m.getErrMsg());
		
		sb.append((char)9);
		sb.append((char)10);
		sb.append("ciao");
		
		TableMaticO tc= new SQLTableMaticO().DB(nomedb).table(Ciao.class);
		m.exec(tc.create());
		System.out.println(m.getErrMsg());
		
		Ciao c=new Ciao();
		c.id=1;
		c.nome=validateValue(sb.toString());
		
		System.out.println(tc.insertData(c).build());
		m.exec(tc.insertData(c).build());
		System.out.println(m.getErrMsg());
		
		
		System.out.println("#### FINE   MYSQLCONNECTION TEST ####");
	}

}
