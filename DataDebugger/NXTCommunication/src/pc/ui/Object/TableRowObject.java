package pc.ui.Object;

import pc.object.DataFieldType;

public class TableRowObject{
	
	private String fieldName;
	private DataFieldType type;
	private Object value;
	
	public TableRowObject(String fieldName, DataFieldType type, Object value){
		this.fieldName = fieldName;
		this.type = type;
		
		try{
			setValidValue(value);
		}catch(NumberFormatException e){
			this.value = 0;
		}
	}
	
	public String getFieldName(){
		return fieldName;
	}
	
	public DataFieldType getType(){
		return type;
	}
	
	public Object getValue(){
		return value;
	}
	
	public void setFieldName(String newFieldName){
		this.fieldName = newFieldName;
	}
	
	public void setType(DataFieldType newType){
		this.type = newType;
	}
	
	public void setValue(Object newValue){
		try{
			setValidValue(newValue);
		}catch(NumberFormatException e){
			this.value = 0;
		}
	}
	
	@Override
	public String toString(){
		return fieldName + ":" + type.toString() +":" + value + ";";
	}
	
	private void setValidValue(Object value){
		try{
			switch(type){
				case STRING:
					this.value = value.toString();
					break;
				case INTEGER:
					this.value = Integer.parseInt(value + "");
					break;
				case LONG:
					this.value = Long.parseLong(value + "");
					break;
				case DOUBLE:
					this.value = Double.parseDouble(value + "");
					break;
				case FLOAT:
					this.value = Float.parseFloat(value + "");
					break;
				case BOOLEAN:
					this.value = Boolean.parseBoolean(value + "");
					break;
				default:
					this.value = 0;
					break;
			}
		}catch(NumberFormatException nfe){
			this.value = 0;
		}
	}
	
}