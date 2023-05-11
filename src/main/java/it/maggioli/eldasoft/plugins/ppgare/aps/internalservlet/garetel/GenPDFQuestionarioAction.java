package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import it.eldasoft.sil.portgare.datatypes.FirmatarioType;
import it.eldasoft.sil.portgare.datatypes.ImpresaType;
import it.eldasoft.sil.portgare.datatypes.IndirizzoType;
import it.eldasoft.sil.portgare.datatypes.ListaDocumentiType;
import it.eldasoft.sil.portgare.datatypes.OffertaEconomicaType;
import it.eldasoft.sil.portgare.datatypes.ReportOffertaEconomicaDocument;
import it.eldasoft.sil.portgare.datatypes.ReportOffertaEconomicaType;
import it.eldasoft.sil.portgare.datatypes.StazioneAppaltanteType;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioStazioneAppaltanteType;
import it.eldasoft.www.sil.WSGareAppalto.GaraType;
import it.eldasoft.www.sil.WSGareAppalto.StazioneAppaltanteBandoType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.DocumentiAllegatiHelper;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.qcompiler.inc.QCQuestionario;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.report.GenPDFAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.NtpManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.IComponente;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.CalendarValidator;
import org.apache.xmlbeans.XmlObject;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GenPDFQuestionarioAction extends GenPDFAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -2247694427155191909L;
	
	protected IBandiManager bandiManager;
	protected NtpManager ntpManager;
	protected IComunicazioniManager comunicazioniManager;
	protected IEventManager eventManager;
	
	private Long idCom;
		
	private ComunicazioneType comunicazione;
	private QCQuestionario questionario;
	@Validate(EParamValidation.CODICE)
	private String codice;
	@Validate(EParamValidation.CODICE)
	private String codiceGara;
	private int operazione;
	private int tipoBusta;
	@Validate(EParamValidation.DIGIT)
	private String progressivoOfferta;
	
	
	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public void setNtpManager(NtpManager ntpManager) {
		this.ntpManager = ntpManager;
	}

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}

	public Long getIdCom() {
		return idCom;
	}

	public void setIdCom(Long idCom) {
		this.idCom = idCom;
	}

	@Override
	public String getUrlErrori() {
		return this.urlPage +
				"?actionPath=/ExtStr2/do/FrontEnd/GareTel/openQC.action" +
				"&currentFrame=" + this.currentFrame;
	}

	/**
	 * costruttore del report generico
	 */
	public GenPDFQuestionarioAction(String reportName, String xmlRootNode) {
		super(reportName, xmlRootNode);
	}

	/**
	 * costruttore del report 
	 */
	public GenPDFQuestionarioAction() {
		super("RiepilogoQForm",				// nome del file della risorsa jasper 
		      "content");					// nodo radice del datasource json
	}
		
	@Override
	protected boolean reportInit() {
		boolean continua = true;
		
		this.questionario = null;
		try {
			// recupera la comunicazione e l'allegato "questionario.json"
			this.comunicazione = this.comunicazioniManager.getComunicazione(
					CommonSystemConstants.ID_APPLICATIVO, 
					this.idCom);
			
			this.getParametriFromComunicazione(comunicazione);
				
			WizardDocumentiBustaHelper documenti = GestioneBuste
				.getBustaFromSession(this.tipoBusta).getHelperDocumenti();
			
			// recupera il json del questionario...
			this.questionario = documenti.getQuestionarioAllegato(DocumentiAllegatiHelper.QUESTIONARIO_GARE_FILENAME, this.tipoBusta);
			
			if (this.questionario == null) {
				// la sessione e' scaduta, occorre riconnettersi
				session.put(ERROR_DOWNLOAD, this.getText("Errors.sessionExpired"));
				this.setTarget(INPUT);
				continua = false;
			} else {
				if(this.questionario != null) {
					// 4. GENERAZIONE PDF DI RIEPILOGO: ESTENSIONE DEL DATASOURCE JSON
					//Si estende il json in modo da prevedere oltre a:
					// - survey: i dati del questionario
					// - systemVariables: le variabili per il controllo del questionario
					// anche il nuovo attributo
					// - reportVariables: tracciato dei dati di testata da utilizzare per la sola stampa PDF
					// Da alcune nostre verifiche, in DB c'� l'intero json del questionario, e non la sola componente survey. 
					// Pertanto se il datasource sar� l'intero json, lascerei le systemVariables esistenti senza replicarle 
					// tra le reportVariables.
					// Allego una proposta di JSON per la parte �reportVariables�; 
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
		String dta = UtilityDate.convertiData(this.ntpManager.getNtpDate(), UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);

		// imposta i parametri del report con i dati disponibili...
		DettaglioGaraType dettGara = this.bandiManager.getDettaglioGara(this.codiceGara);
		GaraType gara = dettGara.getDatiGeneraliGara();
		
		WizardPartecipazioneHelper partecipazioneHelper = GestioneBuste.getPartecipazioneFromSession().getHelper();

		if(partecipazioneHelper != null) {
			
			boolean isRTI = (partecipazioneHelper.isRti());
	
			params.put("RIEPILOGOELENCO", false);
//			params.put("LIBERO_PROFESSIONISTA", datiImpresaHelper.isLiberoProfessionista());
			params.put("TIMESTAMP", UtilityDate.convertiData(this.ntpManager.getNtpDate(), 
														     UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
			params.put("IS_RTI", isRTI);
			params.put("DENOMINAZIONE_RTI", partecipazioneHelper.getDenominazioneRTI());

			params.put("NOME_CLIENTE", (String) this.appParamManager.getConfigurationValue(AppParamManager.NOME_CLIENTE));
	
			if(partecipazioneHelper.isPlicoUnicoOfferteDistinte()){
//				params.put("CODICE_INTERNO_LOTTO", bustaRiepilogativa.getListaCodiciInterniLotti().get(this.codice));
//				params.put("OGGETTO_LOTTO", bustaRiepilogativa.getBusteEconomicheLotti().get(this.codice).getOggetto());
			}
			params.put("PUNTIORDINANTIVISIBILI", this.customConfigManager.isVisible("GARE-DOCUMOFFERTA", "PUNTIORDIISTR"));
			
			params.put("ITER_GARA", gara.getIterGara());
			params.put("TIPORIBASSO", gara.getTipoRibasso());
			
			// in caso di ribasso pesato 
			// calcola il ribasso equivalente come sommatoria dei ribassi pesati
//			double ribassoEquivalente = 0.0;
//			if(helper.getGara().getTipoRibasso() != null && helper.getGara().getTipoRibasso() == 3 && 
//			   helper.getVociDettaglio() != null) 
//			{
//				for(VoceDettaglioOffertaType item : helper.getVociDettaglio()) {
//					if(item.getRibassoPesato() != null) {
//						ribassoEquivalente += item.getRibassoPesato().doubleValue();
//					}
//				}
//			}
//			params.put("RIBASSOEQUIVALENTE", ribassoEquivalente);
//			params.put("RIBASSOEQUIVALENTELETTERE", UtilityNumeri.getDecimaleInLinguaItaliana(ribassoEquivalente, 1, 
//													helper.getNumDecimaliRibasso().intValue()));
//			params.put("PERCENTUALEMANODOPERAVISIBILE", helper.isPercentualeManodoperaVisible());

			params.put("ACCORDOQUADRO", gara.isAccordoQuadro());
			params.put("IMPNORIBASSO", gara.getImportoNonSoggettoRibasso());
			params.put("IMPSICUREZZA", gara.getImportoSicurezza());
			params.put("SICINC", gara.isOffertaComprensivaSicurezza());
			params.put("IMPONERIPROGETT", gara.getImportoOneriProgettazione());
		}
		params.put("SOC_COOP_VISIBLE", hasToShowSocCoop(partecipazioneHelper.getImpresa()));

//		// JASPER 6.16.0: 
//		// servono ??? 
//		// come vanno inizializzati i seguenti parametri ??? 
//		params.put("JSON_INPUT_STREAM", "");
//		params.put("net.sf.jasperreports.json.source", "");
//		params.put("net.sf.jasperreports.json.date.pattern", "");
//		params.put("net.sf.jasperreports.json.number.pattern", "");
//		params.put("JSON_LOCALE", "");
//		params.put("net.sf.jasperreports.json.locale.code", "");
//		params.put("JSON_TIME_ZONE", "");
//		params.put("net.sf.jasperreports.json.timezone.id", "");
	}

	@Override
	protected void reportCompleted() {
		String target = SUCCESS;
		try {
//			WizardDocumentiBustaHelper helper = WizardDocumentiBustaHelper
//				.getFromSession(this.session, this.tipoBusta);
			WizardDocumentiBustaHelper helper = GestioneBuste
				.getBustaFromSession(this.tipoBusta).getHelperDocumenti();
		
			if (helper == null) {
				// la sessione e' scaduta, occorre riconnettersi
				this.addActionError(this.getText("Errors.sessionExpired"));
				target = CommonSystemConstants.PORTAL_ERROR;
			} else {
				this.codice = helper.getCodice();
				this.codiceGara = helper.getCodiceGara();
				
				if(SUCCESS.equals(this.getTarget()) && 
				   this.comunicazione != null && this.questionario != null) 
				{
					// aggiorna l'hash del pdf e aggiorna il contenuto json del questionario...
					helper.setPdfUuid(this.uuid);
					helper.updateQuestionario(this.questionario.getQuestionario());
					
					// salva l'hash nell'xml della comunicazione (uuid)...
					target = ProcessPageDocumentiBustaAction.saveDocumenti(
							this.codiceGara,
							this.codice,
							this.operazione,
							this.tipoBusta,
							this.session,
							this); 
				}
			}
		} catch (Throwable e) {	
			ApsSystemUtils.logThrowable(e, this, "reportCompleted");
			session.put(ERROR_DOWNLOAD,
						"Si e' verificato un errore inaspettato durante l'elaborazione dell'operazione: "
						+ e.getMessage());
			target = INPUT;	
		}
		this.setTarget(target);
	}

	/**
	 * genera il documento JSON con i rendendo i campi chiave descrittivi
	 */
	private void generaJsonConDatiTipizzati() throws Exception {
		//////////////////////////////////////////////////////////////////
		// recupera i dati necessari per la creazione dell'XML...
		WizardPartecipazioneHelper partecipazione = GestioneBuste.getPartecipazioneFromSession().getHelper();
    	
		WizardDatiImpresaHelper impresa = (WizardDatiImpresaHelper) session
    		.get(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA);
		
		WizardDocumentiBustaHelper documenti = GestioneBuste
			.getBustaFromSession(this.tipoBusta).getHelperDocumenti();

		DettaglioGaraType gara = this.bandiManager.getDettaglioGara(codiceGara);
		
		// per gara a lotti ESPLBASE e' valorizzato solo per la gara e non per il lotto
		int nascondiImportoBaseGara = (gara != null && gara.getDatiGeneraliGara() != null 
									   ? gara.getDatiGeneraliGara().getNascondiImportoBaseGara()
									   : 0);
		if( !documenti.getCodice().equals(documenti.getCodiceGara()) ) {
			DettaglioGaraType g = this.bandiManager.getDettaglioGara(documenti.getCodiceGara());
			nascondiImportoBaseGara = (g != null && g.getDatiGeneraliGara() != null
									   ? g.getDatiGeneraliGara().getNascondiImportoBaseGara()
									   : 0);
		}
		
		DettaglioStazioneAppaltanteType sa = null;
		if(gara != null && gara.getStazioneAppaltante() != null && 
		   StringUtils.isNotEmpty(gara.getStazioneAppaltante().getCodice())) { 
			sa = this.bandiManager.getDettaglioStazioneAppaltante(gara.getStazioneAppaltante().getCodice());
		}

		//////////////////////////////////////////////////////////////////
		// genera l'XML di una busta economica (senza i dati dell'offerta)
		ReportOffertaEconomicaDocument offerta = ReportOffertaEconomicaDocument.Factory.newInstance();
		offerta.addNewReportOffertaEconomica();

		// GENERICI
		offerta.getReportOffertaEconomica().setCodiceOfferta(gara.getDatiGeneraliGara().getCodice());
		offerta.getReportOffertaEconomica().setOggetto(gara.getDatiGeneraliGara().getOggetto());
		offerta.getReportOffertaEconomica().setCig(gara.getDatiGeneraliGara().getCig());
		offerta.getReportOffertaEconomica().setTipoAppalto(gara.getDatiGeneraliGara().getTipoAppalto());
		offerta.getReportOffertaEconomica().setCriterioAggiudicazione(gara.getDatiGeneraliGara().getTipoAggiudicazione());
		offerta.getReportOffertaEconomica().setDataPresentazione(new GregorianCalendar());
		
		// INFO DI GARA
		OffertaEconomicaType o = offerta.getReportOffertaEconomica().addNewOfferta();
		
		o.setImportoBaseGara(gara.getDatiGeneraliGara().getImporto());
		o.setImportoOffertoLettere(UtilityNumeri.getDecimaleInLinguaItaliana(gara.getDatiGeneraliGara().getImporto(), 2, 5));		
		o.setImportoNonSoggettoRibasso(gara.getDatiGeneraliGara().getImportoNonSoggettoRibasso());
		o.setImportoSicurezza(gara.getDatiGeneraliGara().getImportoSicurezza());
		o.setImportoOneriProgettazione(gara.getDatiGeneraliGara().getImportoOneriProgettazione());
		
		// DOCUMENTAZIONE
		ListaDocumentiType listaDocumenti = offerta.getReportOffertaEconomica().addNewDocumenti();
		documenti.addDocumenti(listaDocumenti, false);
		
//		// si inseriscono le voci NON soggette a ribasso per inserirle nel report
//		//addVociNonSoggetteARibasso(offerta, helper);

		// SA
		this.addStazioneAppaltante(
				sa, 
				offerta.getReportOffertaEconomica(),
				gara.getStazioneAppaltante());
		
		// DATI IMPRESA
		this.addDatiImpresa(
				impresa, 
				offerta.getReportOffertaEconomica().addNewDatiImpresa());

		// FIRMATARI
		this.addFirmatari(
				offerta, 
				partecipazione.getDenominazioneRTI(),
				partecipazione.getComponenti(),
				impresa);
		
		offerta.getReportOffertaEconomica().setNumGiorniValiditaOfferta(
				gara.getDatiGeneraliGara().getNumGiorniValiditaOfferta());

		// si sostituiscono tutti i dati tabellati con le
		// corrispondenti descrizioni e si eliminano i dati non previsti in
		// configurazione
		this.sostituzioneTabellati(
				offerta.getReportOffertaEconomica().getDatiImpresa().getImpresa(),
				offerta.getReportOffertaEconomica(),
				getMaps(), 
				impresa, 
				documenti);
		
		// genera il JSON sorgente per il report
		String json = this.questionario.getJsonReportOfferta(offerta.toString(), nascondiImportoBaseGara);
		
		this.setJsonSource( json );
	}
	
	/**
	 * genera il documento XML con i rendendo i campi chiave descrittivi
	 */
	@Override
	protected XmlObject generaXmlConDatiTipizzati() throws Exception {
		return null;
	}
	
//	/**
//	 * Generazione del documento xml contentente i dati dell'offerta economica 
//	 */
//	protected XmlObject generaXmlConDatiTipizzati(
//			WizardOffertaEconomicaHelper helper) throws Exception 
//	{
//		XmlObject document = null;
//		return document;
//	}

	/**
	 * prepara i parametri per il riutilizzo del metodo "aggiornaAllegato()"
	 */
	private void getParametriFromComunicazione(ComunicazioneType comunicazione) {
		this.codice = "";
		this.codiceGara = "";
		this.operazione = 0;
	    this.tipoBusta = 0;
	    this.progressivoOfferta = "";
	    
		if(comunicazione.getDettaglioComunicazione().getTipoComunicazione()
				.startsWith(PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT)) {
			this.tipoBusta = PortGareSystemConstants.BUSTA_PRE_QUALIFICA;
		} else if(comunicazione.getDettaglioComunicazione().getTipoComunicazione()
				.startsWith(PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_AMMINISTRATIVA)) {
			this.tipoBusta = PortGareSystemConstants.BUSTA_AMMINISTRATIVA;
		} else if(comunicazione.getDettaglioComunicazione().getTipoComunicazione()
				.startsWith(PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA)) {
			this.tipoBusta = PortGareSystemConstants.BUSTA_TECNICA;
		} else if(comunicazione.getDettaglioComunicazione().getTipoComunicazione()
				.startsWith(PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA)) {
			this.tipoBusta = PortGareSystemConstants.BUSTA_ECONOMICA;
		} 
		
		if(comunicazione.getDettaglioComunicazione().getTipoComunicazione()
				.startsWith(PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT)) {
			this.operazione = PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA;
		} else if(comunicazione.getDettaglioComunicazione().getTipoComunicazione()
				.startsWith(PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT)) {
			this.operazione = PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA;
		//} else {if(comunicazione.getDettaglioComunicazione().getTipoComunicazione()
		//		.startsWith(PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA)) {
		} else {
			this.operazione = PortGareSystemConstants.TIPOLOGIA_EVENTO_COMPROVA_REQUISITI;
		} 
		
		this.progressivoOfferta = comunicazione.getDettaglioComunicazione().getChiave3();
		
		this.codice = comunicazione.getDettaglioComunicazione().getChiave2();		// viene inizializzato dopo aver recuperato l'helper dalla sessione
		this.codiceGara = comunicazione.getDettaglioComunicazione().getChiave2();	// viene inizializzato dopo aver recuperato l'helper dalla sessione
	}
	
	/**
	 * ...
	 */
	protected void addStazioneAppaltante(
			DettaglioStazioneAppaltanteType from,
			ReportOffertaEconomicaType report,
			StazioneAppaltanteBandoType stazioneAppaltanteBando) 
	{
		StazioneAppaltanteType to = report.addNewStazioneAppaltante();
		report.setPuntoOrdinanteArray( stazioneAppaltanteBando.getPuntoOrdinante() );
		report.setPuntoIstruttoreArray( stazioneAppaltanteBando.getPuntoIstruttore() );
		report.setRup(stazioneAppaltanteBando.getRup());
		Object defaultDen = appParamManager.getConfigurationValue(AppParamManager.DENOMINAZIONE_STAZIONE_APPALTANTE_UNICA);
		if (defaultDen != null && StringUtils.isNotEmpty(defaultDen.toString()))
			to.setDenominazione(defaultDen.toString());
		else
			to.setDenominazione(from.getDenominazione());
		
		to.setCodiceFiscale(from.getCodiceFiscale());
		to.setIndirizzo(from.getIndirizzo());
		to.setNumCivico(from.getNumCivico());
		to.setCap(from.getCap());
		to.setComune(from.getComune());
		to.setProvincia(from.getProvincia());
		to.setEmail(from.getEmail());
		to.setPec(from.getPec());
		to.setTelefono(from.getTelefono());
		to.setFax(from.getFax());
	}

	/**
	 * sostituisci i codici dei tabellati con i corrispettivi valori in chiaro 
	 */
	protected void sostituzioneTabellati(
			ImpresaType docImpresa,
			ReportOffertaEconomicaType docOfferta,
			Map<String, LinkedHashMap<String, String>> maps,
			WizardDatiImpresaHelper impresa, 
			DocumentiAllegatiHelper documenti) throws ApsException 
	{
		// sostituisci i tabellati 
		if (StringUtils.isNotEmpty(docImpresa.getNaturaGiuridica())) {
			docImpresa.setNaturaGiuridica(maps
					.get(InterceptorEncodedData.LISTA_TIPI_NATURA_GIURIDICA)
					.get(docImpresa.getNaturaGiuridica()));
		}
		if (StringUtils.isNotEmpty(docImpresa.getTipoSocietaCooperativa())) {
			docImpresa.setTipoSocietaCooperativa(maps
					 .get(InterceptorEncodedData.LISTA_TIPOLOGIE_SOCIETA_COOPERATIVE)
					 .get(docImpresa.getTipoSocietaCooperativa()));
		}

		if (StringUtils.isNotEmpty(docImpresa.getTipoImpresa())) {
			docImpresa.setTipoImpresa(maps
					.get(InterceptorEncodedData.LISTA_TIPI_IMPRESA)
					.get(docImpresa.getTipoImpresa()));
		}

		if (StringUtils.isNotEmpty(docImpresa.getSettoreProduttivo())) {
			docImpresa.setSettoreProduttivo(maps
					.get(InterceptorEncodedData.LISTA_SETTORI_PRODUTTIVI)
					.get(docImpresa.getSettoreProduttivo()));
		}

		if (StringUtils.isNotEmpty(docImpresa.getCciaa().getProvinciaIscrizione())) {
			docImpresa.getCciaa().setProvinciaIscrizione(maps
					.get(InterceptorEncodedData.LISTA_PROVINCE)
					.get(docImpresa.getCciaa().getProvinciaIscrizione()));
		}

		if (StringUtils.isNotEmpty(docOfferta.getCriterioAggiudicazione())) {
			docOfferta.setCriterioAggiudicazione(maps
					.get(InterceptorEncodedData.LISTA_TIPI_AGGIUDICAZIONE)
					.get(docOfferta.getCriterioAggiudicazione()));
		}


		FirmatarioType[] firmatari = docOfferta.getFirmatarioArray();
		for (int i = 0; i < firmatari.length; i++) {
			this.decodeFirmatario(firmatari[i]);
		}
		// solo se la mandante e' libero professionista, correggi cognome e nome con i dati dell'impresa
		if(impresa.isLiberoProfessionista() && 
		   impresa.getAltriDatiAnagraficiImpresa().getNome() != null &&
		   impresa.getAltriDatiAnagraficiImpresa().getCognome() != null &&
		   firmatari.length > 0)
		{
			firmatari[0].setNome(impresa.getAltriDatiAnagraficiImpresa().getNome());
			firmatari[0].setCognome(impresa.getAltriDatiAnagraficiImpresa().getCognome());
		}
	}

	/**
	 * ...
	 */
	protected String getQualificaSoggetto(IComponente soggetto) {
		String qualifica = null;
//		
//		if(StringUtils.isNotBlank(firmatario.getQualifica()) && 
//				   !"Libero professionista".equalsIgnoreCase(firmatario.getQualifica())) {
//					
//					if(String.valueOf(firmatario.getQualifica().charAt(0)).equals(CommonSystemConstants.TIPO_SOGGETTO_DIRETTORE_TECNICO)) {
//						firmatario.setQualifica(getMaps()
//								.get(InterceptorEncodedData.LISTA_TIPI_SOGGETTO)
//								.get(CommonSystemConstants.TIPO_SOGGETTO_DIRETTORE_TECNICO + 
//								     CommonSystemConstants.SEPARATORE_TABELLATI_CONCATENATI));
//						
//					} else if(String.valueOf(firmatario.getQualifica().charAt(0)).equals(CommonSystemConstants.TIPO_SOGGETTO_LEGALE_RAPPRESENTANTE)) {
//						firmatario.setQualifica(getMaps()
//								.get(InterceptorEncodedData.LISTA_TIPI_SOGGETTO)
//								.get(CommonSystemConstants.TIPO_SOGGETTO_LEGALE_RAPPRESENTANTE + 
//								     CommonSystemConstants.SEPARATORE_TABELLATI_CONCATENATI));
//					} else {
//						firmatario.setQualifica(getMaps()
//								.get(InterceptorEncodedData.LISTA_TIPI_ALTRA_CARICA)
//								.get(firmatario.getQualifica()));
//					}
//				} else {
//					firmatario.setQualifica("libero professionista");
//				}
		return qualifica;
	}
	
	
	/**
	 * ... 
	 */
	protected void addFirmatari(
			ReportOffertaEconomicaDocument offerta,
			String denominazioneRTI,
			List<IComponente> componenti,
			WizardDatiImpresaHelper impresa) 
	{
		if(componenti != null) {
						
			if(denominazioneRTI != null) {
				// partecipazione.getDenominazioneRTI()
				//offerta.getReportOffertaEconomica().getOfferta().???
			}	
			
			// componenti del raggruppamenti RTI 
			if(componenti != null && componenti.size() > 0) {
				// RTI (mandataria + mandanti)
				// aggiungi la mandataria 
				FirmatarioType firmatario = offerta.getReportOffertaEconomica().addNewFirmatario();
				if (impresa.isLiberoProfessionista()) {
					// mandataria di tipo libero professionista
					firmatario.setNome(impresa.getAltriDatiAnagraficiImpresa().getNome());
					firmatario.setCognome(impresa.getAltriDatiAnagraficiImpresa().getCognome());
					firmatario.setQualifica("libero professionista");
					firmatario.setCodiceFiscaleFirmatario(impresa.getDatiPrincipaliImpresa().getCodiceFiscale());
					firmatario.setComuneNascita(impresa.getAltriDatiAnagraficiImpresa().getComuneNascita());
					firmatario.setDataNascita(CalendarValidator.getInstance().
							validate(impresa.getAltriDatiAnagraficiImpresa().getDataNascita(), "dd/MM/yyyy"));
					firmatario.setProvinciaNascita(impresa.getAltriDatiAnagraficiImpresa().getProvinciaNascita());
					firmatario.setSesso(impresa.getAltriDatiAnagraficiImpresa().getSesso());
					firmatario.setComuneNascita(impresa.getAltriDatiAnagraficiImpresa().getComuneNascita());
					firmatario.setCodiceFiscaleImpresa(impresa.getDatiPrincipaliImpresa().getCodiceFiscale());
					firmatario.setPartitaIVAImpresa(impresa.getDatiPrincipaliImpresa().getPartitaIVA());
					firmatario.setTipoImpresa(impresa.getDatiPrincipaliImpresa().getTipoImpresa());
					firmatario.setRagioneSociale(impresa.getDatiPrincipaliImpresa().getRagioneSociale());
					firmatario.setNazione(impresa.getDatiPrincipaliImpresa().getNazioneSedeLegale());
					
					IndirizzoType residenza = firmatario.addNewResidenza();
					residenza.setCap(impresa.getDatiPrincipaliImpresa().getCapSedeLegale());
					residenza.setIndirizzo(impresa.getDatiPrincipaliImpresa().getIndirizzoSedeLegale());
					residenza.setNumCivico(impresa.getDatiPrincipaliImpresa().getNumCivicoSedeLegale());
					residenza.setComune(impresa.getDatiPrincipaliImpresa().getComuneSedeLegale());
					residenza.setNazione(impresa.getDatiPrincipaliImpresa().getNazioneSedeLegale());
					residenza.setProvincia(impresa.getDatiPrincipaliImpresa().getProvinciaSedeLegale());
				} else {
					// mandataria di tipo impresa o consorzio
					firmatario.setNome(impresa.getDatiPrincipaliImpresa().getRagioneSociale());
					firmatario.setCognome(null);
					//firmatario.setQualifica(null);
					firmatario.setCodiceFiscaleFirmatario(impresa.getDatiPrincipaliImpresa().getCodiceFiscale());
					//firmatario.setComuneNascita(componente.getComuneNascita());
					//firmatario.setDataNascita(CalendarValidator.getInstance()
					//		.validate(soggettoFromLista.getDataNascita(), "dd/MM/yyyy"));
					//firmatario.setProvinciaNascita(soggettoFromLista.getProvinciaNascita());
					//firmatario.setSesso(soggettoFromLista.getSesso());
					//firmatario.setComuneNascita(soggettoFromLista.getComuneNascita());
					firmatario.setCodiceFiscaleImpresa(impresa.getDatiPrincipaliImpresa().getCodiceFiscale());
					firmatario.setPartitaIVAImpresa(impresa.getDatiPrincipaliImpresa().getPartitaIVA());
					firmatario.setRagioneSociale(impresa.getDatiPrincipaliImpresa().getRagioneSociale());
					firmatario.setTipoImpresa(impresa.getDatiPrincipaliImpresa().getTipoImpresa());
					
					//IndirizzoType residenza = firmatario.addNewResidenza();
					//residenza.setCap(soggettoFromLista.getCap());
					//residenza.setIndirizzo(soggettoFromLista.getIndirizzo());
					//residenza.setNumCivico(soggettoFromLista.getNumCivico());
					//residenza.setComune(soggettoFromLista.getComune());
					//residenza.setNazione(soggettoFromLista.getNazione());
					//residenza.setProvincia(soggettoFromLista.getProvincia());
				}

				// mandanti
				for(int i = 0; i < componenti.size(); i++) {
					IComponente mandante = componenti.get(i);
					firmatario = offerta.getReportOffertaEconomica().addNewFirmatario();
					
					// CASO MANDANTI
					firmatario.setNome(mandante.getRagioneSociale());
					//firmatario.setCognome(mandante.);
					if ("6".equals(mandante.getTipoImpresa())) {
						// libero professionista
						firmatario.setQualifica("libero professionista");
					} else {
						// impresa o consorzio
						firmatario.setQualifica( this.getQualificaSoggetto(mandante) );
					}
					firmatario.setCodiceFiscaleFirmatario(mandante.getCodiceFiscale());
					//firmatario.setComuneNascita(currentFirmatario.getComuneNascita());
					//firmatario.setNazione(currentFirmatario.getNazione());
					//firmatario.setProvinciaNascita(currentFirmatario.getProvinciaNascita());
					//firmatario.setSesso(currentFirmatario.getSesso());
					//firmatario.setDataNascita(CalendarValidator.getInstance().validate(currentFirmatario.getDataNascita(), "dd/MM/yyyy"));						
					//firmatario.setComuneNascita(currentFirmatario.getComuneNascita());
					String cf = ("2".equals(mandante.getAmbitoTerritoriale()) 
								 ? mandante.getCodiceFiscale() 
								 : mandante.getIdFiscaleEstero());
					firmatario.setCodiceFiscaleImpresa(mandante.getCodiceFiscale());
					firmatario.setPartitaIVAImpresa(mandante.getPartitaIVA());
					firmatario.setRagioneSociale(mandante.getRagioneSociale());
					firmatario.setTipoImpresa(mandante.getTipoImpresa());
					
					//IndirizzoType residenza = firmatario.addNewResidenza();
					//residenza.setCap(currentFirmatario.getCap());
					//residenza.setIndirizzo(currentFirmatario.getIndirizzo());
					//residenza.setNumCivico(currentFirmatario.getNumCivico());
					//residenza.setComune(currentFirmatario.getComune());
					//residenza.setNazione(currentFirmatario.getNazione());
					//residenza.setProvincia(currentFirmatario.getProvincia());
				}
			} else {
				// libero professionista, impresa o consorzio
				FirmatarioType firmatario = offerta.getReportOffertaEconomica().addNewFirmatario();
				
				if(impresa.isLiberoProfessionista()) {
					// LIBERO PROFESSIONISTA
					if (StringUtils.isNotEmpty(impresa.getAltriDatiAnagraficiImpresa().getCognome())) {
						firmatario.setNome(impresa.getAltriDatiAnagraficiImpresa().getNome());
						firmatario.setCognome(impresa.getAltriDatiAnagraficiImpresa().getCognome());
					} else {
						firmatario.setNome(impresa.getDatiPrincipaliImpresa().getRagioneSociale());
						firmatario.setCognome(null);
					}
					firmatario.setCodiceFiscaleFirmatario(impresa.getDatiPrincipaliImpresa().getCodiceFiscale());
					firmatario.setComuneNascita(impresa.getAltriDatiAnagraficiImpresa().getComuneNascita());
					firmatario.setDataNascita(CalendarValidator.getInstance()
							.validate(impresa.getAltriDatiAnagraficiImpresa().getDataNascita(),	"dd/MM/yyyy"));
					firmatario.setProvinciaNascita(impresa.getAltriDatiAnagraficiImpresa().getProvinciaNascita());
					firmatario.setQualifica("Libero professionista");
					firmatario.setSesso(impresa.getAltriDatiAnagraficiImpresa().getSesso());
					firmatario.setComuneNascita(impresa.getAltriDatiAnagraficiImpresa().getComuneNascita());
					firmatario.setCodiceFiscaleImpresa(impresa.getDatiPrincipaliImpresa().getCodiceFiscale());
					firmatario.setPartitaIVAImpresa(impresa.getDatiPrincipaliImpresa().getPartitaIVA());
					firmatario.setTipoImpresa(impresa.getDatiPrincipaliImpresa().getTipoImpresa());
					firmatario.setRagioneSociale(impresa.getDatiPrincipaliImpresa().getRagioneSociale());
					
					IndirizzoType residenza = firmatario.addNewResidenza();
					residenza.setCap(impresa.getDatiPrincipaliImpresa().getCapSedeLegale());
					residenza.setIndirizzo(impresa.getDatiPrincipaliImpresa().getIndirizzoSedeLegale());
					residenza.setNumCivico(impresa.getDatiPrincipaliImpresa().getNumCivicoSedeLegale());
					residenza.setComune(impresa.getDatiPrincipaliImpresa().getComuneSedeLegale());
					residenza.setNazione(impresa.getDatiPrincipaliImpresa().getNazioneSedeLegale());
					residenza.setProvincia(impresa.getDatiPrincipaliImpresa().getProvinciaSedeLegale());
				} else {
					// IMPRESA O CONSORZIO
					firmatario.setNome(null);
					firmatario.setCognome(null);
					firmatario.setQualifica(null);
					firmatario.setCodiceFiscaleFirmatario(null);
					//firmatario.setComuneNascita(soggettoFromLista.getComuneNascita());
					//firmatario.setDataNascita(CalendarValidator.getInstance().validate(soggettoFromLista.getDataNascita(), "dd/MM/yyyy"));
					//firmatario.setProvinciaNascita(soggettoFromLista.getProvinciaNascita());
					//firmatario.setSesso(soggettoFromLista.getSesso());
					//firmatario.setComuneNascita(soggettoFromLista.getComuneNascita());
					firmatario.setCodiceFiscaleImpresa(impresa.getDatiPrincipaliImpresa().getCodiceFiscale());
					firmatario.setPartitaIVAImpresa(impresa.getDatiPrincipaliImpresa().getPartitaIVA());
					firmatario.setTipoImpresa(impresa.getDatiPrincipaliImpresa().getTipoImpresa());
					
					//IndirizzoType residenza = firmatario.addNewResidenza();
					//residenza.setCap(soggettoFromLista.getCap());
					//residenza.setIndirizzo(soggettoFromLista.getIndirizzo());
					//residenza.setNumCivico(soggettoFromLista.getNumCivico());
					//residenza.setComune(soggettoFromLista.getComune());
					//residenza.setNazione(soggettoFromLista.getNazione());
					//residenza.setProvincia(soggettoFromLista.getProvincia());
				}
			} 
		}
	}
	
}
