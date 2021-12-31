package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.io.InputStream;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Action per la gestione del download di documenti allegati alla domanda
 * d'iscrizione all'albo.
 * 
 * @version 1.0
 * @author Stefano.Sabbadin
 */
public class DownloadAllegatoIscrizioneAction extends ActionSupport 
	implements SessionAware 
{
    /**
     * UID
     */
    private static final long serialVersionUID = -2479103800236560071L;

    private Map<String, Object> session;

    private int id;

    private InputStream inputStream;
    private String filename;
    private String contentType;

    @Override
    public void setSession(Map<String, Object> session) {
    	this.session = session;
    }

    public void setId(int id) {
    	this.id = id;
    }

    public int getId() {
    	return id;
    }

    public InputStream getInputStream() {
    	return inputStream;
    }

    public String getFilename() {
    	return filename;
    }

    public String getContentType() {
    	return contentType;
    }

    /**
     * Restituisce lo stream contenente un documento allegato richiesto per
     * l'iscrizione ad un elenco fornitori
     */
    public String downloadAllegatoRichiesto() {
		String target = SUCCESS;
	
		WizardIscrizioneHelper iscrizioneHelper = (WizardIscrizioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
	
		if (iscrizioneHelper == null) {
		    // la sessione e' scaduta, occorre riconnettersi
		    this.addActionError(this.getText("Errors.sessionExpired"));
		    target = ERROR;
		} else {
		    try {
	    		// la sessione non e' scaduta, per cui proseguo regolarmente
	    		WizardDocumentiHelper documentiHelper = iscrizioneHelper.getDocumenti();
	
	    		this.contentType = documentiHelper.getDocRichiestiContentType().get(this.id);
	    		this.filename = documentiHelper.getDocRichiestiFileName().get(this.id);    		
	    		this.inputStream = documentiHelper.downloadDocRichiesto(this.id);
    		} catch (Exception e) {
    			// il download è una url indipendente e non dentro una porzione
				// di pagina
    			ApsSystemUtils.logThrowable(e, this, "downloadAllegatoRichiesto");
    			ExceptionUtils.manageExceptionError(e, this);
				target = ERROR;
    		} catch (Throwable e) {
    			ApsSystemUtils.logThrowable(e, this, "downloadAllegatoRichiesto");
    			ExceptionUtils.manageExceptionError(e, this);
				target = ERROR;
		    }
		}

		return target;
    }

    /**
     * Restituisce lo stream contenente un documento allegato inserito
     * dall'utente ma non richiesto per l'iscrizione ad un elenco fornitori
     */
    public String downloadAllegatoUlteriore() {
		String target = SUCCESS;
	
		WizardIscrizioneHelper iscrizioneHelper = (WizardIscrizioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
	
		if (iscrizioneHelper == null) {
		    // la sessione e' scaduta, occorre riconnettersi
		    this.addActionError(this.getText("Errors.sessionExpired"));
		    target = ERROR;
		} else {
		    try {
	    		// la sessione non e' scaduta, per cui proseguo regolarmente
	    		WizardDocumentiHelper documentiHelper = iscrizioneHelper.getDocumenti();
	    		
	    		this.contentType = documentiHelper.getDocUlterioriContentType().get(this.id);
	    		this.filename = documentiHelper.getDocUlterioriFileName().get(this.id);    		
	    		this.inputStream = documentiHelper.downloadDocUlteriore(this.id);
    		} catch (Exception e) {
				// il download è una url indipendente e non dentro una porzione
				// di pagina
    			ApsSystemUtils.logThrowable(e, this,"downloadAllegatoUlteriore");
				ExceptionUtils.manageExceptionError(e, this);
				target = ERROR;
    		} catch (Throwable e) {
    			ApsSystemUtils.logThrowable(e, this, "downloadAllegatoUlteriore");
    			ExceptionUtils.manageExceptionError(e, this);
				target = ERROR;
		    }
		}
	
		return target;
    }

}
