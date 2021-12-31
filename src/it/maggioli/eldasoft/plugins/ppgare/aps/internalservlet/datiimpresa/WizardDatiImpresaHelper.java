package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa;

import it.eldasoft.sil.portgare.datatypes.AbilitazionePreventivaType;
import it.eldasoft.sil.portgare.datatypes.AggAnagraficaImpresaType;
import it.eldasoft.sil.portgare.datatypes.AggiornamentoAnagraficaImpresaDocument;
import it.eldasoft.sil.portgare.datatypes.AlboProfessionaleType;
import it.eldasoft.sil.portgare.datatypes.AltriDatiAnagraficiType;
import it.eldasoft.sil.portgare.datatypes.CameraCommercioType;
import it.eldasoft.sil.portgare.datatypes.CassaEdileType;
import it.eldasoft.sil.portgare.datatypes.CassaPrevidenzaType;
import it.eldasoft.sil.portgare.datatypes.ContoCorrenteDedicatoType;
import it.eldasoft.sil.portgare.datatypes.DatiImpresaAggiornabiliType;
import it.eldasoft.sil.portgare.datatypes.DatiImpresaDocument;
import it.eldasoft.sil.portgare.datatypes.DatiImpresaType;
import it.eldasoft.sil.portgare.datatypes.DatoAnnuoImpresaType;
import it.eldasoft.sil.portgare.datatypes.FirmatarioType;
import it.eldasoft.sil.portgare.datatypes.INAILType;
import it.eldasoft.sil.portgare.datatypes.INPSType;
import it.eldasoft.sil.portgare.datatypes.ISO9001Type;
import it.eldasoft.sil.portgare.datatypes.ImpresaType;
import it.eldasoft.sil.portgare.datatypes.IndirizzoEstesoType;
import it.eldasoft.sil.portgare.datatypes.IndirizzoType;
import it.eldasoft.sil.portgare.datatypes.IscrizioneElenchiRicostruzioneType;
import it.eldasoft.sil.portgare.datatypes.IscrizioneWhitelistAntimafiaType;
import it.eldasoft.sil.portgare.datatypes.RatingLegalitaAggiornabileType;
import it.eldasoft.sil.portgare.datatypes.RatingLegalitaType;
import it.eldasoft.sil.portgare.datatypes.RecapitiType;
import it.eldasoft.sil.portgare.datatypes.ReferenteImpresaAggiornabileType;
import it.eldasoft.sil.portgare.datatypes.ReferenteImpresaType;
import it.eldasoft.sil.portgare.datatypes.SOAType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.FirmatarioBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.CalendarValidator;
import org.apache.struts2.ServletActionContext;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlValidationError;
import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.util.ApsWebApplicationUtils;

/**
 * Contenitore dei dati del wizard registrazione impresa al portale
 *
 * @author Stefano.Sabbadin
 * @since 1.2
 */
public class WizardDatiImpresaHelper implements HttpSessionBindingListener, Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 4334920882908919009L;

	/**
	 * dati principali dell'impresa
	 */
	private IDatiPrincipaliImpresa datiPrincipaliImpresa;

	/**
	 * altri dati anagrafici dell'impresa nel caso di libero professionista.
	 */
	private IAltriDatiAnagraficiImpresa altriDatiAnagraficiImpresa;

	/**
	 * lista degli indirizzi di un'impresa
	 */
	private ArrayList<IIndirizzoImpresa> indirizziImpresa;

	/**
	 * lista dei legali rappresentanti correlati in essere all'impresa
	 */
	private ArrayList<ISoggettoImpresa> legaliRappresentantiImpresa;

	/**
	 * lista dei direttori tecnici correlati in essere all'impresa
	 */
	private ArrayList<ISoggettoImpresa> direttoriTecniciImpresa;

	/**
	 * lista delle altre cariche e qualifiche correlate all'impresa
	 */
	private ArrayList<ISoggettoImpresa> altreCaricheImpresa;

	/**
	 * lista dei collaboratori correlati all'impresa
	 */
	private ArrayList<ISoggettoImpresa> collaboratoriImpresa;

	/**
	 * lista dei dati ulteriori dell'impresa
	 */
	private IDatiUlterioriImpresa datiUlterioriImpresa;

	/**
	 * Identificativo univoco della comunicazione (valorizzata nel caso di
	 * completamento
	 */
	private Long idComunicazione;

	/**
	 * Elenco dei file generati durante la sessione del wizard (barcode, pdf, ...)
	 */
	private List<File> tempFiles;

	/**
	 * Flag di protezione dall'invio ripetuto dei dati se l'utente dopo aver
	 * effettuato un invio preme sul pulsante indietro del browser e poi
	 * riseleziona l'invio
	 */
	private boolean datiInviati;

	/**
	 * Flag per indicare se la ditta &egrave; individuale oppure no. Viene settata
	 * al processing della pagina in cui si imposta l'informazione.
	 */
	private boolean dittaIndividuale;

	/**
	 * Flag per indicare se l'impresa &egrave; di un libero professionista. Viene
	 * settata al processing della pagina in cui si imposta l'informazione.
	 */
	private boolean liberoProfessionista;

	/**
	 * Flag per indicare se l'impresa &egrave; un consorzio. Viene
	 * settata al processing della pagina in cui si imposta l'informazione.
	 */
	private boolean consorzio;
	
	/**
	 * Mantiene il valore della mail di riferimento dell'utente precedentemente
	 * impostata, inizialmente pari a null nel caso di nuova registrazione.
	 */
	private String mailUtentePrecedente;

	/**
	 * Mantiene il valore della mail impostata da interfaccia, alla quale viene
	 * inviata la mail di test.
	 */
	private String mailUtenteImpostata;
	/**
	 * Flag per testare se varia la mail di riferimento, ed in tal caso si invia
	 * una mail di test al nuovo indirizzo.
	 */
	private boolean mailVariata;
	/**
	 * Flag per testare se varia nuovamente la mail di riferimento dopo l'invio
	 * della mail di test.
	 */
	private boolean mailVariataDopoInvio;	
	/**
	 * Flag per testare se i dati sono stati importati da XML
	 */
	private boolean xmlImported;
		
	
	public WizardDatiImpresaHelper() {
		this.datiPrincipaliImpresa = new WizardDatiPrincipaliImpresaHelper();
		this.indirizziImpresa = new ArrayList<IIndirizzoImpresa>();
		this.altriDatiAnagraficiImpresa = new WizardAltriDatiAnagraficiImpresaHelper();
		this.legaliRappresentantiImpresa = new ArrayList<ISoggettoImpresa>();
		this.direttoriTecniciImpresa = new ArrayList<ISoggettoImpresa>();
		this.altreCaricheImpresa = new ArrayList<ISoggettoImpresa>();
		this.collaboratoriImpresa = new ArrayList<ISoggettoImpresa>();
		this.datiUlterioriImpresa = new WizardDatiUlterioriImpresaHelper();
		this.idComunicazione = null;
		this.tempFiles = new ArrayList<File>();
		this.datiInviati = false;
		this.dittaIndividuale = false;
		this.liberoProfessionista = false;
		this.consorzio = false;
		this.mailUtentePrecedente = null;
		this.mailUtenteImpostata = null;
		this.mailVariata = false;
		this.mailVariataDopoInvio = false;
		this.xmlImported = false;
	}
	
	/* --- POPOLAMENTO DA COMUNICAZIONE ---*/
	public WizardDatiImpresaHelper(DatiImpresaType datiImpresa) {
		this.datiPrincipaliImpresa = new WizardDatiPrincipaliImpresaHelper(
						datiImpresa.getImpresa());
		this.indirizziImpresa = new ArrayList<IIndirizzoImpresa>();
		this.altriDatiAnagraficiImpresa = new WizardAltriDatiAnagraficiImpresaHelper(
						datiImpresa.getImpresa().getAltriDatiAnagrafici());
		for (int i = 0; i < datiImpresa.getImpresa().sizeOfIndirizzoArray(); i++) {
			this.indirizziImpresa.add(new IndirizzoImpresaHelper(datiImpresa
							.getImpresa().getIndirizzoArray()[i]));
		}
		this.legaliRappresentantiImpresa = new ArrayList<ISoggettoImpresa>();
		for (int i = 0; i < datiImpresa.sizeOfLegaleRappresentanteArray(); i++) {
			this.legaliRappresentantiImpresa.add(new SoggettoImpresaHelper(
							datiImpresa.getLegaleRappresentanteArray()[i],
							CommonSystemConstants.TIPO_SOGGETTO_LEGALE_RAPPRESENTANTE));
		}
		this.direttoriTecniciImpresa = new ArrayList<ISoggettoImpresa>();
		for (int i = 0; i < datiImpresa.sizeOfDirettoreTecnicoArray(); i++) {
			this.direttoriTecniciImpresa.add(new SoggettoImpresaHelper(
							datiImpresa.getDirettoreTecnicoArray()[i],
							CommonSystemConstants.TIPO_SOGGETTO_DIRETTORE_TECNICO));
		}
		this.altreCaricheImpresa = new ArrayList<ISoggettoImpresa>();
		for (int i = 0; i < datiImpresa.sizeOfAltraCaricaArray(); i++) {
			this.altreCaricheImpresa.add(new SoggettoImpresaHelper(datiImpresa
							.getAltraCaricaArray()[i],
							CommonSystemConstants.TIPO_SOGGETTO_ALTRA_CARICA));
		}
		this.collaboratoriImpresa = new ArrayList<ISoggettoImpresa>();
		for (int i = 0; i < datiImpresa.sizeOfCollaboratoreArray(); i++) {
			this.collaboratoriImpresa.add(new SoggettoImpresaHelper(datiImpresa
							.getCollaboratoreArray()[i],
							CommonSystemConstants.TIPO_SOGGETTO_COLLABORATORE));
		}
		this.datiUlterioriImpresa = new WizardDatiUlterioriImpresaHelper(
				datiImpresa.getImpresa());
		
		this.idComunicazione = null;
		this.tempFiles = new ArrayList<File>();
		this.datiInviati = false;
	}

	/* --- POPOLAMENTO DA BO --- */
	public WizardDatiImpresaHelper(DatiImpresaAggiornabiliType datiImpresa) {
		this.datiPrincipaliImpresa = new WizardDatiPrincipaliImpresaHelper(
						datiImpresa.getImpresa());
		this.indirizziImpresa = new ArrayList<IIndirizzoImpresa>();
		this.altriDatiAnagraficiImpresa = new WizardAltriDatiAnagraficiImpresaHelper(
						datiImpresa.getImpresa().getAltriDatiAnagrafici());
		for (int i = 0; i < datiImpresa.getImpresa().sizeOfIndirizzoArray(); i++) {
			this.indirizziImpresa.add(new IndirizzoImpresaHelper(datiImpresa
							.getImpresa().getIndirizzoArray()[i]));
		}
		this.legaliRappresentantiImpresa = new ArrayList<ISoggettoImpresa>();
		for (int i = 0; i < datiImpresa.sizeOfLegaleRappresentanteArray(); i++) {
			this.legaliRappresentantiImpresa.add(new SoggettoImpresaHelper(
							datiImpresa.getLegaleRappresentanteArray()[i],
							CommonSystemConstants.TIPO_SOGGETTO_LEGALE_RAPPRESENTANTE));
		}
		this.direttoriTecniciImpresa = new ArrayList<ISoggettoImpresa>();
		for (int i = 0; i < datiImpresa.sizeOfDirettoreTecnicoArray(); i++) {
			this.direttoriTecniciImpresa.add(new SoggettoImpresaHelper(
							datiImpresa.getDirettoreTecnicoArray()[i],
							CommonSystemConstants.TIPO_SOGGETTO_DIRETTORE_TECNICO));
		}
		this.altreCaricheImpresa = new ArrayList<ISoggettoImpresa>();
		for (int i = 0; i < datiImpresa.sizeOfAltraCaricaArray(); i++) {
			this.altreCaricheImpresa.add(new SoggettoImpresaHelper(datiImpresa
							.getAltraCaricaArray()[i],
							CommonSystemConstants.TIPO_SOGGETTO_ALTRA_CARICA));
		}
		this.collaboratoriImpresa = new ArrayList<ISoggettoImpresa>();
		for (int i = 0; i < datiImpresa.sizeOfCollaboratoreArray(); i++) {
			this.collaboratoriImpresa.add(new SoggettoImpresaHelper(datiImpresa
							.getCollaboratoreArray()[i],
							CommonSystemConstants.TIPO_SOGGETTO_COLLABORATORE));
		}
		this.datiUlterioriImpresa = new WizardDatiUlterioriImpresaHelper(
						datiImpresa.getImpresa());
		
		this.idComunicazione = null;
		this.tempFiles = new ArrayList<File>();
		this.datiInviati = false;
	}

	@Override
	public void valueBound(HttpSessionBindingEvent arg0) {
	}

	/**
	 * Alla scadenza della sessione o alla rimozione del contenitore dalla
	 * sessione si rimuovono i file creati per questa gestione
	 *
	 * @param arg0
	 */
	@Override
	public void valueUnbound(HttpSessionBindingEvent arg0) {
		Iterator<File> iter = this.tempFiles.listIterator();
		while (iter.hasNext()) {
			File file = iter.next();
			if (file.exists()) {
				file.delete();
			}
		}
	}

	/**
	 * @return the dittaIndividuale
	 */
	public boolean isDittaIndividuale() {
		return dittaIndividuale;
	}

	/**
	 * @param dittaIndividuale the dittaIndividuale to set
	 */
	public void setDittaIndividuale(boolean dittaIndividuale) {
		this.dittaIndividuale = dittaIndividuale;
	}

	/**
	 * @return the liberoProfessionista
	 */
	public boolean isLiberoProfessionista() {
		return liberoProfessionista;
	}

	/**
	 * @param liberoProfessionista the liberoProfessionista to set
	 */
	public void setLiberoProfessionista(boolean liberoProfessionista) {
		this.liberoProfessionista = liberoProfessionista;
	}
	
	/**
	 * @return the consorzio
	 */
	public boolean isConsorzio() {
		return consorzio;
	}

	/**
	 * @param consorzio the consorzio to set
	 */
	public void setConsorzio(boolean consorzio) {
		this.consorzio = consorzio;
	}

	/**
	 * @return the datiPrincipaliImpresa
	 */
	public IDatiPrincipaliImpresa getDatiPrincipaliImpresa() {
		return datiPrincipaliImpresa;
	}

	/**
	 * @param datiPrincipaliImpresa the datiPrincipaliImpresa to set
	 */
	public void setDatiPrincipaliImpresa(
					IDatiPrincipaliImpresa datiPrincipaliImpresa) {
		this.datiPrincipaliImpresa = datiPrincipaliImpresa;
	}

	/**
	 * @return the indirizziImpresa
	 */
	public ArrayList<IIndirizzoImpresa> getIndirizziImpresa() {
		return indirizziImpresa;
	}

	/**
	 * @param indirizziImpresa the indirizziImpresa to set
	 */
	public void setIndirizziImpresa(
					ArrayList<IIndirizzoImpresa> indirizziImpresa) {
		this.indirizziImpresa = indirizziImpresa;
	}

	/**
	 * @return the altriDatiAnagraficiImpresa
	 */
	public IAltriDatiAnagraficiImpresa getAltriDatiAnagraficiImpresa() {
		return altriDatiAnagraficiImpresa;
	}

	/**
	 * @param altriDatiAnagraficiImpresa the altriDatiAnagraficiImpresa to set
	 */
	public void setAltriDatiAnagraficiImpresa(
					IAltriDatiAnagraficiImpresa altriDatiAnagraficiImpresa) {
		this.altriDatiAnagraficiImpresa = altriDatiAnagraficiImpresa;
	}

	/**
	 * @return the legaliRappresentantiImpresa
	 */
	public ArrayList<ISoggettoImpresa> getLegaliRappresentantiImpresa() {
		return legaliRappresentantiImpresa;
	}

	/**
	 * @param soggettiImpresa the soggettiImpresa to set
	 */
	public void setLegaliRappresentantiImpresa(
					ArrayList<ISoggettoImpresa> soggettiImpresa) {
		this.legaliRappresentantiImpresa = soggettiImpresa;
	}

	/**
	 * @return the direttoriTecniciImpresa
	 */
	public ArrayList<ISoggettoImpresa> getDirettoriTecniciImpresa() {
		return direttoriTecniciImpresa;
	}

	/**
	 * @param direttoriTecniciImpresa the direttoriTecniciImpresa to set
	 */
	public void setDirettoriTecniciImpresa(
					ArrayList<ISoggettoImpresa> direttoriTecniciImpresa) {
		this.direttoriTecniciImpresa = direttoriTecniciImpresa;
	}

	/**
	 * @return the altreCaricheImpresa
	 */
	public ArrayList<ISoggettoImpresa> getAltreCaricheImpresa() {
		return altreCaricheImpresa;
	}

	/**
	 * @param altreCaricheImpresa the altreCaricheImpresa to set
	 */
	public void setAltreCaricheImpresa(
					ArrayList<ISoggettoImpresa> altreCaricheImpresa) {
		this.altreCaricheImpresa = altreCaricheImpresa;
	}

	/**
	 * @return the collaboratoriImpresa
	 */
	public ArrayList<ISoggettoImpresa> getCollaboratoriImpresa() {
		return collaboratoriImpresa;
	}

	/**
	 * @param collaboratoriImpresa the collaboratoriImpresa to set
	 */
	public void setCollaboratoriImpresa(
					ArrayList<ISoggettoImpresa> collaboratoriImpresa) {
		this.collaboratoriImpresa = collaboratoriImpresa;
	}

	/**
	 * @return the datiUlterioriImpresa
	 */
	public IDatiUlterioriImpresa getDatiUlterioriImpresa() {
		return datiUlterioriImpresa;
	}

	/**
	 * @param datiUlterioriImpresa the datiUlterioriImpresa to set
	 */
	public void setDatiUlterioriImpresa(
					IDatiUlterioriImpresa datiUlterioriImpresa) {
		this.datiUlterioriImpresa = datiUlterioriImpresa;
	}

	/**
	 * @return the idComunicazione
	 */
	public Long getIdComunicazione() {
		return idComunicazione;
	}

	/**
	 * @param idComunicazione the idComunicazione to set
	 */
	public void setIdComunicazione(Long idComunicazione) {
		this.idComunicazione = idComunicazione;
	}

	/**
	 * @return the tempFiles
	 */
	public List<File> getTempFiles() {
		return tempFiles;
	}

	/**
	 * @return the datiInviati
	 */
	public boolean isDatiInviati() {
		return datiInviati;
	}

	/**
	 * @param datiInviati the datiInviati to set
	 */
	public void setDatiInviati(boolean datiInviati) {
		this.datiInviati = datiInviati;
	}

	/**
	 * @return the mailUtentePrecedente
	 */
	public String getMailUtentePrecedente() {
		return mailUtentePrecedente;
	}

	/**
	 * @param mailUtentePrecedente the mailUtentePrecedente to set
	 */
	public void setMailUtentePrecedente(String mailUtentePrecedente) {
		this.mailUtentePrecedente = mailUtentePrecedente;
	}

	/**
	 * @return the mailUtenteImpostata
	 */
	public String getMailUtenteImpostata() {
		return mailUtenteImpostata;
	}

	/**
	 * @param mailUtenteImpostata the mailUtenteImpostata to set
	 */
	public void setMailUtenteImpostata(String mailUtenteImpostata) {
		this.mailUtenteImpostata = mailUtenteImpostata;
	}

	/**
	 * @return the mailVariata
	 */
	public boolean isMailVariata() {
		return mailVariata;
	}

	/**
	 * @param mailVariata the mailVariata to set
	 */
	public void setMailVariata(boolean mailVariata) {
		this.mailVariata = mailVariata;
	}

	/**
	 * @return the mailVariataDopoInvio
	 */
	public boolean isMailVariataDopoInvio() {
		return mailVariataDopoInvio;
	}

	/**
	 * @param mailVariataDopoInvio the mailVariataDopoInvio to set
	 */
	public void setMailVariataDopoInvio(boolean mailVariataDopoInvio) {
		this.mailVariataDopoInvio = mailVariataDopoInvio;
	}
		
	public boolean isXmlImported() {
		return xmlImported;
	}

	public void setXmlImported(boolean xmlImported) {
		this.xmlImported = xmlImported;
	}

	/**
	 * Crea l'oggetto documento per la generazione della stringa XML per l'inoltro
	 * della richiesta di aggiornamento anagrafica al backoffice
	 *
	 * @return oggetto documento contenente i dati di aggiornamento
	 * dell'anagrafica impresa
	 */
	public XmlObject getXmlDocumentAggiornamentoAnagrafica() {
		AggiornamentoAnagraficaImpresaDocument document = AggiornamentoAnagraficaImpresaDocument.Factory
						.newInstance();
		document.documentProperties().setEncoding("UTF-8");
		AggAnagraficaImpresaType aggAnagrImpresa = document
						.addNewAggiornamentoAnagraficaImpresa();
		// set dati impresa
		DatiImpresaType datiImpresa = aggAnagrImpresa.addNewDatiImpresa();
		this.addImpresa(datiImpresa);
		return document;
	}

	/**
	 * Crea i dati dell'impresa
	 *
	 * @param datiImpresa i dati dell'impresa
	 */
	protected void addImpresa(DatiImpresaType datiImpresa) {
		// impresa
		WizardDatiImpresaHelper.addNewImpresa(
				this.datiPrincipaliImpresa,
				this.datiUlterioriImpresa, 
				this.indirizziImpresa,
				this.altriDatiAnagraficiImpresa, 
				this.liberoProfessionista,
				datiImpresa);
		// set dei soggetti
		if (!this.liberoProfessionista) {
			WizardDatiImpresaHelper.addNewReferentiImpresa(
							this.legaliRappresentantiImpresa, datiImpresa);
			WizardDatiImpresaHelper.addNewReferentiImpresa(
							this.direttoriTecniciImpresa, datiImpresa);
			WizardDatiImpresaHelper.addNewReferentiImpresa(
							this.altreCaricheImpresa, datiImpresa);
			WizardDatiImpresaHelper.addNewReferentiImpresa(
							this.collaboratoriImpresa, datiImpresa);
		}
	}

	/**
	 * Crea un oggetto della classe ImpresaType nel contenitore dei dati impresa
	 * prelevando i dati dalla sorgente in input
	 *
	 * @param datiPrincipali oggetto sorgente dei dati
	 * @param datiUlteriori ulteriori dati dell'impresa
	 * @param indirizzi altre sedi
	 * @param altriDatiAnagrafici altri dati anagrafici, valorizzati solo per i
	 * liberi professionisti
	 * @param liberoProfessionista indica se è o meno un libero professionista
	 * @param datiImpresa dati impresa in cui aggiungere un oggetto della classe
	 * ImpresaType con i dati della sorgente
	 */
	public static void addNewImpresa(
			IDatiPrincipaliImpresa datiPrincipali,
			IDatiUlterioriImpresa datiUlteriori,
			ArrayList<IIndirizzoImpresa> indirizzi,
			IAltriDatiAnagraficiImpresa altriDatiAnagrafici,
			boolean liberoProfessionista, 
			DatiImpresaType datiImpresa)
	{
		ImpresaType impresa = datiImpresa.addNewImpresa();

		impresa.setRagioneSociale(datiPrincipali.getRagioneSociale());
		impresa.setNaturaGiuridica(datiPrincipali.getNaturaGiuridica());
		impresa.setTipoImpresa(datiPrincipali.getTipoImpresa());
		impresa.setCodiceFiscale(datiPrincipali.getCodiceFiscale());
		if (StringUtils.isNotBlank(datiPrincipali.getPartitaIVA())) {
			impresa.setPartitaIVA(datiPrincipali.getPartitaIVA());
		}
		if (StringUtils.isNotBlank(datiUlteriori.getOggettoSociale())) {
			impresa.setOggettoSociale(datiUlteriori.getOggettoSociale());
		}
		
		// micro piccola media impresa
		if (StringUtils.isNotBlank(datiPrincipali.getMicroPiccolaMediaImpresa())) {
			impresa.setMicroPiccolaMediaImpresa(datiPrincipali.getMicroPiccolaMediaImpresa());		
		}		
		
		// NB: "MicroPiccolaMediaImpresa" non e' piu' gestito su form
		// se non valorizzato va impostato in base al valore di 
		// "ClasseDimensioneDipendenti" secondo la codifica: 
		//  - se CLADIM.IMPR = 4 	 ISMPMI.IMPR = '2'
		//  - se CLADIM.IMPR = 1,2,3 ISMPMI.IMPR = '1'
		//	- se CLADIM.IMPR null 	 ISMPMI.IMPR null
		if ( !impresa.isSetMicroPiccolaMediaImpresa() ) {				 
			if("4".equals(datiUlteriori.getClasseDimensioneDipendenti())) {
				impresa.setMicroPiccolaMediaImpresa("2");
			} else if("1".equals(datiUlteriori.getClasseDimensioneDipendenti()) ||
					  "2".equals(datiUlteriori.getClasseDimensioneDipendenti()) ||
					  "3".equals(datiUlteriori.getClasseDimensioneDipendenti())) {
				impresa.setMicroPiccolaMediaImpresa("1");
			}		
		}
		
		// sede legale impresa
		IndirizzoType sedeLegale = impresa.addNewSedeLegale();
		sedeLegale.setIndirizzo(datiPrincipali.getIndirizzoSedeLegale());
		sedeLegale.setNumCivico(datiPrincipali.getNumCivicoSedeLegale());
		if (StringUtils.isNotBlank(datiPrincipali.getCapSedeLegale())) {
			sedeLegale.setCap(datiPrincipali.getCapSedeLegale());
		}
		sedeLegale.setComune(datiPrincipali.getComuneSedeLegale());
		if (StringUtils.isNotBlank(datiPrincipali.getProvinciaSedeLegale())) {
			sedeLegale.setProvincia(datiPrincipali.getProvinciaSedeLegale());
		}
		sedeLegale.setNazione(datiPrincipali.getNazioneSedeLegale());

		if (StringUtils.isNotBlank(datiPrincipali.getSitoWeb())) {
			impresa.setSitoWeb(datiPrincipali.getSitoWeb());
		}

		// recapiti impresa
		RecapitiType recapiti = impresa.addNewRecapiti();
		// recapiti.setModalitaComunicazione(source
		// .getModalitaComunicazioneRecapito());
		recapiti.setTelefono(datiPrincipali.getTelefonoRecapito());
		if (StringUtils.isNotBlank(datiPrincipali.getFaxRecapito())) {
			recapiti.setFax(datiPrincipali.getFaxRecapito());
		}
		if (StringUtils.isNotBlank(datiPrincipali.getCellulareRecapito())) {
			recapiti.setCellulare(datiPrincipali.getCellulareRecapito());
		}
		if (StringUtils.isNotBlank(datiPrincipali.getEmailRecapito())) {
			recapiti.setEmail(datiPrincipali.getEmailRecapito());
		}
		if (StringUtils.isNotBlank(datiPrincipali.getEmailPECRecapito())) {
			recapiti.setPec(datiPrincipali.getEmailPECRecapito());
		}

		// altri indirizzi
		if (indirizzi.size() > 0) {
			for (int i = 0; i < indirizzi.size(); i++) {
				IndirizzoEstesoType indirizzo = impresa.addNewIndirizzo();
				indirizzo.setTipoIndirizzo(indirizzi.get(i).getTipoIndirizzo());
				indirizzo.setIndirizzo(indirizzi.get(i).getIndirizzo());
				indirizzo.setNumCivico(indirizzi.get(i).getNumCivico());
				if (StringUtils.isNotBlank(indirizzi.get(i).getCap())) {
					indirizzo.setCap(indirizzi.get(i).getCap());
				}
				indirizzo.setComune(indirizzi.get(i).getComune());
				if (StringUtils.isNotBlank(indirizzi.get(i).getProvincia())) {
					indirizzo.setProvincia(indirizzi.get(i).getProvincia());
				}
				indirizzo.setNazione(indirizzi.get(i).getNazione());
				if (StringUtils.isNotBlank(indirizzi.get(i).getTelefono())) {
					indirizzo.setTelefono(indirizzi.get(i).getTelefono());
				}
				if (StringUtils.isNotBlank(indirizzi.get(i).getFax())) {
					indirizzo.setFax(indirizzi.get(i).getFax());
				}
			}
		}

		// dati del libero professionista
		if (liberoProfessionista) {
			AltriDatiAnagraficiType altriDati = impresa.addNewAltriDatiAnagrafici();
			if (StringUtils.isNotBlank(altriDatiAnagrafici.getTitolo())) {
				altriDati.setTitolo(altriDatiAnagrafici.getTitolo());
			}
			if (StringUtils.isNotBlank(altriDatiAnagrafici.getCognome())) {
				altriDati.setCognome(altriDatiAnagrafici.getCognome());
			}
			if (StringUtils.isNotBlank(altriDatiAnagrafici.getNome())) {
				altriDati.setNome(altriDatiAnagrafici.getNome());
			}
			
			altriDati.setDataNascita(CalendarValidator.getInstance().validate(
							altriDatiAnagrafici.getDataNascita(), "dd/MM/yyyy"));
			altriDati.setComuneNascita(altriDatiAnagrafici.getComuneNascita());
			if (StringUtils.isNotBlank(altriDatiAnagrafici.getProvinciaNascita())) {
				altriDati.setProvinciaNascita(altriDatiAnagrafici.getProvinciaNascita());
			}
			altriDati.setSesso(altriDatiAnagrafici.getSesso());

			AlboProfessionaleType albo = altriDati.addNewAlboProfessionale();
			if (StringUtils.isNotBlank(altriDatiAnagrafici.getTipologiaAlboProf())) {
				albo.setTipologia(altriDatiAnagrafici.getTipologiaAlboProf());
			}
			if (StringUtils.isNotBlank(altriDatiAnagrafici.getNumIscrizioneAlboProf())) {
				albo.setNumIscrizione(altriDatiAnagrafici.getNumIscrizioneAlboProf());
			}
			if (StringUtils.isNotBlank(altriDatiAnagrafici.getDataIscrizioneAlboProf())) {
				albo.setDataIscrizione(CalendarValidator.getInstance().validate(
								altriDatiAnagrafici.getDataIscrizioneAlboProf(), "dd/MM/yyyy"));
			}
			if (StringUtils.isNotBlank(altriDatiAnagrafici.getProvinciaIscrizioneAlboProf())) {
				albo.setProvinciaIscrizione(altriDatiAnagrafici.getProvinciaIscrizioneAlboProf());
			}

			CassaPrevidenzaType cassa = altriDati.addNewCassaPrevidenza();
			if (StringUtils.isNotBlank(altriDatiAnagrafici.getTipologiaCassaPrevidenza())) {
				cassa.setTipologia(altriDatiAnagrafici.getTipologiaCassaPrevidenza());
			}
			if (StringUtils.isNotBlank(altriDatiAnagrafici.getNumMatricolaCassaPrevidenza())) {
				cassa.setNumMatricola(altriDatiAnagrafici.getNumMatricolaCassaPrevidenza());
			}
		}

		// dati della camera di commercio
		CameraCommercioType cciaa = impresa.addNewCciaa();
		if (StringUtils.isNotBlank(datiUlteriori.getIscrittoCCIAA())) {
			cciaa.setIscritto(datiUlteriori.getIscrittoCCIAA());
		}
		if (StringUtils.isNotBlank(datiUlteriori.getNumRegistroDitteCCIAA())) {
			cciaa.setNumRegistroDitte(datiUlteriori.getNumRegistroDitteCCIAA());
		}
		if (StringUtils.isNotBlank(datiUlteriori.getDataDomandaIscrizioneCCIAA())) {
			cciaa.setDataDomandaIscrizione(CalendarValidator.getInstance()
							.validate(datiUlteriori.getDataDomandaIscrizioneCCIAA(),
											"dd/MM/yyyy"));
		}
		if (StringUtils.isNotBlank(datiUlteriori.getNumIscrizioneCCIAA())) {
			cciaa.setNumIscrizione(datiUlteriori.getNumIscrizioneCCIAA());
		}
		if (StringUtils.isNotBlank(datiUlteriori.getDataIscrizioneCCIAA())) {
			cciaa.setDataIscrizione(CalendarValidator.getInstance().validate(
							datiUlteriori.getDataIscrizioneCCIAA(), "dd/MM/yyyy"));
		}
		if (StringUtils.isNotBlank(datiUlteriori.getProvinciaIscrizioneCCIAA())) {
			cciaa.setProvinciaIscrizione(datiUlteriori
							.getProvinciaIscrizioneCCIAA());
		}
		if (StringUtils.isNotBlank(datiUlteriori
						.getDataNullaOstaAntimafiaCCIAA())) {
			cciaa.setDataNullaOstaAntimafia(CalendarValidator.getInstance()
							.validate(datiUlteriori.getDataNullaOstaAntimafiaCCIAA(),
											"dd/MM/yyyy"));
		}

		// dati del DURC
		if (StringUtils.isNotBlank(datiUlteriori.getSoggettoNormativeDURC())) {
			impresa.setSoggettoDURC(datiUlteriori.getSoggettoNormativeDURC());
		}
		if (StringUtils.isNotBlank(datiUlteriori.getSettoreProduttivoDURC())) {
			impresa.setSettoreProduttivo(datiUlteriori.getSettoreProduttivoDURC());
		}

		// dati inps
		INPSType inps = impresa.addNewInps();
		if (StringUtils.isNotBlank(datiUlteriori.getNumIscrizioneINPS())) {
			inps.setNumIscrizione(datiUlteriori.getNumIscrizioneINPS());
		}
		if (StringUtils.isNotBlank(datiUlteriori.getDataIscrizioneINPS())) {
			inps.setDataIscrizione(CalendarValidator.getInstance().validate(
							datiUlteriori.getDataIscrizioneINPS(), "dd/MM/yyyy"));
		}
		if (StringUtils.isNotBlank(datiUlteriori.getLocalitaIscrizioneINPS())) {
			inps.setLocalitaIscrizione(datiUlteriori.getLocalitaIscrizioneINPS());
		}
		if (StringUtils.isNotBlank(datiUlteriori.getPosizContributivaIndividualeINPS())) {
			inps.setPosizContributivaIndividuale(datiUlteriori.getPosizContributivaIndividualeINPS());
		}
		
		// dati inail
		INAILType inail = impresa.addNewInail();
		if (StringUtils.isNotBlank(datiUlteriori.getNumIscrizioneINAIL())) {
			inail.setNumIscrizione(datiUlteriori.getNumIscrizioneINAIL());
		}
		if (StringUtils.isNotBlank(datiUlteriori.getDataIscrizioneINAIL())) {
			inail.setDataIscrizione(CalendarValidator.getInstance().validate(
							datiUlteriori.getDataIscrizioneINAIL(), "dd/MM/yyyy"));
		}
		if (StringUtils.isNotBlank(datiUlteriori.getLocalitaIscrizioneINAIL())) {
			inail.setLocalitaIscrizione(datiUlteriori.getLocalitaIscrizioneINAIL());
		}
		if (StringUtils.isNotBlank(datiUlteriori.getPosizAssicurativaINAIL())) {
			inail.setPosizAssicurativa(datiUlteriori.getPosizAssicurativaINAIL());
		}
		
		// dati cassa edile
		CassaEdileType cassa = impresa.addNewCassaEdile();
		if (StringUtils.isNotBlank(datiUlteriori.getNumIscrizioneCassaEdile())) {
			cassa.setNumIscrizione(datiUlteriori.getNumIscrizioneCassaEdile());
		}
		if (StringUtils.isNotBlank(datiUlteriori.getDataIscrizioneCassaEdile())) {
			cassa.setDataIscrizione(CalendarValidator.getInstance().validate(
							datiUlteriori.getDataIscrizioneCassaEdile(), "dd/MM/yyyy"));
		}
		if (StringUtils.isNotBlank(datiUlteriori.getLocalitaIscrizioneCassaEdile())) {
			cassa.setLocalitaIscrizione(datiUlteriori.getLocalitaIscrizioneCassaEdile());
		}
		if (StringUtils.isNotBlank(datiUlteriori.getCodiceCassaEdile())) {
			cassa.setCodice(datiUlteriori.getCodiceCassaEdile());
		}
		
		// altri istituti previdenziali
		if (StringUtils.isNotBlank(datiUlteriori.getAltriIstitutiPrevidenziali())) {
			impresa.setAltriIstitutiPrevidenziali(datiUlteriori.getAltriIstitutiPrevidenziali());
		}
		
		// dati soa
		SOAType soa = impresa.addNewSoa();
		if (StringUtils.isNotBlank(datiUlteriori.getNumIscrizioneSOA())) {
			soa.setNumIscrizione(datiUlteriori.getNumIscrizioneSOA());
		}
		if (StringUtils.isNotBlank(datiUlteriori.getDataIscrizioneSOA())) {
			soa.setDataIscrizione(CalendarValidator.getInstance().validate(
							datiUlteriori.getDataIscrizioneSOA(), "dd/MM/yyyy"));
		}
		if (StringUtils.isNotBlank(datiUlteriori.getDataScadenzaQuinquennaleSOA())) {
			soa.setDataScadenza(CalendarValidator.getInstance().validate(
							datiUlteriori.getDataScadenzaQuinquennaleSOA(), "dd/MM/yyyy"));
		}
		if (StringUtils.isNotBlank(datiUlteriori
						.getDataUltimaRichiestaIscrizioneSOA())) {
			soa.setDataUltimaRichiestaIscrizione(CalendarValidator
				.getInstance()
				.validate(datiUlteriori.getDataUltimaRichiestaIscrizioneSOA(),
						  "dd/MM/yyyy"));
		}
		
		if (StringUtils.isNotBlank(datiUlteriori.getDataScadenzaTriennaleSOA())) {
			soa.setDataScadenzaTriennale(CalendarValidator.getInstance().validate(
							datiUlteriori.getDataScadenzaTriennaleSOA(), "dd/MM/yyyy"));
		}
		
		if (StringUtils.isNotBlank(datiUlteriori.getDataVerificaTriennaleSOA())) {
			soa.setDataVerificaTriennale(CalendarValidator.getInstance().validate(
							datiUlteriori.getDataVerificaTriennaleSOA(), "dd/MM/yyyy"));
		}
		
		if (StringUtils.isNotBlank(datiUlteriori.getDataScadenzaIntermediaSOA())) {
			soa.setDataScadenzaIntermedia(CalendarValidator.getInstance().validate(
							datiUlteriori.getDataScadenzaIntermediaSOA(), "dd/MM/yyyy"));
		}
		
		if (StringUtils.isNotBlank(datiUlteriori.getOrganismoCertificatoreSOA())) {
			soa.setOrganismoCertificatore(datiUlteriori
							.getOrganismoCertificatoreSOA());
		}
		
		// dati della certificazione iso 9001
		ISO9001Type iso9001 = impresa.addNewIso9001();
		if (StringUtils.isNotBlank(datiUlteriori.getNumIscrizioneISO())) {
			iso9001.setNumIscrizione(datiUlteriori.getNumIscrizioneISO());
		}
		if (StringUtils.isNotBlank(datiUlteriori.getDataScadenzaISO())) {
			iso9001.setDataScadenza(CalendarValidator.getInstance().validate(
							datiUlteriori.getDataScadenzaISO(), "dd/MM/yyyy"));
		}
		if (StringUtils.isNotBlank(datiUlteriori.getOrganismoCertificatoreISO())) {
			iso9001.setOrganismoCertificatore(datiUlteriori
							.getOrganismoCertificatoreISO());
		}
		
		// dati iscrizione whitelist antimafia
		boolean iscritto = false;
		IscrizioneWhitelistAntimafiaType whitelist = impresa.addNewIscrizioneWhitelistAntimafia();
		if (StringUtils.isNotBlank(datiUlteriori.getIscrittoWhitelistAntimafia())) {
			whitelist.setIscritto(datiUlteriori.getIscrittoWhitelistAntimafia());
			iscritto = true;
		}			
		if (iscritto && StringUtils.isNotBlank(datiUlteriori.getSedePrefetturaWhitelistAntimafia())) {
			whitelist.setSedePrefetturaCompetente(datiUlteriori.getSedePrefetturaWhitelistAntimafia());
		}
		if(iscritto) {
			String sezioni = WizardDatiUlterioriImpresaHelper
				.getSezioniIscrizioneForBO(datiUlteriori.getSezioniIscrizioneWhitelistAntimafia());
			if (StringUtils.isNotBlank(sezioni)) {
				whitelist.setSezioniIscrizione(sezioni);
			}
		}
		if (iscritto && StringUtils.isNotBlank(datiUlteriori.getDataIscrizioneWhitelistAntimafia())) {
			whitelist.setDataIscrizione(CalendarValidator.getInstance().validate(
					datiUlteriori.getDataIscrizioneWhitelistAntimafia(), "dd/MM/yyyy"));
		}
		if (iscritto && StringUtils.isNotBlank(datiUlteriori.getDataScadenzaIscrizioneWhitelistAntimafia())) {
			whitelist.setDataScadenzaIscrizione(CalendarValidator.getInstance().validate(
					datiUlteriori.getDataScadenzaIscrizioneWhitelistAntimafia(), "dd/MM/yyyy"));
		}
		if (iscritto && StringUtils.isNotBlank(datiUlteriori.getAggiornamentoWhitelistAntimafia())) {
			whitelist.setAggiornamento(datiUlteriori.getAggiornamentoWhitelistAntimafia());
		}	

		// dati iscrizione elenchi ricostruzione antimafia
		iscritto = false;
		IscrizioneElenchiRicostruzioneType elenchiRicostruzione = impresa.addNewIscrizioneElenchiRicostruzione();
		if (StringUtils.isNotBlank(datiUlteriori.getIscrittoAnagrafeAntimafiaEsecutori())) {
			elenchiRicostruzione.setIscrittoAnagrafeAntimafiaEsecutori(datiUlteriori.getIscrittoAnagrafeAntimafiaEsecutori());
			iscritto = true;
		}
		if (iscritto && StringUtils.isNotBlank(datiUlteriori.getDataScadenzaIscrizioneAnagrafeAntimafiaEsecutori())) {
			elenchiRicostruzione.setDataScadenza(CalendarValidator.getInstance().validate(
					datiUlteriori.getDataScadenzaIscrizioneAnagrafeAntimafiaEsecutori(), "dd/MM/yyyy"));
		}
		if (iscritto && StringUtils.isNotBlank(datiUlteriori.getRinnovoIscrizioneInCorsoAnagrafeAntimafiaEsecutori())) {
			elenchiRicostruzione.setRinnovoIscrizioneInCorso(datiUlteriori.getRinnovoIscrizioneInCorsoAnagrafeAntimafiaEsecutori());
		}
		if (StringUtils.isNotBlank(datiUlteriori.getIscrittoElencoSpecialeProfessionisti())) {
			elenchiRicostruzione.setIscrittoElencoSpecialeProfessionisti(datiUlteriori.getIscrittoElencoSpecialeProfessionisti());
		}
		
		// dati rating legalita
		iscritto = false;
		RatingLegalitaType rating = impresa.addNewRatingLegalita();
		if (StringUtils.isNotBlank(datiUlteriori.getPossiedeRatingLegalita())) {
			rating.setPossiedeRating(datiUlteriori.getPossiedeRatingLegalita());
			iscritto = true;
		}
		if (iscritto && StringUtils.isNotBlank(datiUlteriori.getRatingLegalita())) {
			rating.setRating(datiUlteriori.getRatingLegalita());
		}
		if (iscritto && StringUtils.isNotBlank(datiUlteriori.getDataScadenzaPossessoRatingLegalita())) {
			rating.setDataScadenza(CalendarValidator.getInstance().validate(
					datiUlteriori.getDataScadenzaPossessoRatingLegalita(), "dd/MM/yyyy"));
		}
		if (iscritto && StringUtils.isNotBlank(datiUlteriori.getAggiornamentoRatingLegalita())) {
			rating.setAggiornamentoRatingInCorso(datiUlteriori.getAggiornamentoRatingLegalita());
		}
		
		// altre attestazioni
		if (StringUtils.isNotBlank(datiUlteriori.getAltreCertificazioniAttestazioni())) {
			impresa.setAltreCertificazioniAttestazioni(datiUlteriori.getAltreCertificazioniAttestazioni());
		}
		
		// dati conto corrente dedicato
		ContoCorrenteDedicatoType ccDedicato = impresa.addNewContoCorrente();
		if (StringUtils.isNotBlank(datiUlteriori.getCodiceIBANCCDedicato())) {
			ccDedicato.setEstremi(datiUlteriori.getCodiceIBANCCDedicato());
		}
		if (StringUtils.isNotBlank(datiUlteriori.getCodiceBICCCDedicato())) {
			ccDedicato.setBic(datiUlteriori.getCodiceBICCCDedicato());
		}
		if (StringUtils.isNotBlank(datiUlteriori.getSoggettiAbilitatiCCDedicato())) {
			ccDedicato.setSoggettiAbilitati(datiUlteriori.getSoggettiAbilitatiCCDedicato());
		}
		
		// socio unico
		if (StringUtils.isNotBlank(datiUlteriori.getSocioUnico())) {
			impresa.setSocioUnico(datiUlteriori.getSocioUnico());
		}
		
		// regime fiscale
		if (StringUtils.isNotBlank(datiUlteriori.getRegimeFiscale())) {
			impresa.setRegimeFiscale(datiUlteriori.getRegimeFiscale());
		}
		
		// settore attivita' economica
		if (StringUtils.isNotBlank(datiUlteriori.getSettoreAttivitaEconomica())) {
			impresa.setSettoreAttivitaEconomica(datiUlteriori.getSettoreAttivitaEconomica());
		}
		
		// abilitazione preventiva
		AbilitazionePreventivaType abilitazione = impresa
						.addNewAbilitazionePreventiva();
		if (StringUtils.isNotBlank(datiUlteriori
						.getDataScadenzaAbilitPreventiva())) {
			abilitazione.setDataScadenzaRinnovo(CalendarValidator.getInstance()
							.validate(datiUlteriori.getDataScadenzaAbilitPreventiva(),
											"dd/MM/yyyy"));
		}
		if (StringUtils.isNotBlank(datiUlteriori.getRinnovoAbilitPreventiva())) {
			abilitazione.setFaseRinnovo(datiUlteriori
							.getRinnovoAbilitPreventiva());
		}
		if (StringUtils.isNotBlank(datiUlteriori
						.getDataRichRinnovoAbilitPreventiva())) {
			abilitazione.setDataRichiestaRinnovo(CalendarValidator
							.getInstance().validate(
											datiUlteriori.getDataRichRinnovoAbilitPreventiva(),
											"dd/MM/yyyy"));
		}
		
		// zone attivita
		String zoneAttivita = WizardDatiUlterioriImpresaHelper
						.getZoneAttivitaForBO(datiUlteriori.getZoneAttivita());
		if (StringUtils.isNotBlank(zoneAttivita)) {
			impresa.setZoneAttivita(zoneAttivita);
		}
		
		// dati annui e assunzioni obbligate
		if (StringUtils.isNotBlank(datiUlteriori.getAssunzioniObbligate())) {
			impresa.setAssunzioniObbligate(datiUlteriori.getAssunzioniObbligate());
		}
		if(datiUlteriori.getAnni() != null){
			addDatoAnnuo(datiUlteriori, 0, impresa);
			addDatoAnnuo(datiUlteriori, 1, impresa);
			addDatoAnnuo(datiUlteriori, 2, impresa);
		}
		if (StringUtils.isNotBlank(datiUlteriori.getClasseDimensioneDipendenti())) {
			impresa.setClasseDimensione(datiUlteriori.getClasseDimensioneDipendenti());
		}
		
		if (StringUtils.isNotBlank(datiUlteriori.getUlterioriDichiarazioni())) {
			impresa.setUlterioriDichiarazioni(datiUlteriori.getUlterioriDichiarazioni());
		}
	}

	/**
	 * Aggiunge i dati per un determinato anno
	 *
	 * @param datiUlteriori dati da consultare per estrarre i dati annuali
	 * @param indice indice dell'elemento nel triennio, oggetto di modifica
	 * @param impresa oggetto da popolare
	 */
	public static void addDatoAnnuo(IDatiUlterioriImpresa datiUlteriori, int indice,
					ImpresaType impresa) {
		DatoAnnuoImpresaType anno = impresa.addNewDatoAnnuo();
		anno.setAnno(datiUlteriori.getAnni()[indice]);
		if (datiUlteriori.getNumDipendenti()[indice] != null) {
			anno.setDipendenti(datiUlteriori.getNumDipendenti()[indice]);
		}
	}

	/**
	 * Inserisce l'elenco di soggetti nel dettaglio dell'impresa
	 *
	 * @param soggetti elenco dei soggetti da inserire
	 * @param datiImpresa dettaglio impresa in cui inserire i dati
	 * @param listaC 
	 */
	public static void addNewReferentiImpresa(ArrayList<ISoggettoImpresa> soggetti, DatiImpresaType datiImpresa) {

		ReferenteImpresaType referente = null;
		if (soggetti.size() > 0) {
			for (ISoggettoImpresa soggetto : soggetti) {
				if (CommonSystemConstants.TIPO_SOGGETTO_LEGALE_RAPPRESENTANTE
								.equals(soggetto.getTipoSoggetto())) {
					referente = datiImpresa.addNewLegaleRappresentante();
				} else if (CommonSystemConstants.TIPO_SOGGETTO_DIRETTORE_TECNICO
								.equals(soggetto.getTipoSoggetto())) {
					referente = datiImpresa.addNewDirettoreTecnico();
				} else if (CommonSystemConstants.TIPO_SOGGETTO_ALTRA_CARICA
								.equals(soggetto.getTipoSoggetto())) {
					referente = datiImpresa.addNewAltraCarica();
				} else if (CommonSystemConstants.TIPO_SOGGETTO_COLLABORATORE
								.equals(soggetto.getTipoSoggetto())) {
					referente = datiImpresa.addNewCollaboratore();
				}
				fillReferenteFields(referente, soggetto);
			}
		}
	}

	public static void fillReferenteFields(ReferenteImpresaType referente, ISoggettoImpresa soggetto) {

		if (StringUtils.isNotBlank(soggetto.getQualifica())) {
			referente.setQualifica(soggetto.getQualifica());
		}
		referente.setCognome(soggetto.getCognome());
		referente.setNome(soggetto.getNome());
		if (StringUtils.isNotBlank(soggetto.getTitolo())) {
			referente.setTitolo(soggetto.getTitolo());
		}
		referente.setCodiceFiscale(soggetto.getCodiceFiscale());
		referente.setSesso(soggetto.getSesso());
		IndirizzoType residenza = referente.addNewResidenza();
		residenza.setIndirizzo(soggetto.getIndirizzo());
		residenza.setNumCivico(soggetto.getNumCivico());
		if (StringUtils.isNotBlank(soggetto.getCap())) {
			residenza.setCap(soggetto.getCap());
		}
		residenza.setComune(soggetto.getComune());
		if (StringUtils.isNotBlank(soggetto.getProvincia())) {
			residenza.setProvincia(soggetto.getProvincia());
		}
		residenza.setNazione(soggetto.getNazione());
		referente.setDataNascita(CalendarValidator.getInstance()
						.validate(soggetto.getDataNascita(), "dd/MM/yyyy"));
		referente.setComuneNascita(soggetto.getComuneNascita());
		referente.setProvinciaNascita(soggetto.getProvinciaNascita());
		AlboProfessionaleType albo = referente.addNewAlboProfessionale();
		if (StringUtils.isNotBlank(soggetto.getTipologiaAlboProf())) {
			albo.setTipologia(soggetto.getTipologiaAlboProf());
		}
		if (StringUtils.isNotBlank(soggetto.getNumIscrizioneAlboProf())) {
			albo.setNumIscrizione(soggetto.getNumIscrizioneAlboProf());
		}
		if (StringUtils.isNotBlank(soggetto.getDataIscrizioneAlboProf())) {
			albo.setDataIscrizione(CalendarValidator.getInstance()
							.validate(soggetto.getDataIscrizioneAlboProf(), "dd/MM/yyyy"));
		}
		if (StringUtils.isNotBlank(soggetto.getProvinciaIscrizioneAlboProf())) {
			albo.setProvinciaIscrizione(soggetto.getProvinciaIscrizioneAlboProf());
		}
		CassaPrevidenzaType cassa = referente.addNewCassaPrevidenza();
		if (StringUtils.isNotBlank(soggetto.getTipologiaCassaPrevidenza())) {
			cassa.setTipologia(soggetto.getTipologiaCassaPrevidenza());
		}
		if (StringUtils.isNotBlank(soggetto.getNumMatricolaCassaPrevidenza())) {
			cassa.setNumMatricola(soggetto.getNumMatricolaCassaPrevidenza());
		}
		if (StringUtils.isNotBlank(soggetto.getNote())) {
			referente.setNote(soggetto.getNote());
		}
		referente.setDataInizioIncarico(CalendarValidator.getInstance()
						.validate(soggetto.getDataInizioIncarico(),
										"dd/MM/yyyy"));
		if (StringUtils.isNotBlank(soggetto.getDataFineIncarico())) {
			referente.setDataFineIncarico(CalendarValidator
							.getInstance().validate(
											soggetto.getDataFineIncarico(),
											"dd/MM/yyyy"));
		}
		if (StringUtils.isNotBlank(soggetto.getResponsabileDichiarazioni())) {
			referente.setResponsabileDichiarazioni(soggetto
					.getResponsabileDichiarazioni());
		}
		referente.setEsistente(soggetto.isEsistente());
		referente.setSolaLettura(soggetto.isSolaLettura());
	}
	
	/**
	 * Valida il contenuto del contenitore sia dal punto di vista sintattico (dati
	 * obbligatori) sia dal punto di vista semantico per quanto riguarda i campi
	 * il cui valore &egrave; condizionato da altri campi e per quanto riguarda i
	 * dati configurabili.
	 *
	 * @return true se i dati sono completi e corretti, false altrimenti
	 */
	public boolean validate() {
		boolean esito = true;

		
		ICustomConfigManager customConfigManager = (ICustomConfigManager) ApsWebApplicationUtils
						.getBean(CommonSystemConstants.CUSTOM_CONFIG_MANAGER,
								 ServletActionContext.getRequest());

		boolean hasDatiUlteriori = true;
		boolean hasCcia = true;
		boolean hasPrevidenza = true;
		boolean hasSezAltriDati = true;

		
		try {
	    	if(!customConfigManager.isVisible("IMPRESA-DATIULT", "STEP")){
	    		hasDatiUlteriori = false;
   			}
	    	
	    	if(!customConfigManager.isVisible("IMPRESA-DATIULT", "CCIAA")){
	    		hasCcia = false;
   			}
	    	
	    	if(!customConfigManager.isVisible("IMPRESA-DATIULT", "PREVIDENZA")){
	    		hasPrevidenza = false;
	    	}
	    	
	    	if(!customConfigManager.isVisible("IMPRESA-DATIULT", "ALTRIDATI")){
	    		hasSezAltriDati = false;
   			}
	    	
   		}catch (Exception e) {
   			// Configurazione sbagliata
   			ApsSystemUtils.logThrowable(e, this, "next",
   					"Errore durante la ricerca delle proprietà di visualizzazione dello step dati ulteriori");
   		}
		
		// check sintattico
		if (esito) {
			XmlOptions validationOptions = new XmlOptions();
			ArrayList<XmlValidationError> validationErrors = new ArrayList<XmlValidationError>();
			validationOptions.setErrorListener(validationErrors);
			esito = this.getXmlDocumentAggiornamentoAnagrafica().validate(validationOptions);
//			if(!esito) {				
//				ApsSystemUtils.logThrowable(new Exception(validationErrors.toString()), this, "validate");
//			} 
		}
		if (esito) {
			// check provincia sede legale
			esito = checkProvincia(
							this.datiPrincipaliImpresa.getProvinciaSedeLegale(),
							this.datiPrincipaliImpresa.getNazioneSedeLegale());
		}
		if (esito) {
			// check della provincia di ogni indirizzo inserito
			for (int i = 0; i < this.indirizziImpresa.size() && esito; i++) {
				esito = checkProvincia(this.indirizziImpresa.get(i).getProvincia(), 
						               this.indirizziImpresa.get(i).getNazione());
			}
		}
		if (esito) {
			// check della provincia di ogni legale rappresentante inserito
			for (int i = 0; i < this.legaliRappresentantiImpresa.size() && esito; i++) {
				esito = checkProvincia(this.legaliRappresentantiImpresa.get(i).getProvincia(), 
									   this.legaliRappresentantiImpresa.get(i).getNazione());
				// && StringUtils.isBlank(this.legaliRappresentantiImpresa
				// .get(i).getQuotaAzionista())
				// && StringUtils.isBlank(this.legaliRappresentantiImpresa
				// .get(i).getTipoIncaricoAzionista());
			}
		}
		if (esito) {
			// check della provincia di ogni direttore tecnico inserito
			for (int i = 0; i < this.direttoriTecniciImpresa.size() && esito; i++) {
				esito = checkProvincia(this.direttoriTecniciImpresa.get(i).getProvincia(), 
									   this.direttoriTecniciImpresa.get(i).getNazione());
				// && StringUtils.isBlank(this.direttoriTecniciImpresa
				// .get(i).getQuotaAzionista())
				// && StringUtils.isBlank(this.direttoriTecniciImpresa
				// .get(i).getTipoIncaricoAzionista());
			}
		}
		if (esito) {
			// check della provincia di ogni altra carica inserita
			for (int i = 0; i < this.altreCaricheImpresa.size() && esito; i++) {
				esito = checkProvincia(this.altreCaricheImpresa.get(i).getProvincia(), 
									   this.altreCaricheImpresa.get(i).getNazione());
			}
		}
		try {

			if (esito) {
				// check della provincia di ogni collaboratore inserito
				for (int i = 0; i < this.collaboratoriImpresa.size() && esito; i++) {
					esito = checkProvincia(this.collaboratoriImpresa.get(i).getProvincia(), 
										   this.collaboratoriImpresa.get(i).getNazione());
					// check dei dati iscrizione albo professionale per i
					// professionisti di un'associazione di professionisti o di
					// uno studio associato nel caso
					if (esito 
							&& "7".equals(this.datiPrincipaliImpresa.getNaturaGiuridica())
							&& "2".equals(this.collaboratoriImpresa.get(i).getQualifica())
							&& customConfigManager.isMandatory("IMPRESA-DATIANAGR-SEZ",
															   "ISCRIZIONEALBOPROF")) {
						esito = StringUtils.isNotBlank(this.collaboratoriImpresa.get(i).getTipologiaAlboProf())
								&& StringUtils.isNotBlank(this.collaboratoriImpresa.get(i).getNumIscrizioneAlboProf())
								&& StringUtils.isNotBlank(this.collaboratoriImpresa.get(i).getDataIscrizioneAlboProf())
								&& StringUtils.isNotBlank(this.collaboratoriImpresa.get(i).getProvinciaIscrizioneAlboProf());
					}
				}
			}
			if (esito) {
				if (esito && customConfigManager.isMandatory(
												"IMPRESA-DATIPRINC-RECAPITI", "FAX")) {
					esito = StringUtils.isNotBlank(this.datiPrincipaliImpresa
									.getFaxRecapito());
				}
				if (esito && customConfigManager.isMandatory(
												"IMPRESA-DATIPRINC-RECAPITI", "CELL")) {
					esito = StringUtils.isNotBlank(this.datiPrincipaliImpresa
									.getCellulareRecapito());
				}
				if (esito && customConfigManager.isMandatory(
												"IMPRESA-DATIPRINC-RECAPITI", "MAIL")) {
					esito = StringUtils.isNotBlank(this.datiPrincipaliImpresa
									.getEmailRecapito());
				}
				if (esito && customConfigManager.isMandatory(
												"IMPRESA-DATIPRINC-RECAPITI", "PEC")) {
					boolean pecObbl = true;
					if( !"ITALIA".equalsIgnoreCase(this.datiPrincipaliImpresa.getNazioneSedeLegale()) ) {
						// nazione estero
						pecObbl = (customConfigManager.isMandatory("IMPRESA-DATIPRINC-RECAPITI", "PECESTERO"));
					}
					if(pecObbl) {
						esito = StringUtils.isNotBlank(this.datiPrincipaliImpresa
									.getEmailPECRecapito());
					}
				}
				if(hasDatiUlteriori){
					if (esito && hasCcia) {
						// check iscritto alla camera di commercio
						esito = StringUtils.isNotBlank(this.datiUlterioriImpresa.getIscrittoCCIAA());
					}
					if (esito && hasCcia && ("1".equalsIgnoreCase(this.datiUlterioriImpresa.getIscrittoCCIAA()))
							  && customConfigManager.isMandatory(
													"IMPRESA-DATIULT-CCIAA", "DATANULLAOSTAANTIMAFIA")) {
						esito = StringUtils.isNotBlank(this.datiUlterioriImpresa
										.getDataNullaOstaAntimafiaCCIAA());
					}
					if (esito && hasSezAltriDati && customConfigManager.isVisible(
													"IMPRESA-DATIULT-SEZ", "ABILITAZIONEPREVENTIVA")) {
						boolean rinnovo = "1".equals(this.datiUlterioriImpresa
										.getRinnovoAbilitPreventiva());
						boolean dataRichRinnovoNonValorizzata = StringUtils
										.isBlank(this.datiUlterioriImpresa
														.getDataRichRinnovoAbilitPreventiva());
						esito = rinnovo ^ dataRichRinnovoNonValorizzata; // XOR logico
					}
					if (esito && hasSezAltriDati && customConfigManager.isMandatory(
													"IMPRESA-DATIULT-ABILITPREVENTIVA", "DATASCADENZA")) {
						esito = StringUtils.isNotBlank(this.datiUlterioriImpresa
										.getDataScadenzaAbilitPreventiva());
					}			
					if (esito && hasSezAltriDati && customConfigManager.isMandatory(
													"IMPRESA-DATIULT", "ZONEATTIVITA")) {
						String zoneAttivita = WizardDatiUlterioriImpresaHelper
										.getZoneAttivitaForBO(this.datiUlterioriImpresa
														.getZoneAttivita());
						// ci deve essere la gestione delle zone attivita', ed
						// almeno una regione deve essere considerata come regione
						// di attivita' (quindi ci deve essere almeno un 1
						// all'interno della stringa)
						esito = zoneAttivita != null
										&& zoneAttivita.indexOf('1') != -1;
					}
			
					if (esito && this.liberoProfessionista
							  && customConfigManager.isMandatory(
													"IMPRESA-DATIANAGR-SEZ", "ISCRIZIONEALBOPROF")) {
						esito = StringUtils
										.isNotBlank(this.altriDatiAnagraficiImpresa
														.getTipologiaAlboProf())
										&& StringUtils
										.isNotBlank(this.altriDatiAnagraficiImpresa
														.getNumIscrizioneAlboProf())
										&& StringUtils
										.isNotBlank(this.altriDatiAnagraficiImpresa
														.getDataIscrizioneAlboProf())
										&& StringUtils
										.isNotBlank(this.altriDatiAnagraficiImpresa
														.getProvinciaIscrizioneAlboProf());
					}
					if (esito && hasCcia && !this.liberoProfessionista
							  && customConfigManager.isMandatory(
													"IMPRESA-DATIULT-CCIAA", "OGGETTOSOCIALE") && "1".equals(this.datiUlterioriImpresa.getIscrittoCCIAA())) {
						esito = StringUtils
										.isNotBlank(this.datiUlterioriImpresa.getOggettoSociale());
					}				
					if (esito && this.liberoProfessionista) {
						esito = StringUtils
								.isNotBlank(this.altriDatiAnagraficiImpresa.getNome()) && StringUtils
								.isNotBlank(this.altriDatiAnagraficiImpresa.getCognome());
					}				
					if (esito && hasPrevidenza && customConfigManager.isMandatory(
													"IMPRESA-DATIULT", "SOGGOBBLIGODURC")) {
						esito = StringUtils
										.isNotBlank(this.datiUlterioriImpresa.getSoggettoNormativeDURC());
					}
					if (esito && hasSezAltriDati && customConfigManager.isMandatory(
													"IMPRESA-DATIULT", "SOGGASSUNZIONIOBBLIGATORIE")) {
						esito = StringUtils
										.isNotBlank(this.datiUlterioriImpresa.getAssunzioniObbligate());
					}
					if (esito && hasSezAltriDati && customConfigManager.isMandatory(
													"IMPRESA-DATIULT", "DIPENDENTITRIENNIO")) {
						esito = this.datiUlterioriImpresa.getNumDipendenti()[0] != null
								&& this.datiUlterioriImpresa.getNumDipendenti()[1] != null
								&& this.datiUlterioriImpresa.getNumDipendenti()[2] != null;
					}
					if (esito && !this.isLiberoProfessionista()) {
						esito = this.contaResponsabiliDichiarazioni() > 0;
					}				
					if (esito && hasSezAltriDati && customConfigManager.isMandatory(
													"IMPRESA-DATIULT", "SETTOREATTIVITA")) {
						esito = StringUtils
							.isNotBlank(this.datiUlterioriImpresa.getSettoreAttivitaEconomica());
					}
					if (esito && hasSezAltriDati && customConfigManager.isMandatory(
													"IMPRESA-DATIULT", "CLASSEDIMENSIONE")) {
						esito = StringUtils
								.isNotBlank(this.datiUlterioriImpresa.getClasseDimensioneDipendenti());
					}
	
					// Iscrizione elenchi ricostruzione (DL 189/2016)
					if (esito && customConfigManager.isMandatory(
													"IMPRESA-DATIULT", "ISCRELENCHIDL189-2016")) {
						esito = StringUtils
							.isNotBlank(this.datiUlterioriImpresa.getIscrittoAnagrafeAntimafiaEsecutori());
		
						if(esito && "1".equals(this.datiUlterioriImpresa.getIscrittoAnagrafeAntimafiaEsecutori())) {
							esito = esito && StringUtils
								.isNotBlank(this.datiUlterioriImpresa.getDataScadenzaIscrizioneAnagrafeAntimafiaEsecutori()) 
							&& StringUtils
								.isNotBlank(this.datiUlterioriImpresa.getRinnovoIscrizioneInCorsoAnagrafeAntimafiaEsecutori());
						}
	
						esito = esito && StringUtils
								.isNotBlank(this.datiUlterioriImpresa.getIscrittoElencoSpecialeProfessionisti());
					}
					
					// Rating di legalità (DL 1/2012)
					if (esito && customConfigManager.isMandatory(
													"IMPRESA-DATIULT", "RATINGLEGALITA")) {
						esito = StringUtils
								.isNotBlank(this.datiUlterioriImpresa.getPossiedeRatingLegalita());
						
						if(esito && "1".equals(this.datiUlterioriImpresa.getPossiedeRatingLegalita())) {
							esito = esito && StringUtils
								.isNotBlank(this.datiUlterioriImpresa.getRatingLegalita())
							&& StringUtils
								.isNotBlank(this.datiUlterioriImpresa.getDataScadenzaPossessoRatingLegalita())
							&& StringUtils
								.isNotBlank(this.datiUlterioriImpresa.getAggiornamentoRatingLegalita());
						}
					}
					
					// whitelist antimafia
					if(esito) {
						String sezioniIscrizione = WizardDatiUlterioriImpresaHelper
							.getSezioniIscrizioneForBO(this.datiUlterioriImpresa
										.getSezioniIscrizioneWhitelistAntimafia());					
						//esito = (StringUtils.isNotEmpty(sezioniIscrizione);
						esito = true;
					}
				}
			}
		} catch (Exception e) {
			// il fatto che si faccia il check su una configurazione scritta
			// male deve provocare il fallimento della validazione in modo
			// da accorgermene
			esito = false;
		}
		return esito;
	}

	/**
	 * Verifica che la provincia sia valorizzata esclusivamente per l'Italia
	 *
	 * @param provincia provincia
	 * @param nazione nazione
	 * @return true se la provincia viene valorizzata per la nazione italiana e
	 * non viene valorizzata per tutte le altre nazioni, false altrimenti
	 */
	private boolean checkProvincia(String provincia, String nazione) {
		boolean provinciaValorizzata = StringUtils.isNotBlank(provincia);
		boolean nazioneNoItalia = !"ITALIA".equalsIgnoreCase(StringUtils
						.stripToNull(nazione));
		return provinciaValorizzata ^ nazioneNoItalia; // XOR logico
	}

	/**
	 * Conta tra i responsabili attivi di una lista sono quelli autorizzati alla
	 * firma delle dichiarazioni.
	 *
	 * @return numero di soggetti attivi responsabili di firma
	 */
	public int contaResponsabiliDichiarazioni() {
		int conteggio = 0;
		for (ISoggettoImpresa soggetto : this.legaliRappresentantiImpresa) {
			if (StringUtils.stripToNull(soggetto.getDataFineIncarico()) == null
							&& "1".equals(soggetto.getResponsabileDichiarazioni())) {
				conteggio++;
			}
		}
		for (ISoggettoImpresa soggetto : this.direttoriTecniciImpresa) {
			if (StringUtils.stripToNull(soggetto.getDataFineIncarico()) == null
							&& "1".equals(soggetto.getResponsabileDichiarazioni())) {
				conteggio++;
			}
		}
		for (ISoggettoImpresa soggetto : this.collaboratoriImpresa) {
			if (StringUtils.stripToNull(soggetto.getDataFineIncarico()) == null
							&& "1".equals(soggetto.getResponsabileDichiarazioni())) {
				conteggio++;
			}
		}
		for (ISoggettoImpresa soggetto : this.altreCaricheImpresa) {
			if (StringUtils.stripToNull(soggetto.getDataFineIncarico()) == null
							&& "1".equals(soggetto.getResponsabileDichiarazioni())) {
				conteggio++;
			}
		}
		return conteggio;
	}

	public static void fillFirmatarioFields(FirmatarioType referente,
			ISoggettoImpresa soggetto) {
		if (StringUtils.isNotBlank(soggetto.getQualifica())) {
			referente.setQualifica(soggetto.getQualifica());
		}
		referente.setCognome(soggetto.getCognome());
		referente.setNome(soggetto.getNome());
		referente.setCodiceFiscaleFirmatario(soggetto.getCodiceFiscale());
		referente.setSesso(soggetto.getSesso());
		IndirizzoType residenza = referente.addNewResidenza();
		residenza.setIndirizzo(soggetto.getIndirizzo());
		residenza.setNumCivico(soggetto.getNumCivico());
		if (StringUtils.isNotBlank(soggetto.getCap())) {
			residenza.setCap(soggetto.getCap());
		}
		residenza.setComune(soggetto.getComune());
		if (StringUtils.isNotBlank(soggetto.getProvincia())) {
			residenza.setProvincia(soggetto.getProvincia());
		}
		residenza.setNazione(soggetto.getNazione());
		referente.setDataNascita(CalendarValidator.getInstance()
						.validate(soggetto.getDataNascita(), "dd/MM/yyyy"));
		referente.setComuneNascita(soggetto.getComuneNascita());
		referente.setProvinciaNascita(soggetto.getProvinciaNascita());
	}

	
	/**
	 * Costruisce l'elenco dei firmatari associati all'operatore (impresa/consorzio/privato)
	 *  
	 *  @param maps mappa delle decodifiche presenti in InterceptorEncodedData 
	 *  @param qualificaFirmatario (opzionale) se viene passata, restituisce una 
	 *  	   tabella con le decodifiche dei tipi di firmatario (LISTA_TIPI_SOGGETTO)
	 *  
	 *  @return List<FirmatarioBean>
	 */	
	public List<FirmatarioBean> getElencoFirmatari() {		
		List<FirmatarioBean> listaFirmatari = null;		
		try {
			listaFirmatari = new ArrayList<FirmatarioBean>();
			
			if (this.isLiberoProfessionista()) {
			    // la mandataria è un libero professionista allora 
				// 1 solo possibile firmatario
			    FirmatarioBean firmatario = new FirmatarioBean();
			    if(this.getAltriDatiAnagraficiImpresa().getCognome() != null && this.getAltriDatiAnagraficiImpresa().getNome() != null){
			    	firmatario.setNominativo(this.getAltriDatiAnagraficiImpresa().getCognome() + " " + this.getAltriDatiAnagraficiImpresa().getNome());
			    } else {
			    	firmatario.setNominativo(this.getDatiPrincipaliImpresa().getRagioneSociale());
			    }
			    listaFirmatari.add(firmatario);
			    
			} else {
			    // la mandantaria è un'impresa/consorzio allora 
				// 1..N possibili firmatari
			    for (int i = 0; i < this.getLegaliRappresentantiImpresa().size(); i++) {
					addSoggettoMandataria(
						listaFirmatari,
						this.getLegaliRappresentantiImpresa().get(i), 
						i, 
						CataloghiConstants.LISTA_LEGALI_RAPPRESENTANTI);
			    }
			    
			    for (int i = 0; i < this.getDirettoriTecniciImpresa().size(); i++) {
					addSoggettoMandataria(
						listaFirmatari,
						this.getDirettoriTecniciImpresa().get(i), 
						i,
						CataloghiConstants.LISTA_DIRETTORI_TECNICI);
			    }
			    
			    for (int i = 0; i < this.getAltreCaricheImpresa().size(); i++) {
					addSoggettoMandataria(
						listaFirmatari,
						this.getAltreCaricheImpresa().get(i), 
						i,
						CataloghiConstants.LISTA_ALTRE_CARICHE);
			    }
			}		
		} catch(Exception e) {		
			listaFirmatari = null; 
		}		
		return listaFirmatari;		
	}

	/**
	 * Aggiunge un soggetto dell'operatore(impresa/consorzio/privato) alla 
	 * lista dei firmatari 
	 */
	private void addSoggettoMandataria(
    		List<FirmatarioBean> listaFirmatari,
    		ISoggettoImpresa soggetto,
    	    int index, 
    	    String lista) {
    	if (soggetto.getDataFineIncarico() == null
    		&& "1".equals(soggetto.getResponsabileDichiarazioni())) {
    	    FirmatarioBean firmatario = new FirmatarioBean();
    	    String cognome = StringUtils.capitalize(
    	    		soggetto.getCognome().substring(0, 1)) 
    	    		+ soggetto.getCognome().substring(1);
    	    String nome = StringUtils.capitalize(
    	    		soggetto.getNome().substring(0, 1))
    	    		+ soggetto.getNome().substring(1);
    	    firmatario.setNominativo(
    	    	new StringBuilder().append(cognome).append(" ").append(nome).toString());
    	    firmatario.setIndex(index);
    	    firmatario.setLista(lista);
    	    listaFirmatari.add(firmatario);
    	}
    }

	/**
	 * Costruisce la tabella di decodifica per i tipi di firmatario ('tipiSoggetto', 'tipiAltraCarica')
	 *  
	 *  @param maps mappa delle decodifiche presenti in InterceptorEncodedData
	 *  
	 *  @return List<String> 
	 */
	public List<String> getTipoQualificaCodifica(
			List<FirmatarioBean> firmatari,
			Map<String, LinkedHashMap<String, String>> maps) {
		List<String> qualificaFirmatario = null;
		try {
			if(!this.isLiberoProfessionista() 
			   && maps.get(InterceptorEncodedData.LISTA_TIPI_SOGGETTO) != null 
			   && maps.get(InterceptorEncodedData.LISTA_TIPI_ALTRA_CARICA) != null) {			
				qualificaFirmatario = new ArrayList<String>();
				for(int i = 0; i < firmatari.size(); i++) {
					FirmatarioBean firmatarioCorrente = firmatari.get(i);

					if(CataloghiConstants.LISTA_LEGALI_RAPPRESENTANTI.equals(firmatarioCorrente.getLista())) {
						qualificaFirmatario.add(
							maps.get(InterceptorEncodedData.LISTA_TIPI_SOGGETTO)
								.get(CommonSystemConstants.TIPO_SOGGETTO_LEGALE_RAPPRESENTANTE
								     + CommonSystemConstants.SEPARATORE_TABELLATI_CONCATENATI));
					} else if (CataloghiConstants.LISTA_DIRETTORI_TECNICI.equals(firmatarioCorrente.getLista())) {
						qualificaFirmatario.add(
							maps.get(InterceptorEncodedData.LISTA_TIPI_SOGGETTO)
								.get(CommonSystemConstants.TIPO_SOGGETTO_DIRETTORE_TECNICO
									 + CommonSystemConstants.SEPARATORE_TABELLATI_CONCATENATI));
					} else {
						String codiceQualifica = this.getAltreCaricheImpresa()
								.get(firmatarioCorrente.getIndex()).getSoggettoQualifica();
						qualificaFirmatario.add(
							maps.get(InterceptorEncodedData.LISTA_TIPI_ALTRA_CARICA)
								.get(codiceQualifica));
					}
				}
			}	
		} catch(Exception e) {
			qualificaFirmatario = null;
		}
		return qualificaFirmatario;	
	}	
	
	
	////////////////////////////////////////////////////////////////////////////
	// Gestione delle liste di soggetti impresa (ISoggettiImpresa)
	////////////////////////////////////////////////////////////////////////////
	private static final SimpleDateFormat DDMMYYYY = new SimpleDateFormat("dd/MM/yyyy");

	/**
	 * Copia i dati da un soggetto impresa sorgente ad un soggetto impresa 
	 * destinazione 
	 */	
	public static void synchronizeSoggettoImpresa(ISoggettoImpresa from, ISoggettoImpresa to) {
		to.setEsistente(from.isEsistente());
		to.setSolaLettura(true);
		if(!to.isEsistente()){
			// Nuovo soggetto aggiunto
			to.setSolaLettura(false);
		}else{
			if(from.getDataFineIncarico() != to.getDataFineIncarico()) {
				// Ho inserito o modificato la data dine per un soggetto già presente
				to.setSolaLettura(false);
			}
		}
		to.setSolaLettura(from.isSolaLettura());
		to.setSoggettoQualifica(from.getSoggettoQualifica());
		to.setTipoSoggetto(from.getTipoSoggetto());
		to.setDataInizioIncarico(StringUtils.stripToNull(from.getDataInizioIncarico()));
		to.setDataFineIncarico(StringUtils.stripToNull(from.getDataFineIncarico()));
		to.setQualifica(StringUtils.stripToNull(from.getQualifica()));
		to.setResponsabileDichiarazioni(from.getResponsabileDichiarazioni());
		to.setCognome(from.getCognome());
		to.setNome(from.getNome());
		to.setTitolo(StringUtils.stripToNull(from.getTitolo()));
		to.setCodiceFiscale(from.getCodiceFiscale());
		to.setSesso(from.getSesso());
		to.setIndirizzo(from.getIndirizzo());
		to.setNumCivico(from.getNumCivico());
		to.setCap(from.getCap());
		to.setComune(from.getComune());
		to.setProvincia(StringUtils.stripToNull(from.getProvincia()));
		to.setNazione(from.getNazione());
		to.setDataNascita(from.getDataNascita());
		to.setComuneNascita(from.getComuneNascita());
		to.setProvinciaNascita(from.getProvinciaNascita());
		to.setTipologiaAlboProf(from.getTipologiaAlboProf());
		to.setNumIscrizioneAlboProf(from.getNumIscrizioneAlboProf());
		to.setDataIscrizioneAlboProf(from.getDataIscrizioneAlboProf());
		to.setProvinciaIscrizioneAlboProf(from.getProvinciaIscrizioneAlboProf());
		to.setTipologiaCassaPrevidenza(from.getTipologiaCassaPrevidenza());
		to.setNumMatricolaCassaPrevidenza(from.getNumMatricolaCassaPrevidenza());
		to.setNote(from.getNote());
	}

	/**
	 * Resetta i dati di un soggetto impresa  
	 */
	public static void resetSoggettoImpresa(ISoggettoImpresa soggetto) {
		soggetto.setEsistente(false);
		soggetto.setSolaLettura(false);
		soggetto.setSoggettoQualifica(null);
		soggetto.setSoggettoQualifica(null);
		soggetto.setTipoSoggetto(null);
		soggetto.setDataInizioIncarico(null);
		soggetto.setDataFineIncarico(null);
		soggetto.setQualifica(null);
		soggetto.setCognome(null);
		soggetto.setNome(null);
		soggetto.setResponsabileDichiarazioni(null);
		soggetto.setTitolo(null);
		soggetto.setCodiceFiscale(null);
		soggetto.setSesso(null);
		soggetto.setIndirizzo(null);
		soggetto.setNumCivico(null);
		soggetto.setCap(null);
		soggetto.setComune(null);
		soggetto.setProvincia(null);
		soggetto.setNazione("Italia");
		soggetto.setDataNascita(null);
		soggetto.setComuneNascita(null);
		soggetto.setProvinciaNascita(null);
		soggetto.setTipologiaAlboProf(null);
		soggetto.setNumIscrizioneAlboProf(null);
		soggetto.setDataIscrizioneAlboProf(null);
		soggetto.setProvinciaIscrizioneAlboProf(null);
		soggetto.setTipologiaCassaPrevidenza(null);
		soggetto.setNumMatricolaCassaPrevidenza(null);
		soggetto.setNote(null);
	}

	/**
	 * Restituisce la lista dei soggetti impresa in base al tipo soggetti 
	 * richiesto
	 */
	public List<ISoggettoImpresa> getSoggettiImpresa(String tipoSoggetto) {
		List<ISoggettoImpresa> soggetti = null;		
		if (CommonSystemConstants.TIPO_SOGGETTO_LEGALE_RAPPRESENTANTE.equals(tipoSoggetto)) {
			soggetti = this.getLegaliRappresentantiImpresa();
		} else if (CommonSystemConstants.TIPO_SOGGETTO_DIRETTORE_TECNICO.equals(tipoSoggetto)) {
			soggetti = this.getDirettoriTecniciImpresa();
		} else if (CommonSystemConstants.TIPO_SOGGETTO_ALTRA_CARICA.equals(tipoSoggetto)) {
			soggetti = this.getAltreCaricheImpresa();
		} else if (CommonSystemConstants.TIPO_SOGGETTO_COLLABORATORE.equals(tipoSoggetto)) {
			soggetti = this.getCollaboratoriImpresa();
		} else {
			// non gestito
		}				
		return soggetti;
	}

	private ReferenteImpresaAggiornabileType[] getReferentiImpresa(
			DatiImpresaDocument datiImpresa, 
			String tipoSoggetto) {
		ReferenteImpresaAggiornabileType[] referenti = null;
		if(CommonSystemConstants.TIPO_SOGGETTO_LEGALE_RAPPRESENTANTE.equalsIgnoreCase(tipoSoggetto)) {
			referenti = datiImpresa.getDatiImpresa().getLegaleRappresentanteArray();
		} else if(CommonSystemConstants.TIPO_SOGGETTO_DIRETTORE_TECNICO.equalsIgnoreCase(tipoSoggetto)) {
			referenti = datiImpresa.getDatiImpresa().getDirettoreTecnicoArray();
		} else if(CommonSystemConstants.TIPO_SOGGETTO_ALTRA_CARICA.equalsIgnoreCase(tipoSoggetto)) {
			referenti = datiImpresa.getDatiImpresa().getAltraCaricaArray();
		} else if(CommonSystemConstants.TIPO_SOGGETTO_COLLABORATORE.equalsIgnoreCase(tipoSoggetto)) {
			referenti = datiImpresa.getDatiImpresa().getCollaboratoreArray();		
		} else {
			// non gestito
		}		
		return referenti;
	}

	/**
	 * Rimuove un soggetto impresa dalla lista di appartenenza in base al tipo 
	 * di lista. 
	 */
	public void removeSoggettoImpresa(String tipoSoggetto, int idSoggetto) {
		List<ISoggettoImpresa> soggetti = this.getSoggettiImpresa(tipoSoggetto);
		if(soggetti != null) {
			soggetti.remove(idSoggetto);
		}
	}
	
	/**
	 * Controlla se esiste un altro soggetto identico per la medesima lista di
	 * soggetti. La verifica avviene per codice fiscale.
	 *
	 * @param soggetti lista di soggetti da controllare
	 * @param codiceFiscaleSoggetto codice fiscale del soggetto da
	 * 			inserire/aggiornare per cui va garantita l'univocit&agrave;
	 * @param soggettoQualifica qualifica del soggetto da inserire/aggiornare
	 * @param dataInizio data inizio incarico
	 * @param dataFine data fine incarico
	 * @param indiceSoggetto indice del soggetto se presente nella lista (quindi
	 * 		   	in caso di aggiornamento), -1 altrimenti (quindi in caso di inserimento)
	 * 
	 * @return true se il soggetto esiste gi&agrave e quindi e' duplicato,
	 * 		   false altrimenti
	 */	
	public boolean isSoggettoDuplicato(			
			String codiceFiscaleSoggetto, 
			String soggettoQualifica, 
			String dataInizio, 
			String dataFine,
			List<ISoggettoImpresa> soggetti,
			int indiceSoggetto,
			String username) 
	{		
		return (isSoggettoDuplicato(
					soggetti,
					indiceSoggetto,
					codiceFiscaleSoggetto,
					soggettoQualifica,
					dataInizio,
					dataFine,
					username) != null);
	}
	
	/**
	 * Vedi isSoggettoDuplicato(...)
	 *  
	 * @return i dati del soggetto duplicato se il soggetto esiste gi&agrave;
	 * 		   null altrimenti
	 * 		   Il tipo di dato restituito può essere ISoggettoImpresa se il 
	 * 		   duplicato è presente nell'elenco dell'impresa,  
	 * 		   ReferenteImpresaAggiornabileType se il duplicato è presente in
	 * 		   B.O.	 	 
	 */
	public Object isSoggettoDuplicato(
			List<ISoggettoImpresa> soggetti,
			int indiceSoggetto,
			String codiceFiscaleSoggetto, 
			String soggettoQualifica, 
			String dataInizio, 
			String dataFine,			
			String username) 
	{
		boolean esito = false;
		
		Object soggettoDuplicato = null; 
		String tipoSoggetto = null;		
		Date inizio = null;
		try {
           	inizio = DDMMYYYY.parse(dataInizio);
        } catch (Exception e) {
		}		
		Date fine = null;
		try {
           	fine = DDMMYYYY.parse(dataFine);
		} catch (Exception e) {
		}				
		
		// lista dei soggetti da non verificare in BO...
		List<String> soggettiVerificati = new ArrayList<String>();
		
		//for (int i = 0; i < soggetti.size() && !esito; i++) {
		for (int i = 0; i < soggetti.size(); i++) {
			ISoggettoImpresa soggetto = (ISoggettoImpresa) soggetti.get(i);
			tipoSoggetto = soggetto.getTipoSoggetto();
			
			if (codiceFiscaleSoggetto.equals(soggetto.getCodiceFiscale())
				&& soggettoQualifica.equals(soggetto.getSoggettoQualifica())
				&& i != indiceSoggetto) {
				// se anche se i periodi di validita' si sovrappongono allora 
				// non e' possibile aggiungere lo stesso soggetto				
				Date d1 = null;				
				try {
		           	d1 = DDMMYYYY.parse(soggetto.getDataInizioIncarico());
				} catch (Exception e) {
					d1 = null;
				}
				Date d2 = null;
				try {
		           	d2 = DDMMYYYY.parse(soggetto.getDataFineIncarico());
				} catch (Exception e) {
					d2 = null;
				}
				
				if( this.isPeriodiSovrapposti(inizio, fine, d1, d2) ) {				
					esito = true;
					soggettoDuplicato = soggetto;					
				}
				
				// aggiorna la lista dei soggetti da non verificare 
				// successivamente in BO...
				String key = 
					(soggetto.getCodiceFiscale() != null ? soggetto.getCodiceFiscale() : "") + "|" +  							
					tipoSoggetto + "-" + (soggetto.getQualifica() != null ? soggetto.getQualifica() : "") + "|" +
					(d1 != null ? DDMMYYYY.format(d1) : "");					
				soggettiVerificati.add(key);
			}
		}
		
		// In caso di "esito" negativo, verifica anche in BO se ci sono
		// sovrapposizioni con eventuali cessati che non sono visibili
		// nella lista corrente
		// NB: in caso di registrazione di nuova impresa salta questa verifica
		boolean registrazioneNuovaImpresa = (StringUtils.isEmpty(username) ||
				 							 SystemConstants.GUEST_USER_NAME.equalsIgnoreCase(username));

		if( !esito && !registrazioneNuovaImpresa ) {
			// in caso di inserimento di nuovo soggetto passa i dati in ingresso
			// mentre in caso di aggiornamento passa i dati del soggetto selezionato
			if(indiceSoggetto >= 0) {		
				codiceFiscaleSoggetto = soggetti.get(indiceSoggetto).getCodiceFiscale();
				soggettoQualifica = soggetti.get(indiceSoggetto).getQualifica(); 
				dataInizio = soggetti.get(indiceSoggetto).getDataInizioIncarico();
				dataFine = soggetti.get(indiceSoggetto).getDataFineIncarico();
			}
			soggettoQualifica = (soggettoQualifica != null ? soggettoQualifica : tipoSoggetto + "-");
			
			try {
				IBandiManager bandiManager = (IBandiManager) ApsWebApplicationUtils 
					.getBean(PortGareSystemConstants.BANDI_MANAGER,
							ServletActionContext.getRequest());

				// recupera l'elenco di tutti soggetti dell'impresa (legali 
				// rappresentanti, direttori tecnici, altre cariche, collaboratori) 
				// utilizzando una data cessazione al 31/12/1800, quindi recupera 
				// tutti i soggetti presenti in BO
				DatiImpresaDocument datiImpresa = bandiManager.getDatiImpresa(
						username, 
						new GregorianCalendar(1800, GregorianCalendar.DECEMBER, 31).getTime());

				String keySoggetto = 
					codiceFiscaleSoggetto + "|" +
				 	soggettoQualifica + "|" +
				 	(dataInizio != null ? dataInizio : "");

				ReferenteImpresaAggiornabileType[] referenti = 
					this.getReferentiImpresa(datiImpresa, tipoSoggetto);
				
				if(referenti != null) {
					for (int i = 0; i < referenti.length && !esito; i++) {
						String qualifica = tipoSoggetto + "-" + 
										   (referenti[i].getQualifica() != null ? referenti[i].getQualifica() : "");												
						String key = 
							(referenti[i].getCodiceFiscale() != null ? referenti[i].getCodiceFiscale() : "") + "|" +  							
							qualifica + "|" +
							(referenti[i].getDataInizioIncarico() != null ? DDMMYYYY.format(referenti[i].getDataInizioIncarico().getTime()) : "");
						
						if (codiceFiscaleSoggetto.equals(referenti[i].getCodiceFiscale())
							&& soggettoQualifica.equals(qualifica)
							&& !key.equalsIgnoreCase(keySoggetto) 
							&& !soggettiVerificati.contains(key)) {							
							// se anche se i periodi di validita' si sovrappongono allora 
							// non e' possibile aggiungere lo stesso soggetto
							Date d1 = null;							
							try {
					           	d1 = referenti[i].getDataInizioIncarico().getTime();
							} catch (Exception e) {
								d1 = null;
							}
							Date d2 = null;
							try {
					           	d2 = referenti[i].getDataFineIncarico().getTime();
							} catch (Exception e) {
								d2 = null;
							}							
							esito = this.isPeriodiSovrapposti(inizio, fine, d1, d2);							
							if(esito) {
								soggettoDuplicato = referenti[i];
							}
						}
					}				
				}
			} catch (Exception ex) {
				ApsSystemUtils.logThrowable(ex, this, "isSoggettoDuplicato");
			}
		}
		
		return soggettoDuplicato;		
	}

	private boolean isPeriodiSovrapposti(Date inizio1, Date fine1, Date inizio2, Date fine2) {
		// Caso solo "inizio1"                                             Sovrapposto?
		// 		                    inizio1............................>
		//-----------------------------------------------------------------------------
		// inizio2.....................................................>   TRUE
		//                                       inizio2...............>   TRUE
		//			                             inizio2...........fine2   TRUE		
		//                 inizio2...........fine2						   TRUE	 		
		// <.......................................................fine2   TRUE  
		// <................fine2                                          FALSE !!!
		// inizio2..........fine2                                          FALSE !!!
		//
		// Caso solo "fine1"
		// <.........................fine1
		//-----------------------------------------------------------------------------
		// inizio2.....................................................>   TRUE
		//                                       inizio2...............>   FALSE !!!
		//			                             inizio2...........fine2   FALSE !!!				
		//                 inizio2...........fine2						   TRUE	 
		// <.......................................................fine2   TRUE  
		// <................fine2                                          TRUE
		// inizio2..........fine2                                          TRUE
		//
		// Caso "inizio1" e "fine1"
		//                  inizio1.........fine1
		//-----------------------------------------------------------------------------
		// inizio2.....................................................>   TRUE
		//                          inizio2............................>   TRUE
		//                          inizio2...........fine2				   TRUE
		//                                               inizio2...fine2   FALSE !!!
		// <.......................................................fine2   TRUE		
		// <........................fine2                                  TRUE
		//	      inizio2...........fine2                                  TRUE
		// inizio2...fine2                                                 FALSE !!!
		//
		boolean nonSovrapposti = true;
		if(inizio1 == null && fine1 == null) {			
			nonSovrapposti = false;	
		} else if(inizio1 != null && fine1 == null) {
			nonSovrapposti = (fine2 != null && fine2.compareTo(inizio1) < 0);
		} else if(inizio1 == null && fine1 != null) {
			nonSovrapposti = (inizio2 != null && inizio2.compareTo(fine1) > 0);
		} else if(inizio1 != null && fine1 != null) {
			nonSovrapposti = ((inizio2 == null && fine2 == null) ||
					          (inizio2 != null && inizio2.compareTo(fine1) > 0) ||
			                  (fine2 != null && fine2.compareTo(inizio1) < 0));
		}
		return !nonSovrapposti;
	}
	
}
