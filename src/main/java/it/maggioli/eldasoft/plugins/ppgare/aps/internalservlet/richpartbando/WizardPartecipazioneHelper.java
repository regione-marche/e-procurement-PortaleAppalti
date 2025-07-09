package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando;

import it.eldasoft.sil.portgare.datatypes.ImpresaAusiliariaType;
import it.eldasoft.sil.portgare.datatypes.ListaImpreseAusiliarieType;
import it.eldasoft.sil.portgare.datatypes.ListaPartecipantiRaggruppamentoType;
import it.eldasoft.sil.portgare.datatypes.PartecipanteRaggruppamentoType;
import it.eldasoft.sil.portgare.datatypes.TipoPartecipazioneDocument;
import it.eldasoft.sil.portgare.datatypes.TipoPartecipazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractWizardHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.*;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.io.File;
import java.io.Serializable;
import java.util.*;

/**
 * Contenitore dei dati del wizard partecipazione impresa ad una gara
 *
 * @author Stefano.Sabbadin
 */
public class WizardPartecipazioneHelper extends AbstractWizardHelper 
	implements Serializable, HttpSessionBindingListener, IRaggruppamenti, IAvvalimento 
{
	/**
	 * UID
	 */
	private static final long serialVersionUID = 7252718947878694303L;

	public static final int PROCEDURA_APERTA 		= 1;
	public static final int PROCEDURA_RISTRETTA 	= 2;
	public static final int PROCEDURA_NEGOZIATA 	= 3;
	public static final int PROCEDURA_RDO 			= 10;
	public static final int INDAGINE_MERCATO		= 7;
	public static final int CONCORSO_I_GRADO		= 9;
	public static final int CONCORSO_II_GRADO		= 10;

	public static final String STEP_IMPRESA 		= "impresa";
	public static final String STEP_RTI 			= "rti";
	public static final String STEP_COMPONENTI		= "componenti";
	public static final String STEP_AVVALIMENTO		= "avvalimento";
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
	 * tipologia di gara (1=lotto unico, 2=piu' lotti con offerte distinte, 3=piu' lotti con offerta unica)
	 */
	private int genere;

	/**
	 * true se e' prevista la selezione del lotto (gara a piu' lotti distinti),
	 * false altrimenti (lotto unico o piu' lotti con offerta unica)
	 */
	private boolean lottiDistinti;

	/**
	 * true se la pagina RTI e' editabile, false altrimenti
	 */
	private boolean editRTI;

	/**
	 * true se la descrizione RTI non e' editabile, false altrimenti
	 */
	private boolean denominazioneRTIReadonly;

	private String codiceCNEL;
	
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
	 *   INDAGINE MERCATO = { 7 } =>  iterGara = 7
	 *   RICERCA MERCATO = { 8 }  =>  iterGara = 8
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
	 * interfaccia IRaggruppamenti
	 */
	private boolean rti;						// true se impresa RTI, false altrimenti
	
	private String denominazioneRTI;			// denominazione dell'RTI
	
	private Double quotaRTI;					// % di partecipazione dell'associazione temporanea d'impresa per cui il richiedente rappresenta la mandataria
	
	private List<IComponente> componenti;		// elenco delle componenti in caso di consorzio o RTI.
	
	private WizardDatiImpresaHelper impresa;	// helper per lo step dati impresa
	
//	private String checkQuota() 				// non previsto
	
	/**
	 * interfaccia IAvvalimento
	 */
	private boolean avvalimento;						// true se c'e' avvalimento, false altrimenti
	
	private List<IImpresaAusiliaria> impreseAusiliarie;	// elenco delle componenti in caso di consorzio o RTI. 

	
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
		this.denominazioneRTIReadonly = true;
		this.codiceCNEL = null;
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
		//this.tipoRaggruppamento = null;
		
		// IRaggruppamenti
		this.rti = false;
		this.denominazioneRTI = null;
		this.quotaRTI = null;
		this.componenti = new ArrayList<IComponente>();
		this.impresa = null;
		//this.checkQuota			// <= non previsto
		
		// IAvvalimento
		this.avvalimento = false;
		this.impreseAusiliarie = new ArrayList<IImpresaAusiliaria>();
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
	
	public boolean isEditRTI() {
		return editRTI;
	}

	public void setEditRTI(boolean editRTI) {
		this.editRTI = editRTI;
		this.denominazioneRTIReadonly = !editRTI;
	}

	public boolean isDenominazioneRTIReadonly() {
		return denominazioneRTIReadonly;
	}

	public void setDenominazioneRTIReadonly(boolean denominazioneRTIReadonly) {
		this.denominazioneRTIReadonly = denominazioneRTIReadonly;
	}

	public String getCodiceCNEL() {
		return codiceCNEL;
	}

	public void setCodiceCNEL(String codiceCNEL) {
		this.codiceCNEL = codiceCNEL;
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
	 * interfaccia IRaggruppamenti
	 */
	@Override
	public boolean isRti() {
		return rti;
	}

	@Override
	public void setRti(boolean rti) {
		this.rti = rti;
	}

	@Override
	public String getDenominazioneRTI() {
		return denominazioneRTI;
	}

	@Override
	public void setDenominazioneRTI(String denominazioneRTI) {
		this.denominazioneRTI = denominazioneRTI;
	}
	
	@Override
	public Double getQuotaRTI() {
		return quotaRTI;
	}
	
	@Override
	public void setQuotaRTI(Double quotaRTI) {
		this.quotaRTI = quotaRTI;
	}

	@Override
	public List<IComponente> getComponenti() {
		return componenti;
	}
	
	@Override
	public WizardDatiImpresaHelper getImpresa() {
		return impresa;
	}

	@Override
	public IDatiPrincipaliImpresa getDatiPrincipaliImpresa() {
		return this.impresa.getDatiPrincipaliImpresa();
	}

	@Override
	public boolean checkQuota() {
		return true;
	}

	// NON appartiene a IRaggruppamenti ma e' utile per il Wizard
	public void setImpresa(WizardDatiImpresaHelper impresa) {
		this.impresa = impresa;
	}
	
	// NON appartiene a IRaggruppamenti ma e' utile per il Wizard
	public void setComponenti(List<IComponente> componenti) {
		this.componenti = componenti;
	}

	/**
	 * interfaccia IAvvalimento
	 */
	@Override
	public boolean isAvvalimento() {
		return avvalimento;
	}

	@Override
	public void setAvvalimento(boolean avvalimento) {
		this.avvalimento = avvalimento;
	}

	@Override
	public List<IImpresaAusiliaria> getImpreseAusiliarie() {
		return impreseAusiliarie;
	}

	@Override
	public void setImpreseAusiliarie(List<IImpresaAusiliaria> impreseAusiliarie) {	
		this.impreseAusiliarie = impreseAusiliarie;
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
				partecipante.setTipoImpresa(componente.getTipoImpresa());
				partecipante.setRagioneSociale(componente.getRagioneSociale());
				partecipante.setAmbitoTerritoriale(componente.getAmbitoTerritoriale());
				partecipante.setNazione(componente.getNazione());
				partecipante.setCodiceFiscale(componente.getCodiceFiscale());
				partecipante.setPartitaIVA(componente.getPartitaIVA());
				partecipante.setIdFiscaleEstero(componente.getIdFiscaleEstero());
				if (this.rti && componente.getQuota() != null) {
					partecipante.setQuota(componente.getQuota());
				}
			}
		}
		
		if(this.garaTelematica || this.tipoEvento == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA) {
			if(this.lotti != null) {
				Iterator<String> lottiIterator = this.lotti.iterator();
				while(lottiIterator.hasNext()) {
					tipoPartecipazione.addCodiceLotto(lottiIterator.next());
				}
			}
		}
		
		if(this.tipoEvento == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA) {
			//tipoPartecipazione.setCodiceCNEL((this.codiceCNEL != null ? this.codiceCNEL.toUpperCase() : this.codiceCNEL));
			tipoPartecipazione.setCodiceCNEL(this.codiceCNEL);
		}
		
		// avvalimento e imprese ausiliarie
		tipoPartecipazione.setAvvalimento(this.avvalimento);
		if(this.avvalimento) {
			if(this.impreseAusiliarie != null && this.impreseAusiliarie.size() > 0) {
				ListaImpreseAusiliarieType listaImpresaAusiliarie = tipoPartecipazione.addNewImpreseAusiliarie();
				for (IImpresaAusiliaria impresa : this.impreseAusiliarie) {
					ImpresaAusiliariaType impresaAusiliaria = listaImpresaAusiliarie.addNewImpresaAusiliaria();
					impresaAusiliaria.setTipoImpresa(impresa.getTipoImpresa());
					impresaAusiliaria.setRagioneSociale(impresa.getRagioneSociale());
					impresaAusiliaria.setAmbitoTerritoriale(impresa.getAmbitoTerritoriale());
					// ??? impresaAusiliaria.setNazione(impresa.getNazione());
					impresaAusiliaria.setCodiceFiscale(impresa.getCodiceFiscale());
					impresaAusiliaria.setPartitaIVA(impresa.getPartitaIVA());
					impresaAusiliaria.setIdFiscaleEstero(impresa.getIdFiscaleEstero());
					impresaAusiliaria.setAvvalimentoPer(impresa.getAvvalimentoPer());
				}
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
//		this.setProgressivoOfferta(document.getTipoPartecipazione().getProgressivoOfferta());  // W_INVCOM.COMKEY3
		
		if (this.isRti()) {
			this.setDenominazioneRTI(document.getTipoPartecipazione().getDenominazioneRti());
			this.setQuotaRTI(document.getTipoPartecipazione().getQuotaMandataria());
		}
		if (document.getTipoPartecipazione().getPartecipantiRaggruppamento() != null) {
			List<IComponente> componenti = new ArrayList<IComponente>();
			for (PartecipanteRaggruppamentoType partecipante : document
					.getTipoPartecipazione().getPartecipantiRaggruppamento().getPartecipanteArray()) {
				IComponente componente = new ComponenteHelper();
				componente.setTipoImpresa(partecipante.getTipoImpresa());
				componente.setRagioneSociale(partecipante.getRagioneSociale());
				componente.setAmbitoTerritoriale(partecipante.getAmbitoTerritoriale());
				componente.setNazione(partecipante.getNazione());
				componente.setCodiceFiscale(partecipante.getCodiceFiscale());
				componente.setPartitaIVA(partecipante.getPartitaIVA());
				componente.setIdFiscaleEstero(partecipante.getIdFiscaleEstero());
				componente.setQuota(partecipante.getQuota());
				componenti.add(componente);
			}
			this.setComponenti(componenti);
		}
		
		// ----- Recupero lotti -----
		if ((isGaraTelematica() && isPlicoUnicoOfferteDistinte()) || isLottiDistinti()) {
			String[] listaLotti = document.getTipoPartecipazione().getCodiceLottoArray();
			if(document.getTipoPartecipazione().getCodiceLottoArray() != null) {
			
				TreeSet<String> listaLottiCompleta = new TreeSet<String>();
				List<String> lottiInterni = new ArrayList<String>();
				List<String> oggetti = new ArrayList<String>();
				for(int i = 0; i < listaLotti.length; i++) {
					listaLottiCompleta.add(listaLotti[i]);
					
					// ricrea la liste dei lotti interni e oggetti
					int j = 0;
					Iterator<String> iter = this.lotti.iterator();
					while (iter.hasNext()) {
						String cod = iter.next();
						if(cod.equals(listaLotti[i])) {
							lottiInterni.add(this.codiciInterniLotti.get(j));
							oggetti.add(this.oggettiLotti.get(j));
							break;
						}
						j++;
					}
				}
				this.setLotti(listaLottiCompleta);
				this.setCodiciInterniLotti(lottiInterni);
				this.setOggettiLotti(oggetti);
			}
		}
		
		this.codiceCNEL = document.getTipoPartecipazione().getCodiceCNEL();
		
		// avvalimento e imprese ausiliarie
		try { 
			this.avvalimento = document.getTipoPartecipazione().getAvvalimento();
		} catch (Exception ex) {
			this.avvalimento = false;
		}
		if (this.avvalimento 
			&& (document.getTipoPartecipazione().isSetImpreseAusiliarie() && document.getTipoPartecipazione().getImpreseAusiliarie() != null)) 
		{
			List<IImpresaAusiliaria> imprese = new ArrayList<IImpresaAusiliaria>();
			for (ImpresaAusiliariaType ditta : document.getTipoPartecipazione().getImpreseAusiliarie().getImpresaAusiliariaList()) {
				IImpresaAusiliaria impresaAusiliaria = new ImpresaAusiliariaHelper();
				impresaAusiliaria.setTipoImpresa(ditta.getTipoImpresa());
				impresaAusiliaria.setRagioneSociale(ditta.getRagioneSociale());
				impresaAusiliaria.setAmbitoTerritoriale(ditta.getAmbitoTerritoriale());
				// ??? impresaAusiliaria.setNazione(ditta.getNazione());
				impresaAusiliaria.setCodiceFiscale(ditta.getCodiceFiscale());
				impresaAusiliaria.setPartitaIVA(ditta.getPartitaIVA());
				impresaAusiliaria.setIdFiscaleEstero(ditta.getIdFiscaleEstero());
				impresaAusiliaria.setAvvalimentoPer(ditta.getAvvalimentoPer());
				imprese.add(impresaAusiliaria);
			}
			this.impreseAusiliarie = imprese;
		}
	}
	
	/**
	 * Decodifica il valore di iter gara del backoffice
	 *  APERTA    = { 1 }			=> PROCEDURA_APERTA (1)
	 *  RISTRETTA = { 2, 4 }		=> PROCEDURA_RISTRETTA (2)
	 *  NEGOZIATA = { 3, 5, 6, 8 }	=> PROCEDURA_NEGOZIATA (3)
	 */
	private static int decodeIterGara(int iterGara) {
		if(iterGara == PROCEDURA_APERTA) {
			return PROCEDURA_APERTA;
		} else if(iterGara == PROCEDURA_RISTRETTA || iterGara == 4) {
			return PROCEDURA_RISTRETTA;
		} else if (iterGara == PROCEDURA_NEGOZIATA || iterGara == 5 || iterGara == 6 || iterGara == 8) {
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

	public static boolean isGaraRdo(String iterGara) {
		int iter = (StringUtils.isEmpty(iterGara) ? 0 : Integer.valueOf(iterGara));
		return (decodeIterGara(iter) == PROCEDURA_RDO);
	}
	
	public static boolean isGaraAperta(String iterGara) {
		int iter = (StringUtils.isEmpty(iterGara) ? 0 : Integer.valueOf(iterGara));
		return (decodeIterGara(iter) == PROCEDURA_APERTA); 
	}
	
    public boolean isConcorsoRiservato() {
		return this.iterGara == 10;
    }

	/**
	 * Genera (o rigenera) lo stack delle pagine del wizard per consentire la
	 * corretta navigazione
	 */
	public String getSTEP_IMPRESA() { return STEP_IMPRESA; }
	public String getSTEP_RTI() { return STEP_RTI; }
	public String getSTEP_COMPONENTI() { return STEP_COMPONENTI; }
	public String getSTEP_AVVALIMENTO() { return STEP_AVVALIMENTO; }
	public String getSTEP_LOTTI() { return STEP_LOTTI; }
	public String getSTEP_RIEPILOGO() { return STEP_RIEPILOGO; }
		
	/**
	 * ... 
	 */
	public void fillStepsNavigazione() {
		// configura la mappa degli step di navigazione
		// e i relativi target associati
		this.clearMappaStepNavigazione();
		this.addMappaStepNavigazione(STEP_IMPRESA, 	  	null,                   	null);
		this.addMappaStepNavigazione(STEP_RTI, 		  	"successSkipRTI", 			"backSkipRTI");		
		this.addMappaStepNavigazione(STEP_COMPONENTI, 	"successSkipComponenti", 	"backSkipComponenti");
		this.addMappaStepNavigazione(STEP_AVVALIMENTO,  null,   					null);
		this.addMappaStepNavigazione(STEP_LOTTI, 	  	"successSkipLotti",      	"backSkipLotti");		
		this.addMappaStepNavigazione(STEP_RIEPILOGO,  	null,      			   		null);
		
		// prepara l'elendo degli step disponibili per il wizard
		boolean domandaPartecipazione = (this.tipoEvento == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA);
		boolean invioOfferta = (this.tipoEvento == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA);
		boolean consorzio = (this.getImpresa().isConsorzio());
		//boolean abilitazioniRistretta = (domandaPartecipazione) ||
		//			 					    (invioOfferta && (!this.rtiPartecipazione || StringUtils.isEmpty(this.codiceCNEL)));
		//boolean abilitazioniNegoziata = 
		//		(!this.rtiBO && consorzio && !this.consorziateEsecutriciPresenti) ||
		//		(!this.rtiBO && !consorzio) ||
		//		(!this.rtiBO && consorzio && this.consorziateEsecutriciPresenti);	// <= consorzio+consorziate esecutrici in BO, può decidere di partecipare come RTI
		
		this.setIterGara(this.iterGara);
		boolean aperta    = (this.iterGara == PROCEDURA_APERTA);
		boolean ristretta = (this.iterGara == PROCEDURA_RISTRETTA);
		boolean negoziata = (this.iterGara == PROCEDURA_NEGOZIATA);
		boolean indagineMercato = (this.iterGara == INDAGINE_MERCATO);
		boolean garaLotti = this.plicoUnicoOfferteDistinte;
		
		// ----- IMPRESA -----
		this.stepNavigazione.add(STEP_IMPRESA);
		
		// STEP RTI, FORMA PARTECIPAZIONE
		//
		// RISTRETTA, PREQUALIFICA, LOTTO UNICO
		// decido rti oppure no => posso editare
		// (step) => domandaPartecipazione && !garaLotti
		// (editabilita) => domandaPartecipazione && !garaLotti
		
		// RISTRETTA, OFFERTA, LOTTO UNICO
		// NEGOZIATA, OFFERTA, LOTTO UNICO
		// invito singola => posso editare in RTI
		// invito RTI => no edit (DEVO VEDERE LO STEP PER EDITARE CNEL)
		// (step) => invioOfferta && !garaLotti
		// (editabilita) => invioOfferta && !garaLotti && !this.rtiBO
		
		// APERTA, OFFERTA, LOTTO UNICO
		// decido rti oppure no => posso editare
		// (step) => invioOfferta && !garaLotti
		// (editabilita) => invioOfferta && !garaLotti
		
		// RISTRETTA A LOTTI, PREQUALIFICA
		// plico singola => no edit
		// plico rti => edit
		// (step) => domandaPartecipazione && garaLotti
		// (editabilita) => domandaPartecipazione && garaLotti --&& this.rti

		// RISTRETTA, OFFERTA, A LOTTI
		// NEGOZIATA A LOTTI
		// plico singola => no edit, (DEVO VEDERE LO STEP PER EDITARE CNEL)
		// plico rti in bo => no edit, (DEVO VEDERE LO STEP PER EDITARE CNEL)
		// plico rti NON in bo => edit
		// (step) => invioOfferta && garaLotti
		// (editabilita) => invioOfferta && garaLotti && !this.rtiBO --&& this.rti
		
		// APERTA, OFFERTA, A LOTTI
		// plico singola => no edit, (DEVO VEDERE LO STEP PER EDITARE CNEL)
		// plico rti (NON in bo) => edit
		// (step) => invioOfferta && garaLotti
		// (editabilita) => invioOfferta && garaLotti && aperta && this.rti
		
		// step (metto in or le condizioni sopra, e ci aggiungo che non sia una gara a rilancio):
		// ((domandaPartecipazione && !garaLotti) ||
		// (domandaPartecipazione && garaLotti) ||
		// (invioOfferta && !garaLotti) ||
		// (invioOfferta && !garaLotti) ||
		// (invioOfferta && garaLotti)
		// (invioOfferta && garaLotti))
		//  && !this.garaRilancio
		// si semplifica così:
		// ((domandaPartecipazione || invioOfferta) && !this.garaRilancio)
		
		// editabilita:
		// ((domandaPartecipazione && !garaLotti) ||
		// (domandaPartecipazione && garaLotti) ||
		// (invioOfferta && !garaLotti && !this.rtiBO) ||
		// (invioOfferta && garaLotti && !this.rtiBO)
		// (invioOfferta && !garaLotti && aperta) ||
		// (invioOfferta && garaLotti && aperta && this.rti))
		// si semplifica così:
		// (domandaPartecipazione || (invioOfferta && !this.rtiBO && !aperta) || (invioOfferta && !garaLotti && aperta) ||
		// (invioOfferta && garaLotti && aperta && this.rti)) 

		// ----- FORMA PARTECIPAZIONE -----
		// per gare ristretta o negoziate, si abilita l'edit della 
		// forma di partecipazione in casi particolari:
		// - ristrette: in domanda di partecipazione oppure 
		//              in offerta quando nella precedente domanda di partecipazione 
		//              non ci si sia presentati come RTI
		// - negoziate: se un consorzio è stato invitato come singolo oppure 
		//   			se un'impresa è stata invitata come singola
		//if( (ristretta && abilitazioniRistretta) || 
		//    (negoziata && abilitazioniNegoziata) ) {
		//	this.setEditRTI(true);
		//}
		// SI COMMMENTANO LE RIGHE PRECEDENTI E SI SEMPLIFICA SULLA BASE DELLE ANALISI DESCRITTE NEI COMMENTI PIU' SOPRA
		if ((domandaPartecipazione || (invioOfferta && !this.rtiBO && !aperta) || (invioOfferta && !garaLotti && aperta)
				|| (invioOfferta && garaLotti && aperta && this.rti))) {
			this.setEditRTI(true);
		}
	
		// la "forma di partecipazione" (RT) è abilitata se:
		// - gara aperta o negoziata o indagine di mercato
		// - gare ristrette
		//   - in partecipazione, sono consorzio, non posso compilare le 
		//     consorziate perchè vanno compilati in fase di offerta,
		//	   posso però partecipare come RTI	
		//   - in partecipazione, NON sono consorzio, e mi presento come rti,
		//     allora in offerta non posso modificare il raggruppamento rti
		//   - in partecipazione e offerta, sono ditta singola, non deve comparire
		//     lo step "STEP_COMPONENTI"
		// - non è una gara di rilancio
		//if(	 (
		//	 (this.editRTI && aperta) ||
		//	 (this.editRTI && indagineMercato) ||
		//	 (negoziata && abilitazioniNegoziata) || 
		//	 (ristretta && abilitazioniRistretta)) 
		//	 && !this.garaRilancio ) 
		//{
		//
		//	this.stepNavigazione.add(STEP_RTI);
		//}
		if ((domandaPartecipazione || invioOfferta) && !this.garaRilancio) {
			this.stepNavigazione.add(STEP_RTI);
		}
		
		
		
		if (iterGara == CONCORSO_I_GRADO || iterGara == CONCORSO_II_GRADO)
			abilitaStepNavigazione(STEP_RTI, true);
		
		// ----- CONSORZIATE ESECUTRICI / COMPONENTI RAGGRUPPAMENTO -----
		// i componenti (consorziate esecutrici/componenti raggruppamento) sono 
		// sono abilitati se:
		//	 - gara aperta o negoziata o indagine di mercato
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

		// ----- AVVALIMENTO -----
		this.stepNavigazione.add(STEP_AVVALIMENTO);

		// ----- LOTTI -----
		// i lotti sono abilitati se: 
		//   - lotti distinti per gara non telematica
		//   - plico unico offerte distinte per gara telematica
		//   - plico unico offerta unica ????????
		//   - gare ristrette sia in partecipazione che in offerta
		if(this.lottiDistinti) {
			boolean lotti =
				(this.isLottiDistinti() && !this.isGaraTelematica()) || 
				//(!this.isPlicoUnicoOfferteDistinte() && this.isGaraTelematica()) || ???
			    (this.isPlicoUnicoOfferteDistinte() && this.isGaraTelematica());
			if(	(!ristretta && lotti) ||		// questa codizione equivale a => if (lotti) { ... }
				(ristretta && lotti) ) 
			{ 
				this.stepNavigazione.add(STEP_LOTTI);
			}
		}

		// ----- RIEPILOGO -----
		this.stepNavigazione.add(STEP_RIEPILOGO);
	}
	
	/**
	 * Restuisce se lo step COMPONENTI e' abilitato o meno nella navigazione
	 * del wizard
	 */
	public boolean isStepComponentiAbilitato() {
		boolean domandaPartecipazione = (tipoEvento == 1); 
		boolean invioOfferta = (tipoEvento == 2);
		boolean consorzio = (getImpresa().isConsorzio());
		this.setIterGara(iterGara);
		boolean aperta    = (iterGara == PROCEDURA_APERTA);
		boolean ristretta = (iterGara == PROCEDURA_RISTRETTA);
		boolean negoziata = (iterGara == PROCEDURA_NEGOZIATA || iterGara == 5);
		boolean indagineMercato = (iterGara == INDAGINE_MERCATO);
		boolean isConcorsoPrimoGrado = (iterGara == CONCORSO_I_GRADO);
		boolean isConcorsoSecondoGrado = (iterGara == CONCORSO_II_GRADO);

		return !this.garaRilancio && (
			   (aperta && consorzio)
			   || (aperta && !consorzio && rti)
			   || (negoziata && !consorzio && !rtiBO && rti)
			   || (negoziata && consorzio && !rtiBO && !consorziateEsecutriciPresenti)
		       || (negoziata && consorzio && !rtiBO && consorziateEsecutriciPresenti && rti)
		       || (negoziata && consorzio && !rtiBO)
		       || (ristretta && consorzio && domandaPartecipazione && rti)
		       || (ristretta && consorzio && invioOfferta && !rtiPartecipazione)
		       || (ristretta && consorzio && invioOfferta && !rti)
		       || (ristretta && !consorzio && domandaPartecipazione && rti)
		       || (ristretta && !consorzio && invioOfferta && rti && !rtiPartecipazione)
		       || (indagineMercato && consorzio)
			   || (indagineMercato && !consorzio && rti)
			   || ((isConcorsoPrimoGrado || isConcorsoSecondoGrado) && rti)
			   || (isConcorsoPrimoGrado && !rti && consorzio)
		   );
	}

}