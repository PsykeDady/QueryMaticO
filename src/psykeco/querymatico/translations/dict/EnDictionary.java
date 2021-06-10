package psykeco.querymatico.translations.dict;

import static psykeco.querymatico.translations.Translations.KEY_MSG.AGGREGATE_NOT_NULL;
import static psykeco.querymatico.translations.Translations.KEY_MSG.AGGREGATE_NOT_VALID;
import static psykeco.querymatico.translations.Translations.KEY_MSG.ALIAS_NOT_VALID;
import static psykeco.querymatico.translations.Translations.KEY_MSG.CLASS_PARAMETERS;
import static psykeco.querymatico.translations.Translations.KEY_MSG.COLUMN_EMPTY;
import static psykeco.querymatico.translations.Translations.KEY_MSG.COLUMN_NOT_VALID;
import static psykeco.querymatico.translations.Translations.KEY_MSG.CONNECTION_MATICO_NOT_AVAIBLE;
import static psykeco.querymatico.translations.Translations.KEY_MSG.CONSTRUCTOR_ERROR;
import static psykeco.querymatico.translations.Translations.KEY_MSG.DB_NOT_VALID;
import static psykeco.querymatico.translations.Translations.KEY_MSG.DB_NULL;
import static psykeco.querymatico.translations.Translations.KEY_MSG.ENTRY_EMPTY;
import static psykeco.querymatico.translations.Translations.KEY_MSG.NOT_EMPTY_ACCESSIBLE_CONSTRUCTOR;
import static psykeco.querymatico.translations.Translations.KEY_MSG.NOT_EMPTY_CONSTRUCTOR;
import static psykeco.querymatico.translations.Translations.KEY_MSG.NOT_SUPPORT_METHOD;
import static psykeco.querymatico.translations.Translations.KEY_MSG.PORT_NOT_VALID;
import static psykeco.querymatico.translations.Translations.KEY_MSG.PREFIX_NOT_VALID;
import static psykeco.querymatico.translations.Translations.KEY_MSG.PRIMARY_KEY_MUST_REFERE;
import static psykeco.querymatico.translations.Translations.KEY_MSG.PRIMARY_NOT_NULL;
import static psykeco.querymatico.translations.Translations.KEY_MSG.PSK_NULL;
import static psykeco.querymatico.translations.Translations.KEY_MSG.SUFFIX_NOT_VALID;
import static psykeco.querymatico.translations.Translations.KEY_MSG.TABLE_NOT_VALID;
import static psykeco.querymatico.translations.Translations.KEY_MSG.TABLE_NULL;
import static psykeco.querymatico.translations.Translations.KEY_MSG.URL_NULL;
import static psykeco.querymatico.translations.Translations.KEY_MSG.USER_NULL;
import static psykeco.querymatico.translations.Translations.KEY_MSG.VALUE_EMPTY;
import static psykeco.querymatico.translations.Translations.KEY_MSG.VALUE_NOT_VALID;
import static psykeco.querymatico.translations.Translations.KEY_MSG.WRONG_CLASS_JOIN;
import static psykeco.querymatico.translations.Translations.KEY_MSG.WRONG_OBJECT_TYPE;
import static psykeco.querymatico.translations.Translations.KEY_MSG.WRONG_TRANSLATIONS_PARAMETER;

import java.util.HashMap;

import psykeco.querymatico.translations.Translations.KEY_MSG;

/**
 * English implementation of a dictionary for {@link psykeco.querymatico.translations.Translations ErrMsg} class
 * 
 * @author PsykeDady (psdady@msn.com)
 */
public class EnDictionary {
	
	/**
	 * @return English translations as {@link HashMap}
	 */
	public static HashMap<KEY_MSG, String> getDict(){
		HashMap<KEY_MSG, String> dict=new HashMap<>();
		
		dict.put(PORT_NOT_VALID, "wrong port value");
		dict.put(URL_NULL, "empty url");
		dict.put(USER_NULL, "invalid user value (null)");
		dict.put(PSK_NULL, "invalid psk value (null)");
		dict.put(DB_NULL, "null or empty db name value");
		dict.put(DB_NOT_VALID, "invalid db name value: %s");
		dict.put(TABLE_NULL, "null or empty table name");
		dict.put(TABLE_NOT_VALID, "invalid db name value: %s");
		dict.put(COLUMN_EMPTY, "Empty column name");
		dict.put(VALUE_EMPTY, "Empty value in column %s");
		dict.put(COLUMN_NOT_VALID, "invalid column name: %s");
		dict.put(VALUE_NOT_VALID, "invalid value: %s");
		dict.put(ALIAS_NOT_VALID, "invalid alias name %s");
		dict.put(AGGREGATE_NOT_VALID, "aggregate %s has invalid column name value '%s'");
		dict.put(AGGREGATE_NOT_NULL, "column %s can't be null cause of aggregate");
		dict.put(ENTRY_EMPTY, "Empty entry list. Almost one column-value pair needed");
		dict.put(CLASS_PARAMETERS, "Without any params, this class can't be parsed into entity");
		dict.put(PREFIX_NOT_VALID, "invalid prefix %s");
		dict.put(SUFFIX_NOT_VALID, "invalid suffix %s");
		dict.put(PRIMARY_NOT_NULL, "Elements corresponding primary key must be not null");
		dict.put(NOT_SUPPORT_METHOD, "%s class not support method %s()");
		dict.put(WRONG_CLASS_JOIN, "Join table must be of %s class");
		dict.put(PRIMARY_KEY_MUST_REFERE, "Primary key must referes to existing column");
		dict.put(WRONG_OBJECT_TYPE,"Incompatible object type. Expected %s but received %s");
		dict.put(NOT_EMPTY_CONSTRUCTOR,"Unaccessible constructor. Please provide a public empty constructor of class to mapping");
		dict.put(NOT_EMPTY_ACCESSIBLE_CONSTRUCTOR,"Unaccessible constructor, interface or abstract class. Please provide a class with public empty constructor for mapping");
		dict.put(CONSTRUCTOR_ERROR,"Error calling class constructor. Please be sure to provide a public empty constructor of class to mapping");
		dict.put(CONNECTION_MATICO_NOT_AVAIBLE,"not avaible instance of %s class");
		dict.put(WRONG_TRANSLATIONS_PARAMETER,"wrong number of parameter into translation. QueryMaticO Framework problem, submit to github issue: https://github.com/PsykeDady/QueryMaticO");

		
		//dict.put(, "");
		return dict;
	}
}
