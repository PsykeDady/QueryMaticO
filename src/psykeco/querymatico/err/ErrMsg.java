package psykeco.querymatico.err;

import java.util.HashMap;

/**
 * This class is intended to automatic translate Error Message of Framework
 * 
 * @author archdady
 */
public final class ErrMsg {
	
	private ErrMsg() {}
	
	public static enum KEY_MSG{
		PORT_NOT_VALID,
		URL_NULL,
		USER_NULL,
		PSK_NULL,
		DB_NULL,
		DB_NOT_VALID(1),
		TABLE_NULL,
		TABLE_NOT_VALID(1),
		COLUMN_EMPTY,
		VALUE_EMPTY(1),
		COLUMN_NOT_VALID(1),
		VALUE_NOT_VALID(1),
		ENTRY_EMPTY,
		NOT_SUPPORT(2),
		CLASS_PARAMETERS,
		PREFIX_NOT_VALID(1),
		SUFFIX_NOT_VALID(1),
		PRIMARY_NOT_NULL
		;
		
		private int substitutions;
		
		private KEY_MSG() {substitutions=0;};
		private KEY_MSG(int sub) {
			substitutions=sub;
		}
		public int getSubstituions() {return substitutions;}
	}
	
	public static enum LANGUAGE{
		IT,
		EN
	}
	
	private static HashMap<KEY_MSG,String> trans;
	
	public static void init ( LANGUAGE ln ) {
		trans=null;
		switch(ln) {
			case IT: break;
			case EN: break;
		}
	}
	
	public static String getMsg(KEY_MSG k, String ...strings ) {
		return trans.get(k);
	}

}
