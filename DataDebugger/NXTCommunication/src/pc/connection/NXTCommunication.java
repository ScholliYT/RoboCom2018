package pc.connection;

import java.io.InputStream;
import java.io.OutputStream;

import pc.in.NxtToPcInputStreamManager;
import pc.out.PcToNxtOutputStreamManager;
import pc.ui.NXTCommunicationFrame;

public class NXTCommunication{
	
	private InputStream rawInput;
	private OutputStream rawOutput;
	private PcToNxtOutputStreamManager out;
	private NxtToPcInputStreamManager in;
	private NXTCommunicationFrame frame;
	
	public NXTCommunication(InputStream in, OutputStream out, NXTCommunicationFrame frame){
		this.frame = frame;
		this.rawInput = in;
		this.rawOutput = out;
		this.out = new PcToNxtOutputStreamManager(this, rawOutput);
		this.in = new NxtToPcInputStreamManager(this, frame, rawInput);
	}
	
	
	public NxtToPcInputStreamManager getInputStream(){
		return in;
	}
	
	public PcToNxtOutputStreamManager getOutputStream(){
		return out;
	}
	
	public void writeString(String str){
		out.write(str);
	}
	
	public void close(boolean closeApplication){
		in.interrupt();
		out.interrupt();
		
		frame.onConnectionClosed(closeApplication);
	}


	public boolean isClosed(){
		return in.isInterrupted() && out.isInterrupted();
	}
	
}