package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import com.agiletec.aps.system.ApsSystemUtils;
import com.opensymphony.xwork2.ActionSupport;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.Attachment;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.struts2.interceptor.SessionAware;

import java.io.InputStream;
import java.util.Map;

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
	@Validate(EParamValidation.FILE_NAME)
    private String filename;
	@Validate(EParamValidation.CONTENT_TYPE)
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
	    		WizardDocumentiHelper documenti = iscrizioneHelper.getDocumenti();
	
				Attachment attachment = documenti.getRequiredDocs().get(id);
				contentType = attachment.getContentType();
				filename = attachment.getFileName();
				//TODO: Da valutare se è meglio passargli direttamente l'attachment
				inputStream = documenti.downloadDocRichiesto(id);
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
	    		Attachment attachment = documentiHelper.getAdditionalDocs().get(id);
	    		contentType = attachment.getContentType();
	    		filename = attachment.getFileName();
	    		inputStream = documentiHelper.downloadDocUlteriore(id);
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
