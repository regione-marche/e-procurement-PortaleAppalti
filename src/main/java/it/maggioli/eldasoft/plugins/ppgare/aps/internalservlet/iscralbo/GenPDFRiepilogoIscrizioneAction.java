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
import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.report.GenPDFAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import org.apache.xmlbeans.XmlObject;

import java.util.HashMap;
import java.util.List;

/**
 * Consente la generazione del PDF di riepilogo dei dati inseriti.
 * 
 * @author stefano.sabbadin
 * @since 1.2
 */
public class GenPDFRiepilogoIscrizioneAction extends GenPDFAction { //extends EncodedDataAction implements SessionAware, IDownloadAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7877095906255185012L;

	private IBandiManager bandiManager;
	
	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	@Override
	public String getUrlErrori() {
		return this.urlPage + 
		       "?actionPath=" + "/ExtStr2/do/FrontEnd/IscrAlbo/openPageRiepilogo.action" + 
		       "&currentFrame=" + this.currentFrame;
	}
	
	public GenPDFRiepilogoIscrizioneAction() {
		super("DatiIscrizioneAlbo", 
			  null);
	}
	
	@Override
	protected boolean reportInit() {
		boolean continua = true;
		
		WizardIscrizioneHelper helper = (WizardIscrizioneHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);

		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			session.put(ERROR_DOWNLOAD, this.getText("Errors.sessionExpired"));
			this.setTarget(INPUT);
			continua = false;
		} else {
			// imposta filename e xmlRootNode in base al tipo di riepilogo...
			if (helper.isAggiornamentoIscrizione()) {
				this.xmlRootNode = "/AggiornamentoIscrizioneImpresaElencoOperatori";
				this.filename = "DatiRichiestaAggiornamento";
			} else {
				this.xmlRootNode = "/IscrizioneImpresaElencoOperatori";
				this.filename = "DatiRichiestaIscrizione";
			}	
		}
		
		return continua;
	}

	@Override
	protected void reportParametersInit(HashMap<String, Object> params) throws Exception {
		WizardIscrizioneHelper helper = (WizardIscrizioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
		if(helper != null) {
			params.put("ELENCO", helper.getTipologia() == PortGareSystemConstants.TIPOLOGIA_ELENCO_STANDARD ? 1 : 0);
			params.put("ISCRIZIONE", !helper.isAggiornamentoIscrizione() ? 1 : 0);
			params.put("VISUALIZZA_SA", (!helper.isUnicaStazioneAppaltante() && !helper.isAggiornamentoSoloDocumenti()) ? 1 : 0);
			params.put("VISUALIZZA_CAT", (!helper.isCategorieAssenti() && !helper.isAggiornamentoSoloDocumenti()) ? 1 : 0);
			params.put("EMAILRECAPITO", this.customConfigManager.isVisible("IMPRESA-DATIPRINC-RECAPITI", "MAIL") ? 1 : 0);
			params.put("DATANULLAOSTAANTIMAFIA", this.customConfigManager.isVisible("IMPRESA-DATIULT-CCIAA", "DATANULLAOSTAANTIMAFIA") ? 1 : 0);
			params.put("ABILITAZIONEPREVENTIVA",this.customConfigManager.isVisible("IMPRESA-DATIULT-SEZ", "ABILITAZIONEPREVENTIVA") ? 1 : 0);
			params.put("ZONEATTIVITA", this.customConfigManager.isVisible("IMPRESA-DATIULT", "ZONEATTIVITA") ? 1 : 0);
			params.put("ISCRELENCHIDL189", this.customConfigManager.isVisible("IMPRESA-DATIULT", "ISCRELENCHIDL189-2016") ? 1 : 0);
			params.put("RATINGLEGALITA", this.customConfigManager.isVisible("IMPRESA-DATIULT", "RATINGLEGALITA") ? 1 : 0);
			params.put("DATIULTVISIBILI", this.customConfigManager.isVisible("IMPRESA-DATIULT", "STEP") ? 1 : 0);
			params.put("INDIRIZZIVISIBILI", this.customConfigManager.isVisible("IMPRESA-INDIRIZZI", "STEP") ? 1 : 0);
			params.put("DATIULTCCIAVISIBILI", this.customConfigManager.isVisible("IMPRESA-DATIULT", "CCIAA") ? 1 : 0);
			params.put("DATIULTPREVVISIBILI", this.customConfigManager.isVisible("IMPRESA-DATIULT", "PREVIDENZA") ? 1 : 0);
			params.put("DATIULTSOAVISIBILI", this.customConfigManager.isVisible("IMPRESA-DATIULT", "SOA") ? 1 : 0);
			params.put("DATIULTISOVISIBILI", this.customConfigManager.isVisible("IMPRESA-DATIULT", "ISO") ? 1 : 0);
			params.put("DATIULTWLVISIBILI", this.customConfigManager.isVisible("IMPRESA-DATIULT", "WHITELIST") ? 1 : 0);
			params.put("DATIULTALTRIDATIVISIBILI", this.customConfigManager.isVisible("IMPRESA-DATIULT", "ALTRIDATI") ? 1 : 0);
			params.put("DATIULTALTRIDATIVISIBILI", this.customConfigManager.isVisible("IMPRESA-DATIULT", "ALTRIDATI") ? 1 : 0);
			params.put("SOC_COOP_VISIBLE", hasToShowSocCoop(helper.getImpresa()));
		}
	}

	@Override
	protected void reportCompleted() {
		WizardIscrizioneHelper helper = (WizardIscrizioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);

		if (SUCCESS.equals(this.getTarget()) && helper != null) {
			//helper.getDocumenti().getPdfGenerati().add(pdf);
		}		
	}

	@Override
	protected XmlObject generaXmlConDatiTipizzati() throws Exception {
		WizardIscrizioneHelper helper = (WizardIscrizioneHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);

		// FASE 1: si genera il documento XML puro
		XmlObject document = helper.getXmlDocument(false);

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
//		} else if(helper.isRinnovoIscrizione()){
//			IscrizioneImpresaElencoOpType d = ((IscrizioneImpresaElencoOperatoriDocument) document).getIscrizioneImpresaElencoOperatori();			
//			datiImpresa = d.getDatiImpresa();
//			listaSA = d.getStazioniAppaltanti();
//			listaDoc = d.getDocumenti();
//			listaCat = d.getCategorieIscrizione();
//			listaPart = d.getPartecipantiRaggruppamento();
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
		decodeDatiImpresa(datiImpresa, helper.getImpresa().isLiberoProfessionista());

		decodePartecipantiRaggruppamento(listaPart);
		
		decodeStazioniAppaltanti(listaSA);
		
		CategoriaBandoIscrizioneType[] categorie = bandiManager
			.getElencoCategorieBandoIscrizione(helper.getIdBando(), null);
		decodeCategorieBandoIscrizione(listaCat, categorie);

		if (listaDoc != null) {
			List<DocumentazioneRichiestaType> documentiRichiesti = bandiManager
					.getDocumentiRichiestiBandoIscrizione(
							helper.getIdBando(), 
							helper.getImpresa().getDatiPrincipaliImpresa().getTipoImpresa(),
							helper.isRti());
			
			decodeDocumentiRichiesti(listaDoc, documentiRichiesti);
		}
		
		if(!helper.isAggiornamentoIscrizione()) {
			for (int i = 0; i < listaFirmatari.length; i++) {
				decodeFirmatario(listaFirmatari[i]);
			}
		}

		return document;
	}

	@Override
	protected void decodeDatiImpresa(DatiImpresaType datiImpresa, boolean isLiberoProfessionista) {
		super.decodeDatiImpresa(datiImpresa, isLiberoProfessionista);
		if (datiImpresa.getImpresa().getTipoSocietaCooperativa() != null) {
			try {
				datiImpresa.getImpresa().setTipoSocietaCooperativa(
						InterceptorEncodedData.get(InterceptorEncodedData.LISTA_TIPOLOGIE_SOCIETA_COOPERATIVE)
								.get(datiImpresa.getImpresa().getTipoSocietaCooperativa()));
			} catch (Exception e) {
				logger.error("Errore durante il recupero dei dati", e);
			}
		}
	}

}
