package psykeco.querycraft.test;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import psykeco.querycraft.ConnectionCraft;
import psykeco.querycraft.DBCraft;
import psykeco.querycraft.TableCraft;
import psykeco.querycraft.sql.MySqlConnection;
import psykeco.querycraft.sql.SQLConnectionCraft;
import psykeco.querycraft.sql.SQLDBCraft;
import psykeco.querycraft.sql.SQLTableCraft;

class FileTest {
	
	String expected=null;
	String actual=null;

	public static class Entity {
		private int identity;
		private String name;
		private File testfile;
		private File testfile2;
		
		public void setIdentity(int identity) {
			this.identity = identity;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setTestfile(File testfile) {
			this.testfile = testfile;
		}

		public void setTestfile2(File testfile2) {
			this.testfile2 = testfile2;
		}
	}

	@Test
	void test() {
		System.out.println("#### INIZIO FILE TEST ####");
		File pskf=new File("psk");
		System.out.println(pskf.getAbsolutePath());
		String psk="";
		try(Scanner sc=new Scanner(pskf)){
			psk=sc.nextLine();
		} catch(Exception e) {}
		ConnectionCraft cnnf= new SQLConnectionCraft().psk(psk).autocommit(true);
		//1st check connection
		MySqlConnection.createConnection((SQLConnectionCraft) cnnf);
		MySqlConnection m = new MySqlConnection();
		DBCraft dbc = new SQLDBCraft().DB("DBName2");
		TableCraft tc = new SQLTableCraft().DB("DBName2").table(Entity.class).primary("identity");
		try {
			//2nd check create database
			actual=dbc.create();
			m.exec(dbc.drop());
			m.exec(actual);
			System.out.println(m.getErrMsg());

			// 3rd check create table
			m.exec(tc.drop());
			actual=tc.create();
			m.exec(actual);
			System.err.println(m.getErrMsg());
			assertEquals(m.getErrMsg(),"");
			
			completeTest(tc, m);
		} catch(Exception e ) {e.printStackTrace(); fail();}
		finally {}
		System.out.println("#### FINE FILE TEST ####");

	}
	
	void completeTest(TableCraft tc, MySqlConnection m) {
		// insert data
		Entity e = new Entity(); e.identity=1; e.name="DOGE"; e.testfile=new File("testfile");e.testfile2=new File("testfile2");
		
		actual=tc.insertData(e).craft();
		System.out.println(actual);
		m.exec(actual);
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: " + m.getErrMsg());
		assertEquals(m.getErrMsg(), "");
		actual=tc.selectData(null).craft();
		System.out.println(actual);
		
		List<Entity> res=m.queryList(Entity.class, actual);
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: " + m.getErrMsg());
		
		for (Entity entita : res) {
			if(entita.testfile==null)fail("entita tornata nulla");
			try (FileInputStream fis= new FileInputStream(entita.testfile)){
				byte[]chars=new byte[fis.available()];
				fis.read(chars,0,chars.length);
				System.out.println(new String(chars));
			} catch (Exception e1) {}
		}
		
		Map<String,Object>[] res2 = m.queryMap(actual);
		
		for (Map<String,Object> mapping :res2) {
				
				System.out.println(new String((byte[])mapping.get("testfile")));
			
		}
	}

}
