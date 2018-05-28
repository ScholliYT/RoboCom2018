package nxt.connection;

import nxt.object.DataFieldType;

/**
 * Stellt ein Datenfeld, welches vom PC geupdatet werden soll dar
 * @author Simon
 */
public class NxtDataField{
	
	private String name;
	private DataFieldType type;
	private Object value;
	
	/**
	 * Nicht manuell instanzieren!
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
		}
	}
	
	/**
	 * Nicht manuell instanzieren!
	 */
	public NxtDataField(String name, DataFieldType type, Object value){
		this.name = name;
		this.type = type;
		this.value = value;
	}
	
	/**
	 * Gibt den Namen dieses Datenfeldes zurück
	 * @return den Namen des Datenfeldes
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Gibt den Datenfeldtyp dieses Datenfeldes zurück
	 * @return den Datenfeldtyp {String, Integer, Long, Double, Float}
	 */
	public DataFieldType getType(){
		return type;
	}
	
	/**
	 * Gibt den Datenfeldwert als Objekt zurück. Der tatsächliche Wert kann mithilfe von
	 * explizitem casten ermittelt werden. Beispiel:<br>
	 * <code>Object datafield = getValue();<br>
	 * if(datafield instanceof String){<br>
	 * String result = (String) datafield;<br>
	 * }<br></code>
	 * usw...
	 * @return den Datenfeldwert als Objekt
	 */
	public Object getValue(){
		return value;
	}
	
}