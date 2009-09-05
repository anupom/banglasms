package org.bd.banglasms.ui.lcdui;

import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;

import org.bd.banglasms.App;
import org.bd.banglasms.Logger;
import org.bd.banglasms.bangla.BanglaRenderer;
import org.bd.banglasms.control.UIEvents;
import org.bd.banglasms.control.event.Event;
import org.bd.banglasms.control.event.EventHandler;
import org.bd.banglasms.control.event.NotifierHelper;
import org.bd.banglasms.store.BanglaMessage;
import org.bd.banglasms.ui.FolderView;

/**
 * Implementation of {@link FolderView} using Form and CustomItem to render
 * Bangla texts.
 * 
 */
public class FolderViewLcdui extends Form implements FolderView, LcduiView, CommandListener, ItemCommandListener {


    private static BanglaRenderer renderer = BanglaRenderer.getBanglaRenderer();
    private static final int TITLE_SUBTITLE_GAP = 1;
    private static final int BANGLA_FONT_HEIGHT = renderer.getFontHeight();
    private static final int TOP_PADDING = 1;
    private static final int BOTTOM_PADDING = 1;
    private static final int LEFT_PADDING = 1;
    private static final int RIGHT_PADDING = 1;
    private static final int IMAGE_TEXT_GAP = 1;
    private static final Image ICON_READ = App.getResourceManager().fetchImageSafely("/read.png");
    private static final Image ICON_UNREAD = App.getResourceManager().fetchImageSafely("/unread.png");

    private NotifierHelper notifier = new NotifierHelper();
    private int id;
    private Command backCommand = new Command("Back", Command.BACK, 1);
    private Command openCommand = new Command("Open", Command.OK, 1);
    private Command deleteCommand = new Command("Delete", Command.ITEM, 1);
    private int banglaAreaBackgroundColor = -1;//white


    public FolderViewLcdui(){
        super(null);
    }

    public void init() {
        this.addCommand(backCommand);
        this.setCommandListener(this);
    }



    public void appendMessage(BanglaMessage message) {
        MessageItem item = new MessageItem(message);
        item.setDefaultCommand(openCommand);
        item.addCommand(deleteCommand);
        item.setItemCommandListener(this);
        this.append(item);
    }

    public void addMessageAtBeginning(BanglaMessage message){
    	MessageItem item = new MessageItem(message);
        item.setDefaultCommand(openCommand);
        item.addCommand(deleteCommand);
        item.setItemCommandListener(this);
        this.insert(0, item);
    }

    public int getIndex(BanglaMessage message) {
    	MessageItem messageItem;
    	for(int i = 0; i < this.size(); i++){
    		messageItem = (MessageItem)this.get(i);
    		if(messageItem.getMessage()== message){
    			return i;
    		}
    	}
    	return -1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Displayable getDisplayable() {
        return this;
    }

    public void addEventHandler(EventHandler eventHandler) {
        notifier.add(eventHandler);
    }

    public void removeEventHandler(EventHandler eventHandler) {
        notifier.add(eventHandler);
    }

    public void commandAction(Command c, Displayable d) {
        if(c == backCommand){
            notifier.notify(new Event(UIEvents.CLOSE_FOLDER, this));
        }
    }

    public void commandAction(Command c, Item item) {
        BanglaMessage message = ((MessageItem)item).getMessage();
        if(c == openCommand){
            Event openMessageEvent = new Event(UIEvents.OPEN_MESSAGE, this);
            openMessageEvent.setValue(UIEvents.PARAM_MESSAGE, message);
            notifier.notify(openMessageEvent);
        }else if(c == deleteCommand){
            Event deleteEvent = new Event(UIEvents.DELETE_MESSAGE, this);
            deleteEvent.setValue(UIEvents.PARAM_MESSAGE, message);
            notifier.notify(deleteEvent);
        }
    }

    private Image getIcon(BanglaMessage message){
        if(message.isRead()){
            return ICON_READ;
        }
        else{
            return ICON_UNREAD;
        }
    }

    private Font getTitleFont(BanglaMessage message, boolean highlighted){
    	int fontStyle = message.isRead() ? Font.STYLE_PLAIN : Font.STYLE_BOLD;
    	int fontSize = message.isRead()?  Font.SIZE_SMALL : Font.SIZE_MEDIUM;
        return Font.getFont(Font.FACE_PROPORTIONAL, fontStyle, fontSize);
    }

    public void remove(BanglaMessage message) {
    	MessageItem item;
    	for (int i = 0 ; i < this.size(); i++ ) {
    		item = (MessageItem)get(i);
    		if(item.getMessage() == message) {
    			delete(i);
    			return;
    		}
    	}
    }

    class MessageItem extends CustomItem{
        private BanglaMessage message;
        private boolean highlighted = false;

        protected MessageItem(BanglaMessage message) {
            super(null);
            this.message = message;
        }

        public BanglaMessage getMessage(){
            return message;
        }

        protected int getMinContentHeight() {
            return TOP_PADDING + getTitleFont(message, highlighted).getHeight() + TITLE_SUBTITLE_GAP + BANGLA_FONT_HEIGHT + BOTTOM_PADDING;
        }

        protected int getMinContentWidth() {
            return getWidth();//of the form
        }

        protected int getPrefContentHeight(int arg0) {
            return getMinContentHeight();
        }

        protected int getPrefContentWidth(int arg0) {
            return getMinContentWidth();
        }

        protected void paint(Graphics g, int width, int height) {
            Image icon = getIcon(message);
            if(highlighted){
            	g.setColor(App.getDisplayManager().getColor(Display.COLOR_HIGHLIGHTED_BACKGROUND));
            }else{
            	g.setColor(App.getDisplayManager().getColor(Display.COLOR_BACKGROUND));
            }
            g.fillRect(0, 0, width, height);

            int x = LEFT_PADDING;
            int y = TOP_PADDING;
            if(icon != null){
                g.drawImage(icon, x, y, Graphics.LEFT | Graphics.TOP);
                x += icon.getWidth();
            }
            x += IMAGE_TEXT_GAP;
            Font font = getTitleFont(message, highlighted);
            g.setFont(font);
            if(highlighted){
            	g.setColor(App.getDisplayManager().getColor(Display.COLOR_HIGHLIGHTED_FOREGROUND));
            }else{
            	g.setColor(App.getDisplayManager().getColor(Display.COLOR_FOREGROUND));
            }
			String title = message.getDisplayableContact();//title is sms://+358...:0 remove sms:// and :0
			g.drawString(title, x, y, Graphics.LEFT | Graphics.TOP);
			y += font.getHeight() + TITLE_SUBTITLE_GAP;
			try{
				g.setColor(banglaAreaBackgroundColor);
				renderer.render(message.getText(), g, x, y, getMinContentWidth() - x - RIGHT_PADDING, renderer.getFontHeight(), false, banglaAreaBackgroundColor, 0, false, 0, 0, 0);
			}catch(IOException ex){
				App.getLogger().log("FolderViewMIDP.paint silently handled Exception : " + ex, Logger.LEVEL_WARNING);
			}
		}

		protected boolean traverse(int dir, int viewportWidth,
				int viewportHeight, int[] visRect_inout) {
			if(!highlighted){
				highlighted = true;
			}
			repaint();//S60 does not repaint itself, works fine in S40 though
			return super.traverse(dir, viewportWidth, viewportHeight, visRect_inout);
		}

		protected void traverseOut() {
			super.traverseOut();
			this.highlighted = false;
			repaint();//S60 does not repaint itself, works fine in S40 though
		}
	}
}