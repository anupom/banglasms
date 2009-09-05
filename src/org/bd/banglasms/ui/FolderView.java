package org.bd.banglasms.ui;

import org.bd.banglasms.control.UIEvents;
import org.bd.banglasms.store.BanglaMessage;

/**
 * This view shows a folder to user. For example, it may show the messages in a
 * list or in other way it prefers.
 *
 * <h2> Events </h2>
 * <UL> It must support the following events.
 * <LI> {@link UIEvents#OPEN_MESSAGE}
 * <LI> {@link UIEvents#CLOSE_FOLDER}
 * <LI> {@link UIEvents#DELETE_MESSAGE}
 * </UL>
 */
public interface FolderView extends View{
	public void appendMessage(BanglaMessage message);
	public void addMessageAtBeginning(BanglaMessage messsage);
	public void remove(BanglaMessage message);
	public void setId(int id);
	public int getId();
}
