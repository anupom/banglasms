package org.bd.banglasms.ui.lcdui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;

import org.bd.banglasms.control.UIEvents;
import org.bd.banglasms.control.event.Event;
import org.bd.banglasms.control.event.EventHandler;
import org.bd.banglasms.control.event.NotifierHelper;
import org.bd.banglasms.ui.CreditView;

/**
 * Implementation of {@link CreditView} that shows typewriter style animated
 * texts.
 *
 */
class CreditViewLcdui extends GameCanvas implements Runnable, CommandListener, CreditView, LcduiView {
	public final String pages[] = {
			"Bangla SMS\nOPEN SOURCE\nbanglasms.org.bd",
			"Concept, Font & 1st version\nAnupom Shyam\n(Anupom)",
			"Software Engineering\nAbdullah Al Mazed\n(Gagan)",
			"Project Management\nMd. Mizanur Rahman\n(Mizan)",
			"Public Relations\nAhsanul Bari\n(Ahsan)",
			"Special thanks to\nSphuron Technologies\nFor supporting the project.",
			"www.sphuronlabs.com",
			"banglasms.org.bd"};

	private final int MINIMUM_RENDERING_TIME = 80;

	private Thread animationThread;
	private Command backCommand;
	private Command replayCommand;

	private Font font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
	private int currentPage;
	private int currentChar;
	private NotifierHelper notifier;
	private Graphics g;

	CreditViewLcdui() {
		super(true);
		this.setFullScreenMode(false);
		g = getGraphics();
		this.setTitle("Credit");
		notifier = new NotifierHelper();
		backCommand = new Command("Back", Command.BACK, 1);
		replayCommand = new Command("Replay", Command.OK, 1);
		addCommand(backCommand);
		setCommandListener(this);
	}

	public void commandAction(Command c, Displayable d) {

		if (c == backCommand) {
			stop();
			notifier.notify(new Event(UIEvents.BACK, this));
		} else if (c == replayCommand) {
			currentPage = 0;
			currentChar = 0;
			this.removeCommand(replayCommand);
		}
	}

	public synchronized void start() {
		if (animationThread != null)
			try {
				animationThread.join();
			} catch (InterruptedException ex) {

			}
		animationThread = new Thread(this);
		animationThread.start();
	}

	public synchronized void stop() {
			animationThread = null;
	}

	public void run() {
		Thread currentThread = Thread.currentThread();
		try {
			while (currentThread == animationThread) {
				long startTime = System.currentTimeMillis();
				draw();
				flushGraphics();
				long timeTaken = System.currentTimeMillis() - startTime;
				if (timeTaken < MINIMUM_RENDERING_TIME) {
					synchronized (this) {
						wait(MINIMUM_RENDERING_TIME - timeTaken);
					}
				} else {
					Thread.yield();
				}
			}
		} catch (InterruptedException ex) {
			// shouldnt be thrown though
			ex.printStackTrace();
		}
	}

	public void draw() {
		int width = getWidth();
		int height = getHeight();
		g.setFont(font);
		g.setColor(255, 255, 255);
		g.fillRect(0, 0, width, height);
		if (currentPage < this.pages.length - 1)// not the last page, so u
												// may go to next page
		{
			if (currentChar > pages[currentPage].length()) {
				currentPage++;
				if (currentPage == this.pages.length - 1) {
					this.addCommand(replayCommand);
				}
				g.setColor(255, 255, 255);
				g.fillRect(0, 0, width, height);
				currentChar = 0;
				try {
					Thread.sleep(2000);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			} else {
				currentChar = type(g, pages[currentPage], currentChar);
			}
		} else {
			int x = this.getWidth() / 2
					- g.getFont().stringWidth(pages[pages.length - 1]) / 2;
			int y = this.getHeight() / 2;
			g.setColor(0, 0, 220);//special color for last page.. well I just felt like it :)
			g.drawString(this.pages[pages.length - 1], x, y, Graphics.LEFT | Graphics.TOP);
		}
	}

	int type(Graphics g, String str, int index) {

		int end = str.length();
		int y = this.getHeight() / 2;
		str = str.substring(0, index);
		int from = 0;
		int newLineIndex = str.indexOf('\n', from);
		while (newLineIndex != -1) {
			int x = this.getWidth()
					/ 2
					- g.getFont().stringWidth(
							str.substring(from, newLineIndex)) / 2;
			g.setColor(0, 0, 0);
			g.drawString(str.substring(from, newLineIndex), x, y, Graphics.LEFT | Graphics.TOP);
			y = y + (g.getFont().getHeight() + 2);
			from = newLineIndex + 1;
			if (from > str.length() - 1) {
				return index + 1;
			}
			newLineIndex = str.indexOf('\n', from);
		}

		g.setColor(0, 0, 0);
		int strWidth = g.getFont().stringWidth(str.substring(from, index));
		int x = this.getWidth() / 2 - strWidth / 2;
		g.drawString(str.substring(from, index), x, y, Graphics.LEFT | Graphics.TOP);

		if (index != end) {

			g.fillRect(x + strWidth, y, g.getFont().charWidth('X'), g
					.getFont().getHeight());
		}

		return index + 1;
	}

	public void addEventHandler(EventHandler eventHandler) {
		notifier.add(eventHandler);
	}

	public void init() {
		start();
	}

	public void removeEventHandler(EventHandler eventHandler) {
		notifier.remove(eventHandler);
	}

	public Displayable getDisplayable() {
		return this;
	}
}
