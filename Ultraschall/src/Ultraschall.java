import java.util.ArrayList;
import java.util.Queue;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.util.Delay;

public class Ultraschall{
	
	static int[] array = new int[] {255, 255, 255};
	
	public static void main(String[] args) {
//		System.out.println("abc!");
//		Delay.msDelay(5000);
		UltrasonicSensor sensor = new UltrasonicSensor(SensorPort.S2);
		sensor.continuous();
		
		
		while(!Button.ENTER.isDown());
		while(!Button.ESCAPE.isDown()){
			LCD.clear(0);
			LCD.drawString("Distance: " + sensor.getDistance(), 0, 0);
			
			array[2] = array[1];
			array[1] = array[0];
			array[0] = sensor.getDistance();
			
			int avg = (array[0] + array[1] + array[2]) / 3;
			System.out.println(avg);
//			if(avg <= 25){
//				System.out.println("ERKANNT!");
//				Delay.msDelay(3000);
////				Motor.A.rotate(-120);
//			}
			Delay.msDelay(250);
		}
	}
	
}