package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando;

import it.eldasoft.sil.portgare.datatypes.ListaPartecipantiRaggruppamentoType;
import it.eldasoft.sil.portgare.datatypes.PartecipanteRaggruppamentoType;
import it.eldasoft.sil.portgare.datatypes.TipoPartecipazioneDocument;
import it.eldasoft.sil.portgare.datatypes.TipoPartecipazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractWizardHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.IDatiPrincipaliImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.IDatiUlterioriImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.IIndirizzoImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ISoggettoImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

/**
 * Contenitore dei dati del wizard partecipazione impresa ad una gara
 *
 * @author Stefano.Sabbadin
 */
public class WizardPartecipazioneHelper extends AbstractWizardHelper 
	implements HttpSessionBindingListener, Serializable, IRaggruppamenti {

	/**
	 * UID
	 */
	private static final long serialVersionUID = 7252718947878694303L;

	public static final int PROCEDURA_APERTA 		= 1;
	public static final int PROCEDURA_RISTRETTA 	= 2;
	public static final int PROCEDURA_NEGOZIATA 	= 3;

	public static final String STEP_IMPRESA 		= "impresa";
	public static final String STEP_RTI 			= "rti";
	public static final String STEP_COMPONENTI      = "componenti";
	public static final String STEP_LOTTI 			= "lotti";
	public static final String STEP_RIEPILOGO 		= "riepilogo";

	
	/**
	 * Data/ora scadenza entro la quale inviare richieste
	 */
	private Date dataScadenza;

	/**
	 * Data ufficiale NTP di presentazione della richiesta (ha senso solo per gli
	 * invii effettivi, per i salvataggi in bozza deve essere valorizzata ma non
	 * ha alcun senso in quanto viene generata all'atto dell'invio di una
	 * richiesta al backoffice)
	 */
	private Date dataPresentazione;

	/**
	 * Chiave del bando
	 */
	private String idBando;

	/**
	 * Descrizione del bando
	 */
	private String descBando;

	/**
	 * tipologia di gara (1=lotto unico, 2=piu' lotti con offerte distinte, 3=piu'
	 * lotti con offerta unica)
	 */
	private int genere;

	/**
	 * true se e' prevista la selezione del lotto (gara a piu' lotti distinti),
	 * false altrimenti (lotto unico o piu' lotti con offerta unica)
	 */
	private boolean lottiDistinti;

	/**
	 * Helper per lo step dati impresa
	 */
	private WizardDatiImpresaHelper impresa;

	/**
	 * true se la pagina RTI e' editabile, false altrimenti
	 */
	private boolean editRTI;

	/**
	 * true se impresa RTI, false altrimenti
	 */
	private boolean rti;

	/**
	 * true se la descrizione RTI non e' editabile, false altrimenti
	 */
	private boolean denominazioneRTIReadonly;
	
	/**
	 * Denominazione dell'associazione temporanea d'impresa per cui il richiedente
	 * rappresenta la mandataria
	 */
	private String denominazioneRTI;

	/**
	 * Quota di partecipazione dell'associazione temporanea d'impresa per cui il
	 * richiedente rappresenta la mandataria
	 */
	private Double quotaRTI;

	/**
	 * Elenco delle componenti in caso di consorzio o RTI.
	 */
	private List<IComponente> componenti;

	/**
	 * Codici dei lotti per cui si richiede la partecipazione (nel caso di gara a
	 * lotto unico o gara a piu' lotti con offerta unica, e' il codice della gara
	 * stessa)
	 */
	private TreeSet<String> lotti;

	/**
	 * Associazione dei lotti con i file di timbri digitali attivi
	 */
	private LinkedHashMap<String, File> lottiBarcodeFiles;

	/**
	 * Tipologia di evento (1=partecipazione gara, 2=invio offerta,
	 * 3=aggiornamento dati art.48)
	 */
	private int tipoEvento;

	/**
	 * Identificativo univoco della comunicazione dei dati di partecipazione
	 */
	private Long idComunicazioneTipoPartecipazione;

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
	 * Indica se e' una gara telematica o meno
	 */
	private boolean garaTelematica;
	
	/**
	 * True se le consorziate esecutrici sono gi&agrave; state acquisite in
	 * backoffice, false altrimenti (consorzio senza alcuna consorziata).
	 */
	private boolean consorziateEsecutriciPresenti;

	
	private boolean plicoUnicoOfferteDistinte;
	
	/* Permette di mantenere traccia dell'oggetto dei vari lotti */
	private List<String> oggettiLotti;
	/* Permette di mantenere traccia del codice interno dei vari lotti */
	private List<String> codiciInterniLotti;
	
	
	/**
	 * lotti ammessi in invio offerta solo per gare ristrette
	 * vuota per aperte e negoziate 
	 */
	private List<String> lottiAmmessiInOfferta;
	
	/** 
	 * Tipo procedura
	 */
	private int tipoProcedura;
	
	/** 
	 * Tipo gara  
	 */
	private int tipoGara;

	/** 
	 * rti della domanda di partecipazione per una gara ristretta  
	 */
	private boolean rtiPartecipazione;	
	
	/**
	 * true se impresa RTI, false altrimenti (gare negoziate)
	 */
	private boolean rtiBO;
	
	/** 
	 * Iter di gara
	 *  ---------------- A1090 - Iter di gara Alice (NON CONFIGURABILE) ------
	 *   A1090  1       Procedura aperta (aperta)                                               
	 *   A1090  2       Procedura ristretta (ristretta)                                             
	 *   A1090  3       Procedura ristretta semplificata (negoziata)                               
	 *   A1090  4       Procedura negoziata previa pubblicazione bando di gara (ristretta)         
	 *   A1090  5       Procedura negoziata senza previa pubblicazione bando di gara (negoziata)   
	 *   A1090  6       Richiesta di Offerta (negoziata) 
	 *   
	 *   APERTA    = { 1 }        =>  iterGara = 1
	 *   RISTRETTA = { 2, 4 }     =>  iterGara = 2
	 *   NEGOZIATA = { 3, 5, 6 }  =>  iterGara = 3
	 */
	private int iterGara;

	/**
	 * Gara di rilancio
	 */
	private boolean garaRilancio;
		
//	/**
//	 * tipo di raggruppamento  
//	 */
//	private String tipoRaggruppamento;
	
	/**
	 * progressivo relativo all'offerta
	 */
	private String progressivoOfferta;
	
	
	/**
	 * Inizializza il contenitore vuoto attribuendo i valori di default
	 */
	public WizardPartecipazioneHelper() {
		super();
		this.dataScadenza = null;
		this.dataPresentazione = null;
		this.idBando = null;
		this.descBando = null;
		this.genere = 1;
		this.impresa = null;
		this.editRTI = false;
		this.rti = false;
		this.denominazioneRTI = null;
		this.denominazioneRTIReadonly = true;
		this.quotaRTI = null;
		this.componenti = new ArrayList<IComponente>();
		this.lottiDistinti = false;
		this.plicoUnicoOfferteDistinte = false;
		this.lotti = new TreeSet<String>();
		this.tipoEvento = 1;
		this.idComunicazioneTipoPartecipazione = null;
		this.tempFiles = new ArrayList<File>();
		this.lottiBarcodeFiles = new LinkedHashMap<String, File>();
		this.datiInviati = false;
		this.garaTelematica = false;
		this.consorziateEsecutriciPresenti = false;
		this.oggettiLotti = new ArrayList<String>();
		this.codiciInterniLotti = new ArrayList<String>();
		this.lottiAmmessiInOfferta = new ArrayList<String>();
		this.tipoProcedura = 0;
		this.rtiPartecipazione = false;
		this.rtiBO = false;
		this.iterGara = 0;
		this.garaRilancio = false;
		this.progressivoOfferta = null;
//		this.tipoRaggruppamento = null;
	}
	
	/**
	 * costruisci l'helper dall'xml della comunicazione 
	 */
	public WizardPartecipazioneHelper(ComunicazioneType comunicazione) throws XmlException {
		this();
		
		String xml = this.getAllegatoXml(comunicazione);
		TipoPartecipazioneDocument doc = TipoPartecipazioneDocument.Factory.parse(xml);
		
		// se sono presenti dei lotti nel documento XML forza la lettura... 
		String[] listaLotti = doc.getTipoPartecipazione().getCodiceLottoArray();
		this.lottiDistinti = (listaLotti != null && listaLotti.length > 0);
		this.fillFromXml(doc);
		
		this.progressivoOfferta = comunicazione.getDettaglioComunicazione().getChiave3();
	}
	
	/**
	 * ... 
	 */
	public String getAllegatoXml(ComunicazioneType comunicazione) {
		return super.getAllegatoXml(comunicazione, PortGareSystemConstants.NOME_FILE_TIPO_PARTECIPAZIONE);
	}
	
	@Override
	public void valueBound(HttpSessionBindingEvent arg0) {
	}

	/**
	 * Alla scadenza della sessione o alla rimozione del contenitore dalla
	 * sessione si rimuovono i file creati per questa gestione
	 *
	 * @param arg0 il session binding event
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

	public Date getDataScadenza() {
		return dataScadenza;
	}

	public void setDataScadenza(Date dataScadenza) {
		this.dataScadenza = dataScadenza;
	}

	public Date getDataPresentazione() {
		return dataPresentazione;
	}

	public void setDataPresentazione(Date dataPresentazione) {
		this.dataPresentazione = dataPresentazione;
	}

	public String getIdBando() {
		return idBando;
	}

	public void setIdBando(String idBando) {
		this.idBando = idBando;
	}

	public String getDescBando() {
		return descBando;
	}

	public void setDescBando(String descBando) {
		this.descBando = descBando;
	}

	public int getGenere() {
		return genere;
	}

	public void setGenere(int genere) {
		this.genere = genere;
	}

	public boolean isLottiDistinti() {
		return lottiDistinti;
	}

	public void setLottiDistinti(boolean lottiDistinti) {
		this.lottiDistinti = lottiDistinti;
	}
	
	@Override
	public WizardDatiImpresaHelper getImpresa() {
		return impresa;
	}

	public void setImpresa(WizardDatiImpresaHelper impresa) {
		this.impresa = impresa;
	}

	public boolean isEditRTI() {
		return editRTI;
	}

	public void setEditRTI(boolean editRTI) {
		this.editRTI = editRTI;
		this.denominazioneRTIReadonly = !editRTI;
	}

	public boolean isRti() {
		return rti;
	}

	public void setRti(boolean rti) {
		this.rti = rti;
	}

	public boolean isDenominazioneRTIReadonly() {
		return denominazioneRTIReadonly;
	}

	public void setDenominazioneRTIReadonly(boolean denominazioneRTIReadonly) {
		this.denominazioneRTIReadonly = denominazioneRTIReadonly;
	}

	public String getDenominazioneRTI() {
		return denominazioneRTI;
	}

	public void setDenominazioneRTI(String denominazioneRTI) {
		this.denominazioneRTI = denominazioneRTI;
	}

	public List<IComponente> getComponenti() {
		return componenti;
	}

	public void setComponenti(List<IComponente> componenti) {
		this.componenti = componenti;
	}

	public TreeSet<String> getLotti() {
		return lotti;
	}

	public void setLotti(TreeSet<String> lotti) {
		this.lotti = lotti;
	}

	public int getTipoEvento() {
		return tipoEvento;
	}

	public void setTipoEvento(int tipoEvento) {
		this.tipoEvento = tipoEvento;
	}

	public Long getIdComunicazioneTipoPartecipazione() {
		return idComunicazioneTipoPartecipazione;
	}

	public void setIdComunicazioneTipoPartecipazione(Long idComunicazioneTipoPartecipazione) {
		this.idComunicazioneTipoPartecipazione = idComunicazioneTipoPartecipazione;
	}

	public List<File> getTempFiles() {
		return tempFiles;
	}

	public boolean isDatiInviati() {
		return datiInviati;
	}

	public void setDatiInviati(boolean datiInviati) {
		this.datiInviati = datiInviati;
	}

	public LinkedHashMap<String, File> getLottiBarcodeFiles() {
		return lottiBarcodeFiles;
	}

	public IDatiPrincipaliImpresa getDatiPrincipaliImpresa() {
		return this.impresa.getDatiPrincipaliImpresa();
	}

	public ArrayList<IIndirizzoImpresa> getIndirizziImpresa() {
		return this.impresa.getIndirizziImpresa();
	}

	public ArrayList<ISoggettoImpresa> getLegaliRappresentantiImpresa() {
		return this.impresa.getLegaliRappresentantiImpresa();
	}

	public ArrayList<ISoggettoImpresa> getDirettoriTecniciImpresa() {
		return this.impresa.getDirettoriTecniciImpresa();
	}

	public ArrayList<ISoggettoImpresa> getAzionistiImpresa() {
		return this.impresa.getAltreCaricheImpresa();
	}

	public IDatiUlterioriImpresa getDatiUlterioriImpresa() {
		return this.impresa.getDatiUlterioriImpresa();
	}

	public boolean isGaraTelematica() {
		return garaTelematica;
	}

	public void setGaraTelematica(boolean garaTelematica) {
		this.garaTelematica = garaTelematica;
	}
	
	public boolean isConsorziateEsecutriciPresenti() {
		return consorziateEsecutriciPresenti;
	}

	public void setConsorziateEsecutriciPresenti(boolean consorziateEsecutriciPresenti) {
		this.consorziateEsecutriciPresenti = consorziateEsecutriciPresenti;
	}

	public Double getQuotaRTI() {
		return quotaRTI;
	}

	public void setQuotaRTI(Double quotaRTI) {
		this.quotaRTI = quotaRTI;
	}

	public boolean checkQuota() {
		return true;
	}
	
	public boolean isPlicoUnicoOfferteDistinte() {
		return plicoUnicoOfferteDistinte;
	}
	
	public void setPlicoUnicoOfferteDistinte(boolean plicoUnicoOfferteDistinte) {
		this.plicoUnicoOfferteDistinte = plicoUnicoOfferteDistinte;
	}

	public List<String> getOggettiLotti() {
		return oggettiLotti;
	}

	public void setOggettiLotti(List<String> oggettiLotti) {
		this.oggettiLotti = oggettiLotti;
	}

	public List<String> getCodiciInterniLotti() {
		return codiciInterniLotti;
	}

	public void setCodiciInterniLotti(List<String> codiciInterniLotti) {
		this.codiciInterniLotti = codiciInterniLotti;
	}

	public List<String> getLottiAmmessiInOfferta() {
		return lottiAmmessiInOfferta;
	}

	public void setLottiAmmessiInOfferta(List<String> lottiAmmessiInOfferta) {
		this.lottiAmmessiInOfferta = lottiAmmessiInOfferta;
	}

	public int getTipoProcedura() {
		return tipoProcedura;
	}

	public void setTipoProcedura(int tipoProcedura) {
		this.tipoProcedura = tipoProcedura;
	}

	public boolean isRtiPartecipazione() {
		return rtiPartecipazione;
	}

	public void setRtiPartecipazione(boolean rtiPartecipazione) {
		this.rtiPartecipazione = rtiPartecipazione;
	}

    public boolean isRtiBO() {
		return rtiBO;
	}

	public void setRtiBO(boolean rtiBO) {
		this.rtiBO = rtiBO;
	}

	public int getIterGara() {
		return iterGara;
	}

	public void setIterGara(int iterGara) {
		this.iterGara = decodeIterGara(iterGara);
	}

	public boolean isGaraRilancio() {
		return garaRilancio;
	}
	
	public void setGaraRilancio(boolean garaRilancio) {
		this.garaRilancio = garaRilancio;
	}

//	public String getTipoRaggruppamento() {
//		return tipoRaggruppamento;
//	}
//
//	public void setTipoRaggruppamento(String tipoRaggruppamento) {
//		this.tipoRaggruppamento = tipoRaggruppamento;
//	}
	
	public String getProgressivoOfferta() {
		return progressivoOfferta;
	}

	public void setProgressivoOfferta(String progressivoOfferta) {
		this.progressivoOfferta = progressivoOfferta;
	}

	/**
	 * Crea l'oggetto documento per la generazione della stringa XML per l'inoltro
	 * della richiesta al backoffice del tipo partecipazione.
	 *
	 * @return oggetto documento contenente i dati per il tipo di partecipazione
	 */
	public XmlObject getXmlDocumentTipoPartecipazione() {
		GregorianCalendar dataUfficiale = new GregorianCalendar();
		dataUfficiale.setTime(this.dataPresentazione);
		XmlObject document;
		TipoPartecipazioneDocument doc = TipoPartecipazioneDocument.Factory.newInstance();
		TipoPartecipazioneType tipoPartecipazione = doc.addNewTipoPartecipazione();
		tipoPartecipazione.setDataPresentazione(dataUfficiale);
		tipoPartecipazione.setRti(this.rti);
//		tipoPartecipazione.setTipoRaggruppamento(this.tipoRaggruppamento);
		
		if (this.rti) {
			tipoPartecipazione.setDenominazioneRti(this.denominazioneRTI);
			if (this.quotaRTI != null) {
				tipoPartecipazione.setQuotaMandataria(this.quotaRTI);
			}
		}
		if ((this.rti || this.getImpresa().isConsorzio())
			&& this.componenti.size() > 0) {
			ListaPartecipantiRaggruppamentoType listaPartecipanti = tipoPartecipazione.addNewPartecipantiRaggruppamento();
			for (IComponente componente : this.componenti) {
				PartecipanteRaggruppamentoType partecipante = listaPartecipanti.addNewPartecipante();
				partecipante.setRagioneSociale(componente.getRagioneSociale());
				partecipante.setNazione(componente.getNazione());
				partecipante.setCodiceFiscale(componente.getCodiceFiscale());
				partecipante.setPartitaIVA(componente.getPartitaIVA());
				partecipante.setTipoImpresa(componente.getTipoImpresa());
				if (this.rti && componente.getQuota() != null) {
					partecipante.setQuota(componente.getQuota());
				}
			}
		}
				
		if(this.garaTelematica || this.tipoEvento == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA) {
			Iterator<String> lottiIterator = this.lotti.iterator();
			while(lottiIterator.hasNext()) {
				tipoPartecipazione.addCodiceLotto(lottiIterator.next());
			}
		}
		
		document = doc;
		document.documentProperties().setEncoding("UTF-8");
		return document;
	}
	
	/**
	 * ... 
	 */
	@Override
	public XmlObject getXmlDocument(
			XmlObject document,
			boolean attachFileContents, 
			boolean report) throws Exception 
	{
		return getXmlDocumentTipoPartecipazione();
	}
	
	/**
	 * Popola l'helper per la parte di dati gestiti mediante XML a partire da un
	 * XML in input.
	 * 
	 * @param document
	 *            documento XML di partecipazione con i dati memorizzati in
	 *            precedenza
	 */
	public void fillFromXml(TipoPartecipazioneDocument document) {
		this.setRti(document.getTipoPartecipazione().getRti());
//		this.setTipoRaggruppamento(document.getTipoPartecipazione().getTipoRaggruppamento());
		//this.setProgressivoOfferta(document.getTipoPartecipazione().getProgressivoOfferta());  // W_INVCOM.COMKEY3
		
		if (this.isRti()) {
			this.setDenominazioneRTI(document.getTipoPartecipazione().getDenominazioneRti());
			this.setQuotaRTI(document.getTipoPartecipazione().getQuotaMandataria());
		}
		if (document.getTipoPartecipazione().getPartecipantiRaggruppamento() != null) {
			List<IComponente> componenti = new ArrayList<IComponente>();
			for (PartecipanteRaggruppamentoType partecipante : document
					.getTipoPartecipazione().getPartecipantiRaggruppamento().getPartecipanteArray()) {
				IComponente componente = new ComponenteHelper();
				componente.setRagioneSociale(partecipante.getRagioneSociale());
				componente.setTipoImpresa(partecipante.getTipoImpresa());
				componente.setNazione(partecipante.getNazione());
				componente.setCodiceFiscale(partecipante.getCodiceFiscale());
				componente.setPartitaIVA(partecipante.getPartitaIVA());
				componente.setQuota(partecipante.getQuota());
				componenti.add(componente);
			}
			this.setComponenti(componenti);
		}
		
		// ----- Recupero lotti -----
		if((isGaraTelematica() && isPlicoUnicoOfferteDistinte()) || isLottiDistinti()) {
			String[] listaLotti = document.getTipoPartecipazione().getCodiceLottoArray();
			if(document.getTipoPartecipazione().getCodiceLottoArray() != null) {
			
				TreeSet<String> listaLottiCompleta = new TreeSet<String>();
				for(int i = 0; i < listaLotti.length; i++) {
					listaLottiCompleta.add(listaLotti[i]);
				}
	
				this.setLotti(listaLottiCompleta);
			}
		}
	}
	
	/**
	 * Decodifica il valore di iter gara del backoffice
	 *  APERTA    = { 1 }			=> PROCEDURA_APERTA (1)
	 *  RISTRETTA = { 2, 4 }		=> PROCEDURA_RISTRETTA (2)
	 *  NEGOZIATA = { 3, 5, 6 }		=> PROCEDURA_NEGOZIATA (3)
	 */
	private static int decodeIterGara(int iterGara) {
		if(iterGara == PROCEDURA_APERTA) {
			return PROCEDURA_APERTA;
		} else if(iterGara == PROCEDURA_RISTRETTA || iterGara == 4) {
			return PROCEDURA_RISTRETTA;
		} else if (iterGara == PROCEDURA_NEGOZIATA || iterGara == 5 || iterGara == 6) {
			return PROCEDURA_NEGOZIATA;
		} else {
			return iterGara;
		}
	}

	public static boolean isGaraRistretta(String iterGara) {
		int iter = (StringUtils.isEmpty(iterGara) ? 0 : Integer.valueOf(iterGara));
		return (decodeIterGara(iter) == PROCEDURA_RISTRETTA); 
	}
	
	public static boolean isGaraNegoziata(String iterGara) {
		int iter = (StringUtils.isEmpty(iterGara) ? 0 : Integer.valueOf(iterGara));
		return (decodeIterGara(iter) == PROCEDURA_NEGOZIATA); 
	}
	
	public static boolean isGaraAperta(String iterGara) {
		int iter = (StringUtils.isEmpty(iterGara) ? 0 : Integer.valueOf(iterGara));
		return (decodeIterGara(iter) == PROCEDURA_APERTA); 
	}

	/**
	 * Genera (o rigenera) lo stack delle pagine del wizard per consentire la
	 * corretta navigazione
	 */
	public String getSTEP_IMPRESA() { return STEP_IMPRESA; }
	public String getSTEP_RTI() { return STEP_RTI; }
	public String getSTEP_COMPONENTI() { return STEP_COMPONENTI; }
	public String getSTEP_LOTTI() { return STEP_LOTTI; }
	public String getSTEP_RIEPILOGO() { return STEP_RIEPILOGO; }
		
	/**
	 * ... 
	 */
	public void fillStepsNavigazione() {
		// configura la mappa degli step di navigazione
		// e i relativi target associati
		this.clearMappaStepNavigazione();
		this.addMappaStepNavigazione(STEP_IMPRESA, 	  null,                    null);
		this.addMappaStepNavigazione(STEP_RTI, 		  "successSkipRTI",        "backSkipRTI");
		this.addMappaStepNavigazione(STEP_COMPONENTI, "successSkipComponenti", "backSkipComponenti");
		this.addMappaStepNavigazione(STEP_LOTTI, 	  "successSkipLotti",      "backSkipLotti");
		this.addMappaStepNavigazione(STEP_RIEPILOGO,  null,      			   null);
		
		// prepara l'elendo degli step disponibili per il wizard
		boolean domandaPartecipazione = (this.tipoEvento == 1); // PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA
		boolean invioOfferta = (this.tipoEvento == 2);			// PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA
		boolean consorzio = (this.getImpresa().isConsorzio());
		boolean abilitazioniRistretta = (domandaPartecipazione) ||
				 					    (invioOfferta && !this.rtiPartecipazione);
		boolean abilitazioniNegoziata = 
			(!this.rtiBO && consorzio && !this.consorziateEsecutriciPresenti) ||
			(!this.rtiBO && !consorzio) ||
			(!this.rtiBO && consorzio && this.consorziateEsecutriciPresenti);	// <= consorzio+consorziate esecutrici in BO, può decidere di partecipare come RTI
		
		this.setIterGara(this.iterGara);
		boolean aperta    = (this.iterGara == PROCEDURA_APERTA);
		boolean ristretta = (this.iterGara == PROCEDURA_RISTRETTA);
		boolean negoziata = (this.iterGara == PROCEDURA_NEGOZIATA);
		boolean garaLotti = this.plicoUnicoOfferteDistinte;
		
		// ----- IMPRESA -----
		this.stepNavigazione.add(STEP_IMPRESA);
				
		// ----- FORMA PARTECIPAZIONE -----
		// per gare ristretta o negoziate, si abilita l'edit della 
		// forma di partecipazione in casi particolari:
		// - ristrette: in domanda di partecipazione oppure 
		//              in offerta quando nella precedente domanda di partecipazione 
		//              non ci si sia presentati come RTI
		// - negoziate: se un consorzio è stato invitato come singolo oppure 
		//   			se un'impresa è stata invitata come singola
		if( (ristretta && abilitazioniRistretta) || 
		    (negoziata && abilitazioniNegoziata) ) {
			this.setEditRTI(true);
		}
	
		// la "forma di partecipazione" è abilitata se:
		// - gara aperta o negoziata
		// - gare ristrette
		//   - in partecipazione, sono consorzio, non posso compilare le 
		//     consorziate perchè vanno compilati in fase di offerta,
		//	   posso però partecipare come RTI	
		//   - in partecipazione, NON sono consorzio, e mi presento come rti,
		//     allora in offerta non posso modificare il raggruppamento rti
		//   - in partecipazione e offerta, sono ditta singola, non deve comparire
		//     lo step "STEP_COMPONENTI"
		// - non è una gara di rilancio
		if(	((this.editRTI && aperta) || 
			 (negoziata && abilitazioniNegoziata) || 
			 (ristretta && abilitazioniRistretta)) 
			 && !this.garaRilancio ) {
			this.stepNavigazione.add(STEP_RTI);
		}   
		
		// ----- CONSORZIATE ESECUTRICI/COMPONENTI RAGGRUPPAMENTO -----
		// i componenti (consorziate esecutrici/componenti raggruppamento) sono 
		// sono abilitati se:
		//	 - gara aperta o negoziata
	    //   - gara ristretta in fase di domanda partecipazione e sono un'impresa 
	    //   - gara ristretta in fase di domanda partecipazione e sono un consorzio ma partecipo come RTI
		//   - gara ristretta in fase di offerta che non si è presentata come RTI
		//   NB: 
		//	   abilitazione/disabilitazione dello step STEP_COMPONENTI 
		//     va gestita dinamicamente in
		//       - OpenPageRTIAction
		//       - ProcessPageRTIAction
		//       - datiRTISection
		//     quindi è sempre aggiunto ma viene abilitato/disabilitato 
		//     in "ProcessPageRTIAction"
		if( !this.garaRilancio ) {
			this.stepNavigazione.add(STEP_COMPONENTI);
			this.abilitaStepNavigazione(STEP_COMPONENTI, this.isStepComponentiAbilitato());
		}

		// ----- LOTTI -----
		// i lotti sono abilitati se: 
		//   - lotti distinti per gara non telematica
		//   - plico unico offerte distinte per gara telematica
		//   - plico unico offerta unica ????????
		//   - gare ristrette sia in partecipazione che in offerta
		boolean lotti =
			(this.isLottiDistinti() && !this.isGaraTelematica()) || 
			//(!this.isPlicoUnicoOfferteDistinte() && this.isGaraTelematica()) || ???
		    (this.isPlicoUnicoOfferteDistinte() && this.isGaraTelematica());
		if(	(!ristretta && lotti) ||
			(ristretta && lotti)) { 
			this.stepNavigazione.add(STEP_LOTTI);
		}
				
		// ----- RIEPILOGO -----
		this.stepNavigazione.add(STEP_RIEPILOGO);
	}
	
	/**
	 * Restuisce se lo step COMPONENTI è abilitato o meno nella navigazione
	 * del wizard
	 */
	public boolean isStepComponentiAbilitato() {
		boolean domandaPartecipazione = (this.tipoEvento == 1); 
		boolean invioOfferta = (this.tipoEvento == 2);
		boolean consorzio = (this.getImpresa().isConsorzio());
		this.setIterGara(this.iterGara);
		boolean aperta    = (this.iterGara == PROCEDURA_APERTA);
		boolean ristretta = (this.iterGara == PROCEDURA_RISTRETTA);
		boolean negoziata = (this.iterGara == PROCEDURA_NEGOZIATA);
	
		return !this.garaRilancio && (
			   (aperta && consorzio) || 
			   (aperta && !consorzio && this.rti) ||
			   (negoziata && !consorzio && !this.rtiBO && this.rti) ||
			   (negoziata && consorzio && !this.rtiBO && !this.consorziateEsecutriciPresenti) ||
		       (negoziata && consorzio && !this.rtiBO && this.consorziateEsecutriciPresenti && this.rti) ||
		       (ristretta && consorzio && domandaPartecipazione && this.rti) ||
		       (ristretta && consorzio && invioOfferta && !this.rtiPartecipazione) ||
		       (ristretta && !consorzio && domandaPartecipazione && this.rti) ||
		       (ristretta && !consorzio && invioOfferta && this.rti && !this.rtiPartecipazione)
		       );
	}
	
	/**
	 * copia le impostazioni rti e consorziate da un helper di partecipazione 
	 * Utilizzato per sincronizzare le impostazioni rti e consorziate tra 2 helper
	 * o per copiare le impostazioni per una gara di rilancio 
	 */
	public void copyRtiFrom(WizardPartecipazioneHelper from) {
		// sincronizza helper destinazione con l'helper sorgente...
		if(from != null) {
			this.setEditRTI(from.isEditRTI());
			this.setRti(from.isRti());
			this.setDenominazioneRTI(from.getDenominazioneRTI());
			this.setDenominazioneRTIReadonly(from.isDenominazioneRTIReadonly());
			this.setRtiPartecipazione(from.isRtiPartecipazione());
			this.setRtiBO(from.isRtiBO());
			this.setProgressivoOfferta(from.getProgressivoOfferta());
//			this.setTipoRaggruppamento(from.getTipoRaggruppamento());
			
			if(this.getTipoEvento() == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA) {
				// recupera i componenti raggruppamento RTI / consorziate esecutrici
				this.setComponenti(from.getComponenti());
				// per le gara ristrette recupera dalla partecipazione 
				// l'elenco dei lottiAmmessi in offerta 
				if(this.getIterGara() == WizardPartecipazioneHelper.PROCEDURA_RISTRETTA) {	
					this.setLottiAmmessiInOfferta(from.getLottiAmmessiInOfferta());
				}
			}
		}
	}

}
