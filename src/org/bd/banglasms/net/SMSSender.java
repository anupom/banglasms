package org.bd.banglasms.net;

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.TextMessage;

import org.bd.banglasms.App;
import org.bd.banglasms.Logger;
import org.bd.banglasms.control.event.Event;
import org.bd.banglasms.control.event.EventHandler;
import org.bd.banglasms.control.event.NotifierHelper;
import org.bd.banglasms.store.BanglaMessage;

/**
 * This class sends sms in a convenient method running in a separate thread and
 * notifies about the result.
 */
public class SMSSender {

	public static final String EVENT_SMS_SEND_SUCCESS = "event_sms_send_success";
	public static final String EVENT_SMS_SEND_FAILED = "event_sms_send_failed";
	public static final String PARAM_MESSAGE = "param_message";

	private int port;
	private NotifierHelper notifier = new NotifierHelper();

	public SMSSender(int port){
		this.port = port;
	}

	/**
	 * This is a non-blocking method that will send the sms over the network
	 * using a separate thread and will notify with an Event about the status.
	 * 
	 * @param message
	 *            message to be sent
	 * 
	 */
	public void send(final BanglaMessage message){
		new Thread(){
			public void run(){
				MessageConnection connection = null;
				try{
					String address = "sms://"+ message.getContactNumber() + ":" + port;
					connection = (MessageConnection) Connector.open(address);
					TextMessage textMessage = (TextMessage)connection.newMessage(MessageConnection.TEXT_MESSAGE);
					textMessage.setAddress(address);
					textMessage.setPayloadText(message.getText());
					connection.send(textMessage);
					Event event = new Event(EVENT_SMS_SEND_SUCCESS, this);
					event.setValue(PARAM_MESSAGE, message);
					notifier.notify(event);
				}catch(Exception ex){
					 // IllegalArgumentException If address is invalid.
					 // ConnectionNotFoundException  If the target of the address cannot be found, or if the requested protocol type is not supported.
					 // IOException If some other kind of I/O error occurs.
					 // SecurityException May be thrown if access to the protocol handler is prohibited.
					Event event = new Event(EVENT_SMS_SEND_FAILED, this);
					event.setValue(PARAM_MESSAGE, message);
					notifier.notify(event);
				}
				finally{
					if(connection != null){
						try {
							connection.close();
						} catch (IOException e) {
							App.getLogger().log("SMSSender.send silently caught Exception while closing message connection : " + e, Logger.LEVEL_ERROR);
						}
					}
				}
			}
		}.start();


	}

	public void addEventHandler(EventHandler eventHandler){
		notifier.add(eventHandler);
	}

	public void removeEventHandler(EventHandler eventHandler){
		notifier.remove(eventHandler);
	}
}
