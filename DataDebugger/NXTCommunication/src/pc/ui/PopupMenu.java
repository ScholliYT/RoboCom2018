package pc.ui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import pc.object.SettingsManager;
import pc.ui.Object.MyFileTableModel;

public class PopupMenu extends JPopupMenu{
	
	private static final long serialVersionUID = -4279358223838574754L;
	
	private Point clickLocation;
	private NXTCommunicationFrame frame;
	private JTable table;
	
	private JMenuItem itemUpload, itemEditCell, itemDeleteRow, itemAddRow, itemEditRow;
	
	public PopupMenu(){
		this.clickLocation = null;
		itemUpload = new JMenuItem("Datenfelder uploaden");
		itemUpload.setIcon(new ImageIcon(NXTCommunicationFrame.class.getResource("/resources/upload_icon_16px.png")));
		itemUpload.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				frame.uploadCurrentDatafields();
			}
		});
		itemEditCell = new JMenuItem("Zelle editieren");
		itemEditCell.setIcon(new ImageIcon(NXTCommunicationFrame.class.getResource("/resources/edit_icon_16px.png")));
		itemEditCell.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				int row = table.rowAtPoint(clickLocation);
				int column = table.columnAtPoint(clickLocation);
				table.editCellAt(row, column, e);
			}
		});
		
		itemEditRow = new JMenuItem("Zeile editieren...");
		itemEditRow.setIcon(new ImageIcon(NXTCommunicationFrame.class.getResource("/resources/edit_icon_16px.png")));
		itemEditRow.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				int row = table.rowAtPoint(clickLocation);
				frame.showEditRowDialog(row);
			}
		});
		
		itemDeleteRow = new JMenuItem("Zeile löschen");
		itemDeleteRow.setIcon(new ImageIcon(NXTCommunicationFrame.class.getResource("/resources/delete_icon_16px.png")));
		itemDeleteRow.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				int row = table.rowAtPoint(clickLocation);
				if(!SettingsManager.getSingletone().getDeleteRowsWithoutDialog()){
					int dialogResult = JOptionPane.showConfirmDialog(frame, "Möchten Sie diesen Eintrag endgültig löschen?", "Datenfeld löschen?", JOptionPane.YES_NO_OPTION);
					if(dialogResult != JOptionPane.OK_OPTION){
						return;
					}
				}
				frame.cancelCurrentTableEdit();
				((MyFileTableModel) table.getModel()).deleteRow(row);
				table.updateUI();
				table.clearSelection();
			}
		});
		itemAddRow = new JMenuItem("Neues Datenfeld...");
		itemAddRow.setIcon(new ImageIcon(NXTCommunicationFrame.class.getResource("/resources/add_new_icon_16px.png")));
		itemAddRow.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				frame.showAddNewRowDialog();
			}
		});
		
		
		add(itemUpload);
		add(new JSeparator());
		add(itemAddRow);
		add(new JSeparator());
		add(itemEditCell);
		add(itemEditRow);
		add(new JSeparator());
		add(itemDeleteRow);
		pack();
	}
	
	
	public void showAt(NXTCommunicationFrame frame, JTable table, int x, int y){
		this.frame = frame;
		this.clickLocation = new Point(x, y);
		this.table = table;
		boolean b = table.rowAtPoint(clickLocation) != -1;
		itemDeleteRow.setEnabled(b);
		itemEditCell.setEnabled(b);
		itemEditRow.setEnabled(b);
		SwingUtilities.updateComponentTreeUI(this);
		show(table, x, y);
	}
	
	public void hidePopup(){
		this.setVisible(false);
	}
	
}