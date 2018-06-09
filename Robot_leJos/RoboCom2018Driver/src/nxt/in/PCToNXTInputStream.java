package nxt.in;

import java.io.IOException;
import java.io.InputStream;

/**
 * "InputStream" to read data sent by the PC,
 * does not extend any inputstream, and has only
 * access to methods we use to communicate with our PC
 * @author Simon
 */
public class PCToNXTInputStream{

	private InputStream in;
	
	/**
	 * Construcor to create a PCToNXTInputStream
	 * @param in The "raw" inpustream for communication to the PC
	 */
	public PCToNXTInputStream(InputStream source){
		this.in = source;
	}
	
	/**
	 * reads a single character from the stream
	 * @return a single <code>java.lang.Character</code>, in an int-value
	 * @throws IOException if something went wrong while reading a char from the underlying stream
	 */
	public int read() throws IOException{
		return in.read();
	}
	
	/**
	 * Tries to close the stream (for a detailed description, take a look for the description in the close()-Method of the InputStream!)
	 * @throws IOException if something went wrong
	 */
	public void close() throws IOException{
		in.close(); // Die Methode "close" aus InputStream macht nichts....
	}
	
}