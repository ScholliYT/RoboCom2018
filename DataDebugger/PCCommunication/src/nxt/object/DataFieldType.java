package nxt.object;

public enum DataFieldType{
	
	STRING,
	INTEGER,
	LONG,
	DOUBLE,
	FLOAT;
	
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
			default:
				return DataFieldType.STRING;
		}
	}
	
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
			default:
				return str;
		}
	}
	
	public static DataFieldType guessDataFieldTypeFromObject(Object o){
		if(o instanceof Integer){
			return INTEGER;
		}else if(o instanceof Long){
			return LONG;
		}else if(o instanceof Double){
			return DOUBLE;
		}else if(o instanceof Float){
			return FLOAT;
		}else{
			return STRING;
		}
	}
	
}