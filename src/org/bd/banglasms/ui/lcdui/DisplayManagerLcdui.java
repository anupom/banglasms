package org.bd.banglasms.ui.lcdui;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

import org.bd.banglasms.App;
import org.bd.banglasms.Logger;
import org.bd.banglasms.control.event.Event;
import org.bd.banglasms.control.event.EventHandler;
import org.bd.banglasms.ui.BanglaTextView;
import org.bd.banglasms.ui.BusyView;
import org.bd.banglasms.ui.ChoiceView;
import org.bd.banglasms.ui.CreditView;
import org.bd.banglasms.ui.DisplayManager;
import org.bd.banglasms.ui.FolderView;
import org.bd.banglasms.ui.MainView;
import org.bd.banglasms.ui.BanglaMessageView;
import org.bd.banglasms.ui.MessageBox;
import org.bd.banglasms.ui.SplashScreen;
import org.bd.banglasms.ui.View;

/**
 * Implementation of {@link DisplayManager} using javax.microedition.lcdui
 * package.
 * 
 */
public class DisplayManagerLcdui implements DisplayManager{

	private Display display;
	private Displayable lastNonAlert;
	private View currentView;

	private static int clearKeyCode;
	private static boolean keyCodeCheckDone = false;
	private static boolean clearKeyFound = false;

	public DisplayManagerLcdui(Display display){
		this.display = display;
	}

	public FolderView getFolderView() {
		return new FolderViewLcdui();
	}

	public MainView getMainView(String title) {
		return new MainViewLcdui(title);
	}

	public BanglaMessageView getEditorView() {
		return new BanglaMessageViewLcdui();
	}

	public SplashScreen getSplashScreen(){
		return new SplashScreenLcdui();
	}

	public ChoiceView getChoiceView(String title) {
		return new ChoiceViewLcdui (title);
	}

	public BanglaTextView getTextView() {
		return new BanglaTextViewLcdui();
	}
	public CreditView getCreditView() {
		return new CreditViewLcdui();
	}
	public void setOnScreen(View view) {
		if(view instanceof LcduiView){
			Displayable displayable = ((LcduiView)view).getDisplayable();
			boolean isAlert = displayable instanceof Alert;
			if(!isAlert){
				lastNonAlert = displayable;
			}
			//trick to avoid the error "Alert cannot be displayed on Alert"
			if(!isAlert){
				display.setCurrent(displayable);
			}else{
				if(lastNonAlert != null){
					display.setCurrent((Alert)displayable, lastNonAlert);
				}else{
					App.getLogger().log("DisplayManagerLcdui handling alert on alert. New alert text : " + ((Alert)displayable).getString(), Logger.LEVEL_DEBUG);
					display.setCurrent((Alert)displayable, new Form(null));
				}
			}
			currentView = view;
		}
		else{
			App.getLogger().log("DisplayManagerLcdui cannot accept any view other than LcduiView", Logger.LEVEL_WARNING);
		}
	}

	public int getColor(int colorSpecifier) {
		return display.getColor(colorSpecifier);
	}

	public BusyView getBusyView(){
		return new BusyViewLcdui();
	}

	public MessageBox getMessageBox() {
		return new MessageBoxLcdui();
	}

	public void showMessage(String message, boolean waitForUserOk) {
		MessageBox messageBox = getMessageBox();
		messageBox.setText(message);
		if(waitForUserOk){
			messageBox.setOptionStyle(MessageBox.OPTION_OK);
		}else{
			messageBox.setOptionStyle(MessageBox.NO_OPTION);
		}
		messageBox.addEventHandler(new EventHandler(){

			public void handleEvent(Event event) {
				display.setCurrent(lastNonAlert);
			}

		});
		setOnScreen(messageBox);
	}

	public void showMessage(String message) {
		showMessage(message, false);
	}

	/**
	 * Returns true if the keyCode indicates a clear or backspace key. Note,
	 * this method may return false for some devices where there is a key for
	 * clear.
	 * 
	 * @param keyCode
	 *            key code to be checked
	 * @return true if the keyCode indicates a clear or backspace key, else
	 *         false
	 */
	public static boolean isClearKey(int keyCode) {
		if(! keyCodeCheckDone){
			keyCodeCheckDone = true;

			if(! clearKeyFound){
		        try {
		            Class.forName("com.nokia.mid.ui.FullCanvas");
		            clearKeyCode = -8;
		            clearKeyFound = true;
		        } catch (ClassNotFoundException _ex) {
		        }
			}

			if(! clearKeyFound){
				String platform = System.getProperty("microedition.platform");
				if(platform != null){
					platform = platform.toLowerCase();
					if((platform.indexOf("sun") >= 0 && platform.indexOf("wtk") >=0) ||
							platform.indexOf("sonyericsson") >= 0 ||
							platform.indexOf("samsung") >= 0){
						clearKeyCode = -8;
						clearKeyFound = true;
					}
				}
			}

			if(! clearKeyFound){
		        try {
		        	//Siemens
		            Class.forName("com.siemens.mp.game.Light");
		            clearKeyFound = true;
		            clearKeyCode = -12;
		        } catch (ClassNotFoundException _ex) {
		        }
			}

			if(! clearKeyFound){
		        try {
		        	//Blackberry
		            Class.forName("net.rim.device.api.system.Application");
		            clearKeyCode = 8;
		            clearKeyFound = true;
		        } catch (ClassNotFoundException ex) {
		        }
			}
		}
		if(clearKeyFound && keyCode == clearKeyCode){
			return true;
		}
		return false;
	}
	
	public View getCurrent() {
		return this.currentView;
	}
}
