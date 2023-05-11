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
import it.eldasoft.www.sil.WSGareAppalto.GaraType;
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
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
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

	public static final int CRITERIO_VALUTAZIONE_TESTO_MAXLEN = 2000;
		

	/**
	 * Numero massimo di decimali utilizzabile per esprimere un ribasso o un
	 * aumento
	 */
	private Long numDecimaliRibasso;

	private StazioneAppaltanteBandoType stazioneAppaltanteBando;
	
	// dati inseriti nell'interfaccia (form di inserimento dati)
	private String[] criterioValutazione;
	private Boolean[] criterioValutazioneEditabile;	 

	
	/**
	 * gestione dei criteri di valutazione per OEPV
	 */
	private List<CriterioValutazioneOffertaType> listaCriteriValutazione;	

	public String[] getCriterioValutazione() {
		return criterioValutazione;
	}

	public void setCriterioValutazione(String[] criterioValutazione) {
		this.criterioValutazione = criterioValutazione;
	}

	/**
	 * costruttore 
	 */
	public WizardOffertaTecnicaHelper() {
		super(PortGareSystemConstants.BUSTA_TECNICA, PortGareSystemConstants.BUSTA_TEC);
		this.stepPrefixPage = "openPageOffTec";
		this.numDecimaliRibasso = null;
		this.listaCriteriValutazione = null;		
		this.criterioValutazione = null;
		this.criterioValutazioneEditabile = null;		
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

	public List<CriterioValutazioneOffertaType> getListaCriteriValutazione() {
		return listaCriteriValutazione;
	}
	
	/**
	 * Verifica se per un data busta (tecnica o economica) di una gara esistono 
	 * dei criteri di valutazione.
	 * 
	 * @param tipoCriterio
	 * 			tipo di criteri da verificare (PortGareSystemConstants.CRITERIO_TECNICO, PortGareSystemConstants.CRITERIO_ECONOMICO)
	 * @param gara
	 * 			dati della gara 
	 * @param listaCriteriValutazione
	 * 			lista dei criteri di valutazione associati alla gara 
	 */
	public static boolean isCriteriValutazioneVisibili(
			int tipoCriterio, 
			GaraType gara, 
			List<CriterioValutazioneOffertaType> listaCriteriValutazione) 
	{
		int countCriteri = 0;
		if(listaCriteriValutazione != null) { 
			for(int i = 0; i < listaCriteriValutazione.size(); i++) {
				if(listaCriteriValutazione.get(i).getTipo() == tipoCriterio) {
					countCriteri++;
				}					
			}			
		}		
		return (gara != null &&
				gara.isProceduraTelematica() &&
				gara.isOffertaTelematica() && 
				(gara.getModalitaAggiudicazione() == 6) &&  
				(countCriteri > 0));
	}

	public boolean isCriteriValutazioneVisibili() {
		return isCriteriValutazioneVisibili(
				PortGareSystemConstants.CRITERIO_TECNICO, 
				this.gara, 
				this.listaCriteriValutazione);
	}
	
	public boolean isCriteriValutazioneEditabili() {
		return true;
	}

	/**
	 * @param listaCriteriValutazione the listaCriteriValutazione to set
	 */
	public void setListaCriteriValutazione(
			List<CriterioValutazioneOffertaType> listaCriteriValutazione) {
		this.listaCriteriValutazione = listaCriteriValutazione;		
		
		this.criterioValutazioneEditabile = null;
		this.criterioValutazione = null;
		if(listaCriteriValutazione != null) {
			this.criterioValutazione = new String[listaCriteriValutazione.size()];
			this.criterioValutazioneEditabile = new Boolean[listaCriteriValutazione.size()];			
			for(int i = 0; i < this.listaCriteriValutazione.size(); i++) {
				this.criterioValutazione[i] = null; 
				this.criterioValutazioneEditabile[i] = new Boolean(true);				
			}			
		}		
	}

	/**
	 * @return the criterioValutazioneEditabile
	 */
	public Boolean[] getCriterioValutazioneEditabile() {
		return criterioValutazioneEditabile;
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
	 * Restituisce il valore di un criterio di valutazione estratto da un 
	 * documento xml   
	 */	
	public static String getValoreCriterioValutazioneXml(
			AttributoGenericoType attr,
			CriterioValutazioneOffertaType criterio,
			Cipher cipher) 
	{
		String value = null;
		try {																				
			if (cipher != null) {
				// in caso di cifratura, decifra il valore...
				if(attr.isSetValoreCifrato()) {				
					value = new String(SymmetricEncryptionUtils.translate(
							cipher, 
							attr.getValoreCifrato()));
				}
			} else {
				// il valore e' in chiaro e non cifrato...
				int numDecimali = (criterio.getNumeroDecimali() != null ? criterio.getNumeroDecimali() : 0);
				numDecimali = Math.min(5, Math.max(1, numDecimali));
				
				switch (attr.getTipo()) {
				case PortGareSystemConstants.CRITERIO_VALUTAZIONE_DATA:
					if(criterio.getTipo() == PortGareSystemConstants.CRITERIO_TECNICO) {
						value = (attr.isSetValoreData() ? attr.getValoreData().toString() : null);
					}
					break;
				case PortGareSystemConstants.CRITERIO_VALUTAZIONE_IMPORTO:
					if(attr.isSetValoreNumerico()) {
						value = BigDecimal.valueOf(Double.valueOf(attr.getValoreNumerico()))
											.setScale(2, BigDecimal.ROUND_HALF_UP)
											.toPlainString();
					}
					break;
				case PortGareSystemConstants.CRITERIO_VALUTAZIONE_DECIMALE:
					if(attr.isSetValoreNumerico()) {
						value = BigDecimal.valueOf(Double.valueOf(attr.getValoreNumerico()))
											.setScale(numDecimali, BigDecimal.ROUND_HALF_UP)
											.toPlainString();
					}
					break;
				case PortGareSystemConstants.CRITERIO_VALUTAZIONE_LISTA_VALORI:
					value = (attr.isSetValoreStringa() ? attr.getValoreStringa() : null);						 
					break;
				case PortGareSystemConstants.CRITERIO_VALUTAZIONE_TESTO:
					if(criterio.getTipo() == PortGareSystemConstants.CRITERIO_TECNICO) {
						value = (attr.isSetValoreStringa() ? attr.getValoreStringa() : null);
					}
					break;
				case PortGareSystemConstants.CRITERIO_VALUTAZIONE_INTERO:
					if(attr.isSetValoreNumerico()) {							
						Double v = new Double(attr.getValoreNumerico());							
						value = Long.toString(v.longValue());
					}
					break;
				case PortGareSystemConstants.CRITERIO_VALUTAZIONE_OFFERTA_MEDIANTE_IMPORTO:
					if(criterio.getTipo() == PortGareSystemConstants.CRITERIO_ECONOMICO) {
						if(attr.isSetValoreNumerico()) {						
							value = BigDecimal.valueOf(Double.valueOf(attr.getValoreNumerico()))
												.setScale(numDecimali, BigDecimal.ROUND_HALF_UP)
												.toPlainString();
						}
					}
					break;
				case PortGareSystemConstants.CRITERIO_VALUTAZIONE_OFFERTA_MEDIANTE_PREZZIUNITARI:
					if(criterio.getTipo() == PortGareSystemConstants.CRITERIO_ECONOMICO) {
						if(attr.isSetValoreNumerico()) {						
							value = BigDecimal.valueOf(Double.valueOf(attr.getValoreNumerico()))
												.setScale(numDecimali, BigDecimal.ROUND_HALF_UP)
												.toPlainString();
						}
					}
					break;
				case PortGareSystemConstants.CRITERIO_VALUTAZIONE_OFFERTA_MEDIANTE_RIBASSO:
					if(criterio.getTipo() == PortGareSystemConstants.CRITERIO_ECONOMICO) {
						if(attr.isSetValoreNumerico()) {						
							value = BigDecimal.valueOf(Double.valueOf(attr.getValoreNumerico()))
												.setScale(numDecimali, BigDecimal.ROUND_HALF_UP)
												.toPlainString();
						}
					}
					break;
				}		
			}
		} catch (Throwable e) {
			value = null;
		}	
		return value;
	}

	/**
	 * Imposta il valore di un criterio di valutazione per un documento xml 
	 * (es: busta economica o tecnica). 
	 * In caso di cifratura i valori vengono cifrati.   
	 */	
	public static void setValoreCriterioValutazioneXml(
			AttributoGenericoType criterio,
			CriterioValutazioneOffertaType valore,			
			Cipher cipher) 
	{
		String value;
		try {
			// cipher e' null se... 
			// non ho cifratura oppure non ho cifratura perche' sto 
			// preparando un documento xml per generare un report, 
			// in entrambi i casi i dati devono essere in chiaro...	
			
			// valida ed imposta il valore...  
			setValoreCriterioValutazione(valore, valore.getValore(), true);
			value = valore.getValore();
			
			if (cipher == null) {
				switch (valore.getFormato()) {
				case PortGareSystemConstants.CRITERIO_VALUTAZIONE_DATA:
					if(valore.getTipo() == PortGareSystemConstants.CRITERIO_TECNICO) {
						Calendar c = Calendar.getInstance();
						c.setTime(DATEFORMAT_DDMMYYYY.parse(valore.getValore()));
						criterio.setValoreData(c);
					}
					break;
				case PortGareSystemConstants.CRITERIO_VALUTAZIONE_IMPORTO:		
					criterio.setValoreNumerico(Double.valueOf(valore.getValore()));
					break;
				case PortGareSystemConstants.CRITERIO_VALUTAZIONE_DECIMALE:
					criterio.setValoreNumerico(Double.valueOf(valore.getValore()));
					break;
				case PortGareSystemConstants.CRITERIO_VALUTAZIONE_LISTA_VALORI:
					criterio.setValoreStringa(valore.getValore());
					break;
				case PortGareSystemConstants.CRITERIO_VALUTAZIONE_TESTO:
					if(valore.getTipo() == PortGareSystemConstants.CRITERIO_TECNICO) {
						criterio.setValoreStringa(valore.getValore());						
					}
					break;
				case PortGareSystemConstants.CRITERIO_VALUTAZIONE_INTERO:
					criterio.setValoreNumerico(Long.valueOf(valore.getValore()));
					break;
				case PortGareSystemConstants.CRITERIO_VALUTAZIONE_OFFERTA_MEDIANTE_IMPORTO:
					if(valore.getTipo() == PortGareSystemConstants.CRITERIO_ECONOMICO) {
						criterio.setValoreNumerico(Double.valueOf(valore.getValore()));
					}
					break;
				case PortGareSystemConstants.CRITERIO_VALUTAZIONE_OFFERTA_MEDIANTE_PREZZIUNITARI:
					if(valore.getTipo() == PortGareSystemConstants.CRITERIO_ECONOMICO) {
						criterio.setValoreNumerico(Double.valueOf(valore.getValore()));
					}
					break;
				case PortGareSystemConstants.CRITERIO_VALUTAZIONE_OFFERTA_MEDIANTE_RIBASSO:
					if(valore.getTipo() == PortGareSystemConstants.CRITERIO_ECONOMICO) {
						criterio.setValoreNumerico(Double.valueOf(valore.getValore()));
					}					
					break;
				}	
			} else {			
				// nel caso di cifratura i valori vanno criptati...
				// in questo caso si sta preparando il documento xml da 
				// allegare alla comunicazione
				criterio.setValoreCifrato(
						SymmetricEncryptionUtils.translate(cipher, value.toString().getBytes()));
			}
		} catch (Throwable e) {
			value = null;
		}					
	}
	
	/**
	 * Valida ed imposta il valore di un criterio di valutazione
	 * 
	 * @throws Exception 
	 */
	public static void setValoreCriterioValutazione(
			CriterioValutazioneOffertaType criterio,
			String valore,
			boolean validateAndSet) throws Exception
	{	
		if(criterio != null) {
			int n = (criterio.getNumeroDecimali() != null ? criterio.getNumeroDecimali() : 0);   
			int numDecimali = Math.min(5, Math.max(1, n));
	
			// valida i decimali dei campi numerici...		
			switch (criterio.getFormato()) {			
			case PortGareSystemConstants.CRITERIO_VALUTAZIONE_TESTO:
				// il tipo testo non può eccedere i 2000 char
				//String val = valore.replaceAll("[\\t\\n\\r]+", " ");
				String val = valore.replaceAll("[\\n\\r]+", "  ");		//cr+lf
				if(val.length() > CRITERIO_VALUTAZIONE_TESTO_MAXLEN) {
					throw new ParseException("Errors.tooManyChars", 0);
				}
				break;
			case PortGareSystemConstants.CRITERIO_VALUTAZIONE_IMPORTO:
			case PortGareSystemConstants.CRITERIO_VALUTAZIONE_DECIMALE:
			case PortGareSystemConstants.CRITERIO_VALUTAZIONE_OFFERTA_MEDIANTE_IMPORTO:							
			case PortGareSystemConstants.CRITERIO_VALUTAZIONE_OFFERTA_MEDIANTE_PREZZIUNITARI:		
			case PortGareSystemConstants.CRITERIO_VALUTAZIONE_OFFERTA_MEDIANTE_RIBASSO:			
				if(criterio.getFormato() == PortGareSystemConstants.CRITERIO_VALUTAZIONE_IMPORTO) {
					numDecimali = 2;
				}			
				String[] aumentoArray = valore.split("\\.");
				String decimalPartString = "";
				Integer decimalPart = 0;
				try {
					if (aumentoArray.length > 1) {
						decimalPartString = aumentoArray[1];				
						decimalPart = new Integer(decimalPartString);
					}
				} catch(Exception e) {
					// errore da gestire nella sezione di validazione ed impostazione 
					// del campo!!!
					decimalPartString = null;
				}
				if(decimalPartString != null) {
					if (numDecimali == 0 && decimalPart > 0) {
						throw new Exception("Errors.noDecimalsNeeded");    													
					} else if (numDecimali > 0 && decimalPartString.length() > numDecimali) {
						throw new NumberFormatException("Errors.tooManyDecimals"); 
					}
				}
				break;
			}
			
			// valida ed imposta il valore del campo...
			try {
				String newValue = null;
				
				switch (criterio.getFormato()) {			
				case PortGareSystemConstants.CRITERIO_VALUTAZIONE_DATA:
					if(criterio.getTipo() == PortGareSystemConstants.CRITERIO_TECNICO) {
						Calendar c = Calendar.getInstance();
						c.setTime(DATEFORMAT_DDMMYYYY.parse(valore));
						newValue = DATEFORMAT_DDMMYYYY.format(c.getTime());
					}
					break;
				case PortGareSystemConstants.CRITERIO_VALUTAZIONE_IMPORTO:
					newValue = BigDecimal.valueOf(Double.valueOf(valore))
										.setScale(2, BigDecimal.ROUND_HALF_UP)
										.toPlainString();
					break;
				case PortGareSystemConstants.CRITERIO_VALUTAZIONE_DECIMALE:
					newValue = BigDecimal.valueOf(Double.valueOf(valore))
										.setScale(numDecimali, BigDecimal.ROUND_HALF_UP)
										.toPlainString();
					break;
				case PortGareSystemConstants.CRITERIO_VALUTAZIONE_LISTA_VALORI:
					newValue = valore;
					break;
				case PortGareSystemConstants.CRITERIO_VALUTAZIONE_TESTO:
					if(criterio.getTipo() == PortGareSystemConstants.CRITERIO_TECNICO) {
						newValue = valore;
					}
					break;
				case PortGareSystemConstants.CRITERIO_VALUTAZIONE_INTERO:
					newValue =  BigDecimal.valueOf(Double.valueOf(valore))
										.setScale(0, BigDecimal.ROUND_HALF_UP)
										.toPlainString();
					break;
				case PortGareSystemConstants.CRITERIO_VALUTAZIONE_OFFERTA_MEDIANTE_IMPORTO:				
					if(criterio.getTipo() == PortGareSystemConstants.CRITERIO_ECONOMICO) {
						newValue = BigDecimal.valueOf(Double.valueOf(valore))
											.setScale(numDecimali, BigDecimal.ROUND_HALF_UP)
											.toPlainString();
					}
					break;
				case PortGareSystemConstants.CRITERIO_VALUTAZIONE_OFFERTA_MEDIANTE_PREZZIUNITARI:
					if(criterio.getTipo() == PortGareSystemConstants.CRITERIO_ECONOMICO) {
						newValue = BigDecimal.valueOf(Double.valueOf(valore))
											.setScale(numDecimali, BigDecimal.ROUND_HALF_UP)
											.toPlainString();
					}
					break;
				case PortGareSystemConstants.CRITERIO_VALUTAZIONE_OFFERTA_MEDIANTE_RIBASSO:
					if(criterio.getTipo() == PortGareSystemConstants.CRITERIO_ECONOMICO) {
						newValue = BigDecimal.valueOf(Double.valueOf(valore))
											.setScale(numDecimali, BigDecimal.ROUND_HALF_UP)
											.toPlainString();
					}					
					break;
				}
			
				// aggiorna il nuovo valore del criterio...
				if(validateAndSet) {
					criterio.setValore(newValue);
				}				
			} catch (Throwable e) {
				throw new Exception("Errors.wrongField"); 
			}
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
			if(criteriValutazione == null) {
				criteriValutazione = new ArrayList<CriterioValutazioneOffertaType>();
			} else {
				criteriValutazione = new ArrayList<CriterioValutazioneOffertaType>(criteriValutazione);
			}
			for(int i = 0; i < offerta.getListaCriteriValutazione().getCriterioValutazioneArray().length; i++) {
				AttributoGenericoType attr = offerta.getListaCriteriValutazione().getCriterioValutazioneArray()[i];
				
				// cerca il criterio di valutazione tra quelli presenti...
				CriterioValutazioneOffertaType criterio = null;
				for(int j = 0; j < criteriValutazione.size(); j++) {
					if(criteriValutazione.get(j).getCodice().equals(attr.getCodice())) {
						criterio = criteriValutazione.get(j);
						break;
					}
				}
				if(criterio == null) {
					// aggiungi un nuovo criterio...
					criterio = new CriterioValutazioneOffertaType();
					criteriValutazione.add(criterio);
					criterio.setCodice(attr.getCodice());
					criterio.setDescrizione(attr.getCodice());
					criterio.setFormato(attr.getTipo());
					criterio.setNumeroDecimali(0);
				}

				// imposta i dati del criterio (tipo, formato e valore)... 
				criterio.setTipo(PortGareSystemConstants.CRITERIO_TECNICO);
				//criterio.setFormato(attr.getTipo());
				if(criterio.getFormato() != attr.getTipo()) {
					criterio.setValore(null);
				} else {
					criterio.setValore(WizardOffertaTecnicaHelper
							.getValoreCriterioValutazioneXml(attr, criterio, cipher));
				}
			}
		}
		this.setListaCriteriValutazione(criteriValutazione);

		if (offerta.isSetUuid()) {
			this.setPdfUUID(offerta.getUuid());
		}
	}

}
