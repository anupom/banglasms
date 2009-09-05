package org.bd.banglasms.util;

import java.util.Enumeration;

import javax.microedition.pim.Contact;
import javax.microedition.pim.ContactList;
import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;

import org.bd.banglasms.App;
import org.bd.banglasms.Logger;

/**
 * Utility class to search for a contact name using PIM API.
 *
 */
public class ContactUtil {
	
	private static boolean isPIMSupportChecked = false;
	private static boolean isPIMSupported = false;
	
	/**
	 * Searches contact and returns the suitable name if found, else returns null
	 * @param number contact number
	 * @return contact name or null if not found, or PIM not supported
	 */
	public static String getContactName(String number) {
		if( !isPIMSupportChecked ){
			if (System.getProperty( "microedition.pim.version" ) != null) {
				isPIMSupported = true;
			}
			isPIMSupportChecked = true;
		}
		
		if( !isPIMSupported ) {
			return null;
		}
		
		try {
			String[] allLists = PIM.getInstance().listPIMLists(PIM.CONTACT_LIST);
			if (allLists.length > 0) {
				String results[] = new String[allLists.length];
				for (int i = 0; i < allLists.length; i++) {
					results[i] = findNameInList(number, allLists[i]);
				}
				// if there is more than one, only the first is returned
				for (int i = 0; i < allLists.length; i++) {
					if (results[i] != null) {
						return results[i];
					}
				}
			}
		}catch(SecurityException ex) {
			App.getLogger().log("ContactUtl.getContactName caught "  + ex, Logger.LEVEL_ERROR);
		}
		return null;
	}
	
	/**
	 *  
	 * @return contact name or null if not found
	 */
	private static String findNameInList(String number, String list) {
		ContactList contactList = null;

		try {
			contactList = (ContactList) PIM.getInstance().openPIMList(
					PIM.CONTACT_LIST, PIM.READ_ONLY, list);
			if (contactList.isSupportedField(Contact.TEL)
					&& contactList.isSupportedField(Contact.FORMATTED_NAME)) {
				Contact pattern = contactList.createContact();
				pattern.addString(Contact.TEL, PIMItem.ATTR_NONE, number);
				Enumeration matching = contactList.items(pattern);
				if (matching.hasMoreElements()) {
					// will only return the first match
					Contact ci = (Contact) matching.nextElement();
					// FORMATTED_NAME is mandatory
					return ci.getString(Contact.FORMATTED_NAME, 0);
				}
			}
		} catch (PIMException e) {
			App.getLogger().log("ContactUtl.findNameInList caught "  + e, Logger.LEVEL_ERROR);
		} catch (SecurityException e) {
			App.getLogger().log("ContactUtl.findNameInList caught "  + e, Logger.LEVEL_ERROR);
		} finally {
			if (contactList != null) {
				try {
					contactList.close();
				} catch (PIMException e) {
					// ignore, nothing can be done here
				}
			}
		}
		return null;
	}
}
