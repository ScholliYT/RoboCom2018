package pc.ui.Object;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Implements a custom renderer for our JTable in the ExceptionparsingDialog. For an accurate documentation, take a look in the doc of {@link DefaultTableCellRenderer}
 * @author Simon
 */
public class ExceptionParsingTableCellRenderer extends DefaultTableCellRenderer{
	
	private static final long serialVersionUID = 4501670121410030546L;
	
	private Color defaultSelectionForeground, defaultSelectionBackground, myLightGray;
	
	/**
	 * Create a new custom CellRenderer
	 */
	public ExceptionParsingTableCellRenderer(){
		UIDefaults defaults = UIManager.getDefaults();
		this.myLightGray = new Color(235, 235, 235);
		this.defaultSelectionForeground = defaults.getColor("List.selectionForeground");
		this.defaultSelectionBackground = defaults.getColor("List.selectionBackground");
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