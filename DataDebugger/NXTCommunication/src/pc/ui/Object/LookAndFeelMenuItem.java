package pc.ui.Object;

import javax.swing.JRadioButtonMenuItem;
import javax.swing.UIManager.LookAndFeelInfo;

public class LookAndFeelMenuItem extends JRadioButtonMenuItem{
	
	private static final long serialVersionUID = 3165788739281419184L;
	
	private LookAndFeelInfo me;
	
	public LookAndFeelMenuItem(LookAndFeelInfo me, LookAndFeelActionListener listener){
		super(me.getName());
		this.addActionListener(listener);
		this.me = me;
	}
	
	public LookAndFeelInfo getInfo(){
		return me;
	}
	
}