package psykeco.querymatico.sql;

import static psykeco.querymatico.sql.utility.SQLClassParser.getTrueName;
import static psykeco.querymatico.sql.utility.SQLClassParser.parseType;
import static psykeco.querymatico.sql.utility.SQLClassParser.str;
import static psykeco.querymatico.sql.utility.SQLClassParser.validateBase;
import static psykeco.querymatico.sql.utility.SQLClassParser.validateValue;

import java.util.HashMap;
import java.util.Map.Entry;

import psykeco.querymatico.QueryMaticO;
import psykeco.querymatico.sql.runners.MySqlConnection;

/**
 * Costruisce la insert per le query di tipo SQL<br>
 * implementa {@link QueryMaticO}
 * 
 * @author psykedady
 *
 */
public class SQLInsertMaticO implements QueryMaticO {
	
	public SQLInsertMaticO() {}
	
	private String table;
	private String db;
	private HashMap<String,Object> kv=new HashMap<>();
	
	
	@Override
	public SQLInsertMaticO DB(String DB) {
		this.db=DB;
		return this;
	}
	
	@Override
	public SQLInsertMaticO table(String table) {
		this.table=table;
		return this;
	}

	@Override
	public SQLInsertMaticO entry(Entry<String, Object> kv) {
		return entry(kv.getKey(),kv.getValue());
	}
	
	@Override
	public SQLInsertMaticO entry(String colonna, Object valore) {
		this.kv.putIfAbsent(colonna, valore);
		return this;
	}

	@Override
	public String validate() {
		
		if (table==null || table.equals("")) return "nome tabella necessario";
		if (db   ==null || db   .equals("")) return "nome db necessario"     ;
		
		String tmp=validateBase(table);
		if (tmp==null) return " nome tabella "+table+" non valido";
		
		tmp=validateBase(db);
		if (tmp==null) return " nome db "+db+" non valido";
		
		if ( kv.size() < 1 ) return "lista entry vuota. Serve almeno una coppia colonna-valore";
		
		
		for (Entry<String,Object> kv : this.kv.entrySet()) {
			String type=parseType((getTrueName(kv.getValue().getClass())),false);
			boolean isString= parseType("String",false).equals(type);
			String value= kv.getValue().toString();
			
			if (kv.getKey()  == null || kv.getKey().equals("") ) return "Una colonna \u00e8 stata trovata vuota";
			if (kv.getValue()== null || value      .equals("") ) return "Il valore di "+kv.getKey()+ "\u00e8 stata trovata vuota";
			
			tmp=validateBase(kv.getKey());
			if ( tmp==null ) return "La colonna "+kv.getKey()+" non \u00e8 valida";
			tmp= isString ? validateValue(value): value;
			if ( tmp==null ) return "Il valore " +value      +" non \u00e8 valido";
		}
		
		return "";
	}

	@Override
	public String build() {
		StringBuilder column=new StringBuilder(kv.size()*20);
		StringBuilder values=new StringBuilder(kv.size()*10);
		String thisdb=this.db;
		this.db=(this.db==null)? MySqlConnection.db():this.db;
		String validation=validate();
		if( ! validation.equals("") ) throw new IllegalArgumentException(validation);
		String db=validateBase(this.db), table=validateBase(this.table);
		
		values.append("INSERT INTO "+'`'+db+"`.`"+table+'`'+" ( ");
		column.append(" VALUES (");
		
		for (Entry<String,Object> kv : this.kv.entrySet()) {
			String key=validateBase(kv.getKey()),
					value=str(kv.getValue());
			values.append( '`'+key+"`," );
			column.append(value+"," );
		}
		
		values.setCharAt(values.length()-1, ')');
		column.setCharAt(column.length()-1, ')');
		
		this.db=thisdb;
		return values.toString()+column.toString();
	}

	@Override
	public SQLInsertMaticO filter(Entry<String, Object> filter) {
		throw new UnsupportedOperationException("SqlInsertMaticO does not support filter");
	}

	@Override
	public SQLInsertMaticO filter(String colonna, Object valore) {
		throw new UnsupportedOperationException("SqlInsertMaticO does not support filter");
	}

	@Override
	public SQLInsertMaticO copy() {
		SQLInsertMaticO cf=new SQLInsertMaticO().DB(db).table(table);
		if (kv!=null) for( Entry <String,Object > kv: this.kv.entrySet()) {
			cf.entry(kv);
		}
		return cf;
	}

}
