package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.fileupload;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.dispatcher.multipart.MultiPartRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.LocalizedTextUtil;

import it.maggioli.eldasoft.plugins.ppcommon.aps.XSSValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.ClassFieldValidator;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.ParamValidationResult;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;

/**
 * Copia della classe "JakartaStreamMultiPartRequest" 
 * di Chris Cranford per Struts 2.3.18
 *   
 */
public class JakartaStreamMultiPartRequest implements MultiPartRequest {

	static final Logger LOG = LoggerFactory.getLogger(JakartaStreamMultiPartRequest.class);

	/**
	 * Defines the internal buffer size used during streaming operations.
	 */
	private static final int BUFFER_SIZE = 10240;

	/**
	 * Map between file fields and file data.
	 */
	private Map<String, List<FileInfo>> fileInfos = new HashMap<String, List<FileInfo>>();

	/**
	 * Map between non-file fields and values.
	 */
	private Map<String, List<String>> parameters = new HashMap<String, List<String>>();

	/**
	 * Internal list of raised errors to be passed to the the Struts2 framework.
	 */
	private List<String> errors = new ArrayList<String>();

	/**
	 * Internal list of non-critical messages to be passed to the Struts2 framework.
	 */
	private List<String> messages = new ArrayList<String>();

	/**
	 * Specifies the maximum size of the entire request.
	 */
	private Long maxSize;

	/**
	 * Specifies the buffer size to use during streaming.
	 */
	private int bufferSize = BUFFER_SIZE;

	/**
	 * Localization to be used regarding errors.
	 */
	private Locale defaultLocale = Locale.ENGLISH;
	
	/**
	 * ...
	 */
	private FileUploadListener progress = null;

	/**
	 * @param maxSize Injects the Struts multiple part maximum size.
	 */
	@Inject(StrutsConstants.STRUTS_MULTIPART_MAXSIZE)
	public void setMaxSize(String maxSize) {
		this.maxSize = Long.parseLong(maxSize);
	}

	/**
	 * @param bufferSize Sets the buffer size to be used.
	 */
//	@Inject(value = StrutsConstants.STRUTS_MULTIPART_BUFFERSIZE, required = false)  ???
	public void setBufferSize(String bufferSize) {
		this.bufferSize = Integer.parseInt(bufferSize);
	}

	/**
	 * @param provider Injects the Struts locale provider.
	 */
//	@Inject ???
	public void setLocaleProvider(LocaleProvider provider) {
		defaultLocale = provider.getLocale();
	}

	/* (non-Javadoc)
	 * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#cleanUp()
	 */
	public void cleanUp() {
		LOG.debug("Performing File Upload temporary storage cleanup.");
		for (String fieldName : fileInfos.keySet()) {
			for (FileInfo fileInfo : fileInfos.get(fieldName)) {
				File file = fileInfo.getFile();
				LOG.debug("Deleting file '{}'.", file.getName());
				if (!file.delete()) {
					LOG.warn("There was a problem attempting to delete file '{}'.", file.getName());
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getContentType(java.lang.String)
	 */
	public String[] getContentType(String fieldName) {
		List<FileInfo> infos = fileInfos.get(fieldName);
		if (infos == null) {
			return null;
		}

		List<String> types = new ArrayList<String>(infos.size());
		for (FileInfo fileInfo : infos) {
			types.add(fileInfo.getContentType());
		}

		return types.toArray(new String[types.size()]);
	}

	/* (non-Javadoc)
	 * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getErrors()
	 */
	public List<String> getErrors() {
		return errors;
	}

	/**
	 * Allows interceptor to fetch non-critical messages that can be passed to the action.
	 *
	 * @return list of string messages
	 */
	public List<String> getMessages() {
		return messages;
	}

	/* (non-Javadoc)
	 * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getFile(java.lang.String)
	 */
	public File[] getFile(String fieldName) {
		List<FileInfo> infos = fileInfos.get(fieldName);
		if (infos == null) {
			return null;
		}

		List<File> files = new ArrayList<File>(infos.size());
		for (FileInfo fileInfo : infos) {
			files.add(fileInfo.getFile());
		}

		return files.toArray(new File[files.size()]);
	}

	/* (non-Javadoc)
	 * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getFileNames(java.lang.String)
	 */
	public String[] getFileNames(String fieldName) {
		List<FileInfo> infos = fileInfos.get(fieldName);
		if (infos == null) {
			return null;
		}

		List<String> names = new ArrayList<String>(infos.size());
		for (FileInfo fileInfo : infos) {
			names.add(getCanonicalName(fileInfo.getOriginalName()));
		}

		return names.toArray(new String[names.size()]);
	}

	/* (non-Javadoc)
	 * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getFileParameterNames()
	 */
	public Enumeration<String> getFileParameterNames() {
		return Collections.enumeration(fileInfos.keySet());
	}

	/* (non-Javadoc)
	 * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getFilesystemName(java.lang.String)
	 */
	public String[] getFilesystemName(String fieldName) {
		List<FileInfo> infos = fileInfos.get(fieldName);
		if (infos == null) {
			return null;
		}

		List<String> names = new ArrayList<String>(infos.size());
		for (FileInfo fileInfo : infos) {
			names.add(fileInfo.getFile().getName());
		}

		return names.toArray(new String[names.size()]);
	}

	/* (non-Javadoc)
	 * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getParameter(java.lang.String)
	 */
	public String getParameter(String name) {
		List<String> values = parameters.get(name);
		if (values != null && values.size() > 0) {
			return values.get(0);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getParameterNames()
	 */
	public Enumeration<String> getParameterNames() {
		return Collections.enumeration(parameters.keySet());
	}

	/* (non-Javadoc)
	 * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#getParameterValues(java.lang.String)
	 */
	public String[] getParameterValues(String name) {
		List<String> values = parameters.get(name);
		if (values != null && values.size() > 0) {
			return values.toArray(new String[values.size()]);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.apache.struts2.dispatcher.multipart.MultiPartRequest#parse(javax.servlet.http.HttpServletRequest, java.lang.String)
	 */
	public void parse(HttpServletRequest request, String saveDir) throws IOException {
		try {
			setLocale(request);
			processUpload(request, saveDir);
		} catch (Exception e) {
			LOG.warn("Error occurred during parsing of multi part request", e);
			String errorMessage = buildErrorMessage(e, new Object[]{});
			if (!errors.contains(errorMessage)) {
				errors.add(errorMessage);
			}
		}
	}

	/**
	 * @param request Inspect the servlet request and set the locale if one wasn't provided by
	 * the Struts2 framework.
	 */
	protected void setLocale(HttpServletRequest request) {
		if (defaultLocale == null) {
			defaultLocale = request.getLocale();
		}
	}

	/**
	 * Processes the upload.
	 *
	 * @param request the servlet request
	 * @param saveDir location of the save dir
	 * @throws Exception
	 */
	private void processUpload(HttpServletRequest request, String saveDir) throws Exception {
		// Sanity check that the request is a multi-part/form-data request.
		if (ServletFileUpload.isMultipartContent(request)) {
			// XSS - recupera gli attributi della action che andra' eseguita a breve...
			// (vedi Struts2ServletDispatcher.prepareDispatcherAndWrapRequest(...))
			Object action = (ActionContext.getContext() != null && ActionContext.getContext().getActionInvocation() != null  
							 ? ActionContext.getContext().getActionInvocation().getAction()
							 : null);
			List<Field> actionFields = (action != null 
							 ? ClassFieldValidator.getAllFieldsIncludingParent(action.getClass())
							 : null);
			
			// Sanity check on request size.
			boolean requestSizePermitted = isRequestSizePermitted(request);

			// Interface with Commons FileUpload API
			// Using the Streaming API
			ServletFileUpload servletFileUpload = new ServletFileUpload();
			
			// add custom progress listener...
			// add progress bar...
			this.progress = new FileUploadListener(request.getSession());
			servletFileUpload.setProgressListener(this.progress);
			
			FileItemIterator i = null;
			try {
				i = servletFileUpload.getItemIterator(request);
			} catch (Throwable t) {
				LOG.error("Error occurred during process upload", t);
				System.out.println("Error occurred during process upload: " + t.getMessage());
			}
			
			// Iterate the file items
			if(i != null) {
				while (i.hasNext()) {
					try {
						FileItemStream itemStream = i.next();
	
						// If the file item stream is a form field, delegate to the
						// field item stream handler
						if (itemStream.isFormField()) {
							processFileItemStreamAsFormField(itemStream, request, actionFields);
						}
	
						// Delegate the file item stream for a file field to the
						// file item stream handler, but delegation is skipped
						// if the requestSizePermitted check failed based on the
						// complete content-size of the request
						else {
							// prevent processing file field item if request size not allowed.
							// also warn user in the logs
							if (!requestSizePermitted) {
								addFileSkippedError(itemStream.getName(), request);
								LOG.warn("Skipped stream '" + itemStream.getName() + "', request maximum size (" + maxSize + ") exceeded.");
								continue;
							}
	
							processFileItemStreamAsFileField(itemStream, saveDir);
						}
					} catch (IOException e) {
						LOG.warn("Error occurred during process upload", e);
					}
				}
			}
			
			this.progress = null;
		}
	}

	/**
	 * Defines whether the request allowed based on content length.
	 *
	 * @param request the servlet request
	 * @return true if request size is permitted
	 */
	private boolean isRequestSizePermitted(HttpServletRequest request) {
		// if maxSize is specified as -1, there is no sanity check and it's
		// safe to return true for any request, delegating the failure
		// checks later in the upload process
		if (maxSize == -1 || request == null) {
			return true;
		}

		return request.getContentLength() < maxSize;
	}

	/**
	 * @param request the servlet request
	 * @return the request content length.
	 */
	private long getRequestSize(HttpServletRequest request) {
		long requestSize = 0;
		if (request != null) {
			requestSize = request.getContentLength();
		}

		return requestSize;
	}

	/**
	 * Add a file skipped message notification for action messages.
	 *
	 * @param fileName file name
	 * @param request the servlet request
	 */
	private void addFileSkippedError(String fileName, HttpServletRequest request) {
		String exceptionMessage = "Skipped file " + fileName + "; request size limit exceeded.";
		FileSizeLimitExceededException exception = new FileUploadBase.FileSizeLimitExceededException(exceptionMessage, getRequestSize(request), maxSize);
		String message = buildErrorMessage(exception, new Object[]{fileName, getRequestSize(request), maxSize});
		if (!errors.contains(message)) {
			errors.add(message);
		}
	}

	/**
	 * Processes the FileItemStream as a Form Field.
	 *
	 * @param itemStream file item stream
	 */
	private void processFileItemStreamAsFormField(FileItemStream itemStream, HttpServletRequest request, List<Field> actionFields) {
		String fieldName = itemStream.getFieldName();
		try {
			String fieldValue = Streams.asString(itemStream.openStream());
			fieldValue = XSSValidateFieldValue(fieldName, fieldValue, actionFields);

			List<String> values;
			if (!parameters.containsKey(fieldName)) {
				values = new ArrayList<String>();
				parameters.put(fieldName, values);
			} else {
				values = parameters.get(fieldName);
			}
			values.add(fieldValue);
		} catch (IOException e) {
			LOG.warn("Failed to handle form field '" + fieldName + "'.", e);
		}
	}

	/**
	 * XSS - verifica e filtra il valore associato al parametro (PORTAPPALT-1170)
	 */
	private String XSSValidateFieldValue(String fieldName, String fieldValue, List<Field> actionFields) {
//		LOG.debug("START - validating field {}", fieldName);
		boolean invalid = false;
		
		if(XSSValidation.hasToBeChecked(fieldName, fieldValue)) {
			// gestisci i parametri NON definiti in una action
			invalid = (XSSValidation.isNotValid(fieldName, fieldValue));
		} else if(actionFields != null) {
			// gestisci i parametri definiti da una action
			// 1) recupera l'annotazione del "field" nella action
			Validate annotation = actionFields.stream()
				.filter(f -> fieldName.equalsIgnoreCase(f.getName()) && f.getAnnotation(Validate.class) != null)
				.map(f -> f.getAnnotation(Validate.class))
				.findFirst()
				.orElse(null);
			
			// 2) applica il validatore dell'annotazione al valore del "field"
			if(annotation != null) {
		        // in questo contesto il valore da verificare e' sempre di tipo String (vedi "parameters")!!!
				EParamValidation validator = annotation.value();
				ParamValidationResult res = validator.validate(fieldValue);
		        String invalidPart = (res != null ? res.getInvalidPart() : null);
		        invalid = (StringUtils.isNotEmpty(invalidPart));
			}
		}
		
		if(invalid) {
			LOG.error("The value: {}; for the field {} is not valid", fieldValue, fieldName);
			fieldValue = "";
		}
		
//		LOG.debug("END - validating field {}", fieldName);
		return fieldValue; 
	}	

	/**
	 * Processes the FileItemStream as a file field.
	 *
	 * @param itemStream file item stream
	 * @param location location
	 */
	private void processFileItemStreamAsFileField(FileItemStream itemStream, String location) {
		// Skip file uploads that don't have a file name - meaning that no file was selected
		if (itemStream.getName() == null || itemStream.getName().trim().length() < 1) {
			LOG.debug("No file has been uploaded for the field: {}", itemStream.getFieldName());
			return;
		}

		File file = null;
		try {
			// Create the temporary upload file.
			file = createTemporaryFile(itemStream.getName(), location);

			if(this.progress != null) {
				this.progress.startUpload();
			}
			
			if (streamFileToDisk(itemStream, file)) {
				createFileInfoFromItemStream(itemStream, file);
			}
			
			if(this.progress != null) {
				this.progress.endUpload();
			}
		} catch (IOException e) {
			if (file != null) {
				try {
					file.delete();
				} catch (SecurityException se) {
					LOG.warn("Failed to delete '" + file.getName() + "' due to security exception above.", se);
				}
			}
		}
	}

	/**
	 * Creates a temporary file based on the given filename and location.
	 *
	 * @param fileName file name
	 * @param location location
	 * @return temporary file based on the given filename and location
	 * @throws IOException in case of IO errors
	 */
	private File createTemporaryFile(String fileName, String location) throws IOException {
		String name = fileName
			.substring(fileName.lastIndexOf('/') + 1)
			.substring(fileName.lastIndexOf('\\') + 1);

		String prefix = name;
		String suffix = "";

		if (name.contains(".")) {
			prefix = name.substring(0, name.lastIndexOf('.'));
			suffix = name.substring(name.lastIndexOf('.'));
		}

		if (prefix.length() < 3) {
			prefix = UUID.randomUUID().toString();
		}
		
		File file = File.createTempFile(prefix + "_", suffix, new File(location));
		LOG.debug("Creating temporary file '{}' (originally '{}').", file.getName(), fileName);
		return file;
	}

	/**
	 * Streams the file upload stream to the specified file.
	 *
	 * @param itemStream file item stream
	 * @param file the file
	 * @return true if stream was successfully
	 * @throws IOException in case of IO errors
	 */
	private boolean streamFileToDisk(FileItemStream itemStream, File file) throws IOException {
		boolean result = false;
		InputStream input = null;
		OutputStream output = null;
		try {
			input = itemStream.openStream();
			output = new BufferedOutputStream(new FileOutputStream(file), bufferSize); 
			byte[] buffer = new byte[bufferSize];
			LOG.debug("Streaming file using buffer size " + bufferSize + ".");
			for (int length = 0; ((length = input.read(buffer)) > 0); ) {
				output.write(buffer, 0, length);
			}
			output.close();
			input.close();
			result = true;
		} catch (Exception ex) {
			//...
		} finally {
			if(output != null) 
				try{ output.close(); } finally {}
			if(input != null) 
				try{ input.close(); } finally {}
		}
		return result;
	}

	/**
	 * Creates an internal <code>FileInfo</code> structure used to pass information
	 * to the <code>FileUploadInterceptor</code> during the interceptor stack
	 * invocation process.
	 *
	 * @param itemStream file item stream
	 * @param file the file
	 */
	private void createFileInfoFromItemStream(FileItemStream itemStream, File file) {
		// gather attributes from file upload stream
		String fileName = itemStream.getName();
		String fieldName = itemStream.getFieldName();
		// create internal structure
		FileInfo fileInfo = new FileInfo(file, itemStream.getContentType(), fileName);
		// append or create new entry
		if (!fileInfos.containsKey(fieldName)) {
			List<FileInfo> infos = new ArrayList<FileInfo>();
			infos.add(fileInfo);
			fileInfos.put(fieldName, infos);
		} else {
			fileInfos.get(fieldName).add(fileInfo);
		}
	}

	/**
	 * @param fileName file name
	 * @return the canonical name based on the supplied filename
	 */
	private String getCanonicalName(String fileName) {
		int forwardSlash = fileName.lastIndexOf("/");
		int backwardSlash = fileName.lastIndexOf("\\");
		if (forwardSlash != -1 && forwardSlash > backwardSlash) {
			fileName = fileName.substring(forwardSlash + 1, fileName.length());
		} else {
			fileName = fileName.substring(backwardSlash + 1, fileName.length());
		}
		return fileName;
	}

	/**
	 * Build error message.
	 *
	 * @param e the Throwable/Exception
	 * @param args arguments
	 * @return error message
	 */
	private String buildErrorMessage(Throwable e, Object[] args) {
		String msg = "";
		String errorKey = "struts.message.upload.error." + e.getClass().getSimpleName();
		try {
			LOG.debug("Preparing error message for key: [{}]", errorKey);
			msg = LocalizedTextUtil.findText(this.getClass(), errorKey, defaultLocale, e.getMessage(), args);	
		} catch (Throwable t) {
			msg = "errorKey=" + errorKey + " => " + e.getMessage();
		}
		return msg;
	}

	/**
	 * Build action message.
	 *
	 * @param e the Throwable/Exception
	 * @param args arguments
	 * @return action message
	 */
	private String buildMessage(Throwable e, Object[] args) {
		String msg = "";
		String messageKey = "struts.message.upload.message." + e.getClass().getSimpleName();
		try {
			LOG.debug("Preparing message for key: [{}]", messageKey);
			msg = LocalizedTextUtil.findText(this.getClass(), messageKey, defaultLocale, e.getMessage(), args);
		} catch (Throwable t) {
			msg = "messageKey=" + messageKey + " => " + e.getMessage();
		}
		return msg;
	}

	/**
	 * Internal data structure used to store a reference to information needed
	 * to later pass post processing data to the <code>FileUploadInterceptor</code>.
	 *
	 * @since 7.0.0
	 */
	private static class FileInfo implements Serializable {

		private static final long serialVersionUID = 1083158552766906037L;

		private File file;
		private String contentType;
		private String originalName;

		/**
		 * Default constructor.
		 *
		 * @param file the file
		 * @param contentType content type
		 * @param originalName original file name
		 */
		public FileInfo(File file, String contentType, String originalName) {
			this.file = file;
			this.contentType = contentType;
			this.originalName = originalName;
		}

		/**
		 * @return the file
		 */
		public File getFile() {
			return file;
		}

		/**
		 * @return content type
		 */
		public String getContentType() {
			return contentType;
		}

		/**
		 * @return original file name
		 */
		public String getOriginalName() {
			return originalName;
		}
	}

}
