package me.tomstein;

import lejos.nxt.ColorSensor;
import lejos.nxt.SensorPort;

public class SuperColorSensor extends ColorSensor{
	
	private int zero, hundred;
	
	public SuperColorSensor(){
		super(SensorPort.S3);
		this.zero = 0;
		this.hundred = 0;
		setFloodlight(true);
	}
	
	@Override
	public void calibrateHigh() {
		hundred = port.readRawValue();
	}
	
	@Override
	public void calibrateLow() {
		zero = port.readRawValue();
	}
	
	public int getLightValue(){
		if(hundred == zero) return 0;
		return 100*(port.readRawValue() - zero)/(hundred - zero); 
	}
	
	@Override
	public void setHigh(int high){
		hundred = 1023 - high;
	}
	
	@Override
	public void setLow(int low){
		zero = 1023 - low;
	}
	
}