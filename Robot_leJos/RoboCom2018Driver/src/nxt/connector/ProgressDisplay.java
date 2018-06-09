package nxt.connector;

import lejos.nxt.LCD;
import lejos.util.Delay;

/**
 * Shows the progress of the current connect-attempt on the NXT's LCD
 * @author Simon
 *
 */
public class ProgressDisplay extends Thread{
	
	private volatile boolean isConnected;
	
	/**
	 * Create a new Progressdisplay
	 */
	public ProgressDisplay(){
		this.setDaemon(true);
		this.isConnected = false;
	}
	
	@Override
	public void run(){
		LCD.clear();
		LCD.drawString("Verbinden:", 0, 0);
		LCD.drawString("[          ]", 2, 2);
		outer : while(!isConnected){
			for(int step = 0; step < 10; step++){
				if(isConnected) break outer;
				if(step == 0){
					LCD.clear(10 + 2, 2, 1);
				}else{
					LCD.clear(step + 2, 2, 1);
				}
				LCD.drawChar(';', 3 + step, 2);
				Delay.msDelay(250);
			}
		}
		Delay.msDelay(100);
		LCD.clear();
	}
	
	/**
	 * Called, when an connection was established successfully
	 */
	public void connectionEstablished(){
		this.isConnected = true;
	}
	
}