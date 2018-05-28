package nxt.object;

import nxt.connection.PCCommunicationManager;

public class ShutdownHook extends Thread{
	
	private PCCommunicationManager man;
	
	private ShutdownHook(PCCommunicationManager man){
		this.man = man;
		Runtime.getRuntime().addShutdownHook(this);
	}
	
	@Override
	public void run(){
		if(man != null && man.isAvailable()){
			man.close();
		}
	}
	
	public static void addShutdownHook(PCCommunicationManager man){
		new ShutdownHook(man);
	}
	
}