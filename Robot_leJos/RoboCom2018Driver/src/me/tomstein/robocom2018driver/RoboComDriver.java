package me.tomstein.robocom2018driver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.comm.NXTCommConnector;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.RS485;

import lejos.nxt.*;
import lejos.util.*;
import java.util.*;

public class RoboComDriver {

    private NXTRegulatedMotor motorL, motorR;
    private LightSensor lightsensor;
    private UltrasonicSensor ultrasonicSensor;

    private NXTConnection connectionToNXT_ARM;
    private final String NAME_OF_NXT_ARM = "Rob_5"; // The name of the NXT which controls the Arm
    private DataInputStream dis;  // Stream for data coming from the NXT_ARM
    private DataOutputStream dos; // Stream for data going out to the NXT_ARM

    private final int OK_RECIEVED = 200;
    private final int START_LIFTING = 10;

    private final float STOP_DISTANCE = 15; // Distance to the tennis ball when stop lineFollowing

    // Start Constants for the lineFollowing
    private int speed = 200;
    private float kp = 1.6f;
    private float ki = 0.0f;
    private float kd = 2.0f;
    // End Constants for the lineFollowing

    public static void main(String[] args) {
        new RoboComDriver();
    }

    public RoboComDriver() {
        LCD.clear();
        motorL = new NXTRegulatedMotor(MotorPort.B);
        motorR = new NXTRegulatedMotor(MotorPort.C);
        lightsensor = new LightSensor(SensorPort.S3);
        ultrasonicSensor = new UltrasonicSensor(SensorPort.S2);

        setupRS485Connection();
        lineFollower();
        closeRS485Connection();
    }

    private void setupRS485Connection() {
        LCD.clear();
        LCD.drawString("Name: " + NAME_OF_NXT_ARM, 0, 0);
        LCD.drawString("Type: " + "RS485", 0, 1);
        LCD.drawString("Mode: " + "Packet", 0, 2);
        LCD.drawString("Connecting...", 0, 3);    
        connectionToNXT_ARM = RS485.getConnector().connect(NAME_OF_NXT_ARM, NXTConnection.PACKET); // Start a connection
        if (connectionToNXT_ARM == null) // Check if connection is available
        {
            LCD.drawString("Connect fail", 0, 5);
            Delay.msDelay(2000);
            System.exit(1); // quit system
        }

        LCD.drawString("Connected       ", 0, 3);
        LCD.refresh();

        dis = connectionToNXT_ARM.openDataInputStream();  // Open Stream for incoming data
        dos = connectionToNXT_ARM.openDataOutputStream(); // Open Stream for outgoing data
    }

    private void closeRS485Connection() {
        try
        {
            LCD.drawString("Closing...    ", 0, 3);
            dis.close();
            dos.close();
            connectionToNXT_ARM.close();
        }
        catch (IOException ioe)
        {
            LCD.drawString("Close Exception", 0, 5);
            LCD.refresh();
        }
    }

    /*
    *   Send a int to the NXT_ARM.
    *   returns true if the sending was succsessfull. Otherwise it returns false.
    */
    private boolean sendToNXT_ARM(int i) {
        if(connectionToNXT_ARM == null) {
            throw new RuntimeException("There is no available connection to the NXT_ARM");
        }
        if(dos == null) {
            throw new RuntimeException("The DataOutputStream to the NXT_ARM is not available");   
        }
        try
        {
            LCD.drawString("write: ", 0, 6);
            LCD.drawInt(i, 8, 6, 6);
            dos.writeInt(i);
            dos.flush();
        }
        catch (IOException ioe)
        {
            LCD.drawString("Write Exception", 0, 5);
            return false;
        }

        try
        {
            LCD.drawString("Read: ", 0, 7);
            int read = dis.readInt();
            LCD.drawInt(read, 8, 6, 7);
            if(read == OK_RECIEVED) { 
                return true;        
            } else {
                return false;
            }
        }
        catch (IOException ioe)
        {
            LCD.drawString("Read Exception ", 0, 5);
            return false;
        }
    }

    private boolean ballIsInRange() {
        return ultrasonicSensor.getDistance() <= STOP_DISTANCE;
    }

    private void lineFollower(){
        int target = 50;

        int integral = 0;
        int derivative = 0;
        int lasterror = 0;

        while (!ballIsInRange()) { // Solange ausführen, bis der der Ball gefunden ist
            int readValue = lightsensor.readValue(); // Atuellen Wert des Lichtsensors einlesen

            int error = target - readValue; // Differenz zur Kante berrechnen
            if (error == 0) { // Auf der Kante
                integral = 0; // Integral zurücksetzen
            }

            integral += error;
            derivative = error - lasterror;
            //System.out.println("Error: " + error);
            //System.out.println("Integral: " + integral);
            //System.out.println("Derivative: " + derivative);

            int turn = (int)(kp * error + ki * integral + kd * derivative);
            //System.out.println(turn);
            motorR.setSpeed(speed - turn);
            motorL.setSpeed(speed + turn);
            //System.out.println((speed-turn) + ":" + (speed+turn));
            lasterror = error;
        }
    }

}
