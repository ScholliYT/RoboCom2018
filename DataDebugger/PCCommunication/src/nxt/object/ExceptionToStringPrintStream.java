package nxt.object;

import java.io.PrintStream;

public class ExceptionToStringPrintStream extends PrintStream{
	
	private ExceptionToStringOutputStream out;
	
	public ExceptionToStringPrintStream(ExceptionToStringOutputStream out){
		super(out);
		this.out = out;
	}
	
	public String getExcpetionAsString(){
		return out.getBuffer();
	}
	
}