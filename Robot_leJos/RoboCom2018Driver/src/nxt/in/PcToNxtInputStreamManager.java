package nxt.in;

import java.io.InputStream;

import lejos.util.Delay;
import nxt.connection.NxtDataField;
import nxt.connection.PCCommunicationManager;

/**
 * Manages the Datainputstream and interprets the data sent by the PC
 * @author Simon
 */
public class PcToNxtInputStreamManager extends Thread{
	
	private PCCommunicationManager parent;
	private PCToNXTInputStream in;
	
	/**
	 * @param com The NXTCommunication
	 * @param parent the PCCommnicationManager for the current connection
	 * @param in The "raw" InputStream for communicating with the PC
	 */
	public PcToNxtInputStreamManager(PCCommunicationManager parent, InputStream in){
		this.parent = parent;
		this.in = new PCToNXTInputStream(in);
		this.setDaemon(true);
		super.start();
	}
	
	@Override
	public void run(){
		try{
			while(parent.isAvailable()){
				int read;
				String buffer = "";
				reader : while((read = in.read()) != -1){
					buffer += (char) read;
					if(buffer == null || buffer.isEmpty()){
						Delay.msDelay(50);
						continue reader;
					}
					if(buffer.charAt(buffer.length()-1) == '\n'){
						if(buffer.startsWith("update")){
							String upload = "df!";
							
							for(NxtDataField df: parent.getDatafields()){
								upload += df.getName() + ":" + df.getType().toString() + ":" + df.getValue().toString() + ";";
							}
							parent.writeString(upload);
							break reader;
						}else if(buffer.startsWith("df!")){
							parent.resolveIncomingDatafieldUpdate(buffer);
						}else if(buffer.toLowerCase().startsWith("shutdown")){
							parent.close();
							in.close();
							return;
						}
						buffer = "";
					}
				}
				Delay.msDelay(50);
			}
			in.close();
		}catch(Exception e){
			System.out.println(e.getMessage());
			parent.close();
		}
	}
	
	/**
	 * Closes the underlying stream and interrupts this manager
	 */
	public void close(){
		try{
			in.close();
		}catch(Exception ignore){}
	}
	
}