package nxt.out;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class NxtToPcOutputStream{
	
	private OutputStream out;
	private BufferedWriter bw;
	
	protected NxtToPcOutputStream(OutputStream source){
		this.out = source;
		this.bw = new BufferedWriter(new OutputStreamWriter(source));
	}
	
	public void write(String str) throws IOException{
		bw.write(str);
	}
	
	public void flush() throws IOException{
		bw.flush();
	}
	
	public void close() throws IOException{
		bw.close();
	}

	public OutputStream getOutputStream(){
		return out;
	}
	
}