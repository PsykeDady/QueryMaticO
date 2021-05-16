package psykeco.querymatico.sql.runners;


import static psykeco.querymatico.sql.utility.SQLClassParser.getTrueName;
import static psykeco.querymatico.translations.Translations.KEY_MSG.CONNECTION_CLOSED;
import static psykeco.querymatico.translations.Translations.KEY_MSG.CONNECTION_MATICO_NOT_AVAIBLE;
import static psykeco.querymatico.translations.Translations.KEY_MSG.CONSTRUCTOR_ERROR;
import static psykeco.querymatico.translations.Translations.KEY_MSG.NOT_EMPTY_ACCESSIBLE_CONSTRUCTOR;
import static psykeco.querymatico.translations.Translations.KEY_MSG.NOT_EMPTY_CONSTRUCTOR;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
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

import psykeco.querymatico.DBMaticO;
import psykeco.querymatico.sql.SQLConnectionMaticO;
import psykeco.querymatico.sql.SQLDBMaticO;
import psykeco.querymatico.sql.utility.SQLClassParser;
import psykeco.querymatico.translations.Translations;


/**
 * <p></br></p>
 * <p>Manage mysql connection and session.</br></p>
 * <p>It start a singleton instance of {@link java.sql.Connection Connection} created through {@link psykeco.querymatico.sql SQLConnectionMaticO}, checking state of connection, getting mysql errors string, exec query and other</br></p>
 * <p></br></p>
 * <p>To start connection call {@link #createConnection(String,int,String,String)} or {@link #createConnection(SQLConnectionMaticO)}</br></p>
 * 
 * @author PsykeDady (psdady@msn.com)
 *
 */
public class MySqlConnection {
	
	/**
	 * the connection builder
	 */
	private static SQLConnectionMaticO connMaticO;
	
	/**
	 * Connection to database
	 */
	private static Connection connection;
	
	/**
	 * a single statement can perform more query, if you want to rollback more actions
	 */
	private static Statement statement;
	
	/**
	 * last SQL Error Message
	 */
	private String errMsg="";
	
	/**
	 * this is a DB name to test if connection work properly
	 */
	public static final String TEST_ECHO="test_QueryMaticO_Connection";
	
	
	/**
	 * Empty constructor. Do not initialize a connection, but if a connection are already present, it can using that
	 */
	public MySqlConnection() { }

	
	/**
	 * <p>Execute a single MySql command and return a string contains an error message. If no error occur, it returns an empty string.</br></p>
	 * <p>If no connection through {@link #createConnection(String,int,String,String)} or {@link #createConnection(SQLConnectionMaticO)} are established, methods return immediately an error string</br></p>
	 * <p></br></p>
	 * 
	 * @param command sql command to execute
	 * 
	 * @return empty string if no error occurs. Error message instead
	 */
	public String exec(String command){
		if(!existConnection()) {
			errMsg= Translations.getMsg(CONNECTION_CLOSED);
			return errMsg;
		}
		try{
			if (connection.getAutoCommit()) connection.createStatement().execute(command);
			else {
				if( statement==null ) statement=connection.createStatement();
				statement.execute(command);
			}
			return errMsg="";
		}catch(SQLException s){
			return errMsg=buildSQLErrMessage(s);
		}//try-catch
	}//esegui
	
	/**
	 * <p>Execute a single MySql query and return the resultSet. </br></p>
	 * <p>If error occur, it returns <code>null</code> and message errors can be queried from {@link #getErrMsg()} </br></p>
	 * <p>If no connection through {@link #createConnection(String,int,String,String)} or {@link #createConnection(SQLConnectionMaticO)} are established, methods return immediately</br></p>
	 * <p></br></p>
	 * 
	 * @param query sql query to execute
	 * 
	 * @return the ResultSet or <code>null</code>
	 */
	public ResultSet query(String query){
		if(!existConnection()) {
			errMsg= Translations.getMsg(CONNECTION_CLOSED);
			return null;
		}
		try{
			ResultSet rs=connection.createStatement().executeQuery(query);
			errMsg="";
			return  rs;
		}catch(SQLException s){
			errMsg=buildSQLErrMessage(s);
		}//try-catch
		return null;
	}//query
	
	/**
	 * <p>Execute a single MySql query and return a list of class objects represents the table. </br></p>
	 * <p>If error occur, it returns an empty list and message errors can be queried from {@link #getErrMsg()}</br></p>
	 * <p>If no connection through {@link #createConnection(String,int,String,String)} or {@link #createConnection(SQLConnectionMaticO)} are established, methods return immediately</br></p>
	 * <p>Automatic Relation-Object-mapping with input class is possible only if <b>empty constructor is avaible</b> and <b>class is concrete</b></br></p>
	 * 
	 * @param <T> the class of expected result ( class of queried table ), automatic selected through c parameter
	 * @param c the class of expected result ( class of queried table )
	 * @param query sql query to execute
	 * 
	 * @return {@link java.util.List List} &lt; c &gt;, if empty, check {@link #getErrMsg()}
	 */
	public <T> List<T> queryList(Class<T> c, String query){
		LinkedList<T> ris=new LinkedList<T>();
		if(!existConnection()) {
			errMsg= Translations.getMsg(CONNECTION_CLOSED);
			return ris;
		}
		ResultSet rs = query(query);
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
					
					Object inst=SQLClassParser.parseResultToField(rs,x,columns);
					
					x.set(istanza, inst==null? SQLClassParser.nullValue(x.getType()) : inst );
					x.setAccessible(access);
				}
				ris.add(istanza);
			}
			errMsg="";
		}catch (SQLException s){
			errMsg=buildSQLErrMessage(s);
		} catch (IllegalAccessException e) {
			errMsg=Translations.getMsg(NOT_EMPTY_CONSTRUCTOR);
		} catch (InstantiationException e) {
			errMsg=Translations.getMsg(NOT_EMPTY_ACCESSIBLE_CONSTRUCTOR);
		} catch (Exception e) {
			errMsg=Translations.getMsg(CONSTRUCTOR_ERROR);
		} 
		return ris;
	}
	
	/**
	 * <p>Execute a single MySql query and return an array of map. Every map rappresent a row of resultset</br></p>
	 * 
	 * <p>map will be structurated in this way:</br></p>
	 * <ul>
	 * 	<li>key = column name</li>
	 * 	<li>value = row value as {@link Object}</li>
	 * </ul>
	 * 
	 * if column represent a BLOB, byte array will be returned
	 * 
	 * <p>if an error occur, <code>null</code> value will be returned and message errors can be queried from {@link #getErrMsg()}</br></p>
	 * <p>If no connection through {@link #createConnection(String,int,String,String)} or {@link #createConnection(SQLConnectionMaticO)} are established, methods return immediately</br></p>
	 * 
	 * 
	 * @param query la query
	 * @return a {@link java.util.Map Map} &lt; {@link java.lang.String String},{@link java.lang.Object Object} &gt;
	 */ 
	@SuppressWarnings("unchecked")
	public Map<String,Object>[] queryMap(String query){
		if(!existConnection()) {
			errMsg= Translations.getMsg(CONNECTION_CLOSED);
			return null;
		}
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
	
	// STATIC METHODS
	
	/**
	 * <p>if any connection exists, it will be created</br></p>
	 * @param connMaticO connection builder
	 */
	public static void createConnection(SQLConnectionMaticO connMaticO) {
		
		if(connection==null) {
			MySqlConnection.connMaticO=connMaticO;
			initConnection();
		} 
	}
	
	/**
	 * <p>if any connection exists, it will be created</br></p>
	 * 
	 * <p>it call {@link #createConnection(SQLConnectionMaticO)}</br></p>
	 * @param url ip address
	 * @param port port of mysql server
	 * @param user user name
	 * @param psk password
	 */
	public static void createConnection(
		String url, int port, 
		String user, String psk
	){
		createConnection (
			(SQLConnectionMaticO) new SQLConnectionMaticO()
				.url(url)
				.port(port)
				.user(user)
				.psk(psk)
		);
	}
	
	/** 
	 * create the connection with connectionMaticO
	 */
	private static void initConnection() {
		statement=null;
		connection=connMaticO.connect();
		String msg=testConnessione();
		if(!msg.equals("")) {
			connection=null;
			throw new IllegalArgumentException(msg);
		}
	}
	
	/**
	 * 
	 * @return empty string if connection is active and work ( a db named {@link #TEST_ECHO} will be created and then destroyed )
	 */
	private static String testConnessione(){
		MySqlConnection m=new MySqlConnection();
		
		DBMaticO dbc=new SQLDBMaticO().DB(TEST_ECHO);
		
		Boolean f=InformationSchema.existsDB(TEST_ECHO);
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
	 * @return true if connected
	 */
	public static boolean existConnection(){
		return connection!=null;
	}
	
	/**
	 * @return the connected db
	 */
	public static String db() {
		return (existConnection())?
				connMaticO.getDB():
				null; 
	}
	
	/** 
	 * reset della connessione per reimpostarne una nuova
	 */
	public static void reset() {
		connection=null;
		statement=null;
	}
	
	/**
	 * connection will be recreated
	 */
	public static void reboot() {
		if (connMaticO==null)
			throw new IllegalStateException(Translations.getMsg(CONNECTION_MATICO_NOT_AVAIBLE, getTrueName(SQLConnectionMaticO.class)));
		initConnection();
	}
	
	/**
	 * <p>commit all mysql suspended istructions, if connection have autocommit flag at <code>true</code> (see {@link psykeco.querymatico.ConnectionMaticO #autocommit(boolean) ConnectionMaticO.autocommit})</br></p>
	 */
	public static void commit() {
		if(!existConnection()) return;
		try{
			connection.commit();
			statement=null;
		}catch(SQLException s){}
	}
	
	/**
	 * 
	 * <p>discard every suspended istructions on statement , if connection have autocommit flag at <code>true</code> (see {@link psykeco.querymatico.ConnectionMaticO #autocommit(boolean) ConnectionMaticO.autocommit})</br></p>
	 */
	public static void rollback() {
		if(!existConnection()) return;
		try{
			connection.rollback();
			statement=null;
		}catch(SQLException s){}
	}
	
	
		
	/**
	 * connection will be closed
	 */
	public static void close(){
		if(!existConnection()) return;
		try{
			connection.close();
		}catch(SQLException s){}
	}
	
	/**
	 * <p>build SQL error message if an exception on Mysql Server side attempted. </br></p>
	 * <p>The message will builded following this template:</br></p>
	 * <pre>ErrLine number=
	 * state= the sql state
	 * code=the error code
	 * msg=the error message
	 * </pre>
	 * @param e the exception describe error
	 * @return sql error message
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
	
}
