 package org.bd.banglasms.net;



import java.io.IOException;
import java.io.InterruptedIOException;

import javax.microedition.io.Connector;
import javax.microedition.io.PushRegistry;
import javax.wireless.messaging.Message;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.MessageListener;
import org.bd.banglasms.App;
import org.bd.banglasms.Logger;

/**
 * This class receives messages and notifies listeners about incoming messages through {@link SMSNotificationHandler}.
 *
 */
public class SMSReceiver implements MessageListener {
	private String connectionString;
	private MessageConnection connection;
	private ReceiverThread receiverThread;
	private SMSNotificationHandler handler;
	private String midletClass;

	/**
	 * Constructs the receiver.
	 * @param midletClass Fully qualified class name of the MIDlet which will be used to register the sms listener
	 * @param port port to which sms will be listened
	 * @param handler SMSNotificationHandler which will be notified about the incoming messages
	 */
	public SMSReceiver(String midletClass, int port, SMSNotificationHandler handler) {
		connectionString = "sms://:" +  port;
		this.handler = handler;
		this.midletClass = midletClass;
	}
	
	/**
	 * This registers itself to PushRegistry to listen to incoming messages
	 * @throws IOException in case of IO error
	 * @throws ClassNotFoundException in case of error
	 * @throws SecurityException in case of permission missing
	 */
	public void connect() throws IOException, ClassNotFoundException {
		//if in case push registry was not done (for example, some installed from jar directly?)
		String midletRegistered = PushRegistry.getMIDlet(connectionString);
		if(midletRegistered != null){
			if(! midletClass.equals(midletRegistered)){
				//Oh someone else already registered for it?
				throw new IOException("Some other midlet already registered the connection. Other midlet name = " + midletRegistered);
			}
		}else{
			//register
			PushRegistry.registerConnection(connectionString, midletClass, "*");
		}
		connection = (MessageConnection) Connector.open(connectionString);
		receiverThread = new ReceiverThread();
		receiverThread.start();
		connection.setMessageListener(this);
	}

	public void notifyIncomingMessage(MessageConnection conn) {
		if (conn == connection) {
			receiverThread.handleMessage();
		}
	}

	/**
	 * Closes the listening Thread. Note this does not deregister from
	 * PushRegistry. So incoming sms will still be notified after
	 * MIDlet exit.
	 */
	public void close(){
		if(connection != null){
			try {
				connection.close();
			} catch (IOException ex) {
				App.getLogger().log("SMSReceiver.close() caught silently Exception : " + ex, Logger.LEVEL_ERROR);
			}
		}
		if(receiverThread != null){
			receiverThread.close();
		}
	}

	class ReceiverThread extends Thread {

		private boolean done = false;
		private int pendingMessages = 0;

		public void run() {
			while (!done) {
				synchronized (this) {
					while (pendingMessages == 0) {
						try {
							wait();
						} catch (InterruptedException e) {
						}
					}
					pendingMessages--;
				}
				try {
					Message message = connection.receive();
					if (message != null && handler != null) {
						try{
							handler.smsReceived(message);
						}catch(Throwable ex){
							App.getLogger().log("SMSReceiver caught unhandled exception on notification, but thread will keep running : " + ex, Logger.LEVEL_ERROR);
						}
					}
				} catch (InterruptedIOException ex) {
					if(handler != null){
						handler.smsReceptionError(ex);
					}
				} catch (IOException ex) {
					if(handler != null){
						handler.smsReceptionError(ex);
					}
				}catch(SecurityException ex){
					if(handler != null){
						handler.smsReceptionError(ex);
					}
				}
			}
		}

		public void close() {
			this.done = true;
			synchronized (this) {
				notifyAll();
			}

		}

		public synchronized void handleMessage()
 		{
 	    	pendingMessages++;
 	    	notifyAll();
 		}
	}
}
