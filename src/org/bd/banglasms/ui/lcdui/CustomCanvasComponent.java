package org.bd.banglasms.ui.lcdui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Graphics;

/**
 * This interface is used to create custom UI components based on Canvas. A
 * CustomCanvasComponent can hold any number of CustomCanvasComponents inside
 * it. If a CustomCanvasComponent holds other components, it must forward key
 * events and other system events to those components.
 * 
 */
public interface CustomCanvasComponent {

	/**
	 * Sets the component's parent.
	 * 
	 * @param parent
	 *            parent of the component
	 */
	public void setParent(CustomCanvasComponent parent);

	/**
	 * Returns the parent component or null if it has no parent.
	 * 
	 * @return the parent component or null
	 */
	public CustomCanvasComponent getParent();

	/**
	 * Called when a key is pressed. If a component has child components, it
	 * must forward the notification to the children
	 * 
	 * @param keyCode
	 *            the key code of the key pressed
	 * @param gameActionCode
	 *            Gets the game action associated with the given key code of the
	 *            device, zero if no game action is associated with this key
	 *            code.
	 */
	public void keyPressed(int keyCode, int gameActionCode);

	/**
	 * Called when a key is released. If a component has child components, it
	 * must forward the notification to the children
	 * 
	 * @param keyCode
	 *            the key code of the key
	 * @param gameActionCode
	 *            Gets the game action associated with the given key code of the
	 *            device, zero if no game action is associated with this key
	 *            code.
	 */
	public void keyRepeated(int keyCode, int gameActionCode);

	/**
	 * Called when a key is repeated (held down). If a component has child
	 * components, it must forward the notification to the children
	 * 
	 * @param keyCode
	 *            the key code of the key
	 * @param gameActionCode
	 *            Gets the game action associated with the given key code of the
	 *            device, zero if no game action is associated with this key
	 *            code.
	 */
	public void keyReleased(int keyCode, int gameActionCode);
	
	/**
	 * Called when the screen is needed to be updated. If a component has child
	 * components, it must forward the notification to the children
	 * @param g Graphics object
	 */
	public void paint(Graphics g);
	
	/**
	 * Called when the view is being made visible on the display. If a component has child
	 * components, it must forward the notification to the children
	 */
	public void showNotify();

	/**
	 * Called after the Canvas has been removed from the display. If a component has child
	 * components, it must forward the notification to the children
	 */
	public void hideNotify();
	
	/**
	 * Called when the drawable area of the Canvas has been changed. If a component has child
	 * components, it must forward the notification to the children.
	 * @param width the new width in pixels of the drawable area of the Canvas
	 * @param height the new height in pixels of the drawable area of the Canvas
	 */
	public void sizeChanged(int width, int height);

	/**
	 * Called when a Command has been selected by user. If a component has child
	 * components, it must forward the notification to the children.
	 * @param command Command that has been selected
	 */
	public void commandAction(Command command);

	/**
	 * Called by the child components to notify that child has modified the
	 * Canvas drawing. If a Component does not have any child, it does not need
	 * implement anything in this method.
	 */
	public void repaintNeeded();

	/**
	 * Called by the child components to notify that child has modified the
	 * Canvas drawing. If a Component does not have any child, it does not need
	 * implement anything in this method.
	 * 
	 * @param x the x coordinate of the rectangle to be repainted
	 * @param y the y coordinate of the rectangle to be repainted
	 * @param width the width of the rectangle to be repainted
	 * @param height the height of the rectangle to be repainted
	 */
	public void repaintNeeded(int x, int y, int width, int height);

	/**
	 * Called by the child components to add a Command to parent Canvas.
	 * If a Component does not have any child, it does not need
	 * implement anything in this method.
	 * @param command Command that is requested by a child component to be added
	 */
	public void addCommand(Command command);

	/**
	 * Called by the child components to remove a Command from parent Canvas.
	 * If a Component does not have any child, it does not need
	 * implement anything in this method.
	 * @param command Command that is requested by a child component to be removed
	 */
	public void removeCommand(Command command);

	/**
	 * Adds a child component to this component.
	 * @param component child component to be added
	 */
	public void addComponent(CustomCanvasComponent component);

	/**
	 * Adds a child component from this component.
	 * @param component child component to be removed
	 */
	public void removeComponent(CustomCanvasComponent component);
}
