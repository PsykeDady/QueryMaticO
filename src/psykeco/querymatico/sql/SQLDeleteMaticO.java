package psykeco.querymatico.sql;

import static psykeco.querymatico.sql.utility.SQLClassParser.getTrueName;
import static psykeco.querymatico.sql.utility.SQLClassParser.parseType;
import static psykeco.querymatico.sql.utility.SQLClassParser.str;
import static psykeco.querymatico.sql.utility.SQLClassParser.validateBase;
import static psykeco.querymatico.sql.utility.SQLClassParser.validateValue;

import java.util.HashMap;
import java.util.Map.Entry;

import psykeco.querymatico.QueryMaticO;
import psykeco.querymatico.sql.runners.MySqlConnection;

/**
 * MySQL implementation of {@link QueryMaticO}.<br>   
 * 
 * Perform delete operations on database,
 * table name and db name are required! <br>   
 * 
 * filters are used into <code>where</code> clausole
 * 
 * @author PsykeDady (psdady@msn.com) 
 * */
public class SQLDeleteMaticO implements QueryMaticO{

	/** table name */
	private String table;

	/** db name */
	private String db;

	/** map of filter */
	private HashMap<String,Object> filter=new HashMap<>();
	
	/** Set db name
	 *  @param name of db
	 *  @return SQLDeleteMaticO updated reference
	 *  */
	@Override
	public SQLDeleteMaticO DB(String DB) {
		this.db=DB;
		return this;
	}
	
	/** set table name
	 *  @param name of table
	 *  @return SQLDeleteMaticO updated reference
	 *  */
	@Override
	public SQLDeleteMaticO table(String table) {
		this.table=table;
		return this;
	}
	
	/**
	 * entries are not supported in delete operations
	 * 
	 * @throws UnsupportedOperationException : always, entries are not supported on SQLDeleteMaticO
	 */
	@Override
	public SQLDeleteMaticO entry(Entry<String, Object> kv) {
		throw new UnsupportedOperationException("SqlDeleteMaticO does not support entry");
	}
	
	/**
	 * entries are not supported in delete operations
	 * 
	 * @throws UnsupportedOperationException : always, entries are not supported on SQLDeleteMaticO
	 */
	@Override
	public SQLDeleteMaticO entry(String column, Object value) {
		throw new UnsupportedOperationException("SqlDeleteMaticO does not support entry");
	}

	/** add "column name-column value" as filter of query (into where clausole or similar) 
	 * 
	 * @param   Couple name-value as {@link java.util.Map.Entry Entry} class
	 * @return SQLDeleteMaticO updated reference
	 *  */
	@Override
	public SQLDeleteMaticO filter(Entry<String, Object> filter) {
		return filter(filter.getKey(),filter.getValue());
	}
	
	/** add "column name-column value" as filter of query (into where clausole or similar) 
	 * 
	 *  @param  column : column name
	 *  @param  value : column value
	 *  @return SQLDeleteMaticO updated reference
	 *  */
	@Override
	public SQLDeleteMaticO filter(String column, Object value) {
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
		
		if (table==null || table.equals("")) return "nome tabella necessario";
		if (db   ==null || db   .equals("")) return "nome db necessario"     ;
		
		String tmp=validateBase(table);
		if (tmp==null) return " nome tabella "+table+" non valido";
		
		tmp=validateBase(db);
		if (tmp==null) return " nome db "+db+" non valido";
		
		for (Entry<String,Object> kv : this.filter.entrySet()) {
			String type=parseType((getTrueName(kv.getValue().getClass())),false);
			boolean isString= parseType("String",false).equals(type);
			String value= kv.getValue().toString();
			
			if (kv.getKey()  == null || kv.getKey().equals("") ) return "Una colonna \u00e8 stata trovata vuota";
			if (kv.getValue()== null || value      .equals("") ) return "Il valore di "+kv.getKey()+ "\u00e8 stata trovata vuota";
			
			tmp=validateBase(kv.getKey());
			if ( tmp==null ) return "La colonna "+kv.getKey()+" non \u00e8 valida";
			tmp= isString ? validateValue(value): value;
			if ( tmp==null ) return "Il valore " +value      +" non \u00e8 valido";
		}
		
		return "";
	}

	/**
	 * Build delete istruction and return it as String
	 * @return DELETE istruction, as String, <code>null</code> if {@link #validate() validazione} fail

	 * */
	@Override
	public String build() {
		StringBuilder values=new StringBuilder(filter.size()*20);
		String thisdb=this.db;
		this.db=(this.db==null)? MySqlConnection.db():this.db;
		String validation=validate();
		if( ! validation.equals("")) throw new IllegalArgumentException(validation);
		
		String db=validateBase(this.db),table=validateBase(this.table);
		
		values.append("DELETE FROM `"+db+"`.`"+table+"` WHERE 1=1 ");
		
		for (Entry<String,Object> f : filter.entrySet()) {
			String key=validateBase(f.getKey()),
				value=str(f.getValue());
			values.append("AND `"+key+"`="+value+" " );
		}
		this.db=thisdb;
		return values.toString().trim();
	}

	/**
	 * create a SQLDeleteMaticO as new object with same data of this.
	 * 
	 * @return the new instance
	 */
	@Override
	public SQLDeleteMaticO copy() {
		SQLDeleteMaticO cf=new SQLDeleteMaticO().DB(db).table(table);
		if(filter!=null) for( Entry <String,Object> kv: filter.entrySet()) {
			cf.filter(kv);
		}
		return cf;
	}

	
}
