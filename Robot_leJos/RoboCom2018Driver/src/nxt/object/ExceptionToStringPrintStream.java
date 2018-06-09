package nxt.object;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Creates a String out of an Exception. use:
 * <code><br>
 * {some code}<br>
 * }catch(Exception e){ //we try to get a string from e<br>
 * 	ExceptionToStringPrintStream ps = new ExceptionToStringPrintStream(new ExceptionToStringOutputStream());<br>
 * e.printStackTrace(ps);<br>
 * String result = ps.getExcpetionAsString();<br>
 * </code>
 * 
 * @author Simon
 */
public class ExceptionToStringPrintStream extends PrintStream{
	
	private ExceptionToStringOutputStream out;
	
	/**
	 * reate a new ExceptionToStringPrintStream
	 * @param out the ExceptionToStringOutputStream to be used by this instance
	 */
	public ExceptionToStringPrintStream(ExceptionToStringOutputStream out){
		super(out);
		this.out = out;
	}
	
	/**
	 * Prints a String to the underlying stream
	 * @param s the String to be printed
	 */
	@Override
	public void print(String s){
		for(char c: s.toCharArray()){
			try{
				if(c == '\n'){
					out.write((int) ';');
				}else{
					out.write((int) c);
				}
			}catch(IOException e){
				System.out.println("FEHLER!");
			}
		}
	}
	
	/**
	 * Prints a String to the underlying stream and adds a newLine-char at the end of the String
	 * @param s the String to be printed
	 */
	@Override
	public void println(String s){
		print(s);
		print(";");
	}
	
	/**
	 * @return the result of this class, the exception as a String
	 */
	public String getExcpetionAsString(){
		return out.getBuffer();
	}
	
}