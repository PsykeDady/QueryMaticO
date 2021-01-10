package psykeco.querycraft.sql;

import java.util.HashMap;
import java.util.Map.Entry;

import psykeco.querycraft.QueryCraft;

/**
 * Costruisce la insert per le query di tipo SQL<br>
 * implementa {@link QueryCraft}
 * 
 * @author psykedady
 *
 */
public class SQLInsertCraft implements QueryCraft {
	
	public SQLInsertCraft() {}
	
	public final static String INIT="insert into ";
	
	private String table;
	private String db;
	private HashMap<String,Object> kv=new HashMap<>();
	
	
	@Override
	public SQLInsertCraft DB(String DB) {
		this.db=DB;
		return this;
	}
	
	@Override
	public SQLInsertCraft table(String table) {
		this.table=table;
		return this;
	}

	@Override
	public SQLInsertCraft entry(Entry<String, Object> kv) {
		return entry(kv.getKey(),kv.getValue());
	}
	
	@Override
	public SQLInsertCraft entry(String colonna, Object valore) {
		this.kv.putIfAbsent(colonna, valore);
		return this;
	}

	@Override
	public String validate() {
		
		if (table==null || table.equals("")) return "nome tabella necessario";
		if (db   ==null || db   .equals("")) return "nome db necessario";
		
		if (! table.matches(BASE_REGEX)) return " nome tabella "+table+" non valido";
			
		if (! db   .matches(BASE_REGEX)) return " nome db "+db+" non valido";
		
		if ( kv.size() < 1 ) return "lista entry vuota. Serve almeno una coppia colonna-valore";
		
		
		for (Entry<String,Object> kv : this.kv.entrySet()) {
			String val=kv.getValue().toString();
			
			if (kv.getKey()  == null || kv.getKey().equals("") ) return "Una colonna \u00e8 stata trovata vuota";
			if (kv.getValue()== null || val        .equals("") ) return "Il valore di "+kv.getKey()+ "\u00e8 stata trovata vuota";
			if ( ! kv.getKey().matches( BASE_REGEX) ) return "La colonna "+kv.getKey()+" non \u00e8 valida";
			if ( ! val        .matches(VALUE_REGEX) ) return "Il valore " +val        +" non \u00e8 valido";
		}
		
		return "";
	}

	@Override
	public String craft() {
		StringBuilder column=new StringBuilder(kv.size()*20);
		StringBuilder values=new StringBuilder(kv.size()*10);
		
		String validation=validate();
		if( ! validation.equals("") ) throw new IllegalArgumentException(validation);
		
		values.append(INIT+'`'+db+"`.`"+table+'`'+" ( ");
		column.append(" values (");
		
		for (Entry<String,Object> kv : this.kv.entrySet()) {
			values.append( '`'+kv.getKey()  +"`," );
			column.append(QueryCraft.str(kv.getValue())+"," );
		}
		
		values.setCharAt(values.length()-1, ')');
		column.setCharAt(column.length()-1, ')');
		
		return values.toString()+column.toString();
	}

	@Override
	public QueryCraft filter(Entry<String, Object> filter) {
		throw new UnsupportedOperationException("SqlInsertCraft does not support filter");
	}

	@Override
	public QueryCraft filter(String colonna, Object valore) {
		throw new UnsupportedOperationException("SqlInsertCraft does not support filter");
	}

}
