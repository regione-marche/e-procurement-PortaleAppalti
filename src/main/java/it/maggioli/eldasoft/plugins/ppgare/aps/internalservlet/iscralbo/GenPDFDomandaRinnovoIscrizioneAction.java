package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo;


import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlObject;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.exception.ApsSystemException;

import it.eldasoft.www.sil.WSGareAppalto.DettaglioStazioneAppaltanteType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.FirmatarioBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;


/**
 * Consente la generazione del PDF per la domanda di rinnovo di iscrizione.
 * 
 * @author eleonora.favaro
 * @since 1.8.2
 */
public class GenPDFDomandaRinnovoIscrizioneAction extends GenPDFDomandaIscrizioneAction { 
	/**
     * UID
     */
	private static final long serialVersionUID = -6086785870328864846L;

//	private String codice;
//	private int tipoElenco;

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}
	
	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}


	@Override
	public String getUrlErrori() {
		return this.urlPage + 
			   "?actionPath=" + "/ExtStr2/do/FrontEnd/IscrAlbo/openPageRinnovoGeneraPdfRichiesta.action" + 
			   "&currentFrame=" + this.currentFrame;
	}
	
	/**
	 * costruttore del report 
	 */	
	public GenPDFDomandaRinnovoIscrizioneAction() {
		super("DomandaRinnovoAlbo",
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
		
		try {
			WizardRinnovoHelper rinnovoHelper = (WizardRinnovoHelper) session
				.get(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);
			if (rinnovoHelper == null) {
				// la sessione e' scaduta, occorre riconnettersi
				this.addActionError(this.getText("Errors.sessionExpired"));
				// TEMPORANEAMENTE SI USA QUESTO TARGET, IN ATTESA DI UNA 
				// RISTRUTTURAZIONE A WIZARD, IN TAL CASO MODIFICARE ANCHE LA URLERRORI
				this.setTarget("homepage");
				continua = false;
			} else if(this.getFirmatarioSelezionato() == null && !rinnovoHelper.isRti()) {
				session.put(ERROR_DOWNLOAD, this.getText("Errors.firmatarioNonSelezionato"));
				this.setTarget(INPUT);
				continua = false;
			} else {
				// la sessione non e' scaduta, per cui proseguo regolarmente
				if(rinnovoHelper.getComponentiRTI().size() == 0 || !rinnovoHelper.isRti()) {
					FirmatarioBean firmatario = new FirmatarioBean();
					// -- FIRMATARIO NON LIBERO PROFESSIONISTA --
					if(!rinnovoHelper.getImpresa().isLiberoProfessionista()) {
						firmatario.setIndex(Integer.valueOf((StringUtils.substring(
								this.getFirmatarioSelezionato(), 
								this.getFirmatarioSelezionato().lastIndexOf("-") + 1, 
								this.getFirmatarioSelezionato().length()))));
						firmatario.setLista(StringUtils.substring(
								this.getFirmatarioSelezionato(), 
								0, 
								this.getFirmatarioSelezionato().lastIndexOf("-")));
						boolean firmatarioFound = false;
						for(int i = 0; i < rinnovoHelper.getListaFirmatariMandataria().size() && !firmatarioFound; i++) {
							String firmatarioLista = rinnovoHelper.getListaFirmatariMandataria().get(i).getLista() + "-";
							String firmatarioIndex = rinnovoHelper.getListaFirmatariMandataria().get(i).getIndex().toString();
							if((firmatarioLista + firmatarioIndex).equals(this.getFirmatarioSelezionato())) {
								rinnovoHelper.setIdFirmatarioSelezionatoInLista(i);
								firmatarioFound = true;
							}
						}
					}
					rinnovoHelper.setFirmatarioSelezionato(firmatario);
				} 
				
				// verifica se esiste un report custom per il rinnovo...
				this.reportName = this.customReportManager.getActiveReport(
						rinnovoHelper.getIdBando(),
						PortGareSystemConstants.JASPER_REPORT_RINNOVO);
			}
		} catch (ApsSystemException e) {
			ApsSystemUtils.logThrowable(e, this, "reportInit");
			session.put(ERROR_DOWNLOAD,
						this.getText("Errors.unexpectedAndMessage", new String[] { e.getMessage()}));
			continua = false;
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "reportInit");
			session.put(ERROR_DOWNLOAD,
						this.getText("Errors.unexpectedAndMessage", new String[] { e.getMessage()}));
			continua = false;
		}
		
		return continua;
	}
	
	@Override
	protected void reportParametersInit(HashMap<String, Object> params) throws Exception {
		WizardRinnovoHelper rinnovoHelper = (WizardRinnovoHelper) session
			.get(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);
		if (rinnovoHelper != null) {
			params.put("VISUALIZZA_SA", (!rinnovoHelper.isUnicaStazioneAppaltante() && !rinnovoHelper.isAggiornamentoSoloDocumenti()) ? 1 : 0);
			params.put("VISUALIZZA_CAT", (!rinnovoHelper.isCategorieAssenti() && !rinnovoHelper.isAggiornamentoSoloDocumenti()) ? 1 : 0);
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
			params.put("ELENCO", rinnovoHelper.getDescBando());
			params.put("LIBERO_PROFESSIONISTA", rinnovoHelper.getImpresa().isLiberoProfessionista());
			params.put("RTI", rinnovoHelper.isRti());
			
			// estrai i dati della Stazione Appaltante...
			DettaglioStazioneAppaltanteType stazione = null;
			if(rinnovoHelper.getStazioniAppaltanti() != null) {
				// se più di 1 quale prendere ?
				stazione = this.bandiManager
					.getDettaglioStazioneAppaltante(rinnovoHelper.getStazioniAppaltanti().first());
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
		WizardIscrizioneHelper rinnovoHelper = (WizardIscrizioneHelper) session
			.get(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);
		WizardIscrizioneHelper helper = getWizardIscrizioneHelper(rinnovoHelper);
		return super.generaXmlConDatiTipizzati(helper);
	}

	private WizardIscrizioneHelper getWizardIscrizioneHelper(
			WizardIscrizioneHelper rinnovoHelper) 
	{
		WizardIscrizioneHelper helper =	new WizardIscrizioneHelper();

		// NB: 
		// i set...() assegnano l'istanza del rinnovo all'iscrizione
		// quindi "componenti" e "componentiRTI" non sono cloni 
		// quindi dato che setRTI() puo' resettare "componenti" e "componentiRTI"
		// il rischio e' di pulire anche le istanze del rinnovo
		// questo dipende dal fatto che questo metodo non crea una copia 
		// del rinnovo!!!
		// setRTI() eseguita come prima assegnazione copre il problema della finta copia 
		helper.setRti(rinnovoHelper.isRti());
		
		helper.setAggiornamentoIscrizione(rinnovoHelper.isAggiornamentoIscrizione());
		helper.setAggiornamentoSoloDocumenti(rinnovoHelper.isAggiornamentoSoloDocumenti());
		helper.setAmmesseRTI(rinnovoHelper.isAmmesseRTI());
		// SEPR-47: elenco in cui sono state sostituite le categorie e non ci
		// sono corrispondenze tra categorie selezionate ed esistenti per cui si
		// va in NullPointerException
		// helper.setCategorie(rinnovoHelper.getCategorie());
		// helper.setCategorieAssenti(rinnovoHelper.isCategorieAssenti());
		// si tratta di un rinnovo, non devo portarmi dietro le categorie, anche
		// se le recupero
		helper.setCategorieAssenti(true); // si simula pertanto il fatto che non ci siano categorie
		helper.setComponenti(rinnovoHelper.getComponenti());
		helper.setComponentiRTI(rinnovoHelper.getComponentiRTI());
		helper.setDataPresentazione(rinnovoHelper.getDataPresentazione());
		helper.setDataScadenza(rinnovoHelper.getDataScadenza());
		helper.setDatiModificati(rinnovoHelper.isDatiModificati());
		helper.setDenominazioneRTI(rinnovoHelper.getDenominazioneRTI());
		helper.setDescBando(rinnovoHelper.getDescBando());
		helper.setDocumenti(rinnovoHelper.getDocumenti());
		helper.setEditRTI(rinnovoHelper.isEditRTI());
		helper.setFirmatarioSelezionato(rinnovoHelper.getFirmatarioSelezionato());
		helper.setFromBozza(rinnovoHelper.isFromBozza());
		helper.setIdBando(rinnovoHelper.getIdBando());
		helper.setIdComunicazione(rinnovoHelper.getIdComunicazione());
		helper.setIdFirmatarioSelezionatoInLista(rinnovoHelper.getIdFirmatarioSelezionatoInLista());
		helper.setImpresa(rinnovoHelper.getImpresa());
		helper.setIscrizioneDomandaVisible(rinnovoHelper.isIscrizioneDomandaVisible());
		helper.setListaFirmatariMandataria(rinnovoHelper.getListaFirmatariMandataria());
		helper.setQuotaRTI(rinnovoHelper.getQuotaRTI());
		helper.setRinnovoIscrizione(rinnovoHelper.isRinnovoIscrizione());
		helper.setSerialNumberMarcaBollo(rinnovoHelper.getSerialNumberMarcaBollo());
		helper.setStazioniAppaltanti(rinnovoHelper.getStazioniAppaltanti());
		helper.setStepNavigazione(rinnovoHelper.getStepNavigazione());
		helper.setTipoClassifica(rinnovoHelper.getTipoClassifica());
		helper.setTipologia(rinnovoHelper.getTipologia());
		helper.setUnicaStazioneAppaltante(rinnovoHelper.isUnicaStazioneAppaltante());
		return helper;
	}

}
