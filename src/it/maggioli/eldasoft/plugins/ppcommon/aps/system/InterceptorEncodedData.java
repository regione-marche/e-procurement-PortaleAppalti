package it.maggioli.eldasoft.plugins.ppcommon.aps.system;

import it.eldasoft.www.sil.WSAste.GetElencoTerminiAstaOutTypeElencoEntry;
import it.eldasoft.www.sil.WSAste.GetElencoTipiClassificaOutTypeElencoEntry;
import it.eldasoft.www.sil.WSGareAppalto.GetCfgCheckPIFacoltativaOutType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.IEncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.codifiche.ICodificheManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.WSOperazioniGeneraliWrapper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.aste.WSAsteWrapper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.WSGareAppaltoWrapper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.eorders.WSOrdiniNSOWrapper;
import it.maggioli.eldasoft.wsOperazioniGenerali.daticomuni.DatoCodificatoType;
import it.maggioli.eldasoft.wsOperazioniGenerali.daticomuni.ListaDatiCodificatiDocument;
import it.maggioli.eldasoft.wsOperazioniGenerali.daticomuni.ListaDatiCodificatiType;

import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.xmlbeans.XmlException;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * Interceptor generico per la gestione dei dati in formato tabellato (codice,
 * descrizione)
 * 
 * @author Stefano.Sabbadin
 */
public class InterceptorEncodedData extends AbstractInterceptor {

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
	public static final String LISTA_TIPI_COMUNICAZIONE = "tipiComunicazione";
	public static final String LISTA_STATI_COMUNICAZIONE = "statiComunicazione";
	public static final String LISTA_TIPI_RAGGRUPPAMENTO = "tipiRaggruppamento";

	// tabellati fissi da portale
	public static final String LISTA_TIPI_SOGGETTO = "tipiSoggetto";
	public static final String LISTA_SESSI = "sesso";
	public static final String LISTA_SINO = "sino";
	public static final String LISTA_AMBITO_TERRITORIALE = "ambitoTerritoriale";

	// tabellati "ibridi"
	//public static final String LISTA_TIPI_SOGGETTO_QUALIFICA = "tipiSoggettoQualifica";
	public static final String LISTA_TIPI_AVVISO = "tipiAvviso";

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
	
	public static final String LISTA_SOGGETTI_TIPI_QUALIFICA = "SoggettoImpresa.tipiSoggettoQualifica";
	public static final String LISTA_SOGGETTI_TIPI_SOGGETTO = "SoggettoImpresa.tipiSoggetto";

	// tabellati di configurazione web service gare
	public static final String CHECK_PARTITA_IVA_FACOLTATIVA_LIBERO_PROFESSIONISTA = "checkPILiberoProf";
	public static final String CHECK_PARTITA_IVA_FACOLTATIVA_IMPRESA_SOCIALE = "checkPIImprSociale";

	//tabellati da ppcommon_properties
	public static final String LISTA_TIPOLOGIE_ASSISTENZA = "tipologieAssistenza";

    // tabellati web service aste
    public static final String LISTA_TIPI_TERMINE_ASTA = "terminiAsta";
    public static final String LISTA_TIPI_CLASSIFICA_ASTA = "tipiClassificaAsta";

    // tabellati web service NSO
    public static final String LISTA_STATI_ORDINI_NSO = "statiOrdineNso";
    
	/**
	 * UID
	 */
	private static final long serialVersionUID = 4981865790431039966L;

	private String types;

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
				// split per l'ottenimento dell'elenco di tabellati di
				// richiedere mediante servizio
				String[] typeArray = this.types.split(",");
				String type = null;
				LinkedHashMap<String, String> lista = null;
				// per ogni codifica viene delegata l'attivita' di lettura dei
				// valori all'opportuno metodo di gestione e quindi si valorizza
				// la hash dell'azione invocante con il tabellato
				for (int i = 0; i < typeArray.length; i++) {
					type = typeArray[i].trim();
					lista = getList(type);
					
					((IEncodedDataAction) actionInvocation.getAction())
						.getMaps().put(type, lista);
				}
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
	 * inizializza le liste di decodifica dei tabellati 
	 * @throws XmlException 
	 * @throws RemoteException 
	 */
	public LinkedHashMap<String, String> getList(String type) throws RemoteException, XmlException {
		LinkedHashMap<String, String> lista = null;
		
		if (LISTA_PROVINCE.equals(type)) {
			lista = this.getProvince();
		} else if (LISTA_TIPI_NATURA_GIURIDICA.equals(type)) {
			lista = this.getTipiNaturaGiuridica();
		} else if (LISTA_TIPI_IMPRESA.equals(type)) {
			lista = this.getTipiImpresa();
		} else if (LISTA_TIPI_IMPRESA_PER_ISCRIZ_ALBO.equals(type)) {
			lista = this.getTipiImpresaPerIscrizioneAlbo();
		} else if (LISTA_TIPI_IMPRESA_DITTA_INDIVIDUALE.equals(type)) {
			lista = this.getTipiImpresaDittaIndividuale();
		} else if (LISTA_TIPI_IMPRESA_LIBERO_PROFESSIONISTA.equals(type)) {
			lista = this.getTipiImpresaLiberoProfessionista();
		}else if (LISTA_TIPI_IMPRESA_SOCIALE.equals(type)) {
			lista = this.getTipiImpresaSociale();
		} else if (LISTA_TIPI_IMPRESA_CONSORZIO.equals(type)) {
			lista = this.getTipiImpresaConsorzio();
		} else if (LISTA_MODALITA_COMUNICAZIONE.equals(type)) {
			lista = this.getModalitaComunicazione();
		} else if (LISTA_CERTIFICATORI_SOA.equals(type)) {
			lista = this.getCertificatoriSOA();
		} else if (LISTA_TIPI_CERTIFICAZIONE_ISO.equals(type)) {
			lista = this.getTipiCertificazioneISO();
		} else if (LISTA_CERTIFICATORI_ISO.equals(type)) {
			lista = this.getCertificatoriISO();
		} else if (LISTA_GRADI_ABILITAZIONE_PREVENTIVA.equals(type)) {
			lista = this.getGradiAbilitazionePreventiva();
		} else if (LISTA_TIPI_INDIRIZZO.equals(type)) {
			lista = this.getTipiIndirizzo();
		} else if (LISTA_TIPI_ALTRA_CARICA.equals(type)) {
			lista = this.getTipiAltraCarica();
		} else if (LISTA_TIPI_COLLABORAZIONE.equals(type)) {
			lista = this.getTipiCollaborazione();
		} else if (LISTA_TIPI_TITOLO_TECNICO.equals(type)) {
			lista = this.getTipiTitoloTecnico();
		} else if (LISTA_TIPI_ALBO_PROFESSIONALE.equals(type)) {
			lista = this.getTipiAlboProfessionale();
		} else if (LISTA_TIPI_CASSA_PREVIDENZA.equals(type)) {
			lista = this.getTipiCassaPrevidenza();
		} else if (LISTA_ALIQUOTE_IVA.equals(type)) {
			lista = this.getAliquoteIVA();
		} else if (LISTA_SETTORI_PRODUTTIVI.equals(type)) {
			lista = this.getSettoriProduttivi();
		} else if (LISTA_NAZIONI.equals(type)) {
			lista = this.getNazioni();
		} else if (LISTA_TIPI_COMUNICAZIONE.equals(type)) {
			lista = this.getTipiComunicazione();
		} else if (LISTA_STATI_COMUNICAZIONE.equals(type)) {
			lista = this.getStatiComunicazione();
		} else if (LISTA_TIPI_SOGGETTO.equals(type)) {
			lista = this.getTipiSoggetto();
		} else if (LISTA_SOGGETTI_TIPI_SOGGETTO.equals(type)) {
			// tabellato creato runtime!!!
			// utilizzato per la codifica/decodifica del campo "ISoggettoImpresa.TipoSoggetto"
			lista = this.getSoggettoImpresaTipiSoggetto();
		} else if (LISTA_SOGGETTI_TIPI_QUALIFICA.equals(type)) {
			// tabellato creato runtime!!!
			// utilizzato per la codifica/decodifica del campo "ISoggettoImpresa.SoggettoQualifica"
			lista = this.getSoggettoImpresaTipiSoggettoQualifica();
		} else if (LISTA_SESSI.equals(type)) {
			lista = this.getSessi();
		} else if (LISTA_SINO.equals(type)) {
			lista = this.getSiNo();
		} else if (LISTA_TIPI_AVVISO.equals(type)) {
			lista = this.getTipiAvviso();
		} else if (LISTA_STAZIONI_APPALTANTI.equals(type)) {
			lista = this.getStazioniAppaltanti();
		} else if (LISTA_STAZIONI_APPALTANTI_L190.equals(type)) {
			lista = this.getStazioniAppaltantiL190();
		} else if (LISTA_TIPI_APPALTO.equals(type)) {
			lista = this.getTipiAppalto();
		} else if (LISTA_TIPI_APPALTO_ISCRIZIONE_ELENCO_OPERATORI.equals(type)) {
			lista = this.getTipiAppaltoIscrizioneAlbo();
		} else if (LISTA_TIPI_PROCEDURA.equals(type)) {
			lista = this.getTipiProcedura();
		} else if (LISTA_TIPI_AGGIUDICAZIONE.equals(type)) {
			lista = this.getTipiAggiudicazione();
		} else if (LISTA_TIPI_ELENCO_OPERATORI.equals(type)) {
			lista = this.getTipiElencoOperatori();
		} else if (LISTA_TIPI_CLASSIFICA.equals(type)) {
			lista = this.getTipiClassifica();
		} else if (LISTA_STATI_GARA.equals(type)) {
			lista = this.getStatiGara();
		} else if (LISTA_ESITI_GARA.equals(type)) {
			lista = this.getEsitiGara();
		} else if (LISTA_STATI_ISCRIZIONE_ELENCO_OPERATORI.equals(type)) {
			lista = this.getStatiIscrizioneAlbo();
		} else if (LISTA_CLASSIFICAZIONE_LAVORI.equals(type)) {
			lista = this.getClassificazioneLavori();
		} else if (LISTA_CLASSIFICAZIONE_LAVORI_ISCRIZIONE_ELENCO_OPERATORI.equals(type)) {
			lista = this.getClassificazioneLavoriIscrizioneAlbo();
		} else if (LISTA_CLASSIFICAZIONE_LAVORI_SOTTO_SOGLIA.equals(type)) {
			lista = this.getClassificazioneLavoriSottoSoglia();
		} else if (LISTA_CLASSIFICAZIONE_FORNITURE.equals(type)) {
			lista = this.getClassificazioneForniture();
		} else if (LISTA_CLASSIFICAZIONE_SERVIZI.equals(type)) {
			lista = this.getClassificazioneServizi();
		} else if (LISTA_CLASSIFICAZIONE_SERVIZI_PROFESSIONALI.equals(type)) {
			lista = this.getClassificazioneServiziProfessionali();
		} else if (LISTA_TIPI_ARTICOLO.equals(type)) {
			lista = this.getTipiArticolo();
		} else if (LISTA_TIPI_PREZZO_ARTICOLO_RIFERITO_A.equals(type)) {
			lista = this.getTipiPrezzoArticoloRiferitoA();
		} else if (LISTA_TIPI_UNITA_MISURA.equals(type)) {
			lista = this.getTipiUnitaMisura();
		} else if (LISTA_TIPI_UNITA_MISURA_TEMPI_CONSEGNA.equals(type)) {
			lista = this.getTipiUnitaMisuraTempiConsegna();
		} else if (LISTA_STATI_PRODOTTO.equals(type)) {
			lista = this.getStatiProdotto();
		} else if (CHECK_PARTITA_IVA_FACOLTATIVA_LIBERO_PROFESSIONISTA.equals(type)) {
			lista = this.getCheckPartitaIVALiberoProfessionista();
		} else if (CHECK_PARTITA_IVA_FACOLTATIVA_IMPRESA_SOCIALE.equals(type)) {
			lista = this.getCheckPartitaIVAImpresaSociale();
		} else if (LISTA_TIPOLOGIE_ASSISTENZA.equals(type)) {
			lista = this.getListeTipologieAssistenza();
		} else if (LISTA_TIPI_TERMINE_ASTA.equals(type)) {
			lista = this.getTerminiAsta();
		} else if (LISTA_TIPI_CLASSIFICA_ASTA.equals(type)) {
			lista = this.getTipiClassificaAsta();
		} else if (LISTA_SEZIONI_WHITELIST_ANTIMAFIA.equals(type)) {
			lista = this.getElencoSezioniIscrizioneWhitelistAntimafia();
		} else if (LISTA_RATING_LEGALE.equals(type)) {
			lista = this.getElencoRatingLegale();
		} else if (LISTA_CLASSI_DIMENSIONE.equals(type)) {
			lista = this.getClassiDimensioneDipendente();
		} else if (LISTA_SETTORE_ATTIVITA_ECONOMICA.equals(type)) {
			lista = this.getSettoriAttivitaEconomica();
		} else if (LISTA_TIPO_ALTRI_SOGGETTI.equals(type)) {
			lista = this.getElencoAltriSoggetti();
		} else if (LISTA_STATI_ORDINI_NSO.equals(type)) {
			lista = this.getElencoStatiOrdiniNSO();
		} else if (LISTA_AMBITO_TERRITORIALE.equals(type)) {
			lista = this.getAmbitoTerritoriale();
		} else if (LISTA_TIPI_RAGGRUPPAMENTO.equals(type)) {
			lista = this.getTipiRaggruppamento();
		} else if (LISTA_TIPI_REGIME_FISCALE.equals(type)) {
			lista = this.getTipiRegimeFiscale();
		} else {
			// eccezione che si puo' verificare solo in fase di sviluppo
			throw new RuntimeException(
					"il tipo '" + type + "' definito nell'interceptor non corrisponde ad alcun dato tipizzato attualmente gestito");
		}		
		return lista;
	}	

	/**
	 * Ritorna il bean cache delle codifiche ricevute mediante i servizi
	 * 
	 * @return cache delle codifiche
	 */
	private Hashtable<String, LinkedHashMap<String, String>> getHash() {
		ICodificheManager manager = (ICodificheManager) ApsWebApplicationUtils
				.getBean(CommonSystemConstants.CACHE_CODIFICHE,
						ServletActionContext.getRequest());
		return manager.getCodifiche();
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
	private LinkedHashMap<String, String> clonaHash(
			LinkedHashMap<String, String> lista) {
		LinkedHashMap<String, String> risultato = new LinkedHashMap<String, String>();
		if (lista != null)
			risultato = (LinkedHashMap<String, String>) lista.clone();
		return risultato;
	}

	//********************************************************************************
	//********************************************************************************
	//********************************************************************************
	
	private LinkedHashMap<String, String> getProvince() 
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getTipiNaturaGiuridica()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getTipiImpresa()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getTipiImpresaPerIscrizioneAlbo()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getTipiImpresaDittaIndividuale()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getTipiImpresaLiberoProfessionista()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}
	
	private LinkedHashMap<String, String> getTipiImpresaSociale()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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
		
		return this.clonaHash(lista);
	}
	
	private LinkedHashMap<String, String> getTipiImpresaConsorzio()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getModalitaComunicazione()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getCertificatoriSOA()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getTipiCertificazioneISO()
			throws RemoteException, XmlException 		
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getCertificatoriISO()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getGradiAbilitazionePreventiva()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getTipiIndirizzo()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getTipiAltraCarica()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getTipiCollaborazione()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getTipiTitoloTecnico()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getTipiAlboProfessionale()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getTipiCassaPrevidenza()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getAliquoteIVA()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getSettoriProduttivi()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	/**
	 * Attenzione: la lista nazioni viene ritornata senza codice ma come coppie (descrizione, descrizione).
	 * @return mappa con coppie (descrizione, descrizione)
	 * @throws RemoteException
	 * @throws XmlException
	 */
	private LinkedHashMap<String, String> getNazioni() 
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getTipiComunicazione() 
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getStatiComunicazione()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}
	
	private LinkedHashMap<String, String> getSoggettoImpresaTipiSoggettoQualifica() 
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
		LinkedHashMap<String, String> lista = null;

		if (hash.containsKey(LISTA_SOGGETTI_TIPI_QUALIFICA)) {
			lista = hash.get(LISTA_SOGGETTI_TIPI_QUALIFICA);
		} else {
			lista = this.getList(InterceptorEncodedData.LISTA_TIPI_SOGGETTO);
			LinkedHashMap<String, String> lst = this.getList(InterceptorEncodedData.LISTA_TIPI_ALTRA_CARICA);
			if(lst != null) {
				lista.putAll(lst);
			}
			lst = this.getList(InterceptorEncodedData.LISTA_TIPI_COLLABORAZIONE);
			if(lst != null) {
				lista.putAll(lst);
			}
			
			hash.put(LISTA_SOGGETTI_TIPI_QUALIFICA, lista);
		}
		
		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getSoggettoImpresaTipiSoggetto() {
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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
		
		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getTipiSoggetto() {
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getSessi() {
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getSiNo() {
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}
	
	private LinkedHashMap<String, String> getAmbitoTerritoriale() {
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}
	
	private LinkedHashMap<String, String> getTipiAvviso()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getStazioniAppaltanti()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}
	
	private LinkedHashMap<String, String> getStazioniAppaltantiL190()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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
		
		return this.clonaHash(lista);
		}

	private LinkedHashMap<String, String> getTipiAppalto()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getTipiAppaltoIscrizioneAlbo()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getTipiProcedura()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getTipiAggiudicazione()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getTipiElencoOperatori()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getTipiClassifica()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getStatiGara()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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
			hash.put(LISTA_STATI_GARA, lista);
		}

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getEsitiGara()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getStatiIscrizioneAlbo()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}


	private LinkedHashMap<String, String> getClassificazioneLavori()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getClassificazioneLavoriIscrizioneAlbo()
			throws RemoteException, XmlException 			
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getClassificazioneLavoriSottoSoglia()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getClassificazioneForniture()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getClassificazioneServizi()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getClassificazioneServiziProfessionali()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getTipiUnitaMisuraTempiConsegna() 
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getTipiUnitaMisura() 
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getTipiPrezzoArticoloRiferitoA() 
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getTipiArticolo() 
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getStatiProdotto()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getCheckPartitaIVALiberoProfessionista()
			throws RemoteException, XmlException
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getCheckPartitaIVAImpresaSociale()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getListeTipologieAssistenza()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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
		return this.clonaHash(lista);
	}
		
	private LinkedHashMap<String, String> getTerminiAsta() 
			throws RemoteException, XmlException 
	{	
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}
		
	private LinkedHashMap<String, String> getTipiClassificaAsta() 
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getElencoSezioniIscrizioneWhitelistAntimafia()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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
		
		return this.clonaHash(lista);
	}
	
	private LinkedHashMap<String, String> getElencoRatingLegale()
		throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}
		
	private LinkedHashMap<String, String> getClassiDimensioneDipendente()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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
		
		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getSettoriAttivitaEconomica()
			throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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
		
		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getElencoAltriSoggetti() 
		throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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

		return this.clonaHash(lista);
	}
	
	private LinkedHashMap<String, String> getElencoStatiOrdiniNSO() 
		throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
		LinkedHashMap<String, String> lista = null;
	
		if (hash.containsKey(LISTA_STATI_ORDINI_NSO)) {
			lista = hash.get(LISTA_STATI_ORDINI_NSO);
		} else {
			// non viene applicata alcuna sincronizzazione in quanto Hashtable
			// e' una classe sincronizzata, e i valori che si andrebbero a
			// scrivere comunque sono sempre i medesimi, al piu' potrebbero
			// esserci 2 scritture se il primo test ritorna false
			WSOrdiniNSOWrapper wrapper = (WSOrdiniNSOWrapper) ApsWebApplicationUtils
				.getBean(PortGareSystemConstants.WS_ORDINI_NSO,
					ServletActionContext.getRequest());
			
			//String xml = wrapper.getNso().getElencoStatiOrdine();
			//lista = InterceptorEncodedData.parseXml(xml);
			lista = new LinkedHashMap<String, String>();
			lista.put("1", "inviato nso da BO");
			lista.put("2", "in attesa conferma fornitore");
			lista.put("3", "inviata conferma nso da FO");
			lista.put("4", "inviato rifiuto nso da FO");
			lista.put("5", "inviata modifica nso da FO");
			lista.put("6", "accettato");
			lista.put("7", "accettato automaticamente");
			lista.put("8", "rifiutato");
			lista.put("9", "accettato con modifiche");
			
			hash.put(LISTA_STATI_ORDINI_NSO, lista);
		}
	
		return this.clonaHash(lista);
	}

	private LinkedHashMap<String, String> getTipiRaggruppamento() 
		throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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
	
		return this.clonaHash(lista);
	}
	
	private LinkedHashMap<String, String> getTipiRegimeFiscale() 
		throws RemoteException, XmlException 
	{
		Hashtable<String, LinkedHashMap<String, String>> hash = this.getHash();
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
	
		return this.clonaHash(lista);
	}

}
