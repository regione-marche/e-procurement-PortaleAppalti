package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;


import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.apsadmin.system.BaseAction;

import it.eldasoft.sil.portgare.datatypes.DocumentazioneBustaDocument;
import it.eldasoft.sil.portgare.datatypes.DocumentazioneBustaType;
import it.eldasoft.sil.portgare.datatypes.ListaDocumentiType;
import it.eldasoft.sil.portgare.datatypes.TipoPartecipazioneDocument;
import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.QuestionarioType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaDocumenti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.DocumentiAllegatiFirmaBean;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.DocumentiAllegatiHelper;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.EncryptionUtils;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.RiepilogoBustaBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

/**
 * Bean di memorizzazione in sessione dei dati inputati nello step di
 * inserimento dati per i documenti della busta amministrativa, tecnica,
 * economica.
 *
 * @author Marco.Perazzetta
 */
public class WizardDocumentiBustaHelper extends DocumentiAllegatiHelper implements Serializable {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -711416711915050922L;

	private int tipoBusta;
	private String codiceGara;							// codice gara
	private String codice;								// codice lotto per gare A LOTTI, altimenti e' il codice della gara 
	private int operazione;
	private Date dataTermine;
	private boolean docOffertaPresente;
	private boolean datiModificati;
	private String progressivoOfferta;					// progressivo offerta per le gare a lotti
	private String pdfUuid;								// SHA256 del pdf della busta 
	
	/**
	 * Indica che il prodotto &grave; gi&agrave; stato caricato.
	 */
	private boolean alreadyLoaded;

	/**
	 * Data ufficiale NTP di presentazione della richiesta (ha senso solo per gli
	 * invii effettivi, per i salvataggi in bozza deve essere valorizzata ma non
	 * ha alcun senso in quanto viene generata all'atto dell'invio di una
	 * richiesta al backoffice)
	 */
	private Date dataPresentazione;

	/**
	 * Descrizione del bando per elenco fornitori a cui si vuole iscriversi
	 */
	private String descBando;

	/**
	 * Helper per lo step dati impresa
	 */
	private WizardDatiImpresaHelper impresa;

	public int getTipoBusta() {
		return tipoBusta;
	}

	public void setTipoBusta(int tipoBusta) {
		this.tipoBusta = tipoBusta;
	}

	public String getCodiceGara() {
		return codiceGara;
	}

	public void setCodiceGara(String codiceGara) {
		this.codiceGara = codiceGara;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public int getOperazione() {
		return operazione;
	}

	public void setOperazione(int operazione) {
		this.operazione = operazione;
	}

	public boolean isDatiModificati() {
		return datiModificati;
	}

	public void setDatiModificati(boolean datiModificati) {
		this.datiModificati = datiModificati;
	}

	public Date getDataPresentazione() {
		return dataPresentazione;
	}

	public void setDataPresentazione(Date dataPresentazione) {
		this.dataPresentazione = dataPresentazione;
	}

	public boolean isDocOffertaPresente() {
		return docOffertaPresente;
	}

	public void setDocOffertaPresente(boolean docOffertaPresente) {
		this.docOffertaPresente = docOffertaPresente;
	}

	public String getDescBando() {
		return descBando;
	}

	public void setDescBando(String descBando) {
		this.descBando = descBando;
	}

	public WizardDatiImpresaHelper getImpresa() {
		return impresa;
	}

	public void setImpresa(WizardDatiImpresaHelper impresa) {
		this.impresa = impresa;
	}

	public boolean isAlreadyLoaded() {
		return alreadyLoaded;
	}

	public void setAlreadyLoaded(boolean alreadyLoaded) {
		this.alreadyLoaded = alreadyLoaded;
	}

	public Date getDataTermine() {
		return dataTermine;
	}

	public void setDataTermine(Date dataTermine) {
		this.dataTermine = dataTermine;
	}
	
	public byte[] getChiaveCifratura() {
		return chiaveCifratura;
	}

	public void setChiaveCifratura(byte[] chiaveCifratura) {
		this.chiaveCifratura = chiaveCifratura;
	}
	
	public byte[] getChiaveSessione() {
		return chiaveSessione;
	}

	public void setChiaveSessione(byte[] chiaveSessione) {
		this.chiaveSessione = chiaveSessione;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}	
	
	public String getProgressivoOfferta() {
		return progressivoOfferta;
	}

	public void setProgressivoOfferta(String progressivoOfferta) {
		this.progressivoOfferta = progressivoOfferta;
	}

	public QuestionarioType getQuestionarioAssociato() {
		return questionarioAssociato;
	}

	public String getPdfUuid() {
		return pdfUuid;
	}

	public void setPdfUuid(String pdfUuid) {
		this.pdfUuid = pdfUuid;
	}

	/**
	 * costruttore 
	 */
	public WizardDocumentiBustaHelper(int tipoBusta) {
		super(null, null, null, true, true);
		this.codiceGara = null;
		this.codice = null;
		this.impresa = null;
		this.descBando = null;
		this.datiModificati = false;
		this.docOffertaPresente = false;
		this.tipoBusta = tipoBusta;
		this.progressivoOfferta = null;
		this.questionarioAssociato = null;
		this.pdfUuid = null;
		if(this.tipoBusta != PortGareSystemConstants.BUSTA_AMMINISTRATIVA &&
		   this.tipoBusta != PortGareSystemConstants.BUSTA_TECNICA &&
		   this.tipoBusta != PortGareSystemConstants.BUSTA_ECONOMICA &&
		   this.tipoBusta != PortGareSystemConstants.BUSTA_PRE_QUALIFICA) 
		{
			ApsSystemUtils.getLogger().error("WizardDocumentiBustaHelper() Tipologia di busta non valida (" + this.tipoBusta + ")");
		}
	}

	/**
	 * Crea il contenitore da porre in sessione
	 *
	 * @param session la sessione dell'utente loggato
	 * @param codice il codice del bando per gara senza lotti o codice del lottoper gara a lotti 
	 * @param tipoBusta la tipologia di busta su cui si sta lavorando
	 * @param operazione l'operazione voluta
	 * @param dettaglioComunicazione (opzionale) il dettaglio della comununicazione
	 * @return helper da memorizzare in sessione
	 * @throws Throwable 
	 * @throws org.apache.xmlbeans.XmlException
	 */
	public static WizardDocumentiBustaHelper getInstance(
			String codiceGara,
			String codice, 
			int tipoBusta, 
			int operazione,
			String progOfferta,
			DettaglioComunicazioneType dettaglioComunicazione) throws Throwable
	{
		ApsSystemUtils.getLogger().debug(
				"Classe: " + "WizardDocumentiBustaHelper " + " - Metodo: getInstance \n" +  
				"Messaggio: inizio metodo" + 
				"\ncodiceGara=" + codiceGara + ", codice=" + codice + ", tipoBusta=" + tipoBusta +
				"\nprogOfferta=" + progOfferta );
		
		GestioneBuste buste = GestioneBuste.getFromSession();
		BustaDocumenti busta = buste.getBusta(tipoBusta);
		WizardDocumentiBustaHelper helper = busta.getHelperDocumenti();

		if (helper == null || helper.getCodice() == null || !helper.getCodice().equals(codice)) {
			buste = new GestioneBuste(
					dettaglioComunicazione.getChiave1(),
					codiceGara,
					progOfferta,
					operazione);
			buste.get();

			busta = buste.getBusta(tipoBusta);
			helper = busta.getHelperDocumenti();
		}
		
		ApsSystemUtils.getLogger().debug(
				"Classe: " + "WizardDocumentiBustaHelper " + " - Metodo: getInstance \n" +  
				"Messaggio: fine metodo");

		return helper;
	}
	
	/**
	 * Crea il contenitore da porre in sessione 
	 * (utilizza il metodo generalizzato getInstance)
	 * @throws Throwable 
	 */
	public static WizardDocumentiBustaHelper getInstance(
			Map<String, Object> session,
			String codiceGara,
			String codice,
			int tipoBusta, 
			int operazione,
			String progOfferta) throws Throwable 
	{
		return getInstance(codiceGara, codice, tipoBusta, operazione, progOfferta, null);
	}

	/**
	 * restituisce il documento xml associato per l'allegato della comuncazione della busta 
	 */
	public XmlObject getXmlDocument() throws IOException {
		GregorianCalendar dataUfficiale = new GregorianCalendar();
		if (this.dataPresentazione != null) {
			dataUfficiale.setTime(this.dataPresentazione);
		}
		DocumentazioneBustaDocument doc = DocumentazioneBustaDocument.Factory.newInstance();
		DocumentazioneBustaType busta = doc.addNewDocumentazioneBusta();
		busta.setDataPresentazione(dataUfficiale);
		busta.setUuid(this.pdfUuid);
		this.addDocumenti(busta.addNewDocumenti(), true);
		XmlObject document = doc;
		document.documentProperties().setEncoding("UTF-8");
		return document;
	}
	
	/**
	 * ... 
	 */
	public void addDocumenti(ListaDocumentiType listaDocumenti, boolean attachFileContents) 
		throws IOException 
	{
		boolean applicataCifratura = (this.chiaveCifratura != null ? true : false);
		super.addDocumenti(listaDocumenti, attachFileContents, applicataCifratura);
	}
	
	/**
	 * ... 
	 */
	public static TipoPartecipazioneDocument getDocumentoPartecipazione(ComunicazioneType comunicazione) 
		throws ApsException, XmlException 
	{
		TipoPartecipazioneDocument documento = null;
		AllegatoComunicazioneType allegatoPartecipazione = null;
		int i = 0;
		while (comunicazione.getAllegato() != null
			   && i < comunicazione.getAllegato().length
			   && allegatoPartecipazione == null) {
			// si cerca l'xml con i dati dell'iscrizione tra
			// tutti gli allegati
			if (PortGareSystemConstants.NOME_FILE_TIPO_PARTECIPAZIONE.equals(comunicazione.getAllegato()[i].getNomeFile())) {
				allegatoPartecipazione = comunicazione.getAllegato()[i];
			} else {
				i++;
			}
		}
		if (allegatoPartecipazione == null) {
			throw new ApsException("Errors.invioBuste.xmlPartecipazioneNotFound");
		} else {
			// si interpreta l'xml ricevuto
			documento = TipoPartecipazioneDocument.Factory.parse(new String(allegatoPartecipazione.getFile()));
		}
		return documento;
	}

	/**
	 * aggiungi tutte le info relative ad un documento richiesto con un unico metodo
	 * 
	 * @throws GeneralSecurityException 
	 */
	@Override
	public void addDocRichiesto(
		Long id, 
		File file, 
		String contentType, 
		String fileName, 
		Event evento, DocumentiAllegatiFirmaBean checkFirma) throws GeneralSecurityException    
	{
		super.addDocRichiesto(id, file, contentType, fileName, evento, checkFirma);
		this.setDatiModificati(true);
	}
	
	/**
	 * aggiungi tutte le info relative ad un documento richiesto con un unico metodo
	 * 
	 * @throws GeneralSecurityException 
	 * @throws IOException 
	 */
	@Override
	public void addDocRichiesto(
		Long id, 
		byte[] file, 
		String contentType, 
		String fileName, 
		Event evento, DocumentiAllegatiFirmaBean checkFirma) throws GeneralSecurityException, IOException
	{
		super.addDocRichiesto(id, file, contentType, fileName, evento, checkFirma);
		this.setDatiModificati(true);
	}

	/**
	 * rimuovi le info relative ad un documento richiesto con un unico metodo  
	 */
	@Override
	public void removeDocRichiesto(int id) {
		this.setDatiModificati(true);
		super.removeDocRichiesto(id);
	}

	/**
	 * aggiungi tutte le info relative ad un documento ulteriore con un unico metodo
	 * 
	 * @throws GeneralSecurityException 
	 */
	public void addDocUlteriore(
		String descrizione, 
		File file, 
		String contentType, 
		String fileName, 
		Event evento, DocumentiAllegatiFirmaBean checkFirma) throws GeneralSecurityException  
	{
		super.addDocUlteriore(descrizione, file, contentType, fileName, evento, checkFirma);
		this.setDatiModificati(true);
	}	

	/**
	 * aggiungi tutte le info relative ad un documento ulteriore con un unico metodo
	 * 
	 * @throws GeneralSecurityException 
	 * @throws IOException 
	 */
	public void addDocUlteriore(
		String descrizione, 
		byte[] data, 
		String contentType, 
		String fileName,
		String uuid,
		Event evento, DocumentiAllegatiFirmaBean checkFirma) throws GeneralSecurityException, IOException  
	{
		super.addDocUlteriore(descrizione, data, contentType, fileName, uuid, evento, checkFirma);
		this.setDatiModificati(true);
	}	

	/**
	 * rimuovi le info relative ad un documento richiesto con un unico metodo  
	 */
	public void removeDocUlteriore(int id) {
		this.setDatiModificati(true);
		super.removeDocUlteriore(id);
	}

	/**
	 * verifica ed inizializza la chiave di sessione   
	 * @throws ApsException 
	 * @throws GeneralSecurityException 
	 * @throws UnsupportedEncodingException 
	 */
	private void refreshChiaveSessione(BustaDocumenti busta) 
		throws ApsException, UnsupportedEncodingException, GeneralSecurityException 
	{
		IBandiManager bandiManager = busta.getGestioneBuste().getBandiManager();
		
		Long idComunicazione = null;
		String stato = null;
		
		String sessionKey = null;
		if (busta.getId() > 0) {
			idComunicazione = Long.valueOf(busta.getId());
			stato = busta.getComunicazioneFlusso().getDettaglioComunicazione().getStato();
			
			if(busta.getComunicazioneFlusso().getDettaglioComunicazione() != null && 
			   busta.getComunicazioneFlusso().getDettaglioComunicazione().getSessionKey() != null) 
			{
				sessionKey = new String(busta.getComunicazioneFlusso().getDettaglioComunicazione().getSessionKey());
			}
		}
		
		this.setIdComunicazione(idComunicazione);
		
		// NB: verifica se esiste una chiave di cifratura per la busta in CHIAVIBUSTE  
		if(this.getChiaveCifratura() == null) {
			// c'e' la cifratura attiva e non e' presente la chiave di cifratura
			// recupera la chiave di cifratura associata a gara e tipo comunicazione
			this.setChiaveCifratura( 
					bandiManager.getChiavePubblica(this.codiceGara, busta.getComunicazioneFlusso().getTipoComunicazione()) );
		}
		
		// verifica se e' attiva la cifratura della busta
		if (this.getChiaveCifratura() != null) {
			byte[] chiaveSessione = null; 
			if(StringUtils.isEmpty(stato) || CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA.equals(stato)) {
				// decifro la chiave solo se la comunicazione non esiste oppure e' ancora in stato BOZZA
				chiaveSessione = EncryptionUtils.decodeSessionKey(sessionKey, busta.getUsername());	
			} else {
				chiaveSessione = (sessionKey != null ? sessionKey.getBytes() : null);
			}
			this.setChiaveSessione(chiaveSessione);
			this.setUsername(busta.getUsername());
		}
	}

	/**
	 * popola i documenti allegati dal documenti xml, integrando le dimensioni dalla comunicazione
	 *  
	 * @param listaDocumenti
	 * @param comunicazione 
	 * 
	 * @throws IOException 
	 * @throws ApsException 
	 * @throws GeneralSecurityException 
	 */
	public boolean popolaFromXml(
		ListaDocumentiType listaDocumenti,
		BustaDocumenti busta) 
		throws Exception 
	{	
		// NB: 
		// se lo stato della comunicazione e' diverso da BOZZA (1)
		// significa che la busta e' stata inviata e criptata
		// In questo caso non risulta piu' leggibile e cosi' 
		// come la chiave di sessione non e' piu' decifrabile...
		boolean refreshSessionKey = (StringUtils.isEmpty(busta.getComunicazioneFlusso().getStato()) || 
					      			 CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA.equals(busta.getComunicazioneFlusso().getStato()));
		
		// aggiorna la chiave di sessione in caso di cifratura
		if(refreshSessionKey) {
			this.refreshChiaveSessione(busta);
		}

		// aggiorna i documenti dell'helper in base 
		// agli allegati presenti nel documento xml della busta
		boolean popola = false;
		if(listaDocumenti != null && busta.getId() > 0) {
			// NB: 
			// la dimensione degli allegati non viene memorizzata nel documento XML
			// relativo alla ma viene gestita direttamente dal servizio WSOperazioniGenerali
			// che in fase di lettura della comunicazione calcola la dimensione degli allegati
			// direttamente sulla colonna W_DOCDIG.DIGOGG
			//
			// per aggiornare i documenti dell'helper si utilizzano i metodi
			// gia' previsti per inizializzare gli allegati da un documento XML 
			// ...quindi prima si "prepara" un xml aggiornato da utilizzare 
			// successivamente per inizializzare l'helper da un doc XML...
			// Attenzione: 
			// le dimensioni degli allegati non vengono memorizzate nel documento XML
			// ma vengono calcolate dal servizio WSOperazioniGenerali 
			// che le restituisce con la comunicazione richiesta dal metodo
			// WSOperazioniGenerali.getComunicazione(...)
			AllegatoComunicazioneType[] allegati = busta.getComunicazioneFlusso().getComunicazione().getAllegato();
			
			// 1) aggiorna le dimensioni degli allegati nel documento l'XML 
			// con i dati calcolati dal servizio...
			Map<String, Long> uuids = new HashMap<String, Long>();
			for(int i = 0; i < allegati.length; i++) {
				if(allegati[i].getUuid() != null) {
					uuids.put(allegati[i].getUuid(), Long.valueOf(i));
				}
			}
			
			// 2) verifica quali documenti hanno uuid ed aggiorna la dimensione...
			for(int i = 0; i < listaDocumenti.sizeOfDocumentoArray(); i++) {
				String uuid = listaDocumenti.getDocumentoArray(i).getUuid();
				if(uuid == null) {
					// ***** fino alla 1.14.x *****
					// in questo caso allegatiBusta.getDocumentoArray(i).getFile() 
					// dovrebbe contenere lo stream binario dell'allegato
					if(listaDocumenti.getDocumentoArray(i).getDimensione() <= 0 && 
					   listaDocumenti.getDocumentoArray(i).getFile() != null) 
					{
						// correggi eventuali bug di visualizzazione della dimensione...
						listaDocumenti.getDocumentoArray(i).setDimensione( listaDocumenti.getDocumentoArray(i).getFile().length );
					}
				} else {
					// ***** dalla 2.0.0 *****
					Long k = uuids.get(uuid);
					if(k != null && k >= 0) {
						listaDocumenti.getDocumentoArray(i).setDimensione(allegati[k.intValue()].getDimensione());
//							byte[] f = null;
//							if( comunicazione.getAllegato()[k.intValue()].getFile() != null ) {
//								f = Arrays.copyOf( comunicazione.getAllegato()[k.intValue()].getFile(),
//											       comunicazione.getAllegato()[k.intValue()].getFile().length );
//							}
//							allegatiBusta.getDocumentoArray(i).setFile(f);
					}
				}
			}
			
			// 3) popola gli allegati dell'helper dal documento xml...
			super.popolaDocumentiFromXml(
					listaDocumenti, 
					busta.getId());
			
			popola = true;
		}
		
		// (QCompiler/QFORM)
		// verifica se e' presente un questionario e se e' necessario 
		// forzare il salvataggio della comunicazione relativo alla busta
		this.caricaQuestionarioAssociato();
		
		return popola;
	}
	
	/**
	 * Verifica se l'helper e e la action che lo sta utilizzando sono sincronizzati 
	 * se i valori non sono sincronizzati invalida l'esecuzione della action
	 * 
	 * @return true helper e action sono sincronizzati
	 */
	public boolean isSynchronizedToAction(String codice, BaseAction action) {
		boolean synch = false;
		if (StringUtils.isNotEmpty(codice) && StringUtils.isNotEmpty(this.codice)) {
			synch = this.codice.equals(codice);
		}
		if(!synch) {
			// aggiungi un warning al log...
			ApsSystemUtils.getLogger().warn(
					this.getClass().getName(), "isSynchronizedToAction(" + this.codice + "," + codice + ")");
			// aggiungi l'errore alla action...
			if(action != null) {
				action.addActionError(action.getText("Errors.sessionNotSynchronized"));
			}
		}
		return synch;
	}

	/**
	 * (QCompiler/QFORM) verifica se e' presente un questionario associato alla busta in BO
	 * 
	 * @throws ApsException 
	 */
	public static QuestionarioType getQuestionarioAssociatoBO(
			String codice,
			String codiceGara,
			String tipologia, 
			int tipoBusta) 
		throws ApsException 
	{
		IBandiManager bandiManager = (IBandiManager) ApsWebApplicationUtils
			.getBean(PortGareSystemConstants.BANDI_MANAGER, ServletActionContext.getRequest());		
	
		boolean garaLotti = false;
		if(StringUtils.isNotEmpty(codice) && StringUtils.isNotEmpty(codiceGara) &&
		   !codice.equalsIgnoreCase(codiceGara)) {
			garaLotti = true;
		}
		
		// per le gare a lotti in caso di busta tecnica o economica (tipoBusta = 2 o 3) 
		// utilizza il codice lotto, altrimenti utilizza il codice della gara
		String cod = codiceGara;
		if(garaLotti && 
		   (tipoBusta == PortGareSystemConstants.BUSTA_TECNICA || tipoBusta == PortGareSystemConstants.BUSTA_ECONOMICA)) {
			cod = codice;
		}
		
		List<QuestionarioType> q = bandiManager.getQuestionari(
				cod, 
				null, // <-- "tipologia" attualmente non usato!!!
				Integer.toString(tipoBusta));
		
		QuestionarioType questionario = null;
		if(q != null && q.size() > 0) {
			questionario = q.get(0);
		}
		
		return questionario;
	}
	
	/**
	 * (QCompiler/QFORM) verifica se e' presente un questionario e se e' necessario
	 * forzare il salvataggio della comunicazione relativo alla busta 
	 */
	private boolean caricaQuestionarioAssociato() throws ApsException {
		boolean questionarioPresente = false;
		if(this.idComunicazione != null) {
			// verifica se tra gli allegati della comunicazione 
			// esiste gia' il questionario come allegato...
			questionarioPresente = this.isQuestionarioAllegato();
		}
//		if(!questionarioPresente && this.questionarioAssociato == null) {
//			// verifica se esiste un "questionario" in BO associato alla busta
//			// da poter includere tra i documenti della busta
//			this.questionarioAssociato = getQuestionarioAssociatoBO(
//					this.codice, 
//					this.codiceGara, 
//					null,  
//					this.tipoBusta);
//			questionarioPresente = (this.questionarioAssociato != null);
//		}
		// verifica se in BO e' stato associato un questionario...  
		if(this.questionarioAssociato == null) {
			this.questionarioAssociato = getQuestionarioAssociatoBO(
					this.codice, 
					this.codiceGara, 
					null,
					this.tipoBusta);
		}
		return questionarioPresente;
	}

	/**
	 * (QCompiler/QFORM)
	 * verifica se la modulistica del questionario e' cambiata in BO 
	 */
	public boolean isQuestionarioModulisticaVariata() {
		// recupera il riepilogo associato alla busta
		RiepilogoBusteHelper riepilogo = GestioneBuste.getBustaRiepilogoFromSession().getHelper();
		RiepilogoBustaBean busta = riepilogo.getRiepilogoBusta(this.tipoBusta, this.codice);
	
		long idBusta = (busta != null ? busta.getQuestionarioId() : 0);
		long idModello = (this.questionarioAssociato != null ? this.questionarioAssociato.getId() : 0);		
		
		return (idModello != idBusta); 
	}
	
}
