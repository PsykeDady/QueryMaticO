package psykeco.querymatico;

import java.sql.Connection;

/**
 * Builder for connection.<br> 
 * it instantiate also the {@link Connection} variable to use for query
 * 
 * 
 * @author PsykeDady (psdady@msn.com)
 * */
public interface ConnectionMaticO {
	
	/**
	 * set driver to use
	 * 
	 * @param driver
	 * 
	 * @return ConnectionMaticO instance updated
	 */
	public ConnectionMaticO driver(String driver);
	
	/**
	 * set connection URL
	 * @param url
	 * @return ConnectionMaticO instance updated
	 */
	public ConnectionMaticO url(String url);
	
	/**
	 * set user name for authentication
	 * @param user
	 * @return ConnectionMaticO instance updated
	 */
	public ConnectionMaticO user(String user);
	
	/**
	 * set password for authentication
	 * @param psk
	 * @return ConnectionMaticO instance updated
	 */
	public ConnectionMaticO psk (String psk);
	
	
	/**
	 * set db name to establish  connection
	 * @param db
	 * @return ConnectionMaticO instance updated
	 */
	public ConnectionMaticO db (String db);
	
	/**
	 * get db Name 
	 * 
	 * @return db name
	 */
	public String getDB();

	/** 
	 * set on/off autocommit settings for add/update/delete 
	 * 
	 * @param autocommit
	 * @return ConnectionMaticO instance updated 
	 * */
	public ConnectionMaticO autocommit(boolean autocommit);
	
	/**
	 * set port number to establish connection
	 * @param port
	 * @return ConnectionMaticO instance updated
	 */
	public ConnectionMaticO port (int port);
	
	/**
	 * Connect to database, {@link #build()} is called before to generate url
	 * 
	 * @return connection instance
	 * 
	 * @throws IllegalStateException if can't establish connection
	 */
	public Connection connect(); 
	
	
	/**
	 * check all the fields in order to validate a possible query. <br>
	 * Returned value represent a String with encountered 
	 * error or empty string if every controls passes
	 * 
	 * @return empty string if all check is passed, an error message otherwise
	 */
	public String validate();
	
	/**
	 * 
	 * Build connection url string, {@link #validation} is called before to check parameters
	 * 
	 * @return url connection string
	 * 
	 * @throws IllegalArgumentException if {@link #validate()} return error
	 */
	public String build();


}
