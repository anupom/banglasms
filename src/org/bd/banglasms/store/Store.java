package org.bd.banglasms.store;

import org.bd.banglasms.store.rms.RMSFolder;

/**
 * Store holds the folders, such as Inbox, Sent etc.
 */
public class Store {

	public static final int INBOX = 0;
	public static final int SENT = 1;
	public static final int DRAFT = 2;
	public static final int TEMPLATE = 3;

	private static final String STORE_NAME_INITIAL = "banglasms";
	private static final int STORE_VERSION = 2;
	private static final Folder[] FOLDERS = new Folder[4];

	static{
		for(int id = 0; id < 4 ; id++){
			FOLDERS[id] = new RMSFolder(getRMSStoreName(id), id);
		}
	}

	public static Folder getFolder(int id){
		if(id < 0 || id > 3){
			throw new IllegalArgumentException("Folder id is invalid");
		}
		return FOLDERS[id];
	}

	private static String getRMSStoreName(final int id){
		return STORE_NAME_INITIAL + "v" + STORE_VERSION + id;
	}	
}
