package pc.ui;

import javax.swing.JDialog;
import javax.swing.JFileChooser;

import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import pc.object.SettingsManager;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowEvent;

public class SettingsDialog extends JDialog{
	
	private static final long serialVersionUID = 8837043242901482204L;
	private static SettingsDialog SINGLETONE;
	
	private JTextField txtUserhome;
	private JButton btnSelectLogFolder;
	private JButton btnResetSettings;
	private JButton btnDismiss;
	private JButton btnAdopt;
	private JCheckBox chckbxAutomaticallyCreateLogs;
	private JCheckBox chckbxCreateNewRowWithDialog;
	private JCheckBox chckbxDeleteWithoutDialog;
	private JCheckBox chckbxChangesDirectlyToNxt;
	private JCheckBox chckbxSaveNewSettingsDirectly;
	private JCheckBox chckbxEnableAutoscroll;
	private JCheckBox chckbxUpdateIncremtally;
	private JComboBox<String> cbLanguage;
	
	private JFileChooser fileChooser;
	
	private SettingsManager settings;
	private JCheckBox chckbxArchiveOldLogFiles;
	private JButton btnOpen;
	private JCheckBox chckbxExceptionparsingAktivieren;
	private JButton btnEnterData;
	
	private ExceptionParsingDialog dialogExceptionParsing;
	
	public SettingsDialog(){
		addWindowFocusListener(new WindowFocusListener() {
			public void windowGainedFocus(WindowEvent e){
				if(dialogExceptionParsing.isVisible()){
					dialogExceptionParsing.setVisible(false);
				}
			}
			public void windowLostFocus(WindowEvent e){
			}
		});
		setIconImage(Toolkit.getDefaultToolkit().getImage(SettingsDialog.class.getResource("/resources/settings_icon_16px.png")));
		this.settings = SettingsManager.getSingletone();
		setTitle("Einstellungen");
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setType(Type.POPUP);
		setResizable(false);
		setBounds(100, 100, 450, 504);
		getContentPane().setLayout(null);
		
		btnAdopt = new JButton("\u00DCbernehmen");
		btnAdopt.setToolTipText("\u00DCbernimmt die aktuellen Einstellungen");
		btnAdopt.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				settings.saveCurrentSettings();
				setVisible(false);
			}
		});
		btnAdopt.setBounds(324, 441, 110, 23);
		btnAdopt.setFocusPainted(false);
		getContentPane().add(btnAdopt);
		
		btnDismiss = new JButton("Verwerfen");
		btnDismiss.setToolTipText("Verwirft die neuen Einstellungen");
		btnDismiss.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				setVisible(false);
				loadCurrentSettings();
			}
		});
		btnDismiss.setBounds(210, 441, 104, 23);
		btnDismiss.setFocusPainted(false);
		getContentPane().add(btnDismiss);
		
		JPanel panelGeneral = new JPanel();
		panelGeneral.setBorder(new TitledBorder(null, "Generelles", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelGeneral.setBounds(10, 11, 424, 70);
		getContentPane().add(panelGeneral);
		panelGeneral.setLayout(null);
		
		cbLanguage = new JComboBox<>();
		cbLanguage.setToolTipText("Geben Sie hier die Sprache des Programmes ein");
		cbLanguage.setBounds(83, 11, 331, 20);
		panelGeneral.add(cbLanguage);
		cbLanguage.setEnabled(false);
		cbLanguage.setModel(new DefaultComboBoxModel<String>(new String[] {"Deutsch (Deutschland)"}));
		
		JLabel lblLanguage = new JLabel("Sprache:");
		lblLanguage.setBounds(10, 14, 46, 14);
		panelGeneral.add(lblLanguage);
		
		chckbxSaveNewSettingsDirectly = new JCheckBox("Neue Einstellungen automatisch speichern");
		chckbxSaveNewSettingsDirectly.setEnabled(false);
		chckbxSaveNewSettingsDirectly.setToolTipText("Geben Sie hier an, ob die neuen Einstellungen gespeichert, oder nur f\u00FCr diese Session gelten sollen");
		chckbxSaveNewSettingsDirectly.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				settings.put(settings.SAVE_AUTOMATICALLY_KEY, chckbxSaveNewSettingsDirectly.isSelected() + "");
			}
		});
		chckbxSaveNewSettingsDirectly.setSelected(true);
		chckbxSaveNewSettingsDirectly.setBounds(83, 38, 280, 23);
		chckbxSaveNewSettingsDirectly.setFocusPainted(false);
		panelGeneral.add(chckbxSaveNewSettingsDirectly);
		
		JLabel lblSettings = new JLabel("Einstellungen:");
		lblSettings.setBounds(10, 42, 67, 14);
		panelGeneral.add(lblSettings);
		
		JPanel panelDatafields = new JPanel();
		panelDatafields.setBorder(new TitledBorder(null, "Datenfelder", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelDatafields.setBounds(10, 92, 424, 124);
		getContentPane().add(panelDatafields);
		panelDatafields.setLayout(null);
		
		chckbxChangesDirectlyToNxt = new JCheckBox("\u00C4nderungen automatisch \u00FCbertragen");
		chckbxChangesDirectlyToNxt.setToolTipText("Sollen ver\u00E4nderte Datenfelder automatisch an den NXT \u00FCbertragen werden?");
		chckbxChangesDirectlyToNxt.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				settings.put(settings.UPLOAD_CHANGES_DIRECTLY_KEY, chckbxChangesDirectlyToNxt.isSelected() + "");
			}
		});
		chckbxChangesDirectlyToNxt.setBounds(6, 15, 282, 23);
		chckbxChangesDirectlyToNxt.setFocusPainted(false);
		panelDatafields.add(chckbxChangesDirectlyToNxt);
		
		chckbxDeleteWithoutDialog = new JCheckBox("Eintr\u00E4ge ohne Dialog sofort l\u00F6schen");
		chckbxDeleteWithoutDialog.setToolTipText("Sollen Eintr\u00E4ge ohne Best\u00E4tigungsanfrage sofort gel\u00F6scht werden?");
		chckbxDeleteWithoutDialog.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				settings.put(settings.DELETE_ROWS_DIRECTLY_KEY, chckbxDeleteWithoutDialog.isSelected() + "");
			}
		});
		chckbxDeleteWithoutDialog.setBounds(6, 67, 282, 23);
		chckbxDeleteWithoutDialog.setFocusPainted(false);
		panelDatafields.add(chckbxDeleteWithoutDialog);
		
		chckbxCreateNewRowWithDialog = new JCheckBox("Neue Eintr\u00E4ge mit Dialog erstellen");
		chckbxCreateNewRowWithDialog.setToolTipText("Sollen neue Datenfelder mit Hilfe eines Dialoges erstellt werden?");
		chckbxCreateNewRowWithDialog.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				settings.put(settings.CREATE_ROWS_WITH_DIALOG_KEY, chckbxCreateNewRowWithDialog.isSelected() + "");
			}
		});
		chckbxCreateNewRowWithDialog.setSelected(true);
		chckbxCreateNewRowWithDialog.setBounds(6, 93, 282, 23);
		chckbxCreateNewRowWithDialog.setFocusPainted(false);
		panelDatafields.add(chckbxCreateNewRowWithDialog);
		
		chckbxUpdateIncremtally = new JCheckBox("\u00C4nderungen inkrementell \u00FCbertragen");
		chckbxUpdateIncremtally.setEnabled(false);
		chckbxUpdateIncremtally.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				settings.put(settings.UPLOAD_CHANGES_INCREMENTALLY_KEY, chckbxUpdateIncremtally.isSelected() + "");
			}
		});
		chckbxUpdateIncremtally.setToolTipText("Nur ver\u00E4nderte Datenfelder werden an den NXT geschickt. Hilft, den Datenverkehr und die Verarbeitungsgeschwindigkeit auf dem NXT zu verringern");
		chckbxUpdateIncremtally.setBounds(6, 41, 282, 23);
		chckbxUpdateIncremtally.setFocusPainted(false);
		panelDatafields.add(chckbxUpdateIncremtally);
		
		btnResetSettings = new JButton("Standard wiederherstellen");
		btnResetSettings.setToolTipText("Stellt die Standardeinstellungen wieder her");
		btnResetSettings.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				chckbxSaveNewSettingsDirectly.setSelected(true);
				
				chckbxChangesDirectlyToNxt.setSelected(false);
				chckbxDeleteWithoutDialog.setSelected(false);
				chckbxCreateNewRowWithDialog.setSelected(true);
				
				chckbxAutomaticallyCreateLogs.setSelected(false);
				txtUserhome.setText(System.getProperty("user.home") + "\\nxt\\");
				btnSelectLogFolder.setEnabled(false);
				btnOpen.setEnabled(false);
				
				chckbxEnableAutoscroll.setSelected(true);
				
				settings.put(settings.LANGUAGE_KEY, "german");
				settings.put(settings.SAVE_AUTOMATICALLY_KEY, "true");
				
				settings.put(settings.UPLOAD_CHANGES_DIRECTLY_KEY, "false");
				settings.put(settings.DELETE_ROWS_DIRECTLY_KEY, "false");
				settings.put(settings.CREATE_ROWS_WITH_DIALOG_KEY, "true");
				settings.put(settings.UPLOAD_CHANGES_INCREMENTALLY_KEY, "true");
				
				settings.put(settings.CREATE_AUTOMATIC_LOG_FILES_KEY, "false");
				settings.put(settings.CURRENT_LOG_FOLDER_KEY, System.getProperty("user.home") + "\\nxt\\");
				settings.put(settings.AUTOSCROLL_ACTIVE_KEY, "true");
				settings.put(settings.ARCHIVE_OLD_LOG_FILES_KEY, "true");
				
				settings.put(settings.EXCEPTIONPARSING_ENABLED, "false");
				settings.put(settings.RECENT_EXCEPTION_PARSING_DATA, "");
				
				settings.saveCurrentSettings();
			}
		});
		btnResetSettings.setBounds(10, 441, 190, 23);
		btnResetSettings.setFocusPainted(false);
		getContentPane().add(btnResetSettings);
		
		JPanel panelNxtDebugging = new JPanel();
		panelNxtDebugging.setBorder(new TitledBorder(null, "NXT-Debugging", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelNxtDebugging.setBounds(10, 227, 424, 203);
		getContentPane().add(panelNxtDebugging);
		panelNxtDebugging.setLayout(null);
		
		chckbxAutomaticallyCreateLogs = new JCheckBox("Automatische Logs mit NXT-Daten erstellen");
		chckbxAutomaticallyCreateLogs.setToolTipText("Sollen die vom NXT \u00FCbertragenen Daten automatisch in eine Datei geschrieben werden?");
		chckbxAutomaticallyCreateLogs.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				btnSelectLogFolder.setEnabled(chckbxAutomaticallyCreateLogs.isSelected());
				btnOpen.setEnabled(chckbxAutomaticallyCreateLogs.isSelected());
				settings.put(settings.CREATE_AUTOMATIC_LOG_FILES_KEY, chckbxAutomaticallyCreateLogs.isSelected() + "");
			}
		});
		chckbxAutomaticallyCreateLogs.setBounds(6, 20, 308, 23);
		chckbxAutomaticallyCreateLogs.setFocusPainted(false);
		panelNxtDebugging.add(chckbxAutomaticallyCreateLogs);
		
		JLabel lblLogSavePath = new JLabel("Log-Speicherort:");
		lblLogSavePath.setBounds(16, 53, 80, 14);
		panelNxtDebugging.add(lblLogSavePath);
		
		txtUserhome = new JTextField();
		txtUserhome.setToolTipText("Speicherort f\u00FCr die Logdateien der vom NXT \u00FCbertragenen Daten");
		txtUserhome.setEditable(false);
		txtUserhome.setText(System.getProperty("user.home") + "\\nxt\\");
		txtUserhome.setBounds(106, 50, 308, 20);
		panelNxtDebugging.add(txtUserhome);
		txtUserhome.setColumns(10);
		
		btnSelectLogFolder = new JButton("Speicherort bearbeiten");
		btnSelectLogFolder.setToolTipText("Geben Sie den Pfad f\u00FCr die Logs der vom NXT \u00FCbertragenen Daten an");
		btnSelectLogFolder.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(fileChooser.showDialog(SettingsDialog.this, null) == JFileChooser.APPROVE_OPTION){
					txtUserhome.setText(fileChooser.getSelectedFile().getPath());
					settings.put(settings.CURRENT_LOG_FOLDER_KEY, txtUserhome.getText());
				}
			}
		});
		btnSelectLogFolder.setEnabled(false);
		btnSelectLogFolder.setBounds(241, 81, 173, 23);
		btnSelectLogFolder.setFocusPainted(false);
		panelNxtDebugging.add(btnSelectLogFolder);
		
		JSeparator separatorNxtDebugging = new JSeparator();
		separatorNxtDebugging.setBounds(6, 110, 408, 2);
		panelNxtDebugging.add(separatorNxtDebugging);
		
		chckbxEnableAutoscroll = new JCheckBox("NXT-Debbuging Autoscroll aktiv");
		chckbxEnableAutoscroll.setToolTipText("Soll das Textfeld der vom NXT \u00FCbertragenen Daten automatisch runter scrollen?");
		chckbxEnableAutoscroll.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				settings.put(settings.AUTOSCROLL_ACTIVE_KEY, chckbxEnableAutoscroll.isSelected() + "");
			}
		});
		chckbxEnableAutoscroll.setSelected(true);
		chckbxEnableAutoscroll.setBounds(6, 145, 225, 23);
		chckbxEnableAutoscroll.setFocusPainted(false);
		panelNxtDebugging.add(chckbxEnableAutoscroll);
		
		chckbxArchiveOldLogFiles = new JCheckBox("Alte Logdateien archivieren, um Speicherplatz zu sparen");
		chckbxArchiveOldLogFiles.setToolTipText("Spart Speicherplatz, indem alte Logfiles zusammen in eine Zipdatei geschrieben werden");
		chckbxArchiveOldLogFiles.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				settings.put(settings.ARCHIVE_OLD_LOG_FILES_KEY, chckbxArchiveOldLogFiles.isSelected() + "");
			}
		});
		chckbxArchiveOldLogFiles.setSelected(true);
		chckbxArchiveOldLogFiles.setBounds(6, 119, 333, 23);
		chckbxArchiveOldLogFiles.setFocusPainted(false);
		panelNxtDebugging.add(chckbxArchiveOldLogFiles);
		
		btnOpen = new JButton("Anzeigen...");
		btnOpen.setToolTipText("Zeigt den aktuellen Speicherpfad im System an");
		btnOpen.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try{
					Desktop.getDesktop().open(settings.getCurrentLogFileFolder());
				}catch(IOException e1){
					ExceptionReporter.showDialog(SettingsDialog.this, e1, false);
				}
			}
		});
		btnOpen.setBounds(116, 81, 115, 23);
		btnOpen.setFocusPainted(false);
		panelNxtDebugging.add(btnOpen);
		
		chckbxExceptionparsingAktivieren = new JCheckBox("Exceptionparsing aktivieren");
		chckbxExceptionparsingAktivieren.setToolTipText("Aktiviert die Aufl\u00F6sung von Fehlermeldungen mit den eingegebenen Daten");
		chckbxExceptionparsingAktivieren.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				btnEnterData.setEnabled(chckbxExceptionparsingAktivieren.isSelected());
				settings.put(settings.EXCEPTIONPARSING_ENABLED, chckbxExceptionparsingAktivieren.isSelected() + "");
			}
		});
		chckbxExceptionparsingAktivieren.setBounds(6, 171, 225, 23);
		chckbxExceptionparsingAktivieren.setFocusPainted(false);
		panelNxtDebugging.add(chckbxExceptionparsingAktivieren);
		
		btnEnterData = new JButton("Daten eingeben...");
		btnEnterData.setToolTipText("Daten zur Fehleraufl\u00F6sung eingeben");
		btnEnterData.setEnabled(false);
		btnEnterData.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dialogExceptionParsing.setLocationRelativeTo(SettingsDialog.this);
				SwingUtilities.updateComponentTreeUI(dialogExceptionParsing);
				dialogExceptionParsing.setVisible(true);
			}
		});
		btnEnterData.setBounds(257, 171, 157, 23);
		btnEnterData.setFocusPainted(false);
		panelNxtDebugging.add(btnEnterData);
		
		fileChooser = new JFileChooser(txtUserhome.getText());
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setDialogTitle("Einen Ordner auswählen");
		fileChooser.setApproveButtonText("Okay");
		
		this.dialogExceptionParsing = ExceptionParsingDialog.getSingletone();
		
		loadCurrentSettings();
	}
	
	private void loadCurrentSettings(){
		settings.reload();
		chckbxSaveNewSettingsDirectly.setSelected(settings.getAutmaticallySaveSettings());
		
		chckbxChangesDirectlyToNxt.setSelected(settings.getUploadChangesAutomatically());
		chckbxDeleteWithoutDialog.setSelected(settings.getDeleteRowsWithoutDialog());
		chckbxCreateNewRowWithDialog.setSelected(settings.getCreateNewRowsWithDialog());
		
		chckbxAutomaticallyCreateLogs.setSelected(settings.getCreateLogFilesAutomatically());
		btnOpen.setEnabled(settings.getCreateLogFilesAutomatically());
		btnSelectLogFolder.setEnabled(settings.getCreateLogFilesAutomatically());
		txtUserhome.setText(settings.getCurrentLogFileFolder().getPath());
		chckbxArchiveOldLogFiles.setSelected(settings.getArchiveOldLogFiles());
		
		chckbxEnableAutoscroll.setSelected(settings.getNxtDebuggingAutoscrollActive());
		
		chckbxExceptionparsingAktivieren.setSelected(settings.getExceptionparsingEnabled());
		btnEnterData.setEnabled(settings.getExceptionparsingEnabled());
		this.dialogExceptionParsing.parseRawData(settings.getRecentExceptionParsingData());
	}
	
	public static void showDialog(NXTCommunicationFrame parent){
		if(SINGLETONE == null){
			SINGLETONE = new SettingsDialog();
		}
		SINGLETONE.setLocationRelativeTo(parent);
		SwingUtilities.updateComponentTreeUI(SINGLETONE);
		SINGLETONE.setVisible(true);
	}
	
	public static void load(){
		ensureSingletoneExists();
	}
	
	private static void ensureSingletoneExists(){
		if(SINGLETONE == null){
			SINGLETONE = new SettingsDialog();
		}
	}
}