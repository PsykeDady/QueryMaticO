package psykeco.querymatico;


/**
 * DBMaticO builds istructions to create, delete or query db's (or schemas) 
 * 
 * @author PsykeDady (psdady@msn.com)
 * */
public interface DBMaticO{
	
	/**
	 * set db name
	 * @param db : db new name
	 * @return updated DBMaticO instance
	 */
	public DBMaticO DB(String db);
	
	/**
	 * check all the fields in order to validate a possible query. <br>
	 * Returned value rappresent a String with encountered 
	 * error or empty string if every controls passes
	 * 
	 * @return empty string if all check is passed, an error message otherwise
	 */
	public String validate();
	
	/**
	 * Build statement to create a new schema, only if 
	 * {@link #validate()} passes with success.
	 * 
	 * @return String of instruction 
	 * 
	 * @throws IllegalArgumentException if {@link #validate()} fail
	 */
	public String create();
	
	
	/**
	 * Build statement to check if a schema exists, only if 
	 * {@link #validate()} passes with success.
	 * 
	 * @return String of instruction 
	 * 
	 * @throws IllegalArgumentException if {@link #validate()} fail
	 */
	public String exists();
	
	/**
	 * Build statement to delete a schema, only if 
	 * {@link #validate()} passes with success.
	 * 
	 * @return String of instruction 
	 * 
	 * @throws IllegalArgumentException if {@link #validate()} fail
	 */
	public String drop();
	
	/**
	 * Build statement to list db of a schema, only if 
	 * {@link #validate()} passes with success.
	 * 
	 * @return String of instruction 
	 * 
	 * @throws IllegalArgumentException if {@link #validate()} fail
	 */
	public String listTables();
	
	/**
	 * create a DBMaticO as new object with same data of this.
	 * 
	 * @return the new instance
	 */
	public DBMaticO copy();

}
