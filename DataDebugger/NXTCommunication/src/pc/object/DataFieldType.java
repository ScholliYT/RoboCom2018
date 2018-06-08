package pc.object;

/**
 * Simple enumeration of all the data types I chose to allow for editing on the run
 * @author Simon
 *
 */
public enum DataFieldType{
	
	STRING,
	INTEGER,
	LONG,
	DOUBLE,
	FLOAT,
	BOOLEAN;
	
	/**
	 * Returns a Datafieldtype from the given string. Example: "string" will return DataFieldType.STRING, "float" will return DataFieldType.FLOAT
	 * @param type The type as String
	 * @return the DataFieldType represented by the string, or as a default value DataFieldType.STRING
	 */
	public static DataFieldType getDataFieldTypeFromString(String type){
		switch(type.trim().toLowerCase()){
			case "string":
				return DataFieldType.STRING;
			case "integer":
				return DataFieldType.INTEGER;
			case "long":
				return DataFieldType.LONG;
			case "double":
				return DataFieldType.DOUBLE;
			case "float":
				return DataFieldType.FLOAT;
			case "boolean":
				return DataFieldType.BOOLEAN;
			default:
				return DataFieldType.STRING;
		}
	}
	
}