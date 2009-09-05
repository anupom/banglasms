package org.bd.banglasms.ui;

import org.bd.banglasms.control.UIEvents;
import org.bd.banglasms.store.BanglaMessage;

/**
 * This view shows a Bangla text message. View can be set as editable as well, in which case, user is able to edit the text message.
 * <p/>
 * View must update the options available to users depending on message id. For example, if the give message has Inbox id, then view must show the Reply option to user.
 *
 * <a name="DifferentEvents"><h2> Different Events </h2></a>
 * <DL> Events will be depending on message folder id. User options must be shown accordingly.
 * <DT><STRONG>Inbox</STRONG>
 * <DD> {@link UIEvents#REPLY_MESSAGE}, {@link UIEvents#FORWARD_MESSAGE}, {@link UIEvents#SAVE_MESSAGE_AS_DRAFT}, {@link UIEvents#SAVE_MESSAGE_AS_TEMPLATE}, {@link UIEvents#DELETE_MESSAGE}, {@link UIEvents#BACK_FROM_MESSAGE}
 * <DT><STRONG>Sent</STRONG>
 * <DD>{@link UIEvents#FORWARD_MESSAGE}, {@link UIEvents#SAVE_MESSAGE_AS_DRAFT}, {@link UIEvents#SAVE_MESSAGE_AS_TEMPLATE}, {@link UIEvents#DELETE_MESSAGE}, {@link UIEvents#BACK_FROM_MESSAGE}
 * <DT><STRONG>Template</STRONG>
 * <DD>{@link UIEvents#EDIT_MESSAGE}, {@link UIEvents#FORWARD_MESSAGE}, {@link UIEvents#SAVE_MESSAGE_AS_DRAFT}, {@link UIEvents#SAVE_MESSAGE_AS_TEMPLATE}, {@link UIEvents#DELETE_MESSAGE}, {@link UIEvents#BACK_FROM_MESSAGE}
 * <DT><STRONG>Draft</STRONG>
 * </DL>
 *
 */
public interface BanglaMessageView extends View{
	public void setMessage(BanglaMessage message);
	public BanglaMessage getMessage();
	public void setEditable(boolean editable);
	public String getText();
}
