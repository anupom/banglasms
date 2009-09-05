package org.bd.banglasms.ui;
/**
 * This view shows an idicator to user that application is working on background.
 * <h2>Events</h2>
 * Upon cancellation it will throw {@link #EVENT_BUSY_VIEW_CANCELLED}.
 *
 */
public interface BusyView extends View{
	public static final String EVENT_BUSY_VIEW_CANCELLED = "event_busy_view_cancelled";
	public void setText(String text);
	public void setCancel(boolean hasCancel);
	public void setIndefinite(boolean isIndefinite);
	public void setProgress(int alreadyDone, int totalToDo);
}
