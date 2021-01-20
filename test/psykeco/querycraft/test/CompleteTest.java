package psykeco.querycraft.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import psykeco.querycraft.ConnectionCraft;
import psykeco.querycraft.DBCraft;
import psykeco.querycraft.SelectCraft;
import psykeco.querycraft.TableCraft;
import psykeco.querycraft.sql.MySqlConnection;
import psykeco.querycraft.sql.SQLConnectionCraft;
import psykeco.querycraft.sql.SQLDBCraft;
import psykeco.querycraft.sql.SQLSelectCraft;
import psykeco.querycraft.sql.SQLTableCraft;

class CompleteTest {
	
	String expected=null;
	String actual=null;

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
		System.out.println("#### INIZIO COMPLETE TEST ####");
		File pskf=new File("psk");
		System.out.println(pskf.getAbsolutePath());
		String psk="";
		try(Scanner sc=new Scanner(pskf)){
			psk=sc.nextLine();
		} catch(Exception e) {}
		ConnectionCraft cnnf= new SQLConnectionCraft().psk(psk).autocommit(true);
		//1st check connection
		expected="jdbc:mysql://localhost:3306";
		actual=cnnf.craft();
		System.out.println(actual);
		assertEquals(expected, actual);
		
		MySqlConnection.createConnection((SQLConnectionCraft) cnnf);
		MySqlConnection m = new MySqlConnection();
		DBCraft dbc = new SQLDBCraft().DB("DBName");
		TableCraft tc = new SQLTableCraft().DB("DBName").table(Entity.class).primary("identity");
		try {
			//2nd check create database
			expected="CREATE DATABASE `DBName`";
			actual=dbc.create();
			System.out.println(actual);
			assertEquals(expected, actual);
			
			m.exec(actual);
			if (!m.getErrMsg().equals(""))
				throw new IllegalStateException("an error occur: " + m.getErrMsg());

			// 3rd check create table
			expected="CREATE TABLE `DBName`.`Entity` (identity INT,name TEXT,description TEXT,PRIMARY KEY(identity))";
			actual=tc.create();
			System.out.println(actual);
			assertEquals(expected, actual);
			
			m.exec(actual);
			if (!m.getErrMsg().equals(""))
				throw new IllegalStateException("an error occur: " + m.getErrMsg());
			
			completeTest(tc, m);
		} finally {
			// reset
			
			// nth check drop table
			expected="DROP TABLE IF EXISTS `DBName`.`Entity`";
			actual=tc.drop();
			System.out.println(actual);
			assertEquals(expected, actual);
			
			m.exec(actual);
			if (!m.getErrMsg().equals(""))
				System.out.println("an error occur: \n" + m.getErrMsg());
			
			// nth check drop db
			expected="DROP DATABASE `DBName`";
			actual=dbc.drop();
			System.out.println(actual);
			assertEquals(expected, actual);
			
			m.exec(actual);
			if (!m.getErrMsg().equals(""))
				System.out.println("an error occur: \n" + m.getErrMsg());
		}
		System.out.println("#### FINE   COMPLETE TEST ####");

	}
	
	
	void completeTest(TableCraft tc, MySqlConnection m) {
		// insert data
		Entity e = new Entity(); e.identity=1; e.name="DOGE"; e.description=("funny dog");
		// 4th check insert data 1
		expected="INSERT INTO `DBName`.`Entity` ( `identity`,`name`,`description`) VALUES (1,'DOGE','funny dog')";
		actual=tc.insertData(e).craft();
		System.out.println(actual);
		assertEquals(expected, actual);
		
		m.exec(actual);
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: " + m.getErrMsg());

		e = new Entity(); e.identity=2; e.name="MARIO"; e.description="italian plumber";
		// 5th check insert data 2
		expected="INSERT INTO `DBName`.`Entity` ( `identity`,`name`,`description`) VALUES (2,'MARIO','italian plumber')";
		actual=tc.insertData(e).craft();
		System.out.println(actual);
		assertEquals(expected, actual);
		
		m.exec(actual);
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: " + m.getErrMsg());

		e = new Entity(); e.identity=3; e.name="Ugly"; e.description="ugly column to delete";
		// 6th check insert data 3
		expected="INSERT INTO `DBName`.`Entity` ( `identity`,`name`,`description`) VALUES (3,'Ugly','ugly column to delete')";
		actual=tc.insertData(e).craft();
		System.out.println(actual);
		assertEquals(expected, actual);
		
		m.exec(actual);
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: " + m.getErrMsg());
		
		// 7th check delete data 
		expected="DELETE FROM `DBName`.`Entity` WHERE 1=1 AND `identity`=3 AND `name`='Ugly' AND `description`='ugly column to delete'";
		actual=tc.deleteData(e).craft();
		System.out.println(actual);
		assertEquals(expected, actual);
		
		m.exec(actual);
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: " + m.getErrMsg());

		e = new Entity(); e.identity=3; e.name="STEVEN"; e.description="strange magic mix of diamond and  kid";
		// 8th check insert data 4
		expected="INSERT INTO `DBName`.`Entity` ( `identity`,`name`,`description`) VALUES (3,'STEVEN','strange magic mix of diamond and  kid')";
		actual=tc.insertData(e).craft();
		System.out.println(actual);
		assertEquals(expected, actual);
		
		m.exec(actual);
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: " + m.getErrMsg());
		
		e = new Entity(); e.identity=4; e.name="Link"; e.description="he come to town Come to save the princess Zelda";
		// 9th check insert data 5
		expected="INSERT INTO `DBName`.`Entity` ( `identity`,`name`,`description`) VALUES (4,'Link','he come to town Come to save the princess Zelda')";
		actual=tc.insertData(e).craft();
		System.out.println(actual);
		assertEquals(expected, actual);
		
		m.exec(actual);
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: " + m.getErrMsg());
		
		
		SelectCraft sel=new SQLSelectCraft().DB("DBName").table("Entity");
		// 10th check select craft
		expected="SELECT * FROM `DBName`.`Entity` WHERE 1=1";
		actual=sel.craft();
		System.out.println(actual);
		assertEquals(expected, actual);
		
		List<Entity> res=m.queryList(Entity.class, actual);
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: " + m.getErrMsg());
		int i=0;
		Entity e1=new Entity(),e2=new Entity(),e3=new Entity(),e4=new Entity();
		e1.identity=1;e1.name="DOGE"  ;e1.description="funny dog";
		e2.identity=2;e2.name="MARIO" ;e2.description="italian plumber";
		e3.identity=3;e3.name="STEVEN";e3.description="strange magic mix of diamond and  kid";
		e4.identity=4;e4.name="Link"  ;e4.description="he come to town Come to save the princess Zelda";
		Entity[]expList= { e1,e2,e3,e4	};
		for(Entity ent : res ) {
			// 11th check list values from select 1
			expected=expList[i].identity+" "+expList[i].name+" "+expList[i].description;
			actual=ent.identity+" "+ent.name+" "+ent.description;
			System.out.println(actual);
			assertEquals(expected, actual);
			i++;
		}
		
		// 12th check select all data
		expected="SELECT * FROM `DBName`.`Entity` WHERE 1=1";
		actual=tc.selectData(null).craft();
		System.out.println(actual);
		assertEquals(expected, actual);
		
		List<Entity> res2=m.queryList(Entity.class, actual);
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: " + m.getErrMsg());
		
		// 13th check list values from select 2
		Iterator<Entity> it=res.iterator(), it2=res2.iterator();
		while (it.hasNext() && it2.hasNext()) {
			Entity en1=it.next(),en2=it2.next();
			assertEquals(en1.identity   , en2.identity   );
			assertEquals(en1.name       , en2.name       );
			assertEquals(en1.description, en2.description);
		}
		assertEquals(it.hasNext(), it2.hasNext());
		
		Map<String,Object> []amap=m.queryMap(sel.craft());
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: \n" + m.getErrMsg());
		
		i=0; int j=0;
		for(Map<String,Object> map: amap) {
			// 12th check map values from select
			for(Entry<String,Object> kv : map.entrySet()) {
				switch(j) {
					case 0 : expected="identity "   +new Integer(expList[i].identity).getClass()
							+" "+expList[i].identity   ; break;
					case 1 : expected="name "       +expList[i].name.getClass()                 
							+" "+expList[i].name       ; break;
					case 2 : expected="description "+expList[i].description.getClass()          
							+" "+expList[i].description; break;
				}
				
				actual=kv.getKey()+" "+kv.getValue().getClass()+" "+kv.getValue();
				System.out.println(actual);
				assertEquals(expected, actual);
				j++;
			}
			i++;j=0;
		}
		
	}
	

}
