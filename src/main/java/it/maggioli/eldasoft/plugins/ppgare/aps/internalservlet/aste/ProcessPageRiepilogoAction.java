package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.MessageFormat;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.plugins.jpmail.aps.services.mail.IMailManager;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.INtpManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.wsdm.IWSDMManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste.helper.WizardOffertaAstaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

/**
 * Action di gestione dell'apertura delle pagine del wizard
 *
 * @author ...
 */
public class ProcessPageRiepilogoAction extends AbstractProcessPageAction {	
	/**
	 * UID 
	 */
	private static final long serialVersionUID = -787491114883004956L;
	
	private IAppParamManager appParamManager;
	private IBandiManager bandiManager;
	private INtpManager ntpManager;
	private IComunicazioniManager comunicazioniManager;
	private IEventManager eventManager;	
	private IWSDMManager wsdmManager;
	private IMailManager mailManager;
	private ICustomConfigManager customConfigManager;
	
	@Validate(EParamValidation.CODICE)
	private String codice;
	@Validate(EParamValidation.CODICE)
	private String codiceLotto;	
	private Boolean lottiDistinti;

	// attributi utilizzati dalla pagina "invioOfferta.jsp"
	@Validate(EParamValidation.DATE_DDMMYYYY)
	private String dataInvio;
	@Validate(EParamValidation.DATE_DDMMYYYY)
	private String dataProtocollo;
	private Boolean presentiDatiProtocollazione; 
	private Long annoProtocollo;
	@Validate(EParamValidation.GENERIC)
	private String numeroProtocollo;
	@Validate(EParamValidation.ERRORE)
	private String msgErrore;
	@Validate(EParamValidation.GENERIC)
	private String codiceSistema;
	
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

	public void setCustomConfigManager(ICustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
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
	
	public String getCodiceSistema() {
		return codiceSistema;
	}

	/**
	 * restituisce la label LABEL_RICHIESTA_CON_ID associata a LAPIS 
	 */
	public String getLABEL_RICHIESTA_CON_ID() {
		return MessageFormat.format(this.getI18nLabel("LABEL_RICHIESTA_CON_ID"), new Object[] {dataInvio, numeroProtocollo});
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
		return SUCCESS;		// non dovrebbe essere raggiungibile
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
						this.getRequest().getSession().getId());
	
				// invia la comunicazione FS12 in stato INVIATA...
				comunicazione.sendRichiestaInvioComunicazioneOffertaAsta(
						helper,
						CommonSystemConstants.STATO_COMUNICAZIONE_INVIATA);
				
				// modifica la comunicazione FS13 da BOZZA a PROCESSATA...
				comunicazione.sendComunicazioneGenerazioneOffertaAsta(
						helper,
						true);

				// dati da restituire alla pagina per la visualizzazione del
				// messaggio utente
				this.dataInvio = comunicazione.getDataInvio();
				this.dataProtocollo = comunicazione.getDataProtocollo();
				this.presentiDatiProtocollazione = (comunicazione.getAnnoProtocollo() != null && 
						                            comunicazione.getAnnoProtocollo() > 0); 
				this.annoProtocollo = comunicazione.getAnnoProtocollo();
				this.numeroProtocollo = comunicazione.getNumeroProtocollo();
				this.msgErrore = comunicazione.getMsgErrore();
				this.codiceSistema = comunicazione.getCodiceSistema();
				
			} catch(Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "send");
				this.addActionError(this.getText("Errors.invioComunicazione"));
				this.msgErrore = t.getMessage();
				target = CommonSystemConstants.PORTAL_ERROR;
			}
			
			// rimozione dell'helper dalla sessione...
			if(SUCCESS.equals(target)) {
				//helper.setDatiInviati(true);
				helper.removeFromSession();
			}
		}
		
		return target;
	}

}
