package it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;

public class ServletUtilities {

    /**
     * Binary streams the specified file to the HTTP response in 1KB chunks.
     * 
     * @param file
     *            the file to be streamed.
     * @param response
     *            the HTTP response object.
     * 
     * @throws IOException
     *             if there is an I/O problem.
     */
    public static void sendTempFile(File file, HttpServletResponse response)
	    throws IOException {

	String mimeType = null;
	String filename = file.getName();
	int pointIndex = filename.lastIndexOf('.');
	if (pointIndex != -1) {
	    if (filename.toLowerCase().substring(pointIndex + 1).equals("jpeg")
		    || filename.toLowerCase().substring(pointIndex + 1).equals(
			    "jpg")) {
		mimeType = "image/jpeg";
	    } else if (filename.toLowerCase().substring(pointIndex + 1).equals(
		    "gif")) {
		mimeType = "image/gif";
	    } else if (filename.toLowerCase().substring(pointIndex + 1).equals(
		    "png")) {
		mimeType = "image/png";
	    }
	}
	ServletUtilities.sendTempFile(file, response, mimeType);
    }

    /**
     * Binary streams the specified file to the HTTP response in 1KB chunks.
     * 
     * @param file
     *            the file to be streamed.
     * @param response
     *            the HTTP response object.
     * @param mimeType
     *            the mime type of the file, null allowed.
     * 
     * @throws IOException
     *             if there is an I/O problem.
     */
    public static void sendTempFile(File file, HttpServletResponse response,
	    String mimeType) throws IOException {

	if (file.exists()) {
	    BufferedInputStream bis = new BufferedInputStream(
		    new FileInputStream(file));

	    // Set HTTP headers
	    if (mimeType != null) {
		response.setHeader("Content-Type", mimeType);
	    }
	    response.setHeader("Content-Length", String.valueOf(file.length()));
	    SimpleDateFormat sdf = new SimpleDateFormat(
		    "EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
	    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
	    response.setHeader("Last-Modified", sdf.format(new Date(file
		    .lastModified())));

	    BufferedOutputStream bos = new BufferedOutputStream(response
		    .getOutputStream());
	    byte[] input = new byte[1024];
	    boolean eof = false;
	    while (!eof) {
		int length = bis.read(input);
		if (length == -1) {
		    eof = true;
		} else {
		    bos.write(input, 0, length);
		}
	    }
	    bos.flush();
	    bis.close();
	    bos.close();
	} else {
	    throw new FileNotFoundException(file.getAbsolutePath());
	}
	return;
    }

}
