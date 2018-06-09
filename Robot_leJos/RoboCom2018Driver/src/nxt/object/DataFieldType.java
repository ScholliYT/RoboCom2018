package nxt.object;

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
	
	/**
	 * Returns the actual Object from a given String
	 * @param str the String that represents a object
	 * @return the actual Object of a datafield
	 */
	public Object getObjectFromString(String str){
		switch(this){
			case STRING:
				return str;
			case INTEGER:
				return Integer.parseInt(str);
			case LONG:
				return Long.parseLong(str);
			case DOUBLE:
				return Double.parseDouble(str);
			case FLOAT:
				return Float.parseFloat(str);
			case BOOLEAN:
				return Boolean.parseBoolean(str.toLowerCase());
			default:
				return str;
		}
	}
	
	/**
	 * Guesses the DataFieldType given by a Object
	 * @param o the Object, from which this method tries to guess the Datafieldtype
	 * @return a DataFieldType, or null, if there was no positive match
	 */
	public static DataFieldType guessDataFieldTypeFromObject(Object o){
		if(o instanceof Integer){
			return INTEGER;
		}else if(o instanceof Long){
			return LONG;
		}else if(o instanceof Double){
			return DOUBLE;
		}else if(o instanceof Float){
			return FLOAT;
		}else if(o instanceof String){
			return STRING;
		}else if(o instanceof Boolean){
			return BOOLEAN;
		}else{
			return null;
		}
	}
	
}