package psykeco.querycraft.test;

import java.io.File;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import psykeco.querycraft.ConnectionCraft;
import psykeco.querycraft.DBCraft;
import psykeco.querycraft.TableCraft;
import psykeco.querycraft.sql.SQLConnectionCraft;
import psykeco.querycraft.sql.SQLDBCraft;
import psykeco.querycraft.sql.SQLTableCraft;
import psykeco.querycraft.sql.runners.InformationSchema;
import psykeco.querycraft.sql.runners.MySqlConnection;

class QueryListTest {
	
	static class Entita {
		int id;
		String name;
		int n;
		
		public Entita(){}
		Entita(int id, String name, int n){
			this.id=id;
			this.name=name;
			this.n=n;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj==null) return false;
			if(!(obj instanceof Entita)) return false;
			Entita altra=(Entita) obj;
			return id==altra.id;
		}
		
		@Override
		public String toString() {
			
			return "id="+id+"name="+name+"n="+n;
		}
	}

	@Test
	void test() {
		final String DBNAME="DBName";
		File pskf=new File("psk");
		System.out.println(pskf.getAbsolutePath());
		String psk="";
		try(Scanner sc=new Scanner(pskf)){
			psk=sc.nextLine();
		} catch(Exception e) {}
		ConnectionCraft cnnf= new SQLConnectionCraft().psk(psk).autocommit(true);
		//1st check connection
		
		MySqlConnection.createConnection((SQLConnectionCraft) cnnf);
		MySqlConnection mysql = new MySqlConnection();
		DBCraft dbc = new SQLDBCraft().DB(DBNAME);
		TableCraft tc = new SQLTableCraft().DB(DBNAME).table(Entita.class);
		if(!InformationSchema.listDB().contains(DBNAME)) mysql.exec(dbc.create());
		if(!InformationSchema.existsTable(DBNAME, Entita.class))
			mysql.exec(tc.copy().primary("id").create());
		
		Entita e= new Entita(1,"nome",0);
		
		List<Entita> l=mysql.queryList(Entita.class,tc.selectData(null).craft());
		if(l==null||! l.contains(e)) mysql.exec(tc.insertData(e).craft());
		
		mysql.exec(tc.updateData(e).entry("n", null).craft());
		if(!mysql.getErrMsg().equals("")) throw new IllegalArgumentException(mysql.getErrMsg());
		
		l=mysql.queryList(Entita.class, tc.selectData(null).craft());
		if(!mysql.getErrMsg().equals("")) throw new IllegalArgumentException(mysql.getErrMsg());

		
		for(Entita a:l) {
			System.out.println(a);
		}
		
		mysql.exec(tc.copy().primary("id").deleteData(e).craft());
	}

}
