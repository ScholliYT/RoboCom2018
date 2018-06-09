package pc.ui.Object;

import pc.object.DataFieldType;

/**
 * Represents a row in our JTable
 * @author Simon
 *
 */
public class TableRowObject{
	
	private String fieldName;
	private DataFieldType type;
	private Object value;
	
	/**
	 * Creates a new row-object for our table, however it is not added automatically
	 * @param fieldNamen the fieldname for the new row
	 * @param type the datatype for the new row
	 * @param value the default-value for the new row
	 */
	public TableRowObject(String fieldName, DataFieldType type, Object value){
		this.fieldName = fieldName;
		this.type = type;
		
		try{
			setValidValue(value);
		}catch(NumberFormatException e){
			this.value = 0;
		}
	}
	
	/**
	 * Getter for the fieldname of this row
	 * @return the fieldname
	 */
	public String getFieldName(){
		return fieldName;
	}
	
	/**
	 * Getter for the DataFieldType of this row
	 * @return the DataFieldType
	 */
	public DataFieldType getType(){
		return type;
	}
	
	/**
	 * Getter for the value of this row
	 * @return the value for this row
	 */
	public Object getValue(){
		return value;
	}
	
	/**
	 * Sets a new name for this row. Does not check, if the value is still available
	 * @param newFieldName the new fieldname
	 */
	public void setFieldName(String newFieldName){
		this.fieldName = newFieldName;
	}
	
	/**
	 * Sets a new DataFieldType for this row.
	 * @param newType the new DataFieldType
	 */
	public void setType(DataFieldType newType){
		this.type = newType;
	}
	
	/**
	 * Sets a new value for this row, checks for valid fields
	 * @param newValue the new value
	 */
	public void setValue(Object newValue){
		try{
			setValidValue(newValue);
		}catch(NumberFormatException e){
			this.value = 0;
		}
	}
	
	/**
	 * Returns a string that represents this object
	 */
	@Override
	public String toString(){
		return fieldName + ":" + type.toString() +":" + value + ";";
	}
	
	/**
	 * Sets a valid value for this row. If the given object is not a valid one, default ones will be set
	 * @param value the new value
	 */
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