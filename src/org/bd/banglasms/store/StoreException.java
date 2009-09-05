package org.bd.banglasms.store;

/**
 * Exception which represents an error in Store persistence error.
 */
public class StoreException extends Exception{
	public static final int REASON_OTHERS = 0;
	public static final int REASON_STORE_FULL = 1;
	public static final int REASON_INVALID_STORE_LOCATION = 2;
	public static final int REASON_STORE_NOT_FOUND = 3;
	public static final int REASON_STORE_NOT_OPEN = 4;
	public static final int REASON_INVALID_DATA_FORMAT = 5;


	private Exception origin;
	private int reason = REASON_OTHERS;


	public StoreException(){
		this(null, null);
	}

	public StoreException(String message){
		this(message, null);
	}

	public StoreException(int reason){
		this.reason = reason;
	}

	public StoreException(Exception origin){
		this.origin = origin;
	}

	public StoreException(String message, int reason){
		super(message);
		this.reason = reason;
	}

	public StoreException(int reason,Exception origin){
		this.reason = reason;
		this.origin = origin;
	}

	public StoreException(String message, Exception origin){
		super(message);
		this.origin = origin;
	}

	public String toString(){
		String string = super.toString();
		string += " Origin = " + this.origin + ", Reason = " + reason;
		return string;
	}

}
