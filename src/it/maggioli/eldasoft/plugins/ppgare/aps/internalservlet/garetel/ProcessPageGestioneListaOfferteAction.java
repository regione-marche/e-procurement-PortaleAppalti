package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import java.util.List;
import org.apache.commons.lang.StringUtils;
import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.opensymphony.xwork2.ActionContext;

import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

/**
 * ...
 *
 * @author ...
 */
public class ProcessPageGestioneListaOfferteAction extends EncodedDataAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 2464823859198364408L;
	
	private static final String CONFIRM_RETTIFICA_OFFERTA			= "confirmRettificaOfferta";
	private static final String CONFIRM_RETTIFICA_OFFERTE_DISTINTE 	= "confirmRettificaOfferteDistinte";
	
	/**
	 * Riferimento al manager per la gestione dei parametri applicativo.
	 */
	private ICustomConfigManager customConfigManager;
	private IAppParamManager appParamManager;
	private IComunicazioniManager comunicazioniManager;
	private IEventManager eventManager;
	
	private String codice;
	private String codiceGara;
	private int operazione;
	private String progressivoOfferta;	 
	private String nextResultAction;
	private String fromListaOfferte;	// "1"=true; altrimenti false 
	
	// serve per la navigazione tra le pagine
	private Long idComunicazione;
	
	public void setCustomConfigManager(ICustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}
	
	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getCodiceGara() {
		return codiceGara;
	}

	public void setCodiceGara(String codiceGara) {
		this.codiceGara = codiceGara;
	}

	public int getOperazione() {
		return operazione;
	}

	public void setOperazione(int operazione) {
		this.operazione = operazione;
	}

	public String getProgressivoOfferta() {
		return progressivoOfferta;
	}

	public void setProgressivoOfferta(String progressivoOfferta) {
		this.progressivoOfferta = progressivoOfferta;
	}

	public String getFromListaOfferte() {
		return fromListaOfferte;
	}

	public void setFromListaOfferte(String fromListaOfferte) {
		this.fromListaOfferte = fromListaOfferte;
	}

	public String getNextResultAction() {
		return this.nextResultAction;
	}
	
	public Long getIdComunicazione() {
		return idComunicazione;
	}

	public void setIdComunicazione(Long idComunicazione) {
		this.idComunicazione = idComunicazione;
	}

	/**
	 * prepara la richiesta per un nuovo plico in una gara a lotti 
	 */
	public String nuovaOfferta() {
		this.setTarget(SUCCESS);
		
		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			try {
				// NB: 
				//   se in prequalifica non c'e' la partecipazione come "singola"
				//   allora in offerta NON puo' esserci una partecipazione come "singola" 
				//   ma solo come RTI
				String codGara = (StringUtils.isEmpty(this.codice) ? this.codiceGara : this.codice);

				String tipoComunicazione = (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA)
							? PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT 
							: PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT;
				
				long maxProgOfferta;
				if("1".equals(this.progressivoOfferta)) {
					// se progressivoOfferta = 1 allora 
					// e' stato premuto il pulsante "Aggiungi domanda/offerta singola" !!!
					maxProgOfferta = 1;
				} else {
					// le RTI devono avere progressivo sempre >= 2 !!!
					maxProgOfferta = getMaxProgressivoOfferta(codGara, this.getCurrentUser().getUsername(), tipoComunicazione) + 1;
					if(maxProgOfferta <= 1) {
						maxProgOfferta = 2;
					}
				}
				
				// memorizza il parametro "progressivoOfferta" per il getter/setter della prossima action...
				// (NB: da riverificare se con setProgressivoOfferta(...) funziona!) 
				ActionContext.getContext().getParameters().put("progressivoOfferta", Long.toString(maxProgOfferta));
				ActionContext.getContext().getParameters().put("nuovaOfferta", "1");
				
			} catch (ApsException t) {
				ApsSystemUtils.logThrowable(t, this, "nuovaOfferta");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "nuovaOfferta");
				this.addActionError(this.getText("Errors.invioBuste.config"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}	
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		
		return this.getTarget();
	}
	
	/**
	 * prepara la richiesta per un nuovo plico in una gara a lotti
	 */
	public String nuovaOffertaSingola() {
		this.progressivoOfferta = "1";
		return this.nuovaOfferta();
	}

	/**
	 * annulla una nuova offerta in una gara a lotti 
	 */
	public String annullaOfferta() {
		this.setTarget(SUCCESS);
		
		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			try {
				String codGara = (StringUtils.isEmpty(this.codice) ? this.codiceGara : this.codice);

				// Per l'annullamento si riutilizza l'operazione di "rettifica" gia' esistente
				// per domande e offerte distinte.
				// in caso di nuova offerta senza relativa domanda di prequalifica
				// elimina tutte le comunicazioni comprese FS11, FS11R
				// mentre se esiste domanda di prequalifica con lo stesso progressivo
				// mantieni le comunicazioni FS11/FS11R, ed elimina solo le comunicazioni 
				// delle buste (FS11A,B,C).
				
				this.nextResultAction = (this.operazione == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA
						? CONFIRM_RETTIFICA_OFFERTE_DISTINTE 
						: CONFIRM_RETTIFICA_OFFERTA);
				
				this.setFromListaOfferte("1");	// indica che la richiesta e' partita dalla lista delle offerte
				
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "annullaOfferta");
				ExceptionUtils.manageExceptionError(e, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}	
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		
		return this.getTarget();
	}

	/**
	 * recupera il numero massimo di numero plico (max(comkey3))
	 * @throws ApsException 
	 */
	private long getMaxProgressivoOfferta(String codiceGara, String username, String tipoComunicazione) throws ApsException { 
		long maxNumPlico = 0L;
		
		DettaglioComunicazioneType criteriRicerca = new DettaglioComunicazioneType();
		criteriRicerca.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
		criteriRicerca.setChiave1(username);
		criteriRicerca.setChiave2(codiceGara);
		criteriRicerca.setTipoComunicazione(tipoComunicazione);
		List<DettaglioComunicazioneType> comunicazioni = this.comunicazioniManager.getElencoComunicazioni(criteriRicerca);
		
		if (!comunicazioni.isEmpty()) {
			for (DettaglioComunicazioneType comunicazione : comunicazioni) {
				long numPlico = 0;
				try { 
					numPlico = Long.parseLong(comunicazione.getChiave3());
				} catch(Exception e) {
					numPlico = 0;
				}
				maxNumPlico = (numPlico > maxNumPlico ? numPlico : maxNumPlico);
			}
		}
		
		return maxNumPlico;
	}
	
}
