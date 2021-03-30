package psykeco.querymatico.sql;

import static psykeco.querymatico.sql.utility.SQLClassParser.validateBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import psykeco.querymatico.ConnectionMaticO;

public class SQLConnectionMaticO  implements ConnectionMaticO{
	
	
	/**
	 * serve in alcune implementazioni di mysql che richiedono esplicitamente la timezone
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
	 * semi-URL standard della connessione con jdbc. <br>
	 * Va completato con : <br>
	 * <ul>
	 * 	<li> il link (obbligatorio) </li>
	 * 	<li> portadi connessione (non obbligatoria) </li>
	 * 	<li> il nome del database (non obbligatorio) </li>
	 * </ul>
	 * <br> 
	 * ecco un esempio di forma completa:<br>
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
	
	@Override
	public SQLConnectionMaticO driver(String driver) {
		this.driver=driver;
		return this;
	}

	@Override
	public SQLConnectionMaticO url(String url) {
		this.url=url;
		return this;
	}

	@Override
	public SQLConnectionMaticO user(String user) {
		this.user=user;
		return this;
	}

	@Override
	public SQLConnectionMaticO psk(String psk) {
		this.psk=psk;
		return this;
	}

	@Override
	public SQLConnectionMaticO db(String db) {
		this.db=db;
		return this;
	}
	
	@Override
	public String getDB() {
		return db;
	}
	
	@Override
	public SQLConnectionMaticO port(int port) {
		this.port=port;
		return this;
	}
	
	@Override
	public SQLConnectionMaticO autocommit(boolean autocommit) {
		this.autocommit=autocommit;
		return this;
	}
	
	@Override
	public String validate() {
		if(url==null) return "url vuoto";
		if(port < 1024 || 49151 < port) return "valore della porta errato";
		if(user==null) return "utente non valido";
		if(psk==null) return "psk non valida";

		return "";
	}
	
	/**
	 * @return istanza di connessione da usare 
	 * 
	 * @throws    IllegalStateException se la connessione non viene stabilita
	 * @throws IllegalArgumentException se i parametri non passano la validazione
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

	@Override
	public String build() {
		String validate = validate() ;
		String db=validateBase(this.db);
		if (!validate.equals("")) throw new IllegalArgumentException(validate);
		if (this.db!=null&&db==null) throw new IllegalArgumentException("nome db"+this.db+"non valido");
		
		
		return URL_INIT+url+':'+port+((db!=null)?'/'+db:"")+"?"+TIMEZONE;
	}

}