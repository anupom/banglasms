package org.bd.banglasms.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;


/**
 * Utility class that contains http related functions.
 *
 */
public class HTTPTool {

	/**
	 * Connects to Internet and returns the server response.
	 * @param url url of the server
	 * @return content from server
	 * @throws IOException in case of I/O error
	 * @throws IllegalArgumentException if url is null
	 * @throws SecurityException in case of security error
	 */
	public static String fetchContent(String url) throws IOException {
		if (url == null ) {
			throw new IllegalArgumentException ("URL cannot be null");
		}
		HttpConnection httpConnection = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;

		try {
			httpConnection = (HttpConnection) Connector.open(url);
			httpConnection.setRequestMethod(HttpConnection.GET);

			int responseCode = httpConnection.getResponseCode();
			if (responseCode == HttpConnection.HTTP_OK) {
				StringBuffer buffer = new StringBuffer();
				outputStream = httpConnection.openOutputStream();
				inputStream = httpConnection.openDataInputStream();
				int chr;
				while ((chr = inputStream.read()) != -1)
					buffer.append((char) chr);
				return buffer.toString();
			}
			else {
				throw new IOException("HTML server did not responsd HTTP_OK, respone = " + responseCode);
			}

		} finally {
			if (inputStream != null)
				inputStream.close();
			if (outputStream != null)
				outputStream.close();
			if (httpConnection != null)
				httpConnection.close();
		}

	}

}
