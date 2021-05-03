package psykeco.querymatico.sql.runners;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import psykeco.querymatico.TableMaticO;
import psykeco.querymatico.sql.SQLTableMaticO;
import psykeco.querymatico.sql.models.Tables;
import psykeco.querymatico.sql.utility.SQLClassParser;

/**
 * <p>InformationSchema perform query on `information_schema` db and `tables` table in order to receive tables and schemas 
 * informations. </br></p>
 * <p>For this purpose, class {@link psykeco.querymatico.sql.models.Tables Tables} is used to mapping column to get table name and db name</br></p>
 * 
 * @author PsykeDady (psdady@msn.com)
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
	/** TableMaticO for Tables query */
	private final static TableMaticO tcf= new SQLTableMaticO().DB(DB).table(Tables.class);
	
	/* * STATIC METHODS * */
	
	/* queries */

	/**
	 * get a copy of InformationSchema's {@link TableMaticO} 
	 * @return
	 */
	public static TableMaticO getMaticOCopy() {
		return tcf.copy();
	}
	
	/**
	 * build a query to list every distinct db 
	 * @return select query of all db
	 */
	public static String listDBBuild() {
		return tcf.selectData(null).distinct(TABLE_SCHEMA).build();
	}
	
	/**
	 * build a query to check existance of specific db
	 * @param db database to query existance 
	 * @return 'select' query of db
	 */
	public static String existsDBBuild(String db) {
		Tables tab=new Tables();
		tab.setTableSchema(db);
		
		return tcf.copy().primary(TABLE_SCHEMA).selectData(tab).distinct(TABLE_SCHEMA).build();
	}
	
	/**
	 * build a query to list all tables into a db
	 * @param db : database that contains the queries
	 * @return 'select' query of all db tables
	 */
	public static String listTablesBuild(String db) {
		Tables tab=new Tables();
		tab.setTableSchema(db);
		
		return	tcf.copy().primary(TABLE_SCHEMA).selectData(tab).build();
	}
	
	/**
	 * build a query to check existance of specific db table
	 * @param db database that contains the table
	 * @param table the class of table
	 * @return 'select' query of table existance
	 */
	public static String existTableBuild(String db, Class<?> table) {
		return existTableBuild(db,  SQLClassParser.getTrueName(table));
	}
	
	/**
	 * build a query to check existance of specific db table
	 * @param db database that contains the table
	 * @param table the table (name of)
	 * @return 'select' query of table existance
	 */
	public static String existTableBuild(String db, String table) {
		Tables tab=new Tables();
		tab.setTableSchema(db);
		tab.setTableName(table);
		
		return	tcf.copy().primary(TABLE_SCHEMA).primary(TABLE_NAME).selectData(tab).build();
	}
	
	/**
	 * if an instance of {@link MySqlConnection} is active, it returns a list of all db ( using {@link #listDBBuild()} as query )
	 * @return {@link java.util.List List}&lt;{@link java.lang.String String}&gt;  of all db 
	 */
	public static List<String> listDB () {
		if(!MySqlConnection.existConnection()) return null; 
		
		MySqlConnection mysql= new MySqlConnection(); 		
		
		String query= listDBBuild();
		List<Tables> list=mysql.queryList(Tables.class, query);
		
		List<String> dbs= new ArrayList<String>(list.size());
		for(Tables t: list) dbs.add(t.getTableSchema());
		
		return dbs;
	}
	
	/**
	 * <p>if an instance of {@link MySqlConnection} is active, it returns a {@link Boolean} describe that input db existance or not.</br></p>
	 * <p>The query is build with {@link #existsDBBuild(String)}</br></p>
	 * 
	 * @param db name of db
	 * @return <code>true</code> if database exists, <code>false</code> otherwise.<br><code>null</code> if an error occur ( MySqlConnection not connected )
	 * */
	public static Boolean existsDB(String db) {
		if(!MySqlConnection.existConnection()) return null; 
		
		MySqlConnection mysql= new MySqlConnection(); 
		
		List<Tables> list=mysql.queryList(Tables.class, existsDBBuild(db));
		
		
		return list!=null && list.size()>0;
	}
	
	/**
	 * <p>if an instance of {@link MySqlConnection} is active, it returns a {@link java.util.List List} contains all table of given db.</br></p>
	 * <p>The query is build with {@link #listTablesBuild(String)</br></p>
	 * 
	 * @param db name of db
	 * @return {@link java.util.List List}&lt;{@link java.lang.String String}&gt;  of all db's Table
	 * */
	public static List<String> listTables(String db){
		if(!MySqlConnection.existConnection()) return null; 
		
		MySqlConnection mysql= new MySqlConnection(); 		
		
		List<Tables> list=mysql.queryList(Tables.class, listTablesBuild(db));
		List<String> tables= new ArrayList<String>(list.size());
		for(Tables t: list) tables.add(t.getTableSchema());
		
		return tables;
	}
	
	/**
	 * <p>if an instance of {@link MySqlConnection} is active, it returns a {@link Boolean} describe given db contains table or not.</br></p>
	 * <p>The query is build with {@link #existTableBuild(String, String)}</br></p>
	 * 
	 * @param db name of db
	 * @param table class of table
	 * @return <code>true</code> if table exists, <code>false</code> otherwise.<br><code>null</code> if an error occur ( MySqlConnection not connected )
	 * */
	public static Boolean existsTable (String db, Class<?> table) {
		return existsTable(db, SQLClassParser.getTrueName(table));
	}
	
	/**
	 * <p>if an instance of {@link MySqlConnection} is active, it returns a {@link Boolean} describe given db contains table or not.</br></p>
	 * <p>The query is build with {@link #existTableBuild(String, String)}</br></p>
	 * 
	 * @param db name of db
	 * @param table name of table
	 * @return <code>true</code> if table exists, <code>false</code> otherwise.<br><code>null</code> if an error occur ( MySqlConnection not connected )
	 * */
	public static Boolean existsTable(String db, String table) {
		if(!MySqlConnection.existConnection()) return null; 
		
		MySqlConnection mysql= new MySqlConnection(); 
		
		
		
		List<Tables> list=mysql.queryList(Tables.class, existTableBuild(db, table));
		
		
		return list!=null && list.size()>0;
	}
	
	/**
	 * 
	 * @return {@link java.util.List List}&lt;{@link java.util.Map.Entry Entry}&lt;{@link java.lang.String String},{@link java.lang.String String}&gt;&gt;  of all db-tables informations
	 */
	public static List<Entry<String,String>> getAllInfo(){
		if(!MySqlConnection.existConnection()) return null; 
		
		MySqlConnection mysql= new MySqlConnection();
		
		List<Tables> list=mysql.queryList(Tables.class, 
				tcf.selectData(null).build());
		List<Entry<String,String>> schemaTable= new LinkedList<>();
		for(Tables t: list) schemaTable.add(new SimpleEntry<>(t.getTableSchema(),t.getTableName()));
		
		return schemaTable;
	}	

}
