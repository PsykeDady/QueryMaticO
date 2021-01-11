package psykeco.querycraft;

public interface TableCraft {
	
	/** Regex per validare i singoli componenti delle query */
	public static String BASE_REGEX=QueryCraft.BASE_REGEX;
	
	/**
	 * imposta il nome del db
	 * @param db : nuovo nome del db
	 * @return istanza di TableCraft con db aggiornato
	 */
	public TableCraft DB(String db);
	
	/**
	 * Imposta nome tabella e parametri o colonne
	 * @param c classe da cui prelevare i dati (nome tabella, attributi)
	 * @return istanza di TableCraft con i campi aggioranti
	 */
	@SuppressWarnings("rawtypes")
	public TableCraft table(Class c);
	
	/**
	 * imposta un nuovo valore al suffisso
	 * @param suffix : nuovo suffisso 
	 * @return l'istanza di SQLCreateTableCraft col suffisso
	 */
	public TableCraft suffix(String suffix);
	
	/**
	 * 
	 * @param prefix : nuovo prefisso 
	 * @return l'instanza di SQLCreateTableCraft col prefisso
	 */
	public TableCraft prefix(String prefix);
	
	/**
	 * aggiunge (se esiste) una nuova chiave primaria 
	 * 
	 * @param key : colonna da far diventare chiave primaria. Deve essere una variabile della classe esistente
	 * @return l'istanza di SQLCreateTableCraft con la chiave primaria aggiunta
	 * 
	 * @throws IllegalArgumentException se il parametro passato non è un attributo della classe 
	 */
	public TableCraft primary(String key);
	
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
	public String select();
	
	/**
	 * Questo metodo costruisce l'istruzione da mandare al DB per eliminare la tabella
	 * 
	 * @return l'istruzione con tutti i campi impostati
	 * 
	 * @throws IllegalArgumentException se i campi non hanno passato il controllo di validazione
	 */
	public String drop();
	
	/**
	 * crea un istanza di QueryCraft che inserisce una tupla dell'oggetto indicato
	 * 
	 * @param o un oggetto che rappresenta i campi non null da cercare ( deve essere un oggetto della stessa classe passata al metodo {@link #table(Class)}
	 * 
	 * @return un istanza di {@link QueryCraft} che rappresenta la insert
	 */
	public QueryCraft insertData(Object o);
	
	/**
	 * crea un istanza di QueryCraft che effettua una select in base ai parametri non null
	 * dell'oggetto passato come parametro
	 * 
	 * @param o un oggetto che rappresenta i campi non null da cercare ( deve essere un oggetto della stessa classe passata al metodo {@link #table(Class)}
	 * 
	 * @return un istanza di {@link SelectCraft} che rappresenta la select
	 */
	public SelectCraft selectData(Object o);
	
	/**
	 * crea un istanza di QueryCraft che elimina le tuple con le caratteristiche 
	 * non null dell'oggetto indicato
	 * 
	 * @param o un oggetto che rappresenta i campi non null da cercare ( deve essere un oggetto della stessa classe passata al metodo {@link #table(Class)}
	 * 
	 * @return un istanza di {@link QueryCraft} che rappresenta la delete
	 */
	public QueryCraft deleteData(Object o);
	
	

}
