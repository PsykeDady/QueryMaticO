package psykeco.querycraft.sql;

import static psykeco.querycraft.utility.SQLClassParser.getTrueName;
import static psykeco.querycraft.utility.SQLClassParser.parseType;

import java.util.HashMap;
import java.util.Map.Entry;

import psykeco.querycraft.QueryCraft;

public class SQLUpdateCraft implements QueryCraft {
	
	private String table;
	private String db;
	private HashMap<String,Object> filter=new HashMap<>();
	private HashMap<String,Object> kv    =new HashMap<>();
	
	
	@Override
	public SQLUpdateCraft DB(String DB) {
		this.db=DB;
		return this;
	}
	
	@Override
	public SQLUpdateCraft table(String table) {
		this.table=table;
		return this;
	}
	
	
	@Override
	public SQLUpdateCraft entry(Entry<String, Object> kv) {
		return entry(kv.getKey(),kv.getValue());
	}
	
	@Override
	public SQLUpdateCraft entry(String colonna, Object valore) {
		this.kv.putIfAbsent(colonna, valore);
		return this;
	}

	
	@Override
	public SQLUpdateCraft filter(Entry<String, Object> filter) {
		return filter(filter.getKey(),filter.getValue());
	}
	
	@Override
	public SQLUpdateCraft filter(String colonna, Object valore) {
		this.filter.putIfAbsent(colonna, valore);
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
			
			if (kv.getKey()  == null || kv.getKey().equals("") ) return "Una chiave \u00e8 stata trovata vuota";
			if (kv.getValue()== null || val        .equals("") ) return "Il valore di "+kv.getKey()+ "\u00e8 stata trovata vuota";
			if ( ! kv.getKey().matches( BASE_REGEX) ) return "La chiave "+kv.getKey()+" non \u00e8 valida";
			if ( ! val        .matches(VALUE_REGEX) ) return "Il valore "+val        +" non \u00e8 valido";
		}
		
		for (Entry<String,Object> kv : this.filter.entrySet()) {
			String type=parseType((getTrueName(kv.getValue().getClass())),false);
			boolean isString= parseType("String",false).equals(type);
			String value= kv.getValue().toString();
			
			if (kv.getKey()  == null || kv.getKey().equals("") ) return "Una colonna \u00e8 stata trovata vuota";
			if (kv.getValue()== null || value      .equals("") ) return "Il valore di "+kv.getKey()+ "\u00e8 stata trovata vuota";
			if ( ! kv.getKey().matches( BASE_REGEX) ) return "La colonna "+kv.getKey()+" non \u00e8 valida";
			if ( isString && ! value      .matches(VALUE_REGEX) ) return "Il valore " +value      +" non \u00e8 valido";
		}
		
		return "";
	}

	@Override
	public String craft() {
		StringBuilder column=new StringBuilder(kv.size()*20);		
		StringBuilder values=new StringBuilder(filter.size()*20);
		
		String validation=validate();
		if( ! validation.equals("") ) throw new IllegalArgumentException(validation);
		
		column.append("UPDATE `"+db+"`.`"+table+"` SET ");
		
		for (Entry<String,Object> kv : this.kv.entrySet()) {
			column.append("`"+kv.getKey()+"`="+QueryCraft.str(kv.getValue())+"," );
		}
		
		column.deleteCharAt(column.length()-1);
		
		values.append(" WHERE 1=1 ");
		
		for (Entry<String,Object> f : filter.entrySet()) {
			values.append("AND `"+f.getKey() +"`="+QueryCraft.str(f.getValue())+" " );
		}
		
		return (column.toString()+values.toString()).trim();
	}

}
