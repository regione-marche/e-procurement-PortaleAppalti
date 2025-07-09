package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.fileupload;

import com.opensymphony.xwork2.Action;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import net.sf.json.JSONObject;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

public class FileUploadAsyncAction extends EncodedDataAction // <= extends ActionSupport
	implements SessionAware, ServletResponseAware {

	/**
	 * UID 
	 */
	private static final long serialVersionUID = -823570130061857657L;

	private static final String SESSION_EXPIRED = "Sessione scaduta, effettuare nuovamente l''accesso";
	
	private Map<String, Object> session;
	private HttpServletResponse response;
	private IEventManager eventManager;

	@Validate(EParamValidation.ACTION)
	private String actionUrl;				// url della pagina\action che utilizza l'upload
	private InputStream inputStream;		// stream per la risposta json
		
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
	
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}
	
	public String getActionUrl() {
		return actionUrl;
	}

	public void setActionUrl(String actionUrl) {
		this.actionUrl = actionUrl;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	/**
	 * ... 
	 */			  
	public String uploadRefreshAsync() {
		this.setTarget(SUCCESS);			
		try {
			this.inputStream = null;
			
			// recupera dalla sessione lo stato dell'upload...
			// e rendilo disponibile via JSON..			
			FileUploadListener uploadStatus = (FileUploadListener) this.session.get(FileUploadListener.SESSION_FILEUPLOAD_LISTENER);
			if(uploadStatus == null) {
				// upload non attivo...				
				uploadStatus = new FileUploadListener();
				uploadStatus.setContentLength(-1);
				uploadStatus.setCompleted(false);
			} 
			
			// prepara la risposta in formato JSON...
			JSONObject json = new JSONObject();
			json.put("completed", (uploadStatus != null ? Boolean.toString(uploadStatus.isCompleted()) : "false"));
			json.put("percentDone", (uploadStatus != null ? Integer.toString(uploadStatus.getPercentDone()) : "0"));
			json.put("bytesRead", (uploadStatus != null ? Long.toString(uploadStatus.getBytesRead()) : "0"));
			json.put("contentLength", (uploadStatus != null ? Long.toString(uploadStatus.getContentLength()) : "0"));
			
			this.inputStream = new ByteArrayInputStream(json.toString().getBytes("UTF-8"));
				
			json.clear();
			json = null;
			if(uploadStatus != null && uploadStatus.isCompleted()){
				this.session.remove(FileUploadListener.SESSION_FILEUPLOAD_LISTENER);
			}
			this.setTarget(SUCCESS);
			
		} catch (Exception ex) {
			this.addActionError(ex.getMessage());
			this.setTarget(Action.INPUT);
			this.inputStream = null;
		}		
		return this.getTarget();
	}
	
}
