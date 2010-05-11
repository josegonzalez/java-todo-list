package todo;

import javax.microedition.io.*;
import java.io.*;

/**
 *
 * @author jose
 */
public class HttpRequest {

        /*
 * Sends a Post Request
 *
 * @link http://wiki.forum.nokia.com/index.php/How_to_use_Http_POST_request_in_Java_ME
 */
    public String post(String url, String message) {
	HttpConnection hc = null;
	InputStream in = null;
	OutputStream out = null;

        String responseMessage = "";
	// specifying the query string
	try {
            hc = (HttpConnection)Connector.open(url);
            hc.setRequestMethod(HttpConnection.POST);
            hc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            hc.setRequestProperty("Content-Length", Integer.toString(message.length()));
            out = hc.openOutputStream();
            out.write(message.getBytes());
            in = hc.openInputStream();
            int length = (int)hc.getLength();
            byte[] data = new byte[length];
            in.read(data);
            responseMessage = new String(data);
	} catch (IOException ioe) {
	} finally {
            // freeing up i/o streams and http connection
            try {
		if (hc != null) hc.close();
            } catch (IOException ignored) { }
            try {
		if (in != null) in.close();
            } catch (IOException ignored) { }
            try {
                if (out != null) out.close();
            } catch (IOException ignored) { }
	}
        return responseMessage;
    }
}
