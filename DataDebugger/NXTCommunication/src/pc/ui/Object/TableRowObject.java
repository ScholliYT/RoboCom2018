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
					this.value = value + "";
					break;
				case INTEGER:
					Integer.parseInt(value + "");
					this.value = value;
					break;
				case LONG:
					Long.parseLong(value + "");
					this.value = value;
					break;
				case DOUBLE:
					Double.parseDouble(value + "");
					this.value = value;
					break;
				case FLOAT:
					Float.parseFloat(value + "");
					this.value = value;
					break;
				default:
					this.value = 0;
			}
		}catch(NumberFormatException nfe){
			this.value = 0;
		}
	}
	
}