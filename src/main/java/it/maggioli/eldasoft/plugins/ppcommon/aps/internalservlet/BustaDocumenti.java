package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.apsadmin.system.BaseAction;
import it.eldasoft.sil.portgare.datatypes.BustaEconomicaDocument;
import it.eldasoft.sil.portgare.datatypes.BustaEconomicaType;
import it.eldasoft.sil.portgare.datatypes.BustaTecnicaDocument;
import it.eldasoft.sil.portgare.datatypes.BustaTecnicaType;
import it.eldasoft.sil.portgare.datatypes.DocumentazioneBustaDocument;
import it.eldasoft.sil.portgare.datatypes.DocumentazioneBustaType;
import it.eldasoft.sil.portgare.datatypes.ListaDocumentiType;
import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.CriterioValutazioneOffertaType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.eldasoft.www.sil.WSGareAppalto.LottoDettaglioGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.Attachment;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.EncryptionUtils;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.WizardDocumentiBustaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.WizardOffertaTecnicaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.beans.RiepilogoBustaBean;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.security.PGPEncryptionUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;


/**
 * generica busta con documenti allegati per una partecipazione o offerta di gara
 */
public class BustaDocumenti extends BustaGara implements HttpSessionBindingListener {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 5881082117048066847L;
		
	protected WizardDocumentiBustaHelper helperDocumenti;
	protected List<DocumentazioneRichiestaType> documentiRichiestiDB;
	protected List<DocumentazioneRichiestaType> documentiMancantiDB;
	protected List<DocumentazioneRichiestaType> documentiInseritiDB;
	
	public WizardDocumentiBustaHelper getHelperDocumenti() {
		return helperDocumenti;
	}

	public void setHelperDocumenti(WizardDocumentiBustaHelper helperDocumenti) {
		this.helperDocumenti = helperDocumenti;
	}
	
	public List<DocumentazioneRichiestaType> getDocumentiMancantiDB() {
		return documentiMancantiDB;
	}

	public List<DocumentazioneRichiestaType> getDocumentiInseritiDB() {
		return documentiInseritiDB;
	}

	public List<DocumentazioneRichiestaType> getDocumentiRichiestiDB() {
		return documentiRichiestiDB;
	}

	/**
	 * costruttore
	 * @throws Throwable 
	 */
	public BustaDocumenti(
			GestioneBuste buste,
			String tipoComunicazione,
			int tipoBusta) throws Throwable 
	{
		super(buste, null, tipoBusta);
		
		this.comunicazioneFlusso.getDettaglioComunicazione().setTipoComunicazione(tipoComunicazione);
		this.comunicazioneFlusso.getDettaglioComunicazione().setTipoBusta(new Long(tipoBusta)); 

		// recupera l'elenco dei documenti richiesti, mancanti ed inseriti...
		this.documentiRichiestiDB = new ArrayList<DocumentazioneRichiestaType>();
		this.documentiMancantiDB = new ArrayList<DocumentazioneRichiestaType>();
		this.documentiInseritiDB = new ArrayList<DocumentazioneRichiestaType>();
						
		this.helperDocumenti = null;
		//this.initHelper();
	}
	
	@Override
	public void valueBound(HttpSessionBindingEvent arg0) {
	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent event) {
		GestioneBuste.traceLog("BustaDocumenti.valueUnbound " + comunicazioneFlusso.getTipoComunicazione() + " " + codiceGara + " " + codiceLotto);
		if(helperDocumenti != null) {
			helperDocumenti.valueUnbound(event);
		}
	}
	
	/**
	 * invia la busta al servizio con un determinato stato 
	 */
	protected boolean send(
			XmlObject xmlDocument,
			WizardDocumentiBustaHelper documenti,
			String stato) 
		throws Throwable 
	{
		GestioneBuste.traceLog("BustaDocumenti.send " + this.comunicazioneFlusso.getTipoComunicazione() + 
							   " " +(StringUtils.isNotEmpty(this.codiceLotto) ? this.codiceLotto : this.codiceGara) +
				               " idCom=" + this.comunicazioneFlusso.getId() + " stato=" + stato);
		boolean continua = true;
		
		BaseAction action = GestioneBuste.getAction();
		
		// Invia la comunicazione con i documenti della busta
		// e riallinea il riepilogo ed invia la comunicazione del riepilogo 
		// traccia l'evento di salvataggio...
		Event evento = new Event();
		evento.setUsername(this.username);
		evento.setDestination(documenti.getCodice());
		evento.setLevel(Event.Level.INFO);
		evento.setEventType(PortGareEventsConstants.SALVATAGGIO_COMUNICAZIONE);
		evento.setIpAddress(action.getCurrentUser().getIpAddress());
		evento.setSessionId(action.getRequest().getSession().getId());
		// NB: piu' avanti il messaggio viene riaggiornato con id della comunicazione
		evento.setMessage("Salvataggio comunicazione " + this.comunicazioneFlusso.getTipoComunicazione()
						  + " in stato " + stato);
		
		try {
			synchronized (this) {
				Date dataAttuale = new Date();
				documenti.setDataPresentazione(dataAttuale);
	
				// FASE 1:
				String ragioneSociale = documenti.getImpresa().getDatiPrincipaliImpresa().getRagioneSociale();
				String ragioneSociale200 = StringUtils.left(ragioneSociale, 200);
				String oggetto = MessageFormat.format(
						action.getI18nLabelFromDefaultLocale("NOTIFICA_GARETEL_OGGETTO"), 
						new Object[] { this.getDescrizioneBusta(), documenti.getCodice() });
				String testo = MessageFormat.format(
						action.getI18nLabelFromDefaultLocale("NOTIFICA_GARETEL_TESTO"), 
						new Object[] { ragioneSociale200, this.getDescrizioneBusta() });
				String descrizioneFile = MessageFormat.format(
						action.getI18nLabelFromDefaultLocale("NOTIFICA_GARETEL_ALLEGATO_DESCRIZIONE"), 
						new Object[] { this.getDescrizioneBusta() });
				
				// FASE 2: 
				// popolamento della testata
				DettaglioComunicazioneType dettaglioComunicazione = this.comunicazioneFlusso.getDettaglioComunicazione();
				dettaglioComunicazione.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
				if(dettaglioComunicazione.getId() == null) {
					dettaglioComunicazione.setId(documenti.getIdComunicazione());
				}
				dettaglioComunicazione.setChiave1(this.username);
				dettaglioComunicazione.setChiave2(documenti.getCodice());	// ???this.codiceGara
				dettaglioComunicazione.setChiave3(this.progressivoOfferta);
				dettaglioComunicazione.setMittente(StringUtils.left(ragioneSociale, 60));
				if(StringUtils.isNotEmpty(stato)) {
					dettaglioComunicazione.setStato(stato);
				}
				dettaglioComunicazione.setOggetto(oggetto);
				dettaglioComunicazione.setTesto(testo);
				dettaglioComunicazione.setTipoComunicazione(this.comunicazioneFlusso.getTipoComunicazione());
				dettaglioComunicazione.setComunicazionePubblica(false);
				dettaglioComunicazione.setDataPubblicazione(null);
				// aggiorna la chiave di sessione
				if(documenti.getChiaveSessione() != null) {
					dettaglioComunicazione.setSessionKey(
							EncryptionUtils.encodeSessionKey(documenti.getChiaveSessione(), this.username));
				}
				
				// FASE 3:
				// aggiungi gli allegati alla comunicazione ed invia la busta al servizio
				// ed invia la comunicazione al servizio...
				// prima di inviare 
	//			DocumentazioneBustaDocument doc = (DocumentazioneBustaDocument) xmlDocument;
	//			this.setDimensioneAllegatiFromComunicazione(
	//					this.comunicazioneFlusso.getComunicazione(),
	//					doc.getDocumentazioneBusta().getDocumenti());
				List<AllegatoComunicazioneType> allegati = new ArrayList<AllegatoComunicazioneType>();
				documenti.documentiToAllegatiComunicazione(allegati);
				
				Long id = this.comunicazioneFlusso.send(
						xmlDocument,
						descrizioneFile,
						allegati);
				
				continua = (id != null && id.longValue() > 0);
				
				// FASE 4:
				// riallinea ed invia la busta di riepilogo FS10R/FS11R
				if(continua) {
					continua = this.gestioneBuste.getBustaRiepilogo().send(this);
				}
				
				// FASE 5: 
				// completa l'invio 
				documenti.setIdComunicazione(id);
				if(continua) {
					// aggiorna lo stato dell'helper dei documenti e lo stato di invio
					documenti.resetStatiInvio( this.comunicazioneFlusso.getComunicazione() );
					documenti.setDatiModificati(false);
				
					if("99".equals(stato)) {
						// reset della busta
						evento.setMessage(String.format(action.getText("Event.reset.envelope")
        												, comunicazioneFlusso.getTipoComunicazione()
        												, comunicazioneFlusso.getId()
										  ));
					} else {
						evento.setMessage("Salvataggio comunicazione " + this.comunicazioneFlusso.getTipoComunicazione()
		  								  + " con id " + comunicazioneFlusso.getId() 
		  								  + " in stato " + stato);
					}
					
					// QUESTIONARI
					// NB: se e' presente la gestione di un questionario
					// il flusso viene ridiretto ad un'altra action che
					// recupera l'helper documenti dalla sessione e quindi 
					// l'helper va rimesso in sessione!!! 
					if(documenti.isGestioneQuestionario())
						this.gestioneBuste.putToSession();
				}
				
				// se lo stato della busta e' 99 (reset/eliminata)
				// si ricarica la busta come fosse vuota...
				if("99".equals(stato)) {
					this.comunicazioneFlusso.reset();
					get();
				}
			}
		} catch (ApsException e) {
			continua = false;
			evento.setError(e);
			throw e;
		} catch (IOException e) {
			continua = false;
			evento.setError(e);
			throw e;
		} catch (GeneralSecurityException e) {
			continua = false;
			evento.setError(e);
			throw e;
		} catch (OutOfMemoryError e) {
			continua = false;
			evento.setError(e);
			throw e;
		} catch (Throwable t) {
			continua = false;
			evento.setError(t);
			throw t;
		} finally {
			this.gestioneBuste.getEventManager().insertEvent(evento);
		}
		
		return continua;
	}

	/**
	 * invia la busta al servizio con un determinato stato 
	 */
	public boolean send(String stato) throws Throwable {
		GestioneBuste.traceLog("BustaDocumenti.send");
		this.helperDocumenti.correggiDocumentiRichiestiConBO(documentiRichiestiDB);
		return this.send(this.helperDocumenti.getXmlDocument(), this.helperDocumenti, stato);
	}
	
	/**
	 * invia la busta al servizio 
	 */
	@Override
	protected boolean send() throws Throwable {
		return this.send(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
	}

	/**
	 * conferma ed invia la busta al servizio 
	 */
	public boolean sendConfirm(String stato) throws Throwable {
		GestioneBuste.traceLog("BustaDocumenti.sendConfirm " + this.comunicazioneFlusso.getTipoComunicazione() + 
				   " " +(StringUtils.isNotEmpty(this.codiceLotto) ? this.codiceLotto : this.codiceGara) +
	               " idCom=" + this.comunicazioneFlusso.getId() + " stato=" + stato);
		boolean continua = true;
		
		BaseAction action = GestioneBuste.getAction();
		IBandiManager bandiManager = this.gestioneBuste.getBandiManager();
		IComunicazioniManager comunicazioniManager = this.gestioneBuste.getComunicazioniManager();
		IEventManager eventManager = this.gestioneBuste.getEventManager();

		// aggiornamento stato + cifratura della sessionKey	
		// in presenza di cifratura si ricava la chiave di sessione 
		// e la si cifra in modo irreversibile con la chiave pubblica
		// e se la comunicazione e' in stato BOZZA (1)
		if(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA.equals(comunicazioneFlusso.getDettaglioComunicazione().getStato())) {
			try {
				this.comunicazioneFlusso.getDettaglioComunicazione().setStato(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
				
				// recupera dalla tabella CHIAVIBUSTE la chiave pubblica associata alla busta
				byte[] chiavePubblica = bandiManager.getChiavePubblica(
						this.codiceGara, 
						this.comunicazioneFlusso.getTipoComunicazione());
				
				if (chiavePubblica != null) {
					GestioneBuste.traceLog("BustaDocumenti.send cifratura della chiave di sessione...");
					
					byte[] sessionKey = EncryptionUtils.decodeSessionKey(
							this.comunicazioneFlusso.getDettaglioComunicazione().getSessionKey(), 
							this.username);
					
					this.comunicazioneFlusso.getDettaglioComunicazione().setSessionKey(
							Base64.encodeBase64String(PGPEncryptionUtils.encrypt(new ByteArrayInputStream(chiavePubblica), sessionKey)));
					
					GestioneBuste.traceLog("BustaDocumenti.send chiave di sessione cifrata");
				}
			} catch (Throwable t) {
				continua = false;
				ApsSystemUtils.getLogger().error("sendConfirm ", t);
			}
		
			if (continua) {
				// aggiorna lo stato della comunicazione...
				Event evento = new Event();
				evento.setUsername(this.username);
				evento.setDestination( StringUtils.isNotEmpty(this.codiceLotto) ? this.codiceLotto : this.codiceGara );
				evento.setMessage("Aggiornamento comunicazione con id " + this.comunicazioneFlusso.getId() + 
								  " allo stato " + stato);
				evento.setLevel(Event.Level.INFO);
				evento.setIpAddress(action.getCurrentUser().getIpAddress());
				evento.setSessionId(action.getRequest().getSession().getId());
				evento.setEventType(PortGareEventsConstants.CAMBIO_STATO_COMUNICAZIONE);

				try {
					if(this.comunicazioneFlusso.getDettaglioComunicazione().getSessionKey() != null) {
						comunicazioniManager.updateSessionKeyComunicazione(
								this.comunicazioneFlusso.getDettaglioComunicazione().getApplicativo(), 
								this.comunicazioneFlusso.getDettaglioComunicazione().getId(), 
								this.comunicazioneFlusso.getDettaglioComunicazione().getSessionKey(), 
								stato);
					} else {
						comunicazioniManager.updateStatoComunicazioni(
								new DettaglioComunicazioneType[] {this.comunicazioneFlusso.getDettaglioComunicazione()}, 
								stato);
					}
				} catch (Throwable t) {
					evento.setError(t);
					throw t;
				} finally {
					eventManager.insertEvent(evento);
				}
			}
		}
			
		return continua;
	}
	/**
	 * restituisce il documento XML associato alla busta
	 * @throws XmlException 
	 */
	public static DocumentazioneBustaDocument getBustaDocument(ComunicazioneType comunicazione) 
		throws XmlException 
	{
		DocumentazioneBustaDocument doc = null;
		
		String xml = ComunicazioneFlusso.getXmlDoc(comunicazione);
		
		if(StringUtils.isNotEmpty(xml)) {
			try {
				doc = DocumentazioneBustaDocument.Factory.parse(xml);
				
			} catch(XmlException e) {
				// formato xml errato... converti nel formato corretto !!!
				// converti un xml BustaEconomica/BustaTecnica in DocumentazioneBusta
				// PORTAPPALT-188
				ApsSystemUtils.getLogger().warn("Conversione formato documentazione busta " + comunicazione.getDettaglioComunicazione().getChiave2());
				
				// verifica il tipo di busta (tec/eco)...
				Calendar dataPresentazione = null;
				ListaDocumentiType documenti = null;
				try {
					// busta economica...
					BustaEconomicaDocument docEco = BustaEconomicaDocument.Factory.parse(xml);
					BustaEconomicaType bustaSrc = docEco.getBustaEconomica();
					if(bustaSrc != null) {
						dataPresentazione = bustaSrc.getDataPresentazione();
						documenti = bustaSrc.getDocumenti();
					}
				} catch(XmlException ex) {
					// busta tecnica
					BustaTecnicaDocument docTec = BustaTecnicaDocument.Factory.parse(xml);
					BustaTecnicaType bustaSrc = docTec.getBustaTecnica();
					if(bustaSrc != null) {
						dataPresentazione = bustaSrc.getDataPresentazione();
						documenti = bustaSrc.getDocumenti();
					}
				}
				
				// converti in documentazion busta...
				doc = DocumentazioneBustaDocument.Factory.newInstance();
				DocumentazioneBustaType bustaDst = doc.addNewDocumentazioneBusta();
				bustaDst.addNewDocumenti();
				
				if(bustaDst != null) {
					bustaDst.setDataPresentazione(dataPresentazione);
					bustaDst.setDocumenti(documenti);
				}
			}
		} else {
			doc = DocumentazioneBustaDocument.Factory.newInstance();
			doc.addNewDocumentazioneBusta();
		}
		return doc; 
	}
	
	/**
	 * restituisce il documento XML associato alla busta
	 * @throws XmlException 
	 */
	//public XmlObject DocumentazioneBustaDocument getBustaDocument() throws XmlException {
	public XmlObject getBustaDocument() throws XmlException {
		return BustaDocumenti.getBustaDocument(this.comunicazioneFlusso.getComunicazione());
	}
	
	/**
	 * invalida i dati dell'helper associato alla busta  
	 */
	@Override
	protected void invalidateHelper() throws Throwable {
		// rimuove dalla sessione la busta ed elimina dalla cartella "temp" i file temporanei 
		valueUnbound(null);
	}

	/**
	 * inizializza un nuovo helper associato alla busta  
	 */
	@Override
	protected boolean initHelper() throws Throwable {
		GestioneBuste.traceLog("BustaDocumenti.initHelper");
		
		this.helperDocumenti = null;	//new WizardDocumentiBustaHelper(this.tipoBusta);
		try {
			DettaglioGaraType dettGara = this.gestioneBuste.getDettaglioGara(); 
			WizardDatiImpresaHelper impresa = this.gestioneBuste.getImpresa();
			
			// crea un nuovo helper dei documenti...
			this.helperDocumenti = new WizardDocumentiBustaHelper(this.tipoBusta);
			this.helperDocumenti.setUsername(this.username);
			this.helperDocumenti.setCodiceGara(this.codiceGara);
			this.helperDocumenti.setCodice(this.codiceLotto);
			this.helperDocumenti.setProgressivoOfferta(this.progressivoOfferta);
			this.helperDocumenti.setOperazione(this.gestioneBuste.getOperazione());
			this.helperDocumenti.setImpresa(impresa);
			this.helperDocumenti.setDescBando(dettGara.getDatiGeneraliGara().getOggetto());

			// inizializza i documenti dell'helper documenti...
			if ( !this.helperDocumenti.isAlreadyLoaded() ) {
				// carica i documenti dal documento xml...
				DocumentazioneBustaDocument doc = (DocumentazioneBustaDocument)this.getBustaDocument();
				ListaDocumentiType listaDocumenti = (doc != null ? doc.getDocumentazioneBusta().getDocumenti() : null);
				
				this.helperDocumenti.popolaFromXml(
						listaDocumenti,
						this);
			}
			
			// recupera la documentazione richiesta comune a tutti lotti della gara...
			// e la documentazione richiesta per la busta
			this.recuperaDocumentazioneRichiesta(this.helperDocumenti);

		} catch (Throwable t) {
			this.helperDocumenti = null;
			ApsSystemUtils.getLogger().error("BustaDocumenti", "loadHelper", t);
			throw t;
		}

		return (this.helperDocumenti != null);
	}
		
	/**
	 * elemina la comunicazione 
	 * @param stato
	 * 			elenco degli stati della comunicazione da reperire
	 * @throws ApsException 
	 */
	public boolean delete(String stato) throws Throwable {
		GestioneBuste.traceLog("BustaDocumenti.delete");
		boolean continua = true;
		
		// cerca ed elimina la comunicazione della busta
		List<String> stati = new ArrayList<String>();
		if(StringUtils.isNotEmpty(stato)) {
			stati.add(stato);
		} else {
			stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
			stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA);
			stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA_CON_ERRORE);
			stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
		}
		
		String codice = (StringUtils.isNotEmpty(this.codiceLotto) ? this.codiceLotto : this.codiceGara); 
		
		DettaglioComunicazioneType dettaglioComunicazione = this.comunicazioneFlusso.find(
				this.username, 
				codice, 
				this.progressivoOfferta,
				this.comunicazioneFlusso.getTipoComunicazione(), 
				stati);
		
		continua = continua && this.comunicazioneFlusso.delete(dettaglioComunicazione.getId());
		
		// aggiorna la busta di riepilogo...
		if(continua) {
			String codLotto = (StringUtils.isNotEmpty(this.codiceLotto) && !this.codiceLotto.equals(this.codiceGara) 
							   ? this.codiceLotto
							   : null);
			
			BustaRiepilogo bustaRiepilogo = this.gestioneBuste.getBustaRiepilogo();
			
			// in caso di QFORM resetta le info
			RiepilogoBustaBean riepilogo = bustaRiepilogo.getHelper().getRiepilogoBusta(this.tipoBusta, codLotto);
			bustaRiepilogo.getHelper().resetQuestionario(riepilogo);
			
			continua = bustaRiepilogo.send(this);
		}

		return continua;
	}

	/**
	 * elemina la comunicazione 
	 */
	public boolean delete() throws Throwable {
		return this.delete(null);
	}

	/**
	 * restiruisce se e' presente la gestione dell'OEPV per la busta
	 * OEPV: in caso di busta tecnica con 
	 *	- gara telematica
	 *  - gara con offerta telematica 
	 *  - gara con modalita' aggiudicazione = 6
	 *	- criteri di valutazione
	 * va visualizzato il nuovo wizard per la gestione dell'offerta tecnica 
	 */
	protected boolean getOEPV(int tipoCriteri)	{
		GestioneBuste.traceLog("BustaDocumenti.getOEPV");
		boolean apriOEPV = false;
//		try {
			boolean garaLotti = (StringUtils.isNotEmpty(codiceLotto) && !this.codiceLotto.equals(this.codiceGara));
			
			DettaglioGaraType dettGara = this.gestioneBuste.getDettaglioGara();
			LottoDettaglioGaraType[] lotto = dettGara.getLotto();
			
//			if(garaLotti) {
//				dettGara = this.gestioneBuste.getBandiManager().getDettaglioGara(this.codiceLotto);
//			}
//				
//			if(dettGara.getListaCriteriValutazione() != null) {
//				List<CriterioValutazioneOffertaType> criteri =
//								Arrays.asList(dettGara.getListaCriteriValutazione());
//				apriOEPV = WizardOffertaTecnicaHelper.isCriteriValutazioneVisibili(
//								tipoCriteri, 
//								dettGara.getDatiGeneraliGara(),
//								criteri);
//			}
			
			// verifico se nel lotto da visualizzare ci sono criteri di valutazione...
			if(lotto != null && lotto.length > 0) {
				for (int i = 0; i < lotto.length; i++) {
					if(lotto[i].getCodiceLotto().equals(this.codiceLotto)) {
						if(lotto[i].getListaCriteriValutazione() != null) {
							List<CriterioValutazioneOffertaType> criteri =
											Arrays.asList(lotto[i].getListaCriteriValutazione());
							apriOEPV = WizardOffertaTecnicaHelper.isCriteriValutazioneVisibili(
											tipoCriteri, 
											dettGara.getDatiGeneraliGara(),
											criteri);
						}
						break;
					}
				}
			}
//		} catch (ApsException ex) {
//			ApsSystemUtils.getLogger().error("getOEPV", ex);
//		}
		return apriOEPV;
	}
	
	/**
	 * restiruisce se e' presente la gestione dell'OEPV per la busta
	 * OEPV: in caso di busta tecnica con 
	 *	- gara telematica
	 *  - gara con offerta telematica 
	 *  - gara con modalita' aggiudicazione = 6
	 *	- criteri di valutazione
	 * va visualizzato il nuovo wizard per la gestione dell'offerta tecnica 
	 */
	public boolean getOEPV() {
		return false;
	}

	/**
	 * recupera la documentazione richiesta comune a tutti lotti della gara
	 * e la documentazione richiesta per la busta
	 * @throws ApsException 
	 */	
	protected void recuperaDocumentazioneRichiesta(WizardDocumentiBustaHelper documenti) throws ApsException {
		GestioneBuste.traceLog("BustaDocumenti.recuperaDocumentazioneRichiesta");
		
		IBandiManager bandiManager = this.gestioneBuste.getBandiManager();
		WizardDatiImpresaHelper impresa = this.gestioneBuste.getImpresa();
		WizardPartecipazioneHelper partecipazione = this.gestioneBuste.getBustaPartecipazione().getHelper();

		List<DocumentazioneRichiestaType> documentiGara = null;
		List<DocumentazioneRichiestaType> documentiLotto = null;

		if(partecipazione.isPlicoUnicoOfferteDistinte()) {
			// gara a lotti
			// cerca i documenti associati alla gara
			documentiGara = bandiManager.getDocumentiRichiestiBandoGara(
					this.codiceGara,
					null,
					impresa.getDatiPrincipaliImpresa().getTipoImpresa(), 
					partecipazione.isRti(), 
					this.tipoBusta + "");
			
			// cerco i documenti associati al lotto
			documentiLotto = bandiManager.getDocumentiRichiestiBandoGara(
					this.codiceGara,
					this.codiceLotto,
					impresa.getDatiPrincipaliImpresa().getTipoImpresa(), 
					partecipazione.isRti(), 
					this.tipoBusta + "");
		} else {
			// gara NO lotti
			// cerca i documenti associati alla gara
			documentiGara = bandiManager.getDocumentiRichiestiBandoGara(
					this.codiceGara,
					this.codiceGara,
					impresa.getDatiPrincipaliImpresa().getTipoImpresa(), 
					partecipazione.isRti(), 
					this.tipoBusta + "");

			// gara lotti, plico unico, offerta unica...
			// questo tipo di gara ha un'unica offerta per tutti i lotti (quindi assomiglia ad una gara No lotti) 
			if(documentiGara == null || (documentiGara != null && documentiGara.size() <= 0)) {
				// se non ho trovato documenti richiesti potrebbe essere  
				// gara lotti, plico unico, offerta unica...
				documentiGara = bandiManager.getDocumentiRichiestiBandoGara(
						this.codiceGara,
						null,
						impresa.getDatiPrincipaliImpresa().getTipoImpresa(), 
						partecipazione.isRti(), 
						this.tipoBusta + "");
			}
		}
		
		// aggiorna i documenti richiesti
		this.documentiRichiestiDB = new ArrayList<DocumentazioneRichiestaType>();
		if(documentiGara != null) {
			this.documentiRichiestiDB.addAll(documentiGara);
		}
		if(documentiLotto != null) {
			this.documentiRichiestiDB.addAll(documentiLotto);
		}
		
		// aggiorna i documenti inseriti e mancanti
		this.documentiInseritiDB = new ArrayList<DocumentazioneRichiestaType>();
		this.documentiMancantiDB = new ArrayList<DocumentazioneRichiestaType>();
		if(documenti != null) {
			// recupera i documenti specifici del lotto
			// con l'elenco dei documentiRichiestiDB richiesti si creano 2
			// liste, una con gli elementi inseriti e una con quelli mancanti
			// DOCUMENTI COMUNI A TUTTI I LOTTI + SPECIFICI 
			// NEL CASO DI PLICO UNICO OFFERTE DISTINTE
			for (int i = 0; i < this.documentiRichiestiDB.size(); i++) {
				DocumentazioneRichiestaType elem = this.documentiRichiestiDB.get(i);
				if (Attachment.indexOf(documenti.getRequiredDocs(), Attachment::getId, elem.getId()) == -1) {
					// si clona l'elemento in quanto oggetto di modifiche
					DocumentazioneRichiestaType docDaInserire = new DocumentazioneRichiestaType();
					// si accorcia il nome in modo da non creare una
					// combobox troppo estesa
					docDaInserire.setNome(elem.getNome().substring(0, Math.min(40, elem.getNome().length())));
					docDaInserire.setId(elem.getId());
					docDaInserire.setIdfacsimile(elem.getIdfacsimile());
					docDaInserire.setObbligatorio(elem.isObbligatorio());
					this.documentiMancantiDB.add(docDaInserire);
				} else {
					this.documentiInseritiDB.add(elem);
				}
			}
		}
	}	

	/**
	 * restituisce se la busta e' gia' stata inviata  
	 */
	public boolean isBustaInviata() {
		String stato = this.comunicazioneFlusso.getStato();
		if( StringUtils.isEmpty(stato) || CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA.equals(stato) ) {
			return false;
		} 
		return true;
	}

	/**
	 * restituisce chiave pubblica della busta/lotto 
	 * 
	 * @throws ApsException 
	 */
	public byte[] getChiavePubblica() throws ApsException {
		byte[] chiavePubblica = this.gestioneBuste.getBandiManager().getChiavePubblica(
				this.codiceGara,
				this.comunicazioneFlusso.getTipoComunicazione());
		return chiavePubblica; 
	}

	/**
	 * verifica l'esistenza del documento marcato "dgue" tra i documenti della busta (idstampa='dgue')
	 */
	public Long getIdDgueRequestDocument() {
		Long id = null;
		for (DocumentazioneRichiestaType doc : this.getDocumentiRichiestiDB()) {
			ApsSystemUtils.getLogger().debug("id: {}, idfacsimile: {}, idstampa(): {}", doc.getId(), doc.getIdfacsimile(), doc.getIdstampa());
			if(GestioneBuste.DGUEREQUEST.equalsIgnoreCase(doc.getIdstampa())) {
				id = doc.getIdfacsimile();
				break;
			}
		}
		return id;
	}

}
