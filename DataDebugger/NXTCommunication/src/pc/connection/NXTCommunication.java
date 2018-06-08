package pc.connection;

import java.io.InputStream;
import java.io.OutputStream;

import pc.in.NxtToPcInputStreamManager;
import pc.object.ShutdownHook;
import pc.out.PcToNxtOutputStreamManager;
import pc.ui.NXTCommunicationFrame;

/**
 * Maininterface in order to communicate with the NXT
 * @author Simon
 */
public class NXTCommunication{
	
	private InputStream rawInput; //"Raw" inputstream
	private OutputStream rawOutput; //"raw" outputstream
	private PcToNxtOutputStreamManager out; //Manager for sending data to the NXT
	private NxtToPcInputStreamManager in; //Manager for recieving data from the NXT
	private NXTCommunicationFrame frame; //Instance of the Main-ui
	
	/**
	 * Create a new NxtCommunicationinstance in order to communicate properly with the NXT
	 * @param in a "raw" Inputstream from the NXT
	 * @param out a "raw" Outputstream to the NXT
	 * @param frame An instance of the Main-ui
	 */
	public NXTCommunication(InputStream in, OutputStream out, NXTCommunicationFrame frame){
		this.frame = frame;
		this.rawInput = in;
		this.rawOutput = out;
		this.out = new PcToNxtOutputStreamManager(this, rawOutput); //Creating an Outputstreammanager
		this.in = new NxtToPcInputStreamManager(this, frame, rawInput); //Creating an Inputstreammanager
		ShutdownHook.addShutdownHook(this); //Adding a Shutdownhook
	}
	
	/**
	 * Getter for the InputStream for Data recieved from the NXT
	 * @return the NxtToPcInputStreamManager in order to recieve data from the NXT
	 */
	public NxtToPcInputStreamManager getInputStream(){
		return in;
	}
	
	/**
	 * Getter for the OutputStream for Data sent to the NXT
	 * @return the PcToNxtOutputStreamManager in order to send data to the NXT
	 */
	public PcToNxtOutputStreamManager getOutputStream(){
		return out;
	}
	
	/**
	 * Sends a String to the NXT
	 * @param str the String
	 */
	public void writeString(String str){
		out.write(str);
	}
	
	/**
	 * Closes all connections and tells the NXT that the connection was closed
	 * @param closeApplication indicated, wether the program should terminate or not
	 */
	public void close(boolean closeApplication){
		in.interrupt(); //Interrupts the InputStreamManager
		out.interrupt(); //Interrupts the OutputstreamManager
		
		frame.onConnectionClosed(closeApplication); //Closes the frame
	}
	
	/**
	 * Check if the connection is still alive
	 * @return <code>true</code> if the connection is alive, <code>false</code> otherwise
	 */
	public boolean isClosed(){
		return in.isInterrupted() && out.isInterrupted();
	}
	
}