package psykeco.querycraft;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.Map.Entry;

import psykeco.querycraft.utility.SQLClassParser;

/**
 * Interfaccia di generatori (builder) di istruzioni per query 
 * 
 * 
 * @author archdady
 */
public interface QueryCraft {
	
	public static String validateBase(String base) {
		if(base==null) return null;
		StringBuilder sb=new StringBuilder(base);
		final char dupl='`';
		int i=0, end = sb.length(); 
		if((sb.charAt(i)<'A'||'Z'<sb.charAt(i)) &&  (sb.charAt(i)<'a' || 'z' < sb.charAt(i)))
			return null; //start with a letters
		for (;i<end;i++) {
			char cur=sb.charAt(i);
			if (cur<32 || cur ==127) return null;
			if(cur==dupl) {
				i++;
				if((i==end)|| sb.charAt(i)!=dupl ) {
					sb.insert(i-1,dupl); 
					end++; 
				} 
			}
				
		}
		return sb.toString();
	}
	
	public static String validateValue(String value) {
		if(value.length()>60) return null;
		StringBuilder sb=new StringBuilder(value);
		final char dupl='\'';
		int i=0, end = sb.length(); 
		for (;i<end;i++) {
			char cur=sb.charAt(i);
			if ((cur<32 && cur!=9 && cur!=10 ) || cur ==127) return null;
			
			if(cur==dupl) {
				i++;
				if((i==end)|| sb.charAt(i)!=dupl ) {
					sb.insert(i-1,dupl); 
					end++; 
				} 
			}
		}
		return sb.toString();
	}
	
	/**
	 * create a temporary file to call native LOAD_FILE from db
	 * @param f : origin file
	 * @return LOAD_FILE(absolutepath of tmp file)
	 */
	public static String FileParsing(File f) {
		File tmp=null;
		try {
			tmp=File.createTempFile("tmp", "tmp");
		}catch(Exception e) {
			return null;
		}
		try (
			FileInputStream  fis =new FileInputStream (f);
			FileOutputStream fos =new FileOutputStream(tmp);
 		){
			while(fis.available()>0) {	
				fos.write(fis.read());
			}
		} catch(Exception e) {
			return null;
		}
		tmp.deleteOnExit();
		
		return "LOAD_FILE('"+tmp.getAbsoluteFile()+"')";
	}
	
	/**
	 * ritorna una versione "stringa" dell'oggetto da usare nelle query.
	 * Ad esempio, le stringhe vanno racchiuse tra singoli apici
	 * @param o : l'oggetto
	 * @return mysql string dell'oggetto
	 */
	public static String str(Object o) {
		if ( 
			o instanceof Integer || o instanceof Double || o instanceof Float ||
			o instanceof Short   || o instanceof Long   || o instanceof Byte 
		) return o.toString();
		
		if (o instanceof Boolean) return (Boolean)o ? "1":"0";
		
		if(o instanceof File) return FileParsing((File)o);
		//TODO gestire le date
		
		return "'"+validateValue(o.toString())+"'";
	}
	
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
