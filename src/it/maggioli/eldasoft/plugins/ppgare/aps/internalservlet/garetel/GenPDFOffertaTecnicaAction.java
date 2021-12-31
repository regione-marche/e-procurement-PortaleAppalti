package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.eldasoft.sil.portgare.datatypes.FirmatarioType;
import it.eldasoft.sil.portgare.datatypes.ImpresaType;
import it.eldasoft.sil.portgare.datatypes.ReportOffertaTecnicaDocument;
import it.eldasoft.sil.portgare.datatypes.ReportOffertaTecnicaType;
import it.eldasoft.sil.portgare.datatypes.StazioneAppaltanteType;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioStazioneAppaltanteType;
import it.eldasoft.www.sil.WSGareAppalto.StazioneAppaltanteBandoType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.report.GenPDFAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.NtpManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.FirmatarioBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.SoggettoImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlObject;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;

public class GenPDFOffertaTecnicaAction extends GenPDFAction {
	/**
	 * UID 
	 */
	private static final long serialVersionUID = 3765575699618154856L;
	
	private IBandiManager bandiManager;
	private NtpManager ntpManager;
	private IComunicazioniManager comunicazioniManager;
	
	private String firmatarioSelezionato;

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public void setNtpManager(NtpManager ntpManager) {
		this.ntpManager = ntpManager;
	}

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public String getFirmatarioSelezionato() {
		return firmatarioSelezionato;
	}

	public void setFirmatarioSelezionato(String firmatarioSelezionato) {
		this.firmatarioSelezionato = firmatarioSelezionato;
	}
	
	@Override
	public String getUrlErrori() {
		return this.urlPage +
				"?actionPath=/ExtStr2/do/FrontEnd/GareTel/openPageOffTecScaricaOfferta.action" +
				"&currentFrame=" + this.currentFrame;
	}
	
	/**
	 * costruttore del report 
	 */
	public GenPDFOffertaTecnicaAction() {
		super("OffertaTecnica",						 
		      "/ReportOffertaTecnica");
	}
	
	@Override
	protected boolean reportInit() {
		boolean continua = true;
	
		RiepilogoBusteHelper bustaRiepilogativa = (RiepilogoBusteHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA);
		
		WizardOffertaTecnicaHelper helper = (WizardOffertaTecnicaHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_OFFERTA_TECNICA);

		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			session.put(ERROR_DOWNLOAD, this.getText("Errors.sessionExpired"));
			this.setTarget(INPUT);
			continua = false;
		} else if (this.firmatarioSelezionato == null && helper.getComponentiRTI().size() == 0) {
			session.put(ERROR_DOWNLOAD, this.getText("Errors.firmatarioNotSet"));
			this.setTarget(INPUT);
			continua = false;
		} else {
			// la sessione non e' scaduta, si puo' procedere...
			WizardDatiImpresaHelper datiImpresaHelper = helper.getImpresa();
			
			FirmatarioBean firmatario = null;
			HashMap<String, SoggettoImpresaHelper> firmatariRTI = null;
			if (helper.getComponentiRTI().size() == 0) {
				// -- FIRMATARIO NON LIBERO PROFESSIONISTA --
				helper.setFirmatarioSelezionato(firmatarioSelezionato);
				firmatario = helper.getFirmatarioSelezionato();
			} else {
				// all'interno della stessa sessione di lavoro si passa da 
				// una rilettura di FS11R senza RTI a imposizione RTI  
				// => genero la lista degli ultimi firmatari
//				if(bustaRiepilogativa.getUltimiFirmatariInseriti() == null) {
//					bustaRiepilogativa.setUltimiFirmatariInseriti(new ArrayList<SoggettoImpresaHelper>());
//				}
//				if(bustaRiepilogativa.getCorrelazioniFirmatari() == null) {
//					bustaRiepilogativa.setCorrelazioniFirmatari(new ArrayList<String>());
//				}
				
				// Memorizzo nella FS11R l'ultimo set di firmatari inseriti, 
				// per ripresentalo nei lotti successivi
				bustaRiepilogativa.memorizzaUltimiFirmatariInseriti(helper.getComponentiRTI());
			}
		}
	
		return continua;
	}

	@Override
	protected void reportParametersInit(HashMap<String, Object> params)	throws Exception {
		WizardOffertaTecnicaHelper helper = (WizardOffertaTecnicaHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_OFFERTA_TECNICA);
		
		WizardPartecipazioneHelper partecipazioneHelper = (WizardPartecipazioneHelper) session
			.get(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);
		
		if(helper != null && partecipazioneHelper != null) {
			WizardDatiImpresaHelper datiImpresaHelper = helper.getImpresa();
			
			boolean isRTI = (helper.getComponentiRTI().size() > 0);
			
			params.put("LIBERO_PROFESSIONISTA", datiImpresaHelper.isLiberoProfessionista());
			params.put("TIMESTAMP", UtilityDate.convertiData(this.ntpManager.getNtpDate(), 
					                                         UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
			params.put("IS_RTI", isRTI);
			params.put("DENOMINAZIONE_RTI",partecipazioneHelper.getDenominazioneRTI());
			params.put("IMPNORIBASSO", helper.getGara().getImportoNonSoggettoRibasso());
			params.put("IMPSICUREZZA", helper.getGara().getImportoSicurezza());
			params.put("SICINC", helper.getGara().isOffertaComprensivaSicurezza());
			params.put("IMPONERIPROGETT", helper.getGara().getImportoOneriProgettazione());
			params.put("NOME_CLIENTE", (String) this.appParamManager.getConfigurationValue(AppParamManager.NOME_CLIENTE));
			
			RiepilogoBusteHelper bustaRiepilogativa = (RiepilogoBusteHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA);
			
			if(partecipazioneHelper.isPlicoUnicoOfferteDistinte()) {
				params.put("CODICE_INTERNO_LOTTO", bustaRiepilogativa.getListaCodiciInterniLotti().get(helper.getCodice()));
				params.put("OGGETTO_LOTTO", bustaRiepilogativa.getBusteTecnicheLotti().get(helper.getCodice()).getOggetto());
			}
			
			params.put("PUNTIORDINANTIVISIBILI", this.customConfigManager.isVisible("GARE-DOCUMOFFERTA", "PUNTIORDIISTR"));		
			//params.put("ITER_GARA", helper.getGara().getIterGara());
		}
	}

	@Override
	protected void reportCompleted() {
		try {
			RiepilogoBusteHelper bustaRiepilogativa = (RiepilogoBusteHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA);
	
			WizardOffertaTecnicaHelper helper = (WizardOffertaTecnicaHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_OFFERTA_TECNICA);

			if(SUCCESS.equals(this.getTarget())) {
				// si inserisce una referenza nel bean in sessione per la
				// rimozione del file alla rimozione del bean dalla sessione
				//helper.getPdfGenerati().add(pdf);

				// si marca che la rigenerazione del pdf non e' necessaria e
				// si setta l'uuid del file generato
				helper.setPdfUUID(this.uuid);
				helper.setRigenPdf(false);

				// si elimina l'eventuale precedente offerta allegata
				if (helper.getDocumenti().isDocOffertaPresente()) {
					helper.deleteDocumentoOffertaTecnica(this, this.eventManager);
				}

				// salva la comunicazione in bozza in modo da salvare il CRC
				// e l'eventuale eliminazione del PDF offerta precedente
				ProcessPageDocumentiOffertaAction.sendComunicazioneBusta(
						this, 
						this.comunicazioniManager, 
						this.eventManager,
						helper,
						CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
				helper.getDocumenti().setDatiModificati(false);
				helper.setDatiModificati(false);

				// Fase di riallineamento - necessaria in quanto viene 
				// eliminato il documento relativo all'offerta economica
				if(bustaRiepilogativa.getBustaTecnica() != null) {
					// --- LOTTO UNICO ---
					bustaRiepilogativa.getBustaTecnica().riallineaDocumenti(helper.getDocumenti());
				} else {
					// --- LOTTI DISTINTI ---
					// ho bisogno qua del codice lotto
					bustaRiepilogativa.getBusteTecnicheLotti().get(helper.getCodice()).riallineaDocumenti(helper.getDocumenti());
				}
				this.session.put(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_TECNICA, 
								 helper.getDocumenti());
				
				WizardDatiImpresaHelper datiImpresaHelper = helper.getImpresa();

				// Preparazione per invio busta riepilogativa
				// ed invia la nuova comunicazione con gli allineamenti del caso
				bustaRiepilogativa.sendComunicazioneBusta(
						CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA, 
						datiImpresaHelper, 
						this.getCurrentUser().getUsername(), 
						helper.getGara().getCodice(), 
						datiImpresaHelper.getDatiPrincipaliImpresa().getRagioneSociale(), 
						this.comunicazioniManager, 
						PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_RIEPILOGO,
						this);
			}			
		} catch (Throwable e) {	
			ApsSystemUtils.logThrowable(e, this, "reportCompleted");
			session.put(ERROR_DOWNLOAD,
						"Si e' verificato un errore inaspettato durante l'elaborazione dell'operazione: "
						+ e.getMessage());
			this.setTarget(INPUT);	
		}		
	}

	/**
	 * genera il documento XML con i rendendo i campi chiave descrittivi 
	 */
	@Override
	protected XmlObject generaXmlConDatiTipizzati() throws Exception {
		XmlObject document = null;
		
		WizardOffertaTecnicaHelper helper = (WizardOffertaTecnicaHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_OFFERTA_TECNICA);
		
		if(helper != null) {
			WizardDatiImpresaHelper datiImpresaHelper = helper.getImpresa();
	
			FirmatarioBean firmatario = null;
			if (helper.getComponentiRTI().size() == 0) {
				firmatario = helper.getFirmatarioSelezionato();
			} 
	
			// FASE 1: aggiungo i dati comuni al documento xml da agganciare al report
			ReportOffertaTecnicaDocument offerta = ReportOffertaTecnicaDocument.Factory.newInstance();
			
			helper.getXmlDocument(offerta, false, true);
			
//			// si inseriscono le voci NON soggette a ribasso per inserirle nel
//			// report
//			addVociNonSoggetteARibasso(offerta, helper);
			
			addStazioneAppaltante(
					bandiManager.getDettaglioStazioneAppaltante(
							helper.getStazioneAppaltanteBando().getCodice()),
							offerta.getReportOffertaTecnica(),
							helper.getStazioneAppaltanteBando());
			
			addDatiImpresa(
					datiImpresaHelper, 
					offerta.getReportOffertaTecnica().addNewDatiImpresa());

//			if(firmatario != null) {
//			//addFirmatario(firmatario,
//			//				offerta.getReportOffertaEconomica().addNewFirmatario(),
//			//				datiImpresaHelper);
//		}

			offerta.getReportOffertaTecnica().setNumGiorniValiditaOfferta(
					helper.getGara().getNumGiorniValiditaOfferta());
				
//			// aggiungi i dati dell'offerta tecnica...
//			addOffertaTecnica(offerta, helper);			
        
	
			// FASE 2: si sostituiscono tutti i dati tabellati con le corrispondenti 
			// descrizioni e si eliminano i dati non previsti in configurazione
			sostituzioneTabellati(
					offerta.getReportOffertaTecnica().getDatiImpresa().getImpresa(),
					offerta.getReportOffertaTecnica(),
					getMaps(),
					helper);
			
			document = offerta;
		}
		
		return document;
	}

	/**
	 * ... 
	 */
	private void addStazioneAppaltante(
			DettaglioStazioneAppaltanteType from,
			ReportOffertaTecnicaType report,
			StazioneAppaltanteBandoType stazioneAppaltanteBando) 
	{
		StazioneAppaltanteType to = report.addNewStazioneAppaltante();
		report.setPuntoOrdinanteArray( stazioneAppaltanteBando.getPuntoOrdinante() );
		report.setPuntoIstruttoreArray( stazioneAppaltanteBando.getPuntoIstruttore() );		
		report.setRup(stazioneAppaltanteBando.getRup());
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
	 * ...
	 */
	private void sostituzioneTabellati(
			ImpresaType docImpresa,
			ReportOffertaTecnicaType docOfferta,
			Map<String, LinkedHashMap<String, String>> maps,
			WizardOffertaTecnicaHelper offertaTecnica) throws ApsException 
	{
		if (StringUtils.isNotEmpty(docImpresa.getNaturaGiuridica())) {
			docImpresa.setNaturaGiuridica(maps
					.get(InterceptorEncodedData.LISTA_TIPI_NATURA_GIURIDICA)
					.get(docImpresa.getNaturaGiuridica()));
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

		if(StringUtils.isNotEmpty(docOfferta.getCriterioAggiudicazione())) {
			docOfferta.setCriterioAggiudicazione(
					this.getMaps().get(InterceptorEncodedData.LISTA_TIPI_AGGIUDICAZIONE).get(docOfferta.getCriterioAggiudicazione()));
		} else {
			docOfferta.setCriterioAggiudicazione(docOfferta.getCriterioAggiudicazione());
		}
		
		if (offertaTecnica.getListaCriteriValutazione() != null) {			
//			for (CriterioValutazioneOffertaType criterio : offertaTecnica.getListaCriteriValutazione()) {
//				//criterio.getCodice()
//				//criterio.getDescrizione()
//				//criterio.getFormato()
//				//criterio.getListaValori()
//				//criterio.getNumeroDecimali()
//				//criterio.getTipo()
//				//criterio.getValore()
//				//criterio.getValoreMax()
//				//criterio.getValoreMin()
//			}
		}

		FirmatarioType[] firmatari = docOfferta.getFirmatarioArray();		
		for (int i = 0; i < firmatari.length; i++) {
			this.decodeFirmatario(firmatari[i]);
		}
		// solo se la mandante è libero professionista, correggi cognome e nome con i dati dell'impresa
		if(offertaTecnica.getImpresa().isLiberoProfessionista() && 
		   offertaTecnica.getImpresa().getAltriDatiAnagraficiImpresa().getNome() != null &&
		   offertaTecnica.getImpresa().getAltriDatiAnagraficiImpresa().getCognome() != null &&
		   firmatari.length > 0)
		{
			firmatari[0].setNome(offertaTecnica.getImpresa().getAltriDatiAnagraficiImpresa().getNome());
			firmatari[0].setCognome(offertaTecnica.getImpresa().getAltriDatiAnagraficiImpresa().getCognome());
		}
	}
	
//	/**
//	 * ... 
//	 */
//	private void addOffertaTecnica(XmlObject document, WizardOffertaTecnicaHelper helper) {		
//	    XmlCursor cur = null;
//	    try {
//			// completa la sezione "offerta tecnica relativa a" del documento xml
//	    	// con i dati dell'offerta economica :
//	    	// - importoBaseGara
//	    	// - importoNonSoggettoRibasso
//	    	// - importoSicurezza
//	    	// - importoOneriProgettazione
//			
//	    	File tempDir = new File(StrutsUtilities.getTempDir(
//	    			this.getRequest().getSession().getServletContext())
//	    			.getAbsolutePath());
//	    	
//	    	// recupera l'helper dell'offerta economica...
//			WizardOffertaEconomicaHelper helperEco = InitOffertaEconomicaAction.getInstance(
//					this.session, 
//					this.getCurrentUser().getUsername(), 
//					helper.getGara().getCodice(),		// codice della gara 
//					helper.getCodice(),					// codice del lotto
//					this.bandiManager,
//					this.comunicazioniManager, 
//					tempDir);
//			
//			ReportOffertaEconomicaDocument documentEco = ReportOffertaEconomicaDocument.Factory
//				.newInstance();
//			helperEco.getXmlDocument(documentEco, false, true);
//			
//			OffertaEconomicaType offertaEco = null;
//			if(documentEco != null && documentEco.getReportOffertaEconomica() != null) {
//				offertaEco = documentEco.getReportOffertaEconomica().getOfferta();
//			}			
//			if(offertaEco != null) {
//				XmlObject offertaTec = ((ReportOffertaTecnicaDocument) document)
//										.getReportOffertaTecnica().getOfferta();
//		        cur = offertaTec.newCursor();
//		        cur.toFirstChild();			
//		        cur.insertElementWithText("importoBaseGara", 
//		        		Double.toString(offertaEco.getImportoBaseGara()));
//		        cur.insertElementWithText("importoNonSoggettoRibasso", 
//		        		Double.toString(offertaEco.getImportoNonSoggettoRibasso()));
//		        cur.insertElementWithText("importoSicurezza", 
//		        		Double.toString(offertaEco.getCostiSicurezzaAziendali()));
//		        cur.insertElementWithText("importoOneriProgettazione", 
//		        		Double.toString(offertaEco.getImportoOneriProgettazione()));
//			}
//			
//	    } catch (Exception e) {
//	    	ApsSystemUtils.logThrowable(e, this, "addOffertaEconomica");
//			ExceptionUtils.manageExceptionError(e, this);
//			session.put(ERROR_DOWNLOAD, e.getMessage());
//			this.setTarget(INPUT);			
//		} finally {
//	    	if (cur != null) cur.dispose();
//	    }        
//	}
		
}
