package pc.ui;

import javax.swing.JDialog;
import javax.swing.JTextArea;

import java.awt.SystemColor;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class LoadNxtSettingsDialog extends JDialog{
	
	private static final long serialVersionUID = 6178518182675913635L;
	
	private static LoadNxtSettingsDialog SINGLETONE;
	
	public LoadNxtSettingsDialog() {
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(false);
		setType(Type.POPUP);
		setTitle("Warte auf Daten vom NXT...");
		setBounds(100, 100, 450, 168);
		getContentPane().setLayout(null);
		
		JTextArea txtrEsWirdDarauf = new JTextArea();
		txtrEsWirdDarauf.setLineWrap(true);
		txtrEsWirdDarauf.setForeground(Color.BLACK);
		txtrEsWirdDarauf.setFont(new Font("Tahoma", Font.PLAIN, 13));
		txtrEsWirdDarauf.setEditable(false);
		txtrEsWirdDarauf.setBackground(SystemColor.menu);
		txtrEsWirdDarauf.setWrapStyleWord(true);
		txtrEsWirdDarauf.setText("Es wird darauf gewartet, dass die vom NXT angeforderten Daten von diesem \u00DCbertragen werden. Dies kann ein paar Sekunden in Anspruch nehmen.");
		txtrEsWirdDarauf.setBounds(10, 11, 424, 52);
		txtrEsWirdDarauf.setHighlighter(null);
		getContentPane().add(txtrEsWirdDarauf);
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setBounds(10, 63, 424, 24);
		getContentPane().add(progressBar);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(0, 98, 444, 2);
		getContentPane().add(separator);
		
		JButton btnAbbrechen = new JButton("Abbrechen");
		btnAbbrechen.setEnabled(false);
		btnAbbrechen.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				//TODO Abbruch initialisieren!
				LoadNxtSettingsDialog.this.setVisible(false);
			}
		});
		btnAbbrechen.setBounds(314, 111, 120, 23);
		btnAbbrechen.setFocusPainted(false);
		getContentPane().add(btnAbbrechen);
	}
	
	public static void showDialogAt(Container parent){
		if(SINGLETONE == null){
			SINGLETONE = new LoadNxtSettingsDialog();
		}
		SINGLETONE.setLocationRelativeTo(parent);
		SINGLETONE.setVisible(true);
	}
	
	public static void hideDialog(){
		if(SINGLETONE == null){
			return;
		}
		SINGLETONE.setVisible(false);
	}
	
}