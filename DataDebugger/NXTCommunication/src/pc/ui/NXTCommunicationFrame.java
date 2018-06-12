package pc.ui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTable;

import pc.connection.NXTCommunication;
import pc.object.DataFieldType;
import pc.object.SettingsManager;
import pc.ui.Object.DataFieldTypeCellEditor;
import pc.ui.Object.LookAndFeelActionListener;
import pc.ui.Object.LookAndFeelMenuItem;
import pc.ui.Object.MyFileTableModel;
import pc.ui.Object.MyTableCellRenderer;

import javax.swing.ListSelectionModel;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JTextPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.awt.event.InputEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.event.MouseMotionAdapter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JSplitPane;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JRadioButtonMenuItem;

/**
 * The Mainui for this Application. Lets the user interact with all datafields and edit them
 * Also shows Strings sent by the NXT in order to debug the NXT's program
 * @author Simon
 *
 */
public class NXTCommunicationFrame extends JFrame{
	
	private static final long serialVersionUID = 3967436775678112075L;
	
	private final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	private JPanel contentPane;
	private JTable table;
	private JButton btnAddField;
	private JButton btnLoadNxtSettings;
	private JButton btnFireUpdate;
	private JButton btnSave;
	private JButton btnClear;
	private JTextPane textAreaNxtInput;
	private MyFileTableModel model;
	private PopupMenu popupMenu;
	private NXTCommunication com;
	private SettingsManager settings;
	private FileOutputStream fos;
	private File logFile;
	private boolean disposing;
	private JPanel panelDataFields;
	private JPanel panelNxtInput;
	private MyTableCellRenderer renderer;
	private JScrollPane scrollPaneNxtInput;
	private JScrollPane scrollPaneTable;
	private JSplitPane splitPane;
	private JLabel lblWarning;
	private JRadioButtonMenuItem menuItemSystemLookAndFeel;
	private JMenu mnView;
	
	public NXTCommunicationFrame(){
		addComponentListener(new ComponentAdapter(){
			@Override
			public void componentResized(ComponentEvent e){
				if(splitPane != null){
					splitPane.setDividerLocation((int) (splitPane.getSize().getWidth()/2)); //Making sure, that both sides of this Frame (the Table and the NXT-iput textarea) have 50% of the space
				}
			}
		});
		this.disposing = false;
		this.settings = SettingsManager.getSingletone();
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e){
				fireEvent();
			}
			
			@Override
			public void windowClosed(WindowEvent e){
				if(com != null && !com.isClosed()){
					com.close(true);
				}
				System.exit(0);
			}
			
			private void fireEvent(){
				if(com != null && !com.isClosed()){
					com.close(true);
				}
				if(!disposing){
					System.exit(0);
				}
			}
		});
		setIconImage(Toolkit.getDefaultToolkit().getImage(NXTCommunicationFrame.class.getResource("/resources/icon_frame_128x200px.jpg")));
		setTitle("NXT Kommunikation");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 770, 500);
		setMinimumSize(new Dimension(770, 500));
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnDatei = new JMenu("Datei");
		menuBar.add(mnDatei);
		
		JMenuItem mntmEinstellungen = new JMenuItem("Einstellungen");
		mntmEinstellungen.setIcon(new ImageIcon(NXTCommunicationFrame.class.getResource("/resources/settings_icon_16px.png")));
		mntmEinstellungen.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				SettingsDialog.showDialog(NXTCommunicationFrame.this);
			}
		});
		mntmEinstellungen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
		mnDatei.add(mntmEinstellungen);
		
		JSeparator separator_3 = new JSeparator();
		mnDatei.add(separator_3);
		
		JMenuItem mntmBeenden = new JMenuItem("Beenden");
		mntmBeenden.setIcon(new ImageIcon(NXTCommunicationFrame.class.getResource("/resources/shutdown_icon_16px.png")));
		mntmBeenden.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(com != null && !com.isClosed()){
					com.close(true);
				}
				System.exit(0);
			}
		});
		mntmBeenden.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
		mnDatei.add(mntmBeenden);
		
		JMenu mnNxt = new JMenu("NXT");
		menuBar.add(mnNxt);
		
		JMenuItem mntmDatenfelderUpdaten = new JMenuItem("Datenfelder herunterladen");
		mntmDatenfelderUpdaten.setIcon(new ImageIcon(NXTCommunicationFrame.class.getResource("/resources/download_icon_16px.png")));
		mntmDatenfelderUpdaten.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				com.writeString("update");
				LoadNxtSettingsDialog.showDialogAt(NXTCommunicationFrame.this);
			}
		});
		mntmDatenfelderUpdaten.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		mnNxt.add(mntmDatenfelderUpdaten);
		
		JMenu mnComputer = new JMenu("Computer");
		menuBar.add(mnComputer);
		
		JMenuItem mntmNeuesDatenfeld = new JMenuItem("Neues Datenfeld...");
		mntmNeuesDatenfeld.setIcon(new ImageIcon(NXTCommunicationFrame.class.getResource("/resources/add_new_icon_16px.png")));
		mntmNeuesDatenfeld.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				showAddNewRowDialog();
			}
		});
		mntmNeuesDatenfeld.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		mnComputer.add(mntmNeuesDatenfeld);
		
		JMenuItem mntmAusgewhlteDatenfelderEntfernen = new JMenuItem("Ausgew\u00E4hlte Datenfelder entfernen");
		mntmAusgewhlteDatenfelderEntfernen.setIcon(new ImageIcon(NXTCommunicationFrame.class.getResource("/resources/delete_icon_16px.png")));
		mntmAusgewhlteDatenfelderEntfernen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				deleteSelectedRows();
			}
		});
		mntmAusgewhlteDatenfelderEntfernen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		mnComputer.add(mntmAusgewhlteDatenfelderEntfernen);
		
		JSeparator separator_4 = new JSeparator();
		mnComputer.add(separator_4);
		
		JMenuItem mntmNeueDatenfelderUploaden = new JMenuItem("Datenfelder uploaden");
		mntmNeueDatenfelderUploaden.setIcon(new ImageIcon(NXTCommunicationFrame.class.getResource("/resources/upload_icon_16px.png")));
		mntmNeueDatenfelderUploaden.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				uploadCurrentDatafields();
			}
		});
		mntmNeueDatenfelderUploaden.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK));
		mnComputer.add(mntmNeueDatenfelderUploaden);
		
		JMenu mnVerbindung = new JMenu("Verbindung");
		menuBar.add(mnVerbindung);
		
		JMenuItem mntmNewConnection = new JMenuItem("Neue Verbindung...");
		mntmNewConnection.setIcon(new ImageIcon(NXTCommunicationFrame.class.getResource("/resources/connect_icon_16px.png")));
				mntmNewConnection.setEnabled(false);
				mntmNewConnection.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						com.close(false);
						NXTCommunicationFrame frame = new NXTCommunicationFrame();
						frame.setVisible(true);
						setVisible(false);
					}
				});
				mnVerbindung.add(mntmNewConnection);
				
				JSeparator separator_5 = new JSeparator();
				mnVerbindung.add(separator_5);
				
				JMenuItem mntmDisconnect = new JMenuItem("Trennen");
				mntmDisconnect.setIcon(new ImageIcon(NXTCommunicationFrame.class.getResource("/resources/disconnect_icon_16px.png")));
				mntmDisconnect.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						try{
							com.close(true);
						}catch(Exception ignore){}
					}
				});
				mnVerbindung.add(mntmDisconnect);
				
				mnView = new JMenu("Ansicht");
				menuBar.add(mnView);
				
				menuItemSystemLookAndFeel = new JRadioButtonMenuItem("Systemansicht");
				menuItemSystemLookAndFeel.setSelected(true);
				mnView.add(menuItemSystemLookAndFeel);
				
				JSeparator separator = new JSeparator();
				mnView.add(separator);
				
				JMenu mnInformationen = new JMenu("Informationen");
				menuBar.add(mnInformationen);
				
				JMenuItem mntmber = new JMenuItem("\u00DCber dieses Programm");
				mntmber.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e){
						AboutProgramDialog.showDialogAt(NXTCommunicationFrame.this);
					}
				});
				mntmber.setIcon(new ImageIcon(NXTCommunicationFrame.class.getResource("/resources/about_icon_16px.png")));
				mnInformationen.add(mntmber);
		
		loadLookAndFeels();
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		model = new MyFileTableModel(this);
		renderer = new MyTableCellRenderer(model);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		lblWarning = new JLabel("");
		lblWarning.setHorizontalAlignment(SwingConstants.CENTER);
		lblWarning.setForeground(Color.RED);
		contentPane.add(lblWarning);
		
		splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.5);
		splitPane.setOpaque(true);
		splitPane.setBorder(null);
		splitPane.setDividerSize(0);
		
		splitPane.setEnabled(false);
		contentPane.add(splitPane);
		
		panelDataFields = new JPanel();
		splitPane.setLeftComponent(panelDataFields);
		panelDataFields.setBorder(new TitledBorder(null, "Datenfelder", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelDataFields.setLayout(new BorderLayout(0, 0));
		
		scrollPaneTable = new JScrollPane();
		panelDataFields.add(scrollPaneTable, BorderLayout.CENTER);
		scrollPaneTable.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPaneTable.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		table = new JTable();
		table.addMouseMotionListener(new MouseMotionAdapter(){
			@Override
			public void mouseMoved(MouseEvent me){
				int row = table.rowAtPoint(me.getPoint());
				if(row != renderer.getCurrentHover()){
					renderer.setCurrentHover(row);
					table.updateUI();
				}
			}
		});
		table.setToolTipText("Alle Datenfelder werden hier aufgelistet");
		table.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				firePopupMenu(e);
			}
			
			@Override
			public void mouseReleased(MouseEvent e){
				firePopupMenu(e);
			}
			
			private void firePopupMenu(MouseEvent e){
				if(e.isPopupTrigger()){
					table.clearSelection();
					try{
						int row = table.rowAtPoint(e.getPoint());
						table.addRowSelectionInterval(row, row);
					}catch(Exception ignore){}
					popupMenu.showAt(NXTCommunicationFrame.this, table, e.getX(), e.getY());
				}
			}
			
		});
		table.setSurrendersFocusOnKeystroke(true);
		table.setRowHeight(table.getRowHeight() + 9);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setModel(model);
		table.setDefaultEditor(Object.class, new DataFieldTypeCellEditor(this, model));
		table.setDefaultRenderer(Object.class, renderer);
		table.getColumnModel().getColumn(0).setPreferredWidth(106);
		table.getColumnModel().getColumn(1).setPreferredWidth(113);
		table.getColumnModel().getColumn(2).setPreferredWidth(180);
		table.setFillsViewportHeight(true);
		table.getTableHeader().setReorderingAllowed(false);
		scrollPaneTable.setViewportView(table);
		
		JPanel panelTableBottom = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panelTableBottom.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		panelDataFields.add(panelTableBottom, BorderLayout.SOUTH);
		
		btnAddField = new JButton("Datenfeld hinzuf\u00FCgen");
		panelTableBottom.add(btnAddField);
		btnAddField.setToolTipText("F\u00FCgt dem NXT ein neues, \u00FCber den PC einstellbares, Datenfeld hinzu");
		btnAddField.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				showAddNewRowDialog();
			}
		});
		btnAddField.setFocusPainted(false);
		
		btnLoadNxtSettings = new JButton("Load NXT-Settings");
		panelTableBottom.add(btnLoadNxtSettings);
		btnLoadNxtSettings.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				com.writeString("update");
				LoadNxtSettingsDialog.showDialogAt(NXTCommunicationFrame.this);
				lblWarning.setText("");
			}
		});
		btnLoadNxtSettings.setToolTipText("Lie\u00DFt die aktuellen NXT-Datenfelder neu ein");
		btnLoadNxtSettings.setFocusPainted(false);
		
		btnFireUpdate = new JButton("Fire Update");
		panelTableBottom.add(btnFireUpdate);
		btnFireUpdate.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				lblWarning.setForeground(Color.RED);
				lblWarning.setText("Update ausstehend...");
				uploadCurrentDatafields();
				lblWarning.setForeground(Color.BLACK);
				lblWarning.setText("Update erfolgreich ausgef�hrt.");
			}
		});
		btnFireUpdate.setToolTipText("Updatet die oben eingestellten Datenfelder");
		btnFireUpdate.setFocusPainted(false);
		
		panelNxtInput = new JPanel();
		splitPane.setRightComponent(panelNxtInput);
		panelNxtInput.setBorder(new TitledBorder(null, "NXT-Input", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelNxtInput.setLayout(new BorderLayout(0, 0));
		
		JSeparator separator_2 = new JSeparator();
		panelNxtInput.add(separator_2);
		
		scrollPaneNxtInput = new JScrollPane();
		scrollPaneNxtInput.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panelNxtInput.add(scrollPaneNxtInput, BorderLayout.CENTER);
		
		textAreaNxtInput = new JTextPane();
		textAreaNxtInput.setToolTipText("Vom NXT \u00FCbertragene Debug-Daten");
		textAreaNxtInput.setEditable(false);
		scrollPaneNxtInput.setViewportView(textAreaNxtInput);
		
		JPanel panelNxtInputBottom = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panelNxtInputBottom.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		panelNxtInput.add(panelNxtInputBottom, BorderLayout.SOUTH);
		
		btnSave = new JButton("Speichern...");
		panelNxtInputBottom.add(btnSave);
		btnSave.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JFileChooser chooser = new JFileChooser(System.getProperty("user.home"));
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setDialogTitle("Eine Datei ausw�hlen");
				chooser.setApproveButtonText("Okay");
				if(chooser.showDialog(NXTCommunicationFrame.this, null) == JFileChooser.APPROVE_OPTION){
					File f = chooser.getSelectedFile();
					try{
						if(!f.exists()) f.createNewFile();
						
						if(f.canRead() && f.canWrite()){
							FileOutputStream fos = new FileOutputStream(f);
							fos.write(textAreaNxtInput.getText().getBytes());
							fos.flush();
							fos.close();
						}
						
					}catch(Exception e1){
						ExceptionReporter.showDialog(NXTCommunicationFrame.this, e1, true);
					}
				}
			}
		});
		btnSave.setToolTipText("Speichert die oben stehenden Daten in eine Datei");
		btnSave.setFocusPainted(false);
		
		btnClear = new JButton("L\u00F6schen");
		panelNxtInputBottom.add(btnClear);
		btnClear.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				textAreaNxtInput.setText("");
			}
		});
		btnClear.setToolTipText("L\u00F6scht das obere Textfeld");
		btnClear.setFocusPainted(false);
		
		popupMenu = new PopupMenu();
		
		if(settings.getCreateLogFilesAutomatically()){
			try{
				logFile = new File(settings.getCurrentLogFileFolder() + "\\" + new SimpleDateFormat("dd.MM.yyyy HH-mm-ss-SSS", Locale.GERMANY).format(new Date()) + ".txt");
				if(!logFile.exists()){
					logFile.getParentFile().mkdirs();
				}
				fos = new FileOutputStream(logFile, logFile.exists());
			}catch(Exception e){
				ExceptionReporter.showDialog(this, e, false);
			}
		}
	}
	
	/**
	 * Checks for a given DatafieldName to be available
	 * @param name the name we wish to check
	 * @return <code>true</code> if the datafield is available, <code>false</code> otherwise
	 */
	public boolean isDatafieldNameAvailable(String name){
		return model.isDatafieldNameAvailable(name);
	}
	
	/**
	 * Adds a new row to the Datafieldtable
	 * @param fieldName the name of the new datafield
	 * @param type the datafieldtype for the new datafield
	 * @param value the value of the new datafield
	 */
	public void addNewRow(String fieldName, DataFieldType type, Object value){
		model.addRow(fieldName, type, value);
		table.updateUI();
	}
	
	/**
	 * Writes a message to the current logfile, if this is enabled in the settings
	 * @param message the message to write to the logfile
	 */
	private void writeDisplayMessageToLogFile(String message){
		if(settings.getCreateLogFilesAutomatically()){
			try{
				fos.write(message.getBytes());
			}catch(IOException e){
				ExceptionReporter.showDialog(this, e, false);
			}
		}
	}
	
	/**
	 * Displays a String sent by the NXT on our Frame and writes it to the logfile, if enabled
	 * @param input the String sent by the NXT to be displayed on the frame
	 */
	public void displayNxtInput(String input){
		String out = input + LINE_SEPARATOR;
		writeDisplayMessageToLogFile(out);
		addTextToTextPane(out, Color.BLACK, null);
	}
	
	/**
	 * Displays an error sent by the NXT on the frame, if enabled
	 * the Exception will be parsed before it will be displayed
	 * @param input the Exception sent by the NXT
	 */
	public void displayNxtErrorInput(String input){
		String[] traces = input.substring(0, input.length()-1).split(";");
		String buffer = "Auf dem NXT ist ein Fehler aufgetreten:" + LINE_SEPARATOR;
		if(settings.getExceptionparsingEnabled()){
			for(String trace: ExceptionParsingDialog.getSingletone().parseException(traces)){
				buffer += trace + LINE_SEPARATOR;
			}
		}else{
			
			for(String trace: traces){
				buffer += trace + LINE_SEPARATOR;
			}
		}
		addTextToTextPane(buffer, Color.RED, null);
		writeDisplayMessageToLogFile(buffer);
	}
	
	/**
	 * Makes this frame ready to be used and gets the datafields the NXT
	 * wants to get updated by this program
	 * @param in The "raw" InputStream from the NXT
	 * @param out The "raw" OutputStream to the NXT
	 */
	public void init(InputStream in, OutputStream out){
		this.com = new NXTCommunication(in, out, this);
		com.writeString("update");
	}
	
	/**
	 * Getter for our FileTableModel
	 * @return The current FileTableMode we are using
	 */
	public MyFileTableModel getModel(){
		return model;
	}
	
	/**
	 * Getter for our current JTable that is displaying our datafields
	 * @return the current JTable
	 */
	public JTable getTable(){
		return table;
	}
	
	/**
	 * Called automatically, when the connection was closed by the NXT, makes this application ready to exit, and exits is
	 * @param closeApplication curently unused i guess
	 */
	public void onConnectionClosed(boolean closeApplication){
		this.setVisible(!closeApplication);
		if(!com.isClosed()){
			com.close(closeApplication);
		}
		System.exit(0);
	}
	
	/**
	 * Uploads the current datafields on this PC to the NXT
	 */
	public void uploadCurrentDatafields(){
		com.writeString(model.toString());
	}
	
	/**
	 * Shows the NewRowDialog in order to create a new datafield if enabled, or just adds a randomized entry to the table
	 */
	public void showAddNewRowDialog(){
		if(settings.getCreateNewRowsWithDialog()){
			NewRowDialog.showDialog(NXTCommunicationFrame.this);
		}else{
			model.addRow("default" + new Random().nextInt(Integer.MAX_VALUE), DataFieldType.STRING, "default");
		}
		table.updateUI();
	}
	
	/**
	 * Shows a dialog to edit a row of the table
	 * @param row the row to be edited
	 */
	public void showEditRowDialog(int row){
		NewRowDialog.showDialog(this, (String) model.getValueAt(row, 0), (DataFieldType) model.getValueAt(row, 1), model.getValueAt(row, 2), row);
	}
	
	/**
	 * Deletes all selected rows from the table. If enabled, a dialog is shown to let the user confirm the deletion of all selected rows
	 */
	public void deleteSelectedRows(){
		if(table.getSelectedRows().length > 0){
			int[] selected = table.getSelectedRows();
			if(!settings.getDeleteRowsWithoutDialog()){
				int dialogResult = JOptionPane.showConfirmDialog(NXTCommunicationFrame.this, "M�chten Sie die " + selected.length + " ausgew�hlten Eintr�ge endg�ltig l�schen?", "Datenfelder l�schen?", JOptionPane.YES_NO_OPTION);
				if(dialogResult != JOptionPane.OK_OPTION){
					return;
				}
			}
			
			for(int i = selected.length - 1; i >= 0; i--){ //MUSS r�ckw�rts laufen!
				model.deleteRow(selected[i]);
			}
			table.getSelectionModel().clearSelection();
			
			if(settings.getUploadChangesAutomatically()){
				uploadCurrentDatafields();
			}else{
				showWarning();
			}
			
			table.updateUI();
		}
	}
	
	/**
	 * Just cancels the current edit-attempt on the table
	 */
	public void cancelCurrentTableEdit(){
		table.editingCanceled(null);
	}
	
	/**
	 * Shows the warning, that there are not transmitted changes, that need to be sent over to the NXT
	 * (Has currently no affect, because i removed the label, when i made this frame resizable and habe no idea, where i want to place this again
	 */
	public void showWarning(){
		lblWarning.setForeground(Color.RED);
		lblWarning.setText("Obacht! Nicht �bertragene �nderungen!");
	}
	
	/**
	 * Loads all available LookAndFeels and adds them to the Userinterface
	 */
	private void loadLookAndFeels(){
		ButtonGroup bg = new ButtonGroup();
		
		LookAndFeelActionListener listener = new LookAndFeelActionListener(this);
		menuItemSystemLookAndFeel.addActionListener(listener);
		bg.add(menuItemSystemLookAndFeel);
		
		LookAndFeelInfo[] lookAndFeels = UIManager.getInstalledLookAndFeels();
		for(LookAndFeelInfo lookAndFeel: lookAndFeels){
			LookAndFeelMenuItem item = new LookAndFeelMenuItem(lookAndFeel, listener);
			mnView.add(item);
			bg.add(item);
		}
	}
	
	/**
	 * Adds a String to the textpane
	 * @param str the String to be added
	 * @param color the color that the String should be displayed with
	 * @param fontFamily the Font for the new Text in the pane
	 */
	private void addTextToTextPane(String str, Color color, String fontFamily){
		StyledDocument doc = textAreaNxtInput.getStyledDocument();
		
		Style style = textAreaNxtInput.addStyle("Color Style", null);
		StyleConstants.setForeground(style, color);
		try{
			doc.insertString(doc.getLength(), (textAreaNxtInput.getText().endsWith("\n") || textAreaNxtInput.getText().isEmpty() ? "" : "\n") + str, style);
		}catch(BadLocationException e){
			ExceptionReporter.showDialog(this, e, true);
		}
		if(settings.getNxtDebuggingAutoscrollActive()){
			textAreaNxtInput.setCaretPosition(textAreaNxtInput.getDocument().getLength());
		}
	}
	
	/**
	 * Getter for the Splitpane
	 * @return the current splitpane, which holds all our ui-objects
	 */
	public JSplitPane getSplitPane(){
		return splitPane;
	}
	
}