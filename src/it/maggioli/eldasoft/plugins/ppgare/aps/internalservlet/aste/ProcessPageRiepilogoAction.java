package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.INtpManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.wsdm.IWSDMManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste.helper.WizardOffertaAstaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.plugins.jpmail.aps.services.mail.IMailManager;

/**
 * Action di gestione dell'apertura delle pagine del wizard
 *
 * @author ...
 */
public class ProcessPageRiepilogoAction extends AbstractProcessPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 1L;
	
	private IAppParamManager appParamManager;
	private IBandiManager bandiManager;
	private INtpManager ntpManager;
	private IComunicazioniManager comunicazioniManager;			
	private IEventManager eventManager;	
	private IWSDMManager wsdmManager;
	private IMailManager mailManager;
	
	private String codice;
	private String codiceLotto;
	private Boolean lottiDistinti;

	// attributi utilizzati dalla pagina "invioOfferta.jsp"
	private String dataInvio;
	private String dataProtocollo;
	private Boolean presentiDatiProtocollazione; 
	private Long annoProtocollo;
	private String numeroProtocollo;
	private String msgErrore;

	
	public void setNtpManager(INtpManager ntpManager) {
		this.ntpManager = ntpManager;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}
	
	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public void setMailManager(IMailManager mailManager) {
		this.mailManager = mailManager;
	}

	public void setWsdmManager(IWSDMManager wsdmManager) {
		this.wsdmManager = wsdmManager;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getCodiceLotto() {
		return codiceLotto;
	}

	public void setCodiceLotto(String codiceLotto) {
		this.codiceLotto = codiceLotto;
	}

	public Boolean getLottiDistinti() {
		return lottiDistinti;
	}

	public void setLottiDistinti(Boolean lottiDistinti) {
		this.lottiDistinti = lottiDistinti;
	}	

	public String getDataInvio() {
		return dataInvio;
	}

	public String getDataProtocollo() {
		return dataProtocollo;
	}

	public Boolean getPresentiDatiProtocollazione() {
		return presentiDatiProtocollazione;
	}

	public Long getAnnoProtocollo() {
		return annoProtocollo;
	}

	public String getNumeroProtocollo() {
		return numeroProtocollo;
	}

	public String getMsgErrore() {
		return msgErrore;
	}

	/**
	 * costruttore
	 */
	public ProcessPageRiepilogoAction() {
		 super(PortGareSystemConstants.SESSION_ID_DETT_OFFERTA_ASTA,
			   WizardOffertaAstaHelper.STEP_RIEPILOGO, 
			   true);
	} 

	/**
	 * ... 
	 */
	@Override
	public String next() {
		return SUCCESS;		// non dovrebbe essere raggingibile
	}

	/**
	 * ... 
	 */
	@Override
	public String back() {
		return this.helperBack();
	}
	
	/**
	 * Effettua l'invio della comunicazione al backoffice costituita da una
	 * testata e da un allegato contenente al suo interno l'XML dei dati inseriti,
	 * e poi ripulisce la sessione dai dati raccolti ed imposta la chiave del
	 * bando oggetto della partecipazione per poter ritornare al suo dettaglio
	 *
	 * @throws ApsException
	 */
	public String send() {
		String target = SUCCESS;
		
		WizardOffertaAstaHelper helper = WizardOffertaAstaHelper.fromSession();
		
		if(helper == null) {
			// la sessione e' scaduta, occorre riconnettersi...
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else if(helper.isDatiInviati()) {
			this.addActionError(this.getText("label.ComunicazioneGiaInviataCorrettamente"));
			target = CommonSystemConstants.PORTAL_ERROR;			
		} else {
			try {
				this.codice = helper.getAsta().getCodice();
				this.codiceLotto = helper.getAsta().getCodiceLotto();
				this.lottiDistinti = (
					helper.getOffertaEconomica().getGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_OFFERTE_DISTINTE ||
					helper.getOffertaEconomica().getGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_PLICO_UNICO
				);

				WizardDatiImpresaHelper impresaHelper = helper.getOffertaEconomica().getImpresa();	
				
				
				// Utilizza la classe "SendComunicazioni" per gestire 
				// l'invio della comunicazione...						
				ComunicazioniUtilities comunicazione = new ComunicazioniUtilities(
						this,
						this.appParamManager,
						this.bandiManager,
						this.comunicazioniManager,
						this.eventManager,
						this.ntpManager,
						this.mailManager,
						this.wsdmManager,
						this.getRequest().getSession().getId());						
	
				// invia la comunicazione FS12...
				comunicazione.sendRichiestaInvioComunicazioneOffertaAsta(
						helper.getAsta().getCodice(), 
						helper.getAsta().getCodiceLotto(),
						"MAIL_ASTE_RICEVUTA_OGGETTO",
						"MAIL_ASTE_PROTOCOLLO_TESTO",
						"MAIL_ASTE_RICEVUTA_OGGETTO",
						"MAIL_ASTE_RICEVUTA_TESTO",
						"MAIL_ASTE_RICEVUTA_TESTOCONPROTOCOLLO",
						"MAIL_ASTE_RICEVUTA_OGGETTO",
						"MAIL_ASTE_PROTOCOLLO_TESTO",
						helper);				
				
				// modifica la comunicazione FS13 da BOZZA a PROCESSATA...
				comunicazione.sendComunicazioneGenerazioneOffertaAsta(
						true,						
						"NOTIFICA_ASTE_OGGETTO",
						"NOTIFICA_ASTE_TESTO",
						"NOTIFICA_ASTE_ALLEGATO_DESCRIZIONE",
						helper);				

				// dati da restituire alla pagina per la visualizzazione del
				// messaggio utente
				this.dataInvio = comunicazione.getDataInvio();
				this.dataProtocollo = comunicazione.getDataProtocollo();
				this.presentiDatiProtocollazione = (comunicazione.getAnnoProtocollo() != null && 
						                            comunicazione.getAnnoProtocollo() > 0); 
				this.annoProtocollo = comunicazione.getAnnoProtocollo();
				this.numeroProtocollo = comunicazione.getNumeroProtocollo();
				this.msgErrore = comunicazione.getMsgErrore();				
								
			} catch(Throwable t) {
				//ApsSystemUtils.logThrowable(t, this, "send");
				//ExceptionUtils.manageExceptionError(t, this);
				this.addActionError(this.getText("Errors.invioComunicazione"));
				target = CommonSystemConstants.PORTAL_ERROR;
				this.msgErrore = t.getMessage();
			}
							
			// rimozione dell'helper dalla sessione...
			//if(SUCCESS.equals(target)) {			
				//helper.setDatiInviati(true);
				helper.removeFromSession();
			//}
		}
		
		return target;
	}

}
