package main;

import javax.swing.UIManager;

import lejos.util.Delay;
import pc.ui.ConnectToNxtDialog;
import pc.ui.NXTCommunicationFrame;
import pc.ui.SettingsDialog;

/**
 * Mainclass to start the program
 * @author Simon
 */
public class Main{
	
	/**
	 * Mainmethod, starts the program
	 * @param args Startarguments
	 */
	public static void main(String args[]){
		//set default-view to the systemview
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//Load the settings
		SettingsDialog.load();
		
		//Create the frame, but do not show it
		NXTCommunicationFrame frame = new NXTCommunicationFrame();
		frame.setLocationRelativeTo(null);
		
		//Create and show the ConnectToNxtDialog
		ConnectToNxtDialog dialog = new ConnectToNxtDialog(frame);
		dialog.setLocationRelativeTo(null);
//		dialog.setVisible(true);
		
		//While there is no connection (dialog is still visble) just spin
		while(dialog.isVisible()){
			Delay.msDelay(50);
		}
		
		//Final programstartup, make the main-ui visible
		frame.setLocationRelativeTo(dialog); //Setting this frame relative to the dialog, does not work, probably because its not visible anymore
		frame.setVisible(true);
	}
}