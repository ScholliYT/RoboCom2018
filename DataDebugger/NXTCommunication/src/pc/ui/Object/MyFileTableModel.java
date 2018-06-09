package pc.ui.Object;


import pc.object.DataFieldType;
import pc.object.SettingsManager;
import pc.ui.NXTCommunicationFrame;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.table.AbstractTableModel;

/**
 * An custom TableModel for our JTable in the Main-UI. For furhter documentation take a look at {@link AbstractTableModel}
 * @author Simon
 */
public class MyFileTableModel extends AbstractTableModel{
	
	private static final long serialVersionUID = -3242633261860549351L;
	
	private String[] columnNames;
	private NXTCommunicationFrame frame;
	private ArrayList<TableRowObject> content;
	
	/**
	 * Creates a new custom TableModel
	 * @param frame the MainUI instance
	 */
	public MyFileTableModel(NXTCommunicationFrame frame){
		this.frame = frame;
		this.columnNames = new String[] {"Feldname", "Datenfeldtyp", "Wert"};
		this.content = new ArrayList<>();
	}
	
	/**
	 * Indicates, if a specific cell is editable
	 * @param row the row of the cell that needs to be checked
	 * @param column the column of the cell that needs to be checked
	 * @return <code>true</code> in any case
	 */
	@Override
	public boolean isCellEditable(int row, int column){
		return true;
	}
	
	/**
	 * Sets the value for a specific cell given by the parameters. Does not check, if the value is valid
	 * @param aValue the new Value
	 * @param rowIndex the row for the new value
	 * @param columnIndex the column for the new value
	 */
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex){
		TableRowObject obj = content.get(rowIndex);
		
		switch(columnIndex){
			case 0:
				obj.setFieldName((String) aValue);
				break;
			case 1:
				obj.setType((DataFieldType) aValue);
				break;
			case 2:
				obj.setValue(aValue);
				break;
		}
		if(SettingsManager.getSingletone().getUploadChangesAutomatically()){
			frame.uploadCurrentDatafields();
		}
	}
	
	/**
	 * @param columnIndex the columnid that needs to be checked for its class
	 * @return the class used by the specified column
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex){
		if(columnIndex == 1){
			return DataFieldType.class;
		}else if(columnIndex == 0){
			return String.class;
		}else{
			return Object.class;
		}
	}
	
	/**
	 * Returns the count of columns
	 * @erturn usually 3
	 */
	@Override
	public int getColumnCount(){
		return columnNames.length;
	}
	
	/**
	 * Returns the name of a specific column
	 * @param column the column which name needs to be checked
	 * @return the name of the column
	 */
	@Override
	public String getColumnName(int column){
		return columnNames[column];
	}
	
	/**
	 * @return the number of rows the table currently holds
	 */
	@Override
	public int getRowCount(){
		return content.size();
	}
	
	/**
	 * Adds a row to the table with the given values. Does not check for valid values
	 * @param fieldName the name the new field should have
	 * @param type the DataFieldType the new field should have
	 * @param value the value the new field should have
	 */
	public void addRow(String fieldName, DataFieldType type, Object value){
		this.content.add(new TableRowObject(fieldName, type, value));
	}
	
	/**
	 * Deletes a specific row
	 * @param id the rownumber that should be deleted
	 */
	public void deleteRow(int id){
		this.content.remove(id);
	}
	
	/**
	 * Gets a value for a specific cell
	 * @param rowIndex the rowIndex for the cell
	 * @param columnIndex the columnIndex for the cell
	 * @return the value of the specified cell or null if there is no such cell
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex){
		Object value = null;
		TableRowObject obj = content.get(rowIndex);
		
		switch(columnIndex){
			case 0:
				value = obj.getFieldName();
				break;
			case 1:
				value = obj.getType();
				break;
			case 2:
				value = obj.getValue();
				break;
		}
		return value;
	}
	
	/**
	 * Checks, if a datafieldname is valid or already used
	 * @param name the name that needs to be checked
	 * @return <code>true<code> if the name is available, otherwise <code>false</code>
	 */
	public boolean isDatafieldNameAvailable(String name){
		Iterator<TableRowObject> it = content.iterator();
		while(it.hasNext()){
			if(it.next().getFieldName().trim().equalsIgnoreCase(name.trim())){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Converts this tablemodel into a String in order to send it over to the NXT
	 * @return a String representing this object
	 */
	@Override
	public String toString(){
		String raw = "df!";
		
		for(TableRowObject row: content){
			raw += row.toString();
		}
		return raw;
	}
	
}