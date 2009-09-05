package org.bd.banglasms.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotFoundException;

import org.bd.banglasms.App;
import org.bd.banglasms.Logger;
import org.bd.banglasms.Properties;

/**
 * {@link Properties} implementation using RMS.
 *
 */
public class PropertiesImpl implements Properties {

	private Hashtable table;
	private String rmsName = "BanglaSMS.properties";
	private boolean loaded = false;

	/**
	 * Default constructor.
	 */
	public PropertiesImpl () {
		table = new Hashtable ();
	}

	public synchronized String getProperty(String key) {
		if( !loaded ){
			loaded = true;
			load();
		}		
		if ( ! table.containsKey(key)) {
			return null;
		}
		return (String)table.get(key);
	}

	public synchronized void setProperty(String key, String value) {
		if (key == null || value == null) {
			throw new IllegalArgumentException ("key or value cannot be null");
		}
		table.put(key, value);
		save();
	}

	protected synchronized void load(){
		table.clear();
		RecordStore recordStore = null;
		try {
			recordStore = RecordStore.openRecordStore(rmsName, true);
			if(recordStore.getNumRecords() != 0 && recordStore.getNumRecords() != 1) {
				App.getLogger().log("PropertiesImpl.load found more than 1 record, which is unusual. Clearing record store" , Logger.LEVEL_WARNING);
				//no record or more than 1 record, so somehow corrupted
				recordStore.closeRecordStore();
				recordStore = null;
				RecordStore.deleteRecordStore(rmsName);
			} else if (recordStore.getNumRecords() == 1){
				RecordEnumeration records = recordStore.enumerateRecords(null, null, false);
				byte[] data = records.nextRecord();
				DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(data));
				int count = inputStream.readInt();
				for(int i = 0; i < count ; i++) {
					String key = inputStream.readUTF();
					String value = inputStream.readUTF();
					table.put(key, value);
				}
			}
		} catch (RecordStoreException ex) {
			App.getLogger().log("PropertiesImpl.load caught " + ex, Logger.LEVEL_ERROR);
		} catch (IOException ex){
			App.getLogger().log("PropertiesImpl.load caught " + ex, Logger.LEVEL_ERROR);
		}finally {
			if(recordStore != null) {
				try {
					recordStore.closeRecordStore();
				} catch (RecordStoreException ex) {
					App.getLogger().log("PropertiesImpl.load caught while closing " + ex, Logger.LEVEL_ERROR);
				}
			}
		}
	}

	protected synchronized void save(){
		RecordStore recordStore = null;
		try {
			try {
				RecordStore.deleteRecordStore(rmsName);//kinda risky to delete before fully saving... well someone has time to fix?
			} catch (RecordStoreNotFoundException ex) {
				//ignore, not found
			}
			recordStore = RecordStore.openRecordStore(rmsName, true);
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			DataOutput outputStream = new DataOutputStream(byteStream);
			outputStream.writeInt(table.size());
			Enumeration keys = table.keys();
			while(keys.hasMoreElements()){
				String key = (String)keys.nextElement();
				String value = (String)table.get(key);
				outputStream.writeUTF(key);
				outputStream.writeUTF(value);
			}
			byte[] data = byteStream.toByteArray();
			recordStore.addRecord(data, 0, data.length);

		} catch (RecordStoreException ex) {
			App.getLogger().log("PropertiesImpl.load caught " + ex, Logger.LEVEL_ERROR);
		} catch (IOException ex){
			App.getLogger().log("PropertiesImpl.load caught " + ex, Logger.LEVEL_ERROR);
		}finally {
			if(recordStore != null) {
				try {
					recordStore.closeRecordStore();
				} catch (RecordStoreException ex) {
					App.getLogger().log("PropertiesImpl.load caught while closing " + ex, Logger.LEVEL_ERROR);
				}
			}
		}
	}
}
