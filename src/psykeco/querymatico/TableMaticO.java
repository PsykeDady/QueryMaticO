package psykeco.querymatico;

/**
 * TableMaticO can map a class directly into a Database Table thanks to java reflection mechanism. 
 * it needs only Class, db name and optionally primary keys list.<br>
 * <b>Class is need to have almost one empty constructor</b> <br>
 * This class build String version of query to <i>create</i>, <i>drop</i> and <i>query existance</i> of Tables, but not only!<br>
 * It can easily provide build from its self other QueryMaticO instance to 
 * <i>insert</i>, <i>select</i>, <i>update</i>, <i>delete</i> or <i>count</i> 
 * records of existent table!
 * 
 * @author PsykeDady (psdady@msn.com) */
public interface TableMaticO {
	
	/**
	 * set db name
	 * @param db 
	 * @return TableMaticO updated reference
	 */
	public TableMaticO DB(String db);
	
	/**
	 * Parse the Class in input, set name of table and create columns from attribute
	 * @param c the class to parse
	 * @return TableMaticO updated reference
	 */
	@SuppressWarnings("rawtypes")
	public TableMaticO table(Class c);
	
	/**
	 * set a table name suffix. If class name is "name" and suffix is "_oftable", table name will be 
	 * "name_oftable"
	 * @param suffix 
	 * @return TableMaticO updated reference
	 */
	public TableMaticO suffix(String suffix);
	
	/**
	 * set a table name prefix. If class name is "name" and prefix is "the_", table name will be 
	 * "the_name"
	 * @param prefix : nuovo prefisso 
	 * @return TableMaticO updated reference
	 */
	public TableMaticO prefix(String prefix);
	
	/**
	 * Specify a primary key. <br>
	 * The value must be name of class variable you want as primary key.<br>
	 * You can call this method more time in order to specify multiple primary keys
	 * 
	 * @param key : Name of primary key column. 
	 * It must be a variable of class and not null
	 * @return TableMaticO updated reference
	 * 
	 * @throws IllegalArgumentException If the key not exists as class variable
	 */
	public TableMaticO primary(String key);
	
	/**
	 * check all the fields in order to validate a possible query. <br>
	 * Returned value represent a String with encountered 
	 * error or empty string if every controls passes
	 * 
	 * @return empty string if all check is passed, an error message otherwise
	 */
	public String validate();
	
	/**
	 * Build query to create a Table
	 * 
	 * @return string representation of table creation istruction
	 * 
	 * @throws IllegalArgumentException if {@link #validate()} fail
	 */
	public String create();
	
	
	/**
	 * Build query to query a Table existance 
	 * 
	 * @return string representation of table existence query
	 * 
	 * @throws IllegalArgumentException if {@link #validate()} fail
	 */
	public String exists();
	
	/**
	 * Build query to query a Table remove instruction
	 * 
	 * @return string representation of table remove istruction
	 * 
	 * @throws IllegalArgumentException if {@link #validate()} fail
	 */
	public String drop();
	
	/**
	 * create a {@link QueryMaticO} instance to insert record of input object
	 * 
	 * @param istance of Object to insert into table ( it must be of the same class setted with {@link #table(Class)} method
	 * 
	 * @return {@link QueryMaticO} instance to perform an insert on table 
	 */
	public QueryMaticO insertData(Object o);
	
	/**
	 * create a {@link SelectMaticO} instance to select records filtering by field specified by input object
	 * 
	 * @param istance of Object to filter query ( it must be of the same class setted with {@link #table(Class)} method or <code>null</code> to select all fields
	 * 
	 * @return {@link SelectMaticO} instance to perform a select on table 
	 */
	public SelectMaticO selectData(Object o);
	
	/**
	 * create a {@link QueryMaticO} instance to delete records of input object
	 * 
	 * @param istance of Object needed to filter rows to delete from table 
	 * ( it must be of the same class setted with {@link #table(Class)} method
	 * 
	 * @return {@link QueryMaticO} insert instance to perform a delete on table 
	 */
	public QueryMaticO deleteData(Object o);
	
	/**
	 * create a {@link QueryMaticO} instance to update records of input object.<br>
	 * Primary keys fields (see {@link #primary}) , if present, are required as not null value to filter records to update
	 * 
	 * @param istance of Object needed to filter rows to update table's records ( it must be of the same class setted with {@link #table(Class)} method.
	 * 
	 * @return {@link QueryMaticO} instance to perform an update on table 
	 */
	public QueryMaticO updateData(Object o);
	
	/**
	 * create a {@link SelectMaticO} instance that count rows with same value of not null fields of input object. If input is null, all records are selected
	 * 
	 * @param istance of Object to filter query ( it must be of the same class setted with {@link #table(Class)} method or <code>null</code> to select all fields
	 * 
	 * @return {@link SelectMaticO} instance to perform a count on table 
	 */
	public SelectMaticO countData(Object o);
	
	/**
	 * create a TableMaticO as new object with same data of this.
	 * 
	 * @return the new instance
	 */
	public TableMaticO copy();

}
