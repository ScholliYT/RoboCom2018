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
import pc.ui.Object.MyFileTableModel;
import pc.ui.Object.MyTableCellRenderer;

import javax.swing.ListSelectionModel;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import java.awt.Color;

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
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.event.MouseMotionAdapter;

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
	private JLabel lblWarning;
	private JPanel panelDataFields;
	private JPanel panelNxtInput;
	private MyTableCellRenderer renderer;
	
	public NXTCommunicationFrame(){
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
		setResizable(false);
		setTitle("NXT Kommunikation");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 828, 545);
		
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
				com.close(true);
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
		//		mntmNewConnection.setIcon(new ImageIcon(NXTCommunicationFrame.class.getResource("/resources/add_new_icon_16px.png")));
				mntmNewConnection.setEnabled(false);
				mntmNewConnection.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						com.close(false);
//				setVisible(false);
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
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		panelNxtInput = new JPanel();
		panelNxtInput.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "NXT-Input", TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panelNxtInput.setBounds(415, 5, 387, 485);
		contentPane.add(panelNxtInput);
		panelNxtInput.setLayout(null);
		
		btnClear = new JButton("L\u00F6schen");
		btnClear.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				textAreaNxtInput.setText("");
			}
		});
		btnClear.setToolTipText("L\u00F6scht das obere Textfeld");
		btnClear.setFocusPainted(false);
		btnClear.setBounds(306, 451, 71, 23);
		panelNxtInput.add(btnClear);
		
		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(10, 447, 367, 2);
		panelNxtInput.add(separator_2);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane_1.setBounds(10, 21, 367, 419);
		panelNxtInput.add(scrollPane_1);
		
		textAreaNxtInput = new JTextPane();
		textAreaNxtInput.setToolTipText("Vom NXT \u00FCbertragene Debug-Daten");
		textAreaNxtInput.setEditable(false);
//		textAreaNxtInput.setWrapStyleWord(true);
//		textAreaNxtInput.setLineWrap(true);
		scrollPane_1.setViewportView(textAreaNxtInput);
		
		btnSave = new JButton("Speichern");
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
						ExceptionReporter.showDialog(NXTCommunicationFrame.this, e1);
					}
				}
			}
		});
		btnSave.setToolTipText("Speichert die oben stehenden Daten in eine Datei");
		btnSave.setFocusPainted(false);
		btnSave.setBounds(217, 451, 79, 23);
		panelNxtInput.add(btnSave);
		
		panelDataFields = new JPanel();
		panelDataFields.setBounds(5, 5, 400, 485);
		panelDataFields.setBorder(new TitledBorder(null, "Datenfelder", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPane.add(panelDataFields);
		panelDataFields.setLayout(null);
		
		btnFireUpdate = new JButton("Fire Update");
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
		btnFireUpdate.setBounds(297, 451, 93, 23);
		panelDataFields.add(btnFireUpdate);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(10, 21, 380, 400);
		panelDataFields.add(scrollPane);
		
		table = new JTable();
		table.addMouseMotionListener(new MouseMotionAdapter(){
			@Override
			public void mouseMoved(MouseEvent me){
				int row = table.rowAtPoint(me.getPoint());
				if(row != renderer.getCurrentHover()){
//					System.out.println("ROW: " + row);
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
		model = new MyFileTableModel(this);
		table.setModel(model);
		table.setDefaultEditor(Object.class, new DataFieldTypeCellEditor(this, model));
		renderer = new MyTableCellRenderer(model);
		table.setDefaultRenderer(Object.class, renderer);
		table.getColumnModel().getColumn(0).setPreferredWidth(106);
		table.getColumnModel().getColumn(1).setPreferredWidth(113);
		table.getColumnModel().getColumn(2).setPreferredWidth(180);
		table.setFillsViewportHeight(true);
		table.getTableHeader().setReorderingAllowed(false);
		scrollPane.setViewportView(table);
		
		btnAddField = new JButton("Datenfeld hinzuf\u00FCgen");
		btnAddField.setToolTipText("F\u00FCgt dem NXT ein neues, \u00FCber den PC einstellbares, Datenfeld hinzu");
		btnAddField.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				showAddNewRowDialog();
			}
		});
		btnAddField.setFocusPainted(false);
		btnAddField.setBounds(10, 451, 135, 23);
		panelDataFields.add(btnAddField);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(10, 447, 380, 2);
		panelDataFields.add(separator_1);
		
		btnLoadNxtSettings = new JButton("Load NXT-Settings");
		btnLoadNxtSettings.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				com.writeString("update");
				LoadNxtSettingsDialog.showDialogAt(NXTCommunicationFrame.this);
				lblWarning.setText("");
			}
		});
		btnLoadNxtSettings.setToolTipText("Lie\u00DFt die aktuellen NXT-Datenfelder neu ein");
		btnLoadNxtSettings.setFocusPainted(false);
		btnLoadNxtSettings.setBounds(155, 451, 132, 23);
		panelDataFields.add(btnLoadNxtSettings);
		
		lblWarning = new JLabel("");
		lblWarning.setHorizontalAlignment(SwingConstants.CENTER);
		lblWarning.setForeground(Color.RED);
		lblWarning.setBounds(10, 426, 380, 14);
		panelDataFields.add(lblWarning);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(5, 486, 797, 2);
		contentPane.add(separator);
		
		popupMenu = new PopupMenu();
		
		if(settings.getCreateLogFilesAutomatically()){
			try{
				logFile = new File(settings.getCurrentLogFileFolder() + "\\" + new SimpleDateFormat("dd.MM.yyyy HH-mm-ss-SSS", Locale.GERMANY).format(new Date()) + ".txt");
				if(!logFile.exists()){
					logFile.getParentFile().mkdirs();
				}
				fos = new FileOutputStream(logFile, logFile.exists());
			}catch(Exception e){
				ExceptionReporter.showDialog(this, e);
			}
		}
	}
	
	public boolean isDatafieldNameAvailable(String name){
		return model.isDatafieldNameAvailable(name);
	}
	
	public void addNewRow(String fieldName, DataFieldType type, Object value){
		model.addRow(fieldName, type, value);
		table.updateUI();
	}
	
	private void writeDisplayMessageToLogFile(String message){
		if(settings.getCreateLogFilesAutomatically()){
			try{
				fos.write(message.getBytes());
			}catch(IOException e){
				ExceptionReporter.showDialog(this, e);
			}
		}
	}
	
	public void displayNxtInput(String input){
		String out = input + LINE_SEPARATOR;
		writeDisplayMessageToLogFile(out);
		addTextToTextPane(out, Color.BLACK, null);
	}
	
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
	
	public void init(InputStream in, OutputStream out){
		this.com = new NXTCommunication(in, out, this);
		com.writeString("update");
	}
	
	public MyFileTableModel getModel(){
		return model;
	}
	
	public JTable getTable(){
		return table;
	}
	
	public void onConnectionClosed(boolean closeApplication){
		this.setVisible(!closeApplication);
		if(!com.isClosed()){
			com.close(closeApplication);
		}
		System.exit(0);
	}
	
	public void uploadCurrentDatafields(){
		com.writeString(model.toString());
	}
	
	public void showAddNewRowDialog(){
		if(settings.getCreateNewRowsWithDialog()){
			NewRowDialog.showDialog(NXTCommunicationFrame.this);
		}else{
			model.addRow("default" + new Random().nextInt(Integer.MAX_VALUE), DataFieldType.STRING, "default");
		}
		table.updateUI();
	}
	
	public void showEditRowDialog(int row){
		NewRowDialog.showDialog(this, (String) model.getValueAt(row, 0), (DataFieldType) model.getValueAt(row, 1), model.getValueAt(row, 2), row);
	}
	
	public void deleteSelectedRows(){
		if(table.getSelectedRows().length > 0){
			int[] selected = table.getSelectedRows();
			if(!settings.getDeleteRowsWithoutDialog()){
				int dialogResult = JOptionPane.showConfirmDialog(NXTCommunicationFrame.this, "M�chten Sie die " + selected.length + " ausgew�hlten Eintr�ge endg�ltig l�schen?", "Datenfelder l�schen?", JOptionPane.YES_NO_OPTION);
//				int dialogResult = JOptionPane.showConfirmDialog(NXTCommunicationFrame.this, "M�chten Sie die " + selected.length + " ausgew�hlten Eintr�ge endg�ltig l�schen?", "Datenfelder l�schen?",
//						JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, new ImageIcon(NXTCommunicationFrame.class.getResource("/resources/delete_icon_15px.png")));
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
	
	public void cancelCurrentTableEdit(){
		table.editingCanceled(null);
	}
	
	public void showWarning(){
		lblWarning.setForeground(Color.RED);
		lblWarning.setText("Obacht! Nicht �bertragene �nderungen!");
	}
	
	private void addTextToTextPane(String str, Color color, String fontFamily){
		StyledDocument doc = textAreaNxtInput.getStyledDocument();
		
		Style style = textAreaNxtInput.addStyle("Color Style", null);
		StyleConstants.setForeground(style, color);
		try{
			doc.insertString(doc.getLength(), (textAreaNxtInput.getText().endsWith("\n") || textAreaNxtInput.getText().isEmpty() ? "" : "\n") + str, style);
		}catch(BadLocationException e){
			ExceptionReporter.showDialog(this, e);
		}
		if(settings.getNxtDebuggingAutoscrollActive()){
			textAreaNxtInput.setCaretPosition(textAreaNxtInput.getDocument().getLength());
		}
	}
	
}

//Altes Entfernen der Datens�tze mit einen Table-Keylistener:
//table.addKeyListener(new KeyAdapter(){
//@Override
//public void keyPressed(KeyEvent e){
//	if(e.getKeyCode() == KeyEvent.VK_DELETE){
//		if(table.getSelectedRows().length > 0){
//			int[] selected = table.getSelectedRows();
//			int dialogResult = JOptionPane.showConfirmDialog(NXTCommunicationFrame.this, "M�chten Sie die " + selected.length + " ausgew�hlten Eintr�ge endg�ltig l�schen?", "Datenfelder l�schen?", JOptionPane.YES_NO_OPTION);
//			if(dialogResult == JOptionPane.OK_OPTION){
//				for(int i = selected.length - 1; i >= 0; i--){ //MUSS r�ckw�rts laufen!
////					System.out.println(selected[i]);
//					model.deleteRow(selected[i]);
//				}
//				table.getSelectionModel().clearSelection();
//				table.updateUI();
//			}
//		}
//	}
//}
//});