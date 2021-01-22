package psykeco.querycraft.sql;

import psykeco.querycraft.DBCraft;
import static psykeco.querycraft.QueryCraft.*;

/**
 * SQLDBCraft costruisceì istruzioni SQL 
 * per creare, distruggere o chiedere se esiste
 *  un db
 * 
 * @author psykedady
 **/
public class SQLDBCraft implements DBCraft{
	
	/** nome db (obbligatorio) */
	private String db;

	@Override
	public DBCraft DB(String db) {
		this.db=validateBase(db);
		return this;
	}

	@Override
	public String validate() {
		if (db   ==null || db   .equals("")) return "nome db necessario";
		
		String tmp=validateBase(db);
		if (tmp==null) return " nome db "+db+" non valido";
		
		return "";
	}

	@Override
	public String create() {
		String validation=validate();
		if(! validation.equals("")) throw new IllegalArgumentException(validation);
		
		return "CREATE DATABASE `"+validateBase(db)+"`";
	}

	@Override
	public String exists() {
		String validation=validate();
		if(! validation.equals("")) throw new IllegalArgumentException(validation);
		return "SHOW DATABASES LIKE '"+validateValue(db)+"'";
	}

	@Override
	public String drop() {
		String validation=validate();
		if(! validation.equals("")) throw new IllegalArgumentException(validation);
		return "DROP DATABASE `"+validateBase(db)+"`";
	}

	/**
	 * Questo metodo costruisce l'istruzione da mandare al DB per prelevare una lista di tabelle
	 * 
	 * @return l'istruzione con i campi impostati
 	 * 
	 * @throws IllegalArgumentException se i campi non hanno passato il controllo di validazione
	 */
	@Override
	public String listTables() {
		String validation=validate();
		if(! validation.equals("")) throw new IllegalArgumentException(validation);
		return "SELECT table_name FROM information_schema.tables WHERE table_schema='"+validateBase(db)+"'";
	}

}
