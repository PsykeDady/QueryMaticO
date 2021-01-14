package psykeco.querycraft;

import java.util.Map.Entry;

/**
 * SelectCraft estende QueryCraft per l'uso delle join nelle select
 * @author psykedady
 *
 */
public abstract class SelectCraft implements QueryCraft{
	
	/**
	 * alias name for table 
	 */
	protected String alias;
	
	
	/**
	 * imposta il nome alias da usare per le query nella select, 
	 * per evitare che in join i nomi si sovrappongono
	 * 
	 * @param alias il nome da usare 
	 * @return l'istanza di SelectCraft aggiornata
	 */
	public SelectCraft alias(String alias) {
		this.alias=alias;
		return this;
	}
	
	/**
	 * attacca a what l'alias, utile per i campi della where e della select
	 * 
	 * @param what a cosa attaccare l'alias. Se null, mette *
	 * 
	 * @return la stringa con l'alias ( se esiste) in prefisso
	 */
	protected String attachAlias(String what) {
		what=(what==null)?"*":"`"+what+"`";
		if(alias==null || alias.equals("")) 
			return what;
		return "`"+alias+"`."+what;
	}
	
	/**
	 * imposta un campo nella select.
	 * @param valore
	 * @return istanza aggiornata di SelectCraft
	 */
	public abstract SelectCraft entry (String valore);
	
	/**
	 * aggiunge una SelectCraft in join
	 * @param joinTable
	 * @return istanza aggiornata di SelectCraft
	 */
	public abstract SelectCraft join(SelectCraft joinTable);
	
	/**
	 * aggiunge nei filtri della where una coppia di colonne
	 * @param thisOther coppia chiave (colonna di this) valore (colonna della tabella in join)
	 * @return istanza aggiornata di SelectCraft
	 */
	public abstract SelectCraft joinFilter (Entry<String,String> thisOther);
	
	/**
	 * aggiunge nei filtri della where una coppia di colonne
	 * @param columnThis colonna di this 
	 * @param columnOther colonna della tabella in join
	 * @return istanza aggiornata di SelectCraft
	 */
	public abstract SelectCraft joinFilter (String columnThis, String columnOther);
	
	/**
	 * 
	 * @return la lista dei campi da mettere nella select
	 */
	protected abstract String selectCraft();
	/**
	 * 
	 * @return la lista dei campi da mettere nella from
	 */
	protected abstract String   fromCraft();
	/**
	 * 
	 * @return la lista dei campi da mettere nella where
	 */
	protected abstract String  whereCraft();
	
}
