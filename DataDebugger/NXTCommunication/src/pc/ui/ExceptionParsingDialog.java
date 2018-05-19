package pc.ui;

import javax.swing.JDialog;
import javax.swing.table.DefaultTableModel;

import pc.object.SettingsManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;

public class ExceptionParsingDialog extends JDialog{
	
	private static ExceptionParsingDialog SINGLETONE;
	
	private static final long serialVersionUID = -4940201110897027396L;
	private JTable tableData;
	
	private HashMap<Integer, String> classes, methods;
	private JTextArea textAreaInput;
	private JButton btnAcceptData;
	
	private SettingsManager settings;
	private JButton btnSaveChanges;
	
	public ExceptionParsingDialog(){
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setAlwaysOnTop(true);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setType(Type.POPUP);
		this.settings = SettingsManager.getSingletone();
		this.classes = new HashMap<Integer, String>();
		this.methods = new HashMap<Integer, String>();
		setTitle("Exceptionparsing bearbeiten");
		setResizable(false);
		setBounds(100, 100, 734, 555);
		getContentPane().setLayout(null);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 10, 350, 480);
		getContentPane().add(scrollPane_1);
		
		textAreaInput = new JTextArea();
		textAreaInput.setToolTipText("Hier die\"rohen\" Exceptiondaten eintragen");
		scrollPane_1.setViewportView(textAreaInput);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(370, 9, 350, 480);
		getContentPane().add(scrollPane);
		
		tableData = new JTable(){
			private static final long serialVersionUID = -6818092493009683656L;
			@Override
			public boolean isCellEditable(int row, int column){
				return false;
			}
		};
		tableData.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"ID", "Klassen- oder Methodenname"
			}
		));
		tableData.getColumnModel().getColumn(0).setPreferredWidth(71);
		tableData.getColumnModel().getColumn(1).setPreferredWidth(177);
		tableData.setFillsViewportHeight(true);
		tableData.setRowSelectionAllowed(false);
		scrollPane.setViewportView(tableData);
		
		btnAcceptData = new JButton("Daten \u00FCbersetzen");
		btnAcceptData.setToolTipText("\u00DCbersetzt die \"Rohdaten\" und f\u00FCgt sie zum \u00DCberpr\u00FCfen rechts in die Tabelle ein");
		btnAcceptData.setBounds(10, 492, 350, 23);
		getContentPane().add(btnAcceptData);
		btnAcceptData.setFocusPainted(false);
		btnAcceptData.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				ExceptionParsingDialog.this.parseRawData(textAreaInput.getText().split("\n"));
			}
		});
		
		btnSaveChanges = new JButton("Daten speichern");
		btnSaveChanges.setToolTipText("\u00DCbernimmt die Daten zum \u00DCbersetzen von Exceptions vom NXT");
		btnSaveChanges.setFocusPainted(false);
		btnSaveChanges.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(!textAreaInput.getText().isEmpty()){
					settings.put(settings.RECENT_EXCEPTION_PARSING_DATA, textAreaInput.getText());
				}
				ExceptionParsingDialog.this.setVisible(false);
			}
		});
		btnSaveChanges.setBounds(370, 492, 350, 23);
		getContentPane().add(btnSaveChanges);
		
		parseRawData(settings.getRecentExceptionParsingData().split("\n"));
	}
	
	public void parseRawData(String[] rawData){
		try{
			for(String s: rawData){
				if(s.startsWith("Class")){
					this.classes.put(Integer.parseInt(s.substring(6, s.indexOf(":"))),
							s.substring(s.indexOf(":")+2));
				}else{
					this.methods.put(Integer.parseInt(s.substring(7, s.indexOf(":"))),
							s.substring(s.indexOf(":")+1, (s.contains(")") ? s.indexOf(")") : s.indexOf(">"))) + ((s.contains(")") ? ")" : ">")));
				}
			}
			
			ArrayList<String[]> rows = new ArrayList<String[]>();
			
			for(Entry<Integer, String> entry: classes.entrySet()){
				rows.add(new String[] {"Class " + entry.getKey(), entry.getValue()});
			}
			
			for(Entry<Integer, String> entry: methods.entrySet()){
				rows.add(new String[] {"Method " + entry.getKey(), entry.getValue()});
			}
			
			DefaultTableModel model = new DefaultTableModel();
			
			model.addColumn("ID");
			model.addColumn("Klassen- oder Methodenname");
			
			for(String[] row: rows){
				model.addRow(row);
			}
			
			tableData.setModel(model);
		}catch(Exception ignore){}
		
		if(!textAreaInput.getText().isEmpty()){
			settings.put(settings.RECENT_EXCEPTION_PARSING_DATA, textAreaInput.getText());
			settings.saveCurrentSettings();
		}
	}
	
	public String[] parseException(String[] exceptionLines){
		String buffer = "";
		for(int i = 0; i < exceptionLines.length; i++){
			buffer = exceptionLines[i];
			
			if(buffer.contains("class")){
				String[] data = buffer.replace("class ", "").replace("\n", "").split(":");
				int Class = Integer.parseInt(data[0]);
				buffer = "Exception: " + classes.get(Class) + (data.length == 2 ? ": " + data[1] : "");
			}else{
				String[] ints = buffer.replace(" at: ", "").split(":");
				int method = Integer.parseInt(ints[0]);
				buffer = " at Method: " + methods.get(method) + ", position " + ints[1];
			}
			exceptionLines[i] = buffer;
		}
		return exceptionLines;
	}
	
	public void parseRawData(String data){
		this.parseRawData(data.split("\n"));
	}
	
	public static ExceptionParsingDialog getSingletone(){
		if(SINGLETONE == null){
			SINGLETONE = new ExceptionParsingDialog();
		}
		return SINGLETONE;
	}
}