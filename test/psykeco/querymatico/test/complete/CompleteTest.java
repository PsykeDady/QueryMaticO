package psykeco.querymatico.test.complete;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import psykeco.querymatico.ConnectionMaticO;
import psykeco.querymatico.DBMaticO;
import psykeco.querymatico.SelectMaticO;
import psykeco.querymatico.TableMaticO;
import psykeco.querymatico.sql.SQLConnectionMaticO;
import psykeco.querymatico.sql.SQLDBMaticO;
import psykeco.querymatico.sql.SQLSelectMaticO;
import psykeco.querymatico.sql.SQLTableMaticO;
import psykeco.querymatico.sql.runners.MySqlConnection;

class CompleteTest {
	
	String expected=null;
	String actual=null;

	public static class Entity {
		private int identity;
		private String name;
		private String description;
		private Date lastUpdate;
		private GregorianCalendar nextUpdate;
		private LocalDateTime firstUpdate;
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
		ConnectionMaticO cnnf= new SQLConnectionMaticO().psk(psk).autocommit(true);
		//1st check connection
		expected="jdbc:mysql://localhost:3306?useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
		actual=cnnf.build();
		System.out.println(actual);
		assertEquals(expected, actual);
		
		MySqlConnection.createConnection((SQLConnectionMaticO) cnnf);
		MySqlConnection m = new MySqlConnection();
		DBMaticO dbc = new SQLDBMaticO().DB("DBName");
		TableMaticO tc = new SQLTableMaticO().DB("DBName").table(Entity.class).primary("identity");
		try {
			
			//eventually drop database
			m.exec(dbc.drop());
			
			//2nd check create database
			expected="CREATE DATABASE `DBName`";
			actual=dbc.create();
			System.out.println(actual);
			assertEquals(expected, actual);
			
			m.exec(actual);
			if (!m.getErrMsg().equals(""))
				throw new IllegalStateException("an error occur: " + m.getErrMsg());

			// 3rd check create table
			expected="CREATE TABLE `DBName`.`Entity` (identity INT,lastUpdate TIMESTAMP null ,name TEXT,description TEXT,firstUpdate TIMESTAMP null ,nextUpdate TIMESTAMP null ,PRIMARY KEY(identity))";
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
	
	
	void completeTest(TableMaticO tc, MySqlConnection m) {
		List<Entity>expList= new ArrayList<Entity>(4);
		// insert data
		Entity e = new Entity(); e.identity=1; e.name="DOGE"; e.description="funny dog";
		e.firstUpdate=LocalDateTime.of(2020,2,24, 0, 0, 0);
		e.lastUpdate=Date.from(LocalDateTime.of(2020,2,24, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant());
		e.nextUpdate=GregorianCalendar.from(LocalDateTime.of(2020,2,24, 0, 0, 0).atZone(ZoneId.systemDefault()));
		expList.add(e);
		// 4th check insert data 1
		expected="INSERT INTO `DBName`.`Entity` ( `identity`,`lastUpdate`,`name`,`description`,`firstUpdate`,`nextUpdate`) VALUES (1,'2020-02-24T00:00:00','DOGE','funny dog','2020-02-24T00:00:00','2020-02-24T00:00:00')";
		actual=tc.insertData(e).build();
		System.out.println(actual);
		assertEquals(expected, actual);
		
		m.exec(actual);
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: " + m.getErrMsg());

		e = new Entity(); e.identity=2; e.name="MARIO"; e.description="italian plumber";
		e.firstUpdate=LocalDateTime.of(2020,2,24, 1, 2, 3);
		e.lastUpdate=null;
		e.nextUpdate=null;
		expList.add(e);
		// 5th check insert data 2
		expected="INSERT INTO `DBName`.`Entity` ( `identity`,`name`,`description`,`firstUpdate`) VALUES (2,'MARIO','italian plumber','2020-02-24T01:02:03')";
		actual=tc.insertData(e).build();
		System.out.println(actual);
		assertEquals(expected, actual);
		
		m.exec(actual);
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: " + m.getErrMsg());

		e = new Entity(); e.identity=3; e.name="Ugly"; e.description="ugly column to delete";
		// 6th check insert data 3
		expected="INSERT INTO `DBName`.`Entity` ( `identity`,`name`,`description`) VALUES (3,'Ugly','ugly column to delete')";
		actual=tc.insertData(e).build();
		System.out.println(actual);
		assertEquals(expected, actual);
		
		m.exec(actual);
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: " + m.getErrMsg());
		
		// 7th check delete data 
		expected="DELETE FROM `DBName`.`Entity` WHERE 1=1 AND `identity`=3 AND `name`='Ugly' AND `description`='ugly column to delete'";
		actual=tc.deleteData(e).build();
		System.out.println(actual);
		assertEquals(expected, actual);
		
		m.exec(actual);
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: " + m.getErrMsg());

		e = new Entity(); e.identity=3; e.name="STEVEN"; e.description="strange magic mix of diamond and  kid";
		// 8th check insert data 4
		expList.add(e);
		expected="INSERT INTO `DBName`.`Entity` ( `identity`,`name`,`description`) VALUES (3,'STEVEN','strange magic mix of diamond and  kid')";
		actual=tc.insertData(e).build();
		System.out.println(actual);
		assertEquals(expected, actual);
		
		m.exec(actual);
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: " + m.getErrMsg());
		
		e = new Entity(); e.identity=4; e.name="Link"; e.description="he come to town Come to save the princess Zelda";
		// 9th check insert data 5
		expList.add(e);
		expected="INSERT INTO `DBName`.`Entity` ( `identity`,`name`,`description`) VALUES (4,'Link','he come to town Come to save the princess Zelda')";
		actual=tc.insertData(e).build();
		System.out.println(actual);
		assertEquals(expected, actual);
		
		m.exec(actual);
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: " + m.getErrMsg());
		
		
		SelectMaticO sel=new SQLSelectMaticO().DB("DBName").table("Entity");
		// 10th check select build
		expected="SELECT * FROM `DBName`.`Entity` WHERE 1=1";
		actual=sel.build();
		System.out.println(actual);
		assertEquals(expected, actual);
		
		List<Entity> res=m.queryList(Entity.class, actual);
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: " + m.getErrMsg());
		int i=0;
		
		
		for(Entity ent : res ) {
			// 11th check list values from select 1
			expected=expList.get(i).identity+" "+expList.get(i).name+" "+expList.get(i).description;
			actual=ent.identity+" "+ent.name+" "+ent.description;
			System.out.println(actual);
			assertEquals(expected, actual);
			i++;
		}
		
		// 12th check select all data
		expected="SELECT * FROM `DBName`.`Entity` WHERE 1=1";
		actual=tc.selectData(null).build();
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
			assertEquals(en1.firstUpdate, en2.firstUpdate);
			assertEquals(en1.lastUpdate , en2.lastUpdate );
			assertEquals(en1.nextUpdate , en2.nextUpdate );
		}
		assertEquals(it.hasNext(), it2.hasNext());
		
		Map<String,Object> []amap=m.queryMap(sel.build());
		if (!m.getErrMsg().equals(""))
			throw new IllegalStateException("an error occur: \n" + m.getErrMsg());
		
		SimpleDateFormat sd=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String dateFormat="%04d-%02d-%02dT%02d:%02d:%02d";
		for(Map<String,Object> map: amap) {
			// 12th check map values from select
			
			Entity ent=expList.stream().filter( ente -> map.get("identity").equals(ente.identity) ).findFirst().get();
			
			for(Entry<String,Object> kv : map.entrySet()) {
				String data="";
				switch(kv.getKey()) {
					case "identity" 	: expected="identity "   +new Integer(ent.identity).getClass()
							+" "+ent.identity   ; break;
					case "name" 		: expected=ent.name==null?"null":"name "       +ent.name.getClass()                 
							+" "+ent.name       ; break;
					case "description" 	: expected=ent.description==null?"null":"description "+ent.description.getClass()          
							+" "+ent.description; break;
					case "lastUpdate" 	:
						if(ent.lastUpdate!=null) data=sd.format(ent.lastUpdate);
						expected=ent.lastUpdate==null? "null" : "lastUpdate "+java.sql.Timestamp.class          
							+" "+data; 
						break;
					case "firstUpdate" 	: 
						if(ent.firstUpdate!=null) data=String.format(dateFormat, ent.firstUpdate.getYear(),ent.firstUpdate.getMonthValue(),ent.firstUpdate.getDayOfMonth(),ent.firstUpdate.getHour(),ent.firstUpdate.getMinute(),ent.firstUpdate.getSecond());
						expected=ent.firstUpdate==null?"null":"firstUpdate "+java.sql.Timestamp.class          
							+" "+data; 
						break;
					case "nextUpdate" 	: 
						if(ent.nextUpdate!=null) data=String.format(dateFormat, ent.nextUpdate.get(GregorianCalendar.YEAR),ent.nextUpdate.get(GregorianCalendar.MONTH)+1,ent.nextUpdate.get(GregorianCalendar.DAY_OF_MONTH),ent.nextUpdate.get(GregorianCalendar.HOUR_OF_DAY),ent.nextUpdate.get(GregorianCalendar.MINUTE),ent.nextUpdate.get(GregorianCalendar.SECOND));
						expected=ent.nextUpdate==null?"null":"nextUpdate "+java.sql.Timestamp.class       
							+" "+data; 
						break;
				}
				
				if(kv.getValue()==null) actual="null";
				else if(kv.getValue() instanceof java.sql.Timestamp) {
					java.sql.Timestamp tm=((java.sql.Timestamp)kv.getValue());
					Calendar c=Calendar.getInstance();
					c.setTime(tm);
					c.add(Calendar.HOUR,-1);
					data=sd.format(c.getTime());
					actual=kv.getKey()+" "+kv.getValue().getClass()+" "+data;
				}
				else actual=kv.getKey()+" "+kv.getValue().getClass()+" "+kv.getValue();
				System.out.println(actual);
				assertEquals(expected, actual);
			}
			
		}
		
	}
	

}
