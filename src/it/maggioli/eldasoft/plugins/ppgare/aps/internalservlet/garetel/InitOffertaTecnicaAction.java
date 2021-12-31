package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.eldasoft.sil.portgare.datatypes.AttributoGenericoType;
import it.eldasoft.sil.portgare.datatypes.BustaEconomicaDocument;
import it.eldasoft.sil.portgare.datatypes.BustaTecnicaDocument;
import it.eldasoft.sil.portgare.datatypes.BustaTecnicaType;
import it.eldasoft.sil.portgare.datatypes.DocumentazioneBustaDocument;
import it.eldasoft.sil.portgare.datatypes.DocumentazioneBustaType;
import it.eldasoft.sil.portgare.datatypes.OffertaTecnicaType;
import it.eldasoft.sil.portgare.datatypes.impl.BustaTecnicaDocumentImpl;
import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.CriterioValutazioneOffertaType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.EncryptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StrutsUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.FirmatarioBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.security.SymmetricEncryptionUtils;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.xmlbeans.XmlException;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.opensymphony.xwork2.inject.Inject;

/**
 * Predispone l'apertura del wizard di inserimento di un'offerta tecnica.
 * 
 * @author ...
 */
public class InitOffertaTecnicaAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 4010317624244436869L;

	private Map<String, Object> session;

	/** Store state of StrutsConstants.STRUTS_MULTIPART_SAVEDIR setting. */
	private String multipartSaveDir;
	private IBandiManager bandiManager;
	private IComunicazioniManager comunicazioniManager;

	/** Memorizza lal prossima dispatchAction da eseguire nel wizard. */
	private String nextResultAction;

	/** Codice del bando di gara per il quale gestire l'offerta telematica. */
	private String codice;
	private String codiceGara;
	private String tipoBusta;
	private String operazione;
	private String progressivoOfferta;

	@Override
	public void setSession(Map<String, Object> arg0) {
		this.session = arg0;
	}

	@Inject(StrutsConstants.STRUTS_MULTIPART_SAVEDIR)
	public void setMultipartSaveDir(String multipartSaveDir) {
		this.multipartSaveDir = multipartSaveDir;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public IBandiManager getBandiManager() {
		return bandiManager;
	}

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getCodiceGara() {
		return codiceGara;
	}

	public void setCodiceGara(String codiceGara) {
		this.codiceGara = codiceGara;
	}
	
	public String getTipoBusta() {
		return tipoBusta;
	}

	public void setTipoBusta(String tipoBusta) {
		this.tipoBusta = tipoBusta;
	}

	public String getOperazione() {
		return operazione;
	}

	public void setOperazione(String operazione) {
		this.operazione = operazione;
	}

	public String getProgressivoOfferta() {
		return progressivoOfferta;
	}

	public void setProgressivoOfferta(String progressivoOfferta) {
		this.progressivoOfferta = progressivoOfferta;
	}

	public String getNextResultAction() {
		return nextResultAction;
	}

	/**
	 * Ritorna la directory per i file temporanei, prendendola da
	 * struts.multipart.saveDir (struts.properties) se valorizzata
	 * correttamente, altrimenti da javax.servlet.context.tempdir
	 * 
	 * @return path alla directory per i file temporanei
	 */
	private File getTempDir() {
		return StrutsUtilities.getTempDir(this.getRequest().getSession().getServletContext(), multipartSaveDir);
	}

	/**
	 * Carica in sessione l'helper di gestione dell'offerta tecnica
	 * opportunamente inizializzato, rilegge gli eventuali dati della bozza
	 * inserita, e decide lo step da visualizzare.
	 * 
	 * @return target struts da visualizzare
	 */
	public String initOffertaTecnica() {
		this.setTarget(SUCCESS);

		// "codice" e "codiceGara" vengono messi in sessione per facilitare
    	// la navigazione da pagina a pagina
		this.session.put("codice", this.codice);				// codice del lotto
		this.session.put("codiceGara", this.codiceGara);		// codice della gara

		if (null != this.getCurrentUser()
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			try {
				// predispone il wizard in sessione caricato con tutti i dati previsti
				int tipoOperazione = (StringUtils.isNotEmpty(this.operazione) ? Integer.valueOf(this.operazione) : 0);
				String codGara = (StringUtils.isEmpty(this.codiceGara) ? this.codice : this.codiceGara);
				
    			// ATTENZIONE:
    			// 	 codiceGara == null ==> gara a lotto unico  
    			//   codiceGara != null ==> gara a lotti distinti
//    			boolean isLottoUnico = (this.codiceGara == null);

				WizardOffertaTecnicaHelper helper = getInstance(
						this.session, 
						this.getCurrentUser().getUsername(), 
						codGara,
						this.codice,
						tipoOperazione,
						this.bandiManager,
						this.comunicazioniManager, 
						this.getTempDir());
				
				// (3.2.0) verifica ed integra la busta di riepilogo...
    			InitOffertaEconomicaAction.verificaIntegraRiepilogo(helper, this.session, this);
    			
				// verifica se e' un'offerta con OEPV ed imposto il wizard 
				// per la gestione della busta tecnica, se necessario...
				if(helper.isCriteriValutazioneVisibili()) {
					// busta tecnica con gestione OEPV...
					this.nextResultAction = helper.getNextAction("");	// vai al primo step del wizard
				} else {
					// busta tecnica standard...
					this.nextResultAction = "openPageDocumenti";
				}					
			} catch (XmlException e) {
				ApsSystemUtils.logThrowable(e, this, "initOfferta");
				this.addActionError(this.getText("Errors.offertaTelematica.parseXml"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (IOException e) {
				ApsSystemUtils.logThrowable(e, this, "initOfferta");
				this.addActionError(this.getText("Errors.offertaTelematica.tempFileAllegati"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "initOfferta");
				ExceptionUtils.manageExceptionError(e, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}

		return this.getTarget();
	}

	/**
	 * Ritorna l'helper di gara per un'offerta tecnica; 
	 * restituisce l'oggetto e presente in sessione, altrimenti crea un nuovo 
	 * oggetto e lo inserisce in sessione.
	 * 
	 * @param session
	 *            sessione di lavoro
	 * @param username
	 * 			  username dell'utente loggato
	 * @param codiceGara
	 *            codice della gara
	 * @param codice
	 * 			  codice del lotto
	 * @param bandiManager
	 * @param comunicazioniManager
	 * @param tempDir
	 *  
	 * @return helper esistente in sessione, altrimenti creato nuovo e vuoto
	 * @throws ApsException
	 * @throws IOException
	 * @throws XmlException
	 * @throws DecoderException
	 * @throws GeneralSecurityException  
	 */
	public static WizardOffertaTecnicaHelper getInstance(
			Map<String, Object> session,
			String username, 
			String codiceGara, 
			String codice,
			int operazione,
			IBandiManager bandiManager,
			IComunicazioniManager comunicazioniManager, 
			File tempDir)
		throws ApsException, XmlException, IOException, DecoderException, GeneralSecurityException 
	{
		WizardOffertaTecnicaHelper helper = (WizardOffertaTecnicaHelper) session
			.get(PortGareSystemConstants.SESSION_ID_OFFERTA_TECNICA);
		WizardPartecipazioneHelper wizardPartecipazione = (WizardPartecipazioneHelper) session
			.get(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);
		WizardDatiImpresaHelper datiImpresaHelper = null;
		
		String codiceDaUtilizzare = codice;
		String cod = (helper != null && StringUtils.isNotEmpty(helper.getCodice()) ? helper.getCodice() : "");	
    	//String codgar = (helper != null && helper.getGara() != null && StringUtils.isNotEmpty(helper.getGara().getCodice()) ? helper.getGara().getCodice() : "");
    	
		try {
			if (helper == null || 
				(helper.getGara() == null ||
				 StringUtils.isEmpty(helper.getGara().getCodice()) ||
				 !codiceGara.equals(helper.getGara().getCodice())) ||
				!cod.equals(codice) ||
				isCambiatoLotto(codice, helper, wizardPartecipazione)) 
			{
				// se non esiste oppure non e' quello relativo alla gara in oggetto,
				// lo rigenero e lo ripopolo
				helper = new WizardOffertaTecnicaHelper();
				session.put(PortGareSystemConstants.SESSION_ID_OFFERTA_TECNICA, helper);

				// carica i dati della gara
				DettaglioGaraType dettGara = null;
				if(!wizardPartecipazione.isPlicoUnicoOfferteDistinte()) {
					codiceDaUtilizzare = codiceGara;
					dettGara = bandiManager.getDettaglioGara(codiceGara);
				} else {	    	
					dettGara = bandiManager.getDettaglioGara(codice);
				}
				
				// aggiorna i dati helper.documenti()...
				if(helper.getDocumenti().getChiaveCifratura() == null) {
					helper.getDocumenti().setChiaveCifratura(bandiManager.getChiavePubblica(codiceGara, PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA));
				}
				helper.getDocumenti().setOperazione(operazione);

	    		// recupera il numero dei decimali per il ribasso
	    		// prima verifica se per la gara esiste TORN.PRERIB 
	    		// e se non esiste un valore utilizza quello che 
	    		// si trova nel tabellato "A1028"
	    		Long numDecimaliRibasso = null;
	    		if(dettGara.getDatiGeneraliGara() != null && dettGara.getDatiGeneraliGara().getNumDecimaliRibasso() != null) {
	    			numDecimaliRibasso = dettGara.getDatiGeneraliGara().getNumDecimaliRibasso();
	    		}
	    		if(numDecimaliRibasso == null) {
	    			numDecimaliRibasso = bandiManager.getNumeroDecimaliRibasso();
	    		}
				
				helper.setGara(dettGara.getDatiGeneraliGara());
				helper.getGara().setCodice(codiceGara);
				if(StringUtils.isNotEmpty(codiceGara) && StringUtils.isNotEmpty(codice)) {
	    			// lotti distinti
					helper.setCodice(codice);
				} else {
	    			//helper.setCodice(codiceGara);		// dovrebbe già essere corretto
				}
				helper.setNumDecimaliRibasso(numDecimaliRibasso);
				helper.setStazioneAppaltanteBando(dettGara.getStazioneAppaltante());
				helper.setProgressivoOfferta(wizardPartecipazione.getProgressivoOfferta());
				
				// aggiorna i criteri di valutazione in caso di OEPV
				if( dettGara.getDatiGeneraliGara().isProceduraTelematica() &&
					dettGara.getDatiGeneraliGara().isOffertaTelematica() &&
					dettGara.getDatiGeneraliGara().getModalitaAggiudicazione() == 6 &&
					(dettGara.getListaCriteriValutazione() != null && dettGara.getListaCriteriValutazione().length > 0)) {					
					helper.setListaCriteriValutazione( 
							Arrays.asList(dettGara.getListaCriteriValutazione()) );
				}
	
				// legge i dati dell'impresa, che sicuramente ci sono perche' li ho
				// caricati all'ingresso in gestione buste
				datiImpresaHelper = (WizardDatiImpresaHelper) session
					.get(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA);
	
				helper.setImpresa(datiImpresaHelper);
	
				// calcolo gli step della navigazione
				helper.fillStepsNavigazione();
	
				// nella gara a lotto unico i documenti stanno nel lotto, nella gara
				// ad offerta unica stanno nella gara
				// NB: per le busta tecnica il documento "Offerta tecnica"
	    		//     in caso di gara a lotti, NON viene generato per la gara 
				//     ma per il lotto.
				//     La ricerca dell'id offerta va effettuata nei documenti
				//     della gara e nei documenti del lotto
				List<DocumentazioneRichiestaType> documentiRichiestiDB = null;
				
				if(wizardPartecipazione.isPlicoUnicoOfferteDistinte()) {
					// recupera i documenti della gara..
					List<DocumentazioneRichiestaType> documentiGara = bandiManager
						.getDocumentiRichiestiBandoGara(
							helper.getGara().getCodice(),
							null, 
							datiImpresaHelper.getDatiPrincipaliImpresa().getTipoImpresa(), 
							wizardPartecipazione.isRti(), 
							String.valueOf(PortGareSystemConstants.BUSTA_TECNICA));
					
					// recupera i documenti del lotto...
					List<DocumentazioneRichiestaType> documentiLotto = bandiManager
						.getDocumentiRichiestiBandoGara(
							helper.getGara().getCodice(), 
							codice,  
							datiImpresaHelper.getDatiPrincipaliImpresa().getTipoImpresa(), 
							wizardPartecipazione.isRti(), 
							String.valueOf(PortGareSystemConstants.BUSTA_TECNICA));
					
					documentiRichiestiDB = new ArrayList<DocumentazioneRichiestaType>();
					documentiRichiestiDB.addAll(documentiGara);
					documentiRichiestiDB.addAll(documentiLotto);
				} else {
					documentiRichiestiDB = bandiManager
						.getDocumentiRichiestiBandoGara(
							helper.getGara().getCodice(),
							helper.getGara().getCodice(),
							datiImpresaHelper.getDatiPrincipaliImpresa().getTipoImpresa(), 
							wizardPartecipazione.isRti(), 
							String.valueOf(PortGareSystemConstants.BUSTA_TECNICA));
				}

				// cerca il documento "Offerta tecnica" tra i documenti della busta...
				for (int j = 0; j < documentiRichiestiDB.size() && helper.getIdOfferta() < 0; j++) {
					if (documentiRichiestiDB.get(j).getNome()
							.equalsIgnoreCase(PortGareSystemConstants.DESCRIZIONE_DOCUMENTO_OFFERTA_TECNICA)
						&& (documentiRichiestiDB.get(j).getGenerato() != null && documentiRichiestiDB.get(j).getGenerato() == 1)) {
						helper.setIdOfferta(documentiRichiestiDB.get(j).getId());
					}
				}
	
				// popola l'helper con i dati della comunicazione estratta
				helper.setListaFirmatariMandataria(
						InitOffertaEconomicaAction.composeListaFirmatariMandataria(datiImpresaHelper));
	
				setDatiWizardOfferta(
						comunicazioniManager, 
						username, 
						codiceDaUtilizzare,
						helper, 
						tempDir);			
			}
	
			InitOffertaEconomicaAction.refreshDatiFirmatari(
					helper, 
					codiceGara, 
					wizardPartecipazione,
					username, 
					bandiManager,
					comunicazioniManager);
			
		} catch (Throwable e) {
			session.remove(PortGareSystemConstants.SESSION_ID_OFFERTA_TECNICA);
			helper = null;
			ApsSystemUtils.logThrowable(e, null, "getInstance");
		}
		return helper;
	}
	
	/**
	 * Si carica in sessione l'eventuale bozza salvata in precedenza, oppure la
	 * comunicazione se in stato da processare (questo per il riepilogo solo
	 * solo parte delle comunicazioni &egrave; stata trasmessa).
	 * 
	 * @param target
	 *            target dell'operazione
	 * @param helper
	 *            helper da porre in sessione
	 * @return target aggiornato
	 * 
	 * @throws XmlException
	 * @throws ApsException
	 * @throws IOException
	 * @throws DecoderException
	 * @throws GeneralSecurityException 
	 */
	private static void setDatiWizardOfferta(
			IComunicazioniManager comunicazioniManager, 
			String username,
			String codice,
			WizardOffertaTecnicaHelper helper, 
			File tempDir)
		throws XmlException, ApsException, IOException, DecoderException, GeneralSecurityException 
	{
		// si estraggono dal B.O. i dati dell'offerta economica in bozza
		// --- lotto unico ---
		DettaglioComunicazioneType dettaglioComunicazione = ComunicazioniUtilities
			.retrieveComunicazione(
				comunicazioniManager,
				username,
				codice,
				helper.getProgressivoOfferta(),
				CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
				PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA);
		if (dettaglioComunicazione == null) {
			// potrei aver gia'  inviato la comunicazione
			dettaglioComunicazione = ComunicazioniUtilities.retrieveComunicazione(
					comunicazioniManager,
					username,
					codice,
					helper.getProgressivoOfferta(),
					CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE,
					PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA);
		}

		// --- lotti distinti ---
		if(dettaglioComunicazione == null && StringUtils.stripToNull(helper.getCodice()) != null){
			dettaglioComunicazione = ComunicazioniUtilities.retrieveComunicazione(
					comunicazioniManager,
					username,
					helper.getCodice(),
					helper.getProgressivoOfferta(),
					CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
					PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_TECNICA);
		}

		ComunicazioneType comunicazione = null;
		String sessionKey = null;
		if (dettaglioComunicazione != null) {
			comunicazione = comunicazioniManager.getComunicazione(
					CommonSystemConstants.ID_APPLICATIVO,
					dettaglioComunicazione.getId());
			sessionKey = comunicazione.getDettaglioComunicazione().getSessionKey();
		}
		
		if (helper.getDocumenti().getChiaveCifratura() != null) {
			// genero la chiave se non esiste la comunicazione, altrimenti la decifro
			helper.getDocumenti().setChiaveSessione(EncryptionUtils.decodeSessionKey(sessionKey, username));
			helper.getDocumenti().setUsername(username);
		}
		
		if (dettaglioComunicazione != null) {
			helper.setIdComunicazione(comunicazione.getDettaglioComunicazione().getId());
			BustaTecnicaDocument documento = InitOffertaTecnicaAction.getBustaTecnicaDocument(comunicazione);
			BustaTecnicaType bustaTecnica = documento.getBustaTecnica();
			popolaFromBozza(
					helper, 
					bustaTecnica,
					tempDir);
		}
	}

	/**
	 * Estrae, a partire dalla comunicazione, il documento XML contenente
	 * l'offerta.
	 * 
	 * @param comunicazione
	 *            comunicazione con in allegato i dati dell'offerta
	 * @return oggetto XmlObject 
	 * @throws ApsException
	 * @throws XmlException
	 */
	public static String getBustaTecnicaDocumentXml(ComunicazioneType comunicazione) 
		throws ApsException, XmlException 
	{
		String xml = null;
		AllegatoComunicazioneType allegatoIscrizione = null;
		int i = 0;
		while (comunicazione.getAllegato() != null
				&& i < comunicazione.getAllegato().length
				&& allegatoIscrizione == null) {
			// si cerca l'xml con i dati dell'iscrizione tra
			// tutti gli allegati
			if (PortGareSystemConstants.NOME_FILE_BUSTA.equals(comunicazione.getAllegato()[i].getNomeFile())) {
				allegatoIscrizione = comunicazione.getAllegato()[i];
			} else {
				i++;
			}
		}

		if (allegatoIscrizione == null) {
			// non dovrebbe succedere mai...si inserisce questo
			// controllo per blindare il codice da eventuali
			// comportamenti anomali
			throw new ApsException("Errors.offertaTelematica.xmlOffertaNotFound");
		} else {
			// si interpreta l'xml ricevuto
			xml = new String(allegatoIscrizione.getFile());
		}
		return xml;
	}

	/**
	 * Estrae, a partire dalla comunicazione, il documento XML contenente
	 * l'offerta.
	 * 
	 * @param comunicazione
	 *            comunicazione con in allegato i dati dell'offerta
	 * @return oggetto XmlObject della classe BustaEconomicaDocument
	 * @throws ApsException
	 * @throws XmlException
	 */
	public static BustaTecnicaDocument getBustaTecnicaDocument(
			ComunicazioneType comunicazione) throws ApsException, XmlException 
	{
    	String xml = getBustaTecnicaDocumentXml(comunicazione);
		BustaTecnicaDocument documento = null;
		try {
			documento = BustaTecnicaDocument.Factory.parse(xml);
		} catch(XmlException e) {
			// formato xml errato... converti nel formato corretto !!!
			documento = convertiBustaTecnica(xml, comunicazione);
		}
		// recupera i documenti allegati alla busta...
		ComunicazioniUtilities.getAllegatiBustaFromComunicazione(
				comunicazione, 
				documento.getBustaTecnica().getDocumenti());		
	    return documento;
	}

	/**
	 * Popola l'offerta a partire dalla comunicazione di bozza estratta.
	 * 
	 * @param helper
	 *            helper da popolare
	 * @param bustaEconomica
	 *            dati della busta economica letta nella comunicazione
	 * @param tempDir
	 *            cartella temporanea in cui ricreare i documenti allegati
	 * @param username login utente
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws DecoderException
	 * @throws GeneralSecurityException 
	 */
	private static void popolaFromBozza(
			WizardOffertaTecnicaHelper helper,
			BustaTecnicaType bustaTecnica,
			File tempDir)
		throws IOException, NoSuchAlgorithmException, DecoderException, GeneralSecurityException 
	{
		Cipher cipher = null;
		if (helper.getDocumenti().getChiaveSessione() != null) {
			cipher = SymmetricEncryptionUtils.getDecoder(
					helper.getDocumenti().getChiaveSessione(), 
					helper.getDocumenti().getUsername());
		}
		
		OffertaTecnicaType offerta = bustaTecnica.getOfferta();
			
		// -- Criteri di valutazione --
		List<CriterioValutazioneOffertaType> criteriValutazione = helper.getListaCriteriValutazione();
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
		helper.setListaCriteriValutazione(criteriValutazione);

		if (offerta.isSetUuid()) {
			helper.setPdfUUID(offerta.getUuid());
		}

		// -- Firmatari --
		// NB: SPOSTATO IN "refreshDatiFirmatari" !!!
		
		// -- Documenti --
		InitOffertaEconomicaAction.setDocumenti(
				helper,
				bustaTecnica.getDocumenti(), 
				tempDir);
	}

	private static boolean isCambiatoLotto(
			String codice, 
			WizardOffertaTecnicaHelper helper, 
			WizardPartecipazioneHelper wizardPartecipazione) 
	{
		if(helper != null && wizardPartecipazione != null &&
		   wizardPartecipazione.isPlicoUnicoOfferteDistinte()) {
			return !helper.getCodice().equals(codice);
		}
		return false;
	}

//	private static void refreshDatiFirmatari(
//			Object helper, 
//			String codiceGara,
//			WizardPartecipazioneHelper wizardPartecipazione, 
//			String username,
//			IBandiManager bandiManager,
//			IComunicazioniManager comunicazioniManager) throws ApsException, XmlException 
//	{
//		InitOffertaEconomicaAction.refreshDatiFirmatari(
//				helper, 
//				codiceGara, 
//				wizardPartecipazione, 
//				username, 
//				bandiManager,
//				comunicazioniManager);
//	}
//	
//	public static ArrayList<FirmatarioBean> composeListaFirmatariMandataria(
//			WizardDatiImpresaHelper datiImpresaHelper) {
//		return InitOffertaEconomicaAction.composeListaFirmatariMandataria(datiImpresaHelper);
//	}
	
	/**
	 * converti un xml DocumentazioneBusta in BustaTecnica 
	 * PORTAPPALT-188
	 * @throws XmlException 
	 */
	private static BustaTecnicaDocument convertiBustaTecnica(String xml, ComunicazioneType comunicazione) 
		throws XmlException 
	{
		// formato xml errato... converti nel formato corretto !!!
		ApsSystemUtils.getLogger().warn("Conversione formato busta tecnica " + comunicazione.getDettaglioComunicazione().getChiave2());			
		
		DocumentazioneBustaDocument doc = DocumentazioneBustaDocument.Factory.parse(xml);
		DocumentazioneBustaType bustaSrc = doc.getDocumentazioneBusta();
		
		BustaTecnicaDocument documento = BustaTecnicaDocument.Factory.newInstance();
		BustaTecnicaType bustaDst = documento.addNewBustaTecnica();
		bustaDst.addNewOfferta();
		bustaDst.addNewDocumenti();
		bustaDst.addNewFirmatario();
		
		if(bustaSrc != null && bustaDst != null) {
			// converti da documenti busta a busta tecnica...
			bustaDst.setDataPresentazione(bustaSrc.getDataPresentazione());
			bustaDst.setDocumenti(bustaSrc.getDocumenti());
		}	
		return documento;
	}
	
}
