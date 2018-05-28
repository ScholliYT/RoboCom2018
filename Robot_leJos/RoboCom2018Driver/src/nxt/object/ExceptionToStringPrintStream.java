package nxt.object;

import java.io.IOException;
import java.io.PrintStream;

public class ExceptionToStringPrintStream extends PrintStream{
	
	private ExceptionToStringOutputStream out;
	
	public ExceptionToStringPrintStream(ExceptionToStringOutputStream out){
		super(out);
		this.out = out;
	}
	
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
	
	@Override
	public void println(String s){
		print(s);
		print(";");
	}
	
	public String getExcpetionAsString(){
		return out.getBuffer();
	}
	
}