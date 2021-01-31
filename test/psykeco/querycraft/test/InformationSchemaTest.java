package psykeco.querycraft.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;

import psykeco.querycraft.ConnectionCraft;
import psykeco.querycraft.sql.InformationSchema;
import psykeco.querycraft.sql.MySqlConnection;
import psykeco.querycraft.sql.SQLConnectionCraft;

class InformationSchemaTest {

	@Test
	void existsTest() {
		File pskf=new File("psk");
		String psk="";
		try(Scanner sc=new Scanner(pskf)){
			psk=sc.nextLine();
		} catch(Exception e) {}
		ConnectionCraft cnnf= new SQLConnectionCraft().psk(psk).autocommit(true);
		
		MySqlConnection.createConnection((SQLConnectionCraft) cnnf);
		
		assertTrue(InformationSchema.existsTable(InformationSchema.DB, "tables"));
		assertFalse(InformationSchema.existsTable(InformationSchema.DB, "tabelle"));
	}
	
	@Test
	void testListDB() {
		File pskf=new File("psk");
		String psk="";
		try(Scanner sc=new Scanner(pskf)){
			psk=sc.nextLine();
		} catch(Exception e) {}
		ConnectionCraft cnnf= new SQLConnectionCraft().psk(psk).autocommit(true);
		
		MySqlConnection.createConnection((SQLConnectionCraft) cnnf);
		
		List <String> db= InformationSchema.listDB();
		
		assertTrue(db.contains(InformationSchema.DB));
	}
	
	void testListTables() {
		File pskf=new File("psk");
		String psk="";
		try(Scanner sc=new Scanner(pskf)){
			psk=sc.nextLine();
		} catch(Exception e) {}
		ConnectionCraft cnnf= new SQLConnectionCraft().psk(psk).autocommit(true);
		
		MySqlConnection.createConnection((SQLConnectionCraft) cnnf);
		
		List <String> tables= InformationSchema.listTables(InformationSchema.DB);
		
		assertTrue(tables.contains("tables"));
		assertFalse(tables.contains("tabelle"));
	}
	
	void testGetAllInfo() {
		File pskf=new File("psk");
		String psk="";
		try(Scanner sc=new Scanner(pskf)){
			psk=sc.nextLine();
		} catch(Exception e) {}
		ConnectionCraft cnnf= new SQLConnectionCraft().psk(psk).autocommit(true);
		
		MySqlConnection.createConnection((SQLConnectionCraft) cnnf);
		
		List<Entry<String,String>> tables= InformationSchema.getAllInfo();
		
		boolean found=false;
		
		for(Entry<String,String> i : tables ) {
			found=i.getKey().equals("information_schema")&&i.getKey().equals("tables");
		}
		assertTrue(found);
	}
}
