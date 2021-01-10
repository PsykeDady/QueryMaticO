package psykeco.querycraft.utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import psykeco.querycraft.MySqlConnection;

/**
 * classe di utilita', statica. Serve a creare una connessione con il Database attraverso il solo richiamo
 * del metodo statico {@link #createInstance(String...)} o a distruggere la connessione attraverso {@link #destroy()}.
 * La connessione poi potra' essere utilizzata con {@link psykeco.ioeasier.db.MySqlConnection}
 * @author psykedady
 *
 */
public class ConnessioneDB {
	/**
	 * frase di testing che serve a vedere se la connesione con mysql e' valida
	 */
	public static String TEST_ECHO="test_PsykeCo_MySql_Connection";
	
	/**
	 * nome standard dei driver jdbc usati
	 */
	public static final String DRIVER="com.mysql.jdbc.Driver";
	
	/**
	 * semi-URL standard della connessione con jdbc. Va completato con il nome del database
	 */
	public static final String URL="jdbc:mysql://localhost/";
	
	
	private static Connection c=null;
	
	private ConnessioneDB(){}
	
	/**
	 * 
	 * @param credenziali
	 * @return true se la connessione era gia' stata creata o lo e' stata, false se le credenziali non vanno
	 */
	public static boolean createInstance(String ...credenziali){
		credenziali=verificaCredenziali(credenziali);
		if(c==null){
			c=connect(credenziali);
			boolean status=testConnessione();
			if(status) return true;
			else {
				c=null;
				return false;
			}//else
		}//if c era null
		else return true;
	}
	
	public static Connection getConnect(){
		return c;
	}
	
	public static void destroy(){
		
		if(c==null)
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		c=null;
	}

	private static boolean testConnessione(){
		MySqlConnection msc= new MySqlConnection();
		boolean status=msc.creaDB(TEST_ECHO);
		status=msc.dropDB(TEST_ECHO)||status;
		return status;
	}
	
	public static Connection connect(String ... credenziali){
		Connection connessione=null;
		try{
			//Class.forName(DRIVER);
			connessione=DriverManager.getConnection(URL,credenziali[0],credenziali[1]);
		}/*catch(ClassNotFoundException c ){
			throw new IllegalStateException("Driver Mancanti");
		}*/catch(SQLException s){
			throw new IllegalStateException(s.getMessage());
		}
		return connessione;
	}
	
	private static String[] verificaCredenziali(String...c){
		String[]credenziali=new String[2];
		credenziali[0]=(c.length<1||c[0]==null||c[0].equals(""))?"root":c[0];
		credenziali[1]=(c.length<2||c[1]==null)?"":c[1];
		return credenziali;
	}
}
