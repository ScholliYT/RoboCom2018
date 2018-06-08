package pc.connector;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;
import pc.object.SettingsManager;
import pc.ui.ConnectToNxtDialog;
import pc.ui.ExceptionReporter;

/**
 * Class for the Attempt to connect to a NXT
 * @author Simon
 */
public class ConnectorThread extends Thread{
	
	private JProgressBar pb; // A progressbar in order to let the user know, that the program is still working
	private int timeout; //timeout, usb only
	private boolean interrupted; //indicates, if the attempt was interrupted by the user (usb only)
	private String nxtName; //The NXT-name to connect to
	private ConnectionType type; //The Connectiontype to use for connection
	private ConnectToNxtDialog dialog; //The Dialog that lets the user interact with this class
	
	private SettingsManager settings; //The current settings
	
	/**
	 * Creates a new Connector to the NXT
	 * @param dialog The current Dialog
	 * @param pb the current JProgressBar
	 * @param timeout the timeout for the connectionattempt (usb only)
	 * @param nxtName the NXT-name to connect to
	 * @param type the Connectiontype
	 */
	public ConnectorThread(ConnectToNxtDialog dialog, JProgressBar pb, int timeout, String nxtName, ConnectionType type){
		this.settings = SettingsManager.getSingletone();
		this.dialog = dialog;
		this.pb = pb;
		if(type == ConnectionType.USB){
			this.pb.setIndeterminate(false);
			this.pb.setStringPainted(true);
			this.pb.setMaximum(100);
			this.pb.setMinimum(0);
			this.pb.setValue(0);
		}else{
			this.pb.setIndeterminate(true);
			this.pb.setStringPainted(false);
		}
		this.interrupted = false;
		this.timeout = timeout;
		
		if(!nxtName.isEmpty()){ //If the name is empty, we try to connect to any NXT (name = null)
			this.nxtName = nxtName;
		}else{
			this.nxtName = null;
		}
		this.type = type;
	}
	
	/**
	 * Contains the Mainloop of the connector
	 */
	@Override
	public void run(){
		NXTComm connection; //The connection, if it was established correctly
		long start = System.currentTimeMillis(); //Starttime, important for the timeout
		long timeLapsed = 0; //important for the timeout
		while(!interrupted){ //as long as the user has not interrupted the attempt:
			try{
//				connection = NXTCommFactory.createNXTComm(type == ConnectionType.USB ? NXTCommFactory.USB : NXTCommFactory.BLUETOOTH); //
				connection = NXTCommFactory.createNXTComm(type.getId() + 1); //Try to get a NxtCommFactory, in oder to connect to the NXT
				
				NXTInfo loaded = settings.getNXTInfoOf(nxtName, type); //Check and, if available, load if there was a connection to the NXT we try to connect to before, to save some time
				
				if(loaded == null){ //There was no previous connection to the NXT named nxtname
					NXTInfo[] info = connection.search(nxtName); //We try to search for that specific NXT
					if(info.length != 0){ //We found a NXT named as we look for
						NXTInfo nxt = info[0];
						if(nxt == null) continue; //Nope we didn't, try it again
						if(connection.open(nxt)){ //try to open the connection, throws an exception, if failed and also gives a boolean, which indicates success of this method
							settings.addRecentNxtData(nxt.name, nxt.deviceAddress, type); //We add this NXT to the recent NXTs in the settings, in oder to let us connect to this nxt a little bit faster in the future
							dialog.onSuccess(connection); //Let the dialog know that the Connection was established successfully
							return; //We are done here, connection successfull
						}
					}
				}else{ //We do have connected to that NXT before
					loaded.protocol = (type.getId() + 1); //set the protocol we want to use for connection
					if(connection.open(loaded)){ //Try to nopen the connection, indicates with boolean or exception the success of this method
						dialog.onSuccess(connection); ////Let the dialog know that the Connection was established successfully
						return; //We are done here, connection successfull
					}
				}
				
				if(type == ConnectionType.BLUETOOTH){ //Connection to the NXT was not successful
					dialog.resetDialog(); //Resetting the dialog
					return; //Exit this Connector
				}
				
				if(type == ConnectionType.USB){ //Check for timeout, if timeout was not reached, try it again
					timeLapsed = System.currentTimeMillis() - start; //calcualte lapsed time
					if(timeLapsed >= timeout){ //check for timeout
						this.interrupt(); //timeout reached, connector will get interrupted
					}
					pb.setValue((int) (((float) timeLapsed / (float) timeout) * 100)); //update the percentage of the progressbar
				}
			}catch(NXTCommException e){ //Error while connecting to the NXT
				int option = JOptionPane.showConfirmDialog(dialog, "Die Bluetoothverbindung mit dem NXT \"" + nxtName + "\" ist fehlgeschlagen.\n"
						+ "Stellen Sie sicher, dass das Gerät über Bluetooth erreichbar ist und das NXT-Program richtig gestartet wurde.\n"
						+ "Möchten Sie sich den Fehler jetzt anzeigen lassen?",
						"Verbindungsversuch fehlgeschlagen!", JOptionPane.YES_NO_OPTION); //Let the user know, what went wrong and ask him/her, if he/she requests to see the StackTrace
				
				if(option == JOptionPane.YES_OPTION){ //Wants the user to see the Stacktrace?
					ExceptionReporter.showDialog(dialog, e, false); //Show Stacktrace in a Dialog
				}
				
				dialog.resetDialog(); //Reset the Dialog, because the Connection was not successfull
				return; //Exit this thread
			}
		}
		dialog.resetDialog();//Reset the Dialog, because the Connection was not successfull
	}
	
	/**
	 * Sets the interrupted-flag for this thread, lets the Thread exit
	 */
	@Override
	public void interrupt(){
		this.interrupted = true;
	}
	
}