package org.bd.banglasms.ui;

/**
 * Class that returns proper view implementations and handles other display related tasks.
 *
 */
public interface DisplayManager {
	public MainView getMainView(String title);
	public BanglaMessageView getEditorView();
	public FolderView getFolderView();
	public int getColor(int colorSpecifier);
	public BusyView getBusyView();
	public SplashScreen getSplashScreen();
	public ChoiceView getChoiceView(String title);
	public BanglaTextView getTextView();
	public CreditView getCreditView();
	public MessageBox getMessageBox();
	public void showMessage(String message);
	public void showMessage(String message, boolean waitForUserOk);
	public void setOnScreen(View view);
	public View getCurrent();
}
