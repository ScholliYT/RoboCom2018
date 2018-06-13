package me.tomstein;

import lejos.nxt.Button;
import lejos.nxt.I2CSensor;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;

public class CameraConnection {
	/*
     * The Lego/NXT uses 8-bit I2C addresses in the range 0x02-0xFE, where the low
     * bit is always zero. Many devices (like the Arduino) use 7-bit addresses in
     * the range 0x01-0x7F. To compensate, 7-bit addresses must be shifted left one
     * place (multiplied by 2). This issue cost me hours of frustration.
     */
    private static int ARDUINO_ADDRESS = 0x43 << 1;
    private I2CSensor arduino;
    
    private byte[] buffer = new byte[] {
    		0,0,0,0,
    		0,0,0,0,
    		0,0,0,0};
    
    private int posX, posY, radius;
    
    public int getPosX() {
    	return posX;
    }
    
    public int getPosY() {
    	return posY;
    }
    
    public int getRadius() {
    	return radius;
    }

    
    public CameraConnection(SensorPort sensorPort) {
        arduino = new I2CSensor(sensorPort);
        arduino.setAddress(ARDUINO_ADDRESS);
    }
    
    public boolean refresh() {
        int result = arduino.getData(0x42, buffer, buffer.length);
        
        //LCD.drawString((buffer[0])  + " " + firstval + " " + (firstvalNew), 0, 7);
        if (result == -5) {
            //LCD.drawString("Not connected", 0, 0);
            return false;
        } else if (result == -3) {
            //LCD.drawString("Bus error", 0, 0);
        	return false;
        } else {
            //LCD.drawString("Name: " + arduino.getProductID(), 0, 0);

            // If LeJOS version 0.9, then use this line

            // LCD.drawString("Type: " + arduino.getSensorType(), 0, 1);

            // Else if LeJOS version 0.9.1 and above, then use this line instead of the
            // previous
            //LCD.drawString("Vendor: " + arduino.getVendorID(), 0, 1);

            //LCD.drawString("Version: " + arduino.getVersion(), 0, 2);
            //LCD.drawString("===============", 0, 3);
            //LCD.drawString("Position:", 0, 4);
            posX = ((int)(buffer[0]) << 12) + ((int)(buffer[1]) << 8) + (buffer[2] << 4) + buffer[3];
            posY = ((int)(buffer[4]) << 12) + ((int)(buffer[5]) << 8) + (buffer[6] << 4) + buffer[7];
            radius = ((int)(buffer[8]) << 12) + ((int)(buffer[9]) << 8) + (buffer[10] << 4) + buffer[11];
            //LCD.drawString("X: " + posX, 7, 5);
            //LCD.drawString("Y: " + posY, 7, 6);
            //LCD.drawString("R: " + radius, 7, 7);
            return true;
        }
    }
}
