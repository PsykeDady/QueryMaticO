package psykeco.querycraft.sql.runners;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import psykeco.querycraft.TableCraft;
import psykeco.querycraft.sql.SQLTableCraft;
import psykeco.querycraft.sql.models.Tables;
import psykeco.querycraft.sql.utility.SQLClassParser;

/**
 * InformationSchema perform query on `information_schema` db and `tables` table in order to receive tables and schemas 
 * informations. <br>
 * For this purpose, class {@link Tables} is used to mapping column to get table name and db name
 * 
 * @author psykedady
 * */
public final class InformationSchema {
	
	/**private constructor*/
	private InformationSchema() {}
	
	/** Information schema DB name */
	public final static String DB = "information_schema";
	/** label for table_name column */
	public final static String TABLE_NAME  ="table_name";
	/** label for table_schema (db name) column */
	public final static String TABLE_SCHEMA="table_schema";
	/** reserved table_name for mysql */
	public final static String INFORMATION_SCHEMA="information_schema";
	/** reserved table_name for mysql (information schema) */
	public final static String MYSQL="mysql";
	/** reserved table_name for mysql (performance schema) */
	public final static String PERFORMANCE_SCHEMA="performance schema";
	
	private final static TableCraft tcf= new SQLTableCraft().DB(DB).table(Tables.class);
	
	/* * STATIC METHODS * */
	
	/* queries */
	/**
	 * get a copy of InformationSchema's TableCraft 
	 * @return
	 */
	public static TableCraft getCraftCopy() {
		return tcf.copy();
	}
	
	public static String listDBCraft() {
		return tcf.selectData(null).distinct(TABLE_SCHEMA).craft();
	}
	
	public static String existsDBCraft(String db) {
		Tables tab=new Tables();
		tab.setTableSchema(db);
		
		return tcf.copy().primary(TABLE_SCHEMA).selectData(tab).distinct(TABLE_SCHEMA).craft();
	}
	
	public static String listTablesCraft(String db) {
		Tables tab=new Tables();
		tab.setTableSchema(db);
		
		return	tcf.copy().primary(TABLE_SCHEMA).selectData(tab).craft();
	}
	
	public static  String existTableCraft(String db, Class<?> table) {
		return existTableCraft(db,  SQLClassParser.getTrueName(table));
	}
	
	public static  String existTableCraft(String db, String table) {
		Tables tab=new Tables();
		tab.setTableSchema(db);
		tab.setTableName(table);
		
		return	tcf.copy().primary(TABLE_SCHEMA).primary(TABLE_NAME).selectData(tab).craft();
	}
	
	/**
	 * 
	 * @return list of all db 
	 */
	public static List<String> listDB () {
		if(!MySqlConnection.existConnection()) return null; 
		
		MySqlConnection mysql= new MySqlConnection(); 		
		
		String query= listDBCraft();
		List<Tables> list=mysql.queryList(Tables.class, query);
		
		List<String> dbs= new ArrayList<String>(list.size());
		for(Tables t: list) dbs.add(t.getTableSchema());
		
		return dbs;
	}
	
	/**
	 * @param db 
	 * @return <code>true</code> if database exists, <code>false</code> otherwise.<br><code>null</code> if an error occur
	 * */
	public static Boolean existsDB(String db) {
		if(!MySqlConnection.existConnection()) return null; 
		
		MySqlConnection mysql= new MySqlConnection(); 
		
		List<Tables> list=mysql.queryList(Tables.class, existsDBCraft(db));
		
		
		return list!=null && list.size()>0;
	}
	
	/**
	 * make a list of all tables of given db
	 * 
	 * @param db
	 * @return an instance of <code>List&lt;String&gt;</code> contains all db tables
	 */
	public static List<String> listTables(String db){
		if(!MySqlConnection.existConnection()) return null; 
		
		MySqlConnection mysql= new MySqlConnection(); 		
		
		List<Tables> list=mysql.queryList(Tables.class, listTablesCraft(db));
		List<String> tables= new ArrayList<String>(list.size());
		for(Tables t: list) tables.add(t.getTableSchema());
		
		return tables;
	}
	
	/**
	 * @param table, class to parse
	 * @return <code>true</code> if table exists in given db, <code>false</code> otherwise.<br><code>null</code> if an error occur
	 * */
	public static Boolean existsTable (String db, Class<?> table) {
		return existsTable(db, SQLClassParser.getTrueName(table));
	}
	
	/**
	 * @param table
	 * @return <code>true</code> if table exists in given db, <code>false</code> otherwise.<br><code>null</code> if an error occur
	 * */
	public static Boolean existsTable(String db, String table) {
		if(!MySqlConnection.existConnection()) return null; 
		
		MySqlConnection mysql= new MySqlConnection(); 
		
		
		
		List<Tables> list=mysql.queryList(Tables.class, existTableCraft(db, table));
		
		
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
