package pc.out;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class PcToNxtOutputStream{
	
	@SuppressWarnings("unused")
	private OutputStream raw;
	private BufferedWriter out;
	
	public PcToNxtOutputStream(OutputStream raw){
		this.raw = raw;
		this.out = new BufferedWriter(new OutputStreamWriter(raw));
	}
	
	public void write(String write) throws IOException{
		out.write(write + "\n");
	}
	
	public void flush() throws IOException{
		out.flush();
	}
	
	public void close() throws IOException{
		out.close();
	}
	
}