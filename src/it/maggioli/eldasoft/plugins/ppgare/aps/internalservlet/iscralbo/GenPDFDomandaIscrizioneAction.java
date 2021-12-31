package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import it.eldasoft.sil.portgare.datatypes.AggIscrizioneImpresaElencoOpType;
import it.eldasoft.sil.portgare.datatypes.AggiornamentoIscrizioneImpresaElencoOperatoriDocument;
import it.eldasoft.sil.portgare.datatypes.DatiImpresaType;
import it.eldasoft.sil.portgare.datatypes.FirmatarioType;
import it.eldasoft.sil.portgare.datatypes.IscrizioneImpresaElencoOpType;
import it.eldasoft.sil.portgare.datatypes.IscrizioneImpresaElencoOperatoriDocument;
import it.eldasoft.sil.portgare.datatypes.ListaCategorieIscrizioneType;
import it.eldasoft.sil.portgare.datatypes.ListaDocumentiType;
import it.eldasoft.sil.portgare.datatypes.ListaPartecipantiRaggruppamentoType;
import it.eldasoft.sil.portgare.datatypes.ListaStazioniAppaltantiType;
import it.eldasoft.www.sil.WSGareAppalto.CategoriaBandoIscrizioneType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioStazioneAppaltanteType;
import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.report.GenPDFAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.FirmatarioBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlObject;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;

/**
 * Consente la generazione del PDF per la domanda di iscrizione.
 * 
 * @author stefano.sabbadin
 * @since 1.8.2
 */
public class GenPDFDomandaIscrizioneAction extends GenPDFAction {
	/**
     * UID
     */
	private static final long serialVersionUID = -6086785870328864846L;

	protected IBandiManager bandiManager;
	protected IComunicazioniManager comunicazioniManager;	
	
	private String firmatarioSelezionato;
	
	private String serialNumberMarcaBollo;
	
	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
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
	
	public String getSerialNumberMarcaBollo() {
		return serialNumberMarcaBollo;
	}

	public void setSerialNumberMarcaBollo(String serialNumberMarcaBollo) {
		this.serialNumberMarcaBollo = serialNumberMarcaBollo;
	}
	
	@Override
	public String getUrlErrori() {
		return this.urlPage + 
			   "?actionPath=" + "/ExtStr2/do/FrontEnd/IscrAlbo/openPageIscrAlboGeneraPdfRichiesta.action" + 
			   "&currentFrame=" + this.currentFrame;
	}

	/**
	 * costruttore generale del report
	 */
	public GenPDFDomandaIscrizioneAction(String reportName, String xmlRootNode) {
		super(reportName, xmlRootNode);
		// NB: in caso di report custom, i nomi di header e footer vengono
		//     costruiti a partire da "reportName"... 
		//     ma nel caso particolare delle iscrizioni si utilizzano come nomi 
		//     di header e footer il "reportName" base (DomandaIscrizioneAlbo, 
		//     AggiornamentoIscrizioneAlbo, DomandaRinnovoAlbo) 
		//     Se anche il custom report ha una suo custom header e footer 
		//     e' necessario aggiungere l'inizializzazione nel metodo
		//     reportInit()!
		this.reportHeaderName = reportName + "Header";
		this.reportFooterName = reportName + "Footer";
	} 

	/**
	 * costruttore del report 
	 */
	public GenPDFDomandaIscrizioneAction() {
		// NB: utilizza il costruttore generale vedi 
		//     GenPDFDomandaIscrizioneAction(String reportName, String xmlRootNode)
		this("DomandaIscrizioneAlbo",						 
		     "/IscrizioneImpresaElencoOperatori"); 
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

	@Override
	protected boolean reportInit() {
		boolean continua = true;
		
		WizardIscrizioneHelper helper = (WizardIscrizioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);

		try {
			if (helper == null) {
				// la sessione e' scaduta, occorre riconnettersi
				session.put(ERROR_DOWNLOAD, this.getText("Errors.sessionExpired"));
				this.setTarget(INPUT);
				continua = false;
			} else if(this.getFirmatarioSelezionato() == null && !helper.isRti()) {
				session.put(ERROR_DOWNLOAD, this.getText("Errors.firmatarioNonSelezionato"));
				this.setTarget(INPUT);
				continua = false;
			} else if (this.customConfigManager.isVisible("ISCRALBO-DOCUM", "NUMSERIEBOLLODOMANDA") && (!this.serialNumberMarcaBollo.matches("^[0-9]{14}") && !"n.d.".equals(this.serialNumberMarcaBollo.toLowerCase()))) {
				// la marca da bollo deve essere inserita e composta esattamente da 14 cifre
				session.put(ERROR_DOWNLOAD, this.getText("Errors.wrongIUBD"));
				this.setTarget(INPUT);
				continua = false;
			} else {
				// la sessione non e' scaduta, per cui proseguo regolarmente	
				if(helper.getComponentiRTI().size()==0 || !helper.isRti()) {
					FirmatarioBean firmatario = new FirmatarioBean();
					// -- FIRMATARIO NON LIBERO PROFESSIONISTA --
					if(!helper.getImpresa().isLiberoProfessionista()) {
						Integer indexFirmatario = Integer.valueOf((StringUtils.substring(
								this.firmatarioSelezionato, 
								this.firmatarioSelezionato.lastIndexOf("-") + 1, 
								this.firmatarioSelezionato.length())));
						
						if(indexFirmatario != null) {
							firmatario.setIndex(indexFirmatario);
							firmatario.setLista(StringUtils.substring(
									this.firmatarioSelezionato, 
									0, 
									this.firmatarioSelezionato.lastIndexOf("-")));
							
							boolean firmatarioFound = false;
							for(int i = 0; i < helper.getListaFirmatariMandataria().size() && !firmatarioFound; i++) {
								String firmatarioLista = helper.getListaFirmatariMandataria().get(i).getLista() + "-";
								String firmatarioIndex = helper.getListaFirmatariMandataria().get(i).getIndex().toString();
								if((firmatarioLista + firmatarioIndex).equals(this.firmatarioSelezionato)) {
									helper.setIdFirmatarioSelezionatoInLista(i);
									firmatarioFound = true;
								}
							}
						}
					}
					helper.setFirmatarioSelezionato(firmatario);
				}	
				
				// imposta la marca da bollo
				helper.setSerialNumberMarcaBollo(this.serialNumberMarcaBollo);
				
				// verifica se esiste un report custom per l'iscrizione...
				this.reportName = this.customReportManager.getActiveReport(
						helper.getIdBando(),
						PortGareSystemConstants.JASPER_REPORT_ISCRIZIONE);
			}
			
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, "reportInit");
			session.put(ERROR_DOWNLOAD,
						this.getText("Errors.unexpectedAndMessage", new String[] { e.getMessage()}));
			continua = false;
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "reportInit");
			ExceptionUtils.manageExceptionError(e, this);
			session.put(ERROR_DOWNLOAD,
						this.getText("Errors.unexpectedAndMessage", new String[] { e.getMessage() }));
			continua = false;
	    }
		
		return continua;
	}
	
	@Override
	protected void reportParametersInit(HashMap<String, Object> params) throws Exception {
		WizardIscrizioneHelper helper = (WizardIscrizioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
		if (helper != null) {
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
			params.put("SA_DENOMINAZIONE", (stazione == null ? null : stazione.getDenominazione()));
			params.put("SA_CF", (stazione == null ? null : stazione.getCodiceFiscale()));
			params.put("SA_INDIRIZZO", (stazione == null ? null : stazione.getIndirizzo()));
			params.put("SA_LOCALITA", (stazione == null ? null : stazione.getComune()));
			params.put("SA_CAP", (stazione == null ? null : stazione.getCap()));
			params.put("SA_PROVINCIA", (stazione == null ? null : stazione.getProvincia()));
			params.put("SA_CIVICO", (stazione == null ? null : stazione.getNumCivico()));
			params.put("SA_MAIL", (stazione == null ? null : stazione.getEmail()));
			params.put("SA_PEC", (stazione == null ? null : stazione.getPec()));
			params.put("SA_WEB", (stazione == null ? null : ""));
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

	@Override
	protected XmlObject generaXmlConDatiTipizzati() throws ApsException, IOException {
		WizardIscrizioneHelper helper = (WizardIscrizioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
		return generaXmlConDatiTipizzati(helper);
	}

	/**
	 * Genera il documento XML dell'iscrizione/rinnovo/aggiornamento ad elenco
	 * 
	 * @throws IOException 
	 * 
	 */
	protected XmlObject generaXmlConDatiTipizzati(
			WizardIscrizioneHelper helper) throws IOException 
	{
		XmlObject document = null;
		
		if (helper != null) {
			try {
				// FASE 1: si genera il documento XML puro
				document = helper.getXmlDocument(false);
	
				DatiImpresaType datiImpresa = null;
				ListaStazioniAppaltantiType listaSA = null;
				ListaDocumentiType listaDoc = null;
				ListaCategorieIscrizioneType listaCat = null;
				ListaPartecipantiRaggruppamentoType listaPart = null;
				FirmatarioType[] listaFirmatari = null;
				
				if (helper.isAggiornamentoIscrizione()) {
					AggIscrizioneImpresaElencoOpType d = ((AggiornamentoIscrizioneImpresaElencoOperatoriDocument) document)
							.getAggiornamentoIscrizioneImpresaElencoOperatori();
					datiImpresa = d.getDatiImpresa();
					listaSA = d.getStazioniAppaltanti();
					listaDoc = d.getDocumenti();
					listaCat = d.getCategorieIscrizione();
					listaFirmatari = d.getFirmatarioArray();
				} else {
					IscrizioneImpresaElencoOpType d = ((IscrizioneImpresaElencoOperatoriDocument) document)
							.getIscrizioneImpresaElencoOperatori();
					datiImpresa = d.getDatiImpresa();
					listaSA = d.getStazioniAppaltanti();
					listaDoc = d.getDocumenti();
					listaCat = d.getCategorieIscrizione();
					listaPart = d.getPartecipantiRaggruppamento();
					listaFirmatari = d.getFirmatarioArray();
				}
				
				// FASE 2: si sostituiscono tutti i dati tabellati con le
				// corrispondenti descrizioni e si eliminano i dati non previsti in configurazione
				this.decodeDatiImpresa(datiImpresa, helper.getImpresa().isLiberoProfessionista());
				
				this.decodePartecipantiRaggruppamento(listaPart);
					
				this.decodeStazioniAppaltanti(listaSA);
	
				CategoriaBandoIscrizioneType[] categorie = this.bandiManager
						.getElencoCategorieBandoIscrizione(helper.getIdBando(), null);			
				this.decodeCategorieBandoIscrizione(listaCat, categorie);
				
				if (listaDoc != null) {
					List<DocumentazioneRichiestaType> documentiRichiesti = this.bandiManager
							.getDocumentiRichiestiBandoIscrizione(
									helper.getIdBando(), 
									helper.getImpresa().getDatiPrincipaliImpresa().getTipoImpresa(),
									helper.isRti());				
					this.decodeDocumentiRichiesti(listaDoc, documentiRichiesti);
				}
							
				for (int i = 0; i < listaFirmatari.length; i++) {
					if(helper.getImpresa().isLiberoProfessionista()){
						listaFirmatari[i].setNome(helper.getImpresa().getAltriDatiAnagraficiImpresa().getNome());
						listaFirmatari[i].setCognome(helper.getImpresa().getAltriDatiAnagraficiImpresa().getCognome());
					}
					this.decodeFirmatario(listaFirmatari[i]);
				}
				
			} catch (ApsException e) {
				ApsSystemUtils.logThrowable(e, this, "generaXmlConDatiTipizzati");
				session.put(ERROR_DOWNLOAD,
						this.getText("Errors.unexpectedAndMessage", new String[] { e.getMessage()}));	
				this.setTarget(INPUT);
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "generaXmlConDatiTipizzati");
				session.put(ERROR_DOWNLOAD,
							this.getText("Errors.unexpectedAndMessage", new String[] { e.getMessage()}));	
				this.setTarget(INPUT);
			}
		}
		
		return document;		
	}

}
