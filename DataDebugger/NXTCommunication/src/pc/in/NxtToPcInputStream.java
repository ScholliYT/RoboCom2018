package pc.in;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class NxtToPcInputStream{
	
	private InputStream raw;
	private InputStreamReader reader;
	
	public NxtToPcInputStream(InputStream in){
		this.raw = in;
		this.reader = new InputStreamReader(raw);
		
	}
	
	public int read() throws IOException{
		return reader.read();
	}

	public void close() throws IOException{
		reader.close();
	}
	
//	public int available() throws IOException{
//		return raw.available();
//	}
	
}