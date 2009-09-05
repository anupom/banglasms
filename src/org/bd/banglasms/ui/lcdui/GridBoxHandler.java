package org.bd.banglasms.ui.lcdui;

/**
 * Interface that is used to notify {@link GridBox} events.
 *
 */
public interface GridBoxHandler {
	/**
	 * This is called when user selects a grid
	 * @param selectedIndex index of the selected grid
	 */
	public void gridSelected(int selectedIndex);
}
