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
			switch(type){
				case STRING:
					this.value = value;
					break;
				case INTEGER:
					this.value = Integer.parseInt((String) value);
					break;
				case LONG:
					this.value = Long.parseLong((String) value);
					break;
				case DOUBLE:
					this.value = Double.parseDouble((String) value);
					break;
				case FLOAT:
					this.value = Float.parseFloat((String) value);
					break;
				default:
					this.value = "";
			}
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
			switch(type){
				case STRING:
					this.value = newValue;
					break;
				case INTEGER:
					this.value = Integer.parseInt((String) newValue);
					break;
				case LONG:
					this.value = Long.parseLong((String) newValue);
					break;
				case DOUBLE:
					this.value = Double.parseDouble((String) newValue);
					break;
				case FLOAT:
					this.value = Float.parseFloat((String) newValue);
					break;
				default:
					this.value = 0;
			}
		}catch(NumberFormatException e){
			this.value = 0;
		}
	}
	
	@Override
	public String toString(){
		return fieldName + ":" + type.toString() +":" + value + ";";
	}
	
}