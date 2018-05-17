package nxt.out;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

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
		while(parent.isAvailable()){
			try{
				synchronized(toWrite){
					while(toWrite.size() > 0){
						Iterator<String> it = toWrite.iterator();
						
						while(it.hasNext()){
							out.write(it.next() + "\n");
							out.flush();
							it.remove();
						}
						toWrite.clear();
					}
				}
				out.write(" " + "\n");
				out.flush();
			}catch(Exception e){
				parent.close();
			}
			Delay.msDelay(125);
		}
		
		try{
			out.close();
		}catch(Exception e){}
	}
}