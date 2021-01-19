package psykeco.querycraft;


/**
 * DBCraft genera una serie di query che riguardano il database
 * 
 * @author psykedady
 * */
public interface DBCraft {
	/** Regex per validare i singoli componenti delle query */
	public static String BASE_REGEX=QueryCraft.BASE_REGEX;
	
	/**
	 * imposta il nome del db
	 * @param db : nuovo nome del db
	 * @return istanza di DBCraft con db aggiornato
	 */
	public DBCraft DB(String db);
	
	/**
	 * analizza i campi della query e quindi ne valida il contenuto, restituendo eventualmente un messaggio di errore che 
	 * esplicita quale &egrave; stato il problema riscontrato. <br><br>
	 * Se tutto va bene restituisce una stringa vuota
	 * @return Una stringa vuota se non c'&egrave; alcun problema, altrimenti un messaggio di errore
	 */
	public String validate();
	
	/**
	 * Questo metodo costruisce l'istruzione da mandare al DB per creare il db.
	 * 
	 * @return l'istruzione con tutti i campi impostati
	 * 
	 * @throws IllegalArgumentException se i campi non hanno passato il controllo di validazione
	 */
	public String create();
	
	
	/**
	 * Questo metodo costruisce l'istruzione da mandare al DB per sapere se esiste (con una select) il db
	 * 
	 * @return l'istruzione con tutti i campi impostati
	 * 
	 * @throws IllegalArgumentException se i campi non hanno passato il controllo di validazione
	 */
	public String exists();
	
	/**
	 * Questo metodo costruisce l'istruzione da mandare al DB per eliminare il db
	 * 
	 * @return l'istruzione con tutti i campi impostati
	 * 
	 * @throws IllegalArgumentException se i campi non hanno passato il controllo di validazione
	 */
	public String drop();
	
	/**
	 * Questo metodo costruisce l'istruzione da mandare al DB per prelevare una lista di tabelle
	 * 
	 * @return l'istruzione con i campi impostati
 	 * 
	 * @throws IllegalArgumentException se i campi non hanno passato il controllo di validazione
	 */
	public String listTables();
	
	

}
