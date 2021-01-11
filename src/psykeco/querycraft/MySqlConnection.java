package psykeco.querycraft;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import psykeco.querycraft.sql.SQLDBCraft;
import psykeco.querycraft.utility.ConnessioneDB;



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
	 * stato di connessione
	 */
	private boolean connesso= false;
	/**
	 * Connessione al database
	 */
	private Connection connessione;
	
	/** codice restituito in caso di query riuscita */
	public final int CODICE_OK= 0;	
	/** codice restituito in caso di query non riuscita */
	public final int CODICE_KO=-1;
	
	public MySqlConnection(){
		connessione=ConnessioneDB.getConnect();
		connesso=connessione!=null;
	}//MySqlConnection()
	
	/**
	 * @return true se connesso
	 */
	public boolean statoConnessione(){
		return connesso;
	}
	
	/**preleva la connessione per fare query 
	 * 
	 * @return la connessione
	 */
	public Connection getConnection(){
		return connessione;
	}
	
	/**
	 * Chiusura della connessione
	 */
	public void chiusura(){
		if(!connesso) return;
		try{
			connessione.close();
			connesso=!connesso;
		}catch(SQLException s){
		}
	}//chiusura
	
	/**
	 * Crea un Database con il nome passato, sostituendo gli spazi con delle sottolineature
	 * 
	 * @param nomeDB : nome del db, ogni spazio verra' rimpiazzato con un underscore
	 * @return true se la creazione ha avuto successo
	 */
	public boolean creaDB(String nomeDB){
		
		if(!connesso) return false;
		
		DBCraft craf=new SQLDBCraft().DB(nomeDB);
		
		try {
			connessione.createStatement().execute(craf.create());
			return true;
		} catch (SQLException e) {
			return false;
		}
		
	}//creaDB
	
	/**
	 * verifica che esista il database passato come parametro
	 * @param nomeDB: il nome del db da controllare
	 * @return true se nomeDB e' un db presente
	 */
	public boolean existDB(String nomeDB){
		
		if(!connesso) throw new IllegalArgumentException() ;
		
		DBCraft craf=new SQLDBCraft().DB(nomeDB);
		
		try {
			ResultSet rs=connessione.createStatement().executeQuery(craf.select());
			return rs.next();
		} catch (SQLException e) {
			return false;
		}//try-catch
	}//existDB
	
	/**
	 * Tenta di eliminare il database passato.
	 * @param nomeDB nome del database da eliminare
	 * @return true se l'eliminazione e' andata a buon fine
	 */
	public boolean dropDB (String nomeDB){
		
		if(!connesso) return false; 
		
		DBCraft craf=new SQLDBCraft().DB(nomeDB);
		
		try{
			connessione.createStatement().execute(craf.drop());
			return true;
		}catch(SQLException s){
			return false;
		}//try-catch
	}//dropDB
	
	/**
	 *esegue un comando mysql e restituisce true se e' stato eseguito correttamente.<BR>
	 *Nel caso in cui non sia stata creata una connessione, ci sia un errore nella richiesta
	 *o comunque la richiesta non fosse andata a buon termine,  ritorna false.
	 *
	 *@param il comando sql da eseguire
	 *
	 *@return lo stato della richiesta
	 */
	public boolean esegui(String comando){
		if(!connesso) return false;
		try{
			connessione.createStatement().execute(comando);
			return true;
		}catch(SQLException s){
			return false;
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
		if(!connesso) return null;
		try{
			return  connessione.createStatement().executeQuery(query);
		}catch(SQLException s){
			return null;
		}//try-catch
	}//query
	
	
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
			while(rs.next()){
				ret.add(rs.getString(1).trim());
			}//while
		}catch(SQLException s){
			return null;
		}//catch
		return ret;
	}//listDB
	
}//classe MySqlConnection
