package nxt.out;

import java.io.OutputStream;
import java.io.PrintStream;

import lejos.nxt.LCDOutputStream;

/**
 * Redirects the System.out.println-data to the pc, and if desired, to the LCD
 * @author Simon
 */
public class PcPrintStream extends PrintStream{
	
	private boolean lcd;
	private LCDOutputStream lcdOut;
	
	/**
	 * Create a new PcPrintStream
	 * @param out the outputStream used to send data to the Pc
	 * @param lcd boolean, that indicates if data should also be displayed on the NXT's LCD
	 */
	public PcPrintStream(OutputStream out, boolean lcd){
		super(out);
		this.lcd = lcd;
		this.lcdOut = new LCDOutputStream();
	}
	
	/**
	 * Prints a line of text to the PC and, if desired, to the LCD
	 */
	@Override
	public void println(String s){
		if(lcd){
			try{
				lcdOut.write((s + "\n").getBytes());
				lcdOut.flush();
			}catch(Exception ignore){}
		}
		super.println(s);
	}
	
	/**
	 * Prints a line of text to the PC and, if desired, to the LCD
	 */
	@Override
	public void println(char[] v){
		if(lcd){
			try{
				lcdOut.write((String.valueOf(v) + "\n").getBytes());
				lcdOut.flush();
			}catch(Exception ignore){}
		}
		super.println(v);
	}
	
}