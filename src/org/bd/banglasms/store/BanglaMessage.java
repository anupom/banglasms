package org.bd.banglasms.store;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Class that represents a Bangla text message.
 */
public class BanglaMessage {
	private String text;
	private String contactNumber;
	private String contactName;
	private int folderId;
	private boolean read;
	private long time;
	private Object storeReference;
	private static final String NULL = "$$$NULL###";//to store a null string
	public static final String UNKNOWN_CONTACT = "Unknown";

	public BanglaMessage(int folderId) {
		this(folderId, null, null);
	}

	public BanglaMessage(int folderId, String contactNumber, String contactName) {
		this.folderId = folderId;
		setContactNumber(contactNumber);
		setContactName(contactName);
		this.time = System.currentTimeMillis();
		setRead(true);//by default, assuming it is read
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setFolderId(int folderId) {
		this.folderId = folderId;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public int getFolderId() {
		return folderId;
	}

	/**
	 * Returns contact's name if found, else the phone number.
	 *
	 * @return contact's name if found, else the formatted phone number or {@link #UNKNOWN_CONTACT}
	 */
	public String getDisplayableContact() {
		if (contactName != null) {
			return contactName;
		} else {
			if(contactNumber != null){
				return contactNumber;
			}else{
				return UNKNOWN_CONTACT;
			}
		}
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public boolean isRead() {
		return read;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getTime() {
		return time;
	}

	public Object getStoreReference() {
		return storeReference;
	}

	public void setStoreReference(Object storeReference) {
		this.storeReference = storeReference;
	}

	public void write(DataOutputStream stream) throws IOException {
		stream.writeBoolean(read);
		stream.writeLong(time);
		writeString(stream, contactName);
		writeString(stream, contactNumber);
		writeString(stream, text);
	}

	public void read(DataInputStream stream) throws IOException {
		read = stream.readBoolean();
		time = stream.readLong();
		contactName = readString(stream);
		contactNumber = readString(stream);
		text = readString(stream);
	}

	private void writeString(DataOutputStream stream, String string)
			throws IOException {
		if (string == null) {
			stream.writeUTF(NULL);
		} else {
			stream.writeUTF(string);
		}
	}

	private String readString(DataInputStream stream) throws IOException {
		String string = stream.readUTF();
		if (NULL.equals(string)) {
			return null;
		} else {
			return string;
		}
	}

	public boolean equals(Object obj) {
		if(obj == null){
			return false;
		}
		else if(!(obj instanceof BanglaMessage)){
			return false;
		}else{
			BanglaMessage toCompare = (BanglaMessage)obj;
			if(getFolderId() == toCompare.getFolderId() &&
					getStoreReference() == toCompare.getStoreReference() &&
					((getText() == null && toCompare.getText() == null) || getText().equals(toCompare)) &&
					getTime() == toCompare.getTime() &&
					getContactNumber() == toCompare.getContactNumber()
					){
				return true;
			}
		}
		return false;

	}
}
