package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet;

import it.eldasoft.sil.portgare.datatypes.BustaTecnicaDocument;
import it.eldasoft.sil.portgare.datatypes.BustaTecnicaType;
import it.eldasoft.sil.portgare.datatypes.DocumentazioneBustaDocument;
import it.eldasoft.sil.portgare.datatypes.DocumentazioneBustaType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.FirmatarioBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.WizardDocumentiBustaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.WizardOffertaTecnicaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import com.agiletec.aps.system.ApsSystemUtils;


public class BustaTecnica extends BustaDocumenti {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 7184284819110556977L;

	
	private WizardOffertaTecnicaHelper helper;

	public WizardOffertaTecnicaHelper getHelper() {
		return helper;
	}
	
	public void setHelper(WizardOffertaTecnicaHelper helper) {
		this.helper = helper;
	}
	
	@Override
	public WizardDocumentiBustaHelper getHelperDocumenti() {
		if(this.helper != null) {
			return this.helper.getDocumenti();
		} else {
			return helperDocumenti;
		}
	}

	@Override
	public void setHelperDocumenti(WizardDocumentiBustaHelper helperDocumenti) {
		if(this.helper != null) {
			this.helper.setDocumenti(helperDocumenti);
		} else {
			this.helperDocumenti = helperDocumenti;
		}
	}


	/**
	 * costruttore
	 * @throws Throwable 
	 */
	public BustaTecnica(GestioneBuste buste) throws Throwable {
		super(buste, 
			  ComunicazioneFlusso.RICHIESTA_TIPO_BUSTA_TECNICA, 
			  BUSTA_TECNICA);
		
		this.soloUploadDocumenti = !this.getOEPV();
		
		this.helper = null;
		//this.initHelper();
	}
	
	/**
	 * invia la busta al servizio 
	 */
	@Override
	public boolean send(String stato) throws Throwable {
		GestioneBuste.traceLog("BustaTecnica.send");
		boolean continua = false;
		
		if(this.helper == null) {
			// busta solo documenti
			//XmlObject doc = this.getBustaDocument();
			
			continua = super.send(stato);
			
		} else {
			// busta tecnica
			// copia i firmatari della busta nella busta di riepilogo...
			//this.helper.copiaUltimiFirmatariInseriti2Busta(this.gestioneBuste.getBustaRiepilogo().getHelper());
			this.gestioneBuste.getBustaRiepilogo().getHelper().memorizzaUltimiFirmatariInseriti(this.helper.getComponentiRTI());
			
			continua = super.send(
					this.helper.getXmlDocument(BustaTecnicaDocument.Factory.newInstance(), true, false),
					this.helper.getDocumenti(),
					stato);
		}
			
		return continua;
	}
	
	/**
	 * invia la busta al servizio 
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
		GestioneBuste.traceLog("BustaTecnica.initHelper");
		this.helperDocumenti = null;
		this.helper = null;
		
		XmlObject doc = this.getBustaDocument();
		boolean soloDoc = this.soloUploadDocumenti || (doc instanceof DocumentazioneBustaDocument);
		
		if(this.getOEPV()) {
			soloDoc = false;
		}
		
		if(soloDoc) {
			// utilizza l'helper predefinito...
			super.initHelper();
		} else {
			// utilizza l'helper per l'offerta economica
			this.helper = new WizardOffertaTecnicaHelper();
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
		return this.getOEPV(CRITERIO_TECNICO);
	}

	/**
	 * restituisce il documento xml associato alla busta
	 */	
	@Override
	public XmlObject getBustaDocument() throws XmlException {
		XmlObject doc = null;
		
//		DettaglioGaraType dettGara = this.gestioneBuste.getDettaglioGara();
//		
//		List<CriterioValutazioneOffertaType> criteri = null;
//		if (dettGara.getListaCriteriValutazione() != null) {
//			criteri = Arrays.asList(dettGara.getListaCriteriValutazione()); 
//		}
//		
//		boolean apriOEPV = WizardOffertaTecnicaHelper.isCriteriValutazioneVisibili(
//				PortGareSystemConstants.CRITERIO_TECNICO, 
//				dettGara.getDatiGeneraliGara(),
//				criteri);
		if(this.getOEPV()) {
			doc = this.getBustaTecnicaDocument();
		} else {
			doc = BustaDocumenti.getBustaDocument(this.comunicazioneFlusso.getComunicazione());
		}
		return doc; 
	}
	
	/**
	 * restituisce il documento XML associato alla busta
	 * @throws XmlException 
	 */
	private BustaTecnicaDocument getBustaTecnicaDocument() throws XmlException {
		BustaTecnicaDocument doc = null;
		String xml = this.comunicazioneFlusso.getXmlDoc();
		if(StringUtils.isNotEmpty(xml)) {
			try {
				doc = BustaTecnicaDocument.Factory.parse(xml);
				
			} catch(XmlException e) {
//				doc = this.convertiBustaTecnica(xml);
				// converti un xml DocumentazioneBusta in BustaTecnica (PORTAPPALT-188)
				// formato xml errato... converti nel formato corretto !!!
				ApsSystemUtils.getLogger().warn("Conversione formato busta tecnica " + this.codiceGara);			
				
				DocumentazioneBustaDocument documento = DocumentazioneBustaDocument.Factory.parse(xml);
				DocumentazioneBustaType bustaSrc = documento.getDocumentazioneBusta();
				
				doc = BustaTecnicaDocument.Factory.newInstance();
				BustaTecnicaType bustaDst = doc.addNewBustaTecnica();
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
	
//	/**
//	 * 
//	 * @throws XmlException 
//	 */
//	private BustaTecnicaDocument convertiBustaTecnica(String xml) 
//		throws XmlException 
//	{
//		return documento;
//	}
	
	/**
	 * inizializza l'helper della busta tecnica 
	 * @throws Throwable 
	 */
	private void initHelperOfferta() throws Throwable {
		GestioneBuste.traceLog("BustaTecnica.initHelperOfferta");
		IBandiManager bandiManager = this.gestioneBuste.getBandiManager();
		//IComunicazioniManager comunicazioniManager = this.gestioneBuste.getComunicazioniManager();
		WizardPartecipazioneHelper partecipazione = this.gestioneBuste.getBustaPartecipazione().getHelper();
		
		boolean garaLotti = (StringUtils.isNotEmpty(this.codiceGara) && StringUtils.isNotEmpty(this.codiceLotto));
		
		// carica i dati della gara...
		DettaglioGaraType dettGara = this.gestioneBuste.getDettaglioGara();
		if(garaLotti) {
			dettGara = bandiManager.getDettaglioGara(this.codiceLotto);
		}
		
    	String codice = ( !partecipazione.isPlicoUnicoOfferteDistinte() ? this.codiceGara : this.codiceLotto );
		String codLotto = null;
		if (dettGara.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_LOTTO_UNICO) {
			codLotto = dettGara.getDatiGeneraliGara().getCodice();
		}
		
		// aggiorna i dati helper.documenti()...
		// inzializza l'helper... 
		if(this.helper.getDocumenti().getChiaveCifratura() == null) {
			this.helper.getDocumenti().setChiaveCifratura(bandiManager.getChiavePubblica(
					this.codiceGara, 
					PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA));
		}

		this.helper.getDocumenti().setOperazione(this.gestioneBuste.getOperazione());

		// recupera il numero dei decimali per il ribasso
		Long numDecimaliRibasso = this.gestioneBuste.getNumeroDecimaliRibasso();
		
		this.helper.setGara(dettGara.getDatiGeneraliGara());
		this.helper.getGara().setCodice(codiceGara);
		if(garaLotti) {
			this.helper.setCodice(codice);
		} else {
			//helper.setCodice(codiceGara);		// dovrebbe gia' essere corretto
		}
		this.helper.setNumDecimaliRibasso(numDecimaliRibasso);
		this.helper.setStazioneAppaltanteBando(dettGara.getStazioneAppaltante());
		this.helper.setProgressivoOfferta(this.progressivoOfferta);
		
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
		// NB: per le busta tecnica il documento "Offerta tecnica"
		//     in caso di gara a lotti, NON viene generato per la gara 
		//     ma per il lotto.
		//     La ricerca dell'id offerta va effettuata nei documenti
		//     della gara e nei documenti del lotto
		this.recuperaDocumentazioneRichiesta(this.helper.getDocumenti());
		
		for (int j = 0; j < this.documentiRichiestiDB.size() && this.helper.getIdOfferta() < 0; j++) {
			if(this.documentiRichiestiDB.get(j).getNome().equalsIgnoreCase(PortGareSystemConstants.DESCRIZIONE_DOCUMENTO_OFFERTA_TECNICA)
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
