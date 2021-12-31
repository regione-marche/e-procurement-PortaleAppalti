package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.WSDocumentoType;
import it.eldasoft.www.WSOperazioniGenerali.WSDocumentoTypeVerso;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioBandoIscrizioneType;
import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.eldasoft.www.sil.WSGareAppalto.FascicoloProtocolloType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.INtpManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.wsdm.IWSDMManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.IDatiPrincipaliImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.WSGareAppaltoWrapper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.datiimpresa.DatiImpresaChecker;
import it.maggioli.eldasoft.ws.dm.WSDMFascicoloType;
import it.maggioli.eldasoft.ws.dm.WSDMInserimentoInFascicoloType;
import it.maggioli.eldasoft.ws.dm.WSDMLoginAttrType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAllegatoType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAnagraficaType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoInType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloInOutType;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.apsadmin.system.BaseAction;
import com.agiletec.plugins.jpmail.aps.services.mail.IMailManager;
import com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.IUserProfile;
import com.lowagie.text.DocumentException;

/**
 * Action di gestione dell'apertura delle pagine del wizard
 * 
 * @author Stefano.Sabbadin
 */
public class SaveWizardIscrizioneAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -4428003251269409570L;

	private Map<String, Object> session;

	/** Riferimento al manager per la gestione dei parametri applicativo. */
	private INtpManager ntpManager;
	private IBandiManager bandiManager;
	private IComunicazioniManager comunicazioniManager;
	private IAppParamManager appParamManager;
	private IWSDMManager wsdmManager;
	private IEventManager eventManager;
	private IMailManager mailManager;
	
	private Integer tipoProtocollazione;
	private String mailUfficioProtocollo;
	private Boolean allegaDocMailUfficioProtocollo;
	
	private String codice;
	private Date dataPresentazione;
	private int tipologia;

	private String numeroProtocollo;
	private Long annoProtocollo;
	private String stazioneAppaltanteProtocollante;
	private String dataProtocollo;
	private String username;
	private String msgErrore;
	
	/** Memorizza lal prossima dispatchAction da eseguire nel wizard. */
	private String nextResultAction;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setNtpManager(INtpManager ntpManager) {
		this.ntpManager = ntpManager;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public void setMailManager(IMailManager mailManager) {
		this.mailManager = mailManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}	

	public void setWsdmManager(IWSDMManager wsdmManager) {
		this.wsdmManager = wsdmManager;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}
	
	public String getNextResultAction() {
		return nextResultAction;
	}

	public String getCodice() {
		return codice;
	}

	public Date getDataPresentazione() {
		return dataPresentazione;
	}

	public String getMsgErrore() {
		return msgErrore;
	}

	public int getTipologia() {
		return tipologia;
	}

	public String getNumeroProtocollo() {
		return numeroProtocollo;
	}

	public void setNumeroProtocollo(String numeroProtocollo) {
		this.numeroProtocollo = numeroProtocollo;
	}

	public Long getAnnoProtocollo() {
		return annoProtocollo;
	}

	public void setAnnoProtocollo(Long annoProtocollo) {
		this.annoProtocollo = annoProtocollo;
	}

	public String getDataProtocollo() {
		return dataProtocollo;
	}

	public void setDataProtocollo(String dataProtocollo) {
		this.dataProtocollo = dataProtocollo;
	}

	public boolean isPresentiDatiProtocollazione() {
		return (this.tipoProtocollazione.intValue() == PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_WSDM) && 
		        this.annoProtocollo != null && 
		        this.numeroProtocollo != null;
	}

	/**
	 * ... 
	 */
	public String cancel() {
		return "cancel";
	}

	/**
	 * ... 
	 */
	public String back() {
		String target = SUCCESS;

		WizardIscrizioneHelper iscrizioneHelper = (WizardIscrizioneHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
		this.nextResultAction = iscrizioneHelper.getPreviousAction(WizardIscrizioneHelper.STEP_PRESENTA_ISCRIZIONE);
		
		return target;
	}

	/**
	 * ...
	 */
	public String save() throws Exception {
		if(SUCCESS.equals(this.getTarget())) {
			this.setTarget("successBozza");
		}

		WizardIscrizioneHelper iscrizioneHelper = (WizardIscrizioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);

		if (iscrizioneHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			iscrizioneHelper.setDataPresentazione( this.getDataPresentazione() );
			
			String target = saveDocumenti(
					iscrizioneHelper,
					this.session, 
					this);
	
			if(SUCCESS.equals(target)) {
				target = "successBozza";
				this.codice = iscrizioneHelper.getIdBando();
			} 
			
			this.setTarget(target);
		}
		
		return this.getTarget();
	}
	
	/**
	 * salva/aggiorna un allegato ed invia la relativa comunicazione FS2 o FS4 
	 */
	public static String saveDocumenti(
			WizardIscrizioneHelper helper,						
			Map<String, Object> session,
			BaseAction action) 
	{
		String target = SUCCESS;
		
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			action.addActionError(action.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente
			IEventManager eventManager = (IEventManager) ApsWebApplicationUtils
				.getBean(PortGareSystemConstants.EVENTI_MANAGER,
					ServletActionContext.getRequest());							
			
			Event evento = null;			
			try {
				String codice = helper.getIdBando();
				
				String tipoComunicazione = PortGareSystemConstants.RICHIESTA_ISCRIZIONE_ALBO;
				if(helper.isAggiornamentoIscrizione()) {
					tipoComunicazione = PortGareSystemConstants.RICHIESTA_AGGIORNAMENTO_ISCRIZIONE_ALBO;
				}				
				
				// recupera la tabella <codice descrizione> delle statizioni appaltanti
				WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO, ServletActionContext.getRequest());
				String xml = wrapper.getProxyWSGare().getElencoStazioniAppaltanti();
				LinkedHashMap<String, String> listaSA = InterceptorEncodedData.parseXml(xml);
			
				// memorizzo una data non significativa in quanto si tratta di bozza
				// (la data comunque serve in quanto il campo e' obbligatorio nel
				// tracciato xml da inviare)
				helper.setDataPresentazione(new Date());
				
				evento = new Event();
				evento.setUsername(action.getCurrentUser().getUsername());
				evento.setDestination(codice);
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.SALVATAGGIO_COMUNICAZIONE);
				evento.setIpAddress(action.getCurrentUser().getIpAddress());
				evento.setSessionId(action.getRequest().getSession().getId());
				evento.setMessage("Salvataggio comunicazione "
								  + tipoComunicazione
								  + " con id " + helper.getIdComunicazione()
								  + " in stato "
								  + CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);			
							
				sendComunicazione(
						helper,
						CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
						tipoComunicazione,
						listaSA,
						evento, 
						action);				
			}
			catch (XmlException e) {				
				ApsSystemUtils.logThrowable(e, action, "saveDocumenti");
				ExceptionUtils.manageExceptionError(e, action);
				target = "errorWS";
			} 
			catch (IOException e) {
				ApsSystemUtils.logThrowable(e, action, "saveDocumenti");
				action.addActionError(action.getText("Errors.cannotLoadAttachments"));
				target = "errorWS";
			}
			catch (OutOfMemoryError e) {
				evento.setError(e);
				ApsSystemUtils.logThrowable(e, action, "saveDocumenti");
				action.addActionError(action.getText("Errors.save.outOfMemory"));				
				target = INPUT;
			} 
			catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, action, "saveDocumenti");
				ExceptionUtils.manageExceptionError(e, action);
				target = "errorWS";
			}
			finally {
				if (evento != null) {
					eventManager.insertEvent(evento);
				}
			}
		}

		return target; 
	}
	
	/**
	 * Effettua l'invio della comunicazione al backoffice, componendola 
	 * opportunamente in caso fosse una richiesta di iscrizione o di 
	 * aggiornamento. 
	 * 
	 * @param iscrizioneHelper
	 *            contenitore dei dati inseriti nel wizard
	 * @param idComunicazione
	 * 			  id della comunicazione            
	 * @param stato
	 *            stato della comunicazione (1=bozza, 5=da processare, 9=da protocollare)
	 * @param tipoComunicazione
	 *            tipo di comunicazione (FS2 = richiesta iscrizione, FS4 = richiesta aggiornamento)
	 * @throws Throwable 
	 */
	private static ComunicazioneType sendComunicazione(
			WizardIscrizioneHelper iscrizioneHelper, 
			String stato, 
			String tipoComunicazione,
			LinkedHashMap<String, String> listaSA,
			Event evento,
			BaseAction action) 
		throws Throwable
	{
		ComunicazioneType comunicazione = null;

		//Preparazione delle informazioni da inviare
		String username        = action.getCurrentUser().getUsername();              
		String ragioneSociale  = iscrizioneHelper.getImpresa().getDatiPrincipaliImpresa().getRagioneSociale();  
		String descBando       = iscrizioneHelper.getDescBando();   
		String testo           = null;
		String oggetto         = null;
		String nomeFile        = null;
		String descrizioneFile = null;

		if(PortGareSystemConstants.RICHIESTA_ISCRIZIONE_ALBO.equals(tipoComunicazione)) {
			//Compongo i campi necesari per la richiesta di iscrizione
			TreeSet<String> stazioniAppaltanti = iscrizioneHelper
				.getStazioniAppaltanti();

			StringBuilder parametroSA = new StringBuilder("");
			if(listaSA != null) {
				int cont = 0;
				for (Iterator<String> iterator = stazioniAppaltanti.iterator(); iterator.hasNext();) {
					cont++;
					if (cont > 1) {
						if (cont < stazioniAppaltanti.size())
							parametroSA.append(", ");
						else
							parametroSA.append(" e ");
					}
					parametroSA.append(listaSA.get(iterator.next()));
				}
			}
			
			oggetto = MessageFormat.format(action.getI18nLabelFromDefaultLocale("NOTIFICA_ISCRIZIONE_OGGETTO"), 
					new Object[] { iscrizioneHelper.getIdBando() });

			testo = MessageFormat.format(action.getI18nLabelFromDefaultLocale("NOTIFICA_ISCRIZIONE_TESTO"), 
					new Object[] { StringUtils.left(ragioneSociale, 200),
								   StringUtils.left(descBando, 200),
								   parametroSA.toString() });

			descrizioneFile = action.getI18nLabelFromDefaultLocale("NOTIFICA_ISCRIZIONE_ALLEGATO_DESCRIZIONE"); 
			nomeFile = PortGareSystemConstants.NOME_FILE_ISCRIZIONE;
			
		} else {
			// Compongo i campi necessari per la richiesta di aggiornamento
			oggetto = MessageFormat.format(action.getI18nLabelFromDefaultLocale("NOTIFICA_AGGISCRIZIONE_OGGETTO"), 
					new Object[] { iscrizioneHelper.getIdBando() });
			
			testo = MessageFormat.format(action.getI18nLabelFromDefaultLocale("NOTIFICA_AGGISCRIZIONE_TESTO"), 
					new Object[] { StringUtils.left(ragioneSociale, 200),
								   StringUtils.left(descBando, 200) });

			descrizioneFile = action.getI18nLabelFromDefaultLocale("NOTIFICA_AGGISCRIZIONE_ALLEGATO_DESCRIZIONE");
			nomeFile = PortGareSystemConstants.NOME_FILE_AGG_ISCRIZIONE;			
		} 

		// Invio della comunicazione
		try {
			IComunicazioniManager comunicazioniManager = (IComunicazioniManager) ApsWebApplicationUtils
				.getBean(PortGareSystemConstants.COMUNICAZIONI_MANAGER,
						 ServletActionContext.getRequest());
			
			// Verifica l'esistenza di una comunicazione preesistente 
			// in stato BOZZA...
			// l'helper al primo accesso del wizard per un elenco non è ancora
			// stato inizializzato per l'attributo idComunicazione, questo perchè   
			// l'inizializzazione viene fatta dopo l'invio della comunicazione 
			// alla FASE 4! 
			DettaglioComunicazioneType criteri = new DettaglioComunicazioneType();
			criteri.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
			criteri.setTipoComunicazione(tipoComunicazione);
			criteri.setStato(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
			criteri.setChiave1(username);
			criteri.setChiave2(iscrizioneHelper.getIdBando());
			
			List<DettaglioComunicazioneType> comunicazioni = comunicazioniManager
				.getElencoComunicazioni(criteri);
			
			Long idComunicazione = null;
			if(comunicazioni != null && comunicazioni.size() == 1) {
				idComunicazione = comunicazioni.get(0).getId();
			} 		
			synchronized (iscrizioneHelper) {
				
				// FASE 1: costruzione del contenitore della comunicazione
				comunicazione = new ComunicazioneType();
	
				// FASE 2: popolamento della testata
				DettaglioComunicazioneType dettaglioComunicazione = ComunicazioniUtilities
					.createDettaglioComunicazione(
							idComunicazione,
							username,
							iscrizioneHelper.getIdBando(), 
							null,
							ragioneSociale, 
							stato, 
							oggetto, 
							testo,
							tipoComunicazione, 
							null);
				comunicazione.setDettaglioComunicazione(dettaglioComunicazione);
	
				// FASE 3: popolamento dell'allegato con l'xml dei dati da inviare
				setAllegatoComunicazione(
						comunicazione,
						iscrizioneHelper,	//iscrizioneHelper.getXmlDocument(true),
						nomeFile, 
						descrizioneFile);
				
				// FASE 4: invio comunicazione
				idComunicazione = comunicazioniManager.sendComunicazione(comunicazione);
				comunicazione.getDettaglioComunicazione().setId(idComunicazione);
//				// 06/11/2015: commentata la riga perche' in caso di protocollazione
//				// con errori, venendo aggiornato il dato, il successivo invio
//				// riusava lo stesso idcomunicazione, ma se nel frattempo qualcuno
//				// inseriva la comunicazione con lo stesso id questa veniva persa in
//				// favore della successiva riesecuzione del sendComunicazione sopra!
//				//			iscrizioneHelper.setIdComunicazione(comunicazione.getDettaglioComunicazione().getId());
				// 23/07/2019:
				// il bug della nota 06/11/2015 viene corretto dal porting alla versione 2.0.0 
				// e dalla modifica al metodo sendUpdate() che non elimina più la comunicazione 
				// in caso di errore, ma la annulla rimettendola in stato BOZZA
				if(iscrizioneHelper.getIdComunicazione() == null) {
					iscrizioneHelper.setIdComunicazione(idComunicazione);
				}
			}
			
			// visto l'esito con successo si arricchisce il messaggio di dettagli
			String messageAggiornato = "Invio comunicazione "
				+ tipoComunicazione + " con id " + comunicazione.getDettaglioComunicazione().getId() 
				+ " in stato " + comunicazione.getDettaglioComunicazione().getStato();
			if (!CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA.equals(stato)) {
				messageAggiornato += " e timestamp ntp "
						+ UtilityDate.convertiData(
								iscrizioneHelper.getDataPresentazione(),
								UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
			}
			evento.setMessage(messageAggiornato);
			
			// dopo aver inviato la comunicazione marca i documenti come elaborati
			// resettanto il flag "modificato"
			iscrizioneHelper.getDocumenti().resetStatiInvio();
			
		} catch (IOException e) {
			evento.setError(e);
			throw e;
		} catch (Throwable e) {
			evento.setError(e);
			throw e;
		}
		return comunicazione;
	}
		
	/**
	 * ... 
	 */
	public String send() throws Exception {

		WizardIscrizioneHelper iscrizioneHelper = (WizardIscrizioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
		
		if (iscrizioneHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {
			// per prima cosa si estrae la data NTP dell'istante in cui avviene la richiesta
			String ntpError = null;
			String nomeOperazioneLog = this.getI18nLabelFromDefaultLocale("BUTTON_ISCRALBO_RICHIESTA_ISCRIZIONE").toLowerCase();
			String nomeOperazione = this.getI18nLabel("BUTTON_ISCRALBO_RICHIESTA_ISCRIZIONE").toLowerCase();
			try {
				this.dataPresentazione = ntpManager.getNtpDate();
				iscrizioneHelper.setDataPresentazione(this.dataPresentazione);
			} catch (SocketTimeoutException e) {
				ApsSystemUtils.logThrowable(e, this, "send",
						this.getTextFromDefaultLocale(
								"Errors.ntpTimeout", nomeOperazioneLog));
				ntpError = this.getText("Errors.ntpTimeout", new String[] { nomeOperazione });
			} catch (UnknownHostException e) {
				ApsSystemUtils.logThrowable(e, this, "send",
						this.getTextFromDefaultLocale(
								"Errors.ntpUnknownHost", nomeOperazioneLog));
				ntpError = this.getText("Errors.ntpUnknownHost", new String[] { nomeOperazione });
			} catch (ApsSystemException e) {
				ApsSystemUtils.logThrowable(e, this, "send",
						this.getTextFromDefaultLocale(
								"Errors.ntpUnexpectedError", nomeOperazioneLog));
				ntpError = this.getText("Errors.ntpUnexpectedError", new String[] { nomeOperazione });
			}

			this.tipoProtocollazione = (Integer) this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_TIPO);
			if (this.tipoProtocollazione == null) {
				this.tipoProtocollazione = new Integer(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA);
			}
			this.mailUfficioProtocollo = (String) this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_MAIL_DESTINATARI);
			this.allegaDocMailUfficioProtocollo = (Boolean) this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_MAIL_ALLEGA_FILE);
			this.stazioneAppaltanteProtocollante = (String) this.appParamManager.getConfigurationValue(AppParamManager.STAZIONE_APPALTANTE_PROTOCOLLANTE);

			this.codice = iscrizioneHelper.getIdBando();
			DettaglioBandoIscrizioneType bando = this.bandiManager.getDettaglioBandoIscrizione(this.codice);
			String stazioneAppaltanteProcedura = bando.getStazioneAppaltante().getCodice();
			this.appParamManager.setStazioneAppaltanteProtocollazione(stazioneAppaltanteProcedura);
			
			if(!this.appParamManager.isConfigStazioneAppaltantePresente()) {
				// se la configurazione WSDM della stazione appaltante 
				// non e' presente, resetta il tipo protocollazione
				this.tipoProtocollazione = new Integer(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA);
			}
			
			if (!SaveWizardIscrizioneAction.isProtocollazionePrevista(
					this.tipoProtocollazione, 
					this.stazioneAppaltanteProtocollante, 
					stazioneAppaltanteProcedura)) {
				// se la protocollazione non e' prevista, si resetta il tipo protocollazione cosi' in seguito il codice testa su un'unica condizione
				this.tipoProtocollazione = new Integer(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA);
			}
			
			this.username = this.getCurrentUser().getUsername();
			String prefissoLabel = "NOTIFICA_ISCRIZIONE";
			boolean controlliOk = true;
			boolean inviataComunicazione = false;
			boolean protocollazioneOk = true;
			ComunicazioneType nuovaComunicazione = null;
			Event evento = null;

			// FASE 1: invio della comunicazione

			if(controlliOk) {
				if (iscrizioneHelper.getStazioniAppaltanti().isEmpty()
					|| (!iscrizioneHelper.isCategorieAssenti() && iscrizioneHelper.getCategorie().getCategorieSelezionate().size() == 0)) {
					this.addActionError(this.getText("Errors.checkDatiObbligatori"));
					controlliOk = false;
					this.setTarget(INPUT);
				}
			}

			// per prima cosa si effettuano i controlli legati
			// all'invio della domanda ufficiale: premesso che i
			// dati di impresa, stazione appaltante e categorie sono
			// garantiti dall'interfaccia, resta da verificare che
			// tutti i documenti richiesti ed obbligatori siano
			// presenti nella domanda
			if(controlliOk) {
				List<DocumentazioneRichiestaType> documentiRichiesti = this.bandiManager
						.getDocumentiRichiestiBandoIscrizione(
								iscrizioneHelper.getIdBando(),
								iscrizioneHelper.getImpresa().getDatiPrincipaliImpresa().getTipoImpresa(),
								iscrizioneHelper.isRti());
				
				for (int i = 0; i < documentiRichiesti.size(); i++) {
					if (documentiRichiesti.get(i).isObbligatorio()
						&& !iscrizioneHelper
							.getDocumenti().getDocRichiestiId().contains(
									documentiRichiesti.get(i).getId())) {
						controlliOk = false;
						this.setTarget(INPUT);
						this.addActionError(this.getText(
								"Errors.docRichiestoObbligatorioNotFound", 
								new String[] { documentiRichiesti.get(i).getNome() }));
					}
				}
			}

			if(controlliOk) {
				// WE535: si controlla nuovamente prima dell'invio
				// la lunghezza del nome file in modo da consentire
				// la memorizzazione nel campo previsto nel
				// backoffice, perche' potrebbe esserci un
				// salvataggio in bozza con un nome che sfora la
				// dimensione massima effettuato prima
				// dell'inserimento del controllo in fase di upload
				//for (String nomeFile : iscrizioneHelper.getDocumenti().getDocRichiestiFileName()) {
				for (int i = 0; i < iscrizioneHelper.getDocumenti().getDocRichiestiFileName().size() && controlliOk; i++) {
					String nomeFile = iscrizioneHelper.getDocumenti().getDocRichiestiFileName().get(i);
					if (nomeFile.length() > FileUploadUtilities.MAX_LUNGHEZZA_NOME_FILE) {
						controlliOk = false;
						this.setTarget(INPUT);
						this.addActionError(this.getText(
								"Errors.docRichiestoOverflowFileNameLength",
								new String[] { nomeFile }));
					}
				}
				//for (String nomeFile : iscrizioneHelper.getDocumenti().getDocUlterioriFileName() ) {
				for (int i = 0; i < iscrizioneHelper.getDocumenti().getDocUlterioriFileName().size() && controlliOk; i++) {					
					String nomeFile = iscrizioneHelper.getDocumenti().getDocUlterioriFileName().get(i);				
					if (nomeFile.length() > FileUploadUtilities.MAX_LUNGHEZZA_NOME_FILE) {
						controlliOk = false;
						this.setTarget(INPUT);
						this.addActionError(this.getText(
								"Errors.docUlterioreOverflowFileNameLength",
								 new String[] { nomeFile }));
					}
				}
			}

			// se i controlli sono andati a buon fine, si controlla se e' stata 
			// correttamente letta la data NTP, ed in caso negativo si esce con errore
			if (controlliOk) {
				if (this.dataPresentazione == null) {
					this.addActionError(ntpError);
					this.setTarget(INPUT);
					controlliOk = false;
				} else  {
					// se i controlli sono andati a buon fine e ho la data e posso
					// proseguire con il controllo della data e se sono dentro i
					// termini proseguo con l'invio
					evento = new Event();
					evento.setUsername(this.getCurrentUser().getUsername());
					evento.setDestination(this.codice);
					evento.setLevel(Event.Level.INFO);
					if (this.tipoProtocollazione.intValue() == PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA) {
						evento.setEventType(PortGareEventsConstants.INVIO_COMUNICAZIONE_DA_PROCESSARE);
					} else {
						evento.setEventType(PortGareEventsConstants.INVIO_COMUNICAZIONE_DA_PROTOCOLLARE);
					}
					evento.setIpAddress(this.getCurrentUser().getIpAddress());
					evento.setSessionId(this.getRequest().getSession().getId());
					evento.setMessage("Invio comunicazione " + PortGareSystemConstants.RICHIESTA_ISCRIZIONE_ALBO + " con id " + iscrizioneHelper.getIdComunicazione() + " e timestamp ntp " + iscrizioneHelper.getDataPresentazione());
					
					if (iscrizioneHelper.getDataScadenza() != null
						&& this.dataPresentazione.compareTo(iscrizioneHelper.getDataScadenza()) > 0) 
					{
						this.addActionError(this.getText("Errors.richiestaFuoriTempoMassimo"));
						this.setTarget(CommonSystemConstants.PORTAL_ERROR);
						evento.setLevel(Event.Level.ERROR);
						evento.setDetailMessage(PortGareEventsConstants.DETAIL_MESSAGE_ERRORE_FUORI_TEMPO_MASSIMO + " (" + UtilityDate.convertiData(this.dataPresentazione, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS) + ")");
						controlliOk = false;
						// si pulisce la sessione visto che termina il processo
						this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
						this.session.remove(PortGareSystemConstants.SESSION_ID_PAGINA);
					}
				}
			}
			
			try{
				if (controlliOk) {
					String statoComunicazione = (this.tipoProtocollazione.intValue() == PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA
							? CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE
							: CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROTOCOLLARE);

					nuovaComunicazione = sendComunicazione(
							iscrizioneHelper,
							statoComunicazione,
							PortGareSystemConstants.RICHIESTA_ISCRIZIONE_ALBO,
							this.getMaps().get(""),
							evento, 
							this);
					
					inviataComunicazione = true;
					this.setTarget("successIscrizione");
				}
			} catch (IOException t) {
				ApsSystemUtils.logThrowable(t, this, "send");
				this.addActionError(this.getText("Errors.cannotLoadAttachments"));
				this.setTarget(INPUT);
			} catch (OutOfMemoryError e) {
				ApsSystemUtils.logThrowable(e, this, "send");
				this.addActionError(this.getText("Errors.send.outOfMemory"));
				evento.setError(e);
				this.setTarget(INPUT);
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "send");
				ExceptionUtils.manageExceptionError(e, this);
				this.setTarget(INPUT);
			} finally {
				if (evento != null) {
					this.eventManager.insertEvent(evento);
				}
			}

			String tipoIscrizioneDefaultLocale = iscrizioneHelper.getTipologia() == PortGareSystemConstants.TIPOLOGIA_ELENCO_STANDARD 
				? this.getI18nLabelFromDefaultLocale("LABEL_ALL_ELENCO") 
				: this.getI18nLabelFromDefaultLocale("LABEL_AL_MERCATO_ELETTRONICO");
			String tipoIscrizione = iscrizioneHelper.getTipologia() == PortGareSystemConstants.TIPOLOGIA_ELENCO_STANDARD 
				? this.getI18nLabel("LABEL_ALL_ELENCO") 
				: this.getI18nLabel("LABEL_AL_MERCATO_ELETTRONICO");

			// FASE 2: ove previsto, si invia alla protocollazione.		
			if (inviataComunicazione) {
				evento = new Event();
				evento.setUsername(this.getCurrentUser().getUsername());
				evento.setDestination(this.codice);
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.PROTOCOLLAZIONE);
				evento.setIpAddress(this.getCurrentUser().getIpAddress());
				evento.setSessionId(this.getRequest().getSession().getId());
				
				switch (this.tipoProtocollazione) {

				case PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_MAIL:
					evento.setMessage("Protocollazione via mail a " + this.mailUfficioProtocollo);

					boolean mailProtocollazioneInviata = false;

					try {
						// si invia la richiesta di protocollazione via mail
						this.setMailRichiestaUfficioProtocollo(
								iscrizioneHelper, 
								PortGareSystemConstants.RICHIESTA_ISCRIZIONE_ALBO,
								tipoIscrizioneDefaultLocale);
						mailProtocollazioneInviata = true;
						this.eventManager.insertEvent(evento);

						// si aggiorna lo stato a 5
						evento.setEventType(PortGareEventsConstants.CAMBIO_STATO_COMUNICAZIONE);
						evento.setMessage("Aggiornamento comunicazione con id " + nuovaComunicazione.getDettaglioComunicazione().getId() 
								          + " allo stato " + CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
						this.comunicazioniManager.updateStatoComunicazioni(
								new DettaglioComunicazioneType[] {nuovaComunicazione.getDettaglioComunicazione()}, 
								CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
						this.eventManager.insertEvent(evento);
						this.setDataProtocollo(UtilityDate.convertiData(
								this.dataPresentazione, 
								UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
					} catch (Throwable t) {
						if (mailProtocollazioneInviata) {
							evento.setError(t);
							this.eventManager.insertEvent(evento);

							// segnalo l'errore, comunque considero la
							// protocollazione andata a buon fine e segnalo nel
							// log e a video che va aggiornato a mano lo stato
							// della comunicazione
							this.msgErrore = this.getText(
									"Errors.updateStatoComunicazioneDaProcessare",
									new String[]{nuovaComunicazione.getDettaglioComunicazione().getId().toString()});
							ApsSystemUtils.logThrowable(t, this, "send", this.getTextFromDefaultLocale(
									"Errors.updateStatoComunicazioneDaProcessare",
									nuovaComunicazione.getDettaglioComunicazione().getId().toString()));
							this.addActionError(this.msgErrore);

							evento.setEventType(PortGareEventsConstants.COMPLETAMENTO_MANUALE_PROTOCOLLAZIONE_COMUNICAZIONE);
							evento.setLevel(Event.Level.WARNING);
							evento.setMessage("Aggiornare manualmente la comunicazione con id " + nuovaComunicazione.getDettaglioComunicazione().getId());
							this.eventManager.insertEvent(evento);
							this.setDataProtocollo(UtilityDate.convertiData(
									this.dataPresentazione, 
									UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
						} else {
							evento.setError(t);
							this.eventManager.insertEvent(evento);

							ApsSystemUtils.logThrowable(t, this, "send", "Per errori durante la connessione al server di posta, non e' stato possibile inviare la richiesta all'ufficio protocollo");
							ExceptionUtils.manageExceptionError(t, this);
							this.setTarget(INPUT);
							// si deve annullare l'invio che si e' tentato di protocollare
							this.annullaComunicazioneInviata(
									nuovaComunicazione, 
									iscrizioneHelper.getDescBando());
							protocollazioneOk = false;
						}
					}
					break;

				case PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_WSDM:
					evento.setMessage("Protocollazione via WSDM");

					boolean protocollazioneInoltrata = false;
					WSDocumentoType documento = null; 

					try {
						WizardDatiImpresaHelper datiImpresaHelper = iscrizioneHelper.getImpresa();

						FascicoloProtocolloType fascicoloBackOffice = this.bandiManager.getFascicoloProtocollo(this.codice);
						WSDMLoginAttrType loginAttr = this.wsdmManager.getLoginAttr();
						if (fascicoloBackOffice != null && fascicoloBackOffice.getCodiceAoo() != null) {
							// nel caso di protocollazione Titulus il codiceAoo
							// risulta valorizzato nel fascicolo a deve essere
							// letto per essere usato in protocollazione in
							// ingresso
							loginAttr.getLoginTitAttr().setCodiceAmministrazioneAoo(fascicoloBackOffice.getCodiceAoo());
						}
						if (fascicoloBackOffice != null && fascicoloBackOffice.getCodiceUfficio() != null) {
							// nel caso di protocollazione Titulus il codiceUfficio
							// risulta valorizzato nel fascicolo a deve essere
							// letto per essere usato in protocollazione in
							// ingresso
							loginAttr.getLoginTitAttr().setCodiceUfficio(fascicoloBackOffice.getCodiceUfficio());
						}

						WSDMProtocolloDocumentoInType wsdmProtocolloDocumentoIn = creaInputProtocollazioneWSDM(
								iscrizioneHelper,
								datiImpresaHelper,
								prefissoLabel,
								PortGareSystemConstants.RICHIESTA_ISCRIZIONE_ALBO,
								fascicoloBackOffice, 
								nuovaComunicazione, 
								bando);

						WSDMProtocolloDocumentoType ris = this.wsdmManager.inserisciProtocollo(loginAttr, wsdmProtocolloDocumentoIn);
						this.annoProtocollo = ris.getAnnoProtocollo();
						this.numeroProtocollo = ris.getNumeroProtocollo();
						if (ris.getDataProtocollo() != null) {
							this.setDataProtocollo(UtilityDate.convertiData(
									ris.getDataProtocollo().getTime(), 
									UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
						}
						protocollazioneInoltrata = true;
						this.eventManager.insertEvent(evento);

						// si aggiorna lo stato a 5 aggiornando inoltre anche i dati di protocollazione
						long id = nuovaComunicazione.getDettaglioComunicazione().getId();

						documento = new WSDocumentoType();
						documento.setEntita("GARE");
						documento.setChiave1(iscrizioneHelper.getIdBando());
						documento.setNumeroDocumento(ris.getNumeroDocumento());
						documento.setAnnoProtocollo(ris.getAnnoProtocollo());
						documento.setNumeroProtocollo(ris.getNumeroProtocollo());
						documento.setVerso(WSDocumentoTypeVerso.IN);
						documento.setOggetto(wsdmProtocolloDocumentoIn.getOggetto());

						evento.setEventType(PortGareEventsConstants.PROTOCOLLA_COMUNICAZIONE_DA_PROCESSARE);
						evento.setMessage("Aggiornamento comunicazione con id "
								+ nuovaComunicazione
								.getDettaglioComunicazione().getId()
								+ " allo stato "
								+ CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE
								+ ", protocollata con anno "
								+ ris.getAnnoProtocollo() + " e numero "
								+ ris.getNumeroProtocollo());
						
						this.comunicazioniManager.protocollaComunicazione(
								CommonSystemConstants.ID_APPLICATIVO,
								id,
								ris.getNumeroProtocollo(),
								(ris.getDataProtocollo() != null ? ris.getDataProtocollo().getTime() : this.dataPresentazione),
								CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE,
								documento);
												
						this.eventManager.insertEvent(evento);
					} catch (Throwable t) {
						if (protocollazioneInoltrata) {
							evento.setError(t);
							this.eventManager.insertEvent(evento);

							// segnalo l'errore, comunque considero la
							// protocollazione andata a buon fine e segnalo nel
							// log e a video che va aggiornato a mano lo stato
							// della comunicazione
							msgErrore = this.getText(
									"Errors.updateStatoComunicazioneDaProcessare",
									new String[] {nuovaComunicazione.getDettaglioComunicazione().getId().toString()});
							ApsSystemUtils.logThrowable(t, this, "send", this.getTextFromDefaultLocale(
									"Errors.updateStatoComunicazioneDaProcessare",
									nuovaComunicazione.getDettaglioComunicazione().getId().toString()));
							this.addActionError(this.msgErrore);

							evento.setEventType(PortGareEventsConstants.COMPLETAMENTO_MANUALE_PROTOCOLLAZIONE_COMUNICAZIONE);
							evento.setLevel(Event.Level.WARNING);
							evento.setMessage("Aggiornare manualmente la comunicazione con id "
									+ nuovaComunicazione
									.getDettaglioComunicazione()
									.getId()
									+ " ed inserire inoltre un documento in ingresso per entità "
									+ documento.getEntita()
									+ ", chiave1 "
									+ documento.getChiave1()
									+ ", oggetto "
									+ documento.getOggetto()
									+ ", numero documento "
									+ documento.getNumeroDocumento()
									+ ", anno protocollo "
									+ documento.getAnnoProtocollo()
									+ " e numero protocollo "
									+ documento.getNumeroProtocollo());
							this.eventManager.insertEvent(evento);
						} else {
							evento.setError(t);
							this.eventManager.insertEvent(evento);

							ApsSystemUtils.logThrowable(t, this, "send", "Per errori durante la connessione al servizio WSDM, non e' stato possibile inviare la richiesta di protocollazione");
							ExceptionUtils.manageExceptionError(t, this);
							this.setTarget(INPUT);
							
							// si deve annullare l'invio che si e' tentato di protocollare
							this.annullaComunicazioneInviata(
									nuovaComunicazione, 
									iscrizioneHelper.getDescBando());
							protocollazioneOk = false;
						}
					}
					break;
				default:
					break;
				}
			}

			// FASE 3: se gli step precedenti sono andati a buon fine, si invia la ricevuta all'impresa
			if (inviataComunicazione && protocollazioneOk) {
				evento = new Event();
				evento.setUsername(this.getCurrentUser().getUsername());
				evento.setDestination(this.codice);
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.INVIO_MAIL_CONFERMA_RICEVUTA);
				evento.setMessage("Invio mail ricevuta di conferma trasmissione comunicazione " + nuovaComunicazione.getDettaglioComunicazione().getTipoComunicazione() 
								  + " a " + (String) ((IUserProfile) this.getCurrentUser().getProfile()).getValue("email"));
				evento.setIpAddress(this.getCurrentUser().getIpAddress());
				evento.setSessionId(this.getRequest().getSession().getId());

				try {
					this.sendMailConfermaImpresa(tipoIscrizione,iscrizioneHelper);
				} catch (ApsException t) {
					ApsSystemUtils.getLogger().error(
							"Per errori durante la connessione al server di posta, non e' stato possibile inviare all'impresa {} la ricevuta della richiesta di iscrizione per {}.",
							new Object[] { this.getCurrentUser().getUsername(), iscrizioneHelper });
					this.msgErrore = this.getText("Errors.sendMailError");
					ApsSystemUtils.logThrowable(t, this, "send");
					evento.setError(t);
				} finally {
					this.eventManager.insertEvent(evento);
				}
			}

			// FASE 4: se invio e protocollazione sono andate a buon fine, anche se la ricevuta
			// all'impresa non e' stata inviata, si procede con la pulizia della sessione
			if (inviataComunicazione && protocollazioneOk) {
				// pulizia e impostazione navigazione futura
				// se tutto e' andato a buon fine si eliminano
				// le informazioni dalla sessione ...
				this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
				this.session.remove(PortGareSystemConstants.SESSION_ID_PAGINA);
				this.tipologia = iscrizioneHelper.getTipologia();
			}
			
			// concludi la protocollazione
			this.appParamManager.setStazioneAppaltanteProtocollazione(null);
		}

		return this.getTarget();
	}

	/**
	 * Verifica se la protocollazione va attivata.
	 * 
	 * @param tipoProtocollazione
	 *            tipologia di protocollazione (2=WSDM, 1=Mail, 0=non prevista)
	 * @param stazioneAppaltanteProtocollante
	 *            eventuale unica stazione appaltante protocollante, da
	 *            configurazione
	 * @param stazioneAppaltanteProcedura
	 *            stazione appaltante della procedura per cui si richiede la
	 *            protocollazione
	 * @return true se la protocollazione va applicata, false altrimenti
	 */
	public static boolean isProtocollazionePrevista(
			int tipoProtocollazione, 
			String stazioneAppaltanteProtocollante, 
			String stazioneAppaltanteProcedura) 
	{
		return (PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA!= tipoProtocollazione) &&
			   				( (StringUtils.stripToNull(stazioneAppaltanteProtocollante)==null)
			   				   || (stazioneAppaltanteProtocollante.equals(stazioneAppaltanteProcedura)));
	}

	/**
	 * ... 
	 */
	private void sendMailConfermaImpresa(
			String tipoIscrizione, 
			WizardIscrizioneHelper iscrizioneHelper)
		throws ApsSystemException 
	{
		String text = null;
		String subject = null;
		String ragioneSociale = iscrizioneHelper.getImpresa().getDatiPrincipaliImpresa().getRagioneSociale();
		String descBando = iscrizioneHelper.getDescBando();
		String data = UtilityDate.convertiData(
				iscrizioneHelper.getDataPresentazione(),
				UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
		String destinatario = (String)((IUserProfile) this.getCurrentUser().getProfile()).getValue("email");

		if (!iscrizioneHelper.isAggiornamentoIscrizione()) {
			subject = MessageFormat.format(this.getI18nLabel("MAIL_ISCRIZIONE_RICEVUTA_OGGETTO"), 
					new Object[] { tipoIscrizione, iscrizioneHelper.getDescBando(), iscrizioneHelper.getIdBando() });
			
			text = MessageFormat.format(this.getI18nLabel("MAIL_ISCRIZIONE_RICEVUTA_TESTO"),
					new Object[] {ragioneSociale, descBando, data });
			if (this.isPresentiDatiProtocollazione()) {
				text = MessageFormat.format(this.getI18nLabel("MAIL_ISCRIZIONE_RICEVUTA_TESTOCONPROTOCOLLO"),
						new Object[] { ragioneSociale, this.codice, data, this.annoProtocollo.toString(), this.numeroProtocollo });
			}
		} else {
			subject = MessageFormat.format(this.getI18nLabel("MAIL_AGGISCRIZIONE_RICEVUTA_OGGETTO"), 
					new Object[] { tipoIscrizione, iscrizioneHelper.getDescBando(), iscrizioneHelper.getIdBando() });
			
			text = MessageFormat.format(this.getI18nLabel("MAIL_AGGISCRIZIONE_RICEVUTA_TESTO"), 
					new Object[] { ragioneSociale, descBando, data });
			if (this.isPresentiDatiProtocollazione()) {
				text = MessageFormat.format(this.getI18nLabel("MAIL_AGGISCRIZIONE_RICEVUTA_TESTOCONPROTOCOLLO"), 
						new Object[] {ragioneSociale, this.codice, data, this.annoProtocollo.toString(), this.numeroProtocollo });
			}
		}

		this.mailManager.sendMail(
				text, 
				subject, 
				new String[] { destinatario },
				null, 
				null, 
				CommonSystemConstants.SENDER_CODE);
	}

	/**
	 * ... 
	 */
	private void setMailRichiestaUfficioProtocollo(
			WizardIscrizioneHelper iscrizioneHelper, 
			String tipoComunicazione, 
			String tipoIscrizione) 
		throws ApsSystemException 
	{
		if (1 == this.tipoProtocollazione) {
			if (StringUtils.isBlank(this.mailUfficioProtocollo)) {
				throw new ApsSystemException("Valorizzare la configurazione " + AppParamManager.PROTOCOLLAZIONE_MAIL_DESTINATARI);
			}
			// nel caso di email dell'ufficio protocollo, si
			// procede con l'invio della notifica a tale ufficio

			Properties p = null;

			// solo se indicato da configurazione si allegano i doc inseriti
			// nella domanda
			if (this.allegaDocMailUfficioProtocollo) {
				// si predispongono gli allegati da inserire
				if (iscrizioneHelper.getDocumenti().getDocRichiesti().size()
					+ iscrizioneHelper.getDocumenti().getDocUlteriori().size() > 0) {
					p = new Properties();
					for (int i = 0; i < iscrizioneHelper.getDocumenti().getDocRichiesti().size(); i++) {
						p.put(iscrizioneHelper.getDocumenti().getDocRichiestiFileName().get(i),
							  iscrizioneHelper.getDocumenti().getDocRichiesti().get(i).getAbsolutePath());
					}
					for (int i = 0; i < iscrizioneHelper.getDocumenti().getDocUlteriori().size(); i++) {
						if (!iscrizioneHelper.getDocumenti().getDocUlterioriNascosti()
							 .contains(iscrizioneHelper.getDocumenti().getDocUlterioriDesc().get(i))) {
							p.put(iscrizioneHelper.getDocumenti().getDocUlterioriFileName().get(i),
								  iscrizioneHelper.getDocumenti().getDocUlteriori().get(i).getAbsolutePath());
						}
					}
				}
			}

			String text = null;
			String subject = null;
			String ragioneSociale = iscrizioneHelper.getImpresa().getDatiPrincipaliImpresa().getRagioneSociale();
			String codFiscale = iscrizioneHelper.getImpresa().getDatiPrincipaliImpresa().getCodiceFiscale();
			String partitaIVA = iscrizioneHelper.getImpresa().getDatiPrincipaliImpresa().getPartitaIVA();
			String mail = (String) ((IUserProfile) this.getCurrentUser().getProfile()).getValue("email");
			String sede = iscrizioneHelper.getImpresa()
					.getDatiPrincipaliImpresa().getIndirizzoSedeLegale()
					+ " "
					+ iscrizioneHelper.getImpresa().getDatiPrincipaliImpresa()
					.getNumCivicoSedeLegale()
					+ ", "
					+ iscrizioneHelper.getImpresa().getDatiPrincipaliImpresa()
					.getCapSedeLegale()
					+ " "
					+ iscrizioneHelper.getImpresa().getDatiPrincipaliImpresa()
					.getComuneSedeLegale()
					+ " ("
					+ iscrizioneHelper.getImpresa().getDatiPrincipaliImpresa()
					.getProvinciaSedeLegale() + ")";
			String descBando = iscrizioneHelper.getDescBando();
			String data = UtilityDate.convertiData(
					iscrizioneHelper.getDataPresentazione(),
					UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
			String[] destinatari = this.mailUfficioProtocollo.split(",");

			if (!iscrizioneHelper.isAggiornamentoIscrizione()) {
				subject = MessageFormat.format(this.getI18nLabelFromDefaultLocale("MAIL_ISCRIZIONE_RICEVUTA_OGGETTO"),
						new Object[] {tipoIscrizione, iscrizioneHelper.getDescBando(), iscrizioneHelper.getIdBando() });
				if (this.allegaDocMailUfficioProtocollo && p != null) {
					text = MessageFormat.format(this.getI18nLabelFromDefaultLocale("MAIL_ISCRIZIONE_PROTOCOLLO_TESTOCONALLEGATI"),
							new Object[] { ragioneSociale, codFiscale, partitaIVA, mail, sede, data, descBando });
				} else {
					text = MessageFormat.format(this.getI18nLabelFromDefaultLocale("MAIL_ISCRIZIONE_PROTOCOLLO_TESTO"),
							new Object[] { ragioneSociale, codFiscale, partitaIVA, mail, sede, data, descBando });
				}
			} else {
				subject = MessageFormat.format(this.getI18nLabelFromDefaultLocale("MAIL_AGGISCRIZIONE_RICEVUTA_OGGETTO"),
						new Object[] { tipoIscrizione, iscrizioneHelper.getDescBando(), iscrizioneHelper.getIdBando() });
				if (this.allegaDocMailUfficioProtocollo && p != null) {
					text = MessageFormat.format(this.getI18nLabelFromDefaultLocale("MAIL_AGGISCRIZIONE_PROTOCOLLO_TESTOCONALLEGATI"),
							new Object[] { ragioneSociale, codFiscale, partitaIVA, mail, sede, data, descBando });
				} else {
					text = MessageFormat.format(this.getI18nLabelFromDefaultLocale("MAIL_AGGISCRIZIONE_PROTOCOLLO_TESTO"),
							new Object[] { ragioneSociale, codFiscale, partitaIVA, mail, sede, data, descBando });
				}
			}

			if (this.isPresentiDatiProtocollazione()) {
				if(PortGareSystemConstants.RICHIESTA_AGGIORNAMENTO_ISCRIZIONE_ALBO.equals(tipoComunicazione)){
					text = MessageFormat.format(this.getI18nLabelFromDefaultLocale("MAIL_AGGISCRIZIONE_RICEVUTA_TESTOCONPROTOCOLLO"), 
							new Object[]{ragioneSociale, descBando, data, this.getAnnoProtocollo().toString(), this.getNumeroProtocollo()});
				} else {
					text = MessageFormat.format(this.getI18nLabelFromDefaultLocale("MAIL_ISCRIZIONE_RICEVUTA_TESTOCONPROTOCOLLO"),
							new Object[] { ragioneSociale, descBando, data, this.getAnnoProtocollo().toString(), this.getNumeroProtocollo() });
				}
			}

			this.mailManager.sendMail(
					text, 
					subject,
					IMailManager.CONTENTTYPE_TEXT_PLAIN, 
					p, 
					destinatari, 
					null,
					null, 
					CommonSystemConstants.SENDER_CODE);
		}
	}

	/**
	 * 
	 */
	public String sendUpdate() throws ApsException {
		if (SUCCESS.equals(this.getTarget())) {
			this.setTarget("successUpdate");
		}

		WizardIscrizioneHelper iscrizioneHelper = (WizardIscrizioneHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);

		if (iscrizioneHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} else {

			// per prima cosa si estrae la data NTP dell'istante in cui avviene la richiesta
			String ntpError = null;
			String nomeOperazioneLog = this.getI18nLabelFromDefaultLocale("BUTTON_ISCRALBO_AGGIORNA_DATI_DOCUMENTI").toLowerCase();
			String nomeOperazione = this.getI18nLabel("BUTTON_ISCRALBO_AGGIORNA_DATI_DOCUMENTI").toLowerCase();
			try {
				this.dataPresentazione = ntpManager.getNtpDate();
				iscrizioneHelper.setDataPresentazione(this.dataPresentazione);
			} catch (SocketTimeoutException e) {
				ApsSystemUtils.logThrowable(e, this, "sendUpdate",
						this.getTextFromDefaultLocale(
								"Errors.ntpTimeout", nomeOperazioneLog));
				ntpError = this.getText("Errors.ntpTimeout", new String[] { nomeOperazione });
			} catch (UnknownHostException e) {
				ApsSystemUtils.logThrowable(e, this, "sendUpdate",
						this.getTextFromDefaultLocale(
								"Errors.ntpUnknownHost", nomeOperazioneLog));
				ntpError = this.getText("Errors.ntpUnknownHost", new String[] { nomeOperazione });
			} catch (ApsSystemException e) {
				ApsSystemUtils.logThrowable(e, this, "sendUpdate",
						this.getTextFromDefaultLocale(
								"Errors.ntpUnexpectedError", nomeOperazioneLog));
				ntpError = this.getText("Errors.ntpUnexpectedError", new String[] { nomeOperazione });
			}
			
			this.tipoProtocollazione = (Integer) this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_TIPO);
			if (this.tipoProtocollazione == null) {
				this.tipoProtocollazione = new Integer(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA);
			}

			this.mailUfficioProtocollo = (String)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_MAIL_DESTINATARI);
			this.allegaDocMailUfficioProtocollo = (Boolean)this.appParamManager.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_MAIL_ALLEGA_FILE);
			this.stazioneAppaltanteProtocollante = (String) this.appParamManager.getConfigurationValue(AppParamManager.STAZIONE_APPALTANTE_PROTOCOLLANTE);

			// la sessione non e' scaduta, per cui proseguo regolarmente
			this.codice = iscrizioneHelper.getIdBando();

			DettaglioBandoIscrizioneType bando = this.bandiManager.getDettaglioBandoIscrizione(this.codice);
			String stazioneAppaltanteProcedura = bando.getStazioneAppaltante().getCodice();
			this.appParamManager.setStazioneAppaltanteProtocollazione(stazioneAppaltanteProcedura);
			
			if(!this.appParamManager.isConfigStazioneAppaltantePresente()) {
				// se la configurazione WSDM della stazione appaltante 
				// non e' presente, resetta il tipo protocollazione
				this.tipoProtocollazione = new Integer(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA);
			}
			
			if (!SaveWizardIscrizioneAction.isProtocollazionePrevista(this.tipoProtocollazione, this.stazioneAppaltanteProtocollante, stazioneAppaltanteProcedura)) {
				// se la protocollazione non e' prevista, si resetta il tipo protocollazione cosi' in seguito il codice testa su un'unica condizione
				this.tipoProtocollazione = new Integer(PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA);
			}
			
			// valido i dati
			if (iscrizioneHelper.getStazioniAppaltanti().size() == 0
				|| (!iscrizioneHelper.isCategorieAssenti() && iscrizioneHelper.getCategorie().getCategorieSelezionate().size() == 0)) {
				this.addActionError(this.getText("Errors.checkDatiObbligatori"));
				this.setTarget(INPUT);
			} else {
				String prefissoLabel = "NOTIFICA_AGGISCRIZIONE";
				boolean controlliOk = true;
				boolean inviataComunicazione = false;
				boolean protocollazioneOk = true;
				ComunicazioneType nuovaComunicazione = null;
				Event evento = null;

				// FASE 1: invio della comunicazione

				if (iscrizioneHelper.getStazioniAppaltanti().isEmpty()
					|| (!iscrizioneHelper.isCategorieAssenti() && iscrizioneHelper.getCategorie().getCategorieSelezionate().size() == 0)) {
					this.addActionError(this.getText("Errors.checkDatiObbligatori"));
					controlliOk = false;
					this.setTarget(INPUT);
				}

				// WE535: si controlla nuovamente prima dell'invio
				// la lunghezza del nome file in modo da consentire
				// la memorizzazione nel campo previsto nel
				// backoffice, perche' potrebbe esserci un
				// salvataggio in bozza con un nome che sfora la
				// dimensione massima effettuato prima
				// dell'inserimento del controllo in fase di upload
				if(controlliOk){
					for (String nomeFile : iscrizioneHelper.getDocumenti().getDocUlterioriFileName()) {
						if (nomeFile.length() > FileUploadUtilities.MAX_LUNGHEZZA_NOME_FILE) {
							controlliOk = false;
							this.setTarget(INPUT);
							this.addActionError(this.getText(
									"Errors.docUlterioreOverflowFileNameLength",
									new String[] { nomeFile }));
						}
					}
				}

				// se i controlli sono andati a buon fine, si controlla se e' stata correttamente letta la data NTP, ed in caso negativo si esce con errore
				if (controlliOk) {
					if (this.dataPresentazione == null) {
						this.addActionError(ntpError);
						this.setTarget(INPUT);
						controlliOk = false;
					} else  {
						// se i controlli sono andati a buon fine e ho la data e posso
						// proseguire con il controllo della data e se sono dentro i
						// termini proseguo con l'invio
						evento = new Event();
						evento.setUsername(this.getCurrentUser().getUsername());
						evento.setDestination(this.codice);
						evento.setLevel(Event.Level.INFO);
						if (this.tipoProtocollazione.intValue() == PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA) {
							evento.setEventType(PortGareEventsConstants.INVIO_COMUNICAZIONE_DA_PROCESSARE);
						} else {
							evento.setEventType(PortGareEventsConstants.INVIO_COMUNICAZIONE_DA_PROTOCOLLARE);
						}
						evento.setIpAddress(this.getCurrentUser().getIpAddress());
						evento.setSessionId(this.getRequest().getSession().getId());
						evento.setMessage("Invio comunicazione " 
								+ PortGareSystemConstants.RICHIESTA_AGGIORNAMENTO_ISCRIZIONE_ALBO
								+ " con id " + iscrizioneHelper.getIdComunicazione()
								+ " e timestamp ntp " + iscrizioneHelper.getDataPresentazione());
						
						if (iscrizioneHelper.getDataScadenza() != null
							&& this.dataPresentazione.compareTo(iscrizioneHelper.getDataScadenza()) > 0) 
						{
							this.addActionError(this.getText("Errors.richiestaFuoriTempoMassimo"));
							this.setTarget(CommonSystemConstants.PORTAL_ERROR);
							evento.setLevel(Event.Level.ERROR);
							evento.setDetailMessage(PortGareEventsConstants.DETAIL_MESSAGE_ERRORE_FUORI_TEMPO_MASSIMO + " (" + UtilityDate.convertiData(this.dataPresentazione, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS) + ")");
							controlliOk = false;
							// si pulisce la sessione visto che termina il processo
							this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
							this.session.remove(PortGareSystemConstants.SESSION_ID_PAGINA);
						}
					}
				}

				try {
					if (controlliOk) {
						// estrazione dei parametri necessari per i dati di
						// testata della comunicazione

						if (iscrizioneHelper.getIdComunicazione() != null
							&& this.comunicazioniManager.isComunicazioneProcessata(
										CommonSystemConstants.ID_APPLICATIVO,
										iscrizioneHelper.getIdComunicazione().longValue())) {
							// si sbianca l'id comunicazione in quanto si deve
							// procedere con una nuova comunicazione dato che
							// quella che si deve aggiornare nel frattempo e' 
							// stata processata
							iscrizioneHelper.setIdComunicazione(null);

							// si eliminano i documenti riportati di nascosto
							// dalla precedente comunicazione in quanto, se
							// gia' processata, devo bloccarne il reinserimento
							WizardDocumentiHelper helperDoc = iscrizioneHelper.getDocumenti();
							for (int i = iscrizioneHelper.getDocumenti().getDocUlterioriDesc().size() - 1; i >= 0; i--) {
								if (helperDoc.getDocUlterioriNascosti()
										.contains(helperDoc.getDocUlterioriDesc().get(i))) 
								{
									//helperDoc.getDocUlterioriDesc().remove(i);
									//File file = helperDoc.getDocUlteriori().remove(i);
									//if (file.exists())
									//	file.delete();
									//helperDoc.getDocUlterioriContentType().remove(i);
									//helperDoc.getDocUlterioriFileName().remove(i);
									helperDoc.removeDocUlteriore(i);
								}
							}
						}
					}
					if(controlliOk) {
						String statoComunicazione = (this.tipoProtocollazione.intValue() == PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_NON_PREVISTA 
								? CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE
								: CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROTOCOLLARE);
						
						nuovaComunicazione = sendComunicazione(
								iscrizioneHelper,
								statoComunicazione,
								PortGareSystemConstants.RICHIESTA_AGGIORNAMENTO_ISCRIZIONE_ALBO,
								(LinkedHashMap<String, String>) this.getMaps().get(InterceptorEncodedData.LISTA_STAZIONI_APPALTANTI),
								evento,
								this);
						
						inviataComunicazione = true;
					}
				} catch (IOException t) {
					ApsSystemUtils.logThrowable(t, this, "sendUpdate");
					this.addActionError(this.getText("Errors.cannotLoadAttachments"));
					this.setTarget(INPUT);
				} catch (OutOfMemoryError e) {
					ApsSystemUtils.logThrowable(e, this, "sendUpdate");
					this.addActionError(this.getText("Errors.send.outOfMemory"));
					evento.setError(e);
					this.setTarget(INPUT);
				} catch (Throwable e) {
					ApsSystemUtils.logThrowable(e, this, "sendUpdate");
					ExceptionUtils.manageExceptionError(e, this);
					this.setTarget(INPUT);
				} finally {
					if (evento != null) {
						this.eventManager.insertEvent(evento);
					}
				}

				// FASE 2: ove previsto, si invia alla protocollazione
				if (inviataComunicazione) {
					evento = new Event();
					evento.setUsername(this.getCurrentUser().getUsername());
					evento.setDestination(this.codice);
					evento.setLevel(Event.Level.INFO);
					evento.setEventType(PortGareEventsConstants.PROTOCOLLAZIONE);
					evento.setIpAddress(this.getCurrentUser().getIpAddress());
					
					switch (this.tipoProtocollazione) {

					case PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_MAIL:
						evento.setMessage("Protocollazione via mail a " + this.mailUfficioProtocollo);

						boolean mailProtocollazioneInviata = false;
						try {
							String tipoIscrizioneDefaultLocale = iscrizioneHelper.getTipologia() == PortGareSystemConstants.TIPOLOGIA_ELENCO_STANDARD 
									? this.getI18nLabelFromDefaultLocale("LABEL_ALL_ELENCO") 
									: this.getI18nLabelFromDefaultLocale("LABEL_AL_MERCATO_ELETTRONICO");
							// si invia la richiesta di protocollazione via mail
							this.setMailRichiestaUfficioProtocollo(iscrizioneHelper,PortGareSystemConstants.RICHIESTA_AGGIORNAMENTO_ISCRIZIONE_ALBO,tipoIscrizioneDefaultLocale);
							mailProtocollazioneInviata = true;
							this.eventManager.insertEvent(evento);

							// si aggiorna lo stato a 5 (da processare)
							evento.setEventType(PortGareEventsConstants.CAMBIO_STATO_COMUNICAZIONE);
							evento.setMessage("Aggiornamento comunicazione con id " + nuovaComunicazione.getDettaglioComunicazione().getId() + " allo stato " + CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
							this.comunicazioniManager.updateStatoComunicazioni(new DettaglioComunicazioneType[] {nuovaComunicazione.getDettaglioComunicazione()}, CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
							this.eventManager.insertEvent(evento);
							this.setDataProtocollo(UtilityDate.convertiData(this.dataPresentazione, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
						} catch (Throwable t) {
							if (mailProtocollazioneInviata) {
								evento.setError(t);
								this.eventManager.insertEvent(evento);

								// segnalo l'errore, comunque considero la
								// protocollazione andata a buon fine e segnalo nel
								// log e a video che va aggiornato a mano lo stato
								// della comunicazione
								this.msgErrore = this.getText(
										"Errors.updateStatoComunicazioneDaProcessare",
										new String[]{nuovaComunicazione.getDettaglioComunicazione().getId().toString()});
								ApsSystemUtils.logThrowable(t, this, "sendUpdate", this.getTextFromDefaultLocale(
										"Errors.updateStatoComunicazioneDaProcessare",
										nuovaComunicazione.getDettaglioComunicazione().getId().toString()));
								this.addActionError(this.msgErrore);

								evento.setEventType(PortGareEventsConstants.COMPLETAMENTO_MANUALE_PROTOCOLLAZIONE_COMUNICAZIONE);
								evento.setLevel(Event.Level.WARNING);
								evento.setMessage("Aggiornare manualmente la comunicazione con id " + nuovaComunicazione.getDettaglioComunicazione().getId());
								evento.resetDetailMessage();
								this.eventManager.insertEvent(evento);
								this.setDataProtocollo(UtilityDate.convertiData(this.dataPresentazione, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
							} else {
								evento.setError(t);
								this.eventManager.insertEvent(evento);

								ApsSystemUtils.logThrowable(t, this, "sendUpdate", "Per errori durante la connessione al servizio WSDM, non e' stato possibile inviare la richiesta di protocollazione");
								ExceptionUtils.manageExceptionError(t, this);
								this.setTarget(INPUT);
								// si deve annullare l'invio che si e' tentato di protocollare							
								//this.eliminaComunicazioneInviata(CommonSystemConstants.ID_APPLICATIVO, nuovaComunicazione.getDettaglioComunicazione().getId(),iscrizioneHelper.getDescBando());
								this.annullaComunicazioneInviata(
										nuovaComunicazione, 
										iscrizioneHelper.getDescBando());
								protocollazioneOk = false;
							}
						}
						break;

					case PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_WSDM:
						evento.setMessage("Protocollazione via WSDM");

						boolean protocollazioneInoltrata = false;
						WSDocumentoType documento = null; 

						try {
							WizardDatiImpresaHelper datiImpresaHelper = iscrizioneHelper.getImpresa();
							
							FascicoloProtocolloType fascicoloBackOffice = this.bandiManager.getFascicoloProtocollo(this.codice);
							WSDMLoginAttrType loginAttr = this.wsdmManager.getLoginAttr();
							if (fascicoloBackOffice != null && fascicoloBackOffice.getCodiceAoo() != null) {
								// nel caso di protocollazione Titulus il codiceAoo
								// risulta valorizzato nel fascicolo a deve essere
								// letto per essere usato in protocollazione in
								// ingresso
								loginAttr.getLoginTitAttr().setCodiceAmministrazioneAoo(fascicoloBackOffice.getCodiceAoo());
							}
							if (fascicoloBackOffice != null && fascicoloBackOffice.getCodiceUfficio() != null) {
								// nel caso di protocollazione Titulus il codiceUfficio
								// risulta valorizzato nel fascicolo a deve essere
								// letto per essere usato in protocollazione in
								// ingresso
								loginAttr.getLoginTitAttr().setCodiceUfficio(fascicoloBackOffice.getCodiceUfficio());
							}

							WSDMProtocolloDocumentoInType wsdmProtocolloDocumentoIn = creaInputProtocollazioneWSDM(
									iscrizioneHelper,
									datiImpresaHelper,
									prefissoLabel,
									PortGareSystemConstants.RICHIESTA_AGGIORNAMENTO_ISCRIZIONE_ALBO,
									fascicoloBackOffice, nuovaComunicazione, bando);

							WSDMProtocolloDocumentoType ris = this.wsdmManager.inserisciProtocollo(loginAttr, wsdmProtocolloDocumentoIn);
							
							this.annoProtocollo = ris.getAnnoProtocollo();
							this.numeroProtocollo = ris.getNumeroProtocollo();
							if (ris.getDataProtocollo() != null) {
								this.setDataProtocollo(UtilityDate.convertiData(
										ris.getDataProtocollo().getTime(), 
										UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
							}
							protocollazioneInoltrata = true;
							this.eventManager.insertEvent(evento);

							// si aggiorna lo stato a 5 (da processare)  
							// aggiornando inoltre anche i dati di protocollazione
							long id = nuovaComunicazione.getDettaglioComunicazione().getId();

							documento = new WSDocumentoType();
							documento.setEntita("GARE");
							documento.setChiave1(iscrizioneHelper.getIdBando());
							documento.setNumeroDocumento(ris.getNumeroDocumento());
							documento.setAnnoProtocollo(ris.getAnnoProtocollo());
							documento.setNumeroProtocollo(ris.getNumeroProtocollo());
							documento.setVerso(WSDocumentoTypeVerso.IN);
							documento.setOggetto(wsdmProtocolloDocumentoIn.getOggetto());

							evento.setEventType(PortGareEventsConstants.PROTOCOLLA_COMUNICAZIONE_DA_PROCESSARE);
							evento.setMessage("Aggiornamento comunicazione con id "
									+ nuovaComunicazione.getDettaglioComunicazione().getId()
									+ " allo stato " + CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE
									+ ", protocollata con anno " + ris.getAnnoProtocollo() 
									+ " e numero " + ris.getNumeroProtocollo());
							this.comunicazioniManager.protocollaComunicazione(
									CommonSystemConstants.ID_APPLICATIVO,
									id,
									ris.getNumeroProtocollo(),
									(ris.getDataProtocollo() != null ? ris.getDataProtocollo().getTime() : this.dataPresentazione),
									CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE,
									documento);
							this.eventManager.insertEvent(evento);
						} catch (Throwable t) {
							if (protocollazioneInoltrata) {
								evento.setError(t);
								this.eventManager.insertEvent(evento);

								// segnalo l'errore, comunque considero la
								// protocollazione andata a buon fine e segnalo nel
								// log e a video che va aggiornato a mano lo stato
								// della comunicazione
								msgErrore = this.getText(
										"Errors.updateStatoComunicazioneDaProcessare",
										new String[] {nuovaComunicazione.getDettaglioComunicazione().getId().toString()});
								ApsSystemUtils.logThrowable(t, this, "sendUpdate", this.getTextFromDefaultLocale(
										"Errors.updateStatoComunicazioneDaProcessare",
										nuovaComunicazione.getDettaglioComunicazione().getId().toString()));
								this.addActionError(this.msgErrore);

								evento.setEventType(PortGareEventsConstants.COMPLETAMENTO_MANUALE_PROTOCOLLAZIONE_COMUNICAZIONE);
								evento.setLevel(Event.Level.WARNING);
								evento.setMessage("Aggiornare manualmente la comunicazione con id "
										+ nuovaComunicazione.getDettaglioComunicazione().getId()
										+ " ed inserire inoltre un documento in ingresso per entità " + documento.getEntita()
										+ ", chiave1 " + documento.getChiave1()
										+ ", oggetto " + documento.getOggetto()
										+ ", numero documento " + documento.getNumeroDocumento()
										+ ", anno protocollo " + documento.getAnnoProtocollo()
										+ " e numero protocollo " + documento.getNumeroProtocollo());
								evento.resetDetailMessage();
								this.eventManager.insertEvent(evento);
							} else {
								evento.setError(t);
								this.eventManager.insertEvent(evento);

								ApsSystemUtils.logThrowable(t, this, "sendUpdate", "Per errori durante la connessione al servizio WSDM, non e' stato possibile inviare la richiesta di protocollazione");
								ExceptionUtils.manageExceptionError(t, this);
								this.setTarget(INPUT);
								
								// si deve annullare l'invio che si e' tentato di protocollare							
								//this.eliminaComunicazioneInviata(
								//		CommonSystemConstants.ID_APPLICATIVO, 
								//		nuovaComunicazione.getDettaglioComunicazione().getId(),
								//		iscrizioneHelper.getDescBando());
								this.annullaComunicazioneInviata(
										nuovaComunicazione, 
										iscrizioneHelper.getDescBando());
								protocollazioneOk = false;
							}
						}
						break;
					default:
						break;
					}
				}

				// FASE 3: se gli step precedenti sono andati a buon fine, si invia la ricevuta all'impresa
				if (inviataComunicazione && protocollazioneOk) {
					evento = new Event();
					evento.setUsername(this.getCurrentUser().getUsername());
					evento.setDestination(this.codice);
					evento.setLevel(Event.Level.INFO);
					evento.setEventType(PortGareEventsConstants.INVIO_MAIL_CONFERMA_RICEVUTA);
					evento.setMessage("Invio mail ricevuta di conferma trasmissione comunicazione " + nuovaComunicazione.getDettaglioComunicazione().getTipoComunicazione() + " a " + (String) ((IUserProfile) this.getCurrentUser().getProfile()).getValue("email"));
					evento.setIpAddress(this.getCurrentUser().getIpAddress());
					
					try {
						String tipoIscrizione = iscrizioneHelper.getTipologia() == PortGareSystemConstants.TIPOLOGIA_ELENCO_STANDARD 
							? this.getI18nLabel("LABEL_ALL_ELENCO") 
							: this.getI18nLabel("LABEL_AL_MERCATO_ELETTRONICO");
						this.sendMailConfermaImpresa(tipoIscrizione, iscrizioneHelper);
					} catch (ApsException t) {
						ApsSystemUtils.getLogger().error(
								"Per errori durante la connessione al server di posta, non e' stato possibile inviare all'impresa {} la ricevuta della richiesta di aggiornamento per  {}.",
								new Object[] { this.getCurrentUser().getUsername(), this.codice });
						this.msgErrore = this.getText("Errors.sendMailError");
						ApsSystemUtils.logThrowable(t, this, "sendUpdate");
						evento.setError(t);
					} finally {
						this.eventManager.insertEvent(evento);
					}
				}

				// FASE 4: se invio e protocollazione sono andate a buon fine, anche se la ricevuta
				// all'impresa non e' stata inviata, si procede con la pulizia della sessione
				if (inviataComunicazione && protocollazioneOk) {
					// pulizia e impostazione navigazione futura
					// se tutto e' andato a buon fine si eliminano
					// le informazioni dalla sessione ...
					this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
					this.session.remove(PortGareSystemConstants.SESSION_ID_PAGINA);
					this.tipologia = iscrizioneHelper.getTipologia();
				}
			}
			
			// concludi la protocollazione
			this.appParamManager.setStazioneAppaltanteProtocollazione(null);
		}
		
		return this.getTarget();
	}

	/**
	 * ...
	 * @throws DocumentException 
	 */
	private WSDMProtocolloDocumentoInType creaInputProtocollazioneWSDM(
			WizardIscrizioneHelper iscrizioneHelper,
			WizardDatiImpresaHelper datiImpresaHelper, 
			String prefissoLabel,
			String tipoComunicazione,
			FascicoloProtocolloType fascicoloBackOffice,
			ComunicazioneType comunicazione, 
			DettaglioBandoIscrizioneType bando) throws IOException, ApsException, DocumentException 
	{
		WizardDocumentiHelper documentiHelper = iscrizioneHelper.getDocumenti();
		
		String codiceSistema = (String) this.appParamManager
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_CODICE_SISTEMA);
	
		boolean esisteFascicolo = (fascicoloBackOffice != null); 
		String codiceFascicolo = null;
		Long annoFascicolo = null;
		String numeroFascicolo = null;
		String classificaFascicolo = null;
		String idUnitaOperDestinataria = null;
		String idIndice = null;
		String idTitolazione = null;
		Integer cfMittente = (Integer) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_CODICE_FISCALE_MITTENTE);
		boolean usaCodiceFiscaleMittente = (cfMittente != null ? 1 == cfMittente : true);
		String channelCode = (String) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_CODICE_CHANNEL_CODE);
		String sottoTipo = (String)this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_SOTTOTIPO_COMUNICAZIONE);
		String rup = (bando.getStazioneAppaltante() != null ? bando.getStazioneAppaltante().getRup() : null);
		String acronimoRup = ""; 
		if(rup != null) {
			String[] s = rup.toUpperCase().split(" ");
			for(int i = 0; i < s.length; i++) acronimoRup += s[i].substring(0, 1);  
		}
		
		if (esisteFascicolo) {
			codiceFascicolo = fascicoloBackOffice.getCodice();
			annoFascicolo = (fascicoloBackOffice.getAnno() != null 
						     ? fascicoloBackOffice.getAnno().longValue() : null);
			numeroFascicolo = fascicoloBackOffice.getNumero();
			classificaFascicolo = fascicoloBackOffice.getClassifica();			
			// questo serve per la protocollazione engineering
			idTitolazione = classificaFascicolo;
		} else {
			// si legge la classifica presa dalla configurazione solo se non esiste il fascicolo			
			classificaFascicolo = (String)this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_ISCRIZIONE_CLASSIFICA);
			
			// ELENCO
			idTitolazione = (String)this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_ISCRIZIONE_TITOLAZIONE);
		}

		idUnitaOperDestinataria = (String)this.appParamManager
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_ID_UNITA_OPERATIVA_DESTINATARIA);
		// serve per PRISMA (se PROTOCOLLAZIONE_WSDM_ID_UNITA_OPERATIVA_DESTINATARIA è vuoto utilizza WSFASCICOLO.struttura)
		if(StringUtils.isEmpty(idUnitaOperDestinataria) && fascicoloBackOffice != null) {
			idUnitaOperDestinataria = fascicoloBackOffice.getStrutturaCompetente();
		}

		// ELENCO
		idIndice = (String)this.appParamManager
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_ISCRIZIONE_INDICE);
		
		WSDMProtocolloDocumentoInType wsdmProtocolloDocumentoIn = new WSDMProtocolloDocumentoInType();
		wsdmProtocolloDocumentoIn.setIdUnitaOperativaDestinataria(idUnitaOperDestinataria);
		wsdmProtocolloDocumentoIn.setIdIndice(idIndice);
		wsdmProtocolloDocumentoIn.setIdTitolazione(idTitolazione);
		wsdmProtocolloDocumentoIn.setClassifica(classificaFascicolo);		
		wsdmProtocolloDocumentoIn.setTipoDocumento( (String)this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_ISCRIZIONE_TIPO_DOCUMENTO));
		wsdmProtocolloDocumentoIn.setChannelCode(channelCode);
		
		if(IWSDMManager.CODICE_SISTEMA_JDOC.equals(codiceSistema)) {
			wsdmProtocolloDocumentoIn.setGenericS11(sottoTipo);
			wsdmProtocolloDocumentoIn.setGenericS12(rup);
		}

		Calendar data = new GregorianCalendar();
		data.setTimeInMillis(this.getDataPresentazione().getTime());
		wsdmProtocolloDocumentoIn.setData(data);
		
		String ragioneSociale = datiImpresaHelper.getDatiPrincipaliImpresa().getRagioneSociale();
		String ragioneSociale200 = StringUtils.left(ragioneSociale, 200);
		String codiceFiscale = datiImpresaHelper.getDatiPrincipaliImpresa().getCodiceFiscale();
		String partitaIva = datiImpresaHelper.getDatiPrincipaliImpresa().getPartitaIVA();
		String descrizione = MessageFormat.format(
				this.getI18nLabelFromDefaultLocale(prefissoLabel + "_TESTO"),
				new Object[] { iscrizioneHelper.getDescBando(), ragioneSociale200, bando.getStazioneAppaltante().getDenominazione() });
		
		String key;
		if(iscrizioneHelper.getTipologia() == PortGareSystemConstants.TIPOLOGIA_ELENCO_STANDARD) {
			key = PortGareSystemConstants.RICHIESTA_ISCRIZIONE_ALBO.equals(tipoComunicazione)
					? "LABEL_WSDM_OGGETTO_ISCR_ELENCO"
					: "LABEL_WSDM_OGGETTO_AGGISCR_ELENCO";
		} else {
			key = PortGareSystemConstants.RICHIESTA_ISCRIZIONE_ALBO.equals(tipoComunicazione)
					? "LABEL_WSDM_OGGETTO_ISCR_CATALOGO"
					: "LABEL_WSDM_OGGETTO_AGGISCR_CATALOGO";
		}
		String oggetto = MessageFormat.format(this.getI18nLabelFromDefaultLocale(key),
											  new Object[] { ragioneSociale200, codiceFiscale });
		
		wsdmProtocolloDocumentoIn.setOggetto(oggetto);
		wsdmProtocolloDocumentoIn.setDescrizione(descrizione);
		wsdmProtocolloDocumentoIn.setInout(WSDMProtocolloInOutType.IN);
		wsdmProtocolloDocumentoIn.setCodiceRegistro( (String)this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_ISCRIZIONE_CODICE_REGISTRO));
		
		// serve per Titulus
		wsdmProtocolloDocumentoIn.setIdDocumento("W_INVCOM/"
				+ CommonSystemConstants.ID_APPLICATIVO + "/"
				+ comunicazione.getDettaglioComunicazione().getId());
		
		// servono per Archiflow
		wsdmProtocolloDocumentoIn.setMittenteEsterno(ragioneSociale);
		wsdmProtocolloDocumentoIn.setSocieta(bando.getStazioneAppaltante().getCodice());
		if(IWSDMManager.CODICE_SISTEMA_FOLIUM.equals(codiceSistema)) {
			wsdmProtocolloDocumentoIn.setCodiceGaraLotto(IWSDMManager.PROT_AUTOMATICA_PORTALE_APPALTI + " " + bando.getDatiGeneraliBandoIscrizione().getCodice());
		} else {
			wsdmProtocolloDocumentoIn.setCodiceGaraLotto(bando.getDatiGeneraliBandoIscrizione().getCodice());
		}
		//wsdmProtocolloDocumentoIn.setCig(null);
		wsdmProtocolloDocumentoIn.setDestinatarioPrincipale(bando.getStazioneAppaltante().getCodice());
		wsdmProtocolloDocumentoIn.setMezzo((String) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_MEZZO));

		// Mittente
		wsdmProtocolloDocumentoIn.setMittenteInterno((String) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_MITTENTE_INTERNO));
		if (IWSDMManager.CODICE_SISTEMA_JIRIDE.equals(codiceSistema)
				&& esisteFascicolo
				&& StringUtils.isNotEmpty(fascicoloBackOffice
						.getStrutturaCompetente())) {
			// solo per JIRIDE valorizzo il mittente interno con la struttura del fascicolo, se valorizzata
			wsdmProtocolloDocumentoIn.setMittenteInterno(fascicoloBackOffice
					.getStrutturaCompetente());
		}
		
		IDatiPrincipaliImpresa impresa = datiImpresaHelper.getDatiPrincipaliImpresa();
		WSDMProtocolloAnagraficaType[] mittenti = new WSDMProtocolloAnagraficaType[1];
		mittenti[0] = new WSDMProtocolloAnagraficaType();
		// JPROTOCOL: "Cognomeointestazione" accetta al massimo 100 char 
		if(IWSDMManager.CODICE_SISTEMA_JPROTOCOL.equals(codiceSistema)) {
			mittenti[0].setCognomeointestazione(StringUtils.left(ragioneSociale, 100));
		} else if(IWSDMManager.CODICE_SISTEMA_JDOC.equals(codiceSistema)) {
			mittenti[0].setCognomeointestazione(StringUtils.left(ragioneSociale, 200));
		} else {
			mittenti[0].setCognomeointestazione(ragioneSociale);
		}
		if (usaCodiceFiscaleMittente) {
			mittenti[0].setCodiceFiscale(codiceFiscale);
		} else {
			mittenti[0].setCodiceFiscale("");
		}
		mittenti[0].setPartitaIVA(partitaIva);
		String indirizzo = impresa.getIndirizzoSedeLegale() + " " + impresa.getNumCivicoSedeLegale(); 
		mittenti[0].setIndirizzoResidenza(indirizzo);
		mittenti[0].setLocalitaResidenza(impresa.getComuneSedeLegale());
		mittenti[0].setComuneResidenza(impresa.getComuneSedeLegale());
		mittenti[0].setNazionalita(impresa.getNazioneSedeLegale());
	    mittenti[0].setMezzo((String) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_MEZZO));
	    String email = DatiImpresaChecker.getEmailRiferimento(impresa.getEmailPECRecapito(), impresa.getEmailRecapito());
	    mittenti[0].setEmail(email);
	    mittenti[0].setProvinciaResidenza(impresa.getProvinciaSedeLegale());
	    mittenti[0].setCapResidenza(impresa.getCapSedeLegale());
		wsdmProtocolloDocumentoIn.setMittenti(mittenti);

		// Inserimento in fascicolo
		if (esisteFascicolo) {
			wsdmProtocolloDocumentoIn.setInserimentoInFascicolo(WSDMInserimentoInFascicoloType.SI_FASCICOLO_ESISTENTE);
			WSDMFascicoloType fascicolo = new WSDMFascicoloType();
			fascicolo.setClassificaFascicolo(classificaFascicolo);
			fascicolo.setCodiceFascicolo(codiceFascicolo);
			fascicolo.setAnnoFascicolo(annoFascicolo);
			fascicolo.setNumeroFascicolo(numeroFascicolo);
			// oggettoFascicolo serve per Titulus
		    fascicolo.setOggettoFascicolo(iscrizioneHelper.getDescBando());
		    if(IWSDMManager.CODICE_SISTEMA_JDOC.equals(codiceSistema)) {
		    	fascicolo.setGenericS11(acronimoRup);
		    	fascicolo.setGenericS12(rup);
		    }
			wsdmProtocolloDocumentoIn.setFascicolo(fascicolo);
		} else {
			wsdmProtocolloDocumentoIn.setInserimentoInFascicolo(WSDMInserimentoInFascicoloType.NO);
		}

		/* crea l'elenco degli allegati da inviare alla protocollazione */
		// recupera l'elenco dei documenti richiesti
		List<DocumentazioneRichiestaType> listaDocRichiesti = null;
		if(PortGareSystemConstants.RICHIESTA_ISCRIZIONE_ALBO.equals(tipoComunicazione)) {
			listaDocRichiesti = this.bandiManager
					.getDocumentiRichiestiBandoIscrizione(
							iscrizioneHelper.getIdBando(), 
							iscrizioneHelper.getImpresa().getDatiPrincipaliImpresa().getTipoImpresa(),
							iscrizioneHelper.isRti());			
		}
		
		List<File>   docRichiesti     = null;
		List<String> nomeDocRichiesti = null;
		if(listaDocRichiesti != null) {
			docRichiesti = documentiHelper.getDocRichiesti();
			nomeDocRichiesti = documentiHelper.getDocRichiestiFileName();
		}

		List<File>   docUlteriori       = null;
		List<String> nomeDocUlteriori   = null;
		List<String> titoloDocUlteriori = null;
		if(documentiHelper.getDocUlteriori() != null) {
			docUlteriori = documentiHelper.getDocUlteriori();
			nomeDocUlteriori = documentiHelper.getDocUlterioriFileName();
			titoloDocUlteriori = documentiHelper.getDocUlterioriDesc();
		}
		
		// prepara 1+N allegati...
		// inserire prima gli allegati e poi l'allegato della comunicazione
		// ...verifica in che posizione inserire "comunicazione.pdf" (in testa o in coda)
		boolean inTesta = false;		// default, inserisci in coda
		if(IWSDMManager.CODICE_SISTEMA_JDOC.equals(codiceSistema)) {
			Integer v = (Integer) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_POSIZIONE_ALLEGATO_COMUNICAZIONE);
			if(v != null && v == 1) {
				inTesta = true;
			}
		}
		
		// (inserisciProtocollo richiede obbligatoriamente un documento da protocollare)
		int numDocDaProtocollare = 
			(docRichiesti == null ? 0 : docRichiesti.size()) + 
	        (docUlteriori == null ? 0 : docUlteriori.size()) + 1;
		
		WSDMProtocolloAllegatoType[] allegati = new WSDMProtocolloAllegatoType[numDocDaProtocollare];
		
		int n = 0;
		if(inTesta) {
			n++;
		}
		
		// inserisci gli eventuali allegati della comunicazione
		if(numDocDaProtocollare > 0) {
			// allego i documenti richiesti
			if(documentiHelper.getDocRichiestiId() != null && !documentiHelper.getDocRichiestiId().isEmpty()) {
				for(int j = 0; j < documentiHelper.getDocRichiestiId().size(); j++) {
					
					// verifica se esiste il documento richiesto in BO... 
					DocumentazioneRichiestaType elem = null;
					for (int k = 0; k < listaDocRichiesti.size(); k++) {
						if (documentiHelper.getDocRichiestiId().get(j) == listaDocRichiesti.get(k).getId()) {
							elem = listaDocRichiesti.get(k);
							break;
						}
					}
					if(elem == null) {
						// WE1914
						// titolo del documento richiesto non trovato!!!
						// annulla l'inserimento e segnala all'utente che questo documento 
						// verrà cancellato dalla busta e che sarà necessario ricontrollare 
						// i documenti inseriti prima dell'invio!!!
						documentiHelper.removeDocRichiesto(j);
						this.session.put(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO,
										 iscrizioneHelper);
						this.addActionError(this.getText("Errors.document.notAvailable"));
						throw new IOException(this.getTextFromDefaultLocale("Errors.document.notAvailable"));
					} else {
						allegati[n] = new WSDMProtocolloAllegatoType();
						allegati[n].setNome(nomeDocRichiesti.get(j));
						allegati[n].setTipo(StringUtils.substringAfterLast(nomeDocRichiesti.get(j), "."));
						allegati[n].setTitolo(elem.getNome());
						allegati[n].setContenuto(documentiHelper.getContenutoDocRichiesto(j));
						// serve per Titulus
						allegati[n].setIdAllegato("W_INVCOM/"
								+ CommonSystemConstants.ID_APPLICATIVO + "/"
								+ comunicazione.getDettaglioComunicazione().getId() + "/" + n);
						n++;
					}
				}
			}

			// allego i documenti ulteriori
			if(docUlteriori != null) {
				for(int j = 0; j < docUlteriori.size(); j++) {
					allegati[n] =  new WSDMProtocolloAllegatoType();
					allegati[n].setNome(nomeDocUlteriori.get(j));
					allegati[n].setTipo(StringUtils.substringAfterLast(nomeDocUlteriori.get(j), "."));
					allegati[n].setTitolo(titoloDocUlteriori.get(j));
					//byte[] bf = FileUtils.readFileToByteArray(docUlteriori.get(j));
					allegati[n].setContenuto(documentiHelper.getContenutoDocUlteriore(j));
					// serve per Titulus
					allegati[n].setIdAllegato("W_INVCOM/"
							+ CommonSystemConstants.ID_APPLICATIVO + "/"
							+ comunicazione.getDettaglioComunicazione().getId() + "/" + n);
					n++;
				}
			}
		}

		//... inserisci "comunicazione.pdf" nella lista degli allegati (in testa/in coda)
		//... prepara l'allegato "comunicazione.pdf"
		int n2 = n;
		if(inTesta) {
			n2 = 0;
		}

		String titolo = null;
		if(iscrizioneHelper.isAggiornamentoIscrizione()) {
			titolo = MessageFormat.format(this.getI18nLabelFromDefaultLocale("NOTIFICA_AGGISCRIZIONE_OGGETTO"),  
								  new Object[] { iscrizioneHelper.getIdBando() });
		} else {
			titolo = MessageFormat.format(this.getI18nLabelFromDefaultLocale("NOTIFICA_ISCRIZIONE_OGGETTO"),
								  new Object[] { iscrizioneHelper.getIdBando() });
		}
		String contenuto = null;
		if(iscrizioneHelper.isAggiornamentoIscrizione()) {
			contenuto = MessageFormat.format(this.getI18nLabelFromDefaultLocale("MAIL_AGGISCRIZIONE_PROTOCOLLO_TESTOCONALLEGATI"),
					new Object[] { 
						ragioneSociale, 
						codiceFiscale,
						iscrizioneHelper.getImpresa().getDatiPrincipaliImpresa().getPartitaIVA(), 
						iscrizioneHelper.getImpresa().getDatiPrincipaliImpresa().getEmailPECRecapito(),
						indirizzo, 
						UtilityDate.convertiData(this.getDataPresentazione(), UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS),
						iscrizioneHelper.getDescBando() 
					});
		} else {
			contenuto = MessageFormat.format(this.getI18nLabelFromDefaultLocale("MAIL_ISCRIZIONE_PROTOCOLLO_TESTOCONALLEGATI"),
					new Object[] { 
						ragioneSociale, 
						codiceFiscale,
						iscrizioneHelper.getImpresa().getDatiPrincipaliImpresa().getPartitaIVA(), 
						iscrizioneHelper.getImpresa().getDatiPrincipaliImpresa().getEmailPECRecapito(), 
						indirizzo, 
						UtilityDate.convertiData(this.getDataPresentazione(), UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS),
						iscrizioneHelper.getDescBando() 
					});
		}
		byte[] contenutoPdf = UtilityStringhe.string2Pdf(contenuto);
		
		allegati[n2] = new WSDMProtocolloAllegatoType();
		allegati[n2].setNome("comunicazione.pdf");
		allegati[n2].setTipo("pdf");
		allegati[n2].setTitolo(titolo);
		allegati[n2].setContenuto(contenutoPdf);
		// serve per Titulus
		allegati[n2].setIdAllegato("W_INVCOM/"
				+ CommonSystemConstants.ID_APPLICATIVO + "/"
				+ comunicazione.getDettaglioComunicazione().getId() + "/" + n2);

		wsdmProtocolloDocumentoIn.setAllegati(allegati);

		// INTERVENTI ARCHIFLOW
		if(IWSDMManager.CODICE_SISTEMA_ARCHIFLOWFA.equals(codiceSistema)) {
			oggetto = this.codice + "-" + wsdmProtocolloDocumentoIn.getOggetto()+"-" + iscrizioneHelper.getDescBando();
			wsdmProtocolloDocumentoIn.setOggetto(oggetto);
		}
	    Calendar dataArrivo = Calendar.getInstance();
	    dataArrivo.setTimeInMillis(System.currentTimeMillis());
	    wsdmProtocolloDocumentoIn.setNumeroAllegati(Long.valueOf(allegati.length -1));
	    wsdmProtocolloDocumentoIn.setDataArrivo(dataArrivo);
	    wsdmProtocolloDocumentoIn.setSupporto((String) this.appParamManager
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_SUPPORTO));
	    
		String struttura = (String) this.appParamManager
			.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_STRUTTURA);
		// se specificata in configurazione la uso, altrimenti riutilizzo la
		// configurazione presente nel fascicolo di backoffice (così avveniva
		// per ARCHIFLOW)
		if (StringUtils.isNotEmpty(struttura)) {
			wsdmProtocolloDocumentoIn.setStruttura(struttura);
		} else if (esisteFascicolo) {
	    	wsdmProtocolloDocumentoIn.setStruttura(fascicoloBackOffice.getStrutturaCompetente());
	    }
		
		String tipoAssegnazione = (String) this.appParamManager
				.getConfigurationValue(AppParamManager.PROTOCOLLAZIONE_WSDM_TIPOASSEGNAZIONE);
		// se specificata in configurazione la uso (JPROTOCOL)
		if (StringUtils.isNotEmpty(tipoAssegnazione)) {
			wsdmProtocolloDocumentoIn.setTipoAssegnazione(tipoAssegnazione);
		}
		
		return wsdmProtocolloDocumentoIn;
	}
	
	/**
	 * Annulla gli effetti della comunicazione inviata riportandola nello stato bozza
	 * 
	 * @param comunicazione
	 *            comunicazione da riportare in stato bozza
	 */
	private void annullaComunicazioneInviata(
			ComunicazioneType comunicazione, 
			String descBando) 
	{
		Event evento = new Event();
		evento.setUsername(this.username);
		evento.setDestination(this.codice);
		evento.setLevel(Event.Level.INFO);
		evento.setEventType(PortGareEventsConstants.ABORT_INVIO_COMUNICAZIONE);
		evento.setMessage("Annullamento comunicazione con id " + comunicazione.getDettaglioComunicazione().getId());
		evento.setIpAddress(this.getCurrentUser().getIpAddress());
		evento.setSessionId(this.getRequest().getSession().getId());
		
		try {
			this.comunicazioniManager.updateStatoComunicazioni(
					new DettaglioComunicazioneType[] { comunicazione.getDettaglioComunicazione() },
					CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "invio",
					"In fase di annullamento delle modifiche apportate si è verificato un errore, si consiglia una rimozione manuale delle comunicazioni");
			ExceptionUtils.manageExceptionError(e, this);
			evento.setError(e);
		} finally {
			this.eventManager.insertEvent(evento);
		}
	}
		
	/**
	 * imposta gli allegati della comunicazione
	 */
	private static void setAllegatoComunicazione(
			ComunicazioneType comunicazione, 
			WizardIscrizioneHelper helper,
			String nomeFile, 
			String descrizioneFile) throws IOException 
	{	
		HashMap<String, String> suggestedPrefixes = new HashMap<String, String>();
		suggestedPrefixes.put("http://www.eldasoft.it/sil/portgare/datatypes", "");		
		XmlOptions opts = new XmlOptions();
		opts.setSaveSuggestedPrefixes(suggestedPrefixes);		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		// inserisci il documento xml come I allegato della comunicazione 
		// e tutti i documenti dell'xml come allegati della comunicazione
		List<AllegatoComunicazioneType> allegatiXml = new ArrayList<AllegatoComunicazioneType>();

		AllegatoComunicazioneType allegato = ComunicazioniUtilities
			.createAllegatoComunicazione(nomeFile, descrizioneFile, null);		
		allegatiXml.add(allegato);
		
		// aggiungi i documenti come allegati della comunicazione 
		helper.getDocumenti().documentiToAllegatiComunicazione(allegatiXml);
		
	    // qui l'helper e' aggiornato e si puo' generare l'xml del documento
		XmlObject xmlDocument = helper.getXmlDocument(true);
	    xmlDocument.save(baos, opts);
	    allegatiXml.get(0).setFile( baos.toString("UTF-8").getBytes() ); 
	    
		comunicazione.setAllegato(
				allegatiXml.toArray(new AllegatoComunicazioneType[allegatiXml.size()]) );
	}
	
}
