package pc.ui;

import javax.swing.JDialog;
import java.awt.Toolkit;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import java.awt.SystemColor;
import javax.swing.JButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Dialog used for giving information(s) about the program, as requested, for example, by our icon-provider (icons8.com)
 * @author Simon
 */
public class AboutProgramDialog extends JDialog{
	
	private static final long serialVersionUID = -6790002224347714637L; //Just here to remove an annoying warning of eclipse
	
	private JTextPane textPaneAbout; //The main-textpane, used for all our text we want to show
	private JButton btnClose; //The button used to hide this dialog
	
	/**
	 * Creates this Dialog
	 */
	public AboutProgramDialog(){
		setModalityType(ModalityType.APPLICATION_MODAL); //Modality of this dialog
		setResizable(false); //makes this dialog non-resizable
		setTitle("\u00DCber dieses Programm"); //Sets the title for this Dialog
		setIconImage(Toolkit.getDefaultToolkit().getImage(AboutProgramDialog.class.getResource("/resources/about_icon_16px.png"))); //Sets the Icon for this dialog (on windows in the upper left corner)
		setBounds(100, 100, 345, 465); //sets size and location for this dialog, the location will be overwritten, when we try to show this dialog
		getContentPane().setLayout(null); //Sets the default layout for the contetnpane (final positioning)
		
		JScrollPane scrollPane = new JScrollPane(); //The scollpane used for this frame, so we can scroll our textpane down
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS); //Always show our vertical scrollbar
		scrollPane.setBounds(10, 11, 320, 380); //Set the bounds (as for this dialog) for the scrollpane, since we have a final positioning
		getContentPane().add(scrollPane); //add this scrollpane to the contentpane, so it will be displayed in our dialog
		
		textPaneAbout = new JTextPane(); //create the textpane
		textPaneAbout.setContentType("text/html"); //set the contenttype to Html, so we can use html-tags to display links in this pane, for example
		textPaneAbout.setText("<html>\r\n<h2 style=\"text-align: center\">\u00DCber die Software</h2>\r\n<p style=\"text-align: justify\">Dieses Programm wurde von Simon R\u00FCsweg im Rahmen der Robotik-AG (angewandte Informatik) unter der Leitung von Herr Bittner w\u00E4hrend des Unterrichts und zu gro\u00DFen Teilen in seiner Freizeit entwickelt. Der Zweck von diesem Programm dient im einfachen \u00DCbertragen von Daten an den NXT w\u00E4hrend dieser ein Programm ausf\u00FChrt. Ebenfalls dient es zum einfachen Senden von Daten vom NXT-Brick an den PC, um Daten schnell analysieren zu k\u00F6nnen, um so einen erweiterten und verbesserten NXT-Remotedebugger darzustellen. Verwendete Bibliotheken und Resourcen werden unten aufgef\u00FChrt.</p><br>\r\n\r\n<h3>Verwendete Bibliotheken</h3>\r\n<ul>\r\n<li><a href=\"http://www.lejos.org/\">LeJos 0.9.1 f\u00FCr NXT-Bricks</a></li>\r\n</ul>\r\n\r\n<h3>Verwendete Ressoucen:</h3>\r\n<ul>\r\n<li>Ein Gro\u00DFteil der Icons werden zur Verf\u00FCgung gestellt von <a href=\"https://icons8.com/\">icons8.com</a></li>\r\n</ul>\r\n\r\n<h3>Github-Seite des Projektes:</h3>\r\n<ul>\r\n<li><a href=\"https://github.com/ScholliYT/RoboCom2018/\">ScholliYT/RoboCom2018</a></li>\r\n</ul>\r\n</html>");
		textPaneAbout.setBackground(SystemColor.menu); //Set the background-color to the same color the frame has
		textPaneAbout.setEditable(false); //set the textpane non-editable
		textPaneAbout.setHighlighter(null); //disable markup for our text
		textPaneAbout.setCaretPosition(0); //Make sure, that the scrollpane is on top
		textPaneAbout.addHyperlinkListener(new HyperlinkListener(){ //A hyperlink-listener
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) { //is called, when the user interacts with our links in the textpane
				if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED){ //When the user clicks on the links
					try{ //We try to open the link in the default webbrowser
						Desktop.getDesktop().browse(e.getURL().toURI());
					}catch(Exception ex){} //Exceptions will be ignored
				}
			}
		});
		scrollPane.setViewportView(textPaneAbout); //Add our textpane to the scrollpane, so we can actually scroll it
		
		btnClose = new JButton("Schlie\u00DFen");  //Create the close-button
		btnClose.addActionListener(new ActionListener(){ //Add an Actionlistener to the button (will be called, when we click the button)
			public void actionPerformed(ActionEvent e){
				AboutProgramDialog.this.setVisible(false); //Make this dialog invisible
			}
		});
		btnClose.setToolTipText("Diesen Dialog schlie\u00DFen"); //Set a tooltip
		btnClose.setBounds(220, 402, 110, 23); //set our bounds
		btnClose.setFocusPainted(false); //disable focus-paintig for this component (that is just my personal preference)
		getContentPane().add(btnClose); //Add this button to our dialog
	}
	
	public static void showDialogAt(Component parent){ //Make the dialog accessable for third classes
		AboutProgramDialog dialog = new AboutProgramDialog(); //Create a new dialog
		dialog.setLocationRelativeTo(parent); //Set the location relative to our parent (usually the main-ui, NXTCommunicationFrame)
		SwingUtilities.updateComponentTreeUI(dialog); //Make sure that this frame will be rendered in the chosen LookAndFeel
		dialog.setVisible(true); //Make the dialog visible
	}
	
}