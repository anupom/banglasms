package org.bd.banglasms.ui.lcdui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import org.bd.banglasms.App;
import org.bd.banglasms.ResourceManager;
import org.bd.banglasms.control.UIEvents;
import org.bd.banglasms.control.event.Event;
import org.bd.banglasms.control.event.EventHandler;
import org.bd.banglasms.control.event.NotifierHelper;
import org.bd.banglasms.ui.MainView;

/**
 * Implementation of {@link MainView} using Form.
 *
 */
public class MainViewLcdui extends List implements MainView, LcduiView, CommandListener {

	private String[] listNames = new String[]{"Write Message", "Inbox", "Sent", "Draft", "Template", "Credit", "How to write", "Update", "Exit"};
	private String[] listIcons = new String[]{"/Write Message.png", "/Inbox.png", "/Sent.png", "/Draft.png", "/Template.png", "/Credit.png", "/Help.png", "/Update.png", "/Exit.png"};
	private String[] eventNames = new String[]{UIEvents.WRITE_MESSAGE, UIEvents.OPEN_INBOX, UIEvents.OPEN_SENT, UIEvents.OPEN_DRAFTS, UIEvents.OPEN_TEMPLATE, UIEvents.OPEN_CREDIT, UIEvents.OPEN_HELP, UIEvents.UPDATE, UIEvents.EXIT};
	private Command exitCommand = new Command("Exit", Command.EXIT, 1);
	NotifierHelper notifier = new NotifierHelper();

	public MainViewLcdui(String title){
		super(title, List.IMPLICIT);
		this.addCommand(exitCommand);
		this.setCommandListener(this);
	}

	public void addEventHandler(EventHandler eventHandler) {
		notifier.add(eventHandler);
	}

	public void removeEventHandler(EventHandler eventHandler) {
		notifier.remove(eventHandler);
	}

	public void init(){
		ResourceManager res = App.getResourceManager();
		for(int i = 0; i < listNames.length; i++){
			this.append(listNames[i], res.fetchImageSafely(listIcons[i]));
		}
	}

	public void commandAction(Command c, Displayable d) {
		if (c == List.SELECT_COMMAND) {
			notifier.notify(new Event(eventNames[this.getSelectedIndex()], this));
		}else if(c == exitCommand){
			notifier.notify(new Event(UIEvents.EXIT, this));
		}
	}

	public Displayable getDisplayable() {
		return this;
	}
}
