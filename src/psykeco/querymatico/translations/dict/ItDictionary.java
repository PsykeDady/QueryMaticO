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
 * Implementazione dizionario Italiano da utilizzare in {@link psykeco.querymatico.translations.Translations ErrMsg}
 * 
 * @author PsykeDady (psdady@msn.com)
 */
public final class ItDictionary {
	
	/**
	 * @return Dizionario italiano come istanza di {@link HashMap}
	 */
	public static HashMap<KEY_MSG, String> getDict(){
		HashMap<KEY_MSG, String> dict=new HashMap<>();
		
		dict.put(PORT_NOT_VALID, "valore della porta errato");
		dict.put(URL_NULL, "url vuoto");
		dict.put(USER_NULL, "utente non valido");
		dict.put(PSK_NULL, "psk non valida");
		dict.put(DB_NULL, "nome db necessario");
		dict.put(DB_NOT_VALID, "nome db %s non valido");
		dict.put(TABLE_NULL, "nome tabella necessario");
		dict.put(TABLE_NOT_VALID, " nome tabella %s non valido");
		dict.put(COLUMN_EMPTY, "Una colonna \u00e8 stata trovata vuota");
		dict.put(VALUE_EMPTY, "Il valore di %s \u00e8 stata trovata vuota");
		dict.put(COLUMN_NOT_VALID, "La colonna %s non \u00e8 valida");
		dict.put(VALUE_NOT_VALID, "Il valore %s non \u00e8 valido");
		dict.put(ALIAS_NOT_VALID, "Il nome alias %s non \u00e8 valido");
		dict.put(AGGREGATE_NOT_VALID, "la colonna indicata da %s '%s' non \u00e8 valida");
		dict.put(AGGREGATE_NOT_NULL, "la colonna indicata da %s non pu\u00f2 essere nulla");
		dict.put(ENTRY_EMPTY, "lista entry vuota. Serve almeno una coppia colonna-valore");
		dict.put(CLASS_PARAMETERS, "Questa classe non ha parametri, non puo' essere trasformata");
		dict.put(PREFIX_NOT_VALID, "prefisso %s scelto non valido");
		dict.put(SUFFIX_NOT_VALID, "suffisso %s scelto non valido");
		dict.put(PRIMARY_NOT_NULL, "Gli elementi nella chiave primaria non possono essere null");
		dict.put(NOT_SUPPORT_METHOD, "%s non supporta il metodo %s()");
		dict.put(WRONG_CLASS_JOIN, "la tabella di join deve essere di tipo %s");
		dict.put(PRIMARY_KEY_MUST_REFERE, "La chiave primaria deve riferirsi ad una colonna reale");
		dict.put(WRONG_OBJECT_TYPE,"oggetto passato di classe non compatibile. Ricevuto %s, valore atteso %s");
		dict.put(NOT_EMPTY_CONSTRUCTOR,"costruttore non accessibile. Prevedere un costruttore vuoto!");
		dict.put(NOT_EMPTY_ACCESSIBLE_CONSTRUCTOR,"costruttore non accessibile, classe astratta o interfaccia! Prevedere un costruttore vuoto!");
		dict.put(CONSTRUCTOR_ERROR,"Errore chiamando il costruttore. Prevedere un costruttore vuoto!");
		dict.put(CONNECTION_MATICO_NOT_AVAIBLE,"%s non disponibile");
		dict.put(WRONG_TRANSLATIONS_PARAMETER,"numero errato di parametri per questa traduzione. Segnala l'errore su github: https://github.com/PsykeDady/QueryMaticO");

		
		//dict.put(, "");
		return dict;		
	}
	

}
