package nxt.in;

import java.io.IOException;
import java.io.InputStream;

public class PcToNxtInputStream{
	
	private InputStream in;
	
	public PcToNxtInputStream(InputStream source){
		this.in = source;
	}
	
	public int read() throws IOException{
		return in.read();
	}
	
	public void close() throws IOException{
		in.close(); //Die Methode "close" aus InputStream macht nichts....
	}
	
}