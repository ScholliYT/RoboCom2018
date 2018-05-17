package pc.ui.Object;

import javax.swing.table.*;

import pc.object.DataFieldType;

import java.util.*;

public class MyFileTableModel extends AbstractTableModel{
	
	private static final long serialVersionUID = -3242633261860549351L;
	
	private String[] columnNames;
	private ArrayList<TableRowObject> content;
	
	public MyFileTableModel(){
		this.columnNames = new String[] {"Feldname", "Datenfeldtyp", "Wert"};
		this.content = new ArrayList<>();
//		addRow("feldname", DataFieldType.STRING, "abc123");
	}
	
	@Override
	public boolean isCellEditable(int row, int column){
		return true;
	}
	
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
		
	}
	
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
	
	@Override
	public int getColumnCount(){
		return columnNames.length;
	}
	
	@Override
	public String getColumnName(int column){
		return columnNames[column];
	}
	
	@Override
	public int getRowCount(){
		return content.size();
	}
	
	public void addRow(String fieldName, DataFieldType type, String value){
		this.content.add(new TableRowObject(fieldName, type, value));
	}
	
	public void deleteRow(int id){
		this.content.remove(id);
	}
	
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

	public boolean isDatafieldNameAvailable(String name){
		Iterator<TableRowObject> it = content.iterator();
		while(it.hasNext()){
			if(it.next().getFieldName().trim().equalsIgnoreCase(name.trim())){
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String toString(){
		String raw = "df!";
		
		for(TableRowObject row: content){
			raw += row.toString();
		}
		return raw;
	}
	
}