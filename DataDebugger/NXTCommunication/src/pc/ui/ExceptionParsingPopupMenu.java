package pc.ui;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;

public class ExceptionParsingPopupMenu extends JPopupMenu{
	
	private static final long serialVersionUID = -4538191008314737487L;
	
	private JTextArea textArea;
	
	public ExceptionParsingPopupMenu(JTextArea textArea){
		this.textArea = textArea;
		Toolkit tk = Toolkit.getDefaultToolkit();
		JMenuItem itemPaste = new JMenuItem("Einfügen");
		itemPaste.setIcon(new ImageIcon(NXTCommunicationFrame.class.getResource("/resources/paste_icon_16px.png")));
		itemPaste.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				try{
					textArea.append((String) tk.getSystemClipboard().getData(DataFlavor.stringFlavor));
				}catch(Exception ignore){}
			}
		});
		add(itemPaste);
	}
	
	public void showAt(int x, int y){
		show(textArea, x, y);
	}
	
	public void hidePopup(){
		this.setVisible(false);
	}
	
}