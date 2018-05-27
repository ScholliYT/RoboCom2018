package pc.ui.Object;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.table.*;

import pc.object.DataFieldType;
import pc.object.SettingsManager;
import pc.ui.NXTCommunicationFrame;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class DataFieldTypeCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener, ChangeListener, DocumentListener{
	
	private static final long serialVersionUID = -6016733647582631554L;
	
	private DataFieldType type;
	private Object value;
	
	private NXTCommunicationFrame frame;
	private MyFileTableModel model;
	
	private int currentRow, currentColumn;
	
	public DataFieldTypeCellEditor(NXTCommunicationFrame frame, MyFileTableModel model){
		this.frame = frame;
		this.model = model;
	}
	
	@Override
	public Object getCellEditorValue(){
		Object result = null;
		if(type != null){
			result = type;
		}else{
			result = value;
		}
		
		if(currentColumn == 1){
			model.setValueAt(type, currentRow, 1);
			ensureValidValueAfterDatatypeChange(currentRow, type);
		}else if(currentColumn == 0){
			if(model.isDatafieldNameAvailable((String) value)){
				result = value;
			}else{
				result = model.getValueAt(currentRow, currentColumn);
			}
		}
		
		value = null;
		type = null;
		
		if(result == null){
			return model.getValueAt(currentRow, currentColumn);
		}
		if(!SettingsManager.getSingletone().getUploadChangesAutomatically()){
			frame.showWarning();
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void actionPerformed(ActionEvent e){
		if(e.getSource() instanceof JComboBox){
			JComboBox<String> box = (JComboBox<String>) e.getSource();
			String value = (String) box.getSelectedItem();
			if(box.getItemCount() > 2){
				box.setFocusable(false);
				switch(value){
					case "String":
						this.type = DataFieldType.STRING;
						break;
					case "Integer":
						this.type = DataFieldType.INTEGER;
						break;
					case "Long":
						this.type = DataFieldType.LONG;
						break;
					case "Double":
						this.type = DataFieldType.DOUBLE;
						break;
					case "Float":
						this.type = DataFieldType.FLOAT;
						break;
					case "Boolean":
						this.type = DataFieldType.BOOLEAN;
						break;
					default:
						this.type = DataFieldType.STRING;
						break;
				}
			}else{
				this.value = Boolean.parseBoolean(value.toLowerCase());
			}
		}else if(e.getSource() instanceof JTextField){
			this.value = ((JTextField) e.getSource()).getText();
		}else if(e.getSource() instanceof JSpinner){
			JSpinner spinner = (JSpinner) e.getSource();
			Number nbr = (Number) spinner.getValue();
			this.value = nbr;
		}else if(e.getSource() instanceof JCheckBox){
			this.value = ((JCheckBox) e.getSource()).isSelected();
		}
	}
	
	@Override
	public void changedUpdate(DocumentEvent e){
		fireDocumentEvent(e);
	}
	
	@Override
	public void insertUpdate(DocumentEvent e){
		fireDocumentEvent(e);
	}
	
	@Override
	public void removeUpdate(DocumentEvent e){
		fireDocumentEvent(e);
	}
	
	private void fireDocumentEvent(DocumentEvent e){
		this.value = ((JTextField) (e.getDocument().getProperty("me"))).getText();
	}
	
	@Override
	public void stateChanged(ChangeEvent e){
		JSpinner spinner = (JSpinner) e.getSource();
		this.value = "" + (Number) spinner.getValue();
	}
	
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column){
		this.currentRow = row;
		this.currentColumn = column;
		if(value instanceof DataFieldType){
			this.type = (DataFieldType) value;
		}
		if(column == 1){
			JComboBox<String> box = new JComboBox<>(new String[] {"String", "Integer", "Long", "Double", "Float", "Boolean"});
			
			if(type != null){
				switch(type){
					case STRING:
						box.setSelectedIndex(0);
						break;
					case INTEGER:
						box.setSelectedIndex(1);
						break;
					case LONG:
						box.setSelectedIndex(2);
						break;
					case DOUBLE:
						box.setSelectedIndex(3);
						break;
					case FLOAT:
						box.setSelectedIndex(4);
						break;
					case BOOLEAN:
						box.setSelectedIndex(5);
						break;
				}
			}
			box.addActionListener(this);
			return box;
		}else if(column == 0){
			this.value = (String) value;
			JTextField field = new JTextField((String) value);
			field.addActionListener(this);
			field.getDocument().putProperty("me", field);
			field.getDocument().addDocumentListener(this);
			return field;
		}else{
			this.value = value;
			DataFieldType type = (DataFieldType) model.getValueAt(row, 1);
			JSpinner spinner = null;
			switch(type){
				case STRING:
					JTextField field = new JTextField(value + "");
					field.addActionListener(this);
					field.getDocument().putProperty("me", field);
					field.getDocument().addDocumentListener(this);
					return field;
				case INTEGER:
					spinner = new JSpinner(new SpinnerNumberModel(parseInt(value), null, null, 1));
					break;
				case LONG:
					spinner = new JSpinner(new SpinnerNumberModel(parseLong(value), null, null, 1));
					break;
				case FLOAT:
					spinner = new JSpinner(new SpinnerNumberModel(parseFloat(value), null, null, 0.1));
					break;
				case DOUBLE:
					spinner = new JSpinner(new SpinnerNumberModel(parseDouble(value), null, null, 0.1));
					break;
				case BOOLEAN:
					JComboBox<String> box = new JComboBox<>(new String[] {"True", "False"});
					box.setFocusable(false);
					box.addActionListener(this);
					boolean b = Boolean.parseBoolean((model.getValueAt(row, 2) + "").toLowerCase());
					box.setSelectedIndex(b ? 0 : 1);
					return box;
			}
			spinner.addChangeListener(this);
			return spinner;
		}
	}
	
	@Override
	public boolean isCellEditable(EventObject e){
		if(super.isCellEditable(e) && e instanceof MouseEvent){
			MouseEvent me = (MouseEvent) e;
			return me.getClickCount() >= 2;
		}else if(e instanceof ActionEvent){
			return true;
		}
		return false;
	}
	
	private int parseInt(Object value){
		try{
			return Integer.parseInt(value + "");
		}catch(NumberFormatException nfe){
			return 0;
		}
	}
	
	private long parseLong(Object value){
		try{
			return Long.parseLong(value + "");
		}catch(NumberFormatException nfe){
			return 0;
		}
	}
	
	private float parseFloat(Object value){
		try{
			return Float.parseFloat(value + "");
		}catch(NumberFormatException nfe){
			return 0.0F;
		}
	}
	
	private double parseDouble(Object value){
		try{
			return Double.parseDouble(value + "");
		}catch(NumberFormatException nfe){
			return 0.0;
		}
	}
	
	private void ensureValidValueAfterDatatypeChange(int row, DataFieldType type){
		Object value = model.getValueAt(row, 2);
		System.out.println("Alter Wert: " + value);
		switch(type){
			case STRING:
				model.setValueAt(value.toString(), row, 2);
				break;
			case INTEGER:
				if(!(value instanceof Integer)){
					try{
						int newInt = Integer.parseInt(value + "");
						model.setValueAt(newInt, row, 2);
					}catch(Exception e){
						model.setValueAt(0, row, 2);
						break;
					}
				}
				break;
			case LONG:
				if(!(value instanceof Long)){
					try{
						long newLong = Long.parseLong(value + "");
						model.setValueAt(newLong, row, 2);
					}catch(Exception e){
						model.setValueAt(0L, row, 2);
						break;
					}
				}
				break;
			case DOUBLE:
				if(!(value instanceof Double)){
					try{
						double newDouble = Double.parseDouble(value + "");
						model.setValueAt(newDouble, row, 2);
					}catch(Exception e){
						model.setValueAt(0.0D, row, 2);
						break;
					}
				}
				break;
			case FLOAT:
				if(!(value instanceof Float)){
					try{
						float newFloat = Float.parseFloat(value + "");
						model.setValueAt(newFloat, row, 2);
					}catch(Exception e){
						model.setValueAt(0.0F, row, 2);
						break;
					}
				}
				break;
			case BOOLEAN:
				if(!(value instanceof Boolean)){
					try{
						model.setValueAt(Boolean.parseBoolean(value + ""), row, 2);
					}catch(Exception e){
						model.setValueAt(true, row, 2);
					}
				}else{
					model.setValueAt((boolean) value, row, 2);
				}
				break;
			default:
				break;
		}
	}
	
}