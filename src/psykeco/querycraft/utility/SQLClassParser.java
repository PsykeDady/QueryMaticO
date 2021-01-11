package psykeco.querycraft.utility;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import sun.net.www.content.audio.x_aiff;

/**
 * classe di utilit&agrave; per leggere i parametri di una classe e quindi mapparli 
 * su tipi di SQL
 * 
 * @author psykedady
 */
public final class SQLClassParser {
	
	/**
	 * lista dei tipi di mysql
	 * @author psykedady
	 *
	 */
	public static enum Tipo{
		VARCHAR   ,
		INT       ,
		DECIMAL   ,
		CHAR      ,
		BLOB      ,
		BINARY    ,
		LONGBLOB  ,
		MEDIUMBLOB,
		TINYBLOB  ,
		VARBINARY ,
		DATE      ,
		TIME      ,
		TIMESTAMP ,
		DATETIME  ;
	}//enum
	
	//static class, private constructor
	private SQLClassParser() {}
	
	/**
	 * ritaglia la stringa che rappresenta il nome di una classe con il suo package, e 
	 * ne preleva quindi solo il nome
	 * @param s package.della.classe$subclasse
	 * @return l'ultimo nome
	 */
	public static String getTrueName(String s) {
		int li=s.lastIndexOf('.');
		int sub=s.lastIndexOf('$');
		s= (-1 < sub && sub < s.length()-1)? s.substring(sub+1) : 
				(-1<li&&li<s.length()-1) ? 
					s.substring(li+1): 
		s;
					
		return s;
	}
	
	/**
	 * Preleva il nome della classe, privato di package e altro
	 * @param c la classe 
	 * @return il nome
	 */
	public static String getTrueName(Class c) {
		return getTrueName(c.toString());
	}
	/**
	 * Preleva il nome della classe, privato di package e altro
	 * @param t il tipo
	 * @return il nome
	 */
	public static String getTrueName(Type t) {
		return getTrueName(t.toString());
	}
	
	/**
	 * Costruisce una mappa con i campi di una classe e i tipi SQL
	 * @param c
	 * @return la mappa <nome attributo,tipo sql>
	 */
	public static Map<String,String> parseClass(Class c){
		HashMap<String,String> map= new HashMap<>();
		Field[] f= c.getDeclaredFields();
		
		for ( Field x : f ) {
			String s = getTrueName(x.getType());
			map.put(x.getName(), parseType(s));
		}
		
		return map;
	}
	
	/**
	 * Costruisce una mappa "nomecampo-valore" dell'oggetto istanza 
	 * passata con i campi della classe passata ( se è un istanza di quella classe)
	 * 
	 * @param type     classe supposta
	 * @param instance istanza della classe 
	 * 
	 * @return mappa nomecampo,valore
	 */
	public static Map<String,Object> parseInstance(Class type, Object instance){
		if(!type.isInstance(instance))
			throw new IllegalArgumentException("oggetto passato di classe non supportata");

		Map<String,Object> mappa=new HashMap<>();
		
		Field[] f= type.getDeclaredFields();
		
		for ( Field x : f ) {
			boolean acc=x.isAccessible();
			x.setAccessible(true);
			Object value=null;
			try {value = x.get(instance);} catch (Exception e) {}
			mappa.put(x.getName(), value);
			x.setAccessible(acc);
		}
		
		return mappa;
		
	}
	
	/**
	 * Dato una stringa che rappresenta una classe java, restituisce un tipo SQL
	 * @param type una stringa rappresentate la classe
	 * @return il tipo sql associato
	 */
	public static String parseType(String type) {
		
		switch (type) {
			case "int"    : case "Integer":
			case "byte"   : case "Byte"   :
			case "long"   : case "Long"   :
			case "boolean": case "Boolean":
			case "short"  : case "Short"  : return Tipo.INT.name();
			
			case "float"  : case "Float"  :
			case "double" : case "Double" : return Tipo.DECIMAL.name();
			
			case "Date"   : 
			case "GregorianCalendar"      : return Tipo.DATE.name();
			
			case "File"   : return Tipo.BLOB.name();
		}
		
		return "NVARCHAR(32766)";
	}
}//ParametroTabella
