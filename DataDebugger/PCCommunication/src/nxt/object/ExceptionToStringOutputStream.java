package nxt.object;

import java.io.IOException;
import java.io.OutputStream;

public class ExceptionToStringOutputStream extends OutputStream{
	
	private String buffer;
	
	public ExceptionToStringOutputStream(){
		this.buffer = "";
	}
	
	@Override
	public void write(int b) throws IOException{
		this.buffer += (char) b;
	}
	
	public String getBuffer(){
		return buffer;
	}
	
}