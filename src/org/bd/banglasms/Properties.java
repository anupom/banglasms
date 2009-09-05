package org.bd.banglasms;

/**
 * Properties can be use to store and retrieve key-value pairs.
 *
 */
public interface Properties {

	/**
	 * Returns the value associated with the key or null if not found
	 * @param key key for which value to be returned
	 * @return value for the key or null if not found
	 * @throws IllegalArgumentException if key is null
	 */
	public String getProperty (String key);

	/**
	 * Sets the property key-value pair
	 * @param key key of the property, must not be null
	 * @param value value of the property, must not be null
	 * @throws IllegalArgumentException if any of the parameter is null
	 */
	public void setProperty (String key, String value);
}
