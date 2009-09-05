package org.bd.banglasms.bangla;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.bd.banglasms.App;
import org.bd.banglasms.Logger;

/**
 * This class is capable of rendering Bangla texts on graphics. </p> This class
 * wrapped up all the functionality related to bangla rendering. Other classes
 * uses this class to render any bangla text on <code>Graphics</code> object.
 */
public class BanglaRenderer {

	private String imageDir = "/data/";
	public static final String engDir = "e/";
	public static final String bangDir = "";
	public static final String conjDir = "cdata/";
	private static final String TOGGLE = "toggle.png";

	public static final char CONJ_ESC = '&';
	public static final int ENGLISH_SMALL = 2;
	public static final int ENGLISH_CAPITAL = 1;
	public static final int BANGLA = 0;
	public static final char BEGIN = '(';
	public static final char END = ')';
	private static final int JUKTAKKHOR_COUNT = 37;

	private static final int FONT_HEIGHT = 16;
	private static final int MAX_FONT_WIDTH = 16;

	Converter converter;

	private static BanglaRenderer _theInstance = new BanglaRenderer();

	private BanglaRenderer() {
		converter = new Converter(imageDir);
	}

	public static BanglaRenderer getBanglaRenderer() {
		return _theInstance;
	}

	public void render(String encodedString, Graphics g, int startX, int startY, int width, int height, boolean transparentBackground, int backgroundColor, int foregroundColor, boolean drawCursor, int cursorAt, int cursorColor, int cursorWidth) throws IOException {
		this.render(new StringBuffer(encodedString), g, startX, startY, width, height, transparentBackground, backgroundColor, foregroundColor, drawCursor, cursorAt, cursorColor, cursorWidth);
	}

	public void render(StringBuffer encodedString, Graphics g, int startX, int startY, int width, int height, boolean transparentBackground, int backgroundColor, int foregroundColor, boolean drawCursor, int cursorAt, int cursorColor, int cursorWidth) throws IOException {

		if (encodedString == null) {
			return;// nothing to draw
		}

		char ascii;
		boolean bMode = true;
		boolean esc = false;
		BanglaFont font = null;
		int length = encodedString.length();

		int x = startX;
		int y = startY;
		boolean excessHeight = false;

		if (!transparentBackground) {
			g.setColor(backgroundColor);
			g.fillRect(x, y, width, height);
		}

		int i;
		// true, if last char decided to go to next line because of no more
		// space available on the line
		boolean lastCharStartedNewLine = false;
		// true, if last char was 'newlinechar'
		boolean lastCharWasNewLineChar = false;
		for (i = 0; i < length; i++) {
			if (drawCursor && cursorAt == i) {
				g.setColor(cursorColor);
				g.fillRect(x, y, cursorWidth, getFontHeight());
			}
			ascii = encodedString.charAt(i);
			boolean drawNow = true;
			if (ascii == CONJ_ESC) {
				esc = true;
				font = new BanglaFont(getImage(imageDir + TOGGLE), false, true);// toggle
				drawNow = false;
			} else if (ascii == BEGIN) {
				bMode = false;
				font = new BanglaFont(getImage(imageDir + TOGGLE), false, true);// toggle
				drawNow = false;
			} else if (ascii == END) {
				bMode = true;
				font = new BanglaFont(getImage(imageDir + TOGGLE), false, true);// toggle
				drawNow = false;
			}
			if (drawNow) {
				font = converter.getFont(ascii, bMode, esc);
				if (esc == true)
					esc = false;

				if (font.xFactor < 98) // not newLine, newLine shldnt be drawn
				{
					g.drawImage(font.image, (x - font.xFactor), y, 0);// TODO fix: return when width exceeds
					x = x + font.image.getWidth() - font.widthFactor;

					if (x + 16 > startX + width)// TODO define a constant for this 16
					{
						if (y + FONT_HEIGHT >= startY + height) {
							excessHeight = true;// TODO think if we should return from function already, since y has exceeded
						} else {
							x = startX;
							y += FONT_HEIGHT;
						}
						lastCharStartedNewLine = true;
					} else {
						lastCharStartedNewLine = false;
					}
					lastCharWasNewLineChar = false;
				} else if (font.xFactor == 99) {
					// for new line
					if (y + FONT_HEIGHT >= startY + height) {
						excessHeight = true;
					} else {
						// If last character exceeded line width and started new
						// line, there is no need to create another new line for
						// 'newlinechar'. Only exception is, if last char was
						// itself a'newlinechar'.
						x = startX;
						if (!lastCharStartedNewLine) {
							y += FONT_HEIGHT;
						} else if (lastCharStartedNewLine && i > 0 && lastCharWasNewLineChar) {
							y += FONT_HEIGHT;
						}
					}
					lastCharStartedNewLine = true;
					lastCharWasNewLineChar = true;
				} else {
					// dont draw coz itz a toggle mode character 98
					lastCharWasNewLineChar = false;
				}
				font = null;
			}
		}
		if (drawCursor && cursorAt == i) {
			g.setColor(cursorColor);
			g.fillRect(x, y, cursorWidth, getFontHeight());
		}

	}

	public Vector breakIntoLines(StringBuffer text, int width) {
		Vector lines = new Vector();
		if (text != null) {
			char ascii;
			boolean bMode = true;
			boolean esc = false;
			BanglaFont font = null;
			int length = text.length();
			Converter converter = new Converter(imageDir);
			int x = 0;
			StringBuffer currentLine = new StringBuffer();
			boolean newline = false;
			boolean drawNow = true;
			try {

				for (int i = 0; i < length; i++) {
					ascii = text.charAt(i);
					drawNow = true;
					newline = false;
					if (ascii == CONJ_ESC) {
						esc = true;
						font = new BanglaFont(getImage(imageDir + TOGGLE), false, true);// toggle
						drawNow = false;
					} else if (ascii == BEGIN) {
						bMode = false;
						font = new BanglaFont(getImage(imageDir + TOGGLE), false, true);// toggle
						drawNow = false;
					} else if (ascii == END) {
						bMode = true;
						font = new BanglaFont(getImage(imageDir + TOGGLE), false, true);// toggle
						drawNow = false;
					}
					if (drawNow) {
						font = converter.getFont(ascii, bMode, esc);
						if (esc == true)
							esc = false;

						if (font.xFactor < 98) {
							// not newLine, newLine shldnt be drawn
							x = x + font.image.getWidth() - font.widthFactor;
							if (x > width) {
								newline = true;
								i--;// recheck this character again for next line
								// TODO hmm chance of infinite loop if widht is
								// too small to fit a single character. it will never leave value 0
							} else {
								currentLine.append(ascii);
							}

						} else if (font.xFactor == 99) {
							// for new line
							newline = true;
							currentLine.append(ascii);
						} else {
							// dont draw coz itz a toggle mode character 98
							currentLine.append(ascii);
						}
						font = null;
					} else {
						currentLine.append(ascii);
					}
					if (newline) {
						lines.addElement(currentLine.toString());
						currentLine.setLength(0);
						x = 0;
					}
				}
				// add the last line
				if (currentLine.length() > 0) {
					lines.addElement(currentLine.toString());
				}
			} catch (IOException ex) {
				App.getLogger().log("BanglaRenderer.getWidth silently caught exception : " + ex, Logger.LEVEL_ERROR);
			}
		}
		return lines;
	}

	public int getStringWidth(String text) {
		char ascii;
		boolean bMode = true;
		boolean esc = false;
		BanglaFont font = null;
		int length = text.length();
		Converter converter = new Converter(imageDir);
		int x = 0;
		try {

			for (int i = 0; i < length; i++) {
				ascii = text.charAt(i);
				boolean drawNow = true;
				if (ascii == CONJ_ESC) {
					esc = true;
					font = new BanglaFont(getImage(imageDir + TOGGLE), false, true);// toggle
					drawNow = false;
				} else if (ascii == BEGIN) {
					bMode = false;
					font = new BanglaFont(getImage(imageDir + TOGGLE), false, true);// toggle
					drawNow = false;
				} else if (ascii == END) {
					bMode = true;
					font = new BanglaFont(getImage(imageDir + TOGGLE), false, true);// toggle
					drawNow = false;
				}
				if (drawNow) {
					font = converter.getFont(ascii, bMode, esc);
					if (esc == true)
						esc = false;

					if (font.xFactor < 98) {
						// not newLine
						x = x + font.image.getWidth() - font.widthFactor;

					} else if (font.xFactor == 99) {
						// ignoring new line
					} else {
						// dont draw coz itz a toggle mode character 98
					}
					font = null;
				}
			}
		} catch (IOException ex) {
			App.getLogger().log("BanglaRenderer.getWidth silently caught exception : " + ex, Logger.LEVEL_ERROR);
		}
		return x;
	}

	public int insert(int currentMode, int cursorAt, StringBuffer smsString, char newLetter) {
		if (currentMode != BANGLA) {
			// ENGLISH mode
			if (cursorAt != 0 && smsString.charAt(cursorAt - 1) == END) {
				cursorAt--; // mst b atleast 1 char b4 ')' so corsor-- shldnt
				// create ne prob
			} else if (cursorAt != smsString.length() && smsString.charAt(cursorAt) == BEGIN)

			{
				cursorAt++; // mst b atleast 1 char after '(' so corsor++ shldnt
				// create ne prob
			}
		}

		int leftCharMode = getLeftCharacterMode(currentMode, smsString, cursorAt);

		if (leftCharMode != currentMode) // say *(a
		{
			cursorAt = closeMode(leftCharMode, smsString, cursorAt);
			cursorAt = openMode(currentMode, smsString, cursorAt);
		}

		smsString.insert(cursorAt, newLetter);

		cursorAt = moveCursorRight(smsString, cursorAt);

		int rightCharMode = getRightCharacterMode(currentMode, leftCharMode, smsString, cursorAt);

		if (rightCharMode != currentMode) {
			cursorAt = closeMode(currentMode, smsString, cursorAt);
			cursorAt = openMode(rightCharMode, smsString, cursorAt);
		}
		return cursorAt;
	}

	public int insertJuktakkhor(int currentMode, int cursorAt, StringBuffer smsString, int juktakkorIndex) {
		// takes two chars
		smsString.insert(cursorAt, CONJ_ESC);
		cursorAt++;
		smsString.insert(cursorAt, converter.getGSM(juktakkorIndex));
		cursorAt = moveCursorRight(smsString, cursorAt);
		return cursorAt;
	}

	public int insertSmiley(int currentMode, int cursorAt, StringBuffer smsString, int smileyIndex) {
		// takes two chars
		smsString.insert(cursorAt, CONJ_ESC);
		cursorAt++;
		smsString.insert(cursorAt, converter.getGSM(smileyIndex + JUKTAKKHOR_COUNT));
		cursorAt = moveCursorRight(smsString, cursorAt);
		return cursorAt;
	}

	public int getMaxPressCount(int key, int currentMode) {
		int maxTimes = 3; // maxm time a key can be repeated & pressed (actually
		// 4, starting from 0)

		if (currentMode == BanglaRenderer.BANGLA) {
			// setting the maxm time for different keys
			if (key == 0)
				maxTimes = 2; // 1 means 2

			else if (key == 9)
				maxTimes = 1; // 1 means 2

			else if (key == 10)
				maxTimes = 3; // 3 means 4

			else if (key == 1)
				maxTimes = 2; // 2 means 3
		} else// English mode
		{
			if (key == 0)
				maxTimes = 2; // means 3
			else if (key == 1 || key == 7 || key == 9)
				maxTimes = 4; // means 5
		}
		return maxTimes;// to make it real count, it was calculated as 0 started
						// index
	}

	/*
	 * returns how many images(letters) are there for this particular key &
	 * times that key pressed
	 */
	public int getMaxLetterCountInCurrentPanel(int currentMode, int currentKeyCode, int currentKeyPressCount) {
		int total = 2;

		if (currentMode == BanglaRenderer.BANGLA) {
			if (currentKeyCode == 0) {
				total = 1;
			} else if (currentKeyCode == 1) {
				if (currentKeyPressCount == 0)
					total = 4;
				else if (currentKeyPressCount == 1)
					total = 3;
				else
					total = 1;
			} else if (currentKeyCode == 2) {
				if (currentKeyPressCount == 3)
					total = 1;
			} else if (currentKeyCode == 3) {
				if (currentKeyPressCount == 3)
					total = 1;
			} else if (currentKeyCode == 4) {
				if (currentKeyPressCount == 2 || currentKeyPressCount == 3)
					total = 1;
			} else if (currentKeyCode == 5) {
				if (currentKeyPressCount == 2 || currentKeyPressCount == 3)
					total = 1;
			} else if (currentKeyCode == 6) {
				if (currentKeyPressCount == 1)
					total = 3;
				else if (currentKeyPressCount == 3)
					total = 1;
			} else if (currentKeyCode == 7) {
				if (currentKeyPressCount == 1)
					total = 4;
				else if (currentKeyPressCount == 2)
					total = 3;
				else if (currentKeyPressCount == 3)
					total = 1;
			} else if (currentKeyCode == 8) {
				if (currentKeyPressCount == 0)
					total = 3;
				else if (currentKeyPressCount == 3)
					total = 1;
			} else if (currentKeyCode == 9) {
				if (currentKeyPressCount == 1)
					total = 1;
			} else if (currentKeyCode == 10) {
				if (currentKeyPressCount == 3)
					total = 5;
				else
					total = 3;
			} else if (currentKeyCode == 11) {
				if (currentKeyPressCount == 1)
					total = 3;
			}
		} else {
			// English mode total image to show = 1 alwayz
			total = 1;
		}

		return total;
	}

	public int moveCursorRight(StringBuffer smsString, int cursorAt) {
		if (cursorAt != smsString.length()) {
			if (smsString.charAt(cursorAt) == BEGIN || smsString.charAt(cursorAt) == CONJ_ESC) {
				// We must make sure at least 1 char after '('
				cursorAt++;
			}

			cursorAt++; // mst make sure,.atleast 1 char

			if (cursorAt != smsString.length() && smsString.charAt(cursorAt) == END) {
				cursorAt++;
			}

		}
		return cursorAt;
	}

	public int moveCursorLeft(StringBuffer smsString, int cursorAt) {
		if (cursorAt != 0) {
			if (smsString.charAt(cursorAt - 1) == END) {
				// We must make sure at least 1 char before ')'
				cursorAt--;
			}

			cursorAt--; // mst make sure,.atleast 1 char

			if (cursorAt != 0) {
				if (smsString.charAt(cursorAt - 1) == BEGIN || smsString.charAt(cursorAt - 1) == CONJ_ESC) {
					cursorAt--;
				}
			}
		}
		return cursorAt;
	}

	private int getRightCharacterMode(int currentMode, int leftCharMode, StringBuffer smsString, int cursorAt) {
		String cropStr = smsString.toString().substring(cursorAt);

		int indexOfEND = cropStr.indexOf(END);

		if (cropStr.length() == 0) {
			// Nothing is there after the cursor, so returning current mode
			return currentMode;
		} else if (indexOfEND != -1) {
			int indexOfBEGIN = cropStr.substring(0, indexOfEND).indexOf(BEGIN);
			// cropping |.........) then searching (
			if (indexOfBEGIN == -1) {
				// English mode closed, opened before
				if (currentMode != BANGLA)
					return currentMode;
				else
					return ENGLISH_CAPITAL;
			} else {
				return BANGLA;
			}
		} else {
			// No End Char Found So returning Left Mode
			return leftCharMode;
		}
	}

	private int openMode(int currentMode, StringBuffer smsString, int cursorAt) {
		if (currentMode != BANGLA) {
			// English
			smsString.insert(cursorAt, BEGIN);
			return cursorAt + 1;
		}
		return cursorAt;
	}

	private int closeMode(int modeToClose, StringBuffer smsString, int cursorAt) {
		if (modeToClose != BANGLA) {
			// mode is ENGLISH
			smsString.insert(cursorAt, END);
			return cursorAt + 1;
		}
		return cursorAt;
	}

	private int getLeftCharacterMode(int currentMode, StringBuffer smsString, int cursorAt) {
		// TODO improve using string buffer
		String cropStr = smsString.toString().substring(0, cursorAt);

		if (cropStr.lastIndexOf(BEGIN) > cropStr.lastIndexOf(END)) {
			// English mode opened, not closed yet
			if (currentMode != BANGLA)
				return currentMode;
			else
				return ENGLISH_CAPITAL;
		} else {
			return BANGLA;
		}
	}

	public int removeLetter(StringBuffer smsString, int currentMode, int cursorAt) {
		if (cursorAt != 0) {
			boolean delEND = false;
			boolean delBEG = false;

			if (smsString.charAt(cursorAt - 1) == END) {
				// make sure at least 1 char before ')'
				smsString.deleteCharAt(cursorAt - 1);
				cursorAt--;
				delEND = true;
			}
			smsString.deleteCharAt(cursorAt - 1);
			cursorAt--;
			if (cursorAt != 0) {
				if (smsString.charAt(cursorAt - 1) == BEGIN) {
					smsString.deleteCharAt(cursorAt - 1);
					cursorAt--;
					delBEG = true;
				} else if (smsString.charAt(cursorAt - 1) == CONJ_ESC) {
					smsString.deleteCharAt(cursorAt - 1);
					cursorAt--;
				}
			}

			if (delEND && getLeftCharacterMode(currentMode, smsString, cursorAt) != BANGLA) {
				smsString.insert(cursorAt, END);
				cursorAt++;
				// AB)| -> A| -> A)|
			}
			if (delBEG && !delEND && cursorAt != smsString.length()) {
				smsString.insert(cursorAt, BEGIN);

			}
			if (cursorAt != 0 && smsString.charAt(cursorAt - 1) == END) {
				if (cursorAt != smsString.length() && smsString.charAt(cursorAt) == BEGIN) {
					smsString.deleteCharAt(cursorAt);
					smsString.deleteCharAt(cursorAt - 1);
					cursorAt--;
				}
			}

			App.getLogger().log("BanglaRenderer.removeLetter() : smsString " + smsString, Logger.LEVEL_DEBUG);
		}

		return cursorAt;
	}

	public Image[] getJuktakkhorImages() {
		Image[] images = new Image[37];
		String imageLocation = "";
		try {
			for (int i = 0; i < images.length; i++) {
				imageLocation = this.imageDir + conjDir + "_" + Converter.imgConjArray[i] + ".png";
				images[i] = getImage(imageLocation);
			}
		} catch (IOException ex) {
			App.getLogger().log("BanglaRenderer.getJuktakkhorImages caught exception while loading image " + imageLocation + " : " + ex, Logger.LEVEL_ERROR);
		}
		return images;
	}

	public Image[] getSmileyImages() {
		Image[] images = new Image[10];
		String imageLocation = "";
		try {
			for (int i = 0; i < images.length; i++) {
				imageLocation = this.imageDir + conjDir + "_" + Converter.imgConjArray[i + 37] + ".png";
				images[i] = getImage(imageLocation);
			}
		} catch (IOException ex) {
			App.getLogger().log("BanglaRenderer.getSmileyImages caught exception while loading image " + imageLocation + " : " + ex, Logger.LEVEL_ERROR);
		}
		return images;
	}

	/**
	 * @throws IllegalStateException
	 *             if ResourceManager has not been set yet
	 * @throws IOException
	 *             in case of i/o error during loading
	 */
	Image getImage(String location) throws IOException {
		return App.getResourceManager().fetchImage(location);
	}

	public int getFontHeight() {
		return FONT_HEIGHT;
	}

	public int getMaxFontWidth() {
		return MAX_FONT_WIDTH;
	}

	public char getGSM(int keyCode, int pressCount, int pos, boolean isInBanglaMode) {
		return converter.getGSM(keyCode, pressCount, pos, isInBanglaMode);
	}

	public BanglaFont getFont(char gsm, boolean bMode, boolean esc) {
		return converter.getFont(gsm, bMode, esc);
	}

}
