package org.bd.banglasms.store;


/**
 * Represents a container that holds a list of <code>BanglaMessage</code>
 * objects, for example, Inbox.
 * </p>
 * To listen for any FolderEvent affecting a folder invoke addFolderListener.
 *
 */
public interface Folder {
	
	/**
	 * Opens the folder. To perform any read-write action on folder, folder must be opened first.
	 * @throws StoreException in case of error
	 */
	public void open() throws StoreException;
	
	/**
	 * Closes the folder. If the folder is already closed, this method has no effect. After read-write action,
	 * if the folder is no longer in use, it is recommended to close the folder.
	 */
	public void close();

	/**
	 * Returns the number of messages in the folder.
	 * @return the number of messages in the folder
	 * @throws StoreException in case of error in read-write or if the folder is not open
	 */
	public int getMessageCount() throws StoreException;
	
	/**
	 * Returns the message referred by the <code>storeReference</code>
	 * @param storeReference reference of the Message which contains location information for the message in the Folder 
	 * @throws StoreException in case of error in read-write or if the folder is not open
	 * @throws {@link IllegalArgumentException} if <code>storeReference</code> is null or of invalid type
	 */
	public BanglaMessage getMessage(Object storeReference) throws StoreException;
	
	/**
	 * Adds a {@link FolderListener}.
	 * @param folderListener listener to be added
	 */	
	public void addFolderListener(FolderListener folderListener);
	
	/**
	 * Removes the {@link FolderListener}.
	 * @param folderListener listener to be removed
	 */
	public void removeFolderListener(FolderListener folderListener);
	
	/**
	 * Returns the Id of the folder.
	 * @return id of the folder, which is an unique number for the folder
	 */
	public int getId();

	/**
	 * Adds the message to store and updates the store reference
	 * @param message Message to be added
	 * @throws StoreException in case of error in read-write or if the folder is not open
	 */
	public void addMessage(BanglaMessage message) throws StoreException;
	
	/**
	 * Updates the message and its store reference
	 * @param message Message to be added
	 * @throws StoreException in case of error in read-write or if the folder is not open
	 */
	public void updateMessage(BanglaMessage message) throws StoreException;
	
	/**
	 * Removes the message from Folder
	 * @param message Message to be removed
	 * @throws StoreException in case of error in read-write or if the folder is not open
	 */
	public void removeMessage(BanglaMessage message) throws StoreException;
	
	/**
	 * Removes the message from Folder
	 * @param storeReference reference of the message that is to be removed
	 * @throws StoreException in case of error in read-write or if the folder is not open
	 */
	public void removeMessage(Object storeReference) throws StoreException;
	
	/**
	 * Resets the folder by cleaning all storage information
	 * @throws StoreException in case of error in read-write
	 */
	public void reset() throws StoreException;
	
	/**
	 * Returns an {@link BanglaMessageEnumerator} that traverses through all the messages in the Folder.
	 * @return an Enumerator that traverses through all the messages in the Folder
	 * @throws StoreException in case of error in read-write
	 */
	public BanglaMessageEnumerator getEnumerator() throws StoreException;
}
