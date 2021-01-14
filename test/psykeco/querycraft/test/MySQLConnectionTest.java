package psykeco.querycraft.test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import psykeco.querycraft.sql.MySqlConnection;
import psykeco.querycraft.sql.SQLConnectionCraft;

class MySQLConnectionTest {

	@Test
	void test() {
		SQLConnectionCraft s=new SQLConnectionCraft();
		File pskf=new File("psk");
		System.out.println(pskf.getAbsolutePath());
		String psk="";
		try(Scanner sc=new Scanner(pskf)){
			psk=sc.nextLine();
		} catch(Exception e) {}
		s.psk(psk);
		MySqlConnection m=new MySqlConnection(s);
		
		for (String x: m.listDB()) {
			System.out.println(x);
		}
	}

}
