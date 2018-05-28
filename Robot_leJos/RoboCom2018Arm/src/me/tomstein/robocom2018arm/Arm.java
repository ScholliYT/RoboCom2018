package me.tomstein.robocom2018arm;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import lejos.nxt.Motor;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

import lejos.nxt.LCD;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTCommConnector;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.RS485;
import lejos.nxt.comm.USB;
import lejos.util.TextMenu;

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
		NXTConnection con = RS485.getConnector().waitForConnection(0, NXTConnection.PACKET);
		LCD.drawString("Connected...", 0, 2);
		dis = con.openDataInputStream();
		dos = con.openDataOutputStream();

		int n = 0;
		while (true) {
			
			try {
				n = dis.readInt();
				break;
			} catch (IOException e) {
				break;
			}
		}
		LCD.drawString("Read: " + n, 0, 4);
		if(n==1) {
			motor.rotate(25 * 360);
			try {
				dos.writeInt(200);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
