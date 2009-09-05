package org.bd.banglasms.ui.lcdui;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import org.bd.banglasms.App;
import org.bd.banglasms.Logger;
import org.bd.banglasms.bangla.BanglaRenderer;

/**
 * A custom UI component that can show or edit Bangla text.
 *
 */
public class BanglaEditor implements CustomCanvasComponent {
	private StringBuffer textBuffer;
	private Vector lines;
	private BanglaRenderer renderer;
	private boolean transparentBackground;
	private int foregroundColor;
	private int backgroundColor;
	private int lineGap;
	private int width;
	private int height;
	private boolean needLineRecalculation;
	private boolean isEditable;
	private int firstVisibleLineIndex;
	private int maxVisibleLineCount;
	private InputHandler inputHandler;
	private int bottomBarHeight;
	private CustomCanvasComponent parent;
	private int cursorAtCharIndex;//index of the character before which the cursor should appear. So 0 for the beginning position
	private int cursorColor;
	int cursorAtLine ;
	int charCountBeforeCursorLine;
	private boolean resetCursor;
	private Timer cursorBlinkTimer;
	private long cursorBlinkTime;
	private boolean isCursorVisible;
	private int cursorWidth;
	private CustomCanvasComponentHelper componentHelper;
	private int warningAtCharCount;
	private int warningBgColor;
	private int warningFgColor;
	private Font warningFont;
	private int scrollButtonHeight;
	private int scrollButtonY;//since we need to make floating point operation on this, so we are saving in member to avoid recalculation on each paint
	private int scrollbarHeight;
	private int scrollBarWidth;

	protected BanglaEditor(int width, int height, CustomCanvasComponent parent) {
		componentHelper = new CustomCanvasComponentHelper();
		setParent(parent);
		textBuffer = new StringBuffer();
		lines = new Vector();
		renderer = BanglaRenderer.getBanglaRenderer();
		lineGap = 1;
		backgroundColor = -1;
		foregroundColor = 0;
		cursorAtCharIndex = 0;
		transparentBackground = false;
		needLineRecalculation = true;
		cursorColor = 0xFFFF0000;
		cursorBlinkTime = 500;
		isCursorVisible = false;
		cursorWidth = 2;
		scrollBarWidth = 3;
		warningAtCharCount = 161;
		warningBgColor = App.getDisplayManager().getColor(Display.COLOR_HIGHLIGHTED_BACKGROUND);
		warningFgColor = App.getDisplayManager().getColor(Display.COLOR_HIGHLIGHTED_FOREGROUND);
		warningFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
		sizeChanged(width, height);
	}

	public void setParent(CustomCanvasComponent parent) {
		this.parent = parent;
		if(parent != null){
			parent.addComponent(this);
		}
	}

	public CustomCanvasComponent getParent() {
		return this.parent;
	}

	public void sizeChanged(int width, int height){
		this.width = width;
		this.height = height;
		recalculateLines();
		recalculateLayout();
	}

	public void setText(String text){
		this.textBuffer = new StringBuffer();
		if(text != null && text.length() > 0){
			textBuffer.append(text);
		}
		needLineRecalculation = true;
		this.resetCursor = true;
	}

	public String getText(){
		return textBuffer.toString();
	}

	private void recalculateLayout(){
		bottomBarHeight = inputHandler == null ? 0 : this.renderer.getFontHeight();
		maxVisibleLineCount = (height - bottomBarHeight) / (renderer.getFontHeight() + lineGap);
	}

	private void recalculateLines(){
		lines = renderer.breakIntoLines(textBuffer, width - scrollBarWidth);
		recalculateScrollPosition();
		needLineRecalculation = false;
	}

	private void recalculateScrollPosition() {
		scrollbarHeight = this.getHeight() - this.bottomBarHeight;
		if(this.lines.size() > 0 && maxVisibleLineCount < this.lines.size()){
			scrollButtonHeight = (int)(((double)this.maxVisibleLineCount / this.lines.size()) * scrollbarHeight);
			scrollButtonY = (int)(((double)this.firstVisibleLineIndex / this.lines.size()) * scrollbarHeight);
		}else{
			scrollButtonY = 0;
			scrollButtonHeight = scrollbarHeight;
		}
	}

	private boolean scrollUp(){
		if(firstVisibleLineIndex == 0){
			return false;
		}else{
			firstVisibleLineIndex--;
			recalculateScrollPosition();
			if(parent != null){
				parent.repaintNeeded();//TODO improvement, we could repaint only the area near cursor?;
			}
			return true;
		}
	}

	private boolean scrollDown(){
		if(firstVisibleLineIndex + maxVisibleLineCount >= lines.size()){
			return false;
		}else{
			firstVisibleLineIndex++;
			recalculateScrollPosition();
			if(parent != null){
				parent.repaintNeeded();//TODO improvement, we could repaint only the area near cursor?;
			}
			return true;
		}
	}

	protected boolean  moveCursor(int direction){
		boolean cursorMoved = false;
		if(this.textBuffer.length() > 0){
			if(direction == Canvas.LEFT){
				int newCursorIndex = renderer.moveCursorLeft(textBuffer, cursorAtCharIndex);
				if(newCursorIndex != cursorAtCharIndex){
					cursorAtCharIndex = newCursorIndex;
					cursorMoved = true;
				}
			}else if(direction == Canvas.RIGHT){
				int newCursorIndex = renderer.moveCursorRight(textBuffer, cursorAtCharIndex);
				if(newCursorIndex != cursorAtCharIndex){
					cursorAtCharIndex = newCursorIndex;
					cursorMoved = true;
				}
			}else if(direction == Canvas.UP){
				//TODO implement cursor UP
				//at this point, lets move it left
				moveCursor(Canvas.LEFT);
			}else if(direction == Canvas.DOWN){
				//TODO implement cursor DOWN
				moveCursor(Canvas.RIGHT);
			}
		}
		if(cursorMoved){
			updateCursorPosition();
			isCursorVisible = true;
			if(parent != null){
				parent.repaintNeeded();//TODO improvement, we could repaint only the area near cursor?;
			}
		}
		return cursorMoved;
	}

	public void clearLetter(){
		if(textBuffer.length() > 0){
			int newCursorIndex = renderer.removeLetter(textBuffer, inputHandler.getCurrentMode(), cursorAtCharIndex);
			if(newCursorIndex != cursorAtCharIndex){
				cursorAtCharIndex = newCursorIndex;
				recalculateLines();
				updateCursorPosition();
				isCursorVisible = true;
				if(parent != null){
					parent.repaintNeeded();//TODO improvement, we could repaint only the area near cursor?;
				}
			}
		}
	}

	public void paint(Graphics g) {
		if(width == 0 || height == 0){
			//PHONE_BUG: at least in 6630 width appeared as 0 !! Seems like after calling Graphics.Translate! So workaround: no painting when phone width shrunk to 0 (huh! 0 width phon :D)
			return;
		}
		g.setColor(backgroundColor);
		g.fillRect(0, 0, width, height);
		if(needLineRecalculation){
			recalculateLines();
		}
		if(isEditable){
			if(resetCursor){
				cursorAtCharIndex = textBuffer.length();
				resetCursor = false;
				updateCursorPosition();
			}
		}
		int x = 0;
		int y = 0;
		int heightBeforeFirstVisibleLine = firstVisibleLineIndex * renderer.getFontHeight();
		g.translate(0, - heightBeforeFirstVisibleLine );
		try {
			renderer.render(textBuffer, g, x, y, width - scrollBarWidth, Integer.MAX_VALUE, transparentBackground, backgroundColor, foregroundColor, isEditable && isCursorVisible, cursorAtCharIndex, cursorColor, cursorWidth);
		} catch (IOException ex) {
			App.getLogger().log("BanglaEditor.paint silently caught exception : " + ex, Logger.LEVEL_ERROR);
		}
		g.translate(0, heightBeforeFirstVisibleLine);
		try{
			componentHelper.paint(g);
		}catch(Exception ex){
			App.getLogger().log("BanglaEditor.paint caught exception while painting its components: " + ex, Logger.LEVEL_ERROR);
		}

		if(isCursorVisible && textBuffer.length() >= warningAtCharCount) {
			//warn user about the high number of chars
			String warningText = textBuffer.length() + "(" + (textBuffer.length() / warningAtCharCount + 1) + ")";
			int warningBoxWidth = warningFont.stringWidth(warningText) + 4;
			int warningX = this.getWidth() - scrollBarWidth - warningBoxWidth - 5;
			int warningY = 5;
			g.setColor(warningBgColor);
			g.fillRect(warningX, warningY, warningBoxWidth, warningFont.getHeight());
			g.setColor(warningFgColor);
			g.setFont(warningFont);
			g.drawString(warningText, warningX + warningBoxWidth / 2, warningY , Graphics.TOP | Graphics.HCENTER);
		}

		paintScrollBar(g);
	}


	private void paintScrollBar(Graphics g) {
		int screenWidth = this.getWidth();
		g.setColor(0xFFA0A0A0);
		g.drawLine(screenWidth - scrollBarWidth, 0, screenWidth - scrollBarWidth, scrollbarHeight);
		g.drawLine(screenWidth - 1, 0, screenWidth - 1, scrollbarHeight);
		g.setColor(0xFF404040);
		g.fillRect(screenWidth - scrollBarWidth + 1, 0, scrollBarWidth - 2, scrollbarHeight);
		g.setColor(0xFF7F92FF);
		g.drawLine(screenWidth - scrollBarWidth, scrollButtonY, screenWidth - scrollBarWidth, scrollButtonY + scrollButtonHeight);
		g.drawLine(screenWidth - 1, scrollButtonY, screenWidth - 1, scrollButtonY + scrollButtonHeight);
		g.setColor(0xFF7FC9FF);
		g.fillRect(screenWidth - scrollBarWidth + 1, scrollButtonY, scrollBarWidth - 2, scrollButtonHeight);
	}

	private void updateCursorPosition(){
		if(lines.size() == 0){
			cursorAtLine = 0;
		}else{
			charCountBeforeCursorLine = 0;
			cursorAtLine = 0;
			String currentLine;
			for(int line = 0; line < lines.size(); line++){
				currentLine = (String)lines.elementAt(line);
				if(cursorAtCharIndex <= charCountBeforeCursorLine + currentLine.length()){
					cursorAtLine = line;
					break;
				}else{
					charCountBeforeCursorLine += currentLine.length();
				}
			}
		}

		while(cursorAtLine < firstVisibleLineIndex){
			scrollUp();
		}

		while(cursorAtLine > firstVisibleLineIndex + maxVisibleLineCount - 1){
			scrollDown();
		}
	}

	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
		if(this.isEditable){
			this.inputHandler = createInputHandler();
			recalculateLayout();
			setCursorAtEnd();
			startCursorBlinkThread();
		}else{
			this.inputHandler = null;
			stopCursorBlinkThread();
		}

	}

	private void setCursorAtEnd(){
		this.resetCursor = true;
	}

	private void toggleCursor(){
		isCursorVisible = !isCursorVisible;
		if(parent != null){
			parent.repaintNeeded();//TODO improvement, we could repaint only the area near cursor?;
		}
	}
	private void startCursorBlinkThread(){
		if(cursorBlinkTimer != null){
			cursorBlinkTimer.cancel();
			cursorBlinkTimer = null;
		}
		cursorBlinkTimer = new Timer();
		cursorBlinkTimer.schedule(new CursorBlinkTask(), 0, cursorBlinkTime);
	}

	private void stopCursorBlinkThread(){
		if(cursorBlinkTimer != null){
			cursorBlinkTimer.cancel();
			cursorBlinkTimer = null;
		}
	}


	private InputHandler createInputHandler() {
		return new InputHandler(this, this);
	}

	public void insertLetter(char newLetter){
		int newCurosrPosition = renderer.insert(this.inputHandler.getCurrentMode(), cursorAtCharIndex, textBuffer, newLetter);
		this.cursorAtCharIndex = newCurosrPosition;
		this.recalculateLines();
		this.updateCursorPosition();
		if(parent != null){
			parent.repaintNeeded();//TODO improvement, we could repaint only the area near cursor?;
		}
	}

	public void insertJuktakkhor(int index){
		int newCurosrPosition = renderer.insertJuktakkhor(this.inputHandler.getCurrentMode(), cursorAtCharIndex, textBuffer, index);
		this.cursorAtCharIndex = newCurosrPosition;
		this.recalculateLines();
		this.updateCursorPosition();
		if(parent != null){
			parent.repaintNeeded();//TODO improvement, we could repaint only the area near cursor?;
		}
	}

	public void insertSmiley(int index){
		int newCurosrPosition = renderer.insertSmiley(this.inputHandler.getCurrentMode(), cursorAtCharIndex, textBuffer, index);
		this.cursorAtCharIndex = newCurosrPosition;
		this.recalculateLines();
		this.updateCursorPosition();
		if(parent != null){
			parent.repaintNeeded();//TODO improvement, we could repaint only the area near cursor?;
		}
	}

	public boolean isEditable() {
		return isEditable;
	}

	protected int getWidth(){
		return width;
	}

	protected int getHeight(){
		return height;
	}

	public void showNotify(){
		if(isEditable){
			startCursorBlinkThread();
		}
		componentHelper.showNotify();
	}

	public void hideNotify(){
		if(isEditable){
			stopCursorBlinkThread();
		}
		componentHelper.hideNotify();
	}
	
	public void keyPressed(int keyCode, int gameActionCode) {		
		if(! isEditable){
			if(gameActionCode == Canvas.UP){
				scrollUp();
			}else if(gameActionCode == Canvas.DOWN){
				scrollDown();
			}
		}
		componentHelper.keyPressed(keyCode, gameActionCode);

	}

	public void keyReleased(int keyCode, int gameActionCode) {
		componentHelper.keyReleased(keyCode, gameActionCode);
	}

	public void keyRepeated(int keyCode, int gameActionCode) {
		componentHelper.keyRepeated(keyCode, gameActionCode);
	}

	public void commandAction(Command command){
		componentHelper.commandAction(command);
	}

	public void addCommand(Command command) {
		if(parent != null){
			parent.addCommand(command);
		}
	}

	public void addComponent(CustomCanvasComponent component) {
		componentHelper.addCustomComponent(component);
	}

	public void removeCommand(Command command) {
		if(parent != null){
			parent.removeCommand(command);
		}
	}

	public void removeComponent(CustomCanvasComponent component) {
		componentHelper.removeCustomComponent(component);
	}

	public void repaintNeeded() {
		if(parent != null){
			parent.repaintNeeded();
		}
	}

	public void repaintNeeded(int x, int y, int width, int height) {
		if(parent != null){
			parent.repaintNeeded(x, y, width, height);
		}
	}

	class CursorBlinkTask extends TimerTask{
		public void run() {
			toggleCursor();
		}
	}
}
