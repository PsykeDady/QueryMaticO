package psykeco.querycraft;

import java.sql.Connection;

/**
 * Crafter for connection.<br> 
 * it istantiate also the {@link Connection} variable to use for query
 * 
 * 
 * @author PsykeDady
 * */
public interface ConnectionCraft {
	
	/**
	 * driver to use
	 * 
	 * @param driver
	 * 
	 * @return ConnectionCraft instance updated
	 */
	public ConnectionCraft driver(String driver);
	
	/**
	 * set dell'url da usare
	 * @param url
	 * @return ConnectionCraft instance updated
	 */
	public ConnectionCraft url(String url);
	
	/**
	 * set del nome utente da usare 
	 * @param user
	 * @return ConnectionCraft instance updated
	 */
	public ConnectionCraft user(String user);
	
	/**
	 * set della password da usare 
	 * @param psk
	 * @return ConnectionCraft instance updated
	 */
	public ConnectionCraft psk (String psk);
	
	
	/**
	 * set db name for connection
	 * @param db
	 * @return ConnectionCraft instance updated
	 */
	public ConnectionCraft db (String db);
	
	/**
	 * @return db name
	 */
	public String getDB();

	/** 
	 * se true, ogni transizione avrà l'autocommit 
	 * @param autcommit
	 * @return ConnectionCraft instance updated 
	 * */
	ConnectionCraft autocommit(boolean autocommit);
	
	/**
	 * set della porta a cui connettersi
	 * @param port
	 * @return ConnectionCraft instance updated
	 */
	public ConnectionCraft port (int port);
	
	/**
	 * @return istanza di connessione da usare 
	 * 
	 * @throws IllegalStateException if can't establish connection
	 */
	public Connection connect(); 
	
	
	/**
	 * esegue una validazione dei parametri
	 * 
	 * @return stringa vuota (<code>""</code> se la validazione &egrave; andata a buon fine, 
	 * altrimenti il messaggio dell'errore
	 */
	public String validate();
	
	/**
	 * 
	 * genera la stringa di connessione, eventualmente chiamando prima la {@link #validation}
	 * 
	 * @return la stringa di connessione
	 * 
	 * @throws IllegalArgumentException if {@link #validate()} return error
	 */
	public String craft();


}
