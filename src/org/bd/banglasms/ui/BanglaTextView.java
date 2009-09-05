package org.bd.banglasms.ui;

import org.bd.banglasms.control.UIEvents;

/**
 * A view that shows bangla text, which is not-editable and large texts must be scrollable.
 * <h2>Events</h2>
 * It will notify {@link UIEvents#BACK} when user intends to go to last view.
 *
 */
public interface BanglaTextView extends View {
	public void setText(String text);
}
