package psykeco.querycraft.test;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import psykeco.querycraft.ConnectionCraft;
import psykeco.querycraft.TableCraft;
import psykeco.querycraft.sql.MySqlConnection;
import psykeco.querycraft.sql.SQLConnectionCraft;
import psykeco.querycraft.sql.SQLTableCraft;

class NoDBTest {
	
	static class Entity{
		int id;
		String descr;
		
		public Entity() {}
		public Entity(int id, String descr) { this.id=id; this.descr=descr;}
	}
	
	private static MySqlConnection getConn() {
		File pskf=new File("psk");
		System.out.println(pskf.getAbsolutePath());
		String psk="";
		try(Scanner sc=new Scanner(pskf)){
			psk=sc.nextLine();
		} catch(Exception e) {}
		ConnectionCraft cnnf= new SQLConnectionCraft().psk(psk).autocommit(true).db("TestDB");
		//1st check connection
		
		MySqlConnection.createConnection((SQLConnectionCraft) cnnf);
		MySqlConnection m = new MySqlConnection();
		
		
		return m; 
		
	}
	
	

	@Test
	void completeTest() {
		MySqlConnection m=getConn();
		TableCraft tc = new SQLTableCraft().table(Entity.class).primary("id");
		try {
			String query=tc.create();
			System.out.println(query);
			m.exec(query);
			query =tc.insertData(new Entity(0,"ciao")).craft();
			System.out.println(query);
			m.exec(query);
			if (!m.getErrMsg().equals("")) 
				fail("an error occur: \n" + m.getErrMsg());
			query=tc.insertData(new Entity(1,"ciao")).craft();
			System.out.println(query);
			m.exec(query);
			if (!m.getErrMsg().equals("")) 
				fail("an error occur: \n" + m.getErrMsg());
			query=tc.deleteData(new Entity(1,"ciao")).craft();
			System.out.println(query);
			m.exec(query);
			if (!m.getErrMsg().equals("")) 
				fail("an error occur: \n" + m.getErrMsg());
			
			query= tc.selectData(null).craft();
			System.out.println(query);
			java.util.List <Entity> lista_entita = m.queryList(Entity.class,query );
			for(Entity o : lista_entita) {
				System.out.println(""+o.id+" "+o.descr);
			}
		
			
		} finally {
			// reset
			m.exec(tc.drop());
			if (!m.getErrMsg().equals(""))
				System.out.println("an error occur: \n" + m.getErrMsg());
		}
	}

}
