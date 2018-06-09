package nxt.object;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Creates a String out of an Exception
 * @author Simon
 */
public class ExceptionToStringOutputStream extends OutputStream{
	
	private String buffer;
	
	/**
	 * Create a new ExceptionToStringOutputStream
	 */
	public ExceptionToStringOutputStream(){
		this.buffer = "";
	}
	
	/**
	 * Writes a single character to the buffer
	 */
	@Override
	public void write(int b) throws IOException{
		this.buffer += (char) b;
	}
	
	/**
	 * @return the buffer
	 */
	public String getBuffer(){
		String result = buffer.replace('\n', ';');
		this.buffer = "";
		return result;
	}
	
}