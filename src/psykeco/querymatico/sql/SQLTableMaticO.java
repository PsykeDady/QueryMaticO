package psykeco.querymatico.sql;

import static psykeco.querymatico.sql.utility.SQLClassParser.getTrueName;
import static psykeco.querymatico.sql.utility.SQLClassParser.parseClass;
import static psykeco.querymatico.sql.utility.SQLClassParser.parseType;
import static psykeco.querymatico.sql.utility.SQLClassParser.validateBase;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import psykeco.querymatico.TableMaticO;
import psykeco.querymatico.sql.runners.InformationSchema;
import psykeco.querymatico.sql.runners.MySqlConnection;
import psykeco.querymatico.sql.utility.SQLClassParser;
import psykeco.querymatico.translations.Translations;
import static psykeco.querymatico.translations.Translations.KEY_MSG.*;

/**
 * MySQL implementation of {@link TableMaticO}
 * 
 * @author PsykeDady (psdady@msn.com) */
public class SQLTableMaticO implements TableMaticO{
	
	
	/** table name */
	private String table;
	/** db name */
	private String db;
	/** added after names */
	private String suffix="";
	/** added before names */
	private String prefix="";
	/** Map <field name,field type> contains column name-column type */
	private Map<String,String> kv =new HashMap<>();
	/** list of primaries keys */
	private List<String> primary = new LinkedList<>();
	/** java Class representation of table */
	private Class<?> type;
	
	
	/**
	 * concatenate prefix, an input string and suffix
	 * @param what stringa in input
	 * @return prefisso+what+suffisso
	 */
	private String attachPreSuf(String what) {
		return validateBase(prefix+what+suffix);
	}
	
	public SQLTableMaticO() {}
	
	/**
	 * use input db as db name and convert input class with {@link #table} into name of table and map of columns with their types 
	 * 
	 * @param db db name
	 * @param c Class to convert  
	 */
	@SuppressWarnings("rawtypes")
	public SQLTableMaticO(String db, Class c) {
		this.db=db;
		table(c);
	}
	
	/**
	 * set db name
	 * @param db 
	 * @return SQLTableMaticO updated reference
	 */
	@Override
	public SQLTableMaticO DB(String db) {
		this.db=db;
		return this;
	}

	/**
	 * Parse the Class in input, set name of table and create columns from attribute
	 * @param c the class to parse
	 * @return SQLTableMaticO updated reference
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public SQLTableMaticO table(Class c) {
		type=c;
		table=getTrueName(c);
		kv=parseClass(c);
		return this;
	}
	
	/**
	 * set a table name suffix. If class name is "name" and suffix is "_oftable", table name will be 
	 * "name_oftable"
	 * @param suffix 
	 * @return SQLTableMaticO updated reference
	 */
	public SQLTableMaticO suffix(String suffix) { 
		if(suffix!=null)
			this.suffix=suffix; 
		return this; 
	}
	
	/**
	 * set a table name prefix. If class name is "name" and prefix is "the_", table name will be 
	 * "the_name"
	 * @param prefix : nuovo prefisso 
	 * @return SQLTableMaticO updated reference
	 */
	public SQLTableMaticO prefix(String prefix) { 
		if(prefix!=null)
			this.prefix=prefix; 
		return this; 
	}

	/**
	 * Specify a primary key. <br>
	 * The value must be name of class variable you want as primary key.<br>
	 * You can call this method more time in order to specify multiple primary keys
	 * 
	 * @param key : Name of primary key column. 
	 * It must be a variable of class and not null
	 * @return SQLTableMaticO updated reference
	 * 
	 * @throws IllegalArgumentException If the key not exists as class variable
	 */
	public SQLTableMaticO primary(String key) { 
		if(! kv.containsKey(key) ) throw new IllegalArgumentException(Translations.getMsg(PRIMARY_KEY_MUST_REFERE));
		primary.add(key);
		return this;
	}

	/**
	 * check all the fields in order to validate table creation. <br>
	 * Returned value represent a String with encountered 
	 * error or empty string if every controls passes.<br>  
	 * Field required:
	 * <ul>
	 * 		<li>db</li>
	 * 		<li>table</li>
	 * 		<li>class must be almost one field</li>
	 * </ul>
	 * 
	 * 
	 * @return empty string if all check is passed, an error message otherwise
	 */
	public String validate() {
		
		if (table==null || table.equals("")) return Translations.getMsg(TABLE_NULL);
		if (db   ==null || db   .equals("")) return Translations.getMsg(DB_NULL);
		if ( kv.size() < 1 ) return Translations.getMsg(CLASS_PARAMETERS);
		
		String tmp=validateBase(db);
		if (tmp==null) return Translations.getMsg(DB_NOT_VALID,db);
		db=tmp;		
		
		tmp=validateBase(table);
		if (tmp==null) return Translations.getMsg(TABLE_NOT_VALID,table); 
		
		String tmp2=validateBase(prefix+table);
		if (tmp2==null) return Translations.getMsg(PREFIX_NOT_VALID,prefix);
		
		tmp2=validateBase(table+suffix);
		if (tmp2==null) return Translations.getMsg(SUFFIX_NOT_VALID,suffix);
		
		table=tmp;
		
		for (Entry<String,String> kv : kv.entrySet()) {
			if (kv.getKey()  == null || kv.getKey().equals("") ) return Translations.getMsg(COLUMN_EMPTY);
			if ( validateBase(kv.getKey())==null ) return Translations.getMsg(COLUMN_NOT_VALID,kv.getKey());
		}
		
		return "";
	}
	
	/**
	 * Build instruction to create a Table
	 * 
	 * @return string representation of table creation istruction
	 * 
	 * @throws IllegalArgumentException if {@link #validate()} fail
	 */
	public String create() {
		
		StringBuilder sb=new StringBuilder(kv.size()*20);
		String thisdb=this.db;
		this.db=(this.db==null)? MySqlConnection.db():this.db;
		String db=validateBase(this.db), table =attachPreSuf(this.table);
		
		String validation=validate();
		
		if(!validation.equals("")) throw new IllegalArgumentException(validation);
		
		sb.append("CREATE TABLE `"+db+"`.`"+table+"` (");
		
		for (Entry<String,String> kv :this.kv.entrySet() ) {
			boolean isPrimary=primary.contains(kv.getKey());
			String parsedType=parseType(kv.getValue(),isPrimary);
			String key=validateBase(kv.getKey());
			sb.append(key+' '+parsedType+",");
		}
		
		if(! primary.isEmpty()) {
			sb.append("PRIMARY KEY(");

			for (String k : primary) sb.append(validateBase(k)+',');
			sb.setCharAt(sb.length()-1,')');
			sb.append(',');
		}
		
		sb.setCharAt(sb.length()-1, ')');
		
		this.db=thisdb;
		return sb.toString();
	}

	/**
	 * Build query of Table existance 
	 * 
	 * @return string representation of table existence query
	 * 
	 * @throws IllegalArgumentException if {@link #validate()} fail
	 */
	@Override
	public String exists() {
		return InformationSchema.existTableBuild(db, table);
	}

	/**
	 * Build Table remove instruction
	 * 
	 * @return string representation of table remove istruction
	 * 
	 * @throws IllegalArgumentException if {@link #validate()} fail
	 */
	@Override
	public String drop() {
		String thisdb=this.db;
		this.db=(this.db==null)? MySqlConnection.db():this.db;
		String validation=validate();
		String db=validateBase(this.db), table =attachPreSuf(this.table);

		if(!validation.equals("")) throw new IllegalArgumentException(validation);
		
		String sb="DROP TABLE IF EXISTS `"+db+"`.`"+table+"`";
		
		this.db=thisdb;
		return sb;
	}

	/**
	 * create a {@link SQLInsertMaticO} instance to insert record of input object
	 * 
	 * @param istance of Object to insert into table ( it must be of the same class setted with {@link #table(Class)} method
	 * 
	 * @return {@link SQLInsertMaticO} instance to perform an insert on table 
	 */
	@Override
	public SQLInsertMaticO insertData(Object o) {
		String db=validateBase(this.db), table= attachPreSuf(this.table); 

		SQLInsertMaticO qc=new SQLInsertMaticO().DB(db).table(table);
		Map<String,Object> map=SQLClassParser.parseInstance(type, o);
		
		for (Entry<String,Object> entry : map.entrySet()) {
			if(entry.getValue()==null) continue;
			qc.entry(entry);
		}
		
		return qc;
	}

	/**
	 * create a {@link SQLSelectMaticO} instance to select records filtering by field specified by input object
	 * 
	 * @param istance of Object to filter query ( it must be of the same class setted with {@link #table(Class)} method or <code>null</code> to select all fields
	 * 
	 * @return {@link SQLSelectMaticO} instance to perform a select on table 
	 */
	@Override
	public SQLSelectMaticO selectData(Object o) { 
		String table= attachPreSuf(this.table);
		
		SQLSelectMaticO qc=new SQLSelectMaticO().DB(db).table(table);
		if ( o!= null ) { 
			Map<String,Object> map=SQLClassParser.parseInstance(type, o);
		
			for (Entry<String,Object> entry : map.entrySet()) {
				if(entry.getValue()==null) continue;
				qc.filter(entry);
			}
		}
		
		return qc;
	}

	/**
	 * create a {@link QueryMSQLDeleteMaticOaticO} instance to delete records of input object
	 * 
	 * @param istance of Object needed to filter rows to delete from table 
	 * ( it must be of the same class setted with {@link #table(Class)} method
	 * 
	 * @return {@link SQLDeleteMaticO} insert instance to perform a delete on table 
	 */
	@Override
	public SQLDeleteMaticO deleteData(Object o) {
		String table= attachPreSuf(this.table);

		SQLDeleteMaticO qc=new SQLDeleteMaticO().DB(db).table(table);
		
		Map<String,Object> map=SQLClassParser.parseInstance(type, o);
		
		for (Entry<String,Object> entry : map.entrySet()) {
			if(entry.getValue()==null) continue;
			qc.filter(entry);
		}
		
		return qc;
	}

	/**
	 * create a {@link SQLUpdateMaticO} instance to update records of input object.<br>
	 * Primary keys fields (see {@link #primary}) , if present, are required as not null value to filter records to update
	 * 
	 * @param istance of Object is intended to update table's records ( it must be of the same class setted with {@link #table(Class)} method, primary keys corrisponding field (if primary keys are present ) are used to filter what records update
	 * 
	 * @return {@link SQLUpdateMaticO} instance to perform an update on table 
	 */
	@Override
	public SQLUpdateMaticO updateData(Object o) {
		String table= attachPreSuf(this.table);

		SQLUpdateMaticO qc=new SQLUpdateMaticO().DB(db).table(table);
		
		Map<String,Object> map=SQLClassParser.parseInstance(type, o);
		
		for (Entry<String,Object> entry : map.entrySet()) {
			if(primary.contains(entry.getKey()) ) {
				if(entry.getValue()==null) 
					throw new IllegalArgumentException(Translations.getMsg(PRIMARY_NOT_NULL));
				
				qc.filter(entry);
			} else {
				if(entry.getValue()==null) continue;
				
				qc.entry(entry);
			}
		}
		
		return qc;
	}

	/**
	 * create a {@link SQLSelectMaticO} instance that count rows with same value of not null fields of input object. If input is null, all records are selected
	 * 
	 * @param istance of Object to filter query ( it must be of the same class setted with {@link #table(Class)} method or <code>null</code> to select all fields
	 * 
	 * @return {@link SQLSelectMaticO} instance to perform a count on table 
	 */
	@Override
	public SQLSelectMaticO countData(Object o) {
		SQLSelectMaticO qc=new SQLSelectMaticO().DB(db).table(table);
		
		if ( o!= null ) { 
			Map<String,Object> map=SQLClassParser.parseInstance(type, o);
		
			for (Entry<String,Object> entry : map.entrySet()) {
				if(entry.getValue()==null) continue;
				qc.filter(entry);
			}
		} 
		qc.count(null);
		
		return qc;
	}

	/**
	 * create a SQLTableMaticO as new object with same data of this.
	 * 
	 * @return the new instance
	 */
	@Override
	public SQLTableMaticO copy() {
		SQLTableMaticO tf= new SQLTableMaticO().DB(db).prefix(prefix).suffix(suffix);
		if (table!=null && kv!=null) tf.table(type);
		if (primary!=null) for (String key : primary)
			tf.primary(key);
		
		return tf;
	}
	
}
