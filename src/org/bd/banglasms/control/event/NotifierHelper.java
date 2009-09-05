package org.bd.banglasms.control.event;

import java.util.Vector;

/**
 * This utility class can be used by any class that needs to notify others about
 * some events.
 * 
 */
public class NotifierHelper {
	private Vector eventHandlers = new Vector();

	/**
	 *
	 * @param eventHandler
	 * @throws IllegalArgumentException if parameter is null
	 */
	public void add(EventHandler eventHandler){
		if(eventHandler == null){
			throw new IllegalArgumentException("EventHandler cannot be null");
		}
		eventHandlers.addElement(eventHandler);
	}

	/**
	 *
	 * @param eventHandler
	 * @throws IllegalArgumentException if parameter is null
	 */
	public void remove(EventHandler eventHandler){
		if(eventHandler == null){
			throw new IllegalArgumentException("EventHandler cannot be null");
		}
		eventHandlers.removeElement(eventHandler);
	}

	/**
	 *
	 * @param event
	 * @throws IllegalArgumentException if parameter is null
	 */
	public void notify(Event event){
		if(event == null){
			throw new IllegalArgumentException("Event cannot be null");
		}
		if(eventHandlers.size() > 0){
			//using array so that if some handler is removed during notification,
			// no mess is created.
			EventHandler[] array = new EventHandler[eventHandlers.size()];
			eventHandlers.copyInto(array);
			for(int i = 0; i < array.length ; i++){
				array[i].handleEvent(event);
			}
		}
	}
}
