package main;

import javax.swing.UIManager;

import lejos.util.Delay;
import pc.ui.ConnectToNxtDialog;
import pc.ui.NXTCommunicationFrame;
import pc.ui.SettingsDialog;

public class Main{
	
	public static void main(String args[]){
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception e){
			e.printStackTrace();
		}
		
		SettingsDialog.load();
		
		NXTCommunicationFrame frame = new NXTCommunicationFrame();
		frame.setLocationRelativeTo(null);
		
		ConnectToNxtDialog dialog = new ConnectToNxtDialog(frame);
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
		
		while(dialog.isVisible()){
			Delay.msDelay(50);
		}
		
		frame.setLocationRelativeTo(dialog);
		frame.setVisible(true);
	}
}