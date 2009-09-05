package org.bd.banglasms.ui;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;

import org.bd.banglasms.control.event.Event;
import org.bd.banglasms.control.event.EventHandler;

/**
 * A view is a general interface for all UI views. A view notifies user
 * interactions through {@link Event}s.
 * <p/>
 * This interface hides how the actual user interface may be. View interfaces
 * will only define, which events it must support, but does not impose how the
 * view must be shown.
 * <p/>
 * For example, BusyView interface only tells implementations must support a
 * cancel event. One implementation may choose MIDP Lcdui {@link Alert} with
 * indefinite gauge to show busy view and a {@link Command} with "Cancel" label
 * may notify the cancel event.Other implementation may choose to show an
 * animated image in the middle of screen and any key press may act as cancel.
 * </p>
 * Since the view interface is independent of any UI library, implementations
 * are free to choose native lcdui package or any other thrid party UI library
 * to implement view without modifiying the control part.
 *
 */
public interface View {
	public void addEventHandler(EventHandler eventHandler);

	public void removeEventHandler(EventHandler eventHandler);

	public void init();

	public void setTitle(String title);
}
