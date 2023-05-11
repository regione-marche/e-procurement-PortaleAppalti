package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.INtpManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo.InitIscrizioneAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import org.apache.struts2.interceptor.SessionAware;
import org.slf4j.Logger;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;

/**
 * Action di inizializzazione apertura wizard partecipazione al bando di gara.
 * Questa classe estende EncodedDataAction esclusivamente per poter usufruire
 * del metodo statico ImpresaAction.getLatestDatiImpresa
 *
 * @author Stefano.Sabbadin
 */
public class InitPartecipazioneAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 8730921987830449769L;
	private final Logger logger = ApsSystemUtils.getLogger();
	
	private INtpManager ntpManager;
	private IBandiManager bandiManager;
	private IComunicazioniManager comunicazioniManager;
	private IAppParamManager appParamManager;

	private Map<String, Object> session;

	/**
	 * Codice del bando per elenco operatori economici per il quale gestire un'iscrizione
	 */
	@Validate(EParamValidation.CODICE)
	private String codice;
	@Validate(EParamValidation.GENERIC)
	private String operazione;
	@Validate(EParamValidation.DIGIT)
	private String progressivoOfferta;

	public void setNtpManager(INtpManager ntpManager) {
		this.ntpManager = ntpManager;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}
	
	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getOperazione() {
		return operazione;
	}

	public void setOperazione(String operazione) {
		this.operazione = operazione;
	}

	public String getProgressivoOfferta() {
		return progressivoOfferta;
	}

	public void setProgressivoOfferta(String progressivoOfferta) {
		this.progressivoOfferta = progressivoOfferta;
	}

	/**
	 * Richiede al backoffice i dati dell'impresa per consentire la gestione del
	 * wizard di partecipazione ad una gara
	 */
	public String newPartecipazione() {
		return this.newWizard(PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA);
	}

	/**
	 * Richiede al backoffice i dati dell'impresa per consentire la gestione del
	 * wizard di invio offerta
	 */
	public String newInvioOfferta() {
		return this.newWizard(PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA);
	}

	/**
	 * Richiede al backoffice i dati dell'impresa per consentire la gestione del
	 * wizard di comprova requisiti ex articolo 48
	 */
	public String newComprovaRequisiti() {
		return this.newWizard(PortGareSystemConstants.TIPOLOGIA_EVENTO_COMPROVA_REQUISITI);
	}

	/**
	 * Richiede al backoffice i dati dell'impresa mediante una comunicazione o
	 * l'accesso ai dati dell'impresa per consentire la gestione del wizard di
	 * partecipazione ad una gara/invio offerta/comprova requisiti e memorizza
	 * tali dati in sessione
	 */
	private String newWizard(int tipoEvento) {
		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			try {
				// si estraggono dal B.O. i dati del bando di gara a cui
				// partecipare
				//DettaglioGaraType dettGara = this.bandiManager.getDettaglioGara(this.codice);
				GestioneBuste buste = GestioneBuste.getFromSession();
				if(buste == null) {
					buste = new GestioneBuste(
							this.getCurrentUser().getUsername(),
							this.codice,
							this.progressivoOfferta,
							tipoEvento);
					buste.get();
				}
				logger.info("newWizard->buste {}",buste);
				buste.getBustaPartecipazione().getHelper().setTipoEvento(tipoEvento);
				
				DettaglioGaraType dettGara = buste.getDettaglioGara();

				// il controllo sulla data non si effettua per la comprova
				// requisiti
				Date dataTermine = null;
				String oraTermine = null;
				if (tipoEvento == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA) {
					dataTermine = dettGara.getDatiGeneraliGara().getDataTerminePresentazioneDomanda();
					oraTermine = dettGara.getDatiGeneraliGara().getOraTerminePresentazioneDomanda();
				} else if (tipoEvento == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA) {
					dataTermine = dettGara.getDatiGeneraliGara().getDataTerminePresentazioneOfferta();
					oraTermine = dettGara.getDatiGeneraliGara().getOraTerminePresentazioneOfferta();
				}
				
				String nomeOperazione = null;
				switch (tipoEvento) {
				case PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA:
					nomeOperazione = this.getI18nLabel("BUTTON_DETTAGLIO_GARA_PRESENTA_PARTECIPAZIONE");
					break;
				case PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA:
					nomeOperazione = this.getI18nLabel("BUTTON_DETTAGLIO_GARA_PRESENTA_OFFERTA");
					break;
				case PortGareSystemConstants.TIPOLOGIA_EVENTO_COMPROVA_REQUISITI:
					nomeOperazione = this.getI18nLabel("BUTTON_DETTAGLIO_GARA_COMPROVA_REQUISITI");
					break;
				}
				
				if (dataTermine != null) {
					// si prosegue solo se la data termine e' valorizzata

					// si calcola il timestamp ntp
					Date dataAttuale = null;
					try {
						dataAttuale = this.ntpManager.getNtpDate();
					} catch (SocketTimeoutException e) {
						this.addActionError(this.getText("Errors.ntpTimeout", new String[] { nomeOperazione }));
						this.setTarget("block");
					} catch (UnknownHostException e) {
						this.addActionError(this.getText("Errors.ntpUnknownHost", new String[] { nomeOperazione }));
						this.setTarget(CommonSystemConstants.PORTAL_ERROR);
					} catch (ApsSystemException e) {
						this.addActionError(this.getText("Errors.ntpUnexpectedError", new String[] { nomeOperazione }));
						this.setTarget("block");
					}

					if (dataAttuale != null) {
						if (dataAttuale.compareTo(InitIscrizioneAction.calcolaDataOra(dataTermine, oraTermine, true)) > 0) {
							this.addActionError(this.getText("Errors.richiestaFuoriTempoMassimo"));
							this.setTarget("block");
						}
					}
				}

			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "newWizard - " + tipoEvento);
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}

		return this.getTarget();
	}

}
