package pc.object;

import pc.connection.NXTCommunication;

public class ShutdownHook extends Thread{
	
	private NXTCommunication com;
	
	private ShutdownHook(NXTCommunication com){
		this.com = com;
		Runtime.getRuntime().addShutdownHook(this);
	}
	
	@Override
	public void run(){
		if(com != null && !com.isClosed()){
			com.close(true);
		}
	}
	
	public static void addShutdownHook(NXTCommunication com){
		new ShutdownHook(com);
	}
	
}