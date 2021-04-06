package psykeco.querymatico;

/**
 * TableMaticO can map a class directly into a Database Table thanks to java reflection mechanism. 
 * it needs only Class, db name and optionally primary keys list.<br>
 * <b>Class is need to have almost one empty constructor</b> <br>
 * This class build String version of query to <i>create</i>, <i>drop</i> and <i>query existance</i> of Tables, but not only!<br>
 * It can easily provide build from its self other QueryMaticO instance to 
 * <i>insert</i>, <i>select</i>, <i>update</i>, <i>delete</i> or <i>count</i> 
 * records of existent table!
 * 
 * @author PsykeDady (psdady@msn.com) */
public interface TableMaticO {
	
	/**
	 * imposta il nome del db
	 * @param db : nuovo nome del db
	 * @return istanza di TableMaticO con db aggiornato
	 */
	public TableMaticO DB(String db);
	
	/**
	 * Imposta nome tabella e parametri o colonne
	 * @param c classe da cui prelevare i dati (nome tabella, attributi)
	 * @return TableMaticO updated reference
	 */
	@SuppressWarnings("rawtypes")
	public TableMaticO table(Class c);
	
	/**
	 * imposta un nuovo valore al suffisso
	 * @param suffix : nuovo suffisso 
	 * @return TableMaticO updated reference
	 */
	public TableMaticO suffix(String suffix);
	
	/**
	 * 
	 * @param prefix : nuovo prefisso 
	 * @return l'instanza di SQLCreateTableMaticO col prefisso
	 */
	public TableMaticO prefix(String prefix);
	
	/**
	 * aggiunge (se esiste) una nuova chiave primaria 
	 * 
	 * @param key : colonna da far diventare chiave primaria. Deve essere una variabile della classe esistente
	 * @return TableMaticO updated reference
	 * 
	 * @throws IllegalArgumentException se il parametro passato non è un attributo della classe 
	 */
	public TableMaticO primary(String key);
	
	/**
	 * analizza i campi della query e quindi ne valida il contenuto, restituendo eventualmente un messaggio di errore che 
	 * esplicita quale &egrave; stato il problema riscontrato. <br><br>
	 * Se tutto va bene restituisce una stringa vuota
	 * @return Una stringa vuota se non c'&egrave; alcun problema, altrimenti un messaggio di errore
	 */
	public String validate();
	
	/**
	 * Questo metodo costruisce l'istruzione da mandare al DB per costruire la tabella.
	 * 
	 * @return l'istruzione con tutti i campi impostati
	 * 
	 * @throws IllegalArgumentException se i campi non hanno passato il controllo di validazione
	 */
	public String create();
	
	
	/**
	 * Questo metodo costruisce l'istruzione da mandare al DB per sapere se esiste (con una select) la tabella
	 * 
	 * @return l'istruzione con tutti i campi impostati
	 * 
	 * @throws IllegalArgumentException se i campi non hanno passato il controllo di validazione
	 */
	public String exists();
	
	/**
	 * Questo metodo costruisce l'istruzione da mandare al DB per eliminare la tabella
	 * 
	 * @return l'istruzione con tutti i campi impostati
	 * 
	 * @throws IllegalArgumentException se i campi non hanno passato il controllo di validazione
	 */
	public String drop();
	
	/**
	 * crea un istanza di QueryMaticO che inserisce una tupla dell'oggetto indicato
	 * 
	 * @param o un oggetto che rappresenta i campi non null da cercare ( deve essere un oggetto della stessa classe passata al metodo {@link #table(Class)}
	 * 
	 * @return un istanza di {@link QueryMaticO} che rappresenta la insert
	 */
	public QueryMaticO insertData(Object o);
	
	/**
	 * crea un istanza di QueryMaticO che effettua una select in base ai parametri 
	 * dell'oggetto passato come parametro
	 * 
	 * @param o un oggetto che rappresenta 
	 * 	i campi non null da cercare 
	 * ( deve essere un oggetto della stessa classe passata al metodo {@link #table(Class)}.
	 * Se <code>null</code>, seleziona tutte le righe 
	 * 
	 * @return un istanza di {@link SelectMaticO} che rappresenta la select
	 */
	public SelectMaticO selectData(Object o);
	
	/**
	 * crea un istanza di QueryMaticO che elimina le tuple con le caratteristiche 
	 * non null dell'oggetto indicato
	 * 
	 * @param o un oggetto che rappresenta i campi non null da cercare ( deve essere un oggetto della stessa classe passata al metodo {@link #table(Class)}
	 * 
	 * @return un istanza di {@link QueryMaticO} che rappresenta la delete
	 */
	public QueryMaticO deleteData(Object o);
	
	/**
	 * crea un istanza di QueryMaticO che aggiorna le tuple con le informazioni dell'istanza passata.
	 * Per farlo, utilizza come chiave di ricerca il campo indicato da {@link #primary}
	 * 
	 * @param o un oggetto che rappresenta i campi non null da aggiornare 
	 * ( deve essere un oggetto della stessa classe passata al metodo {@link #table(Class)}. 
	 * Il campo primario è obbligatorio
	 * 
	 * @throws IllegalArgumentException se primary non &egrave; stato specificato
	 * 
	 * @return un istanza di {@link QueryMaticO} che rappresenta la delete
	 */
	public QueryMaticO updateData(Object o);
	
	/**
	 * crea un istanza di QueryMaticO che conta le tuple con le caratteristiche 
	 * non null dell'oggetto indicato
	 * 
	 * @param o un oggetto che rappresenta i campi non null da cercare (
	 *  deve essere un oggetto della stessa classe passata al metodo 
	 *  {@link #table(Class)}
	 * 
	 * @return un istanza di {@link QueryMaticO} che rappresenta la select count
	 */
	public SelectMaticO countData(Object o);
	
	/**
	 * copia tutti i campi del TableMaticO e ne restituisce una nuova istanza 
	 * 
	 * @return nuova istanza copia del builder
	 */
	public TableMaticO copy();

}
