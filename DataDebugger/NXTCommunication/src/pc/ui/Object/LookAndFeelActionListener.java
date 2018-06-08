package pc.ui.Object;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import pc.ui.NXTCommunicationFrame;

public class LookAndFeelActionListener implements ActionListener{
	
	private NXTCommunicationFrame frame;
	
	public LookAndFeelActionListener(NXTCommunicationFrame frame){
		this.frame = frame;
	}
	
	@Override
	public void actionPerformed(ActionEvent e){
		String className;
		if(e.getSource() instanceof LookAndFeelMenuItem){
			className = ((LookAndFeelMenuItem) e.getSource()).getInfo().getClassName();
		}else{
			className = UIManager.getSystemLookAndFeelClassName();
		}
		
		try{
			UIManager.setLookAndFeel(className);
			SwingUtilities.updateComponentTreeUI(frame);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
}