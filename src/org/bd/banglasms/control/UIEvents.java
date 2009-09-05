package org.bd.banglasms.control;

import org.bd.banglasms.store.BanglaMessage;

/**
 * Inteface containing general application level UI events.
 *
 */
public interface UIEvents {
	public static final String WRITE_MESSAGE = "write_new_sms";
	public static final String OPEN_INBOX = "open_inbox";
	public static final String OPEN_SENT = "open_sent";
	public static final String OPEN_DRAFTS = "open_drafts";
	public static final String OPEN_TEMPLATE = "open_template";
	public static final String OPEN_CREDIT = "open_credit";
	public static final String OPEN_HELP = "open_help";
	public static final String UPDATE = "update";
	public static final String BACK = "back";
	public static final String EXIT = "exit";
	/** This Event will contain {@link #PARAM_MESSAGE} as parameter, which will indicate to the message to be opened*/
	public static final String OPEN_MESSAGE = "open_message";
	/** This Event will contain {@link #PARAM_MESSAGE} as parameter, which will indicate to the message to be closed*/
	public static final String BACK_FROM_MESSAGE = "back_from_message";
	/** This Event will contain {@link #PARAM_MESSAGE} as parameter, which will indicate to the message to be sent */
	public static final String SEND_MESSAGE = "send_message";
	/** This Event will contain {@link #PARAM_MESSAGE} as parameter, which will indicate to the message to be deleted */
	public static final String DELETE_MESSAGE = "delete_message";
	/** This Event will contain {@link #PARAM_MESSAGE} as parameter, which will indicate to the message to be edited. */
	public static final String EDIT_MESSAGE = "edit_message";
	/** This Event will contain {@link #PARAM_MESSAGE} as parameter, which will indicate to the message to be forwarded. */
	public static final String FORWARD_MESSAGE = "forward_message";
	/** This Event will contain {@link #PARAM_MESSAGE} as parameter, which will indicate to the message to be replied */
	public static final String REPLY_MESSAGE = "reply_message";
	/** This Event will contain {@link #PARAM_MESSAGE} as parameter, which will indicate to the message to be saved in drafts folder. */
	public static final String SAVE_MESSAGE_AS_DRAFT = "save_message_as_draft";
	/** This Event will contain {@link #PARAM_MESSAGE} as parameter, which will indicate to the message to be saved in template folder. */
	public static final String SAVE_MESSAGE_AS_TEMPLATE = "save_message_as_template";

	public static final String CLOSE_FOLDER = "close_folder";
	/** This parameter object be of type {@link BanglaMessage} and will never be null*/
	public static final String PARAM_MESSAGE = "param_message";

}
