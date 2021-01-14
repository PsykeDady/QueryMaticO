package psykeco.querycraft.test;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
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

		public Entity(int identity, String name, String description) {
			setIdentity(identity);
			setName(name);
			setDescription(description);
		}

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
		SQLConnectionCraft s = (SQLConnectionCraft) 
				new SQLConnectionCraft().psk(psk);
		MySqlConnection m = new MySqlConnection(s);

		// create db
		DBCraft dbc = new SQLDBCraft().DB("DBName");
		m.exec(dbc.create());
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: " + m.getErrMsg());

		// create table
		TableCraft tc = new SQLTableCraft().DB("DBName").table(Entity.class).primary("identity");
		m.exec(tc.create());
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: " + m.getErrMsg());

		// insert data
		Entity e = new Entity(1, "DOGE", "funny dog");
		m.exec(tc.insertData(e).craft());
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: " + m.getErrMsg());

		e = new Entity(2, "MARIO", "italian plumber"); // con 1 non da errore... FIXME
		m.exec(tc.insertData(e).craft());
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: " + m.getErrMsg());

		e = new Entity(3, "STEVEN", "strange magic mix of diamond and a kid");
		m.exec(tc.insertData(e).craft());
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: " + m.getErrMsg());
		
		SelectCraft sel=new SQLSelectCraft().DB("DBName").table("Entity");
		ResultSet rs=m.query(sel.craft());
		
		try {
			while (rs.next()) {
				System.out.println(rs.getInt("identity")+" "+rs.getString("name")+" "+rs.getString("description"));
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
	
	

}
