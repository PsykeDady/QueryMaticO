package psykeco.querymatico;

import java.util.Map.Entry;

/**
 * Query Builders generic interface 
 * 
 * 
 * @author PsykeDady (psdady@msn.com)
 */
public interface QueryMaticO {
	
	/** Set db name
	 *  @param name of db
	 *  @return QueryMaticO updated reference
	 *  */
	public QueryMaticO DB(String DB);
	
	/** set table name
	 *  @param name of table
	 *  @return QueryMaticO updated reference
	 *  */
	public QueryMaticO table(String table);
	
	/** add "column name-column value" into insert, select or update fields
	 * 
	 *  @param  Couple name-value as {@link java.util.Map.Entry Entry} class
	 *  @return QueryMaticO updated reference
	 *  */
	public QueryMaticO entry(Entry<String,Object> kv);
	
	/** add "column name-column value" into insert, select or update fields
	 * 
	 *  @param  column : column name
	 *  @param  value : column value
	 *  @return QueryMaticO updated reference
	 *  */
	public QueryMaticO entry(String column, Object value);
	
	/** add "column name-column value" as filter of query (into where clausole or similar) 
	 * 
	 * @param   Couple name-value as {@link java.util.Map.Entry Entry} class
	 * @return QueryMaticO updated reference
	 *  */
	public QueryMaticO filter(Entry<String,Object> filter);
	
	/** add "column name-column value" as filter of query (into where clausole or similar) 
	 * 
	 *  @param  column : column name
	 *  @param  value : column value
	 *  @return QueryMaticO updated reference
	 *  */
	public QueryMaticO filter(String column, Object value);
	
	/**
	 * check all the fields in order to validate a possible query. <br>
	 * Returned value represent a String with encountered 
	 * error or empty string if every controls passes
	 * 
	 * @return empty string if all check is passed, an error message otherwise
	 */
	public String validate();
	
	/**
	 * Build query and return it as String
	 * @return query, as String, <code>null</code> if {@link #validate() validazione} fail
	 * 
	 * */
	public String build();
	
	/**
	 * create a QueryMaticO as new object with same data of this.
	 * 
	 * @return the new instance
	 */
	public QueryMaticO copy();
}
