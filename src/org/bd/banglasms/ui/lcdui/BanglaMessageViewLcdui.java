package org.bd.banglasms.ui.lcdui;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;

import org.bd.banglasms.App;
import org.bd.banglasms.control.UIEvents;
import org.bd.banglasms.control.event.Event;
import org.bd.banglasms.control.event.EventHandler;
import org.bd.banglasms.control.event.NotifierHelper;
import org.bd.banglasms.store.BanglaMessage;
import org.bd.banglasms.store.Store;
import org.bd.banglasms.ui.BanglaMessageView;
import org.bd.banglasms.ui.View;

/**
 * Implements {@link BanglaMessageView} using native library. This class acts as
 * a parent {@link CustomCanvasComponent}, which contains other
 * CustomCanvasComponents for editing, input handling etc.
 *
 */
public class BanglaMessageViewLcdui extends Canvas implements
		BanglaMessageView, LcduiView, CommandListener, CustomCanvasComponent {
	//TODO better to make this class extending BanglaTextView
	private BanglaMessage message;
	private int popupDelayTime;
	private NotifierHelper notifier = new NotifierHelper();
	private Command cmdSend = new Command("Send", Command.OK, 2);
	private Command cmdBack = new Command("Back", Command.CANCEL, 2);
	private Command cmdCancelFromPhoneNumber = new Command("Cancel",Command.CANCEL, 1);
	private Command cmdDelete = new Command("Delete", Command.ITEM, 9);
	private Command cmdEdit = new Command("Edit", Command.ITEM, 1);
	private Command cmdForward = new Command("Forward", Command.ITEM, 1);
	private Command cmdReply = new Command("Reply", Command.ITEM, 1);
	private Command cmdSaveAsDraft = new Command("Save in Draft", Command.ITEM,4);
	private Command cmdSaveAsTemplate = new Command("Save in Template",Command.ITEM, 4);
	private Command cmdShowDetails = new Command("Show details", Command.ITEM, 5);
	// This array will be used to remove all commands first before installing
	// new set of command, wish there were removeAllCommands method
	private Command[] commands = new Command[] { cmdSend, cmdBack,
			cmdCancelFromPhoneNumber, cmdDelete, cmdEdit, cmdForward, cmdReply, cmdSaveAsDraft,
			cmdSaveAsTemplate, cmdShowDetails };

	private CustomCanvasComponentHelper componentHelper = new CustomCanvasComponentHelper();
	private BanglaEditor editor = new BanglaEditor(getWidth(), getHeight(), this);
	private CustomCanvasComponent parent;

	public BanglaMessageViewLcdui() {
		super();
		popupDelayTime = 3000;
	}

	public BanglaMessage getMessage() {
		return message;
	}

	public String getText(){
		return this.editor.getText();
	}

	public void setMessage(BanglaMessage message) {
		this.message = message;
		editor.setText(message.getText());
		refreshCommands();
	}

	public void addEventHandler(EventHandler eventHandler) {
		this.notifier.add(eventHandler);
	}

	public void init() {
		this.setCommandListener(this);
		this.refreshCommands();
	}

	public void removeEventHandler(EventHandler eventHandler) {
		this.notifier.remove(eventHandler);
	}

	public Displayable getDisplayable() {
		return this;
	}

	public void commandAction(Command c, Displayable d) {
		if (c == cmdBack) {
			Event backFromMessageEvent = new Event(UIEvents.BACK_FROM_MESSAGE,
					this);
			backFromMessageEvent.setValue(UIEvents.PARAM_MESSAGE, getMessage());
			notifier.notify(backFromMessageEvent);
		} else if (c == cmdSend) {
			final BanglaMessage messageToSend = getMessage();
			messageToSend.setText(this.editor.getText());
			App.getDisplayManager().setOnScreen(
						new PhoneNumberTaker(messageToSend, this, messageToSend.getContactNumber()));

		} else if (c == cmdForward) {
			Event event = new Event(UIEvents.FORWARD_MESSAGE, this);
			event.setValue(UIEvents.PARAM_MESSAGE, getMessage());
			notifier.notify(event);
		} else if (c == cmdDelete) {
			Event event = new Event(UIEvents.DELETE_MESSAGE, this);
			event.setValue(UIEvents.PARAM_MESSAGE, getMessage());
			notifier.notify(event);
		} else if (c == cmdSaveAsDraft) {
			Event event = new Event(UIEvents.SAVE_MESSAGE_AS_DRAFT, this);
			event.setValue(UIEvents.PARAM_MESSAGE, getMessage());
			notifier.notify(event);
		} else if (c == cmdSaveAsTemplate) {
			Event event = new Event(UIEvents.SAVE_MESSAGE_AS_TEMPLATE, this);
			event.setValue(UIEvents.PARAM_MESSAGE, getMessage());
			notifier.notify(event);
		} else if (c == cmdReply) {
			Event event = new Event(UIEvents.REPLY_MESSAGE, this);
			event.setValue(UIEvents.PARAM_MESSAGE, getMessage());
			notifier.notify(event);
		} else if (c == cmdEdit) {
			Event event = new Event(UIEvents.EDIT_MESSAGE, this);
			event.setValue(UIEvents.PARAM_MESSAGE, getMessage());
			notifier.notify(event);
		} else if ( c == cmdShowDetails ){
			showMessageDetails();
		}
		componentHelper.commandAction(c);

	}

	private void refreshCommands() {
		removeAllCommands();
		this.addCommand(cmdBack);
		this.addCommand(cmdShowDetails);
		if (message != null) {
			addCommand(cmdSaveAsDraft);
			addCommand(cmdSaveAsTemplate);
			if(message.getStoreReference() != null){
				//no delete for new message
				addCommand(cmdDelete);
			}
			if (message.getFolderId() == Store.INBOX) {
				addCommand(cmdReply);
			}
			if (editor.isEditable()) {
				addCommand(cmdSend);
			} else {
				if(message.getFolderId() == Store.TEMPLATE || message.getFolderId() == Store.DRAFT){
					addCommand(cmdEdit);
				}
				addCommand(cmdForward);
			}
		}
	}

	private void removeAllCommands() {
		for (int i = 0; i < commands.length; i++) {
			removeCommand(commands[i]);
		}
	}

	public void setEditable(boolean editable) {
		this.editor.setEditable(editable);
		if(!editable && message != null) {
			showMessageDetails();
		}
	}

	private void showMessageDetails() {
		//we want to show from / to number and date as a tooltip like popup
		final CustomCanvasComponent messageDetails = new CustomCanvasComponent() {

			public void paint(Graphics g) {

				String title = null;
				String numberHeading = null;
				if(message.getFolderId() == Store.INBOX) {
					numberHeading = "From : ";
				} else if(message.getFolderId() == Store.SENT) {
					numberHeading = " To : ";
				}
				if(numberHeading != null) {
					title = numberHeading + message.getDisplayableContact();
				}
				StringBuffer dateString = new StringBuffer();
				Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
				long time = message.getTime();

				if(message.getFolderId() == Store.INBOX) {
			    	//PLATFORM_ISSUE: Nokia bug, which assumes for incoming messages timezone is GMT
					//Hmm, when they will fix the problem, we will be in trouble again
					boolean isNokia = false;
					try {
			            Class.forName("com.nokia.mid.ui.FullCanvas");
			            isNokia = true;
			        } catch (ClassNotFoundException _ex) {
			        }

			        if(isNokia){
			        	time -= TimeZone.getDefault().getRawOffset();
			        }
				}
				calendar.setTime(new Date(time));
				dateString.append(calendar.get(Calendar.DATE));
				dateString.append('/');
				dateString.append(calendar.get(Calendar.MONTH) + 1);
				dateString.append('/');
				int year = calendar.get(Calendar.YEAR) % 100;
				if ( year < 10 ){
					dateString.append('0');
				}
				dateString.append(year);
				dateString.append(' ');
				int hour = calendar.get(Calendar.HOUR);
				if (hour == 0) {
					hour = 12;
				}
				dateString.append(hour);
				dateString.append(':');
				int minute = calendar.get(Calendar.MINUTE);
				if (minute < 10) {
					dateString.append('0');
				}
				dateString.append(minute);
				int am = calendar.get(Calendar.AM_PM);
				if(am == Calendar.AM) {
					dateString.append(" am");
				} else {
					dateString.append(" pm");
				}



				Font font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL);
				g.setFont(font);
				int boxHeight = (font.getHeight() + 1) * 2;
				int bottomPadding = 20;
				g.setColor(0xFFFFFF80);
				g.fillRect(0, getHeight() - boxHeight - bottomPadding, getWidth(), boxHeight);
				g.setColor(0xFFCCCCCC);
				g.drawRect(0, getHeight() - boxHeight - bottomPadding, getWidth() - 1 , boxHeight - 1);
				g.setColor(0);
				if(title != null){
					g.drawString(title, getWidth() / 2, getHeight() - boxHeight - bottomPadding + 1, Graphics.HCENTER | Graphics.TOP);
				}
				g.drawString(dateString.toString(), getWidth() / 2, getHeight() - font.getHeight() - bottomPadding - 1, Graphics.HCENTER | Graphics.TOP);

			}
			public void keyPressed(int keyCode, int gameActionCode) {
				removeComponent(this);
				repaint();
			}
			public void commandAction(Command command) {}
			public CustomCanvasComponent getParent() {return null;}
			public void hideNotify() {}
			public void keyReleased(int keyCode, int gameActionCode) {}
			public void keyRepeated(int keyCode, int gameActionCode) {}						
			public void showNotify() {}
			public void sizeChanged(int width, int height) {}
			public void addCommand(Command command) {}
			public void addComponent(CustomCanvasComponent component) {}
			public void removeCommand(Command command) {}
			public void removeComponent(CustomCanvasComponent component) {}
			public void repaintNeeded() {}
			public void repaintNeeded(int x, int y, int width, int height) {}
			public void setParent(CustomCanvasComponent parent) {}
		};
		this.addComponent(messageDetails);
		repaint();
		Timer timer = new Timer();
		TimerTask disappearTask = new TimerTask() {
			public void run() {
				removeComponent(messageDetails);
				repaint();
			}
		};
		timer.schedule(disappearTask, popupDelayTime);
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

	public void paint(Graphics g) {
		componentHelper.paint(g);
	}


	public void repaintNeeded() {
		this.repaint();
	}

	public void repaintNeeded(int x, int y, int width, int height) {
		this.repaint(x, y, width, height);
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

	public void addComponent(CustomCanvasComponent component) {
		componentHelper.addCustomComponent(component);
	}

	public void removeComponent(CustomCanvasComponent component) {
		componentHelper.removeCustomComponent(component);
	}


	class PhoneNumberTaker implements View, LcduiView, CommandListener,
			ItemCommandListener {
		Form form = new Form(null);
		final TextField input = new TextField("Enter phone number", "", 30,
				TextField.PHONENUMBER);
		StringItem sendButton = new StringItem(null, "Send", StringItem.BUTTON);
		private BanglaMessage message;
		private View viewOnCancel;

		public PhoneNumberTaker(BanglaMessage message, View viewOnCancel, String contactNumber) {
			this.message = message;
			this.viewOnCancel = viewOnCancel;
			this.input.setString(contactNumber);
			sendButton.setDefaultCommand(cmdSend);
			sendButton.setItemCommandListener(this);
			form.append(input);
			form.append(sendButton);
			form.addCommand(cmdSend);
			form.addCommand(cmdCancelFromPhoneNumber);
			form.setCommandListener(this);
		}

		public void commandAction(Command c, Displayable d) {
			if (c == cmdSend) {
				String inputValue = input.getString();
				if (inputValue.length() == 0) {
					input.setLabel("Enter a valid phone number");
					return;
				}
				message.setContactNumber(inputValue);
				Event sendEvent = new Event(UIEvents.SEND_MESSAGE, this);
				sendEvent.setValue(UIEvents.PARAM_MESSAGE, message);
				notifier.notify(sendEvent);
			} else if (c == cmdCancelFromPhoneNumber) {
				App.getDisplayManager().setOnScreen(viewOnCancel);
			}
		}

		public void commandAction(Command c, Item item) {
			// handle the send button similary
			this.commandAction(c, form);
		}

		public void addEventHandler(EventHandler eventHandler) {
			// no need for internal view
		}

		public void init() {

		}

		public void removeEventHandler(EventHandler eventHandler) {
			// no need for internal view
		}

		public void setTitle(String title) {
			form.setTitle(title);
		}

		public Displayable getDisplayable() {
			return form;
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
