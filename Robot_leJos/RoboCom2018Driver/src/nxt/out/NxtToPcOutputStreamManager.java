package nxt.out;

import java.io.OutputStream;
import java.util.ArrayList;

import lejos.util.Delay;
import nxt.connection.PCCommunicationManager;

/**
 * Managed den Output für das NXT-Gerät
 * @author Simon
 *
 */
public class NxtToPcOutputStreamManager extends Thread{
	
	private PCCommunicationManager parent;
	private NxtToPcOutputStream out;
	private ArrayList<String> toWrite;
	
	/**
	 * Konstruktor der Klasse, wird intern aufgerufen
	 * @param parent Der PCCommunikationsmanager, um bei Fehlern den kompletten Debugger zu beenden
	 * @param out Der Default-Outputstream, durch die Verbindung mit dem PC bekommen
	 */
	public NxtToPcOutputStreamManager(PCCommunicationManager parent, OutputStream out){
		this.parent = parent;
		this.out = new NxtToPcOutputStream(out);
		this.toWrite = new ArrayList<String>();
		this.setDaemon(true);
		super.start();
	}
	
	/**
	 * Fügt eine neue Nachricht zum Senden an den PC hinzu
	 * @param str der zu versendende String
	 */
	public void addStringToQueue(String str){
		synchronized(toWrite){
			toWrite.add(str);
		}
	}
	
	/**
	 * Threadschleife
	 */
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
	
	/**
	 * Gibt den Outputstream zurück
	 * @return den "rohen" Outputstream
	 */
	public OutputStream getRawOutputStream(){
		return out.getOutputStream();
	}
	
}