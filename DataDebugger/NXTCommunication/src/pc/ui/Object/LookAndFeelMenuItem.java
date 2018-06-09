package pc.ui.Object;

import javax.swing.JRadioButtonMenuItem;
import javax.swing.UIManager.LookAndFeelInfo;

/**
 * Implements an LookAndFeelRadioButtonMenuItem and saves its LookAndFeelInfo for easy switching to it, if this component is clicked at
 * @author Simon
 *
 */
public class LookAndFeelMenuItem extends JRadioButtonMenuItem{
	
	private static final long serialVersionUID = 3165788739281419184L;
	
	private LookAndFeelInfo me;
	
	/**
	 * Create an new MenuItem for a LookAndFeel
	 * @param me the LookAndFeelInfo that needs to be represented by this MenuItem
	 * @param listener the general Listener for all LookAndFeelMenuItems
	 */
	public LookAndFeelMenuItem(LookAndFeelInfo me, LookAndFeelActionListener listener){
		super(me.getName());
		this.addActionListener(listener);
		this.me = me;
	}
	
	/**
	 * @return the {@link LookAndFeelInfo} represented by this class
	 */
	public LookAndFeelInfo getInfo(){
		return me;
	}
	
}