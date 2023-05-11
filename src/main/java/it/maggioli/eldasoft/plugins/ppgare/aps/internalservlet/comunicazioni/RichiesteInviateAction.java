package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.user.UserDetails;
import it.eldasoft.www.sil.WSGareAppalto.InvioType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractOpenPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.beans.ComunicazioniConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class RichiesteInviateAction extends AbstractOpenPageAction 
	implements SessionAware, Serializable  
{ 
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7377057872191014515L;
	
	private Map<String, Object> session;
	
	/** Riferimento al manager per la gestione dei bandi. */
	private IBandiManager bandiManager;
	
	/** Riferimento al manager per la gestione dei parametri applicativo. */
	private IAppParamManager appParamManager;

	/** codice dell'elenco selezionato */
	@Validate(EParamValidation.CODICE)
	private String codiceElenco;
	
	/** lista degli invii relativi all'elenco */
	private List<InvioType> elencoInvii;	

	
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
	
	public Map<String, Object> getSession() {
		return session;
	}
	
	public IBandiManager getBandiManager() {
		return bandiManager;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public IAppParamManager getAppParamManager() {
		return appParamManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}
	
	public String getCodiceElenco() {
		return codiceElenco;
	}

	public void setCodiceElenco(String codiceElenco) {
		this.codiceElenco = codiceElenco;
	}

	public List<InvioType> getElencoInvii() {
		return elencoInvii;
	}

	public void setElencoInvii(List<InvioType> elencoInvii) {
		this.elencoInvii = elencoInvii;
	}	
	
	/**
	 * Visualizza tutte le richieste inviate  da un operatore
	 *  
	 * @return
	 */
	@Override
	public String openPage() {		
		this.setTarget(SUCCESS);
		
		try {
			UserDetails userDetails = this.getCurrentUser();
			
			if( !this.isSessionExpired()
				&& (userDetails != null && userDetails.getUsername() != null) 	
				&& (codiceElenco != null && !codiceElenco.isEmpty()) ) {
				
				String token = StringUtils.stripToNull((String) this.appParamManager
						.getConfigurationValue(AppParamManager.WS_BANDI_ESITI_AVVISI_AUTHENTICATION_TOKEN));

				// recupera l'elenco delle richieste inviate 
				// dall'operatore all'ente...
				ArrayList<String> tipiComunicazione = new ArrayList<String>();
				tipiComunicazione.add(PortGareSystemConstants.RICHIESTA_ISCRIZIONE_ALBO);
				tipiComunicazione.add(PortGareSystemConstants.RICHIESTA_RINNOVO_ISCRIZIONE);
				tipiComunicazione.add(PortGareSystemConstants.RICHIESTA_AGGIORNAMENTO_ISCRIZIONE_ALBO);
		
				Long genere = (Long) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_GENERE_PROCEDURA);					
				if(genere == 20) { 					
					// CATALOGO
					tipiComunicazione.add(PortGareSystemConstants.RICHIESTA_VARIAZIONE_PRODOTTI_CATALOGO);
					tipiComunicazione.add(PortGareSystemConstants.RICHIESTA_VARIAZIONE_PREZZI_E_SCADENZE_CATALOGO);					 
				} 
									
				this.elencoInvii = bandiManager.getElencoInvii(
						this.getCurrentUser().getUsername(), 
						codiceElenco, 
						tipiComunicazione.toArray(new String[tipiComunicazione.size()])); 
			} else {
				// nessun risultato, restituiscono una lista vuota...
				this.elencoInvii = new ArrayList<InvioType>();
			}

		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "openPage");
			ExceptionUtils.manageExceptionError(e, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}
	
}
