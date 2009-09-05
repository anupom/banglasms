package org.bd.banglasms.net;

import javax.wireless.messaging.Message;

/**
 * Interface that is used to notify on incoming message.
 */
public interface SMSNotificationHandler {

	/**
	 * This is called when a message arrive to phone. This method sends messages
	 * serially, meaning if multiple messages arrive almost at the same time, this
	 * method will send them one by one, next message will not be sent until
	 * current message is handled.
	 *
	 * @param message
	 *            message received
	 */
	public void smsReceived(Message message);

	/**
	 * This is called when an error occurs during message reception
	 *
	 * @param ex
	 *            Exception that was generated during message reception
	 */
	public void smsReceptionError(Exception ex);
}
