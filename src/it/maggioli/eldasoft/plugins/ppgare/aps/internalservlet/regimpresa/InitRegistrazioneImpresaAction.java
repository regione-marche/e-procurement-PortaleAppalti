package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import it.eldasoft.sil.portgare.datatypes.RegistrazioneImpresaDocument;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.areapers.RegistrazioneManualeOperatoriEconomiciAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.AccountSSO;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event.Level;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.regimpresa.IRegistrazioneImpresaManager;
import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.apsadmin.system.BaseAction;

/**
 * Action di inizializzazione apertura form registrazione impresa
 * 
 * @author Stefano.Sabbadin
 */
public class InitRegistrazioneImpresaAction extends BaseAction implements SessionAware {
    /**
     * UID
     */
    private static final long serialVersionUID = 8150594723091295857L;

    private Map<String, Object> session;

	private IAppParamManager appParamManager;
	private IComunicazioniManager comunicazioniManager;
	private IRegistrazioneImpresaManager registrazioneImpresaManager;
	private IEventManager eventManager;

	private String target;
	private String token;						// token di registrazione in attesa di valutazione 

	@Override
    public void setSession(Map<String, Object> session) {
    	this.session = session;
    }

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public void setRegistrazioneImpresaManager(IRegistrazioneImpresaManager registrazioneImpresaManager) {
		this.registrazioneImpresaManager = registrazioneImpresaManager;
	}
	
	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * inizializza
	 */
	public String init() {
		this.target = SUCCESS;
		this.session.put(PortGareSystemConstants.SESSION_ID_DETT_REGISTRAZIONE_IMPRESA,
						 this.createWizardRegistrazioneImpresa());
		return this.target;
    }
    
	/**
	 * Accesso previsto nel caso di autenticazione preventiva mediante single
	 * sign on e selezione del link in area personale soggetto fisico. Mi devo
	 * accertare che la sessione sia ancora valida.
	 * 
	 * @return
	 */
    public String initSSO() {
    	this.target = SUCCESS;
    	
		AccountSSO accountSSO = (AccountSSO)this.session.get(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO);

		int ssoProtocollo = 0; //di default l'utilizzo di sso e' disabilitato
		Integer configurazioneSSOProtocollo = (Integer)appParamManager.getConfigurationValue(PortGareSystemConstants.TIPO_PROTOCOLLO_SSO);
		if(configurazioneSSOProtocollo != null) {
			if(configurazioneSSOProtocollo.intValue() >= PortGareSystemConstants.MIN_NUM_SSO_PROTOCOLLO && 
			   configurazioneSSOProtocollo <= PortGareSystemConstants.MAX_NUM_SSO_PROTOCOLLO) {
				ssoProtocollo = configurazioneSSOProtocollo.intValue();
			}
		}
		
		if (ssoProtocollo > 0 && accountSSO == null) {
			// se sono rimasto nella pagina dell'area personale soggetto fisico
			// e dopo molto tempo seleziono la funzione di registrazione, mi
			// accerto di possedere i dati in sessione quando sono previsti
			this.addActionError(this.getText("Errors.userRegistration.ssoSessionExpired"));
			this.target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			// proseguo 
			this.session.put(PortGareSystemConstants.SESSION_ID_DETT_REGISTRAZIONE_IMPRESA,
							 this.createWizardRegistrazioneImpresa());
		}
	
		return this.target;
    }
    
    /**
     * prepara un nuovo WizardRegistrazioneImpresaHelper 
     * 
     * @throws Exception 
     */
    private WizardRegistrazioneImpresaHelper createWizardRegistrazioneImpresa() {
    	WizardRegistrazioneImpresaHelper registrazioneImpresa = null;
    	
    	// verifica se e' stato richiesta la rivalutazione di una registrazione manuale 
    	// se è presente un "token" allora va riaperta la comunicazione FS1 in stato BOZZA 
    	// relativa alla registrazione manuale
    	if(StringUtils.isNotEmpty(this.token)) {
    		Event evento = null;
			try {
				evento = new Event();
				evento.setUsername(this.getCurrentUser().getUsername());
				evento.setIpAddress(this.getCurrentUser().getIpAddress());
				evento.setSessionId(this.getRequest().getSession().getId());
				evento.setMessage("Accesso alla registrazione riportata in bozza");
				evento.setLevel(Level.INFO);
				evento.setEventType(PortGareEventsConstants.PROCESSA_TOKEN);
				
				// decodifica il token
				// username | id comunicazione | data scadenza token (default nessuna scadenza)
				String[] v = RegistrazioneManualeOperatoriEconomiciAction.decodeToken(this.token);
	    		String username = (v.length > 0 ? v[0] : "");
	    		String idcom = (v.length > 1 ? v[1] : "");
	    		String scadenza = (v.length > 2 ? v[2] : "");

	    		evento.setMessage(evento.getMessage() + " con id comunicazione " + idcom);
	    		
	    		// recupera la comunicazione in stato BOZZA
	    		ComunicazioneType comunicazione = this.comunicazioniManager
					.getComunicazione(CommonSystemConstants.ID_APPLICATIVO, Long.parseLong(idcom));
	    		
	    		if(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA.equals(comunicazione.getDettaglioComunicazione().getStato())) {
	    			
					RegistrazioneImpresaDocument doc = (RegistrazioneImpresaDocument) WizardRegistrazioneImpresaHelper
						.getXmlDocumentRegistrazione(comunicazione);
	        		registrazioneImpresa = new WizardRegistrazioneImpresaHelper(doc.getRegistrazioneImpresa().getDatiImpresa());
	        		registrazioneImpresa.setUsername(username);
	        		registrazioneImpresa.setUsernameConfirm(username);
	        		registrazioneImpresa.setAttesaValidazione(true);
	        		
	        		// elimina un eventuale profilo temporaneo prelativo all'utente
	        		// per evitare che le validazioni di CV e P.IVA impediscando
	        		// di completare la registrazione impresa
	        		this.registrazioneImpresaManager.deleteImpresa(username);
	        		
	        		this.target = "successRegManuale";
	    		}
			} catch (Exception e) {
				this.target = CommonSystemConstants.PORTAL_ERROR; 
				ApsSystemUtils.getLogger().error("createWizardRegistrazioneImpresa", e);
				evento.setLevel(Level.ERROR);
				evento.setError(e);
			}
			
			if(evento != null) {
				this.eventManager.insertEvent(evento);
			}
    	}
    	
    	if(registrazioneImpresa == null) {
    		registrazioneImpresa = new WizardRegistrazioneImpresaHelper();
    	}
    	
    	return registrazioneImpresa;
    }
    

}