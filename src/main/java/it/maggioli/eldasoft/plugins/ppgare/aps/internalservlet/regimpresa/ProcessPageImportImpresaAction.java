package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa;

import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.AccountSSO;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa.inc.ImpresaImportExport.WrongCRCException;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.struts2.interceptor.SessionAware;

import java.io.File;
import java.util.Map;

/**
 * Action di importazione da XML dei dati dell'impresa
 * 
 * @author Cristiano.Crescenti
 * @since 2.3.0
 */			 
public class ProcessPageImportImpresaAction extends EncodedDataAction
	implements SessionAware {

	/**
     * UID
     */
	private static final long serialVersionUID = 4161078045796110374L;
   
	private IEventManager eventManager;
	
	private Map<String, Object> session;

	@Validate(EParamValidation.TIPO_IMPORT)
    private String tipoImport;				// tipo di formato del file XML da importare ('PORTALE', 'DGUE')
    private File xmlImport;					// file XML da importare
	@Validate(EParamValidation.FILE_NAME)
    private String xmlImportFileName;		// nomefile del file XML da importare

    @Override
	public void setSession(Map<String, Object> session) {
		this.session = session;		
	}
    	
	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}
    
    public String getTipoImport() {
		return tipoImport;
	}

	public void setTipoImport(String tipoImport) {
		this.tipoImport = tipoImport;
	}

	public File getXmlImport() {
		return xmlImport;
	}

	public void setXmlImport(File xmlImport) {
		this.xmlImport = xmlImport;
	}

	public String getXmlImportFileName() {
		return xmlImportFileName;
	}

	public void setXmlImportFileName(String xmlImportFileName) {
		this.xmlImportFileName = xmlImportFileName;
	}

	/**
     * validazione
     */
    @Override
	public void validate() {
		super.validate();
	}

    public String cancel() {
    	return "cancel";
    }
    
    /**
     * importa i dati da file XML esportato da un altro PortaleAppalti
     */
    public String importFile() {
    	this.setTarget(OpenPageImportImpresaAction.XML_IMPORT_PORTALE.equalsIgnoreCase(this.tipoImport) ? "errorXMLPortale" : "errorXMLDGUE");

    	WizardRegistrazioneImpresaHelper helper = (WizardRegistrazioneImpresaHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_REGISTRAZIONE_IMPRESA);
    	
    	if(helper == null) {
    		// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
    	} else {
    		helper.setXmlImported(false);
    		
    		Event evento = null;
    		try {
    			// traccia l'evento di esportazione dei dati impresa...
    			evento = new Event();
    			evento.setUsername(this.getCurrentUser().getUsername());
    			//evento.setDestination("");
    			evento.setLevel(Event.Level.INFO);
    			evento.setEventType(PortGareEventsConstants.UPLOAD_FILE);
    			evento.setIpAddress(this.getCurrentUser().getIpAddress());
    			evento.setSessionId(this.getRequest().getSession().getId());
    			evento.setMessage("Importazione dati impresa da file" + (this.xmlImport != null ? " " + this.xmlImportFileName : ""));
    			
				if (this.xmlImport == null) {
					// file non selezionato
					evento.setLevel(Event.Level.ERROR);
					evento.setDetailMessage(this.getTextFromDefaultLocale("Errors.fileNotSet"));
					this.addActionError(this.getText("Errors.fileNotSet"));
	
				} else if (!this.xmlImportFileName.toLowerCase().endsWith(".xml")) {
					// file non in formato xml
					evento.setLevel(Event.Level.ERROR);
					evento.setDetailMessage(this.getTextFromDefaultLocale("Errors.importXml.xmlRequired"));
					this.addActionError(this.getText("Errors.importXml.xmlRequired"));
	
				} else if(OpenPageImportImpresaAction.XML_IMPORT_PORTALE.equalsIgnoreCase(this.tipoImport)) {
					// importa da PortaleAppalti
					try {
						AccountSSO sso = (AccountSSO) this.session.get(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO);
						boolean isSpidBusiness = sso != null && PortGareSystemConstants.TIPOLOGIA_LOGIN_MAGGIOLI_AUTH_SSO_BUSINESS.equals(sso.getTipologiaLogin());

						helper.importFromXmlPortale(this.xmlImport, this.xmlImportFileName, isSpidBusiness);
						evento.setMessage("Importazione dati impresa in formato M-XML da file " + this.xmlImportFileName + 
	    						  		  " per la ditta " + helper.getDatiPrincipaliImpresa().getRagioneSociale());
					} catch (WrongCRCException ex) {
						evento.setLevel(Event.Level.WARNING);
						evento.setMessage(ex.getMessage());
						this.addActionMessage(this.getText("Warnings.importXml.crc"));
					}
		    		this.setTarget(SUCCESS);
				
		    	} else if(OpenPageImportImpresaAction.XML_IMPORT_DGUE.equalsIgnoreCase(tipoImport)) {
		    		// importa da eDGUE-IT
	    			helper.importFromXmlDGUE(xmlImport, xmlImportFileName);
		    		evento.setMessage("Importazione dati impresa in formato DGUE da file " + xmlImportFileName +
		    						  " per la ditta " + helper.getDatiPrincipaliImpresa().getRagioneSociale());	
		    		this.setTarget(SUCCESS);
		    	} else {
		    		// importazione non definita...
		    		this.setTarget(CommonSystemConstants.PORTAL_ERROR);
					evento.setLevel(Event.Level.ERROR);
					evento.setDetailMessage(this.getTextFromDefaultLocale("Errors.importXml.undefined"));
		    	}
	    
	    	} catch (Exception ex) {
//	    		evento.setLevel(Event.Level.ERROR);
//	    		evento.setMessage(ex.getMessage());
//				evento.setDetailMessage(ex.getMessage());
    			evento.setError(ex);
    			this.addActionError( ex.getMessage() );
	    	} finally {	    		
				// registra l'evento di importazione...
	    		if(evento != null)
	    			this.eventManager.insertEvent(evento);
	    	}
	    	
	    	// memorizza i dati dell'helper in sessione...
	    	if(SUCCESS.equals(this.getTarget())) {
	    		helper.setXmlImported(true);
	    		this.session.put(PortGareSystemConstants.SESSION_ID_DETT_REGISTRAZIONE_IMPRESA, helper);
	    	}
    	}	    
    	return this.getTarget();
    }

}
