package org.bd.banglasms.store;

/**
 * Enumerator that enumerates through the messages inside a folder (such as Inbox).
 */
public interface BanglaMessageEnumerator {

	/**
	 * Returns the next message from the folder
	 * @return the next message
	 * @throws StoreException in case of store operational error
	 */
	public BanglaMessage nextMessage() throws StoreException;

	/**
	 * Returns true if the folder has any more message.
	 * @return true if folder has more message or false
	 * @throws StoreException in case of store operational error
	 */
	public boolean hasMoreMessage() throws StoreException;

}
