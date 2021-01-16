package psykeco.querycraft.test;

import java.io.File;
import java.util.List;
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

		// create db
		DBCraft dbc = new SQLDBCraft().DB("DBName");
		System.out.println(dbc.create());
		m.exec(dbc.create());
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: " + m.getErrMsg());

		// create table
		TableCraft tc = new SQLTableCraft().DB("DBName").table(Entity.class).primary("identity");
		System.out.println(tc.create());
		m.exec(tc.create());
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: " + m.getErrMsg());

		// insert data
		Entity e = new Entity(); e.identity=1; e.name="DOGE"; e.description=("funny dog");
		System.out.println(tc.insertData(e).craft());
		m.exec(tc.insertData(e).craft());
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: " + m.getErrMsg());

		e = new Entity(); e.identity=2; e.name="MARIO"; e.description="italian plumber"; // con 1 non da errore... FIXME
		System.out.println(tc.insertData(e).craft());
		m.exec(tc.insertData(e).craft());
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: " + m.getErrMsg());

		e = new Entity(); e.identity=3; e.name="STEVEN"; e.description="strange magic mix of diamond and a kid";
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
		
		

	}
	
	

}
