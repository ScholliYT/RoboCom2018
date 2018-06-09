package pc.object;

import pc.connection.NXTCommunication;

/**
 * A Thread, that will be executed, when the Program is beeing closed
 * @author Simon
 */
public class ShutdownHook extends Thread{
	
	//The Communication
	private NXTCommunication com;
	
	/**
	 * Private constructor, use <code>addShutdownHook(NXTCommunication)</code> instead!
	 * @param com the current NXTCommunication, we try to close after the application wished to exit
	 */
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
	
	/**
	 * Adds this Shutdownhook to the runtime, in oder to make it execute, when the program is exit
	 * @param com the current NXTCommunication, that needs to be closed, before the program is exited
	 */
	public static void addShutdownHook(NXTCommunication com){
		new ShutdownHook(com);
	}
	
}