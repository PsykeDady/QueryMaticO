package psykeco.querycraft.sql;

import static psykeco.querycraft.sql.utility.SQLClassParser.validateBase;

import psykeco.querycraft.DBCraft;
import psykeco.querycraft.sql.runners.InformationSchema;

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
	public SQLDBCraft DB(String db) {
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
	 **/
	@Override
	public String exists() {
		return InformationSchema.existsDBCraft(db);
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
	 */
	@Override
	public String listTables() {
		
		return InformationSchema.listTablesCraft(db);
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
	public SQLDBCraft copy() {
		SQLDBCraft dbcf=new SQLDBCraft();
		dbcf.DB(db);
		
		return dbcf;
	}

}
