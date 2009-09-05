package org.bd.banglasms.ui.lcdui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import org.bd.banglasms.control.event.Event;
import org.bd.banglasms.control.event.EventHandler;
import org.bd.banglasms.control.event.NotifierHelper;
import org.bd.banglasms.ui.ChoiceView;

/**
 * Implementation of {@link ChoiceView} using <code>List</code>.
 *
 */
public class ChoiceViewLcdui implements ChoiceView, LcduiView, CommandListener {

	private List list;
	private NotifierHelper notifier;

	public ChoiceViewLcdui (String title) {
		list = new List(title, List.IMPLICIT);
		list.addCommand( new Command("Cancel", Command.CANCEL, 1));
		list.setCommandListener(this);
		notifier = new NotifierHelper();
	}

	public void init() {

	}

	public void setTitle(String title) {
		list.setTitle(title);
	}

	public void setOptions(String[] options) {
		if (options != null) {
			for (int i = 0 ; i < options.length ; i++) {
				list.append(options[i], null);
			}
		}
	}

	public void setSelectedOption(int index) {
		list.setSelectedIndex(index, true);
	}

	public int getSelectedOption() {
		return list.getSelectedIndex();
	}


	public void addEventHandler(EventHandler eventHandler) {
		notifier.add(eventHandler);
	}

	public void removeEventHandler(EventHandler eventHandler) {
		notifier.add(eventHandler);
	}

	public void commandAction(Command c, Displayable d) {
		if( c == List.SELECT_COMMAND) {
			notifier.notify(new Event(ChoiceView.EVENT_CHOICE_SELECTED, this));
		} else {
			notifier.notify(new Event(ChoiceView.EVENT_CHOICE_CANCELLED, this));
		}
	}

	public Displayable getDisplayable() {
		return list;
	}

}
