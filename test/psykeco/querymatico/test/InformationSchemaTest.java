package psykeco.querymatico.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import psykeco.querymatico.ConnectionMaticO;
import psykeco.querymatico.sql.SQLConnectionMaticO;
import psykeco.querymatico.sql.runners.InformationSchema;
import psykeco.querymatico.sql.runners.MySqlConnection;

class InformationSchemaTest {
	
	@Test
	void existsDBTest() {
		File pskf=new File("psk");
		String psk="";
		try(Scanner sc=new Scanner(pskf)){
			psk=sc.nextLine();
		} catch(Exception e) {}
		ConnectionMaticO cnnf= new SQLConnectionMaticO().psk(psk).autocommit(true);
		MySqlConnection.createConnection((SQLConnectionMaticO) cnnf);
		
		assertTrue(InformationSchema.existsDB(InformationSchema.DB));
	}

	@Test
	void existsTableTest() {
		File pskf=new File("psk");
		String psk="";
		try(Scanner sc=new Scanner(pskf)){
			psk=sc.nextLine();
		} catch(Exception e) {}
		ConnectionMaticO cnnf= new SQLConnectionMaticO().psk(psk).autocommit(true);
		
		MySqlConnection.createConnection((SQLConnectionMaticO) cnnf);
		
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
		ConnectionMaticO cnnf= new SQLConnectionMaticO().psk(psk).autocommit(true);
		
		MySqlConnection.createConnection((SQLConnectionMaticO) cnnf);
		
		List <String> db= InformationSchema.listDB();
		
		assertTrue(db.contains(InformationSchema.DB));
	}
	
	void testListTables() {
		File pskf=new File("psk");
		String psk="";
		try(Scanner sc=new Scanner(pskf)){
			psk=sc.nextLine();
		} catch(Exception e) {}
		ConnectionMaticO cnnf= new SQLConnectionMaticO().psk(psk).autocommit(true);
		
		MySqlConnection.createConnection((SQLConnectionMaticO) cnnf);
		
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
		ConnectionMaticO cnnf= new SQLConnectionMaticO().psk(psk).autocommit(true);
		
		MySqlConnection.createConnection((SQLConnectionMaticO) cnnf);
		
		List<Entry<String,String>> tables= InformationSchema.getAllInfo();
		
		boolean found=false;
		
		for(Entry<String,String> i : tables ) {
			found=i.getKey().equals("information_schema")&&i.getKey().equals("tables");
		}
		assertTrue(found);
	}
}
