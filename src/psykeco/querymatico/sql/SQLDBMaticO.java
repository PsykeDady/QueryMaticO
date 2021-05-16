package psykeco.querymatico.sql;

import static psykeco.querymatico.sql.utility.SQLClassParser.validateBase;
import static psykeco.querymatico.translations.Translations.KEY_MSG.DB_NOT_VALID;
import static psykeco.querymatico.translations.Translations.KEY_MSG.DB_NULL;

import psykeco.querymatico.DBMaticO;
import psykeco.querymatico.sql.runners.InformationSchema;
import psykeco.querymatico.translations.Translations;

/**
 * MySQL implementation of {@link DBMaticO}
 * 
  * @author PsykeDady (psdady@msn.com)
 * */
public class SQLDBMaticO implements DBMaticO{
	
	/** db name (required) */
	private String db;

	/**
	 * set db name
	 * @param db : db new name
	 * @return updated SQLDBMaticO instance
	 */
	@Override
	public SQLDBMaticO DB(String db) {
		this.db=validateBase(db);
		return this;
	}
	
	/**
	 * check all the fields in order to validate a possible query. <br>
	 * Returned value represent a String with encountered 
	 * error or empty string if every controls passes
	 * 
	 * @return empty string if all check is passed, an error message otherwise
	 */
	@Override
	public String validate() {
		if (db   ==null || db   .equals("")) return Translations.getMsg(DB_NULL);
		
		String tmp=validateBase(db);
		if (tmp==null) return Translations.getMsg(DB_NOT_VALID,db);
		
		return "";
	}
	
	/**
	 * Build statement to create a new schema, only if 
	 * {@link #validate()} passes with success.
	 * 
	 * @return String of instruction 
	 * 
	 * @throws IllegalArgumentException if {@link #validate()} fail
	 */
	@Override
	public String create() {
		String validation=validate();
		if(! validation.equals("")) throw new IllegalArgumentException(validation);
		
		return "CREATE DATABASE `"+validateBase(db)+"`";
	}

	/**
	 * Build statement to check if a schema exists, only if 
	 * {@link #validate()} passes with success.
	 * 
	 * @return String of instruction 
	 * 
	 * @throws IllegalArgumentException if {@link #validate()} fail
	 */
	@Override
	public String exists() {
		return InformationSchema.existsDBBuild(db);
	}

	/**
	 * Build statement to delete a schema, only if 
	 * {@link #validate()} passes with success.
	 * 
	 * @return String of instruction 
	 * 
	 * @throws IllegalArgumentException if {@link #validate()} fail
	 */
	@Override
	public String drop() {
		String validation=validate();
		if(! validation.equals("")) throw new IllegalArgumentException(validation);
		return "DROP DATABASE `"+validateBase(db)+"`";
	}

	/**
	 * Build statement to list db of a schema, only if 
	 * {@link #validate()} passes with success.
	 * 
	 * @return String of instruction 
	 * 
	 * @throws IllegalArgumentException if {@link #validate()} fail
	 */
	@Override
	public String listTables() {
		
		return InformationSchema.listTablesBuild(db);
	}
	
	/**
	 * create a DBMaticO as new object with same data of this.
	 * 
	 * @return the new instance
	 */
	@Override
	public SQLDBMaticO copy() {
		SQLDBMaticO dbcf=new SQLDBMaticO();
		dbcf.DB(db);
		
		return dbcf;
	}

}
