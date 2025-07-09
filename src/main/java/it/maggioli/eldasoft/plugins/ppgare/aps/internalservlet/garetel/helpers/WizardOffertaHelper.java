package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.helpers;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.apsadmin.system.BaseAction;
import it.eldasoft.sil.portgare.datatypes.AttributoGenericoType;
import it.eldasoft.sil.portgare.datatypes.BustaEconomicaType;
import it.eldasoft.sil.portgare.datatypes.BustaTecnicaType;
import it.eldasoft.sil.portgare.datatypes.DocumentazioneBustaType;
import it.eldasoft.sil.portgare.datatypes.FirmatarioType;
import it.eldasoft.sil.portgare.datatypes.IndirizzoType;
import it.eldasoft.sil.portgare.datatypes.ListaCriteriValutazioneType;
import it.eldasoft.sil.portgare.datatypes.ListaDocumentiType;
import it.eldasoft.sil.portgare.datatypes.RiepilogoBusteOffertaDocument;
import it.eldasoft.www.sil.WSGareAppalto.CriterioValutazioneOffertaType;
import it.eldasoft.www.sil.WSGareAppalto.GaraType;
import it.eldasoft.www.sil.WSGareAppalto.MandanteRTIType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractWizardHelper;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaDocumenti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.ComponentiRTIList;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.Attachment;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.EncryptionUtils;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.FirmatarioBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ISoggettoImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.SoggettoFirmatarioImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.SoggettoImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.RiepilogoBusteHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.WizardDocumentiBustaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.ComponenteHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.IComponente;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.security.SymmetricEncryptionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.CalendarValidator;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Helper di memorizzazione dei dati relativi alla gestione della compilazione
 * di un'offerta telematica (economica, tecnica, ...).
 * 
 * @author ...
 */
public abstract class WizardOffertaHelper extends AbstractWizardHelper 
	implements HttpSessionBindingListener, Serializable 
{
	/**
	 * UID 
	 */
	private static final long serialVersionUID = -4233963232292954113L;

	// utilizzati dalla jsp ?
	public static final Integer CHIAVE_PARTITA_IVA 		= 1;
	public static final Integer CHIAVE_CODICE_FISCALE 	= 2;
	
	public static final int CRITERIO_VALUTAZIONE_TESTO_MAXLEN = 2000;
	
	protected static final DateFormat DATEFORMAT_DDMMYYYY = new SimpleDateFormat("dd/MM/yyyy"); 

	protected String stepPrefixPage;

	protected WizardDocumentiBustaHelper documenti;
	protected ComponentiRTIList componentiRTI;
	protected List<FirmatarioBean> listaFirmatariMandataria;
	protected FirmatarioBean firmatarioSelezionato;
	protected int idFirmatarioSelezionatoInLista;
	protected WizardDatiImpresaHelper impresa;
	protected int tipoBusta;
	protected String codice;
	protected GaraType gara;
	protected Long idComunicazione;		/** Identificativo univoco della comunicazione (valorizzata nel caso di completamento) */
	protected List<File> pdfGenerati;	/** Pdf generati per l'offerta */
	protected String pdfUUID;			/** UUID generato e attribuito come Keywords nell'ultimo PDF */
	protected boolean rigenPdf;
	protected boolean datiModificati;
	protected Long idOfferta;
	protected String progressivoOfferta;
	
	// gestione dei criteri di valutazione per OEPV
	protected List<CriterioValutazioneOffertaType> listaCriteriValutazione;
	protected Boolean[] criterioValutazioneEditabile;	// dati inseriti nell'interfaccia (form di inserimento dati)
	protected String[] criterioValutazione;				// dati inseriti nell'interfaccia (form di inserimento dati)

	
	
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

	public Boolean[] getCriterioValutazioneEditabile() {
		return criterioValutazioneEditabile;
	}

	public String[] getCriterioValutazione() {
		return criterioValutazione;
	}

	public void setCriterioValutazione(String[] criterioValutazione) {
		this.criterioValutazione = criterioValutazione;
	}

	public List<CriterioValutazioneOffertaType> getListaCriteriValutazione() {
		return listaCriteriValutazione;
	}
	
	public void setListaCriteriValutazione(List<CriterioValutazioneOffertaType> listaCriteri) {
		listaCriteriValutazione = listaCriteri;
		
		// verifica ed elimina i criteri non previsti dal tipo di busta (tec/eco)...
		if(listaCriteriValutazione != null) {
			listaCriteriValutazione = listaCriteriValutazione.stream()
				.filter(c -> isCriterioValutazionePrevisto(c))
				.collect(Collectors.toList());
		}

		// inizializza le strutture per gli input della pagina
		criterioValutazioneEditabile = null;
		criterioValutazione = null;
		if(listaCriteriValutazione != null) {
			criterioValutazione = new String[listaCriteriValutazione.size()];
			criterioValutazioneEditabile = new Boolean[listaCriteriValutazione.size()];
			for(int i = 0; i < listaCriteriValutazione.size(); i++) {
				criterioValutazione[i] = null; 
				criterioValutazioneEditabile[i] = new Boolean(true);
			}
		}
	}

	/**
	 * costruttore 
	 */
	public WizardOffertaHelper(String sessionKeyHelperId, boolean legacyPrevNext) {
		super(sessionKeyHelperId, legacyPrevNext);
		DATEFORMAT_DDMMYYYY.setLenient(false);
	}

	public WizardOffertaHelper(int tipoBusta, String nomeBusta) {
		super(null, false);
		this.gara = new GaraType();
		this.impresa = null;
		this.tipoBusta = tipoBusta;
		this.documenti = new WizardDocumentiBustaHelper(tipoBusta);
		this.componentiRTI = new ComponentiRTIList();
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
		this.listaCriteriValutazione = null;
		this.criterioValutazione = null;
		this.criterioValutazioneEditabile = null;
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

		if(this.pdfGenerati != null) {
			Iterator<File> iter = this.pdfGenerati.listIterator();
			while (iter.hasNext()) {
				File file = iter.next();
				if (file != null && file.exists()) {
					file.delete();
				}
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
						firmatario.setSesso(currentFirmatario.getSesso());
						firmatario.setComuneNascita(currentFirmatario.getComuneNascita());
						firmatario.setNazione(currentFirmatario.getNazione());
						firmatario.setProvinciaNascita(currentFirmatario.getProvinciaNascita());
						firmatario.setDataNascita(CalendarValidator.getInstance().validate(currentFirmatario.getDataNascita(), "dd/MM/yyyy"));						
						
						firmatario.setCodiceFiscaleImpresa(componentiRTI.get(i).getCodiceFiscale());
						firmatario.setPartitaIVAImpresa(componentiRTI.get(i).getPartitaIVA());
						//firmatario.setIdFiscaleEsteroImpresa(...);	// manca nell'XML del firmatario
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
	 * Verifica se l'helper e la action che lo sta utilizzando sono sincronizzati 
	 * se i valori non sono sincronizzati invalida l'esecuzione della action
	 * 
	 * @return true helper e action sono sincronizzati
	 */
	private FirmatarioType addNewFirmatario(DocumentazioneBustaType busta) {
		FirmatarioType firmatario = null;
		if(busta instanceof BustaEconomicaType || this.tipoBusta == PortGareSystemConstants.BUSTA_ECONOMICA) {
			firmatario = ((BustaEconomicaType)busta).addNewFirmatario();
		} else if(busta instanceof BustaTecnicaType || this.tipoBusta == PortGareSystemConstants.BUSTA_TECNICA) { 	
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

	/**
	 * verifica ed inizializza la chiave di sessione   
	 * @throws GeneralSecurityException 
	 * @throws UnsupportedEncodingException 
	 */
	private void refreshChiaveSessione(BustaDocumenti busta) 
		throws UnsupportedEncodingException, GeneralSecurityException 
	{
		this.getDocumenti().setUsername(busta.getUsername());
		this.setIdComunicazione(busta.getId());
		
		// se la comunicazione e' in stato BOZZA (1) 
		// ed e' attiva la cifratura sulle buste
		// aggiorna la chiave di sessione
		if(this.getDocumenti().getChiaveCifratura() != null) {
			byte[] chiaveSessione = null;
			if(busta.getComunicazioneFlusso().getDettaglioComunicazione() != null && 
			   busta.getComunicazioneFlusso().getDettaglioComunicazione().getSessionKey() != null) 
			{
				chiaveSessione = busta.getComunicazioneFlusso().getDettaglioComunicazione().getSessionKey().getBytes();
				this.getDocumenti().setChiaveSessione(chiaveSessione);
			}
				
			String stato = busta.getComunicazioneFlusso().getDettaglioComunicazione().getStato();
			if((StringUtils.isEmpty(stato) || CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA.equals(stato))) {			
				chiaveSessione = EncryptionUtils.decodeSessionKey(
						busta.getComunicazioneFlusso().getDettaglioComunicazione().getSessionKey(), 
						busta.getUsername());
				this.getDocumenti().setChiaveSessione(chiaveSessione);
			}
		}
	}

	/**
	 * popola l'helper con i dati del documento XML della busta
	 */
	public abstract void popolaFromXml(BustaDocumenti busta) throws Throwable;
	
	/**
	 * popola gli allegati da un documento XML 
	 * @throws Exception 
	 */
	protected boolean popolaDocumentiFromXml(
			BustaDocumenti busta,
			ListaDocumentiType listaDocumenti) 
		throws Exception
	{
		// NB: 
		// se lo stato della comunicazione e' diverso da BOZZA (1)
		// significa che la busta e' stata inviata e criptata
		// In questo caso non risulta piu' leggibile, cosi' 
		// come la chiave di sessione non e' piu' decifrabile...
		boolean popola = (StringUtils.isEmpty(busta.getComunicazioneFlusso().getStato()) ||
					      CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA.equals(busta.getComunicazioneFlusso().getStato())); 		
		
		// verifica ed inizializza la chiave di sessione...
		if(popola) {
			this.refreshChiaveSessione(busta);
		}

		// allineo i documenti...
		if(popola && listaDocumenti != null) {
			this.documenti.popolaFromXml(
					listaDocumenti,
					busta);
		
			if (this.documenti.getRequiredDocs().size() > 0) {
		    	// marca se e' presente il documento per l'offerta
				this.documenti.setDocOffertaPresente(false);
				long idOfferta = this.getIdOfferta();
				for (Attachment attachment : documenti.getRequiredDocs()) {
					if(attachment.getId() != null
					   && attachment.getId().longValue() == idOfferta)
					{
						this.documenti.setDocOffertaPresente(true);
						break;
					}
				}
			}
			
			// marca la rigenerazione del pdf come non necessaria...
			this.setRigenPdf(false);
			
			this.documenti.setAlreadyLoaded(true);
		}	
		
		//// allinea i firmatari...
		//this.popolaFirmatariFromXml(busta, listaFirmatari);
		
		return popola;
	}
	
	/**
	 * popola i firmatari da un documento XML
	 * @throws XmlException 
	 * @throws ApsException 
	 */
	protected boolean popolaFirmatariFromXml(
			BustaDocumenti busta,
			FirmatarioType[] listaFirmatari) 
		throws XmlException, ApsException 
	{
		GestioneBuste buste = busta.getGestioneBuste();
		WizardPartecipazioneHelper partecipazione = buste.getBustaPartecipazione().getHelper();
		WizardDatiImpresaHelper impresa = buste.getImpresa();
		IBandiManager bandiManager = buste.getBandiManager();
		GaraType gara = buste.getDettaglioGara().getDatiGeneraliGara();
		String codiceGara = buste.getCodiceGara();
		String username = buste.getUsername();
		
		List<FirmatarioBean> listaFirmatariMandataria = this.getListaFirmatariMandataria();
		Long idComunicazione = this.getIdComunicazione();
		
    	if (gara.getCodice().equals(codiceGara) && partecipazione != null) {

			ComponentiRTIList rti = null;

    		if (partecipazione.isRti()) {
    			////////////////////////////////////////////////////////////////
    			// recupera solo la mandataria 
    	    	rti = new ComponentiRTIList();
    	    	
    			// 1) aggiungi la mandataria sempre in posizione 0...
    	    	IComponente mandataria = new ComponenteHelper();
    			if (impresa.isLiberoProfessionista() &&
    				StringUtils.isEmpty(impresa.getDatiPrincipaliImpresa().getPartitaIVA())) 
    			{
    			    // se e' un libero professionista e partita iva è vuota => usa il codice fiscale
    			    mandataria.setCodiceFiscale(impresa.getDatiPrincipaliImpresa().getCodiceFiscale());
    			} else {
    				mandataria.setPartitaIVA(impresa.getDatiPrincipaliImpresa().getPartitaIVA());
    				mandataria.setCodiceFiscale(impresa.getDatiPrincipaliImpresa().getCodiceFiscale());
    			}
    			mandataria.setRagioneSociale(impresa.getDatiPrincipaliImpresa().getRagioneSociale());
    			mandataria.setTipoImpresa(impresa.getDatiPrincipaliImpresa().getTipoImpresa());
    			mandataria.setAmbitoTerritoriale(impresa.getDatiPrincipaliImpresa().getAmbitoTerritoriale());
    			if("2".equals(impresa.getDatiPrincipaliImpresa().getAmbitoTerritoriale())) { 
    				mandataria.setIdFiscaleEstero(impresa.getDatiPrincipaliImpresa().getCodiceFiscale());
    			} else {
    				mandataria.setIdFiscaleEstero(null);
    			}
    			rti.add(mandataria);
    			
//    			// 2) aggiungi le mandanti alle posizioni 1,2, ... , N-1
//    			if(partecipazioneHelper != null) {
//    				for (int i = 0; i < partecipazioneHelper.getComponenti().size(); i++) {
//    				    rti.add(partecipazioneHelper.getComponenti().get(i));
//    				}
//    			}

    			////////////////////////////////////////////////////////////////
    			// se in BO esiste la composizione RTI recupera il raggruppamento 
    			// dal BO, 
    			// altrimenti recupera la composizione RTI dal portale (XML)
    			List<MandanteRTIType> mandantiBO = null;
    			if ( !partecipazione.isEditRTI() ) {
    				mandantiBO = bandiManager.getMandantiRTI(
    						codiceGara, 
    						username, 
    						this.progressivoOfferta);
    			}
    			if(mandantiBO != null && mandantiBO.size() > 0) {
    				// RTI in BO
    				for (int i = 0; i < mandantiBO.size(); i++) {
    					IComponente mandante = new ComponenteHelper();
    					mandante.setCodiceFiscale(mandantiBO.get(i).getCodiceFiscale());
    					mandante.setPartitaIVA(mandantiBO.get(i).getPartitaIVA());
    					mandante.setRagioneSociale(mandantiBO.get(i).getRagioneSociale());
    					if (mandantiBO.get(i).getTipologia() != null) {
        					mandante.setTipoImpresa(mandantiBO.get(i).getTipologia().toString());
    					}
    					if (mandantiBO.get(i).getAmbitoTerritoriale() != null) {
        					mandante.setAmbitoTerritoriale(mandantiBO.get(i).getAmbitoTerritoriale());
    					}
    					if (mandantiBO.get(i).getIdFiscaleEstero() != null) {
        					mandante.setIdFiscaleEstero(mandantiBO.get(i).getIdFiscaleEstero());
    					}
    					rti.add(mandante);
    				} 
    			} else {
    				// RTI in portale appalti (XML)
    				for (int i = 0; i < partecipazione.getComponenti().size(); i++) {
    					rti.add(partecipazione.getComponenti().get(i));
    				}
    			}
    			
    			////////////////////////////////////////////////////////////////
    	    	// aggiorna i firmatari...
    			FirmatarioType[] firmatari = null;
    			
    			// recupera i firmatari nella busta dell'offerta (tecnica o economica)...
    			if(idComunicazione != null && idComunicazione > 0) {
    				firmatari = listaFirmatari;
    			}
    			
    			// ...se nella comunicazione non ci sono firmatari 
				// verifica se ci sono dei firmatari nella busta di riepilogo FS11R...
				if(firmatari == null) {
					// estrai i firmatari dalla busta di riepilogo...
					String xml = buste.getBustaRiepilogo().getComunicazioneFlusso().getXmlDoc();
					if(xml != null) {
						RiepilogoBusteOffertaDocument doc = RiepilogoBusteOffertaDocument.Factory.parse(xml);
						if(doc.getRiepilogoBusteOfferta() != null) {
							firmatari = doc.getRiepilogoBusteOfferta().getFirmatarioArray();
						}
					}
				}
    			
				// configura i firmatari dello helper...
				if(firmatari != null) {
					if (rti.size() <= 0) {
						// Passaggio da RTI salvato in comunicazione a non più RTI 
						// => prendo solo il frammento con indice 0
						FirmatarioType unicaImpresa = firmatari[0];
						this.setIdFirmatarioSelezionatoInLista(unicaImpresa);
					} else {
						if (firmatari.length == 1) {
							// CASO 1 : Firmatario unico
							if (firmatari.length == 1) {
								FirmatarioType firmatarioRecuperato = firmatari[0];
								this.setIdFirmatarioSelezionatoInLista(firmatarioRecuperato);
							}
							
						} else if (firmatari.length > 1) {
							// CASO 2: RTI (1 mandataria + N mandanti)
							for (int i = 0; i < firmatari.length; i++) {
								
								boolean removeFirmatarioMandataria = false;
								
								SoggettoFirmatarioImpresaHelper firmatario = new SoggettoFirmatarioImpresaHelper();
								firmatario.setNome(firmatari[i].getNome());
								firmatario.setCognome(firmatari[i].getCognome());
				
								if (i == 0) {
									// il primo elemento è sempre relativo 
									// al firmatario della mandataria
									if (impresa.isLiberoProfessionista()) {
										//firmatario.setNome(firmatari[i].getRagioneSociale());
										//firmatario.setNominativo(firmatari[i].getRagioneSociale());
										firmatario.setNome(impresa.getAltriDatiAnagraficiImpresa().getNome());
										firmatario.setCognome(impresa.getAltriDatiAnagraficiImpresa().getCognome());
									} else {
										firmatario.setNome(firmatari[i].getNome());
										firmatario.setCognome(firmatari[i].getCognome());
									}
									firmatario.setNominativo( firmatario.getCognome() + " " + firmatario.getNome());
									
									boolean firmatarioRecuperatoFound = false;
									for (int j = 0; j < listaFirmatariMandataria.size() && !firmatarioRecuperatoFound; j++) {
										if (firmatario.getNominativo().equalsIgnoreCase(listaFirmatariMandataria.get(j).getNominativo())) {
//												helper.setIdFirmatarioSelezionatoInLista(j);
											firmatarioRecuperatoFound = true;
										}
									}

									// se non hai trovato il firmatario della  
									// mandataria nella lista rimuovilo dalla
									// lista dei firmatari della RTI
									if (!firmatarioRecuperatoFound) {
										removeFirmatarioMandataria = true;
									}
								} else {
									// gli elementi successivi riguardano i 
									// firmatari delle mandanti
									if (StringUtils.isNotBlank(firmatario.getCognome()) || 
										StringUtils.isNotBlank(firmatario.getNome())) {
										// mandante NON libero professionista
										firmatario.setNome(firmatari[i].getNome());
										firmatario.setCognome(firmatari[i].getCognome());
										firmatario.setNominativo(firmatari[i].getCognome() + " " + firmatari[i].getNome());
									} else {
										firmatario.setNome(firmatari[i].getRagioneSociale());
										firmatario.setNominativo(firmatari[i].getRagioneSociale());
									}
								}
				
								firmatario.setCodiceFiscale(firmatari[i].getCodiceFiscaleFirmatario());
								firmatario.setComuneNascita(firmatari[i].getComuneNascita());
								firmatario.setProvinciaNascita(firmatari[i].getProvinciaNascita());
								if (firmatari[i].getDataNascita() != null) {
									Date c = firmatari[i].getDataNascita().getTime();
									firmatario.setDataNascita(DATEFORMAT_DDMMYYYY.format(c));
								}
								firmatario.setSesso(firmatari[i].getSesso());
								if (StringUtils.isNotBlank(firmatari[i].getQualifica())) {
									firmatario.setQualifica(StringUtils.substring(
											firmatari[i].getQualifica(), 
											firmatari[i].getQualifica().lastIndexOf("-") + 1,
											firmatari[i].getQualifica().length()));
									firmatario.setSoggettoQualifica(firmatari[i].getQualifica());
								}
								if (firmatari[i].getResidenza() != null) {
									firmatario.setCap(firmatari[i].getResidenza().getCap());
									firmatario.setIndirizzo(firmatari[i].getResidenza().getIndirizzo());
									firmatario.setNumCivico(firmatari[i].getResidenza().getNumCivico());
									firmatario.setComune(firmatari[i].getResidenza().getComune());
									firmatario.setProvincia(firmatari[i].getResidenza().getProvincia());
									firmatario.setNazione(firmatari[i].getResidenza().getNazione());
								}
								firmatario.setPartitaIvaImpresa(firmatari[i].getPartitaIVAImpresa());
								firmatario.setCodiceFiscaleImpresa(firmatari[i].getCodiceFiscaleImpresa());
								
								if (removeFirmatarioMandataria) {
									rti.removeFirmatario(firmatari[i]);
								} else {
									rti.addFirmatario(firmatari[i], firmatario);
								}
							}
						}
					}
    			}
    			////////////////////////////////////////////////////////////////

				this.setComponentiRTI(rti);
    		}

    		if(rti != null && rti.size() > 0) {
    			// cerca l'id della lista a cui è associato il firmatario
    			boolean firmatarioIndividuato = false;
    			SoggettoImpresaHelper mandataria = rti.getFirmatario(impresa.getDatiPrincipaliImpresa());
				
    			if(mandataria != null) {
	    			for (int i = 0; i < listaFirmatariMandataria.size() && !firmatarioIndividuato; i++) {
						if (listaFirmatariMandataria.get(i).getNominativo().equalsIgnoreCase(mandataria.getNominativo())) {
							firmatarioIndividuato = true;
							this.setIdFirmatarioSelezionatoInLista(i);
						}
	    			}
    			}
    		}
    	}
    	return true;
    }
	
    /**
     * cerca il firmatario selezionato nella lista dei firmatari disponibili 
     */
    private void setIdFirmatarioSelezionatoInLista(FirmatarioType firmatario) {
		List<FirmatarioBean> listaFirmatariMandataria = this.getListaFirmatariMandataria();

		String nominativoFirmatarioRecuperato = firmatario.getCognome() + " " + firmatario.getNome();
		boolean firmatarioRecuperatoFound = false;
		for (int i = 0; i < listaFirmatariMandataria.size() && !firmatarioRecuperatoFound; i++) {
		    if (nominativoFirmatarioRecuperato.equalsIgnoreCase(listaFirmatariMandataria.get(i).getNominativo())) {
				firmatarioRecuperatoFound = true;
				this.setIdFirmatarioSelezionatoInLista(i);
		    }
		}
    }

    /** 
     * restituisce la lista dei firmatari per un'impresa mandataria 
     */
	public List<FirmatarioBean> composeListaFirmatariMandataria() {
		return ComponentiRTIList.composeListaFirmatariMandataria(impresa);
    }


	/**
	 * restituisce l'oggetto per decifrare i dati cifrati 
	 *   
	 * @throws InvalidAlgorithmParameterException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchProviderException 
	 * @throws NoSuchAlgorithmException 
	 * @throws UnsupportedEncodingException 
	 * @throws InvalidKeyException 
	 */
	protected Cipher getDecoder() 
		throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidAlgorithmParameterException 
	{ 
		Cipher cipher = null;
		if (this.getDocumenti().getChiaveSessione() != null) {
			cipher = SymmetricEncryptionUtils.getDecoder(
					this.getDocumenti().getChiaveSessione(), 
					this.getDocumenti().getUsername());
		}
		return cipher;
	}

	/**
	 * restituisce l'oggetto per cifrare i dati in chiaro 
	 *   
	 * @throws InvalidAlgorithmParameterException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchProviderException 
	 * @throws NoSuchAlgorithmException 
	 * @throws UnsupportedEncodingException 
	 * @throws InvalidKeyException 
	 */
	protected Cipher getEncoder() 
		throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidAlgorithmParameterException 	
	{ 
		Cipher cipher = null;
		if (this.getDocumenti().getChiaveSessione() != null) {
			cipher = SymmetricEncryptionUtils.getEncoder(
					this.getDocumenti().getChiaveSessione(), 
					this.getDocumenti().getUsername());
		}
		return cipher;
	}

	/**
	 * Verifica la presenza dei firmatari 
	 */
	public boolean testPresenzaFirmatari(RiepilogoBusteHelper bustaRiepilogativa) {
		boolean firmatariPresenti = true;
		
		// se non trovo firmatari si tenta il recupero dal riepilogo...
		//recuperaFirmatariRiepilogo(bustaRiepilogativa);
		int firmatari = 0;
		for(int i = 0; i < this.getComponentiRTI().size(); i++) {
			if(this.getComponentiRTI().getFirmatario(this.getComponentiRTI().get(i)) != null) {
				firmatari++;
			}
		}
		if(firmatari <= 0) {
			if(bustaRiepilogativa != null && bustaRiepilogativa.getUltimiFirmatariInseriti() != null) {
				for(int i = 0; i < bustaRiepilogativa.getUltimiFirmatariInseriti().size(); i++) {
					SoggettoFirmatarioImpresaHelper firmatarioProposto = bustaRiepilogativa.getUltimiFirmatariInseriti().get(i);
					IComponente componenteRTI = this.getComponentiRTI().getComponenteRTI(firmatarioProposto);
					if(componenteRTI != null) {
						this.getComponentiRTI().addFirmatario(componenteRTI, firmatarioProposto);
					}
				}
			}
		}
		
		// controlla se sono presenti tutti i firmatari per ogni componente della rti...
		for(int i = 0; i < this.getComponentiRTI().size() && firmatariPresenti; i++) {
			if(this.getComponentiRTI().getFirmatario(this.getComponentiRTI().get(i)) == null) {
				firmatariPresenti = false;
			}
		}

		return firmatariPresenti && !this.isDatiModificati();
	}

	/**
	 * verifca la presenza del firmatario della mandataria ed eventualmente lo aggiunge 
	 * La mandataria e' sempre il primo elemento di "componentiRTI" 
	 */
	public void verificaFirmatarioMandataria() {
		if(this.listaFirmatariMandataria.size() != 1) {
			return;
		}
		
		SoggettoFirmatarioImpresaHelper firmatarioMandataria = new SoggettoFirmatarioImpresaHelper();
		if( !this.impresa.isLiberoProfessionista() ) {
			// inizializza i dati del firmatario con i dati relativi al libero professionista
			FirmatarioBean firmatario = this.listaFirmatariMandataria.get(0);
			
			List<ISoggettoImpresa> lista = null;
			if(CataloghiConstants.LISTA_LEGALI_RAPPRESENTANTI.equals(firmatario.getLista())) {
				lista = this.impresa.getLegaliRappresentantiImpresa();
			} else if(CataloghiConstants.LISTA_DIRETTORI_TECNICI.equals(firmatario.getLista())) {
				lista = this.impresa.getDirettoriTecniciImpresa();
			} else {
				lista = this.impresa.getAltreCaricheImpresa();
			}
			
			ISoggettoImpresa soggetto = lista.get(firmatario.getIndex());
			firmatarioMandataria.copyFrom(soggetto);
			firmatarioMandataria.setNominativo(firmatario.getNominativo());
		} else {
			// inizializza i dati del firmatario con i dati relativi all'impresa
			String nominativo = this.impresa.getAltriDatiAnagraficiImpresa().getCognome() != null 
								? this.impresa.getAltriDatiAnagraficiImpresa().getCognome() + " " + 
								  this.impresa.getAltriDatiAnagraficiImpresa().getNome() 
								: this.impresa.getDatiPrincipaliImpresa().getRagioneSociale();
			firmatarioMandataria.setNome(this.impresa.getDatiPrincipaliImpresa().getRagioneSociale());
			firmatarioMandataria.setNominativo(nominativo);
		}
		
		this.componentiRTI.addFirmatario(this.componentiRTI.get(0), firmatarioMandataria);
	}

	/**
	 * restituisce le qualifiche dei firmatari 
	 */
	public List<String> getQualificheFirmatarioMandataria() {
		List<String> lista = new ArrayList<String>();
		for(int i = 0; i < this.listaFirmatariMandataria.size(); i++) {
			FirmatarioBean firmatario = this.listaFirmatariMandataria.get(i);
			List<String> qualifiche = firmatario.getQualifiche(this.impresa.getAltreCaricheImpresa());
			lista.addAll(qualifiche);
		}
		return lista;
	}


	/**
	 * salva il firmatario della mandataria in caso di RTI 
	 */
	public SoggettoFirmatarioImpresaHelper saveFirmatarioMandataria(String firmatarioSelezionato) {
		SoggettoFirmatarioImpresaHelper firmatario = new SoggettoFirmatarioImpresaHelper();
		
		String[] v = (StringUtils.isNotEmpty(firmatarioSelezionato) ? firmatarioSelezionato.split("-") : null);
		String lista = (v != null && v.length > 0 ? v[0] : "");
		
		ISoggettoImpresa soggettoFromLista = impresa.findSoggettoImpresa(firmatarioSelezionato);
		
		// NB: il firmatario va cercato nella lista dei firmatari per (ccognome nome e tipo soggetto!!!) 
		for(int i = 0; i < listaFirmatariMandataria.size(); i++) {
			FirmatarioBean f = listaFirmatariMandataria.get(i);
			if(f.getNominativo().equalsIgnoreCase(soggettoFromLista.getCognome() + " " + soggettoFromLista.getNome())
			   && lista.equals(f.getLista())) 
			{
				// trovato il firmatario della mandataria
				firmatario.copyFrom(soggettoFromLista);
				firmatario.setNominativo(soggettoFromLista.getCognome() + " " + soggettoFromLista.getNome());
				idFirmatarioSelezionatoInLista = i;
				break;
			}
		}
	
		// CF, PIVA impresa 
		firmatario.setCodiceFiscaleImpresa(impresa.getDatiPrincipaliImpresa().getCodiceFiscale());
		firmatario.setPartitaIvaImpresa(impresa.getDatiPrincipaliImpresa().getPartitaIVA());
	
		// aggiungi il firmatario all'helper...
		componentiRTI.addFirmatario(impresa.getDatiPrincipaliImpresa(), firmatario);
		
		return firmatario;
	}
		
	/**
	 * restrituisce il tipo di criterio in base al tipo di busta 
	 */
    protected static int getTipoCriterio(int tipoBusta) {
    	return (tipoBusta == PortGareSystemConstants.BUSTA_ECONOMICA ? PortGareSystemConstants.CRITERIO_ECONOMICO
    			: tipoBusta == PortGareSystemConstants.BUSTA_TECNICA ? PortGareSystemConstants.CRITERIO_TECNICO
    			: 0);
    }
    
	/**
	 * verifica se un criterio di valudatione e' previsto o meno per il tipo di busta 
	 */
	private static boolean isCriterioValutazionePrevisto(int formatoCriterio, int tipoCriterio, String descrizione) {
		boolean previsto = false;
		if(tipoCriterio != 0) {
			switch (formatoCriterio) {
			case PortGareSystemConstants.CRITERIO_VALUTAZIONE_DATA:
				previsto = true;
				break;
				
			case PortGareSystemConstants.CRITERIO_VALUTAZIONE_IMPORTO:
				previsto = true;
				break;
				
			case PortGareSystemConstants.CRITERIO_VALUTAZIONE_DECIMALE:
				previsto = true;
				break;
				
			case PortGareSystemConstants.CRITERIO_VALUTAZIONE_LISTA_VALORI:
				previsto = true;
				break;
				
			case PortGareSystemConstants.CRITERIO_VALUTAZIONE_TESTO:
				previsto = true;
				break;
				
			case PortGareSystemConstants.CRITERIO_VALUTAZIONE_INTERO:
				previsto = true;
				break;
				
			case PortGareSystemConstants.CRITERIO_VALUTAZIONE_OFFERTA_MEDIANTE_IMPORTO:
				previsto = (tipoCriterio == PortGareSystemConstants.CRITERIO_ECONOMICO);
				break;
				
			case PortGareSystemConstants.CRITERIO_VALUTAZIONE_OFFERTA_MEDIANTE_PREZZIUNITARI:
				previsto = (tipoCriterio == PortGareSystemConstants.CRITERIO_ECONOMICO);
				break;
				
			case PortGareSystemConstants.CRITERIO_VALUTAZIONE_OFFERTA_MEDIANTE_RIBASSO:
				previsto = (tipoCriterio == PortGareSystemConstants.CRITERIO_ECONOMICO);
				break;
			}
			
			// traccia evetnuali anomalie del criterio 
			if(!previsto) {
				ApsSystemUtils.getLogger().warn("Criterio di valutazione non previsto (descrizione={} formato={})", descrizione, formatoCriterio);	
			}
		}
		return previsto;
	}

	protected static boolean isCriterioValutazionePrevisto(CriterioValutazioneOffertaType criterio) {
		return (criterio != null 
				? isCriterioValutazionePrevisto(criterio.getFormato(), criterio.getTipo(), criterio.getDescrizione())
				: null);
	}	
	
	/**
	 * Restituisce il valore di un criterio di valutazione estratto da un 
	 * documento xml   
	 */	
	public String getValoreCriterioValutazioneXml(
			AttributoGenericoType attr,
			CriterioValutazioneOffertaType criterio,
			Cipher cipher) 
	{
		String value = null;
		if(isCriterioValutazionePrevisto(criterio)) {
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
						value = (attr.isSetValoreData() 
							? DATEFORMAT_DDMMYYYY.format(attr.getValoreData().getTime())
							: null
						);
						break;
					case PortGareSystemConstants.CRITERIO_VALUTAZIONE_IMPORTO:
						if(attr.isSetValoreNumerico()) {
							value = BigDecimal.valueOf(Double.valueOf(attr.getValoreNumerico()))
												.setScale(2, BigDecimal.ROUND_HALF_UP)
												.setScale(numDecimali, BigDecimal.ROUND_HALF_UP)
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
						value = (attr.isSetValoreStringa() ? attr.getValoreStringa() : null);
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
		}
		return value;
	}

	/**
	 * Imposta il valore di un criterio di valutazione per un documento xml 
	 * (es: busta economica o tecnica). 
	 * In caso di cifratura i valori vengono cifrati.
	 */	
	public void setValoreCriterioValutazioneXml(
			AttributoGenericoType criterio,
			CriterioValutazioneOffertaType valore,
			Cipher cipher) 
	{
		String value;
		if(isCriterioValutazionePrevisto(valore)) {
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
						Calendar c = Calendar.getInstance();
						c.setTime(DATEFORMAT_DDMMYYYY.parse(valore.getValore()));
						criterio.setValoreData(c);
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
						criterio.setValoreStringa(valore.getValore());
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
					criterio.setValoreCifrato(SymmetricEncryptionUtils.translate(cipher, value.toString().getBytes()));
				}
			} catch (Throwable e) {
				value = null;
			}
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
		if(criterio != null && isCriterioValutazionePrevisto(criterio)) {
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
//				if(criterio.getFormato() == PortGareSystemConstants.CRITERIO_VALUTAZIONE_IMPORTO) {
//					numDecimali = 2;
//				}
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
					Calendar c = Calendar.getInstance();
					c.setTime(DATEFORMAT_DDMMYYYY.parse(valore));
					newValue = DATEFORMAT_DDMMYYYY.format(c.getTime());
					break;
				case PortGareSystemConstants.CRITERIO_VALUTAZIONE_IMPORTO:
					newValue = BigDecimal.valueOf(Double.valueOf(valore))
										.setScale(numDecimali, BigDecimal.ROUND_HALF_UP)
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
					newValue = valore;
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
	 * popola la lista dei criteri di valutazione da un documento XML
	 */
	public List<CriterioValutazioneOffertaType> popolaCriteriValutazioneFromXml(ListaCriteriValutazioneType criteriXml) throws Throwable {		
		int tipoCriteri = getTipoCriterio(tipoBusta); 
		List<CriterioValutazioneOffertaType> criteriValutazione = this.getListaCriteriValutazione();
		
		if(criteriXml != null && tipoCriteri != 0) {
			if(criteriValutazione == null) {
				criteriValutazione = new ArrayList<CriterioValutazioneOffertaType>();
			} else {
				criteriValutazione = new ArrayList<CriterioValutazioneOffertaType>(criteriValutazione);
			}
			
			for(int i = 0; i < criteriXml.getCriterioValutazioneArray().length; i++) {
				AttributoGenericoType attr = criteriXml.getCriterioValutazioneArray()[i];
				
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
					criterio.setCodice(attr.getCodice());
					criterio.setDescrizione(attr.getCodice());
					criterio.setFormato(attr.getTipo());
					criterio.setNumeroDecimali(0);
					criteriValutazione.add(criterio);
				}
				
				// imposta i dati del criterio (tipo, formato e valore)... 
				criterio.setTipo(tipoCriteri);
				//criterio.setFormato(attr.getTipo());
				if(criterio.getFormato() != attr.getTipo()) {
					criterio.setValore(null);
				} else {
					criterio.setValore(getValoreCriterioValutazioneXml(attr, criterio, getDecoder()));
				}
			}
		}
		
		// verifica ed elimina i criteri non previsti dal tipo di busta (tec/eco)...
		if(criteriValutazione != null) {
			criteriValutazione.removeIf(c -> !isCriterioValutazionePrevisto(c));
		}

		return criteriValutazione;
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
			for(CriterioValutazioneOffertaType criterio : listaCriteriValutazione) {
				if(criterio.getTipo() == tipoCriterio) {
					countCriteri++;
					break;
				}
			}
		}
		return (gara != null &&
				gara.isProceduraTelematica() &&
				gara.isOffertaTelematica() && 
				(gara.getModalitaAggiudicazione() == 6) &&  // 6 = ...
				(countCriteri > 0));
	}
	
	public boolean isCriteriValutazioneVisibili() {
		return isCriteriValutazioneVisibili(getTipoCriterio(tipoBusta), gara, listaCriteriValutazione);
	}
	
	/**
	 * indica se i criteri di valutazione sono editabili 
	 */
	public boolean isCriteriValutazioneEditabili() {
		return true;
	}

}
