package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.fileupload;

import java.io.Serializable;

import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.ProgressListener;


public class FileUploadListener implements ProgressListener, Serializable {
	
	private static final long serialVersionUID = -8980962197429639666L;

	public static final String SESSION_FILEUPLOAD_LISTENER = "FileUploadListener";
	
	public static final int STATO_NONE 				= 0;
	public static final int STATO_UPLOAD_START 		= 1;
	public static final int STATO_UPLOAD_STOP 		= 2;
	//public static final int STATO_UPLOAD_COMPLETED	= 3;
	//public static final int STATO_UPLOAD_ERROR 		= 4;
	
	private int stato					= STATO_NONE;
	private boolean completed 			= false;
	private int percentDone 			= 0;
	private long bytesRead 				= 0;	
	private long contentLength 			= -1;
	private boolean contentLengthKnown 	= false;
	private long count100K				= 0;
	
	
	private HttpSession session;
		
	public FileUploadListener(HttpSession session) {
		this.session = session;
		
		if (this.session != null) {
			this.session.removeAttribute(SESSION_FILEUPLOAD_LISTENER);
		}		
	}
	
	public FileUploadListener() {
		this(null);
	}
	
	/**
	 * ...
	 */
	@Override
	public void update(long bytesRead, long contentLength, int items) {
		boolean lastRefresh = (!this.completed && bytesRead >= contentLength);
		
		this.bytesRead = bytesRead;
		this.contentLength = contentLength;
		this.completed = (bytesRead >= contentLength);
		if (contentLength > -1) {
			this.contentLengthKnown = true;
		}
		
		long count = this.bytesRead / 100000;
		boolean refresh = (count > this.count100K || lastRefresh);
		if (refresh) {
			this.count100K = count;
			if (this.contentLengthKnown) {
				this.percentDone = (int) Math.round(100.00 * this.bytesRead / this.contentLength);
			}			
		}	
		
		// update status in session...
		if (this.session != null && refresh) {
			this.session.setAttribute(SESSION_FILEUPLOAD_LISTENER, this);
		}			
	}
	
	/**
	 * start upload session  
	 */
	public void startUpload() {
		this.stato = STATO_UPLOAD_START;
		this.completed = false;
		this.bytesRead = 0;
		this.contentLength = -1;
		if (this.session != null) {
			this.session.setAttribute(SESSION_FILEUPLOAD_LISTENER, this);
		}	
	}
	
	/**
	 * complete upload session 
	 */
	public void endUpload() {
		this.stato = STATO_UPLOAD_STOP;
		if (this.session != null) {						
			this.session.setAttribute(SESSION_FILEUPLOAD_LISTENER, this);
		}	
	}
	
	public String getMessage() {		
		if (this.contentLength == -1) {
			return "" + this.bytesRead + " of Unknown-Total bytes have been read.";
		} else {
			return "" + this.bytesRead + " of " + this.contentLength + " bytes have been read (" + this.percentDone + "% done).";
		}
	}
	
	public int getStato() {
		return stato;
	}

	public void setStato(int stato) {
		this.stato = stato;
	}

	public long getBytesRead() {
		return bytesRead;
	}

	public void setBytesRead(long bytesRead) {
		this.bytesRead = bytesRead;
	}

	public long getContentLength() {
		return contentLength;
	}

	public void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}

	public int getPercentDone() {
		return percentDone;
	}

	public void setPercentDone(int percentDone) {
		this.percentDone = percentDone;
	}

	public boolean isContentLengthKnown() {
		return contentLengthKnown;
	}

	public void setContentLengthKnown(boolean contentLengthKnown) {
		this.contentLengthKnown = contentLengthKnown;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

}
