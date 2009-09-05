package org.bd.banglasms.control;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.wireless.messaging.Message;
import javax.wireless.messaging.TextMessage;

import org.bd.banglasms.App;
import org.bd.banglasms.Logger;
import org.bd.banglasms.control.event.Event;
import org.bd.banglasms.control.event.EventHandler;
import org.bd.banglasms.net.HTTPTool;
import org.bd.banglasms.net.SMSNotificationHandler;
import org.bd.banglasms.net.SMSReceiver;
import org.bd.banglasms.net.SMSSender;
import org.bd.banglasms.store.BanglaMessage;
import org.bd.banglasms.store.BanglaMessageEnumerator;
import org.bd.banglasms.store.Folder;
import org.bd.banglasms.store.FolderListener;
import org.bd.banglasms.store.Store;
import org.bd.banglasms.store.StoreException;
import org.bd.banglasms.ui.BanglaMessageView;
import org.bd.banglasms.ui.BanglaTextView;
import org.bd.banglasms.ui.BusyView;
import org.bd.banglasms.ui.CreditView;
import org.bd.banglasms.ui.FolderView;
import org.bd.banglasms.ui.MainView;
import org.bd.banglasms.ui.MessageBox;
import org.bd.banglasms.ui.View;
import org.bd.banglasms.util.ContactUtil;

/**
 * Main control class handling application logic and view operations.
 *
 */
public class MainController implements EventHandler, SMSNotificationHandler{

	private static final String TITLE = "BanglaSMS";
	private static final String UPDATE_SERVER = "http://banglasms.org.bd/update.php/?";//TODO JAD would be a better place for this?
	private static final String HELP_TEXT = "9KpvP 0p*Lp vLKp qJ&Us Ks0 aFH# vJpP .&LX vJpfpn NvP XpKpX vJpvPp 5XJpX vPD# vm .&LXhp vLKp 5XJpX vahpX Jp3pJpq3 vbpPpn 9NP (ENGLISH LETTER )9D ep VpSnp mpv0#\"\"vmNP 6Xp mpJ$ J qLKve 2pD# J 9X Nvep vbpPpn (K) # epXNpvP$ (K )2pVve Fv0$ .fzpg M 2pVve Fv0$ me&LQ Pp J v5Kp mpn# 9XVvX 5XJpX FvL (LEFT RIGHT KEY )0à0FpX JXve Fv0#\"\"5pqX JNp Deàpq5 VpSnp mpv0 - 9 #\"\"/ JpX$ j JpX VpSnp mpv0 (STAR )9 #\"\"ms&Mp&LX VpSnp mpv0 (HASH) ()9() ().()f()0()p( OPTION) 9 qBvn #\"\"/X Fà+p$ (OPTION) 9 /v3 (SMILEY &l) #\"\"/vXp HpPve 2vL mpP$ (BANGLASMS.ORG.BD )ve #";

	public final String[] TEMPLATE_TEXTS = new String[]{
            "9Jhs v5qX Fv0&n\"(SORRY",
            "vJNP /v3p%&l\"(SMS) JvXp Pp vJP%",
            "1rcQ 0à&d#\"VvX( CALL )JvXp$\"(PLEASE DONT MIND",
            "K0X qJ%&q"
        };
	private int smsPort;
	private SMSReceiver smsReceiver;
	private MainView mainView;
	private FolderUpdater folderUpdater;
	private FolderView lastFolderView;//we are storing folderView for faster viewing
	private String smsReceptinoToneFile;
	private String smsReceptionToneType;
	private String midletClassName;


	public MainController(int smsPort, String midletClass){
		this.smsPort = smsPort;
		this.midletClassName = midletClass;
		this.smsReceptinoToneFile = "/msg.mid";
		this.smsReceptionToneType = "audio/midi";
	}

	public void onStart() {
		showMainView();
		if(smsReceiver == null){
			smsReceiver = new SMSReceiver(midletClassName, smsPort, this);
			try {
				smsReceiver.connect();
			} catch (IOException e) {
				App.getDisplayManager().showMessage("Oops! Failed to setup sms connection. You may not be able to send or receive BanglaSMS. Try restarting the application. If that does not help, try reinstalling BanglaSMS.", true);
			}catch(ClassNotFoundException ex){
				App.getDisplayManager().showMessage("Oops! Failed to setup sms connection. You may not be able to send or receive BanglaSMS. Try restarting the application. If that does not help, try reinstalling BanglaSMS.", true);
			}
		}
	}

	public void onExit(){
		if(smsReceiver != null){
			smsReceiver.close();
		}
	}

	private void showMainView() {
		closeFolderView();//to clear folder ui from memory
		if(mainView == null){
			mainView = App.getDisplayManager().getMainView(TITLE);
			mainView.addEventHandler(this);
			mainView.init();
		}
		App.getDisplayManager().setOnScreen(mainView);
	}

	private void showFolderView(final int id) {
		if(lastFolderView != null && lastFolderView.getId() == id){
			App.getDisplayManager().setOnScreen(lastFolderView);
		}else{
			final FolderView folderView = App.getDisplayManager().getFolderView();
			folderView.setId(id);
			if(id == Store.INBOX){
				folderView.setTitle("Inbox");
			}else if(id == Store.SENT){
				folderView.setTitle("Sent");
			}else if(id == Store.DRAFT){
				folderView.setTitle("Draft");
			}else if(id == Store.TEMPLATE){
				folderView.setTitle("Template");
			}
			final Folder folder = Store.getFolder(id);
			folderUpdater = new FolderUpdater(folderView);
			folderView.init();
			folderView.addEventHandler(this);
			folder.addFolderListener(folderUpdater);

			this.lastFolderView = folderView;
			final BusyView busyView = App.getDisplayManager().getBusyView();
			busyView.setText("Loading messages...");
			busyView.setCancel(false);
			App.getDisplayManager().setOnScreen(busyView);
			new Thread(){
				public void run(){
					try{
						Thread.yield();//let the other thread load progress bar first
						folder.open();
						BanglaMessageEnumerator enumerator = folder.getEnumerator();
						int total = folder.getMessageCount();
						int loaded = 0;
						BanglaMessage [] messages = new BanglaMessage [total];

						while(enumerator.hasMoreMessage()){
							loaded++;
							messages [loaded - 1] = enumerator.nextMessage();
							busyView.setText("Loading... " + loaded + " / " + total);
							busyView.setProgress(loaded, total);
						}
						sortMessages(messages);
						for (int i = 0; i < messages.length; i++) {
							folderView.appendMessage(messages[i]);
						}

						App.getDisplayManager().setOnScreen(folderView);
					}catch(Exception ex){
						App.getLogger().log("MainController.showFolderView ( " + id + " ) caught exception in thread : " + ex, Logger.LEVEL_ERROR);
						showMainView();
					}finally{
						if(folder != null){
							folder.close();
						}
					}
				}
			}.start();
		}
	}

	private void closeFolderView(){

		if(this.lastFolderView != null){
			Folder folder = Store.getFolder(lastFolderView.getId());
			folder.removeFolderListener(folderUpdater);
		}
		folderUpdater = null;
		lastFolderView = null;
	}

	private void showMessageView(BanglaMessage message, boolean editable){
		BanglaMessageView messageView = App.getDisplayManager().getEditorView();
		messageView.setMessage(message);
		messageView.setEditable(editable);
		messageView.addEventHandler(this);
		messageView.init();
		App.getDisplayManager().setOnScreen(messageView);
	}

	private boolean saveMessage(BanglaMessage messageToSave, int folderId){
		BanglaMessage message = new BanglaMessage(folderId);
		message.setTime(System.currentTimeMillis());
		message.setFolderId(folderId);
		message.setText(messageToSave.getText());
		message.setContactName(messageToSave.getContactName());
		message.setContactNumber(messageToSave.getContactNumber());
		Folder folder = Store.getFolder(folderId);
		try{
			folder.open();
			folder.addMessage(message);
			//now remove the old one, if in the same folder
			if(messageToSave.getFolderId() == folderId){
				if(messageToSave.getStoreReference() != null){
					folder.removeMessage(messageToSave);
				}else{
					//some new message! it will now have the saved reference. For example, saving a new message.
					messageToSave.setStoreReference(message.getStoreReference());
				}
			}
			return true;
		}catch(StoreException ex){
			App.getLogger().log("MainController.updateDraft caught Exception and will return false. " + ex, Logger.LEVEL_ERROR);
		}finally{
			folder.close();
		}
		return false;
	}

	public void handleEvent(final Event event) {
		try{
			final String EVENT_NAME = event.getName();
			if(UIEvents.EXIT.equals(EVENT_NAME)){
				App.exit();
			}
			else if(UIEvents.WRITE_MESSAGE.equals(EVENT_NAME)){
				showMessageView(new BanglaMessage(Store.DRAFT), true);
			}
			else if(UIEvents.OPEN_INBOX.equals(EVENT_NAME)){
				showFolderView(Store.INBOX);
			}
			else if(UIEvents.OPEN_DRAFTS.equals(EVENT_NAME)){
				showFolderView(Store.DRAFT);
			}
			else if(UIEvents.OPEN_SENT.equals(EVENT_NAME)){
				showFolderView(Store.SENT);
			}
			else if(UIEvents.OPEN_TEMPLATE.equals(EVENT_NAME)){
				Folder folder = Store.getFolder(Store.TEMPLATE);
				folder.open();
				if(folder.getMessageCount() == 0){
					//load template
					for(int i = 0; i < TEMPLATE_TEXTS.length;i++){
						BanglaMessage message = new BanglaMessage(Store.TEMPLATE);
						message.setText(TEMPLATE_TEXTS[i]);
						folder.addMessage(message);
					}
				}
				folder.close();
				showFolderView(Store.TEMPLATE);
			}
			else if(UIEvents.CLOSE_FOLDER.equals(EVENT_NAME)){
				closeFolderView();
				showMainView();
			}
			else if(UIEvents.OPEN_MESSAGE.equals(EVENT_NAME)){
				final BanglaMessage message = getMessage(event);
				showMessageView(message, false);
				//update the read status in background
				if(!message.isRead()){
					message.setRead(true);
					new Thread(){
						public void run(){
							final Folder folder = Store.getFolder(message.getFolderId());
							try {
								folder.open();//TODO synchronization problem? If folder is closed after reading all the messages?
								folder.updateMessage(message);
							} catch (StoreException ex) {
								App.getLogger().log("MainController.OPEN_MESSAGE silently caught exception while updating message read status : " + ex, Logger.LEVEL_DEBUG);
							}finally{
								folder.close();
							}

						}
					}.start();
				}
			}
			else if(UIEvents.BACK_FROM_MESSAGE.equals(EVENT_NAME)){
				if(this.lastFolderView  != null){
					showFolderView(lastFolderView.getId());
				}else{
					showMainView();//for example, back from write message
				}
			}else if(UIEvents.DELETE_MESSAGE.equals(EVENT_NAME)){
				MessageBox confirmDelete = App.getDisplayManager().getMessageBox();
				confirmDelete.setText("Delete message?");
				confirmDelete.setOptionStyle(MessageBox.OPTION_OK_CANCEL);
				confirmDelete.addEventHandler(new EventHandler(){
					public void handleEvent(Event confirmationEvent) {
						if(MessageBox.EVENT_MESSAGE_OK.equals(confirmationEvent.getName())){
							BanglaMessage message = getMessage(event);
							boolean deleted = deleteMessage(message);
							if(deleted){
								//refresh the folder view
								if(event.getSource() instanceof FolderView){
									FolderView view = (FolderView)event.getSource();
									view.remove(message);
								}else if(event.getSource() instanceof BanglaMessageView){
									int folderId = getMessage(event).getFolderId();
									showFolderView(folderId);
								}
								App.getDisplayManager().showMessage("Message deleted");
							}
						}else{
							App.getDisplayManager().setOnScreen((View)event.getSource());
						}
					}

				});
				App.getDisplayManager().setOnScreen(confirmDelete);

			}else if(UIEvents.FORWARD_MESSAGE.equals(EVENT_NAME)){
				BanglaMessage toForwardMessage = getMessage(event);
				BanglaMessage message = new BanglaMessage(Store.DRAFT);
				message.setText(toForwardMessage.getText());
				showMessageView(message, true);
			}else if(UIEvents.EDIT_MESSAGE.equals(EVENT_NAME)){
				BanglaMessage toEdit = getMessage(event);
				showMessageView(toEdit, true);
			}else if(UIEvents.REPLY_MESSAGE.equals(EVENT_NAME)){
				BanglaMessage toReply = getMessage(event);
				BanglaMessage message = new BanglaMessage(Store.DRAFT);
				message.setContactName(toReply.getContactName());
				message.setContactNumber(toReply.getContactNumber());
				showMessageView(message, true);
			}else if(UIEvents.SAVE_MESSAGE_AS_DRAFT.equals(EVENT_NAME)){
				BanglaMessage message = getMessage(event);
				if(event.getSource() instanceof BanglaMessageView){
					message.setText(((BanglaMessageView)event.getSource()).getText());
				}
				if(saveMessage(message, Store.DRAFT)){
					App.getDisplayManager().showMessage("Message saved in Draft");
				}else{
					App.getDisplayManager().showMessage("Oops! Failed to save in Draft");
				}
			}else if(UIEvents.SAVE_MESSAGE_AS_TEMPLATE.equals(EVENT_NAME)){
				BanglaMessage message = getMessage(event);
				if(event.getSource() instanceof BanglaMessageView){
					message.setText(((BanglaMessageView)event.getSource()).getText());
				}
				if(saveMessage(message, Store.TEMPLATE)){
					App.getDisplayManager().showMessage("Message saved in Template");
				}else{
					App.getDisplayManager().showMessage("Oops! Failed to save in Template");
				}
			} else if(UIEvents.UPDATE.equals(EVENT_NAME)) {
				BusyView busyView = App.getDisplayManager().getBusyView();
				busyView.setText("Checking for update...");
				busyView.setIndefinite(true);
				busyView.setCancel(false);
				App.getDisplayManager().setOnScreen(busyView);
				new Thread () {
					public void run() {
						checkUpdate();
					}
				}.start();

			} else if (UIEvents.OPEN_HELP.equals(EVENT_NAME)) {
				BanglaTextView helpView = App.getDisplayManager().getTextView();
				helpView.setText(HELP_TEXT);
				App.getDisplayManager().setOnScreen(helpView);
				helpView.addEventHandler(new EventHandler() {
						public void handleEvent(Event helpEvent) {
							//must be back
							showMainView();
						}
					}
				);

			} else if (UIEvents.OPEN_CREDIT.equals(EVENT_NAME)) {
				CreditView creditView = App.getDisplayManager().getCreditView();
				creditView.addEventHandler(new EventHandler() {
						public void handleEvent(Event helpEvent) {
							showMainView();
						}
					}
				);
				creditView.init();
				App.getDisplayManager().setOnScreen(creditView);
			}else if(UIEvents.SEND_MESSAGE.equals(EVENT_NAME)){
				final BanglaMessage message = getMessage(event);
				if(message.getContactNumber() == null){
					throw new IllegalStateException("Programming error. Did someone forget to set contact address before sending?");
				}
				BusyView busyView = App.getDisplayManager().getBusyView();
				busyView.setCancel(false);
				busyView.setText("Sending...");
				App.getDisplayManager().setOnScreen(busyView);
				new Thread(){
					public void run(){
						Thread.yield();//to allow the screen to be updated first
						SMSSender sender = new SMSSender(App.getSMSPort());
						sender.addEventHandler(new EventHandler(){
							public void handleEvent(Event smsEvent) {
								String smsSendResult = smsEvent.getName();
								MessageBox messageBox = App.getDisplayManager().getMessageBox();
								messageBox.addEventHandler(new EventHandler(){
									public void handleEvent(Event event) {
										//after messagebox is dissmissed go to main view
										showMainView();
									}

								});
								if(SMSSender.EVENT_SMS_SEND_SUCCESS.equals(smsSendResult)){
									BanglaMessage sent = new BanglaMessage(Store.SENT);
									sent.setText(message.getText());
									sent.setContactName(message.getContactName());
									sent.setContactNumber(message.getContactNumber());
									sent.setContactName(message.getContactName());
									Folder folder = Store.getFolder(Store.SENT);
									try{
										folder.open();
										folder.addMessage(sent);
										messageBox.setText("Sent!");
									}catch(StoreException ex){
										App.getLogger().log("MainController showing erorr message while saving message to sent folder aftar catching : " + ex, Logger.LEVEL_ERROR);
										messageBox.setOptionStyle(MessageBox.OPTION_OK);
										messageBox.setText("Oops! Failed to save message in Sent folder. Technical details: " + ex);
									}
									finally{
										folder.close();
									}
									//now remove the draft
									if(message.getFolderId() == Store.DRAFT && message.getStoreReference() != null){
										folder = Store.getFolder(Store.DRAFT);
										deleteMessage(message);
									}
								}else{
									messageBox.setOptionStyle(MessageBox.OPTION_OK);
									messageBox.setText("Oops! Sending failed. Message saved in Draft");
									if(!saveMessage(message, Store.DRAFT)){
										App.getDisplayManager().showMessage("Big oops! Message sending failed and message could not be saved in Drafts as well", true);
									}
								}
								App.getDisplayManager().setOnScreen(messageBox);

							}

						});
						if(message.getContactName() == null) {
								message.setContactName(ContactUtil.getContactName(message.getContactNumber()));
						}
						sender.send(message);
					}
				}.start();
			}else{
				App.getDisplayManager().showMessage("Not implemented yet.. working on it");
			}

		}catch(Throwable ex){
			System.err.println("MainController.handleEvent: " + ex);
			App.getDisplayManager().showMessage("Oops! Unexpected error ! Details : " + ex);
		}
	}

	private void checkUpdate() {
		try{
			StringBuffer updateUrl = new StringBuffer();
			updateUrl.append(UPDATE_SERVER);
			updateUrl.append("Update-id=");
			updateUrl.append(App.CURRENT_UPDATE_ID);
			updateUrl.append("&microedition.platform=");
			String platform = System.getProperty("microedition.platform");
			if (platform == null ) {
				platform = "NULL";
			}
			updateUrl.append(platform);
			String serverResponse = HTTPTool.fetchContent(updateUrl.toString());
			final String SEPARATOR = "$^@%$%%#$$";
			final String NO_UPDATE = "NO_UPDATE";

			if ( ! serverResponse.startsWith(SEPARATOR)) {
				App.getDisplayManager().showMessage("Update server returned invalid response. Try again later. Details : First separator not found." , true);
				return;
			}

			if ( serverResponse.startsWith(SEPARATOR + NO_UPDATE + SEPARATOR)) {
				String message = serverResponse.substring(SEPARATOR.length() + NO_UPDATE.length() + SEPARATOR.length());
				App.getDisplayManager().showMessage(message, true);
				return;
			}

			String urlHolder = serverResponse.substring(SEPARATOR.length());
			int nextSeparatorIndex = urlHolder.indexOf(SEPARATOR);
			if (nextSeparatorIndex < 0) {
				App.getDisplayManager().showMessage("Update server returned invalid response. Try again later. Details : Second separator not found !", true);
				return;
			}
			final String DOWNLOAD_URL = urlHolder.substring(0, nextSeparatorIndex);
			String message = serverResponse.substring(SEPARATOR.length() + DOWNLOAD_URL.length() + SEPARATOR.length());
			App.getLogger().log("MainController.commandAction.UPDATE : DOWNLOAD_URL = " + DOWNLOAD_URL, Logger.LEVEL_INFO);
			MessageBox messageBox = App.getDisplayManager().getMessageBox();
			messageBox.setText(message);
			messageBox.setOptionStyle(MessageBox.OPTION_OK_CANCEL);
			messageBox.addEventHandler(new EventHandler () {
				public void handleEvent(Event event) {
					if (MessageBox.EVENT_MESSAGE_OK.equals(event.getName())) {
						try {
							App.platformRequest(DOWNLOAD_URL);
							App.exit();
						} catch (Exception ex) {
							App.getDisplayManager().showMessage("Opps! Failed to download new software. Check internet connection and try again. Details : " + ex, true);
						}
					} else {
						showMainView();
					}
				}
			}
			);
			App.getDisplayManager().setOnScreen(messageBox);

		} catch (SecurityException ex) {
			App.getDisplayManager().showMessage("Update cannot be done unless you allow the applicaiton to connect to internet. Restart application and try updating again.", true);
		}
		catch (Exception ex) {
			App.getDisplayManager().showMessage("Oops ! Failed to get update information from server. Check your internet connection settings and try again later. Details: " + ex, true);
		}

	}

	private boolean deleteMessage(BanglaMessage message){
		int folderId = message.getFolderId();
		Folder folder = Store.getFolder(folderId);
		try{
			folder.open();
			folder.removeMessage(message);
			return true;
		}catch(StoreException ex){
			App.getDisplayManager().showMessage("Ops! Failed to delete.\n Details: " + ex + ", folderId = " + folderId + ", message ref = " + message.getStoreReference(), true);
		}finally{
			folder.close();
		}
		return false;
	}

	private BanglaMessage getMessage(Event event){
		BanglaMessage message = (BanglaMessage)event.getValue(UIEvents.PARAM_MESSAGE);
		if(message == null){
			throw new IllegalStateException("Most likely a programmer error. Did someone forget to pass the message as event parameter? It cannt be null !!");
		}
		return message;
	}

	public void smsReceived(Message message) {
		if(message instanceof TextMessage){
			TextMessage textMessage = (TextMessage)message;
			BanglaMessage banglaSMS = new BanglaMessage(Store.INBOX);
			banglaSMS.setText(textMessage.getPayloadText());
			String number = formatContactNumber(textMessage.getAddress());
			banglaSMS.setContactNumber(number);
			banglaSMS.setRead(false);
			if(textMessage.getTimestamp() != null){
				banglaSMS.setTime(textMessage.getTimestamp().getTime());
			}
			playSMSReceptionTone();

			banglaSMS.setContactName(ContactUtil.getContactName(number));

			Folder folder = Store.getFolder(Store.INBOX);
			try {
				folder.open();
				folder.addMessage(banglaSMS);
			} catch (StoreException e) {
				App.getDisplayManager().showMessage("Oops! Failed to save this message into Inbox!!", true);
			}finally{
				folder.close();
			}

			App.getDisplayManager().showMessage("You have got new BanglaSMS!", true);
		}else{
			App.getDisplayManager().showMessage("Oops! Message received in an Unknown format. It's not a 'TextMessage'", true);
		}
	}

	public void smsReceptionError(Exception ex) {
		App.getDisplayManager().showMessage("Oops! Message reception failed. Technical details: " + ex, true);
	}

	private String formatContactNumber(String textMessageAddress){
		String formattedNumber;
		String smsPrefix = "sms://";
		if(textMessageAddress.startsWith(smsPrefix)){
			formattedNumber = textMessageAddress.substring(smsPrefix.length());
		}else{
			formattedNumber = textMessageAddress;
		}

		int colonIndex = formattedNumber.indexOf(':');
		if(colonIndex >= 0){
			formattedNumber = formattedNumber.substring(0, colonIndex);
		}
		return formattedNumber;
	}

	public void playSMSReceptionTone() {
		if(smsReceptinoToneFile != null){
			new Thread() {
				public void run() {
					InputStream is = getClass().getResourceAsStream(smsReceptinoToneFile);
					Player player;
					try {
						player = Manager.createPlayer(is, smsReceptionToneType);
						player.realize();
						player.start();
					} catch (IOException ex) {
						App.getLogger().log("MainController.playSMSReceptionTone caught " + ex, Logger.LEVEL_ERROR);
					} catch (MediaException ex) {
						App.getLogger().log("MainController.playSMSReceptionTone caught " + ex, Logger.LEVEL_ERROR);
					}
				}
			}.start();
		}
	}

	void sortMessages (BanglaMessage [] list) {
		//insertion sort
		int firstOutOfOrder, location;
		BanglaMessage temp;

	    for(firstOutOfOrder = 1; firstOutOfOrder < list.length; firstOutOfOrder++) { //Starts at second term, goes until the end of the array.
	        if(list[firstOutOfOrder].getTime() > list[firstOutOfOrder - 1].getTime()) { //If the two are out of order, we move the element to its rightful place.
	            temp = list[firstOutOfOrder];
	            location = firstOutOfOrder;

	            do { //Keep moving down the array until we find exactly where it's supposed to go.
	                list[location] = list[location-1];
	                location--;
	            }
	            while (location > 0 && list[location-1].getTime() < temp.getTime());

	            list[location] = temp;
	        }
	    }
	}

	class FolderUpdater implements FolderListener{

		private FolderView folderView;

		public FolderUpdater(FolderView folderView){
			this.folderView = folderView;
		}

		public void messageAdded(BanglaMessage message) {
			folderView.addMessageAtBeginning(message);
		}

		public void messageRemoved(BanglaMessage message) {
			folderView.remove(message);
		}
	}
}
