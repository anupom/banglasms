package org.bd.banglasms;

import javax.microedition.io.ConnectionNotFoundException;

import org.bd.banglasms.ui.DisplayManager;
import org.bd.banglasms.util.ConsoleLogger;


/**
 * Class that knows all the application level modules.
 */
public class App {

	public static final String CURRENT_UPDATE_ID = "200902210001";
	//we are not using MIDlet-version because that has a very restricted numbering format
	public static final String CURRENT_VERSION = "2.0.0";

	private static Logger _logger = new ConsoleLogger(Logger.LEVEL_INFO);
	private static DisplayManager _displayManager;
	private static BanglaSMS _midlet;
	private static ResourceManager _resourceManager;
	private static Properties _properties;
	private static int _smsPort;
	private static boolean _initDone = false;

	public static void init(DisplayManager displayManager, BanglaSMS midlet, ResourceManager resourceManager, Properties properties, int smsPort){
		_displayManager = displayManager;
		_midlet = midlet;
		_resourceManager = resourceManager;
		_properties = properties;
		_smsPort = smsPort;
		_initDone = true;
	}

	public static DisplayManager getDisplayManager(){
		if (! _initDone) {
			throw new IllegalStateException ("init must be called before.");
		}
		return _displayManager;
	}

	public static void exit(){
		_midlet.exit();
		_midlet.notifyDestroyed();
	}

	public static ResourceManager getResourceManager(){
		if (! _initDone) {
			throw new IllegalStateException ("init must be called before.");
		}
		return _resourceManager;
	}

	public static Logger getLogger(){
		return _logger;
	}

	public static int getSMSPort(){
		if (! _initDone) {
			throw new IllegalStateException ("init must be called before.");
		}
		return _smsPort;
	}

	public static Properties getProperties() {
		if (! _initDone) {
			throw new IllegalStateException ("init must be called before.");
		}
		return _properties;
	}

	public static boolean platformRequest(String request) throws ConnectionNotFoundException {
		if (! _initDone) {
			throw new IllegalStateException ("init must be called before.");
		}
		return _midlet.platformRequest(request);
	}
}
