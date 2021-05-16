package psykeco.querymatico.sql;

import java.util.AbstractMap.SimpleEntry;

import static psykeco.querymatico.sql.utility.SQLClassParser.getTrueName;
import static psykeco.querymatico.sql.utility.SQLClassParser.parseType;
import static psykeco.querymatico.sql.utility.SQLClassParser.str;
import static psykeco.querymatico.sql.utility.SQLClassParser.validateBase;
import static psykeco.querymatico.sql.utility.SQLClassParser.validateValue;

import java.util.HashMap;
import java.util.Map.Entry;

import psykeco.querymatico.SelectMaticO;
import psykeco.querymatico.sql.runners.MySqlConnection;
import psykeco.querymatico.sql.utility.SQLClassParser;
import psykeco.querymatico.translations.Translations;

import static psykeco.querymatico.translations.Translations.KEY_MSG.*;

import java.util.Set;
import java.util.TreeSet;

/**
 * MySQL select implementation of {@link QueryMaticO}.<br>   
 * 
 * Perform select query on database,
 * table name and db name are required! <br>   
 * 
 * entry are used into <code>select</code> clausole.
 * filter are used into <code>where</code> clausole.
 * 
 * @author PsykeDady (psdady@msn.com) 
 * */
public class SQLSelectMaticO extends SelectMaticO {
	
	/**  
	 * aggregate and other select specific operators
	 * 
	 */
	private static enum AGGREGATE {
		SUM,
		AVG,
		COUNT,
		GROUP_CONCAT,
		COUNT_DISTINCT,
		DISTINCT,
		MAX,
		MIN
	}

	/** table name */
	private String table;
	/** db namme */
	private String db;                  
	/** filters, needed for <code>where</code> */
	private HashMap<String,Object> filter=new HashMap<>();
	/** set of field name, needed <code>select</code> in  statement */
	private Set<String> kv=new TreeSet<>();
	/** couple join table and alias name*/
	private SQLSelectMaticO joinTable; 
	/** map of this-column join-column filter  */
	private HashMap<String,String> joinFilter=new HashMap<>();
	
	/** map of aggregate (key) and values */
	private HashMap<AGGREGATE,String> aggregatesColumn=new HashMap<>();

	/** needed in orderby, this couple name-boolean indicate column name used to sort query result, true is ascendenting */
	private Entry<String,Boolean> orderBy; 
	
	/** column name in group by clausole */ 
	private String groupBy;
	
	/** Set db name
	 *  @param DB name of db
	 *  @return SQLSelectMaticO updated reference
	 *  */
	@Override
	public SQLSelectMaticO DB(String DB) {
		this.db=DB;
		return this;
	}
	/** set table name
	 *  @param table name of table
	 *  @return SQLSelectMaticO updated reference
	 *  */
	@Override
	public SQLSelectMaticO table(String table) {
		this.table=table;
		return this;
	}
	
	
	/** add "column name" into select clausole. (value will be ignored)
	 * 
	 *  @param  column : column name
	 *  @return SQLSelectMaticO updated reference
	 *  */
	@Override
	public SQLSelectMaticO entry(String column) {
		this.kv.add(column);
		return this;
	}
	
	/** add "column name" into select clausole. (value will be ignored)
	 * 
	 *  @param  column : column name
	 *  @param  value : column value
	 *  @return SQLSelectMaticO updated reference
	 *  */
	@Override
	public SQLSelectMaticO entry(String column, Object value) {
		return entry(column);
	}
	
	/** add "column name" (key) into select clausole. (value will be ignored)
	 * 
	 *  @param  kv name-value as {@link java.util.Map.Entry Entry} class
	 *  
	 *  @return SQLSelectMaticO updated reference
	 *  */
	@Override
	public SQLSelectMaticO entry(Entry<String, Object> kv) {
		return entry(kv.getKey());
	}

	/** add "column name-column value" as filter of where clausole
	 *
	 * @param   filter name-value as {@link java.util.Map.Entry Entry} class
	 * @return SQLSelectMaticO updated reference
	 *  */
	@Override
	public SQLSelectMaticO filter(Entry<String, Object> filter) {
		return filter(filter.getKey(),filter.getValue());
	}

	/** add "column name-column value" as filter of where clausole
	 * 
	 *  @param  column : column name
	 *  @param  value : column value
	 *  @return SQLSelectMaticO updated reference
	 *  */
	@Override
	public SQLSelectMaticO filter(String column, Object value) {
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
		
		if (table==null || table.equals("")) return Translations.getMsg(TABLE_NULL);
		if (db   ==null || db   .equals("")) return Translations.getMsg(DB_NULL);
		
		String tmp=validateBase(table);
		if (tmp==null) return Translations.getMsg(TABLE_NOT_VALID,table);
		
		tmp=validateBase(db);
		if (tmp==null) return Translations.getMsg(DB_NOT_VALID,db);
		
		tmp=validateBase(alias);
		if (alias!=null && tmp==null) return Translations.getMsg(ALIAS_NOT_VALID, alias);
		
		if ( groupBy!=null && validateBase(groupBy)==null) 
			return Translations.getMsg(AGGREGATE_NOT_VALID, "group by", groupBy);
		
		if ( orderBy != null && orderBy.getKey() == null )
			return Translations.getMsg(AGGREGATE_NOT_NULL, orderBy.getKey());
		
		if ( orderBy != null && validateBase(orderBy.getKey())==null)
			return Translations.getMsg(AGGREGATE_NOT_VALID, "order By", orderBy.getKey());
		
		for (Entry<AGGREGATE,String> kv: aggregatesColumn.entrySet()) {
			if(kv.getValue()!=null && validateBase(kv.getValue())==null)
				return Translations.getMsg(AGGREGATE_NOT_VALID, kv.getKey().name(), kv.getValue());
		}
		
		for (String s : this.kv) {
			if (s==null || s.equals("")) return Translations.getMsg(COLUMN_EMPTY);
			if (validateBase(s)==null) return Translations.getMsg(COLUMN_NOT_VALID,s);
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
		
		return (joinTable!=null)?joinTable.validate():"";
	}

	/**
	 * Build query and return it as String
	 * @return query, as String, <code>null</code> if {@link #validate() validazione} fail
	 * 
	 * */
	@Override
	public String build() {
		String thisdb=this.db;
		this.db=(this.db==null)? MySqlConnection.db():this.db;
		String validation=validate();
		if( ! validation.equals("") ) throw new IllegalArgumentException(validation);
		
		
		String query= 
			("SELECT "+selectBuild()+
			" FROM "+fromBuild()+
			" WHERE 1=1 "+whereBuild()+
			groupByBuild()+
			orderByBuild()).trim()
		;
		this.db=thisdb;
		return query;
	}

	/**
	 * add a SelectMaticO in join with this. 
	 * 
	 * @param joinSelect 
	 * @return SelectMaticO updated reference
	 */
	@Override
	public SQLSelectMaticO join(SelectMaticO joinSelect) {
		if( ! (joinSelect instanceof SelectMaticO) ) 
			throw new IllegalArgumentException(Translations.getMsg(WRONG_CLASS_JOIN,SQLClassParser.getTrueName(SQLSelectMaticO.class)));
		this.joinTable=(SQLSelectMaticO) joinSelect;
		return this;
	}

	/**
	 * add a couple <code>column of this</code>-<code>column of other</code> as filter of join
	 * @param thisOther couple column-column (this-other) as {@link Entry} class
	 * @return SelectMaticO updated reference
	 */
	@Override
	public SQLSelectMaticO joinFilter(Entry<String, String> thisOther) {
		
		return joinFilter(thisOther.getKey(),thisOther.getValue());
	}

	/**
	 * add a couple <code>column of this</code>-<code>column of other</code> as filter of join
	 * @param columnThis column of this SelectMaticO
	 * @param columnOther column of SelectMaticO in join 
	 * @return SelectMaticO updated reference
	 */
	@Override
	public SQLSelectMaticO joinFilter(String columnThis, String columnOther) {
		joinFilter.put(columnThis, columnOther);
		return this;
	}
	 
	/**
	 * build only <i>select clausole</i> of query<br>
	 * example:<br>
	 * <pre>Select field1,field2,field3...
	 * </pre>
	 *  
	 * @return word "select" and fields to select
	 */
	@Override
	public String selectBuild() {
		StringBuilder sb=new StringBuilder();
		
		for (String k : kv ) {
			sb.append(attachAlias(k)+",");
		}
		
		for(Entry<AGGREGATE,String> kv:aggregatesColumn.entrySet()) {
			if(kv.getKey()==AGGREGATE.COUNT_DISTINCT)
				sb.append(AGGREGATE.COUNT+"("+AGGREGATE.DISTINCT+"("+attachAlias(kv.getValue())+")),");
			else 
				sb.append(kv.getKey().name()+"("+attachAlias(kv.getValue())+"),");
		}
		
		if( joinTable != null ) {
			sb.append(joinTable.selectBuild());
		}
		
		if(sb.length()>0 && sb.charAt(sb.length()-1)==',') sb.deleteCharAt(sb.length()-1);
		
		String result=sb.toString();	
		return result.equals("") ? attachAlias(null) : result.trim();
	}
	 
	/**
	 * build only <i>from clausole</i> of query 
	 * example:<br>
	 * <pre>from table1,table2,table3...</pre>
	 * @return word "from" and tables field to join
	 */
	@Override
	public String fromBuild() {
		StringBuilder sb=new StringBuilder();
		String db=validateBase(this.db), table=validateBase(this.table),
				alias=validateBase(this.alias);
		
		sb.append("`"+db+"`.`"+table+"`");
		if(alias!=null)
			sb.append(" `"+alias+"`");
		
		if( joinTable != null ) {
			sb.append(", "+joinTable.fromBuild());
		}
		
		String result=sb.toString();	
		return result.trim();

	}
	 
	/**
	 * build only <i>where clausole</i> of query 
	 * example:<br>
	 * <pre>where field1=value1,field2=other.field2,field3 is null...</pre>
	 * @return "where" word and all couple field=value to filter
	 */
	@Override
	public String whereBuild() {
		StringBuilder sb=new StringBuilder();
		
		for (Entry<String,Object> f : filter.entrySet()) {
			sb.append("AND "+attachAlias(f.getKey())+"="+str(f.getValue())+" " );
		}
		
		if( joinTable != null ) {
			sb.append(joinTable.whereBuild());
			for (Entry<String,String> f : joinFilter.entrySet()) {
				sb.append(" AND "+attachAlias(f.getKey())+"="+joinTable.attachAlias(f.getValue())+" " );
			}
		}
		
		String result=sb.toString();	
		return result.trim();
	}
	 
	/**
	 * build only <i>groupby clausole</i> of query 
	 * example:<br>
	 * <pre>groupby field1</pre>
	 * @return "group By" words and column that group others fields
	 */
	@Override
	protected String groupByBuild() {
		if(groupBy==null||groupBy.equals("")) return "";
		return " GROUP BY "+attachAlias(groupBy);
	}
	 
	/**
	 * build only <i>order by clausole</i> of query 
	 * example:<br>
	 * <pre>order by field1</pre>
	 * @return "order By" words and column needed to order results
	 */
	@Override
	protected String orderByBuild() {
		if(orderBy==null) return "";
		return " ORDER BY "+attachAlias(orderBy.getKey())+" "+(orderBy.getValue()?"ASC":"DESC");
	}

	/**
	 * add count aggregate on a column name<br>
	 * if the same column is in distinct clausole, <code>count</code> become a <code>count(distinct())</code>
	 * 
	 * @param   column to count
	 * @return  updated instance of SelectMaticO
	 */
	@Override
	public SQLSelectMaticO count(String column) {
		String c=aggregatesColumn.get(AGGREGATE.DISTINCT);
		if(column!=null && column.equals(c)) {
			aggregatesColumn.put(AGGREGATE.COUNT_DISTINCT, column);
			aggregatesColumn.remove(AGGREGATE.DISTINCT);
		}
		else aggregatesColumn.put(AGGREGATE.COUNT, column);
		return this;
	}

	/**
	 * add distinct aggregate on a column name<br>
	 * if the same column is in count clausole, <code>distinct</code> become a <code>count(distinct())</code>
	 * 
	 * @param   column to distinct
	 * @return  updated instance of SelectMaticO
	 */
	@Override
	public SQLSelectMaticO distinct(String column) {
		String c=aggregatesColumn.get(AGGREGATE.COUNT);
		if(column!=null && column.equals(c)) {
			aggregatesColumn.put(AGGREGATE.COUNT_DISTINCT, column);
			aggregatesColumn.remove(AGGREGATE.COUNT);
		}
		else aggregatesColumn.put(AGGREGATE.DISTINCT, column);
		return this;
	}

	/**
	 * add sum aggregate on a column name<br>
	 * 
	 * @param   column to sum
	 * @return  updated instance of SelectMaticO
	 */
	@Override
	public SQLSelectMaticO sum(String column) {
		aggregatesColumn.put(AGGREGATE.SUM, column);
		return this;
	}

	/**
	 * query will be group on specified column<br>
	 * 
	 * @param   column to specify in group by clausole 
	 * @return  updated instance of SelectMaticO
	 */
	@Override
	public SQLSelectMaticO groupBy(String column) {
		groupBy=column;
		return this;
	}
	
	/**
	 * query will be order on specified column<br>
	 * 
	 * @param   column to use in ordering 
	 * @param   asc if <code>false</code>, order will be descendant
	 * @return  updated instance of SelectMaticO
	 */
	@Override
	public SQLSelectMaticO orderBy(String column, boolean asc) {
		orderBy=new SimpleEntry<>(column, asc);
		return this;
	}
	 
	/**
	 * create a SelectMaticO as new object with same data of this.
	 * 
	 * @return the new instance
	 */
	@Override
	public SQLSelectMaticO copy() {
		SQLSelectMaticO scf=new SQLSelectMaticO().DB(db).table(table).groupBy(groupBy);
		if (orderBy!=null && orderBy.getKey()!=null) 
				scf.orderBy(orderBy.getKey(),orderBy.getValue());
		
		if( filter!=null) for(Entry<String,Object> kv : filter.entrySet()) scf.filter(kv);
		if (kv!=null) for(String entry:kv) scf.entry(entry);
		if (joinFilter!=null) for(Entry<String,String> kv : joinFilter.entrySet()) scf.joinFilter(kv);
		if (aggregatesColumn!=null) for(Entry<AGGREGATE,String> kv: aggregatesColumn.entrySet()) 
			switch(kv.getKey()) {
				case AVG : break ; 
				case COUNT : scf.count(kv.getValue()); break ; 
				case COUNT_DISTINCT : scf.count(kv.getValue());scf.distinct(kv.getValue()); break ; 
				case DISTINCT : scf.distinct(kv.getValue()); break ; 
				case GROUP_CONCAT :  break ; 
				case MAX :  break ; 
				case MIN :  break ; 
				case SUM : scf.sum(kv.getValue()); break ; 
				default : break ; 
			}
		if(joinTable!=null) scf.join(joinTable.copy());
		
		return scf;
	}

}
