package pc.in;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * "InputStream" to read data sent by the NXT,
 * does not extend any inputstream, and has only
 * access to methods we use to communicate with our NXT
 * @author Simon
 */
public class NxtToPcInputStream{
	
	private InputStream raw; //the "raw" inputstream
	private InputStreamReader reader; //a reader we use to read stuff the NXT sent to us
	
	/**
	 * Construcor to create a NxtToPcInputStream
	 * @param in The "raw" inpustream for communication to the NXT
	 */
	public NxtToPcInputStream(InputStream in){
		this.raw = in;
		this.reader = new InputStreamReader(raw);
	}
	
	/**
	 * reads a single character from the stream
	 * @return a single <code>java.lang.Character</code>, in an int-value
	 * @throws IOException if something went wrong while reading a char from the underlying stream
	 */
	public int read() throws IOException{
		return reader.read();
	}
	
	/**
	 * Tries to close the stream (for a detailed description, take a look for the description in the close()-Method of the InputStreamReader!)
	 * @throws IOException if something went wrong
	 */
	public void close() throws IOException{
		reader.close();
	}
	
}