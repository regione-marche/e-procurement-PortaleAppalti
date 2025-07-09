package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import com.agiletec.apsadmin.system.BaseAction;
import it.eldasoft.sil.portgare.datatypes.*;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.www.sil.WSGareAppalto.AttributoAggiuntivoOffertaType;
import it.eldasoft.www.sil.WSGareAppalto.CriterioValutazioneOffertaType;
import it.eldasoft.www.sil.WSGareAppalto.StazioneAppaltanteBandoType;
import it.eldasoft.www.sil.WSGareAppalto.VoceDettaglioOffertaType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaDocumenti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaEconomica;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StringUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.FirmatarioBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.helpers.WizardOffertaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.security.SymmetricEncryptionUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.CalendarValidator;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import javax.crypto.Cipher;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.util.*;


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
	private String[] dataConsegnaOfferta;
	private String[] tipo;
	private String[] note;
	
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

	
	private String passoe;

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
	
	public String[] getDataConsegnaOfferta() {
		return dataConsegnaOfferta;
	}

	public void setDataConsegnaOfferta(String[] dataConsegnaOfferta) {
		this.dataConsegnaOfferta = dataConsegnaOfferta;
	}

	public String[] getTipo() {
		return tipo;
	}

	public void setTipo(String[] tipo) {
		this.tipo = tipo;
	}

	public String[] getNote() {
		return note;
	}

	public void setNote(String[] note) {
		this.note = note;
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
			this.dataConsegnaOfferta = new String[vociDettaglio.size()];
			this.tipo = new String[vociDettaglio.size()];
			this.note =	new String[vociDettaglio.size()];
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
		this.dataConsegnaOfferta = null;
		this.tipo = null;
		this.note = null;
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
		Cipher cipher = this.getEncoder();

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
				if(vociDettaglio.get(i).getPrezzoUnitarioBaseGara() != null) {
					lavorazione.setPrezzoUnitarioBaseGara(vociDettaglio.get(i).getPrezzoUnitarioBaseGara());
				}
				
				if (ribassiPesati && vociDettaglio.get(i).getRibassoPercentuale() != null) {
					// ricalcola il prezzo unitario tenendo conto del ribasso percentuale
					double przUni = (vociDettaglio.get(i).getPrezzoUnitarioBaseGara() != null 
									 ? vociDettaglio.get(i).getPrezzoUnitarioBaseGara() : 0);
					prezzoUnitario[i] = BigDecimal
										.valueOf(new Double( przUni * (1 - vociDettaglio.get(i).getRibassoPercentuale() / 100) ))
										.setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue();
					totaleRibassi += vociDettaglio.get(i).getRibassoPesato();
					
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
				
				if("8".equals(gara.getIterGara())) {
					String dtaConsRich = (vociDettaglio.get(i).getDataConsegnaRichiesta() != null
							? CalendarValidator.getInstance().format(vociDettaglio.get(i).getDataConsegnaRichiesta(), "dd/MM/yyyy")
							: "");
					if(StringUtils.isNotEmpty(dtaConsRich)) {
						lavorazione.setDataConsegnaRichiesta(CalendarValidator.getInstance().validate(dtaConsRich, "dd/MM/yyyy"));
					}
					
					if (this.dataConsegnaOfferta[i] != null) {
						Calendar dta = null;
						try {
							dta = CalendarValidator.getInstance().validate(this.dataConsegnaOfferta[i], "dd/MM/yyyy");
						} catch (Exception e) {
							dta = null;
						}
						if (report) {
							lavorazione.setDataConsegnaOfferta(dta);
						} else {
							lavorazione.setDataConsegnaOfferta(dta);
						}
					}
					
					if (this.tipo[i] != null) {
						if (report) {
							lavorazione.setTipo(this.tipo[i]);
						} else {
							lavorazione.setTipo(this.tipo[i]);
						}
					}
					
					if (this.note[i] != null) {
						if (report) {
							lavorazione.setNote(this.note[i]);
						} else {
							lavorazione.setNote(this.note[i]);
						}
					}
					
					if(vociDettaglio.get(i).getQuantitaOfferta() != null && vociDettaglio.get(i).getQuantitaOfferta() == 0.0) {
						lavorazione.setQuantitaOfferta(vociDettaglio.get(i).getQuantitaOfferta());
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
					setValoreCriterioValutazioneXml(
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

	@Override
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
			for (int i = 0; i < documentiHelper.getRequiredDocs().size() && !offertaEconomicaRimossa; i++) {
				if(documentiHelper.getRequiredDocs().get(i) != null
				   && documentiHelper.getRequiredDocs().get(i).getId().longValue() == this.idOfferta.longValue())
				{
					// traccia l'evento di eliminazione di un file...
					if(evento != null) {
						message += "cancellazione forzata offerta per variazione dati inseriti, " +
								   "file=" + documentiHelper.getRequiredDocs().get(i).getFileName() + ", " +
							       "dimensione=" + documentiHelper.getRequiredDocs().get(i).getSize() + "KB";
					}
					
					// elimina il documento ed il file associato (in chiaro o cifrato)
					documentiHelper.removeDocRichiesto(i);
					documentiHelper.setDocOffertaPresente(false);
					offertaEconomicaRimossa = true;
					
					// resetta le info relative al pdf
					datiModificati = true;
					rigenPdf = true;
					pdfUUID = null;
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
		// recupera l'xml della busta economica
		// nel caso xml sia in formato "errato", 
		// converti nel formato corretto!!!
		BustaEconomica bustaEco = (BustaEconomica) busta;
		BustaEconomicaDocument doc = (BustaEconomicaDocument)bustaEco.getBustaDocument();
		BustaEconomicaType xml = (doc != null ? doc.getBustaEconomica() : null);
		ListaDocumentiType documenti = (xml != null ? xml.getDocumenti() : null);
		FirmatarioType[] firmatari = (xml != null ? xml.getFirmatarioArray() : null);
		OffertaEconomicaType offerta = (xml != null ? xml.getOfferta() : null);
		
    	// ************************************************************
    	// inizializza la cifratura, allinea i documenti e i firmatari...
		boolean continua = this.popolaDocumentiFromXml(
    			busta,
    			documenti);
		continua = continua && this.popolaFirmatariFromXml(
    			busta,
    			firmatari);
    	if(!continua || offerta == null) {
    		return;
    	}
    	
		// ************************************************************
    	// popola l'helper con i dati del documento xml...
    	Cipher cipher = this.getDecoder();

    	// estrazione dati step "prezzi unitari"
    	if (offerta.isSetListaComponentiDettaglio()) {
    		Double[] prezziUnitari = new Double[this.getVociDettaglio().size()];
    		Double[] importi = new Double[this.getVociDettaglio().size()];
    		String[] dataConsegnaOfferta = new String[this.getVociDettaglio().size()];
    		String[] tipo = new String[this.getVociDettaglio().size()];
    		String[] note = new String[this.getVociDettaglio().size()];
    		
    		ComponenteDettaglioOffertaType[] componenti = offerta
    			.getListaComponentiDettaglio()
	    		.getComponenteDettaglioArray();
    		
    		Double importoTotale = 0D;
    		
    		// inizializzo la mappa
    		Map<String, String> definizioneAttrAggiuntivi = new HashMap<String, String>();
    		Map<String, String[]> attributiAggiuntivi = new HashMap<String, String[]>();
    		if (this.getAttributiAggiuntivi() != null) {
    			for (AttributoAggiuntivoOffertaType attr : this.getAttributiAggiuntivi()) {
    				String[] valoriAgg = new String[componenti.length];
    				attributiAggiuntivi.put(attr.getCodice(), valoriAgg);
    				definizioneAttrAggiuntivi.put(attr.getCodice(), attr.getFormato());
    			}
    		}

    		for (int i = 0; i < componenti.length; i++) {
    			
    			// cerca l'elemento dell'xml che corrisponde 
    			// alla voce di dettaglio dell'helper
    			int id = componenti[i].getId();
    			int indice = -1;
    			for (int j = 0; j < this.getVociDettaglio().size() && (indice == -1); j++) {
    				if (this.getVociDettaglio().get(j).getId() == id) {
    					indice = j;
    				}
    			}
    			
    			// una volta trovato, prendo il peso e il ribasso pesato e li riporto
    			// nei dati per l'helper
    			if (indice != -1) {
    				// prezzo base di gara
    				if (componenti[i].isSetPrezzoUnitarioBaseGara()) {
    					this.getVociDettaglio().get(indice).setPrezzoUnitarioBaseGara( componenti[i].getPrezzoUnitarioBaseGara() );
    				}
    				
    				// peso
    				if (componenti[i].isSetPeso()) {
    					this.getVociDettaglio().get(indice).setPeso( componenti[i].getPeso() );
    				}
    				
					// ribasso
    				if (componenti[i].isSetRibasso()) {
    					this.getVociDettaglio().get(indice).setRibassoPercentuale( componenti[i].getRibasso() );
    				} else if (this.getDocumenti().getChiaveSessione() != null && componenti[i].isSetRibassoCifrato()) {
    					// caso di cifratura dei dati
    					String ribasso = new String(
								SymmetricEncryptionUtils.translate(cipher, componenti[i].getRibassoCifrato()));
    					this.getVociDettaglio().get(indice).setRibassoPercentuale( Double.valueOf(ribasso) );
    				}

    				// ribasso pesato  
    				if (componenti[i].isSetRibassoPesato()) {
    					this.getVociDettaglio().get(indice).setRibassoPesato( componenti[i].getRibassoPesato() );
    				} else if (this.getDocumenti().getChiaveSessione() != null && componenti[i].isSetRibassoPesatoCifrato()) {
    					// caso di cifratura dei dati
    					String ribassoPesato = new String(
								SymmetricEncryptionUtils.translate(cipher, componenti[i].getRibassoPesatoCifrato()));
    					BigDecimal bdRibassoPesato = BigDecimal.valueOf( Double.valueOf(ribassoPesato) )
														.setScale(this.getNumDecimaliRibasso().intValue(), BigDecimal.ROUND_HALF_UP);
    					this.getVociDettaglio().get(indice).setRibassoPesato( bdRibassoPesato.doubleValue() );
    				}
    				
    				// prezzo unitario
    				if (componenti[i].isSetPrezzoUnitario()) {
    					prezziUnitari[indice] = componenti[i].getPrezzoUnitario();
    				} else if (this.getDocumenti().getChiaveSessione() != null && componenti[i].isSetPrezzoUnitarioCifrato()) {
    					// caso di cifratura dei dati
						String prezzoUnitarioDecifrato = new String(
								SymmetricEncryptionUtils.translate(cipher, componenti[i].getPrezzoUnitarioCifrato()));
    					prezziUnitari[indice] = Double.valueOf(prezzoUnitarioDecifrato);
    				}
    				
    				// importo
    				if (componenti[i].isSetImporto()) {
    					importi[indice] = componenti[i].getImporto();
    					importoTotale += importi[indice];
    				} else if (this.getDocumenti().getChiaveSessione() != null && componenti[i].isSetImportoCifrato()) {
    					// caso di cifratura dei dati
						String importoDecifrato = new String(
								SymmetricEncryptionUtils.translate(cipher, componenti[i].getImportoCifrato()));
    					importi[indice] = Double.valueOf(importoDecifrato);
    					importoTotale += importi[indice];
    				}
    				
    				if("8".equals(gara.getIterGara())) {
	    				// data consegna offerta
	    				if (componenti[i].isSetDataConsegnaOfferta()) {
	    					String dta = null;
	    					try {
	    						dta = CalendarValidator.getInstance().format(componenti[i].getDataConsegnaOfferta(), "dd/MM/yyyy");
	    					} catch (Exception e) {
	    						dta = null;
							}
	    					if(componenti[i].getDataConsegnaOfferta() != null) {
	    						this.getVociDettaglio().get(indice).setDataConsegnaOfferta(componenti[i].getDataConsegnaOfferta().getTime());
	    					}
	    					dataConsegnaOfferta[indice] = dta;
	    				} 
	    				
	    				// tipo
	    				if (componenti[i].isSetTipo()) {
	    					this.getVociDettaglio().get(indice).setTipo(componenti[i].getTipo());
	    					tipo[indice] = componenti[i].getTipo();
	    				}
	    				
	    				// note
	    				if (componenti[i].isSetNote()) {
	    					this.getVociDettaglio().get(indice).setNote(componenti[i].getNote());
	    					note[indice] = componenti[i].getNote();
	    				}
	    				
	    				// quantita offerta
	    				if(componenti[i].isSetQuantitaOfferta()) {
	    					this.getVociDettaglio().get(indice).setQuantitaOfferta(componenti[i].getQuantitaOfferta()) ;
						}
					}

    				// carico i valori degli attributi aggiuntivi
    				AttributoGenericoType[] dettaglioOfferta = componenti[i].getAttributoAggiuntivoArray();
    				for (AttributoGenericoType a : dettaglioOfferta) {
    					String codice = a.getCodice();
    					String[] curValues = attributiAggiuntivi.get(codice);
    					if(curValues != null) {
	    					if (a.isSetValoreData()) {
	    						if(a.getValoreData() != null) {
	    							curValues[indice] = DATEFORMAT_DDMMYYYY.format(a.getValoreData().getTime());
	    						} else {
	    							curValues[indice] = null;
	    						}
	    					} else if (a.isSetValoreNumerico()) {
	    						String val = String.valueOf(a.getValoreNumerico());
	    						if (definizioneAttrAggiuntivi.get(codice) == null) {
	    							curValues[indice] = val;
	    						} else {
	    							curValues[indice] = StringUtils.remove(val, ".0");
	    						}
	    					} else if (a.isSetValoreStringa()) {
	    						curValues[indice] = a.getValoreStringa();
	    					}
    					}
    				}
    			}
    		}
    		this.setValoreAttributiAgg(attributiAggiuntivi);
    		this.setPrezzoUnitario(prezziUnitari);
    		this.setImportoUnitario(importi);
    		BigDecimal bdImportoTotaleOffertaPrezziUnitari = BigDecimal
    			.valueOf(new Double(importoTotale)).setScale(5, BigDecimal.ROUND_HALF_UP);
    		this.setTotaleOffertaPrezziUnitari(bdImportoTotaleOffertaPrezziUnitari.doubleValue());    		
    		this.setDataConsegnaOfferta(dataConsegnaOfferta);
    		this.setTipo(tipo);
    		this.setNote(note);
    	}

    	// estrazione dati step "offerta"
    	if (offerta.isSetImportoOfferto()) {
    		this.setImportoOfferto(offerta.getImportoOfferto());
    	} else if (this.getDocumenti().getChiaveSessione() != null && offerta.isSetImportoOffertoCifrato()) {
    		// caso di cifratura dei dati
			String importoOffertoDecifrato = new String(
					SymmetricEncryptionUtils.translate(
							cipher, 
							offerta.getImportoOffertoCifrato()));
			this.setImportoOfferto(Double.valueOf(importoOffertoDecifrato));
    	}
    	
    	if (offerta.isSetPercentualeAumento()) {
    		this.setAumento(offerta.getPercentualeAumento());
    	} else if (this.getDocumenti().getChiaveSessione() != null && offerta.isSetPercentualeAumentoCifrato()) {
    		// caso di cifratura dei dati
			String aumento = new String(
					SymmetricEncryptionUtils.translate(
							cipher, 
							offerta.getPercentualeAumentoCifrato()));
			this.setAumento(Double.valueOf(aumento));
    	}
    	
    	if (offerta.isSetPercentualeRibasso()) {
    		this.setRibasso(offerta.getPercentualeRibasso());
    	} else if (this.getDocumenti().getChiaveSessione() != null && offerta.isSetPercentualeRibassoCifrato()) {
    		// caso di cifratura dei dati
			String ribasso = new String(
					SymmetricEncryptionUtils.translate(
							cipher, 
							offerta.getPercentualeRibassoCifrato()));
			this.setRibasso(Double.valueOf(ribasso));
    	}
    	
    	this.setCostiSicurezzaAziendali(offerta.getCostiSicurezzaAziendali());
    	this.setImportoManodopera(offerta.getImportoManodopera());	
    	this.setPercentualeManodopera(offerta.getPercentualeManodopera());

    	// --- PERMUTA E ASSISTENZA ---
    	this.setImportoOffertoPerPermuta(offerta.getImportoOffertoPerPermuta());
    	if (this.getDocumenti().getChiaveSessione() != null && offerta.isSetImportoOffertoPerPermutaCifrato()){
    		// caso di cifratura dei dati
			String importoOffertoPerPermuta = new String(
					SymmetricEncryptionUtils.translate(
							cipher, 
							offerta.getImportoOffertoPerPermutaCifrato()));
    		BigDecimal bdImportoOffertoPerPermuta = BigDecimal
    			.valueOf(new Double(importoOffertoPerPermuta)).setScale(2, BigDecimal.ROUND_HALF_UP);
    		this.setImportoOffertoPerPermuta(bdImportoOffertoPerPermuta.doubleValue());
    	}

    	this.setImportoOffertoCanoneAssistenza(offerta.getImportoOffertoPerCanoneAssistenza());
    	if (this.getDocumenti().getChiaveSessione() != null && offerta.isSetImportoOffertoPerCanoneAssistenzaCifrato()) {
    		// caso di cifratura dei dati
    		String importoOffertoCanoneAssistenza = new String(
    				SymmetricEncryptionUtils.translate(
    						cipher, 
    						offerta.getImportoOffertoPerCanoneAssistenzaCifrato()));
    		BigDecimal bdImportoOffertoCanoneAssistenza = BigDecimal
    			.valueOf(new Double(importoOffertoCanoneAssistenza)).setScale(2, BigDecimal.ROUND_HALF_UP);
    		this.setImportoOffertoCanoneAssistenza(bdImportoOffertoCanoneAssistenza.doubleValue());
		}

		// -- Criteri di valutazione --
		List<CriterioValutazioneOffertaType> criteriValutazione = this.getListaCriteriValutazione();
		if(offerta.isSetListaCriteriValutazione()) {
			criteriValutazione = popolaCriteriValutazioneFromXml(offerta.getListaCriteriValutazione());
		}
		this.setListaCriteriValutazione(criteriValutazione);

		// PASSOE
		if(offerta.isSetPassoe()) {
			this.setPassoe(offerta.getPassoe());
		}
		
    	if (offerta.isSetUuid()) {
    		this.setPdfUUID(offerta.getUuid());
    	}
    }
	
}
