package psykeco.querycraft.sql;

import java.util.HashMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import psykeco.querycraft.QueryCraft;
import psykeco.querycraft.SelectCraft;

public class SQLSelectCraft implements SelectCraft {

	private String table;
	private String db;
	private HashMap<String,Object> filter=new HashMap<>();
	private Set<String> kv=new TreeSet<>();
	
	
	@Override
	public SQLSelectCraft DB(String DB) {
		this.db=DB;
		return this;
	}
	
	@Override
	public SQLSelectCraft table(String table) {
		this.table=table;
		return this;
	}
	
	
	/**
	 * Aggiunge un campo alla select.
	 * 
	 * @param colonna : campo da aggiungere alla select
	 * 
	 * @return un istanza di SQLSelectCraft con il campo aggiunto alla select
	 * */
	public SQLSelectCraft entry(String colonna) {
		this.kv.add(colonna);
		return this;
	}
	
	/**
	 * Aggiunge un campo alla select, ignora il parametro valore. Richiama {@link #entry(String)}
	 * 
	 * @param colonna : campo da aggiungere alla select
	 * 
	 * @param valore : ignorato
	 * 
	 * @return un istanza di SQLSelectCraft con il campo aggiunto alla select
	 * */
	@Override
	public SQLSelectCraft entry(String colonna, Object valore) {
		return entry(colonna);
	}
	
	/**
	 * Aggiunge un campo alla select, ignora il parametro valore. <br>
	 * Leggere {@link #entry(String)}
	 * 
	 * @param kv : un istanza chiave valore di {@link Entry} di cui viene ignorato il valore.
	 * 
	 * @return un istanza di SQLSelectCraft con il campo aggiunto alla select
	 * */
	@Override
	public SQLSelectCraft entry(Entry<String, Object> kv) {
		return entry(kv.getKey());
	}

	
	@Override
	public SQLSelectCraft filter(Entry<String, Object> filter) {
		return filter(filter.getKey(),filter.getValue());
	}
	
	@Override
	public SQLSelectCraft filter(String colonna, Object valore) {
		this.filter.putIfAbsent(colonna, valore);
		return this;
	}

	@Override
	public String validate() {
		
		if (table==null || table.equals("")) return "nome tabella necessario";
		if (db   ==null || db   .equals("")) return "nome db necessario";
		
		if (! table.matches(BASE_REGEX)) return " nome tabella "+table+" non valido";
			
		if (! db   .matches(BASE_REGEX)) return " nome db "+db+" non valido";
		
		for (String s : this.kv) {
			if (s==null || s.equals("")) return "Una colonna \u00e8 stata trovata vuota";
			if (! s.matches(BASE_REGEX)) return "La colonna "+s+" non \u00e8 valida";
		}
		
		for (Entry<String,Object> kv : this.filter.entrySet()) {
			String value=kv.getValue().toString();
			if (kv.getKey()  == null || kv.getKey().equals("") ) return "Una colonna \u00e8 stata trovata vuota";
			if (kv.getValue()== null || value      .equals("") ) return "Il valore di "+kv.getKey()+ "\u00e8 stata trovata vuota";
			if ( ! kv.getKey().matches( BASE_REGEX) ) return "La colonna "+kv.getKey()+" non \u00e8 valida";
			if ( ! value      .matches(VALUE_REGEX) ) return "Il valore " +value      +" non \u00e8 valido";
		}
		
		return "";
	}

	@Override
	public String craft() {
		StringBuilder column=new StringBuilder(kv.size()*10);		
		StringBuilder values=new StringBuilder(filter.size()*20);
		
		String validation=validate();
		if( ! validation.equals("") ) throw new IllegalArgumentException(validation);
		
		column.append("select");
		for (String k : kv ) {
			column.append(" `"+k+"`,");
		}
		if (kv.size()==0) 
			column.append(" *");
		else
			column.deleteCharAt(column.length()-1);
		
		values.append(" from `"+db+"`.`"+table+"`");
		
		
		values.append(" where 1=1 ");
		
		for (Entry<String,Object> f : filter.entrySet()) {
			values.append("AND `"+f.getKey() +"`="+QueryCraft.str(f.getValue())+" " );
		}
		
		
		return column.toString()+values.toString();
	}


}
