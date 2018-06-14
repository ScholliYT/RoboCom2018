package me.tomstein.robocom2018arm;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;

import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.RS485;
import lejos.util.Delay;

public class Arm {
	private NXTRegulatedMotor motor;
	private DataInputStream dis;
	private DataOutputStream dos;

	public static void main(String[] args) {
		new Arm();
	}
	public Arm() {
		motor = Motor.A;
		this.connect();
	}
	
	private void connect() {
		LCD.drawString("Waiting: ", 0, 1);
		Sound.playTone(2000, 500);
		NXTConnection con = RS485.getConnector().waitForConnection(0, NXTConnection.PACKET);
		LCD.drawString("Connected...", 0, 2);
		Sound.playTone(1000, 200);
		dis = con.openDataInputStream();
		dos = con.openDataOutputStream();

		int n = 0;
		while (true) {
			
			try {
				n = dis.readInt();
				LCD.drawString("Read: " + n, 0, 4);
				if(n==1) {
					motor.rotate((6 * 360) + 120);
					try {
						dos.writeInt(200);
					} catch (IOException e) {
						e.printStackTrace();
					}
					while(!Button.ENTER.isDown());
					break;
				}
			} catch (IOException e) {
				break;
			}
		}
		
	}
}
