package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet;

import com.agiletec.aps.system.ApsSystemUtils;
import it.eldasoft.sil.portgare.datatypes.BustaEconomicaDocument;
import it.eldasoft.sil.portgare.datatypes.BustaEconomicaType;
import it.eldasoft.sil.portgare.datatypes.DocumentazioneBustaDocument;
import it.eldasoft.sil.portgare.datatypes.DocumentazioneBustaType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.VoceDettaglioOffertaType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.FirmatarioBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.WizardDocumentiBustaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.WizardOffertaEconomicaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;


public class BustaEconomica extends BustaDocumenti {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 2321953026200082226L;

		
	private WizardOffertaEconomicaHelper helper;

	public WizardOffertaEconomicaHelper getHelper() {
		return this.helper;
	}
	
	public void setHelper(WizardOffertaEconomicaHelper helper) {
		this.helper = helper;
	}

	@Override
	public WizardDocumentiBustaHelper getHelperDocumenti() {
		if(this.helper != null) {
			return this.helper.getDocumenti();
		} else {
			return super.getHelperDocumenti();
		}
	}

	@Override
	public void setHelperDocumenti(WizardDocumentiBustaHelper helperDocumenti) {
		if(this.helper != null) {
			this.helper.setDocumenti(helperDocumenti);
		} else {
			super.setHelperDocumenti(helperDocumenti);
		}
	}
	
	
	/**
	 * costruttore
	 * @throws Throwable 
	 */
	public BustaEconomica(GestioneBuste buste) throws Throwable {
		super(buste, 
			  ComunicazioneFlusso.RICHIESTA_TIPO_BUSTA_ECONOMICA, 
			  BUSTA_ECONOMICA);
		
		this.soloUploadDocumenti = !this.gestioneBuste.getDettaglioGara().getDatiGeneraliGara().isOffertaTelematica();
		
		this.helperDocumenti = null;
		this.helper = null;
		//this.initHelper();
	}

	/**
	 * invia la busta al servizio 
	 */
	@Override
	public boolean send(String stato) throws Throwable {
		GestioneBuste.traceLog("BustaEconomica.send");
		boolean continua = false;
		
		if(this.helper == null) {
			// busta solo documenti
			//XmlObject doc = this.getBustaDocument();
			
			continua = super.send(stato);
			
		} else {
			// busta economica
			// copia i firmatari della busta nella busta di riepilogo...
			//this.helper.copiaUltimiFirmatariInseriti2Busta(this.gestioneBuste.getBustaRiepilogo().getHelper());
			this.gestioneBuste.getBustaRiepilogo().getHelper().memorizzaUltimiFirmatariInseriti(this.helper.getComponentiRTI());

			continua = super.send(
					this.helper.getXmlDocument(BustaEconomicaDocument.Factory.newInstance(), true, false),
					this.helper.getDocumenti(),
					stato);
		}
		
		return continua;
	}

	/**
	 * invia la busta al servizio in stato BOZZA (1)
	 */
	@Override
	public boolean send() throws Throwable {
		return this.send(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
	}
	
	/**
	 * inizializza un nuovo helper associato alla busta  
	 * @throws Throwable 
	 */
	@Override
	protected boolean initHelper() throws Throwable {
		GestioneBuste.traceLog("BustaEconomica.initHelper");
		super.helperDocumenti = null;
		this.helper = null;
		
		XmlObject doc = this.getBustaDocument();
		boolean soloDoc = this.soloUploadDocumenti || (doc instanceof DocumentazioneBustaDocument);

		if(this.getOEPV()) {
			soloDoc = false;
		}
		
		if(soloDoc) {
			// utilizza l'helper ereditato...
			super.initHelper();	
		} else {
			// utilizza l'helper per l'offerta economica
			// ricrea un nuovo helper per l'offerta economica...
			this.helper = new WizardOffertaEconomicaHelper();
			this.initHelperOfferta();
			//this.helperDocumenti = this.helper.getDocumenti();
		}
		
		return (this.helperDocumenti != null || this.helper != null);
	}

	/**
	 * Restituisce se la busta ha la gestione OEPV 
	 */
	@Override
	public boolean getOEPV() {
		return this.getOEPV(CRITERIO_ECONOMICO);
	}
	
	/**
	 * restituisce il documento xml associato alla busta
	 */	
	@Override
	public XmlObject getBustaDocument() throws XmlException {
		XmlObject doc = null;
		
		DettaglioGaraType dettGara = this.gestioneBuste.getDettaglioGara();
		
		if(dettGara.getDatiGeneraliGara().isOffertaTelematica()) {
			doc = this.getBustaEconomicaDocument();
		} else {
			doc = BustaDocumenti.getBustaDocument(this.comunicazioneFlusso.getComunicazione());
		}
		return doc; 
	}

	/**
	 * restituisce il documento XML associato alla busta
	 * @throws XmlException 
	 */
	private BustaEconomicaDocument getBustaEconomicaDocument() throws XmlException {
		BustaEconomicaDocument doc = null;
		String xml = this.comunicazioneFlusso.getXmlDoc();
		if(StringUtils.isNotEmpty(xml)) {
			try {
				doc = BustaEconomicaDocument.Factory.parse(xml);
			} catch(XmlException e) {
//				doc = this.convertiBustaEconomica(xml);
				
				// converti un xml DocumentazioneBusta in BustaEconomica (PORTAPPALT-188)
				// formato xml errato... converti nel formato corretto !!!
				ApsSystemUtils.getLogger().warn("Conversione formato busta economica " + this.codiceGara);			
				
				DocumentazioneBustaDocument documento = DocumentazioneBustaDocument.Factory.parse(xml);
				DocumentazioneBustaType bustaSrc = documento.getDocumentazioneBusta();
				
				doc = BustaEconomicaDocument.Factory.newInstance();
				BustaEconomicaType bustaDst = doc.addNewBustaEconomica();
				bustaDst.addNewOfferta();
				bustaDst.addNewDocumenti();
				bustaDst.addNewFirmatario();
				
				if(bustaSrc != null && bustaDst != null) {
					// converti da documenti busta a busta tecnica...
					bustaDst.setDataPresentazione(bustaSrc.getDataPresentazione());
					bustaDst.setDocumenti(bustaSrc.getDocumenti());
				}
			}
		}
		return doc; 
	}

	/**
	 * inizializza l'helper dell'offerta economica 
	 * @throws Throwable 
	 */
	private void initHelperOfferta() throws Throwable {
		GestioneBuste.traceLog("BustaEconomica.initHelperOfferta");
		
		IBandiManager bandiManager = this.gestioneBuste.getBandiManager();
    	WizardPartecipazioneHelper partecipazione = this.gestioneBuste.getBustaPartecipazione().getHelper();
		
    	String codice = ( !partecipazione.isPlicoUnicoOfferteDistinte() ? this.codiceGara : this.codiceLotto );
		
		boolean garaLotti = (StringUtils.isNotEmpty(this.codiceGara) && StringUtils.isNotEmpty(this.codiceLotto));
		
		DettaglioGaraType dettGara = this.gestioneBuste.getDettaglioGara();
		if(garaLotti) {
			dettGara = bandiManager.getDettaglioGara(this.codiceLotto);
		}

		Long numDecimaliRibasso = this.gestioneBuste.getNumeroDecimaliRibasso();

		// recupera da BO le voci di dettaglio e le voci non soggette a ribasso 
		List<VoceDettaglioOffertaType> vociDettaglio = bandiManager
			.getVociDettaglioOfferta(codice);				// codice gara o codice lotto 
		List<VoceDettaglioOffertaType> vociDettaglioNoRibasso = bandiManager
			.getVociDettaglioOffertaNoRibasso(codice);		// codice gara o codice lotto
		
		// inzializza l'helper... 
		if(this.helper.getDocumenti().getChiaveCifratura() == null) {
			this.helper.getDocumenti().setChiaveCifratura(bandiManager.getChiavePubblica(
					this.codiceGara, 
					PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA));
		}
		
		if(this.gestioneBuste.getOperazione() >= 0) {
			this.helper.getDocumenti().setOperazione(this.gestioneBuste.getOperazione());
		}

		this.helper.setGara(dettGara.getDatiGeneraliGara());
//???	//this.helper.getGara().setCodice(this.codiceGara);
		if(garaLotti) {
			this.helper.setCodice(this.codiceLotto);
		} else {
			this.helper.setCodice(this.codiceGara);
		}
		this.helper.setNumDecimaliRibasso(numDecimaliRibasso);
		this.helper.setStazioneAppaltanteBando(dettGara.getStazioneAppaltante());
		this.helper.setProgressivoOfferta(this.progressivoOfferta);
		this.helper.setEsistonoVociDettaglioOffertaPrezzi(vociDettaglio.size() > 0);
		this.helper.setVociDettaglio(vociDettaglio);
		this.helper.setVociDettaglioNoRibasso(vociDettaglioNoRibasso);
		if (this.helper.isEsistonoVociDettaglioOffertaPrezzi()) {
			// si crea il set di prezzi e importi da valorizzare
			this.helper.setPrezzoUnitario(new Double[vociDettaglio.size()]);
			this.helper.setImportoUnitario(new Double[vociDettaglio.size()]);
			this.helper.setAttributiAggiuntivi(bandiManager.getAttributiAggiuntiviOfferta(codice));
		}

		// nel caso di voci di dettaglio solo sicurezza o non
		// soggetto a ribasso il prezzo unitario e' fissato da
		// backoffice e non editabile
		for (int i = 0; i < this.helper.getVociDettaglioNoRibasso().size(); i++) {
			VoceDettaglioOffertaType voce = this.helper.getVociDettaglioNoRibasso().get(i);
			if (voce.getPrezzoUnitario() != null) {
				Double importoUni = new Double( voce.getQuantita() * voce.getPrezzoUnitario() );
				BigDecimal bdImportoUnitario = BigDecimal.valueOf(importoUni).setScale(5, BigDecimal.ROUND_HALF_UP);
				voce.setImportoUnitario(bdImportoUnitario.doubleValue());
			}
		}

		// inizializza le voci soggette a ribasso
		boolean negoziata8 = ("8".equals(dettGara.getDatiGeneraliGara().getIterGara()));
		
		for (int i = 0; i < this.helper.getVociDettaglio().size(); i++) {
			VoceDettaglioOffertaType voce = this.helper.getVociDettaglio().get(i);

			// nel caso iter gara = 8, 
			// inizializza le date di consegna offerta solo se non esiste ancora la comunicazione
			if(negoziata8 && this.comunicazioneFlusso.getId() <= 0) {
				if (voce.getDataConsegnaOfferta() == null) {
					voce.setDataConsegnaOfferta( voce.getDataConsegnaRichiesta() );
				}
			}
		}

		// aggiorna i criteri di valutazione in caso di OEPV
		if( dettGara.getDatiGeneraliGara().isProceduraTelematica() &&
			dettGara.getDatiGeneraliGara().isOffertaTelematica() &&
			dettGara.getDatiGeneraliGara().getModalitaAggiudicazione() == 6 &&
			(dettGara.getListaCriteriValutazione() != null && dettGara.getListaCriteriValutazione().length > 0)) 
		{
			this.helper.setListaCriteriValutazione(
					Arrays.asList(dettGara.getListaCriteriValutazione()) );
		}

		// legge i dati dell'impresa, che sicuramente ci sono perche' li ho
		// caricati all'ingresso in gestione buste
		this.helper.setImpresa(this.gestioneBuste.getImpresa());

		// calcolo gli step della navigazione
		this.helper.fillStepsNavigazione();

		// nella gara a lotto unico i documenti stanno nel lotto, nella gara
		// ad offerta unica stanno nella gara
		// NB: per le busta economica il documento "Offerta economica"
		//     viene generato sempre per la gara ed è quindi visibile 
		//     anche per tutti i lotti.
		//     La ricerca dell'id offerta puo' essere effettuata nei 
		//     documenti della gara !!
		this.recuperaDocumentazioneRichiesta(this.helper.getDocumenti());
		
		for (int j = 0; j < this.documentiRichiestiDB.size() && this.helper.getIdOfferta() < 0; j++) {
			if(this.documentiRichiestiDB.get(j).getNome().equalsIgnoreCase(PortGareSystemConstants.DESCRIZIONE_DOCUMENTO_OFFERTA_ECONOMICA)
			   && (this.documentiRichiestiDB.get(j).getGenerato() != null && this.documentiRichiestiDB.get(j).getGenerato() == 1)) 
			{
				this.helper.setIdOfferta(this.documentiRichiestiDB.get(j).getId());
				break;
			}
		}

		// popola l'helper con i dati della comunicazione estratta
		List<FirmatarioBean> firmatari = this.helper.composeListaFirmatariMandataria();
		this.helper.setListaFirmatariMandataria(firmatari);
			
		// si estraggono dal B.O. i dati dell'offerta economica in bozza...
		// popola l'helper con i dati del documento XML...
		this.helper.popolaFromXml(this);
	}

//	/**
//	 * restituisce se la busta e' gia' stata inviata (anche in caso di lotti)  
//	 */
//	@Override
//	public boolean isBustaInviata() {
//		return super.isBustaInviata()
//	}
	
	/**
	 * restituisce se le buste dei lotti sono gia' state inviate
	 * NB: 
	 * per verificare lo stato delle comunicazioni di tutti i lotti
	 * potrebbe essere oneroso, perche' implica eseguire N query in base 
	 * al numero dei lotti.
	 * Per ora non si effettua il controllo!
	 */
	public boolean isBusteLottiInviate() {
//		List<String> lotti = this.gestioneBuste.getBustaPartecipazione().getLottiAttivi();
//		if(lotti != null) {
//			inviata = true;
//			
//			// verifica se ci sono comunicazione in stato DA PROCESSARE (5)...
//			List<String> stati = new ArrayList<String>();
//			stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
//			
//			for(int i = 0; i < lotti.size(); i++) {
//				DettaglioComunicazioneType dettCom = this.comunicazioneFlusso.find(
//					this.username, 
//					lotti.get(i), 
//					this.comunicazioneFlusso.getTipoComunicazione(), 
//					stati);
//				if(dettCom == null) {
//					inviata = false;
//					break;
//				}
//			}
//		}
		return false;
	}

}
