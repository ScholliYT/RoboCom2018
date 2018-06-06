package nxt.out;

import java.io.OutputStream;
import java.io.PrintStream;

import lejos.nxt.LCDOutputStream;

/**
 * Leitet die Daten auf den LCD und den übergebenen OutputStream um
 * @author Simon
 */
public class PcPrintStream extends PrintStream{
	
	private boolean lcd;
	private LCDOutputStream lcdOut;
	
	public PcPrintStream(OutputStream out, boolean lcd){
		super(out);
		this.lcd = lcd;
		this.lcdOut = new LCDOutputStream();
	}
	
	@Override
	public void println(String s){
		if(lcd){
			try{
				lcdOut.write(s.getBytes());
				lcdOut.flush();
			}catch(Exception ignore){}
		}
		super.println(s);
	}
	
	@Override
	public void println(char[] v) {
		if(lcd){
			try{
				lcdOut.write(String.valueOf(v).getBytes());
				lcdOut.flush();
			}catch(Exception ignore){}
		}
		super.println(v);
	}
	
}