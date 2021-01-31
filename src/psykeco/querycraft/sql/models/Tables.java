package psykeco.querycraft.sql.models;

/**
 * This class is needed to query Informations_schema DB and tables 
 * @author PsykeDady
 * */
public class Tables {
	
	private String table_schema;
	private String table_name;
	
	public String getTableSchema() {
		return table_schema;
	}
	public void setTableSchema(String table_schema) {
		this.table_schema = table_schema;
	}
	public String getTableName() {
		return table_name;
	}
	public void setTableName(String table_name) {
		this.table_name = table_name;
	}

}
