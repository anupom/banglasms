package org.bd.banglasms.store;

/**
 * Interface through which a folder updater its listener about message addition,
 * deletion etc.
 *
 */
public interface FolderListener {
	/**
	 * Called when a message has just been added to the Folder.
	 * @param message message that has been added
	 */
	public void messageAdded(BanglaMessage message);

	/**
	 * Called when a message has just been removed from the Folder.
	 * @param message message that has been removed
	 */
	public void messageRemoved(BanglaMessage message);
}
