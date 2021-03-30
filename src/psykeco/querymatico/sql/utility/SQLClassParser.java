package psykeco.querymatico.sql.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
		TEXT	  ,
		TIME      ,
		TIMESTAMP ,
		DATETIME  ;
		
		public static String precision() {
			return "(10,2)";
		}
		
		public static String NVARCHAR_PRIMARY() {
			return "NVARCHAR(676)";
		}
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
	public static String getTrueName(@SuppressWarnings("rawtypes") Class c) {
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
	 * La mappa contiene come:
	 * <ul>
	 *  <li>chiave : nome campo</li>
	 *  <li>valore : tipo campo</li>
	 * </ul>
	 * @param c
	 * @return la mappa <nome attributo,tipo sql>
	 */
	public static Map<String,String> parseClass(@SuppressWarnings("rawtypes") Class c){
		Map<String,String> map= new HashMap<>();
		Field[] f= c.getDeclaredFields();
		
		for ( Field x : f ) {
			String s = getTrueName(x.getType());
			map.put(x.getName(), s);
		}
		
		return map;
	}
	
	/**
	 * Costruisce una mappa "nome del campo-valore" dell'oggetto istanza 
	 * passata con i campi della classe passata ( se è un istanza di quella classe)
	 * 
	 * @param type     classe supposta
	 * @param instance istanza della classe 
	 * 
	 * @return mappa nomecampo,valore
	 */
	public static Map<String,Object> parseInstance(@SuppressWarnings("rawtypes") Class type, Object instance){
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
	public static String parseType(String type, boolean primary) {
		
		switch (type) {
			case "int"    : case "Integer":
			case "byte"   : case "Byte"   :
			case "long"   : case "Long"   :
			case "boolean": case "Boolean":
			case "short"  : case "Short"  : return Tipo.INT.name();
			
			case "float"  : case "Float"  :
			case "double" : case "Double" : return Tipo.DECIMAL.name()+Tipo.precision();
			
			case "Date"   : 
			case "GregorianCalendar"      : 
			case "LocalDateTime"          : return Tipo.TIMESTAMP.name()+" null ";
			
			case "File"                   :  return Tipo.LONGBLOB.name();
		}
		
		return primary ? Tipo.NVARCHAR_PRIMARY() : Tipo.TEXT.name();
	}
	
	/**
	 * Dato una stringa che rappresenta una classe java, restituisce un tipo SQL
	 * @param type una stringa rappresentate la classe
	 * @return il tipo sql associato
	 */
	public static Object nullValue(Class<?> c) {
		String type=getTrueName(c);
		
		switch (type) {
			case "byte"   : case "long"   : 
			case "short"  : case "int"    : return 0;
			case "boolean": return false;
			
			case "float"  : case "double" : return 0f;
			
			case "char"   : return '\0';
			
		}
		return null;
		
	}

	public static Object parseResultToField(ResultSet rs, Field x,Set<String> columns) throws SQLException, IOException {
		Object inst=null;
		boolean f=rs.wasNull();
		if(columns.contains(x.getName()) && !f ){
			inst=rs.getObject(x.getName());
		}
		if(inst==null) return SQLClassParser.nullValue(x.getType());
		
		if(x.getType().equals(File.class)) {
			inst=rs.getBinaryStream(x.getName());
			File file=File.createTempFile("result", "query");
			file.deleteOnExit();
			try(
				InputStream is=(InputStream) inst;
				FileOutputStream fos=new FileOutputStream(file);
			){
				int data=is.read();
				while(data!=-1) {
					fos.write(data);
					data=is.read();
				}
				inst=file;
			} catch (Exception e) {inst=null;}
		} else if(x.getType().equals(Date.class) || 
				  x.getType().equals(LocalDateTime.class) || 
				  x.getType().equals(GregorianCalendar.class)   ) {
			
			Timestamp t=(Timestamp) inst;
			
			if(x.getType().equals(Date.class)) {
				inst=Date.from(t.toInstant());
			}
			else if(x.getType().equals(LocalDateTime.class)) {
				inst=LocalDateTime.ofInstant(t.toInstant(),ZoneId.systemDefault());
			}
			else if(x.getType().equals(GregorianCalendar.class)) {
				inst=GregorianCalendar.from(t.toLocalDateTime().atZone(ZoneId.systemDefault()));
			}
		} else {
			inst=rs.getObject(x.getName(), x.getType());
		}
		
		return inst;
	}
	
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
	
	public static String DateParsing(Object o) {
		Class<?> type=o.getClass();
		
		LocalDateTime l=null;
		
		if(type.equals(Date.class)) {
			Date d=(Date)o;
			
			l=LocalDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault());
		} else if (type.equals(GregorianCalendar.class)) {
			GregorianCalendar gc=(GregorianCalendar)o;
			
			l=gc.toZonedDateTime().toLocalDateTime();
		} else {
			l=(LocalDateTime)o;
		}
		
		String s=String.format(
			"%04d-%02d-%02dT%02d:%02d:%02d", 
			l.getYear(),l.getMonthValue(),l.getDayOfMonth(),
			l.getHour(),l.getMinute(),l.getSecond()
		);
		return s;
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
		
		if( o instanceof Date || 
			o instanceof GregorianCalendar ||
			o instanceof LocalDateTime )
			return "'"+DateParsing(o)+"'";
		
		return "'"+validateValue(o.toString())+"'";
	}
}
