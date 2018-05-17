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

public class SettingsManager{
	
	private static SettingsManager SINGLETONE;
	
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
	
	private HashMap<String, String> currentSettings;
	
	private SettingsManager(){
		this.currentSettings = new HashMap<String, String>();
		this.loadCurrentSettings();
	}
	
	@SuppressWarnings("unchecked")
	private void loadCurrentSettings(){
		try{
			File settings = new File(System.getProperty("user.home") + "\\nxt\\settings.dat");
			if(!settings.exists()){
				settings.getParentFile().mkdirs();
				settings.createNewFile();
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(settings));
				currentSettings = new HashMap<String, String>();
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
				
				oos.writeObject(currentSettings);
				oos.flush();
				oos.close();
			}else{
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(settings));
				currentSettings = (HashMap<String, String>) ois.readObject();
				ois.close();
			}
			
			if(getArchiveOldLogFiles()){
				zipOlderLogFiles();
			}
			
		}catch(Exception e){
			ExceptionReporter.showDialog(null, e);
		}
	}
	
	public void reload(){
		loadCurrentSettings();
	}
	
	private void zipOlderLogFiles() throws Exception{
		File[] oldLogFiles = getCurrentLogFileFolder().listFiles(new FileFilter(){
			@Override
			public boolean accept(File pathname){
				return pathname.getAbsolutePath().endsWith(".txt");
			}
		});
		
		File archive = new File(getCurrentLogFileFolder(), "\\archive.zip");
		if(!archive.exists()){
			archive.createNewFile();
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(archive));
			out.closeEntry();
			out.finish();
			out.finish();
			out.close();
		}
		
		URI zip = new URI("jar:" + archive.toURI());
		HashMap<String, String> env = new HashMap<String, String>();
		env.put("create", "true");
		try(FileSystem fs = FileSystems.newFileSystem(zip, env);){
			for(File log: oldLogFiles){
				Path location = fs.getPath(log.getName());
				if(log.length() > 0){
					Files.copy(Paths.get(log.toURI()), location, StandardCopyOption.REPLACE_EXISTING);
				}
				
				log.delete();
			}
		}
	}
	
	private void ensureEntryExists(String key, String defaultValue){
		if(!currentSettings.containsKey(key)){
			currentSettings.put(key, defaultValue);
		}
	}
	
	public void put(String key, String value){
		currentSettings.put(key, value);
	}
	
	public String getLanguage(){
		ensureEntryExists(LANGUAGE_KEY, "german");
		return currentSettings.get(LANGUAGE_KEY);
	}
	
	public boolean getAutmaticallySaveSettings(){
		ensureEntryExists(SAVE_AUTOMATICALLY_KEY, "true");
		return Boolean.parseBoolean(currentSettings.get(SAVE_AUTOMATICALLY_KEY));
	}
	
	public boolean getUploadChangesAutomatically(){
		ensureEntryExists(UPLOAD_CHANGES_DIRECTLY_KEY, "false");
		return Boolean.parseBoolean(currentSettings.get(UPLOAD_CHANGES_DIRECTLY_KEY));
	}
	
	public boolean getDeleteRowsWithoutDialog(){
		ensureEntryExists(DELETE_ROWS_DIRECTLY_KEY, "false");
		return Boolean.parseBoolean(currentSettings.get(DELETE_ROWS_DIRECTLY_KEY));
	}
	
	public boolean getCreateNewRowsWithDialog(){
		ensureEntryExists(CREATE_ROWS_WITH_DIALOG_KEY, "true");
		return Boolean.parseBoolean(currentSettings.get(CREATE_ROWS_WITH_DIALOG_KEY));
	}
	
	public boolean getUploadChangesIncrementally(){
		ensureEntryExists(UPLOAD_CHANGES_DIRECTLY_KEY, "false");
		return Boolean.parseBoolean(currentSettings.get(UPLOAD_CHANGES_INCREMENTALLY_KEY));
	}
	
	public boolean getCreateLogFilesAutomatically(){
		ensureEntryExists(CREATE_AUTOMATIC_LOG_FILES_KEY, "true");
		return Boolean.parseBoolean(currentSettings.get(CREATE_AUTOMATIC_LOG_FILES_KEY));
	}
	
	public File getCurrentLogFileFolder(){
		ensureEntryExists(CURRENT_LOG_FOLDER_KEY, System.getProperty("user.home") + "\\nxt\\");
		return new File(currentSettings.get(CURRENT_LOG_FOLDER_KEY));
	}
	
	public boolean getNxtDebuggingAutoscrollActive(){
		ensureEntryExists(AUTOSCROLL_ACTIVE_KEY, "true");
		return Boolean.parseBoolean(currentSettings.get(AUTOSCROLL_ACTIVE_KEY));
	}
	
	public boolean getArchiveOldLogFiles(){
		ensureEntryExists(ARCHIVE_OLD_LOG_FILES_KEY, "true");
		return Boolean.parseBoolean(currentSettings.get(ARCHIVE_OLD_LOG_FILES_KEY));
	}
	
	public NXTInfo[] getRecentNXTInfo(ConnectionType connectionType){
		ensureEntryExists(RECENT_NXTS_CACHE_KEY, "");
		String data = currentSettings.get(RECENT_NXTS_CACHE_KEY);
		if(data == null || data.isEmpty()){
			return new NXTInfo[] {};
		}else{
			String[] rawInfo = data.split(";");
			NXTInfo[] result = new NXTInfo[rawInfo.length];
			int count = 0;
			for(String rawNxtInfo: rawInfo){
				String[] nxtInfo = rawNxtInfo.split(":");
				if(nxtInfo[2].equalsIgnoreCase(connectionType.getId() + "")){
					result[count++] = new NXTInfo((connectionType.getId() == 0 ? NXTCommFactory.USB : NXTCommFactory.BLUETOOTH), nxtInfo[0], nxtInfo[1]);
				}
			}
			NXTInfo[] finalResult = new NXTInfo[count];
			System.arraycopy(result, 0, finalResult, 0, count);
			return finalResult;
		}
	}
	
	public NXTInfo getNXTInfoOf(String name, ConnectionType connectionType){
		NXTInfo[] data = getRecentNXTInfo(connectionType);
		
		for(NXTInfo nxt: data){
			if(nxt != null && nxt.name.equals(name)){
				return nxt;
			}
		}
		return null;
	}
	
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
	
	public void saveCurrentSettings(){
//		if(!chckbxSaveNewSettingsDirectly.isSelected()) return; TODO FIX
		try{
			File settings = new File(System.getProperty("user.home") + "\\nxt\\settings.dat");
			if(!settings.exists()){
				settings.getParentFile().mkdirs();
				settings.createNewFile();
			}
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(settings));
			oos.writeObject(currentSettings);
			oos.flush();
			oos.close();
		}catch(Exception e){
			ExceptionReporter.showDialog(null, e);
		}
	}
	
	
	
	public static SettingsManager getSingletone(){
		ensureSingletoneExists();
		return SINGLETONE;
	}
	
	private static void ensureSingletoneExists(){
		if(SINGLETONE == null){
			SINGLETONE = new SettingsManager();
		}
	}
	
}