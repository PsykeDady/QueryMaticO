package psykeco.querycraft.sql;

import java.sql.ResultSet;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import psykeco.querycraft.TableCraft;
import psykeco.querycraft.sql.models.Tables;
import psykeco.querycraft.utility.SQLClassParser;

public final class InformationSchema {
	
	private InformationSchema() {}
	
	public final static String DB = "information_schema";
	public final static String TABLE_NAME  ="table_name";
	public final static String TABLE_SCHEMA="table_schema";
	private final static TableCraft tcf= new SQLTableCraft().DB(DB).table(Tables.class);
	
	
	public static List<String> listDB () {
		if(!MySqlConnection.existConnection()) return null; 
		
		MySqlConnection mysql= new MySqlConnection(); 		
		
		String query= tcf.selectData(null).distinct(TABLE_SCHEMA).craft();
		List<Tables> list=mysql.queryList(Tables.class, query);
		
		List<String> dbs= new ArrayList<String>(list.size());
		for(Tables t: list) dbs.add(t.getTableSchema());
		
		return dbs;
	}
	
	public static List<String> listTables(String db){
		if(!MySqlConnection.existConnection()) return null; 
		
		MySqlConnection mysql= new MySqlConnection(); 		
		Tables tab=new Tables();
		tab.setTableSchema(db);
		
		List<Tables> list=mysql.queryList(Tables.class, 
				tcf.copy().primary(TABLE_SCHEMA).selectData(tab).craft());
		List<String> tables= new ArrayList<String>(list.size());
		for(Tables t: list) tables.add(t.getTableSchema());
		
		return tables;
	}
	
	public static Boolean existsTable (String db, Class<?> table) {
		return existsTable(db, SQLClassParser.getTrueName(table));
	}
	
	public static Boolean existsTable(String db, String table) {
		if(!MySqlConnection.existConnection()) return null; 
		
		MySqlConnection mysql= new MySqlConnection(); 
		
		Tables tab=new Tables();
		tab.setTableSchema(db);
		tab.setTableName(table);
		
		List<Tables> list=mysql.queryList(Tables.class, 
				tcf.copy().primary(TABLE_SCHEMA).primary(TABLE_NAME).selectData(tab).craft());
		
		
		return list!=null && list.size()>0;
	}
	
	/**
	 * 
	 * @return a list of entries with key=schema value=table
	 */
	public static List<Entry<String,String>> getAllInfo(){
		if(!MySqlConnection.existConnection()) return null; 
		
		MySqlConnection mysql= new MySqlConnection();
		
		List<Tables> list=mysql.queryList(Tables.class, 
				tcf.selectData(null).craft());
		List<Entry<String,String>> schemaTable= new LinkedList<>();
		for(Tables t: list) schemaTable.add(new SimpleEntry<>(t.getTableSchema(),t.getTableName()));
		
		return schemaTable;
	}	

}
