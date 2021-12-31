package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa;

import it.eldasoft.sil.portgare.datatypes.AggiornamentoAnagraficaImpresaDocument;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.report.GenPDFAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.util.HashMap;

import org.apache.xmlbeans.XmlObject;

/**
 * Consente la generazione del PDF
 * 
 * @author stefano.sabbadin
 * @since 1.2
 */
public class GenPDFDatiImpresaAction extends GenPDFAction { 
    /**
     * UID
     */
    private static final long serialVersionUID = -7877095906255185012L;

	private int from;

	@Override
	public String getUrlErrori() {
		String url = this.urlPage;
		switch (this.from) {
		case 0:
			break;
		case 1:
			url += "?actionPath=" + "/ExtStr2/do/FrontEnd/RegistrImpr/openPageRiepilogo.action" + 
			       "&currentFrame=" + this.currentFrame;
			break;
		}
		return url;
	}
	
	public GenPDFDatiImpresaAction() {
		super("DatiImpresa", 
			  "/AggiornamentoAnagraficaImpresa/datiImpresa");
		this.filename = "AnagraficaOpEconomico";
	}

	
	@Override
	protected boolean reportInit() {
		boolean continua = true;
		
		this.from = 0;
		
		WizardDatiImpresaHelper helper = (WizardDatiImpresaHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA);
		
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			session.put(ERROR_DOWNLOAD, this.getText("Errors.sessionExpired"));
			this.setTarget(INPUT);
			continua = false;
		} 
		
		return continua;
	}

	@Override
	protected void reportParametersInit(HashMap<String, Object> params) throws Exception {
		WizardDatiImpresaHelper helper = (WizardDatiImpresaHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA);
		
		params.put("EMAILRECAPITO", this.customConfigManager.isVisible("IMPRESA-DATIPRINC-RECAPITI", "MAIL") ? 1 : 0);
		params.put("DATANULLAOSTAANTIMAFIA", this.customConfigManager.isVisible("IMPRESA-DATIULT-CCIAA", "DATANULLAOSTAANTIMAFIA") ? 1 : 0);
		params.put("ABILITAZIONEPREVENTIVA", this.customConfigManager.isVisible("IMPRESA-DATIULT-SEZ", "ABILITAZIONEPREVENTIVA") ? 1 : 0);
		params.put("ZONEATTIVITA", this.customConfigManager.isVisible("IMPRESA-DATIULT", "ZONEATTIVITA") ? 1 : 0);
		params.put("ISCRELENCHIDL189", this.customConfigManager.isVisible("IMPRESA-DATIULT", "ISCRELENCHIDL189-2016") ? 1 : 0);
		params.put("RATINGLEGALITA", this.customConfigManager.isVisible("IMPRESA-DATIULT", "RATINGLEGALITA") ? 1 : 0);
		params.put("TIPOIMPRESA", helper.getDatiPrincipaliImpresa().getTipoImpresa());
		params.put("DATIULTVISIBILI", this.customConfigManager.isVisible("IMPRESA-DATIULT", "STEP") ? 1 : 0);
		params.put("INDIRIZZIVISIBILI", this.customConfigManager.isVisible("IMPRESA-INDIRIZZI", "STEP") ? 1 : 0);
		params.put("DATIULTCCIAVISIBILI", this.customConfigManager.isVisible("IMPRESA-DATIULT", "CCIAA") ? 1 : 0);
		params.put("DATIULTPREVVISIBILI", this.customConfigManager.isVisible("IMPRESA-DATIULT", "PREVIDENZA") ? 1 : 0);
		params.put("DATIULTSOAVISIBILI", this.customConfigManager.isVisible("IMPRESA-DATIULT", "SOA") ? 1 : 0);
		params.put("DATIULTISOVISIBILI", this.customConfigManager.isVisible("IMPRESA-DATIULT", "ISO") ? 1 : 0);
		params.put("DATIULTWLVISIBILI", this.customConfigManager.isVisible("IMPRESA-DATIULT", "WHITELIST") ? 1 : 0);
		params.put("DATIULTALTRIDATIVISIBILI", this.customConfigManager.isVisible("IMPRESA-DATIULT", "ALTRIDATI") ? 1 : 0);
		
	}

	@Override
	protected void reportCompleted() {
		WizardDatiImpresaHelper helper = (WizardDatiImpresaHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA);
		
		if (SUCCESS.equals(this.getTarget()) && helper != null) {
			//helper.getTempFiles().add(pdf);
		}
	}

	@Override
	protected XmlObject generaXmlConDatiTipizzati() throws Exception {
		XmlObject document = null;
		
		WizardDatiImpresaHelper helper = (WizardDatiImpresaHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA);
		
		if(helper != null) {
			// FASE 1: si genera il documento XML
			AggiornamentoAnagraficaImpresaDocument doc = (AggiornamentoAnagraficaImpresaDocument) helper
					.getXmlDocumentAggiornamentoAnagrafica();
				
			// FASE 2: si sostituiscono tutti i dati tabellati con le corrispondenti
			// descrizioni e si eliminano i dati non previsti in configurazione
			decodeDatiImpresa(
					doc.getAggiornamentoAnagraficaImpresa().getDatiImpresa(), 
					helper.isLiberoProfessionista());
			
			document = doc;
		}
		
		return document;
	}

	
//	/**
//	 * Genera il PDF dei dati anagrafici dell'impresa a partire dalla
//	 * funzionalit&agrave; di registrazione impresa.
//	 * 
//	 * @return forward dell'azione
//	 */
//	public String createPdfFromRegImpr() {
//		this.from = 1;
//		this.filename = "AnagraficaOpEconomico.pdf";
//		WizardRegistrazioneImpresaHelper helper = (WizardRegistrazioneImpresaHelper) this.session
//				.get(PortGareSystemConstants.SESSION_ID_DETT_REGISTRAZIONE_IMPRESA);
//		if (helper == null) {
//			// la sessione e' scaduta, occorre riconnettersi
//			session.put(ERROR_DOWNLOAD, this.getText("Errors.sessionExpired"));
//			this.setTarget(INPUT);
//		} else {
//			File f = this.createPdf(helper);
//			if (SUCCESS.equals(this.getTarget()))
//				helper.getTempFiles().add(f);
//		}
//
//		return this.getTarget();
//	}

//	/**
//	 * Genera il PDF dei dati anagrafici dell'impresa a partire dalla
//	 * funzionalit&agrave; di iscrizione albo fornitori (iscrizione,
//	 * completamento bozza o aggiornamento).
//	 * 
//	 * @return forward dell'azione
//	 */
//	public String createPdfFromIscrAlbo() {
//		this.from = 2;
//		this.filename = "AnagraficaOpEconomico.pdf";
//		WizardIscrizioneHelper helper = (WizardIscrizioneHelper) this.session
//				.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
//		if (helper == null) {
//			// la sessione e' scaduta, occorre riconnettersi
//			session.put(ERROR_DOWNLOAD, this.getText("Errors.sessionExpired"));
//			this.setTarget(INPUT);
//		} else {
//			File f = this.createPdf(helper.getImpresa());
//			if (SUCCESS.equals(this.getTarget()))
//				helper.getDocumenti().getPdfGenerati().add(f);
//		}
//
//		return this.getTarget();
//	}

//	/**
//	 * Genera il PDF dei dati anagrafici dell'impresa a partire dalla
//	 * funzionalit&agrave; di richiesta partecipazione ad un bando di gara,
//	 * oppure richiesta di offerta o infine comprova requisiti.
//	 * 
//	 * @return forward dell'azione
//	 */
//	public String createPdfFromRichPart() {
//		this.from = 2;
//		this.filename = "AnagraficaOpEconomico.pdf";
//		WizardPartecipazioneHelper helper = (WizardPartecipazioneHelper) this.session
//				.get(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);
//		if (helper == null) {
//			// la sessione e' scaduta, occorre riconnettersi
//			this.addActionError(this.getText("Errors.sessionExpired"));
//			this.setTarget(ERROR);
//		} else {
//			File f = this.createPdf(helper.getImpresa());
//			if (SUCCESS.equals(this.getTarget()))
//				helper.getTempFiles().add(f);
//		}
//
//		return this.getTarget();
//	}

}
