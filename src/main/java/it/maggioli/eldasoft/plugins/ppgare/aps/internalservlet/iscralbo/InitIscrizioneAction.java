package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.opensymphony.xwork2.inject.Inject;
import it.eldasoft.sil.portgare.datatypes.AggIscrizioneImpresaElencoOpType;
import it.eldasoft.sil.portgare.datatypes.AggiornamentoIscrizioneImpresaElencoOperatoriDocument;
import it.eldasoft.sil.portgare.datatypes.CategoriaType;
import it.eldasoft.sil.portgare.datatypes.FirmatarioType;
import it.eldasoft.sil.portgare.datatypes.IscrizioneImpresaElencoOpType;
import it.eldasoft.sil.portgare.datatypes.IscrizioneImpresaElencoOperatoriDocument;
import it.eldasoft.sil.portgare.datatypes.ListaCategorieIscrizioneType;
import it.eldasoft.sil.portgare.datatypes.ListaDocumentiType;
import it.eldasoft.sil.portgare.datatypes.ListaPartecipantiRaggruppamentoType;
import it.eldasoft.sil.portgare.datatypes.ListaStazioniAppaltantiType;
import it.eldasoft.sil.portgare.datatypes.PartecipanteRaggruppamentoType;
import it.eldasoft.sil.portgare.datatypes.RinnovoIscrizioneImpresaElencoOperatoriDocument;
import it.eldasoft.sil.portgare.datatypes.RinnovoIscrizioneImpresaElencoOperatoriType;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.BandoIscrizioneType;
import it.eldasoft.www.sil.WSGareAppalto.CategoriaBandoIscrizioneType;
import it.eldasoft.www.sil.WSGareAppalto.CategoriaImpresaType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioBandoIscrizioneType;
import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.eldasoft.www.sil.WSGareAppalto.ImpresaIscrizioneType;
import it.eldasoft.www.sil.WSGareAppalto.MandanteRTIType;
import it.eldasoft.www.sil.WSGareAppalto.QuestionarioType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.ComponentiRTIList;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.Attachment;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.DocumentiAllegatiHelper;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.qcompiler.inc.QCQuestionario;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.INtpManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StrutsUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.FirmatarioBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ImpresaAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.SoggettoFirmatarioImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.ComponenteHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.IComponente;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Action di inizializzazione apertura wizard iscrizione all'albo
 *
 * @author Stefano.Sabbadin
 */
public class InitIscrizioneAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -617432626981364951L;
	private static final Logger logger = ApsSystemUtils.getLogger();
	
	private static final String MODULISTICA_CAMBIATA = "modulisticaCambiata";
	
	private INtpManager ntpManager;
	private IBandiManager bandiManager;
	private IComunicazioniManager comunicazioniManager;
	private ICustomConfigManager customConfigManager;
	private IAppParamManager appParamManager;
	private IEventManager eventManager;

	protected Map<String, Object> session;

	/** Memorizza lal prossima dispatchAction da eseguire nel wizard. */
	@Validate(EParamValidation.ACTION)
	protected String nextResultAction;

	/**
	 * Codice del bando per elenco operatori economici per il quale gestire
	 * un'iscrizione
	 */
	@Validate(EParamValidation.CODICE)
	private String codice;

	/**
	 * Store state of StrutsConstants.STRUTS_MULTIPART_SAVEDIR setting.
	 */
	@Validate(EParamValidation.GENERIC)
	private String multipartSaveDir;

	
	public void setNtpManager(INtpManager ntpManager) {
		this.ntpManager = ntpManager;
	}

	public INtpManager getNtpManager() {
		return this.ntpManager;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public IBandiManager getBandiManager() {
		return bandiManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}
	
	public IAppParamManager getAppParamManager() {
		return this.appParamManager;
	}
	
	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public IComunicazioniManager getComunicazioniManager() {
		return comunicazioniManager;
	}

	public void setCustomConfigManager(ICustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
	}

	public ICustomConfigManager getCustomConfigManager() {
		return customConfigManager;
	}
	
	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}
	
	public IEventManager getEventManager() {
		return this.eventManager;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public Map<String, Object> getSession() {
		return this.session;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getCodice() {
		return this.codice;
	}

	@Inject(StrutsConstants.STRUTS_MULTIPART_SAVEDIR)
	public void setMultipartSaveDir(String multipartSaveDir) {
		this.multipartSaveDir = multipartSaveDir;
	}

	public String getNextResultAction() {
		return this.nextResultAction;
	}

	/**
	 * inizializza un'iscrizione ad elenco operatori
	 */
	public String newIscrizione() throws Exception {
		return newIscrizione(PortGareSystemConstants.TIPOLOGIA_ELENCO_STANDARD);
	}

	/**
	 * inizializza un'iscrizione a catalogo
	 */
	public String newCatalogoIscrizione() throws Exception {
		return newIscrizione(PortGareSystemConstants.TIPOLOGIA_ELENCO_CATALOGO);
	}

	/**
	 * Richiede al backoffice i dati dell'impresa per consentire la gestione del
	 * wizard di registrazione all'albo e memorizza tali dati in sessione.
	 * 
	 * @throws Exception 
	 */
	private String newIscrizione(int tipologia) throws Exception {
//		boolean datiInterpretati = false;
		
		EFlussiAccessiDistinti flusso = (tipologia == PortGareSystemConstants.TIPOLOGIA_ELENCO_STANDARD
				? EFlussiAccessiDistinti.ISCRIZIONE_ELENCO 
				: EFlussiAccessiDistinti.ISCRIZIONE_CATALOGO);

		// verifica il profilo di accesso ed esegui un LOCK alla funzione 
		if( !lockAccessoFunzione(flusso, this.codice) ) {
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			return this.getTarget();
		}
		
		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			try {
				// Pulisco la sessione da eventuali wizard pending, i quali non 
				// sono stati annullati tramite l'operazione di "annulla" 
				resetSessionHelper();
				
				// si estraggono dal B.O. i dati del bando a cui iscriversi
				DettaglioBandoIscrizioneType dettBando = this.bandiManager
						.getDettaglioBandoIscrizione(this.codice);

				// si calcola il timestamp ntp
				checkFaseWithTimestampNTP(1, dettBando, "Richiesta iscrizione", "BUTTON_ISCRALBO_RICHIESTA_ISCRIZIONE");

				// si determina se viene gestita una o piu' stazioni appaltanti
				LinkedHashMap<String, String> listaStazioni = this.bandiManager
						.getElencoStazioniAppaltantiPerIscrizione(this.codice);

				CategoriaBandoIscrizioneType[] listaCategorie = this.bandiManager
					.getElencoCategorieBandoIscrizione(this.codice, null);
				Set<String> setCategorieFoglia = new HashSet<String>();
				if(listaCategorie != null) {
					for (CategoriaBandoIscrizioneType cat : listaCategorie) {
						if (cat.isFoglia()) {
							setCategorieFoglia.add(cat.getCodice());
						}
					}
				}

				// si crea il contenitore da porre in sessione
				WizardIscrizioneHelper iscrizioneHelper = null;

				if (SUCCESS.equals(this.getTarget())) {

					iscrizioneHelper = this.createHelper(
							dettBando, 
							listaStazioni,
							listaCategorie, 
							tipologia);
				
					iscrizioneHelper.setListaFirmatariMandataria(iscrizioneHelper.composeListaFirmatariMandataria());
					if (dettBando.getDatiGeneraliBandoIscrizione().getAmmesseRTI()) {
						iscrizioneHelper.setAmmesseRTI(true);
					}

					iscrizioneHelper.setIscrizioneDomandaVisible(this.customConfigManager.isVisible("ISCRALBO-DOCUM", "PDFDOMANDA"));
					if(!iscrizioneHelper.isIscrizioneDomandaVisible()) {
						iscrizioneHelper.getStepNavigazione().remove(WizardIscrizioneHelper.STEP_SCARICA_ISCRIZIONE);
					}
					
					if(tipologia == PortGareSystemConstants.TIPOLOGIA_ELENCO_STANDARD) {
						this.setTarget(
							setDatiWizardIscrizioneRinnovoFromUltimaComunicazione(
									this.getTarget(), 
									iscrizioneHelper, 
									setCategorieFoglia));
//						datiInterpretati = true;
					}
					
					if (iscrizioneHelper.isAmmesseRTI()) {
						iscrizioneHelper.setEditRTI(true);
					}
					
					// (QFORM - QUESTIONARI)
					this.setTarget(this.initQuestionario(iscrizioneHelper));
					
					iscrizioneHelper.fillStepsNavigazione();
					
					this.session.put(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO, iscrizioneHelper);

					if(this.getTarget().equals(MODULISTICA_CAMBIATA)) {
						//this.nextResultAction = "openQuestionarioModuloCambiato";
					} else {
						this.nextResultAction = iscrizioneHelper.getNextAction("");		// vai al primo step del wizard
					}
				}
			} catch (XmlException e) {
				ApsSystemUtils.logThrowable(e, this, "newIscrizione");
				this.addActionError(this.getText("Errors.aggiornamentoAnagrafica.parseXml"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "newIscrizione");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}

		return this.getTarget();
	}
	
	/**
	 * resetto la sessione da tutti i vecchi helper presenti in sessione
	 */
	protected void resetSessionHelper() {
		// pulisco la sessione da tutti gli eventuali precedenti helper ancora presenti in sessione!!! 
		GestioneBuste.resetSession();
		getSession().remove(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);
		getSession().remove(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
	}

	/**
	 * Si crea il contenitore da porre in sessione
	 *
	 * @param dettBando 
	 * 			dettaglio del bando d'iscrizione estratto dal backoffice
	 * @param listaStazioni 
	 * 			elenco delle stazioni appaltanti, da usare nel caso di presenza 
	 * 			di unica stazione appaltante
	 * @param listaCategorie 
	 * 			elenco delle categorie selezionabili nel bando
	 * @param tipologia
	 * 
	 * @return helper da memorizzare in sessione
	 * 
	 * @throws Exception 
	 */
	private WizardIscrizioneHelper createHelper(
			DettaglioBandoIscrizioneType dettBando,
			LinkedHashMap<String, String> listaStazioni,
			CategoriaBandoIscrizioneType[] listaCategorie, 
			int tipologia) throws Exception 
	{
		WizardIscrizioneHelper iscrizioneHelper = new WizardIscrizioneHelper();

		iscrizioneHelper.setTipologia(tipologia);
		iscrizioneHelper.setDataScadenza(InitIscrizioneAction.calcolaDataOra(
				dettBando.getDatiGeneraliBandoIscrizione().getDataFineIscrizione(), 
				dettBando.getDatiGeneraliBandoIscrizione().getOraFineIscrizione(), 
				true));
		iscrizioneHelper.setIdBando(this.codice);
		iscrizioneHelper.setDescBando(dettBando.getDatiGeneraliBandoIscrizione().getOggetto());
		iscrizioneHelper.setCategorieAssenti(listaCategorie == null || listaCategorie.length == 0);
		iscrizioneHelper.setTipoClassifica(dettBando.getDatiGeneraliBandoIscrizione().getTipoClassifica());
		iscrizioneHelper.setUnicaStazioneAppaltante(listaStazioni.size() == 1);		
		if (iscrizioneHelper.isUnicaStazioneAppaltante()) {
			// si fissa la stazione appaltante in modo da non
			// richiederla all'utente
			Set<String> setChiavi = listaStazioni.keySet();
			for (Iterator<String> iterator = setChiavi.iterator(); iterator.hasNext();) {
				// si ha un ciclo su un unico elemento, e lo si estrae
				iscrizioneHelper.getStazioniAppaltanti().add((String) iterator.next());
			}
		}

		iscrizioneHelper.setRichiestaCoordinatoreSicurezza(
				dettBando.getDatiGeneraliBandoIscrizione().getCoordinatoreSicurezza());
		
		iscrizioneHelper.setRichiestaAscesaTorre(
				dettBando.getDatiGeneraliBandoIscrizione().getAscesaTorre());
		
		iscrizioneHelper.setIdComunicazioneBozza(
				getComunicazioneStatoBozza(
						iscrizioneHelper.getIdBando(), 
						PortGareSystemConstants.RICHIESTA_ISCRIZIONE_ALBO,
						this.getCurrentUser().getUsername()));
		
		WizardDatiImpresaHelper impresa = ImpresaAction.getLatestDatiImpresa(
				this.getCurrentUser().getUsername(), 
				this);
		iscrizioneHelper.setImpresa(impresa);

		return iscrizioneHelper;
	}

	/**
	 * Verifica se esiste una comunicazione in stato BOZZA relativa 
	 * alla domanda per poter presentare a video i pulsanti di 
	 * "Completamento rinnovo" o "Completamento iscrizione"
	 * Restituisce l'id della comunicazione in stato BOZZA, se esiste
	 *  
	 * @throws ApsException 
	 */
	public static Long getComunicazioneStatoBozza(
			String codice,
			String tipoComunicazione,
			String username) throws ApsException 
	{ 
		Long id = null;
		
		IComunicazioniManager comunicazioniManager = (IComunicazioniManager) ApsWebApplicationUtils
			.getBean(CommonSystemConstants.COMUNICAZIONI_MANAGER,
					 ServletActionContext.getRequest());

		DettaglioComunicazioneType dettComunicazione = ComunicazioniUtilities
			.retrieveComunicazione(
				comunicazioniManager,
				username,
				codice, 
				null,
				CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
				tipoComunicazione);
		if (dettComunicazione != null) {
			id = dettComunicazione.getId();
		}
		return id;
	}

	/**
	 * ...
	 */
	public String reloadIscrizione() {
		return this.reloadIscrizione(PortGareSystemConstants.TIPOLOGIA_ELENCO_STANDARD);
	}

	/**
	 * ...
	 */
	public String reloadCatalogoIscrizione() {
		return this.reloadIscrizione(PortGareSystemConstants.TIPOLOGIA_ELENCO_CATALOGO);
	}

	/**
	 * Richiede al backoffice i dati inseriti in precedenza in stato di bozza nel
	 * wizard di registrazione all'albo e memorizza tali dati in sessione
	 */
	private String reloadIscrizione(int tipologia) {
		
		EFlussiAccessiDistinti flusso = (tipologia == PortGareSystemConstants.TIPOLOGIA_ELENCO_STANDARD
				? EFlussiAccessiDistinti.ISCRIZIONE_ELENCO 
				: EFlussiAccessiDistinti.ISCRIZIONE_CATALOGO);

		// verifica il profilo di accesso ed esegui un LOCK alla funzione 
		if( !lockAccessoFunzione(flusso, this.codice) ) {
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			return this.getTarget();
		}

		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			Boolean datiInterpretati = Boolean.FALSE;
			try {
				// si estraggono dal B.O. i dati del bando a cui
				// iscriversi
				DettaglioBandoIscrizioneType dettBando = this.bandiManager
						.getDettaglioBandoIscrizione(this.codice);

				// si calcola il timestamp ntp
				checkFaseWithTimestampNTP(1, dettBando, "Completamento iscrizione", "BUTTON_ISCRALBO_RICHIESTA_ISCRIZIONE");

				// si determina se viene gestita una o piu' stazioni
				// appaltanti
				LinkedHashMap<String, String> listaStazioni = this.bandiManager
						.getElencoStazioniAppaltantiPerIscrizione(this.codice);

				CategoriaBandoIscrizioneType[] listaCategorie = this.bandiManager
						.getElencoCategorieBandoIscrizione(this.codice, null);
				Set<String> setCategorieFoglia = new HashSet<String>();
				if(listaCategorie != null) {
					for (CategoriaBandoIscrizioneType cat : listaCategorie) {
						if (cat.isFoglia()) {
							setCategorieFoglia.add(cat.getCodice());
						}
					}
				}

				// si crea il contenitore da porre in sessione
				WizardIscrizioneHelper iscrizioneHelper = null;
				if (SUCCESS.equals(this.getTarget())) {
					iscrizioneHelper = this.createHelper(
							dettBando,
							listaStazioni, 
							listaCategorie, 
							tipologia);
	
					iscrizioneHelper.setIscrizioneDomandaVisible(this.customConfigManager.isVisible("ISCRALBO-DOCUM", "PDFDOMANDA"));
					if(iscrizioneHelper.isIscrizioneDomandaVisible()) {
						iscrizioneHelper.setListaFirmatariMandataria(iscrizioneHelper.composeListaFirmatariMandataria());
					}

					if (dettBando.getDatiGeneraliBandoIscrizione().getAmmesseRTI()) {
						iscrizioneHelper.setAmmesseRTI(true);
					}
					if (iscrizioneHelper.isAmmesseRTI()) {
						iscrizioneHelper.setEditRTI(true);
					}
					this.setTarget(this.setDatiWizardIscrizioneRinnovoFromBozza(
											this.getTarget(), 
											iscrizioneHelper, 
											setCategorieFoglia));
					datiInterpretati = true;
				}
				
				// (QFORM - QUESTIONARI)
				this.setTarget(this.initQuestionario(iscrizioneHelper));
				
				iscrizioneHelper.fillStepsNavigazione();
				if(!iscrizioneHelper.isIscrizioneDomandaVisible()) {
					iscrizioneHelper.getStepNavigazione().remove(WizardIscrizioneHelper.STEP_SCARICA_ISCRIZIONE);
				}

				if(this.getTarget().equals(MODULISTICA_CAMBIATA)) {
					//this.nextResultAction = "openQuestionarioModuloCambiato";
				} else {
					if (SUCCESS.equals(this.getTarget())) {
						this.session.put(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO, iscrizioneHelper);						
						this.nextResultAction = iscrizioneHelper.getNextAction("");		// vai al primo step del wizard
					}
				}
			
			} catch (XmlException e) {
				ApsSystemUtils.logThrowable(e, this, "reloadIscrizione");
				this.addActionError(this.getText("Errors.reloadIscrizione.parseXml"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (IOException e) {
				ApsSystemUtils.logThrowable(e, this, "reloadIscrizione");
				if (!datiInterpretati.booleanValue()) {
					this.addActionError(this.getText("Errors.reloadIscrizione.bufferXml"));
				} else {
					this.addActionError(this.getText("Errors.reloadIscrizione.tempFileAllegati"));
				}
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (Exception e) {
				ApsSystemUtils.logThrowable(e, this, "reloadIscrizione");
				this.addActionError(this.getText("Errors.reloadIscrizione.config"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (OutOfMemoryError e) {
				ApsSystemUtils.logThrowable(e, this, "reloadIscrizione");
				this.addActionError(this.getText("Errors.reloadIscrizione.outOfMemory"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "reloadIscrizione");
				ExceptionUtils.manageExceptionError(e, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}

		this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, 
						 WizardIscrizioneHelper.STEP_IMPRESA);

		return this.getTarget();
	}

	/**
	 * Se fromBozza = true, 
	 * si estraggono i dati del wizard d'iscrizione riprendendo la precedente
	 * comunicazione in stato di bozza. Vengono popolati nel wizard tutti i 
	 * dati inseriti in precedenza eccetto i dati dell'impresa.
	 * 
	 * Se fromBozza = false,
	 * si estraggono i dati del wizard d'iscrizione riprendendo la precedente
	 * comunicazione rinnovo, aggiornamento, iscrizione, in stato da processare 
	 * o processata. Vengono popolati nel wizard tutti i dati inseriti in 
	 * precedenza eccetto i dati dell'impresa.
	 *
	 * @param target target dell'operazione
	 * @param helper helper da porre in sessione (iscrizione/rinnovo)
	 * @param setCategorieFoglia set di categorie corrispondenti a foglie
	 *        dell'albero
	 * @param fromBozza indica che tipo di comunicazione cercare per inizializzare
	 *        il wizard 
	 * @return target aggiornato
	 * 
	 * @throws Exception
	 */
	private String setDatiWizardIscrizioneRinnovo(
			String target,
			WizardIscrizioneHelper helper, 
			Set<String> setCategorieFoglia,
			boolean fromBozza) throws Exception 
	{
		// si estraggono dal B.O. i dati dell'iscrizione
		DettaglioComunicazioneType criteriRicerca = new DettaglioComunicazioneType();
		criteriRicerca.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
		criteriRicerca.setChiave1(this.getCurrentUser().getUsername());
		criteriRicerca.setChiave2(this.codice);
				
		ComunicazioneType comunicazione = null;
		List<DettaglioComunicazioneType> comunicazioni = null;
	
		helper.setIdComunicazioneBozza(null);

		if(fromBozza) {
			// cerca l'ultima comunicazione di ISCRIZIONE in stato BOZZA... 
			comunicazioni = ricercaComunicazioni(setRicercaIscrizioneBozza(criteriRicerca));
	
			if (comunicazioni == null || comunicazioni.size() != 1) {
				// non dovrebbe succedere mai...si inserisce questo
				// controllo esclusivamente per blindare il codice da
				// eventuali comportamenti anomali
				this.addActionError(this.getText("Errors.reloadIscrizione.iscrizioneNotUnique"));
				target = CommonSystemConstants.PORTAL_ERROR;
			} else {
				comunicazione = this.comunicazioniManager.getComunicazione(
						CommonSystemConstants.ID_APPLICATIVO,
						comunicazioni.get(0).getId());
				helper.setIdComunicazione(comunicazione.getDettaglioComunicazione().getId());
			}
	
			boolean controlliOK = ricalcoloDaComunicazione(
					comunicazione, 
					comunicazione.getDettaglioComunicazione().getId(),
					helper, 
					setCategorieFoglia);
			if(!controlliOK) {
				// non dovrebbe succedere mai...si inserisce questo
				// controllo per blindare il codice da eventuali
				// comportamenti anomali
				this.addActionError(this.getText("Errors.reloadIscrizione.xmlIscrizioneNotFound"));
				target = CommonSystemConstants.PORTAL_ERROR;
			}
			
		} else {
			// 1) cerca la comunicazione in stato BOZZA relativa al tipo di
			//    domanda...
			// 2) se non esiste, cerca la comunicazione di RINNOVO, 
			//    AGGIORNAMENTO, ISCRIZIONE in stato DA PROCESSARE o PROCESSATA 
			//    più recente, quindi quella con id comunicazione maggiore...
			String tipoDomanda = PortGareSystemConstants.RICHIESTA_ISCRIZIONE_ALBO;
			if(helper.isRinnovoIscrizione()) {
				tipoDomanda = PortGareSystemConstants.RICHIESTA_RINNOVO_ISCRIZIONE;
			} else if(helper.isAggiornamentoIscrizione()) {
				tipoDomanda = PortGareSystemConstants.RICHIESTA_AGGIORNAMENTO_ISCRIZIONE_ALBO;
			}
			
			long idComunicazione = -1;
			long idComFirmatari = -1;
			comunicazioni = ricercaComunicazioni(
					setRicercaComunicazioni(criteriRicerca, tipoDomanda, CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA));
			
			if(comunicazioni != null && comunicazioni.size() > 1) {
				// NON dovrebbe mai succedere!!!
				throw new ApsException("Errors.retrieveComunicazione.notUnique");
				
			} else if(comunicazioni != null && comunicazioni.size() == 1) {
				// trovata la comunicazione in stato BOZZA relativa al tipo di domanda...
				// si tratta quindi del completamento di una domanda 
				// precedentemente inziata ma non completata/inviata!!!
				idComunicazione = comunicazioni.get(0).getId();
				helper.setIdComunicazioneBozza(idComunicazione); 
				
			} else {
				// cerca la precedente comunicazione di RINNOVO, AGGIORNAMENTO, ISCRIZIONE 
				// in stato DA PROCESSARE o PROCESSATA... 
				// In caso di AGGIORNAMENTO non vanno considerate le precedenti comunicazioni di RINNOVO
				// perche' nei rinnovi non ci sono le informazioni delle categorie (e dei questionari)
				// I dati dei firmatari vanno recuperati sempre dall'ultima comunicazione (max(idcom)) 
				// tra tutte le FS2,FS3,FS4
				comunicazioni = ricercaComunicazioni(
						setRicercaComunicazioni(criteriRicerca, PortGareSystemConstants.RICHIESTA_RINNOVO_ISCRIZIONE, CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE));
				for(int i = 0; i < comunicazioni.size(); i++) {
					if( !helper.isAggiornamentoIscrizione() ) {
						// solo il rinnovo puo' recuperare da un precedente rinnovo 
						idComunicazione = Math.max(idComunicazione, comunicazioni.get(i).getId());
					}
					idComFirmatari = Math.max(idComFirmatari, comunicazioni.get(i).getId());
				}
				
				comunicazioni = ricercaComunicazioni(
						setRicercaComunicazioni(criteriRicerca, PortGareSystemConstants.RICHIESTA_RINNOVO_ISCRIZIONE, CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA));
				for(int i = 0; i < comunicazioni.size(); i++) {
					if( !helper.isAggiornamentoIscrizione() ) {
						// solo il rinnovo puo' recuperare da un precedente rinnovo
						idComunicazione = Math.max(idComunicazione, comunicazioni.get(i).getId());
					}
					idComFirmatari = Math.max(idComFirmatari, comunicazioni.get(i).getId());
				}
				
				comunicazioni = ricercaComunicazioni(
						setRicercaComunicazioni(criteriRicerca, PortGareSystemConstants.RICHIESTA_AGGIORNAMENTO_ISCRIZIONE_ALBO, CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE));
				for(int i = 0; i < comunicazioni.size(); i++) {
					idComunicazione = Math.max(idComunicazione, comunicazioni.get(i).getId());
					idComFirmatari = Math.max(idComFirmatari, comunicazioni.get(i).getId());
				}
				
				comunicazioni = ricercaComunicazioni(
						setRicercaComunicazioni(criteriRicerca, PortGareSystemConstants.RICHIESTA_AGGIORNAMENTO_ISCRIZIONE_ALBO, CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA));
				for(int i = 0; i < comunicazioni.size(); i++) {
					idComunicazione = Math.max(idComunicazione, comunicazioni.get(i).getId());
					idComFirmatari = Math.max(idComFirmatari, comunicazioni.get(i).getId());
				}
		
				comunicazioni = ricercaComunicazioni(
						setRicercaComunicazioni(criteriRicerca, PortGareSystemConstants.RICHIESTA_ISCRIZIONE_ALBO, CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE));
				for(int i = 0; i < comunicazioni.size(); i++) {
					idComunicazione = Math.max(idComunicazione, comunicazioni.get(i).getId());
					idComFirmatari = Math.max(idComFirmatari, comunicazioni.get(i).getId());
				}
		
				comunicazioni = ricercaComunicazioni(
						setRicercaComunicazioni(criteriRicerca, PortGareSystemConstants.RICHIESTA_ISCRIZIONE_ALBO, CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA));
				for(int i = 0; i < comunicazioni.size(); i++) {
					idComunicazione = Math.max(idComunicazione, comunicazioni.get(i).getId());
					idComFirmatari = Math.max(idComFirmatari, comunicazioni.get(i).getId());
				}
			}
			
			// 2) se esiste, recupera i dati dalla comunicazione più aggiornata
			//    altrimenti recupera i dati dal BO
			if (idComunicazione > 0) {
				comunicazione = this.getComunicazioniManager().getComunicazione(
						CommonSystemConstants.ID_APPLICATIVO,
						idComunicazione);
				
				boolean controlliOK = ricalcoloDaComunicazione(
						comunicazione,
						idComFirmatari,
						helper, 
						setCategorieFoglia);
				if (!controlliOK) {
					// non dovrebbe succedere mai...si inserisce questo
					// controllo per blindare il codice da eventuali
					// comportamenti anomali
					this.addActionError(this.getText("Errors.reloadIscrizione.xmlIscrizioneNotFound"));
					target = CommonSystemConstants.PORTAL_ERROR;
				}
			} else {
				// se non lo trovo in nessuno dei due casi precedenti cerco i dati in BO
				List<MandanteRTIType> mandantiBO = this.getBandiManager().getMandantiRTI(
						this.getCodice(), 
						this.getCurrentUser().getUsername(),
						null);
	
				if(mandantiBO.size() > 0) {
					popolaFromBO(mandantiBO, helper);
				}

				// non avendo una comunicazione pregressa da leggere, la lista dei firmatari si recupera dai dati dell'impresa
				helper.setListaFirmatariMandataria(helper.composeListaFirmatariMandataria());
			}
			
			// in caso di RTI imposta i flag per l'RTI
			if(helper.getComponentiRTI() != null && helper.getComponentiRTI().size() > 1) {
				helper.setRti(true);
			}
		}

		return target;
	}
	
	/**
	 * Si estraggono i dati del wizard d'iscrizione riprendendo la precedente
	 * comunicazione in stato di bozza. Vengono popolati nel wizard tutti i 
	 * dati inseriti in precedenza eccetto i dati dell'impresa.
	 */
	protected String setDatiWizardIscrizioneRinnovoFromBozza(
			String target,
			WizardIscrizioneHelper helper, 
			Set<String> setCategorieFoglia) throws Exception 
	{
		return this.setDatiWizardIscrizioneRinnovo(target, helper, setCategorieFoglia, true);
	}
	
	/**
	 * Si estraggono i dati del wizard d'iscrizione riprendendo la precedente
	 * comunicazione rinnovo, aggiornamento, iscrizione, in stato da processare 
	 * o processata. Vengono popolati nel wizard tutti i dati inseriti in 
	 * precedenza eccetto i dati dell'impresa.
	 */	
	protected String setDatiWizardIscrizioneRinnovoFromUltimaComunicazione(
			String target,
			WizardIscrizioneHelper helper, 
			Set<String> setCategorieFoglia) throws Exception 
	{
		return this.setDatiWizardIscrizioneRinnovo(target, helper, setCategorieFoglia, false);
	}

	/**
	 * inizializza la domanda di Iscrizione/Rinnovo/Aggiornamento dalla comunicazione
	 */	
	protected boolean ricalcoloDaComunicazione(
			ComunicazioneType comunicazione,
			long idComunicazioneFirmatari,
			WizardIscrizioneHelper helper, 
			Set<String> setCategorieFoglia) throws ApsException, XmlException, Exception
	{	
		boolean controlliOK = false;
		if(comunicazione != null) {
			String tipoComunicazione = comunicazione.getDettaglioComunicazione().getTipoComunicazione();
			long idComunicazione = comunicazione.getDettaglioComunicazione().getId().longValue();
			boolean popolaDocumentiDaBozza = (CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA.equals( 
											  comunicazione.getDettaglioComunicazione().getStato()));
			FirmatarioType[] firmatariImpresa = null;
			
			// recupera i dati dei firmatari dalla comunicazione (iscrizione, aggiornamento o rinnovo)...
			if(idComunicazioneFirmatari > 0 && idComunicazioneFirmatari != idComunicazione) {
				ComunicazioneType c = getComunicazioniManager().getComunicazione(
						CommonSystemConstants.ID_APPLICATIVO,
						idComunicazioneFirmatari);
				if(c != null) {
					XmlObject xml = getImpresaElencoOperatoriType(c);
					if(xml instanceof IscrizioneImpresaElencoOpType) {
						firmatariImpresa = ((IscrizioneImpresaElencoOpType) xml).getFirmatarioArray(); 
					} else if(xml instanceof AggIscrizioneImpresaElencoOpType) {
						firmatariImpresa = ((AggIscrizioneImpresaElencoOpType) xml).getFirmatarioArray();
					} else if(xml instanceof RinnovoIscrizioneImpresaElencoOperatoriType) {
						firmatariImpresa = ((RinnovoIscrizioneImpresaElencoOperatoriType) xml).getFirmatarioArray();
					}
				}
			}
			 
			// NB: se la comunicazione e' in stato BOZZA allora ricarica 
			//     nell'helper i documenti allegati della bozza...
			if(PortGareSystemConstants.RICHIESTA_RINNOVO_ISCRIZIONE.equals(tipoComunicazione)) {
				// DOMANDA RINNOVO ISCRIZIONE
				RinnovoIscrizioneImpresaElencoOperatoriType rinnovoImpresa = 
						(RinnovoIscrizioneImpresaElencoOperatoriType) getImpresaElencoOperatoriType(comunicazione);

				resetInfoQuestionariPerRinnovo(rinnovoImpresa, helper);
				
				if(popolaDocumentiDaBozza) {
					ComunicazioniUtilities.getAllegatiBustaFromComunicazione(comunicazione, rinnovoImpresa.getDocumenti());
					popolaDocumentiWizard(rinnovoImpresa.getDocumenti(), helper, false);
				}
				
				popolaFromRinnovo(rinnovoImpresa, firmatariImpresa, helper);
				refreshDatiFirmatari(helper);
				
			} else if(PortGareSystemConstants.RICHIESTA_AGGIORNAMENTO_ISCRIZIONE_ALBO.equals(tipoComunicazione)) {
				// DOMANDA AGGIORNAMENTO ISCRIZIONE
				AggIscrizioneImpresaElencoOpType aggIscrizioneImpresa = 
						(AggIscrizioneImpresaElencoOpType) getImpresaElencoOperatoriType(comunicazione);
	
				resetInfoQuestionariPerRinnovo(aggIscrizioneImpresa, helper);

				if(popolaDocumentiDaBozza) {
					ComunicazioniUtilities.getAllegatiBustaFromComunicazione(comunicazione, aggIscrizioneImpresa.getDocumenti());
					popolaDocumentiWizard(aggIscrizioneImpresa.getDocumenti(), helper, false);
					InitIscrizioneAction.setCategorie(
							helper, 
							aggIscrizioneImpresa.getCategorieIscrizione(),
							setCategorieFoglia, 
							false);
					if(aggIscrizioneImpresa.isSetQuestionarioId()) {
						helper.getDocumenti().setQuestionarioId(aggIscrizioneImpresa.getQuestionarioId());
						QuestionarioType questionario = WizardDocumentiHelper.getQuestionarioAssociatoBO(helper.getIdBando(), null, 0);
						helper.getDocumenti().setQuestionarioAssociato(questionario);
						aggIscrizioneImpresa.getQuestionarioId();
					}
				}
				
				setDatiRTI(aggIscrizioneImpresa, firmatariImpresa, helper);
				refreshDatiFirmatari(helper);
			
			} else if(PortGareSystemConstants.RICHIESTA_ISCRIZIONE_ALBO.equals(tipoComunicazione)) {
				// DOMANDA ISCRIZIONE 
				IscrizioneImpresaElencoOpType iscrizioneImpresa = 
						(IscrizioneImpresaElencoOpType) getImpresaElencoOperatoriType(comunicazione);
	
				resetInfoQuestionariPerRinnovo(iscrizioneImpresa, helper);
				
				if(popolaDocumentiDaBozza) {
					ComunicazioniUtilities.getAllegatiBustaFromComunicazione(comunicazione, iscrizioneImpresa.getDocumenti());
					popolaDocumentiWizard(iscrizioneImpresa.getDocumenti(), helper, false);
				}
				
				setDatiRTI(iscrizioneImpresa, firmatariImpresa, helper);
				popolaFromComunicazioneEsistente(
						helper, 
						iscrizioneImpresa, 
						setCategorieFoglia,
						popolaDocumentiDaBozza);
				refreshDatiFirmatari(helper);
			}
			
			if(popolaDocumentiDaBozza) {
		    	// NB: per il download dei documenti è necessario che 
		    	// WizardDocumentiHelper abbia valorizzato "idComunicazione" !
		    	// In linea generale è WizardIscrizioneHelper che imposta 
		    	// idComunicazione del proprio WizardDocumentiHelper, 
		    	// ma visto che per correggere un'altra segnalazione 
		    	// idComunicazione non viene aggiornato...
		    	// E' quindi necessario assegnare direttamente "idComunicazione"
		    	// in  WizardDocumentiHelper!!!
		    	helper.getDocumenti().setIdComunicazione(comunicazione.getDettaglioComunicazione().getId());
			}
			
			// (QFORM - QUESTIONARI) 
			// SOLO PER ISCRIZIONE E AGGIORNAMENTO ISCRIZIONE 
			// il questionario e' previsto SOLO in fase di iscrizione/aggiornamento elenco
			// quindi nel rinnovo NON serve recuperare dalla comunicazione precedente
			// i dati del questionario
			if( !popolaDocumentiDaBozza ) {
				boolean iscrizioneOAggiornamento = (!helper.isRinnovoIscrizione())
												    || helper.isAggiornamentoIscrizione();
				if(iscrizioneOAggiornamento) {
					AllegatoComunicazioneType questionario = getAllegatoComunicazione(comunicazione, DocumentiAllegatiHelper.QUESTIONARIO_ELENCHI_FILENAME);
					if(questionario != null) {
						byte[] contenuto = questionario.getFile();
					
						// se il contenuto dell'allegato non e' presente nella comunicazione, scaricalo dal servizio
						if(contenuto == null) {
							ComunicazioneType c = comunicazioniManager.getComunicazione(CommonSystemConstants.ID_APPLICATIVO, idComunicazione, questionario.getUuid());
							contenuto = (c != null ? c.getAllegato(0).getFile() : null);
						}
						
						// ricava dalla comunicazione precedente l'allegato "questionario.json" 
						// ed aggiungilo agli allegati dell'helper corrente (iscrizione/aggiornamento)
						if(contenuto != null) {
							helper.getDocumenti().addDocUlteriore(
									DocumentiAllegatiHelper.QUESTIONARIO_DESCR, 
									contenuto, 
									"JSON", 
									DocumentiAllegatiHelper.QUESTIONARIO_ELENCHI_FILENAME, 
									null,
									null,
									null);
							logger.debug("Inserito questionario presente nella comunicazione precedente.");
						}
					}
					logger.debug("Conclusa operazione di caricamento dati da comunicazione precedente {}", idComunicazione);
				}	
			}
			
			controlliOK = true;
		}
		
		return controlliOK;
	}
	
	/**
	 * estra il documento XML dall'allegato XML della comunicazione  
	 * @throws ApsException 
	 * @throws XmlException 
	 */
	private XmlObject getImpresaElencoOperatoriType(ComunicazioneType comunicazione) throws ApsException, XmlException {
		XmlObject xmlDoc = null;
		
		// estrai il documento XML dall'allegato xml
		if(PortGareSystemConstants.RICHIESTA_ISCRIZIONE_ALBO.equals(comunicazione.getDettaglioComunicazione().getTipoComunicazione())) {
			// DOMANDA ISCRIZIONE
			AllegatoComunicazioneType xml = getAllegatoComunicazione(
					comunicazione, 
					PortGareSystemConstants.NOME_FILE_ISCRIZIONE);
			if(xml != null) {
				IscrizioneImpresaElencoOperatoriDocument documento = IscrizioneImpresaElencoOperatoriDocument
						.Factory.parse(new String(xml.getFile()));
				IscrizioneImpresaElencoOpType iscrizioneImpresa = documento
						.getIscrizioneImpresaElencoOperatori();
				xmlDoc = iscrizioneImpresa;
			}
		} else if(PortGareSystemConstants.RICHIESTA_AGGIORNAMENTO_ISCRIZIONE_ALBO.equals(comunicazione.getDettaglioComunicazione().getTipoComunicazione())) {
			// DOMANDA AGGIORNAMENTO ISCRIZIONE
			AllegatoComunicazioneType xml = getAllegatoComunicazione(
					comunicazione, 
					PortGareSystemConstants.NOME_FILE_AGG_ISCRIZIONE);
			if(xml != null) {
				AggiornamentoIscrizioneImpresaElencoOperatoriDocument documento = AggiornamentoIscrizioneImpresaElencoOperatoriDocument
						.Factory.parse(new String(xml.getFile()));
				AggIscrizioneImpresaElencoOpType aggIscrizioneImpresa = documento
						.getAggiornamentoIscrizioneImpresaElencoOperatori();
				xmlDoc = aggIscrizioneImpresa;
			}			
		} else if(PortGareSystemConstants.RICHIESTA_RINNOVO_ISCRIZIONE.equals(comunicazione.getDettaglioComunicazione().getTipoComunicazione())) {
			// DOMANDA RINNOVO ISCRIZIONE
			AllegatoComunicazioneType xml = getAllegatoComunicazione(
					comunicazione, 
					PortGareSystemConstants.NOME_FILE_RINNOVO_ISCRIZIONE);
			if(xml != null) {
				RinnovoIscrizioneImpresaElencoOperatoriDocument documento = RinnovoIscrizioneImpresaElencoOperatoriDocument
						.Factory.parse(new String(xml.getFile()));
				RinnovoIscrizioneImpresaElencoOperatoriType rinnovoImpresa = documento
						.getRinnovoIscrizioneImpresaElencoOperatori();
				xmlDoc = rinnovoImpresa;
			}
		}
		return xmlDoc;
	}
	
	/**
	 * rimuove le informazioni relative ai questionari in un documento XML  
	 */
	private void resetInfoQuestionariPerRinnovo(XmlObject impresaElencoOperatoriType, WizardIscrizioneHelper helper) throws XmlException {
		// in caso di rinnovo l'allegato "questionario.json" non e' previsto
		// e non va preso in considerazione dalla comunicazione precente
		if(helper.isRinnovoIscrizione()) {
			ListaDocumentiType allegati = null;
			
			// esamina il documento xml della precedente comunicazione...
			if(impresaElencoOperatoriType instanceof RinnovoIscrizioneImpresaElencoOperatoriType) {
				// DOMANDA RINNOVO ISCRIZIONE
				RinnovoIscrizioneImpresaElencoOperatoriType rinnovoImpresa = (RinnovoIscrizioneImpresaElencoOperatoriType)impresaElencoOperatoriType;
				allegati = rinnovoImpresa.getDocumenti();
				// il rinnovo non prevede i questionari!!!
				
			} else if(impresaElencoOperatoriType instanceof AggIscrizioneImpresaElencoOpType) {
				// DOMANDA AGGIORNAMENTO ISCRIZIONE
				AggIscrizioneImpresaElencoOpType aggIscrizioneImpresa = (AggIscrizioneImpresaElencoOpType)impresaElencoOperatoriType;
				allegati = aggIscrizioneImpresa.getDocumenti();
				if(aggIscrizioneImpresa.isSetQuestionarioCompletato()) {
					aggIscrizioneImpresa.unsetQuestionarioCompletato();
				}
				if(aggIscrizioneImpresa.isSetQuestionarioId()) {
					aggIscrizioneImpresa.unsetQuestionarioId();
				}
				
			} else if(impresaElencoOperatoriType instanceof IscrizioneImpresaElencoOpType) {
				// DOMANDA ISCRIZIONE
				IscrizioneImpresaElencoOpType iscrizioneImpresa = (IscrizioneImpresaElencoOpType)impresaElencoOperatoriType;
				allegati = iscrizioneImpresa.getDocumenti();
				if(iscrizioneImpresa.isSetQuestionarioCompletato()) {
					iscrizioneImpresa.unsetQuestionarioCompletato();
				}
				if(iscrizioneImpresa.isSetQuestionarioId()) {
					iscrizioneImpresa.unsetQuestionarioId();
				}
			}
			
			if(allegati != null) {
				// rimuovi dagli allegati il documento "questionario.json"
				for(int i = allegati.sizeOfDocumentoArray() - 1; i >= 0; i--) {
					if(DocumentiAllegatiHelper.QUESTIONARIO_ELENCHI_FILENAME.equalsIgnoreCase(allegati.getDocumentoArray()[i].getNomeFile())) {
						allegati.removeDocumento(i);
					}
				}
			}
		}
	}
	
	/**
	 * popola da BO i componenti RTI 
	 */
	protected void popolaFromBO(
			List<MandanteRTIType> mandantiBO, 
			WizardIscrizioneHelper iscrizioneHelper) throws ApsException 
	{
		setComponentiRTIFromBO(mandantiBO, iscrizioneHelper);
		iscrizioneHelper.setRti(true);
		iscrizioneHelper.setAmmesseRTI(true);
		
		ArrayList<IComponente> boRTI = iscrizioneHelper.getComponentiRTI();
		
		for(int i = 0; i < boRTI.size(); i++) {	
			SoggettoFirmatarioImpresaHelper firmatario = new SoggettoFirmatarioImpresaHelper();
			iscrizioneHelper.getComponentiRTI().addFirmatario(boRTI.get(i), firmatario);
		}
	}

	/**
	 * ... 
	 */
	private void setComponentiRTIFromBO(
			List<MandanteRTIType> mandantiBO, 
			WizardIscrizioneHelper iscrizioneHelper)
	{
		// mandataria
		IComponente mandataria = new ComponenteHelper();
		mandataria.setCodiceFiscale(iscrizioneHelper.getImpresa().getDatiPrincipaliImpresa().getCodiceFiscale());
		mandataria.setPartitaIVA(iscrizioneHelper.getImpresa().getDatiPrincipaliImpresa().getPartitaIVA());
		mandataria.setRagioneSociale(iscrizioneHelper.getImpresa().getDatiPrincipaliImpresa().getRagioneSociale());
		mandataria.setTipoImpresa(iscrizioneHelper.getImpresa().getDatiPrincipaliImpresa().getTipoImpresa());
		mandataria.setAmbitoTerritoriale(iscrizioneHelper.getImpresa().getDatiPrincipaliImpresa().getAmbitoTerritoriale());
		if("2".equals(iscrizioneHelper.getImpresa().getDatiPrincipaliImpresa().getAmbitoTerritoriale())) { 
			mandataria.setIdFiscaleEstero(iscrizioneHelper.getImpresa().getDatiPrincipaliImpresa().getCodiceFiscale());
		} else {
			mandataria.setIdFiscaleEstero(null);
		}
		iscrizioneHelper.getComponentiRTI().add(mandataria);
		
		// mandanti
		for(int i = 0; i < mandantiBO.size();i++) {
			IComponente mandante = new ComponenteHelper();
			mandante.setCodiceFiscale(mandantiBO.get(i).getCodiceFiscale());
			mandante.setPartitaIVA(mandantiBO.get(i).getPartitaIVA());
			mandante.setRagioneSociale(mandantiBO.get(i).getRagioneSociale());
			if (mandantiBO.get(i).getTipologia() != null) {
				mandante.setTipoImpresa(mandantiBO.get(i).getTipologia().toString());
			}
			if (mandantiBO.get(i).getAmbitoTerritoriale() != null) {
				mandante.setTipoImpresa(mandantiBO.get(i).getAmbitoTerritoriale());
			}
			if (mandantiBO.get(i).getIdFiscaleEstero() != null) {
				mandante.setTipoImpresa(mandantiBO.get(i).getIdFiscaleEstero());
			}
			iscrizioneHelper.getComponenti().add(mandante);
			iscrizioneHelper.getComponentiRTI().add(mandante);
		}
	}

	/**
	 * Richiede al backoffice i dati dell'impresa per consentire la gestione del
	 * wizard di completamento dati o documenti della registrazione all'albo e
	 * memorizza tali dati in sessione.<br/>
	 * Si cerca l'ultima comunicazione in stato da processare (FS4 o FS2) per
	 * riprendere i dati dell'anagrafica, quindi prova leggendo i dati in
	 * backoffice in quanto vuol dire che i dati sono aggiornati e non ci sono
	 * comunicazioni con dati piu' aggiornati del backoffice.
	 * 
	 * @return il target dove andare
	 */
	public String updateIscrizione() {
		return this.update(1, PortGareSystemConstants.TIPOLOGIA_ELENCO_STANDARD);
	}

	/**
	 * ...
	 */
	public String updateCatalogoIscrizione() {
		return this.update(1, PortGareSystemConstants.TIPOLOGIA_ELENCO_CATALOGO);
	}

	/**
	 * ... 
	 */
	private String update(int matchFase, int tipologia) {
		
		EFlussiAccessiDistinti flusso = (tipologia == PortGareSystemConstants.TIPOLOGIA_ELENCO_STANDARD
				? EFlussiAccessiDistinti.ISCRIZIONE_ELENCO 
				: EFlussiAccessiDistinti.ISCRIZIONE_CATALOGO);

		// verifica il profilo di accesso ed esegui un LOCK alla funzione 
		if( !lockAccessoFunzione(flusso, this.codice) ) {
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			return this.getTarget();
		}

		boolean impresaSet = false;
		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			try {
				// si estraggono dal B.O. i dati del bando a cui iscriversi
				DettaglioBandoIscrizioneType dettBando = this.bandiManager
						.getDettaglioBandoIscrizione(this.codice);

				// si calcola il timestamp ntp
				checkFaseWithTimestampNTP(matchFase, dettBando, "Aggiornamento dati/documenti", "BUTTON_ISCRALBO_AGGIORNA_DATI_DOCUMENTI");

				// si determina se viene gestita una o piu' stazioni appaltanti
				LinkedHashMap<String, String> listaStazioni = this.bandiManager
						.getElencoStazioniAppaltantiPerIscrizione(this.codice);

				it.eldasoft.www.sil.WSGareAppalto.CategoriaBandoIscrizioneType[] listaCategorie = this.bandiManager
						.getElencoCategorieBandoIscrizione(this.codice, null);
				Set<String> setCategorieFoglia = new HashSet<String>();
				if (listaCategorie != null) {
					for (CategoriaBandoIscrizioneType cat : listaCategorie) {
						if (cat.isFoglia()) {
							setCategorieFoglia.add(cat.getCodice());
						}
					}
				}

				// si crea il contenitore da porre in sessione
				WizardIscrizioneHelper iscrizioneHelper = null;
				if (SUCCESS.equals(this.getTarget())) {
					iscrizioneHelper = this.createHelper(
							dettBando,
							listaStazioni, 
							listaCategorie, 
							tipologia);
					
					impresaSet = true;
					this.getLatestDatiIscrizione(iscrizioneHelper, setCategorieFoglia);
					iscrizioneHelper.setAggiornamentoIscrizione(true);
					if (matchFase == 2) {
						// nel caso di fase 2, la scadenza e' prorogata al
						// termine di validita' dell'elenco
						iscrizioneHelper.setDataScadenza(
								InitIscrizioneAction.calcolaDataOra(
										dettBando.getDatiGeneraliBandoIscrizione().getDataFineValidita(), 
										null, 
										true));
						iscrizioneHelper.setAggiornamentoSoloDocumenti(true);
					}
					
					if(tipologia == PortGareSystemConstants.TIPOLOGIA_ELENCO_STANDARD) {
						iscrizioneHelper.setIscrizioneDomandaVisible(this.customConfigManager.isVisible("ISCRALBO-DOCUM", "PDFDOMANDA"));
					}
					
					// se e' una domanda di solo aggiornamento documenti,
					// quindi i termini dell'iscrizione sono chiusi,
					// allora lo step per scaricare la domanda non deve mai  
					// essere visibile
					if(iscrizioneHelper.isAggiornamentoSoloDocumenti()) {
						iscrizioneHelper.setIscrizioneDomandaVisible(false);
					}
					
					// verifica se e' presente una RTI...
					//if(iscrizioneHelper.isIscrizioneDomandaVisible()) {
						this.setTarget(
								setDatiWizardIscrizioneRinnovoFromUltimaComunicazione(
										this.getTarget(), 
										iscrizioneHelper, 
										setCategorieFoglia));
					//}
						
					// (QFORM - QUESTIONARI)
					this.setTarget( this.initQuestionario(iscrizioneHelper) );
					
					iscrizioneHelper.fillStepsNavigazione();
				}
				
				if(this.getTarget().equals(MODULISTICA_CAMBIATA)) {
					//this.nextResultAction = "openQuestionarioModuloCambiato";
				} else {
					if (SUCCESS.equals(this.getTarget())) {
						this.session.put(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO, iscrizioneHelper);
						this.nextResultAction = iscrizioneHelper.getNextAction("");		// vai al primo step del wizard
					}
				}

			} catch (XmlException e) {
				ApsSystemUtils.logThrowable(e, this, "updateIscrizione");
				if (!impresaSet) {
					this.addActionError(this.getText("Errors.aggiornamentoAnagrafica.parseXml"));
				} else {
					this.addActionError(this.getText("Errors.updateIscrizione.parseXml"));
				}
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (IOException e) {
				ApsSystemUtils.logThrowable(e, this, "updateIscrizione");
				this.addActionError(this.getText("Errors.updateIscrizione.tempFileAllegati"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (Exception e) {
				ApsSystemUtils.logThrowable(e, this, "updateIscrizione");
				this.addActionError(this.getText("Errors.updateIscrizione.config"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "updateIscrizione");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}

		return this.getTarget();
	}

	/**
	 * Richiede al backoffice i dati dell'impresa per consentire la gestione del
	 * wizard di completamento dei soli documenti della registrazione all'albo e
	 * memorizza tali dati in sessione.<br/>
	 * Si cerca l'ultima comunicazione in stato da processare (FS4 o FS2) per
	 * riprendere i dati dell'anagrafica, quindi prova leggendo i dati in
	 * backoffice in quanto vuol dire che i dati sono aggiornati e non ci sono
	 * comunicazioni con dati piu' aggiornati del backoffice.
	 * 
	 * @return il target dove andare
	 */
	public String updateDocIscrizione() {
		return this.update(2, PortGareSystemConstants.TIPOLOGIA_ELENCO_STANDARD);
	}

	/**
	 * ... 
	 */
	public String updateCatalogoDocIscrizione() {
		return this.update(2, PortGareSystemConstants.TIPOLOGIA_ELENCO_CATALOGO);
	}

	/**
	 * Estrae i dati aggiornati da riproporre nell'iscrizione.<br/>
	 * Si procede nel seguente modo:
	 * <ul>
	 * <li>Si ricerca l'ultima comunicazione di aggiornamento iscrizione in stato
	 * da processare: se esiste, vengono reperiti i dati delle categorie
	 * d'iscrizione, e vengono letti i documenti inseriti in precedenza in quanto
	 * la comunicazione andra' aggiornata inserendo i dati vecchi + i nuovi
	 * (<b>occhio che le stazioni appaltanti selezionate in passato vanno
	 * perse!</b>)</li>
	 * <li>Se non esiste, si ricerca se esiste la (se c’è è unica per impresa su 
	 * un elenco) comunicazione di aggiornamento iscrizione in stato processata 
	 * che ha una occorrenza in GARACQUISIZ con STATO=1; in tal caso si leggono 
	 * le categorie dalla comunicazione individuata 
	 * </li>
	 * <li>Se non esiste, si ricerca la comunicazione di iscrizione in stato da
	 * processare: se esiste, si reperiscono i dati delle categorie d'iscrizione
	 * </li>
	 * <li>Se non esiste, si ricercano nelle tabelle del backoffice le categorie
	 * effettivamente associate all'albo per l'impresa</li>
	 * </ul>
	 *
	 * @param iscrizioneHelper helper con i dati dell'iscrizione da popolare
	 * @param setCategorieFoglia set delle categorie corrispondenti a foglie
	 * dell'albero. Se NULL utilizza tutte le categorie
	 * @param username username della sessione
	 * @param codice codice univoco del bando d'iscrizione
	 * @param comunicazioniManager istanza a IComunicazioniManager 
	 * @param bandiManager istanza a IBandiManager
	 * @return restituisce FALSE se nell'ultima comunicazione da processare non
	 * ha almeno un allegato 
	 * @throws Exception
	 */
	public static boolean getLatestDatiIscrizione(
			WizardIscrizioneHelper iscrizioneHelper, 
			Set<String> setCategorieFoglia,
			String username, 
			String codice,
			IComunicazioniManager comunicazioniManager,
			IBandiManager bandiManager)
		throws Exception 
	{
		if(iscrizioneHelper == null) {
			return false;
		}

		// se setCategorieFoglia non è specificato 
		// caricalo automaticamente con tutte le categorie
		if(setCategorieFoglia == null) {
			setCategorieFoglia = new HashSet<String>();	
			it.eldasoft.www.sil.WSGareAppalto.CategoriaBandoIscrizioneType[] categorie = bandiManager
				.getElencoCategorieBandoIscrizione(codice, null);
			for (CategoriaBandoIscrizioneType cat : categorie) {
				if (cat.isFoglia()) {
					setCategorieFoglia.add(cat.getCodice());
				}
			}
			iscrizioneHelper.getCategorie().setFoglie( (HashSet<String>)setCategorieFoglia );
		}

		List<DettaglioComunicazioneType> comunicazioni = null;
		boolean iscrizione = false;

		// si estraggono dal B.O. i dati delle richieste di
		// aggiornamento inviate e non ancora processate; se ne trovo
		// almeno una, prendo l'ultima per poter rileggere i dati
		// dell'impresa da proporre nel form 
		DettaglioComunicazioneType criteriRicerca = new DettaglioComunicazioneType();
		criteriRicerca.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
		criteriRicerca.setChiave1(username);
		criteriRicerca.setChiave2(codice);
		criteriRicerca.setStato(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
		
		// si cercano le comunicazioni di aggiornamento 
		// (da processare o processate) (FS4 stato=5)
		criteriRicerca.setTipoComunicazione(PortGareSystemConstants.RICHIESTA_AGGIORNAMENTO_ISCRIZIONE_ALBO);
		comunicazioni = comunicazioniManager.getElencoComunicazioni(criteriRicerca);

		// se non si trovano comunicazioni di aggiornamento si cercano le
		// comunicazioni di iscrizione (FS4 stato=6), 
		// presenti in GARACQUISIZ stato=1
		if (comunicazioni == null || comunicazioni.size() == 0) {
			Long idComunicazionePosticipata = bandiManager
				.checkAggiornamentoIscrizionePosticipata(username, codice);
			if(idComunicazionePosticipata != null) {
				DettaglioComunicazioneType filtri = new DettaglioComunicazioneType();
				filtri.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
				filtri.setId(idComunicazionePosticipata);
				comunicazioni = comunicazioniManager.getElencoComunicazioni(filtri);
			}
		}
		
		if (comunicazioni != null && comunicazioni.size() == 1) {
			// AGGIORNAMENTO
			// carica le categoria dell'aggiornamento iscrizione posticipata
			ComunicazioneType comunicazione = comunicazioniManager
				.getComunicazione(CommonSystemConstants.ID_APPLICATIVO, 
								  comunicazioni.get(0).getId());
			
			XmlObject xml = getXmlIscrizione(comunicazione, false);
			
			setCategorieFromXml( 
					xml, 
					iscrizioneHelper, 
					setCategorieFoglia,
					false);

			setIscrizioneImpresaFromXml(
					xml, 
					iscrizioneHelper);

		} else { 
			// se non si trovano comunicazioni di aggiornamento si cercano le
			// comunicazioni di iscrizione (FS2 stato=5)
			if (comunicazioni == null || comunicazioni.size() == 0) {
				criteriRicerca.setTipoComunicazione(PortGareSystemConstants.RICHIESTA_ISCRIZIONE_ALBO);
				comunicazioni = comunicazioniManager.getElencoComunicazioni(criteriRicerca);
				iscrizione = comunicazioni.size() > 0;
			}
			
			if (comunicazioni == null || comunicazioni.size() == 0) {
				// AGGIORNAMENTO
				// non esistono comunicazioni in stato da processare di
				// aggiornamento o iscrizione albo, pertanto si prendono i dati
				// direttamente dal backoffice in quanto e' il posto piu' aggiornato
				// NB: 
				// nelle categorie viene resituito anche "coordinatoreSicurezza"
				// estratto da DITG ed è quindi uguale per tutte le categorie
				List<CategoriaImpresaType> listaCategorie = bandiManager
						.getCategorieImpresaPerIscrizione(username, codice);
				
				setCategorieFromBackoffice(
						iscrizioneHelper, 
						listaCategorie, 
						setCategorieFoglia, 
						false);
				
				// recupera altri dati relativi all'iscrizione
				ImpresaIscrizioneType impresa = bandiManager
						.getImpresaIscrizione(username, codice);
				
				setImpresaIscrizioneFromBackoffice(
						iscrizioneHelper, 
						impresa);
				
			} else {
				// ISCRIZIONE/AGGIORNAMENTO 
				// si individua l'ultima comunicazione in stato da processare 
				// (quella con id massimo, dato che e' un contatore)
				Long maxId = Long.valueOf(-1);
				for (int i = 0; i < comunicazioni.size(); i++) {
					if (comunicazioni.get(i).getId() > maxId) {
						maxId = comunicazioni.get(i).getId();
					}
				}
				logger.debug("trovata comunicazione con maxId: {}",maxId);
				// si estraggono i dati dall'ultima comunicazione in stato
				// da processare
				ComunicazioneType comunicazione = comunicazioniManager
						.getComunicazione(CommonSystemConstants.ID_APPLICATIVO, maxId);
				
				XmlObject xml = getXmlIscrizione(comunicazione, iscrizione);
			
				setCategorieFromXml( 
						xml, 
						iscrizioneHelper, 
						setCategorieFoglia,
						iscrizione);
				
				setIscrizioneImpresaFromXml(
						xml, 
						iscrizioneHelper);
			}
		}

		return true;
	}

	/**
	 * Estrae i dati aggiornati da riproporre nell'iscrizione.<br/>
	 * Si procede nel seguente modo:
	 * <ul>
	 * <li>Si ricerca l'ultima comunicazione di aggiornamento iscrizione in stato
	 * da processare: se esiste, vengono reperiti i dati delle categorie
	 * d'iscrizione, e vengono letti i documenti inseriti in precedenza in quanto
	 * la comunicazione andra' aggiornata inserendo i dati vecchi + i nuovi
	 * (<b>occhio che le stazioni appaltanti selezionate in passato vanno
	 * perse!</b>)</li>
	 * <li>Se non esiste, si ricerca se esiste la (se c’è è unica per impresa su
	 * un elenco) comunicazione di aggiornamento iscrizione in stato processata
	 * che ha una occorrenza in GARACQUISIZ con STATO=1; in tal caso si leggono
	 * le categorie dalla comunicazione individuata
	 * </li>
	 * <li>Se non esiste, si ricerca la comunicazione di iscrizione in stato da
	 * processare: se esiste, si reperiscono i dati delle categorie d'iscrizione
	 * </li>
	 * <li>Se non esiste, si ricercano nelle tabelle del backoffice le categorie
	 * effettivamente associate all'albo per l'impresa</li>
	 * </ul>
	 *
	 * @param iscrizioneHelper helper con i dati dell'iscrizione da popolare
	 * @param setCategorieFoglia set delle categorie corrispondenti a foglie
	 * dell'albero. Se NULL utilizza tutte le categorie
	 * @param username username della sessione
	 * @param codice codice univoco del bando d'iscrizione
	 * @param comunicazioniManager istanza a IComunicazioniManager
	 * @param bandiManager istanza a IBandiManager
	 * @return restituisce FALSE se nell'ultima comunicazione da processare non
	 * ha almeno un allegato
	 * @throws Exception
	 */
	public static WizardIscrizioneHelper retrieveNotProcessedCategories(
			String username
			, String codice
			, Set<String> setCategorieFoglia
			, IComunicazioniManager comunicazioniManager
			, IBandiManager bandiManager
	) throws Exception {
		WizardIscrizioneHelper iscrHelper = null;
		// se setCategorieFoglia non è specificato
		// caricalo automaticamente con tutte le categorie
		setCategorieFoglia =
				setCategorieFoglia == null
					? Arrays.stream(bandiManager.getElencoCategorieBandoIscrizione(codice, null))
							.filter(CategoriaBandoIscrizioneType::isFoglia)
							.map(CategoriaBandoIscrizioneType::getCodice)
						.collect(Collectors.toSet())
					: setCategorieFoglia;
		// si estraggono dal B.O. i dati delle richieste di
		// aggiornamento inviate e non ancora processate; se ne trovo
		// almeno una, prendo l'ultima per poter rileggere i dati
		// dell'impresa da proporre nel form
		DettaglioComunicazioneType criteriRicerca = new DettaglioComunicazioneType();
		criteriRicerca.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
		criteriRicerca.setChiave1(username);
		criteriRicerca.setChiave2(codice);
		criteriRicerca.setStato(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
		// si cercano le comunicazioni di aggiornamento
		// (da processare o processate) (FS4 stato=5)
		criteriRicerca.setTipoComunicazione(PortGareSystemConstants.RICHIESTA_AGGIORNAMENTO_ISCRIZIONE_ALBO);
		List<DettaglioComunicazioneType> comunicazioni = comunicazioniManager.getElencoComunicazioni(criteriRicerca);
		// se non si trovano comunicazioni di aggiornamento si cercano le
		// comunicazioni di iscrizione (FS4 stato=6),
		// presenti in GARACQUISIZ stato=1
		if (CollectionUtils.isEmpty(comunicazioni)) {
			Long idComunicazionePosticipata = bandiManager.checkAggiornamentoIscrizionePosticipata(username, codice);
			if (idComunicazionePosticipata != null) {
				DettaglioComunicazioneType filtri = new DettaglioComunicazioneType();
				filtri.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
				filtri.setId(idComunicazionePosticipata);
				comunicazioni = comunicazioniManager.getElencoComunicazioni(filtri);
			}
		}
		if (CollectionUtils.size(comunicazioni) == 1) {
			iscrHelper = new WizardIscrizioneHelper();
			// AGGIORNAMENTO
			// carica le categoria dell'aggiornamento iscrizione posticipata
			ComunicazioneType comunicazione = comunicazioniManager.getComunicazione(
					CommonSystemConstants.ID_APPLICATIVO
					, comunicazioni.get(0).getId()
			);

			XmlObject xml = getXmlIscrizione(comunicazione, false);

			setCategorieFromXml(
					xml
					, iscrHelper
					, setCategorieFoglia
					, false
			);

			setIscrizioneImpresaFromXml(xml, iscrHelper);
		}

		return iscrHelper;
	}

	/**
	 * Recupero le categorie assegnate ad un organizzazione per un elenco
	 * le recupero direttamente dal DB di appalti per non rischiare di
	 * avere problemi di inconsistenza, ovvero, che l'ultima FS4 ha n
	 * categorie, ma, magari da appalti sono state tolte alcune delle categorie
	 *
	 * @param username username della ditta
	 * @param codice codice dell'elenco
	 * @param setCategorieFoglia
	 * @param comunicazioniManager	manager per contattare il DB di Appalti
	 * @param bandiManager	altro manager per contattare il DB di Appalti
	 * @return Wizard contenente i dati richiesti (le categorie)
	 * @throws Exception
	 */
	public static WizardIscrizioneHelper retrieveProcessedCategories(
			String username
			, String codice
			, Set<String> setCategorieFoglia
			, IComunicazioniManager comunicazioniManager
			, IBandiManager bandiManager
	) throws Exception {
		WizardIscrizioneHelper iscrHelper = new WizardIscrizioneHelper();
		// se setCategorieFoglia non è specificato
		// caricalo automaticamente con tutte le categorie
		setCategorieFoglia =
				setCategorieFoglia == null
				? Arrays.stream(bandiManager.getElencoCategorieBandoIscrizione(codice, null))
						.filter(CategoriaBandoIscrizioneType::isFoglia)
						.map(CategoriaBandoIscrizioneType::getCodice)
					.collect(Collectors.toSet())
				: setCategorieFoglia;

		// AGGIORNAMENTO
		// non esistono comunicazioni in stato da processare di
		// aggiornamento o iscrizione albo, pertanto si prendono i dati
		// direttamente dal backoffice in quanto e' il posto piu' aggiornato
		// NB:
		// nelle categorie viene resituito anche "coordinatoreSicurezza"
		// estratto da DITG ed è quindi uguale per tutte le categorie
		List<CategoriaImpresaType> listaCategorie = bandiManager.getCategorieImpresaPerIscrizione(username, codice);

		setCategorieFromBackoffice(
				iscrHelper
				, listaCategorie
				, setCategorieFoglia
				, false
		);

		// recupera altri dati relativi all'iscrizione
		ImpresaIscrizioneType impresa = bandiManager.getImpresaIscrizione(username, codice);

		setImpresaIscrizioneFromBackoffice(iscrHelper, impresa);

		return iscrHelper;
	}

	/**
	 * Estrae i dati aggiornati da riproporre nell'iscrizione.<br/>
	 * Si procede nel seguente modo:
	 * <ul>
	 * <li>Si ricerca l'ultima comunicazione di aggiornamento iscrizione in stato
	 * da processare: se esiste, vengono reperiti i dati delle categorie
	 * d'iscrizione, e vengono letti i documenti inseriti in precedenza in quanto
	 * la comunicazione andra' aggiornata inserendo i dati vecchi + i nuovi
	 * (<b>occhio che le stazioni appaltanti selezionate in passato vanno
	 * perse!</b>)</li>
	 * <li>Se non esiste, si ricerca la comunicazione di iscrizione in stato da
	 * processare: se esiste, si reperiscono i dati delle categorie d'iscrizione
	 * </li>
	 * <li>Se non esiste, si ricercano nelle tabelle del backoffice le categorie
	 * effettivamente associate all'albo per l'impresa</li>
	 * </ul>
	 *
	 * @param iscrizioneHelper 
	 * 			helper con i dati dell'iscrizione da popolare
	 * @param setCategorieFoglia 
	 * 			set delle categorie corrispondenti a foglie dell'albero
	 * @param iscrizioneHelper
	 * @param setCategorieFoglia
	 * 
	 * @throws Exception
	 */
	private void getLatestDatiIscrizione(
			WizardIscrizioneHelper iscrizioneHelper, 
			Set<String> setCategorieFoglia) throws Exception 
	{		
		boolean allegato = getLatestDatiIscrizione(
				iscrizioneHelper, 
				setCategorieFoglia,
				this.getCurrentUser().getUsername(),
				this.codice,
				this.comunicazioniManager,
				this.bandiManager);
		
		if(!allegato) {
			// non dovrebbe succedere mai...si inserisce questo
			// controllo per blindare il codice da eventuali
			// comportamenti anomali
			this.addActionError(this.getText("Errors.updateIscrizione.xmlNotFound"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
	}
	
	/**
	 * Compone i parametri in modo da creare un oggetto data costituito da data ed
	 * ora.
	 *
	 * @param data 
	 * 			data
	 * @param ora 
	 * 			ora (ore e minuti separati da ":")
	 * @param giornoIncluso 
	 * 			parametro considerato solo in caso di ora non valorizzata; 
	 * 			true se si include l'intero giorno (quindi fino alle 23.59.59), 
	 * 			false altrimenti (quindi dalle 00.00.01)
	 * 
	 * @return data ora composta
	 */
	public static Date calcolaDataOra(Date data, String ora, boolean giornoIncluso) {
		Date risultato = null;
		if (data != null) {
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(data);
			int ore = 24;
			int minuti = 0;
			if (!giornoIncluso) {
				ore = 0;
				minuti = 0;
			}
			if (ora != null && !"".equals(ora)) {
				ore = Integer.parseInt(ora.substring(0, ora.indexOf(':')));
				minuti = Integer.parseInt(ora.substring(ora.indexOf(':') + 1));
			}
			gc.set(Calendar.HOUR_OF_DAY, ore);
			gc.set(Calendar.MINUTE, minuti);
			gc.set(Calendar.SECOND, 0);
			gc.set(Calendar.MILLISECOND, 0);
			risultato = gc.getTime();
		}
		return risultato;
	}

	/**
	 * Calcola la fase del bando d'iscrizione.
	 *
	 * @param dataOraAttuale data attuale
	 * @param bandoIscrizione dati di dettaglio del bando d'iscrizione
	 * @return fase calcolata:
	 * <ul>
	 * <li>0: iscrizione chiusa (prima di data inizio o dopo il termine di
	 * validit&agrave;)</li>
	 * <li>1: iscrizione aperta</li>
	 * <li>2: iscrizione chiusa ma termine di validit&agrave; non ancora
	 * scaduto</li>
	 * </ul>
	 */
	public static int calcolaFase(
			Date dataOraAttuale,
			BandoIscrizioneType bandoIscrizione) 
	{
		int risultato = 0;

		Date dataInizioIscrizione = InitIscrizioneAction.calcolaDataOra(
				bandoIscrizione.getDataInizioIscrizione(),
				bandoIscrizione.getOraInizioIscrizione(), false);
		Date dataFineIscrizione = InitIscrizioneAction.calcolaDataOra(
				bandoIscrizione.getDataFineIscrizione(),
				bandoIscrizione.getOraFineIscrizione(), true);
		Date dataFineValidita = InitIscrizioneAction.calcolaDataOra(
				bandoIscrizione.getDataFineValidita(), null, true);

		if ((dataInizioIscrizione != null && dataOraAttuale.compareTo(dataInizioIscrizione) >= 0)
			&& (dataFineIscrizione == null || dataOraAttuale.compareTo(dataFineIscrizione) <= 0)) {
			risultato = 1;
		} else if ((dataFineIscrizione != null && dataOraAttuale.compareTo(dataFineIscrizione) > 0)
				   && (dataFineValidita == null || dataOraAttuale.compareTo(dataFineValidita) <= 0)) {
			risultato = 2;
		}

		return risultato;
	}

	/**
	 * Popola il contenitore da porre in sessione a partire dai dati letti dal
	 * backoffice ed in stato bozza.<br/>
	 * <b>NB: i dati dell'impresa non vengono aggiornati!</b>
	 *
	 * @param iscrizioneHelper contenitore da popolare
	 * @param iscrizioneImpresa contenitore con i dati ricevuti dal backoffice
	 * @param setCategorieFoglia set di categorie corrispondenti a foglie
	 * dell'albero
	 * @throws Exception
	 */
	private void popolaFromComunicazioneEsistente(
			WizardIscrizioneHelper iscrizioneHelper,
			IscrizioneImpresaElencoOpType iscrizioneImpresa,
			Set<String> setCategorieFoglia,
			boolean fromBozza) throws Exception 
	{
		ListaStazioniAppaltantiType listaSA = iscrizioneImpresa.getStazioniAppaltanti();

//		iscrizioneHelper.setFromBozza(true);
		iscrizioneHelper.setFromBozza(fromBozza);

		for (int i = 0; i < listaSA.sizeOfStazioneAppaltanteArray(); i++) {
			iscrizioneHelper.getStazioniAppaltanti().add(listaSA.getStazioneAppaltanteArray()[i]);
		}
		
		// categorie
		InitIscrizioneAction.setCategorie(
				iscrizioneHelper, 
				iscrizioneImpresa.getCategorieIscrizione(),
				setCategorieFoglia, 
				false);
		
		// serial number
		if (iscrizioneImpresa.isSetSerialNumberMarcaBollo()) {
			iscrizioneHelper.setSerialNumberMarcaBollo(iscrizioneImpresa.getSerialNumberMarcaBollo());
		}
		
		// documenti
		if(fromBozza) {
			this.popolaDocumentiWizard(iscrizioneImpresa.getDocumenti(), iscrizioneHelper, false);
		}
		
		if(iscrizioneHelper.isRti() 
		   || StringUtils.isNotEmpty(StringUtils.stripToNull(iscrizioneImpresa.getDenominazioneRTI()))) {
			// dati RTI
			iscrizioneHelper.setRti(true);
			this.setDatiRTI(iscrizioneImpresa, null, iscrizioneHelper);
		} else if(iscrizioneHelper.getImpresa().isConsorzio()) {
			//Ripopolamento da bozza per consorzio
			this.setDatiConsorzio(iscrizioneHelper, iscrizioneImpresa);
		}
		
		if(iscrizioneImpresa.getFirmatarioArray().length > 0) {
			FirmatarioType firmatarioXML = iscrizioneImpresa.getFirmatarioArray(0);
			FirmatarioBean firmatarioSelezionato = new FirmatarioBean();
			firmatarioSelezionato.setIndex(0);
			//firmatarioSelezionato.setLista(firmatarioXML.getQualifica());
			if("1-".equals(firmatarioXML.getQualifica())) {
				firmatarioSelezionato.setLista("LEGALI_RAPPRESENTANTI");
			} else if ("2-".equals(firmatarioXML.getQualifica())) {
				firmatarioSelezionato.setLista("DIRETTORI_TECNICI");
			} else {
				firmatarioSelezionato.setLista("ALTRE_CARICHE");
			}
			firmatarioSelezionato.setNominativo(firmatarioXML.getCognome() + " " + firmatarioXML.getNome());
			iscrizioneHelper.setFirmatarioSelezionato(firmatarioSelezionato);
		}		
		
		// possesso requisiti coordinatore di sicurezza
//		iscrizioneHelper.setRequisitiCoordinatoreSicurezza(false);
		if (iscrizioneImpresa.isSetRequisitiCoordinatoreSicurezza()) {
			iscrizioneHelper.setRequisitiCoordinatoreSicurezza( iscrizioneImpresa.getRequisitiCoordinatoreSicurezza() );
		}
		
		// requisiti Ascesa Torre
		if (iscrizioneImpresa.isSetRequisitiAscesaTorre()) {
			iscrizioneHelper.setRequisitiAscesaTorre( iscrizioneImpresa.getRequisitiAscesaTorre() );
		}
		
		// (QFORM - QUESTIONARI)
		// in caso di RINNOVO i dati del questionario NON sono previsti
		if( !iscrizioneHelper.isRinnovoIscrizione() ) {
			QuestionarioType questionario = null;
			if (iscrizioneImpresa.isSetQuestionarioId()) {
				iscrizioneHelper.getDocumenti().setQuestionarioId(iscrizioneImpresa.getQuestionarioId());
				questionario = WizardDocumentiHelper.getQuestionarioAssociatoBO(iscrizioneHelper.getIdBando(), null, 0);
				iscrizioneHelper.getDocumenti().setQuestionarioAssociato(questionario);
			}
			//if (iscrizioneImpresa.isSetQuestionarioCompletato()) {
			//	// calcolato da "QuestionarioAssociato"
			//}
		}
	}
	
	/**
	 * Popola il contenitore da porre in sessione a partire dai dati letti dal
	 * backoffice ed in stato bozza.<br/>
	 * 
	 * @param helper helper di iscrizione/rinnovo
	 * @param rinnovoIscrizioneImpresa ...
	 * @param setCategorieFoglia ...
	 */
	private void popolaFromRinnovo(
			RinnovoIscrizioneImpresaElencoOperatoriType rinnovoIscrizioneImpresa,
			FirmatarioType[] firmatariImpresa,
			WizardIscrizioneHelper helper) throws XmlException, Exception 
	{
		FirmatarioType[] firmatari = rinnovoIscrizioneImpresa.getFirmatarioArray();

		DettaglioComunicazioneType criteriRicerca = new DettaglioComunicazioneType();
		criteriRicerca.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
		criteriRicerca.setChiave1(this.getCurrentUser().getUsername());
		criteriRicerca.setChiave2(this.getCodice());
		
		if(firmatari != null && firmatari.length > 0) {
			// c'e' sempre almeno un firmatario, al minimo e' l'impresa singola
			// creo comunque un elenco di imprese, contenente anche l'impresa 
			// principale che sara' la mandataria nel caso eventuale di RTI
			// NOTA: componentiRTI viene poi usato solo per RTI (quindi almeno 
			//       2 firmatari)
			setComponentiRTIFromFirmatari(helper,firmatari);
			if (helper.getComponentiRTI().size() != 0) {
				if (firmatari.length > 1) {
					helper.setRti(true);
					helper.setAmmesseRTI(true);
					setDatiFirmatariRTI(firmatari, helper);
				} else {
					if (firmatari.length == 1) {
						// impresa singola
						// --- CESSATI ---
						WizardDatiImpresaHelper datiImpresaHelper = ImpresaAction.getLatestDatiImpresa(
										this.getCurrentUser().getUsername(), 
										this);
						if (datiImpresaHelper.getDatiPrincipaliImpresa()
								.getRagioneSociale()
								.equals(firmatari[0].getRagioneSociale())) {
							// caso libero professionista
							setFirmatario(firmatari[0], helper);
							findFirmatarioSelezionatoInLista(firmatari[0], helper);
						} else {
							// caso impresa normale
							setFirmatario(firmatari[0], helper);
						}
					}
				}
			} else {
				// probabilmente non si entrera' mai qui perche' si inserisce
				// almeno l'impresa stessa in componentiRTI
				if (firmatari.length == 1) {
					setFirmatario(firmatari[0], helper);
					findFirmatarioSelezionatoInLista(firmatari[0], helper);
				}
			}
		}
	}

	/**
	 * Popola l'helper con le categorie presenti nella comunicazione analizzata
	 *
	 * @param iscrizioneHelper helper da popolare
	 * @param listaCategorieDomanda lista delle categorie da popolare
	 * @param setCategorieFoglia set di categorie corrispondenti a foglie
	 * dell'albero
	 * @param blocca true per bloccare le categorie, false altrimenti
	 * @throws Exception
	 */
	private static void setCategorie(
			WizardIscrizioneHelper iscrizioneHelper,
			ListaCategorieIscrizioneType listaCategorieDomanda,
			Set<String> setCategorieFoglia, 
			boolean blocca) throws Exception 
	{
		WizardCategorieHelper categorie = new WizardCategorieHelper();
		if(listaCategorieDomanda != null) {
			for (int i = 0; i < listaCategorieDomanda.sizeOfCategoriaArray(); i++) {
				CategoriaType categoria = listaCategorieDomanda.getCategoriaArray(i);
				categorie.getCategorieSelezionate().add(categoria.getCategoria());
				
				if (blocca) {
					categorie.getCategorieBloccate().add(categoria.getCategoria());
				}
				if (categoria.getClassificaMinima() != null) {
					categorie.getClasseDa().put(categoria.getCategoria(),
										        String.valueOf(categoria.getClassificaMinima()));
				}
				if (categoria.getClassificaMassima() != null) {
					categorie.getClasseA().put(categoria.getCategoria(),
											   String.valueOf(categoria.getClassificaMassima()));
				}
				if (categoria.getNota() != null) {
					categorie.getNota().put(categoria.getCategoria(),
											categoria.getNota());
				}
				if (setCategorieFoglia.contains(categoria.getCategoria())) {
					categorie.getFoglie().add(categoria.getCategoria());
				}
			}
		}
		iscrizioneHelper.setCategorie(categorie);
	}

	/**
	 * Popola l'helper con le categorie presenti nel backoffice
	 *
	 * @param iscrizioneHelper helper da popolare
	 * @param listaCategorie lista delle categorie da popolare
	 * @param setCategorieFoglia set di categorie corrispondenti a foglie
	 * dell'albero
	 * @param blocca true per bloccare le categorie, false altrimenti
	 */
	private static void setCategorieFromBackoffice(
			WizardIscrizioneHelper iscrizioneHelper,
			List<CategoriaImpresaType> listaCategorie,
			Set<String> setCategorieFoglia, 
			boolean blocca) 
	{
		WizardCategorieHelper categorie = new WizardCategorieHelper();
		CategoriaImpresaType categoria;

		for (int i = 0; i < listaCategorie.size(); i++) {
			categoria = listaCategorie.get(i);
			categorie.getCategorieSelezionate().add(categoria.getCategoria());
			if (blocca) {
				categorie.getCategorieBloccate().add(categoria.getCategoria());
			}
			if (categoria.getClassificaMinima() != null) {
				categorie.getClasseDa().put(categoria.getCategoria(),
										    categoria.getClassificaMinima());
			}
			if (categoria.getClassificaMassima() != null) {
				categorie.getClasseA().put(categoria.getCategoria(),
										   categoria.getClassificaMassima());
			}
			if (categoria.getNota() != null) {
				categorie.getNota().put(categoria.getCategoria(),
										categoria.getNota());
			}
			if (setCategorieFoglia.contains(categoria.getCategoria())) {
				categorie.getFoglie().add(categoria.getCategoria());
			}
		}
		iscrizioneHelper.setCategorie(categorie);
	}

	/**
	 * Popola l'helper con i dati relativi all'iscrizione dell'impresa presenti nel backoffice
	 *
	 * @param iscrizioneHelper helper da popolare
	 * @param listaCategorie lista delle categorie da popolare
	 * @param setCategorieFoglia set di categorie corrispondenti a foglie
	 * dell'albero
	 * @param blocca true per bloccare le categorie, false altrimenti
	 */
	private static void setImpresaIscrizioneFromBackoffice(
			WizardIscrizioneHelper iscrizioneHelper,
			ImpresaIscrizioneType impresa)
	{
		if(impresa != null) {
			// "possesso dei requisiti coordinatore di sicurezza"
			if(iscrizioneHelper.isRichiestaCoordinatoreSicurezza() 
			   && impresa.getCoordinatoreSicurezza() != null) {
				iscrizioneHelper.setRequisitiCoordinatoreSicurezza( impresa.getCoordinatoreSicurezza() );
			}
			
			// "richiedere requisito ascesa torre"
			if(iscrizioneHelper.isRichiestaAscesaTorre() 
					   && impresa.getAscesaTorre() != null) {
						iscrizioneHelper.setRequisitiAscesaTorre( impresa.getAscesaTorre() );
					}
		}
	}

	/**
	 * recupera il documento XML dalla comunicazione  
	 * (IscrizioneImpresaElencoOpType o AggIscrizioneImpresaElencoOpType)
	 * 
	 * @param comunicazione comunicazione dalla quale estrarre il documento xml
	 * @param iscrizione tipo di xml (True=iscrizione, False=aggiornamento)
	 * @return il documento xml contenuto nella comunicazione 
	 */
	private static XmlObject getXmlIscrizione(
			ComunicazioneType comunicazione, 
			boolean iscrizione) 
		throws Exception 
	{	
		XmlObject obj = null;
		
		AllegatoComunicazioneType allegato = null;
		int i = 0;
		while (comunicazione.getAllegato() != null
				&& i < comunicazione.getAllegato().length
				&& allegato == null) {
			// si cerca l'xml con i dati dell'aggiornamento
			// anagrafica tra tutti gli allegati
			if ((!iscrizione && PortGareSystemConstants.NOME_FILE_AGG_ISCRIZIONE
					.equals(comunicazione.getAllegato()[i].getNomeFile()))
				 || (iscrizione && PortGareSystemConstants.NOME_FILE_ISCRIZIONE
					.equals(comunicazione.getAllegato()[i].getNomeFile()))) {
				allegato = comunicazione.getAllegato()[i];
			}
			i++;
		}
		if (allegato == null) {
			// non dovrebbe succedere mai...si inserisce questo
			// controllo per blindare il codice da eventuali
			// comportamenti anomali
		} else {
			String xml = new String(allegato.getFile(), "UTF-8");
			if (iscrizione) {
				// IscrizioneImpresaElencoOpType
				IscrizioneImpresaElencoOperatoriDocument doc = 
					IscrizioneImpresaElencoOperatoriDocument.Factory.parse(xml);
				obj = doc.getIscrizioneImpresaElencoOperatori();
			} else {
				// AggIscrizioneImpresaElencoOpType
				AggiornamentoIscrizioneImpresaElencoOperatoriDocument doc = 
					AggiornamentoIscrizioneImpresaElencoOperatoriDocument.Factory.parse(xml);
				obj = doc.getAggiornamentoIscrizioneImpresaElencoOperatori();
			}
		}
		return obj; 
	}

	/**
	 * Popola l'helper con le categorie presenti nel backoffice
	 *
	 * @param objXml oggetto xml contentente un IscrizioneImpresaElencoOpType o AggIscrizioneImpresaElencoOpType
	 * @param iscrizioneHelper helper iscrizione da popolare
	 * @param setCategorieFoglia set di categorie corrispondenti a foglie dell'albero
	 * @param iscrizione tipo di xml (True=iscrizione, False=aggiornamento)
	 * @throws Exception 
	 */
	private static void setCategorieFromXml(
			XmlObject objXml, 
			WizardIscrizioneHelper iscrizioneHelper,
			Set<String> setCategorieFoglia,
			boolean iscrizione) throws Exception 
	{	
		ListaCategorieIscrizioneType categorie = null;
		if (iscrizione) {
			IscrizioneImpresaElencoOpType doc = (IscrizioneImpresaElencoOpType) objXml; 
			
			if(doc != null &&  doc.getCategorieIscrizione() != null) {
				categorie = doc.getCategorieIscrizione();
			}
		} else {
			AggIscrizioneImpresaElencoOpType doc = (AggIscrizioneImpresaElencoOpType) objXml;
			
			if(doc != null && doc.getCategorieIscrizione() != null) {
				categorie = doc.getCategorieIscrizione();
			}
		}

		if (!iscrizioneHelper.isCategorieAssenti() && 
			categorie != null && categorie.sizeOfCategoriaArray() > 0) {
			setCategorie(iscrizioneHelper,
					 	 categorie,
						 setCategorieFoglia, 
						 false);
		}
	}
	
	/**
	 * Popola l'helper con i dati relativi all'iscrizione dell'impresa 
	 */
	private static void setIscrizioneImpresaFromXml(
			XmlObject objXml, 
			WizardIscrizioneHelper iscrizioneHelper) throws Exception
	{		
		if(objXml instanceof IscrizioneImpresaElencoOpType) {
			IscrizioneImpresaElencoOpType iscr = (IscrizioneImpresaElencoOpType) objXml;
			
			// "possesso dei requisiti coordinatore di sicurezza"
			if(iscr != null && iscr.isSetRequisitiCoordinatoreSicurezza()) {
				iscrizioneHelper.setRequisitiCoordinatoreSicurezza( iscr.getRequisitiCoordinatoreSicurezza() );
			}
			
			// "possesso dei requisiti ascesa torre"
			if(iscr != null && iscr.isSetRequisitiAscesaTorre()) {
				iscrizioneHelper.setRequisitiAscesaTorre( iscr.getRequisitiAscesaTorre() );
			}

		} else if(objXml instanceof AggIscrizioneImpresaElencoOpType) {
			AggIscrizioneImpresaElencoOpType agg = (AggIscrizioneImpresaElencoOpType) objXml;
			
			// "possesso dei requisiti coordinatore di sicurezza"
			if(agg != null && agg.isSetRequisitiCoordinatoreSicurezza()) {
				iscrizioneHelper.setRequisitiCoordinatoreSicurezza( agg.getRequisitiCoordinatoreSicurezza() );
			}
			
			// "possesso dei requisiti ascesa torre"
			if(agg != null && agg.isSetRequisitiAscesaTorre()) {
				iscrizioneHelper.setRequisitiAscesaTorre( agg.getRequisitiAscesaTorre() );
			}
		}
	}
		
	/**
	 * Popola l'helper con i documenti presenti nell'XML relativo alla comunicazione
	 * 
	 * @param documentiXml 
	 * 			lista dei documenti
	 * @param helper 
	 * 			helper (iscrizione/rinnovo/aggiornamento) con i documenti da popolare
	 * @param nascondi 
	 * 			TRUE per nascondere i documenti (mantenerli solo per aggiornare 
	 * 			la comunicazione), FALSE altrimenti. <b>NB: Valido solo per i
	 * 			documenti ulteriori e non quelli richiesti.</b>
	 * 
	 * @throws IOException
	 */
	private void popolaDocumentiWizard(
			ListaDocumentiType documentiXml,
			WizardIscrizioneHelper helper,
			boolean nascondi) throws IOException 
	{
		if(documentiXml != null) {
			WizardDocumentiHelper documenti = new WizardDocumentiHelper();

			Long idComunicazione = (helper.getIdComunicazione() == null
									? helper.getIdComunicazioneBozza() 
									: helper.getIdComunicazione());
			
			documenti.popolaDocumentiFromXml(documentiXml, idComunicazione);
	    	
			// verifica se i documenti richiesti caricati dall'XML di una precedente comunicazione sono ancora presenti in BO
			try {
				List<DocumentazioneRichiestaType> docRichiestiBO = helper.getDocumentiRichiestiBO(); 
				if(docRichiestiBO != null) {
					List<Attachment> allegatiInconsistenti = documenti.getRequiredDocs().stream()
						.filter(d -> docRichiestiBO.stream()
										.noneMatch(docBO -> docBO.getId() == d.getId().longValue()) )
						.collect(Collectors.toList());
					
					// rimuovi i documenti inconsistenti per il contesto "ad oggi"
					// NB: alcuni allegati potrebbero aver cambiato contesto di validita' o essere stati archiviati etc.
					if(allegatiInconsistenti != null && allegatiInconsistenti.size() > 0) {
						allegatiInconsistenti.stream()
							.forEach(a -> logger.warn(this.getText("Errors.allegatoIncosistente", 
													 			   new String[] {a.getId().toString(), a.getFileName()})));
						// rimuovi gli allegati incosistenti con definizione diversa lato BO (archiviati o con contesto di validita')
						documenti.getRequiredDocs()
							.removeIf(d -> allegatiInconsistenti.stream()
												.anyMatch(a -> d.getId().longValue() == a.getId().longValue()) );
					}
				}
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "popolaDocumentiWizard", "Errore durante la sincronizzazione dei documenti");
				ExceptionUtils.manageExceptionError(t, this);
			}
			
	    	helper.setDocumenti(documenti);
		}
	}
	
	/**
	 * Ritorna la directory per i file temporanei, prendendola da
	 * 		struts.multipart.saveDir (struts.properties) 
	 * se valorizzata correttamente, altrimenti da 
	 * 		javax.servlet.context.tempdir
	 *
	 * @return path alla directory per i file temporanei
	 */
	@SuppressWarnings("unused")
	private File getTempDir() {
		return StrutsUtilities.getTempDir(this.getRequest().getSession().getServletContext(), multipartSaveDir);
	}

	/**
	 * Popola l'helper con gli eventuali componenti RTI e firmatari presenti 
	 * nella comunicazione analizzata (iscrizione o aggiornamento)
	 *
	 * @param iscrizioneImpresa 
	 * 			contenitore con i dati ricevuti dal backoffice
	 * @param iscrizioneHelper 
	 * 			helper da popolare
	 * 
	 * @throws Exception
	 */	
	protected void setDatiRTI(
			XmlObject domandaImpresa,
			FirmatarioType[] firmatariImpresa, 
			WizardIscrizioneHelper iscrizioneHelper) throws Exception 
	{
		if(domandaImpresa instanceof IscrizioneImpresaElencoOpType) {
			setDatiRTIIscrizione((IscrizioneImpresaElencoOpType) domandaImpresa, firmatariImpresa, iscrizioneHelper);
		} else if(domandaImpresa instanceof AggIscrizioneImpresaElencoOpType) {
			setDatiRTIAggiornamento((AggIscrizioneImpresaElencoOpType) domandaImpresa, firmatariImpresa, iscrizioneHelper);
		}
	}
	
	/**
	 * ... 
	 */
	private void setDatiRTIIscrizione(
			IscrizioneImpresaElencoOpType iscrizioneImpresa,
			FirmatarioType[] firmatariImpresa,
			WizardIscrizioneHelper iscrizioneHelper) throws Exception 
	{
		if (StringUtils.isNotEmpty(iscrizioneImpresa.getDenominazioneRTI())) {
			iscrizioneHelper.setDenominazioneRTI(iscrizioneImpresa.getDenominazioneRTI());
			iscrizioneHelper.setRti(true);
			iscrizioneHelper.getComponentiRTI().clear();
		}
		
		// --- FASE DI POPOLAMENTO ---
		if(iscrizioneHelper.isIscrizioneDomandaVisible()) {
			// recupera l'elenco dei firmatari 
			FirmatarioType[] firmatari = (firmatariImpresa != null ? firmatariImpresa : iscrizioneImpresa.getFirmatarioArray());
			
			if(firmatari.length == 0) {
				setComponentiRTIFromPartecipantiRaggruppamento(
						iscrizioneHelper,
						iscrizioneImpresa.getPartecipantiRaggruppamento(),
						true);
			} else {
				setComponentiRTIFromFirmatari(iscrizioneHelper, firmatari);
				// CASO : salvataggio parziale di una RTI in cui solo il 
				// 		  firmatario per la mandantaria era stato 
				//        valorizzato => integro la composizione della RTI 
				//        dai partecipanti
				setComponentiRTIFromPartecipantiRaggruppamento(
						iscrizioneHelper, 
						iscrizioneImpresa.getPartecipantiRaggruppamento(),
						false);
			}

			if(iscrizioneHelper.getComponentiRTI().size() != 0) {
				// in caso di RTI recupera il firmatario della mandataria
				if(firmatari.length > 1) {
					setDatiFirmatariRTI(firmatari, iscrizioneHelper);
				} else {
					if(firmatari.length == 1) {
						FirmatarioType firmatario = firmatari[0];
						if(iscrizioneImpresa.getDatiImpresa().getImpresa().getRagioneSociale().equals(firmatario.getRagioneSociale())) {
							// se l'unico firmatario recuperato e' quello della mandataria
							setFirmatario(firmatario, iscrizioneHelper);
							
							// NB: se la lista dei firmatari della mandataria è 
							//     vuota nello step "Scarica domanda ..." non 
							//     si vedrà alcun firmatario... 
							//     Se è necessario aggiorna la lista, prima di 
							//     eseguire "findFirmatarioSelezionatoInLista()" !!!
							if(iscrizioneHelper.getListaFirmatariMandataria().size() <= 0) {
								FirmatarioBean soggetto = new FirmatarioBean();
								if(iscrizioneHelper.getImpresa().isLiberoProfessionista()){
									soggetto.setNominativo(
											(StringUtils.isNotEmpty(iscrizioneHelper.getImpresa().getAltriDatiAnagraficiImpresa().getCognome()) ? iscrizioneHelper.getImpresa().getAltriDatiAnagraficiImpresa().getCognome() : "") + " " + 
											(StringUtils.isNotEmpty(iscrizioneHelper.getImpresa().getAltriDatiAnagraficiImpresa().getNome()) ? iscrizioneHelper.getImpresa().getAltriDatiAnagraficiImpresa().getNome() : "") );
									
								} else {
									soggetto.setNominativo(
										(StringUtils.isNotEmpty(firmatario.getCognome()) ? firmatario.getCognome() : "") + " " + 
										(StringUtils.isNotEmpty(firmatario.getNome()) ? firmatario.getNome() : "") );
								}
								iscrizioneHelper.getListaFirmatariMandataria().add(soggetto);
							}

							findFirmatarioSelezionatoInLista(firmatario, iscrizioneHelper);	
						} else {
							// l'unico firmatario recuperato è quello di una mandante
							setFirmatario(firmatario, iscrizioneHelper);
						}
					}	
				}
			} else if (firmatari.length == 1) {
				// in caso di ditta singola recupera il primo firmatario della ditta
				setFirmatario(firmatari[0], iscrizioneHelper);
				findFirmatarioSelezionatoInLista(firmatari[0], iscrizioneHelper);
			}
		} else {
			// Scarica domanda disabilitata : impresa corrente + recupero info 
			// delle altre da partecipanti
			setComponentiRTIFromPartecipantiRaggruppamento(
					iscrizioneHelper, 
					iscrizioneImpresa.getPartecipantiRaggruppamento(),
					true);
		}
	}
	
	/**
	 * ... 
	 */
	private void setDatiRTIAggiornamento(
			AggIscrizioneImpresaElencoOpType aggIscrizioneImpresa,
			FirmatarioType[] firmatariImpresa,
			WizardIscrizioneHelper iscrizioneHelper) throws Exception 
	{
		FirmatarioType[] firmatari = (firmatariImpresa != null ? firmatariImpresa : aggIscrizioneImpresa.getFirmatarioArray());
		
		if (firmatari != null && firmatari.length > 1) {
			// aggiungi una denominazione fittizia...
			iscrizioneHelper.setDenominazioneRTI("RTI_" + iscrizioneHelper.getImpresa().getDatiPrincipaliImpresa().getRagioneSociale());
			iscrizioneHelper.setRti(true);
			iscrizioneHelper.getComponentiRTI().clear();
		}
		
		// --- FASE DI POPOLAMENTO --- 
		if(iscrizioneHelper.isIscrizioneDomandaVisible()) {
			firmatari = aggIscrizioneImpresa.getFirmatarioArray();
			if(firmatari != null) {
				setComponentiRTIFromFirmatari(iscrizioneHelper, firmatari);
	
				if(iscrizioneHelper.getComponentiRTI().size() != 0) {
					if(firmatari.length > 1 ) {
						setDatiFirmatariRTI(firmatari, iscrizioneHelper);
					} else {
						if(firmatari.length == 1) {
							FirmatarioType firmatario = aggIscrizioneImpresa.getFirmatarioArray()[0];
							
							if(aggIscrizioneImpresa.getDatiImpresa().getImpresa().getRagioneSociale().equals(firmatari[0].getRagioneSociale())) {
								// se l'unico firmatario recuperato è quello della mandataria
								setFirmatario(aggIscrizioneImpresa.getFirmatarioArray()[0], iscrizioneHelper);
								
								// NB: se la lista dei firmatari della mandataria è 
								//     vuota nello step "Scarica domanda ..." non 
								//     si vedrà alcun firmatario... 
								//     Se è necessario aggiorna la lista, prima di 
								//     eseguire "findFirmatarioSelezionatoInLista()" !!!
								if(iscrizioneHelper.getListaFirmatariMandataria().size() <= 0) {
									FirmatarioBean soggetto = new FirmatarioBean();
									if(iscrizioneHelper.getImpresa().isLiberoProfessionista()){
										soggetto.setNominativo(
												(StringUtils.isNotEmpty(iscrizioneHelper.getImpresa().getAltriDatiAnagraficiImpresa().getCognome()) ? iscrizioneHelper.getImpresa().getAltriDatiAnagraficiImpresa().getCognome() : "") + " " + 
												(StringUtils.isNotEmpty(iscrizioneHelper.getImpresa().getAltriDatiAnagraficiImpresa().getNome()) ? iscrizioneHelper.getImpresa().getAltriDatiAnagraficiImpresa().getNome() : "") );
										
									} else {
										soggetto.setNominativo(
											(StringUtils.isNotEmpty(firmatario.getCognome()) ? firmatario.getCognome() : "") + " " + 
											(StringUtils.isNotEmpty(firmatario.getNome()) ? firmatario.getNome() : "") );
									}
									iscrizioneHelper.getListaFirmatariMandataria().add(soggetto);
								}

								findFirmatarioSelezionatoInLista(aggIscrizioneImpresa.getFirmatarioArray()[0], iscrizioneHelper);	
							} else {
								// l'unico firmatario recuperato è quello di una mandante
								setFirmatario(firmatario, iscrizioneHelper);
							}
						}
					}
				} else {
					if (firmatari.length == 1) {
						setFirmatario(aggIscrizioneImpresa.getFirmatarioArray()[0], iscrizioneHelper);
						findFirmatarioSelezionatoInLista(aggIscrizioneImpresa.getFirmatarioArray()[0], iscrizioneHelper);
					}
				}
			}
		} else { 
			// Scarica domanda disabilitata : impresa corrente + recupero info delle altre da partecipanti
			IComponente componente = new ComponenteHelper();
			componente.setRagioneSociale(iscrizioneHelper.getImpresa().getDatiPrincipaliImpresa().getRagioneSociale());
			componente.setNazione(iscrizioneHelper.getImpresa().getDatiPrincipaliImpresa().getNazioneSedeLegale());
			componente.setCodiceFiscale(iscrizioneHelper.getImpresa().getDatiPrincipaliImpresa().getCodiceFiscale());
			componente.setPartitaIVA(iscrizioneHelper.getImpresa().getDatiPrincipaliImpresa().getPartitaIVA());
			componente.setTipoImpresa(iscrizioneHelper.getImpresa().getDatiPrincipaliImpresa().getTipoImpresa());
			iscrizioneHelper.getComponentiRTI().add(componente);
			
			firmatari = aggIscrizioneImpresa.getFirmatarioArray();
			if(firmatari != null) {
				for(int i = 0; i < firmatari.length; i++) {
					componente = new ComponenteHelper();
					componente.setRagioneSociale(firmatari[i].getRagioneSociale());
					componente.setNazione(firmatari[i].getNazione());
					componente.setCodiceFiscale(firmatari[i].getCodiceFiscaleImpresa());
					componente.setPartitaIVA(firmatari[i].getPartitaIVAImpresa());
					componente.setTipoImpresa(firmatari[i].getTipoImpresa());
					iscrizioneHelper.getComponentiRTI().add(componente);
				}
			}
		}
	}

	/**
	 * Popola l'helper con gli eventuali componenti di un consorzio e firmatari 
	 * presenti nella comunicazione analizzata.
	 *
	 * @param iscrizioneHelper 
	 * 			helper da popolare
	 * @param iscrizioneImpresa 
	 * 			contenitore con i dati ricevuti dal backoffice
	 * 
	 * @throws Exception
	 */
	protected void setDatiConsorzio(
			WizardIscrizioneHelper iscrizioneHelper,
			IscrizioneImpresaElencoOpType iscrizioneImpresa) 
		throws Exception 
	{
		if(iscrizioneHelper.isIscrizioneDomandaVisible()) {
			
			FirmatarioType[] firmatari = iscrizioneImpresa.getFirmatarioArray();
			if (firmatari.length > 0) {
	            // Recupero il firmatario dell'azienda
	            setFirmatario(firmatari[0], iscrizioneHelper);
			}

		}
		
		// Ripopolo la lista dei componenti del consorzio
		setComponentiConsorzioFromPartecipantiRaggruppamento(
				iscrizioneHelper, 
				iscrizioneImpresa.getPartecipantiRaggruppamento());
	}

	/**
	 * Permette di valorizzare nell'helper in sessione l'indice del firmatario
	 * selezionato nella lista dei firmatari dell'impresa o della mandataria a
	 * partire dal firmatario in input.
	 * 
	 * @param firmatario
	 *            firmatario selezionato
	 * @param helper
	 *            helper dati iscrizione
	 */
	protected static void findFirmatarioSelezionatoInLista(
			FirmatarioType firmatario, 
			WizardIscrizioneHelper helper) 
	{
		String nominativoFirmatarioRecuperato = firmatario.getCognome() + " " + firmatario.getNome();
		boolean firmatarioRecuperatoFound = false;
		for(int i = 0; i < helper.getListaFirmatariMandataria().size() && !firmatarioRecuperatoFound; i++) {
			if(nominativoFirmatarioRecuperato.equalsIgnoreCase(helper.getListaFirmatariMandataria().get(i).getNominativo())) {
				helper.setIdFirmatarioSelezionatoInLista(i);
				firmatarioRecuperatoFound = true;
			}
		}
	}

	/**
	 * ... 
	 */
	protected static void refreshDatiFirmatari(
			WizardIscrizioneHelper helper) 
		throws ApsException
	{
		WizardDatiImpresaHelper datiImpresaHelper = helper.getImpresa();

		if(helper.isAmmesseRTI() && helper.getComponentiRTI().size() == 0) {
			helper.setComponentiRTI(createComponentiRTI(helper, datiImpresaHelper));
		}

		if(helper.getComponentiRTI().size() > 0) {
			boolean firmatarioIndividuato = false;
			
			int idxMandataria = helper.getComponentiRTI().getMandataria(datiImpresaHelper.getDatiPrincipaliImpresa());
			
			String nominativo = null;
			if(helper.getFirmatarioSelezionato() != null) {
				// il firmatario è quello selezionato...
				nominativo = helper.getFirmatarioSelezionato().getNominativo();
			} else if(idxMandataria >= 0) {
				// il firmatario è quello della mandataria...
				nominativo = helper.getComponentiRTI().getFirmatario(idxMandataria).getNominativo();
			}
			
			for(int i = 0; i < helper.getListaFirmatariMandataria().size() && !firmatarioIndividuato; i++) {
				if(helper.getListaFirmatariMandataria().get(i).getNominativo().equalsIgnoreCase(nominativo)) {
					helper.setIdFirmatarioSelezionatoInLista(i);
					firmatarioIndividuato = true;
				}
			}
		}
	}

	/**
	 * crea l'elenco dei partecipanti alla RTI
	 */
	private static ComponentiRTIList createComponentiRTI(
			WizardIscrizioneHelper helper, 
			WizardDatiImpresaHelper datiImpresaHelper) 
	{	
		ComponentiRTIList componentiRTI = new ComponentiRTIList();
		IComponente mandataria = new ComponenteHelper();

		// Compongo la lista delle partecipanti alla RTI: 
		//  0    => la mandataria in testa
		if(datiImpresaHelper.isLiberoProfessionista() && 
		   StringUtils.isEmpty(datiImpresaHelper.getDatiPrincipaliImpresa().getPartitaIVA())) {
			// se tipo impresa è libero professionista e partita iva vuota => 
			// usare codice fiscale come chiave
			mandataria.setCodiceFiscale(datiImpresaHelper.getDatiPrincipaliImpresa().getCodiceFiscale());
		} else {
			mandataria.setPartitaIVA(datiImpresaHelper.getDatiPrincipaliImpresa().getPartitaIVA());
		}
		mandataria.setRagioneSociale(datiImpresaHelper.getDatiPrincipaliImpresa().getRagioneSociale());
		componentiRTI.add(mandataria);

		// 1..n  => le mandanti
		for(int i = 0; i < helper.getComponenti().size(); i++) {
			componentiRTI.add(helper.getComponenti().get(i));
		}

		return componentiRTI;
	}

	/**
	 * Inserisce il firmatario nella hash (dato fiscale, soggetto).
	 * 
	 * @param firmatarioRecuperato
	 *            dati del firmatario
	 * @param iscrizioneHelper
	 *            contenitore di dati in sessione
	 */
	protected static void setFirmatario(
			FirmatarioType firmatarioRecuperato, 
			WizardIscrizioneHelper iscrizioneHelper) 
	{
		SoggettoFirmatarioImpresaHelper firmatario = new SoggettoFirmatarioImpresaHelper();

		firmatario.setNome(firmatarioRecuperato.getNome());
		firmatario.setCognome(firmatarioRecuperato.getCognome());
		firmatario.setNominativo(firmatarioRecuperato.getCognome() + " " + firmatarioRecuperato.getNome());

		firmatario.setCodiceFiscale(firmatarioRecuperato.getCodiceFiscaleFirmatario());
		firmatario.setComuneNascita(firmatarioRecuperato.getComuneNascita());
		firmatario.setProvinciaNascita(firmatarioRecuperato.getProvinciaNascita());
		if(firmatarioRecuperato.getDataNascita() != null) {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy"); 
			Date c = firmatarioRecuperato.getDataNascita().getTime();
			firmatario.setDataNascita(df.format(c));
		}
		firmatario.setSesso(firmatarioRecuperato.getSesso());
		if(StringUtils.isNotBlank(firmatarioRecuperato.getQualifica())) {
			firmatario.setQualifica(StringUtils.substring(
					firmatarioRecuperato.getQualifica(),
					firmatarioRecuperato.getQualifica().lastIndexOf("-") + 1, 
					firmatarioRecuperato.getQualifica().length()));
			firmatario.setSoggettoQualifica(firmatarioRecuperato.getQualifica());
		}
		if(firmatarioRecuperato.getResidenza() != null) {
			firmatario.setCap(firmatarioRecuperato.getResidenza().getCap());
			firmatario.setIndirizzo(firmatarioRecuperato.getResidenza().getIndirizzo());
			firmatario.setNumCivico(firmatarioRecuperato.getResidenza().getNumCivico());
			firmatario.setComune(firmatarioRecuperato.getResidenza().getComune());
			firmatario.setProvincia(firmatarioRecuperato.getResidenza().getProvincia());
			firmatario.setNazione(firmatarioRecuperato.getResidenza().getNazione());
		}
	
		iscrizioneHelper.getComponentiRTI().addFirmatario(firmatarioRecuperato, firmatario);
	}
	
	/**
	 * Estrae l'allegato di una comunicazione in base al "filename"
	 *
	 * @param comunicazione 
	 * 			comunicazione da cui recuperare l'allegato
	 * @param nomeFile 
	 * 			nome dell'allegato da estrarre
	 * 
	 * @throws ApsException
	 */
	private AllegatoComunicazioneType getAllegatoComunicazione(
			ComunicazioneType comunicazione, 
			String nomefile) throws ApsException 
	{
		AllegatoComunicazioneType allegato = null;
		int i = 0;
		while (comunicazione.getAllegato() != null && i < comunicazione.getAllegato().length && allegato == null) {
			if (nomefile.equals(comunicazione.getAllegato()[i].getNomeFile())) {
				allegato = comunicazione.getAllegato()[i];
			} else {
				i++;
			}
		}
		return allegato;
	}

	/**
	 * aggiorna i componenti dell'RTI 
	 */
	private static void setComponentiRTIFromPartecipantiRaggruppamento(
			WizardIscrizioneHelper iscrizioneHelper, 
			ListaPartecipantiRaggruppamentoType partecipanteRaggruppamento,
			boolean addMandataria)
	{
		if (partecipanteRaggruppamento != null) {
			// aggiungi la mandataria sempre come I elemento...
			if(addMandataria) { // iscrizioneHelper.getComponentiRTI().size() <= 0) {
				IComponente mandataria = new ComponenteHelper();
				mandataria.setRagioneSociale(iscrizioneHelper.getImpresa().getDatiPrincipaliImpresa().getRagioneSociale());
				mandataria.setNazione(iscrizioneHelper.getImpresa().getDatiPrincipaliImpresa().getNazioneSedeLegale());
				mandataria.setCodiceFiscale(iscrizioneHelper.getImpresa().getDatiPrincipaliImpresa().getCodiceFiscale());
				mandataria.setPartitaIVA(iscrizioneHelper.getImpresa().getDatiPrincipaliImpresa().getPartitaIVA());
				mandataria.setTipoImpresa(iscrizioneHelper.getImpresa().getDatiPrincipaliImpresa().getTipoImpresa());
				iscrizioneHelper.getComponentiRTI().add(mandataria);
			}
			
			// aggiungi le mandatarie...
			for (int i = 0; i < partecipanteRaggruppamento.sizeOfPartecipanteArray(); i++) {
				PartecipanteRaggruppamentoType partecipante = partecipanteRaggruppamento.getPartecipanteArray(i);

				// verifica se esiste gia' la ditta nel raggruppamento...
				IComponente componente = null;
				for(int j = 0; j < iscrizioneHelper.getComponentiRTI().size(); j++) {
					if(iscrizioneHelper.getComponentiRTI().get(j).getRagioneSociale().equals(partecipante.getRagioneSociale())) {
						componente = iscrizioneHelper.getComponentiRTI().get(j);
						break;
					}
				}
				if(componente == null) {
					// se non esiste si crea e si aggiunge al raggruppamento...
					componente = new ComponenteHelper();
					iscrizioneHelper.getComponentiRTI().add(componente);
				}
				
				// aggiorna i dati del partecipante...
				componente.setRagioneSociale(partecipante.getRagioneSociale());
				componente.setTipoImpresa(partecipante.getTipoImpresa());
				componente.setNazione(partecipante.getNazione());
				componente.setCodiceFiscale(partecipante.getCodiceFiscale());
				componente.setPartitaIVA(partecipante.getPartitaIVA());
				componente.setAmbitoTerritoriale(partecipante.getAmbitoTerritoriale());
				componente.setIdFiscaleEstero(partecipante.getIdFiscaleEstero());
			}
		}
	}

	/**
	 * ... 
	 */
	private static void setComponentiConsorzioFromPartecipantiRaggruppamento(
			WizardIscrizioneHelper iscrizioneHelper, 
			ListaPartecipantiRaggruppamentoType partecipanteRaggruppamento)
	{
		if (partecipanteRaggruppamento != null) {
			for (int i = 0; i < partecipanteRaggruppamento.sizeOfPartecipanteArray(); i++) {
				boolean found = false;
				PartecipanteRaggruppamentoType partecipante = partecipanteRaggruppamento.getPartecipanteArray(i);
				IComponente componente = new ComponenteHelper();
				componente.setRagioneSociale(partecipante.getRagioneSociale());
				componente.setNazione(partecipante.getNazione());
				componente.setCodiceFiscale(partecipante.getCodiceFiscale());
				componente.setPartitaIVA(partecipante.getPartitaIVA());
				componente.setTipoImpresa(partecipante.getTipoImpresa());
				for(int j = 0; j < iscrizioneHelper.getComponenti().size() && !found; j++) {
					if(iscrizioneHelper.getComponenti().get(j).getRagioneSociale().equals(componente.getRagioneSociale())) {
						found = true;
					}
				}
				if(!found) {
					iscrizioneHelper.getComponenti().add(componente);
				}
			}
		}	
	}

	/**
	 * ... 
	 */
	protected void setComponentiRTIFromFirmatari(
			WizardIscrizioneHelper iscrizioneHelper, 
			FirmatarioType[] firmatari) 
	{
		for (int i = 0; i < firmatari.length; i++) {
			IComponente componente = new ComponenteHelper();
			componente.setRagioneSociale(firmatari[i].getRagioneSociale());
			componente.setCodiceFiscale(firmatari[i].getCodiceFiscaleImpresa());
			componente.setPartitaIVA(firmatari[i].getPartitaIVAImpresa());
			componente.setTipoImpresa(firmatari[i].getTipoImpresa());
			if("Italia".equalsIgnoreCase(firmatari[i].getNazione())) {
				componente.setAmbitoTerritoriale("1");
			} else {
				componente.setAmbitoTerritoriale("2");
				componente.setIdFiscaleEstero(firmatari[i].getCodiceFiscaleImpresa());
			}
			iscrizioneHelper.getComponentiRTI().add(componente);
		}
	}

	/**
	 * ... 
	 */
	@SuppressWarnings("unlikely-arg-type")
	protected static void setDatiFirmatariRTI(
			FirmatarioType[] firmatari, 
			WizardIscrizioneHelper iscrizioneHelper) 
	{	
		for(int i = 0; i < firmatari.length; i++) {
			boolean firmatarioRimosso = false;
			
			if(iscrizioneHelper.getListaFirmatariMandataria().contains(firmatari[i])) {
			}
			
			SoggettoFirmatarioImpresaHelper firmatario = new SoggettoFirmatarioImpresaHelper();
			firmatario.setNome(firmatari[i].getNome());
			firmatario.setCognome(firmatari[i].getCognome());

			if(i == 0) {
				boolean firmatarioRecuperatoFound = false;
				if(iscrizioneHelper.getImpresa().isLiberoProfessionista()) {
					if (StringUtils.isNotEmpty(iscrizioneHelper.getImpresa().getAltriDatiAnagraficiImpresa().getCognome())) {
						// nuova gestione con cognome e nome obbligatori per libero professionista: li recupero dai dati in linea
						firmatario.setNome(iscrizioneHelper.getImpresa().getAltriDatiAnagraficiImpresa().getNome());
						firmatario.setCognome(iscrizioneHelper.getImpresa().getAltriDatiAnagraficiImpresa().getCognome());
						firmatario.setNominativo(firmatario.getCognome() + " " + firmatario.getNome());
						firmatarioRecuperatoFound = true;
					} else {
						// vecchia gestione libero professionista: si riportava la ragione sociale
						firmatario.setNominativo(firmatari[i].getRagioneSociale());
						firmatario.setNome(firmatari[i].getRagioneSociale());
					}
				} else {
					firmatario.setNome(firmatari[i].getNome());
					firmatario.setCognome(firmatari[i].getCognome());
					firmatario.setNominativo(firmatari[i].getCognome() + " " + firmatari[i].getNome());
				}
				String nominativoFirmatarioRecuperato = firmatario.getNominativo();
				for(int j = 0; j < iscrizioneHelper.getListaFirmatariMandataria().size() && !firmatarioRecuperatoFound; j++) {
					if(nominativoFirmatarioRecuperato.equalsIgnoreCase(iscrizioneHelper.getListaFirmatariMandataria().get(j).getNominativo())) {
						iscrizioneHelper.setIdFirmatarioSelezionatoInLista(j);
						firmatarioRecuperatoFound = true;
					}
				}
				
				if(!firmatarioRecuperatoFound) {
					iscrizioneHelper.getComponentiRTI().removeFirmatario(firmatari[i]);
					firmatarioRimosso = true;
				}
			} else { 
				if(StringUtils.isNotBlank(firmatario.getCognome() + " " + firmatario.getNome())) {
					firmatario.setNome(firmatari[i].getNome());
					firmatario.setCognome(firmatari[i].getCognome());
					firmatario.setNominativo(firmatari[i].getCognome() + " " + firmatari[i].getNome());
				} else {
					firmatario.setNominativo(firmatari[i].getRagioneSociale());
					firmatario.setNome(firmatari[i].getRagioneSociale());
				}
			}
			
			if(!firmatarioRimosso) {
				firmatario.setCodiceFiscale(firmatari[i].getCodiceFiscaleFirmatario());
				firmatario.setComuneNascita(firmatari[i].getComuneNascita());
				firmatario.setProvinciaNascita(firmatari[i].getProvinciaNascita());
				if(firmatari[i].getDataNascita()!=null) {
					DateFormat df = new SimpleDateFormat("dd/MM/yyyy"); 
					Date c  = firmatari[i].getDataNascita().getTime();
					firmatario.setDataNascita(df.format(c));
				}
				firmatario.setSesso(firmatari[i].getSesso());
				if(StringUtils.isNotBlank(firmatari[i].getQualifica())) {
					firmatario.setQualifica(StringUtils.substring(
							firmatari[i].getQualifica(),
							firmatari[i].getQualifica().lastIndexOf("-") + 1, 
							firmatari[i].getQualifica().length()));
					firmatario.setSoggettoQualifica(firmatari[i].getQualifica());
				}
				if(firmatari[i].getResidenza() != null) {
					firmatario.setCap(firmatari[i].getResidenza().getCap());
					firmatario.setIndirizzo(firmatari[i].getResidenza().getIndirizzo());
					firmatario.setNumCivico(firmatari[i].getResidenza().getNumCivico());
					firmatario.setComune(firmatari[i].getResidenza().getComune());
					firmatario.setProvincia(firmatari[i].getResidenza().getProvincia());
					firmatario.setNazione(firmatari[i].getResidenza().getNazione());
				}

				iscrizioneHelper.getComponentiRTI().addFirmatario(firmatari[i], firmatario);
			}
		}
	}

	/**
	 * ... 
	 */
	protected void checkFaseWithTimestampNTP(
			int matchFase, 
			DettaglioBandoIscrizioneType dettBando, 
			String funzione,
			String keyNomeOperazione)
	{
		Date dataAttuale = null;
		String nomeOperazione = this.getI18nLabel(keyNomeOperazione).toLowerCase();
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
			if (InitIscrizioneAction.calcolaFase(dataAttuale,
					dettBando.getDatiGeneraliBandoIscrizione()) != matchFase) 
			{
				this.addActionError(this.getText("Errors.richiestaFuoriTempoMassimo"));
				this.setTarget("block");
				// si tracciano solo i tentativi di accesso alle funzionalita' fuori tempo massimo
				Event evento = new Event();
				evento.setUsername(this.getCurrentUser().getUsername());
				evento.setDestination(dettBando.getDatiGeneraliBandoIscrizione().getCodice());
				evento.setLevel(Event.Level.ERROR);
				evento.setEventType(PortGareEventsConstants.ACCESSO_FUNZIONE);
				evento.setIpAddress(this.getCurrentUser().getIpAddress());
				evento.setSessionId(this.getRequest().getSession().getId());
				evento.setMessage("Accesso alla funzione " + funzione);
				evento.setDetailMessage(PortGareEventsConstants.DETAIL_MESSAGE_ERRORE_FUORI_TEMPO_MASSIMO + " (" + UtilityDate.convertiData(dataAttuale, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS) + ")");
				this.eventManager.insertEvent(evento);
			}
		}
	}

	/**
	 * Recupera le comunicazioni in base a una serie di criteri di ricerca
	 *
	 * @param criteriRicerca i criteri di ricerca per le comunicazioni
	 * @throws ApsException
	 */
	protected List<DettaglioComunicazioneType> ricercaComunicazioni(
			DettaglioComunicazioneType criteriRicerca) throws ApsException 
	{
		List<DettaglioComunicazioneType> comunicazioni = this.comunicazioniManager
				.getElencoComunicazioni(criteriRicerca);
		return comunicazioni;
	}

	/**
	 * Inizializza i filtri per il metodo ricercaComunicazioni() 
	 */
	protected DettaglioComunicazioneType setRicercaComunicazioni(
			DettaglioComunicazioneType criteriRicerca,
			String tipoComunicazione,
			String stato) 
	{
		criteriRicerca.setStato(stato);
		criteriRicerca.setTipoComunicazione(tipoComunicazione);
		return criteriRicerca;
	}

	/**
	 * Inizializza i filtri per la ricerca in stato BOZZA delle RICHIESTE DI ISCRIZIONE
	 */
	private DettaglioComunicazioneType setRicercaIscrizioneBozza(DettaglioComunicazioneType criteriRicerca) {
		return setRicercaComunicazioni(
				criteriRicerca, 
				PortGareSystemConstants.RICHIESTA_ISCRIZIONE_ALBO, 
				CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
	}
	
	/**
	 * (QFORM - QUESTIONARI) 
	 * verifica se e' presente un questionario per l'elenco ed inizializza l'helper
	 */
	private String initQuestionario(WizardIscrizioneHelper helper) {
		String target = SUCCESS;
		logger.debug("initQuestionario");
		
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
			return target;
		}
		
		this.session.put(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO, helper);
		
		String docUlterioreDesc = DocumentiAllegatiHelper.QUESTIONARIO_DESCR;
		String docUlterioreFileName = DocumentiAllegatiHelper.QUESTIONARIO_ELENCHI_FILENAME;
		String docUlterioreContentType = "JSON";
		int dimensioneDocumento = 0;
		
		// verifica se l'elenco prevede un questionario
		// e se e' attiva la gestione dei questionari apri la pagina dei QFORM
		try {
			// la sessione non e' scaduta, per cui proseguo regolarmente
			boolean isAggiornamento = helper.isAggiornamentoIscrizione();
			
//			boolean isIscrizione = !isAggiornamento && !helper.isRinnovoIscrizione();
			String funzione = helper.getDescrizioneFunzione();
			
			WizardDocumentiHelper documenti = helper.getDocumenti();
			
			// verifica se la modulistica del questionario e' cambiata...
			if(documenti.isQuestionarioAllegato()) {
				if(helper.isQuestionarioModulisticaVariata()) {
					target = MODULISTICA_CAMBIATA;
				}
			}
			logger.debug("helper.isFromBozza()? {},target: {}",helper.isFromBozza(),target);
			if( !target.equals(MODULISTICA_CAMBIATA) ) {
				// ed eventualmente aggiungi alla comunicazione un allegato "questionario.json"
				QuestionarioType questionarioBO = WizardDocumentiHelper.getQuestionarioAssociatoBO(helper.getIdBando(), null, 0);
				if(questionarioBO != null) {
					
					// aggiorna i dati sul questionario
					documenti.setQuestionarioAssociato(questionarioBO);
					documenti.setQuestionarioId(questionarioBO.getId());
					
					// prepara gli attributi per l'aggiunta del questionario...
					byte[] survey = null;
					if(documenti.getQuestionarioAssociato().getOggetto() != null) {
						survey = documenti.getQuestionarioAssociato().getOggetto().getBytes("UTF-8");
					} else {
						survey = new byte[0];
					}
					dimensioneDocumento = (int) Math.ceil(survey.length / 1024.0);
					
					// in caso di aggiornamento iscrizione compila l'elenco dei file "serverFiles"
					// con l'elenco dei file presenti in W_DOCDIG
					if(isAggiornamento) {
						// controllo se e' presente un questionario...
						QCQuestionario questionario = documenti.getQuestionarioElenchi(); 
						
						// ...altrimenti inserisco il template
						if(questionario == null && documenti.getQuestionarioAssociato().getOggetto() != null ) {
							questionario = new QCQuestionario(documenti.getQuestionarioAssociato().getOggetto());
						}
						
						// e aggiorno la sezione "serverFilesUuids" per il client angular
						if(questionario != null) {
							questionario.addServerFilesUuids(documenti);
							String json = questionario.getQuestionario();
							documenti.updateQuestionario(json);
							survey = json.getBytes(StandardCharsets.UTF_8);
						}
					}
					
					// aggiungi il documento...
					Event evento = new Event();
					evento.setUsername(this.getCurrentUser().getUsername());
					evento.setDestination(helper.getIdBando());
					evento.setLevel(Event.Level.INFO);
					evento.setEventType(PortGareEventsConstants.AUTOSAVE_FILE);
					evento.setIpAddress(this.getCurrentUser().getIpAddress());
					evento.setSessionId(this.getRequest().getSession().getId());
					evento.setMessage(funzione + ": documento ulteriore" 
							  + ", file=" + docUlterioreFileName
							  + ", dimensione=" + dimensioneDocumento + "KB");

					if (Attachment.indexOf(documenti.getAdditionalDocs(), Attachment::getDesc, docUlterioreDesc) == -1) {
						documenti.addDocUlteriore(
								docUlterioreDesc,
								survey,
								docUlterioreContentType,
								docUlterioreFileName,
								null,
								evento,null);
						
					}
					target = SaveWizardIscrizioneAction.saveDocumenti(
							helper, 
							this.session, 
							this);
					if( !SUCCESS.equals(target) ) {
						target = INPUT;
					}
					this.eventManager.insertEvent(evento);
				}
			} else if(isAggiornamento && !helper.isFromBozza()) {
				logger.debug("Siamo in aggiornamento e non esiste in bozza il qform si presenta variato rispetto a quanto inviato in precedenza.");
				target = SUCCESS;
				QuestionarioType questionarioBO = WizardDocumentiHelper.getQuestionarioAssociatoBO(helper.getIdBando(), null, 0);
				if(questionarioBO != null) {
					// aggiorna i dati sul questionario
					documenti.setQuestionarioAssociato(questionarioBO);
					documenti.setQuestionarioId(questionarioBO.getId());
					if(documenti.getQuestionarioAssociato().getOggetto() != null) {
						QCQuestionario questionario  = new QCQuestionario(documenti.getQuestionarioAssociato().getOggetto());
						questionario.addServerFilesUuids(documenti);
						String json = questionario.getQuestionario();
						documenti.updateQuestionario(json);

						if (Attachment.indexOf(documenti.getAdditionalDocs(), Attachment::getDesc, docUlterioreDesc) == -1) {
							byte[] survey = json.getBytes("UTF-8");
							documenti.addDocUlteriore(
									docUlterioreDesc,
									survey,
									docUlterioreContentType,
									docUlterioreFileName,
									null,
									null,null);
							if( !SUCCESS.equals(target) ) {
								target = INPUT;
							}
						}
						target = SaveWizardIscrizioneAction.saveDocumenti(
								helper, 
								this.session, 
								this);
					}
					this.addActionMessage(getI18nLabel("LABEL_MODULISTICA_ELENCO_CAMBIATA_WARN"));
				}
			}
		} catch (GeneralSecurityException e) {
			ApsSystemUtils.logThrowable(e, this, "initQuestionario", "Errore durante la cifratura dell'allegato richiesto " + docUlterioreFileName);
			ExceptionUtils.manageExceptionError(e, this);
			target = CommonSystemConstants.PORTAL_ERROR;
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "initQuestionario", "Errore durante la verifica del formato dell'allegato richiesto " + docUlterioreFileName);
			ExceptionUtils.manageExceptionError(e, this);
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		return target;
	}

	/**
	 * (QFORM - QUESTIONARI) 
	 * verifica se e' presente un questionario per l'elenco ed inizializza l'helper
	 */
	public String eliminaDocumentiElenco() {
		String target = SUCCESS;
		
		this.nextResultAction = "cancelIscrizione";
		
		Event evento = null;		
		try {
			WizardIscrizioneHelper helper = (WizardIscrizioneHelper)this.session.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
			WizardDocumentiHelper documenti = helper.getDocumenti();
			
			if (documenti == null) {
				// la sessione e' scaduta, occorre riconnettersi
				this.addActionError(this.getText("Errors.sessionExpired"));
				target = CommonSystemConstants.PORTAL_ERROR;
			} else {
				// crea l'evento...
				evento = new Event();
				evento.setUsername(this.getCurrentUser().getUsername());
				evento.setDestination(this.codice);
				evento.setLevel(Event.Level.INFO);
				evento.setEventType(PortGareEventsConstants.SALVATAGGIO_COMUNICAZIONE);
				evento.setIpAddress(this.getCurrentUser().getIpAddress());
				evento.setSessionId(this.getRequest().getSession().getId());
				evento.setMessage(helper.getDescrizioneFunzione() + ": rettifica elenco");

				// elimina i documenti dall'helper...
				for(int i = documenti.getRequiredDocs().size() - 1; i >= 0; i--) {
					documenti.removeDocRichiesto(i);
				}
				for(int i = documenti.getAdditionalDocs().size() - 1; i >= 0; i--) {
					documenti.removeDocUlteriore(i);
				}
				documenti.setQuestionarioAssociato(null);

				// elimina la comunicazione della richiesta
				Long comunicazioneDaElminare = helper.getIdComunicazione() != null ? helper.getIdComunicazione() : helper.getIdComunicazioneBozza();
				if(comunicazioneDaElminare != null) {
					logger.debug("InitIscrizioneAction - eliminazione comunicazione con id: {}",comunicazioneDaElminare);
					this.comunicazioniManager.deleteComunicazione(
							CommonSystemConstants.ID_APPLICATIVO, 
							comunicazioneDaElminare);
				}

				target = SUCCESS;
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "eliminaDocumentiElenco", "Errore durante l'eliminazione della richiesta per l''elenco");
			ExceptionUtils.manageExceptionError(t, this);
			target = CommonSystemConstants.PORTAL_ERROR;
			if(evento != null) {
				evento.setError(t);
			}
		} finally {
			if(evento != null) {
				this.eventManager.insertEvent(evento);
			}
		}
		
		return target;
	}
	
}
