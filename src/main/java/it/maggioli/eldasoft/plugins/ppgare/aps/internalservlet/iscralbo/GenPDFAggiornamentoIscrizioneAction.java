package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlObject;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.exception.ApsSystemException;

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
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.FirmatarioBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

/**
 * Consente la generazione del PDF per l'aggiornamento dell'iscrizione.
 * 
 * @author 
 * @since 
 */
public class GenPDFAggiornamentoIscrizioneAction extends GenPDFDomandaIscrizioneAction {
	/**
     * UID
     */
	private static final long serialVersionUID = -6198876024571416287L;
	
	
	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}
	
	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}


	@Override
	public String getUrlErrori() {
		return this.urlPage + 
			   "?actionPath=" + "/ExtStr2/do/FrontEnd/IscrAlbo/openPageIscrAlboGeneraPdfRichiesta.action" + 
			   "&currentFrame=" + this.currentFrame;
	}

	/**
	 * costruttore del report 
	 */	
	public GenPDFAggiornamentoIscrizioneAction() {
		super("AggiornamentoIscrizioneAlbo",						 
			  "/AggiornamentoIscrizioneImpresaElencoOperatori"); 
	} 
	
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
		
		try {
			WizardIscrizioneHelper helper = (WizardIscrizioneHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
			if (helper == null) {
				// la sessione e' scaduta, occorre riconnettersi
				session.put(ERROR_DOWNLOAD, this.getText("Errors.sessionExpired"));
				this.setTarget(INPUT);
				continua = false; 
			} else if(this.getFirmatarioSelezionato() == null && !helper.isRti()){
				session.put(ERROR_DOWNLOAD, this.getText("Errors.firmatarioNonSelezionato"));
				this.setTarget(INPUT);
				continua = false;			
			} else {
				// la sessione non e' scaduta, per cui proseguo regolarmente
				
				if(helper.getComponentiRTI().size() == 0 || !helper.isRti()) {
					FirmatarioBean firmatario = new FirmatarioBean();
					// FIRMATARIO NON LIBERO PROFESSIONISTA
					if(!helper.getImpresa().isLiberoProfessionista()) {
						Integer indexFirmatario = Integer.valueOf(StringUtils.substring(
								this.getFirmatarioSelezionato(), 
								this.getFirmatarioSelezionato().lastIndexOf("-") + 1, 
								this.getFirmatarioSelezionato().length()));
						
						if(indexFirmatario != null) {
							firmatario.setIndex(indexFirmatario);
							firmatario.setLista(StringUtils.substring(
									this.getFirmatarioSelezionato(), 
									0, 
									this.getFirmatarioSelezionato().lastIndexOf("-")));
							boolean firmatarioFound = false;
							for(int i = 0; i < helper.getListaFirmatariMandataria().size() && !firmatarioFound; i++) {
								String firmatarioLista = helper.getListaFirmatariMandataria().get(i).getLista() + "-";
								String firmatarioIndex = helper.getListaFirmatariMandataria().get(i).getIndex().toString();
								if((firmatarioLista + firmatarioIndex).equals(this.getFirmatarioSelezionato())) {
									helper.setIdFirmatarioSelezionatoInLista(i);
									firmatarioFound = true;
								}
							}
						}
					}
					helper.setFirmatarioSelezionato(firmatario);
				}	
				
				// verifica se esiste un report custom per l'aggiornamento iscrizione...			
				this.reportName = this.customReportManager.getActiveReport(
						helper.getIdBando(),
						PortGareSystemConstants.JASPER_REPORT_AGG_ISCRIZIONE);							
			}
		} catch (ApsSystemException e) {
			ApsSystemUtils.logThrowable(e, this, "reportInit");
			session.put(ERROR_DOWNLOAD,
						this.getText("Errors.unexpectedAndMessage", new String[] { e.getMessage() }));
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
			params.put("DATANULLAOSTAANTIMAFIA", 
						this.customConfigManager.isVisible("IMPRESA-DATIULT-CCIAA", "DATANULLAOSTAANTIMAFIA") ? 1 : 0);
			params.put("ABILITAZIONEPREVENTIVA", 
					    this.customConfigManager.isVisible("IMPRESA-DATIULT-SEZ", "ABILITAZIONEPREVENTIVA") ? 1 : 0);
			params.put("ZONEATTIVITA", 
						this.customConfigManager.isVisible("IMPRESA-DATIULT", "ZONEATTIVITA") ? 1 : 0);
			params.put("ISCRELENCHIDL189", this.customConfigManager
						.isVisible("IMPRESA-DATIULT", "ISCRELENCHIDL189-2016") ? 1 : 0);
			params.put("RATINGLEGALITA", this.customConfigManager
						.isVisible("IMPRESA-DATIULT", "RATINGLEGALITA") ? 1 : 0);
			params.put("ELENCO", helper.getDescBando());
			params.put("LIBERO_PROFESSIONISTA", helper.getImpresa().isLiberoProfessionista());
			params.put("RTI", helper.isRti());
			params.put("CONSORZIO", helper.getImpresa().isConsorzio());
			params.put("IMPRESA_SINGOLA", (!helper.isRti() && !helper.getImpresa().isConsorzio()));
			
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
		}
	}

	@Override
	protected void reportCompleted() {
		if (SUCCESS.equals(this.getTarget())) {
			WizardIscrizioneHelper helper = (WizardIscrizioneHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
			if (helper != null) {
				// aggiungi il pdf generato all'elenco dei file dell'helper
				//helper.getDocumenti().getPdfGenerati().add(pdf);
			}
		}
	}

	@Override
	protected XmlObject generaXmlConDatiTipizzati() throws ApsException, IOException {
		XmlObject document = null;
		
		WizardIscrizioneHelper helper = (WizardIscrizioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);	
		if (helper != null) {
			// FASE 1: si genera il documento XML puro
			document = helper.getXmlDocument(false);

			DatiImpresaType datiImpresa = null;
			ListaStazioniAppaltantiType listaSA = null;
			ListaDocumentiType listaDoc = null;
			ListaCategorieIscrizioneType listaCat = null;
			ListaPartecipantiRaggruppamentoType listaPart = null;
			FirmatarioType[] listaFirmatari = null;
			
			if (helper.isAggiornamentoIscrizione()) {
				// aggiornamento iscrizione elenco
				AggIscrizioneImpresaElencoOpType d = ((AggiornamentoIscrizioneImpresaElencoOperatoriDocument) document)
						.getAggiornamentoIscrizioneImpresaElencoOperatori();
				datiImpresa = d.getDatiImpresa();
				listaSA = d.getStazioniAppaltanti();
				listaDoc = d.getDocumenti();
				listaCat = d.getCategorieIscrizione();				
				listaFirmatari = d.getFirmatarioArray();				
			} else {
				// iscrizione elenco
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
			// corrispondenti descrizioni e si eliminano i dati non previsti 
			// in configurazione			
			decodeDatiImpresa(datiImpresa, helper.getImpresa().isLiberoProfessionista());
			
			decodePartecipantiRaggruppamento(listaPart);
				
			decodeStazioniAppaltanti(listaSA);

			if (listaCat != null) {
				CategoriaBandoIscrizioneType[] categorie = bandiManager
					.getElencoCategorieBandoIscrizione(helper.getIdBando(), null);
				decodeCategorieBandoIscrizione(listaCat, categorie);
			}
			
			if (listaDoc != null) {
				List<DocumentazioneRichiestaType> documentiRichiesti = bandiManager
						.getDocumentiRichiestiBandoIscrizione(
								helper.getIdBando(), 
								helper.getImpresa().getDatiPrincipaliImpresa().getTipoImpresa(),
								helper.isRti());				
				decodeDocumentiRichiesti(listaDoc, documentiRichiesti);
			}
			
			if(listaFirmatari != null) {
				for (int i = 0; i < listaFirmatari.length; i++) {
					if(helper.getImpresa().isLiberoProfessionista()){
						listaFirmatari[i].setNome(helper.getImpresa().getAltriDatiAnagraficiImpresa().getNome());
						listaFirmatari[i].setCognome(helper.getImpresa().getAltriDatiAnagraficiImpresa().getCognome());
					}
					decodeFirmatario(listaFirmatari[i]);
				}
			} 
			
////***DEBUG***
//if(true) {			
//	File fn = new File("C:\\temp\\AggiornamentoIscrizioneImpresaElencoOperatori.xml");		
//	document.save(fn);
//}
		}
		
		return document;
	}

}
