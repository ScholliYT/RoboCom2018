package pc.in;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import lejos.util.Delay;
import pc.connection.NXTCommunication;
import pc.object.DataFieldType;
import pc.ui.LoadNxtSettingsDialog;
import pc.ui.NXTCommunicationFrame;
import pc.ui.Object.MyFileTableModel;
import pc.ui.Object.TableRowObject;

/**
 * Manages the Datainputstream and interprets the data sent by the NXT
 * @author Simon
 *
 */
public class NxtToPcInputStreamManager extends Thread{
	
	private NXTCommunication com; //The NXTCommunication
	
	private NXTCommunicationFrame frame; //The Main-UI, for printing debug-Strings, for example
	private NxtToPcInputStream input; // The InputStream, which is used for reading data
	private volatile boolean interrupted; //Indicates, wether the underlying stream is closed or not
	
	/**
	 * @param com The NXTCommunication
	 * @param frame The Main-UI, for printing debug-Strings, for example
	 * @param in The "raw" InputStream for communicating with the NXT
	 */
	public NxtToPcInputStreamManager(NXTCommunication com, NXTCommunicationFrame frame, InputStream in){
		this.com = com;
		this.frame = frame;
		this.input = new NxtToPcInputStream(in);
		this.interrupted = false;
		this.setDaemon(true); //Sets the daemon-flag, which means, that this thread will terminate, if there is no other thread without the deamonthread is running anymore
		this.start(); //Starts this thread
	}
	
	@Override
	public void run(){
		while(!interrupted){ //While the interrupted-flag is not set
			int read; //
			String buffer = ""; //buffer for a single line sent by the nxt
			try{
				while((read = input.read()) != -1){ //run while the stream is empty, usually the read-method blocks until new data is available
					buffer += (char) read; //the buffer is extended by the next read char
					if(buffer.charAt(buffer.length() - 1) == '\n'){ //if the new char is a indicator for a new line, the current buffer is interpreted
						if(buffer.startsWith(" ")){ //if the buffer is just an empty String, the message is ignored
							buffer = ""; //reset the buffer
							Delay.msDelay(10); //wait a while, the NXT does only sent 8 times a second, so we have time
							continue; //Next continuous loop
						}else if(buffer.startsWith("df!")){ //wenn der String mit 'df!' anfängt, hat der NXT uns ein Datenfeldupdate zukommen lassenu
							buffer = buffer.substring(3, buffer.length()-1); //Das df! vom anfang, und den escape-char '\n' vom string abschneiden
							String name = ""; //buffer for the datafieldname
							String type = ""; //buffer for the datafiledtype as String
							String value = ""; //buffer for the datafieldvalue as String
							String readBuffer = ""; //readbuffer
							int doppelCount = 0; //counts for splitchars, in this case, i use the ':'-char to indicate, that the string is to be splitted
							ArrayList<TableRowObject> list = new ArrayList<>(); //an empty ArrayList in order to save resolved datafields recieved from the NXT
							char[] chars = buffer.toCharArray(); //char[] from the buffer, in order to check every char
							for(int i = 0; i < chars.length; i++){ //while there are chars to interpret, do following:
								char c = chars[i]; //push the current char into a buffer
								if(c != ':' && c != ';'){ //if the current char is not an escape-char, which i chose, the readbuffer is extended by this char
									readBuffer += c;
									continue; //continuous loop
								}else if(c == ':'){ //An escape-char is found
									doppelCount++; //increase the variable, which indicates, which part we just read, wheter is was the name, the type or the value of a datafield
									if(doppelCount == 1){ //The one indicates, that we just read the name
										name = readBuffer; //set the name for the current datafield we are resolving
									}else if(doppelCount == 2){ //the two indicates, that we just read the type of the datafield
										type = readBuffer; //set the current type into the type-buffer
									}
									readBuffer = ""; //reset the readbuffer
								}else if(c == ';'){ //the semicolon indicates, that we have read an entire Datafield, so we make things ready to add it to the ui
									value = readBuffer; //the last part we read was the value of the datafield
									doppelCount = 0; //we reset the doppelcount, because if the string isnt empty, we have more datafields we have to interpret
									list.add(new TableRowObject(name, DataFieldType.getDataFieldTypeFromString(type), value)); //add the datafield to the list
									readBuffer = ""; //reset all buffers
									name = "";
									type = "";
									value = "";
								}
							}
							
							MyFileTableModel model = frame.getModel(); //Get the tablemodel of the Datafieldtable in the main-ui
							
							while(model.getRowCount() > 0){ //Delete all current datafields in the UI
								model.deleteRow(0); //Delete an specific Datafield
							}
							
							//Add all datafields, we just got by the nxt
							for(TableRowObject obj: list){
								model.addRow(obj.getFieldName(), obj.getType(), obj.getValue().toString());
							}
							
							frame.getTable().updateUI(); //Let the UI update its list
							
							LoadNxtSettingsDialog.hideDialog(); //Hide the Dialog, that tells the user, that this program is triing to load the datafirelds the nxt has
						}else if(buffer.startsWith("ex!")){ //The "ex!" at the beginninh of an line indicated, that the NXT has thrown an Exception and has sent that to us
							frame.displayNxtErrorInput(buffer.substring(3)); //We display the Exception in the Main-UI
						}else if(buffer.startsWith("shutdown")){ //The "shutdown"-String indicates, that the NXT requested to close the connections and therfore also the program
							com.close(true); //close all the connections
							input.close();
							buffer = ""; //reset the buffer
						}else{ //If none of these special Stingindicators were found, we figure that the String we got is a simple debugstring
							frame.displayNxtInput(buffer.substring(0, buffer.length() - 1)); //Show the String in the UI
							buffer = ""; //reset the buffer
						}
						break;
					}
				}
				Delay.msDelay(10);
			}catch(IOException e){
				com.close(true); //When we run in an error, we figure, that the program was closed by the NXT and start to terminate this program
			}
			Delay.msDelay(10);
		}
		try{
			input.close();
		}catch(Exception ignore){}
		
	}
	
	/**
	 * Sets the interrupted-flag for this thread, lets the Thread exit
	 */
	@Override
	public void interrupt(){
		this.interrupted = true;
	}
	
	/**
	 * Returns the interrupted-status this thread has
	 * @return the current interrupted-flag
	 */
	@Override
	public boolean isInterrupted(){
		return interrupted;
	}
	
}