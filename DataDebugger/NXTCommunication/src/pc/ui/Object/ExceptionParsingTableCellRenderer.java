package pc.ui.Object;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

public class ExceptionParsingTableCellRenderer extends DefaultTableCellRenderer{
	
	private static final long serialVersionUID = 4501670121410030546L;
	
	private Color defaultSelectionForeground, defaultSelectionBackground, myLightGray;
	
	public ExceptionParsingTableCellRenderer(){
		UIDefaults defaults = UIManager.getDefaults();
		this.myLightGray = new Color(235, 235, 235);
		defaultSelectionForeground = defaults.getColor("List.selectionForeground");
		defaultSelectionBackground = defaults.getColor("List.selectionBackground");
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
		setText(value + "");
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
		return this;
	}
	
}