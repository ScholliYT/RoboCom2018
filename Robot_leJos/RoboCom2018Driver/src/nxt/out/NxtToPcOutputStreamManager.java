package nxt.out;

import java.io.OutputStream;
import java.util.ArrayList;

import lejos.util.Delay;
import nxt.connection.PCCommunicationManager;

public class NxtToPcOutputStreamManager extends Thread{
	
	private PCCommunicationManager parent;
	private NxtToPcOutputStream out;
	private ArrayList<String> toWrite;
	
	public NxtToPcOutputStreamManager(PCCommunicationManager parent, OutputStream out){
		this.parent = parent;
		this.out = new NxtToPcOutputStream(out);
		this.toWrite = new ArrayList<String>();
		this.setDaemon(true);
		super.start();
	}
	
	public void addStringToQueue(String str){
		synchronized(toWrite){
			toWrite.add(str);
		}
	}
	
	@Override
	public void run(){
		String[] msg = new String[0];
		int length;
		while(parent.isAvailable()){
			try{
				if(toWrite.size() != 0){
					synchronized(toWrite){
						length = toWrite.size();
						if(length != 0){
							msg = new String[length];
							System.arraycopy(toWrite.toArray(new String[length]), 0, msg, 0, length);
							toWrite.clear();
						}
					}
					for(String message: msg){
						out.write(message + "\n");
						out.flush();
					}
				}else{
					out.write(" " + "\n");
					out.flush();
				}
			}catch(Exception e){
				parent.close();
			}
			Delay.msDelay(250);
		}
		
		try{
			out.close();
		}catch(Exception e){}
	}
	
	public OutputStream getRawOutputStream(){
		return out.getOutputStream();
	}
	
}