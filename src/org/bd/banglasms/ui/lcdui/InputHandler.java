package org.bd.banglasms.ui.lcdui;

import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.bd.banglasms.App;
import org.bd.banglasms.Logger;
import org.bd.banglasms.bangla.BanglaFont;
import org.bd.banglasms.bangla.BanglaRenderer;
import org.bd.banglasms.control.event.Event;
import org.bd.banglasms.control.event.EventHandler;
import org.bd.banglasms.ui.ChoiceView;
import org.bd.banglasms.ui.View;

/**
 * This class wraps up user key inputs and other options to edit Bangla text and
 * notifies {@link BanglaEditor} about user inputs. This implements
 * {@link CustomCanvasComponent} which shows a list of selectable letters to
 * user on key press.
 *
 */
public class InputHandler implements CustomCanvasComponent {
	
	private static final String[] WRITING_SPEED_OPTIONS = new String[]{"Fast", "Medium", "Slow"};
	private int[] WRITING_SPEED_DELAYS = new int[]{800, 2000, 4000};
	private final String WRITING_SPEED_PROPERTY_KEY = "writing_speed";
	private static final int DEFAULT_WRITING_SPEED_INDEX = 1;
	private int currentWritingSpeedIndex;
	private BanglaRenderer renderer = BanglaRenderer.getBanglaRenderer();
	private boolean isLetterPadActive = false;//key press will change letter selections, if false, keypress should move cursor
	private BanglaEditor editor;
	private int previousKeyCode = -2;
	private int currentKeyCode = -1;
	private int currentKeyPressCount = -1;
	private int currentLetterPosition;
	private int currentMode = BanglaRenderer.BANGLA;
	private int letterSelectionColorBorder = 0xFFFF0000;
	private int letterPanelBackgroundColor = 0xFFDCDCDC;
	private CustomCanvasComponent parent;
	private Command commandInsertSmiley;
	private Command commandClear;
	private Command commandInsertJuktakkhor;
	private Command commandAddLetter;
	private Command commandSetBanglaMode;
	private Command commandSetEnglishMode;
	private Command commandSetWritingSpeed;
	private GridBox gridBox;
	private Timer autoInsertTimer;
	private long autoInsertionDelay;

	public InputHandler(BanglaEditor editor, CustomCanvasComponent parent){
		this.editor = editor;
		setParent(parent);
		//set writing speed
		int speedIndex = DEFAULT_WRITING_SPEED_INDEX;
		String savedChoice = App.getProperties().getProperty(WRITING_SPEED_PROPERTY_KEY);
		if (savedChoice != null) {
			try {
				speedIndex = Integer.parseInt(savedChoice);
			} catch(NumberFormatException ex) {
				App.getLogger().log("InputHandler.commandAction > while parsing saved choice from properties caught " + ex, Logger.LEVEL_ERROR);
			}
		}
		currentWritingSpeedIndex = speedIndex;
		autoInsertionDelay = WRITING_SPEED_DELAYS [speedIndex];
	}

	public void keyPressed(int keyCode, int gameActionCode) {
		//letter pad active, a list of letters, to choose from, should be on display
		if(gameActionCode == Canvas.LEFT && keyCode != Canvas.KEY_NUM4){
			if(isLetterPadActive){
				if(this.currentLetterPosition > 0){
					currentLetterPosition--;
				}else{
					currentLetterPosition = renderer.getMaxLetterCountInCurrentPanel(currentMode, currentKeyCode, currentKeyPressCount) - 1;
				}
				if(parent != null){
					parent.repaintNeeded();
				}
				resetAutoInsertionTimer();
			}else if(gridBox == null){
				editor.moveCursor(Canvas.LEFT);
			}
		}else if(gameActionCode == Canvas.RIGHT && keyCode != Canvas.KEY_NUM6){
			if(isLetterPadActive){
				if(this.currentLetterPosition < renderer.getMaxLetterCountInCurrentPanel(currentMode, currentKeyCode, currentKeyPressCount) - 1){
					currentLetterPosition++;
					if(parent != null){
						parent.repaintNeeded();
					}
				}else{
					currentLetterPosition = 0;
				}
				resetAutoInsertionTimer();
			}else if(gridBox == null){
				editor.moveCursor(Canvas.RIGHT);
			}
		}else if(gameActionCode == Canvas.UP && keyCode != Canvas.KEY_NUM2){
			if(isLetterPadActive){
				//Nothing to do for now, we could think of selecting previous letter though
			}else if(gridBox == null){
				editor.moveCursor(Canvas.UP);
			}
		}
		else if(gameActionCode == Canvas.DOWN && keyCode != Canvas.KEY_NUM8){
			if(isLetterPadActive){
				//Nothing to do for now, we could think of selecting previous letter though
			}else if(gridBox == null){
				editor.moveCursor(Canvas.DOWN);
			}
		}
		else if(isLetterPadActive && gameActionCode == Canvas.FIRE && keyCode != Canvas.KEY_NUM5){
			insertCurrentLetter();
			stopAutoInsertionTimer();
		}else if(DisplayManagerLcdui.isClearKey(keyCode)){
			clearLetter();
			stopAutoInsertionTimer();
		}
		else if(keyCode >= Canvas.KEY_NUM0 && keyCode <= Canvas.KEY_NUM9 ||
				(keyCode == Canvas.KEY_STAR && currentMode == BanglaRenderer.BANGLA)){// Key0 to Key 9 and special key * for Bangla mode
			int newKeyCode;
			if(keyCode == Canvas.KEY_STAR){
				newKeyCode = 10;//Some hard coded hacky value //TODO fix hard coded value 10 somehow
			}else{
				newKeyCode = keyCode - Canvas.KEY_NUM0;//to make key values from 0 to 9
			}
			if(isLetterPadActive && newKeyCode != currentKeyCode){
				insertCurrentLetter();
			}

			if(newKeyCode == currentKeyCode && currentKeyPressCount < renderer.getMaxPressCount(currentKeyCode, currentMode)){
				currentKeyPressCount++;
			}else{
				currentKeyPressCount = 0;
			}

			previousKeyCode = currentKeyCode;
			currentKeyCode = newKeyCode;
			currentLetterPosition = 0;
			isLetterPadActive = true;
			resetAutoInsertionTimer();
			if(parent != null){
				parent.repaintNeeded();
			}
		} else if (keyCode == Canvas.KEY_POUND) {
			if (currentMode == BanglaRenderer.BANGLA && this.gridBox == null) {
				showGrid(true);
			}
			stopAutoInsertionTimer();
		}
	}

	private void clearLetter() {
		if(this.gridBox != null){
			clearGridBox();
		}
		if(isLetterPadActive){
			resetLetterPanel();
		}else{
			editor.clearLetter();
		}
	}

	private void insertCurrentLetter() {
		boolean isBanglaMode = currentMode == BanglaRenderer.BANGLA;
		editor.insertLetter(renderer.getGSM(currentKeyCode, currentKeyPressCount, isBanglaMode ? currentLetterPosition : currentMode, isBanglaMode));//TODO BANGLA Looks like hacky way isBanglaMode ? currentLetterPosition : BanglaRenderer.ENGLISH_CAPITAL
		resetLetterPanel();
		if(parent != null){
			parent.repaintNeeded();
		}
	}

	private void resetLetterPanel(){
		currentLetterPosition = 0;
		currentKeyCode = -1;
		previousKeyCode = -2;//TODO need explanation!
		isLetterPadActive = false;
	}
	public void keyReleased(int keyCode, int gameActionCode) {
	}

	public void keyRepeated(int keyCode, int gameActionCode) {
		this.keyPressed(keyCode, gameActionCode);
	}

	public void paint(Graphics g) {
		g.setColor(letterPanelBackgroundColor);
		int x = 0;
		int y = editor.getHeight() - renderer.getFontHeight();
		g.fillRect(x, y, editor.getWidth(), renderer.getFontHeight());
		if(isLetterPadActive){
			int letterCount = renderer.getMaxLetterCountInCurrentPanel(currentMode, currentKeyCode, currentKeyPressCount);
			boolean isBanglaMode = this.currentMode == BanglaRenderer.BANGLA ;			
			BanglaFont font;
			int eachLetterWidth = renderer.getMaxFontWidth() + 2;
			int centerOfLetterBox = eachLetterWidth / 2;
			for(int i = 0; i < letterCount; i++){				
				char gsm = this.renderer.getGSM(currentKeyCode, currentKeyPressCount, isBanglaMode ? i : currentMode, isBanglaMode);//TODO looks strange when passing the mode for English, while passing pos for bangla
				font = this.renderer.getFont(gsm, isBanglaMode, false);
				if(font.image != null){
					if(currentLetterPosition == i){
						g.setColor(letterSelectionColorBorder);
						g.drawRect(x, y, eachLetterWidth - 1, renderer.getFontHeight() - 1);
					}
					g.drawImage(font.image, x + centerOfLetterBox, y, Graphics.HCENTER| Graphics.TOP);
					x += eachLetterWidth;
				}
			}
		}else{
			stopAutoInsertionTimer();
		}

	}

	private void clearGridBox(){
		if(parent != null && gridBox != null){
			parent.removeComponent(gridBox);
			parent.repaintNeeded();
			gridBox = null;
		}
	}

	public int getCurrentMode(){
		return this.currentMode;
	}

	public void setCurrentMode(int mode){
		this.currentMode = mode;
		resetLetterPanel();
	}

	public void commandAction(Command command) {
		if (command == commandInsertJuktakkhor) {
			showGrid(true);
		}else if (command == commandInsertSmiley) {
			showGrid(false);
		}else if (command == commandClear) {
			clearLetter();
			clearGridBox();//if any
		}else if (command == commandAddLetter) {
			if(isLetterPadActive && gridBox == null) {
				insertCurrentLetter();
			}
		}else if (command == commandSetBanglaMode) {
			parent.removeCommand(commandSetBanglaMode);
			parent.addCommand(commandSetEnglishMode);
			setCurrentMode(BanglaRenderer.BANGLA);
		}else if (command == commandSetEnglishMode) {
			parent.addCommand(commandSetBanglaMode);
			parent.removeCommand(commandSetEnglishMode);
			setCurrentMode(BanglaRenderer.ENGLISH_CAPITAL);
		}else if (command == commandSetWritingSpeed) {			
			final View lastView = App.getDisplayManager().getCurrent();
			final ChoiceView choiceView = App.getDisplayManager().getChoiceView("Select writing speed");
			choiceView.setOptions(WRITING_SPEED_OPTIONS);
			choiceView.setSelectedOption(currentWritingSpeedIndex);
			choiceView.addEventHandler(new EventHandler(){
				public void handleEvent(Event event) {
					if (ChoiceView.EVENT_CHOICE_SELECTED.equals(event.getName())){
						int selectedChoice = choiceView.getSelectedOption();
						if (selectedChoice != currentWritingSpeedIndex) {
							currentWritingSpeedIndex = selectedChoice;
							autoInsertionDelay = WRITING_SPEED_DELAYS [currentWritingSpeedIndex];
							App.getProperties().setProperty(WRITING_SPEED_PROPERTY_KEY, String.valueOf(selectedChoice));
						}
					}
					App.getDisplayManager().setOnScreen(lastView);
				}
			});

			choiceView.init();
			App.getDisplayManager().setOnScreen(choiceView);
		}
	}

	private void showGrid(final boolean isJuktakkhorGrid) {
		clearGridBox();//remove old one if any
		resetLetterPanel();
		Image[] images = null;
		if(isJuktakkhorGrid){
			images = renderer.getJuktakkhorImages();
		}else{
			images = renderer.getSmileyImages();
		}
		gridBox = new GridBox(images, 22,22, 4, 5, editor.getWidth(), editor.getHeight());//TODO hardcoded values
		gridBox.setParent(parent);
		gridBox.setAddCommand(commandAddLetter);
		parent.addComponent(gridBox);
		parent.repaintNeeded();

		gridBox.setGridBoxHandler(new GridBoxHandler(){
			public void gridSelected(int selectedIndex) {
				if(isJuktakkhorGrid){
					editor.insertJuktakkhor(selectedIndex);
				}else{
					editor.insertSmiley(selectedIndex);
				}
				clearGridBox();
			}
		});
	}



	public CustomCanvasComponent getParent() {
		return this.parent;
	}

	public void setParent(CustomCanvasComponent parent) {
		this.parent = parent;
		if(parent != null){
			parent.addComponent(this);
			commandClear = new Command("Clear",  Command.CANCEL, 1);
			commandAddLetter = new Command("Add", Command.OK, 1);
			commandSetBanglaMode = new Command("Bangla Mode", Command.ITEM, 1);
			commandSetEnglishMode = new Command("English Mode", Command.ITEM, 1);
			commandInsertJuktakkhor = new Command("Insert Juktakkhor",  Command.ITEM, 2);
			commandInsertSmiley= new Command("Insert Smiley", Command.ITEM, 2);
			commandSetWritingSpeed = new Command("Writing speed", Command.ITEM, 3);

			parent.addCommand(commandClear);
			parent.addCommand(commandInsertJuktakkhor);
			parent.addCommand(commandInsertSmiley);
			parent.addCommand(commandAddLetter);
			parent.addCommand(commandSetWritingSpeed);

			Command command;
			if(getCurrentMode() == BanglaRenderer.BANGLA){
				command = commandSetEnglishMode;
			}else{
				command = commandSetBanglaMode;
			}
			this.parent.addCommand(command);
		}
	}

	private void startAutoInsertionTimer(){
		if(autoInsertTimer != null){
			autoInsertTimer.cancel();
		}
		autoInsertTimer = new Timer();
		autoInsertTimer.schedule(new AutoInsertTimerTask(), autoInsertionDelay);
	}

	private void stopAutoInsertionTimer(){
		if(autoInsertTimer != null){
			autoInsertTimer.cancel();
			autoInsertTimer = null;
		}
	}

	private void resetAutoInsertionTimer(){
		stopAutoInsertionTimer();
		startAutoInsertionTimer();
	}
	
	public void repaintNeeded() {
		if (parent != null) {
			parent.repaintNeeded();
		}
	}

	public void repaintNeeded(int x, int y, int width, int height) {
		if (parent != null) {
			parent.repaintNeeded(x, y, width, height);
		}
	}
	
	public void showNotify() {
	}
	
	public void hideNotify() {
		resetLetterPanel();
		clearGridBox();
		stopAutoInsertionTimer();
	}

	public void addCommand(Command command) {//we dont have any sub component, so no worries to implement this method} 
	}

	public void addComponent(CustomCanvasComponent component) {//we dont have any sub component, so no worries to implement this method} 
	}

	public void removeCommand(Command command) {//we dont have any sub component, so no worries to implement this method} 
	}

	public void removeComponent(CustomCanvasComponent component) {//we dont have any sub component, so no worries to implement this method} 
	}
	
	public void sizeChanged(int width, int height){//we dont have any sub component, so no worries to implement this method} 
	}	
	
	class AutoInsertTimerTask extends TimerTask{
		public void run() {
			if(isLetterPadActive){
				insertCurrentLetter();
			}
		}
	}	
}
