package psykeco.querycraft.sql;

import static psykeco.querycraft.utility.SQLClassParser.getTrueName;
import static psykeco.querycraft.utility.SQLClassParser.parseType;
import static psykeco.querycraft.QueryCraft.*;
import java.util.HashMap;
import java.util.Map.Entry;

import psykeco.querycraft.QueryCraft;

public class SQLDeleteCraft implements QueryCraft{
	private String table;
	private String db;
	private HashMap<String,Object> filter=new HashMap<>();
	
	@Override
	public SQLDeleteCraft DB(String DB) {
		this.db=DB;
		return this;
	}
	
	@Override
	public SQLDeleteCraft table(String table) {
		this.table=table;
		return this;
	}
	
	
	@Override
	public SQLDeleteCraft entry(Entry<String, Object> kv) {
		throw new UnsupportedOperationException("SqlDeleteCraft does not support entry");
	}
	
	@Override
	public SQLDeleteCraft entry(String colonna, Object valore) {
		throw new UnsupportedOperationException("SqlDeleteCraft does not support entry");
	}

	
	@Override
	public SQLDeleteCraft filter(Entry<String, Object> filter) {
		return filter(filter.getKey(),filter.getValue());
	}
	
	@Override
	public SQLDeleteCraft filter(String colonna, Object valore) {
		this.filter.putIfAbsent(colonna, valore);
		return this;
	}

	@Override
	public String validate() {
		
		if (table==null || table.equals("")) return "nome tabella necessario";
		if (db   ==null || db   .equals("")) return "nome db necessario";
		
		String tmp=validateBase(table);
		if (tmp==null) return " nome tabella "+table+" non valido";
		
		tmp=validateBase(db);
		if (tmp==null) return " nome db "+db+" non valido";
		
		for (Entry<String,Object> kv : this.filter.entrySet()) {
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
	public String craft() {
		StringBuilder values=new StringBuilder(filter.size()*20);
		
		String validation=validate();
		if( ! validation.equals("")) throw new IllegalArgumentException(validation);
		
		String db=validateBase(this.db),table=validateBase(this.table);
		
		values.append("DELETE FROM `"+db+"`.`"+table+"` WHERE 1=1 ");
		
		for (Entry<String,Object> f : filter.entrySet()) {
			String key=validateBase(f.getKey()),
				value=str(f.getValue());
			values.append("AND `"+key+"`="+value+" " );
		}
		
		return values.toString().trim();
	}

	@Override
	public QueryCraft copy() {
		QueryCraft cf=new SQLDeleteCraft().DB(db).table(table);
		if(filter!=null) for( Entry <String,Object > kv: filter.entrySet()) {
			cf.filter(kv);
		}
		return cf;
	}

	
}
