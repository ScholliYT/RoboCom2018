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

public class ConnectToNxtDialog extends JFrame{
	
	private static final long serialVersionUID = -382665578594990268L;
	private JRadioButton rdbtnUsbConnection;
	private JRadioButton rdbtnBluetoothConnection;
	private JSpinner spinnerTimeout;
	private JProgressBar pbConnection;
	private JButton btnConnect;
	private JButton btnCancel;
	private ConnectorThread connector;
	private NXTCommunicationFrame parent;
	private JComboBox<String> cbNxtName;
	private SettingsManager settings;
	
	private boolean bluetoothEnabled;
	
	public ConnectToNxtDialog(NXTCommunicationFrame parent){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().getImage(ConnectToNxtDialog.class.getResource("/resources/icon_frame_128x200px.jpg")));
		this.settings = SettingsManager.getSingletone();
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosed(WindowEvent e){
				System.exit(0);
			}
			
			@Override
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}
		});
		this.parent = parent;
		this.connector = null;
		setTitle("Mit NXT verbinden");
		setResizable(false);
//		setModalityType(ModalityType.APPLICATION_MODAL);
//		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setBounds(100, 100, 450, 227);
		getContentPane().setLayout(null);
		
		btnConnect = new JButton("Auf Verbindung warten");
		btnConnect.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(btnConnect.getText().startsWith("Auf")){
					btnConnect.setText("Versuch abbrechen");
					if(!rdbtnUsbConnection.isSelected()){
						btnConnect.setEnabled(false);
					}
					
					rdbtnBluetoothConnection.setEnabled(false);
					rdbtnUsbConnection.setEnabled(false);
					cbNxtName.setEnabled(false);
					
					connector = new ConnectorThread(ConnectToNxtDialog.this, pbConnection, (int) spinnerTimeout.getValue(), cbNxtName.getSelectedItem().toString(), (rdbtnUsbConnection.isSelected() ? ConnectionType.USB : ConnectionType.BLUETOOTH));
					connector.start();
				}else{
					connector.interrupt();
				}
			}
		});
		btnConnect.setToolTipText("Versuchen, zu einem NXT mit den obrigen Einstellungen zu verbinden");
		btnConnect.setFocusPainted(false);
		btnConnect.setBounds(129, 167, 177, 23);
		getContentPane().add(btnConnect);
		
		btnCancel = new JButton("Beenden");
		btnCancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				System.exit(0);
			}
		});
		btnCancel.setToolTipText("Software beenden");
		btnCancel.setFocusPainted(false);
		btnCancel.setBounds(316, 167, 118, 23);
		getContentPane().add(btnCancel);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Verbindungsoptionen", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(10, 11, 424, 145);
		getContentPane().add(panel);
		panel.setLayout(null);
		
		JLabel lblNxtName = new JLabel("NXT-Name:");
		lblNxtName.setBounds(10, 21, 80, 14);
		panel.add(lblNxtName);
		
		JLabel lblConnectionType = new JLabel("Verbindungsmodus:");
		lblConnectionType.setBounds(10, 49, 118, 14);
		panel.add(lblConnectionType);
		
		rdbtnUsbConnection = new JRadioButton("USB-Verbindung");
		rdbtnUsbConnection.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				spinnerTimeout.setEnabled(true);
				reloadRecentNXTData();
			}
		});
		rdbtnUsbConnection.setFocusPainted(false);
		rdbtnUsbConnection.setToolTipText("NXT & PC mithilfe einer USB-Verbindung verbinden");
		rdbtnUsbConnection.setSelected(true);
		rdbtnUsbConnection.setBounds(134, 45, 127, 23);
		panel.add(rdbtnUsbConnection);
		
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
		
		try{
			LocalDevice.getLocalDevice();
			this.bluetoothEnabled = true;
		}catch(BluetoothStateException e1){
			this.bluetoothEnabled = false;
			rdbtnBluetoothConnection.setEnabled(bluetoothEnabled);
			rdbtnBluetoothConnection.setToolTipText("Das lokale Bluetoothgerät konnte nicht gefunden werden.");
		}
		
		ButtonGroup bg = new ButtonGroup();
		bg.add(rdbtnBluetoothConnection);
		bg.add(rdbtnUsbConnection);
		
		JLabel lblTimeout = new JLabel("Timeout:");
		lblTimeout.setBounds(10, 74, 118, 14);
		panel.add(lblTimeout);
		
		spinnerTimeout = new JSpinner();
		spinnerTimeout.setToolTipText("Maximale Laufzeit des Verbindungsvorgangs in Millisekunden. (Nur USB)");
		spinnerTimeout.setModel(new SpinnerNumberModel(5000, 0, 100000, 100));
		spinnerTimeout.setBounds(134, 75, 280, 20);
		panel.add(spinnerTimeout);
		
		pbConnection = new JProgressBar();
		pbConnection.setToolTipText("Aktueller Verbindungsversuchsfortschritt");
		pbConnection.setForeground(Color.GREEN);
		pbConnection.setStringPainted(true);
		pbConnection.setBounds(10, 106, 404, 23);
		panel.add(pbConnection);
		
		cbNxtName = new JComboBox<>();
		cbNxtName.setToolTipText("Den NXT-Namen hier eingeben oder ausw\u00E4hlen");
		cbNxtName.setEditable(true);
		
		cbNxtName.setBounds(134, 18, 280, 20);
		
		panel.add(cbNxtName);
		
		this.setLocationRelativeTo(parent);
		reloadRecentNXTData();
		
	}
	
	public void onSuccess(NXTComm connection){
		this.setVisible(false);
		parent.init(connection.getInputStream(), connection.getOutputStream());
	}
	
	public void resetDialog(){
		pbConnection.setValue(0);
		pbConnection.setIndeterminate(false);
		pbConnection.setStringPainted(false);
		btnConnect.setEnabled(true);
		rdbtnBluetoothConnection.setEnabled(bluetoothEnabled);
		rdbtnUsbConnection.setEnabled(true);
		cbNxtName.setEnabled(true);
		btnConnect.setText("Auf Verbindung warten");
	}
	
	private void reloadRecentNXTData(){
		if(settings.getRecentNXTInfo(rdbtnUsbConnection.isSelected() ? ConnectionType.USB : ConnectionType.BLUETOOTH).length > 0){
			DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
			NXTInfo[] data = settings.getRecentNXTInfo(rdbtnUsbConnection.isSelected() ? ConnectionType.USB : ConnectionType.BLUETOOTH);
			for(NXTInfo nxt: data){
				if(nxt != null){
					model.addElement(nxt.name);
				}
			}
			cbNxtName.setModel(model);
		}else{
			cbNxtName.setModel(new DefaultComboBoxModel<String>(new String[] {"(NXT-Namen eintragen)"}));
		}
	}
	
}