package nxt.out;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Outputstream zum PC, kein Outputstream im eigentlichen sinne, ist nicht castbar!
 * @author Simon
 *
 */
public class NxtToPcOutputStream{
	
	private OutputStream out;
	private BufferedWriter bw;
	
	/**
	 * Instanziert die Klasse
	 * @param source Der "rohe" Outputstream zum PC
	 */
	protected NxtToPcOutputStream(OutputStream source){
		this.out = source;
		this.bw = new BufferedWriter(new OutputStreamWriter(source));
	}
	
	/**
	 * Schreibt einen String in den Datenstom und sendet diesen somit zum PC, kein autoflush!
	 * @param str der zu sendende String
	 * @throws IOException Falls ein unerwarteter Fehler auftritt
	 */
	public void write(String str) throws IOException{
		bw.write(str);
	}
	
	/**
	 * Flusht den Stream, sogt also dafür, dass alle im Buffer vorhandenen Daten gesendet werden
	 * @throws IOException Falls ein unerwarteter Fehler auftritt
	 */
	public void flush() throws IOException{
		bw.flush();
	}
	
	/**
	 * Schließt den Stream
	 * @throws IOException
	 */
	public void close() throws IOException{
		bw.close();
	}
	
	/**
	 * Gibt den "rohen" Datenstrom zurück
	 * @return der Datenstrom
	 */
	public OutputStream getOutputStream(){
		return out;
	}
	
}