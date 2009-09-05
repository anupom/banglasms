package org.bd.banglasms.ui.lcdui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.bd.banglasms.App;
import org.bd.banglasms.control.event.Event;
import org.bd.banglasms.control.event.EventHandler;
import org.bd.banglasms.control.event.NotifierHelper;
import org.bd.banglasms.ui.SplashScreen;

/**
 * Implementation of {@link SplashScreen} using Canvas that can show an Image in
 * the middle of the Canvas.
 */
public class SplashScreenLcdui extends Canvas implements SplashScreen, LcduiView{

	private String text;
	private NotifierHelper notifier;
	private Image image;
	private int bgColor;
	private int textColor;
	private Font textFont;

	public SplashScreenLcdui(){
		setFullScreenMode(true);
		notifier = new NotifierHelper();
		textFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
		image = App.getResourceManager().fetchImageSafely("/splash.png");
		textColor = 0;
		bgColor = -1;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void addEventHandler(EventHandler eventHandler) {
		notifier.add(eventHandler);
	}

	public void init() {
	}

	public void removeEventHandler(EventHandler eventHandler) {
		notifier.remove(eventHandler);
	}

	public Displayable getDisplayable() {
		return this;
	}

	protected void paint(Graphics g){
		int width = this.getWidth();
		int height = this.getHeight();
		g.setColor(bgColor);
		g.fillRect(0, 0, width, height);
		if(image != null){
			g.drawImage(image, width/2, height/ 2,  Graphics.HCENTER | Graphics.VCENTER);
		}
		if(text != null){
			g.setFont(textFont);
			g.setColor(textColor);
			g.drawString(text, width/2, height, Graphics.HCENTER | Graphics.BOTTOM);
		}
	}

	protected void keyPressed(int keyCode) {
		notifier.notify(new Event(SplashScreen.EVENT_KEY_PRESS_ON_SPLASH, this));
	}
}
