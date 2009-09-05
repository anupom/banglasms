package org.bd.banglasms;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import org.bd.banglasms.control.MainController;
import org.bd.banglasms.control.event.Event;
import org.bd.banglasms.control.event.EventHandler;
import org.bd.banglasms.ui.SplashScreen;
import org.bd.banglasms.ui.lcdui.DisplayManagerLcdui;
import org.bd.banglasms.util.PropertiesImpl;
import org.bd.banglasms.util.ResourceManagerImpl;

/**
 * Application MIDlet class.
 *
 */
public class BanglaSMS extends MIDlet {
    private static final int DEFAULT_PORT = 6543;
    private static final int SPLASH_SCREEN_DELAY = 2000;
    private boolean startAppFirstTime = true;
    private boolean splashDone = false;
    private MainController mainController;


    public BanglaSMS() {
    }

    protected void destroyApp(boolean unconditional){
      if(mainController != null){
          mainController.onExit();
      }
    }

    protected void pauseApp() {
    }

    protected final void startApp() throws MIDletStateChangeException {
       if(startAppFirstTime){
    	   //first job is to initialize the App
    	   startAppFirstTime = false;
    	   int smsPort = DEFAULT_PORT;
		   String portDefinedInJad = this.getAppProperty("SMS-Port");
		   if(portDefinedInJad != null){
	    	   try{
	    		   smsPort = Integer.parseInt(portDefinedInJad);
	    	   }catch(NumberFormatException ex){
	    		   App.getLogger().log("BanglaSMS.startApp could not read port. Check if a valid number is defined in Property 'BS-Port' in JAD or if the application was installed using JAD. Default port will be used now. Exception Details: " + ex, Logger.LEVEL_ERROR);
	    	   }
		   }
           App.init(new DisplayManagerLcdui(Display.getDisplay(this)), this, new ResourceManagerImpl(), new PropertiesImpl(), smsPort);
           mainController = new MainController(smsPort, BanglaSMS.class.getName());

           //show the splash screen
           final SplashScreen splash = App.getDisplayManager().getSplashScreen();
    	   splash.setText("Version " + App.CURRENT_VERSION);
    	   splash.addEventHandler(new EventHandler(){
    		  public void handleEvent(Event event) {
    			  //must be on key press on splash screen
    			  synchronized (splash) {
    				  if(! splashDone){
        				  splashDone = true;
        				  mainController.onStart();
    				  }
    			  }
    		  }
    	   });
    	   splash.init();
    	   App.getDisplayManager().setOnScreen(splash);

    	   //launch the main control in a separate thread, allowing splash screen to show early
			new Thread() {
				public void run() {
					try {
						Thread.sleep(SPLASH_SCREEN_DELAY);
						synchronized (splash) {
							if (!splashDone) {
								splashDone = true;
								mainController.onStart();
							}
						}
					} catch (InterruptedException ex) {
    				   App.getLogger().log("BanglaSMS.startApp caught exception while waiting in splash screen " + ex, Logger.LEVEL_ERROR);
    			   }
    		   }
    	   }.start();
       }
    }

    void exit(){
        this.destroyApp(true);
    }

}
