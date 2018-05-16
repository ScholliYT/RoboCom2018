package nxt.connector;

import java.util.ArrayList;

import lejos.nxt.Sound;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.USB;
import lejos.util.Delay;
import nxt.connection.NxtDataField;
import nxt.connection.PCCommunicationManager;
import nxt.object.DataFieldType;

/**
 * Dient zur Verbindung mit dem PC. Sobald initiiert, die attemptConnection()-Methode dieser
 * Klasse aufrufen, um einen Verbindungsversuch zu starten
 * @author Simon
 *
 */
public class PCConnector{
	
	private boolean showProgress, audibleFeedback;
	private ConnectionType connectionType;
	private byte[] pin;
	private ArrayList<NxtDataField> dataFields;
	
	/**
	 * Erstellt eine neue Instanz dieser Klasse, um sich mit einem PC zu verbinden mit Hilfe des Standard-Bluetoothpins 1234
	 * @param connectionType Der Verbindungsmodus, der verwendet werden soll. (Verbindung über USB oder Bluetooth)
	 * @param showProgress <code>true</code> um auf dem Display eine Anzeige zu haben, während auf eine Verbindung vom PC gewartet wird, sonst <code>false</code>
	 * @param audibleFeedback <code>true</code>, wenn der NXT ein Geräusch von sich geben soll, wenn eine Verbindung erfolgreich hergestellt wurde
	 * @param datafields Ein Array bestehend aus den Datenfeldern, die mit dem PC synchronisiert werden sollen, Aufbau des Arrays:<br>
	 * String mit Datenfeldname,<br>
	 * Wert, der Standardmäßig zugewiesen wird (String, Integer, Long, Double oder Float)<br>
	 * Beispiel: <code>new Object[] {"Datenfeldname", "Datenfeldwert", "Datenfeldname 2", 3.3F, "Datenfeldname 3, 3};</code><br>
	 * Obriges Array fügt 3 Datenfelder hinzu:<br>
	 * <ol>
	 * 	<li> Ein Datenfeld mit dem Namen 'Datenfeldname' und dem Wert 'Datenfeldwert'</li>
	 * 	<li> Ein Datenfeld mit dem Namen 'Datenfeldname 2' und dem Wert '3.3F' (als Float)</li>
	 * 	<li> Ein Datenfeld mit dem Namen 'Datenfeldname 3' und dem Wert '3' (als Integer)</li>
	 * </ol>
	 * Bitte bedenkt, dass der PC dennoch aleinige Kontrolle über die Werte hat, d. H. sobald er die Werte löscht, sind die während der Programmausführung
	 * weg. Er kann ebenfalls neue Datenfelder hinzufügen
	 * 
	 * @throws IllegalArgumentException Wenn das Objectarray fehlerhaft ist. Es wird nur auf Richtigkeit der Daten geprüft, sprich darauf, dass Datenfeldnamen
	 * Strings sind und Datenfelderwerte String, Integer, Long, Double oder Floatwerte sind. Die Datenfeldtypen werden automatisch zugewiesen. Es wird nicht auf 
	 * Doppelt vergebene Datenfeldnamen geprüft, das ist euer Job! Zusätzlicher Tipp: mit Hilfe von <code>exception.getMessage()</code> bekommt ihr einen String,
	 * der den Fehler näher beschreibt!
	 */
	public PCConnector(ConnectionType connectionType, boolean showProgress, boolean audibleFeedback, Object[] datafields)throws IllegalArgumentException{
		this(connectionType, new byte[] {1, 2, 3, 4}, showProgress, audibleFeedback, datafields);
	}
	
	/**
	 * Erstellt eine neue Instanz dieser Klasse, um sich mit einem PC zu verbinden
	 * @param connectionType Der Verbindungsmodus, der verwendet werden soll. (Verbindung über USB oder Bluetooth)
	 * @param showProgress <code>true</code> um auf dem Display eine Anzeige zu haben, während auf eine Verbindung vom PC gewartet wird, sonst <code>false</code>
	 * @param audibleFeedback <code>true</code>, wenn der NXT ein Geräusch von sich geben soll, wenn eine Verbindung erfolgreich hergestellt wurde
	 * @param bluetoothPin der Pin, der zur Bluetoothverbindung verwendet werden soll
	 * @param datafields Ein Array bestehend aus den Datenfeldern, die mit dem PC synchronisiert werden sollen, Aufbau des Arrays:<br>
	 * String mit Datenfeldname,<br>
	 * Wert, der Standardmäßig zugewiesen wird (String, Integer, Long, Double oder Float)<br>
	 * Beispiel: <code>new Object[] {"Datenfeldname", "Datenfeldwert", "Datenfeldname 2", 3.3F, "Datenfeldname 3, 3};</code><br>
	 * Obriges Array fügt 3 Datenfelder hinzu:<br>
	 * <ol>
	 * 	<li> Ein Datenfeld mit dem Namen 'Datenfeldname' und dem Wert 'Datenfeldwert'</li>
	 * 	<li> Ein Datenfeld mit dem Namen 'Datenfeldname 2' und dem Wert '3.3F' (als Float)</li>
	 * 	<li> Ein Datenfeld mit dem Namen 'Datenfeldname 3' und dem Wert '3' (als Integer)</li>
	 * </ol>
	 * Bitte bedenkt, dass der PC dennoch aleinige Kontrolle über die Werte hat, d. H. sobald er die Werte löscht, sind die während der Programmausführung
	 * weg. Er kann ebenfalls neue Datenfelder hinzufügen
	 * 
	 * @throws IllegalArgumentException Wenn das Objectarray fehlerhaft ist. Es wird nur auf Richtigkeit der Daten geprüft, sprich darauf, dass Datenfeldnamen
	 * Strings sind und Datenfelderwerte String, Integer, Long, Double oder Floatwerte sind. Die Datenfeldtypen werden automatisch zugewiesen. Es wird nicht auf 
	 * Doppelt vergebene Datenfeldnamen geprüft, das ist euer Job! Zusätzlicher Tipp: mit Hilfe von <code>exception.getMessage()</code> bekommt ihr einen String,
	 * der den Fehler näher beschreibt!
	 */
	public PCConnector(ConnectionType connectionType, byte[] bluetoothPin, boolean showProgress, boolean audibleFeedback, Object[] datafields) throws IllegalArgumentException{
		this.connectionType = connectionType;
		this.showProgress = showProgress;
		this.audibleFeedback = audibleFeedback;
		this.pin = bluetoothPin;
		this.validateDatafields(datafields);
	}
	
	private void validateDatafields(Object[] data) throws IllegalArgumentException{
		if(data.length % 2 != 0) throw new IllegalArgumentException("Die datafields im PCConnector müssen eine gerade Anzahl haben!");
		ArrayList<NxtDataField> list = new ArrayList<>();
		String currentName = "";
		for(int i = 0; i < data.length; i++){
			if((i % 2) == 0){ //Aktuell ist ein Datenfeldname am Start
				if(data[i] instanceof String && !((String) data[i]).isEmpty()){
					currentName = (String) data[i];
				}else{
					throw new IllegalArgumentException("Ein Datenfeldname ist kein String oder leer! (i = " + i + ")");
				}
			}else{
				Object field = data[i];
				DataFieldType type = null;
				if(field instanceof String){
					type = DataFieldType.STRING;
				}else if(field instanceof Integer){
					type = DataFieldType.INTEGER;
				}else if(field instanceof Long){
					type = DataFieldType.LONG;
				}else if(field instanceof Double){
					type = DataFieldType.DOUBLE;
				}else if(field instanceof Float){
					type = DataFieldType.FLOAT;
				}else{
					throw new IllegalArgumentException("Falscher Datenfeldtyp im PCConnector");
				}
				list.add(new NxtDataField(currentName, type, field + ""));
				currentName = "";
			}
		}
		this.dataFields = list;
	}
	
	/**
	 * Versucht, sich mit dem Computer zu verbinden und so Daten mit diesem auszutauschen
	 * @return {@link PCCommunicationManager}, wenn die Verbindung erfolgreich war, ansonsten <code>null<code>
	 */
	public PCCommunicationManager attemptConnection(/*ConnectionType connectionType, long delayMs, boolean showProgress, boolean audibleFeedback*/){
		Sound.setVolume(100);
		ProgressDisplay progress = new ProgressDisplay();
		PCCommunicationManager result = null;
		if(showProgress){
			progress.start();
		}
		
		switch(connectionType){
			case USB:
				NXTConnection con = USB.waitForConnection(0, NXTConnection.PACKET);
				if(con != null){
					result = new PCCommunicationManager(con.openInputStream(), con.openOutputStream(), dataFields);
					if(audibleFeedback){
						Sound.playTone(2500, 100);
						Delay.msDelay(150);
						Sound.playTone(3000, 100);
						Delay.msDelay(120);
					}
					break;
				}else if(audibleFeedback){
					Sound.playTone(200, 500);
					Delay.msDelay(510);
				}
				break;
			case BLUETOOTH:
				if(!Bluetooth.getPower()){
					Bluetooth.setPower(true);
					Bluetooth.setVisibility((byte) 1);
					while(!Bluetooth.getPower() && Bluetooth.getVisibility() != 1);
				}else if(Bluetooth.getVisibility() != 1){
					Bluetooth.setVisibility((byte) 1);
					while(Bluetooth.getVisibility() != 1);
				}
				
				BTConnection btCon = Bluetooth.waitForConnection(0, NXTConnection.PACKET, pin);
				if(btCon != null){
					result = new PCCommunicationManager(btCon.openInputStream(), btCon.openOutputStream(), dataFields);
					if(audibleFeedback){
						Sound.playTone(2500, 100);
						Delay.msDelay(150);
						Sound.playTone(3000, 100);
						Delay.msDelay(120);
					}
				}else if(audibleFeedback){
					Sound.playTone(200, 500);
					Delay.msDelay(510);
				}
				break;
		}
		progress.connectionEstablished();
		Delay.msDelay(150);
		return result;
	}
	
}