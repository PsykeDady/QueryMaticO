package psykeco.querycraft.sql;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import psykeco.querycraft.QueryCraft;
import psykeco.querycraft.SelectCraft;
import static psykeco.querycraft.utility.SQLClassParser.parseType;
import static psykeco.querycraft.utility.SQLClassParser.getTrueName;

public class SQLSelectCraft extends SelectCraft {

	private String table;
	private String db;
	private HashMap<String,Object> filter=new HashMap<>();
	private Set<String> kv=new TreeSet<>();
	/** couple join table and alias name*/
	private SQLSelectCraft joinTable; 
	/** map of this-column join-column filter  */
	private HashMap<String,String> joinFilter=new HashMap<>();
	
	
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
	@Override
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
			String type=parseType((getTrueName(kv.getValue().getClass())),false);
			boolean isString= parseType("String",false).equals(type);
			String value= kv.getValue().toString();
			
			if (kv.getKey()  == null || kv.getKey().equals("") ) return "Una colonna \u00e8 stata trovata vuota";
			if (kv.getValue()== null || value      .equals("") ) return "Il valore di "+kv.getKey()+ "\u00e8 stata trovata vuota";
			if ( ! kv.getKey()      .matches( BASE_REGEX) ) return "La colonna "+kv.getKey()+" non \u00e8 valida";
			if ( isString && ! value.matches(VALUE_REGEX) ) return "Il valore " +value      +" non \u00e8 valido";
		}
		
		return (joinTable!=null)?joinTable.validate():"";
	}

	@Override
	public String craft() {
		String validation=validate();
		if( ! validation.equals("") ) throw new IllegalArgumentException(validation);
		
		
		return "SELECT "+selectCraft()+" FROM "+fromCraft()+" WHERE 1=1 "+whereCraft();
	}

	@Override
	public SelectCraft join(SelectCraft joinTable) {
		if( ! (joinTable instanceof SelectCraft) ) 
			throw new IllegalArgumentException("la tabella di join deve essere di tipo SQLSelectCraft");
		this.joinTable=(SQLSelectCraft) joinTable;
		return this;
	}

	@Override
	public SelectCraft joinFilter(Entry<String, String> thisOther) {
		
		return joinFilter(thisOther.getKey(),thisOther.getValue());
	}

	@Override
	public SelectCraft joinFilter(String columnThis, String columnOther) {
		joinFilter.put(columnThis, columnOther);
		return this;
	}

	@Override
	public String selectCraft() {
		StringBuilder sb=new StringBuilder();
		
		if (kv.size()!=0) {
			for (String k : kv ) {
				sb.append(attachAlias(k)+",");
			}
			sb.deleteCharAt(sb.length()-1);
		}
		
		if( joinTable != null ) {
			sb.append(","+joinTable.selectCraft());
		}
		
		String result=sb.toString();	
		return result.equals("") ? attachAlias(null) : result.trim();
	}

	@Override
	public String fromCraft() {
		StringBuilder sb=new StringBuilder();
		
		sb.append("`"+db+"`.`"+table+"`");
		if(alias!=null)
			sb.append("`"+alias+"`");
		
		if( joinTable != null ) {
			sb.append(", "+joinTable.fromCraft());
		}
		
		String result=sb.toString();	
		return result.equals("") ? attachAlias("*") : result.trim();

	}

	@Override
	public String whereCraft() {
		StringBuilder sb=new StringBuilder();
		
		for (Entry<String,Object> f : filter.entrySet()) {
			sb.append("AND "+attachAlias(f.getKey())+"="+QueryCraft.str(f.getValue())+" " );
		}
		
		if( joinTable != null ) {
			sb.append(joinTable.whereCraft());
			for (Entry<String,String> f : joinFilter.entrySet()) {
				sb.append(" AND "+attachAlias(f.getKey())+"="+joinTable.attachAlias(f.getValue())+" " );
			}
		}
		
		String result=sb.toString();	
		return result.trim();
	}


}
