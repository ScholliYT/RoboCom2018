package nxt.connection;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import lejos.nxt.LCDOutputStream;
import lejos.util.Delay;
import nxt.connector.PCConnector;
import nxt.in.PcToNxtInputStreamManager;
import nxt.object.DataFieldType;
import nxt.object.ExceptionToStringOutputStream;
import nxt.object.ExceptionToStringPrintStream;
import nxt.out.NxtToPcOutputStreamManager;

/**
 * Dient nach der Verbindungsherstellung mit Hilfe von {@link PCConnector} dazu,
 * mit dem Computerprogramm zu kommunizieren
 * @author Simon
 *
 */
public class PCCommunicationManager implements Closeable{
	
	private NxtToPcOutputStreamManager out;
	private PcToNxtInputStreamManager in;
	private ExceptionToStringPrintStream ps;
	private volatile boolean available;
	private boolean datafieldUpdate;
	private ArrayList<Object> unreadMessages;
	private ArrayList<NxtDataField> dataFields;
	
	public PCCommunicationManager(InputStream in, OutputStream out, ArrayList<NxtDataField> dataFields){
		this.available = true;
		this.datafieldUpdate = false;
		this.dataFields = new ArrayList<>();
		this.ps = new ExceptionToStringPrintStream(new ExceptionToStringOutputStream());
		this.dataFields = dataFields;
		
		this.out = new NxtToPcOutputStreamManager(this, out);
		this.in = new PcToNxtInputStreamManager(this, in);
		this.unreadMessages = new ArrayList<Object>();
	}
	
	/**
	 * Fragt nach neuen, ungelesenen Nachrichten
	 */
	public boolean hasNewMessages(){
		return unreadMessages.size() > 0;
	}
	
	/**
	 * SChaut, ob es ein Update für die Datenfelder gibt
	 * @return <code>true</code>, wenn es ein Update gibt, ansonsten <code>false</code>
	 */
	public boolean hasDatafieldUpdate(){
		return datafieldUpdate;
	}
	
	/**
	 * Gibt die aktuellen Datenfelder als Array zurück und setzt
	 * den hasDatafieldUpdate-Wert zurück
	 * @return <code>ArrayList<NxtDataField</code> mit allen Datenfeldern
	 */
	public ArrayList<NxtDataField> getDatafields(){
		this.datafieldUpdate = false;
		return dataFields;
	}
	
	/**
	 * Uninteressant für aussen, intern wird diese Mehtode verwendet
	 * um ein Update der Datenfelder auszulesen
	 * @param incoming
	 */
	public void resolveIncomingDatafieldUpdate(String incoming){
		this.datafieldUpdate = true; //TODO Synchronisieren?
		dataFields.clear();
		incoming = incoming.substring(3, incoming.length()-1);
		String name = "";
		String type = "";
		String value = "";
		String buffer = "";
		int doppelCount = 0;
		char[] chars = incoming.toCharArray();
		for(int i = 0; i < chars.length; i++){
			char c = chars[i];
			if(c != ':' && c != ';'){
				buffer += c;
				continue;
			}else if(c == ':'){
				doppelCount++;
				if(doppelCount == 1){
					name = buffer;
				}else if(doppelCount == 2){
					type = buffer;
				}
				buffer = "";
			}else if(c == ';'){
				value = buffer;
				doppelCount = 0;
				dataFields.add(new NxtDataField(name, DataFieldType.getDataFieldTypeFromString(type), value));
				buffer = "";
				name = "";
				type = "";
				value = "";
			}
		}
		this.datafieldUpdate = true;
	}
	
	/**
	 * Sendet eine Nachricht an den PC
	 * @param s Die zu versendende Nachricht
	 */
	public void writeString(String s){
		out.addStringToQueue(s);
	}
	
	/**
	 * Sendet eine Fehlermeldung an den PC
	 * @param ex Die zu Versendende Exception
	 */
	public void writeException(Throwable ex){
		ex.printStackTrace(ps);
		writeString("ex!" + ps.getExcpetionAsString());
	}
	
	/**
	 * Leitet den Verkehr von System.out.println(String s) auf den PC-Debugger um<br>
	 * Bei verwendung von anderen Methoden kommt es zu unerwarteten Ergebnissen auf dem PC
	 */
	public void redirectSystemOutputToConnectedPC(){
		PrintStream ps = new PrintStream(out.getRawOutputStream());
		System.out = ps;
	}
	
	/**
	 * Uninteressant für aussen, da diese Methode verwendet wird um zu zeigen,
	 * dass die verbindung getrennt wurde, zum manuellen Schließen <code>close()</code> verwenden!
	 * @param available
	 */
	public void setAvailability(boolean available){
		this.available = available;
	}
	
	/**
	 * Uninteressant für aussen. Lässt die Datenströme eine neue Nachricht hinzufügen
	 * @param msg
	 */
	public void addNewMessage(Object msg){
		this.unreadMessages.add(msg);
	}
	
	/**
	 * Schließt alle Verbindungen zum PC und teilt diesem
	 * mit, dass die Verbindung geschlossen wurde
	 */
	@Override
	public void close(){
		out.addStringToQueue("shutdown");
		System.out =  new PrintStream(new LCDOutputStream());
		Delay.msDelay(150);
		this.available = false;
		in.close();
	}
	
	/**
	 * Gibt zurück, ob die Verbindung noch besteht, oder abgebrochen wurde
	 * @return <code>true</code> wenn die Verbindung noch aktiv ist, ansonsten <code>false</code>
	 */
	public boolean isAvailable(){
		return available;
	}
	
}