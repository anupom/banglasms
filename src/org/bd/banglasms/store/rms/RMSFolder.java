package org.bd.banglasms.store.rms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import org.bd.banglasms.store.BanglaMessage;
import org.bd.banglasms.store.BanglaMessageEnumerator;
import org.bd.banglasms.store.Folder;
import org.bd.banglasms.store.FolderListener;
import org.bd.banglasms.store.StoreException;

/**
 * Implementation of Folder using RMS package.
 *
 */
public class RMSFolder implements Folder {

	private String storeName;
	private Vector folderListeners = new Vector();
	private RecordStore recordStore;
	private int id;
	private boolean isOpen = false;

	public RMSFolder(String storeName, int id) {
		this.storeName = storeName;
		this.id = id;
	}

	public void addFolderListener(FolderListener folderListener) {
		if(folderListener != null){
			this.folderListeners.addElement(folderListener);
		}
	}

	public void addMessage(BanglaMessage message) throws StoreException {
		checkOpen();
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(byteStream);
		try {
			message.write(stream);
		} catch (IOException e) {
			throw new StoreException(e);//should not happen though!!
		}
		byte[] data = byteStream.toByteArray();
		try {
			int recordId = recordStore.addRecord(data, 0, data.length);
			message.setStoreReference(new Integer(recordId));
			notifyMessageAdded(message);
		} catch (RecordStoreNotOpenException e) {
			throw new StoreException(StoreException.REASON_STORE_NOT_OPEN, e);
		} catch (RecordStoreFullException e) {
			throw new StoreException(StoreException.REASON_STORE_FULL, e);
		} catch (RecordStoreException e) {
			throw new StoreException(e);
		}
	}

	public void updateMessage(BanglaMessage message) throws StoreException {
		checkOpen();
		int storeReference = getReference(message.getStoreReference());
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(byteStream);
		try {
			message.write(stream);
		} catch (IOException e) {
			throw new StoreException(e);//should not happen though!!
		}
		byte[] data = byteStream.toByteArray();
		try {
			recordStore.setRecord(storeReference, data, 0, data.length);
		} catch (RecordStoreNotOpenException e) {
			throw new StoreException(StoreException.REASON_STORE_NOT_OPEN, e);
		} catch (InvalidRecordIDException e) {
			throw new StoreException(StoreException.REASON_INVALID_STORE_LOCATION, e);
		} catch (RecordStoreFullException e) {
			throw new StoreException(StoreException.REASON_STORE_FULL, e);
		} catch (RecordStoreException e) {
			throw new StoreException(e);
		}
	}

	private void notifyMessageAdded(BanglaMessage message) {
		for(int i = 0; i < this.folderListeners.size(); i++){
			((FolderListener)folderListeners.elementAt(i)).messageAdded(message);
		}
	}

	private void notifyMessageRemoved(BanglaMessage message) {
		for(int i = 0; i < this.folderListeners.size(); i++){
			((FolderListener)folderListeners.elementAt(i)).messageRemoved(message);
		}
	}

	public void close(){
		if(isOpen){
			try {
				recordStore.closeRecordStore();
				isOpen = false;
			} catch (RecordStoreNotOpenException e) {
				System.err.println("RMSFolder.close() caugh Exception. Store name=" + storeName + " Ex = " + e);
			} catch (RecordStoreException e) {
				System.err.println("RMSFolder.close() caugh Exception. Store name=" + storeName + " Ex = " + e);
			}
		}
	}

	public int getId() {
		return this.id;
	}

	public BanglaMessage getMessage(Object storeReference)
			throws StoreException {
		int recordId = getReference(storeReference);
		checkOpen();
		byte[] data = null;
		try {
			data = recordStore.getRecord(recordId);
		} catch (RecordStoreNotOpenException e) {
			throw new StoreException(StoreException.REASON_STORE_NOT_OPEN, e);
		} catch (InvalidRecordIDException e) {
			throw new StoreException(StoreException.REASON_INVALID_STORE_LOCATION, e);
		} catch (RecordStoreException e) {
			throw new StoreException(StoreException.REASON_OTHERS, e);
		}
		try{
			BanglaMessage message = new BanglaMessage(this.getId());
			message.read(new DataInputStream(new ByteArrayInputStream(data)));
			message.setStoreReference(storeReference);
			return message;
		}catch(IOException e){
			throw new StoreException(StoreException.REASON_INVALID_DATA_FORMAT, e);
		}
	}

	public int getMessageCount() throws StoreException {
		checkOpen();
		try {
			return recordStore.getNumRecords();
		} catch (RecordStoreNotOpenException e) {
			throw new StoreException(StoreException.REASON_STORE_NOT_OPEN, e);
		}
	}

	public void open() throws StoreException {
		if(!this.isOpen){
			try {
				recordStore = RecordStore.openRecordStore(this.storeName, true);
			} catch (RecordStoreFullException e) {
				throw new StoreException(StoreException.REASON_STORE_FULL, e);
			} catch (RecordStoreNotFoundException e) {
				throw new StoreException(StoreException.REASON_STORE_NOT_FOUND, e);
			} catch (RecordStoreException e) {
				throw new StoreException(e);
			}
			isOpen = true;
		}
	}

	public void removeFolderListener(FolderListener folderListener) {
		this.folderListeners.removeElement(folderListener);
	}

	public void removeMessage(BanglaMessage message) throws StoreException {
		Object storeReference = message.getStoreReference();
		remove(storeReference, false);
		notifyMessageRemoved(message);
	}

	public void removeMessage(Object storeReference) throws StoreException {
		remove(storeReference, true);
	}

	private void remove(Object storeReference, boolean notify) throws StoreException{
		checkOpen();
		int recordId = getReference(storeReference);
		BanglaMessage message = null;
		if(notify){
			message = getMessage(storeReference);
		}
		try {
			recordStore.deleteRecord(recordId);
			if(notify){
				notifyMessageRemoved(message);
			}
		} catch (RecordStoreNotOpenException e) {
			throw new StoreException(StoreException.REASON_STORE_NOT_OPEN, e);
		} catch (InvalidRecordIDException e) {
			throw new StoreException(StoreException.REASON_INVALID_STORE_LOCATION, e);
		} catch (RecordStoreException e) {
			throw new StoreException(StoreException.REASON_OTHERS, e);
		}
	}

	private void checkOpen() throws StoreException{
		if(!isOpen){
			throw new StoreException(StoreException.REASON_STORE_NOT_OPEN);
		}
	}

	private int getReference(Object storeReference){
		if(storeReference == null){
			throw new IllegalArgumentException("Reference must not be null");
		}
		if(!(storeReference instanceof Integer)){
			throw new IllegalArgumentException("RMSFolder reference type must be Integer, but it is " + storeReference.getClass());
		}
		return ((Integer)storeReference).intValue();
	}

	public void reset() throws StoreException {
		this.close();
		try {
			RecordStore.deleteRecordStore(this.storeName);
		} catch (RecordStoreNotFoundException e) {
			//no problem, not found!
		} catch (RecordStoreException e) {
			throw new StoreException(e);
		}
		open();

	}

	public BanglaMessageEnumerator getEnumerator() throws StoreException {
		checkOpen();
		return new BanglaMessageEnumeratorImpl();
	}

	private class BanglaMessageEnumeratorImpl implements org.bd.banglasms.store.BanglaMessageEnumerator{

		RecordEnumeration recordEnumeration ;

		public BanglaMessageEnumeratorImpl() throws StoreException{
			try {
				recordEnumeration = recordStore.enumerateRecords(null, null, false);//TODO TRY with true
			} catch (RecordStoreNotOpenException e) {
				throw new StoreException(StoreException.REASON_STORE_NOT_OPEN, e);
			}
		}

		public boolean hasMoreMessage() throws StoreException {
			return recordEnumeration.hasNextElement();
		}
		
		public BanglaMessage nextMessage() throws StoreException {
			hasMoreMessage();

			int recordId;
			try {
				recordId = recordEnumeration.nextRecordId();
			} catch (InvalidRecordIDException e) {
				throw new StoreException(StoreException.REASON_INVALID_STORE_LOCATION, e);
			}
			return getMessage(new Integer(recordId));

		}
	}

}
