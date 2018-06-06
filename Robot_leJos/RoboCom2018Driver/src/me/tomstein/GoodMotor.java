package me.tomstein;

import lejos.nxt.*;

public class GoodMotor extends BasicMotor {

	public GoodMotor(BasicMotorPort port) {
		this.port = port;
		port.setPWMMode(BasicMotorPort.PWM_BRAKE);
	}
}
