package psykeco.querymatico.sql;

import static psykeco.querymatico.sql.utility.SQLClassParser.validateBase;

import psykeco.querymatico.DBMaticO;
import psykeco.querymatico.sql.runners.InformationSchema;

/**
 * SQLDBMaticO costruisceì istruzioni SQL 
 * per creare, distruggere o chiedere se esiste
 *  un db
 * 
 * @author psykedady
 **/
public class SQLDBMaticO implements DBMaticO{
	
	/** nome db (obbligatorio) */
	private String db;

	@Override
	public SQLDBMaticO DB(String db) {
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
		return InformationSchema.existsDBBuild(db);
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
		
		return InformationSchema.listTablesBuild(db);
	}
	
	@Override
	protected SQLDBMaticO clone(){
		SQLDBMaticO dbcf=null;
		try {
			dbcf= (SQLDBMaticO) super.clone();
			return dbcf;
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public SQLDBMaticO copy() {
		SQLDBMaticO dbcf=new SQLDBMaticO();
		dbcf.DB(db);
		
		return dbcf;
	}

}
