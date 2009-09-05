package org.bd.banglasms.ui;


/**
 * Splash screen which can be shown on screen and listens for user key events.
 * <p/>
 * If user presses any key while splash screen is on display,
 * {@link #EVENT_KEY_PRESS_ON_SPLASH} will be notified.
 *
 */
public interface SplashScreen extends View {
	/**
	 * Event that is notified when any key press occurs while splash screen is
	 * on display.
	 */

	public static final String EVENT_KEY_PRESS_ON_SPLASH = "key_press_on_splash_screen";

	public void setText(String text);
}
