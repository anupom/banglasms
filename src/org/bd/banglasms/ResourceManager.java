package org.bd.banglasms;

import java.io.IOException;

import javax.microedition.lcdui.Image;

/**
 * Interface providing access to application resource files.
 */
public interface ResourceManager {
	/**
	 * Returns the image using the url. For resources stored inside jar package,
	 * url starts with '/'
	 *
	 * @param url url of the image
	 * @return Image for the url
	 * @throws IOException in case of I/O error
	 */
	public Image fetchImage(String url) throws IOException;

	/**
	 * Returns the image for the given url or null in case of error.
	 * @param url url of the image
	 * @return Image for the url of null in case of error
	 */
	public Image fetchImageSafely(String url);
}
