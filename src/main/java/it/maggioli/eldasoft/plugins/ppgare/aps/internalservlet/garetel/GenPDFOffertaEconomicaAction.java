package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import it.eldasoft.sil.portgare.datatypes.AttributoGenericoType;
import it.eldasoft.sil.portgare.datatypes.ComponenteDettaglioOffertaType;
import it.eldasoft.sil.portgare.datatypes.FirmatarioType;
import it.eldasoft.sil.portgare.datatypes.ImpresaType;
import it.eldasoft.sil.portgare.datatypes.ListaComponentiOffertaType;
import it.eldasoft.sil.portgare.datatypes.OffertaEconomicaType;
import it.eldasoft.sil.portgare.datatypes.ReportOffertaEconomicaDocument;
import it.eldasoft.sil.portgare.datatypes.ReportOffertaEconomicaType;
import it.eldasoft.sil.portgare.datatypes.StazioneAppaltanteType;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.www.sil.WSGareAppalto.AttributoAggiuntivoOffertaType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioStazioneAppaltanteType;
import it.eldasoft.www.sil.WSGareAppalto.StazioneAppaltanteBandoType;
import it.eldasoft.www.sil.WSGareAppalto.VoceDettaglioOffertaType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaEconomica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.report.GenPDFAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.NtpManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.FirmatarioBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@FlussiAccessiDistinti({ EFlussiAccessiDistinti.OFFERTA_GARA })
public class GenPDFOffertaEconomicaAction extends GenPDFAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 5334802606290489849L;
	
	protected IBandiManager bandiManager;
	protected NtpManager ntpManager;
	protected IComunicazioniManager comunicazioniManager;
	protected IEventManager eventManager;
	
	@Validate(EParamValidation.FIRMATARIO)
	protected String firmatarioSelezionato;
	@Validate(EParamValidation.PASS_OE)	
	protected String passoe;

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
	
	public String getFirmatarioSelezionato() {
		return firmatarioSelezionato;
	}

	public void setFirmatarioSelezionato(String firmatarioSelezionato) {
		this.firmatarioSelezionato = firmatarioSelezionato;
	}
	
	@Override
	public String getUrlErrori() {
		return this.urlPage +
				"?actionPath=/ExtStr2/do/FrontEnd/GareTel/openPageOffTelScaricaOfferta.action" +
				"&currentFrame=" + this.currentFrame;
	}

	public String getPassoe() {
		return passoe;
	}

	public void setPassoe(String passoe) {
		this.passoe = passoe;
	}

	/**
	 * costruttore del report 
	 */
	public GenPDFOffertaEconomicaAction() {
		super("OffertaEconomica",
		      "/ReportOffertaEconomica");
	}
	
	/**
	 * costruttore per le classi figlie
	 */
	public GenPDFOffertaEconomicaAction(String reportName, String xmlRootNode) {
		super(reportName, xmlRootNode);
	}
		
	@Override
	protected boolean reportInit() {
		boolean continua = true;
		
		GestioneBuste buste = GestioneBuste.getFromSession();
		WizardOffertaEconomicaHelper helper = buste.getBustaEconomica().getHelper();
		RiepilogoBusteHelper bustaRiepilogativa = buste.getBustaRiepilogo().getHelper();
				
		try {
			if (helper == null) {
				// la sessione e' scaduta, occorre riconnettersi
				session.put(ERROR_DOWNLOAD, this.getText("Errors.sessionExpired"));
				this.setTarget(INPUT);
				continua = false;
			} else if (this.firmatarioSelezionato == null && helper.getComponentiRTI().size() == 0) {
				session.put(ERROR_DOWNLOAD, this.getText("Errors.firmatarioNotSet"));
				this.setTarget(INPUT);
				continua = false;
			} else if (this.customConfigManager.isVisible("GARE-DOCUMOFFERTA", "PASSOE") && StringUtils.isEmpty(this.passoe)) {
				// il PASSOE deve essere inserito e composto al massimo da 30 caratteri
				session.put(ERROR_DOWNLOAD, this.getText("Errors.requiredstring", new String[] { this.getTextFromDB("passoe") }));
				this.setTarget(INPUT);
				continua = false;
			} if(buste.isConcorsoProgettazionePubblico() || buste.isConcorsoProgettazioneRiservato()) {
				// per i concorsi di progettazione il PDF della busta economica non e' previsto
				session.put(ERROR_DOWNLOAD, this.getText("Errors.sessionExpired"));
				this.setTarget(INPUT);
				continua = false;
			} else {
				FirmatarioBean firmatario = null;
				
				if (helper.getComponentiRTI().size() == 0) {
					firmatario = new FirmatarioBean();
					
					// -- FIRMATARIO NON LIBERO PROFESSIONISTA --
					if (!helper.getImpresa().isLiberoProfessionista()) {
						firmatario.setIndex(Integer.valueOf((StringUtils.substring(
								this.firmatarioSelezionato,
								this.firmatarioSelezionato.lastIndexOf("-") + 1,
								this.firmatarioSelezionato.length()))));
						firmatario.setLista(StringUtils.substring(
								this.firmatarioSelezionato, 0,
								this.firmatarioSelezionato.lastIndexOf("-")));
						
						boolean firmatarioFound = false;
						for (int i = 0; i < helper.getListaFirmatariMandataria().size() && !firmatarioFound; i++) {
							String firmatarioLista = helper
									.getListaFirmatariMandataria().get(i).getLista()
									+ "-";
							String firmatarioIndex = helper
									.getListaFirmatariMandataria().get(i)
									.getIndex().toString();
							if ((firmatarioLista + firmatarioIndex).equals(this.firmatarioSelezionato)) {
								helper.setIdFirmatarioSelezionatoInLista(i);
								firmatarioFound = true;
							}
						}
					}
					helper.setFirmatarioSelezionato(firmatario);
				} else {
					// all'interno della stessa sessione di lavoro si passa da 
					// una rilettura di FS11R senza RTI a imposizione RTI  
					// => genero la lista degli ultimi firmatari
					
					// Memorizzo nella FS11R l'ultimo set di firmatari inseriti, 
					// per ripresentalo nei lotti successivi 
					bustaRiepilogativa.memorizzaUltimiFirmatariInseriti(helper.getComponentiRTI());
				}
				
				// imposta "passoe"
				helper.setPassoe(this.passoe);
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
		
		WizardOffertaEconomicaHelper helper = GestioneBuste.getBustaEconomicaFromSession().getHelper();
		WizardPartecipazioneHelper partecipazioneHelper = GestioneBuste.getPartecipazioneFromSession().getHelper();		
		RiepilogoBusteHelper bustaRiepilogativa = GestioneBuste.getBustaRiepilogoFromSession().getHelper();

		if(helper != null && partecipazioneHelper != null) {
			WizardDatiImpresaHelper datiImpresaHelper = helper.getImpresa();
			
			boolean isRTI = (helper.getComponentiRTI().size() > 0);

			params.put("LIBERO_PROFESSIONISTA", datiImpresaHelper.isLiberoProfessionista());
			params.put("TIMESTAMP", UtilityDate.convertiData(this.ntpManager.getNtpDate(), 
														     UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
			params.put("IS_RTI", isRTI);
			params.put("DENOMINAZIONE_RTI", partecipazioneHelper.getDenominazioneRTI());
			params.put("IMPNORIBASSO", helper.getGara().getImportoNonSoggettoRibasso());
			params.put("IMPSICUREZZA", helper.getGara().getImportoSicurezza());
			params.put("SICINC", helper.getGara().isOffertaComprensivaSicurezza());
			params.put("IMPONERIPROGETT", helper.getGara().getImportoOneriProgettazione());
			params.put("NOME_CLIENTE", (String) this.appParamManager.getConfigurationValue(AppParamManager.NOME_CLIENTE));
				
			if(partecipazioneHelper.isPlicoUnicoOfferteDistinte()){
				params.put("CODICE_INTERNO_LOTTO", bustaRiepilogativa.getListaCodiciInterniLotti().get(helper.getCodice()));
				params.put("OGGETTO_LOTTO", bustaRiepilogativa.getBusteEconomicheLotti().get(helper.getCodice()).getOggetto());
			}
			params.put("PUNTIORDINANTIVISIBILI", this.customConfigManager.isVisible("GARE-DOCUMOFFERTA", "PUNTIORDIISTR"));
			params.put("ITER_GARA", helper.getGara().getIterGara());
			params.put("PERCENTUALEMANODOPERAVISIBILE", helper.isPercentualeManodoperaVisible());
			params.put("TIPORIBASSO", helper.getGara().getTipoRibasso());

			// in caso di ribasso pesato 
			// calcola il ribasso equivalente come sommatoria dei ribassi pesati
			double ribassoEquivalente = 0.0;
			if(helper.getGara().getTipoRibasso() != null && helper.getGara().getTipoRibasso() == 3 && 
			   helper.getVociDettaglio() != null) 
			{
				for(VoceDettaglioOffertaType item : helper.getVociDettaglio()) {
					if(item.getRibassoPesato() != null) {
						ribassoEquivalente += item.getRibassoPesato().doubleValue();
					}
				}
			}
			params.put("RIBASSOEQUIVALENTE", ribassoEquivalente);
			params.put("RIBASSOEQUIVALENTELETTERE", UtilityNumeri.getDecimaleInLinguaItaliana(ribassoEquivalente, 1, 
													helper.getNumDecimaliRibasso().intValue()));
			params.put("ACCORDOQUADRO", helper.getGara().isAccordoQuadro());
			params.put("CODICECNEL", partecipazioneHelper.getCodiceCNEL());
		}
	}

	@Override
	protected void reportCompleted() {
		try {
			GestioneBuste buste = GestioneBuste.getFromSession();
			BustaEconomica bustaEco = buste.getBustaEconomica();
			WizardOffertaEconomicaHelper helper = bustaEco.getHelper();
			RiepilogoBusteHelper bustaRiepilogativa = buste.getBustaRiepilogo().getHelper();
			
			if(SUCCESS.equals(this.getTarget()) && 
			   helper != null && bustaRiepilogativa != null) {
				// si inserisce una referenza nel bean in sessione per la
				// rimozione del file alla rimozione del bean dalla sessione
				//helper.getPdfGenerati().add(pdf);
	
				// si marca che la rigenerazione del pdf non e' necessaria e
				// si setta l'uuid del file generato
				helper.setPdfUUID(this.uuid);
				helper.setRigenPdf(false);
	
				// si elimina l'eventuale precedente offerta allegata
				if (helper.getDocumenti().isDocOffertaPresente()) {
					helper.deleteDocumentoOffertaEconomica(this, this.eventManager);
				}
	
				// salva la comunicazione in bozza in modo da salvare il CRC
				// e l'eventuale eliminazione del PDF offerta precedente
				bustaEco.send(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
				
				helper.getDocumenti().setDatiModificati(false);
				helper.setDatiModificati(false);
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
		WizardOffertaEconomicaHelper helper = GestioneBuste.getBustaEconomicaFromSession().getHelper();
		return this.generaXmlConDatiTipizzati(helper);
	}
	
	/**
	 * Generazione del documento xml contentente i dati dell'offerta economica 
	 */
	protected XmlObject generaXmlConDatiTipizzati(
			WizardOffertaEconomicaHelper helper) throws Exception 
	{
		XmlObject document = null;
		
		if(helper != null) {
			WizardDatiImpresaHelper datiImpresaHelper = helper.getImpresa();
			
			FirmatarioBean firmatario = null;
			if (helper.getComponentiRTI().size() == 0) {
				firmatario = helper.getFirmatarioSelezionato();
			} 
			
			// FASE 1: si genera il documento XML
			ReportOffertaEconomicaDocument offerta = ReportOffertaEconomicaDocument.Factory.newInstance();

			helper.getXmlDocument(offerta, false, true);

			this.decodeVocidDettaglio(offerta);

			// si inseriscono le voci NON soggette a ribasso per inserirle nel report
			addVociNonSoggetteARibasso(offerta, helper);
			
			addStazioneAppaltante(
					bandiManager.getDettaglioStazioneAppaltante(helper
							.getStazioneAppaltanteBando().getCodice()),
							offerta.getReportOffertaEconomica(),
							helper.getStazioneAppaltanteBando());
			
			addDatiImpresa(
					datiImpresaHelper, 
					offerta.getReportOffertaEconomica().addNewDatiImpresa());

//			if(firmatario != null) {
//				//addFirmatario(firmatario,
//				//				offerta.getReportOffertaEconomica().addNewFirmatario(),
//				//				datiImpresaHelper);
//			}
			
			offerta.getReportOffertaEconomica().setNumGiorniValiditaOfferta(
					helper.getGara().getNumGiorniValiditaOfferta());

			// FASE 2: si sostituiscono tutti i dati tabellati con le
			// corrispondenti descrizioni e si eliminano i dati non previsti in
			// configurazione
			sostituzioneTabellati(
					offerta.getReportOffertaEconomica().getDatiImpresa().getImpresa(),
					offerta.getReportOffertaEconomica(),
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
	 * // decodifica in chiaro le colonne delle VociDettaglio collegate a tabellati 
	 */
	private void decodeVocidDettaglio(ReportOffertaEconomicaDocument report) {
		OffertaEconomicaType offerta = report.getReportOffertaEconomica().getOfferta();
		ListaComponentiOffertaType lavorazioni = offerta.getListaComponentiDettaglio();
		for(ComponenteDettaglioOffertaType componente : lavorazioni.getComponenteDettaglioArray()) {
			// tipo
			if(componente.isSetTipo()) {
				if (StringUtils.isNotEmpty(componente.getTipo())) {
					componente.setTipo(getMaps()
							.get(InterceptorEncodedData.LISTA_TIPOLIGIA_PRODOTTO)
							.get(componente.getTipo()));
				}
			}
			
		}
	}

	/**
	 * ... 
	 */
	private void addVociNonSoggetteARibasso(
			ReportOffertaEconomicaDocument document,
			WizardOffertaEconomicaHelper helper) 
	{
		if(helper.getVociDettaglioNoRibasso() != null) {
			for (int i = 0; i < helper.getVociDettaglioNoRibasso().size(); i++) {
				ComponenteDettaglioOffertaType lavorazione = document
						.getReportOffertaEconomica().getOfferta()
						.getListaComponentiDettaglio().addNewComponenteDettaglio();
				lavorazione.setSoggettoRibasso(false);
				lavorazione.setId(helper.getVociDettaglioNoRibasso().get(i).getId());
				lavorazione.setCodice(helper.getVociDettaglioNoRibasso().get(i).getCodice());
				lavorazione.setVoce(helper.getVociDettaglioNoRibasso().get(i).getVoce());
				lavorazione.setDescrizione(helper.getVociDettaglioNoRibasso().get(i).getDescrizione());
				lavorazione.setUnitaMisura(helper.getVociDettaglioNoRibasso().get(i).getUnitaMisura());
				lavorazione.setQuantita(helper.getVociDettaglioNoRibasso().get(i).getQuantita());
				if (helper.getVociDettaglioNoRibasso().get(i).getPrezzoUnitario() != null) {
					lavorazione.setPrezzoUnitario(helper.getVociDettaglioNoRibasso().get(i).getPrezzoUnitario());
					lavorazione.setImporto(helper.getVociDettaglioNoRibasso().get(i).getImportoUnitario());
				}
			}
		}
	}

	/**
	 * ... 
	 */
	private void sostituzioneTabellati(
			ImpresaType docImpresa,
			ReportOffertaEconomicaType docOfferta,
			Map<String, LinkedHashMap<String, String>> maps,
			WizardOffertaEconomicaHelper offertaEconomica) throws ApsException 
	{
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

		ComponenteDettaglioOffertaType[] dettaglioOfferta = docOfferta
				.getOfferta().getListaComponentiDettaglio()
				.getComponenteDettaglioArray();
		
//		WizardOffertaEconomicaHelper offertaEconomica = (WizardOffertaEconomicaHelper) 
//			this.session.get(PortGareSystemConstants.SESSION_ID_OFFERTA_ECONOMICA);
		
		Map<String, String[]> definizioneAttrAgg = new HashMap<String, String[]>();
		if (offertaEconomica.getAttributiAggiuntivi() != null) {
			for (AttributoAggiuntivoOffertaType attr : offertaEconomica.getAttributiAggiuntivi()) {
				definizioneAttrAgg.put(attr.getCodice(),
									   new String[] { attr.getDescrizione(), 
					                                  String.valueOf(attr.getTipo()) });
				if (attr.getTipo() == PortGareSystemConstants.TIPO_VALIDATORE_TABELLATO) {
					try {
						LinkedHashMap<String, String> attrValues = InterceptorEncodedData
								.parseXml(attr.getValoriAmmessi());
						maps.put(attr.getCodice(), attrValues);
					} catch (XmlException e) {
						throw new ApsException(
								"Errore in lettura dei dati tabellati per gli attributi dinamici",
								e);
					}
				}
			}
		}

		for (int m = 0; m < dettaglioOfferta.length; m++) {
			AttributoGenericoType[] attributi = dettaglioOfferta[m].getAttributoAggiuntivoArray();
			for (int n = 0; n < attributi.length; n++) {
				String codice = attributi[n].getCodice();
				if (StringUtils.isNotBlank(codice)) {
					String valore = definizioneAttrAgg.get(codice)[0];
					Integer tipoAttributo = Integer.parseInt(definizioneAttrAgg.get(codice)[1]);
					attributi[n].setCodice(valore);
					if (tipoAttributo != null) {
						switch (tipoAttributo) {
						case PortGareSystemConstants.TIPO_VALIDATORE_TABELLATO:
							attributi[n].setValoreStringa(maps
									.get(codice)
									.get(attributi[n].getValoreStringa()));
							break;
						case PortGareSystemConstants.TIPO_VALIDATORE_FLAG:
							attributi[n].setValoreStringa(maps
									.get(InterceptorEncodedData.LISTA_SINO)
									.get(attributi[n].getValoreStringa()));
							break;
						default:
							break;
						}
					}
				}
			}
		}

		FirmatarioType[] firmatari = docOfferta.getFirmatarioArray();
		for (int i = 0; i < firmatari.length; i++) {
			this.decodeFirmatario(firmatari[i]);
		}
		// solo se la mandante � libero professionista, correggi cognome e nome con i dati dell'impresa
		if(offertaEconomica.getImpresa().isLiberoProfessionista() && 
		   offertaEconomica.getImpresa().getAltriDatiAnagraficiImpresa().getNome() != null &&
		   offertaEconomica.getImpresa().getAltriDatiAnagraficiImpresa().getCognome() != null &&
		   firmatari.length > 0)
		{
			firmatari[0].setNome(offertaEconomica.getImpresa().getAltriDatiAnagraficiImpresa().getNome());
			firmatari[0].setCognome(offertaEconomica.getImpresa().getAltriDatiAnagraficiImpresa().getCognome());
		}
	}

}
