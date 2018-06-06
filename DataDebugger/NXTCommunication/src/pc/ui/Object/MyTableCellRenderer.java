package pc.ui.Object;

import javax.swing.table.*;

import pc.object.DataFieldType;

import java.awt.Component;

import javax.swing.*;
import java.awt.*;

public class MyTableCellRenderer extends DefaultTableCellRenderer{
	
	private static final long serialVersionUID = 7600394344309140741L;
	
	private Color defaultSelectionForeground, defaultSelectionBackground, myLightGray, myDarkGreen, myHoverColor;
	private MyFileTableModel model;
	private int currentHover;
	
	public MyTableCellRenderer(MyFileTableModel model){
		UIDefaults defaults = UIManager.getDefaults();
		this.myLightGray = new Color(235, 235, 235);
		this.myDarkGreen = new Color(0, 128, 0);
		this.myHoverColor = new Color(135, 206, 250, 75);
		defaultSelectionForeground = defaults.getColor("List.selectionForeground");
		defaultSelectionBackground = defaults.getColor("List.selectionBackground");
		this.model = model;
		this.currentHover = -1;
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
		if(isSelected){
			setForeground(defaultSelectionForeground);
			setBackground(defaultSelectionBackground);
		}else if((row % 2) == 0){
			setForeground(Color.BLACK);
			setBackground(Color.WHITE);
		}else{
			setForeground(Color.BLACK);
			setBackground(myLightGray);
		}
		
		if(row == currentHover && !isSelected){
			setBackground(myHoverColor);
		}
		
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
				case BOOLEAN:
					setText("Boolean");
			}
		}else{
			setText(value + "");
			if(column == 2){
				if(((DataFieldType) model.getValueAt(row, 1)) == DataFieldType.STRING){
					setHorizontalAlignment(LEFT);
				}else if(((DataFieldType) model.getValueAt(row, 1)) == DataFieldType.BOOLEAN){
					if(!isSelected){
						setForeground((Boolean.parseBoolean(value + "") ? myDarkGreen : Color.RED));
					}
					setHorizontalAlignment(CENTER);
				}else{
					setHorizontalAlignment(RIGHT);
				}
			}else{
				setHorizontalAlignment(LEFT);
			}
		}
		return this;
	}
	
	public void setCurrentHover(int hover){
		this.currentHover = hover;
	}
	
	public int getCurrentHover(){
		return currentHover;
	}
	
}