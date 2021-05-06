package psykeco.querymatico.sql;

import static psykeco.querymatico.sql.utility.SQLClassParser.validateBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import psykeco.querymatico.ConnectionMaticO;
import psykeco.querymatico.translations.Translations;
import psykeco.querymatico.translations.Translations.KEY_MSG;

/**
 * MySQL implementation of {@link ConnectionMaticO}
 * 
 * @author PsykeDady (psdady@msn.com) 
 * */
public class SQLConnectionMaticO  implements ConnectionMaticO{
	
	
	/**
	 * Some implementation of mysql client require timezone settings
	 */
	public static final String TIMEZONE="useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
	/**
	 * default driver for mysql
	 */
	public static final String DEFAULT_DRIVER="com.mysql.jdbc.Driver";
	
	/**
	 * default URL
	 */
	public static final String DEFAULT_LOCALHOST="localhost";
	
	/**
	 * default port for mysql
	 */
	public static final int DEFAULT_PORT=3306;
	
	/**
	 * default user (root) 
	 */
	public static final String DEFAULT_USER="root";
	
	/**
	 * default autocommit (true)
	 */
	public static final boolean DEFAULT_AUTOCOMMIT=true;
	
	/**
	 * a part of jdbc url. <br>
	 * it must be completed with : <br>
	 * <ul>
	 * 	<li> ip address (required) </li>
	 * 	<li> connection port (not required) </li>
	 * 	<li> schema or database name (not required) </li>
	 * </ul>
	 * <br> 
	 * an example of entire form:<br>
	 * <code>jdbc:mysql://localhost:3306/TestDB</code>
	 */
	public static final String URL_INIT="jdbc:mysql://";
	
	@SuppressWarnings("unused")
	private String driver=DEFAULT_DRIVER;
	private String url=DEFAULT_LOCALHOST;
	private String db;
	private int port = DEFAULT_PORT;
	private String user=DEFAULT_USER;
	private String psk="";
	private boolean autocommit=DEFAULT_AUTOCOMMIT;
	
/**
	 * set driver to use
	 * 
	 * @param driver
	 * 
	 * @return SQLConnectionMaticO instance updated
	 */
	@Override
	public SQLConnectionMaticO driver(String driver) {
		this.driver=driver;
		return this;
	}
/**
	 * set connection URL
	 * @param url
	 * @return SQLConnectionMaticO instance updated
	 */
	@Override
	public SQLConnectionMaticO url(String url) {
		this.url=url;
		return this;
	}
	/**
	 * set user name for authentication
	 * @param user
	 * @return SQLConnectionMaticO instance updated
	 */
	@Override
	public SQLConnectionMaticO user(String user) {
		this.user=user;
		return this;
	}
	/**
	 * set password for authentication
	 * @param psk
	 * @return SQLConnectionMaticO instance updated
	 */
	@Override
	public SQLConnectionMaticO psk(String psk) {
		this.psk=psk;
		return this;
	}
	/**
	 * set db name to establish  connection
	 * @param db
	 * @return SQLConnectionMaticO instance updated
	 */
	@Override
	public SQLConnectionMaticO db(String db) {
		this.db=db;
		return this;
	}
	/**
	 * get db Name 
	 * 
	 * @return db name
	 */
	@Override
	public String getDB() {
		return db;
	}
	/**
	 * set port number to establish connection
	 * @param port
	 * @return SQLConnectionMaticO instance updated
	 */
	@Override
	public SQLConnectionMaticO port(int port) {
		this.port=port;
		return this;
	}
	/** 
	 * set on/off autocommit settings for add/update/delete 
	 * 
	 * @param autocommit
	 * @return SQLConnectionMaticO instance updated 
	 * */
	@Override
	public SQLConnectionMaticO autocommit(boolean autocommit) {
		this.autocommit=autocommit;
		return this;
	}

	/**
	 * check all the fields in order to validate a possible query. <br>
	 * Returned value represent a String with encountered 
	 * error or empty string if every controls passes
	 * 
	 * @return empty string if all check is passed, an error message otherwise
	 */
	@Override
	public String validate() {
		if(url==null) return Translations.getMsg(KEY_MSG.URL_NULL);
		if(port < 1024 || 49151 < port) return Translations.getMsg(KEY_MSG.URL_NULL);
		if(user==null) return Translations.getMsg(KEY_MSG.USER_NULL);
		if(psk==null) return Translations.getMsg(KEY_MSG.PSK_NULL);

		return "";
	}
	
	/**
	 * Connect to database, {@link #build()} is called before to generate url
	 * 
	 * @return connection instance
	 * 
	 * @throws IllegalStateException if can't establish connection
	 */
	@Override
	public Connection connect() {
		
		String URL=build();
		Connection connessione=null;
		try{
			connessione=DriverManager.getConnection(URL,user,psk);
			connessione.setAutoCommit(autocommit);
		}catch(SQLException s){
			throw new IllegalStateException(s.getMessage());
		}
		return connessione;
	}
	
	public boolean equals(Object o) {
		if(o==null) return false;
		if(!(o instanceof SQLConnectionMaticO)) return false;
		SQLConnectionMaticO other=(SQLConnectionMaticO) o;
		
		return 
			((driver==null && other.driver==null) || 
				(driver!=null && driver.equals(other.driver))) &&
			((url==null && other.url==null) || 
				(url!=null && url.equals(other.url))) &&
			port == other.port &&
			((db==null && other.db==null) || 
				(db!=null && db.equals( other.db))) &&
			((user==null && other.user==null) ||
				(user!=null && user.equals( other.user))) &&
			((psk==null && other.psk==null) || 
				(psk!=null && psk.equals( other.psk)))
		;
	}
	/**
	 * 
	 * Build connection url string, {@link #validation} is called before to check parameters
	 * 
	 * @return url connection string
	 * 
	 * @throws IllegalArgumentException if {@link #validate()} return error
	 */
	@Override
	public String build() {
		String validate = validate() ;
		String db=validateBase(this.db);
		if (!validate.equals("")) throw new IllegalArgumentException(validate);
		if (this.db!=null&&db==null) throw new IllegalArgumentException(Translations.getMsg(KEY_MSG.DB_NOT_VALID,this.db));
		
		
		return URL_INIT+url+':'+port+((db!=null)?'/'+db:"")+"?"+TIMEZONE;
	}

}
