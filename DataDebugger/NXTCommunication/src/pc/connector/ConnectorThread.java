package pc.connector;

import javax.swing.JProgressBar;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;
import pc.object.SettingsManager;
import pc.ui.ConnectToNxtDialog;
import pc.ui.ExceptionReporter;

public class ConnectorThread extends Thread{
	
	private JProgressBar pb;
	private int timeout;
	private boolean interrupted;
	private String nxtName;
	private ConnectionType type;
	private ConnectToNxtDialog dialog;
	
	private SettingsManager settings;
	
	public ConnectorThread(ConnectToNxtDialog dialog, JProgressBar pb, int timeout, String nxtName, ConnectionType type){
		this.settings = SettingsManager.getSingletone();
		this.dialog = dialog;
		this.pb = pb;
		if(type == ConnectionType.USB){
			this.pb.setIndeterminate(false);
			this.pb.setStringPainted(true);
			this.pb.setMaximum(100);
			this.pb.setMinimum(0);
			this.pb.setValue(0);
		}else{
			this.pb.setIndeterminate(true);
			this.pb.setStringPainted(false);
		}
		this.interrupted = false;
		this.timeout = timeout;
		
		if(!nxtName.isEmpty()){
			this.nxtName = nxtName;
		}else{
			this.nxtName = null;
		}
		this.type = type;
	}
	
	@Override
	public void run(){
		NXTComm connection;
		long start = System.currentTimeMillis();
		long timeLapsed = 0;
		while(!interrupted){
			try{
				connection = NXTCommFactory.createNXTComm(type == ConnectionType.USB ? NXTCommFactory.USB : NXTCommFactory.BLUETOOTH);
				
				NXTInfo loaded = settings.getNXTInfoOf(nxtName, type);
				
				if(loaded == null){
					NXTInfo[] info = connection.search(nxtName);
					if(info.length != 0){
						NXTInfo nxt = info[0];
						if(nxt == null) continue;
						if(connection.open(nxt)){
							settings.addRecentNxtData(nxt.name, nxt.deviceAddress, type);
							dialog.onSuccess(connection);
							return;
						}
					}
				}else{
					loaded.protocol = (type == ConnectionType.USB ? NXTCommFactory.USB : NXTCommFactory.BLUETOOTH);
					if(connection.open(loaded)){
						dialog.onSuccess(connection);
						return;
					}
				}
				
				if(type == ConnectionType.BLUETOOTH){
					dialog.resetDialog();
					return;
				}
				
				if(type == ConnectionType.USB){
					timeLapsed = System.currentTimeMillis() - start;
					if(timeLapsed >= timeout){
						this.interrupt();
					}
					pb.setValue((int) (((float) timeLapsed / (float) timeout) * 100));
				}
			}catch(NXTCommException e){
				ExceptionReporter.showDialog(dialog, e);
				return;
			}
		}
		dialog.resetDialog();
	}
	
	@Override
	public void interrupt(){
		this.interrupted = true;
	}
	
}