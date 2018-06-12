package me.tomstein;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.RS485;

import lejos.nxt.*;
import lejos.util.*;
import nxt.connection.NxtDataField;
import nxt.connection.PCCommunicationManager;
import nxt.connector.ConnectionType;
import nxt.connector.PCConnector;

import java.util.*;

public class RoboComDriver implements ButtonListener{
	
	private GoodMotor motorL, motorR;
	private SuperColorSensor lightsensor;
//	private LightSensor lightsensor;
	private UltrasonicSensor ultrasonicSensor;
	
	private NXTConnection connectionToNXT_ARM;
	private final String NAME_OF_NXT_ARM = "Rob_7"; // The name of the NXT which controls the Arm
	private DataInputStream dis; // Stream for data coming from the NXT_ARM
	private DataOutputStream dos; // Stream for data going out to the NXT_ARM
	
	private final int OK_RECIEVED = 200;
//	private final int START_LIFTING = 10;
	private final int STOP_DISTANCE = 25; // Distance to the tennis ball when stop lineFollowing
	private final int MEASUREMENT_COUNT = 5; //Count of Distancemeasurements the program is using to compensate for mistakes
	
	// Start Constants for the lineFollowing
	private int speed = 50;
	private int delay = 5;
	private float kp = 0.6f;
	private float ki = 0.0005f;
	private float kd = 0.5F;
	private float aussenmotorfaktor = 1.00F;
	private int integral = 0;
	// End Constants for the lineFollowing
	private PCCommunicationManager man;
	
	private int[] ultrasonicMeasurements;
	
	private static final boolean DEBUG_ENABLED = true;
	
	public static void main(String[] args){
		Sound.setVolume(100);
		new RoboComDriver();
	}

	public RoboComDriver(){
		this.ultrasonicMeasurements = new int[MEASUREMENT_COUNT];
		
		for(int i = 0; i < ultrasonicMeasurements.length; i++){
			ultrasonicMeasurements[i] = 255;
		}
		
		Button.ESCAPE.addButtonListener(this);
		Button.RIGHT.addButtonListener(this);
		Button.LEFT.addButtonListener(this);
		this.loadSettings();
		
		if(DEBUG_ENABLED){
			PCConnector connector = new PCConnector(ConnectionType.BLUETOOTH, true, true, "speed", speed, "kp", kp, "ki", ki, "kd", kd, "delay", delay, "aussenmotorfaktor", aussenmotorfaktor);
			man = connector.attemptConnection();
			if(man == null) return;
			man.redirectSystemOutputToConnectedPC(true);
		}
		LCD.clear();
		motorL = new GoodMotor(MotorPort.C);
		motorR = new GoodMotor(MotorPort.B);
		
		lightsensor = new SuperColorSensor();
//		lightsensor = new LightSensor(SensorPort.S3);
		lightsensor.setFloodlight(true);
		ultrasonicSensor = new UltrasonicSensor(SensorPort.S2);
		ultrasonicSensor.continuous();
		
		setupRS485Connection();
		// LCD.drawString("Linienverfolgung", 0, 5);
		lineFollower();
		// LCD.drawString("DONE", 0, 6);
		closeRS485Connection();
	}

	private void setupRS485Connection(){
		LCD.clear();
		LCD.drawString("Name: " + NAME_OF_NXT_ARM, 0, 0);
		LCD.drawString("Type: " + "RS485", 0, 1);
		LCD.drawString("Mode: " + "Packet", 0, 2);
		LCD.drawString("Connecting...", 0, 3);
		connectionToNXT_ARM = RS485.getConnector().connect(NAME_OF_NXT_ARM, NXTConnection.PACKET); // Start a connection
		if (connectionToNXT_ARM == null){ // Check if connection is available
			LCD.drawString("Connect fail", 0, 5);
			Delay.msDelay(2000);
			System.exit(1); // quit system
		}
		
		LCD.drawString("Connected       ", 0, 3);
		LCD.refresh();
		
		dis = connectionToNXT_ARM.openDataInputStream(); // Open Stream for incoming data
		dos = connectionToNXT_ARM.openDataOutputStream(); // Open Stream for outgoing data
	}
	
	private void closeRS485Connection(){
		try{
			LCD.drawString("Closing...    ", 0, 3);
			dis.close();
			dos.close();
			connectionToNXT_ARM.close();
		}catch(IOException ioe){
			LCD.drawString("Close Exception", 0, 5);
			LCD.refresh();
		}
	}

	/*
	 * Send a int to the NXT_ARM. returns true if the sending was successfull.
	 * Otherwise it returns false.
	 */
	private boolean sendToNXT_ARM(int i){
		if(connectionToNXT_ARM == null){
			throw new RuntimeException("There is no available connection to the NXT_ARM");
		}
		if(dos == null){
			throw new RuntimeException("The DataOutputStream to the NXT_ARM is not available");
		}
		try{
			LCD.drawString("write: ", 0, 6);
			LCD.drawInt(i, 8, 6, 6);
			dos.writeInt(i);
			dos.flush();
		}catch(IOException ioe){
			LCD.drawString("Write Exception", 0, 5);
			return false;
		}
		
		try{
			LCD.drawString("Read: ", 0, 7);
			int read = dis.readInt();
			LCD.drawInt(read, 8, 6, 7);
			if (read == OK_RECIEVED){
				return true;
			}else{
				return false;
			}
		}catch(IOException ioe){
			LCD.drawString("Read Exception ", 0, 5);
			return false;
		}
	}
	
	private void arrayDurchschieben(int add){
		for(int j = (ultrasonicMeasurements.length-1); j > 0; j--){
			ultrasonicMeasurements[j] = ultrasonicMeasurements[j-1];
		}
		ultrasonicMeasurements[0] = add;
	}
	
	private boolean isBallInRange(){
		arrayDurchschieben(ultrasonicSensor.getDistance());
		int count = 0;
		for(int x: ultrasonicMeasurements){
			count += x;
		}
		return (count / MEASUREMENT_COUNT) <= STOP_DISTANCE;
	}
	
//	private boolean oldBallInRange(){
//		array[2] = array[1];
//		array[1] = array[0];
//		array[0] = ultrasonicSensor.getDistance();
//		
//		int avg = (array[0] + array[1] + array[2]) / 3;
//		
//		return avg <= 25;
//	}
	
	private void calibrateLightSensor(int measurements){
		ArrayList<Integer> weis = new ArrayList<>();
		ArrayList<Integer> schwarz = new ArrayList<>();
		
		for(int i = 1; i <= measurements; i++){
			Delay.msDelay(500);
			System.out.println("Bitte auf WEISS stellen!!");
			while (!Button.ENTER.isDown());
//			weis.add(lightsensor.getRawLightValue());
			weis.add(SensorPort.S3.readRawValue());
			System.out.println("Weiss: " + weis.get(i - 1));
		}
		for(int i = 1; i <= measurements; i++){
			Delay.msDelay(500);
			System.out.println("Bitte auf SCHWARZ stellen!!");
			while(!Button.ENTER.isDown());
//			schwarz.add(lightsensor.getRawLightValue());
			schwarz.add(SensorPort.S3.readRawValue());
			System.out.println("Schwarz: " + schwarz.get(i - 1));
		}
		int mitteweis = 0;
		for (int x : weis){
			mitteweis += x;
		}
//		lightsensor.setHigh(mitteweis / measurements);
		lightsensor.setHundred(mitteweis / measurements);

		int mitteschwarz = 0;
		for(int x : schwarz){
			mitteschwarz += x;
		}
//		lightsensor.setLow(mitteschwarz / measurements);
		lightsensor.setZero(mitteschwarz / measurements);
	}
	
	private void lineFollower(){
		calibrateLightSensor(2);
		
		motorR.forward();
		motorL.forward();
		int target = 50;
		
		
		int derivative = 0;
		int lasterror = 0;
		long time;
		int count = 0;
		while(!isBallInRange()){ // Solange ausführen, bis der der Ball gefunden ist
			time = System.currentTimeMillis();
			int readValue = lightsensor.getLightValue(); // Atuellen Wert des Lichtsensors einlesen
			//System.out.println("Measuered: " + readValue);
			
			int error = target - readValue; // Differenz zur Kante berrechnen
			
			if(error <= 1 && error >= -1){ // Auf der Kante
				integral = 0; // Integral zurücksetzen
			}
			
			integral += error;
			derivative = error - lasterror;
			// System.out.println("Error: " + error);
			// System.out.println("Integral: " + integral);
			// System.out.println("Derivative: " + derivative);
			
			int turn = (int) (kp * error + ki * integral + kd * derivative);
			// System.out.println(turn);
			motorR.setPower((int) (speed - (turn * aussenmotorfaktor)));
			motorL.setPower(speed + turn);
			
			
			// System.out.println((speed-turn) + ":" + (speed+turn));
			lasterror = error;
			
			updateDatafields();
			if(++count % 5 == 0){
				count = 0;
				System.out.println("ReadValue: " + readValue + " Error: " + error + " Right: " + (motorR.getPower()) + " Left: " + (motorL.getPower()));
				/*
				System.out.println("Measuered: " + readValue);
				System.out.println("Error: " + error + "; integral: " + integral + "; derivative: " + derivative
						+ "; lasterror: " + lasterror);
				System.out.println("Right: " + (speed - turn));
				System.out.println("Left: " + (speed + turn));
				System.out.println("Durchlauf: " + (System.currentTimeMillis() - time) + "ms.");
				*/
			}
			try {
				Delay.msDelay(delay - (System.currentTimeMillis() - time));
			}catch(Exception e) {}
		}
		Sound.playTone(2600, 2500);
		motorL.setPower(30);
		motorR.setPower(30);
		Delay.msDelay(3000);
		Motor.A.rotate(-120);
		Delay.msDelay(100);
		motorL.stop();
		motorR.stop();
		this.sendToNXT_ARM(1);
	}

	private void updateDatafields(){
		if(DEBUG_ENABLED && man != null && man.isAvailable() && man.hasDatafieldUpdate()){
			NxtDataField[] df = man.getDatafields();
			
			for(NxtDataField datafield : df){
				interpretAndSetDatafield(datafield);
			}
		}
	}
	
	@Override
	public void buttonPressed(Button b){
		if(b.getId() == Button.ID_ESCAPE) {
			System.exit(0);
		}else if(b.getId() == Button.ID_RIGHT){
			integral = 0;
		}else if(b.getId() == Button.ID_LEFT){
			saveCurrentSettings();
		}
	}
	
	@Override
	public void buttonReleased(Button b){}
	
	private void loadSettings(){
		try{
			for(NxtDataField df: DataSaver.getSingletone().loadSettings()){
				interpretAndSetDatafield(df);
			}
		}catch(Exception ignore){
//			e.printStackTrace();
		}
	}
	
	private void saveCurrentSettings(){
		try{
			DataSaver.getSingletone().saveData("speed", speed, "kp", kp, "ki", ki, "kd", kd, "delay", delay, "aussenmotorfaktor", aussenmotorfaktor);
		}catch(Exception e){
			System.out.println("Could not save settings due to an error!");
		}
	}
	
	private void interpretAndSetDatafield(NxtDataField df){
		String name = df.getName();
		switch (name){
		case "speed":
			this.speed = (int) df.getValue();
			break;
		case "kp":
			this.kp = (float) df.getValue();
			break;
		case "ki":
			this.ki = (float) df.getValue();
			break;
		case "kd":
			this.kd = (float) df.getValue();
			break;
		case "delay":
			this.delay = (int) df.getValue();
			break;
		case "aussenmotorfaktor":
			this.aussenmotorfaktor = (float) df.getValue();
			break;
		default:
			break;
		}
	}
	
}