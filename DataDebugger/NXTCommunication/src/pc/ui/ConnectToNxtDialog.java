package pc.ui;

import javax.swing.JFrame;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTInfo;
import pc.connector.ConnectionType;
import pc.connector.ConnectorThread;
import pc.object.SettingsManager;

import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JProgressBar;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.Toolkit;

/**
 * The first Frame our user will see, it is used to connect
 * to any nxt out there<br>
 * Allows the user to enter information needed to connect to the desired NXT
 * @author Simon
 */
public class ConnectToNxtDialog extends JFrame{
	
	private static final long serialVersionUID = -382665578594990268L; //Just here to remove an annoying warning of eclipse
	private JRadioButton rdbtnUsbConnection; //Radiobutton used to show, that we wish to connect with an wired connection
	private JRadioButton rdbtnBluetoothConnection; //Radiobutton used to show, that we wish to connect with an bluetooth connection
	private JSpinner spinnerTimeout; //The spinner, we use to let the user choose, how long the program will try to connect over usb to an NXT
	private JProgressBar pbConnection; //The Progressbar indicates, that the program is still running
	private JButton btnConnect; //The button to start the connect-attempt
	private JButton btnCancel; //The button to cancel any connect-attempt (currently USB-only)
	private ConnectorThread connector; //The Connectorthread, which will try to connect
	private NXTCommunicationFrame parent; //The parentframe
	private JComboBox<String> cbNxtName; //A combobox for showing older NXTs we connected to or enter a new one
	private SettingsManager settings; //Access to the settings
	
	private boolean bluetoothEnabled; //indicates, if we are in the bluetoothmode
	
	/**
	 * Create the dialog
	 * @param parent the Parent (Main-UI)
	 */
	public ConnectToNxtDialog(NXTCommunicationFrame parent){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //When this Frame is closed, the program will terminate
		setIconImage(Toolkit.getDefaultToolkit().getImage(ConnectToNxtDialog.class.getResource("/resources/icon_frame_128x200px.jpg"))); //Sets the icon for this frame
		this.settings = SettingsManager.getSingletone(); //get an instance of the settingsmanager
		addWindowListener(new WindowAdapter(){ //Windowlistener, to make sure, that the program will terminate, when we close this window
			@Override
			public void windowClosed(WindowEvent e){
				System.exit(0);
			}
			
			@Override
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}
		});
		this.parent = parent; //Setting the parent
		this.connector = null; //default connector is null but will be created, when we click on the connect-button
		setTitle("Mit NXT verbinden"); //Seting the title of this frame
		setResizable(false); //Make this Frame non-resizable
		setBounds(100, 100, 450, 227); //Set the size and Location for this frame
		getContentPane().setLayout(null); //set the layout of our contentpane to a final layout
		
		btnConnect = new JButton("Auf Verbindung warten"); //Create the connect-button
		btnConnect.addActionListener(new ActionListener(){ //Add an actionlistner (will be called, when we click the button)
			public void actionPerformed(ActionEvent e){
				if(btnConnect.getText().startsWith("Auf")){ //We use to indicite with the text of this component, wheter the user tries to start an attempt, or cancel it
					btnConnect.setText("Versuch abbrechen"); //We start to connect to an desired nxt
					if(!rdbtnUsbConnection.isSelected()){ //Canceling the attempt is only valid for USB-Connections
						btnConnect.setEnabled(false); //therefore we disable this button, until one bluetooth-attempt has failed
					}
					//disable all components while connecting to the NXT
					rdbtnBluetoothConnection.setEnabled(false);
					rdbtnUsbConnection.setEnabled(false);
					cbNxtName.setEnabled(false);
					
					//Create the connector with our desired NXT-information of the Brick we want to connect to
					connector = new ConnectorThread(ConnectToNxtDialog.this, pbConnection, (int) spinnerTimeout.getValue(), cbNxtName.getSelectedItem().toString(), (rdbtnUsbConnection.isSelected() ? ConnectionType.USB : ConnectionType.BLUETOOTH));
					connector.start(); //start the connect-attempt
				}else{ //the button indicates, that the user wishes to cancel the current connect attempt
					connector.interrupt(); //we interrupt the current connect-attempt
				}
			}
		});
		btnConnect.setToolTipText("Versuchen, zu einem NXT mit den obrigen Einstellungen zu verbinden"); //Set a tooltip for our connect-button
		btnConnect.setFocusPainted(false); //disable focus-paintig for this component (that is just my personal preference)
		btnConnect.setBounds(129, 167, 177, 23); //Setting size and location of this button
		getContentPane().add(btnConnect); //add the button to our frame
		
		btnCancel = new JButton("Beenden"); //Create the exit button
		btnCancel.addActionListener(new ActionListener(){ //Actionlistener for the exit-button
			public void actionPerformed(ActionEvent e){ //Called, when we click our exit-button
				System.exit(0); //Exit this program
			}
		});
		btnCancel.setToolTipText("Software beenden"); //Add a tooltip
		btnCancel.setFocusPainted(false); //disable focus-paintig for this component (that is just my personal preference)
		btnCancel.setBounds(316, 167, 118, 23); //setting the size and location for this button
		getContentPane().add(btnCancel); //add this button to our frame
		
		JPanel panel = new JPanel(); //Create an Panel which will contain all our modificators for creating our connection
		panel.setBorder(new TitledBorder(null, "Verbindungsoptionen", TitledBorder.LEADING, TitledBorder.TOP, null, null)); //Add an titled border for our panel
		panel.setBounds(10, 11, 424, 145); //setting the size and location for this panel
		getContentPane().add(panel); //add this panel to our frame
		panel.setLayout(null); //disable all layoutmanagers for our panel
		
		JLabel lblNxtName = new JLabel("NXT-Name:"); //A label which tells the user that he needs to enter the NXT name into the textfield next to this label
		lblNxtName.setBounds(10, 21, 80, 14); //Setting the size and location for this label
		panel.add(lblNxtName); //add this label to our previous generated panel
		
		JLabel lblConnectionType = new JLabel("Verbindungsmodus:"); //A label which tells the user that he needs to choose, which kind of connection he should use
		lblConnectionType.setBounds(10, 49, 118, 14); //Setting the size and location for this label
		panel.add(lblConnectionType); //add this label to our previous generated panel
		
		rdbtnUsbConnection = new JRadioButton("USB-Verbindung"); //Create the Radiobutton for letting the user choose the USB-Connection
		rdbtnUsbConnection.addActionListener(new ActionListener(){ //add an actionlistener to this radiobutton
			public void actionPerformed(ActionEvent e){ //Will be called, when a user clicks this radiobutton
				spinnerTimeout.setEnabled(true); //Enable the spinner, because we can set a timeout for USB-Connections
				reloadRecentNXTData(); //reload the previous nxts displayed in the JCombobox
			}
		});
		rdbtnUsbConnection.setFocusPainted(false); //disable focus-paintig for this component (that is just my personal preference)
		rdbtnUsbConnection.setToolTipText("NXT & PC mithilfe einer USB-Verbindung verbinden"); //setting a tooltip for this component
		rdbtnUsbConnection.setSelected(true); //this is the default connection, therefore we set this component enabled by default
		rdbtnUsbConnection.setBounds(134, 45, 127, 23); //setting the size and location for this Radiobutton
		panel.add(rdbtnUsbConnection); //add this radiobutton to our panel
		
		//Same procedure, only for a radiobutton which indicates the bluetoothconnection
		rdbtnBluetoothConnection = new JRadioButton("Bluetooth-Verbindung");
		rdbtnBluetoothConnection.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				spinnerTimeout.setEnabled(false);
				reloadRecentNXTData();
			}
		});
		rdbtnBluetoothConnection.setFocusPainted(false);
		rdbtnBluetoothConnection.setToolTipText("NXT & PC mithilfe einer Bluetooth-Verbindung verbinden");
		rdbtnBluetoothConnection.setBounds(263, 45, 151, 23);
		panel.add(rdbtnBluetoothConnection);
		
		try{ //We want to enable the radiobutton for our bluetoothconnection only if the pc supports connection via bluetooth, this is what we try here
			LocalDevice.getLocalDevice(); //Tries to load the local bluetoothdevice, will throw an exception if there is no bluetoothdevice
			this.bluetoothEnabled = true; //No exception indicates, that we have a valid bluetoothdevice we can use for connection with our NXT
		}catch(BluetoothStateException e1){ //There is no valid bluetoothdevice
			this.bluetoothEnabled = false;
			rdbtnBluetoothConnection.setEnabled(bluetoothEnabled); //Disable the radiobutton
			rdbtnBluetoothConnection.setToolTipText("Das lokale Bluetoothger�t konnte nicht gefunden werden."); //Let the user know, why this is disabled
		}
		
		//Create a buttongroup, so only one of the two radiobuttons can be selected at the time
		ButtonGroup bg = new ButtonGroup();
		bg.add(rdbtnBluetoothConnection);
		bg.add(rdbtnUsbConnection);
		
		JLabel lblTimeout = new JLabel("Timeout:"); //A label which tells the user that he needs to enter the USB-timeout
		lblTimeout.setBounds(10, 74, 118, 14); //Setting size and location of this component
		panel.add(lblTimeout); //add this label to our panel
		
		spinnerTimeout = new JSpinner(); //Create the spinner for selection of an timeout for the wired connection
		spinnerTimeout.setToolTipText("Maximale Laufzeit des Verbindungsvorgangs in Millisekunden. (Nur USB)");
		spinnerTimeout.setModel(new SpinnerNumberModel(5000, 0, 100000, 100)); //A spinner model with default value of 5000, min value of 0, max value of 100000 and a stepsize of 100
		spinnerTimeout.setBounds(134, 75, 280, 20); //setting the size and location for this component
		panel.add(spinnerTimeout); //add the component to our panel
		
		pbConnection = new JProgressBar(); //Creating the progressbar
		pbConnection.setToolTipText("Aktueller Verbindungsversuchsfortschritt"); //setting a tooltip
		pbConnection.setForeground(Color.GREEN); //setting the color for the actual bar
		pbConnection.setStringPainted(true); //Show the progress also as an percentage 
		pbConnection.setBounds(10, 106, 404, 23); //setting the size and location
		panel.add(pbConnection); //add this component to our panel
		
		cbNxtName = new JComboBox<>(); //Create the combobox for entering the nxt-name or selection previous nxts
		cbNxtName.setToolTipText("Den NXT-Namen hier eingeben oder ausw\u00E4hlen"); //add a tooltip
		cbNxtName.setEditable(true); //make sure, that the user can enter other names into this combobox (will only be saved, if the connection was successfully initiated, though)
		
		cbNxtName.setBounds(134, 18, 280, 20); //setting size and location of this component
		
		panel.add(cbNxtName); //add this component to our panel
		
		this.setLocationRelativeTo(parent); //set this frame relative to our parentframe
		
		ConnectionType mostRecentConnection = settings.getMostRecentConnection(); //Load the connectiontype we used the last time we connected successfully to an NXT
		if(mostRecentConnection != null){ //Only load, if there is any recent nxt
			if(mostRecentConnection == ConnectionType.USB){ //Last connectiontype was USB
				rdbtnBluetoothConnection.setSelected(false); //make sure, that the USB-radiobutton is selected
				rdbtnUsbConnection.setSelected(true);
			}else{ //The last connection was bluetooth
				rdbtnUsbConnection.setSelected(false); //Make sure, the dialog is configured properly for bluetoothconnections
				rdbtnBluetoothConnection.setSelected(true);
				spinnerTimeout.setEnabled(false);
			}
		}
		
		reloadRecentNXTData(); //reload the recent NXT-data, in order to load the most recent one, and select it
		
		String recentNxt = settings.getMostRecentNxtName(); //load the name of the recent NXT
		if(recentNxt != null && !recentNxt.isEmpty()){ //just try to load the most recent NXT, if it is an actual name
			for(int i = 0; i < cbNxtName.getItemCount(); i++){ //Look in our history, if there is an NXT named as the value we just loaded
				if(cbNxtName.getItemAt(i).equals(recentNxt)){ //When the item at our current positon is the same as the one we are looking for
					cbNxtName.setSelectedIndex(i); //we just select that index
					break; //and cancel this operation, because we just had an positive match
				}
			}
		}
		
	}
	
	/**
	 * Will be called by the connector-thread, when a connection was created successfully
	 * @param connection the new connection to the NXT
	 */
	public void onSuccess(NXTComm connection){
		this.setVisible(false); //Hide this dialog, because it is not needed anymore
		settings.put(settings.MOST_RECENT_CONNECTION_KEY, (rdbtnUsbConnection.isSelected() ? "usb" : "bluetooth")); //Save our most recent connectiontype
		settings.put(settings.MOST_RECENT_NXT_KEY, cbNxtName.getSelectedItem().toString()); //save our most recent NXT name
		settings.saveCurrentSettings(); //save the new entries in our settings
		parent.init(connection.getInputStream(), connection.getOutputStream()); //Initialize the main-frame for this application
	}
	
	/**
	 * Resets this dialog, if the connection was not created successfully
	 */
	public void resetDialog(){
		//reset the Progressbar
		pbConnection.setValue(0);
		pbConnection.setIndeterminate(false);
		pbConnection.setStringPainted(false);
		//Make the connect-button clickable
		btnConnect.setEnabled(true);
		rdbtnBluetoothConnection.setEnabled(bluetoothEnabled);
		rdbtnUsbConnection.setEnabled(true);
		cbNxtName.setEnabled(true);
		btnConnect.setText("Auf Verbindung warten");
	}
	
	/**
	 * Loads the most recent NXTs into our combobox
	 */
	private void reloadRecentNXTData(){
		if(settings.getRecentNXTInfo(rdbtnUsbConnection.isSelected() ? ConnectionType.USB : ConnectionType.BLUETOOTH).length > 0){ //Check, if we have some entries
			DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(); //Create an model for the combobox, which will hold our NXT names
			NXTInfo[] data = settings.getRecentNXTInfo(rdbtnUsbConnection.isSelected() ? ConnectionType.USB : ConnectionType.BLUETOOTH); //load the NXTInfos for the recent NXTs
			for(NXTInfo nxt: data){ //add every entry to our combobox
				if(nxt != null){
					model.addElement(nxt.name); //adding the nxt name to our combobox
				}
			}
			cbNxtName.setModel(model); //update the checkboxes model
		}else{
			cbNxtName.setModel(new DefaultComboBoxModel<String>(new String[] {"(NXT-Namen eintragen)"})); //There were no entries, so we add a default model
		}
	}
	
}