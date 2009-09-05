package org.bd.banglasms.util;

import org.bd.banglasms.Logger;

/**
 * Simplest logger that shows the messages on console.
 *
 */
public class ConsoleLogger implements Logger{

	private int currentLevel = Logger.DEFAUL_LEVEL;
	
	public ConsoleLogger() {
	}
	
	public ConsoleLogger(int level) {
		setLevel(level);
	}
	
	public void setLevel(int level) {
		System.out.println("Logger level set to " + getLevelText(level) + ". Messages with lower level will not be shown.");
		currentLevel = level;
	}
	
	public int getLevel() {
		return currentLevel;
	}
	
	public void log(String message, int level){
		if( level >= currentLevel ) {
			System.out.println(getLevelText(level) + message);
		}
	}

	private String getLevelText(int level) {
		switch (level) {
		case Logger.LEVEL_DEBUG:
			return "[DEBUG]";
		case Logger.LEVEL_INFO:
			return "[INFO]";
		case Logger.LEVEL_WARNING:
			return "[WARNING]";
		case Logger.LEVEL_ERROR:
			return "[ERROR]";
		default:
			return "[UNKNOWN_LEVEL]";
		}
	}
}
