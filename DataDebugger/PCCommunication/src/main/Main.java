package main;

import lejos.nxt.Button;
import lejos.util.Delay;
import nxt.connection.PCCommunicationManager;
import nxt.connector.ConnectionType;
import nxt.connector.PCConnector;

public class Main{
	
	public static void main(String[] args){
		PCConnector connector = null;
		try{
			connector = new PCConnector(ConnectionType.BLUETOOTH, new byte[] {1, 2, 3, 4}, true, true, new Object[] {"Datenfeldname", "I bims 1 String", "Double", 3.3D,
					"Long", 19811918184L, "Float", 3.3F, "Integer", 33});
		}catch(IllegalArgumentException iae){
			System.out.println(iae.getMessage());
			while(!Button.RIGHT.isDown());
			System.exit(0);
		}
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