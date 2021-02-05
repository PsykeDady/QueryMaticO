package psykeco.querycraft.sql;

import psykeco.querycraft.DBCraft;
import psykeco.querycraft.sql.models.Tables;
import psykeco.querycraft.utility.SQLClassParser;

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
	/**
	 * @deprecated use {@link InformationSchema #existsDB(String)} instead
	 **/
	@Override
	@Deprecated
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
	 * 
	 * @deprecated use {@link InformationSchema #listDB()} instead
	 */
	@Override
	@Deprecated
	public String listTables() {
		String validation=validate();
		if(! validation.equals("")) throw new IllegalArgumentException(validation);
		return "SELECT table_name FROM "+InformationSchema.DB+"."+SQLClassParser.getTrueName(Tables.class)+" WHERE table_schema='"+validateBase(db)+"'";
	}
	
	@Override
	protected SQLDBCraft clone(){
		SQLDBCraft dbcf=null;
		try {
			dbcf= (SQLDBCraft) super.clone();
			return dbcf;
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public DBCraft copy() {
		DBCraft dbcf=new SQLDBCraft();
		dbcf.DB(db);
		
		return dbcf;
	}

}
