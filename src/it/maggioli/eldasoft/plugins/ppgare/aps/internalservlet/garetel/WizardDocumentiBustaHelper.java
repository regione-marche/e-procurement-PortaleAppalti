package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;


import it.eldasoft.sil.portgare.datatypes.BustaEconomicaDocument;
import it.eldasoft.sil.portgare.datatypes.BustaEconomicaType;
import it.eldasoft.sil.portgare.datatypes.BustaTecnicaDocument;
import it.eldasoft.sil.portgare.datatypes.BustaTecnicaType;
import it.eldasoft.sil.portgare.datatypes.DocumentazioneBustaDocument;
import it.eldasoft.sil.portgare.datatypes.DocumentazioneBustaType;
import it.eldasoft.sil.portgare.datatypes.ListaDocumentiType;
import it.eldasoft.sil.portgare.datatypes.TipoPartecipazioneDocument;
import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.DocumentiAllegatiHelper;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.EncryptionUtils;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.apsadmin.system.BaseAction;

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
	private String codiceGara;		// codice gara
	private String codice;			// codice gara per gare a senza lotti, codice lotto per gare a lotti
	private int operazione;
	private String nomeOperazione;
	private String nomeIdSessione;
	private String nomeBusta;
	private String tipoComunicazioneBusta;
	private Date dataTermine;
	private boolean docOffertaPresente;
	private boolean datiModificati;
	private String progressivoOfferta;		// progressivo offerta per le gare a lotti
	
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

	public String getNomeIdSessione() {
		return nomeIdSessione;
	}

	public void setNomeIdSessione(String nomeIdSessione) {
		this.nomeIdSessione = nomeIdSessione;
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

	public String getNomeBusta() {
		return nomeBusta;
	}

	public void setNomeBusta(String nomeBusta) {
		this.nomeBusta = nomeBusta;
	}

	public String getTipoComunicazioneBusta() {
		return tipoComunicazioneBusta;
	}

	public void setTipoComunicazioneBusta(String tipoComunicazioneBusta) {
		this.tipoComunicazioneBusta = tipoComunicazioneBusta;
	}

	public boolean isAlreadyLoaded() {
		return alreadyLoaded;
	}

	public void setAlreadyLoaded(boolean alreadyLoaded) {
		this.alreadyLoaded = alreadyLoaded;
	}

	public String getNomeOperazione() {
		return nomeOperazione;
	}

	public void setNomeOperazione(String nomeOperazione) {
		this.nomeOperazione = nomeOperazione;
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

	/**
	 * costruttore 
	 */
	public WizardDocumentiBustaHelper(int tipoBusta) {
		super(null, null, null, true, true);
		this.codiceGara = null;
		this.codice = null;
		this.impresa = null;
		this.descBando = null;
		this.nomeBusta = null;
		this.tipoComunicazioneBusta = null;
		this.datiModificati = false;
		this.docOffertaPresente = false;
		this.tipoBusta = tipoBusta;
		this.progressivoOfferta = null;
		switch (this.tipoBusta) {
		case PortGareSystemConstants.BUSTA_AMMINISTRATIVA:
			this.setNomeIdSessione(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_AMMINISTRATIVA);
			this.setNomeBusta(PortGareSystemConstants.BUSTA_AMM);
			this.setTipoComunicazioneBusta(PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_AMMINISTRATIVA);
			break;
		case PortGareSystemConstants.BUSTA_TECNICA:
			this.setNomeIdSessione(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_TECNICA);
			this.setNomeBusta(PortGareSystemConstants.BUSTA_TEC);
			this.setTipoComunicazioneBusta(PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA);
			break;
		case PortGareSystemConstants.BUSTA_ECONOMICA:
			this.setNomeIdSessione(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_ECONOMICA);
			this.setNomeBusta(PortGareSystemConstants.BUSTA_ECO);
			this.setTipoComunicazioneBusta(PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA);
			break;
		case PortGareSystemConstants.BUSTA_PRE_QUALIFICA:
			this.setNomeIdSessione(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_PRE_QUALIFICA);
			this.setNomeBusta(PortGareSystemConstants.BUSTA_PRE);
			this.setTipoComunicazioneBusta(PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_PRE_QUALIFICA);
			break;
		default:
			ApsSystemUtils.getLogger().error("WizardDocumentiBustaHelper() Tipologia di busta non valida (" + this.tipoBusta + ")");
		}
	}

	public static String getTipoComunicazioneBusta(int tipoBusta) {
		String tc = null;
		switch (tipoBusta) {
		case PortGareSystemConstants.BUSTA_AMMINISTRATIVA:
			tc = PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_AMMINISTRATIVA;
			break;
		case PortGareSystemConstants.BUSTA_TECNICA:
			tc = PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA;
			break;
		case PortGareSystemConstants.BUSTA_ECONOMICA:
			tc = PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA;
			break;
		case PortGareSystemConstants.BUSTA_PRE_QUALIFICA:
			tc = PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_PRE_QUALIFICA;
			break;
		default:
			ApsSystemUtils.getLogger().error("getComunicazioneBusta() Tipologia di busta non valida (" + tipoBusta + ")");
		}
		return tc; 
	}
	
	/**
	 * restiruisce l'helper presente in sessione 
	 */
	public static WizardDocumentiBustaHelper getFromSession(
			Map<String, Object> session, 
			int tipoBusta) throws ApsException 
	{
		WizardDocumentiBustaHelper helper = null;
		switch (tipoBusta) {
		case PortGareSystemConstants.BUSTA_AMMINISTRATIVA:
			if (null != session.get(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_AMMINISTRATIVA)) {
				helper = (WizardDocumentiBustaHelper) session.get(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_AMMINISTRATIVA);
			}
			break;
		case PortGareSystemConstants.BUSTA_TECNICA:
			if (null != session.get(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_TECNICA)) {
				helper = (WizardDocumentiBustaHelper) session.get(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_TECNICA);
			}
			break;
		case PortGareSystemConstants.BUSTA_ECONOMICA:
			if (null != session.get(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_ECONOMICA)) {
				helper = (WizardDocumentiBustaHelper) session.get(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_ECONOMICA);
			}
			break;
		case PortGareSystemConstants.BUSTA_PRE_QUALIFICA:
			if (null != session.get(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_PRE_QUALIFICA)) {
				helper = (WizardDocumentiBustaHelper) session.get(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_PRE_QUALIFICA);
			}
			break;
		default:
			throw new ApsException("Tipologia di busta non valida");
		}
		if (helper != null) {
			helper.setAlreadyLoaded(true);
		}
		return helper;
	}

	/**
	 * Crea il contenitore da porre in sessione
	 *
	 * @param session la sessione dell'utente loggato
	 * @param codice il codice del bando per gara senza lotti o codice del lottoper gara a lotti 
	 * @param tipoBusta la tipologia di busta su cui si sta lavorando
	 * @param operazione l'operazione voluta
	 * @param dettaglioComunicazione (opzionale) il dettaglio della comununicazione
	 * 
	 * @return helper da memorizzare in sessione
	 * 
	 * @throws com.agiletec.aps.system.exception.ApsException
	 * @throws org.apache.xmlbeans.XmlException
	 */
	public static WizardDocumentiBustaHelper getInstance(
			Map<String, Object> session,
			String codiceGara,
			String codice, 
			int tipoBusta, 
			int operazione,
			String progOfferta,
			DettaglioComunicazioneType dettaglioComunicazione) throws ApsException
	{
		ApsSystemUtils.getLogger().debug(
				"Classe: " + "WizardDocumentiBustaHelper " + " - Metodo: getInstance \n" +  
				"Messaggio: inizio metodo" + 
				"\ncodiceGara=" + codiceGara + ", codice=" + codice + ", tipoBusta=" + tipoBusta +
				"\nprogOfferta=" + progOfferta );
		
		WizardDocumentiBustaHelper helper = WizardDocumentiBustaHelper
											.getFromSession(session, tipoBusta);
		if (helper == null || helper.getCodice() == null || !helper.getCodice().equals(codice)) {
			helper = new WizardDocumentiBustaHelper(tipoBusta);
			helper.setCodice(codice);
			helper.setProgressivoOfferta(progOfferta);
			helper.setOperazione(operazione);
			helper.setNomeOperazione( retrieveNomeOperazione(helper.getOperazione()) );
			if(StringUtils.isEmpty(codiceGara) && StringUtils.isNotEmpty(codice)) {
				helper.setCodiceGara(codice);
			} else {
				helper.setCodiceGara(codiceGara);
			}
			
			// in caso di cifratura imposta "username" e "sessionKey" 
			if(dettaglioComunicazione != null) {
				boolean cifratura = (dettaglioComunicazione.getSessionKey() != null);
				if(cifratura) {
					helper.setUsername(dettaglioComunicazione.getChiave1());
					try {
						helper.setChiaveSessione(EncryptionUtils.decodeSessionKey(
								dettaglioComunicazione.getSessionKey(), 
								dettaglioComunicazione.getChiave1()));
					} catch (Throwable ex) {
						new ApsException(ex.getMessage());
					}
				}
			}
		}
		
		// in fase di inizializzazione leggi comunque la chiave di cifratura (chiave pubblica) !!!
		// NB: helper.getCodiceGara e' il codice della gara o del lotto!!!
		if(StringUtils.isNotEmpty(helper.getCodiceGara())) {
			IBandiManager bandiManager = (IBandiManager) ApsWebApplicationUtils
				.getBean(PortGareSystemConstants.BANDI_MANAGER, ServletActionContext.getRequest());
			String tipoComunicazione = helper.getTipoComunicazioneBusta(tipoBusta);
			helper.setChiaveCifratura(bandiManager.getChiavePubblica(helper.getCodiceGara(), tipoComunicazione));
		} else {
			ApsSystemUtils.getLogger().warn(
					helper.getClass().getName(),
					"getInstance(): Chiave pubblica non trovata per " + helper.getCodiceGara() + "!!!");
		}

		ApsSystemUtils.getLogger().debug(
				"Classe: " + "WizardDocumentiBustaHelper " + " - Metodo: getInstance \n" +  
				"Messaggio: fine metodo");

		return helper;
	}
	
	/**
	 * Crea il contenitore da porre in sessione 
	 * (utilizza il metodo generalizzato getInstance)
	 */
	public static WizardDocumentiBustaHelper getInstance(
			Map<String, Object> session,
			String codiceGara,
			String codice,
			int tipoBusta, 
			int operazione,
			String progOfferta) throws ApsException 
	{
		return getInstance(session, codiceGara, codice, tipoBusta, operazione, progOfferta, null);
	}

	/**
	 * ... 
	 */
	public static String retrieveNomeOperazione(int operazione, BaseAction action) throws ApsException {
		String nomeOperazione = null;
		switch (operazione) {
		case PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA:
			nomeOperazione = (action != null 
							  ? action.getI18nLabel("LABEL_GARETEL_PARTECIPAZIONE_GARA")
							  : PortGareSystemConstants.NOME_EVENTO_PARTECIPA_GARA);
			break;
		case PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA:
			nomeOperazione = (action != null 
					  		  ? action.getI18nLabel("LABEL_GARETEL_INVIO_OFFERTA")
					  		  : PortGareSystemConstants.NOME_EVENTO_INVIA_OFFERTA);
			break;			
		case PortGareSystemConstants.TIPOLOGIA_EVENTO_COMPROVA_REQUISITI:
			nomeOperazione = (action != null 
					  		  ? action.getI18nLabel("LABEL_GARETEL_COMPROVA_REQUISITI")
					  		  : PortGareSystemConstants.NOME_EVENTO_COMPROVA_REQUISITI);
			break;
		default:
			throw new ApsException("Tipologia di operazione non valida");
		}
		return nomeOperazione;
	}
	
	public static String retrieveNomeOperazione(int operazione) throws ApsException {
		return retrieveNomeOperazione(operazione, null);
	}
	
	/**
	 * ... 
	 */
	public XmlObject getXmlDocument() throws IOException {
		GregorianCalendar dataUfficiale = new GregorianCalendar();
		if (this.dataPresentazione != null) {
			dataUfficiale.setTime(this.dataPresentazione);
		}
		DocumentazioneBustaDocument doc = DocumentazioneBustaDocument.Factory.newInstance();
		DocumentazioneBustaType busta = doc.addNewDocumentazioneBusta();
		busta.setDataPresentazione(dataUfficiale);
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
	public static DocumentazioneBustaDocument getDocumentoBusta(
			ComunicazioneType comunicazione) 
		throws ApsException, XmlException 
	{
		DocumentazioneBustaDocument documento = null;
		AllegatoComunicazioneType allegatoBusta = null;
		int i = 0;
		while (comunicazione.getAllegato() != null
			   && i < comunicazione.getAllegato().length
			   && allegatoBusta == null) {
			// si cerca l'xml con i dati dell'iscrizione tra
			// tutti gli allegati
			if (PortGareSystemConstants.NOME_FILE_BUSTA.equals(comunicazione.getAllegato()[i].getNomeFile())) {
				allegatoBusta = comunicazione.getAllegato()[i];
			} else {
				i++;
			}
		}
		if (allegatoBusta == null) {
			throw new ApsException("Errors.invioBuste.xmlBustaNotFound");
		} else {
			// si interpreta l'xml ricevuto
			String xml = new String(allegatoBusta.getFile());
			try {
				documento = DocumentazioneBustaDocument.Factory.parse(xml);
			} catch(XmlException e) {
				documento = convertiDocumentazione(xml, comunicazione);
			}
			
			// recupera i documenti allegati alla busta...
			ComunicazioniUtilities.getAllegatiBustaFromComunicazione(
					comunicazione, 
					documento.getDocumentazioneBusta().getDocumenti());
		}
		return documento;
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

	public void setDatiDocumenti(
			IComunicazioniManager comunicazioniManager,
			String username, 
			DettaglioGaraType dettGara, 
			WizardDatiImpresaHelper datiImpresaHelper,
			String tempDir)
		throws ApsException, IOException, XmlException, GeneralSecurityException 
	{
		String tipoComunicazione;
		if (this.getOperazione() == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA) {
			tipoComunicazione = PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_PRE_QUALIFICA;
		} else {
			if (this.getTipoBusta() == PortGareSystemConstants.BUSTA_AMMINISTRATIVA) {
				tipoComunicazione = PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_AMMINISTRATIVA;
			} else if (this.getTipoBusta() == PortGareSystemConstants.BUSTA_TECNICA) {
				tipoComunicazione = PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA;
			} else {
				tipoComunicazione = PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA;
			}
		}

		// si estraggono dal B.O. i dati 
		DettaglioComunicazioneType dettaglioComunicazione = ComunicazioniUtilities
			.retrieveComunicazione(
					comunicazioniManager,
					username,
					this.getCodice(),
					this.getProgressivoOfferta(),
					CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
					tipoComunicazione);

		if (dettaglioComunicazione == null) {
			// potrei aver gia' inviato la comunicazione
			dettaglioComunicazione = ComunicazioniUtilities
				.retrieveComunicazione(
						comunicazioniManager,
						username,
						this.getCodice(),
						this.getProgressivoOfferta(),
						CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE,
						tipoComunicazione);
		}
		
		// recupera la chiave di sessione dalla comunicazione 
		Long idComunicazione = null;
		ComunicazioneType comunicazione = null;
		String sessionKey = null;
		if (dettaglioComunicazione != null) {
			idComunicazione = new Long(dettaglioComunicazione.getId());		
			comunicazione = comunicazioniManager.getComunicazione(
					CommonSystemConstants.ID_APPLICATIVO, 
					idComunicazione);
			if(comunicazione != null && 
			   comunicazione.getDettaglioComunicazione() != null && 
			   comunicazione.getDettaglioComunicazione().getSessionKey()  != null) {
				sessionKey = new String(comunicazione.getDettaglioComunicazione().getSessionKey());
			}
		}

		// NB: se sessionKey è valorizzato significa che la busta è criptata !!!
		//     quindi, se necessario, inizializza la chiave di cifratura 
		//     relativa al tipo di busta
		if((sessionKey != null && this.getChiaveCifratura() == null) &&
		   (PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_PRE_QUALIFICA.equals(tipoComunicazione) ||
  			PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_AMMINISTRATIVA.equals(tipoComunicazione) ||
  			PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA.equals(tipoComunicazione) ||
			PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA.equals(tipoComunicazione))) {
			// c'è la cifratura attiva e non è presente la chiave di cifratura
			// recupera la chiave di cifratura associata a gara e tipo 
			// comunicazione  
			IBandiManager bandiManager = (IBandiManager) ApsWebApplicationUtils
				.getBean(PortGareSystemConstants.BANDI_MANAGER, ServletActionContext.getRequest());

			this.setChiaveCifratura( bandiManager.getChiavePubblica(this.getCodice(), tipoComunicazione) );				
		}

		// verifica se e' attiva la cifratura della busta
		if (this.getChiaveCifratura() != null) {
			// genero la chiave se non esiste la comunicazione, altrimenti la decifro
			this.setChiaveSessione(EncryptionUtils.decodeSessionKey(sessionKey, username));
			this.setUsername(username);
		}
		
		if (dettaglioComunicazione != null) {
			// estraggo le informazioni relative alla comunicazione
			this.setIdComunicazione(idComunicazione);
			
			DocumentazioneBustaDocument document = WizardDocumentiBustaHelper
				.getDocumentoBusta(comunicazione);

			ListaDocumentiType documenti = null;
			if (document != null) {
				documenti = document.getDocumentazioneBusta().getDocumenti();
			}
			//this.popolaDocumentiFromBozza(documenti, idComunicazione);
			this.popolaDocumentiFromComunicazione(documenti, idComunicazione);
		}
		
		this.setImpresa(datiImpresaHelper);
		this.setDescBando(dettGara.getDatiGeneraliGara().getOggetto());
	}

	/**
	 * restituisce la descrizione del tipo di busta 
	 */
	public String getDescTipoBusta() {
		String tipoBusta;
		if(this.getTipoBusta() == PortGareSystemConstants.BUSTA_PRE_QUALIFICA) {
			tipoBusta = PortGareSystemConstants.BUSTA_PRE;
		} else if(this.getTipoBusta() == PortGareSystemConstants.BUSTA_AMMINISTRATIVA) {
			tipoBusta = PortGareSystemConstants.BUSTA_AMM;
		} else if(this.getTipoBusta() == PortGareSystemConstants.BUSTA_TECNICA) {
			tipoBusta = PortGareSystemConstants.BUSTA_TEC;
		} else if(this.getTipoBusta() == PortGareSystemConstants.BUSTA_ECONOMICA) {
			tipoBusta = PortGareSystemConstants.BUSTA_ECO;
		} else {
			tipoBusta = this.getClass().toString();
		}
		return tipoBusta;
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
		Event evento) throws GeneralSecurityException    
	{		
		super.addDocRichiesto(id, file, contentType, fileName, evento);
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
		Event evento) throws GeneralSecurityException  
	{
		super.addDocUlteriore(descrizione, file, contentType, fileName, evento);
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
	 * popola i documenti allegati dalla comunicazione
	 *  
	 * @param documenti
	 * @param idComunicazione
	 * 
	 * @throws IOException 
	 */
	public void popolaDocumentiFromComunicazione(
		ListaDocumentiType documenti, 
		long idComunicazione) throws IOException 
	{
		super.popolaDocumentiFromComunicazione(documenti, idComunicazione);
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
	 * converti un xml BustaEconomica/BustaTecnica in DocumentazioneBusta
	 * PORTAPPALT-188
	 * @throws XmlException 
	 */
	private static DocumentazioneBustaDocument convertiDocumentazione(String xml, ComunicazioneType comunicazione) 
		throws XmlException 
	{
		// formato xml errato... converti nel formato corretto !!!
		ApsSystemUtils.getLogger().warn("Conversione formato documentazione busta " + comunicazione.getDettaglioComunicazione().getChiave2());			
		
		// verifica il tipo di busta (tec/eco)...
		Calendar dataPresentazione = null;
		ListaDocumentiType documenti = null;
		try {
			// busta economica...
			BustaEconomicaDocument doc = BustaEconomicaDocument.Factory.parse(xml);
			BustaEconomicaType bustaSrc = doc.getBustaEconomica();
			if(bustaSrc != null) {
				dataPresentazione = bustaSrc.getDataPresentazione();
				documenti = bustaSrc.getDocumenti();
			}	
		} catch(XmlException e) {
			// busta tecnica
			BustaTecnicaDocument doc = BustaTecnicaDocument.Factory.parse(xml);
			BustaTecnicaType bustaSrc = doc.getBustaTecnica();
			if(bustaSrc != null) {
				dataPresentazione = bustaSrc.getDataPresentazione();
				documenti = bustaSrc.getDocumenti();
			}	
		}
		
		// converti in documentazion busta...
		DocumentazioneBustaDocument documento = DocumentazioneBustaDocument.Factory.newInstance();
		DocumentazioneBustaType bustaDst = documento.addNewDocumentazioneBusta();
		bustaDst.addNewDocumenti();
		
		if(bustaDst != null) {
			bustaDst.setDataPresentazione(dataPresentazione);
			bustaDst.setDocumenti(documenti);
		}

		return documento;
	}

}
