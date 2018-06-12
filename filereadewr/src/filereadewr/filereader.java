package filereadewr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import lejos.nxt.Button;

public class filereader {
	
	private static final File data = new File("robocomSettings.cfg");
	
	public static void main(String[] args){
		try{
			while(!Button.ESCAPE.isDown());
			System.out.println("RUNNING!");
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(data)));
			System.out.println(data.length());
			String buffer = "";
			while((buffer = br.readLine()) != null){
				if(buffer.isEmpty()) {
//					System.out.println("emtpy!");
					break;
				}
				System.out.println(buffer);
			}
			br.close();
			while(!Button.ENTER.isDown());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
