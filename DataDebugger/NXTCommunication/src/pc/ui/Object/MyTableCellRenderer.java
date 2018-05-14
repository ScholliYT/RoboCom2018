package pc.ui.Object;

import javax.swing.table.*;

import pc.object.DataFieldType;

import java.awt.Component;

import javax.swing.*;
import java.awt.*;

public class MyTableCellRenderer extends DefaultTableCellRenderer{
	
	private static final long serialVersionUID = 7600394344309140741L;
	
//	private JComboBox<String> comboBox;
	private Color defaultSelectionForeground, defaultSelectionBackground;
	private MyFileTableModel model;
	
	public MyTableCellRenderer(MyFileTableModel model){
		UIDefaults defaults = UIManager.getDefaults();
		defaultSelectionForeground = defaults.getColor("List.selectionForeground");
		defaultSelectionBackground = defaults.getColor("List.selectionBackground");
		this.model = model;
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
		if(column == 1){ //Combobox
			DataFieldType type = (DataFieldType) value;
			setHorizontalAlignment(LEFT);
			switch(type){
				case STRING:
					setText("String");
					break;
				case INTEGER:
					setText("Integer");
					break;
				case LONG:
					setText("Long");
					break;
				case DOUBLE:
					setText("Double");
					break;
				case FLOAT:
					setText("Float");
					break;
			}
		}else{
			setText(value + "");
			if(column == 2){
				if(((DataFieldType) model.getValueAt(row, 1)) == DataFieldType.STRING){
					setHorizontalAlignment(LEFT);
				}else{
					setHorizontalAlignment(RIGHT);
				}
			}else{
				setHorizontalAlignment(LEFT);
			}
		}
		
		if(isSelected){
			setForeground(defaultSelectionForeground);
			setBackground(defaultSelectionBackground);
		}else{
			setForeground(Color.BLACK);
			setBackground(Color.WHITE);
		}
		return this;
	}
	
}