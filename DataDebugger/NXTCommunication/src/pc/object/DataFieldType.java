package pc.object;

public enum DataFieldType{
	
	STRING,
	INTEGER,
	LONG,
	DOUBLE,
	FLOAT,
	BOOLEAN;
	
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