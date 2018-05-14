package nxt.in;

import java.io.InputStream;

import lejos.util.Delay;
import nxt.connection.NxtDataField;
import nxt.connection.PCCommunicationManager;

public class PcToNxtInputStreamManager extends Thread{
	
	private PCCommunicationManager parent;
	private PcToNxtInputStream in;
	
	public PcToNxtInputStreamManager(PCCommunicationManager parent, InputStream in){
		this.parent = parent;
		this.in = new PcToNxtInputStream(in);
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
			parent.close();
		}
	}
	
	public void close(){
		try{
			in.close();
		}catch(Exception ignore){}
	}
	
}