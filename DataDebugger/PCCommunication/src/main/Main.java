package main;

import lejos.nxt.Button;
import lejos.util.Delay;
import nxt.connection.PCCommunicationManager;
import nxt.connector.ConnectionType;
import nxt.connector.PCConnector;

public class Main{
	
	public static void main(String[] args){
		PCConnector connector = new PCConnector(ConnectionType.BLUETOOTH, new byte[] {1, 2, 3, 4}, true, true);
		PCCommunicationManager man = null;
		while(man == null){
			man = connector.attemptConnection();
			Delay.msDelay(500);
		}
		Delay.msDelay(100);
		int count = 0;
		while(!Button.ESCAPE.isDown()){
			
			if(!man.isAvailable()){
				System.out.println("Connection closed!"); 
				return;
			}
			
			if(Button.LEFT.isDown()){
				man.writeString("Message " + ++count);
				while(Button.LEFT.isDown());
			}
			
			if(man.hasDatafieldUpdate()){
				man.getDatafields();
			}
			Delay.msDelay(10);
		}
		man.close();
	}
}