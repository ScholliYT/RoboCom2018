package pc.ui.Object;

import javax.swing.table.*;

import pc.object.DataFieldType;

import java.awt.Component;

import javax.swing.*;
import java.awt.*;

/**
 *  Implements a custom renderer for our JTable in the Main-UI, for an accurate documentation, take a look in the doc of {@link DefaultTableCellRenderer}
 * @author Simon
 *
 */
public class MyTableCellRenderer extends DefaultTableCellRenderer{
	
	private static final long serialVersionUID = 7600394344309140741L;
	
	private Color defaultSelectionForeground, defaultSelectionBackground, myLightGray, myDarkGreen, myHoverColor;
	private MyFileTableModel model;
	private int currentHover;
	
	/**
	 * Create a new custom CellRenderer
	 */
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
	
	/**
	 * Returns the component, that should be used to render the cell
	 * @param table the table
	 * @param value the current value of the cell that needs to be rendered
	 * @param isSelected indicates if the current cell is selected
	 * @param hasFocus indicates if the current cell has focus
	 * @param row the row of the current cell that needs to be rendered
	 * @param column the column of the current cell that needs to be rendered
	 * @return an Object that displays the cell
	 */
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
	
	/**
	 * Sets the current hovered rownumber in order to render hovered rows differently
	 * @param hover the current hovered row
	 */
	public void setCurrentHover(int hover){
		this.currentHover = hover;
	}
	
	/**
	 * returns the current hovered row
	 * @return the current hovered row
	 */
	public int getCurrentHover(){
		return currentHover;
	}
	
}