package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import it.eldasoft.sil.portgare.datatypes.AggIscrizioneImpresaElencoOpType;
import it.eldasoft.sil.portgare.datatypes.AggiornamentoIscrizioneImpresaElencoOperatoriDocument;
import it.eldasoft.sil.portgare.datatypes.ImpresaType;
import it.eldasoft.sil.portgare.datatypes.IscrizioneImpresaElencoOpType;
import it.eldasoft.sil.portgare.datatypes.IscrizioneImpresaElencoOperatoriDocument;
import it.eldasoft.sil.portgare.datatypes.ListaCategorieIscrizioneType;
import it.eldasoft.sil.portgare.datatypes.ListaDocumentiType;
import it.eldasoft.sil.portgare.datatypes.OffertaEconomicaType;
import it.eldasoft.sil.portgare.datatypes.ReportOffertaEconomicaDocument;
import it.eldasoft.sil.portgare.datatypes.ReportOffertaEconomicaType;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.CategoriaBandoIscrizioneType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioBandoIscrizioneType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioStazioneAppaltanteType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.DocumentiAllegatiHelper;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.qcompiler.inc.QCQuestionario;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.INtpManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.GenPDFQuestionarioAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlObject;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Consente la generazione del PDF per la domanda di iscrizione.
 * 
 * NB: il pdf di iscrizione/aggiornamento e' una copia del Riepilogo QForm dell'offerta economica
 *     quindi per la generazione dell'xml/json si utilizza l'xml del documento "ReportOffertaEconomicaDocument"
 * 	   completando le informazioni comuni (SA, impresa) con i dati dell'iscrizione 
 *     ed integrando l'xml generato con i dati specifici degli elenchi (categorie selezionate)
 */
public class GenPDFQuestionarioIscrizioneAction extends GenPDFQuestionarioAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 2441502315970442714L;	

	protected IBandiManager bandiManager;
	protected IComunicazioniManager comunicazioniManager;
	protected INtpManager ntpManager;	

	protected Long idCom;	
	protected ComunicazioneType comunicazione;
	protected QCQuestionario questionario;
	@Validate(EParamValidation.CODICE)
	protected String codice;
	protected boolean isIscrizione;
	protected boolean isAggiornamentoIscrizione;
	protected boolean isAggiornamentoSoloDocumenti;
	protected boolean isRinnovoIscrizione;
	protected DettaglioBandoIscrizioneType bandoIscrizione;
	@Validate(EParamValidation.GENERIC)
	protected String tipoElenco;

	@Validate(EParamValidation.GENERIC)
	private String firmatarioSelezionato;
	@Validate(EParamValidation.SERIAL_NUMBER)
	private String serialNumberMarcaBollo;
	
	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}
	
	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}
	
	public void setNtpManager(INtpManager ntpManager) {
		this.ntpManager = ntpManager;
	}

	public Long getIdCom() {
		return idCom;
	}

	public void setIdCom(Long idCom) {
		this.idCom = idCom;
	}

	public String getFirmatarioSelezionato() {
		return firmatarioSelezionato;
	}

	public void setFirmatarioSelezionato(String firmatarioSelezionato) {
		this.firmatarioSelezionato = firmatarioSelezionato;
	}
	
	public String getSerialNumberMarcaBollo() {
		return serialNumberMarcaBollo;
	}

	public void setSerialNumberMarcaBollo(String serialNumberMarcaBollo) {
		this.serialNumberMarcaBollo = serialNumberMarcaBollo;
	}
	
	@Override
	public String getUrlErrori() {
		return this.urlPage + 
			   "?actionPath=" + "/ExtStr2/do/FrontEnd/IscrAlbo/openQC.action" + 
			   "&currentFrame=" + this.currentFrame;
	}

	/**
	 * costruttore del report 
	 */
	public GenPDFQuestionarioIscrizioneAction() {
		super("RiepilogoElencoQForm",		// nome del file della risorsa jasper 
			   "content");					// nodo radice del datasource json
	} 

	/**
	 * Genera il PDF dei dati anagrafici dell'impresa a partire dalla
	 * funzionalit&agrave; di visualizzazione o modifica dei dati anagrafici
	 * impresa.
	 * 
	 * @return forward dell'azione
	 */
	@Override
	public String createPdf() {
		// NB: in caso di report custom inserire qui l'inizializzazione di 
		// "reportName" e "xmlRootNode" relativi alla customizzazione...
		//this.reportName = "...";
		//this.xmlRootNode = "...";
		return super.createPdf();
	}

	/**
	 * prepara i parametri per il riutilizzo del metodo "aggiornaAllegato()"
	 */
	protected void getParametriFromComunicazione(ComunicazioneType comunicazione) {	    
	    this.codice = null;
	    this.isIscrizione = false;
	    this.isAggiornamentoIscrizione = false;
	    this.isAggiornamentoSoloDocumenti = false;
	    this.isRinnovoIscrizione = false;
	    
		if(comunicazione.getDettaglioComunicazione().getTipoComunicazione()
				.startsWith(PortGareSystemConstants.RICHIESTA_ISCRIZIONE_ALBO)) {
			this.isIscrizione = true;
		} else if(comunicazione.getDettaglioComunicazione().getTipoComunicazione()
				.startsWith(PortGareSystemConstants.RICHIESTA_AGGIORNAMENTO_ISCRIZIONE_ALBO)) {
			this.isAggiornamentoIscrizione = true;
		} 
		
		this.codice = comunicazione.getDettaglioComunicazione().getChiave2();
	}

	@Override
	protected boolean reportInit() {
		boolean continua = true;
		
		this.bandoIscrizione = null;
		this.questionario = null;
		try {
			// recupera la comunicazione e l'allegato "questionario.json"
			this.comunicazione = this.comunicazioniManager.getComunicazione(
					CommonSystemConstants.ID_APPLICATIVO, 
					this.idCom);
			
			this.getParametriFromComunicazione(comunicazione);
				
			WizardIscrizioneHelper helper = (WizardIscrizioneHelper) this.session
					.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
			WizardDocumentiHelper documenti = helper.getDocumenti();
			
			// recupera le info sul bando d'iscrizione...
			this.bandoIscrizione = this.bandiManager.getDettaglioBandoIscrizione(helper.getIdBando());

			// recupera il json del questionario...
			this.questionario = documenti.getQuestionarioAllegato(DocumentiAllegatiHelper.QUESTIONARIO_ELENCHI_FILENAME);
			
			if (this.questionario == null) {
				// la sessione e' scaduta, occorre riconnettersi
				session.put(ERROR_DOWNLOAD, this.getText("Errors.sessionExpired"));
				this.setTarget(INPUT);
				continua = false;
			} else {
				
//				// imposta la marca da bollo
//				helper.setSerialNumberMarcaBollo(this.serialNumberMarcaBollo);

				if(this.questionario != null) {
					// 4. GENERAZIONE PDF DI RIEPILOGO: ESTENSIONE DEL DATASOURCE JSON
					//Si estende il json in modo da prevedere oltre a:
					// - survey: i dati del questionario
					// - systemVariables: le variabili per il controllo del questionario
					// anche il nuovo attributo
					// - reportVariables: tracciato dei dati di testata da utilizzare per la sola stampa PDF
					// Da alcune nostre verifiche, in DB c'e' l'intero json del questionario, e non la sola componente survey. 
					// Pertanto se il datasource sara' l'intero json, lascerei le systemVariables esistenti senza replicarle 
					// tra le reportVariables.
					// Allego una proposta di JSON per la parte "reportVariables"; 
					// la parte dei firmatari come detto stamane implicherebbe una modifica importante della logica applicativa 
					// e pertanto viene per il momento omessa.
					this.generaJsonConDatiTipizzati();
				}
			}
		} catch(Throwable ex) {
			ApsSystemUtils.logThrowable(ex, this, "reportInit");
			session.put(ERROR_DOWNLOAD,
						"Si e' verificato un errore inaspettato durante l'elaborazione dell'operazione: "
						+ ex.getMessage());
			continua = false;
		}
		
		return continua;
	}
	
	@Override
	protected void reportParametersInit(HashMap<String, Object> params) throws Exception {
		WizardIscrizioneHelper helper = (WizardIscrizioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
		if (helper != null) {
			// il pdf di riepilogo degli elenchi e' un clone del riepilogo offerta QForm 
			// ed alcuni dati andranno comunque passati anche se non fanno parte del contesto 
			// (offerta di gara o iscrizione/aggiornamento elenco operatori)
						
			// sezione dell'offerta
			params.put("TIMESTAMP", UtilityDate.convertiData(this.ntpManager.getNtpDate(), 
				     UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
			params.put("IS_RTI", helper.isRti());
			params.put("DENOMINAZIONE_RTI", helper.getDenominazioneRTI());
			params.put("NOME_CLIENTE", (String) this.appParamManager.getConfigurationValue(AppParamManager.NOME_CLIENTE));
			params.put("PUNTIORDINANTIVISIBILI", this.customConfigManager.isVisible("GARE-DOCUMOFFERTA", "PUNTIORDIISTR"));
			//params.put("ITER_GARA", "");
			//params.put("TIPORIBASSO", "");
			//params.put("ACCORDOQUADRO", "");
			//params.put("IMPNORIBASSO", "");
			//params.put("IMPSICUREZZA", "");
			//params.put("SICINC", "");
			//params.put("IMPONERIPROGETT", "");

			// sezione dell'iscrizione/aggiornamento elenco
			params.put("RIEPILOGOELENCO", true);
			params.put("TIPOELENCO", this.tipoElenco);
			params.put("ISCRIZIONE", !helper.isAggiornamentoIscrizione() ? 1 : 0);
			params.put("VISUALIZZA_SA", (!helper.isUnicaStazioneAppaltante() && !helper.isAggiornamentoSoloDocumenti()) ? 1 : 0);
			params.put("VISUALIZZA_CAT", (!helper.isCategorieAssenti() && !helper.isAggiornamentoSoloDocumenti()) ? 1 : 0);
			params.put("EMAILRECAPITO", this.customConfigManager.isVisible("IMPRESA-DATIPRINC-RECAPITI", "MAIL") ? 1 : 0);
			params.put("DATANULLAOSTAANTIMAFIA", this.customConfigManager
						.isVisible("IMPRESA-DATIULT-CCIAA", "DATANULLAOSTAANTIMAFIA") ? 1 : 0);
			params.put("ABILITAZIONEPREVENTIVA", this.customConfigManager
						.isVisible("IMPRESA-DATIULT-SEZ", "ABILITAZIONEPREVENTIVA") ? 1 : 0);
			params.put("ZONEATTIVITA", this.customConfigManager
						.isVisible("IMPRESA-DATIULT", "ZONEATTIVITA") ? 1 : 0);
			params.put("ISCRELENCHIDL189", this.customConfigManager
						.isVisible("IMPRESA-DATIULT", "ISCRELENCHIDL189-2016") ? 1 : 0);
			params.put("RATINGLEGALITA", this.customConfigManager
						.isVisible("IMPRESA-DATIULT", "RATINGLEGALITA") ? 1 : 0);
			params.put("ELENCO", helper.getDescBando());
			params.put("LIBERO_PROFESSIONISTA", helper.getImpresa().isLiberoProfessionista());
			params.put("RTI", helper.isRti());
			params.put("CONSORZIO", helper.getImpresa().isConsorzio());
			params.put("IMPRESA_SINGOLA", (!helper.isRti() && !helper.getImpresa().isConsorzio()));
			params.put("MEPACONSIP", this.customConfigManager.isVisible("ISCRALBO", "MEPACONSIP"));
			
			// estrai i dati della Stazione Appaltante...
			DettaglioStazioneAppaltanteType stazione = null;
			if(helper.getStazioniAppaltanti() != null) {
				// se più di 1 quale prendere ?
				stazione = this.bandiManager
					.getDettaglioStazioneAppaltante(helper.getStazioniAppaltanti().first());
			} 	
			params.put("SA_DENOMINAZIONE", getContractingAuthorityValue(stazione));
			params.put("SA_CF", (stazione == null ? null : stazione.getCodiceFiscale()));
			params.put("SA_INDIRIZZO", (stazione == null ? null : stazione.getIndirizzo()));
			params.put("SA_LOCALITA", (stazione == null ? null : stazione.getComune()));
			params.put("SA_CAP", (stazione == null ? null : stazione.getCap()));
			params.put("SA_PROVINCIA", (stazione == null ? null : stazione.getProvincia()));
			params.put("SA_CIVICO", (stazione == null ? null : stazione.getNumCivico()));
			params.put("SA_MAIL", (stazione == null ? null : stazione.getEmail()));
			params.put("SA_PEC", (stazione == null ? null : stazione.getPec()));
			params.put("SA_WEB", (stazione == null ? null : ""));
			params.put("SOC_COOP_VISIBLE", hasToShowSocCoop(helper.getImpresa()));
		}
	}

	@Override
	protected void reportCompleted() {
		if (SUCCESS.equals(this.getTarget())) {
			WizardRinnovoHelper rinnovoHelper = (WizardRinnovoHelper) session
				.get(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);
			if (rinnovoHelper != null) {
				// aggiungi il pdf generato all'elenco dei file dell'helper
				//rinnovoHelper.getDocumenti().getPdfGenerati().add(pdf);
			}
		}
	}

	/**
	 * genera il documento JSON con i rendendo i campi chiave descrittivi
	 */
	/**
	 * genera il documento JSON con i rendendo i campi chiave descrittivi
	 */
	protected void generaJsonConDatiTipizzati() throws Exception {
		//////////////////////////////////////////////////////////////////
		// recupera i dati necessari per la creazione dell'XML...
		WizardIscrizioneHelper helper = (WizardIscrizioneHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
		WizardDocumentiHelper documenti = helper.getDocumenti();
		
		DettaglioStazioneAppaltanteType sa = null;
		if(this.bandoIscrizione != null && this.bandoIscrizione.getStazioneAppaltante() != null && 
		   StringUtils.isNotEmpty(this.bandoIscrizione.getStazioneAppaltante().getCodice())) { 
			sa = this.bandiManager.getDettaglioStazioneAppaltante(this.bandoIscrizione.getStazioneAppaltante().getCodice());
		}
		
		//////////////////////////////////////////////////////////////////
		// NB:
		// il PDF dell'iscrizione elenco e' un clone del PDF offerta
		// quindi si genera comunque l'XML di una busta economica 
		// e si "adattano" le infomrazioni al formato per la generazione  
		//////////////////////////////////////////////////////////////////
		ReportOffertaEconomicaDocument offerta = ReportOffertaEconomicaDocument.Factory.newInstance();
		offerta.addNewReportOffertaEconomica();
		
		// genera dei dati offerta vuoti, che comunque non verranno visualizzati dal PDF
		offerta.getReportOffertaEconomica().setCodiceOfferta(helper.getIdBando());
		offerta.getReportOffertaEconomica().setOggetto(helper.getDescBando());
		offerta.getReportOffertaEconomica().setCig("");
		offerta.getReportOffertaEconomica().setTipoAppalto("");
		offerta.getReportOffertaEconomica().setCriterioAggiudicazione("");
		offerta.getReportOffertaEconomica().setDataPresentazione(new GregorianCalendar());
		offerta.getReportOffertaEconomica().setNumGiorniValiditaOfferta("");
		OffertaEconomicaType o = offerta.getReportOffertaEconomica().addNewOfferta();
		o.setImportoBaseGara(0);
		o.setImportoOffertoLettere(UtilityNumeri.getDecimaleInLinguaItaliana(0, 2, 5));
		o.setImportoNonSoggettoRibasso(0);
		o.setImportoSicurezza(0);
		o.setImportoOneriProgettazione(0);
		
		// DOCUMENTAZIONE 
		ListaDocumentiType listaDocumenti = offerta.getReportOffertaEconomica().addNewDocumenti();
		documenti.addDocumenti(listaDocumenti, false);
		
		// SA
		this.addStazioneAppaltante(
				sa, 
				offerta.getReportOffertaEconomica(),
				this.bandoIscrizione.getStazioneAppaltante());
		
		// DATI IMPRESA
		this.addDatiImpresa(
				helper.getImpresa(), 
				offerta.getReportOffertaEconomica().addNewDatiImpresa());
		
		// FIRMATARI
		this.addFirmatari(
				offerta, 
				helper.getDenominazioneRTI(),
				helper.getComponenti(),
				helper.getImpresa());

		// si sostituiscono tutti i dati tabellati con le corrispondenti descrizioni 
		// e si eliminano i dati non previsti in configurazione
		this.sostituzioneTabellati(
				offerta.getReportOffertaEconomica().getDatiImpresa().getImpresa(),
				offerta.getReportOffertaEconomica(),
				this.getMaps(), 
				helper.getImpresa(), 
				documenti);
		
		// CATEGORIE ELENCO SELEZIONATE
		String xml = this.addCategorie(helper, offerta.toString());
		
		// genera il JSON sorgente per il report
		String json = this.questionario.getJsonReportIscrizione(xml);
		
		this.setJsonSource( json );
	}
	
	@Override
	protected XmlObject generaXmlConDatiTipizzati() throws ApsException, IOException {
		//WizardIscrizioneHelper helper = (WizardIscrizioneHelper) this.session
		//	.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
		//return generaXmlConDatiTipizzati(helper);
		return null;
	}

	@Override
	protected void sostituzioneTabellati(
			ImpresaType docImpresa,
			ReportOffertaEconomicaType docOfferta,
			Map<String, LinkedHashMap<String, String>> maps,
			WizardDatiImpresaHelper impresa, 
			DocumentiAllegatiHelper documenti) throws ApsException 
	{
		super.sostituzioneTabellati(docImpresa, docOfferta, maps, impresa, documenti);
		
		this.tipoElenco = "";
		if (StringUtils.isNotEmpty(this.bandoIscrizione.getDatiGeneraliBandoIscrizione().getTipoElenco())) {
			this.tipoElenco = getMaps()
					.get(InterceptorEncodedData.LISTA_TIPI_ELENCO_OPERATORI)
					.get(this.bandoIscrizione.getDatiGeneraliBandoIscrizione().getTipoElenco());
		}
	}

	/**
	 * aggiungi le categorie selezionate
	 * @throws IOException 
	 * @throws ApsException 
	 */
	private String addCategorie(WizardIscrizioneHelper helper, String xml) throws Exception {
		// recupera il documento xml dell'iscrizione
		XmlObject docIscrizione = helper.getXmlDocument(false);
		
		ListaCategorieIscrizioneType listaCat = null;
		if (helper.isAggiornamentoIscrizione()) {
			AggIscrizioneImpresaElencoOpType d = ((AggiornamentoIscrizioneImpresaElencoOperatoriDocument) docIscrizione)
					.getAggiornamentoIscrizioneImpresaElencoOperatori();
			listaCat = d.getCategorieIscrizione();
		} else {
			IscrizioneImpresaElencoOpType d = ((IscrizioneImpresaElencoOperatoriDocument) docIscrizione)
					.getIscrizioneImpresaElencoOperatori();
			listaCat = d.getCategorieIscrizione();
		}
				
		CategoriaBandoIscrizioneType[] categorie = this.bandiManager
				.getElencoCategorieBandoIscrizione(helper.getIdBando(), null);
		
		// normalizza in chiaro i codici
		this.decodeCategorieBandoIscrizione(listaCat, categorie);

		// aggiungi all'xml del documento le categorie iscrizione selezionate...
		if(listaCat != null && listaCat.getCategoriaArray() != null) {
			// NON FUNZIONA!!!!!
			// C'E' UNA SOLUZIONE MIGLIORE, MA MI SONO ROTTO DI PERDERE TEMPO!!! 
//            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
//            DocumentBuilder b = f.newDocumentBuilder();
//            Document d = b.parse(new InputSource(new StringReader(xml)));
//            
//            Node root = d.getFirstChild();
//			Element list = d.createElement("categorieIscrizione");
//			root.appendChild(list);
//			for(int i = 0; i < listaCat.getCategoriaArray().length; i++) {
//				Element cat = d.createElement("categoria");
//				list.appendChild(cat);
//				
//				cat.setAttribute("categoria", listaCat.getCategoriaArray()[i].getCategoria());
//				if(listaCat.getCategoriaArray()[i].isSetClassificaMassima()) {
//					cat.setAttribute("classificaMinima", listaCat.getCategoriaArray()[i].getClassificaMinima());
//				}
//				if(listaCat.getCategoriaArray()[i].isSetClassificaMassima()) {
//					cat.setAttribute("classificaMassima", listaCat.getCategoriaArray()[i].getClassificaMassima());
//				}
//				if(listaCat.getCategoriaArray()[i].isSetNota()) {
//					cat.setAttribute("categoria", listaCat.getCategoriaArray()[i].getNota());
//				}
//			}
//			StringWriter writer = new StringWriter();
//			DOMSource src = new DOMSource(d);
//		    StreamResult res = new StreamResult();
//		    TransformerFactory tf = TransformerFactory.newInstance();
//		    Transformer t = tf.newTransformer();
//		    t.transform(src, res);
//		    xml = writer.toString();
			
			StringBuilder list = new StringBuilder();
			list.append("<categorieIscrizione>").append('\n');
			for(int i = 0; i < listaCat.getCategoriaArray().length; i++) {
				list.append("<categoria>").append('\n');
				list.append("<categoria>").append(listaCat.getCategoriaArray()[i].getCategoria()).append("</categoria>").append('\n');
				if(listaCat.getCategoriaArray()[i].isSetClassificaMinima()) {
					list.append("<classificaMinima>").append(listaCat.getCategoriaArray()[i].getClassificaMinima()).append("</classificaMinima>").append('\n');
				}
				if(listaCat.getCategoriaArray()[i].isSetClassificaMassima()) {
					list.append("<classificaMassima>").append(listaCat.getCategoriaArray()[i].getClassificaMassima()).append("</classificaMassima>").append('\n');
				}
				if(listaCat.getCategoriaArray()[i].isSetNota()) {
					list.append("<nota>").append(listaCat.getCategoriaArray()[i].getNota()).append("</nota>").append('\n');
				}
				list.append("</categoria>").append('\n');
			}
			list.append("</categorieIscrizione>").append('\n');
			
			int i = xml.indexOf("</ReportOffertaEconomica>");
			xml = xml.substring(0, i - 1) + list.toString() + xml.substring(i);   
		}
		
		return xml;
	}

}
