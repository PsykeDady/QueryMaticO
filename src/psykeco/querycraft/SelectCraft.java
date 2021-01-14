package psykeco.querycraft;

import java.util.Map.Entry;

/**
 * SelectCraft estende QueryCraft per l'uso delle join nelle select
 * @author psykedady
 *
 */
public abstract class SelectCraft implements QueryCraft{
	
	protected String alias;
	
	
	public SelectCraft alias(String alias) {
		this.alias=alias;
		return this;
	}
	
	protected String attachAlias(String what) {
		if(alias==null || alias.equals("")) 
			return "";
		return "`"+alias+"`.`"+what+"`";
	}
	
	public abstract SelectCraft entry (String valore);
	
	public abstract SelectCraft join(SelectCraft joinTable);
	public abstract SelectCraft joinFilter (Entry<String,String> thisOther);
	public abstract SelectCraft joinFilter (String columnThis, String columnOther);
	
	protected abstract String selectCraft();
	protected abstract String   fromCraft();
	protected abstract String  whereCraft();
	
}
