package pc.out;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * "OutputStream" to send data to the NXT,
 * does not extend any OutputStream, and has only
 * access to methods we use to communicate with our NXT
 * @author Simon
 */
public class PcToNxtOutputStream{
	
	@SuppressWarnings("unused")
	private OutputStream raw; //the "raw" OutputStream
	private BufferedWriter out; //The BufferedWriter we use to send data to the NXT
	
	/**
	 * Construcor to create a NxtToPcInputStream
	 * @param raw The "raw" OutputStream for communication to the NXT
	 */
	public PcToNxtOutputStream(OutputStream raw){
		this.raw = raw;
		this.out = new BufferedWriter(new OutputStreamWriter(raw));
	}
	
	/**
	 * Writes a String to the BufferedWriter
	 * @param write the String that needs to be written
	 * @throws IOException if something went wrong
	 */
	public void write(String write) throws IOException{
		out.write(write + "\n");
	}
	
	/**
	 * Flushes the stream (makes sure, that any buffer is written to the stream)
	 * @throws IOException if something went wrong
	 */
	public void flush() throws IOException{
		out.flush();
	}
	
	/**
	 * Tries to close the stream (for a detailed description, take a look for the description in the close()-Method of the BufferedWriter!)
	 * @throws IOException if something went wrong
	 */
	public void close() throws IOException{
		out.close();
	}
	
}