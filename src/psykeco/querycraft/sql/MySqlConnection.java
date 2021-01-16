package psykeco.querycraft.sql;


import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import psykeco.querycraft.DBCraft;


/**
 * Gestore delle connessioni di mysql, la classe permette di avviare una connessione,
 * scambiare dati con mysql tirando fuori i ResultSet, dire se sei connesso o no,
 * le cause della mancanza di connessione e altro.
 * 
 * Per Connettere al database la classe da chiamare tuttavia e' {@link psykeco.ioeasier.db.ConnessioneDB},
 * prima di instanziare questa classe basta chiamare {@link psykeco.ioeasier.db.ConnessioneDB #createInstance(String...)} e
 * automaticamente si istanzia la connessione verso il db.
 * @author archdady
 *
 */
public class MySqlConnection {
	
	/**
	 * descrizione della connessione al db
	 */
	private static SQLConnectionCraft connCraft;
	
	/**
	 * Connessione al database
	 */
	private static Connection connessione;
	
	/**
	 * include una serie di query
	 */
	private static Statement statement;
	
	/**
	 * last SQL Error Message
	 */
	private String errMsg="";
	
	/**
	 * frase di testing che serve a vedere se la connesione con mysql e' valida
	 */
	public static final String TEST_ECHO="test_QueryCraft_Connection";
	
	
	/**
	 * non inizia nessuna connessione, ma se ne esiste una 
	 * la può sfruttare<br>
	 * 
	 */
	public MySqlConnection() { }

	
	/**
	 *esegue un comando mysql e restituisce true se e' stato eseguito correttamente.<BR>
	 *Nel caso in cui non sia stata creata una connessione, ci sia un errore nella richiesta
	 *o comunque la richiesta non fosse andata a buon termine,  ritorna false.
	 *
	 *@param il comando sql da eseguire
	 *
	 *@return stringa vuota se il comando &egrave; andato a buon fine. il messaggio di errore altrimenti
	 */
	public String exec(String comando){
		if(!statoConnessione()) {
			errMsg= "Connessione Chiusa";
			return errMsg;
		}
		try{
			if (connessione.getAutoCommit()) connessione.createStatement().execute(comando);
			else {
				if( statement==null ) statement=connessione.createStatement();
				statement.execute(comando);
			}
			return "";
		}catch(SQLException s){
			return errMsg=buildSQLErrMessage(s);
		}//try-catch
	}//esegui
	
	/**
	 * esegue una query e ritorna il ResultSet associato.<br>
	 * Se avviene un errore, o il db non e' connesso allora il valore
	 * ritornato e' null.
	 * 
	 * @param la query da eseguire
	 * 
	 * @return il ResultSet corrispondente
	 * 
	 */
	public ResultSet query(String query){
		if(!statoConnessione()) {
			errMsg= "Connessione Chiusa";
			return null;
		}
		try{
			ResultSet rs=connessione.createStatement().executeQuery(query);
			return  rs;
		}catch(SQLException s){
			errMsg=buildSQLErrMessage(s);
		}//try-catch
		return null;
	}//query
	
	/**
	 * esegue una query e ritorna il ResultSet associato.<br>
	 * Se avviene un errore, o il db non e' connesso allora il valore
	 * ritornato e' null.
	 * Per riuscire il mapping automatico, la classe deve avere almeno un costruttore vuoto! (oltre che essere una classe concreta) 
	 * 
	 * @param c La classe di cui effettuare il mapping
	 * @param query la query da eseguire
	 * 
	 * @return una linked list con il risultato. Se empty, ci potrebbe essere stato un errore (controllare con il msg) 
	 * 
	 */
	public <T> List<T> queryList(Class<T> c, String query){
		ResultSet rs = query(query);
		LinkedList<T> ris=new LinkedList<T>();
		try {
			while(rs.next()) {
				Field[] f= c.getDeclaredFields();
				T istanza = c.newInstance();

				for ( Field x : f ) {
					boolean access=x.isAccessible();
					x.setAccessible(true);
					x.set(istanza, rs.getObject(x.getName(), x.getType()));
					x.setAccessible(access);
				}
				ris.add(istanza);
			}
		}catch (SQLException s){
			errMsg=buildSQLErrMessage(s);
		} catch (IllegalAccessException e) {
			errMsg="costruttore non accessibile. Prevedere un costruttore vuoto!";
		} catch (InstantiationException e) {
			errMsg="costruttore non accessibile, classe astratta o interfaccia! Prevedere un costruttore vuoto!";
		} 
		return ris;
	}//query
	
	public String getErrMsg() {
		return errMsg;
	}
	
	/**
	 * restituisce una lista di database creati con l'account e la password
	 * ricevuti
	 * @return una lista di database
	 */
	public LinkedList<String> listDB (){
		ResultSet rs;
		LinkedList<String > ret= new LinkedList<String>();
		try{
			rs=query("show databases");
			if(! errMsg.equals("")) return null;
			while(rs.next()){
				ret.add(rs.getString(1).trim());
			}//while
		}catch(SQLException s){
			errMsg=buildSQLErrMessage(s);
			return null;
		}//catch
		return ret;
	}//listDB
	
	/**
	 * verifica che esista il database passato come parametro
	 * @param nomeDB: il nome del db da controllare
	 * @return true se nomeDB e' un db presente, false altrimenti. null in caso di errore 
	 */
	public Boolean existDB(String nomeDB){
		
		if(!statoConnessione()) {
			errMsg="connessione chiusa";
			return null;
		}
		
		DBCraft craf=new SQLDBCraft().DB(nomeDB);
		
		try {
			String s=craf.select();
			ResultSet rs=query(s);
			if(rs==null) return false;
			return rs.next();
		} catch (SQLException e) {
			errMsg=buildSQLErrMessage(e);
			return null;
		}//try-catch
	}//existDB
	
	// STATIC METHODS
	
	/**
	 * Se non esiste alcuna connessione ne apre una
	 * @param connCraft la connectionCraft con cui aprire la connessione
	 */
	public static void createConnection(SQLConnectionCraft connCraft) {
		
		if(connessione==null) {
			MySqlConnection.connCraft=connCraft;
			initConnection();
		} 
	}
	
	/**
	 * crea una connessione con i dati inseriti.<br>
	 * richiama {@link #createConnection(SQLConnectionCraft)}
	 * @param url localhost o un indirizzo ip 
	 * @param port 
	 * @param user nome utente
	 * @param psk password
	 */
	public static void createConnection(
		String url, int port, 
		String user, String psk
	){
		createConnection (
			(SQLConnectionCraft) new SQLConnectionCraft()
				.url(url)
				.port(port)
				.user(user)
				.psk(psk)
		);
	}
	
	/** 
	 * create the connection with connectionCraft
	 * 
	 */
	private static void initConnection() {
		statement=null;
		connessione=connCraft.connect();
		String msg=testConnessione();
		if(!msg.equals("")) {
			connessione=null;
			throw new IllegalArgumentException(msg);
		}
	}
	
	/**
	 * 
	 * @return true se la connessione funziona
	 */
	private static String testConnessione(){
		MySqlConnection m=new MySqlConnection();
		
		DBCraft dbc=new SQLDBCraft().DB(TEST_ECHO);
		
		Boolean f=m.existDB(TEST_ECHO);
		if (f==null) return m.getErrMsg();
		else if (f) {
			if( ! m.exec(dbc.drop()).equals("")) 
				return m.getErrMsg();
			return "";
		}
		
		if( ! m.exec(dbc.create()).equals("")) 
			return m.getErrMsg();
		
		if( ! m.exec(dbc.drop()).equals("")) 
			return m.getErrMsg();
		
		return "";
	}
	
	/**
	 * @return true se connesso
	 */
	public static boolean statoConnessione(){
		return connessione!=null;
	}
	
	/** 
	 * reset della connessione per reimpostarne una nuova
	 */
	public static void reset() {
		connessione=null;
		statement=null;
	}
	
	/**
	 * ricrea la connessione usando le ultime informazioni 
	 */
	public static void reboot() {
		if (connCraft==null)
			throw new IllegalStateException("sqlconnectioncraft non disponibile");
		initConnection();
	}
	
	/**
	 * commit delle transizioni
	 */
	public static void commit() {
		if(!statoConnessione()) return;
		try{
			connessione.commit();
			statement=null;
		}catch(SQLException s){}
	}
	
	/**
	 * rollback delle transizioni
	 */
	public static void rollback() {
		if(!statoConnessione()) return;
		try{
			connessione.rollback();
			statement=null;
		}catch(SQLException s){}
	}
	
	
		
	/**
	 * Chiusura della connessione
	 */
	public static void chiusura(){
		if(!statoConnessione()) return;
		try{
			connessione.close();
		}catch(SQLException s){}
	}//chiusura
	
	/**
	 * 
	 * @param e
	 * @return
	 */
	private static String buildSQLErrMessage(SQLException e) {
		return "state="+e.getSQLState()+"\ncode="+e.getErrorCode()+"\nmsg="+e.getMessage();
	}
	
}//classe MySqlConnection
