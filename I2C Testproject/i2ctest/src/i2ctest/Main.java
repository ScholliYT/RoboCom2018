package i2ctest;

import lejos.nxt.*;

public class Main {
    /*
     * The Lego/NXT uses 8-bit I2C addresses in the range 0x02-0xFE, where the low
     * bit is always zero. Many devices (like the Arduino) use 7-bit addresses in
     * the range 0x01-0x7F. To compensate, 7-bit addresses must be shifted left one
     * place (multiplied by 2). This issue cost me hours of frustration.
     */
    private static int ARDUINO_ADDRESS = 0x43 << 1;

    public static void main(String[] args) {
        I2CSensor arduino = new I2CSensor(SensorPort.S1);
        arduino.setAddress(ARDUINO_ADDRESS);

        byte[] buffer = new byte[12];
        buffer[0] = 0;
        buffer[1] = 0;
        buffer[2] = 0;
        buffer[3] = 0;
        buffer[4] = 0;
        buffer[5] = 0;
        buffer[6] = 0;
        buffer[7] = 0;
        buffer[8] = 0;
        buffer[9] = 0;
        buffer[10] = 0;
        buffer[11] = 0;

        LCD.setAutoRefresh(false);

        while (!Button.ESCAPE.isPressed()) {
            LCD.clear();
            int result = arduino.getData(0x42, buffer, buffer.length);
            
            //LCD.drawString((buffer[0])  + " " + firstval + " " + (firstvalNew), 0, 7);
            if (result == -5) {
                LCD.drawString("Not connected", 0, 0);
            } else if (result == -3) {
                LCD.drawString("Bus error", 0, 0);
            } else {
                LCD.drawString("Name: " + arduino.getProductID(), 0, 0);

                // If LeJOS version 0.9, then use this line

                // LCD.drawString("Type: " + arduino.getSensorType(), 0, 1);

                // Else if LeJOS version 0.9.1 and above, then use this line instead of the
                // previous
                LCD.drawString("Vendor: " + arduino.getVendorID(), 0, 1);

                LCD.drawString("Version: " + arduino.getVersion(), 0, 2);
                LCD.drawString("===============", 0, 3);
                LCD.drawString("Position:", 0, 4);
                int posx = ((int)(buffer[0]) << 12) + ((int)(buffer[1]) << 8) + (buffer[2] << 4) + buffer[3];
                int posy = ((int)(buffer[4]) << 12) + ((int)(buffer[5]) << 8) + (buffer[6] << 4) + buffer[7];
                int radius = ((int)(buffer[8]) << 12) + ((int)(buffer[9]) << 8) + (buffer[10] << 4) + buffer[11];
                LCD.drawString("X: " + posx, 7, 5);
                LCD.drawString("Y: " + posy, 7, 6);
                LCD.drawString("R: " + radius, 7, 7);
            }

            LCD.refresh();
            try {
                Thread.sleep(100);
            } catch (Exception e) {
            }
        }
    }

}
