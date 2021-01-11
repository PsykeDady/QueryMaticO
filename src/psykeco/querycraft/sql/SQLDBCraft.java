package psykeco.querycraft.sql;

import java.util.Map.Entry;

import psykeco.querycraft.DBCraft;
import psykeco.querycraft.TableCraft;

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
	public DBCraft db(String db) {
		this.db=db;
		return this;
	}

	@Override
	public String validate() {
		if (db   ==null || db   .equals("")) return "nome db necessario";
		
		if (! db   .matches(BASE_REGEX)) return " nome db "+db+" non valido";
		
		return "";
	}

	@Override
	public String create() {
		String validation=validate();
		if(! validation.equals("")) throw new IllegalArgumentException(validation);
		return "create database `"+db+"`";
	}

	@Override
	public String select() {
		String validation=validate();
		if(! validation.equals("")) throw new IllegalArgumentException(validation);
		return "show databases like '"+db+"'";
	}

	@Override
	public String drop() {
		String validation=validate();
		if(! validation.equals("")) throw new IllegalArgumentException(validation);
		return "drop database `"+db+"`";
	}

}
