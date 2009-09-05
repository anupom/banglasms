package org.bd.banglasms.ui.lcdui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;

import org.bd.banglasms.control.UIEvents;
import org.bd.banglasms.control.event.Event;
import org.bd.banglasms.control.event.EventHandler;
import org.bd.banglasms.control.event.NotifierHelper;
import org.bd.banglasms.ui.BanglaTextView;

/**
 * Implmentation of {@link BanglaTextView} using Canvas.
 *
 */
public class BanglaTextViewLcdui extends Canvas implements BanglaTextView, LcduiView, CustomCanvasComponent, CommandListener {

	private NotifierHelper notifier;
	private CustomCanvasComponentHelper componentHelper;
	private BanglaEditor editor;
	private Command back;
	private CustomCanvasComponent parent;

	public BanglaTextViewLcdui () {
		notifier = new NotifierHelper();
		componentHelper = new CustomCanvasComponentHelper();
		editor = new BanglaEditor(getWidth(), getHeight(), this);
		addComponent(editor);
		back = new Command("Back", Command.BACK, 1);
		addCommand(back);
		setCommandListener(this);
	}

	public void init() {

	}

	public void setText(String text) {
		editor.setText(text);
		editor.setEditable(false);
	}

	public void addEventHandler(EventHandler eventHandler) {
		notifier.add(eventHandler);
	}

	public void removeEventHandler(EventHandler eventHandler) {
		notifier.remove(eventHandler);
	}

	public Displayable getDisplayable() {
		return this;
	}

	public void paint(Graphics g) {
		componentHelper.paint(g);
	}

	public void addComponent(CustomCanvasComponent component) {
		componentHelper.addCustomComponent(component);
	}

	public void removeComponent(CustomCanvasComponent component) {
		componentHelper.removeCustomComponent(component);
	}

	public void repaintNeeded() {
		repaint();
	}

	public void repaintNeeded(int x, int y, int width, int height) {
		repaint(x, y, width, height);
	}

	protected void keyPressed(int keyCode) {
		componentHelper.keyPressed(keyCode, getGameAction(keyCode));
	}

	protected void keyReleased(int keyCode) {
		componentHelper.keyReleased(keyCode, getGameAction(keyCode));
	}

	protected void keyRepeated(int keyCode) {
		componentHelper.keyRepeated(keyCode, getGameAction(keyCode));
	}

	public void showNotify() {
		componentHelper.showNotify();
	}

	public void hideNotify() {
		componentHelper.hideNotify();
	}

	public void sizeChanged(int width, int height) {
		componentHelper.sizeChanged(width, height);
	}

	public void commandAction(Command c, Displayable d) {
		if (c == back) {
			notifier.notify(new Event(UIEvents.BACK, this));
		}
	}

	public void commandAction(Command command) {
		commandAction(command, this);
	}

	public CustomCanvasComponent getParent() {		
		return parent;
	}

	public void keyPressed(int keyCode, int gameActionCode) {
		componentHelper.keyPressed(keyCode, gameActionCode);
	}

	public void keyReleased(int keyCode, int gameActionCode) {
		componentHelper.keyReleased(keyCode, gameActionCode);
	}

	public void keyRepeated(int keyCode, int gameActionCode) {
		componentHelper.keyRepeated(keyCode, gameActionCode);
	}

	public void setParent(CustomCanvasComponent parent) {
		this.parent = parent;
	}

}
