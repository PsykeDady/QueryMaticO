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
import static psykeco.querymatico.translations.Translations.KEY_MSG.NOT_SUPPORT_METHOD;
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
 * MySQL insert implementation of {@link QueryMaticO}.<br>   
 * 
 * Perform insert operations on database,
 * table name and db name are required! <br>   
 * 
 * entry are used into <code>values</code> clausole.
 * 
 * @author PsykeDady (psdady@msn.com) 
 * */
public class SQLInsertMaticO implements QueryMaticO {
	
	public SQLInsertMaticO() {}
	
	/** table name */
	private String table;
	/** db name */
	private String db;
	/** map of value ( entry ) */
	private HashMap<String,Object> kv=new HashMap<>();
	
	/** Set db name
	 *  @param DB name of db
	 *  @return SQLInsertMaticO updated reference
	 *  */
	@Override
	public SQLInsertMaticO DB(String DB) {
		this.db=DB;
		return this;
	}

	/** set table name
	 *  @param table name of table
	 *  @return SQLInsertMaticO updated reference
	 *  */
	@Override
	public SQLInsertMaticO table(String table) {
		this.table=table;
		return this;
	}

	/** add "column name-column value" into insert <code>value</code> fields
	 * 
	 *  @param  kv name-value as {@link java.util.Map.Entry Entry} class
	 *  @return SQLInsertMaticO updated reference
	 *  */
	@Override
	public SQLInsertMaticO entry(Entry<String, Object> kv) {
		return entry(kv.getKey(),kv.getValue());
	}

	/** add "column name-column value" into insert <code>value</code> fields
	 * 
	 *  @param  column : column name
	 *  @param  value : column value
	 *  @return SQLInsertMaticO updated reference
	 *  */
	@Override
	public SQLInsertMaticO entry(String column, Object value) {
		this.kv.putIfAbsent(column, value);
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
	 * 		<li>almost one {@link #entry(Entry)} (or {@link #entry(String, Object)}) couple</li>
	 * </ul>
	 * 
	 * Every couple value-key needed to be valid and not null
	 * 
	 * @return empty string if all check is passed, an error message otherwise
	 */
	@Override
	public String validate() {
		
		if (table==null || table.equals("")) return Translations.getMsg(TABLE_NULL);
		if (db   ==null || db   .equals("")) return Translations.getMsg(DB_NULL);
		
		String tmp=validateBase(table);
		if (tmp==null) return Translations.getMsg(TABLE_NOT_VALID,table);
		
		tmp=validateBase(db);
		if (tmp==null) return Translations.getMsg(DB_NOT_VALID,db);
		
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
		
		return "";
	}

	/**
	 * Build insert istruction and return it as String
	 * @return INSERT istruction, as String, <code>null</code> if {@link #validate() validazione} fail
	 * */
	@Override
	public String build() {
		StringBuilder column=new StringBuilder(kv.size()*20);
		StringBuilder values=new StringBuilder(kv.size()*10);
		String thisdb=this.db;
		this.db=(this.db==null)? MySqlConnection.db():this.db;
		String validation=validate();
		if( ! validation.equals("") ) throw new IllegalArgumentException(validation);
		String db=validateBase(this.db), table=validateBase(this.table);
		
		values.append("INSERT INTO "+'`'+db+"`.`"+table+'`'+" ( ");
		column.append(" VALUES (");
		
		for (Entry<String,Object> kv : this.kv.entrySet()) {
			String key=validateBase(kv.getKey()),
					value=str(kv.getValue());
			values.append( '`'+key+"`," );
			column.append(value+"," );
		}
		
		values.setCharAt(values.length()-1, ')');
		column.setCharAt(column.length()-1, ')');
		
		this.db=thisdb;
		return values.toString()+column.toString();
	}

	/**
	 * filters are not supported in isnert operations
	 * @param filter 
	 * @throws UnsupportedOperationException : always, entries are not supported on SQLDeleteMaticO
	 */
	@Override
	public SQLInsertMaticO filter(Entry<String, Object> filter) {
		throw new UnsupportedOperationException(Translations.getMsg(NOT_SUPPORT_METHOD, getTrueName(SQLInsertMaticO.class),"filter"));
	}

	/**
	 * filters are not supported in isnert operations
	 *  @param  column
	 *  @param  value
	 * @throws UnsupportedOperationException : always, entries are not supported on SQLDeleteMaticO
	 */
	@Override
	public SQLInsertMaticO filter(String column, Object value) {
		throw new UnsupportedOperationException(Translations.getMsg(NOT_SUPPORT_METHOD, getTrueName(SQLInsertMaticO.class),"filter"));
	}

	/**
	 * create a SQLInsertMaticO as new object with same data of this.
	 * 
	 * @return the new instance
	 */
	@Override
	public SQLInsertMaticO copy() {
		SQLInsertMaticO cf=new SQLInsertMaticO().DB(db).table(table);
		if (kv!=null) for( Entry <String,Object > kv: this.kv.entrySet()) {
			cf.entry(kv);
		}
		return cf;
	}

}
