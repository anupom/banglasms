package org.bd.banglasms.control.event;

/**
 * This interface is used to notify about an event occurred. 
 *
 */
public interface EventHandler {
	
	/**
	 * Called when an event has just occurred.
	 * @param event event that just occurred
	 */
	public void handleEvent(Event event);
}
