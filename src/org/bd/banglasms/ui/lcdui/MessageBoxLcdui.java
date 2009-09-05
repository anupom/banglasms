package org.bd.banglasms.ui.lcdui;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import org.bd.banglasms.control.event.Event;
import org.bd.banglasms.control.event.EventHandler;
import org.bd.banglasms.control.event.NotifierHelper;
import org.bd.banglasms.ui.MessageBox;

/**
 * Implementation of {@link MessageBox} using Alert.
 *
 */
public class MessageBoxLcdui implements MessageBox, LcduiView, CommandListener {

	private Command okCommand = new Command("Ok", Command.OK, 1);
	private Command cancelCommand = new Command("Cancel", Command.CANCEL,1);
	private Alert alert;
	private NotifierHelper notifier = new NotifierHelper();

	public MessageBoxLcdui() {
		alert = new Alert(null, null, null, AlertType.INFO);
		alert.setCommandListener(this);
	}
	public void setOptionStyle(int optionStyle) {
		switch (optionStyle) {
		case NO_OPTION:
			break;
		case OPTION_OK:
			alert.addCommand(okCommand);
			alert.setTimeout(Alert.FOREVER);
			break;
		case OPTION_OK_CANCEL:
			alert.addCommand(okCommand);
			alert.addCommand(cancelCommand);
			alert.setTimeout(Alert.FOREVER);
			alert.setType(AlertType.CONFIRMATION);
			break;
		default:
			break;
		}
	}

	public void setText(String text) {
		alert.setString(text);
	}

	public void addEventHandler(EventHandler eventHandler) {
		notifier.add(eventHandler);

	}

	public void init() {
	}

	public void removeEventHandler(EventHandler eventHandler) {
		notifier.remove(eventHandler);

	}

	public void setTitle(String title) {
		alert.setTitle(title);

	}
	public void commandAction(Command c, Displayable d) {
		String eventName = null;
		if (c == okCommand){
			eventName = EVENT_MESSAGE_OK;
		}else if(c == cancelCommand){
			eventName = EVENT_MESSAGE_CANCEL;
		}else
		{
			eventName = EVENT_MESSAGE_DISSMISS;
		}

		if(eventName != null){
			notifier.notify(new Event(eventName, this));
		}
	}

	public Displayable getDisplayable() {
		return alert;
	}
}
