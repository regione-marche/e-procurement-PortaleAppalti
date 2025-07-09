package it.maggioli.eldasoft.plugins.ppcommon.aps.system;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.apsadmin.system.BaseAction;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import it.eldasoft.www.sil.WSAste.GetElencoTerminiAstaOutTypeElencoEntry;
import it.eldasoft.www.sil.WSAste.GetElencoTipiClassificaOutTypeElencoEntry;
import it.eldasoft.www.sil.WSGareAppalto.GetCfgCheckPIFacoltativaOutType;
import it.eldasoft.www.sil.WSGareAppalto.GetCfgCheckParametroOutType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.IDownloadAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.IEncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.SpringAppContext;
import it.maggioli.eldasoft.plugins.ppcommon.aps.interceptor.InterceptorUtil;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.codifiche.CodificheManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.CustomConfigFeatures;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.WSOperazioniGeneraliWrapper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.aste.WSAsteWrapper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.WSGareAppaltoWrapper;
import it.maggioli.eldasoft.wsOperazioniGenerali.daticomuni.DatoCodificatoType;
import it.maggioli.eldasoft.wsOperazioniGenerali.daticomuni.ListaDatiCodificatiDocument;
import it.maggioli.eldasoft.wsOperazioniGenerali.daticomuni.ListaDatiCodificatiType;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.xmlbeans.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedHashMap;

/**
 * Interceptor generico per la gestione dei dati in formato tabellato (codice,
 * descrizione)
 *
 * Valorizza la mappa delle action con i valori dei tabellati richiesti nell'xml della action. (Gestito multilingua tramite localstrings)
 * 
 * @author Stefano.Sabbadin
 */
public class InterceptorEncodedData extends AbstractInterceptor {
	
	private static final Logger logger = LoggerFactory.getLogger(InterceptorEncodedData.class);
	
	// tabellati web service operazioni generali
	public static final String LISTA_PROVINCE = "province";
	public static final String LISTA_TIPI_NATURA_GIURIDICA = "tipiNaturaGiuridica";
	public static final String LISTA_TIPI_IMPRESA = "tipiImpresa";
	public static final String LISTA_MODALITA_COMUNICAZIONE = "modalitaComunicazione";
	public static final String LISTA_CERTIFICATORI_SOA = "certificatoriSOA";
	public static final String LISTA_TIPI_CERTIFICAZIONE_ISO = "certificazioneISO";
	public static final String LISTA_CERTIFICATORI_ISO = "certificatoriISO";
	public static final String LISTA_GRADI_ABILITAZIONE_PREVENTIVA = "gradoAbilitPreventiva";
	public static final String LISTA_TIPI_INDIRIZZO = "tipiIndirizzo";
	public static final String LISTA_TIPI_ALTRA_CARICA = "tipiAltraCarica";
	public static final String LISTA_TIPI_COLLABORAZIONE = "tipiCollaborazione";
	public static final String LISTA_TIPI_TITOLO_TECNICO = "titoliSoggetto";
	public static final String LISTA_TIPI_ALBO_PROFESSIONALE = "albiProfessionali";
	public static final String LISTA_TIPI_CASSA_PREVIDENZA = "cassePrevidenza";
	public static final String LISTA_ALIQUOTE_IVA = "aliquoteIVA";
	public static final String LISTA_SETTORI_PRODUTTIVI = "settoriProduttivi";
	public static final String LISTA_NAZIONI = "nazioni";
	public static final String LISTA_CODICI_NAZIONI = "codicinazioni";
	public static final String LISTA_TIPI_COMUNICAZIONE = "tipiComunicazione";
	public static final String LISTA_TIPI_COMUNICAZIONE_ULTERIORI = "tipiComunicazioneUlteriori";
	public static final String LISTA_STATI_COMUNICAZIONE = "statiComunicazione";
	public static final String LISTA_TIPI_RAGGRUPPAMENTO = "tipiRaggruppamento";
	public static final String LISTA_TIPO_PROCEDURA_CONCORSO = "tipoProceduraConcorso";	

	// tabellati fissi da portale
	public static final String LISTA_TIPI_SOGGETTO = "tipiSoggetto";
	public static final String LISTA_SESSI = "sesso";
	public static final String LISTA_SINO = "sino";
	public static final String LISTA_AMBITO_TERRITORIALE = "ambitoTerritoriale";
	public static final String LISTA_TIPI_BUSTA = "tipiBusta";

	// tabellati "ibridi"
	//public static final String LISTA_TIPI_SOGGETTO_QUALIFICA = "tipiSoggettoQualifica";
	public static final String LISTA_TIPI_AVVISO = "tipiAvviso";
	public static final String LISTA_NAZIONI_CODIFICATE = "nazioniCodificate";
	public static final String LISTA_TIPI_AVVISO_GENERALI = "tipiAvvisoGenerali";

	// tabellati web service gare
	public static final String LISTA_STAZIONI_APPALTANTI = CommonSystemConstants.LISTA_STAZIONI_APPALTANTI;
	public static final String LISTA_STAZIONI_APPALTANTI_L190 = CommonSystemConstants.LISTA_STAZIONI_APPALTANTI + "L190";
	public static final String LISTA_TIPI_IMPRESA_PER_ISCRIZ_ALBO = "tipiImpresaIscrAlbo";
	public static final String LISTA_TIPI_IMPRESA_DITTA_INDIVIDUALE = "tipiImpresaDittaIndiv";
	public static final String LISTA_TIPI_IMPRESA_LIBERO_PROFESSIONISTA = "tipiImpresaLiberoProf";
	public static final String LISTA_TIPI_IMPRESA_SOCIALE = "tipiImpresaSociale";
	public static final String LISTA_TIPI_IMPRESA_CONSORZIO = "tipiImpresaConsorzio";
	public static final String LISTA_TIPI_APPALTO = "tipiAppalto";
	public static final String LISTA_TIPI_APPALTO_ISCRIZIONE_ELENCO_OPERATORI = "tipiAppaltoIscrAlbo";
	public static final String LISTA_TIPI_PROCEDURA = "tipiProcedura";
	public static final String LISTA_TIPI_AGGIUDICAZIONE = "tipiAggiudicazione";
	public static final String LISTA_STATI_GARA = "statiGara";
	public static final String LISTA_STATI_DETTAGLIO_GARA = "statiDettaglioGara";
	public static final String LISTA_ESITI_GARA = "esitiGara";
	public static final String LISTA_CLASSIFICAZIONE_LAVORI = "classifLavori";
	public static final String LISTA_CLASSIFICAZIONE_LAVORI_ISCRIZIONE_ELENCO_OPERATORI = "classifLavoriIscrAlbo";
	public static final String LISTA_CLASSIFICAZIONE_LAVORI_SOTTO_SOGLIA = "classifLavoriSottoSoglia";
	public static final String LISTA_CLASSIFICAZIONE_FORNITURE = "classifForniture";
	public static final String LISTA_CLASSIFICAZIONE_SERVIZI = "classifServizi";
	public static final String LISTA_CLASSIFICAZIONE_SERVIZI_PROFESSIONALI = "classifServiziProf";
	public static final String LISTA_TIPI_ELENCO_OPERATORI = "tipiElencoOperatori";
	public static final String LISTA_TIPI_CLASSIFICA = "tipiClassifica";
	public static final String LISTA_STATI_ISCRIZIONE_ELENCO_OPERATORI = "statiIscrAlbo";
	public static final String LISTA_TIPI_ARTICOLO = "tipiArticolo";
	public static final String LISTA_TIPI_PREZZO_ARTICOLO_RIFERITO_A = "tipiPrezzoArtRiferitoA";
	public static final String LISTA_TIPI_UNITA_MISURA = "tipiUnitaMisura";
	public static final String LISTA_TIPI_UNITA_MISURA_TEMPI_CONSEGNA = "tipiUnitaMisuraTempiConsegna";
	public static final String LISTA_STATI_PRODOTTO = "statiProdotto";
	public static final String LISTA_SEZIONI_WHITELIST_ANTIMAFIA = "sezioniWhitelist";
	public static final String LISTA_RATING_LEGALE = "ratingLegale";
	public static final String LISTA_CLASSI_DIMENSIONE = "classiDimensione";
	public static final String LISTA_SETTORE_ATTIVITA_ECONOMICA = "settoreAttivitaEconomica";
	public static final String LISTA_TIPO_ALTRI_SOGGETTI = "tipoAltriSoggetti";	
	public static final String LISTA_TIPI_REGIME_FISCALE = "tipiRegimeFiscale";
	public static final String LISTA_FASI_STIPULE = "listaFasiStipule";
	public static final String LISTA_STATI_STIPULE = "listaStatiStipule";
	public static final String LISTA_SOGGETTI_TIPI_QUALIFICA = "SoggettoImpresa.tipiSoggettoQualifica";
	public static final String LISTA_SOGGETTI_TIPI_SOGGETTO = "SoggettoImpresa.tipiSoggetto";
	public static final String LISTA_TIPOLIGIA_PRODOTTO = "tipologiaProdotto";
	public static final String LISTA_ORDER_CRITERIA = "orderCriteria";
	public static final String LISTA_TIPO_AMMISSIONE_PROCEDURA = "tipiAmmissioneProcedura";
	public static final String LISTA_TIPI_AVVALIMENTO = "tipiAvvalimento";
	public static final String LISTA_STATI_AVVISO = "statiAvviso";

	public static final String LISTA_STATI_ELENCO = "statiElenco";

	// tabellati di configurazione web service gare
	public static final String CHECK_PARTITA_IVA_FACOLTATIVA_LIBERO_PROFESSIONISTA = "checkPILiberoProf";
	public static final String CHECK_PARTITA_IVA_FACOLTATIVA_IMPRESA_SOCIALE = "checkPIImprSociale";
	public static final String CHECK_GRUPPO_IVA = "checkGruppoIVA";

	// tabellati da ppcommon_properties
	public static final String LISTA_TIPOLOGIE_ASSISTENZA = "tipologieAssistenza";

    // tabellati web service aste
    public static final String LISTA_TIPI_TERMINE_ASTA = "terminiAsta";
    public static final String LISTA_TIPI_CLASSIFICA_ASTA = "tipiClassificaAsta";

    // tabellati web service NSO
    public static final String LISTA_STATI_ORDINI_NSO = "statiOrdineNso";

	// tabellato tipologie di comunicazioni (PORTAPPALT-556)
	public static final String LISTA_TIPOLOGIE_COMUNICAZIONI = "tipologieComunicazioni";
	public static final String LISTA_TIPOLOGIE_SOCIETA_COOPERATIVE = "tipiSocietaCooperative";
	public static final String LISTA_FORME_GIURIDICHE_COOPERATIVE = "formeGiuridicheConSocCoop";
	public static final String LISTA_RATING = "listaRating";

	// tabellato tipi impresa e natura giuridica (Michelangelo (SACE))
	public static final String LISTA_TIPI_IMPRESA_NATURA_GIURIDICA = "tipiImpresaNaturaGiuridica"; 	

	
	// valori per il tabellato "order criteria"
	public static final String ORDER_CRITERIA_DATA_SCADENZA_ASC 		= "DATA_SCADENZA_ASC";			// Data scadenza crescente
	public static final String ORDER_CRITERIA_DATA_SCADENZA_DESC		= "DATA_SCADENZA_DESC"; 		// Data scadenza decrescente
	public static final String ORDER_CRITERIA_DATA_PUBBLICAZIONE_ASC	= "DATA_PUBBLICAZIONE_ASC";		// Data pubblicazione crescente
	public static final String ORDER_CRITERIA_DATA_PUBBLICAZIONE_DESC	= "DATA_PUBBLICAZIONE_DESC"; 	// Data pubblicazione decrescente
	public static final String ORDER_CRITERIA_IMPORTO_ASC				= "IMPORTO_ASC"; 				// Importo crescente
	public static final String ORDER_CRITERIA_IMPORTO_DESC				= "IMPORTO_DESC"; 				// Importo decrescente
	
	
	/**
	 * UID
	 */
	private static final long serialVersionUID = 4981865790431039966L;

	private String types;
	private Object actionInvocation; 

	/**
	 * @param types
	 *            the types to set
	 */
	public void setTypes(String types) {
		this.types = types;
	}
	
	/*
	 * @see
	 * com.opensymphony.xwork2.interceptor.AbstractInterceptor#intercept(com
	 * .opensymphony.xwork2.ActionInvocation)
	 */
	@Override
	public String intercept(ActionInvocation actionInvocation) throws Exception {
		try {
			if (this.types != null) {
				//Se true prover� a sostituire i valori dei tabellati, con la versione tradotta nelle localstrings
				boolean areLocalizedTextNeeded =
						isMultiLanguageEnabled()
								&& !isExportAction(actionInvocation)
								&& !isImportAction(actionInvocation);

				this.actionInvocation = actionInvocation.getAction();

				// split per l'ottenimento dell'elenco di tabellati di
				// richiedere mediante servizio
				Arrays.stream(types.split(","))
					.map(String::trim)
				.forEach(type -> putTabellatiIntoAction((BaseAction) actionInvocation.getAction(), type, areLocalizedTextNeeded));
			}

		} catch (Exception e) {
			// si traccia nel log l'errore
			ApsSystemUtils.logThrowable(e, this, "intercept");
			// si gestisce la messaggistica per l'utente
			ExceptionUtils.manageExceptionError(e,
					(ActionSupport) actionInvocation.getAction());
			// si modifica il target per la determinazione del result
			((IEncodedDataAction) actionInvocation.getAction())
				.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}

		return actionInvocation.invoke();
	}

	/**
	 * Mi faccio ritornare i valore dei tabellati e se necessario lo traduco.
	 * @param actionInvocation
	 * @param type
	 * @param areLocalizedTextNeeded
	 */
	private void putTabellatiIntoAction(BaseAction action, String type, boolean areLocalizedTextNeeded) {
		// per ogni codifica viene delegata l'attivita' di lettura dei
		// valori all'opportuno metodo di gestione e quindi si valorizza
		// la hash dell'azione invocante con il tabellato
		try {
			LinkedHashMap<String, String> tabulati = getList(type);
			((IEncodedDataAction) this.actionInvocation).getMaps().put(type, tabulati);
			if (areLocalizedTextNeeded)
				toLangList(action, type, tabulati);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Controlla se la customization per il multilingua (dell'interceptor) e' abilitata
	 * @return
	 * @throws Exception
	 */
	private boolean isMultiLanguageEnabled() throws Exception {
		ICustomConfigManager configManager = (ICustomConfigManager)
				WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext())
						.getBean(CommonSystemConstants.CUSTOM_CONFIG_MANAGER);
		return configManager.isActiveFunction(CustomConfigFeatures.MULTILINGUA.getKey(), CustomConfigFeatures.MULTILINGUA.getValue());
	}

	/**
	 * Controlla se e' stata richiesta l'istanza di un azione di export
	 * @param actionInvocation
	 * @return
	 */
	private boolean isExportAction(ActionInvocation actionInvocation) {
		return actionInvocation.getAction() instanceof IDownloadAction;
	}

	/**
	 * Controlla se e' stata richiesta l'istanza di una action di import.
	 * Tutti gli import devono utilizzare l'interceptor: "fileUpload"; quindi, se questo interceptor e' nell'elenco
	 * degli interceptor da invocare/invocati, allora sono in un action di import.
	 * @param actionInvocation
	 * @return
	 */
	private boolean isImportAction(ActionInvocation actionInvocation) {
		return InterceptorUtil.isInterceptorPresent(actionInvocation, "fileUpload");
	}

	/**
	 * Sostituisco alla mappa i valori con le traduzioni (se presenti sulle localstrings)
	 * @param action
	 * @param type
	 * @param dbValues
	 */
	private void toLangList(BaseAction action, String type, LinkedHashMap<String, String> dbValues) {
		dbValues.replaceAll((key, value) -> getI18Value(key, value, type, action));
	}

	/**
	 * Mi ritorna il nuovo valore della mappa (in caso la localstrings non esista, o sia vuota, si tiene il valore di default)
	 * @param key
	 * @param value
	 * @param type
	 * @param action
	 * @return
	 */
	public String getI18Value(String key, String value, String type, BaseAction action) {
		String multiLang = value;

		try {
			multiLang = action.getI18nManager().getLabel(
					String.format("TABELLATO_%s_%s", getTypeToPreventDuplicate(type), key)
					, action.getCurrentLang().getCode()
					, false
			);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return StringUtils.isNotEmpty(multiLang) && !StringUtils.equals(multiLang, "#")
			   ? multiLang
			   : value;
	}

	/**
	 * LISTA_TIPI_IMPRESA_PER_ISCRIZ_ALBO effettua la stessa query di LISTA_TIPI_IMPRESA, solo che con qualche filtro in pi�
	 * per non creare label duplicate, utilizzo quelle di LISTA_TIPI_IMPRESA
	 * @param type
	 * @return
	 */
	private String getTypeToPreventDuplicate(String type) {
		return LISTA_TIPI_IMPRESA_PER_ISCRIZ_ALBO.equals(type) ? LISTA_TIPI_IMPRESA : type;
	}

	/**
	 * inizializza le liste di decodifica dei tabellati 
	 * @throws XmlException 
	 * @throws RemoteException 
	 */
	private static LinkedHashMap<String, String> getList(String type) throws RemoteException, XmlException {
		LinkedHashMap<String, String> lista = null;

		//Convertito in switch, in quanto pi� performante per grossi if/else come questo (convertito da IDE e non manualmente)
		switch (type) {
			case LISTA_PROVINCE:
				lista = getProvince();
				break;
			case LISTA_TIPI_NATURA_GIURIDICA:
				lista = getTipiNaturaGiuridica();
				break;
			case LISTA_TIPI_IMPRESA:
				lista = getTipiImpresa();
				break;
			case LISTA_TIPI_IMPRESA_PER_ISCRIZ_ALBO:
				lista = getTipiImpresaPerIscrizioneAlbo();
				break;
			case LISTA_TIPI_IMPRESA_DITTA_INDIVIDUALE:
				lista = getTipiImpresaDittaIndividuale();
				break;
			case LISTA_TIPI_IMPRESA_LIBERO_PROFESSIONISTA:
				lista = getTipiImpresaLiberoProfessionista();
				break;
			case LISTA_TIPI_IMPRESA_SOCIALE:
				lista = getTipiImpresaSociale();
				break;
			case LISTA_TIPI_IMPRESA_CONSORZIO:
				lista = getTipiImpresaConsorzio();
				break;
			case LISTA_MODALITA_COMUNICAZIONE:
				lista = getModalitaComunicazione();
				break;
			case LISTA_CERTIFICATORI_SOA:
				lista = getCertificatoriSOA();
				break;
			case LISTA_TIPI_CERTIFICAZIONE_ISO:
				lista = getTipiCertificazioneISO();
				break;
			case LISTA_CERTIFICATORI_ISO:
				lista = getCertificatoriISO();
				break;
			case LISTA_GRADI_ABILITAZIONE_PREVENTIVA:
				lista = getGradiAbilitazionePreventiva();
				break;
			case LISTA_TIPI_INDIRIZZO:
				lista = getTipiIndirizzo();
				break;
			case LISTA_TIPI_ALTRA_CARICA:
				lista = getTipiAltraCarica();
				break;
			case LISTA_TIPI_COLLABORAZIONE:
				lista = getTipiCollaborazione();
				break;
			case LISTA_TIPI_TITOLO_TECNICO:
				lista = getTipiTitoloTecnico();
				break;
			case LISTA_TIPI_ALBO_PROFESSIONALE:
				lista = getTipiAlboProfessionale();
				break;
			case LISTA_TIPI_CASSA_PREVIDENZA:
				lista = getTipiCassaPrevidenza();
				break;
			case LISTA_ALIQUOTE_IVA:
				lista = getAliquoteIVA();
				break;
			case LISTA_SETTORI_PRODUTTIVI:
				lista = getSettoriProduttivi();
				break;
			case LISTA_NAZIONI:
				lista = getNazioni();
				break;
			case LISTA_CODICI_NAZIONI:
				lista = getCodiciNazioni();
				break;
			case LISTA_TIPI_COMUNICAZIONE:
				lista = getTipiComunicazione();
				break;
			case LISTA_TIPI_COMUNICAZIONE_ULTERIORI:
				lista = getTipiComunicazioneUlteriori();
				break;
			case LISTA_STATI_COMUNICAZIONE:
				lista = getStatiComunicazione();
				break;
			case LISTA_TIPI_SOGGETTO:
				lista = getTipiSoggetto();
				break;
			case LISTA_SOGGETTI_TIPI_SOGGETTO:
				// tabellato creato runtime!!!
				// utilizzato per la codifica/decodifica del campo "ISoggettoImpresa.TipoSoggetto"
				lista = getSoggettoImpresaTipiSoggetto();
				break;
			case LISTA_SOGGETTI_TIPI_QUALIFICA:
				// tabellato creato runtime!!!
				// utilizzato per la codifica/decodifica del campo "ISoggettoImpresa.SoggettoQualifica"
				lista = getSoggettoImpresaTipiSoggettoQualifica();
				break;
			case LISTA_SESSI:
				lista = getSessi();
				break;
			case LISTA_SINO:
				lista = getSiNo();
				break;
			case LISTA_TIPI_AVVISO:
				lista = getTipiAvviso();
				break;
			case LISTA_TIPI_AVVISO_GENERALI:
				lista = getTipiAvvisoGenerali();
				break;
			case LISTA_STAZIONI_APPALTANTI:
				lista = getStazioniAppaltanti();
				break;
			case LISTA_STAZIONI_APPALTANTI_L190:
				lista = getStazioniAppaltantiL190();
				break;
			case LISTA_TIPI_APPALTO:
				lista = getTipiAppalto();
				break;
			case LISTA_TIPI_APPALTO_ISCRIZIONE_ELENCO_OPERATORI:
				lista = getTipiAppaltoIscrizioneAlbo();
				break;
			case LISTA_TIPI_PROCEDURA:
				lista = getTipiProcedura();
				break;
			case LISTA_TIPI_AGGIUDICAZIONE:
				lista = getTipiAggiudicazione();
				break;
			case LISTA_TIPI_ELENCO_OPERATORI:
				lista = getTipiElencoOperatori();
				break;
			case LISTA_TIPI_CLASSIFICA:
				lista = getTipiClassifica();
				break;
			case LISTA_STATI_GARA:
				lista = getStatiGara();
				break;
			case LISTA_STATI_DETTAGLIO_GARA:
				lista = getStatiDettaglioGara();
				break;
			case LISTA_ESITI_GARA:
				lista = getEsitiGara();
				break;
			case LISTA_STATI_ISCRIZIONE_ELENCO_OPERATORI:
				lista = getStatiIscrizioneAlbo();
				break;
			case LISTA_CLASSIFICAZIONE_LAVORI:
				lista = getClassificazioneLavori();
				break;
			case LISTA_CLASSIFICAZIONE_LAVORI_ISCRIZIONE_ELENCO_OPERATORI:
				lista = getClassificazioneLavoriIscrizioneAlbo();
				break;
			case LISTA_CLASSIFICAZIONE_LAVORI_SOTTO_SOGLIA:
				lista = getClassificazioneLavoriSottoSoglia();
				break;
			case LISTA_CLASSIFICAZIONE_FORNITURE:
				lista = getClassificazioneForniture();
				break;
			case LISTA_CLASSIFICAZIONE_SERVIZI:
				lista = getClassificazioneServizi();
				break;
			case LISTA_CLASSIFICAZIONE_SERVIZI_PROFESSIONALI:
				lista = getClassificazioneServiziProfessionali();
				break;
			case LISTA_TIPI_ARTICOLO:
				lista = getTipiArticolo();
				break;
			case LISTA_TIPI_PREZZO_ARTICOLO_RIFERITO_A:
				lista = getTipiPrezzoArticoloRiferitoA();
				break;
			case LISTA_TIPI_UNITA_MISURA:
				lista = getTipiUnitaMisura();
				break;
			case LISTA_TIPI_UNITA_MISURA_TEMPI_CONSEGNA:
				lista = getTipiUnitaMisuraTempiConsegna();
				break;
			case LISTA_STATI_PRODOTTO:
				lista = getStatiProdotto();
				break;
			case CHECK_PARTITA_IVA_FACOLTATIVA_LIBERO_PROFESSIONISTA:
				lista = getCheckPartitaIVALiberoProfessionista();
				break;
			case CHECK_PARTITA_IVA_FACOLTATIVA_IMPRESA_SOCIALE:
				lista = getCheckPartitaIVAImpresaSociale();
				break;
			case CHECK_GRUPPO_IVA:
				lista = getCheckGruppoIVA();
				break;
			case LISTA_TIPOLOGIE_ASSISTENZA:
				lista = getListeTipologieAssistenza();
				break;
			case LISTA_TIPI_TERMINE_ASTA:
				lista = getTerminiAsta();
				break;
			case LISTA_TIPI_CLASSIFICA_ASTA:
				lista = getTipiClassificaAsta();
				break;
			case LISTA_SEZIONI_WHITELIST_ANTIMAFIA:
				lista = getElencoSezioniIscrizioneWhitelistAntimafia();
				break;
			case LISTA_RATING_LEGALE:
				lista = getElencoRatingLegale();
				break;
			case LISTA_CLASSI_DIMENSIONE:
				lista = getClassiDimensioneDipendente();
				break;
			case LISTA_SETTORE_ATTIVITA_ECONOMICA:
				lista = getSettoriAttivitaEconomica();
				break;
			case LISTA_TIPO_ALTRI_SOGGETTI:
				lista = getElencoAltriSoggetti();
				break;
			case LISTA_STATI_ORDINI_NSO:
				lista = getElencoStatiOrdiniNSO();
				break;
			case LISTA_AMBITO_TERRITORIALE:
				lista = getAmbitoTerritoriale();
				break;
			case LISTA_TIPI_RAGGRUPPAMENTO:
				lista = getTipiRaggruppamento();
				break;
			case LISTA_TIPI_REGIME_FISCALE:
				lista = getTipiRegimeFiscale();
				break;
			case LISTA_NAZIONI_CODIFICATE:
				lista = getNazioniCodificate();
				break;
			case LISTA_STATI_STIPULE:
				lista = getStatiStipule();
				break;
			case LISTA_FASI_STIPULE:
				lista = getFasiStipule();
				break;
			case LISTA_TIPOLOGIE_COMUNICAZIONI:
				lista = getElencoEntitaComunicazione();
				break;
			case LISTA_TIPOLIGIA_PRODOTTO:
				lista = getTipologiaProdotto();
				break;
			case LISTA_TIPOLOGIE_SOCIETA_COOPERATIVE:
				lista = getSocietaCooperative();
				break;
			case LISTA_FORME_GIURIDICHE_COOPERATIVE:
				lista = getFormeGiuridicheCooperative();
				break;
			case LISTA_ORDER_CRITERIA:
				lista = getOrderCriteria();
				break;
			case LISTA_TIPO_AMMISSIONE_PROCEDURA:
				lista = getTipiAmmissioneProcedura();
				break;
			case LISTA_STATI_ELENCO:
				lista = getListaStatiElenco();
				break;
			case LISTA_RATING:
				lista = getListaRating();
				break;
			case LISTA_TIPO_PROCEDURA_CONCORSO:
				lista = getTipoProceduraConcorso();
				break;
			case LISTA_TIPI_AVVALIMENTO:
				lista = getTipiAvvalimento();
				break;
			case LISTA_TIPI_IMPRESA_NATURA_GIURIDICA:
				lista = getTipiImpresaNaturaGiuridica();
				break;
			case LISTA_STATI_AVVISO:
				lista = getStatiAvviso();
				break;
			case LISTA_TIPI_BUSTA:
				lista = getTipiBusta();
				break;
			default:
				// eccezione che si puo' verificare solo in fase di sviluppo
				throw new RuntimeException(
						"il tipo '" + type + "' definito nell'interceptor non corrisponde ad alcun dato tipizzato attualmente gestito");
		}

		return lista;
	}

	private static LinkedHashMap<String, String> getListaStatiElenco() {
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_STATI_ELENCO)) {
			lista = hash.get(LISTA_STATI_ELENCO);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			lista = new LinkedHashMap<>();
			lista.put("1", "In verifica da Enti esterni");
			lista.put("0", "Regolare");
			hash.put(LISTA_STATI_ELENCO, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getListaRating() throws RemoteException, XmlException {
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_RATING)) {
			lista = hash.get(LISTA_RATING);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSOperazioniGeneraliWrapper wrapper = (WSOperazioniGeneraliWrapper) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.WS_OPERAZIONI_GENERALI,
							ServletActionContext.getRequest());

			lista = InterceptorEncodedData.parseXml(wrapper.getProxyWSOPGenerali().getListaRating());
			hash.put(LISTA_RATING, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getTipoProceduraConcorso() throws RemoteException, XmlException {
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_TIPO_PROCEDURA_CONCORSO)) {
			lista = hash.get(LISTA_TIPO_PROCEDURA_CONCORSO);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSOperazioniGeneraliWrapper wrapper = (WSOperazioniGeneraliWrapper) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.WS_OPERAZIONI_GENERALI,
							ServletActionContext.getRequest());

			lista = InterceptorEncodedData.parseXml(wrapper.getProxyWSOPGenerali().getListaTipoProceduraConcorso());
			hash.put(LISTA_TIPO_PROCEDURA_CONCORSO, lista);
		}

		return clonaHash(lista);
	}

	/**
	 * Restituisce il tabellato associato al tipo lista
	 * Viene utilizzato in: validatori, import...
	 */
	public static LinkedHashMap<String, String> get(String type) throws Exception {
		return InterceptorEncodedData.getList(type);
	}

	
	private static LinkedHashMap<String, String> getNazioniCodificate() throws RemoteException, XmlException {
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_NAZIONI_CODIFICATE)) {
			lista = hash.get(LISTA_NAZIONI_CODIFICATE);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
						ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getNazioni();
			lista = InterceptorEncodedData.parseXml(xml);
			logger.debug("Loaded LISTA_NAZIONI_CODIFICATE {}",lista.size());
			hash.put(LISTA_NAZIONI_CODIFICATE, lista);
		}

		return clonaHash(lista);
	}

	/**
	 * Ritorna il bean cache delle codifiche ricevute mediante i servizi
	 * 
	 * @return cache delle codifiche
	 */
	private static Hashtable<String, LinkedHashMap<String, String>> getHash() {
		WebApplicationContext ctx =
				WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
		return ((CodificheManager) ctx.getBean(CommonSystemConstants.CACHE_CODIFICHE)).getCodifiche();
	}

	/**
	 * Genera l'elenco dei codici tabellati mediante il parsing della risposta
	 * XML
	 * 
	 * @param xml
	 *            risposta XML contenente i valori tabellati
	 * 
	 * @return hash ordinata di coppie (codice,descrizione)
	 * @throws XmlException
	 */
	public static LinkedHashMap<String, String> parseXml(String xml)
			throws XmlException {
		LinkedHashMap<String, String> lista = new LinkedHashMap<String, String>();

		ListaDatiCodificatiDocument document = ListaDatiCodificatiDocument.Factory
				.parse(xml);
		ListaDatiCodificatiType listaDati = document.getListaDatiCodificati();
		for (int i = 0; i < listaDati.sizeOfElementoArray(); i++) {
			DatoCodificatoType datoCodificato = listaDati.getElementoArray(i);
			lista.put(datoCodificato.getCodice(), datoCodificato
					.getDescrizione());
		}

		return lista;
	}

	/**
	 * Genera l'elenco dei codici tabellati mediante il parsing della risposta
	 * XML ed aggiungendo un prefisso alle chiavi.
	 * 
	 * @param xml
	 *            risposta XML contenente i valori tabellati
	 * @param keyPrefix prefisso da aggiungere ad ogni codice
	 * 
	 * @return hash ordinata di coppie (codice,descrizione)
	 * @throws XmlException
	 */
	public static LinkedHashMap<String, String> parseXml(String xml,
			String keyPrefix) throws XmlException {
		LinkedHashMap<String, String> lista = new LinkedHashMap<String, String>();

		ListaDatiCodificatiDocument document = ListaDatiCodificatiDocument.Factory
				.parse(xml);
		ListaDatiCodificatiType listaDati = document.getListaDatiCodificati();
		for (int i = 0; i < listaDati.sizeOfElementoArray(); i++) {
			DatoCodificatoType datoCodificato = listaDati.getElementoArray(i);
			lista.put(keyPrefix + datoCodificato.getCodice(),
					datoCodificato.getDescrizione());
		}

		return lista;
	}

	/**
	 * Si ritorna una copia della hash, in modo da eventualmente poter lavorare
	 * sui singoli elementi senza sporcare la lista ufficiale estratta
	 */
	@SuppressWarnings("unchecked")
	private static LinkedHashMap<String, String> clonaHash(
			LinkedHashMap<String, String> lista) {
		LinkedHashMap<String, String> risultato = new LinkedHashMap<>();
		if (lista != null)
			risultato = (LinkedHashMap<String, String>) lista.clone();
		return risultato;
	}

	//********************************************************************************
	//********************************************************************************
	//********************************************************************************
	
	private static LinkedHashMap<String, String> getProvince() 
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_PROVINCE)) {
			lista = hash.get(LISTA_PROVINCE);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSOperazioniGeneraliWrapper wrapper = (WSOperazioniGeneraliWrapper) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.WS_OPERAZIONI_GENERALI,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSOPGenerali().getElencoProvince();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_PROVINCE, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getTipiNaturaGiuridica()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_TIPI_NATURA_GIURIDICA)) {
			lista = hash.get(LISTA_TIPI_NATURA_GIURIDICA);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSOperazioniGeneraliWrapper wrapper = (WSOperazioniGeneraliWrapper) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.WS_OPERAZIONI_GENERALI,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSOPGenerali().getElencoTipiNaturaGiuridica();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_TIPI_NATURA_GIURIDICA, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getTipiImpresa()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_TIPI_IMPRESA)) {
			lista = hash.get(LISTA_TIPI_IMPRESA);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSOperazioniGeneraliWrapper wrapper = (WSOperazioniGeneraliWrapper) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.WS_OPERAZIONI_GENERALI,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSOPGenerali().getElencoTipiImpresa();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_TIPI_IMPRESA, lista);
		}

		return clonaHash(lista);
	}
	
	private static LinkedHashMap<String, String> getStatiStipule() throws RemoteException, XmlException {
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_STATI_STIPULE)) {
			lista = hash.get(LISTA_STATI_STIPULE);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
						ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getElencoStatiStipula();
			lista = InterceptorEncodedData.parseXml(xml);
			logger.debug("Loaded LISTA_STATI_STIPULE {}",lista.size());
			hash.put(LISTA_STATI_STIPULE, lista);
		}

		return clonaHash(lista);
	}
	
	private static LinkedHashMap<String, String> getFasiStipule() throws RemoteException, XmlException {
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_FASI_STIPULE)) {
			lista = hash.get(LISTA_FASI_STIPULE);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
						ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getElencoFasiStipula();
			lista = InterceptorEncodedData.parseXml(xml);
			logger.debug("Loaded LISTA_FASI_STIPULE {}",lista.size());
			hash.put(LISTA_FASI_STIPULE, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getTipiImpresaPerIscrizioneAlbo()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_TIPI_IMPRESA_PER_ISCRIZ_ALBO)) {
			lista = hash.get(LISTA_TIPI_IMPRESA_PER_ISCRIZ_ALBO);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getElencoTipiImpresaPerIscrizione();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_TIPI_IMPRESA_PER_ISCRIZ_ALBO, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getTipiImpresaDittaIndividuale()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_TIPI_IMPRESA_DITTA_INDIVIDUALE)) {
			lista = hash.get(LISTA_TIPI_IMPRESA_DITTA_INDIVIDUALE);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getElencoTipiImpresaDittaIndividuale();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_TIPI_IMPRESA_DITTA_INDIVIDUALE, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getTipiImpresaLiberoProfessionista()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_TIPI_IMPRESA_LIBERO_PROFESSIONISTA)) {
			lista = hash.get(LISTA_TIPI_IMPRESA_LIBERO_PROFESSIONISTA);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getElencoTipiImpresaLiberoProfessionista();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_TIPI_IMPRESA_LIBERO_PROFESSIONISTA, lista);
		}

		return clonaHash(lista);
	}
	
	private static LinkedHashMap<String, String> getTipiImpresaSociale()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;
		
		if (hash.containsKey(LISTA_TIPI_IMPRESA_SOCIALE)) {
			lista = hash.get(LISTA_TIPI_IMPRESA_SOCIALE);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getElencoTipiImpresaSociale();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_TIPI_IMPRESA_SOCIALE, lista);
		}
		
		return clonaHash(lista);
	}
	
	private static LinkedHashMap<String, String> getTipiImpresaConsorzio()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_TIPI_IMPRESA_CONSORZIO)) {
			lista = hash.get(LISTA_TIPI_IMPRESA_CONSORZIO);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare()
					.getElencoTipiImpresaConsorzio();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_TIPI_IMPRESA_CONSORZIO, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getModalitaComunicazione()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_MODALITA_COMUNICAZIONE)) {
			lista = hash.get(LISTA_MODALITA_COMUNICAZIONE);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSOperazioniGeneraliWrapper wrapper = (WSOperazioniGeneraliWrapper) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.WS_OPERAZIONI_GENERALI,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSOPGenerali().getElencoModalitaComunicazione();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_MODALITA_COMUNICAZIONE, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getCertificatoriSOA()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_CERTIFICATORI_SOA)) {
			lista = hash.get(LISTA_CERTIFICATORI_SOA);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSOperazioniGeneraliWrapper wrapper = (WSOperazioniGeneraliWrapper) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.WS_OPERAZIONI_GENERALI,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSOPGenerali().getElencoOrganismiCertificatoriSOA();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_CERTIFICATORI_SOA, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getTipiCertificazioneISO()
			throws RemoteException, XmlException 		
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_TIPI_CERTIFICAZIONE_ISO)) {
			lista = hash.get(LISTA_TIPI_CERTIFICAZIONE_ISO);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSOperazioniGeneraliWrapper wrapper = (WSOperazioniGeneraliWrapper) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.WS_OPERAZIONI_GENERALI,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSOPGenerali().getElencoTipiCertificazioneISO();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_TIPI_CERTIFICAZIONE_ISO, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getCertificatoriISO()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_CERTIFICATORI_ISO)) {
			lista = hash.get(LISTA_CERTIFICATORI_ISO);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSOperazioniGeneraliWrapper wrapper = (WSOperazioniGeneraliWrapper) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.WS_OPERAZIONI_GENERALI,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSOPGenerali().getElencoOrganismiCertificatoriISO();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_CERTIFICATORI_ISO, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getGradiAbilitazionePreventiva()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_GRADI_ABILITAZIONE_PREVENTIVA)) {
			lista = hash.get(LISTA_GRADI_ABILITAZIONE_PREVENTIVA);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSOperazioniGeneraliWrapper wrapper = (WSOperazioniGeneraliWrapper) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.WS_OPERAZIONI_GENERALI,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSOPGenerali().getElencoGradiAbilitazionePreventiva();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_GRADI_ABILITAZIONE_PREVENTIVA, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getTipiIndirizzo()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_TIPI_INDIRIZZO)) {
			lista = hash.get(LISTA_TIPI_INDIRIZZO);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSOperazioniGeneraliWrapper wrapper = (WSOperazioniGeneraliWrapper) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.WS_OPERAZIONI_GENERALI,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSOPGenerali().getElencoTipiIndirizzo();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_TIPI_INDIRIZZO, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getTipiAltraCarica()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_TIPI_ALTRA_CARICA)) {
			lista = hash.get(LISTA_TIPI_ALTRA_CARICA);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSOperazioniGeneraliWrapper wrapper = (WSOperazioniGeneraliWrapper) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.WS_OPERAZIONI_GENERALI,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSOPGenerali().getElencoTipiAltraCarica();
			lista = InterceptorEncodedData
					.parseXml(
							xml,
							CommonSystemConstants.TIPO_SOGGETTO_ALTRA_CARICA
							+ CommonSystemConstants.SEPARATORE_TABELLATI_CONCATENATI);
			hash.put(LISTA_TIPI_ALTRA_CARICA, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getTipiCollaborazione()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_TIPI_COLLABORAZIONE)) {
			lista = hash.get(LISTA_TIPI_COLLABORAZIONE);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSOperazioniGeneraliWrapper wrapper = (WSOperazioniGeneraliWrapper) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.WS_OPERAZIONI_GENERALI,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSOPGenerali().getElencoTipiCollaborazione();
			lista = InterceptorEncodedData
					.parseXml(
							xml,
							CommonSystemConstants.TIPO_SOGGETTO_COLLABORATORE
							+ CommonSystemConstants.SEPARATORE_TABELLATI_CONCATENATI);
			hash.put(LISTA_TIPI_COLLABORAZIONE, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getTipiTitoloTecnico()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_TIPI_TITOLO_TECNICO)) {
			lista = hash.get(LISTA_TIPI_TITOLO_TECNICO);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSOperazioniGeneraliWrapper wrapper = (WSOperazioniGeneraliWrapper) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.WS_OPERAZIONI_GENERALI,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSOPGenerali().getElencoTipiTitoloTecnico();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_TIPI_TITOLO_TECNICO, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getTipiAlboProfessionale()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_TIPI_ALBO_PROFESSIONALE)) {
			lista = hash.get(LISTA_TIPI_ALBO_PROFESSIONALE);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSOperazioniGeneraliWrapper wrapper = (WSOperazioniGeneraliWrapper) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.WS_OPERAZIONI_GENERALI,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSOPGenerali().getElencoTipiAlboProfessionale();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_TIPI_ALBO_PROFESSIONALE, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getTipiCassaPrevidenza()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_TIPI_CASSA_PREVIDENZA)) {
			lista = hash.get(LISTA_TIPI_CASSA_PREVIDENZA);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSOperazioniGeneraliWrapper wrapper = (WSOperazioniGeneraliWrapper) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.WS_OPERAZIONI_GENERALI,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSOPGenerali().getElencoTipiCassaPrevidenza();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_TIPI_CASSA_PREVIDENZA, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getAliquoteIVA()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_ALIQUOTE_IVA)) {
			lista = hash.get(LISTA_ALIQUOTE_IVA);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSOperazioniGeneraliWrapper wrapper = (WSOperazioniGeneraliWrapper) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.WS_OPERAZIONI_GENERALI,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSOPGenerali().getElencoAliquoteIVA();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_ALIQUOTE_IVA, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getSettoriProduttivi()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_SETTORI_PRODUTTIVI)) {
			lista = hash.get(LISTA_SETTORI_PRODUTTIVI);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSOperazioniGeneraliWrapper wrapper = (WSOperazioniGeneraliWrapper) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.WS_OPERAZIONI_GENERALI,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSOPGenerali()
					.getElencoSettoriProduttivi();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_SETTORI_PRODUTTIVI, lista);
		}

		return clonaHash(lista);
	}

	/**
	 * Attenzione: la lista nazioni viene ritornata senza codice ma come coppie (descrizione, descrizione).
	 * @return mappa con coppie (descrizione, descrizione)
	 * @throws RemoteException
	 * @throws XmlException
	 */
	private static LinkedHashMap<String, String> getNazioni() 
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_NAZIONI)) {
			lista = hash.get(LISTA_NAZIONI);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSOperazioniGeneraliWrapper wrapper = (WSOperazioniGeneraliWrapper) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.WS_OPERAZIONI_GENERALI,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSOPGenerali()
					.getElencoNazioni();
			lista = new LinkedHashMap<String, String>();
			// si vuole semplificare la gestione del pregresso sostituendo i
			// campi testo per la nazione con la dropdown, ma il cui valore e'
			// sempre una descrizione e non il codice
			ListaDatiCodificatiDocument document = ListaDatiCodificatiDocument.Factory
					.parse(xml);
			ListaDatiCodificatiType listaDati = document.getListaDatiCodificati();
			for (int i = 0; i < listaDati.sizeOfElementoArray(); i++) {
				DatoCodificatoType datoCodificato = listaDati.getElementoArray(i);
				lista.put(datoCodificato.getDescrizione(), datoCodificato
						.getDescrizione());
			}
			hash.put(LISTA_NAZIONI, lista);
		}

		return clonaHash(lista);
	}
	
	/**
	 restiruisce la lista delle nazioni 
	 * @return mappa con coppie (codice, descrizione)
	 * @throws RemoteException
	 * @throws XmlException
	 */
	private static LinkedHashMap<String, String> getCodiciNazioni() 
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_CODICI_NAZIONI)) {
			lista = hash.get(LISTA_CODICI_NAZIONI);
		} else {
			WSOperazioniGeneraliWrapper wrapper = (WSOperazioniGeneraliWrapper) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.WS_OPERAZIONI_GENERALI,
							ServletActionContext.getRequest());
			
			String xml = wrapper.getProxyWSOPGenerali().getElencoNazioni();
			ListaDatiCodificatiDocument document = ListaDatiCodificatiDocument.Factory.parse(xml);
			ListaDatiCodificatiType listaDati = document.getListaDatiCodificati();
			
			lista = new LinkedHashMap<String, String>();			
			for (int i = 0; i < listaDati.sizeOfElementoArray(); i++) {
				DatoCodificatoType datoCodificato = listaDati.getElementoArray(i);
				lista.put(datoCodificato.getCodice(), datoCodificato.getDescrizione());
			}
			hash.put(LISTA_CODICI_NAZIONI, lista);
		}

		return clonaHash(lista);
	}


	private static LinkedHashMap<String, String> getTipiComunicazione() 
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_TIPI_COMUNICAZIONE)) {
			lista = hash.get(LISTA_TIPI_COMUNICAZIONE);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSOperazioniGeneraliWrapper wrapper = (WSOperazioniGeneraliWrapper) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.WS_OPERAZIONI_GENERALI,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSOPGenerali().getElencoTipiComunicazione();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_TIPI_COMUNICAZIONE, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getTipiComunicazioneUlteriori() 
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_TIPI_COMUNICAZIONE_ULTERIORI)) {
			lista = hash.get(LISTA_TIPI_COMUNICAZIONE_ULTERIORI);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSOperazioniGeneraliWrapper wrapper = (WSOperazioniGeneraliWrapper) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.WS_OPERAZIONI_GENERALI,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSOPGenerali().getElencoTipiComunicazioneUlteriori();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_TIPI_COMUNICAZIONE_ULTERIORI, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getStatiComunicazione()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_STATI_COMUNICAZIONE)) {
			lista = hash.get(LISTA_STATI_COMUNICAZIONE);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSOperazioniGeneraliWrapper wrapper = (WSOperazioniGeneraliWrapper) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.WS_OPERAZIONI_GENERALI,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSOPGenerali()
					.getElencoStatiComunicazione();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_STATI_COMUNICAZIONE, lista);
		}

		return clonaHash(lista);
	}
	
	private static LinkedHashMap<String, String> getSoggettoImpresaTipiSoggettoQualifica() 
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_SOGGETTI_TIPI_QUALIFICA)) {
			lista = hash.get(LISTA_SOGGETTI_TIPI_QUALIFICA);
		} else {
			lista = getList(InterceptorEncodedData.LISTA_TIPI_SOGGETTO);
			LinkedHashMap<String, String> lst = getList(InterceptorEncodedData.LISTA_TIPI_ALTRA_CARICA);
			if(lst != null) {
				lista.putAll(lst);
			}
			lst = getList(InterceptorEncodedData.LISTA_TIPI_COLLABORAZIONE);
			if(lst != null) {
				lista.putAll(lst);
			}
			
			hash.put(LISTA_SOGGETTI_TIPI_QUALIFICA, lista);
		}
		
		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getSoggettoImpresaTipiSoggetto() {
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_SOGGETTI_TIPI_SOGGETTO)) {
			lista = hash.get(LISTA_SOGGETTI_TIPI_SOGGETTO);
		} else {
			lista = new LinkedHashMap<String, String>();
			lista.put(CommonSystemConstants.TIPO_SOGGETTO_LEGALE_RAPPRESENTANTE, "Legale rappresentante");
			lista.put(CommonSystemConstants.TIPO_SOGGETTO_DIRETTORE_TECNICO, 	 "Direttore tecnico");
			lista.put(CommonSystemConstants.TIPO_SOGGETTO_ALTRA_CARICA, 		 "Altra carica o qualifica");	
			lista.put(CommonSystemConstants.TIPO_SOGGETTO_COLLABORATORE, 		 "Collaboratore");
			
			hash.put(LISTA_SOGGETTI_TIPI_SOGGETTO, lista);
		}
		
		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getTipiSoggetto() {
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_TIPI_SOGGETTO)) {
			lista = hash.get(LISTA_TIPI_SOGGETTO);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			lista = new LinkedHashMap<String, String>();
			lista.put(CommonSystemConstants.TIPO_SOGGETTO_LEGALE_RAPPRESENTANTE
					+ CommonSystemConstants.SEPARATORE_TABELLATI_CONCATENATI,
					"Legale rappresentante");
			lista.put(CommonSystemConstants.TIPO_SOGGETTO_DIRETTORE_TECNICO
					+ CommonSystemConstants.SEPARATORE_TABELLATI_CONCATENATI,
					"Direttore tecnico");
			// lista.put(CommonSystemConstants.TIPO_SOGGETTO_ALTRA_CARICA,
			// "Altra carica o qualifica");
			// lista.put(CommonSystemConstants.TIPO_SOGGETTO_COLLABORATORE,
			// "Collaboratore");
			hash.put(LISTA_TIPI_SOGGETTO, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getSessi() {
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_SESSI)) {
			lista = hash.get(LISTA_SESSI);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			lista = new LinkedHashMap<String, String>();
			lista.put("F", "Femmina");
			lista.put("M", "Maschio");
			hash.put(LISTA_SESSI, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getSiNo() {
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_SINO)) {
			lista = hash.get(LISTA_SINO);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			lista = new LinkedHashMap<String, String>();
			lista.put("1", "Si");
			lista.put("0", "No");
			hash.put(LISTA_SINO, lista);
		}

		return clonaHash(lista);
	}
	
	private static LinkedHashMap<String, String> getAmbitoTerritoriale() {
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_AMBITO_TERRITORIALE)) {
			lista = hash.get(LISTA_AMBITO_TERRITORIALE);
		} else {
			lista = new LinkedHashMap<String, String>();
			lista.put("1", "Operatore economico italiano");
			//lista.put("2", "Operatore economico UE");
			//lista.put("3", "Operatore economico extra UE");
			lista.put("2", "Operatore economico UE (non italiano) o extra UE");
			hash.put(LISTA_AMBITO_TERRITORIALE, lista);
		}

		return clonaHash(lista);
	}
	
	private static LinkedHashMap<String, String> getTipiAvviso()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_TIPI_AVVISO)) {
			lista = hash.get(LISTA_TIPI_AVVISO);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getElencoTipiAvviso();
			lista = InterceptorEncodedData.parseXml(xml);
			lista.put("0", "Elenco operatori economici");
			hash.put(LISTA_TIPI_AVVISO, lista);
		}

		return clonaHash(lista);
	}
	
	private static LinkedHashMap<String, String> getTipiAvvisoGenerali()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_TIPI_AVVISO_GENERALI)) {
			lista = hash.get(LISTA_TIPI_AVVISO_GENERALI);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSOperazioniGeneraliWrapper wrapper = (WSOperazioniGeneraliWrapper) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.WS_OPERAZIONI_GENERALI,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSOPGenerali().getListaTipiAvvisoGenerali();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_TIPI_AVVISO_GENERALI, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getStazioniAppaltanti()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_STAZIONI_APPALTANTI)) {
			lista = hash.get(LISTA_STAZIONI_APPALTANTI);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getElencoStazioniAppaltanti();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_STAZIONI_APPALTANTI, lista);
		}

		return clonaHash(lista);
	}
	
	private static LinkedHashMap<String, String> getStazioniAppaltantiL190()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;
		
		if (hash.containsKey(LISTA_STAZIONI_APPALTANTI_L190)) {
			lista = hash.get(LISTA_STAZIONI_APPALTANTI_L190);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getElencoStazioniAppaltantiL190();
			lista = InterceptorEncodedData.parseXml(xml);
			
			// NB: per la gestione della amministazione trasparente (L190)  
			// la lista delle "strutture proponenti" recuperata dal servizio 
			// che e' nel formato
			//
			//       ( "[Descrizione]", "[Codice Fiscale]" )
			//
			// nelle pagine jsp va utilizzato il formato
			//
			//       ( "[Descrizione]", "[Codice Fiscale] - [Descrizione]" )
			//
			// percio' si correggono il valori della lista qui
			// si usa la descrizione come chiave in quanto potremmo avere piu'
			// uffici (strutture proponenti) per lo stesso codice fiscale
			for (String key : lista.keySet()) {
				lista.put(key, lista.get(key) +  " - " + key);
			}

			hash.put(LISTA_STAZIONI_APPALTANTI_L190, lista);
		}
		
		return clonaHash(lista);
		}

	private static LinkedHashMap<String, String> getTipiAppalto()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_TIPI_APPALTO)) {
			lista = hash.get(LISTA_TIPI_APPALTO);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getElencoTipiAppalto();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_TIPI_APPALTO, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getTipiAppaltoIscrizioneAlbo()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_TIPI_APPALTO_ISCRIZIONE_ELENCO_OPERATORI)) {
			lista = hash.get(LISTA_TIPI_APPALTO_ISCRIZIONE_ELENCO_OPERATORI);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getElencoTipiAppaltoCategorie();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_TIPI_APPALTO_ISCRIZIONE_ELENCO_OPERATORI, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getTipiProcedura()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_TIPI_PROCEDURA)) {
			lista = hash.get(LISTA_TIPI_PROCEDURA);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getElencoTipiProcedura();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_TIPI_PROCEDURA, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getTipiAggiudicazione()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_TIPI_AGGIUDICAZIONE)) {
			lista = hash.get(LISTA_TIPI_AGGIUDICAZIONE);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getElencoTipiAggiudicazione();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_TIPI_AGGIUDICAZIONE, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getTipiElencoOperatori()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_TIPI_ELENCO_OPERATORI)) {
			lista = hash.get(LISTA_TIPI_ELENCO_OPERATORI);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getElencoTipiElencoOperatori();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_TIPI_ELENCO_OPERATORI, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getTipiClassifica()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_TIPI_CLASSIFICA)) {
			lista = hash.get(LISTA_TIPI_CLASSIFICA);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getElencoTipiClassifica();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_TIPI_CLASSIFICA, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getStatiGara()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_STATI_GARA)) {
			lista = hash.get(LISTA_STATI_GARA);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getElencoStatiGara();
			lista = InterceptorEncodedData.parseXml(xml);
			// rimuovi dalla lista lo stato "Sospesa" 
			// che non deve comparire nelle pagine di ricerca 
			lista.remove("4");
			hash.put(LISTA_STATI_GARA, lista);
		}

		return clonaHash(lista);
	}
	
	private static LinkedHashMap<String, String> getStatiDettaglioGara()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_STATI_DETTAGLIO_GARA)) {
			lista = hash.get(LISTA_STATI_DETTAGLIO_GARA);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getElencoStatiGara();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_STATI_DETTAGLIO_GARA, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getEsitiGara()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_ESITI_GARA)) {
			lista = hash.get(LISTA_ESITI_GARA);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
			.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
					ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getElencoEsitiGara();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_ESITI_GARA, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getStatiIscrizioneAlbo()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_STATI_ISCRIZIONE_ELENCO_OPERATORI)) {
			lista = hash.get(LISTA_STATI_ISCRIZIONE_ELENCO_OPERATORI);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							ServletActionContext.getRequest());

			String xml = wrapper.getProxyWSGare().getElencoStatiIscrizioneElencoOperatori();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_STATI_ISCRIZIONE_ELENCO_OPERATORI, lista);
		}

		return clonaHash(lista);
	}


	private static LinkedHashMap<String, String> getClassificazioneLavori()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_CLASSIFICAZIONE_LAVORI)) {
			lista = hash.get(LISTA_CLASSIFICAZIONE_LAVORI);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getElencoClassificazioniLavori();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_CLASSIFICAZIONE_LAVORI, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getClassificazioneLavoriIscrizioneAlbo()
			throws RemoteException, XmlException
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_CLASSIFICAZIONE_LAVORI_ISCRIZIONE_ELENCO_OPERATORI)) {
			lista = hash
					.get(LISTA_CLASSIFICAZIONE_LAVORI_ISCRIZIONE_ELENCO_OPERATORI);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getElencoClassificazioniLavoriPerIscrizione();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_CLASSIFICAZIONE_LAVORI_ISCRIZIONE_ELENCO_OPERATORI,
					lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getClassificazioneLavoriSottoSoglia()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_CLASSIFICAZIONE_LAVORI_SOTTO_SOGLIA)) {
			lista = hash.get(LISTA_CLASSIFICAZIONE_LAVORI_SOTTO_SOGLIA);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getElencoClassificazioniLavoriSottoSoglia();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_CLASSIFICAZIONE_LAVORI_SOTTO_SOGLIA, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getClassificazioneForniture()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_CLASSIFICAZIONE_FORNITURE)) {
			lista = hash.get(LISTA_CLASSIFICAZIONE_FORNITURE);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getElencoClassificazioniForniture();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_CLASSIFICAZIONE_FORNITURE, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getClassificazioneServizi()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_CLASSIFICAZIONE_SERVIZI)) {
			lista = hash.get(LISTA_CLASSIFICAZIONE_SERVIZI);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getElencoClassificazioniServizi();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_CLASSIFICAZIONE_SERVIZI, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getClassificazioneServiziProfessionali()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_CLASSIFICAZIONE_SERVIZI_PROFESSIONALI)) {
			lista = hash.get(LISTA_CLASSIFICAZIONE_SERVIZI_PROFESSIONALI);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getElencoClassificazioniServiziProfessionali();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_CLASSIFICAZIONE_SERVIZI_PROFESSIONALI, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getTipiUnitaMisuraTempiConsegna() 
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_TIPI_UNITA_MISURA_TEMPI_CONSEGNA)) {
			lista = hash.get(LISTA_TIPI_UNITA_MISURA_TEMPI_CONSEGNA);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getElencoUnitaMisuraTempoConsegna();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_TIPI_UNITA_MISURA_TEMPI_CONSEGNA, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getTipiUnitaMisura() 
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_TIPI_UNITA_MISURA)) {
			lista = hash.get(LISTA_TIPI_UNITA_MISURA);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getElencoUnitaMisura();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_TIPI_UNITA_MISURA, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getTipiPrezzoArticoloRiferitoA() 
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_TIPI_PREZZO_ARTICOLO_RIFERITO_A)) {
			lista = hash.get(LISTA_TIPI_PREZZO_ARTICOLO_RIFERITO_A);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getElencoTipiPrezzoArticoloRiferitoA();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_TIPI_PREZZO_ARTICOLO_RIFERITO_A, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getTipiArticolo() 
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_TIPI_ARTICOLO)) {
			lista = hash.get(LISTA_TIPI_ARTICOLO);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getElencoTipiArticolo();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_TIPI_ARTICOLO, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getStatiProdotto()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_STATI_PRODOTTO)) {
			lista = hash.get(LISTA_STATI_PRODOTTO);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getElencoStatiProdotto();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_STATI_PRODOTTO, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getCheckPartitaIVALiberoProfessionista()
			throws RemoteException, XmlException
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(CHECK_PARTITA_IVA_FACOLTATIVA_LIBERO_PROFESSIONISTA)) {
			lista = hash.get(CHECK_PARTITA_IVA_FACOLTATIVA_LIBERO_PROFESSIONISTA);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							ServletActionContext.getRequest());
			GetCfgCheckPIFacoltativaOutType check = wrapper.getProxyWSGare().getCfgCheckPIFacoltativaLiberoProfessionista();
			if (check.getErrore() != null)
				throw new RemoteException(check.getErrore());
			lista = new LinkedHashMap<String, String>();
			lista.put("1", check.getCheckFacoltativo() ? "1" : "0");
			hash.put(CHECK_PARTITA_IVA_FACOLTATIVA_LIBERO_PROFESSIONISTA, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getCheckPartitaIVAImpresaSociale()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(CHECK_PARTITA_IVA_FACOLTATIVA_IMPRESA_SOCIALE)) {
			lista = hash.get(CHECK_PARTITA_IVA_FACOLTATIVA_IMPRESA_SOCIALE);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							ServletActionContext.getRequest());
			GetCfgCheckPIFacoltativaOutType check = wrapper.getProxyWSGare()
					.getCfgCheckPIFacoltativaImpresaSociale();
			if (check.getErrore() != null)
				throw new RemoteException(check.getErrore());
			lista = new LinkedHashMap<String, String>();
			lista.put("1", check.getCheckFacoltativo() ? "1" : "0");
			hash.put(CHECK_PARTITA_IVA_FACOLTATIVA_IMPRESA_SOCIALE, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getCheckGruppoIVA()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(CHECK_GRUPPO_IVA)) {
			lista = hash.get(CHECK_GRUPPO_IVA);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							ServletActionContext.getRequest());
			GetCfgCheckParametroOutType check = wrapper.getProxyWSGare()
					.getCfgCheckGruppoIvaAbilitato();
			if (check.getErrore() != null)
				throw new RemoteException(check.getErrore());
			lista = new LinkedHashMap<String, String>();
			lista.put("1", check.getCheckAbilitato() ? "1" : "0");
			hash.put(CHECK_GRUPPO_IVA, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getListeTipologieAssistenza()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;
		if (hash.containsKey(LISTA_TIPOLOGIE_ASSISTENZA)) {
			lista = hash.get(LISTA_TIPOLOGIE_ASSISTENZA);
		} else {
			IAppParamManager paramManager = (IAppParamManager) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.APP_PARAM_MANAGER,
							ServletActionContext.getRequest());
			lista = new LinkedHashMap<String, String>();
			String parametro = (String) paramManager.getConfigurationValue(AppParamManager.TIPOLOGIE_ASSISTENZA);
			if (parametro != null) {
				String[] parametri = StringUtils.split(parametro, ";");
				for (int i = 0; i < parametri.length; i++) {
					lista.put(StringUtils.substringBefore(parametri[i], "-"), StringUtils.substringAfter(parametri[i], "-"));
				}				
				hash.put(LISTA_TIPOLOGIE_ASSISTENZA, lista);
			}
		}
		return clonaHash(lista);
	}
		
	private static LinkedHashMap<String, String> getTerminiAsta() 
			throws RemoteException, XmlException 
	{	
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_TIPI_TERMINE_ASTA)) {
			lista = hash.get(LISTA_TIPI_TERMINE_ASTA);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSAsteWrapper wrapper = (WSAsteWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_ASTE,
							ServletActionContext.getRequest());
			lista = new LinkedHashMap<String, String>();
			if(wrapper.getProxyWSAste().getElencoTerminiAsta().getElenco() != null) {
				for (GetElencoTerminiAstaOutTypeElencoEntry item : wrapper.getProxyWSAste().getElencoTerminiAsta().getElenco()) {
					lista.put(item.getKey(), item.getValue());
				}
			}
			hash.put(LISTA_TIPI_TERMINE_ASTA, lista);
		}

		return clonaHash(lista);
	}
		
	private static LinkedHashMap<String, String> getTipiClassificaAsta() 
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_TIPI_CLASSIFICA_ASTA)) {
			lista = hash.get(LISTA_TIPI_CLASSIFICA_ASTA);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSAsteWrapper wrapper = (WSAsteWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_ASTE,
							ServletActionContext.getRequest());
			lista = new LinkedHashMap<String, String>();
			if(wrapper.getProxyWSAste().getElencoTipiClassifica().getElenco() != null) {
				for (GetElencoTipiClassificaOutTypeElencoEntry item : wrapper.getProxyWSAste().getElencoTipiClassifica().getElenco()) {
					lista.put(item.getKey(), item.getValue());
				}
			}
			hash.put(LISTA_TIPI_CLASSIFICA_ASTA, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getElencoSezioniIscrizioneWhitelistAntimafia()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;
		
		if (hash.containsKey(LISTA_SEZIONI_WHITELIST_ANTIMAFIA)) {
			lista = hash.get(LISTA_SEZIONI_WHITELIST_ANTIMAFIA);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getElencoSezioniIscrizioneWhitelistAntimafia();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_SEZIONI_WHITELIST_ANTIMAFIA, lista);
		}
		
		return clonaHash(lista);
	}
	
	private static LinkedHashMap<String, String> getElencoRatingLegale()
		throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_RATING_LEGALE)) {
			lista = hash.get(LISTA_RATING_LEGALE);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
				.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
						 ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getElencoRatingLegale();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_RATING_LEGALE, lista);
		}

		return clonaHash(lista);
	}
		
	private static LinkedHashMap<String, String> getClassiDimensioneDipendente()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;
		
		if (hash.containsKey(LISTA_CLASSI_DIMENSIONE)) {
			lista = hash.get(LISTA_CLASSI_DIMENSIONE);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getElencoClassificazioneDimensioniDipendente();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_CLASSI_DIMENSIONE, lista);
		}
		
		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getSettoriAttivitaEconomica()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;
		
		if (hash.containsKey(LISTA_SETTORE_ATTIVITA_ECONOMICA)) {
			lista = hash.get(LISTA_SETTORE_ATTIVITA_ECONOMICA);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getElencoSettoriAttivitaEconomica();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_SETTORE_ATTIVITA_ECONOMICA, lista);
		}
		
		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getElencoAltriSoggetti() 
		throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_TIPO_ALTRI_SOGGETTI)) {
			lista = hash.get(LISTA_TIPO_ALTRI_SOGGETTI);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
				.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
					ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getElencoAltriSoggetti();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_TIPO_ALTRI_SOGGETTI, lista);
		}

		return clonaHash(lista);
	}
	
	private static LinkedHashMap<String, String> getElencoStatiOrdiniNSO() 
		throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;
	
		if (hash.containsKey(LISTA_STATI_ORDINI_NSO)) {
			lista = hash.get(LISTA_STATI_ORDINI_NSO);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
//			WSOrdiniNSOWrapper wrapper = (WSOrdiniNSOWrapper) ApsWebApplicationUtils
//				.getBean(PortGareSystemConstants.WS_ORDINI_NSO,
//					ServletActionContext.getRequest());
			
			//String xml = wrapper.getNso().getElencoStatiOrdine();
			//lista = InterceptorEncodedData.parseXml(xml);
			lista = new LinkedHashMap<String, String>();
//			lista.put("1", "inviato nso da BO");
			lista.put("2", "in attesa conferma fornitore");
			lista.put("3", "inviata conferma nso da FO");
			lista.put("4", "inviato rifiuto nso da FO");
//			lista.put("5", "inviata modifica nso da FO");
			lista.put("6", "accettato");
			lista.put("7", "accettato automaticamente");
			lista.put("8", "rifiutato");
//			lista.put("9", "accettato con modifiche");
			lista.put("10", "inviata revoca da BO");
			lista.put("11", "revocato");
//			lista.put("12", "errore NSO");
			
			hash.put(LISTA_STATI_ORDINI_NSO, lista);
		}
	
		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getTipiRaggruppamento() 
		throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;
	
		if (hash.containsKey(LISTA_TIPI_RAGGRUPPAMENTO)) {
			lista = hash.get(LISTA_TIPI_RAGGRUPPAMENTO);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
				.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
					ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getTipiRaggruppamento(); 
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_TIPI_RAGGRUPPAMENTO, lista);
		}
	
		return clonaHash(lista);
	}
	
	private static LinkedHashMap<String, String> getTipiRegimeFiscale() 
		throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;
	
		if (hash.containsKey(LISTA_TIPI_REGIME_FISCALE)) {
			lista = hash.get(LISTA_TIPI_REGIME_FISCALE);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
				.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
					ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getTipiRegimeFiscale();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_TIPI_REGIME_FISCALE, lista);
		}
	
		return clonaHash(lista);
	}
	
	private static LinkedHashMap<String, String> getElencoEntitaComunicazione() 
		throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;
		if (hash.containsKey(LISTA_TIPOLOGIE_COMUNICAZIONI)) {
			lista = hash.get(LISTA_TIPOLOGIE_COMUNICAZIONI);
		} else {
			IAppParamManager appParamManager = (IAppParamManager) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.APP_PARAM_MANAGER,
							 ServletActionContext.getRequest());
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			//BaseAction action = (BaseAction) this.actionInvocation;
			BaseAction action = (BaseAction) ActionContext.getContext().getActionInvocation().getAction();
			lista = new LinkedHashMap<String, String>();
			String parametro = (String) appParamManager.getConfigurationValue(AppParamManager.TIPOLOGIE_COMUNICAZIONI);
			if (parametro != null) {
				String[] parametri = StringUtils.split(parametro, ";");
				for (int i = 0; i < parametri.length; i++) {
					lista.put(StringUtils.substringBefore(parametri[i], "-"), StringUtils.substringAfter(parametri[i], "-"));
				}
				hash.put(LISTA_TIPOLOGIE_COMUNICAZIONI, lista);
			}
		}
		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getTipologiaProdotto() 
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;
		if (hash.containsKey(LISTA_TIPOLIGIA_PRODOTTO)) {
			lista = hash.get(LISTA_TIPOLIGIA_PRODOTTO);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
				.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
					ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getTipologiaProdotto();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_TIPOLIGIA_PRODOTTO, lista);
		}
		return clonaHash(lista);
	}
	
	private static LinkedHashMap<String, String> getTipiAmmissioneProcedura() 
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;
		if (hash.containsKey(LISTA_TIPO_AMMISSIONE_PROCEDURA)) {
			lista = hash.get(LISTA_TIPO_AMMISSIONE_PROCEDURA);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							 ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getTipiAmmissioneProcedura();
			lista = InterceptorEncodedData.parseXml(xml);
			hash.put(LISTA_TIPO_AMMISSIONE_PROCEDURA, lista);
		}
		return clonaHash(lista);
	}
	
	/**
	 * Ritorna la lista di societ� cooperative clonate.
	 *
	 * @return lista di societ� cooperative
	 */
	private static LinkedHashMap<String, String> getSocietaCooperative() {	//Throws RuntimeException (RemoteException, XmlException)
		//Il computeIfAbsent effettua la lambda passata come secondo parametro in caso la chiave richiesta non esista
		//La lambda come secondo parametro ritorner� il valore della nuova Entry che verr� aggiunta in automatico
		//corrisponde a: if(!map.contains(key)) map.put(key, calculateValue())
		//throwRuntimeOnThrows ritorna una RuntimeException (che non deve essere specificata in throws) in caso di eccezione

		return clonaHash(
			getHash().computeIfAbsent(LISTA_TIPOLOGIE_SOCIETA_COOPERATIVE
					, (key) -> throwRuntimeOnThrows(InterceptorEncodedData::computeSocietaCooperativa)
				)
		);
	}
	private static LinkedHashMap<String, String> getOrderCriteria()
			throws RemoteException, XmlException {
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_ORDER_CRITERIA)) {
			lista = hash.get(LISTA_ORDER_CRITERIA);
		} else {
			lista = new LinkedHashMap<String, String>();
			lista.put(ORDER_CRITERIA_DATA_SCADENZA_ASC, "Data scadenza crescente");
			lista.put(ORDER_CRITERIA_DATA_SCADENZA_DESC, "Data scadenza decrescente");
			lista.put(ORDER_CRITERIA_DATA_PUBBLICAZIONE_ASC, "Data pubblicazione crescente");
			lista.put(ORDER_CRITERIA_DATA_PUBBLICAZIONE_DESC, "Data pubblicazione decrescente");
			lista.put(ORDER_CRITERIA_IMPORTO_ASC, "Importo crescente");
			lista.put(ORDER_CRITERIA_IMPORTO_DESC, "Importo decrescente");
			hash.put(LISTA_ORDER_CRITERIA, lista);
		}
		return clonaHash(lista);
}

	/**
	 * Ritorna la lista di forme giuridiche che supportano le societ� cooperative.
	 *
	 * @return lista di societ� cooperative
	 */
	private static LinkedHashMap<String, String> getFormeGiuridicheCooperative() {	//Throws RuntimeException (RemoteException, XmlException)
		//Il computeIfAbsent effettua la lambda passata come secondo parametro in caso la chiave richiesta non esista
		//La lambda come secondo parametro ritorner� il valore della nuova Entry che verr� aggiunta in automatico
		//corrisponde a: if(!map.contains(key)) map.put(key, calculateValue())
		//throwRuntimeOnThrows ritorna una RuntimeException (che non deve essere specificata in throws) in caso di eccezione

		return clonaHash(
			getHash().computeIfAbsent(LISTA_FORME_GIURIDICHE_COOPERATIVE
					, (key) -> throwRuntimeOnThrows(InterceptorEncodedData::computeFormeGiuridicheCooperative)
				)
		);
	}

	/**
	 * Mi faccio ritornare dal WSAppalti l'xml con le forme giuridiche che supportano le societ� cooperative e lo
	 * trasformo in linkedhashmap
	 *
	 * @return
	 * @throws RemoteException
	 * @throws XmlException
	 */
	private static LinkedHashMap<String, String> computeFormeGiuridicheCooperative() throws RemoteException, XmlException {
		WSOperazioniGeneraliWrapper wrapper = (WSOperazioniGeneraliWrapper)
				WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext())
						.getBean(CommonSystemConstants.WS_OPERAZIONI_GENERALI);

		String xml = wrapper.getProxyWSOPGenerali().getFormeGiuridicheConSocietaCooperativa();
		return InterceptorEncodedData.parseXml(xml);
	}
	/**
	 * Mi faccio ritornare dal WSAppalti l'xml con le societ� cooperative e lo trasformo in linkedhashmap
	 *
	 * @return
	 * @throws RemoteException
	 * @throws XmlException
	 */
	private static LinkedHashMap<String, String> computeSocietaCooperativa() throws RemoteException, XmlException {
		WSOperazioniGeneraliWrapper wrapper = (WSOperazioniGeneraliWrapper)
				WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext())
						.getBean(CommonSystemConstants.WS_OPERAZIONI_GENERALI);
		String xml = wrapper.getProxyWSOPGenerali().getSocietaCooperativa();
		return InterceptorEncodedData.parseXml(xml);
	}

	/**
	 * Interfaccia rappresentante una lambda utilizzata dal throwRuntimeOnThrows
	 */
	@FunctionalInterface
	private interface ThrowingSupplier {
		LinkedHashMap<String, String> get() throws RemoteException, XmlException;
	}

	/**
	 * Metodo che lancia una RuntimeException in caso fosse catchata una Exception dalla lambda richiamata.
	 * In caso non ci siano errori ritorna il valore ritornato dalla lambda (la linkedhashmap)
	 *
	 * @param tr
	 * @return
	 */
	private static LinkedHashMap<String, String> throwRuntimeOnThrows(ThrowingSupplier tr) {
		try {
			return tr.get();
		} catch (RemoteException | XmlException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * elenco dei tipi di avvalimento (tabellato A1123)
	 */
	private static LinkedHashMap<String, String> getTipiAvvalimento() throws RemoteException, XmlException {
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_TIPI_AVVALIMENTO)) {
			lista = hash.get(LISTA_TIPI_AVVALIMENTO);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							 ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getTipiAvvalimento();
			lista = InterceptorEncodedData.parseXml(xml);
			logger.debug("Loaded LISTA_TIPI_AVVALIMENTO {}",lista.size());
			hash.put(LISTA_TIPI_AVVALIMENTO, lista);
		}

		return clonaHash(lista);
	}

	/**
	 * elenco dei tipi impresa e natura giuridica (tabellato A1z28)
	 */
	private static LinkedHashMap<String, String> getTipiImpresaNaturaGiuridica() throws RemoteException, XmlException {
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_TIPI_IMPRESA_NATURA_GIURIDICA)) {
			lista = hash.get(LISTA_TIPI_IMPRESA_NATURA_GIURIDICA);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
							 ServletActionContext.getRequest());
			String xml = wrapper.getProxyWSGare().getTipiImpresaNaturaGiuridica();
			//lista = InterceptorEncodedData.parseXml(xml);
			
			// PORTAPPALT-1049 SACE 
			// NB: nella lista ci sono 2 occorrenze di PF (persona fisica (6,10), persona fisica estero (6,16))
			lista = new LinkedHashMap<String, String>();
			ListaDatiCodificatiDocument document = ListaDatiCodificatiDocument.Factory.parse(xml);
			ListaDatiCodificatiType dati = document.getListaDatiCodificati();
			for (int i = 0; i < dati.sizeOfElementoArray(); i++) {
				DatoCodificatoType datoCodificato = dati.getElementoArray(i);
				// sostituisco PF per la persona fisica estero con PFE
				String codice = datoCodificato.getCodice();
				if("PF".equalsIgnoreCase(codice) && "6,16".equalsIgnoreCase(datoCodificato.getDescrizione())) {
					codice = "PFE";
				} 
				lista.put(codice, datoCodificato.getDescrizione());
			}
			
			logger.debug("Loaded LISTA_TIPI_IMPRESA_NATURA_GIURIDICA {}",lista.size());
			hash.put(LISTA_TIPI_IMPRESA_NATURA_GIURIDICA, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getStatiAvviso()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_STATI_AVVISO)) {
			lista = hash.get(LISTA_STATI_AVVISO);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
//			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
//					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
//							ServletActionContext.getRequest());
//			String xml = wrapper.getProxyWSGare().getElencoStatiGara();
//			lista = InterceptorEncodedData.parseXml(xml);
			lista = new LinkedHashMap<String, String>();
			lista.put("1", "In corso"); 
			lista.put("2", "Scaduto");
			hash.put(LISTA_STATI_AVVISO, lista);
		}

		return clonaHash(lista);
	}

	private static LinkedHashMap<String, String> getTipiBusta()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_TIPI_BUSTA)) {
			lista = hash.get(LISTA_TIPI_BUSTA);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
//			WSGareAppaltoWrapper wrapper = (WSGareAppaltoWrapper) ApsWebApplicationUtils
//					.getBean(PortGareSystemConstants.WS_GARE_APPALTO,
//							ServletActionContext.getRequest());
//			String xml = wrapper.getProxyWSGare().getElencoStatiGara();
//			lista = InterceptorEncodedData.parseXml(xml);
			lista = new LinkedHashMap<String, String>();
			lista.put("2", "Busta tecnica"); 
			lista.put("3", "Busta economica");
			hash.put(LISTA_TIPI_BUSTA, lista);
		}

		return clonaHash(lista);
	}

}
