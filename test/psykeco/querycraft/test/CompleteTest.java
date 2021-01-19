package psykeco.querycraft.test;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import psykeco.querycraft.DBCraft;
import psykeco.querycraft.SelectCraft;
import psykeco.querycraft.TableCraft;
import psykeco.querycraft.sql.MySqlConnection;
import psykeco.querycraft.sql.SQLConnectionCraft;
import psykeco.querycraft.sql.SQLDBCraft;
import psykeco.querycraft.sql.SQLSelectCraft;
import psykeco.querycraft.sql.SQLTableCraft;

class CompleteTest {

	public static class Entity {
		private int identity;
		private String name;
		private String description;
		
		public void setIdentity(int identity) {
			this.identity = identity;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setDescription(String description) {
			this.description = description;
		}
	}

	@Test
	void test() {
		File pskf=new File("psk");
		System.out.println(pskf.getAbsolutePath());
		String psk="";
		try(Scanner sc=new Scanner(pskf)){
			psk=sc.nextLine();
		} catch(Exception e) {}
		MySqlConnection.createConnection((SQLConnectionCraft) 
				new SQLConnectionCraft().psk(psk).autocommit(true));
		MySqlConnection m = new MySqlConnection();
		DBCraft dbc = new SQLDBCraft().DB("DBName");
		TableCraft tc = new SQLTableCraft().DB("DBName").table(Entity.class).primary("identity");
		try {
			System.out.println(dbc.create());
			m.exec(dbc.create());
			if (!m.getErrMsg().equals(""))
				throw new IllegalStateException("an error occur: " + m.getErrMsg());

			// create table
			System.out.println(tc.create());
			m.exec(tc.create());
			if (!m.getErrMsg().equals(""))
				throw new IllegalStateException("an error occur: " + m.getErrMsg());
			
			completeTest(tc, m);
		} finally {
			// reset
			System.out.println(tc.drop());
			m.exec(tc.drop());
			if (!m.getErrMsg().equals(""))
				System.out.println("an error occur: \n" + m.getErrMsg());
			
			System.out.println(dbc.drop());
			m.exec(dbc.drop());
			if (!m.getErrMsg().equals(""))
				System.out.println("an error occur: \n" + m.getErrMsg());
		}
	}
	
	
	void completeTest(TableCraft tc, MySqlConnection m) {
		// insert data
		Entity e = new Entity(); e.identity=1; e.name="DOGE"; e.description=("funny dog");
		System.out.println(tc.insertData(e).craft());
		m.exec(tc.insertData(e).craft());
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: " + m.getErrMsg());

		e = new Entity(); e.identity=2; e.name="MARIO"; e.description="italian plumber";
		System.out.println(tc.insertData(e).craft());
		m.exec(tc.insertData(e).craft());
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: " + m.getErrMsg());

		e = new Entity(); e.identity=3; e.name="Ugly"; e.description="ugly column to delete";
		System.out.println(tc.insertData(e).craft());
		m.exec(tc.insertData(e).craft());
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: " + m.getErrMsg());
		System.out.println(tc.deleteData(e).craft());
		m.exec(tc.deleteData(e).craft());
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: " + m.getErrMsg());

		e = new Entity(); e.identity=3; e.name="STEVEN"; e.description="strange magic mix of diamond and  kid";
		System.out.println(tc.insertData(e).craft());
		m.exec(tc.insertData(e).craft());
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: " + m.getErrMsg());
		
		e = new Entity(); e.identity=4; e.name="Link"; e.description="he come to town Come to save the princess Zelda ";
		System.out.println(tc.insertData(e).craft());
		m.exec(tc.insertData(e).craft());
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: " + m.getErrMsg());
		
		
		SelectCraft sel=new SQLSelectCraft().DB("DBName").table("Entity");
		System.out.println(sel.craft());
		List<Entity> res=m.queryList(Entity.class, sel.craft());
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: " + m.getErrMsg());
		for(Entity ent : res ) {
			 System.out.println(ent.identity+" "+ent.name+" "+ent.description);
		}
		
		Map<String,Object> []amap=m.queryMap(sel.craft());
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: \n" + m.getErrMsg());
		for(Map<String,Object> map: amap) for(Entry<String,Object> kv : map.entrySet()) {
			 System.out.println(kv.getKey()+" "+kv.getValue().getClass()+" "+kv.getValue());
		}
		
		
		
	}
	

}
