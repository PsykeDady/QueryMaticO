package psykeco.querycraft;

import java.sql.Connection;

public interface ConnectionCraft {
	
	/**
	 * set dei driver da usare 
	 * 
	 * @param driver
	 * 
	 * @return istanza di ConnectionCraft aggiornata
	 */
	public ConnectionCraft driver(String driver);
	
	/**
	 * set dell'url da usare
	 * @param url
	 * @return istanza di ConnectionCraft aggiornata
	 */
	public ConnectionCraft url(String url);
	
	/**
	 * set del nome utente da usare 
	 * @param user
	 * @return istanza di ConnectionCraft aggiornata
	 */
	public ConnectionCraft user(String user);
	
	/**
	 * set della password da usare 
	 * @param psk
	 * @return istanza di ConnectionCraft aggiornata
	 */
	public ConnectionCraft psk (String psk);
	
	
	/**
	 * set del db a cui collegarsi
	 * @param db
	 * @return istanza di ConnectionCraft aggiornata
	 */
	public ConnectionCraft db (String db);

	/** 
	 * se true, ogni transizione avrà l'autocommit 
	 * @param autcommit
	 * @return istanza di ConnectionCraft aggiornata 
	 * */
	ConnectionCraft autocommit(boolean autocommit);
	
	/**
	 * set della porta a cui connettersi
	 * @param port
	 * @return istanza di ConnectionCraft aggiornata
	 */
	public ConnectionCraft port (int port);
	
	/**
	 * @return istanza di connessione da usare 
	 * 
	 * @throws IllegalStateException se la connessione non viene stabilita
	 */
	public Connection connect(); 
	
	
	/**
	 * esegue una validazione dei parametri
	 * 
	 * @return stringa vuota (<code>""</code> se la validazione &egrave; andata a buon fine, 
	 * altrimenti il messaggio dell'errore
	 */
	public String validation();
	
	/**
	 * 
	 * genera la stringa di connessione, eventualmente chiamando prima la {@link #validation}
	 * 
	 * @return la stringa di connessione
	 * 
	 * @throws IllegalArgumentException se la validazione non finisce con successo
	 */
	public String craft();


}
