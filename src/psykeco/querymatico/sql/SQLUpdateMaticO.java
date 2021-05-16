package psykeco.querymatico.sql;

import static psykeco.querymatico.sql.utility.SQLClassParser.getTrueName;
import static psykeco.querymatico.sql.utility.SQLClassParser.parseType;
import static psykeco.querymatico.sql.utility.SQLClassParser.str;
import static psykeco.querymatico.sql.utility.SQLClassParser.validateBase;
import static psykeco.querymatico.sql.utility.SQLClassParser.validateValue;
import static psykeco.querymatico.translations.Translations.KEY_MSG.COLUMN_EMPTY;
import static psykeco.querymatico.translations.Translations.KEY_MSG.COLUMN_NOT_VALID;
import static psykeco.querymatico.translations.Translations.KEY_MSG.DB_NOT_VALID;
import static psykeco.querymatico.translations.Translations.KEY_MSG.DB_NULL;
import static psykeco.querymatico.translations.Translations.KEY_MSG.ENTRY_EMPTY;
import static psykeco.querymatico.translations.Translations.KEY_MSG.TABLE_NOT_VALID;
import static psykeco.querymatico.translations.Translations.KEY_MSG.TABLE_NULL;
import static psykeco.querymatico.translations.Translations.KEY_MSG.VALUE_EMPTY;
import static psykeco.querymatico.translations.Translations.KEY_MSG.VALUE_NOT_VALID;

import java.util.HashMap;
import java.util.Map.Entry;

import psykeco.querymatico.QueryMaticO;
import psykeco.querymatico.sql.runners.MySqlConnection;
import psykeco.querymatico.translations.Translations;

/**
 * MySQL update implementation of {@link QueryMaticO}.<br>   
 * 
 * Perform update operation on database,
 * table name and db name are required! <br>   
 * 
 * entry are used into <code>set</code> clausole.
 * filter are used into <code>where</code> clausole.
 * 
 * @author PsykeDady (psdady@msn.com) 
 * */
public class SQLUpdateMaticO implements QueryMaticO {
	
	/** table name */
	private String table;
	/** db name */
	private String db;
	/** couple name-values needed to filter in where clausole */
	private HashMap<String,Object> filter=new HashMap<>();
	/** couple name-values needed in set clausole */
	private HashMap<String,Object> kv    =new HashMap<>();
	
	/** Set db name
	 *  @param DB name of db
	 *  @return SQLUpdateMaticO updated reference
	 *  */
	@Override
	public SQLUpdateMaticO DB(String DB) {
		this.db=DB;
		return this;
	}

	/** set table name
	 *  @param table name of table
	 *  @return SQLUpdateMaticO updated reference
	 *  */
	@Override
	public SQLUpdateMaticO table(String table) {
		this.table=table;
		return this;
	}
	
	/** add "column name-column value" into set <code>value</code> fields
	 * 
	 *  @param  kv name-value as {@link java.util.Map.Entry Entry} class
	 *  @return SQLUpdateMaticO updated reference
	 *  */
	@Override
	public SQLUpdateMaticO entry(Entry<String, Object> kv) {
		return entry(kv.getKey(),kv.getValue());
	}

	/** add "column name-column value" into set <code>value</code> fields
	 * 
	 *  @param  column : column name
	 *  @param  value : column value
	 *  @return SQLUpdateMaticO updated reference
	 *  */
	@Override
	public SQLUpdateMaticO entry(String column, Object value) {
		this.kv.putIfAbsent(column, value);
		return this;
	}

	/** add "column name-column value" as filter of where clausole
	 *
	 * @param   filter name-value as {@link java.util.Map.Entry Entry} class
	 * @return SQLSelectMaticO updated reference
	 *  */
	@Override
	public SQLUpdateMaticO filter(Entry<String, Object> filter) {
		return filter(filter.getKey(),filter.getValue());
	}

	/** add "column name-column value" as filter of where clausole
	 * 
	 *  @param  column : column name
	 *  @param  value : column value
	 *  @return SQLSelectMaticO updated reference
	 *  */
	@Override
	public SQLUpdateMaticO filter(String column, Object value) {
		this.filter.putIfAbsent(column, value);
		return this;
	}

	/**
	 * check all the fields in order to validate a possible query. <br>
	 * Returned value represent a String with encountered 
	 * error or empty string if every controls passes.<br>  
	 * Field required:
	 * <ul>
	 * 		<li>db</li>
	 * 		<li>table</li>
	 * </ul>
	 * 
	 * Every couple value-key needed to be valid and not null
	 * 
	 * @return empty string if all check is passed, an error message otherwise
	 */
	@Override
	public String validate() {
		
		if (table==null || table.equals("")) return Translations.getMsg(TABLE_NULL) ;
		if (db   ==null || db   .equals("")) return Translations.getMsg(DB_NULL) ;
		
		String tmp=validateBase(table);
		if (tmp==null) return Translations.getMsg(TABLE_NOT_VALID, table);
		table=tmp;
		
		tmp=validateBase(db);
		if (tmp==null) return Translations.getMsg(DB_NOT_VALID, db);
		db=tmp;		
		
		if ( kv.size() < 1 ) return Translations.getMsg(ENTRY_EMPTY);
		
		for (Entry<String,Object> kv : this.kv.entrySet()) {
			String type=parseType((getTrueName(kv.getValue().getClass())),false);
			boolean isString= parseType("String",false).equals(type);
			String value= kv.getValue().toString();
			
			if (kv.getKey()  == null || kv.getKey().equals("") ) return Translations.getMsg(COLUMN_EMPTY);
			if (kv.getValue()== null || value      .equals("") ) return Translations.getMsg(VALUE_EMPTY,kv.getKey());
			
			tmp=validateBase(kv.getKey());
			if ( tmp==null ) return Translations.getMsg(COLUMN_NOT_VALID,kv.getKey());
			tmp= isString ? validateValue(value): value;
			if ( tmp==null ) return Translations.getMsg(VALUE_NOT_VALID,value);
		}
		
		for (Entry<String,Object> kv : this.filter.entrySet()) {
			String type=parseType((getTrueName(kv.getValue().getClass())),false);
			boolean isString= parseType("String",false).equals(type);
			String value= kv.getValue().toString();
			
			if (kv.getKey()  == null || kv.getKey().equals("") ) return Translations.getMsg(COLUMN_EMPTY);
			if (kv.getValue()== null || value      .equals("") ) return Translations.getMsg(VALUE_EMPTY,kv.getKey());
			
			tmp=validateBase(kv.getKey());
			if ( tmp==null ) return Translations.getMsg(COLUMN_NOT_VALID,kv.getKey());
			String tmpV= isString ? validateValue(value): value;
			if ( tmpV==null ) return Translations.getMsg(VALUE_NOT_VALID,value);
		}
		
		return "";
	}
	
	/**
	 * Build query and return it as String
	 * @return query, as String, <code>null</code> if {@link #validate() validazione} fail
	 * 
	 * */
	@Override
	public String build() {
		StringBuilder column=new StringBuilder(kv.size()*20);		
		StringBuilder values=new StringBuilder(filter.size()*20);
		String thisdb=this.db;
		this.db=(this.db==null)? MySqlConnection.db():this.db;
		String db=validateBase(this.db), table=validateBase(this.table);
		
		String validation=validate();
		if( ! validation.equals("") ) throw new IllegalArgumentException(validation);
		
		column.append("UPDATE `"+db+"`.`"+table+"` SET ");
		
		for (Entry<String,Object> kv : this.kv.entrySet()) {
			String key=validateBase(kv.getKey()),value=str(kv.getValue());
			column.append("`"+key+"`="+value+"," );
		}
		
		column.deleteCharAt(column.length()-1);
		
		values.append(" WHERE 1=1 ");
		
		for (Entry<String,Object> f : filter.entrySet()) {
			String key=validateBase(f.getKey()),value=str(f.getValue());
			values.append("AND `"+key +"`="+value+" " );
		}
		
		this.db=thisdb;
		return (column.toString()+values.toString()).trim();
	}

	/**
	 * create a SQLUpdateMaticO as new object with same data of this.
	 * 
	 * @return the new instance
	 */
	@Override
	public SQLUpdateMaticO copy() {
		SQLUpdateMaticO qfc=new SQLUpdateMaticO().table(table).DB(db);
		if(filter!=null) for (Entry<String,Object> kv : filter.entrySet()) 
			qfc.filter(kv);
		if(kv!=null) for (Entry<String,Object> cv : kv.entrySet()) 
			qfc.filter(cv);
		return qfc;
	}

}
