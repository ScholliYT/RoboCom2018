package nxt.connection;

import nxt.object.DataFieldType;

/**
 * Represents a datafield, which should ne updated by the PC
 * @author Simon
 */
public class NxtDataField{
	
	private String name;
	private DataFieldType type;
	private Object value;
	
	/**
	 * Do not instantiate manually, it won't have the intended effect!
	 */
	@Deprecated
	public NxtDataField(String name, DataFieldType type, String value){
		this.name = name;
		this.type = type;
		
		switch(type){
			case STRING:
				this.value = (String) value;
				break;
			case INTEGER:
				this.value = Integer.parseInt(value);
				break;
			case LONG:
				this.value = Long.parseLong(value);
				break;
			case DOUBLE:
				this.value = Double.parseDouble(value);
				break;
			case FLOAT:
				this.value = Float.parseFloat(value);
				break;
			case BOOLEAN:
				this.value = Boolean.parseBoolean(value);
				break;
			default:
				break;
		}
	}
	
	/**
	 * Do not instantiate manually, it won't have the intended effect!
	 */
	public NxtDataField(String name, DataFieldType type, Object value){
		this.name = name;
		this.type = type;
		this.value = value;
	}
	
	/**
	 * returns the name of this datafield
	 * @return the name of this datafield
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * @return the DataFieldType {String, Integer, Long, Double, Float}
	 */
	public DataFieldType getType(){
		return type;
	}
	
	/**
	 * Returns the Datafieldvalue as an object. The actual value can be archived with help of casting. Example:<br>
	 * <code>Object datafield = getValue();<br>
	 * if(datafield instanceof String){<br>
	 * String result = (String) datafield;<br>
	 * }<br></code>
	 * @return the value of this datafield as an object
	 */
	public Object getValue(){
		return value;
	}
	
}