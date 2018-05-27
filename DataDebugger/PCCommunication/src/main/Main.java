package main;

import lejos.nxt.Button;
import lejos.util.Delay;
import nxt.connection.NxtDataField;
import nxt.connection.PCCommunicationManager;
import nxt.connector.ConnectionType;
import nxt.connector.PCConnector;

public class Main{
	
	@SuppressWarnings({ "unused"})
	public static void main(String[] args){
		PCConnector connector = null;
		try{
			connector = new PCConnector(ConnectionType.BLUETOOTH, new byte[] {1, 2, 3, 4}, true, true, "Datenfeldname", "I bims 1 String", "Double", 3.3D,
					"Long", 19811918184L, "Float", 3.3F, "Integer", 33, "Boolean", true); //Versuchen, eine Instanz der Klasse PCConnector zu erstellen
		}catch(IllegalArgumentException iae){ //Das Object[] enthält einen Syntaxfehler
			System.out.println(iae.getMessage()); //Den Fehler ausgeben
			while(!Button.RIGHT.isDown()); //Auf Bestätigung des Nutzers warten
			System.exit(0); //Beenden
		}
		PCCommunicationManager man = null;
		while(man == null){ //Auf Verbindung warten
			man = connector.attemptConnection();
			Delay.msDelay(500);
		}
		Delay.msDelay(100);
		int count = 0;
		man.redirectSystemOutputToConnectedPC();
		while(!Button.ESCAPE.isDown()){ //Programmhauptschleife
			
			if(!man.isAvailable()){ //Prüfen, ob der PCCommunicationmanager geschlossen wurde
				System.out.println("Connection closed!");
				return;
			}
			
			if(Button.LEFT.isDown()){ //Prüfen, ob der Linke Knopp gedrückt ist
//				man.writeString("Message " + ++count); //Eine Nachricht senden
				System.out.println("Message: " + ++count);
				while(Button.LEFT.isDown()); //Waren, bis der Knopp wieder losgelassen wird
			}else if(Button.RIGHT.isDown()){
				try{
//					m2();
					throw new Exception("Ich bin eine Fehlermeldung!");
				}catch(Exception e){
					man.writeException(e);
				}
				while(Button.RIGHT.isDown());
			}
			
			if(man.hasDatafieldUpdate()){ //Prüfen, ob es ein Datenfeldupdate gibt
				NxtDataField[] newDatafields = man.getDatafields(); //Die neuen Datenfelder hohlen
				//Etwas mit den Daten machen
			}
			Delay.msDelay(10);
		}
		man.close(); //Zum Ende des Programms den PCCommunicationmanager schließen
	}
	
	private static void m1(){
		int test[] = new int[2];
		// Force an exception
		test[0] = test[1] + test[2]; // This is line 6
	}
	
	private static void m2(){
		m1();
	}
	
}