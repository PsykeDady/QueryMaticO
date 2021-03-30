package psykeco.querymatico.test;

import java.io.File;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import psykeco.querymatico.ConnectionMaticO;
import psykeco.querymatico.DBMaticO;
import psykeco.querymatico.TableMaticO;
import psykeco.querymatico.sql.SQLConnectionMaticO;
import psykeco.querymatico.sql.SQLDBMaticO;
import psykeco.querymatico.sql.SQLTableMaticO;
import psykeco.querymatico.sql.runners.InformationSchema;
import psykeco.querymatico.sql.runners.MySqlConnection;

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
		ConnectionMaticO cnnf= new SQLConnectionMaticO().psk(psk).autocommit(true);
		//1st check connection
		
		MySqlConnection.createConnection((SQLConnectionMaticO) cnnf);
		MySqlConnection mysql = new MySqlConnection();
		DBMaticO dbc = new SQLDBMaticO().DB(DBNAME);
		TableMaticO tc = new SQLTableMaticO().DB(DBNAME).table(Entita.class);
		if(!InformationSchema.listDB().contains(DBNAME)) mysql.exec(dbc.create());
		if(!InformationSchema.existsTable(DBNAME, Entita.class))
			mysql.exec(tc.copy().primary("id").create());
		
		Entita e= new Entita(1,"nome",0);
		
		List<Entita> l=mysql.queryList(Entita.class,tc.selectData(null).build());
		if(l==null||! l.contains(e)) mysql.exec(tc.insertData(e).build());
		
		mysql.exec(tc.updateData(e).entry("n", null).build());
		if(!mysql.getErrMsg().equals("")) throw new IllegalArgumentException(mysql.getErrMsg());
		
		l=mysql.queryList(Entita.class, tc.selectData(null).build());
		if(!mysql.getErrMsg().equals("")) throw new IllegalArgumentException(mysql.getErrMsg());

		
		for(Entita a:l) {
			System.out.println(a);
		}
		
		mysql.exec(tc.copy().primary("id").deleteData(e).build());
	}

}
