package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.apsadmin.system.BaseAction;
import it.eldasoft.sil.portgare.datatypes.TipoPartecipazioneDocument;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.LottoGaraType;
import it.eldasoft.www.sil.WSGareAppalto.TipoPartecipazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

/**
 * ... 
 */
public class BustaPartecipazione extends BustaGara {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 4533434226163462316L;

	
	private WizardPartecipazioneHelper helper;
	private int operazione;
	private ComunicazioneFlusso domandaPartecipazione;		// solo per gare ristrette
	private List<String> lottiAmmessiPrequalifica;
	private boolean hasFileRiepilogoAllegati;
	private boolean riepilogoAllegatiFirmato;
	private Long idFileRiepilogoAllegati;

	public WizardPartecipazioneHelper getHelper() {
		return helper;
	}
	
	public void setHelper(WizardPartecipazioneHelper helper) {
		this.helper = helper;
	}
	
	public int getOperazione() {
		return operazione;
	}

	public void setOperazione(int operazione) {
		this.operazione = operazione;
	}
	
	public ComunicazioneFlusso getDomandaPartecipazione() {
		return domandaPartecipazione;
	}

	public List<String> getLottiAmmessiPrequalifica() {
		return lottiAmmessiPrequalifica;
	}
	
	public boolean isHasFileRiepilogoAllegati() {
		return hasFileRiepilogoAllegati;
	}

	public void setHasFileRiepilogoAllegati(boolean hasFileRiepilogoAllegati) {
		this.hasFileRiepilogoAllegati = hasFileRiepilogoAllegati;
	}

	public boolean isRiepilogoAllegatiFirmato() {
		return riepilogoAllegatiFirmato;
	}

	public void setRiepilogoAllegatiFirmato(boolean riepilogoAllegatiFirmato) {
		this.riepilogoAllegatiFirmato = riepilogoAllegatiFirmato;
	}
	
	public Long getIdFileRiepilogoAllegati() {
		return idFileRiepilogoAllegati;
	}
	

	/**
	 * costruttore
	 */
	public BustaPartecipazione(
			GestioneBuste buste,
			int tipoBusta) 
	{
		super(buste, 
			  null, 
			  tipoBusta);
		
		String tipoComunicazione = null;
		if(tipoBusta == BUSTA_PARTECIPAZIONE) {			
			tipoComunicazione = PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT;
			this.operazione = PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA;
		} else if(tipoBusta == BUSTA_INVIO_OFFERTA) {
			tipoComunicazione = PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT;
			this.operazione = PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA;
		} else {
			this.operazione = PortGareSystemConstants.TIPOLOGIA_EVENTO_COMPROVA_REQUISITI;
		}
		this.domandaPartecipazione = null;
		this.lottiAmmessiPrequalifica = null;
		this.hasFileRiepilogoAllegati = false;
		this.riepilogoAllegatiFirmato = false;
		
		this.comunicazioneFlusso.getDettaglioComunicazione().setTipoComunicazione(tipoComunicazione);
		
		// inizializza l'helper della partecipazione
		this.helper = null;
	}
	
	/**
	 * recupera la busta dal servizio
	 * 
	 * @throws ApsException 
	 */
	@Override
	public boolean get(List<String> stati) throws Throwable {
		GestioneBuste.traceLog("BustaPartecipazione.get");
		boolean continua = true;

		// si cercano le comunicazioni di tipo partecipazione relative 
		// al plico, all'utente e alla gara in input
		String tipoComunicazione = this.comunicazioneFlusso.getTipoComunicazione();

		if(stati != null) {
			continua = super.get(stati);
		} else {
			// si cerca la comunicazione inviata in precedenza
			DettaglioComunicazioneType criteriRicerca = new DettaglioComunicazioneType();
			criteriRicerca.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
			criteriRicerca.setChiave1(this.username);
			criteriRicerca.setChiave2(this.codiceGara);
			criteriRicerca.setChiave3(this.progressivoOfferta);
			if(this.gestioneBuste.getDettaglioGara().getDatiGeneraliGara().isProceduraTelematica()) {
				criteriRicerca.setStato(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
			} else {
				criteriRicerca.setStato(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
			}
			criteriRicerca.setTipoComunicazione(tipoComunicazione);
			
			List<DettaglioComunicazioneType> comunicazioni = this.gestioneBuste.getComunicazioniManager()
				.getElencoComunicazioni(criteriRicerca);
			if (comunicazioni == null || comunicazioni.isEmpty()) {
	//			// in caso di comunicazione non reperita si utilizzano gli eventuali dati 
	//			// della partecipazione presenti in backoffice
	//			if (tipoPartecipazione != null) {
	//				// ristrette e negoziate
	//				this.helper.setRti(tipoPartecipazione.isRti());
	//				this.helper.setDenominazioneRTI(tipoPartecipazione.getDenominazioneRti());
	//				if (!tipoPartecipazione.isRti()) {
	//					// permetto l'editing dei dati se sono invitato come singola impresa
	//					this.helper.setEditRTI(true);
	//				}
	//			} else {
	//				// aperte
	//				this.helper.setEditRTI(true);
	//			}
			} else {
				// si individua l'ultima comunicazione in stato DA PROCESSARE 
				// (quella con id massimo, dato che e' un contatore)
				Long maxId = (long) -1;
				for (int i = 0; i < comunicazioni.size(); i++) {
					if (comunicazioni.get(i).getId() > maxId) {
						maxId = comunicazioni.get(i).getId();
					}
				}
	
				// si estraggono i dati dall'ultima comunicazione in stato DA PROCESSARE
				continua = this.comunicazioneFlusso.get(CommonSystemConstants.ID_APPLICATIVO, maxId);
			}
		}
		
		if(continua) {
			continua = this.initHelper();
		}
		
		// verifica se nella busta e' presente l'allegato del riepilogo buste 
		// "Riepilogo_buste.pdf" o "Riepilogo_buste.pdf.tsd"
		this.idFileRiepilogoAllegati = null;
		this.hasFileRiepilogoAllegati = false;
		this.riepilogoAllegatiFirmato = false;
		if(this.comunicazioneFlusso.getId() > 0) {
			AllegatoComunicazioneType[] allegati = this.comunicazioneFlusso.getComunicazione().getAllegato();
			for(AllegatoComunicazioneType allegato : allegati) {
				if(allegato.getNomeFile().equalsIgnoreCase(PortGareSystemConstants.FILENAME_RIEPILOGO_BUSTE) || 
				   allegato.getNomeFile().equalsIgnoreCase(PortGareSystemConstants.FILENAME_RIEPILOGO_BUSTE_MARCATURA_TEMPORALE))
				{
					this.hasFileRiepilogoAllegati = true;
					this.idFileRiepilogoAllegati = this.comunicazioneFlusso.getId();
					if(allegato.getNomeFile().equalsIgnoreCase(PortGareSystemConstants.FILENAME_RIEPILOGO_BUSTE_MARCATURA_TEMPORALE)) {
						this.riepilogoAllegatiFirmato = true;
					}
					break;
				}
			}
		}
		
		return continua;
	}
	
	/**
	 * invia la busta al servizio con la descrizione dell'operazione in uno specifico stato
	 * 
	 * @throws ApsException 
	 */
	public boolean send(String stato) throws Throwable {
		GestioneBuste.traceLog("BustaPartecipazione.send " + stato);
		boolean continua = true;
		
		// Effettua l'invio della comunicazione al backoffice costituita da una
		// testata e da un allegato contenente al suo interno l'XML dei dati inseriti,
		// e poi ripulisce la sessione dai dati raccolti ed imposta la chiave del
		// bando oggetto della partecipazione per poter ritornare al suo dettaglio
		BaseAction action = GestioneBuste.getAction();
		String tipoComunicazione = this.comunicazioneFlusso.getTipoComunicazione();
	
		// se lo stato non e' specificato imposta lo stato standard BOZZA...
		if(StringUtils.isEmpty(stato)) {
			stato = (this.helper.isGaraTelematica()
					? CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA
					: CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
		}

		Event evento = new Event();
		evento.setUsername(this.username);
		evento.setIpAddress(action.getCurrentUser().getIpAddress());
		evento.setSessionId(action.getRequest().getSession().getId());
		evento.setDestination(this.codiceGara);
		evento.setLevel(Event.Level.INFO);
		evento.setEventType(PortGareEventsConstants.INVIO_COMUNICAZIONE_DA_PROCESSARE);
		evento.setMessage(
				"Invio comunicazione " + tipoComunicazione
				+ " con id " + this.helper.getIdComunicazioneTipoPartecipazione()
				+ " in stato " + stato
				+ " con timestamp ntp " + UtilityDate.convertiData(UtilityDate.getDataOdiernaAsDate(),
										   						   UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));

		// Invio della comunicazione
		try {
			// estrazione dei parametri necessari per i dati di testata della comunicazione
			// sicuramente lo username si estrae, in quanto l'utente per arrivare
			// qui deve essere loggato e la sessione non puo' essere scaduta
			String ragioneSociale = this.helper.getImpresa().getDatiPrincipaliImpresa().getRagioneSociale();
			String ragioneSociale200 = StringUtils.left(ragioneSociale, 200);
			String oggetto = MessageFormat.format(
						action.getI18nLabelFromDefaultLocale("NOTIFICA_PARTECIPAZIONE_OGGETTO"),
						new Object[] { this.username, this.helper.getIdBando() });
			String testo = MessageFormat.format(
						action.getI18nLabelFromDefaultLocale("NOTIFICA_PARTECIPAZIONE_TESTO"),
						new Object[] { ragioneSociale200 });
			String descrizioneFile = 
						action.getI18nLabelFromDefaultLocale("NOTIFICA_PARTECIPAZIONE_ALLEGATO_DESCRIZIONE");
			
			// FASE 1: 
			// costruzione del contenitore della comunicazione
			evento.setMessage(
					"Invio comunicazione " + tipoComunicazione
					+ " in stato " + stato
					+ " con timestamp ntp " + UtilityDate.convertiData(UtilityDate.getDataOdiernaAsDate(),
											   						   UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
			
			// FASE 2: 
			// popolamento dai dati della comunicazione e degli eventuali allegati
			boolean isNuovaComunicazione = (this.comunicazioneFlusso.getId() <= 0);
			DettaglioComunicazioneType dettaglioComunicazione = this.comunicazioneFlusso.getDettaglioComunicazione();
			dettaglioComunicazione.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
			if(dettaglioComunicazione.getId() == null) {
				dettaglioComunicazione.setId(this.helper.getIdComunicazioneTipoPartecipazione());
			}
			dettaglioComunicazione.setChiave1(this.username);
			dettaglioComunicazione.setChiave2(this.helper.getIdBando());
			dettaglioComunicazione.setChiave3(this.progressivoOfferta);
			dettaglioComunicazione.setMittente(StringUtils.left(ragioneSociale, 60));
			dettaglioComunicazione.setStato(stato);
			dettaglioComunicazione.setOggetto(oggetto);
			dettaglioComunicazione.setTesto(testo);
			dettaglioComunicazione.setTipoComunicazione(this.comunicazioneFlusso.getTipoComunicazione());
			dettaglioComunicazione.setComunicazionePubblica(false);
			dettaglioComunicazione.setDataPubblicazione(null);
			
			List<AllegatoComunicazioneType> allegati = null;
			if(this.comunicazioneFlusso.getComunicazione().getAllegato() != null &&
			   this.comunicazioneFlusso.getComunicazione().getAllegato().length > 1) 
			{
				// NB: il I allegato dovrebbe sempre essere il documento xml 
				// che non va considerato come allegato aggiuntivo
				allegati = new ArrayList<AllegatoComunicazioneType>();
				for(int i = 1; i < this.comunicazioneFlusso.getComunicazione().getAllegato().length; i++) {
					allegati.add(this.comunicazioneFlusso.getComunicazione().getAllegato()[i]);
				}
			}
			
			// FASE 3: 
			// invio comunicazione al servizio
			Long id = this.comunicazioneFlusso.send(
					this.helper.getXmlDocumentTipoPartecipazione(), 
					descrizioneFile,
					allegati);
			
			evento.setMessage(
					"Invio comunicazione " + tipoComunicazione
					+ " con id "  + dettaglioComunicazione.getId() 
					+ " in stato " + stato
					+ " e timestamp ntp " + UtilityDate.convertiData(UtilityDate.getDataOdiernaAsDate(),
											   						 UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
			
			// FASE 4: 
			// aggiorna il riepilogo ed invia la comunicazione FS11R/FS10R 
			if(this.helper.isGaraTelematica() && this.helper.isPlicoUnicoOfferteDistinte()) {
				// scrivi nel riepilogo tutti i lotti ammessi nell'offerta di gara
				// in caso di ristrette/negoziate i lotti ammessi dipendono dalla prequalifica
				//List<String> lottiOfferta = this.getLottiAttivi();
				//this.gestioneBuste.getBustaRiepilogo().getHelper().setListaCompletaLotti(lottiOfferta);
				this.gestioneBuste.getBustaRiepilogo().integraLottiFromPartecipazione();
			}
			
			// se la partecipazione e' appena stata inserita, allinea le info sui documenti
			// mancanti/inseriti/richiesti per tutte le buste presenti nel riepilogo
			if(isNuovaComunicazione) {
				this.gestioneBuste.getBustaRiepilogo().integraBusteFromBO();
			}
		
			this.gestioneBuste.getBustaRiepilogo().send(null);
			
		} catch (Throwable t) {
			continua = false;
			evento.setError(t);
			throw t;
		} finally {
			// inserisci l'evento solo se e' diverso dallo stato BOZZA (1) (?????)
			if( !CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA.equals(stato) ) {
				this.gestioneBuste.getEventManager().insertEvent(evento);
			}
		}
	
//		// FASE 5: 
//		// pulizia e impostazione navigazione futura
//		// se tutto e' andato a buon fine si eliminano le informazioni dalla sessione...
//		this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);
//		this.session.remove(PortGareSystemConstants.SESSION_ID_PAGINA);
//		// ...e si imposta il codice del bando per la riapertura del dettaglio
//		this.setCodice(partecipazione.getIdBando());

		// FASE 6: settaggio della protezione in modo da bloccare eventuali nuovi invii con gli stessi dati
		this.helper.setDatiInviati(true);
		
		return continua;
	}

	/**
	 * invia la busta al servizio con la descrizione dell'operazione
	 * 
	 * @throws ApsException 
	 */
	@Override
	public boolean send() throws Throwable {
		return this.send(null);
	}
	
	/**
	 * conferma ed invia la busta al servizio
	 * @throws Throwable 
	 */
	public boolean sendConfirm() throws Throwable {
		GestioneBuste.traceLog("BustaPartecipazione.sendConfirm " + this.comunicazioneFlusso.getTipoComunicazione() + 
				   " " +(StringUtils.isNotEmpty(this.codiceLotto) ? this.codiceLotto : this.codiceGara) +
	               " idCom=" + this.comunicazioneFlusso.getId());
		boolean continua = true;
		
		IEventManager eventManager = this.gestioneBuste.getEventManager();
		BustaRiepilogo bustaRiepilogo = this.gestioneBuste.getBustaRiepilogo();
		
		Event evento = null;
		try {
			evento = ComunicazioniUtilities.createEventSendComunicazione(
					this.username, 
					this.codiceGara,
					this.comunicazioneFlusso.getComunicazione(),
					this.helper.getDataPresentazione(),
					GestioneBuste.getAction().getCurrentUser().getIpAddress(),
					GestioneBuste.getAction().getRequest().getSession().getId());
			
			// verifica la presenza della comunicazione (in stato BOZZA (1) o DA PROCESSARE (5))
			if(this.comunicazioneFlusso.getId() <= 0 ||
			   ( !CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA.equals(this.comunicazioneFlusso.getDettaglioComunicazione().getStato()) &&
				 !CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE.equals(this.comunicazioneFlusso.getDettaglioComunicazione().getStato()) ) )
			{
				continua = false;
				ApsSystemUtils.getLogger().warn("Comunicazione del tipo partecipazione non trovata in stato BOZZA o DA PROCESSARE");
			}

			// aggiorna data invio e stato della comunicazione
			// verifica il pdf di riepilogo
			// ed invia la comunicazione in stato DA PROCESSARE (5) o DA PROTOCOLLARE (9)
			if(continua) {
				// verifica se la protocollazione e' prevista per la SA corrente...
				String saProcedura = null;
				if(this.gestioneBuste.getDettaglioGara() != null && this.gestioneBuste.getDettaglioGara().getStazioneAppaltante() != null) {
					saProcedura = this.gestioneBuste.getDettaglioGara().getStazioneAppaltante().getCodice();
				}
				
				Integer tipoProtocollazione = this.gestioneBuste.getAppParamManager().getTipoProtocollazione(saProcedura);

				// imposta lo stato della busta...
				String stato = CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE;
				if(tipoProtocollazione.intValue() == PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_WSDM ||
				   tipoProtocollazione.intValue() == PortGareSystemConstants.TIPO_PROTOCOLLAZIONE_MAIL) 
				{
					// comunicazione da protocollare...
					stato = CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROTOCOLLARE;
				}
				
				// aggiorna data invio e stato della comunicazione
				this.comunicazioneFlusso.getDettaglioComunicazione().setDataPubblicazione(this.helper.getDataPresentazione());
				this.comunicazioneFlusso.getDettaglioComunicazione().setStato(stato);
			
				// verifica la presenza dell'allegato del PDF di riepilogo
				// se non esiste viene aggiunto automaticamente agli allegati della comunicazione
				byte[] pdfRiepilogo = bustaRiepilogo.getPdfRiepilogoBuste();
				
				// ed invia la comunicazione aggiornata...
				continua = continua && this.send(stato);
			}
		} catch (Throwable t) {
			continua = false;
			evento.setError(t);
			throw t;
		} finally {
			if(evento != null) {
				eventManager.insertEvent(evento);
			}
		}
			
		return continua;
	}
	
	/**
	 * restituisce il documento xml associato alla busta
	 */	
	@Override
	public XmlObject getBustaDocument() throws XmlException {
		//return this.helper.getXmlDocumentTipoPartecipazione();
		return null;
	}
	
	/**
	 * inizializza un nuovo helper associato alla busta  
	 */
	@Override
	protected boolean initHelper() throws Throwable {
		GestioneBuste.traceLog("BustaPartecipazione.initHelper");
		try {
			if(StringUtils.isEmpty(this.progressivoOfferta)) {
				this.progressivoOfferta = "1";
			}
			
			IBandiManager bandiManager = this.gestioneBuste.getBandiManager();
			WizardDatiImpresaHelper impresa = this.gestioneBuste.getImpresa();
			DettaglioGaraType gara = this.gestioneBuste.getDettaglioGara(); 

			boolean invioOfferta = (PortGareSystemConstants.RICHIESTA_TIPO_INVIO_OFFERTA_GT.equals(this.comunicazioneFlusso.getTipoComunicazione()));
			boolean ristretta = WizardPartecipazioneHelper.isGaraRistretta(gara.getDatiGeneraliGara().getIterGara());

			boolean garaLotti = false;
			if(gara != null && gara.getDatiGeneraliGara() != null) {
				garaLotti =	gara.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_OFFERTE_DISTINTE
							|| (gara.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_PLICO_UNICO);
			}
			
			// in caso di gara a lotti solo il progressivo #1 puo' presentare come "singola"
			// mentre tutti gli altri progressivo possono presentare solo come "rti"
			boolean partecipaInRti = (garaLotti && Long.parseLong(this.progressivoOfferta) > 1);
	
			// recupera se sono presenti delle consorziate esecutrici
			boolean consorziateEsecutriciPresenti = false;
			if (impresa.isConsorzio() && bandiManager.isConsorziateEsecutriciPresenti(this.username, this.codiceGara)) {
				consorziateEsecutriciPresenti = true;
			}

			// NB: 
			//  prima di inizializzare l'helper da un doc XML e' necessario 
			//  impostare le proprieta' dell'helper 
			//  	- "garaTelematica", 
			//		- "plicoUnicoOfferteDistinte"
			//		- "lottiDistinti"
			//  altrimenti i lotti non vengono caricati correttamente
			this.helper = new WizardPartecipazioneHelper();
			this.helper.setIdBando(this.codiceGara);
			this.helper.setDescBando(gara.getDatiGeneraliGara().getOggetto());
			this.helper.setTipoEvento(this.operazione);
			this.helper.setGaraTelematica(gara.getDatiGeneraliGara().isProceduraTelematica());
			this.helper.setTipoProcedura(Integer.parseInt(gara.getDatiGeneraliGara().getTipoProcedura()));
			this.helper.setIterGara(Integer.parseInt(gara.getDatiGeneraliGara().getIterGara()));
			this.helper.setProgressivoOfferta(this.progressivoOfferta);
			this.helper.setDenominazioneRTIReadonly(true);
			this.helper.setImpresa(this.gestioneBuste.getImpresa());
			this.helper.setConsorziateEsecutriciPresenti(consorziateEsecutriciPresenti);
			
			// ATTENZIONE 
			// datiGeneraliGara.Tipologia e' sempre 3 sia per 
			// sia per le gare a lotti, plico unico, offerte distinte  
			// sia per le gare a lotti, plico unico, offerta unica
			// per sapere se e' una gara a offerte distine o offerta unica va usato datiGeneraliGara.BusteDistinte !!!
			boolean busteDistinte = (gara.getDatiGeneraliGara().getBusteDistinte() != null 
	 				 				 ? gara.getDatiGeneraliGara().getBusteDistinte().booleanValue()
	 				 				 : false);

			this.helper.setPlicoUnicoOfferteDistinte(false);
			this.helper.setLottiDistinti(false);
			
			if (gara.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_OFFERTE_DISTINTE) {				
				this.helper.setPlicoUnicoOfferteDistinte(true);
				this.helper.setLottiDistinti(true);
				
			} else if(gara.getDatiGeneraliGara().getTipologia() == PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_PLICO_UNICO && busteDistinte) {
				this.helper.setPlicoUnicoOfferteDistinte(true);
				this.helper.setLottiDistinti(true);
			} else {
				// SERVE ?
			}
			
			// per buste distinte recupero i lotti...
			if(busteDistinte) {
				if(garaLotti) {
					this.retriveLottiGara();
				}
			}
			
			// per RTI si utilizzano i dati della partecipazione presenti in backoffice
			// si cerca l'eventuale tipo di partecipazione se gia' presente in backoffice
			TipoPartecipazioneType tipoPartecipazione = this.gestioneBuste.getBandiManager()
					.getTipoPartecipazioneImpresa(
							this.username, 
							this.codiceGara,
							this.progressivoOfferta);
			if (tipoPartecipazione != null) {
				// ristrette e negoziate
				this.helper.setRti(tipoPartecipazione.isRti());
				this.helper.setDenominazioneRTI(tipoPartecipazione.getDenominazioneRti());
				
				if (!tipoPartecipazione.isRti()) {
					// permetto l'editing dei componenti se sono invitato come singola impresa
					this.helper.setEditRTI(true);
				} 
				
				// vanno recuperati da BO anche i componenti di RTI o le 
				// consorziate esecutrici ???
				// ...
				// helper.getComponenti().add( new IComponente() );
				// ...
	
				// NB: dal BO per le gare negoziate editRti=False !!! 
				this.helper.setRtiBO(tipoPartecipazione.isRti());
			} else {
				// aperte
				if(gara.getDatiGeneraliGara().isProceduraTelematica()) {
					this.helper.setEditRTI(true);
				}
				
				// per le gare ristrette a lotti (itegara = 2, 4)
				// in caso di una nuova offerta si abilita lo step "forma di partecipazione"
				if(ristretta) {
					this.helper.setEditRTI(true);
					this.helper.setRtiPartecipazione(false);
				}
	
				// forza la partecipazione in RTI per i progressivi offerta > 1
				if(partecipaInRti) {
					this.helper.setRti(true);							// imposta il tipo partecipazione come RTI
					this.helper.setEditRTI(true);						// abilita lo step "Forma partecipazione"
					this.helper.setRtiPartecipazione(false);			// ...
					this.helper.setDenominazioneRTIReadonly(false);		// rendi la denominazione RTI editabile
				}
			}
			
			if (invioOfferta && ristretta) {
				// per le gare ristrette in fase di invio offerta
				// se l'impresa si è presentata come RTI, 
				// non e' piu' possibile modificare la composizione
				if( tipoPartecipazione != null && tipoPartecipazione.isRti() ) {
					this.helper.setEditRTI( !tipoPartecipazione.isRti() );
					this.helper.setRtiPartecipazione( tipoPartecipazione.isRti() );
				}
			}
			
			// GARE RISTRETTE 
			// inizializza l'helper con i dati della prequalifica
			// recupero la comunicazione della domanda di partecipazione
			// e si verifica quali sono i lotti con i quali ci si e' presentati 
			// in fase di domanda di paretcipazione (prequalifica)
			this.domandaPartecipazione = null;
			this.lottiAmmessiPrequalifica = null;
			if (invioOfferta && ristretta) {
				List<String> stati = new ArrayList<String>();
				stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
				stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
				stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_INVIATA);
				stati.add(CommonSystemConstants.STATO_COMUNICAZIONE_PROCESSATA);
				
				ComunicazioneFlusso tmp = new ComunicazioneFlusso();
				tmp.setComunicazioniManager(this.gestioneBuste.getComunicazioniManager());
				DettaglioComunicazioneType dettCom = tmp.find(
						this.username, 
						this.codiceGara, 
						this.progressivoOfferta, 
						PortGareSystemConstants.RICHIESTA_TIPO_PARTECIPAZIONE_GT, 
						stati);
				if(dettCom != null) {
					this.domandaPartecipazione = tmp;
					this.domandaPartecipazione.get(dettCom.getApplicativo(), dettCom.getId());
				}
				
				this.lottiAmmessiPrequalifica = this.retriveLottiGara();
			}

			// NB: 
			// per gara RISTRETTA, in fase di offerta va recuperata la partecipazione 
			// della domanda di prequalifica, in modo da proporre i dati, se ancora non e' 
			// stata creata la FS11
			// se esiste la comunicazione, recupera i dati dal documento XML della comunicazione (FS11/FS10)
			// associata alla format di partecipazione
			// NB: se non e' ancora stata inizializzata la partecipazione dell'offerta
			//     per le RISTRETTE si recupera quanto era abilitato in fase di prequalifica
			long idPartecipazione = (this.domandaPartecipazione != null ? this.domandaPartecipazione.getId() : -1);
			boolean caricaDaComunicazione = (this.comunicazioneFlusso.getId() > 0 || idPartecipazione > 0);			
			if(caricaDaComunicazione) {
				String xml = null;
				if(invioOfferta && ristretta && idPartecipazione > 0 && this.comunicazioneFlusso.getId() <= 0) {
					// ristretta in fase di offerta senza FS11
					xml = this.domandaPartecipazione.getXmlDoc();
				} else if (this.comunicazioneFlusso.getId() > 0) {
					xml = this.comunicazioneFlusso.getXmlDoc();
				}
				if(StringUtils.isEmpty(xml)) {
					throw new ApsException(GestioneBuste.getAction().getText("Errors.invioBuste.xmlPartecipazioneNotFound"));
				}
				TipoPartecipazioneDocument document = TipoPartecipazioneDocument.Factory.parse(xml);
				this.helper.fillFromXml(document);
			} 
			
			// configura gli step del wizard
			this.helper.fillStepsNavigazione();

		} catch (Throwable t) {
			ApsSystemUtils.getLogger().error("BustaPartecipazione", "initHelper", t);
		}
		
		return (this.helper != null);
	}

	/**
	 * Estrae da BO i lotti ammessi/abilitati per una gara.
	 *  
	 * @return lista dei lotti selezionati in partecipazione per una gara ristretta
	 * 		   NULL se la gara non è ristretta
	 *  
	 * @throws ApsException
	 */
	private List<String> retriveLottiGara() throws ApsException {
		List<String> lottiAttivi = null;
		TreeSet<String> lotti = null;
		List<String> oggettiLotti = null;
		List<String> lottiInterni = null;

		IBandiManager bandiManager = this.gestioneBuste.getBandiManager();
		DettaglioGaraType gara = this.gestioneBuste.getDettaglioGara();
		
		// NB: per le gare ristrette in fase di offerta vanno recuperati solo i lotti ammessi in prequalifica
		//     quindi solo per i plichi che hanno una prequalifica (FS10) 
		String progOfferta = null; 
		if(this.gestioneBuste.isRistretta() && this.gestioneBuste.isInvioOfferta()) {
			if(this.domandaPartecipazione != null) { 
				progOfferta = this.progressivoOfferta;
			}
		}
		
		// recupera i lotti di gara da BO...
		LottoGaraType[] lottiGara = null;
		if(PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA == this.operazione) {
			lottiGara = bandiManager.getLottiGaraPerDomandePartecipazione(
					this.codiceGara, 
					progOfferta);
			
		} else if(PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA == this.operazione) {
			if( PortGareSystemConstants.TIPOLOGIA_GARA_PIU_LOTTI_PLICO_UNICO == gara.getDatiGeneraliGara().getTipologia() ) {
				lottiGara = bandiManager.getLottiGaraPlicoUnicoPerRichiesteOfferta(
						this.username, 
						this.codiceGara, 
						progOfferta);
			} else {
				lottiGara = bandiManager.getLottiGaraPerRichiesteOfferta(
						this.username, 
						this.codiceGara, 
						progOfferta);
			}
			
		} else if(PortGareSystemConstants.TIPOLOGIA_EVENTO_COMPROVA_REQUISITI == this.operazione) {
			lottiGara = bandiManager.getLottiGaraPerRichiesteCheckDocumentazione(this.username, this.codiceGara);
		}
		
		if(lottiGara != null) {
			lottiAttivi = new ArrayList<String>();
			lotti = new TreeSet<String>();
			oggettiLotti = new ArrayList<String>();
			lottiInterni = new ArrayList<String>();
			for(int i = 0; i < lottiGara.length; i++) {
				lottiAttivi.add(lottiGara[i].getCodiceLotto());
				lotti.add(lottiGara[i].getCodiceLotto());
				oggettiLotti.add(lottiGara[i].getOggetto());
				lottiInterni.add(lottiGara[i].getCodiceInterno());
			}
		}
		
		this.helper.setLotti(lotti);
		this.helper.setOggettiLotti(oggettiLotti);
		this.helper.setLottiAmmessiInOfferta(lottiAttivi);
		this.helper.setCodiciInterniLotti(lottiInterni);
		
		return lottiAttivi;
	}
	
	/**
	 * restituisce i lotti attivi/gestiti per l'offerta di gara (step "forma partecipazione")  
	 */
	public List<String> getLottiAttivi() {
		GestioneBuste.traceLog("BustaPartecipazione.getLottiAttivi");
		
		List<String> lottiAttivi = new ArrayList<String>();
		
		// lotti definiti per la gara
		if(this.helper != null) {
			if(this.helper.getLotti() != null && this.helper.getLotti().size() > 0) {
				lottiAttivi.clear();
				Iterator<String> lottiIterator = this.helper.getLotti().iterator();
				while(lottiIterator.hasNext()) {
					lottiAttivi.add(lottiIterator.next());
				}
			}
		}
		
		// NB: 
		// per le RISTRETTE in fase di offerta i lotti ammessi 
		// sono quelli presentati in prequalifica 
		if (this.gestioneBuste.isInvioOfferta() && this.gestioneBuste.isRistretta()) {
			if(this.lottiAmmessiPrequalifica != null) {
				// preparo l'elenco dei lotti abilitati in fase 
				// di domanda di prequalifica  
				lottiAttivi.clear();
				for(int i = 0; i < this.lottiAmmessiPrequalifica.size(); i++) {
					lottiAttivi.add(this.lottiAmmessiPrequalifica.get(i));
				}
			}
		}
		
		return lottiAttivi;
	}

}
