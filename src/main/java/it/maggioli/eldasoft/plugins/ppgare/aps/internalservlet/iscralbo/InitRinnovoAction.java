package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.BandoIscrizioneType;
import it.eldasoft.www.sil.WSGareAppalto.CategoriaBandoIscrizioneType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioBandoIscrizioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ImpresaAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.xmlbeans.XmlException;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.exception.ApsSystemException;

public class InitRinnovoAction extends InitIscrizioneAction  {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 5266706847248153618L;

	
	/**
	 * ...
	 */
	public String initRinnovo() {
		return this.initRinnovo(PortGareSystemConstants.TIPOLOGIA_ELENCO_STANDARD);
	}

	/**
	 * ...
	 */
	public String initCatalogoRinnovo() {
		return this.initRinnovo(PortGareSystemConstants.TIPOLOGIA_ELENCO_CATALOGO);
	}
	
	/**
	 * ...
	 */
	private String initRinnovo(int tipologia) {
		
		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			
			Boolean datiInterpretati = new Boolean(false);
			try {
				// il rinnovo va effettuato entro la validita' elenco
				Date dataAttuale = null;
				String nomeOperazione = this.getI18nLabel("BUTTON_ISCRALBO_RINNOVO_ISCRIZIONE").toLowerCase();
				try {
					dataAttuale = this.getNtpManager().getNtpDate();
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

				// Pulisco la sessione da eventuali wizard pending, i quali 
				// non sono stati annullati tramite l'operazione di "annulla" 
				this.getSession().remove(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
				this.getSession().remove(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);
				
				// si estraggono dal B.O. i dati del bando a cui iscriversi
				DettaglioBandoIscrizioneType dettBando = this.getBandiManager()
						.getDettaglioBandoIscrizione(this.getCodice());

				// si determina se viene gestita una o piu' stazioni appaltanti
				LinkedHashMap<String, String> listaStazioni = this.getBandiManager()
						.getElencoStazioniAppaltantiPerIscrizione(this.getCodice());

				CategoriaBandoIscrizioneType[] listaCategorie = this.getBandiManager()
						.getElencoCategorieBandoIscrizione(this.getCodice(), null);
				Set<String> setCategorieFoglia = new HashSet<String>();
				if(listaCategorie != null) {
					for (CategoriaBandoIscrizioneType cat : listaCategorie) {
						if (cat.isFoglia()) {
							setCategorieFoglia.add(cat.getCodice());
						}
					}
				}

				// per il rinnovo si verifica solo il termine validita'
				if (dataAttuale != null) {
					Date dataFineValidita = InitIscrizioneAction.calcolaDataOra(
							dettBando.getDatiGeneraliBandoIscrizione().getDataFineValidita(), null, true);
					
					if (dataFineValidita != null &&	dataAttuale.compareTo(dataFineValidita) > 0) {
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
						evento.setMessage("Accesso alla funzione Richiesta rinnovo");
						evento.setDetailMessage(PortGareEventsConstants.DETAIL_MESSAGE_ERRORE_FUORI_TEMPO_MASSIMO + " (" + UtilityDate.convertiData(dataAttuale, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS) + ")");
						this.getEventManager().insertEvent(evento);
					}
				}

				// si crea il contenitore da porre in sessione
				WizardRinnovoHelper rinnovoHelper = null;
				if (SUCCESS.equals(this.getTarget())) {
					rinnovoHelper = createHelper(
							dettBando,
							listaStazioni, 
							listaCategorie, 
							tipologia);
					
					WizardDatiImpresaHelper datiImpresaHelper = ImpresaAction.getLatestDatiImpresa(
									this.getCurrentUser().getUsername(), 
									this);
					
					rinnovoHelper.setImpresa(datiImpresaHelper);

					//rinnovoHelper.setIscrizioneDomandaVisible(this.getCustomConfigManager().isVisible("ISCRALBO-DOCUM", "PDFDOMANDA"));
					rinnovoHelper.setIscrizioneDomandaVisible(this.getCustomConfigManager().isVisible("RINNALBO-DOCUM", "PDFDOMANDA"));
					if(rinnovoHelper.isIscrizioneDomandaVisible()){
						rinnovoHelper.setListaFirmatariMandataria(this.composeListaFirmatariMandataria(datiImpresaHelper));
					}
					if (dettBando.getDatiGeneraliBandoIscrizione().getAmmesseRTI()) {
						rinnovoHelper.setAmmesseRTI(true);
					}
					if (rinnovoHelper.isAmmesseRTI()) {
						rinnovoHelper.setEditRTI(true);
					}
					rinnovoHelper.setRinnovoIscrizione(true);
					this.setTarget(
							setDatiWizardRinnovo(
									this.getTarget(), 
									rinnovoHelper, 
									setCategorieFoglia));
					datiInterpretati = true;
				}
				
				if (rinnovoHelper != null) {
					rinnovoHelper.fillStepsNavigazione();
					if(!rinnovoHelper.isIscrizioneDomandaVisible()){
						rinnovoHelper.getStepNavigazione().remove(WizardIscrizioneHelper.STEP_SCARICA_ISCRIZIONE);
					}
				}

				if (SUCCESS.equals(this.getTarget())) {
					this.getSession().put(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO,
										  rinnovoHelper);
					this.nextResultAction = rinnovoHelper.getNextAction("");
				}

			} catch (XmlException e) {
				ApsSystemUtils.logThrowable(e, this, "initRinnovo");
				this.addActionError(this.getText("Errors.reloadIscrizione.parseXml"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (IOException e) {
				ApsSystemUtils.logThrowable(e, this, "initRinnovo");
				if (!datiInterpretati.booleanValue()) {
					this.addActionError(this.getText("Errors.reloadIscrizione.bufferXml"));
				} else {
					this.addActionError(this.getText("Errors.reloadIscrizione.tempFileAllegati"));
				}
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (Exception e) {
				ApsSystemUtils.logThrowable(e, this, "initRinnovo");
				this.addActionError(this.getText("Errors.reloadIscrizione.config"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (OutOfMemoryError e) {
				ApsSystemUtils.logThrowable(e, this, "initRinnovo");
				this.addActionError(this.getText("Errors.reloadIscrizione.outOfMemory"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "initRinnovo");
				ExceptionUtils.manageExceptionError(e, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} 
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}

		this.getSession().put(PortGareSystemConstants.SESSION_ID_PAGINA, 
				              WizardRinnovoHelper.STEP_IMPRESA);

		return this.getTarget();
	}

	/**
	 * Inizializza l'helper di rinnovo  
	 */
	private String setDatiWizardRinnovo(
			String target,
			WizardIscrizioneHelper helper, 
			Set<String> setCategorieFoglia) throws ApsException, XmlException, IOException, Exception 
	{
		// NB: per sicurezza si inserisce in sessione l'helper anche qui 
		this.getSession().put(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO, helper);
		
		return this.setDatiWizardIscrizioneRinnovoFromUltimaComunicazione(
				target, 
				helper, 
				setCategorieFoglia);
	}

	/**
	 * Si crea il contenitore da porre in sessione
	 *
	 * @param dettBando dettaglio del bando d'iscrizione estratto dal backoffice
	 * @param listaStazioni elenco delle stazioni appaltanti, da usare nel caso di
	 * presenza di unica stazione appaltante
	 * @param listaCategorie elenco delle categorie selezionabili nel bando
	 * @param tipologia
	 * @return helper da memorizzare in sessione
	 * @throws Exception 
	 */
	private WizardRinnovoHelper createHelper(
			DettaglioBandoIscrizioneType dettBando,
			LinkedHashMap<String, String> listaStazioni,
			CategoriaBandoIscrizioneType[] listaCategorie, 
			int tipologia) throws Exception 
	{
		WizardRinnovoHelper rinnovoHelper = new WizardRinnovoHelper();

		rinnovoHelper.setTipologia(tipologia);
		rinnovoHelper.setDataScadenza(InitIscrizioneAction
				.calcolaDataOra(dettBando.getDatiGeneraliBandoIscrizione()
						.getDataFineIscrizione(), dettBando
						.getDatiGeneraliBandoIscrizione()
						.getOraFineIscrizione(), true));
		rinnovoHelper.setIdBando(this.getCodice());
		rinnovoHelper.setDescBando(dettBando.getDatiGeneraliBandoIscrizione().getOggetto());
		rinnovoHelper.setCategorieAssenti(listaCategorie == null || listaCategorie.length == 0);
		rinnovoHelper.setTipoClassifica(dettBando.getDatiGeneraliBandoIscrizione().getTipoClassifica());
		rinnovoHelper.setTipoElenco(tipologia);	
		rinnovoHelper.setUnicaStazioneAppaltante(listaStazioni.size() == 1);
		if (rinnovoHelper.isUnicaStazioneAppaltante()) {
			// si fissa la stazione appaltante in modo da non
			// richiederla all'utente
			Set<String> setChiavi = listaStazioni.keySet();
			for (Iterator<String> iterator = setChiavi.iterator(); iterator.hasNext();) {
				// si ha un ciclo su un unico elemento, e lo si estrae
				rinnovoHelper.getStazioniAppaltanti().add(
						(String) iterator.next());
			}
		}
		
		rinnovoHelper.setRichiestaCoordinatoreSicurezza(
				dettBando.getDatiGeneraliBandoIscrizione().getCoordinatoreSicurezza());

		rinnovoHelper.setRichiestaAscesaTorre(
				dettBando.getDatiGeneraliBandoIscrizione().getAscesaTorre());

		rinnovoHelper.setIdComunicazioneBozza(
				getComunicazioneStatoBozza(
						rinnovoHelper.getIdBando(), 
						PortGareSystemConstants.RICHIESTA_RINNOVO_ISCRIZIONE,
						this.getCurrentUser().getUsername()));

		return rinnovoHelper;
	}
	
	/**
	 * ...
	 */
	public ComunicazioneType findComunicazione(String target) throws ApsException{
		// si estraggono dal B.O. i dati dell'iscrizione
		DettaglioComunicazioneType criteriRicerca = new DettaglioComunicazioneType();
		criteriRicerca.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
		criteriRicerca.setChiave1(this.getCurrentUser().getUsername());
		criteriRicerca.setChiave2(this.getCodice());
		//AllegatoComunicazioneType allegato = null;
		ComunicazioneType comunicazione = null;
		List<DettaglioComunicazioneType> comunicazioni = null;
	
		comunicazioni = ricercaComunicazioni(setRicercaComunicazione(criteriRicerca));
	
		if (comunicazioni == null || comunicazioni.size() != 1) {
			// non dovrebbe succedere mai...si inserisce questo
			// controllo esclusivamente per blindare il codice da
			// eventuali comportamenti anomali
			this.addActionError(this.getText("Errors.reloadIscrizione.iscrizioneNotUnique"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			comunicazione = this.getComunicazioniManager()
					.getComunicazione(CommonSystemConstants.ID_APPLICATIVO,
							comunicazioni.get(0).getId());
//			allegato = estraiAllegato(comunicazione, PortGareSystemConstants.NOME_FILE_ISCRIZIONE);
		}
		return comunicazione;
	}
	
	/**
	 * ...
	 */
	private DettaglioComunicazioneType setRicercaComunicazione(DettaglioComunicazioneType criteriRicerca){
		criteriRicerca.setStato(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA);
		criteriRicerca.setTipoComunicazione(PortGareSystemConstants.RICHIESTA_RINNOVO_ISCRIZIONE);
		return criteriRicerca;
	}

}