package pc.object;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.zip.ZipOutputStream;

import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;
import pc.connector.ConnectionType;
import pc.ui.ExceptionReporter;

/**
 * Manages all the settings a user can make in this program
 * @author Simon
 */
public class SettingsManager{
	
	private static SettingsManager SINGLETONE; //A Singletone, because the program needs only a single SettingsManager
	
	//All Keys for the HashMap that actually holds the saved values; i use Keys for this to reduce the chance of typing-mistakes
	public final String LANGUAGE_KEY = "general.language";
	public final String SAVE_AUTOMATICALLY_KEY = "general.saveAutomatically";
	
	public final String UPLOAD_CHANGES_DIRECTLY_KEY = "datafields.uploadChangesDirectly";
	public final String DELETE_ROWS_DIRECTLY_KEY = "datafields.deleteRowsDirectly";
	public final String CREATE_ROWS_WITH_DIALOG_KEY = "datafields.createNewRowsWithDialog";
	public final String UPLOAD_CHANGES_INCREMENTALLY_KEY = "datafields.uploadIncrementally";
	
	public final String CREATE_AUTOMATIC_LOG_FILES_KEY = "nxt.automaticLogs";
	public final String CURRENT_LOG_FOLDER_KEY = "nxt.currentLogFolder";
	public final String ARCHIVE_OLD_LOG_FILES_KEY = "nxt.archiveOldLogFiles";
	public final String AUTOSCROLL_ACTIVE_KEY = "nxt.autoscrollActive";
	
	public final String RECENT_NXTS_CACHE_KEY = "cache.recentNxts";
	public final String MOST_RECENT_NXT_KEY = "cache.recentConnectedNxt";
	public final String MOST_RECENT_CONNECTION_KEY = "cache.recentConnectionUsed";
	
	public final String RECENT_EXCEPTION_PARSING_DATA = "cache.recentNxtParsingData";
	public final String EXCEPTIONPARSING_ENABLED = "exceptions.parsingEnabled";
	
	private HashMap<String, String> currentSettings; //The HashMap that actually holds the data, and is saved to the Pc's harddrive
	
	/**
	 * Private Constructor, use <code>SettingsManager.getSingletone()</code> instead!
	 */
	private SettingsManager(){
		this.currentSettings = new HashMap<String, String>();
		this.loadCurrentSettings();
	}
	
	/**
	 * Loads the current settings, which are saved on the harddrive,
	 * if no settings were found, a new file is created and the program will use the default settings
	 */
	@SuppressWarnings("unchecked")
	private void loadCurrentSettings(){
		try{
			File settings = new File(System.getProperty("user.home") + "\\nxt\\settings.dat"); //Get the location of the Settingsfile, usually this is: "userfolder/nxt/settings.dat"
			if(!settings.exists()){ //if there is no file found, we load default settings and create a new file
				settings.getParentFile().mkdirs(); //create all directories which this file needs to be created
				settings.createNewFile(); //try to create the new file
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(settings)); //Get ready to write the default settings to the file
				currentSettings = new HashMap<String, String>(); //create the HashMap that holds all settings
				
				//Start setting default settings
				currentSettings.put(LANGUAGE_KEY, "german");
				currentSettings.put(SAVE_AUTOMATICALLY_KEY, "true");
				
				currentSettings.put(UPLOAD_CHANGES_DIRECTLY_KEY, "false");
				currentSettings.put(DELETE_ROWS_DIRECTLY_KEY, "false");
				currentSettings.put(CREATE_ROWS_WITH_DIALOG_KEY, "true");
				currentSettings.put(UPLOAD_CHANGES_INCREMENTALLY_KEY, "true");
				
				currentSettings.put(CREATE_AUTOMATIC_LOG_FILES_KEY, "false");
				currentSettings.put(CURRENT_LOG_FOLDER_KEY, System.getProperty("user.home") + "\\nxt\\");
				currentSettings.put(AUTOSCROLL_ACTIVE_KEY, "true");
				currentSettings.put(ARCHIVE_OLD_LOG_FILES_KEY, "true");
				
				currentSettings.put(RECENT_NXTS_CACHE_KEY, "");
				
				currentSettings.put(EXCEPTIONPARSING_ENABLED, "false");
				currentSettings.put(RECENT_EXCEPTION_PARSING_DATA, "");
				
				//End setting default settings
				
				oos.writeObject(currentSettings); //save the hashmap to the harddrive
				oos.flush(); //Flush the stream (make sure, all data has been written to the harddrive, before closing the stream)
				oos.close(); //Close the stream
			}else{ //A file was found, just load the settings
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(settings)); //Create a InputStream that can read the class we wrote earlier to the harddrive
				currentSettings = (HashMap<String, String>) ois.readObject(); //Just assume, that the read file is an instance of an hashmap
				ois.close(); //close the inputstream
			}
			
			if(getArchiveOldLogFiles()){ //if older logfiles should be zipped, we do so here
				zipOlderLogFiles(); //Zip the older log-files
			}
			
		}catch(Exception e){ //An unexpected exception was thown while reading the settings
			ExceptionReporter.showDialog(null, e, true); //Show the exception in the UI
		}
		ensureEntryExists(MOST_RECENT_CONNECTION_KEY, "usb"); //Ensure, that certain entries exist
		ensureEntryExists(MOST_RECENT_NXT_KEY, "");
	}
	
	/**
	 * Reloads the settings from the harddrive
	 */
	public void reload(){
		loadCurrentSettings();
	}
	
	/**
	 * Writes older logfiles into a zip in the default settingsfolder
	 * @throws Exception if something went wrong while zipping the old files
	 */
	private void zipOlderLogFiles() throws Exception{
		File[] oldLogFiles = getCurrentLogFileFolder().listFiles(new FileFilter(){
			@Override
			public boolean accept(File pathname){
				return pathname.getAbsolutePath().endsWith(".txt");
			}
		}); //Get a list of all Files, that need to be archived (all files that end with .txt in the specific folder)
		
		File archive = new File(getCurrentLogFileFolder(), "\\archive.zip");
		if(!archive.exists()){ //Check if the archive file exists already
			//The archive does not exist, we make now sure that is exists and is formatted in the proper .zip-format
			archive.createNewFile();
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(archive));
			out.closeEntry();
			out.finish();
			out.finish();
			out.close();
		}
		
		URI zip = new URI("jar:" + archive.toURI()); //Create an URI in order to write into the .zip-archive
		HashMap<String, String> env = new HashMap<String, String>();
		env.put("create", "true");
		try(FileSystem fs = FileSystems.newFileSystem(zip, env);){ //Create an FileSystem in order to write to the .zip-file
			for(File log: oldLogFiles){ //For every file we found earlier, do following:
				Path location = fs.getPath(log.getName()); //Create a path into the zip, where we wish the file to be located
				if(log.length() > 0){ //if the file is not empty, we copy it, otherwise we just delete it
					Files.copy(Paths.get(log.toURI()), location, StandardCopyOption.REPLACE_EXISTING); //File's not empty, we copy it to the archive, and replace any file, that has the same name as the file we try to copy
				}
				
				log.delete(); //At the end of the process, just try to delete the file
			}
		}
	}
	
	/**
	 * Makes sure, that an requested enty exists in the settings
	 * @param key The key for that specific setting
	 * @param defaultValue the defaultvalue for the specific setting
	 */
	private void ensureEntryExists(String key, String defaultValue){
		if(!currentSettings.containsKey(key)){ //Check, if the setting exists
			currentSettings.put(key, defaultValue); //The setting does not exist, create it with the default-value
		}
	}
	
	/**
	 * Puts a new value for a given key into the settings
	 * If necassary, older values are overwritten. If there is no such setting, it will be created
	 * @param key
	 * @param value
	 */
	public void put(String key, String value){
		currentSettings.put(key, value);
	}
	
	/**
	 * Gets the current message, that this program is using
	 * @return the current language
	 */
	public String getLanguage(){
		ensureEntryExists(LANGUAGE_KEY, "german");
		return currentSettings.get(LANGUAGE_KEY);
	}
	
	/**
	 * Returns, wether changes of settings should be saved automatically
	 * @return <code>true</code> if settings should be saved immediately after changing, <code>false</code> otherwise
	 */
	public boolean getAutmaticallySaveSettings(){
		ensureEntryExists(SAVE_AUTOMATICALLY_KEY, "true");
		return Boolean.parseBoolean(currentSettings.get(SAVE_AUTOMATICALLY_KEY));
	}
	
	/**
	 * Returns, wether changes of datafields should uploaded automatically
	 * @return <code>true</code> if values should be uploaded immediately after changing, <code>false</code> otherwise
	 */
	public boolean getUploadChangesAutomatically(){
		ensureEntryExists(UPLOAD_CHANGES_DIRECTLY_KEY, "false");
		return Boolean.parseBoolean(currentSettings.get(UPLOAD_CHANGES_DIRECTLY_KEY));
	}
	
	/**
	 * Returns, wether a dialog should be shown, before deleting a datafield
	 * @return <code>true</code> if a dialog should be shown, <code>false</code> otherwise
	 */
	public boolean getDeleteRowsWithoutDialog(){
		ensureEntryExists(DELETE_ROWS_DIRECTLY_KEY, "false");
		return Boolean.parseBoolean(currentSettings.get(DELETE_ROWS_DIRECTLY_KEY));
	}
	
	/**
	 * Returns, wether a dialog should be shown, when a new datafield is created
	 * @return <code>true</code> if a dialog should be shown, <code>false</code> otherwise
	 */
	public boolean getCreateNewRowsWithDialog(){
		ensureEntryExists(CREATE_ROWS_WITH_DIALOG_KEY, "true");
		return Boolean.parseBoolean(currentSettings.get(CREATE_ROWS_WITH_DIALOG_KEY));
	}
	
	/**
	 * Returns, wether changes should be uploaded incementally to the NXT
	 * @return <code>true</code> if the changes should be uploaded incementally, <code>false</code> otherwise
	 */
	public boolean getUploadChangesIncrementally(){
		ensureEntryExists(UPLOAD_CHANGES_DIRECTLY_KEY, "false");
		return Boolean.parseBoolean(currentSettings.get(UPLOAD_CHANGES_INCREMENTALLY_KEY));
	}
	
	/**
	 * Returns, wether Logfiles should be created automatically
	 * @return <code>true</code> if longfiles should be created automatically, <code>false</code> otherwise
	 */
	public boolean getCreateLogFilesAutomatically(){
		ensureEntryExists(CREATE_AUTOMATIC_LOG_FILES_KEY, "true");
		return Boolean.parseBoolean(currentSettings.get(CREATE_AUTOMATIC_LOG_FILES_KEY));
	}
	
	/**
	 * @return the current folder for logfiles and the archive for older logfiles
	 */
	public File getCurrentLogFileFolder(){
		ensureEntryExists(CURRENT_LOG_FOLDER_KEY, System.getProperty("user.home") + "\\nxt\\");
		return new File(currentSettings.get(CURRENT_LOG_FOLDER_KEY));
	}
	/**
	 * 
	 * @return <code>true</code> if the UI should autoscroll the Textpane, when new debugging-messages arrive, <code>false</code> otherwise
	 */
	public boolean getNxtDebuggingAutoscrollActive(){
		ensureEntryExists(AUTOSCROLL_ACTIVE_KEY, "true");
		return Boolean.parseBoolean(currentSettings.get(AUTOSCROLL_ACTIVE_KEY));
	}
	
	/**
	 * @return <code>true</code> if old logfiles should be archived in a zipfile, <code>false</code> otherwise
	 */
	public boolean getArchiveOldLogFiles(){
		ensureEntryExists(ARCHIVE_OLD_LOG_FILES_KEY, "true");
		return Boolean.parseBoolean(currentSettings.get(ARCHIVE_OLD_LOG_FILES_KEY));
	}
	
	/**
	 * @return <code>true</code> if exceptionparsing is enabled, <code>false</code> otherwise
	 */
	public boolean getExceptionparsingEnabled(){
		ensureEntryExists(EXCEPTIONPARSING_ENABLED, "false");
		return Boolean.parseBoolean(currentSettings.get(EXCEPTIONPARSING_ENABLED));
	}
	
	public String getRecentExceptionParsingData(){
		ensureEntryExists(RECENT_EXCEPTION_PARSING_DATA, "");
		return currentSettings.get(RECENT_EXCEPTION_PARSING_DATA);
	}
	
	/**
	 * @return the NXT-name of the last NXT the program was connected with; if there was no earlier connection, the String is empty
	 */
	public String getMostRecentNxtName(){
		ensureEntryExists(MOST_RECENT_NXT_KEY, "");
		return currentSettings.get(MOST_RECENT_NXT_KEY);
	}
	
	/** 
	 * @return the NXT-connectiontype of the last NXT the program was connected with; if there was no earlier connection, the Connectiontype is USB
	 */
	public ConnectionType getMostRecentConnection(){
		ensureEntryExists(MOST_RECENT_CONNECTION_KEY, "usb");
		if(!currentSettings.get(MOST_RECENT_CONNECTION_KEY).isEmpty()){
			if(currentSettings.get(MOST_RECENT_CONNECTION_KEY).equalsIgnoreCase("usb")){
				return ConnectionType.USB;
			}else{
				return ConnectionType.BLUETOOTH;
			}
		}
		return ConnectionType.USB;
	}
	
	/**
	 * Loads all recent NXTs the program was connected with, in order to show them for easy selection in the ConnectToNXTDialog
	 * @param connectionType The Connectiontype that is currently selected in the UI
	 * @return An NXTInfo[] with all NXTs, that were connected earlier with this PC with the given ConnectionType
	 */
	public NXTInfo[] getRecentNXTInfo(ConnectionType connectionType){
		ensureEntryExists(RECENT_NXTS_CACHE_KEY, ""); //Make sure, we have an valid entry for what we need here
		String data = currentSettings.get(RECENT_NXTS_CACHE_KEY); //load the data
		if(data == null || data.isEmpty()){ //is the data valid?
			return new NXTInfo[] {}; //No, data is not valid, return an empty array
		}else{ //Data is valid, resolve the data
			String[] rawInfo = data.split(";"); //every entry is separated by an ";"
			NXTInfo[] result = new NXTInfo[rawInfo.length]; //create an array with the length of all earlier connections we had, including both connectiontypes
			int count = 0; //Count-variable
			for(String rawNxtInfo: rawInfo){ //Filter all NXTs out, that do not have the Connectiontype we are seaching for
				String[] nxtInfo = rawNxtInfo.split(":");
				if(nxtInfo[2].equalsIgnoreCase(connectionType.getId() + "")){ //The current NXT has the connectionType we are looking for
					result[count++] = new NXTInfo((connectionType.getId() == 0 ? NXTCommFactory.USB : NXTCommFactory.BLUETOOTH), nxtInfo[0], nxtInfo[1]); //Add the current NXT to the Arraylist
				}
			}
			NXTInfo[] finalResult = new NXTInfo[count]; //create an empty resultarray with as much valid entries we have
			System.arraycopy(result, 0, finalResult, 0, count); //copy all valid entries we have in the earlier creates result-array to the finalResult-array
			return finalResult; //return only the valid values in a trimmed array
		}
	}
	
	/**
	 * Returns the NXTInfo for a specific NXT and ConnectionType
	 * @param name the NXT's name
	 * @param connectionType the connectiontype we are currently using
	 * @return The NXTInfo for the requested NXT, or null if it was not found
	 */
	public NXTInfo getNXTInfoOf(String name, ConnectionType connectionType){
		NXTInfo[] data = getRecentNXTInfo(connectionType);
		
		for(NXTInfo nxt: data){
			if(nxt != null && nxt.name.equals(name)){
				return nxt;
			}
		}
		return null;
	}
	
	/**
	 * Add a new NXT to the cache of recent NXTs we were connected with
	 * @param nxtName the name of the NXT
	 * @param nxtAddress the address of the NXT
	 * @param connectionType the connectiontype that was used for connection to that NXT
	 */
	public void addRecentNxtData(String nxtName, String nxtAddress, ConnectionType connectionType){
		String data = SINGLETONE.currentSettings.get(RECENT_NXTS_CACHE_KEY);
		if(data.isEmpty()){
			data += nxtName + ":" + nxtAddress + ":" + (connectionType == ConnectionType.USB ? 0 : 1);
		}else{
			data += ";" + nxtName + ":" + nxtAddress + ":" + (connectionType == ConnectionType.USB ? 0 : 1);
		}
		SINGLETONE.currentSettings.put(RECENT_NXTS_CACHE_KEY, data);
		
		SINGLETONE.saveCurrentSettings();
		
	}
	
	/**
	 * Saves the current settings to the harddive
	 */
	public void saveCurrentSettings(){
//		if(!chckbxSaveNewSettingsDirectly.isSelected()) return; TODO FIX
		try{
			File settings = new File(System.getProperty("user.home") + "\\nxt\\settings.dat");
			if(!settings.exists()){ //Create a new file for our settings, if it does not exist
				settings.getParentFile().mkdirs();
				settings.createNewFile();
			}
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(settings)); //Get a stream ready, that can write out object to the PC's harddrive
			oos.writeObject(currentSettings); //write the Object
			oos.flush(); //Make sure, that the object was actually written to the harddrive and is in no buffer anymore
			oos.close(); //Close the Stream
		}catch(Exception e){ //An unexpected Error occured
			ExceptionReporter.showDialog(null, e, true); //Show the Error
		}
	}
	
	
	/**
	 * Get the current instance of this Class
	 * @return the current Instance of this class
	 */
	public static SettingsManager getSingletone(){
		ensureSingletoneExists(); //Make sure, SINGLETONE exists
		return SINGLETONE;
	}
	
	/**
	 * Makes sure, that SINGLETONE is not empty in order to prevent NullPointerExceptions
	 * If Singletone is null, a new instance is created
	 */
	private static void ensureSingletoneExists(){
		if(SINGLETONE == null){
			SINGLETONE = new SettingsManager();
		}
	}
	
}