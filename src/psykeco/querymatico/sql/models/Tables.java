package psykeco.querymatico.sql.models;


/**
 * <p>This class is basically a java-bean needed to query from {@link psykeco.querymatico.sql.runners.InformationSchema InformationSchema} DB and tables.</br></p> 
 * 
 * 
 * @author PsykeDady (psdady@msn.com)
 * */
public class Tables {
	
	/** table_schema name (a.k.a. databases) */
	private String table_schema;
	/** tables name */
	private String table_name;
	
	/**
	 * @return table schema/database name
	 */
	public String getTableSchema() {
		return table_schema;
	}
	/**
	 * set table schema/database name
	 * @param table_schema
	 */
	public void setTableSchema(String table_schema) {
		this.table_schema = table_schema;
	}
	/**
	 * 
	 * @return table name
	 */
	public String getTableName() {
		return table_name;
	}
	/**
	 * set table name
	 * @param table_name
	 */
	public void setTableName(String table_name) {
		this.table_name = table_name;
	}

}
