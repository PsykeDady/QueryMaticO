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
 * utility class to map java classes and fields into MySQL type 
 * 
 * @author PsykeDady (psdady@msn.com) 
 */
public final class SQLClassParser {
	
	/**
	 * MySQL types enumeration
	 * 
	 * 
	 * @author PsykeDady (psdady@msn.com) 
	 */
	public static enum MySqlType{
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
		
		/** precision of numerical types */
		public static String precision() {
			return "(10,2)";
		}
		
		/** number of character primary text type */
		public static String NVARCHAR_PRIMARY() {
			return "NVARCHAR(676)";
		}
	}//enum
	
	/** static class, private constructor */ 
	private SQLClassParser() {}
	
	/**
	 * <p>take name of class from complete java-path of class. </p>
	 * <p>For example if string is: <br></p>
	 * <p><code>path.to.the.package.nameclass</code></p><br>
	 * <p>this function return <code>nameclass</code><br></p>
	 * 
	 * <p><br>it work also with subclass paths, with <code>$</code> sign</p>
	 * 
	 * @param s path.to.the.package.nameclass$subclass
	 * @return name of class
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
	 * <p>take name of class from complete java-path of class. </p>
	 * <p>For example if class is: <br></p>
	 * <p><code>path.to.the.package.nameclass</code></p><br>
	 * <p>this function return <code>nameclass</code><br></p>
	 * 
	 * <p><br>it work also with subclass paths, with <code>$</code> sign</p>
	 * 
	 * @param c the class
	 * @return name of class
	 */
	public static String getTrueName(@SuppressWarnings("rawtypes") Class c) {
		return getTrueName(c.toString());
	}

	/**
	 * <p>take name of type from complete java-path of type. </p>
	 * <p>For example if type is: <br></p>
	 * <p><code>path.to.the.package.nametype</code></p><br>
	 * <p>this function return <code>nametype</code><br></p>
	 * 
	 * <p><br>it work also with subtype paths, with <code>$</code> sign</p>
	 * 
	 * @param c the type
	 * @return name of class
	 */
	public static String getTrueName(Type t) {
		return getTrueName(t.toString());
	}
	
	/**
	 * <p>Build a map using field and type of input class<br></p>
	 * 
	 * <p>Map contains:<br></p>
	 * <ul>
	 *  <li>key : field name</li>
	 *  <li>value : field type</li>
	 * </ul>
	 * <p><br></p>
	 * <p><br></p>
	 * @param c class
	 * @return Map of &lt; field name, field type &gt;
	 */
	public static Map<String,String> parseClass(@SuppressWarnings("rawtypes") Class c){
		Map<String,String> map= new HashMap<>();
		Field[] f= c.getDeclaredFields();
		
		for ( Field x : f ) {
			if(x.getName().contains("this$")) continue;
			String s = getTrueName(x.getType());
			map.put(x.getName(), s);
		}
		
		return map;
	}
	
	/**
	 * 
	 * * <p>Build a map using class and its istance in input contains every fields of class with values<br></p>
	 * 
	 * <p>Map contains:<br></p>
	 * <ul>
	 *  <li>key : field name</li>
	 *  <li>value : instance value</li>
	 * </ul>
	 * <p><br></p>
	 * <p><br></p>
	 * @param type : a type
	 * @param instance : an instance of type
	 * @return Map of &lt; field name, field value &gt; of instance
	 */
	public static Map<String,Object> parseInstance(@SuppressWarnings("rawtypes") Class type, Object instance){
		if(!type.isInstance(instance))
			throw new IllegalArgumentException("oggetto passato di classe non supportata");

		Map<String,Object> mappa=new HashMap<>();
		
		Field[] f= type.getDeclaredFields();
		
		for ( Field x : f ) {
			boolean acc=x.isAccessible();
			if(x.getName().contains("this$")) continue;
			x.setAccessible(true);
			Object value=null;
			try {value = x.get(instance);} catch (Exception e) {}
			mappa.put(x.getName(), value);
			x.setAccessible(acc);
		}
		
		return mappa;
		
	}
	
	/**
	 * <p>return a sql type from {@link MySqlType} toString using an input String contains java class name. <br></p>
	 * <p>if primary, can be different (a primary String will be convert nvarchar, otherwise Text) <br></p>
	 * @param type name of class
	 * @param primary true if column will be primary
	 * @return toString of Mysqltype from {@link MySqlType}
	 */
	public static String parseType(String type, boolean primary) {
		
		switch (type) {
			case "int"    : case "Integer":
			case "byte"   : case "Byte"   :
			case "long"   : case "Long"   :
			case "boolean": case "Boolean":
			case "short"  : case "Short"  : return MySqlType.INT.name();
			
			case "float"  : case "Float"  :
			case "double" : case "Double" : return MySqlType.DECIMAL.name()+MySqlType.precision();
			
			case "Date"   : 
			case "GregorianCalendar"      : 
			case "LocalDateTime"          : return MySqlType.TIMESTAMP.name()+" null ";
			
			case "File"                   :  return MySqlType.LONGBLOB.name();
		}
		
		return primary ? MySqlType.NVARCHAR_PRIMARY() : MySqlType.TEXT.name();
	}
	
	/**
	 * return default <code>null</code> value for specific conversion from java class to mysql value
	 * @param c a java class 
	 * @return default <code>null</code> value 
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

	/**
	 * <p>Create an instance of a specific type parsing a resultset after a query.<br></p>
	 * <p>Through name of input field, search specific column with same name, try to 
	 * automatically parse with getObject method from  ResultSet the column and in some cases 
	 * manage that object to return specific class<br></p>
	 * <p> <br></p>
	 * <p>It can launch some exception if something goes wrong <br></p>
	 * <p> <br></p>
	 * 
	 * @param rs the result set
	 * @param x the field of class, with same name of wanted column
	 * @param columns set of column we expect
	 * @return a parsed object with Type of x and value from ResultSet
	 * @throws SQLException
	 * @throws IOException
	 */
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
	
	/**
	 * <p>check if a string not match with database requirement as name of db, table or column.</br></p>
	 * <p>it must: <br></p>
	 * <ul>
	 * 		<li>start with letter</li>
	 * 		<li>not contain unsupported character (from ASCII 0 to 32 and not 127)</li>
	 * 		<li>if contain a <code>`</code>, it will be duplicated</li>
	 * </ul>
	 * <p> <br></p>
	 * @param base the string will be checked as name of db, table or column
	 * @return <code>null</code> if not valid, base string with eventually <code>`</code> duplicated if valid
	 */
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
	
	/**
	 * <p>check if a string not match with database requirement as varchar string.</br></p>
	 * <p>it must: <br></p>
	 * <ul>
	 * 		<li>not contain unsupported character (from ASCII 0 to 32 and not 127, ASCII 9 and 10 excluded)</li>
	 * 		<li>if contain a <code>'</code>, it will be duplicated</li>
	 * </ul>
	 * <p> <br></p>
	 * @param value the string will be checked as varchar string
	 * @return <code>null</code> if not valid, value string with eventually <code>'</code> duplicated if valid
	 */
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
	 * @return string: <code>LOAD_FILE(absolutepath of tmp file)</code>
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
	 * <p>if input Object is istance of {@link java.util.Date Date}, {@link java.util.GregorianCalendar GregorianCalendar} or {@link java.time.LocalDateTime LocalDateTime} , it will be parsed into String formatted as yyyy-mm-ddThh:mm:ss</br></p>
	 * 
	 * 
	 * @param o the date as {@link java.util.Date Date}, {@link java.util.GregorianCalendar GregorianCalendar} or {@link java.time.LocalDateTime LocalDateTime}
	 * @return the representation in String of date, <code>null</code> if the class can't be converted
	 */
	public static String DateParsing(Object o) {
		Class<?> type=o.getClass();
		
		LocalDateTime l=null;
		
		if(type.equals(Date.class)) {
			Date d=(Date)o;
			
			l=LocalDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault());
		} else if (type.equals(GregorianCalendar.class)) {
			GregorianCalendar gc=(GregorianCalendar)o;
			
			l=gc.toZonedDateTime().toLocalDateTime();
		} else if (type.equals(LocalDateTime.class)){
			l=(LocalDateTime)o;
		} else {
			return null;
		}
		
		String s=String.format(
			"%04d-%02d-%02dT%02d:%02d:%02d", 
			l.getYear(),l.getMonthValue(),l.getDayOfMonth(),
			l.getHour(),l.getMinute(),l.getSecond()
		);
		return s;
	}
	
	/**
	 * <p>return a String representation of object, not always coincide with the <code>toString()</code> operation.</br></p>
	 * <p>In example, String will be enclosed into single quote char (<code>'</code>)</br></p>
	 * 
	 * @param o  object
	 * @return String representation to be send into mysql statement
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
