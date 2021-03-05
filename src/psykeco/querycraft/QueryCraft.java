package psykeco.querycraft;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map.Entry;

/**
 * Interfaccia di generatori (builder) di istruzioni per query 
 * 
 * 
 * @author archdady
 */
public interface QueryCraft {
	
	/** indica il nome del DB alla query
	 *  @param il nome del DB 
	 *  @return l'istanza di QueryCraft a cui è aggiunto il nome del DB 
	 *  */
	public QueryCraft DB(String DB);
	
	/** indica il nome della tabella alla query
	 *  @param il nome della tabella
	 *  @return l'istanza di QueryCraft a cui è aggiunto il nome della tabella
	 *  */
	public QueryCraft table(String table);
	
	/** aggiunge una coppia "nome colonna"-"valore" ai campi "insert", "select", "update" oppure create
	 *  @param  Una coppia scritta con classe {@link java.util.Map.Entry Entry}
	 *  @return l'istanza di QueryCraft a cui è aggiunta la coppia
	 *  */
	public QueryCraft entry(Entry<String,Object> kv);
	
	/** aggiunge una coppia "nome colonna"-"valore" ai campi "insert", "select", "update" oppure create
	 *  @param  colonna
	 *  @param  valore
	 *  @return l'istanza di QueryCraft a cui è aggiunta la coppia
	 *  */
	public QueryCraft entry(String colonna, Object valore);
	
	/** aggiunge una coppia "nome colonna"-"valore" che serve a filtrare la query 
	 * ( nelle where ) 
	 * 
	 * @param  Una coppia scritta con classe {@link java.util.Map.Entry Entry}
	 * @return l'istanza di QueryCraft a cui è aggiunto il fitro
	 *  */
	public QueryCraft filter(Entry<String,Object> filter);
	
	/** aggiunge una coppia "nome colonna"-"valore" che serve a filtrare la query
	 * ( nelle where ) 
	 * 
	 *  @param  colonna
	 *  @param  valore
	 *  @return l'istanza di QueryCraft a cui è aggiunta la coppia
	 *  */
	public QueryCraft filter(String colonna, Object valore);
	
	/** necessaria per validare la query prima che sia stato fatto il build.<br>
	 * Nel pi&ugrave; generico dei casi &egrave; necessario che siano definiti, 
	 * non nulli e validati dalla regex i parametri:<br>
	 * <ul>
	 * 	<li>nome db</li>
	 * 	<li>nome tabella</li>
	 * 	<li>elenco parametri ( ne deve esistere almeno uno. Eccetto alcuni contesti)</li>
	 * </ul>
	 *  
	 *  @return una stringa vuota se la query può essere craftata
	 *  */
	public String validate();
	
	/**
	 * costruisce la query e la manda sotto forma di stringa
	 * @return La query, sotto forma di stringa. <code>null</code> se la {@link #validate() validazione}
	 * fallisce
	 * */
	public String craft();
	
	/**
	 * copia tutti i campi del QueryCraft e ne restituisce una nuova istanza 
	 * 
	 * @return nuova istanza copia del craft
	 */
	public QueryCraft copy();
}
