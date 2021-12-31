package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.eldasoft.sil.portgare.datatypes.AttributoGenericoType;
import it.eldasoft.sil.portgare.datatypes.BustaEconomicaDocument;
import it.eldasoft.sil.portgare.datatypes.BustaEconomicaType;
import it.eldasoft.sil.portgare.datatypes.ComponenteDettaglioOffertaType;
import it.eldasoft.sil.portgare.datatypes.ListaComponentiOffertaType;
import it.eldasoft.sil.portgare.datatypes.ListaCriteriValutazioneType;
import it.eldasoft.sil.portgare.datatypes.ListaDocumentiType;
import it.eldasoft.sil.portgare.datatypes.OffertaEconomicaType;
import it.eldasoft.sil.portgare.datatypes.ReportOffertaEconomicaDocument;
import it.eldasoft.sil.portgare.datatypes.ReportOffertaEconomicaType;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.eldasoft.www.sil.WSGareAppalto.AttributoAggiuntivoOffertaType;
import it.eldasoft.www.sil.WSGareAppalto.CriterioValutazioneOffertaType;
import it.eldasoft.www.sil.WSGareAppalto.StazioneAppaltanteBandoType;
import it.eldasoft.www.sil.WSGareAppalto.VoceDettaglioOffertaType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StringUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.FirmatarioBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.SoggettoFirmatarioImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.helpers.WizardOffertaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.security.SymmetricEncryptionUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.crypto.Cipher;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.CalendarValidator;
import org.apache.xmlbeans.XmlObject;
import com.agiletec.apsadmin.system.BaseAction;


/**
 * Helper di memorizzazione dei dati relativi alla gestione della compilazione
 * di un'offerta telematica.
 * 
 * @author stefano.sabbadin
 */
public class WizardOffertaEconomicaHelper extends WizardOffertaHelper { 
	/**
	 * UID
	 */
	private static final long serialVersionUID = 3744215089572571490L;

	/**
	 * Step di navigazione del wizard 
	 */
	public static final String STEP_PREZZI_UNITARI 		= "prezziUnitari";
	public static final String STEP_OFFERTA 			= "offerta";
	public static final String STEP_SCARICA_OFFERTA 	= "scaricaOfferta";
	public static final String STEP_DOCUMENTI 			= "documenti";

	/**
	 * Numero massimo di decimali utilizzabile per esprimere un ribasso o un
	 * aumento
	 */
	private Long numDecimaliRibasso;

	private StazioneAppaltanteBandoType stazioneAppaltanteBando;
	private List<VoceDettaglioOffertaType> vociDettaglio;
	private List<VoceDettaglioOffertaType> vociDettaglioNoRibasso;
	/**
	 * Contenitore della lista attributi aggiuntivi da valorizzare per ogni
	 * lavorazione e fornitura della gara
	 */
	private List<AttributoAggiuntivoOffertaType> attributiAggiuntivi;

	/**
	 * true se esistono voci di lavorazioni forniture da valorizzare, false
	 * altrimenti.
	 */
	private boolean esistonoVociDettaglioOffertaPrezzi;

	// dati inseriti nell'interfaccia (form inserimento dati)
	private Double[] prezzoUnitario;
	private Double[] importoUnitario;
	private String[] importoUnitarioStringaNotazioneStandard;
	private String[] criterioValutazione;
	private Boolean[] criterioValutazioneEditabile;

	// Struttura generica che mi contiene i dati custom
	private Map<String, String[]> valoreAttributiAgg;

	private Double totaleOffertaPrezziUnitari;

	private Double importoOfferto;
	private Double ribasso;
	private Double aumento;
	private Double costiSicurezzaAziendali;
	private Double importoManodopera;
	private Double percentualeManodopera;
	
	/* --- PERMUTA E ASSISTENZA --- */
	private Double importoOffertoPerPermuta;
	private Double importoOffertoCanoneAssistenza;

//	private Long idOffertaEconomica;

	/**
	 * gestione dei criteri di valutazione per OEPV
	 */
	private List<CriterioValutazioneOffertaType> listaCriteriValutazione;	
	
	private String passoe;

	public String[] getCriterioValutazione() {
		return criterioValutazione;
	}

	public void setCriterioValutazione(String[] criterioValutazione) {
		this.criterioValutazione = criterioValutazione;
	}

	public Boolean[] getCriterioValutazioneEditabile() {
		return criterioValutazioneEditabile;
	}

	public Long getNumDecimaliRibasso() {
		return numDecimaliRibasso;
	}

	public void setNumDecimaliRibasso(Long numDecimaliRibasso) {
		this.numDecimaliRibasso = numDecimaliRibasso;
	}

	public boolean isSoggettoARibasso() {
		return this.gara.isOneriSoggettiRibasso();
	}

	public boolean isEsistonoVociDettaglioOffertaPrezzi() {
		return esistonoVociDettaglioOffertaPrezzi;
	}

	public void setEsistonoVociDettaglioOffertaPrezzi(boolean esistonoVociDettaglioOffertaPrezzi) {
		this.esistonoVociDettaglioOffertaPrezzi = esistonoVociDettaglioOffertaPrezzi;
	}

	public Double[] getPrezzoUnitario() {
		return prezzoUnitario;
	}

	public void setPrezzoUnitario(Double[] prezzoUnitario) {
		this.prezzoUnitario = prezzoUnitario;
	}

	public Double[] getImportoUnitario() {
		return importoUnitario;
	}

	public void setImportoUnitario(Double[] importoUnitario) {
		this.importoUnitario = importoUnitario;
		// si genera anche l'array con gli importi da inserire nella colonna
		// calcolata in apertura della dialog modale, in modo da evitare
		// eventuali visualizzazioni in notazione scientifica con numeri sopra a
		// 10 milioni id euro
		this.importoUnitarioStringaNotazioneStandard = new String[this.importoUnitario.length];
		for (int i = 0; i < this.importoUnitario.length; i++) {
			if (this.importoUnitario[i] != null) {
				this.importoUnitarioStringaNotazioneStandard[i] = StringUtilities.getDoubleNormalNotation(this.importoUnitario[i], 5);
			} else {
				this.importoUnitarioStringaNotazioneStandard[i] = "";
			}
		}
	}
	
	public String[] getImportoUnitarioStringaNotazioneStandard() {
		return importoUnitarioStringaNotazioneStandard;
	}
		
	public Map<String, String[]> getValoreAttributiAgg() {
		return valoreAttributiAgg;
	}

	public void setValoreAttributiAgg(Map<String, String[]> valoreAttributiAgg) {
		this.valoreAttributiAgg = valoreAttributiAgg;
	}

	public Double getTotaleOffertaPrezziUnitari() {
		return totaleOffertaPrezziUnitari;
	}

	public void setTotaleOffertaPrezziUnitari(Double totaleOffertaPrezziUnitari) {
		this.totaleOffertaPrezziUnitari = totaleOffertaPrezziUnitari;
	}

	public Double getImportoOfferto() {
		return importoOfferto;
	}

	public void setImportoOfferto(Double importoOfferto) {
		this.importoOfferto = importoOfferto;
	}

	public String getImportoOffertoNotazioneStandard() {
		return StringUtilities.getDoubleNormalNotation(this.importoOfferto, 5);
	}
	
	public Double getRibasso() {
		return ribasso;
	}

	public void setRibasso(Double ribasso) {
		this.ribasso = ribasso;
	}

	public Double getAumento() {
		return aumento;
	}

	public void setAumento(Double aumento) {
		this.aumento = aumento;
	}

	public Double getCostiSicurezzaAziendali() {
		return costiSicurezzaAziendali;
	}

	public void setCostiSicurezzaAziendali(Double costiSicurezzaAziendali) {
		this.costiSicurezzaAziendali = costiSicurezzaAziendali;
	}
	
	public Double getImportoManodopera() {
		return importoManodopera;
	}

	public void setImportoManodopera(Double importoManodopera) {
		this.importoManodopera = importoManodopera;
	}

	public Double getPercentualeManodopera() {
		return percentualeManodopera;
	}

	public void setPercentualeManodopera(Double percentualeManodopera) {
		this.percentualeManodopera = percentualeManodopera;
	}

	public void setStazioneAppaltanteBando(StazioneAppaltanteBandoType stazioneAppaltanteBando) {
		this.stazioneAppaltanteBando = stazioneAppaltanteBando;
	}

	public StazioneAppaltanteBandoType getStazioneAppaltanteBando() {
		return stazioneAppaltanteBando;
	}

	public void setVociDettaglio(List<VoceDettaglioOffertaType> vociDettaglio) {
		this.vociDettaglio = vociDettaglio;
		if (vociDettaglio != null) {
			// in base alle voci predispongo i prezzi e gli importi
			this.prezzoUnitario = new Double[vociDettaglio.size()];
			this.importoUnitario = new Double[vociDettaglio.size()];
		}
	}

	public List<VoceDettaglioOffertaType> getVociDettaglio() {
		return vociDettaglio;
	}

	public List<VoceDettaglioOffertaType> getVociDettaglioNoRibasso() {
		return vociDettaglioNoRibasso;
	}

	public void setVociDettaglioNoRibasso(List<VoceDettaglioOffertaType> vociDettaglioNoRibasso) {
		this.vociDettaglioNoRibasso = vociDettaglioNoRibasso;
	}

	public List<AttributoAggiuntivoOffertaType> getAttributiAggiuntivi() {
		return attributiAggiuntivi;
	}

	public void setAttributiAggiuntivi(List<AttributoAggiuntivoOffertaType> attributiAggiuntivi) {
		this.attributiAggiuntivi = attributiAggiuntivi;
	}

//	public Long getIdOffertaEconomica() {
//		return idOffertaEconomica;
//	}
//
//	public void setIdOffertaEconomica(long idOffertaEconomica) {
//		this.idOffertaEconomica = idOffertaEconomica;
//	}

	public List<CriterioValutazioneOffertaType> getListaCriteriValutazione() {
		return listaCriteriValutazione;
	}	
	
	public String getPassoe() {
		return passoe;
	}

	public void setPassoe(String passoe) {
		this.passoe = passoe;
	}

	public Double getImportoOffertoPerPermuta() {
		return importoOffertoPerPermuta;
	}

	public void setImportoOffertoPerPermuta(Double importoOffertoPerPermuta) {
		this.importoOffertoPerPermuta = importoOffertoPerPermuta;
	}

	public Double getImportoOffertoCanoneAssistenza() {
		return importoOffertoCanoneAssistenza;
	}
	
	public void setImportoOffertoCanoneAssistenza(Double importoOffertoCanoneAssistenza) {
		this.importoOffertoCanoneAssistenza = importoOffertoCanoneAssistenza;
	}

	
	/**
	 * costruttore 
	 */
	public WizardOffertaEconomicaHelper(String sessionKeyHelperId, boolean legacyPrevNext) {
		super(sessionKeyHelperId, legacyPrevNext);
	}

	/**
	 * costruttore
	 */
	public WizardOffertaEconomicaHelper() {
		super(PortGareSystemConstants.BUSTA_ECONOMICA, PortGareSystemConstants.BUSTA_ECO); 
		this.stepPrefixPage = "openPageOffTel";
		this.numDecimaliRibasso = null;
		this.prezzoUnitario = null;
		this.importoUnitario = null;
		this.totaleOffertaPrezziUnitari = null;
		this.importoOfferto = null;
		this.ribasso = null;
		this.aumento = null;
		this.costiSicurezzaAziendali = null;
		this.importoManodopera = null;
		this.percentualeManodopera = null;
		this.importoOffertoCanoneAssistenza = null;
		this.importoOffertoPerPermuta = null;
		this.valoreAttributiAgg = null;
		this.listaCriteriValutazione = null;
		this.criterioValutazione = null;
		this.criterioValutazioneEditabile = null;
	}

	/**
	 * @param listaCriteriValutazione the listaCriteriValutazione to set
	 */
	public void setListaCriteriValutazione(List<CriterioValutazioneOffertaType> listaCriteriValutazione) {
		this.listaCriteriValutazione = listaCriteriValutazione;		
		
		// riallinea alla nuova lista le strutture aggiuntive per l'edit dalla form
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
	 * Genera (o rigenera) lo stack delle pagine del wizard per consentire la
	 * corretta navigazione
	 */
	@Override
	public void fillStepsNavigazione() {
		this.stepNavigazione.removeAllElements();
		
		boolean addStepPrezziUnitari = false;
		if((this.gara.getModalitaAggiudicazione() == 5 || this.gara.getModalitaAggiudicazione() == 6 || this.gara.getModalitaAggiudicazione() == 14)
		   && this.esistonoVociDettaglioOffertaPrezzi) {
			addStepPrezziUnitari = true;
			if( this.isCriteriValutazioneVisibili() ) {
				// in caso di criteri di valutazione, aggiungi lo step 
				// "prezzi unitari" solo se e' presente il criterio 
				// "offerta mediante prezzi unitari" 
				if(WizardOffertaTecnicaHelper.findCriterioValutazione(
						this.listaCriteriValutazione, 
						PortGareSystemConstants.CRITERIO_VALUTAZIONE_OFFERTA_MEDIANTE_PREZZIUNITARI) == null) {
					addStepPrezziUnitari = false;
				}
			}		
		}
		
		if(addStepPrezziUnitari) {
			this.stepNavigazione.push(WizardOffertaEconomicaHelper.STEP_PREZZI_UNITARI);
		}
		this.stepNavigazione.push(WizardOffertaEconomicaHelper.STEP_OFFERTA);
		this.stepNavigazione.push(WizardOffertaEconomicaHelper.STEP_SCARICA_OFFERTA);
		this.stepNavigazione.push(WizardOffertaEconomicaHelper.STEP_DOCUMENTI);
	}


	/**
	 * Genera l'XML con i dati comuni per l'offerta economica e per il report.
	 * Nel caso del report inserisce la data presentazione come data attuale, ed
	 * aggiunge ulteriori attributi presi dalla gara.
	 * 
	 * @param document
	 *            documento xml creato
	 * @param attachFileContents
	 *            true per allegare il contenuto dei file, false altrimenti
	 * @param index
	 * @return documento aggiornato
	 * @throws IOException
	 */
	@Override
	public XmlObject getXmlDocument(
			XmlObject document, 
			boolean attachFileContents, 
			boolean report) throws IOException, GeneralSecurityException 
	{
		BustaEconomicaType bustaEconomica = null;
		if (document instanceof ReportOffertaEconomicaDocument) {
			ReportOffertaEconomicaType reportOffertaEconomica = ((ReportOffertaEconomicaDocument) document).addNewReportOffertaEconomica();
			bustaEconomica = (BustaEconomicaType) reportOffertaEconomica;

			// GENERICI
			reportOffertaEconomica.setCodiceOfferta(gara.getCodice());
			reportOffertaEconomica.setOggetto(gara.getOggetto());
			reportOffertaEconomica.setCig(gara.getCig());
			reportOffertaEconomica.setTipoAppalto(gara.getTipoAppalto());
			reportOffertaEconomica.setCriterioAggiudicazione(gara.getTipoAggiudicazione());
		} else {
			bustaEconomica = ((BustaEconomicaDocument) document).addNewBustaEconomica();
		}

		// VALORIZZAZIONE PARTE COMUNE: lavorazioni, offerta, documenti
		Cipher cipher = null;
		if (this.getDocumenti().getChiaveSessione() != null) {
			cipher = SymmetricEncryptionUtils.getEncoder(
					this.getDocumenti().getChiaveSessione(), 
					this.getDocumenti().getUsername());
		}

		// memorizzo una data non significativa in quanto si tratta di bozza
		// (la data comunque serve in quanto il campo e' obbligatorio nel
		// tracciato xml da inviare)
		bustaEconomica.setDataPresentazione(new GregorianCalendar());

		OffertaEconomicaType offerta = bustaEconomica.addNewOfferta();

		// LAVORAZIONI
		ListaComponentiOffertaType lavorazioni = offerta.addNewListaComponentiDettaglio();

		// si inseriscono le voci soggette a ribasso
		boolean ribassiPesati = ((gara != null && gara.getTipoRibasso() != null) ? gara.getTipoRibasso() == 3 : false);
		double totaleRibassi = 0;
		if(vociDettaglio != null) {
			for (int i = 0; i < vociDettaglio.size(); i++) {
				ComponenteDettaglioOffertaType lavorazione = lavorazioni.addNewComponenteDettaglio();
	
				Iterator<AttributoAggiuntivoOffertaType> attAggIterator = attributiAggiuntivi.iterator();
				if(valoreAttributiAgg != null){
					while (attAggIterator.hasNext()) {
						AttributoAggiuntivoOffertaType attr = attAggIterator.next();
						AttributoGenericoType attributo = lavorazione.addNewAttributoAggiuntivo();
						attributo.setCodice(attr.getCodice());
		
						String[] valore = valoreAttributiAgg.get(attr.getCodice());
						if (valore != null) {
							switch (attr.getTipo()) {
							case PortGareSystemConstants.TIPO_VALIDATORE_DATA:
								if (StringUtils.isNotBlank(valore[i])) {
									attributo.setValoreData(CalendarValidator.getInstance().validate(valore[i], "dd/MM/yyyy"));
								}
								break;
							case PortGareSystemConstants.TIPO_VALIDATORE_IMPORTO:
							case PortGareSystemConstants.TIPO_VALIDATORE_NUMERO:
								if (StringUtils.isNotBlank(valore[i])) {
									attributo.setValoreNumerico(Double.parseDouble(valore[i]));
								}
								break;
							case PortGareSystemConstants.TIPO_VALIDATORE_TABELLATO:
							case PortGareSystemConstants.TIPO_VALIDATORE_NOTE:
							case PortGareSystemConstants.TIPO_VALIDATORE_FLAG:
							case PortGareSystemConstants.TIPO_VALIDATORE_STRINGA:
								attributo.setValoreStringa(valore[i]);
								break;
							default:
								break;
							}
							// il tipo non può mai essere nullo
							attributo.setTipo(attr.getTipo());
						}
					}
				}
				lavorazione.setSoggettoRibasso(true);
				lavorazione.setId(vociDettaglio.get(i).getId());
				lavorazione.setCodice(vociDettaglio.get(i).getCodice());
				lavorazione.setVoce(vociDettaglio.get(i).getVoce());
				lavorazione.setDescrizione(vociDettaglio.get(i).getDescrizione());
				lavorazione.setUnitaMisura(vociDettaglio.get(i).getUnitaMisura());
				lavorazione.setQuantita(vociDettaglio.get(i).getQuantita());
				
				if (ribassiPesati && vociDettaglio.get(i).getRibassoPercentuale() != null) {
					// ricalcola il prezzo unitario tenendo conto del ribasso percentuale
					double przUni = (vociDettaglio.get(i).getPrezzoUnitarioBaseGara() != null 
									 ? vociDettaglio.get(i).getPrezzoUnitarioBaseGara() : 0);
					prezzoUnitario[i] = BigDecimal
										.valueOf(new Double( przUni * (1 - vociDettaglio.get(i).getRibassoPercentuale() / 100) ))
										.setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue();
					totaleRibassi += vociDettaglio.get(i).getRibassoPesato();
					
					lavorazione.setPrezzoUnitarioBaseGara(vociDettaglio.get(i).getPrezzoUnitarioBaseGara());
					lavorazione.setPeso( vociDettaglio.get(i).getPeso() );
					if (report || this.getDocumenti().getChiaveSessione() == null) {
						lavorazione.setRibasso( vociDettaglio.get(i).getRibassoPercentuale() );
						lavorazione.setRibassoPesato( vociDettaglio.get(i).getRibassoPesato() );
					} else {
						lavorazione.setRibassoCifrato( SymmetricEncryptionUtils.translate(cipher, vociDettaglio.get(i).getRibassoPercentuale().toString().getBytes()) );
						lavorazione.setRibassoPesatoCifrato( SymmetricEncryptionUtils.translate(cipher, vociDettaglio.get(i).getRibassoPesato().toString().getBytes()) );
					}
				}
				
				if (prezzoUnitario[i] != null) {
					double qta = (vociDettaglio.get(i).getQuantita() != null ? vociDettaglio.get(i).getQuantita() : 0);
					importoUnitario[i] = BigDecimal
										.valueOf(new Double( qta * prezzoUnitario[i] ))
										.setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue();
					if (report || this.getDocumenti().getChiaveSessione() == null) {
						lavorazione.setPrezzoUnitario(prezzoUnitario[i]);
						lavorazione.setImporto(importoUnitario[i]);				
					} else {
						// nel caso di cifratura si inseriscono i dati nei campi cifrati effettuando il tostring degli importi
						lavorazione.setPrezzoUnitarioCifrato(SymmetricEncryptionUtils.translate(cipher, prezzoUnitario[i].toString().getBytes()));
						lavorazione.setImportoCifrato(SymmetricEncryptionUtils.translate(cipher, importoUnitario[i].toString().getBytes()));				
					}
				}
			}
		}
		
		// inserisco gli attributi aggiuntivi
		// ... (serve ancora???)

		// OFFERTA 
		offerta.setImportoBaseGara(gara.getImporto());
		if (gara.getImportoNonSoggettoRibasso() != null && gara.getImportoNonSoggettoRibasso() > 0)
			offerta.setImportoNonSoggettoRibasso(gara.getImportoNonSoggettoRibasso());
		if (gara.getImportoSicurezza() != null && gara.getImportoSicurezza() > 0)
			offerta.setImportoSicurezza(gara.getImportoSicurezza());
		if (gara.getImportoOneriProgettazione() != null && gara.getImportoOneriProgettazione() > 0)
			offerta.setImportoOneriProgettazione(gara.getImportoOneriProgettazione());
		
		if (getImportoOfferto() != null && isImportoOffertoVisible()) {
			if (report || this.getDocumenti().getChiaveSessione() == null) {
				offerta.setImportoOfferto(getImportoOfferto());
				offerta.setImportoOffertoLettere(UtilityNumeri.getDecimaleInLinguaItaliana(getImportoOfferto(), 2, 5));
			} else {
				offerta.setImportoOffertoCifrato(SymmetricEncryptionUtils.translate(cipher, getImportoOfferto().toString().getBytes()));
				offerta.setImportoOffertoLettere(Base64.encodeBase64String(SymmetricEncryptionUtils.translate(cipher, UtilityNumeri.getDecimaleInLinguaItaliana(getImportoOfferto(), 2, 5).getBytes())));
			}
		}

		// in caso di ribasso pesato imposta il ribasso come totale dei ribassi pesati 
		if(ribassiPesati &&
		   (gara.getModalitaAggiudicazione() != null && 
		    (gara.getModalitaAggiudicazione() == 5 || gara.getModalitaAggiudicazione() == 14))) 
		{
			setRibasso(BigDecimal.valueOf(new Double(totaleRibassi))
						.setScale(this.numDecimaliRibasso.intValue(), BigDecimal.ROUND_HALF_UP).doubleValue() );
		}
			 
		if (getRibasso() != null) {
			if (report || this.getDocumenti().getChiaveSessione() == null) {
				offerta.setPercentualeRibasso(getRibasso());
				offerta.setPercentualeRibassoLettere(UtilityNumeri.getDecimaleInLinguaItaliana(getRibasso(), 1,
						this.numDecimaliRibasso.intValue()));
			} else {
				offerta.setPercentualeRibassoCifrato(SymmetricEncryptionUtils.translate(cipher, getRibasso().toString().getBytes()));
				offerta.setPercentualeRibassoLettere(Base64.encodeBase64String(SymmetricEncryptionUtils.translate(cipher, UtilityNumeri.getDecimaleInLinguaItaliana(getRibasso(), 1,
						this.numDecimaliRibasso.intValue()).getBytes())));
			}
		}
		
		if (getAumento() != null && isPercentualeAumentoVisible()) {
			if (report || this.getDocumenti().getChiaveSessione() == null) {
				offerta.setPercentualeAumento(getAumento());
				offerta.setPercentualeAumentoLettere(UtilityNumeri.getDecimaleInLinguaItaliana(getAumento(), 1,
						this.numDecimaliRibasso.intValue()));
			} else {
				offerta.setPercentualeAumentoCifrato(SymmetricEncryptionUtils.translate(cipher, getAumento().toString().getBytes()));
				offerta.setPercentualeAumentoLettere(Base64.encodeBase64String(SymmetricEncryptionUtils.translate(cipher, UtilityNumeri.getDecimaleInLinguaItaliana(getAumento(), 1,
						this.numDecimaliRibasso.intValue()).getBytes())));

			}
		}		
		
		if(getCostiSicurezzaAziendali() != null) {
			offerta.setCostiSicurezzaAziendali(getCostiSicurezzaAziendali());
		}
		
		if(getImportoManodopera() != null) {
			offerta.setImportoManodopera(getImportoManodopera());
		}

		if(getPercentualeManodopera() != null) {
			offerta.setPercentualeManodopera(getPercentualeManodopera());
		}
		
		// PERMUTA E CANONE ASSISTENZA 
		if(isImportoOffertoPerPermutaVisible()){
			if(getImportoOffertoPerPermuta()!= null){
				if (report || this.getDocumenti().getChiaveSessione() == null) {
					offerta.setImportoOffertoPerPermuta(getImportoOffertoPerPermuta());
				}else{
					offerta.setImportoOffertoPerPermutaCifrato(SymmetricEncryptionUtils.translate(cipher, getImportoOffertoPerPermuta().toString().getBytes()));
				}
			}
		}
		
		if(isImportoOffertoPerCanoneAssistenzaVisible()){
			if(getImportoOffertoCanoneAssistenza()!=null){
				if (report || this.getDocumenti().getChiaveSessione() == null) {
					offerta.setImportoOffertoPerCanoneAssistenza(getImportoOffertoCanoneAssistenza());
				}else{
					offerta.setImportoOffertoPerCanoneAssistenzaCifrato(SymmetricEncryptionUtils.translate(cipher, getImportoOffertoCanoneAssistenza().toString().getBytes()));
				}
			}
		}

		// CRITERI VALUTAZIONE
		ListaCriteriValutazioneType criteriValutazione = offerta.addNewListaCriteriValutazione();
		if(this.listaCriteriValutazione != null) {
			for (int i = 0; i < this.listaCriteriValutazione.size(); i++) {
				if(this.listaCriteriValutazione.get(i).getTipo().intValue() == PortGareSystemConstants.CRITERIO_ECONOMICO) {
					AttributoGenericoType criterio = criteriValutazione.addNewCriterioValutazione();
					if(report){
						criterio.setCodice( this.listaCriteriValutazione.get(i).getDescrizione() );
						
						Double valore = null;
						try {
							valore = Double.parseDouble(this.listaCriteriValutazione.get(i).getValore());
						} catch(Throwable ex)  {
							valore = null;
						}						
						if(valore != null) {  
							criterio.setValoreLettere( UtilityNumeri.getDecimaleInLinguaItaliana(valore, 2, 5) );
						}						
					} else {	
						criterio.setCodice( this.listaCriteriValutazione.get(i).getCodice() );
					}
					criterio.setTipo( this.listaCriteriValutazione.get(i).getFormato().intValue() );		
					WizardOffertaTecnicaHelper.setValoreCriterioValutazioneXml(
							criterio, this.listaCriteriValutazione.get(i), (report ? null : cipher));
				}
			}
		}
		
		// PASSOE
		if(StringUtils.isNotEmpty(this.passoe)) { 
			offerta.setPassoe(this.passoe);
		}

		if (this.pdfUUID != null) {
			offerta.setUuid(this.pdfUUID);
		}

		// FIRMATARI 
		this.addFirmatariBusta(bustaEconomica);		

		// DOCUMENTAZIONE
		ListaDocumentiType listaDocumenti = bustaEconomica.addNewDocumenti();
		this.documenti.addDocumenti(listaDocumenti, attachFileContents);

		document.documentProperties().setEncoding("UTF-8");
		return document;
	}

	
	public boolean isImportoOneriProgettazioneVisible() {
		return gara.getImportoOneriProgettazione() > 0;
	}

	public boolean isImportoOffertoVisible() {
		return (((this.gara.getModalitaAggiudicazione() == 5) || (this.gara.getModalitaAggiudicazione() == 6)
				|| (this.gara.getModalitaAggiudicazione() == 13) || (this.gara.getModalitaAggiudicazione() == 14)) && this.esistonoVociDettaglioOffertaPrezzi)
				|| isImportoOffertoEditable();
	}

	public boolean isImportoOffertoEditable() {
		return ((this.gara.getModalitaAggiudicazione() == 1 || this.gara.getModalitaAggiudicazione() == 13) && this.gara.getTipoRibasso() == 2)
				|| (this.gara.getModalitaAggiudicazione() == 6 && !this.esistonoVociDettaglioOffertaPrezzi)
				|| ((this.gara.getModalitaAggiudicazione() == 5 || this.gara.getModalitaAggiudicazione() == 14)
					&& !this.esistonoVociDettaglioOffertaPrezzi && this.gara.getTipoRibasso() == 2)
				|| (this.gara.getModalitaAggiudicazione() == 17 && this.gara.getTipoRibasso() == 2);
	}

	public boolean isImportoOffertoMandatory() {
		return isImportoOffertoEditable();
	}
	
	public boolean isComprensivoNonSoggettiARibasso() {
		return this.gara.getImportoNonSoggettoRibasso() > 0;
	}

	public boolean isComprensivoOneriSicurezza() {
		return (this.gara.getImportoSicurezza() > 0) && (this.gara.isOffertaComprensivaSicurezza());
	}

	public boolean isNettoOneriSicurezza() {
		return (this.gara.getImportoSicurezza() > 0) && (!this.gara.isOffertaComprensivaSicurezza());
	}

	public boolean isComprensivoOneriProgettazione() {
		return this.gara.getImportoOneriProgettazione() > 0;
	}

	public boolean isNonSuperioreAImportoBaseAsta() {
		return this.gara.isAmmessaOffertaInAumento();
	}

	public double getValoreMassimoImportoOfferto() {
		if (this.gara.isOffertaComprensivaSicurezza())
			return this.gara.getImporto();
		return this.gara.getImporto() - this.gara.getImportoSicurezza();
	}

	public boolean isPercentualeRibassoEditable() {
		return ((this.gara.getModalitaAggiudicazione() == 1 || this.gara.getModalitaAggiudicazione() == 13) && this.gara.getTipoRibasso() == 1)
				|| ((this.gara.getModalitaAggiudicazione() == 5 || this.gara.getModalitaAggiudicazione() == 14) && this.gara.getTipoRibasso() == 1)
				|| (this.gara.getModalitaAggiudicazione() == 17 && this.gara.getTipoRibasso() == 1);
	}

	public boolean isPercentualeRibassoMandatory() {
		return isPercentualeRibassoEditable() && !isPercentualeAumentoVisible();
	}

	public boolean isPercentualeAumentoVisible() {
		return this.gara.isAmmessaOffertaInAumento() && isPercentualeAumentoEditable();
	}

	public boolean isPercentualeAumentoEditable() {
		return ((this.gara.getModalitaAggiudicazione() == 1 || this.gara.getModalitaAggiudicazione() == 13) && this.gara.getTipoRibasso() == 1)
				|| ((this.gara.getModalitaAggiudicazione() == 5 || this.gara.getModalitaAggiudicazione() == 14) && this.gara.getTipoRibasso() == 1)
				|| (this.gara.getModalitaAggiudicazione() == 17 && this.gara.getTipoRibasso() == 1);
	}

	public boolean isImportoNonSoggettoARibasso() {
		return this.gara.getImportoNonSoggettoRibasso() > 0;
	}
	
	public boolean isCostiSicurezzaVisible() {
		return ("1").equals(this.gara.getRichiestaManodopera());
	}
	
	public boolean isCostiManodoperaVisible() {
		return ("1").equals(this.gara.getRichiestaManodopera());
	}
	
	public boolean isPercentualeManodoperaVisible() {
		// 1=importo, 2=percentuale
		return (this.gara.getModalitaManodopera() != null && this.gara.getModalitaManodopera() == 2);
	}
	
	// --- PERMUTA E ASSISTENZA ---
	public boolean isImportoOffertoPerPermutaVisible(){
		return this.gara.isOffertaPerPermuta();
	}
	
	public boolean isImportoOffertoPerCanoneAssistenzaVisible(){
		return this.gara.isOffertaPerCanoneAssistenza();
	}
	
	public boolean isCriteriValutazioneVisibili() {
		return WizardOffertaTecnicaHelper.isCriteriValutazioneVisibili(
				PortGareSystemConstants.CRITERIO_ECONOMICO, 
				this.gara, 
				this.listaCriteriValutazione);
	}

	public boolean isCriteriValutazioneEditabili() {
		return true;
	}

	public void deleteDocumentoOffertaEconomica(
			BaseAction action,
			IEventManager eventManager) 
	{
		Event evento = null;
		String message = "";
		if(action != null && eventManager != null) {
			evento = new Event();
		}
		
		// Scorporato in due metodi diversi a causa di NullPointerException 
		// su file.exists()/file.delete() partendo da cifrata con prezzi unitari 
		// riletta da comunicazione FS11R + disallineamento liste + mancata 
		// rimozione offerta economica a modifica prezzi unitari
		WizardDocumentiBustaHelper documentiHelper = this.getDocumenti();
		boolean offertaEconomicaRimossa = false;
		
		if(this.idOfferta != null && this.idOfferta.longValue() > 0) {
			for (int i = 0; i < documentiHelper.getDocRichiesti().size() && !offertaEconomicaRimossa; i++) {
				if(documentiHelper.getDocRichiestiId().get(i) != null 
				   && documentiHelper.getDocRichiestiId().get(i).longValue() == this.idOfferta.longValue()) { 				
					// traccia l'evento di eliminazione di un file...
					if(evento != null) {
						message += "cancellazione forzata offerta per variazione dati inseriti, " +
								   "file=" + documentiHelper.getDocRichiestiFileName().get(i) + ", " +
							       "dimensione=" + documentiHelper.getDocRichiestiSize().get(i) + "KB";
					}
					
					// elimina il documento ed il file associato (in chiaro o cifrato)
					documentiHelper.removeDocRichiesto(i);
					documentiHelper.setDocOffertaPresente(false);
					offertaEconomicaRimossa = true;
				}
			}
		}
		
		if(evento != null && offertaEconomicaRimossa) {
			evento.setUsername(action.getCurrentUser().getUsername());
			evento.setDestination(documentiHelper.getCodice());
			evento.setLevel(Event.Level.INFO);
			evento.setEventType(PortGareEventsConstants.DELETE_FILE);
			evento.setIpAddress(action.getCurrentUser().getIpAddress());
			evento.setSessionId(action.getRequest().getSession().getId());
			evento.setMessage("Busta economica : " + message);
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
	 * Copia l'ultimo set di firmatari inseriti nelle liste di una busta 
	 * riepilogativa 
	 */
	public void copiaUltimiFirmatariInseriti2Busta(
			RiepilogoBusteHelper riepilogo) 
	{
		if(riepilogo != null) {
			riepilogo.getUltimiFirmatariInseriti().clear();
			for(int i = 0; i < this.getComponentiRTI().size(); i++) {
				SoggettoFirmatarioImpresaHelper firmatario = this.componentiRTI.getFirmatario(this.componentiRTI.get(i));
				riepilogo.getUltimiFirmatariInseriti().add(firmatario);
			}
		}
	}

	/**
	 * calcolo il totale di  
	 * importo di sicurezza, importo non soggetto a ribasso e importo oneri di progettazione  
	 */
	public double getImportoSicurezzaNonSoggettoRibassoOneriProgettazione() {
		double tot = 0;
		if (this.gara.isOffertaComprensivaSicurezza() && this.gara.getImportoSicurezza() != null) {
			tot += this.gara.getImportoSicurezza();
		}
		if (this.gara.getImportoNonSoggettoRibasso() != null) {
			tot += this.gara.getImportoNonSoggettoRibasso();
		}
		if (!this.gara.isOneriSoggettiRibasso() && this.gara.getImportoOneriProgettazione() != null) {
			tot += this.gara.getImportoOneriProgettazione();
		}
		return tot;
	}

}
