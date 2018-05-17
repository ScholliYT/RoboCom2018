package pc.in;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import lejos.util.Delay;
import pc.connection.NXTCommunication;
import pc.object.DataFieldType;
import pc.ui.ExceptionReporter;
import pc.ui.LoadNxtSettingsDialog;
import pc.ui.NXTCommunicationFrame;
import pc.ui.Object.MyFileTableModel;
import pc.ui.Object.TableRowObject;

public class NxtToPcInputStreamManager extends Thread{
	
	private NXTCommunication com;
	
	private NXTCommunicationFrame frame;
	private NxtToPcInputStream input;
	private volatile boolean interrupted;
	
	public NxtToPcInputStreamManager(NXTCommunication com, NXTCommunicationFrame frame, InputStream in){
		this.com = com;
		this.frame = frame;
		this.input = new NxtToPcInputStream(in);
		this.interrupted = false;
		this.setDaemon(true);
		this.start();
	}
	
	@Override
	public void run(){
		while(!interrupted){
			int read;
			String buffer = "";
			try{
				while((read = input.read()) != -1){
					buffer += (char) read;
					if(buffer.charAt(buffer.length() - 1) == '\n'){
						if(buffer.startsWith(" ")){
							buffer = "";
							continue;
						}else if(buffer.startsWith("df!")){
							buffer = buffer.substring(3, buffer.length()-1);
							String name = "";
							String type = "";
							String value = "";
							String readBuffer = "";
							int doppelCount = 0;
							ArrayList<TableRowObject> list = new ArrayList<>();
							char[] chars = buffer.toCharArray();
							for(int i = 0; i < chars.length; i++){
								char c = chars[i];
								if(c != ':' && c != ';'){
									readBuffer += c;
									continue;
								}else if(c == ':'){
									doppelCount++;
									if(doppelCount == 1){
										name = readBuffer;
									}else if(doppelCount == 2){
										type = readBuffer;
									}
									readBuffer = "";
								}else if(c == ';'){
									value = readBuffer;
									doppelCount = 0;
									list.add(new TableRowObject(name, DataFieldType.getDataFieldTypeFromString(type), value));
									readBuffer = "";
									name = "";
									type = "";
									value = "";
								}
							}
							
							MyFileTableModel model = frame.getModel();
							
							while(model.getRowCount() > 0){
								model.deleteRow(0);
							}
							
							for(TableRowObject obj: list){
								model.addRow(obj.getFieldName(), obj.getType(), obj.getValue().toString());
							}
							
							frame.getTable().updateUI();
							
							LoadNxtSettingsDialog.hideDialog();
						}else if(buffer.startsWith("ex!")){
							frame.displayNxtErrorInput(buffer.substring(3));
						}else if(buffer.startsWith("shutdown")){
							com.close(true);
							input.close();
							System.out.println("in closed!");
							buffer = "";
						}else{
							frame.displayNxtInput(buffer.substring(0, buffer.length() - 1));
							buffer = "";
						}
						break;
					}
				}
				Delay.msDelay(50);
			}catch(IOException e){
				com.close(true);
			}
			try{
				Thread.sleep(10);
			}catch(InterruptedException e){
				ExceptionReporter.showDialog(frame, e);
			}
		}
		try{
			input.close();
		}catch(Exception ignore){}
		
	}
	
	@Override
	public void interrupt(){
		this.interrupted = true;
	}
	
	@Override
	public boolean isInterrupted(){
		return interrupted;
	}
	
}