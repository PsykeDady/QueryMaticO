package psykeco.querymatico.test;

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

import psykeco.querymatico.ConnectionMaticO;
import psykeco.querymatico.DBMaticO;
import psykeco.querymatico.TableMaticO;
import psykeco.querymatico.sql.SQLConnectionMaticO;
import psykeco.querymatico.sql.SQLDBMaticO;
import psykeco.querymatico.sql.SQLTableMaticO;
import psykeco.querymatico.sql.runners.MySqlConnection;

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
		ConnectionMaticO cnnf= new SQLConnectionMaticO().psk(psk).autocommit(true);
		//1st check connection
		MySqlConnection.createConnection((SQLConnectionMaticO) cnnf);
		MySqlConnection m = new MySqlConnection();
		DBMaticO dbc = new SQLDBMaticO().DB("DBName2");
		TableMaticO tc = new SQLTableMaticO().DB("DBName2").table(Entity.class).primary("identity");
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
	
	void completeTest(TableMaticO tc, MySqlConnection m) {
		// insert data
		Entity e = new Entity(); e.identity=1; e.name="DOGE"; e.testfile=new File("testfile");e.testfile2=new File("testfile2");
		
		actual=tc.insertData(e).build();
		System.out.println(actual);
		m.exec(actual);
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: " + m.getErrMsg());
		assertEquals(m.getErrMsg(), "");
		actual=tc.selectData(null).build();
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
