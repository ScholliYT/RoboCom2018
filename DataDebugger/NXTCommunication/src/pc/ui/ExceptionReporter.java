package pc.ui;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import java.awt.Container;
import java.awt.SystemColor;
import javax.swing.JSeparator;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Toolkit;

public class ExceptionReporter extends JDialog{
	
	private static final long serialVersionUID = 7238495030993495532L;
	
	private JTextArea tfException;
	private JButton btnRestart;
	private JButton btnShutdown;
	private JButton btnContinue;
	
	public ExceptionReporter(){
		setIconImage(Toolkit.getDefaultToolkit().getImage(ExceptionReporter.class.getResource("/resources/exception_icon_16px.png")));
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e){
				fireEvent();
			}
			
			@Override
			public void windowClosed(WindowEvent e){
				fireEvent();
			}
			
			private void fireEvent(){
				System.exit(0);
			}
		});
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setType(Type.POPUP);
		setResizable(false);
		setTitle("Exception Reporter");
		setBounds(100, 100, 630, 405);
		getContentPane().setLayout(null);
		
		JLabel lblEinUnerwarteterFehler = new JLabel("Ein unerwarteter Fehler ist aufgetreten:");
		lblEinUnerwarteterFehler.setBounds(10, 11, 604, 14);
		getContentPane().add(lblEinUnerwarteterFehler);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(0, 337, 624, 2);
		getContentPane().add(separator);
		
		btnShutdown = new JButton("Programm beenden");
		btnShutdown.setToolTipText("Das Programm beenden");
		btnShutdown.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				System.exit(0);
			}
		});
		btnShutdown.setBounds(444, 344, 170, 23);
		btnShutdown.setFocusPainted(false);
		getContentPane().add(btnShutdown);
		
		btnRestart = new JButton("Programm neu starten");
		btnRestart.setToolTipText("Das Programm neu starten");
		btnRestart.setEnabled(false);
		btnRestart.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				//TODO
			}
		});
		btnRestart.setBounds(244, 344, 190, 23);
		btnRestart.setFocusPainted(false);
		getContentPane().add(btnRestart);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 36, 604, 290);
		scrollPane.setBorder(null);
		getContentPane().add(scrollPane);
		
		tfException = new JTextArea();
		tfException.setForeground(Color.RED);
		tfException.setEditable(false);
		scrollPane.setViewportView(tfException);
		tfException.setFont(new Font("Tahoma", Font.PLAIN, 12));
		tfException.setBackground(SystemColor.menu);
		tfException.setHighlighter(null);
		
		btnContinue = new JButton("Weiter");
		btnContinue.setToolTipText("Diesen Dialog schlie\u00DFen und das Programm weiter ausf\u00FChren");
		btnContinue.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				ExceptionReporter.this.setVisible(false);
			}
		});
		btnContinue.setEnabled(false);
		btnContinue.setBounds(145, 344, 89, 23);
		btnContinue.setFocusPainted(false);
		getContentPane().add(btnContinue);
		
	}
	
	private void displayException(Exception ex){
		tfException.setText("");
		tfException.append(ex.getClass().getName() + ": " + ex.getLocalizedMessage() + "\n");
		
		for(StackTraceElement element : ex.getStackTrace()){
			tfException.append("\tat " + element.toString() + "\n");
		}
		
		Throwable cause = ex;
		
		while((cause = cause.getCause()) != null){
			tfException.append("Caused by: " + cause.getClass().getName() + ": " + cause.getLocalizedMessage() + "\n");
			for(StackTraceElement element : cause.getStackTrace()){
				tfException.append("\tat " + element.toString() + "\n");
			}
		}
		
	}
	
	public static void showDialog(Container parent, Exception exception, boolean criticalError){
		ExceptionReporter reporter = new ExceptionReporter();
		if(criticalError){
			reporter.btnContinue.setEnabled(false);
			reporter.btnRestart.setEnabled(false); //TODO einf�gen!
			reporter.btnShutdown.setEnabled(true);
		}else{
			reporter.btnContinue.setEnabled(true);
			reporter.btnRestart.setEnabled(false);
			reporter.btnShutdown.setEnabled(true);
		}
		reporter.displayException(exception);
		reporter.setLocationRelativeTo(parent);
		SwingUtilities.updateComponentTreeUI(reporter);
		reporter.setVisible(true);
	}
}