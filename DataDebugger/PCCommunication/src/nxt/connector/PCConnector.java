package nxt.connector;

import lejos.nxt.Sound;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.USB;
import lejos.util.Delay;
import nxt.connection.PCCommunicationManager;

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
	
	/**
	 * Erstellt eine neue Instanz dieser Klasse, um sich mit einem PC zu verbinden mit Hilfe des Standard-Bluetoothpins 1234
	 * @param connectionType Der Verbindungsmodus, der verwendet werden soll. (Verbindung über USB oder Bluetooth)
	 * @param showProgress <code>true</code> um auf dem Display eine Anzeige zu haben, während auf eine Verbindung vom PC gewartet wird, sonst <code>false</code>
	 * @param audibleFeedback <code>true</code>, wenn der NXT ein Geräusch von sich geben soll, wenn eine Verbindung erfolgreich hergestellt wurde
	 */
	public PCConnector(ConnectionType connectionType, boolean showProgress, boolean audibleFeedback){
		this(connectionType, new byte[] {1, 2, 3, 4}, showProgress, audibleFeedback);
	}
	
	/**
	 * Erstellt eine neue Instanz dieser Klasse, um sich mit einem PC zu verbinden
	 * @param connectionType Der Verbindungsmodus, der verwendet werden soll. (Verbindung über USB oder Bluetooth)
	 * @param showProgress <code>true</code> um auf dem Display eine Anzeige zu haben, während auf eine Verbindung vom PC gewartet wird, sonst <code>false</code>
	 * @param audibleFeedback <code>true</code>, wenn der NXT ein Geräusch von sich geben soll, wenn eine Verbindung erfolgreich hergestellt wurde
	 * @param bluetoothPin der Pin, der zur Bluetoothverbindung verwendet werden soll
	 */
	public PCConnector(ConnectionType connectionType, byte[] bluetoothPin, boolean showProgress, boolean audibleFeedback){
		this.connectionType = connectionType;
		this.showProgress = showProgress;
		this.audibleFeedback = audibleFeedback;
		this.pin = bluetoothPin;
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
					result = new PCCommunicationManager(con.openInputStream(), con.openOutputStream());
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
					result = new PCCommunicationManager(btCon.openInputStream(), btCon.openOutputStream());
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