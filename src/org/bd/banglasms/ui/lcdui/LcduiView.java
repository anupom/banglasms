package org.bd.banglasms.ui.lcdui;

import javax.microedition.lcdui.Displayable;

import org.bd.banglasms.ui.View;

/**
 * A general interface for Views that uses javax.microedition.lcdui package for
 * {@link View} implementations.
 * This will be used by {@link DisplayManagerLcdui} to handle Views.
 */
public interface LcduiView {
	public Displayable getDisplayable();
}
