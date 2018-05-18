package pc.ui;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import pc.object.SettingsManager;
import pc.ui.Object.ComponentTitledBorder;

import javax.swing.UIManager;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;

public class ExceptionParsingDialog extends JDialog {
	
	private static ExceptionParsingDialog SINGLETONE;
	
	private static final long serialVersionUID = -4940201110897027396L;
	private JTable tableData;
	
	private HashMap<Integer, String> classes, methods;
	private JTextArea textAreaInput;
	private JButton btnAcceptData;
	
	private SettingsManager settings;
	
	public ExceptionParsingDialog(){
		this.settings = SettingsManager.getSingletone();
		this.classes = new HashMap<Integer, String>();
		this.methods = new HashMap<Integer, String>();
		setTitle("Exceptionparsing bearbeiten");
		setResizable(false);
		setBounds(100, 100, 636, 591);
		getContentPane().setLayout(null);
		
		JCheckBox titleBox = new JCheckBox("Exceptionparsing aktivieren");
		titleBox.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				textAreaInput.setEnabled(titleBox.isSelected());
				tableData.setEnabled(titleBox.isSelected());
				btnAcceptData.setEnabled(titleBox.isSelected());
				
				settings.put(settings.EXCEPTIONPARSING_ENABLED, titleBox.isSelected() + "");
				settings.saveCurrentSettings();
			}
		});
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel.setBorder(new ComponentTitledBorder(titleBox, panel,   new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0))      ));
		panel.setBounds(10, 11, 610, 540);
		getContentPane().add(panel);
		panel.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(301, 23, 299, 506);
		panel.add(scrollPane);
		
		tableData = new JTable();
		tableData.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"ID", "Klassen- oder Methodenname"
			}
		));
		tableData.getColumnModel().getColumn(0).setPreferredWidth(71);
		tableData.getColumnModel().getColumn(1).setPreferredWidth(177);
		tableData.setEnabled(false);
		tableData.setFillsViewportHeight(true);
		tableData.setRowSelectionAllowed(false);
		scrollPane.setViewportView(tableData);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 23, 281, 472);
		panel.add(scrollPane_1);
		
		textAreaInput = new JTextArea();
		textAreaInput.setEnabled(false);
		textAreaInput.setLineWrap(true);
		scrollPane_1.setViewportView(textAreaInput);
		
		btnAcceptData = new JButton("Daten \u00FCbernehmen");
		btnAcceptData.setFocusPainted(false);
		btnAcceptData.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				ExceptionParsingDialog.this.parseRawData(textAreaInput.getText().split("\n"));
			}
		});
		btnAcceptData.setBounds(10, 506, 281, 23);
		panel.add(btnAcceptData);
		
		boolean b = settings.getExceptionparsingEnabled();
		textAreaInput.setEnabled(b);
		tableData.setEnabled(b);
		btnAcceptData.setEnabled(b);
		
		if(b){
			parseRawData(settings.getRecentExceptionParsingData().split("\n"));
		}
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
		
		settings.put(settings.RECENT_EXCEPTION_PARSING_DATA, textAreaInput.getText());
		settings.saveCurrentSettings();
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
	
	public static ExceptionParsingDialog getSingletone(){
		if(SINGLETONE == null){
			SINGLETONE = new ExceptionParsingDialog();
		}
		return SINGLETONE;
	}
	
}