package pc.connector;

public enum ConnectionType{
	
	USB(0),
	BLUETOOTH(1);
	
	private int id;
	
	private ConnectionType(int id){
		this.id = id;
	}
	
	public int getId(){
		return id;
	}
}