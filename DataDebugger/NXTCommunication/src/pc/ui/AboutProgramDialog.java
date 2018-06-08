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

public class AboutProgramDialog extends JDialog{
	
	private static final long serialVersionUID = -6790002224347714637L;
	
	private JTextPane textPaneAbout;
	private JButton btnClose;
	
	public AboutProgramDialog(){
		setModalityType(ModalityType.APPLICATION_MODAL);
		setType(Type.POPUP);
		setResizable(false);
		setTitle("\u00DCber dieses Programm...");
		setIconImage(Toolkit.getDefaultToolkit().getImage(AboutProgramDialog.class.getResource("/resources/about_icon_16px.png")));
		setBounds(100, 100, 345, 465);
		getContentPane().setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(10, 11, 320, 380);
		getContentPane().add(scrollPane);
		
		textPaneAbout = new JTextPane();
		textPaneAbout.setContentType("text/html");
		textPaneAbout.setText("<html>\r\n<h2 style=\"text-align: center\">\u00DCber die Software</h2>\r\n<p style=\"text-align: justify\">Dieses Programm wurde von Simon R\u00FCsweg im Rahmen der Robotik-AG (angewandte Informatik) unter der Leitung von Herr Bittner w\u00E4hrend des Unterrichts und zu gro\u00DFen Teilen in seiner Freizeit entwickelt. Der Zweck von diesem Programm dient im einfachen \u00DCbertragen von Daten an den NXT w\u00E4hrend dieser ein Programm ausf\u00FChrt. Ebenfalls dient es zum einfachen Senden von Daten vom NXT-Brick an den PC, um Daten schnell analysieren zu k\u00F6nnen, um so einen erweiterten und verbesserten NXT-Remotedebugger darzustellen. Verwendete Bibliotheken und Resourcen werden unten aufgef\u00FChrt.</p><br>\r\n\r\n<h3>Verwendete Bibliotheken</h3>\r\n<ul>\r\n<li><a href=\"http://www.lejos.org/\">LeJos 0.9.1 f\u00FCr NXT-Bricks</a></li>\r\n</ul>\r\n\r\n<h3>Verwendete Ressoucen:</h3>\r\n<ul>\r\n<li>Ein Gro\u00DFteil der Icons werden zur Verf\u00FCgung gestellt von <a href=\"https://icons8.com/\">icons8.com</a></li>\r\n</ul>\r\n\r\n<h3>Github-Seite des Projektes:</h3>\r\n<ul>\r\n<li><a href=\"https://github.com/ScholliYT/RoboCom2018/\">ScholliYT/RoboCom2018</a></li>\r\n</ul>\r\n</html>");
		textPaneAbout.setBackground(SystemColor.menu);
		textPaneAbout.setEditable(false);
		textPaneAbout.setHighlighter(null);
		textPaneAbout.setCaretPosition(0);
		textPaneAbout.addHyperlinkListener(new HyperlinkListener(){
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED){
					try{
						Desktop.getDesktop().browse(e.getURL().toURI());
					}catch(Exception ex){}
				}
			}
		});
		scrollPane.setViewportView(textPaneAbout);
		
		btnClose = new JButton("Schlie\u00DFen");
		btnClose.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				AboutProgramDialog.this.setVisible(false);
			}
		});
		btnClose.setToolTipText("Diesen Dialog schlie\u00DFen");
		btnClose.setBounds(220, 402, 110, 23);
		btnClose.setFocusPainted(false);
		getContentPane().add(btnClose);
	}
	
	public static void showDialogAt(Component parent){
		AboutProgramDialog dialog = new AboutProgramDialog();
		dialog.setLocationRelativeTo(parent);
		SwingUtilities.updateComponentTreeUI(dialog);
		dialog.setVisible(true);
	}
	
}