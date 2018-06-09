package pc.ui;

import javax.swing.JDialog;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import pc.object.SettingsManager;
import pc.ui.Object.ExceptionParsingTableCellRenderer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ScrollPaneConstants;

/**
 * Lets the user enter the data he got by the link-verbose option, to make the data more readable<br>
 * From here on, I will not comment every single line, everything should be covered in the previus two frames
 * @author Simon
 *
 */
public class ExceptionParsingDialog extends JDialog{
	
	private static ExceptionParsingDialog SINGLETONE; //Just one instance
	
	private static final long serialVersionUID = -4940201110897027396L; //Just here to remove an annoying warning of eclipse
	private JTable tableData; //The table, which lets the user check the data he/she just added
	private TableColumnModel modelColumn; //The tablecolumnmodel for the table
	
	private HashMap<Integer, String> classes, methods; //Two HashMaps, one for classes and one for methods
	private JTextArea textAreaInput; //The JTextarea, used by the user to just copy the data he/she got by link-verbose into this program
	private JButton btnAcceptData; //Button to resolve the data of the textArea
	private ExceptionParsingPopupMenu popup; //The popupmenu which makes it easier to copy the pasted data into the textarea
	
	private SettingsManager settings; //Current settings
	private JButton btnSaveChanges; //Button to accept the data that was added and to save it
	
	/**
	 * Create the dialog, not accessable from outside
	 */
	private ExceptionParsingDialog(){
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setAlwaysOnTop(true); //This frame will be always on top of our application
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
		textAreaInput.addMouseListener(new MouseAdapter(){ //Mouselistener, used to show our popupmenu
			@Override
			public void mouseClicked(MouseEvent e){
				fireEvent(e);
			}
			@Override
			public void mousePressed(MouseEvent e){
				fireEvent(e);
			}
			@Override
			public void mouseReleased(MouseEvent e){
				fireEvent(e);
			}
			
			/**
			 * Fires the event, because different operating systems have different triggers for popupmenus
			 * @param e the MouseEvent that was fired
			 */
			private void fireEvent(MouseEvent e){
				if(e.isPopupTrigger()){ //Check if the action is the popupmenu trigger for the operating system
					popup.showAt(e.getX(), e.getY()); //Show the dialog on the screen where the mouse currently is
				}
			}
			
		});
		textAreaInput.setToolTipText("Hier die \"rohen\" Exceptiondaten eintragen");
		scrollPane_1.setViewportView(textAreaInput);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(370, 9, 350, 480);
		getContentPane().add(scrollPane);
		
		tableData = new JTable(){ //A customized table
			private static final long serialVersionUID = -6818092493009683656L;
			@Override
			public boolean isCellEditable(int row, int column){ //Make the table non-editable
				return false;
			}
		};
		tableData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableData.setDefaultRenderer(Object.class, new ExceptionParsingTableCellRenderer()); //Setting a custom renderer for the table
		tableData.setModel(new DefaultTableModel( //Set the Model, in this case, just add two columns
			new Object[][] {
			},
			new String[] {
				"ID", "Klassen- oder Methodenname"
			}
		));
		tableData.setAutoCreateColumnsFromModel(false);
		tableData.setRowSelectionAllowed(false); //Disable selection for this table
		tableData.getTableHeader().setReorderingAllowed(false); //Disable reordering of the tablecolumns
		modelColumn = tableData.getColumnModel();
		scrollPane.setViewportView(tableData);
		
		btnAcceptData = new JButton("Daten \u00FCbersetzen");
		btnAcceptData.setToolTipText("\u00DCbersetzt die \"Rohdaten\" und f\u00FCgt sie zum \u00DCberpr\u00FCfen rechts in die Tabelle ein");
		btnAcceptData.setBounds(10, 492, 350, 23);
		getContentPane().add(btnAcceptData);
		btnAcceptData.setFocusPainted(false);
		btnAcceptData.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				ExceptionParsingDialog.this.parseRawData(textAreaInput.getText().split("\n")); //Parse the raw data the user entered and prepare them to be used by this program
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
		
		this.popup = new ExceptionParsingPopupMenu(textAreaInput);
		
		parseRawData(settings.getRecentExceptionParsingData().split("\n"));
	}
	
	/**
	 * Parses raw Data into data that can be used to parse exceptions sent by the NXT
	 * @param rawData The raw link-verbose output, split at any newline character (usually '\n')
	 */
	public void parseRawData(String[] rawData){
		try{
			for(String s: rawData){
				if(s.startsWith("Class ") && !s.contains("records")){
					this.classes.put(Integer.parseInt(s.substring(6, s.indexOf(":"))),
							s.substring(s.indexOf(":")+2));
				}else if(s.startsWith("Method ") && !s.contains("records")){
					this.methods.put(Integer.parseInt(s.substring(7, s.indexOf(":"))),
							s.substring(s.indexOf(":")+1, (s.contains(")") ? s.indexOf(")") : s.indexOf(">"))) + ((s.contains(")") ? ")" : ">")));
				}
			}
			
			ArrayList<String[]> rows = new ArrayList<String[]>();
			
			for(Entry<Integer, String> entry: classes.entrySet()){
				rows.add(new String[] {" Class " + entry.getKey(), " " + entry.getValue()});
			}
			
			for(Entry<Integer, String> entry: methods.entrySet()){
				rows.add(new String[] {" Method " + entry.getKey(), entry.getValue()});
			}
			
			DefaultTableModel model = new DefaultTableModel();
			
			model.addColumn("ID");
			model.addColumn("Klassen- oder Methodenname");
			
			for(String[] row: rows){
				model.addRow(row);
			}
			adjustTablesizeToContent();
			tableData.setModel(model);
		}catch(Exception ignore){}
		
		if(!textAreaInput.getText().isEmpty()){
			settings.put(settings.RECENT_EXCEPTION_PARSING_DATA, textAreaInput.getText());
			settings.saveCurrentSettings();
		}
	}
	
	/**
	 * Resolve an incoming exception from the NXT to an readable Exception
	 * @param exceptionLines the "raw" LeJos-NXT exception
	 * @return The resolved exception, as String[] (each entry is one line)
	 */
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
	
	/**
	 * Parse the raw data of the link-verbose option, without the data beeing split at any new line char
	 * @param data
	 */
	public void parseRawData(String data){
		this.parseRawData(data.split("\n"));
	}
	
	/**
	 * Adjust the columnsize of the table's columns to an optimal size
	 */
	public void adjustTablesizeToContent(){
		tableData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		int maxWidth = 0;
		for(int column = 0; column < modelColumn.getColumnCount(); column++){
			for(int row = 0; row < tableData.getRowCount(); row++){
				TableCellRenderer render = tableData.getCellRenderer(row, column);
				int width = Math.max(tableData.prepareRenderer(render, row, column).getPreferredSize().width, 10);
				if(width > maxWidth){
					maxWidth = width;
					modelColumn.getColumn(column).setPreferredWidth(maxWidth + 5);
				}
			}
		}
	}
	
	
	/**
	 * Get this dialog in order to to stuff with it
	 * @return
	 */
	public static ExceptionParsingDialog getSingletone(){
		if(SINGLETONE == null){
			SINGLETONE = new ExceptionParsingDialog();
		}
		return SINGLETONE;
	}
}