package psykeco.querycraft.sql;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import psykeco.querycraft.DBCraft;
import psykeco.querycraft.utility.SQLClassParser;


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
		if(!existConnection()) {
			errMsg= "Connessione Chiusa";
			return errMsg;
		}
		try{
			if (connessione.getAutoCommit()) connessione.createStatement().execute(comando);
			else {
				if( statement==null ) statement=connessione.createStatement();
				statement.execute(comando);
			}
			return errMsg="";
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
		if(!existConnection()) {
			errMsg= "Connessione Chiusa";
			return null;
		}
		try{
			ResultSet rs=connessione.createStatement().executeQuery(query);
			errMsg="";
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
			ResultSetMetaData rsmeta=rs.getMetaData();
			Set<String> columns=new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
			int count=rsmeta.getColumnCount();
			for(int i=1;i<=count;i++) columns.add(rsmeta.getColumnLabel(i));
			while(rs.next()) {
				Field[] f= c.getDeclaredFields();
				Constructor<T> cons=c.getDeclaredConstructor();
				boolean access=cons.isAccessible();
				cons.setAccessible(true);
				T istanza = cons.newInstance();
				cons.setAccessible(access);
				
				for ( Field x : f ) {
					access=x.isAccessible();
					x.setAccessible(true);
					Object inst=null;
					if(x.getType().equals(File.class)) {
						inst=rs.getBinaryStream(x.getName());
					}
					else {
						inst=
								columns.contains(x.getName())? 
										rs.getObject(x.getName(), x.getType()) 
										: SQLClassParser.nullValue(x.getType());
					}
					
					if(inst!=null && inst instanceof InputStream) {
						File file=File.createTempFile("result", "query");
						file.deleteOnExit();
						try(
							InputStream is=(InputStream) inst;
							FileOutputStream fos=new FileOutputStream(file);
						){
							int data=is.read();
							while(data!=-1) {
								fos.write(data);
								data=is.read();
							}
							inst=file;
						} catch (Exception e) {inst=null;}
					}
					
					x.set(istanza, inst==null? SQLClassParser.nullValue(x.getType()) : inst );
					x.setAccessible(access);
				}
				ris.add(istanza);
			}
			errMsg="";
		}catch (SQLException s){
			errMsg=buildSQLErrMessage(s);
		} catch (IllegalAccessException e) {
			errMsg="costruttore non accessibile. Prevedere un costruttore vuoto!";
		} catch (InstantiationException e) {
			errMsg="costruttore non accessibile, classe astratta o interfaccia! Prevedere un costruttore vuoto!";
		} catch (Exception e) {
			errMsg="Errore chiamando il costruttore. Prevedere un costruttore vuoto!";
		} 
		return ris;
	}//query
	
	/**
	 * restituisce un' array di mappe dove:
	 * <ul>
	 * 	<li>chiave = nome della colonne</li>
	 * 	<li>valore = valore della colonna come {@link Object}</li>
	 * </ul>
	 * Ogni riga è rappresentata da una mappa
	 * <br><br>
	 * In caso di BLOB, viene restituito come valore un array di byte
	 * 
	 * @param query la query
	 * @return un array di mappe, ogni mappa è una riga, chiave=nomecolonna valore=valorecolonna
	 */ 
	@SuppressWarnings("unchecked")
	public Map<String,Object>[] queryMap(String query){
		ResultSet rs = query(query);
		Map<String,Object>[] ris=null;
		int nrow=0;
		try {
			ResultSetMetaData rsmeta=rs.getMetaData();
			int count=rsmeta.getColumnCount();
			if(! rs.last()) return null;
			ris=new HashMap[rs.getRow()];
			
			rs.beforeFirst();
			while(rs.next()) {
				ris[nrow]=new HashMap<String,Object>();
				for(int i=1;i<=count;i++) {
					ris[nrow].put(rsmeta.getColumnLabel(i),rs.getObject(i));
				}
				nrow++;
			}
			errMsg="";
		}catch (SQLException s){
			errMsg=buildSQLErrMessage(s);
		} 
		return ris;
	}
	
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
		
		if(!existConnection()) {
			errMsg="connessione chiusa";
			return null;
		}
		
		DBCraft craf=new SQLDBCraft().DB(nomeDB);
		
		try {
			String s=craf.exists();
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
	public static boolean existConnection(){
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
		if(!existConnection()) return;
		try{
			connessione.commit();
			statement=null;
		}catch(SQLException s){}
	}
	
	/**
	 * rollback delle transizioni
	 */
	public static void rollback() {
		if(!existConnection()) return;
		try{
			connessione.rollback();
			statement=null;
		}catch(SQLException s){}
	}
	
	
		
	/**
	 * Chiusura della connessione
	 */
	public static void close(){
		if(!existConnection()) return;
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
		String ln="";
		for (StackTraceElement o: e.getStackTrace()) {
			if(o.getClassName().equals(MySqlConnection.class.getName())) {
				ln=o.toString();
				break;
			}
		}
		String error=""+
			"ErrLine number="+ln+
			"\nstate="+e.getSQLState()+
			"\ncode="+e.getErrorCode()+
			"\nmsg="+e.getMessage();
		return error;
	}
	
}//classe MySqlConnection
