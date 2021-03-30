package psykeco.querymatico;

import static psykeco.querymatico.sql.utility.SQLClassParser.*;

import java.util.Map.Entry;

/**
 * SelectMaticO estende QueryMaticO per l'uso delle join nelle select
 * @author psykedady
 *
 */
public abstract class SelectMaticO implements QueryMaticO{
	
	/**
	 * alias name for table 
	 */
	protected String alias;
	
	
	/**
	 * imposta il nome alias da usare per le query nella select, 
	 * per evitare che in join i nomi si sovrappongono
	 * 
	 * @param alias il nome da usare 
	 * @return l'istanza di SelectMaticO aggiornata
	 */
	public SelectMaticO alias(String alias) {
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
		what=(what==null)?"*":"`"+validateBase(what)+"`";
		if(alias==null || alias.equals("")) 
			return what;
		return "`"+validateBase(alias)+"`."+what;
	}
	
	/**
	 * imposta un campo nella select.
	 * @param valore
	 * @return istanza aggiornata di SelectMaticO
	 */
	public abstract SelectMaticO entry (String valore);
	
	/**
	 * aggiunge una SelectMaticO in join
	 * @param joinTable
	 * @return istanza aggiornata di SelectMaticO
	 */
	public abstract SelectMaticO join(SelectMaticO joinTable);
	
	/**
	 * aggiunge nei filtri della where una coppia di colonne
	 * @param thisOther coppia chiave (colonna di this) valore (colonna della tabella in join)
	 * @return istanza aggiornata di SelectMaticO
	 */
	public abstract SelectMaticO joinFilter (Entry<String,String> thisOther);
	
	/**
	 * aggiunge nei filtri della where una coppia di colonne
	 * @param columnThis colonna di this 
	 * @param columnOther colonna della tabella in join
	 * @return istanza aggiornata di SelectMaticO
	 */
	public abstract SelectMaticO joinFilter (String columnThis, String columnOther);
	
	/**
	 * add count aggregate on a column name<br>
	 * if the same column is in distinct clausole, <code>count</code> become a <code>count(distinct())</code>
	 * 
	 * @param   column to count
	 * @return  updated instance of SelectMaticO
	 */
	public abstract SelectMaticO count(String column);
	
	/**
	 * add distinct aggregate on a column name<br>
	 * if the same column is in count clausole, <code>distinct</code> become a <code>count(distinct())</code>
	 * 
	 * @param   column to distinct
	 * @return  updated instance of SelectMaticO
	 */
	public abstract SelectMaticO distinct(String column);
	
	/**
	 * add sum aggregate on a column name<br>
	 * 
	 * @param   column to sum
	 * @return  updated instance of SelectMaticO
	 */
	public abstract SelectMaticO sum(String column);
	
	/**
	 * query will be aggregate on specified column<br>
	 * 
	 * @param   column to specify in group by clausole 
	 * @return  updated instance of SelectMaticO
	 */
	public abstract SelectMaticO groupBy(String column);
	
	/**
	 * query will be order on specified column<br>
	 * 
	 * @param   column to use in ordering 
	 * @param   asc if <code>false</code>, order will be descendant
	 * @return  updated instance of SelectMaticO
	 */
	public abstract SelectMaticO orderBy(String column, boolean asc);
	
	/**
	 * query will be order on specified column in ascendent order.
	 * <br>This method call {@link #orderBy(String, boolean)} with asc=true;
	 * 
	 * @param   column to use in ordering 
	 * @return  updated instance of SelectMaticO
	 */
	public SelectMaticO orderBy(String column) {
		orderBy(column, true);
		return this;
	}
	
	/**
	 * 
	 * @return la lista dei campi da mettere nella select
	 */
	protected abstract String selectBuild();
	
	/**
	 * 
	 * @return la lista dei campi da mettere nella from
	 */
	protected abstract String   fromBuild();
	
	/**
	 * 
	 * @return la lista dei campi da mettere nella where
	 */
	protected abstract String  whereBuild();
	
	/**
	 * 
	 * @return la lista dei campi da mettere nella groupBy
	 */
	protected abstract String groupByBuild();
	
	/**
	 * 
	 * @return la lista dei campi da mettere nella orderBy
	 */
	protected abstract String orderByBuild();
	
	@Override
	public abstract SelectMaticO copy();
}
