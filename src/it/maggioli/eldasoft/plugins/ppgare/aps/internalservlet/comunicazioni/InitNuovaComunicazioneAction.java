package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;

import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.www.sil.WSGareAppalto.ComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.INtpManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.beans.ComunicazioniConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardNuovaComunicazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardSoccorsoIstruttorioHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ImpresaAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;

public class InitNuovaComunicazioneAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -2654354870263779124L;
	
	private ICustomConfigManager customConfigManager;
	private IAppParamManager appParamManager;
	private IBandiManager bandiManager;
	private IEventManager eventManager;
	private INtpManager ntpManager;
	
	private Map<String, Object> session;
	private String codice;
	private String tipo;
	private String oggetto;
	private String from;
	private String actionName;
	private String namespace;
	private Long id;
	private Long idDestinatario;	// progressivo dell'impresa relativo alla comunicazione
	private String destinatario;
	private int pagina;
	
	/** Memorizza la prossima dispatchAction da eseguire nel wizard. */
	private String nextResultAction;


	@Override
	public void setSession(Map<String, Object> arg0) {
		this.session = arg0;
	}
	
	public void setCustomConfigManager(ICustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}
	
	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}
	
	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}

	public void setNtpManager(INtpManager ntpManager) {
		this.ntpManager = ntpManager;
	}

	public String getOggetto() {
		return oggetto;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getNextResultAction() {
		return nextResultAction;
	}

	public static String setNextResultAction(String target) {
		return target;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getIdDestinatario() {
		return idDestinatario;
	}

	public void setIdDestinatario(Long idDestinatario) {
		this.idDestinatario = idDestinatario;
	}
	
	public String getDestinatario() {
		return destinatario;
	}

	public void setDestinatario(String destinatario) {
		this.destinatario = destinatario;
	}

	public int getPagina() {
		return pagina;
	}

	public void setPagina(int pagina) {
		this.pagina = pagina;
	}

	/**
	 * ... 
	 */
	public String initNuovaComunicazione() {
		this.setTarget(SUCCESS);
		
		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			try {
				if (this.customConfigManager.isVisible("COMUNICAZIONI", "INVIARISPONDI")) {
					WizardNuovaComunicazioneHelper helper = null;
					
					boolean soccorsoIstruttorio = false;
					ComunicazioneType comunicazione = null;
					if(this.id != null) {
						comunicazione = this.bandiManager.getComunicazioneRicevuta(
									this.getCurrentUser().getUsername(), 
									this.id,
									this.idDestinatario);
						soccorsoIstruttorio = (comunicazione.getModello() != null && comunicazione.getModello() > 0);
					}

					// se esiste una data di scadenza per la comunicazione 
					// verifica se la richiesta e' oltre la data di scadenza
					boolean continua = true;
					Date dataScadenza = null;
					if(comunicazione != null && soccorsoIstruttorio) {
						dataScadenza = WizardNuovaComunicazioneHelper.calcolaDataOra(comunicazione.getDataScadenza(), comunicazione.getOraScadenza());
						if(dataScadenza != null) {
							// verifica se la richiesta e' oltre la data di scadenza
							Date dataOra = this.ntpManager.getNtpDate();
							if(dataOra != null) {
								if(dataOra.after(dataScadenza)) {
									continua = false;
									this.setTarget(CommonSystemConstants.PORTAL_ERROR);
									this.addActionError(this.getText("Errors.richiestaFuoriTempoMassimo"));
									
									// si tracciano solo i tentativi di accesso alle funzionalita' fuori tempo massimo
									Event evento = new Event();
									evento.setUsername(this.getCurrentUser().getUsername());
									evento.setDestination(this.codice);
									evento.setLevel(Event.Level.ERROR);
									evento.setEventType(PortGareEventsConstants.ACCESSO_FUNZIONE);
									evento.setIpAddress(this.getCurrentUser().getIpAddress());
									evento.setSessionId(this.getRequest().getSession().getId());
									evento.setMessage("Accesso alla funzione soccorso istruttorio (comunicazione con id " + this.id + ")");
									evento.setDetailMessage(PortGareEventsConstants.DETAIL_MESSAGE_ERRORE_FUORI_TEMPO_MASSIMO + 
															" (" + UtilityDate.convertiData(dataOra, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS) + ")");
									this.eventManager.insertEvent(evento);
								}
							}
						}
					}

					if(continua) {
						// inizializza il wizard corretto (helper comunicazione o helper soccorso istruttorio)
						String oggettoRisposta = null;
						if( !soccorsoIstruttorio ) {
							// comunicazione standard
							helper = new WizardNuovaComunicazioneHelper();
							oggettoRisposta = (comunicazione != null ? StringUtils.abbreviate("RE: " + comunicazione.getOggetto(), 300) : null);
						} else {
							// soccorso istruttorio
							WizardSoccorsoIstruttorioHelper soccorso = new WizardSoccorsoIstruttorioHelper();
							oggettoRisposta = (comunicazione != null ? StringUtils.abbreviate("Rif: " + comunicazione.getOggetto(), 300) : null);
							
							List<DocumentazioneRichiestaType> documentiRichiestiDB = this.bandiManager
								.getDocumentiRichiestiComunicazione(
										CommonSystemConstants.ID_APPLICATIVO_GARE,
										comunicazione.getIdComunicazione());
							soccorso.setDocumentiRichiesti(documentiRichiestiDB);
							
							helper = soccorso;
						}
	
						// imposta l'oggetto per la comunicazione di risposta
						if(comunicazione != null) {
							helper.setOggetto(oggettoRisposta);
							helper.setStazioneAppaltante(comunicazione.getStazioneAppaltante());
							helper.setModello(comunicazione.getModello());
							helper.setTipoBusta(comunicazione.getTipoBusta());
							helper.setDataScadenza(dataScadenza);
						}
	
						helper.setCodice(this.codice);
						helper.setComunicazioneId(this.id);
						helper.setIdDestinatario(this.idDestinatario);	// progressivo del destinatario relativo alla comunicazione !!!
						helper.setDestinatario(this.destinatario);
						helper.setMittente(this.destinatario);
					
						// --- CESSATI ---
						helper.setDatiImpresaHelper(ImpresaAction.getLatestDatiImpresa(
									this.getCurrentUser().getUsername(), 
									this, 
									this.appParamManager));
						
						if(StringUtils.isEmpty(helper.getMittente())) {
							if(helper.getDatiImpresaHelper() != null && helper.getDatiImpresaHelper().getDatiPrincipaliImpresa() != null) {
								helper.setMittente(helper.getDatiImpresaHelper().getDatiPrincipaliImpresa().getRagioneSociale());
							}
						}
						
						//helper.setDataInvio(retrieveDataInvio(this.ntpManager, this,
						//		"invio nuova comunicazione"));
						helper.fillStepsNavigazione();
						
						this.nextResultAction = InitNuovaComunicazioneAction
								.setNextResultAction(helper.getStepNavigazione().firstElement());
						
						this.session.put(PortGareSystemConstants.SESSION_ID_NUOVA_COMUNICAZIONE, helper);
						this.session.put(ComunicazioniConstants.SESSION_ID_FROM, this.from);
						this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_PAGINA, this.pagina);
						this.session.put(ComunicazioniConstants.SESSION_ID_DETTAGLIO_PRESENTE, true);
						
						// In caso arrivassi da 
						//    area personale -> archiviate/ricevute -> vai alla procedura -> invia nuova comunicazione
						// l'actionName e il namespace sarebbero null, causando 
						// l'errore nell'annulamento della procedura di nuova 
						// comunicazione
						String action = (String) this.session.get(ComunicazioniConstants.SESSION_ID_ACTION_NAME);
						if(StringUtils.isEmpty(action)) {
							// Salvo in sessione quelli passati come parametro 
							// dal link per avviare una nuova comunicazione
							this.session.put(ComunicazioniConstants.SESSION_ID_ACTION_NAME, this.actionName);
							this.session.put(ComunicazioniConstants.SESSION_ID_NAMESPACE, this.namespace);
						} else if(StringUtils.isNotEmpty(this.actionName)) {
							this.session.put(ComunicazioniConstants.SESSION_ID_ACTION_NAME, this.actionName);
							this.session.put(ComunicazioniConstants.SESSION_ID_NAMESPACE, this.namespace);
						}
					}
				} else {
					this.addActionError(this.getText("Errors.function.notEnabled"));
					this.setTarget(CommonSystemConstants.PORTAL_ERROR);
				}
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "initNuovaComunicazione");
				ExceptionUtils.manageExceptionError(e, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}

		return this.getTarget();
	}
	
}
