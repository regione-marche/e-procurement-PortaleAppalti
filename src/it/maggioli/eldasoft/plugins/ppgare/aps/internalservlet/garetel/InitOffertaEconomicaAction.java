package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.eldasoft.sil.portgare.datatypes.AttributoGenericoType;
import it.eldasoft.sil.portgare.datatypes.BustaEconomicaDocument;
import it.eldasoft.sil.portgare.datatypes.BustaEconomicaType;
import it.eldasoft.sil.portgare.datatypes.BustaTecnicaDocument;
import it.eldasoft.sil.portgare.datatypes.BustaTecnicaType;
import it.eldasoft.sil.portgare.datatypes.ComponenteDettaglioOffertaType;
import it.eldasoft.sil.portgare.datatypes.DocumentazioneBustaDocument;
import it.eldasoft.sil.portgare.datatypes.DocumentazioneBustaType;
import it.eldasoft.sil.portgare.datatypes.FirmatarioType;
import it.eldasoft.sil.portgare.datatypes.ListaDocumentiType;
import it.eldasoft.sil.portgare.datatypes.OffertaEconomicaType;
import it.eldasoft.sil.portgare.datatypes.RiepilogoBusteOffertaDocument;
import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.AttributoAggiuntivoOffertaType;
import it.eldasoft.www.sil.WSGareAppalto.CriterioValutazioneOffertaType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.eldasoft.www.sil.WSGareAppalto.GaraType;
import it.eldasoft.www.sil.WSGareAppalto.MandanteRTIType;
import it.eldasoft.www.sil.WSGareAppalto.VoceDettaglioOffertaType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.ComponentiRTIList;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.EncryptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StrutsUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CataloghiConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.FirmatarioBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ISoggettoImpresa;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.SoggettoFirmatarioImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.SoggettoImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.helpers.WizardOffertaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.ComponenteHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.IComponente;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.security.SymmetricEncryptionUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.xmlbeans.XmlException;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.agiletec.apsadmin.system.BaseAction;
import com.opensymphony.xwork2.inject.Inject;

/**
 * Predispone l'apertura del wizard di inserimento di un'offerta economica,
 * caricando l'eventuale comunicazione in stato bozza e predisponendo i dati in
 * sessione. Viene effettuato anche il calcolo degli step della navigazione del
 * wizard, dato che lo step dei prezzi unitari &egrave; disponibile solo per
 * determinate tipologie di gara.
 * 
 * @author Stefano.Sabbadin
 */
public class InitOffertaEconomicaAction extends EncodedDataAction implements SessionAware {
    /**
     * UID
     */
    private static final long serialVersionUID = -1862931437661854979L;

    private Map<String, Object> session;

    private String multipartSaveDir;
    private IBandiManager bandiManager;
    private IComunicazioniManager comunicazioniManager;

    /** Memorizza lal prossima dispatchAction da eseguire nel wizard */
    private String nextResultAction;

    /** Codice del bando di gara per il quale gestire l'offerta telematica */
    private String codice;
    private String codiceGara;
    private String tipoBusta;
    private int operazione;
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

    public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
    }
    
    public String getNextResultAction() {
		return nextResultAction;
    }

	public String getCodiceGara() {
		return codiceGara;
	}

	public void setCodiceGara(String codiceGara) {
		this.codiceGara = codiceGara;
	}
	
    public int getOperazione() {
		return operazione;
	}

	public void setOperazione(int operazione) {
		this.operazione = operazione;
	}

	public String getProgressivoOfferta() {
		return progressivoOfferta;
	}

	public void setProgressivoOfferta(String progressivoOfferta) {
		this.progressivoOfferta = progressivoOfferta;
	}

	public String getTipoBusta() {
		return tipoBusta;
	}

	public void setTipoBusta(String tipoBusta) {
		this.tipoBusta = tipoBusta;
	}

	/**
     * Carica in sessione l'helper di gestione dell'offerta economica
     * opportunamente inizializzato, rilegge gli eventuali dati della bozza
     * inserita, e decide lo step da visualizzare.
     * 
     * @return target struts da visualizzare
     */
    public String initOfferta() {
    	this.setTarget(SUCCESS);

    	// "codice" e "codiceGara" vengono messi in sessione per facilitare
    	// la navigazione da pagina a pagina
    	this.session.put("codice", this.codice);
    	this.session.put("codiceGara", this.codiceGara);

    	if (null != this.getCurrentUser()
    		&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
    		try {
    			// predispone il wizard in sessione caricato con tutti i dati
    			// previsti
    			String codGara = (StringUtils.isEmpty(this.codiceGara) ? this.codice : this.codiceGara);
    			
    			// ATTENZIONE:
    			// 	 codiceGara == null ==> gara a lotto unico  
    			//   codiceGara != null ==> gara a lotti distinti
//    			boolean isLottoUnico = (this.codiceGara == null);
    			
    			WizardOffertaEconomicaHelper helper = getInstance(
    					this.session, 
						this.getCurrentUser().getUsername(),
						codGara,
						this.codice,
						this.operazione, 
						this.bandiManager,
						this.comunicazioniManager, 
						this.getTempDir());
    			
    			// (3.2.0) verifica ed integra la busta di riepilogo...
    			InitOffertaEconomicaAction.verificaIntegraRiepilogo(helper, this.session, this);
    			
    			// vai alla prima pagina del wizard
    			this.nextResultAction = helper.getNextAction("");
    			
    		} catch (ApsException e) {
    			ApsSystemUtils.logThrowable(e, this, "initOfferta");
    			ExceptionUtils.manageExceptionError(e, this);
    			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
    		} catch (XmlException e) {
    			// SEPR-173: formato xml della busta non compatibile...
    			// segnala con un messaggio e chiedi di eliminare e rifare la busta
    			ApsSystemUtils.logThrowable(e, this, "initOfferta");
    			this.addActionError(this.getText("Errors.offertaTelematica.parseXml"));
    			//this.setTarget(CommonSystemConstants.PORTAL_ERROR);
    			this.setTarget("ricreabusta");
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
     * Ritorna l'oggetto creato, preso dalla sessione se esistente per la gara
     * in input, creato nuovo e ripopolato altrimenti.
     * 
     * @param session
     *            sessione di lavoro
     * @param codiceGara
     *            codice della gara
     * @return helper esistente in sessione, altrimenti creato nuovo e vuoto
     * @throws ApsException
     * @throws IOException
     * @throws XmlException
     * @throws DecoderException
     * @throws GeneralSecurityException 
     */
    public static WizardOffertaEconomicaHelper getInstance(
	    Map<String, Object> session, 
	    String username, 
	    String codiceGara, 
	    String codice,
	    int operazione,
	    IBandiManager bandiManager,
	    IComunicazioniManager comunicazioniManager, 
	    File tempDir) throws ApsException, XmlException, IOException, DecoderException, GeneralSecurityException 
	{
    	WizardOffertaEconomicaHelper helper = null;
    	Object h = session.get(PortGareSystemConstants.SESSION_ID_OFFERTA_ECONOMICA);    	
    	if("".equals(h)) {
    		helper = null;
    	} else {
    		helper = (WizardOffertaEconomicaHelper) h;
    	}
    	
    	WizardPartecipazioneHelper wizardPartecipazione = (WizardPartecipazioneHelper) session
    		.get(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);
    	WizardDatiImpresaHelper datiImpresaHelper = null;

    	String codiceDaUtilizzare = codice;
    	String cod = (helper != null && StringUtils.isNotEmpty(helper.getCodice()) ? helper.getCodice() : "");	
    	//String codgar = (helper != null && helper.getGara() != null && StringUtils.isNotEmpty(helper.getGara().getCodice()) ? helper.getGara().getCodice() : "");
    	
    	if (helper == null ||
    		(helper.getGara() == null || 
    	     StringUtils.isEmpty(helper.getGara().getCodice()) ||
    		 !helper.getGara().getCodice().equals(codiceGara)) ||
    		!cod.equals(codice) ||
    		isCambiatoLotto(codice, helper, wizardPartecipazione)) 
    	{
    		// se non esiste oppure non e' quello relativo alla gara in oggetto,
    		// lo rigenero e lo ripopolo
    		helper = new WizardOffertaEconomicaHelper();
    		session.put(PortGareSystemConstants.SESSION_ID_OFFERTA_ECONOMICA, helper);

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
    			helper.getDocumenti().setChiaveCifratura(bandiManager.getChiavePubblica(codiceGara, PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA));
    		}
    		if(operazione >= 0) {
    			helper.getDocumenti().setOperazione(operazione);
    		}

    		List<VoceDettaglioOffertaType> vociDettaglio = bandiManager
    			.getVociDettaglioOfferta(codiceDaUtilizzare);
    		List<VoceDettaglioOffertaType> vociDettaglioNoRibasso = bandiManager
    			.getVociDettaglioOffertaNoRibasso(codiceDaUtilizzare);
    		
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
    		
    		helper.setEsistonoVociDettaglioOffertaPrezzi(vociDettaglio.size() > 0);
    		helper.setVociDettaglio(vociDettaglio);
    		helper.setVociDettaglioNoRibasso(vociDettaglioNoRibasso);
    		if (helper.isEsistonoVociDettaglioOffertaPrezzi()) {
    			// si crea il set di prezzi e importi da valorizzare
    			helper.setPrezzoUnitario(new Double[vociDettaglio.size()]);
    			helper.setImportoUnitario(new Double[vociDettaglio.size()]);
    			helper.setAttributiAggiuntivi(bandiManager
    					.getAttributiAggiuntiviOfferta(codiceDaUtilizzare));
    		}

    		// nel caso di voci di dettaglio solo sicurezza o non
    		// soggetto a ribasso il prezzo unitario e' fissato da
    		// backoffice e non editabile
    		for (int i = 0; i < helper.getVociDettaglioNoRibasso().size(); i++) {
    			VoceDettaglioOffertaType voce = helper.getVociDettaglioNoRibasso().get(i);
    			if (voce.getPrezzoUnitario() != null) {
    				BigDecimal bdImportoUnitario = BigDecimal.valueOf(
    						new Double(voce.getQuantita() * voce.getPrezzoUnitario())).setScale(5, BigDecimal.ROUND_HALF_UP);
    				voce.setImportoUnitario(bdImportoUnitario.doubleValue());
    			}
    		}

			// aggiorna i criteri di valutazione in caso di OEPV
			if( dettGara.getDatiGeneraliGara().isProceduraTelematica() &&
				dettGara.getDatiGeneraliGara().isOffertaTelematica() &&
				dettGara.getDatiGeneraliGara().getModalitaAggiudicazione() == 6 &&
				(dettGara.getListaCriteriValutazione() != null && dettGara.getListaCriteriValutazione().length > 0)) 
			{
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
    		// NB: per le busta economica il documento "Offerta economica"
    		//     viene generato sempre per la gara ed è quindi visibile 
    		//     anche per tutti i lotti.
			//     La ricerca dell'id offerta può essere effettuata nei 
    		//     documenti della gara !! 
    		String codiceLotto = null;
    		if (dettGara.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_LOTTO_UNICO) {
    			codiceLotto = dettGara.getDatiGeneraliGara().getCodice();
    		}
    		List<DocumentazioneRichiestaType> documentiRichiestiDB = bandiManager
    			.getDocumentiRichiestiBandoGara(
    				helper.getGara().getCodice(),
    				codiceLotto,
    				datiImpresaHelper.getDatiPrincipaliImpresa().getTipoImpresa(),
    				wizardPartecipazione.isRti(),
    				String.valueOf(PortGareSystemConstants.BUSTA_ECONOMICA));

			for (int j = 0; j < documentiRichiestiDB.size() && helper.getIdOfferta() < 0; j++) {
				if (documentiRichiestiDB.get(j)
    					.getNome().equalsIgnoreCase(PortGareSystemConstants.DESCRIZIONE_DOCUMENTO_OFFERTA_ECONOMICA) 
    				&& (documentiRichiestiDB.get(j).getGenerato() != null && documentiRichiestiDB.get(j).getGenerato() == 1)) 
				{
    				helper.setIdOfferta(documentiRichiestiDB.get(j).getId());
    			}
    		}

    		// popola l'helper con i dati della comunicazione estratta
    		helper.setListaFirmatariMandataria(composeListaFirmatariMandataria(datiImpresaHelper));    		
    	
			setDatiWizardOfferta(
    				comunicazioniManager, 
    				username, 
    				codiceDaUtilizzare,
    				helper, 
    				tempDir);    		
    	}

    	refreshDatiFirmatari(
    			helper, 
    			codiceGara, 
    			wizardPartecipazione,
    			username, 
    			bandiManager,
    			comunicazioniManager);

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
		WizardOffertaEconomicaHelper helper, 
		File tempDir) throws XmlException, ApsException, IOException, DecoderException, GeneralSecurityException 			
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
						PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA);
		if (dettaglioComunicazione == null) {
			// potrei aver gia' inviato la comunicazione
			dettaglioComunicazione = ComunicazioniUtilities
					.retrieveComunicazione(
							comunicazioniManager,
							username,
							codice,
							helper.getProgressivoOfferta(),
							CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE,
							PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA);
		}

		// --- lotti distinti ---
		if(dettaglioComunicazione == null && StringUtils.stripToNull(helper.getCodice()) != null) {
			 dettaglioComunicazione = ComunicazioniUtilities
						.retrieveComunicazione(
								comunicazioniManager,
								username,
								helper.getCodice(),
								helper.getProgressivoOfferta(),
								CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
								PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA);
		}
			
		ComunicazioneType comunicazione = null;
		String sessionKey = null;
		String statoComunicazione = null;
		if (dettaglioComunicazione != null) {
			comunicazione = comunicazioniManager
					.getComunicazione(CommonSystemConstants.ID_APPLICATIVO,
									  dettaglioComunicazione.getId());
			sessionKey = comunicazione.getDettaglioComunicazione().getSessionKey();
			// se esiste la comunicazione allora lo stato e' 1(bozza) oppure 5(da processare)
			statoComunicazione = comunicazione.getDettaglioComunicazione().getStato();
		}
		if ((statoComunicazione == null || CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA.equals(statoComunicazione))
			 && helper.getDocumenti().getChiaveCifratura() != null) {
			// se la busta non esiste (stato nullo), oppure sono in stato di
			// bozza (=1), se viene richiesta la cifratura, allora gestisco
			// ancora la chiave simmetrica, pertanto genero la chiave se non
			// esiste la comunicazione, altrimenti la decifro
			helper.getDocumenti().setChiaveSessione(
					EncryptionUtils.decodeSessionKey(sessionKey, username));
			helper.getDocumenti().setUsername(username);
		}
		
		if (dettaglioComunicazione != null) {
			helper.setIdComunicazione(comunicazione.getDettaglioComunicazione().getId());
			BustaEconomicaDocument documento = getBustaEconomicaDocument(comunicazione);
			BustaEconomicaType bustaEconomica = documento.getBustaEconomica();
			InitOffertaEconomicaAction.popolaFromBozza(
					helper, 
					bustaEconomica,
					tempDir);
		}		
	}

    /**
     * Estrae, a partire dalla comunicazione, il documento XML contenente l'offerta
     * 
     * @param comunicazione
     *            comunicazione con in allegato i dati dell'offerta
     * @return oggetto XmlObject 
     * @throws ApsException
     * @throws XmlException
     */
    public static String getBustaEconomicaDocumentXml(
			ComunicazioneType comunicazione) throws ApsException, XmlException 
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
    public static BustaEconomicaDocument getBustaEconomicaDocument(
			ComunicazioneType comunicazione) throws ApsException, XmlException 
	{
    	String xml = getBustaEconomicaDocumentXml(comunicazione);
    	BustaEconomicaDocument documento = null;
		try {
			documento = BustaEconomicaDocument.Factory.parse(xml);
		} catch(XmlException e) {
			// formato xml errato... converti nel formato corretto !!!
			documento = convertiBustaEconomica(xml, comunicazione);
		}
		// recupera i documenti allegati alla busta...
		ComunicazioniUtilities.getAllegatiBustaFromComunicazione(
				comunicazione, 
				documento.getBustaEconomica().getDocumenti());		
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
    		WizardOffertaEconomicaHelper helper,
    		BustaEconomicaType bustaEconomica,
			File tempDir) 
    	throws IOException, NoSuchAlgorithmException, DecoderException, GeneralSecurityException 
    {
    	Cipher cipher = null;
    	if (helper.getDocumenti().getChiaveSessione() != null) {
			cipher = SymmetricEncryptionUtils.getDecoder(
					helper.getDocumenti().getChiaveSessione(), 
					helper.getDocumenti().getUsername());
    	}
		
    	OffertaEconomicaType offerta = bustaEconomica.getOfferta();

    	// estrazione dati step "prezzi unitari"
    	if (offerta.isSetListaComponentiDettaglio()) {
    		Double[] prezziUnitari = new Double[helper.getVociDettaglio().size()];
    		Double[] importi = new Double[helper.getVociDettaglio().size()];
    		
    		ComponenteDettaglioOffertaType[] componenti = offerta
    			.getListaComponentiDettaglio()
	    		.getComponenteDettaglioArray();
    		
    		Double importoTotale = 0D;
    		SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");

    		// inizializzo la mappa
    		Map<String, String> definizioneAttrAggiuntivi = new HashMap<String, String>();
    		Map<String, String[]> attributiAggiuntivi = new HashMap<String, String[]>();
    		if (helper.getAttributiAggiuntivi() != null) {
    			for (AttributoAggiuntivoOffertaType attr : helper.getAttributiAggiuntivi()) {
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
    			for (int j = 0; j < helper.getVociDettaglio().size() && (indice == -1); j++) {
    				if (helper.getVociDettaglio().get(j).getId() == id) {
    					indice = j;
    				}
    			}
    			
    			// una volta trovato, prendo il peso e il ribasso pesato e li riporto
    			// nei dati per l'helper
    			if (indice != -1) {
    				// prezzo base di gara
    				if (componenti[i].isSetPrezzoUnitarioBaseGara()) {
    					helper.getVociDettaglio().get(indice).setPrezzoUnitarioBaseGara( componenti[i].getPrezzoUnitarioBaseGara() );
    				}
    				
    				// peso
    				if (componenti[i].isSetPeso()) {
    					helper.getVociDettaglio().get(indice).setPeso( componenti[i].getPeso() );
    				}
    				
					// ribasso
    				if (componenti[i].isSetRibasso()) {
    					helper.getVociDettaglio().get(indice).setRibassoPercentuale( componenti[i].getRibasso() );
    				} else if (helper.getDocumenti().getChiaveSessione() != null && componenti[i].isSetRibassoCifrato()) {
    					// caso di cifratura dei dati
    					String ribasso = new String(
								SymmetricEncryptionUtils.translate(cipher, componenti[i].getRibassoCifrato()));
    					helper.getVociDettaglio().get(indice).setRibassoPercentuale( Double.valueOf(ribasso) );
    				}

    				// ribasso pesato  
    				if (componenti[i].isSetRibassoPesato()) {
    					helper.getVociDettaglio().get(indice).setRibassoPesato( componenti[i].getRibassoPesato() );
    				} else if (helper.getDocumenti().getChiaveSessione() != null && componenti[i].isSetRibassoPesatoCifrato()) {
    					// caso di cifratura dei dati
    					String ribassoPesato = new String(
								SymmetricEncryptionUtils.translate(cipher, componenti[i].getRibassoPesatoCifrato()));
    					BigDecimal bdRibassoPesato = BigDecimal.valueOf( Double.valueOf(ribassoPesato) )
														.setScale(helper.getNumDecimaliRibasso().intValue(), BigDecimal.ROUND_HALF_UP);
    					helper.getVociDettaglio().get(indice).setRibassoPesato( bdRibassoPesato.doubleValue() );
    				}
    				
    				// prezzo unitario
    				if (componenti[i].isSetPrezzoUnitario()) {
    					prezziUnitari[indice] = componenti[i].getPrezzoUnitario();
    				} else if (helper.getDocumenti().getChiaveSessione() != null && componenti[i].isSetPrezzoUnitarioCifrato()) {
    					// caso di cifratura dei dati
						String prezzoUnitarioDecifrato = new String(
								SymmetricEncryptionUtils.translate(cipher, componenti[i].getPrezzoUnitarioCifrato()));
    					prezziUnitari[indice] = Double.valueOf(prezzoUnitarioDecifrato);
    				}
    				
    				// importo
    				if (componenti[i].isSetImporto()) {
    					importi[indice] = componenti[i].getImporto();
    					importoTotale += importi[indice];
    				} else if (helper.getDocumenti().getChiaveSessione() != null && componenti[i].isSetImportoCifrato()) {
    					// caso di cifratura dei dati
						String importoDecifrato = new String(
								SymmetricEncryptionUtils.translate(cipher, componenti[i].getImportoCifrato()));
    					importi[indice] = Double.valueOf(importoDecifrato);
    					importoTotale += importi[indice];
    				}

    				// carico i valori degli attributi aggiuntivi
    				AttributoGenericoType[] dettaglioOfferta = componenti[i].getAttributoAggiuntivoArray();
    				for (AttributoGenericoType a : dettaglioOfferta) {
    					String codice = a.getCodice();
    					String[] curValues = attributiAggiuntivi.get(codice);
    					if(curValues != null) {
	    					if (a.isSetValoreData()) {
	    						if(a.getValoreData() != null) {
	    							curValues[indice] = formatoData.format(a.getValoreData().getTime());
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
    		helper.setValoreAttributiAgg(attributiAggiuntivi);
    		helper.setPrezzoUnitario(prezziUnitari);
    		helper.setImportoUnitario(importi);
    		BigDecimal bdImportoTotaleOffertaPrezziUnitari = BigDecimal
    			.valueOf(new Double(importoTotale)).setScale(5, BigDecimal.ROUND_HALF_UP);
    		helper.setTotaleOffertaPrezziUnitari(bdImportoTotaleOffertaPrezziUnitari.doubleValue());
    	}

    	// estrazione dati step "offerta"
    	if (offerta.isSetImportoOfferto()) {
    		helper.setImportoOfferto(offerta.getImportoOfferto());
    	} else if (helper.getDocumenti().getChiaveSessione() != null && offerta.isSetImportoOffertoCifrato()) {
    		// caso di cifratura dei dati
			String importoOffertoDecifrato = new String(
					SymmetricEncryptionUtils.translate(
							cipher, 
							offerta.getImportoOffertoCifrato()));
    		helper.setImportoOfferto(Double.valueOf(importoOffertoDecifrato));
    	}
    	
    	if (offerta.isSetPercentualeAumento()) {
    		helper.setAumento(offerta.getPercentualeAumento());
    	} else if (helper.getDocumenti().getChiaveSessione() != null && offerta.isSetPercentualeAumentoCifrato()) {
    		// caso di cifratura dei dati
			String aumento = new String(
					SymmetricEncryptionUtils.translate(
							cipher, 
							offerta.getPercentualeAumentoCifrato()));
    		helper.setAumento(Double.valueOf(aumento));
    	}
    	
    	if (offerta.isSetPercentualeRibasso()) {
    		helper.setRibasso(offerta.getPercentualeRibasso());
    	} else if (helper.getDocumenti().getChiaveSessione() != null && offerta.isSetPercentualeRibassoCifrato()) {
    		// caso di cifratura dei dati
			String ribasso = new String(
					SymmetricEncryptionUtils.translate(
							cipher, 
							offerta.getPercentualeRibassoCifrato()));
    		helper.setRibasso(Double.valueOf(ribasso));
    	}
    	
    	helper.setCostiSicurezzaAziendali(offerta.getCostiSicurezzaAziendali());
    	helper.setImportoManodopera(offerta.getImportoManodopera());	
    	helper.setPercentualeManodopera(offerta.getPercentualeManodopera());

    	// --- PERMUTA E ASSISTENZA ---
    	helper.setImportoOffertoPerPermuta(offerta.getImportoOffertoPerPermuta());
    	if (helper.getDocumenti().getChiaveSessione() != null && offerta.isSetImportoOffertoPerPermutaCifrato()){
    		// caso di cifratura dei dati
			String importoOffertoPerPermuta = new String(
					SymmetricEncryptionUtils.translate(
							cipher, 
							offerta.getImportoOffertoPerPermutaCifrato()));
    		BigDecimal bdImportoOffertoPerPermuta = BigDecimal
    			.valueOf(new Double(importoOffertoPerPermuta)).setScale(2, BigDecimal.ROUND_HALF_UP);
    		helper.setImportoOffertoPerPermuta(bdImportoOffertoPerPermuta.doubleValue());
    	}

    	helper.setImportoOffertoCanoneAssistenza(offerta.getImportoOffertoPerCanoneAssistenza());
    	if (helper.getDocumenti().getChiaveSessione() != null && offerta.isSetImportoOffertoPerCanoneAssistenzaCifrato()){
    		// caso di cifratura dei dati
    		String importoOffertoCanoneAssistenza = new String(
    				SymmetricEncryptionUtils.translate(
    						cipher, 
    						offerta.getImportoOffertoPerCanoneAssistenzaCifrato()));
    		BigDecimal bdImportoOffertoCanoneAssistenza = BigDecimal
    			.valueOf(new Double(importoOffertoCanoneAssistenza)).setScale(2, BigDecimal.ROUND_HALF_UP);
    		helper.setImportoOffertoCanoneAssistenza(bdImportoOffertoCanoneAssistenza.doubleValue());
		}

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
				criterio.setTipo(PortGareSystemConstants.CRITERIO_ECONOMICO);
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

		// PASSOE
		if(offerta.isSetPassoe()) {
			helper.setPassoe(offerta.getPassoe());
		}
		
    	if (offerta.isSetUuid()) {
    		helper.setPdfUUID(offerta.getUuid());
    	}

    	// firmatari
    	// NB: SPOSTATO IN "refreshDatiFirmatari" !!!
    	    	
    	// estrazione dati step documenti
    	InitOffertaEconomicaAction.setDocumenti(
    			helper,
    			bustaEconomica.getDocumenti(), 
    			tempDir);    	
    }

    /**
     * cerca il firmatario selezionato nella lista dei firmatari disponibili 
     */
    private static void findFirmatarioSelezionatoInLista(
	    FirmatarioType firmatario, 
	    Object helperOfferta) 
    {
		WizardOffertaHelper helper = (WizardOffertaHelper) helperOfferta;
		List<FirmatarioBean> listaFirmatariMandataria = helper.getListaFirmatariMandataria();

		String nominativoFirmatarioRecuperato = firmatario.getCognome() + " " + 
                                                firmatario.getNome();
		boolean firmatarioRecuperatoFound = false;
		for (int i = 0; i < listaFirmatariMandataria.size() && !firmatarioRecuperatoFound; i++) {
		    if (nominativoFirmatarioRecuperato.equalsIgnoreCase(listaFirmatariMandataria.get(i).getNominativo())) {
				firmatarioRecuperatoFound = true;
				helper.setIdFirmatarioSelezionatoInLista(i);
		    }
		}
    }

    /**
     * Popola l'helper con i documenti presenti nella comunicazione analizzata.
     * 
     * @param offertaHelper
     *            helper da popolare
     * @param listaDocumenti
     *            lista dei documenti da popolare
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
	public static void setDocumenti(
		Object offertaHelper,
	    ListaDocumentiType listaDocumenti,
		File tempDir)
		throws IOException, NoSuchAlgorithmException 
	{
		if( !(offertaHelper instanceof WizardOffertaEconomicaHelper 
			  || offertaHelper instanceof WizardOffertaTecnicaHelper) ) {
			// non gestito... esci!!!
			return;
		}

    	// allineo i documenti e i dati generali
		WizardOffertaHelper helper = (WizardOffertaHelper) offertaHelper;
		WizardDocumentiBustaHelper documenti = helper.getDocumenti();
		long idOfferta = helper.getIdOfferta();
		
		documenti.popolaDocumentiFromComunicazione(listaDocumenti, helper.getIdComunicazione());
		
    	// vado a marcare che il documento per l'offerta e' presente se il primo
    	// documento ulteriore e' proprio il documento dell'offerta
    	if (documenti.getDocRichiestiId().size() > 0) {
    		for (int i = 0; i < documenti.getDocRichiestiId().size() && !documenti.isDocOffertaPresente(); i++) {
    			if(documenti.getDocRichiestiId().get(i) != null
    			   && documenti.getDocRichiestiId().get(i).longValue() == idOfferta) {
    				documenti.setDocOffertaPresente(true);
    			}
    		}
    		// si marca che la rigenerazione del pdf non e' necessaria
			helper.setRigenPdf(false);
    	}

    	documenti.setAlreadyLoaded(true);
    }

    /** 
     * compongo la lista dei firmatari per l'impresa mandataria 
     */
	public static ArrayList<FirmatarioBean> composeListaFirmatariMandataria(
	    WizardDatiImpresaHelper datiImpresaHelper) 
	{
    	ArrayList<FirmatarioBean> listaFirmatari = new ArrayList<FirmatarioBean>();
    	if (datiImpresaHelper.isLiberoProfessionista()) {
    		// Se la mandataria è un libero professionista allora solo un
    		// possibile firmatario
    		FirmatarioBean firmatario = new FirmatarioBean();
    		if(datiImpresaHelper.getAltriDatiAnagraficiImpresa().getCognome() != null && datiImpresaHelper.getAltriDatiAnagraficiImpresa().getNome()!= null){
    			firmatario.setNominativo(datiImpresaHelper.getAltriDatiAnagraficiImpresa().getCognome() + " " +datiImpresaHelper.getAltriDatiAnagraficiImpresa().getNome() );
    		} else {
    			firmatario.setNominativo(datiImpresaHelper.getDatiPrincipaliImpresa().getRagioneSociale());
    		}
    		listaFirmatari.add(firmatario);
    	} else {
    		// La mandantaria non è un libero professionita 
    		//  => possibili 1..N firmatari
    		for (int i = 0; i < datiImpresaHelper.getLegaliRappresentantiImpresa().size(); i++) {
    			ISoggettoImpresa soggetto = datiImpresaHelper.getLegaliRappresentantiImpresa().get(i);
    			addSoggettoMandataria(
    					soggetto, 
    					i,
    					CataloghiConstants.LISTA_LEGALI_RAPPRESENTANTI,
    					listaFirmatari);
    		}
    		for (int i = 0; i < datiImpresaHelper.getDirettoriTecniciImpresa().size(); i++) {
    			ISoggettoImpresa soggetto = datiImpresaHelper.getDirettoriTecniciImpresa().get(i);
    			addSoggettoMandataria(
    					soggetto, 
    					i,
    					CataloghiConstants.LISTA_DIRETTORI_TECNICI,
    					listaFirmatari);
    		}
    		for (int i = 0; i < datiImpresaHelper.getAltreCaricheImpresa().size(); i++) {
    			ISoggettoImpresa soggetto = datiImpresaHelper.getAltreCaricheImpresa().get(i);
    			addSoggettoMandataria(
    					soggetto, 
    					i,
    					CataloghiConstants.LISTA_ALTRE_CARICHE, 
    					listaFirmatari);
    		}
    	}
    	return listaFirmatari;
    }

	/**
	 * ... 
	 */
    private static void addSoggettoMandataria(
    		ISoggettoImpresa soggetto,
    		int index, 
    		String lista, 
    		ArrayList<FirmatarioBean> listaFirmatari) 
    {
		if (soggetto.getDataFineIncarico() == null
			&& "1".equals(soggetto.getResponsabileDichiarazioni())) {
		    FirmatarioBean firmatario = new FirmatarioBean();
		    String cognome = StringUtils.capitalize(soggetto.getCognome().substring(0, 1)) + 
		    										soggetto.getCognome().substring(1);
		    String nome = StringUtils.capitalize(soggetto.getNome().substring(0, 1)) +
			    								 soggetto.getNome().substring(1);
		    firmatario.setNominativo(new StringBuilder().append(cognome).append(" ").append(nome).toString());
		    firmatario.setIndex(index);
		    firmatario.setLista(lista);
		    listaFirmatari.add(firmatario);
		}
    }

	/**
	 * refresh dei dati dei firmatari
	 */
	public static void refreshDatiFirmatari(
		Object helperOfferta, 
	    String codiceGara,
	    WizardPartecipazioneHelper wizardPartecipazione, 
	    String username,
	    IBandiManager bandiManager,
	    IComunicazioniManager comunicazioniManager) throws ApsException, XmlException 
	{
		if( !(helperOfferta instanceof WizardOffertaEconomicaHelper 
		      || helperOfferta instanceof WizardOffertaTecnicaHelper) ) {
			// non previsto... esci!!
			return;
		}
		
		WizardOffertaHelper helper = (WizardOffertaHelper) helperOfferta;
		WizardDatiImpresaHelper datiImpresaHelper = helper.getImpresa();
		GaraType gara = helper.getGara();
		List<FirmatarioBean> listaFirmatariMandataria = helper.getListaFirmatariMandataria();
		Long idComunicazione = helper.getIdComunicazione();
		
    	if (gara.getCodice().equals(codiceGara) && wizardPartecipazione != null) {

			ComponentiRTIList rti = null;

    		if (wizardPartecipazione.isRti()) {
    			////////////////////////////////////////////////////////////////
    			// recupera solo la mandataria 
    	    	rti = new ComponentiRTIList();
    	    	
    			// 1) aggiungi la mandataria sempre in posizione 0...
    	    	IComponente mandataria = new ComponenteHelper();
    			if (datiImpresaHelper.isLiberoProfessionista() &&
    				StringUtils.isEmpty(datiImpresaHelper.getDatiPrincipaliImpresa().getPartitaIVA())) {
    			    // se è un libero professionista e partita iva è vuota => usa il codice fiscale
    			    mandataria.setCodiceFiscale(datiImpresaHelper.getDatiPrincipaliImpresa().getCodiceFiscale());
    			} else {
    			    mandataria.setPartitaIVA(datiImpresaHelper.getDatiPrincipaliImpresa().getPartitaIVA());
    			}		
    			mandataria.setRagioneSociale(datiImpresaHelper.getDatiPrincipaliImpresa().getRagioneSociale());
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
    			if (!wizardPartecipazione.isEditRTI()) {
    				mandantiBO = bandiManager.getMandantiRTI(
    						codiceGara, 
    						username, 
    						helper.getProgressivoOfferta());
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
    					rti.add(mandante);
    				} 
    			} else {
    				// RTI in portale appalti (XML)
    				for (int i = 0; i < wizardPartecipazione.getComponenti().size(); i++) {
    					rti.add(wizardPartecipazione.getComponenti().get(i));
    				}
    			}
    			
    			////////////////////////////////////////////////////////////////
    	    	// aggiorna i firmatari...
    			FirmatarioType[] firmatari = null;
    			
    			// recupera i firmatari nella busta dell'offerta (tecnica o economica)...
    			if(idComunicazione != null && idComunicazione > 0) {
    				ComunicazioneType comunicazione = comunicazioniManager.getComunicazione(
    						CommonSystemConstants.ID_APPLICATIVO, 
    						idComunicazione);
    				
    				if(helperOfferta instanceof WizardOffertaEconomicaHelper) {
    					BustaEconomicaDocument doc = InitOffertaEconomicaAction.getBustaEconomicaDocument(comunicazione);
    					BustaEconomicaType busta = doc.getBustaEconomica();
    					firmatari = busta.getFirmatarioArray();
    				} else if(helperOfferta instanceof WizardOffertaTecnicaHelper) {
    					BustaTecnicaDocument doc = InitOffertaTecnicaAction.getBustaTecnicaDocument(comunicazione);
    					BustaTecnicaType busta = doc.getBustaTecnica();
    					firmatari = busta.getFirmatarioArray();
    				}
    			}
    			
    			// se nella comunicazione non ci sono firmatari 
				// verifica se ci sono dei firmatari nella busta di riepilogo FS11R...
				if(firmatari == null) {
					DettaglioComunicazioneType dettComunicazione = ComunicazioniUtilities.retrieveComunicazione(
							comunicazioniManager,
							username,
							codiceGara, 
							wizardPartecipazione.getProgressivoOfferta(),
							CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
							PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_RIEPILOGO);
					
					ComunicazioneType comunicazione = null;
					if(dettComunicazione != null) {
						comunicazione = comunicazioniManager.getComunicazione(
								CommonSystemConstants.ID_APPLICATIVO, 
								dettComunicazione.getId());
					}
					
					// estrai l'xml della busta di riepilogo...  
					String xml = null;
					if(comunicazione != null) {
						int i = 0;
						while (comunicazione.getAllegato() != null
								&& i < comunicazione.getAllegato().length) {
							if (PortGareSystemConstants.NOME_FILE_BUSTA_RIEPILOGATIVA.equals(comunicazione.getAllegato()[i].getNomeFile())) {
								xml = new String(comunicazione.getAllegato()[i].getFile());
								break;
							}
							i++;
						}
					}
					
					// estrai i firmatari dalla busta di riepilogo...
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
						findFirmatarioSelezionatoInLista(unicaImpresa, helper);							
					} else {
						if (firmatari.length == 1) {
							// CASO 1 : Firmatario unico
							if (firmatari.length == 1) {
								FirmatarioType firmatarioRecuperato = firmatari[0];
								findFirmatarioSelezionatoInLista(firmatarioRecuperato, helper);
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
									if (datiImpresaHelper.isLiberoProfessionista()) {    						
										//firmatario.setNome(firmatari[i].getRagioneSociale());
										//firmatario.setNominativo(firmatari[i].getRagioneSociale());
										firmatario.setNome(datiImpresaHelper.getAltriDatiAnagraficiImpresa().getNome());
										firmatario.setCognome(datiImpresaHelper.getAltriDatiAnagraficiImpresa().getCognome());
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
									DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
									Date c = firmatari[i].getDataNascita().getTime();
									firmatario.setDataNascita(df.format(c));
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

				helper.setComponentiRTI(rti);
    		}

    		if(rti != null && rti.size() > 0) {
    			// cerca l'id della lista a cui è associato il firmatario
    			boolean firmatarioIndividuato = false;
    			SoggettoImpresaHelper mandataria = rti.getFirmatario(datiImpresaHelper.getDatiPrincipaliImpresa());
				
    			if(mandataria != null) {
	    			for (int i = 0; i < listaFirmatariMandataria.size() && !firmatarioIndividuato; i++) {
						if (listaFirmatariMandataria.get(i).getNominativo().equalsIgnoreCase(mandataria.getNominativo())) {
							firmatarioIndividuato = true;
							helper.setIdFirmatarioSelezionatoInLista(i);
						}
	    			}
    			}
    		}
    	}
    }
	
	/**
	 * ... 
	 */
	private static boolean isCambiatoLotto(
			String codice, 
			WizardOffertaEconomicaHelper helper, 
			WizardPartecipazioneHelper wizardPartecipazione) 
	{
		if(helper != null && 
		   helper.getCodice() != null &&
		   wizardPartecipazione != null && 
		   wizardPartecipazione.isPlicoUnicoOfferteDistinte()) {
			return !helper.getCodice().equals(codice);
		}
		return false;
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
	 * verifica i documenti della busta di riepilogo ed integra le informazioni mancanti 
	 * 
	 * @throws ApsException 
	 */
	public static void verificaIntegraRiepilogo(
		Object offertaHelper,
		Map<String, Object> session,
		BaseAction action) throws ApsException 
	{	
		if( !(offertaHelper instanceof WizardOffertaEconomicaHelper 
			  || offertaHelper instanceof WizardOffertaTecnicaHelper) ) {
			// non gestito... esci!!!
			return;
		}
		WizardOffertaHelper helper = (WizardOffertaHelper) offertaHelper; 
		WizardDocumentiBustaHelper documenti = helper.getDocumenti();
		
		RiepilogoBusteHelper bustaRiepilogativa = (RiepilogoBusteHelper) session
			.get(PortGareSystemConstants.SESSION_ID_DETT_BUSTA_RIEPILOGATIVA);
		WizardDatiImpresaHelper impresaHelper = (WizardDatiImpresaHelper) session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA);

		if(bustaRiepilogativa != null && impresaHelper != null) {
			String codiceLotto = helper.getCodice();

			// verifica i documenti del riepilogo...
			bustaRiepilogativa.verificaIntegraDatiDocumenti(documenti, codiceLotto);
			
			// se necessario aggiorna la busta di riepilogo...
			if(bustaRiepilogativa.isModified() 
			   && bustaRiepilogativa.getIdComunicazione() != null) {
				
				// recupera la comunicazione relativa alla busta di riepilogo...
				IComunicazioniManager comunicazioniManager = (IComunicazioniManager) ApsWebApplicationUtils
					.getBean(PortGareSystemConstants.COMUNICAZIONI_MANAGER,
							 ServletActionContext.getRequest());
				
				ComunicazioneType comunicazione = comunicazioniManager.getComunicazione(
						CommonSystemConstants.ID_APPLICATIVO, 
						bustaRiepilogativa.getIdComunicazione());
						
				// creazione e invio della comunicazione FS10R/FS11R 
				// con allegato l'xml di riepilogo delle varie buste/lotti
				
				bustaRiepilogativa.sendComunicazioneBusta(
						comunicazione.getDettaglioComunicazione().getStato(),
						impresaHelper, 
						comunicazione.getDettaglioComunicazione().getChiave1(), 	// username
						comunicazione.getDettaglioComunicazione().getChiave2(), 	// codice gara
						comunicazione.getDettaglioComunicazione().getMittente(),	// ragione sociale
						comunicazioniManager,
						PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_RIEPILOGO,
						action);
			}
		}
	}
	
	/**
	 * conferma per elimina e ricrea della busta economica (SEPR-173) 
	 */
	public String confermaRicreaBusta() {
		this.setTarget(SUCCESS);
		return this.getTarget();
	}
	
	/**
	 * elimina e ricrea la busta economica (SEPR-173) 
	 */
	public String ricreaBusta() {
		this.setTarget(SUCCESS);
		
		try {
			WizardPartecipazioneHelper wizardPartecipazione = (WizardPartecipazioneHelper) session
				.get(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);
		
			String codGara = this.codice;
			if(!wizardPartecipazione.isPlicoUnicoOfferteDistinte()) {
				if(StringUtils.isNotEmpty(this.codiceGara)) {
					codGara = this.codiceGara;
				}
			}
			
			if(StringUtils.isNotEmpty(codGara)) {
				DettaglioComunicazioneType dettComunicazione = ComunicazioniUtilities
					.retrieveComunicazione(
							this.comunicazioniManager,
							this.getCurrentUser().getUsername(),
							codGara,
							wizardPartecipazione.getProgressivoOfferta(),
							CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA,
							PortGareSystemConstants.RICHIESTA_TIPO_BUSTA_ECONOMICA);
				
				if(dettComunicazione != null) {
					this.comunicazioniManager.deleteComunicazione(
							CommonSystemConstants.ID_APPLICATIVO, 
							dettComunicazione.getId());
				}
			}
		} catch (Exception e) {
			ApsSystemUtils.getLogger().error("confermaRicreaBusta", e);
		}
		
		return this.getTarget();
	}
	
	/**
	 * converti un xml DocumentazioneBusta in BustaEconomica
	 * PORTAPPALT-188
	 * @throws XmlException 
	 */
	private static BustaEconomicaDocument convertiBustaEconomica(String xml, ComunicazioneType comunicazione) 
		throws XmlException 
	{
		// formato xml errato... converti nel formato corretto !!!
		ApsSystemUtils.getLogger().warn("Conversione formato busta economica " + comunicazione.getDettaglioComunicazione().getChiave2());			
		
		DocumentazioneBustaDocument doc = DocumentazioneBustaDocument.Factory.parse(xml);
		DocumentazioneBustaType bustaSrc = doc.getDocumentazioneBusta();
		
		BustaEconomicaDocument documento = BustaEconomicaDocument.Factory.newInstance();
		BustaEconomicaType bustaDst = documento.addNewBustaEconomica();
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
