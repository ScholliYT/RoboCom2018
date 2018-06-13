package me.tomstein;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import nxt.connection.NxtDataField;
import nxt.object.DataFieldType;

//Lineformat: datenfeldname=type&wertAlsString

public class DataSaver{
	
	private final File data = new File("robocomSettings.cfg");
	
	private static final DataSaver SINGLETONE = new DataSaver();
	
	private DataSaver(){
		try{
			if(!data.exists()){
				data.createNewFile();
			}
		}catch(Exception e){}
	}
	
	public NxtDataField[] loadSettings() throws Exception{
		if(!data.exists() || data.length() <= 0){
			return new NxtDataField[0];
		}
		
		ArrayList<NxtDataField> loaded = new ArrayList<>();
		
		InputStream in = new FileInputStream(data);
		String buffer = "";
		char read;
		String name;
		DataFieldType type;
		String valueAsString;
		
		while(in.available() > 0){
			read = (char) in.read();
			buffer += String.valueOf(read);
			if(buffer.charAt(buffer.length()-1) == '\n'){
				buffer = buffer.substring(0, buffer.length()-1);
				if(buffer.isEmpty() || buffer.length() < 4) continue;
				name = buffer.substring(0, buffer.indexOf('='));
				type = DataFieldType.getDataFieldTypeFromString(buffer.substring(buffer.indexOf('=')+1, buffer.indexOf('&')));
				valueAsString = buffer.substring(buffer.indexOf('&')+1);
				Object value = null;
				
				
				switch(type){
					case STRING:
						value = valueAsString;
						break;
					case FLOAT:
						value = Float.parseFloat(valueAsString);
						break;
					case DOUBLE:
						value = Double.parseDouble(valueAsString);
						break;
					case INTEGER:
						value = Integer.parseInt(valueAsString);
						break;
					case LONG:
						value = Long.parseLong(valueAsString);
						break;
					case BOOLEAN:
						value = Boolean.parseBoolean(valueAsString);
						break;
					default:
						value = valueAsString;
						break;
			}
			loaded.add(new NxtDataField(name, type, value));
			buffer = "";
			}else{ //Analyse
				
			}
		}
		
//		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(data)));
////		String buffer = "";
//		while((buffer = br.readLine()) != null && !buffer.isEmpty()){
//			if(buffer.isEmpty() || buffer.equalsIgnoreCase("end")) break;
////			String name = buffer.substring(0, buffer.indexOf('='));
////			DataFieldType type = DataFieldType.getDataFieldTypeFromString(buffer.substring(buffer.indexOf('=')+1, buffer.indexOf('&')));
////			String valueAsString = buffer.substring(buffer.indexOf('&')+1);
////			Object value = null;
//			switch(type){
//				case STRING:
//					value = valueAsString;
//					break;
//				case FLOAT:
//					value = Float.parseFloat(valueAsString);
//					break;
//				case DOUBLE:
//					value = Double.parseDouble(valueAsString);
//					break;
//				case INTEGER:
//					value = Integer.parseInt(valueAsString);
//					break;
//				case LONG:
//					value = Long.parseLong(valueAsString);
//					break;
//				case BOOLEAN:
//					value = Boolean.parseBoolean(valueAsString);
//					break;
//				default:
//					value = valueAsString;
//					break;
//			}
//			loaded.add(new NxtDataField(name, type, value));
//		}
		in.close();
		return loaded.toArray(new NxtDataField[loaded.size()]);
	}
	
	public void saveData(Object... toSave) throws IOException{
		data.delete();
		data.createNewFile();
//		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(data)));
		OutputStream out = new FileOutputStream(data);
		for(NxtDataField field: validateDatafields(toSave)){
//			writer.write(field.getName() + "=" + field.getType().toString() + "&" + field.getValue().toString());
			out.write((field.getName() + "=" + field.getType().toString() + "&" + field.getValue().toString() + '\n').getBytes());
//			writer.newLine();
		}
		out.flush();
		out.close();
//		writer.flush();
//		writer.close();
	}
	
	private ArrayList<NxtDataField> validateDatafields(Object[] data) throws IllegalArgumentException{
		if(data.length % 2 != 0) throw new IllegalArgumentException("Die Datafields im PCConnector muessen eine gerade Anzahl haben! (Ein Datenfeldname und ein Wert)");
		ArrayList<NxtDataField> list = new ArrayList<>();
		String currentName = "";
		for(int i = 0; i < data.length; i++){
			if((i % 2) == 0){ //Aktuell ist ein Datenfeldname am Start
				if(data[i] instanceof String && !((String) data[i]).isEmpty()){
					currentName = (String) data[i];
				}else{
					throw new IllegalArgumentException("Ein Datenfeldname ist kein String oder leer! (i = " + i + ")");
				}
			}else{
				Object field = data[i];
				DataFieldType type = DataFieldType.guessDataFieldTypeFromObject(field);
				if(type == null){
					throw new IllegalArgumentException("guessDataFieldTypeFromObject(Object) hat null zurueckgegeben. Das Objekt " + i + " ist ungueltig.");
				}
				
				list.add(new NxtDataField(currentName, type, field));
				currentName = "";
			}
		}
		return list;
	}
	
	public static DataSaver getSingletone(){
		return SINGLETONE;
	}
	
}