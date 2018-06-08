package pc.connector;

/**
 * Simple Enum to let the program know, what kind of connection should be used in order to connect
 * to the NXT
 * @author Simon
 */
public enum ConnectionType{
	
	USB(0),
	BLUETOOTH(1);
	
	private int id;
	
	private ConnectionType(int id){
		this.id = id;
	}
	
	/**
	 * Returns the LeJos-Api-Id for the Connection to use for the Connection to the NXT
	 * @return the JeJos-Api ID for the specific connection
	 */
	public int getId(){
		return id;
	}
}