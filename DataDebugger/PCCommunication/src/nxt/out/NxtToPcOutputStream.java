package nxt.out;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class NxtToPcOutputStream{
	
	private BufferedWriter bw;
	
	protected NxtToPcOutputStream(OutputStream source){
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
	
}