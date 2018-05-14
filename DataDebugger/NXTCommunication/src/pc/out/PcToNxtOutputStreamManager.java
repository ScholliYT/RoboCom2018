package pc.out;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import pc.connection.NXTCommunication;

public class PcToNxtOutputStreamManager extends Thread{
	
	private NXTCommunication com;
	
	private volatile boolean interrupted;
	private PcToNxtOutputStream out;
	private ArrayList<String> writeBuffer;
	
	public PcToNxtOutputStreamManager(NXTCommunication com, OutputStream raw){
		this.com = com;
		this.writeBuffer = new ArrayList<>();
		this.interrupted = false;
		this.out = new PcToNxtOutputStream(raw);
		this.setDaemon(true);
		this.start();
	}
	
	@Override
	public void run(){
		while(!interrupted){
			if(writeBuffer.size() > 0){
				try{
					synchronized(writeBuffer){
						Iterator<String> it = writeBuffer.iterator();
						while(it.hasNext()){
							out.write(it.next());
							out.flush();
							it.remove();
						}
						writeBuffer.clear();
					}
				}catch(Exception e){
					com.close(true);
				}
				try{
					Thread.sleep(100);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		}
		try{
			out.write("shutdown");
			out.flush();
			out.close();
			System.out.println("out closed!");
			return;
		}catch(Exception ignore){}
	}
	
	public void write(String write){
		synchronized (writeBuffer){
			this.writeBuffer.add(write);
		}
	}
	
	@Override
	public boolean isInterrupted(){
		return interrupted;
	}
	
	public void interrupt(){
		this.interrupted = true;
	}
}