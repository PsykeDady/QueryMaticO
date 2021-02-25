package psykeco.querycraft.sql;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;
import java.util.Set;
import java.util.TreeSet;
import static psykeco.querycraft.QueryCraft.*;
import psykeco.querycraft.QueryCraft;
import psykeco.querycraft.SelectCraft;
import static psykeco.querycraft.utility.SQLClassParser.parseType;
import static psykeco.querycraft.utility.SQLClassParser.getTrueName;

public class SQLSelectCraft extends SelectCraft {
	
	// aggregate and other operator 
	private static enum AGGREGATE {
		SUM,
		AVG,
		COUNT,
		GROUP_CONCAT,
		COUNT_DISTINCT,
		DISTINCT,
		MAX,
		MIN
	}

	/** nome tabella */
	private String table;
	/** nome db */
	private String db;
	/** i filtri formano la where */
	private HashMap<String,Object> filter=new HashMap<>();
	/** insieme di chiavi da inserire nella from */
	private Set<String> kv=new TreeSet<>();
	/** couple join table and alias name*/
	private SQLSelectCraft joinTable; 
	/** map of this-column join-column filter  */
	private HashMap<String,String> joinFilter=new HashMap<>();
	
	/** map of aggregate (key) and values */
	private HashMap<AGGREGATE,String> aggregatesColumn=new HashMap<>();
	/** column on order by clausole (key) with boolean flag to indicate if ordering is ascendenting */
	private Entry<String,Boolean> orderBy; 
	
	/** column on group by clausole */ 
	private String groupBy;
	
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
		
		if  (table==null || table.equals(""))                                 return "nome tabella necessario";
		if ((db   ==null || db   .equals("")) && MySqlConnection.db()!=null ) return "nome db necessario"     ;
		
		String tmp=validateBase(table);
		if (tmp==null) return " nome tabella "+table+" non valido";
		
		tmp=validateBase(db);
		if (tmp==null) return " nome db "+db+" non valido";
		
		tmp=validateBase(alias);
		if (alias!=null && tmp==null) return " nome alias "+alias+" non valido";
		
		if ( groupBy!=null && validateBase(groupBy)==null) 
			return "colonna indicata da group by '"+groupBy+"' non valida";
		
		if ( orderBy != null && orderBy.getKey() == null )
			return "colonna indicata da order by non pu&ograve; essere nulla";
		
		if ( orderBy != null && validateBase(orderBy.getKey())==null)
			return "colonna indicata da order by '"+orderBy.getKey()+"' non valida";
		
		for (Entry<AGGREGATE,String> kv: aggregatesColumn.entrySet()) {
			if(kv.getValue()!=null && validateBase(kv.getValue())==null)
				return "colonna indicata da "+kv.getKey().name()+" '"+kv.getValue()+"' non valida";
		}
		
		for (String s : this.kv) {
			if (s==null || s.equals("")) return "Una colonna \u00e8 stata trovata vuota";
			if (validateBase(s)==null) return "La colonna "+s+" non \u00e8 valida";
		}
		
		for (Entry<String,Object> kv : this.filter.entrySet()) {
			String type=parseType((getTrueName(kv.getValue().getClass())),false);
			boolean isString= parseType("String",false).equals(type);
			String value= kv.getValue().toString();
			
			if (kv.getKey()  == null || kv.getKey().equals("") ) return "Una colonna \u00e8 stata trovata vuota";
			if (kv.getValue()== null || value      .equals("") ) return "Il valore di "+kv.getKey()+ "\u00e8 stata trovata vuota";
			
			tmp=QueryCraft.validateBase(kv.getKey());
			if ( tmp==null ) return "La colonna "+kv.getKey()+" non \u00e8 valida";
			String tmpV= isString ? QueryCraft.validateValue(value): value;
			if ( tmpV==null ) return "Il valore " +value      +" non \u00e8 valido";
		}
		
		return (joinTable!=null)?joinTable.validate():"";
	}

	@Override
	public String craft() {
		String validation=validate();
		if( ! validation.equals("") ) throw new IllegalArgumentException(validation);
		
		
		return 
			("SELECT "+selectCraft()+
			" FROM "+fromCraft()+
			" WHERE 1=1 "+whereCraft()+
			groupByCraft()+
			orderByCraft()).trim()
		;
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
		
		for (String k : kv ) {
			sb.append(attachAlias(k)+",");
		}
		
		for(Entry<AGGREGATE,String> kv:aggregatesColumn.entrySet()) {
			if(kv.getKey()==AGGREGATE.COUNT_DISTINCT)
				sb.append(AGGREGATE.COUNT+"("+AGGREGATE.DISTINCT+"("+attachAlias(kv.getValue())+")),");
			else 
				sb.append(kv.getKey().name()+"("+attachAlias(kv.getValue())+"),");
		}
		
		if( joinTable != null ) {
			sb.append(joinTable.selectCraft());
		}
		
		if(sb.length()>0 && sb.charAt(sb.length()-1)==',') sb.deleteCharAt(sb.length()-1);
		
		String result=sb.toString();	
		return result.equals("") ? attachAlias(null) : result.trim();
	}

	@Override
	public String fromCraft() {
		StringBuilder sb=new StringBuilder();
		String db=validateBase(this.db), table=validateBase(this.table),
				alias=validateBase(this.alias);
		
		sb.append("`"+db+"`.`"+table+"`");
		if(alias!=null)
			sb.append(" `"+alias+"`");
		
		if( joinTable != null ) {
			sb.append(", "+joinTable.fromCraft());
		}
		
		String result=sb.toString();	
		return result.trim();

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
	
	@Override
	protected String groupByCraft() {
		if(groupBy==null||groupBy.equals("")) return "";
		return " GROUP BY "+attachAlias(groupBy);
	}

	@Override
	protected String orderByCraft() {
		if(orderBy==null) return "";
		return " ORDER BY "+attachAlias(orderBy.getKey())+" "+(orderBy.getValue()?"ASC":"DESC");
	}

	@Override
	public SelectCraft count(String column) {
		String c=aggregatesColumn.get(AGGREGATE.DISTINCT);
		if(column!=null && column.equals(c)) {
			aggregatesColumn.put(AGGREGATE.COUNT_DISTINCT, column);
			aggregatesColumn.remove(AGGREGATE.DISTINCT);
		}
		else aggregatesColumn.put(AGGREGATE.COUNT, column);
		return this;
	}

	@Override
	public SelectCraft distinct(String column) {
		String c=aggregatesColumn.get(AGGREGATE.COUNT);
		if(column!=null && column.equals(c)) {
			aggregatesColumn.put(AGGREGATE.COUNT_DISTINCT, column);
			aggregatesColumn.remove(AGGREGATE.COUNT);
		}
		else aggregatesColumn.put(AGGREGATE.DISTINCT, column);
		return this;
	}

	@Override
	public SelectCraft sum(String column) {
		aggregatesColumn.put(AGGREGATE.SUM, column);
		return this;
	}

	@Override
	public SelectCraft groupBy(String column) {
		groupBy=column;
		return this;
	}
	

	@Override
	public SelectCraft orderBy(String column, boolean asc) {
		orderBy=new SimpleEntry<>(column, asc);
		return this;
	}

	@Override
	public SelectCraft copy() {
		SelectCraft scf=new SQLSelectCraft().DB(db).table(table).groupBy(groupBy);
		if (orderBy!=null && orderBy.getKey()!=null) 
				scf.orderBy(orderBy.getKey(),orderBy.getValue());
		
		if( filter!=null) for(Entry<String,Object> kv : filter.entrySet()) scf.filter(kv);
		if (kv!=null) for(String entry:kv) scf.entry(entry);
		if (joinFilter!=null) for(Entry<String,String> kv : joinFilter.entrySet()) scf.joinFilter(kv);
		if (aggregatesColumn!=null) for(Entry<AGGREGATE,String> kv: aggregatesColumn.entrySet()) 
			switch(kv.getKey()) {
				case AVG : break ; 
				case COUNT : scf.count(kv.getValue()); break ; 
				case COUNT_DISTINCT : scf.count(kv.getValue());scf.distinct(kv.getValue()); break ; 
				case DISTINCT : scf.distinct(kv.getValue()); break ; 
				case GROUP_CONCAT :  break ; 
				case MAX :  break ; 
				case MIN :  break ; 
				case SUM : scf.sum(kv.getValue()); break ; 
				default : break ; 
			}
		if(joinTable!=null) scf.join(joinTable.copy());
		
		return scf;
	}

}
