package nxt.object;

import nxt.connection.PCCommunicationManager;

/**
 * A Thread, that will be executed, when the Program is beeing closed
 * @author Simon
 */
public class ShutdownHook extends Thread{
	
	private PCCommunicationManager man;
	
	/**
	 * Private constructor, use <code>addShutdownHook(NXTCommunication)</code> instead!
	 * @param man the current PCCommunicationManager, we try to close after the application wished to exit
	 */
	private ShutdownHook(PCCommunicationManager man){
		this.man = man;
		Runtime.getRuntime().addShutdownHook(this);
	}
	
	/**
	 * Is run, when the application is exiting
	 */
	@Override
	public void run(){
		if(man != null && man.isAvailable()){
			man.close();
		}
	}
	
	/**
	 * Adds this ShutdownHook to the Runtime, so it will be executed, when the application terminates
	 * @param man the current PCCommunicationManager, we try to close after the application wished to exit
	 */
	public static void addShutdownHook(PCCommunicationManager man){
		new ShutdownHook(man);
	}
	
}