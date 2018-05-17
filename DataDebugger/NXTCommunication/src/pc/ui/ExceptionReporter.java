package pc.ui;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;

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

public class ExceptionReporter extends JDialog{
	
	private static final long serialVersionUID = 7238495030993495532L;
	
	private JTextArea tfException;
	private JButton btnRestart;
	private JButton btnShutdown;
	
	public ExceptionReporter(){
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
		lblEinUnerwarteterFehler.setBounds(10, 11, 193, 14);
		getContentPane().add(lblEinUnerwarteterFehler);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(0, 337, 624, 2);
		getContentPane().add(separator);
		
		btnShutdown = new JButton("Programm beenden");
		btnShutdown.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				System.exit(0);
			}
		});
		btnShutdown.setBounds(489, 344, 125, 23);
		btnShutdown.setFocusPainted(false);
		getContentPane().add(btnShutdown);
		
		btnRestart = new JButton("Programm neu starten");
		btnRestart.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				//TODO
			}
		});
		btnRestart.setBounds(340, 344, 139, 23);
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
		
//		pack();
		
	}
	
	private void displayException(Exception ex){
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
	
	public static void showDialog(Container parent, Exception exception){
		ExceptionReporter reporter = new ExceptionReporter();
		reporter.displayException(exception);
		reporter.setLocationRelativeTo(parent);
		reporter.setVisible(true);
	}
	
}