package psykeco.querymatico.translations;

import java.util.HashMap;
import java.util.Locale;

import psykeco.querymatico.translations.dict.EnDictionary;
import psykeco.querymatico.translations.dict.ItDictionary;

/**
 * <p>This class is intended to automatic translate Error Message of Framework</br></p>
 * <p>To defualt, messages are in english, to initialize them use {@link #init(LANGUAGE)} where avaible {@link LANGUAGE} are:</br></p>
 * <ul>
 * 		<li>{@link LANGUAGE #IT IT} : ITALIAN</li>
 * 		<li>{@link LANGUAGE #EN EN} : ENGLISH</li>
 * </ul>
 * <p> Every dictionary has a different translation for every {@link KEY_MSG} value and every value has a number of "substituions" string to use in any {@link String #format(String, Object...)} methods to personalize
 * the content of message. </br></p>
 * <p>To write a message with translations in the code, use method {@link #getMsg(KEY_MSG, String...)} passing the right key of message and the substitutions Strings</br></p>
 * <p> </br></p>
 * 
 * @author PsykeDady (psdady@msn.com)
 */
public final class Translations {
	
	/** private constructor. not for instantiation use */
	private Translations() {}
	
	/**
	 * <p>List of possible Messages of framework.</br></p>
	 * <p>Every message has a number of possible parameter to use to customize the message.</br></p>
	 * <p>Every parameter will be passed to a {@link String #format(String, Object...)} </br></p> 
	 * 
	 * @author PsykeDady (psdady@msn.com)
	 */
	public static enum KEY_MSG{
		/** ERR: not valid port (out of range). 0 parameters */
		PORT_NOT_VALID,
		/** ERR: URL can't be null. 0 parameters */
		URL_NULL,
		/** ERR: USER NAME can't be null. 0 parameters */
		USER_NULL,
		/** ERR: PSK  can't be null. 0 parameters */
		PSK_NULL,
		/** ERR: Database can't be null. 0 parameters */
		DB_NULL,
		/** ERR: Database name is not valid. 1 parameter */
		DB_NOT_VALID(1),
		/** ERR: Table can't be null. 0 parameters */
		TABLE_NULL,
		/** ERR: name of table not valid. 1 parameters*/
		TABLE_NOT_VALID(1),
		/** ERR: name of column can't be null or empty. 0 parameters */
		COLUMN_EMPTY,
		/** ERR: this value can't be null or empty. 1 parameter */
		VALUE_EMPTY(1),
		/** ERR: name of column is not valid. 1 parameter */
		COLUMN_NOT_VALID(1),
		/** ERR: this value is not valid. 1 parameter */
		VALUE_NOT_VALID(1),
		/** ERR: alias name not valid. 1 parameter */
		ALIAS_NOT_VALID(1),
		/** ERR: aggregate name not valid. 2 parameter */
		AGGREGATE_NOT_VALID(2),
		/** ERR: aggregate name not valid. 1 parameter */
		AGGREGATE_NOT_NULL(1),
		/** ERR: this Entry is empty. 0 parameters */
		ENTRY_EMPTY,
		/** ERR: this not support this. 2 parameters */
		NOT_SUPPORT(2),
		/** ERR: class not has parameters to mapping. 0 parameters */
		CLASS_PARAMETERS,
		/** ERR: this prefix value is not valid. 1 parameter */
		PREFIX_NOT_VALID(1),
		/** ERR: this suffix value is not valid. 1 parameter */
		SUFFIX_NOT_VALID(1),
		/** ERR: primary value can't be null. 0 parameters */
		PRIMARY_NOT_NULL,
		/** ERR: not support entry method. 2 parameters */
		NOT_SUPPORT_METHOD(2),
		/** ERR: join class wrong. 1 parameter */
		WRONG_CLASS_JOIN(1),
		/** ERR: primary key refer not to a column. 0 parameters */
		PRIMARY_KEY_MUST_REFERE,
		/** ERR: wrong object type. 0 parameters */
		WRONG_OBJECT_TYPE,
		/** ERR: Connection closed. 0 parameters */
		CONNECTION_CLOSED,
		/** ERR: not empty constructor. 0 parameters */
		NOT_EMPTY_CONSTRUCTOR,
		/** ERR: not empty or accessible constructor. 0 parameters */
		NOT_EMPTY_ACCESSIBLE_CONSTRUCTOR,
		/** ERR: error calling constructor. 0 parameters */
		CONSTRUCTOR_ERROR,
		/** ERR : Connection template is not avaible. 1 parameters */
		CONNECTION_MATICO_NOT_AVAIBLE(1)
		;
		
		/** number of expected substitution to apply to String format */
		private int substitutions;
		
		/** Empty constructor set number of substitutios to 0 as default */
		private KEY_MSG() {substitutions=0;};
		
		/**
		 * Set a different number of substitutios ( 0 is default)
		 * @param sub : numbers of substitutios
		 */
		private KEY_MSG(int sub) {
			substitutions=sub;
		}

		/** get numbers of substitutios */
		public int getSubstituions() {return substitutions;}
	}
	
	/**
	 * map of current translations
	 */
	private static HashMap<KEY_MSG,String> trans;
	
	/**
	 * Initialize the map ( {@link #trans} ) of translations with default Language of System
	 */
	public static void init () {
		Locale language=Locale.getDefault();

		init(language);
	}

	/**
	 * Initialize the map ( {@link #trans} ) 
	 * @param language the selected language
	 */
	public static void init (Locale language) {

		if (language.equals(Locale.ITALIAN)){
			trans=ItDictionary.getDict();
		} else {
			trans=EnDictionary.getDict();
		}
	}
	
	/**
	 * get an application message in current translation mode.
	 * 
	 * @param k the message key you want to get 
	 * @param strings list of substitions
	 * @return The message in current translation
	 */
	public static String getMsg(KEY_MSG k, String ...strings ) {
		if(trans==null) init();
		return trans.get(k);
	}

}
