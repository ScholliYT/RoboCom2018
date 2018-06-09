package pc.ui;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * The popupmenu for the {@link ExceptionParsingDialog}, in order to simplify copy and paste for the user
 * @author Simon
 *
 */
public class ExceptionParsingPopupMenu extends JPopupMenu{
	
	private static final long serialVersionUID = -4538191008314737487L;
	
	private JTextArea textArea; //The textarea, which is using this popup menu
	/**
	 * Create this popupmenu
	 * @param textArea the textarea, which is using this popup menu
	 */
	public ExceptionParsingPopupMenu(JTextArea textArea){
		this.textArea = textArea;
		Toolkit tk = Toolkit.getDefaultToolkit(); //Load an default toolkit, in order to get the content that was copied by the user
		JMenuItem itemPaste = new JMenuItem("Einfügen"); //creating the JMenuItem, the component that is clickable by the user
		itemPaste.setIcon(new ImageIcon(NXTCommunicationFrame.class.getResource("/resources/paste_icon_16px.png"))); //Set the icon for this option
		itemPaste.addActionListener(new ActionListener(){ //Add an actionlistener (called, when the JMenuItem was clicked)
			@Override
			public void actionPerformed(ActionEvent e){
				try{
					textArea.append((String) tk.getSystemClipboard().getData(DataFlavor.stringFlavor)); //Try to load the data, that was copied by the user and add it to the textarea
				}catch(Exception ignore){}
			}
		});
		add(itemPaste); //add the JMenuItem to this menu
	}
	
	/**
	 * Used to show this PopupMenu, when it is requested
	 * @param x the x-coordinate, where this popupmenu should be popping up
	 * @param y the y-coordinate, where this popupmenu should be popping up
	 */
	public void showAt(int x, int y){
		SwingUtilities.updateComponentTreeUI(this); //Make sure that this frame will be rendered in the chosen LookAndFeel
		show(textArea, x, y); //Show this popupmenu at the requested location for the specified component
	}
	
	
	/**
	 * Hides this PopupMenu
	 */
	public void hidePopup(){
		this.setVisible(false);
	}
	
}