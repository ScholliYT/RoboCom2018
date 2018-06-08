package pc.out;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import lejos.util.Delay;
import pc.connection.NXTCommunication;

/**
 * Manages the DataOutput to the NXT
 * @author Simon
 *
 */
public class PcToNxtOutputStreamManager extends Thread{
	
	private NXTCommunication com; //Our current Communication-instance
	
	private volatile boolean interrupted; //indicates, whether the thread was interrupted, or not
	private PcToNxtOutputStream out; //The outputstream, we use to send data to the NXT
	private ArrayList<String> writeBuffer; //A local buffer, which is sent every 25 milliseconds
	
	/**
	 * @param com The NXTCommunication
	 * @param raw The "raw" OutputStream for communicating with the NXT
	 */
	public PcToNxtOutputStreamManager(NXTCommunication com, OutputStream raw){
		this.com = com;
		this.writeBuffer = new ArrayList<>();
		this.interrupted = false;
		this.out = new PcToNxtOutputStream(raw);
		this.setDaemon(true); //Sets the daemon-flag, which means, that this thread will terminate, if there is no other thread without the deamonthread is running anymore
		this.start(); //Starts this thread
	}
	
	@Override
	public void run(){
		while(!interrupted){ //While the interrupted-flag is not set
			if(writeBuffer.size() > 0){ //Just do something, when there are messages to send
				try{
					synchronized(writeBuffer){ //Make sure, this thread has exclusive access to the buffer
						//Start: Copy the buffered messages in a secondary buffer, in order to give it as fast free as possible
						Iterator<String> it = writeBuffer.iterator();
						while(it.hasNext()){
							out.write(it.next());
							out.flush();
							it.remove();
						}
						writeBuffer.clear();
						//End copy buffered messages in a secodary buffer
					}
				}catch(Exception e){ //if something went wrong, the connection is closed
					com.close(true);
				}
			}
			Delay.msDelay(25);
		}
		try{ //After end of this thread, we let the NXT know, that the connection was closed by the PC
			out.write("shutdown");
			out.flush();
			out.close();
			System.out.println("out closed!");
			return;
		}catch(Exception ignore){}
	}
	
	/**
	 * Adds a String to the local buffer, and will be sent within the next 30ms to the NXT
	 * @param write the string to be sent
	 */
	public void write(String write){
		synchronized (writeBuffer){
			this.writeBuffer.add(write);
		}
	}
	
	/**
	 * Returns the interrupted-status this thread has
	 * @return the current interrupted-flag
	 */
	@Override
	public boolean isInterrupted(){
		return interrupted;
	}
	
	/**
	 * Sets the interrupted-flag for this thread, lets the Thread exit
	 */
	public void interrupt(){
		this.interrupted = true;
	}
}