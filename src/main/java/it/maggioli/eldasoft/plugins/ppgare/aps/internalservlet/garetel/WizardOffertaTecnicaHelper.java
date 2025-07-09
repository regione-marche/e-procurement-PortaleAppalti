package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import com.agiletec.apsadmin.system.BaseAction;
import it.eldasoft.sil.portgare.datatypes.AttributoGenericoType;
import it.eldasoft.sil.portgare.datatypes.BustaTecnicaDocument;
import it.eldasoft.sil.portgare.datatypes.BustaTecnicaType;
import it.eldasoft.sil.portgare.datatypes.FirmatarioType;
import it.eldasoft.sil.portgare.datatypes.ListaCriteriValutazioneType;
import it.eldasoft.sil.portgare.datatypes.ListaDocumentiType;
import it.eldasoft.sil.portgare.datatypes.OffertaTecnicaType;
import it.eldasoft.sil.portgare.datatypes.ReportOffertaTecnicaDocument;
import it.eldasoft.sil.portgare.datatypes.ReportOffertaTecnicaType;
import it.eldasoft.www.sil.WSGareAppalto.CriterioValutazioneOffertaType;
import it.eldasoft.www.sil.WSGareAppalto.StazioneAppaltanteBandoType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaDocumenti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaTecnica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.FirmatarioBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.helpers.WizardOffertaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.security.SymmetricEncryptionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import javax.crypto.Cipher;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.GregorianCalendar;
import java.util.List;


/**
 * Helper di memorizzazione dei dati relativi alla gestione della compilazione
 * di un'offerta telematica.
 * 
 * @author ...
 */
public class WizardOffertaTecnicaHelper extends WizardOffertaHelper {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 7912841619659525733L;
	
	/**
	 * Step di navigazione del wizard 
	 */
	public static final String STEP_OFFERTA 				= "offerta";
	public static final String STEP_SCARICA_OFFERTA 		= "scaricaOfferta";
	public static final String STEP_DOCUMENTI 				= "documenti";

	public String getSTEP_OFFERTA() { return STEP_OFFERTA; }
	public String getSTEP_SCARICA_OFFERTA() { return STEP_SCARICA_OFFERTA; }
	public String getSTEP_DOCUMENTI() { return STEP_DOCUMENTI; }

	/**
	 * Numero massimo di decimali utilizzabile per esprimere un ribasso o un aumento
	 */
	private Long numDecimaliRibasso;

	private StazioneAppaltanteBandoType stazioneAppaltanteBando;
	
	
	/**
	 * costruttore 
	 */
	public WizardOffertaTecnicaHelper() {
		super(PortGareSystemConstants.BUSTA_TECNICA, PortGareSystemConstants.BUSTA_TEC);
		this.stepPrefixPage = "openPageOffTec";
		this.numDecimaliRibasso = null;
	}

	public Long getNumDecimaliRibasso() {
		return numDecimaliRibasso;
	}

	public void setNumDecimaliRibasso(Long numDecimaliRibasso) {
		this.numDecimaliRibasso = numDecimaliRibasso;
	}
	
	public void setStazioneAppaltanteBando(StazioneAppaltanteBandoType stazioneAppaltanteBando) {
		this.stazioneAppaltanteBando = stazioneAppaltanteBando;
	}

	public StazioneAppaltanteBandoType getStazioneAppaltanteBando() {
		return stazioneAppaltanteBando;
	}

	@Override
	public boolean isCriteriValutazioneEditabili() {
		return true;
	}

	/**
	 * Genera (o rigenera) lo stack delle pagine del wizard per consentire la
	 * corretta navigazione.
	 */
	@Override
	public void fillStepsNavigazione() {
		// configura la mappa degli step di navigazione
		// e i relativi target associati...
		this.clearMappaStepNavigazione();		
		this.addMappaStepNavigazione(STEP_OFFERTA, 			null, null);
		this.addMappaStepNavigazione(STEP_SCARICA_OFFERTA,  null, null);
		this.addMappaStepNavigazione(STEP_DOCUMENTI,   	  	null, null);

		// prepara l'elendo degli step disponibili per il wizard...
		// NB: per default gli step sono disabilitati!!!
		this.stepNavigazione.removeAllElements();
		this.stepNavigazione.push(STEP_OFFERTA);
		this.stepNavigazione.push(STEP_SCARICA_OFFERTA);
		this.stepNavigazione.push(STEP_DOCUMENTI);
		
		// abilita gli step sempre
		this.abilitaStepNavigazione(STEP_OFFERTA, true);
		this.abilitaStepNavigazione(STEP_SCARICA_OFFERTA, true);
		this.abilitaStepNavigazione(STEP_DOCUMENTI, true);		
	}

	/**
	 * Genera l'XML con i dati comuni per l'offerta tecnica e per il report.
	 * Nel caso del report inserisce la data presentazione come data attuale, ed
	 * aggiunge ulteriori attributi presi dalla gara.
	 * 
	 * @param document
	 *            documento xml creato
	 * @param attachFileContents
	 *            true per allegare il contenuto dei file, false altrimenti
	 * @param report
	 * @return documento aggiornato
	 * @throws IOException
	 */
	@Override
	public XmlObject getXmlDocument(
			XmlObject document, 
			boolean attachFileContents, 
			boolean report) throws IOException, GeneralSecurityException 
	{
		BustaTecnicaType bustaTecnica = null;
		if (document instanceof ReportOffertaTecnicaDocument) {
			ReportOffertaTecnicaType reportOffertaTecnica = ((ReportOffertaTecnicaDocument) document).addNewReportOffertaTecnica();
			bustaTecnica = (BustaTecnicaType) reportOffertaTecnica;

			// -- GENERICI --
			reportOffertaTecnica.setCodiceOfferta(gara.getCodice());
			reportOffertaTecnica.setOggetto(gara.getOggetto());
			reportOffertaTecnica.setCig(gara.getCig());
			reportOffertaTecnica.setTipoAppalto(gara.getTipoAppalto());
			reportOffertaTecnica.setCriterioAggiudicazione(gara.getTipoAggiudicazione());
		} else {			
			bustaTecnica = ((BustaTecnicaDocument) document).addNewBustaTecnica();	
		}

		// -- VALORIZZAZIONE PARTE COMUNE: offerta, documenti -- 
		Cipher cipher = null;
		if (this.getDocumenti().getChiaveSessione() != null) {
			cipher = SymmetricEncryptionUtils.getEncoder(
					this.getDocumenti().getChiaveSessione(), 
					this.getDocumenti().getUsername());
		}

		// memorizzo una data non significativa in quanto si tratta di bozza
		// (la data comunque serve in quanto il campo e' obbligatorio nel
		// tracciato xml da inviare)
		bustaTecnica.setDataPresentazione(new GregorianCalendar());

		OffertaTecnicaType offerta = bustaTecnica.addNewOfferta();

		// -- CRITERI VALUTAZIONE --
		ListaCriteriValutazioneType criteriValutazione = offerta.addNewListaCriteriValutazione();
		if(this.listaCriteriValutazione != null) {
			for (int i = 0; i < this.listaCriteriValutazione.size(); i++) {
				if(this.listaCriteriValutazione.get(i).getTipo().intValue() == PortGareSystemConstants.CRITERIO_TECNICO) {
					AttributoGenericoType criterio = criteriValutazione.addNewCriterioValutazione();
					if(report)
						criterio.setCodice( this.listaCriteriValutazione.get(i).getDescrizione() );
					else	
						criterio.setCodice( this.listaCriteriValutazione.get(i).getCodice() );
					criterio.setTipo( this.listaCriteriValutazione.get(i).getFormato().intValue() );		
					setValoreCriterioValutazioneXml(
							criterio, this.listaCriteriValutazione.get(i), (report ? null : cipher));
				}
			}
		}

		if (this.pdfUUID != null) {
			offerta.setUuid(this.pdfUUID);
		}

		// -- FIRMATARI --
		this.addFirmatariBusta(bustaTecnica);

		// -- DOCUMENTAZIONE --
		ListaDocumentiType listaDocumenti = bustaTecnica.addNewDocumenti();
		this.documenti.addDocumenti(listaDocumenti, attachFileContents);

		document.documentProperties().setEncoding("UTF-8");
		return document;
	}

	/**
	 * ...
	 */
	public void deleteDocumentoOffertaTecnica(
			BaseAction action,
			IEventManager eventManager) 
	{
		Event evento = null;
		String message = "";
		if(action != null && eventManager != null) {
			evento = new Event();
		}
		
		// Scorporato in due metodi diversi a causa di null pointer exception 
		// su file.exists/file.delete partendo da cifrata con prezzi unitari 
		// riletta da comunicazione FS11R + disallineamento liste + mancata 
		// rimozione offerta economica a modifica prezzi unitari
		WizardDocumentiBustaHelper documentiHelper = this.getDocumenti();
		boolean offertaTecnicaRimossa = false;
		
		if(this.idOfferta != null && this.idOfferta.longValue() > 0) {
			for (int i = 0; i < documentiHelper.getRequiredDocs().size() && !offertaTecnicaRimossa; i++) {
				if(documentiHelper.getRequiredDocs().get(i).getId() != null
				   && documentiHelper.getRequiredDocs().get(i).getId().longValue() == this.idOfferta.longValue()) {
					// traccia l'evento di eliminazione di un file...
					if(evento != null) {
						message += "cancellazione forzata offerta per variazione dati inseriti, " +
								   "file=" + documentiHelper.getRequiredDocs().get(i).getFileName() + ", " +
								   "dimensione=" + documentiHelper.getRequiredDocs().get(i).getSize() + "KB";
					}
					
					// elimina il documento ed il file associato (in chiaro o cifrato)
					this.getDocumenti().removeDocRichiesto(i);
					this.getDocumenti().setDocOffertaPresente(false);
					offertaTecnicaRimossa = true;
				}
			}
		}

		if(evento != null && offertaTecnicaRimossa) {
			evento.setUsername(action.getCurrentUser().getUsername());
			evento.setDestination(documentiHelper.getCodice());
			evento.setLevel(Event.Level.INFO);
			evento.setEventType(PortGareEventsConstants.DELETE_FILE);
			evento.setIpAddress(action.getCurrentUser().getIpAddress());
			evento.setSessionId(action.getRequest().getSession().getId());
			evento.setMessage("Busta tecnica : " + message);
			eventManager.insertEvent(evento);
		}

	}
	/**
	 * Overload del setter setFirmatarioSelezionato(...)
	 *  
	 * Imposta il firmatario selezionato partendo da una stringa in formato
	 * 		"[descrizione firmatario] - [id firmatario]"  
	 */
	public void setFirmatarioSelezionato(String firmatario) {
		//this.firmatarioSelezionato = null;
		if (this.getComponentiRTI().size() == 0) {
			FirmatarioBean firmatarioBean = new FirmatarioBean();			
			if (this.getImpresa().isLiberoProfessionista()) {
				// libero professionista...
			} else {
				// impresa/consorzio...
				int j = firmatario.lastIndexOf("-");
				Integer idFirmatario = Integer.valueOf(StringUtils
					.substring(firmatario, j+1, firmatario.length()));
				String lista = StringUtils.substring(firmatario, 0, j);
				
				firmatarioBean.setIndex(idFirmatario);
				firmatarioBean.setLista(lista);
				
				for (int i = 0; i < this.getListaFirmatariMandataria().size(); i++) {
					String firmatarioLista = 
						this.getListaFirmatariMandataria().get(i).getLista() 
						+ "-" + 
						this.getListaFirmatariMandataria().get(i).getIndex().toString();
					if (firmatarioLista.equals(firmatario)) {
						this.setIdFirmatarioSelezionatoInLista(i);
						break;
					}
				}
			}
			
			// imposta il firmatario con il setter nativo... 
			this.setFirmatarioSelezionato(firmatarioBean);
		}
	}
	
	/**
	 * cerca un criterio di valutazione in una lista di criteri in base al 
	 * formato. 
	 */
	public static CriterioValutazioneOffertaType findCriterioValutazione(
			List<CriterioValutazioneOffertaType> listaCriteriValutazione,
			int formato) 
	{
		CriterioValutazioneOffertaType criterio = null;
		for(int i = 0; i < listaCriteriValutazione.size(); i++) {
			if(listaCriteriValutazione.get(i).getFormato() == formato) {
				criterio = listaCriteriValutazione.get(i);
				break;
			}
		}
		return criterio;
	}

	/**
	 * popola l'helper con con i dati del documento xml della busta 
	 * @throws XmlException 
	 */
	@Override
	public void popolaFromXml(BustaDocumenti busta) throws Throwable {
		if(busta == null) {
			return;
		}
		
		// recupera il documento XML associato alla busta
		// recupera l'xml della busta tecnica
		// nel caso xml sia in formato "errato", 
		// converti nel formato corretto!!!
		BustaTecnica bustaTec = (BustaTecnica)busta;
		BustaTecnicaDocument doc = (BustaTecnicaDocument)bustaTec.getBustaDocument();
		BustaTecnicaType xml = (doc != null ? doc.getBustaTecnica() : null);
		ListaDocumentiType documenti = (xml != null ? xml.getDocumenti() : null);
		FirmatarioType[] firmatari = (xml != null ? xml.getFirmatarioArray() : null);
		OffertaTecnicaType offerta = (xml != null ? xml.getOfferta() : null);
		
    	// ************************************************************
    	// inizializza la cifratura, allinea i documenti e i firmatari... 
		boolean continua = this.popolaFirmatariFromXml(
    			busta,
    			firmatari);
		continua = continua && this.popolaDocumentiFromXml(
    			busta,
    			documenti);
    	if(!continua || offerta == null) {
    		return;
    	}
    	
		// ************************************************************
		// popola l'helper con i dati del documento xml...
		Cipher cipher = this.getDecoder();
		
		// -- Criteri di valutazione --
		List<CriterioValutazioneOffertaType> criteriValutazione = this.getListaCriteriValutazione();
		if(offerta.isSetListaCriteriValutazione()) {
			criteriValutazione = popolaCriteriValutazioneFromXml(offerta.getListaCriteriValutazione());
		}
		this.setListaCriteriValutazione(criteriValutazione);

		if (offerta.isSetUuid()) {
			this.setPdfUUID(offerta.getUuid());
		}
	}

}
