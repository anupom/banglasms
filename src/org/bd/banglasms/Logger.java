package org.bd.banglasms;

/**
 * Provides an interface to log messages in different level.
 * Implementation may choose to display on console or store in RMS, or even
 * send them via network.
 */
public interface Logger {

	public static final int LEVEL_DEBUG = 0;
	public static final int LEVEL_INFO = 1;
	public static final int LEVEL_WARNING = 2;
	public static final int LEVEL_ERROR = 3;
	public static final int DEFAUL_LEVEL = LEVEL_DEBUG;

	/**
	 * Logs the message. Messages will be logged only if the message level is equal to or greater than current level
	 * @param message message to be logged
	 * @param level level of the message,  messages less than the current level ({@link #getLevel()} will not be logged
	 */
	public void log(String message, int level);
	
	/**
	 * Sets the level of logging. Messages will be logged only if the message level is equal to or greater than current level
	 * @param level level to log, messages less than this level will not be logged
	 */
	public void setLevel(int level);
	
	/**
	 * Returns the current logging level
	 * @return the current logging level
	 */
	public int getLevel();
}
