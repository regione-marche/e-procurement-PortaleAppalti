package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.helpers;

import it.eldasoft.sil.portgare.datatypes.BustaEconomicaType;
import it.eldasoft.sil.portgare.datatypes.BustaTecnicaType;
import it.eldasoft.sil.portgare.datatypes.DocumentazioneBustaType;
import it.eldasoft.sil.portgare.datatypes.FirmatarioType;
import it.eldasoft.sil.portgare.datatypes.IndirizzoType;
import it.eldasoft.www.sil.WSGareAppalto.GaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractWizardHelper;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.ComponentiRTIList;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.FirmatarioBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ISoggettoImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.SoggettoImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.WizardDocumentiBustaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.CalendarValidator;
import org.apache.xmlbeans.XmlObject;

import com.agiletec.apsadmin.system.BaseAction;

/**
 * Helper di memorizzazione dei dati relativi alla gestione della compilazione
 * di un'offerta telematica (economica, tecnica, ...).
 * 
 * @author ...
 */
public class WizardOffertaHelper extends AbstractWizardHelper 
	implements HttpSessionBindingListener, Serializable 
{
	/**
	 * UID 
	 */
	private static final long serialVersionUID = -4233963232292954113L;

	// utilizzati dalla jsp ?
	public static final Integer CHIAVE_PARTITA_IVA 		= 1;
	public static final Integer CHIAVE_CODICE_FISCALE 	= 2;

	/** Stack delle pagine navigate */
	protected Stack<String> stepNavigazione;
	
	protected String stepPrefixPage;
	
	protected WizardDocumentiBustaHelper documenti;

	protected ComponentiRTIList componentiRTI;	// = new ComponentiRTIList();
	
	protected List<FirmatarioBean> listaFirmatariMandataria;	// = new ArrayList<FirmatarioBean>();
	
	protected FirmatarioBean firmatarioSelezionato;	// = new FirmatarioBean();
	
	protected int idFirmatarioSelezionatoInLista;	

	protected WizardDatiImpresaHelper impresa;
	
	protected String nomeBusta;
	
	protected int tipoBusta;
		
	protected String codice;
	
	protected GaraType gara;

	/** Identificativo univoco della comunicazione (valorizzata nel caso di completamento) */	
	protected Long idComunicazione;
	
	/** Pdf generati per l'offerta */
	protected List<File> pdfGenerati;

	/** UUID generato e attribuito come Keywords nell'ultimo PDF */
	protected String pdfUUID;
	
	protected boolean rigenPdf;

	protected boolean datiModificati;

	protected Long idOfferta;
	
	protected String progressivoOfferta;
	
	
	public WizardDatiImpresaHelper getImpresa() {
		return impresa;
	}

	public void setImpresa(WizardDatiImpresaHelper impresa) {
		this.impresa = impresa;
		this.updateDocumenti();
	}
	
	public GaraType getGara() {
		return gara;
	}

	public void setGara(GaraType gara) {
		this.gara = gara;
		this.updateDocumenti();
	}
	
	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
		this.updateDocumenti();
	}

	public Stack<String> getStepNavigazione() {
		return stepNavigazione;
	}

	public void setStepNavigazione(Stack<String> stepNavigazione) {
		this.stepNavigazione = stepNavigazione;
	}

	public List<File> getPdfGenerati() {
		return pdfGenerati;
	}

	public WizardDocumentiBustaHelper getDocumenti() {
		return documenti;
	}

	public void setDocumenti(WizardDocumentiBustaHelper documenti) {
		this.documenti = documenti;
		this.updateDocumenti();
	}

	public Long getIdComunicazione() {
		return idComunicazione;
	}

	public void setIdComunicazione(Long idComunicazione) {
		this.idComunicazione = idComunicazione;
		this.updateDocumenti();
	}

	public String getNomeBusta() {
		return nomeBusta;
	}
	
	public int getTipoBusta() {
		return tipoBusta;
	}
	
	public ComponentiRTIList getComponentiRTI() {
		return componentiRTI;
	}

	public void setComponentiRTI(ComponentiRTIList componentiRTI) {
		this.componentiRTI = componentiRTI;
	}

	public List<FirmatarioBean> getListaFirmatariMandataria() {
		return listaFirmatariMandataria;
	}

	public void setListaFirmatariMandataria(List<FirmatarioBean> listaFirmatariMandataria) {
		this.listaFirmatariMandataria = listaFirmatariMandataria;
	}

	public String getPdfUUID() {
		return pdfUUID;
	}

	public void setPdfUUID(String pdfUUID) {
		this.pdfUUID = pdfUUID;
	}

	public boolean isRigenPdf() {
		return rigenPdf;
	}

	public void setRigenPdf(boolean rigenPdf) {
		this.rigenPdf = rigenPdf;
	}

	public boolean isDatiModificati() {
		return datiModificati;
	}

	public void setDatiModificati(boolean datiModificati) {
		this.datiModificati = datiModificati;
	}
	
	public FirmatarioBean getFirmatarioSelezionato() {
		return firmatarioSelezionato;
	}

	public void setFirmatarioSelezionato(FirmatarioBean firmatarioSelezionato) {
		this.firmatarioSelezionato = firmatarioSelezionato;
	}

	public int getIdFirmatarioSelezionatoInLista() {
		return idFirmatarioSelezionatoInLista;
	}

	public void setIdFirmatarioSelezionatoInLista(int idFirmatarioSelezionatoInLista) {
		this.idFirmatarioSelezionatoInLista = idFirmatarioSelezionatoInLista;
	}
	
	public Long getIdOfferta() {
		return idOfferta;
	}

	public void setIdOfferta(Long idOfferta) {
		this.idOfferta = idOfferta;
	}

	public String getProgressivoOfferta() {
		return progressivoOfferta;
	}

	public void setProgressivoOfferta(String progressivoOfferta) {
		this.progressivoOfferta = progressivoOfferta;
	}

	/**
	 * costruttore 
	 */
	public WizardOffertaHelper(String sessionKeyHelperId, boolean legacyPrevNext) {
		super(sessionKeyHelperId, legacyPrevNext);
	}

	public WizardOffertaHelper(int tipoBusta, String nomeBusta) {
		super(null, false);
		this.gara = new GaraType();
		this.impresa = null;
		this.nomeBusta = nomeBusta;
		this.tipoBusta = tipoBusta;
		this.documenti = new WizardDocumentiBustaHelper(tipoBusta);
		this.componentiRTI = new ComponentiRTIList();
		this.stepNavigazione = new Stack<String>();
		this.stepPrefixPage = null;
		this.idComunicazione = null;
		this.pdfGenerati = new ArrayList<File>();
		this.pdfUUID = null;
		this.datiModificati = false;
		this.rigenPdf = false;
		this.listaFirmatariMandataria = new ArrayList<FirmatarioBean>();
		this.firmatarioSelezionato = new FirmatarioBean();
		//this.idFirmatarioSelezionatoInLista = -1;
		this.idOfferta = (long) -1;
		this.progressivoOfferta = null;
	}	
	
	@Override
	public void valueBound(HttpSessionBindingEvent arg0) {
	}

	/**
	 * Alla scadenza della sessione o alla rimozione del contenitore dalla
	 * sessione si rimuovono i file creati per questa gestione
	 * 
	 * @param arg0
	 *            l'evento di sessione scaduta
	 */
	@Override
	public void valueUnbound(HttpSessionBindingEvent arg0) {
		this.documenti.valueUnbound(arg0); 

		Iterator<File> iter = this.pdfGenerati.listIterator();
		while (iter.hasNext()) {
			File file = iter.next();
			if (file != null && file.exists()) {
				file.delete();
			}
		}
	}
	
	/**
	 * sincronizza i dati generali dell'helper documenti con i dati dell'helper offerta 
	 */
	private void updateDocumenti() {
		if(this.documenti != null) {
			this.documenti.setCodice(this.codice);
			if(this.gara != null) {
				this.documenti.setCodiceGara(this.gara.getCodice());
			}
			this.documenti.setImpresa(this.impresa);
			this.documenti.setIdComunicazione(idComunicazione);
		}	
	}

	@Override
	public void fillStepsNavigazione() {
		// da ridefinire nella classe figlia
	}
	
	/**
	 * restituisce, in base allo step corrente, lo step successiva 
	 * relativa al wizard di iscrizione/aggiornamento ad elenco operarori
	 * Se "currentStep" è vuoto restituisce il primo step del wizard
	 * i.e.: getNextAction[NEXTSTEP]"  
	 */
	public String getNextAction(String currentStep) {
		String target = (StringUtils.isEmpty(currentStep)
				? this.getStepNavigazione().firstElement()		// restitusci la prima pagina del wizard
				: this.getNextStepNavigazione(currentStep) );	// restitusci la prossima pagina del wizard
		return this.stepPrefixPage + StringUtils.capitalize(target);
	}
	
	/**
	 * restituisce, in base allo step corrente, lo step previuos 
	 * relativa al wizard di iscrizione/aggiornamento ad elenco operarori
	 * i.e.: getPreviousAction[NEXTSTEP]"  
	 */
	public String getPreviousAction(String currentStep) {
		return this.stepPrefixPage + StringUtils.capitalize( this.getPreviousStepNavigazione(currentStep) );
	}

	/**
	 * ... 
	 */
	public String getCurrentAction(String currentStep) {
		return this.stepPrefixPage + StringUtils.capitalize(currentStep);
	}

	/**
	 * Ritorna lo step di navigazione successivo allo step in input
	 * 
	 * @param stepAttuale
	 *            step attuale
	 * @return step successivo previsto dopo l'attuale, null se non previsto
	 */
	public String getNextStepNavigazione(String stepAttuale) {
		String step = null;
		int posizione = this.stepNavigazione.indexOf(stepAttuale);
		if (posizione != -1 && posizione + 1 < this.stepNavigazione.size()) {
			step = this.stepNavigazione.elementAt(posizione + 1);
		}
		return step;
	}

	/**
	 * Ritorna lo step di navigazione precedente lo step in input
	 * 
	 * @param stepAttuale
	 *            step attuale
	 * @return step precedente previsto dopo l'attuale, null se non previsto
	 */
	public String getPreviousStepNavigazione(String stepAttuale) {
		String step = null;
		int posizione = this.stepNavigazione.indexOf(stepAttuale);
		if (posizione != -1 && posizione - 1 >= 0) {
			step = this.stepNavigazione.elementAt(posizione - 1);
		}
		return step;
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
	 * @param report
	 * 			  ...
	 * @return documento XML aggiornato
	 * @throws IOException
	 */
	public XmlObject getXmlDocument(XmlObject document, boolean attachFileContents, boolean report) 
		throws IOException, GeneralSecurityException 
	{
		// da ridefinire nella classe figlia
		return null;
	}

	/**
	 * aggiunge i firmatari alla busta xml 
	 * utilizzata da getXmlDocument
	 */
	protected void addFirmatariBusta(DocumentazioneBustaType busta) {
		if (componentiRTI.size() == 0) {
			// UNICO FIRMATARIO
			ISoggettoImpresa soggettoFromLista = null;
			FirmatarioType firmatario = addNewFirmatario(busta);

			if (!impresa.isLiberoProfessionista()) {
				if (this.firmatarioSelezionato.getIndex() == null && this.firmatarioSelezionato.getLista() == null) {
					// se non ho nessun firmatario selezionato (es. salvataggio
					// step prezzi unitari), prespopolo con il primo della lista
					// dei firmatari per la mandataria
					if(this.listaFirmatariMandataria.size() > 0) {
						if (CataloghiConstants.LISTA_LEGALI_RAPPRESENTANTI.equals(this.listaFirmatariMandataria.get(0).getLista())) {
							soggettoFromLista = impresa.getLegaliRappresentantiImpresa().get(this.listaFirmatariMandataria.get(0).getIndex());
						} else if (CataloghiConstants.LISTA_DIRETTORI_TECNICI.equals(this.listaFirmatariMandataria.get(0).getLista())) {
							soggettoFromLista = impresa.getDirettoriTecniciImpresa().get(this.listaFirmatariMandataria.get(0).getIndex());
						} else {
							soggettoFromLista = impresa.getAltreCaricheImpresa().get(this.listaFirmatariMandataria.get(0).getIndex());
						}
					}
				} else {
					if (CataloghiConstants.LISTA_LEGALI_RAPPRESENTANTI.equals(this.firmatarioSelezionato.getLista())) {
						soggettoFromLista = impresa.getLegaliRappresentantiImpresa().get(this.firmatarioSelezionato.getIndex());
					} else if (CataloghiConstants.LISTA_DIRETTORI_TECNICI.equals(this.firmatarioSelezionato.getLista())) {
						soggettoFromLista = impresa.getDirettoriTecniciImpresa().get(this.firmatarioSelezionato.getIndex());
					} else {
						soggettoFromLista = impresa.getAltreCaricheImpresa().get(this.firmatarioSelezionato.getIndex());
					}

					firmatario.setNome(soggettoFromLista.getNome());
					firmatario.setCognome(soggettoFromLista.getCognome());
					firmatario.setCodiceFiscaleFirmatario(soggettoFromLista.getCodiceFiscale());
					firmatario.setComuneNascita(soggettoFromLista.getComuneNascita());
					firmatario.setDataNascita(CalendarValidator.getInstance().validate(soggettoFromLista.getDataNascita(), "dd/MM/yyyy"));
					firmatario.setProvinciaNascita(soggettoFromLista.getProvinciaNascita());
					firmatario.setQualifica(soggettoFromLista.getSoggettoQualifica());
					firmatario.setSesso(soggettoFromLista.getSesso());
					firmatario.setComuneNascita(soggettoFromLista.getComuneNascita());
					firmatario.setCodiceFiscaleImpresa(this.impresa.getDatiPrincipaliImpresa().getCodiceFiscale());
					firmatario.setPartitaIVAImpresa(this.impresa.getDatiPrincipaliImpresa().getPartitaIVA());
					firmatario.setTipoImpresa(this.impresa.getDatiPrincipaliImpresa().getTipoImpresa());
					IndirizzoType residenza = firmatario.addNewResidenza();
					residenza.setCap(soggettoFromLista.getCap());
					residenza.setIndirizzo(soggettoFromLista.getIndirizzo());
					residenza.setNumCivico(soggettoFromLista.getNumCivico());
					residenza.setComune(soggettoFromLista.getComune());
					residenza.setNazione(soggettoFromLista.getNazione());
					residenza.setProvincia(soggettoFromLista.getProvincia());
				}
			} else {
				// LIBERO PROFESSIONISTA
				if (StringUtils.isNotEmpty(this.impresa.getAltriDatiAnagraficiImpresa().getCognome())) {
					firmatario.setNome(this.impresa.getAltriDatiAnagraficiImpresa().getNome());
					firmatario.setCognome(this.impresa.getAltriDatiAnagraficiImpresa().getCognome());
				} else {
					firmatario.setNome(this.impresa.getDatiPrincipaliImpresa().getRagioneSociale());
					firmatario.setCognome(null);
				}
				firmatario.setCodiceFiscaleFirmatario(this.impresa.getDatiPrincipaliImpresa().getCodiceFiscale());
				firmatario.setComuneNascita(this.impresa.getAltriDatiAnagraficiImpresa().getComuneNascita());
				firmatario.setDataNascita(CalendarValidator.getInstance().validate(this.impresa.getAltriDatiAnagraficiImpresa().getDataNascita(),
						"dd/MM/yyyy"));
				firmatario.setProvinciaNascita(this.impresa.getAltriDatiAnagraficiImpresa().getProvinciaNascita());
				firmatario.setQualifica("Libero professionista");
				firmatario.setSesso(this.impresa.getAltriDatiAnagraficiImpresa().getSesso());
				firmatario.setComuneNascita(this.impresa.getAltriDatiAnagraficiImpresa().getComuneNascita());
				firmatario.setCodiceFiscaleImpresa(this.impresa.getDatiPrincipaliImpresa().getCodiceFiscale());
				firmatario.setPartitaIVAImpresa(this.impresa.getDatiPrincipaliImpresa().getPartitaIVA());
				firmatario.setTipoImpresa(this.impresa.getDatiPrincipaliImpresa().getTipoImpresa());
				firmatario.setRagioneSociale(this.impresa.getDatiPrincipaliImpresa().getRagioneSociale());
				IndirizzoType residenza = firmatario.addNewResidenza();
				residenza.setCap(this.impresa.getDatiPrincipaliImpresa().getCapSedeLegale());
				residenza.setIndirizzo(this.impresa.getDatiPrincipaliImpresa().getIndirizzoSedeLegale());
				residenza.setNumCivico(this.impresa.getDatiPrincipaliImpresa().getNumCivicoSedeLegale());
				residenza.setComune(this.impresa.getDatiPrincipaliImpresa().getComuneSedeLegale());
				residenza.setNazione(this.impresa.getDatiPrincipaliImpresa().getNazioneSedeLegale());
				residenza.setProvincia(this.impresa.getDatiPrincipaliImpresa().getProvinciaSedeLegale());
			}
		} else {
			// FIRMATARI IN CASO DI RTI
			int idxMandataria = this.componentiRTI.getMandataria(impresa.getDatiPrincipaliImpresa());			
			for (int i = 0; i < componentiRTI.size(); i++) {
				SoggettoImpresaHelper currentFirmatario = componentiRTI.getFirmatario(componentiRTI.get(i));
			
				FirmatarioType firmatario = addNewFirmatario(busta);
				
				if (i == idxMandataria) {
					// CASO 1 mandataria
					if (impresa.isLiberoProfessionista()) {
						// CASO 1.1 : mandataria di tipo libero professionista
						firmatario.setNome(impresa.getDatiPrincipaliImpresa().getRagioneSociale());
						firmatario.setQualifica("libero professionista");
						firmatario.setCodiceFiscaleFirmatario(impresa.getDatiPrincipaliImpresa().getCodiceFiscale());
						firmatario.setComuneNascita(impresa.getAltriDatiAnagraficiImpresa().getComuneNascita());
						firmatario.setDataNascita(CalendarValidator.getInstance().validate(
								impresa.getAltriDatiAnagraficiImpresa().getDataNascita(), "dd/MM/yyyy"));
						firmatario.setProvinciaNascita(impresa.getAltriDatiAnagraficiImpresa().getProvinciaNascita());
						firmatario.setQualifica("Libero professionista");
						firmatario.setSesso(impresa.getAltriDatiAnagraficiImpresa().getSesso());
						firmatario.setComuneNascita(impresa.getAltriDatiAnagraficiImpresa().getComuneNascita());
						firmatario.setCodiceFiscaleImpresa(impresa.getDatiPrincipaliImpresa().getCodiceFiscale());
						firmatario.setPartitaIVAImpresa(impresa.getDatiPrincipaliImpresa().getPartitaIVA());
						firmatario.setTipoImpresa(impresa.getDatiPrincipaliImpresa().getTipoImpresa());
						firmatario.setRagioneSociale(impresa.getDatiPrincipaliImpresa().getRagioneSociale());
						firmatario.setNazione(impresa.getDatiPrincipaliImpresa().getNazioneSedeLegale());
						firmatario.setCognome(null);
						
						IndirizzoType residenza = firmatario.addNewResidenza();
						residenza.setCap(impresa.getDatiPrincipaliImpresa().getCapSedeLegale());
						residenza.setIndirizzo(impresa.getDatiPrincipaliImpresa().getIndirizzoSedeLegale());
						residenza.setNumCivico(impresa.getDatiPrincipaliImpresa().getNumCivicoSedeLegale());
						residenza.setComune(impresa.getDatiPrincipaliImpresa().getComuneSedeLegale());
						residenza.setNazione(impresa.getDatiPrincipaliImpresa().getNazioneSedeLegale());
						residenza.setProvincia(impresa.getDatiPrincipaliImpresa().getProvinciaSedeLegale());
					} else {
						// CASO 1.2 : mandataria di tipo NON libero professionista
						if(currentFirmatario != null) {
							firmatario.setNome(currentFirmatario.getNome());
							firmatario.setCognome(currentFirmatario.getCognome());
							FirmatarioBean infoQualificaFirmatario = null;
							boolean found = false;
							for (int j = 0; j < listaFirmatariMandataria.size() && !found; j++) {
								if (listaFirmatariMandataria.get(j).getNominativo().equalsIgnoreCase(firmatario.getCognome() + " " + firmatario.getNome())) {
									infoQualificaFirmatario = listaFirmatariMandataria.get(j);
									found = true;
								}
							}
							if (found) {
								// il soggetto è stato trovato all'interno
								// della lista dei firmatari per la
								// mandataria
								ISoggettoImpresa soggettoFromLista = null;
								if (CataloghiConstants.LISTA_LEGALI_RAPPRESENTANTI.equals(infoQualificaFirmatario.getLista())) {
									soggettoFromLista = impresa.getLegaliRappresentantiImpresa().get(infoQualificaFirmatario.getIndex());
								} else if (CataloghiConstants.LISTA_DIRETTORI_TECNICI.equals(infoQualificaFirmatario.getLista())) {
									soggettoFromLista = impresa.getDirettoriTecniciImpresa().get(infoQualificaFirmatario.getIndex());
								} else {
									soggettoFromLista = impresa.getAltreCaricheImpresa().get(infoQualificaFirmatario.getIndex());
								}

								firmatario.setCodiceFiscaleFirmatario(soggettoFromLista.getCodiceFiscale());
								firmatario.setComuneNascita(soggettoFromLista.getComuneNascita());
								firmatario.setDataNascita(CalendarValidator.getInstance().validate(soggettoFromLista.getDataNascita(),
										"dd/MM/yyyy"));
								firmatario.setProvinciaNascita(soggettoFromLista.getProvinciaNascita());
								firmatario.setQualifica(soggettoFromLista.getSoggettoQualifica());
								firmatario.setSesso(soggettoFromLista.getSesso());
								firmatario.setComuneNascita(soggettoFromLista.getComuneNascita());
								firmatario.setCodiceFiscaleImpresa(impresa.getDatiPrincipaliImpresa().getCodiceFiscale());
								firmatario.setPartitaIVAImpresa(impresa.getDatiPrincipaliImpresa().getPartitaIVA());
								firmatario.setRagioneSociale(impresa.getDatiPrincipaliImpresa().getRagioneSociale());
								firmatario.setTipoImpresa(impresa.getDatiPrincipaliImpresa().getTipoImpresa());
								
								IndirizzoType residenza = firmatario.addNewResidenza();
								residenza.setCap(soggettoFromLista.getCap());
								residenza.setIndirizzo(soggettoFromLista.getIndirizzo());
								residenza.setNumCivico(soggettoFromLista.getNumCivico());
								residenza.setComune(soggettoFromLista.getComune());
								residenza.setNazione(soggettoFromLista.getNazione());
								residenza.setProvincia(soggettoFromLista.getProvincia());
							}
						}
					}
				} else {
					// CASO 2 : mandanti
					if (currentFirmatario != null) {
						if ("6".equals(componentiRTI.get(i).getTipoImpresa())) {
							// libero professionista
							firmatario.setQualifica("libero professionista");
						} else {
							// NON libero professionista
							firmatario.setQualifica(currentFirmatario.getSoggettoQualifica());
						}

						firmatario.setNome(currentFirmatario.getNome());
						firmatario.setCognome(currentFirmatario.getCognome());
						firmatario.setCodiceFiscaleFirmatario(currentFirmatario.getCodiceFiscale());
						firmatario.setComuneNascita(currentFirmatario.getComuneNascita());
						firmatario.setNazione(currentFirmatario.getNazione());
						firmatario.setProvinciaNascita(currentFirmatario.getProvinciaNascita());
						firmatario.setSesso(currentFirmatario.getSesso());
						firmatario.setDataNascita(CalendarValidator.getInstance().validate(currentFirmatario.getDataNascita(), "dd/MM/yyyy"));						
						firmatario.setComuneNascita(currentFirmatario.getComuneNascita());
						firmatario.setCodiceFiscaleImpresa(currentFirmatario.getCodiceFiscale());  // ??? oppure firmatario.setCodiceFiscaleImpresa(componentiRTI.get(i).getCodiceFiscale()); 					
						firmatario.setPartitaIVAImpresa(componentiRTI.get(i).getPartitaIVA());
						firmatario.setRagioneSociale(componentiRTI.get(i).getRagioneSociale());
						firmatario.setTipoImpresa(componentiRTI.get(i).getTipoImpresa());						
						
						IndirizzoType residenza = firmatario.addNewResidenza();
						residenza.setCap(currentFirmatario.getCap());
						residenza.setIndirizzo(currentFirmatario.getIndirizzo());
						residenza.setNumCivico(currentFirmatario.getNumCivico());
						residenza.setComune(currentFirmatario.getComune());
						residenza.setNazione(currentFirmatario.getNazione());
						residenza.setProvincia(currentFirmatario.getProvincia());
					}
				}
			}
		}
	}
	
	/**
	 * Verifica se l'helper e e la action che lo sta utilizzando sono sincronizzati 
	 * se i valori non sono sincronizzati invalida l'esecuzione della action
	 * 
	 * @return true helper e action sono sincronizzati
	 */
	private FirmatarioType addNewFirmatario(DocumentazioneBustaType busta) {
		FirmatarioType firmatario = null;
		if(busta instanceof BustaEconomicaType 
		   || this.tipoBusta == PortGareSystemConstants.BUSTA_ECONOMICA) {
			firmatario = ((BustaEconomicaType)busta).addNewFirmatario();
		} else if(busta instanceof BustaTecnicaType 
		   || this.tipoBusta == PortGareSystemConstants.BUSTA_TECNICA) { 	
			firmatario = ((BustaTecnicaType)busta).addNewFirmatario();
		}
		return firmatario;
	}

	/**
	 * Verifica se l'helper e e la action che lo sta utilizzando sono sincronizzati 
	 * se i valori non sono sincronizzati invalida l'esecuzione della action
	 * 
	 * @return true helper e action sono sincronizzati
	 */
	public boolean isSynchronizedToAction(String codice, BaseAction action) {
		boolean isSync = true;
		if(documenti != null) {
			// verifica ed aggiunge a log ed action la segnalazione
			isSync = documenti.isSynchronizedToAction(codice, action);
		} 
		return isSync;
	}
	
	/**
	 * Verifica se l'helper e e la action che lo sta utilizzando sono sincronizzati 
	 * Non aggiunge i messaggi di errore alla action (usare ad esempio in fase di validazione) 
	 * 
	 * @return true helper e action sono sincronizzati
	 */
	public boolean isSynchronizedToAction(String codice) {
		return this.isSynchronizedToAction(codice, null);
	}

}
