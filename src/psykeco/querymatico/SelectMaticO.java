package psykeco.querymatico;

import static psykeco.querymatico.sql.utility.SQLClassParser.*;

import java.util.Map.Entry;

/**
 * SelectMaticO class extends {@link QueryMaticO} 
 * to implements special features of select query as 
 * join or filter on join
 * 
 * @author PsykeDady (psdady@msn.com) */
public abstract class SelectMaticO implements QueryMaticO{
	
	/**
	 * alias name for table 
	 */
	protected String alias;
	
	
	/**
	 * set alias name for table to use in queries
	 * in order to avoid name overlapping of same tables in join
	 * 
	 * @param alias 
	 * @return SelectMaticO updated reference
	 */
	public SelectMaticO alias(String alias) {
		this.alias=alias;
		return this;
	}
	
	/**
	 * append to alias a column name 
	 * 
	 * @param what : the column*
	 * 
	 * @return `alias`.`what` 
	 */
	protected String attachAlias(String what) {
		what=(what==null)?"*":"`"+validateBase(what)+"`";
		if(alias==null || alias.equals("")) 
			return what;
		return "`"+validateBase(alias)+"`."+what;
	}
	
	/**
	 * add a column to select 
	 * @param value : column
	 * @return SelectMaticO updated reference
	 */
	public abstract SelectMaticO entry (String value);
	
	/**
	 * add a SelectMaticO in join with this. 
	 * 
	 * @param joinSelect 
	 * @return SelectMaticO updated reference
	 */
	public abstract SelectMaticO join(SelectMaticO joinSelect);
	
	/**
	 * add a couple <code>column of this</code>-<code>column of other</code> as filter of join
	 * @param thisOther couple column-column as {@link Entry} class
	 * @return SelectMaticO updated reference
	 */
	public abstract SelectMaticO joinFilter (Entry<String,String> thisOther);
	
	/**
	 * add a couple <code>column of this</code>-<code>column of other</code> as filter of join
	 * @param columnThis column of this SelectMaticO
	 * @param columnOther column of SelectMaticO in join 
	 * @return SelectMaticO updated reference
	 */
	public abstract SelectMaticO joinFilter (String columnThis, String columnOther);
	
	/**
	 * add count aggregate on a column name<br>
	 * if the same column is in distinct clausole, <code>count</code> become a <code>count(distinct())</code>
	 * 
	 * @param   column to count
	 * @return  updated instance of SelectMaticO
	 */
	public abstract SelectMaticO count(String column);
	
	/**
	 * add distinct aggregate on a column name<br>
	 * if the same column is in count clausole, <code>distinct</code> become a <code>count(distinct())</code>
	 * 
	 * @param   column to distinct
	 * @return  updated instance of SelectMaticO
	 */
	public abstract SelectMaticO distinct(String column);
	
	/**
	 * add sum aggregate on a column name<br>
	 * 
	 * @param   column to sum
	 * @return  updated instance of SelectMaticO
	 */
	public abstract SelectMaticO sum(String column);
	
	/**
	 * query will be group on specified column<br>
	 * 
	 * @param   column to specify in group by clausole 
	 * @return  updated instance of SelectMaticO
	 */
	public abstract SelectMaticO groupBy(String column);
	
	/**
	 * query will be order on specified column<br>
	 * 
	 * @param   column to use in ordering 
	 * @param   asc if <code>false</code>, order will be descendant
	 * @return  updated instance of SelectMaticO
	 */
	public abstract SelectMaticO orderBy(String column, boolean asc);
	
	/**
	 * query will be order on specified column in ascendent order.
	 * <br>This method call {@link #orderBy(String, boolean)} with asc=true;
	 * 
	 * @param   column to use in ordering 
	 * @return  updated instance of SelectMaticO
	 */
	public SelectMaticO orderBy(String column) {
		orderBy(column, true);
		return this;
	}
	
	/**
	 * build only <i>select part</i> of query<br>
	 * example:<br>
	 * <pre>Select field1,field2,field3...
	 * </pre>
	 *  
	 * @return word "select" and fields to select
	 */
	protected abstract String selectBuild();
	
	/**
	 * build only <i>from part</i> of query 
	 * example:<br>
	 * <pre>from table1,table2,table3...</pre>
	 * @return word "from" and tables field to join
	 */
	protected abstract String   fromBuild();
	
	/**
	 * build only <i>where part</i> of query 
	 * example:<br>
	 * <pre>where field1=value1,field2=other.field2,field3 is null...</pre>
	 * @return "where" word and all couple field=value to filter
	 */
	protected abstract String  whereBuild();
	
	/**
	 * build only <i>groupby part</i> of query 
	 * example:<br>
	 * <pre>groupby field1</pre>
	 * @return "group By" words and column that group others fields
	 */
	protected abstract String groupByBuild();
	
	/**
	 * build only <i>order by part</i> of query 
	 * example:<br>
	 * <pre>order by field1</pre>
	 * @return "order By" words and column needed to order results
	 */
	protected abstract String orderByBuild();
	
	/**
	 * create a SelectMaticO as new object with same data of this.
	 * 
	 * @return the new instance
	 */
	@Override
	public abstract SelectMaticO copy();
}
