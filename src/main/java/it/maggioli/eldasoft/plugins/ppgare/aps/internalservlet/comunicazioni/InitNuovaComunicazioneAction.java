package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.exception.ApsSystemException;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.ComunicazioneType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.DocumentazioneRichiestaType;
import it.eldasoft.www.sil.WSStipule.StipulaContrattoType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.INtpManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.beans.ComunicazioniConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.RichiesteRettificaList;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardNuovaComunicazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardRettificaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardRettificaHelper.FasiRettifica;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.helpers.WizardSoccorsoIstruttorioHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ImpresaAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa.WizardRegistrazioneImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IStipuleManager;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * ...
 */
@FlussiAccessiDistinti({EFlussiAccessiDistinti.COMUNICAZIONE, EFlussiAccessiDistinti.COMUNICAZIONE_STIPULA})
public class InitNuovaComunicazioneAction extends EncodedDataAction implements SessionAware {
    /**
     * UID
     */
    private static final long serialVersionUID = -2654354870263779124L;

    
    private ICustomConfigManager customConfigManager;
    private IAppParamManager appParamManager;
    private IBandiManager bandiManager;
    private IComunicazioniManager comunicazioniManager;
    private IStipuleManager stipuleManager;
    private IEventManager eventManager;
    private INtpManager ntpManager;
    
    private Map<String, Object> session;
    
    @Validate(EParamValidation.CODICE)
    private String codice;
    @Validate(EParamValidation.CODICE)
    private String codice2;
    @Validate(EParamValidation.GENERIC)
    private String tipo;
    @Validate(EParamValidation.OGGETTO_COMUNICAZIONE)
    private String oggetto;
    @Validate(EParamValidation.ACTION)
    private String from;
    @Validate(EParamValidation.ACTION)
    private String actionName;
    @Validate(EParamValidation.ACTION)
    private String namespace;
    @Validate(EParamValidation.GENERIC)
    private String applicativo;
    private Long id;
    private Long idDestinatario;    		// progressivo dell'impresa relativo alla comunicazione
    @Validate(EParamValidation.GENERIC)
    private String destinatario;    
    private int pagina;
    @Validate(EParamValidation.GENERIC)
    private String rettifica;				// RICHIESTA=richiesta rettifica, RETTIFICA=invio rettifica (vedi WizardRettificaHelper.FasiRettifica)
    private Long tipoBusta;
    private int operazione;
    private String progressivoOfferta;

    /**
     * Memorizza la prossima dispatchAction da eseguire nel wizard.
     */
    private String nextResultAction;


    @Override
    public void setSession(Map<String, Object> arg0) {
        this.session = arg0;
    }

    public void setCustomConfigManager(ICustomConfigManager customConfigManager) {
        this.customConfigManager = customConfigManager;
    }

    public void setAppParamManager(IAppParamManager appParamManager) {
        this.appParamManager = appParamManager;
    }

    public void setBandiManager(IBandiManager bandiManager) {
        this.bandiManager = bandiManager;
    }

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public void setStipuleManager(IStipuleManager stipuleManager) {
        this.stipuleManager = stipuleManager;
    }

    public void setEventManager(IEventManager eventManager) {
        this.eventManager = eventManager;
    }

    public void setNtpManager(INtpManager ntpManager) {
        this.ntpManager = ntpManager;
    }

    public String getOggetto() {
        return oggetto;
    }

    public void setOggetto(String oggetto) {
        this.oggetto = oggetto;
    }

    public void setCodice(String codice) {
        this.codice = codice;
    }

    public String getCodice2() {
        return codice2;
    }

    public void setCodice2(String codice2) {
        this.codice2 = codice2;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNextResultAction() {
        return nextResultAction;
    }

    public static String setNextResultAction(String target) {
        return target;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getApplicativo() {
        return applicativo;
    }

    public void setApplicativo(String applicativo) {
        this.applicativo = applicativo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdDestinatario() {
        return idDestinatario;
    }

    public void setIdDestinatario(Long idDestinatario) {
        this.idDestinatario = idDestinatario;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public int getPagina() {
        return pagina;
    }

    public void setPagina(int pagina) {
        this.pagina = pagina;
    }
    
    public String getRettifica() {
		return rettifica;
	}

	public void setRettifica(String rettifica) {
		this.rettifica = rettifica;
	}

	public Long getTipoBusta() {
		return tipoBusta;
	}

	public void setTipoBusta(Long tipoBusta) {
		this.tipoBusta = tipoBusta;
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

	/**
     * inizializza helper e wizard per le comunicazioni (standard, soccorso istruttorio, rettifica)
     */
    public String initNuovaComunicazione() {
        setTarget(SUCCESS);

        // verifica il profilo di accesso ed esegui un LOCK alla funzione
        if (!lockAccessoFunzione(EFlussiAccessiDistinti.COMUNICAZIONE, this.codice)) {
            setTarget(CommonSystemConstants.PORTAL_ERROR);
            return getTarget();
        }

        if (null != getCurrentUser()
            && !getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
        {
            try {
                if (this.customConfigManager.isVisible("COMUNICAZIONI", "INVIARISPONDI")) {
                    boolean continua = true;
                    
                    String entita = (String) this.session.get(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_ENTITA_PROCEDURA);

                    boolean isRichiestaRettifica = (FasiRettifica.RICHIESTA.name().equals(rettifica));
                    boolean isInvioRettifica = (FasiRettifica.RETTIFICA.name().equals(rettifica));
                    boolean contrattoLFS = (namespace != null && namespace.contains("ContrattiLFS"));
                    boolean stipula = (PortGareSystemConstants.ENTITA_STIPULA.equalsIgnoreCase(entita));
                    boolean isSoccorsoIstruttorio = false;
                    boolean isRettifica = isRichiestaRettifica || isInvioRettifica;
                    
                    // recupera i dati della gara
                 	DettaglioGaraType dettaglioGara = bandiManager.getDettaglioGara(codice);
                 	if(dettaglioGara == null) {
                 		// se non viene trovato il dettaglio gara, il codice dovrebbe essere il codice del lotto
                 		String codiceGara = bandiManager.getCodiceGaraFromLotto(codice); 
                 		if(StringUtils.isNotEmpty(codiceGara) && !codiceGara.equalsIgnoreCase(codice)) {
                 			codice = codiceGara; 
                 		}
                 	}
                    
                    // verifica se esiste gia' una comunicazione relativa a un soccorso istruttorio o ad una rettifica (modello > 0)  
                    ComunicazioneType comunicazione = null;
                    if (this.id != null) {
                        comunicazione = this.bandiManager.getComunicazioneRicevuta(
                                getCurrentUser().getUsername(),
                                id,
                                idDestinatario,
                                applicativo
                        );
                        isSoccorsoIstruttorio = WizardRettificaHelper.isModelloSoccorsoItruttorio(comunicazione.getModello());
                        isRettifica = isRettifica || WizardRettificaHelper.isModelloRettifica(comunicazione.getModello());
                    }

                    // in caso di "rettifica" recupera tipo busta e eventuale lotto dalla comuincazione
                    if(isRettifica && id != null) {
                    	it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType c = comunicazioniManager.getComunicazione(applicativo, id);                    	
                    	codice2 = (c != null ? c.getDettaglioComunicazione().getChiave3() : null);
                    	tipoBusta = (comunicazione != null && comunicazione.getTipoBusta() != null ? comunicazione.getTipoBusta() : null);                    	 
                    }

                    // inizializza il wizard corretto (comunicazione o soccorso istruttorio o rettifica)
                    WizardNuovaComunicazioneHelper helper = null;
                    if (isSoccorsoIstruttorio) {
                    	helper = createWizardSoccorsoIstruttorioHelper(comunicazione);
                    } else if(isRettifica) {
                    	helper = createWizardNuovaRettificaHelper(comunicazione);
                    } else {
                    	helper = createWizardNuovaComunicazioneHelper(comunicazione);
                    }

                    // in caso di "soccorso istruttorio" 
                    // verifica se la richiesta e' oltre la data di scadenza
                    Date dataScadenza = null;
                    if(isSoccorsoIstruttorio || isRettifica) {
                    	if(isSoccorsoIstruttorio)
                    		dataScadenza = WizardNuovaComunicazioneHelper.calcolaDataOra(comunicazione.getDataScadenza(), comunicazione.getOraScadenza());
                    	if(isRettifica)
                    		dataScadenza = helper.getDataScadenza();	// calcolata dalla comunicazione di "accettazione" (createWizardNuovaRettificaHelper()) 
                        if(isFuoriTempoMassimo(dataScadenza)) {
                        	continua = false;
                            setTarget(CommonSystemConstants.PORTAL_ERROR);
                            addActionError(this.getText("Errors.richiestaFuoriTempoMassimo"));
                        }
                    }
                    
                    if (continua) {
                        // recupera i dati impresa
                        WizardRegistrazioneImpresaHelper impresa = ImpresaAction.getLatestDatiImpresa(
                        		this.getCurrentUser().getUsername(),
                                this
                        );
                        
                        // recupera concorsoDiProgettazione, fineConc, concorsoCrypted e mittente
                        boolean concorsoDiProgettazione = false;
                    	boolean fineConc = false;
                    	boolean concorsoCrypted = bandiManager.isConcorsoCrypted(getCurrentUser().getUsername(), codice);
                    	String mittente = this.destinatario;
                    	
                    	// recupera i dati del mittente
                    	if(concorsoCrypted) 
                    		mittente = bandiManager.getRagioneSocialeAnonima(getCurrentUser().getUsername(), codice);
                        if (StringUtils.isEmpty(mittente) && impresa != null && impresa.getDatiPrincipaliImpresa() != null)
                        	mittente = impresa.getDatiPrincipaliImpresa().getRagioneSociale();

                        // recupera i dati dei concorsi di progettazione
                        if (dettaglioGara != null && dettaglioGara.getDatiGeneraliGara() != null) {
                        	concorsoDiProgettazione = "9".equals(dettaglioGara.getDatiGeneraliGara().getIterGara())
                             						  || "10".equals(dettaglioGara.getDatiGeneraliGara().getIterGara());
                            if (concorsoDiProgettazione)
                            	fineConc = dettaglioGara.getDatiGeneraliGara().isFineConc();
                        }
                        
                        helper.setCodice(codice);
                        helper.setComunicazioneId(id);
                        helper.setComunicazioneApplicativo(applicativo);
                        helper.setIdDestinatario(idDestinatario);    		// progressivo del destinatario relativo alla comunicazione !!!
                        helper.setDestinatario(destinatario);
                        helper.setMittente(mittente);
                        helper.setImpresa(impresa);
                        helper.setConcorsoDiProgettazioneCrypted(concorsoCrypted);
                        helper.setConcorsoDiProgettazione(concorsoDiProgettazione);
                        helper.setConcEnded(fineConc);
                        helper.setTipoBusta(tipoBusta);
                        helper.setCodice2(codice2);
                        //helper.setDataInvio(retrieveDataInvio(this.ntpManager, this, "invio nuova comunicazione"));
                        
                        // imposta le info per la comunicazione di risposta
                        if (comunicazione != null) {
                            helper.setStazioneAppaltante(comunicazione.getStazioneAppaltante());
                            if(!isRettifica) {
                            	helper.setModello(comunicazione.getModello());
                            	helper.setTipoBusta(comunicazione.getTipoBusta());
                            }
                            helper.setDataScadenza(dataScadenza);
                            helper.setEntita(comunicazione.getEntita());
                            helper.setCodice2(comunicazione.getCodice2());
                        }
                        
                        // CONTRATTI LFS
                        if (contrattoLFS) {
                            helper.setEntita(PortGareSystemConstants.ENTITA_CONTRATTO_LFS);
                            helper.setCodice2(this.codice2);
                        } else if (entita != null && helper != null && helper.getEntita() == null)
                            helper.setEntita(entita);

                        // STIPULE
                        // recupera la stazione appaltante dal dettaglio della stipula
                        if (stipula) {
                            //helper.setEntita(PortGareSystemConstants.ENTITA_STIPULA); // non serve; viene recuperata dalla sessione
                            StipulaContrattoType dettStipula = this.stipuleManager.getDettaglioStipulaContratto(
                                    codice,
                                    getCurrentUser().getUsername(),
                                    true
                            );
                            if (dettStipula != null)
                                helper.setStazioneAppaltante(dettStipula.getCodiceSa());
                        }
                        
                        // prepara gli step del wizard e lo step iniziale 
                        helper.fillStepsNavigazione();

                        // aggiorna la sessione con le info necessarie
                        //
                	    session.put(PortGareSystemConstants.SESSION_ID_NUOVA_COMUNICAZIONE, helper);
                	    session.put(ComunicazioniConstants.SESSION_ID_FROM, from);
                	    session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_PAGINA, pagina);
                	    session.put(ComunicazioniConstants.SESSION_ID_DETTAGLIO_PRESENTE, true);
                	    
                	    // In caso si arrivasse da
                	    //    area personale -> archiviate/ricevute -> vai alla procedura -> invia nuova comunicazione
                	    // l'actionName e il namespace sarebbero null, causando
                	    // l'errore nell'annulamento della procedura di nuova comunicazione
                	    String action = (String) session.get(ComunicazioniConstants.SESSION_ID_ACTION_NAME);
                    	if(helper instanceof WizardRettificaHelper) {
                    		// aggiorna la sessioen con i parametri (actionname, namespace) la richiesta/invio di una rettifica
                    		if("bandi".equals(this.from)) {
	                	        session.put(ComunicazioniConstants.SESSION_ID_ACTION_NAME,
	                	        		(StringUtils.isEmpty(getCodice2()) ? "openRiepilogoOfferta" : "riepilogoOfferteDistinte"));	    	
	                	        session.put(ComunicazioniConstants.SESSION_ID_NAMESPACE, "/do/FrontEnd/GareTel");
	                	        session.put(ComunicazioniConstants.SESSION_ID_DETTAGLIO_PRESENTE, true);
                    		} else {
                    			// metti in sessione i dati per la gestione delle comunicazioni (DettaglioComunicazioneAction)
                    			if(this.id != null) {
                    				// NB: actionname e namespace vengono passati da "comunicazioni.xml"
                    				session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_IDCOMUNICAZIONE, this.id);
                            		session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_IDDESTINATARIO, this.idDestinatario);
                            		session.put(ComunicazioniConstants.SESSION_ID_DETTAGLIO_PRESENTE, true);
                    			}
                    		}
                    	} else if (StringUtils.isEmpty(action)) {
                    		// Salvo in sessione quelli passati come parametro
                	        // dal link per avviare una nuova comunicazione
                	        session.put(ComunicazioniConstants.SESSION_ID_ACTION_NAME, this.actionName);
                	        session.put(ComunicazioniConstants.SESSION_ID_NAMESPACE, this.namespace);
                	    } else if (StringUtils.isNotEmpty(this.actionName)) {
                	        session.put(ComunicazioniConstants.SESSION_ID_ACTION_NAME, this.actionName);
                	        session.put(ComunicazioniConstants.SESSION_ID_NAMESPACE, this.namespace);
                	    }
                    	
                    	// calcola l'url dello step da aprire
                        this.nextResultAction = InitNuovaComunicazioneAction
                                .setNextResultAction(helper.getStepNavigazione().firstElement());
                    }
                } else {
                    addActionError(this.getText("Errors.function.notEnabled"));
                    setTarget(CommonSystemConstants.PORTAL_ERROR);
                }
            } catch (Throwable e) {
                ApsSystemUtils.logThrowable(e, this, "initNuovaComunicazione");
                ExceptionUtils.manageExceptionError(e, this);
                setTarget(CommonSystemConstants.PORTAL_ERROR);
            }
        } else {
            addActionError(this.getText("Errors.sessionExpired"));
            setTarget(CommonSystemConstants.PORTAL_ERROR);
        }

        return getTarget();
    }

    /**
     * verifica se una richiesta e' fuori tempo massimo 
     * @throws ApsSystemException 
     * @throws UnknownHostException 
     * @throws SocketTimeoutException 
     */
    private boolean isFuoriTempoMassimo(Date dataScadenza) throws SocketTimeoutException, UnknownHostException, ApsSystemException {    
    	boolean fuoriTempo = false;
    	if (dataScadenza != null) {
	        // verifica se la richiesta e' oltre la data di scadenza
	        Date dataOra = this.ntpManager.getNtpDate();
	        if (dataOra != null && dataOra.after(dataScadenza)) {
	        	fuoriTempo = true;
	        	
	            // si tracciano solo i tentativi di accesso alle funzionalita' fuori tempo massimo
	            Event evento = new Event();
	            evento.setUsername(getCurrentUser().getUsername());
	            evento.setDestination(codice);
	            evento.setLevel(Event.Level.ERROR);
	            evento.setEventType(PortGareEventsConstants.ACCESSO_FUNZIONE);
	            evento.setIpAddress(getCurrentUser().getIpAddress());
	            evento.setSessionId(getRequest().getSession().getId());
	            evento.setMessage("Accesso alla funzione soccorso istruttorio (comunicazione con id " + this.id + ")");
	            evento.setDetailMessage(PortGareEventsConstants.DETAIL_MESSAGE_ERRORE_FUORI_TEMPO_MASSIMO +
	                    				" (" + UtilityDate.convertiData(dataOra, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS) + ")");
	            eventManager.insertEvent(evento);
	        }
	    }
	    return fuoriTempo;
    }

    /**
     * rimuovi/resetta i dati del wizard in sessione 
     */
    private void clearSession() {
    	session.remove(PortGareSystemConstants.SESSION_ID_NUOVA_COMUNICAZIONE);
	    session.remove(ComunicazioniConstants.SESSION_ID_FROM);
	    session.remove(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_PAGINA);
	    session.remove(ComunicazioniConstants.SESSION_ID_DETTAGLIO_PRESENTE);
	    session.remove(ComunicazioniConstants.SESSION_ID_ACTION_NAME);
	    session.remove(ComunicazioniConstants.SESSION_ID_NAMESPACE);
    }
    
    /**
     * crea un nuovo helper per le comunicazioni standard 
     */
    private WizardNuovaComunicazioneHelper createWizardNuovaComunicazioneHelper(ComunicazioneType comunicazione) {
    	WizardNuovaComunicazioneHelper helper = new WizardNuovaComunicazioneHelper();
    	
    	// imposta l'oggetto per la comunicazione di risposta
        if (comunicazione != null) {
            helper.setOggetto( StringUtils.abbreviate("RE: " + comunicazione.getOggetto(), 300) );
        }
	    	    
		return helper;
    }
    
    /**
     * crea un nuovo helper per le comunicazioni si soccorso istruttorio
     * @throws ApsException 
     */
    private WizardSoccorsoIstruttorioHelper createWizardSoccorsoIstruttorioHelper(ComunicazioneType comunicazione) throws ApsException {
	    WizardSoccorsoIstruttorioHelper soccorso = new WizardSoccorsoIstruttorioHelper();

        // imposta l'oggetto per la comunicazione di risposta
        if (comunicazione != null) {
        	List<DocumentazioneRichiestaType> documentiRichiestiDB = bandiManager
    	            .getDocumentiRichiestiComunicazione(
    	                    CommonSystemConstants.ID_APPLICATIVO_GARE,
    	                    comunicazione.getIdComunicazione());
        
        	soccorso.setOggetto( StringUtils.abbreviate("Rif: " + comunicazione.getOggetto(), 300) );
        	soccorso.setDocumentiRichiesti(documentiRichiestiDB);
        }
        
	    return soccorso;
    }
    
    /**
     * crea un nuovo helper per le comunicazioni di "invio rettifica" 
     * @throws ApsException 
     */
    private WizardRettificaHelper createWizardNuovaRettificaHelper(ComunicazioneType comunicazione) throws ApsException {
    	WizardRettificaHelper rettifica = null;
    	if(tipoBusta != null && tipoBusta.intValue() > 0) {
    		operazione = PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA;
    		
			// recupera la richiesta di rettifica e la risposta di accettazione
			List<DettaglioComunicazioneType> richieste = RichiesteRettificaList.getComunicazioniRettifica(
					comunicazioniManager
					, FasiRettifica.RICHIESTA
					, codice
					, codice2
					, tipoBusta.intValue()
					, getCurrentUser().getUsername()
			);
			
			final DettaglioComunicazioneType ultimaRichiesta = (richieste != null && richieste.size() > 0
				? richieste.get(richieste.size() - 1)
				: null
			);
			
			// recupera la risposta di accettazione
			DettaglioComunicazioneType accettata = null;
			if(ultimaRichiesta == null) {
				throw new ApsException("Errore in creazione del wizard di una rettifica per richiesta di rettifica mancante " 
						+ "(" + getCurrentUser().getUsername() + "," + codice + "," + codice2 + "," + tipoBusta + ")");
			} else {
				DettaglioComunicazioneType filtri = new DettaglioComunicazioneType();
				filtri.setApplicativo(CommonSystemConstants.ID_APPLICATIVO_GARE);
				filtri.setChiave1(codice);
				if(StringUtils.isNotEmpty(codice2))
					filtri.setChiave2(codice2);
				filtri.setIdRisposta(ultimaRichiesta.getId());
				filtri.setApplicativoRisposta(ultimaRichiesta.getApplicativo());
				filtri.setModello(WizardRettificaHelper.getFaseRettifica(tipoBusta.intValue(), FasiRettifica.ACCETTAZIONE));
				List<DettaglioComunicazioneType> accettate = comunicazioniManager.getElencoComunicazioni(filtri);
				if(accettate != null && accettate.size() > 0)
					accettata = accettate.stream()
									.filter(a -> RichiesteRettificaList.isRispostaRichiesta(a, ultimaRichiesta))
									.findFirst()
									.orElse(null);
			}
			
			if(accettata == null) {
				// NON DOVREBBE SUCCEDERE, MANCA IL TIPO BUSTA!!!
	    		throw new ApsException("Errore in creazione del wizard di una rettifica per risposta di accettazione della richiesta di rettifica (" 
	    								+ ultimaRichiesta.getApplicativo() + "," + ultimaRichiesta.getId() + ") mancante");
			}

			// crea l'helper per la rettifica
			rettifica = WizardRettificaHelper.creaInvioRettifica(
	    			getCurrentUser().getUsername()
	    			, codice 
	    			, codice2
	    			, progressivoOfferta
	    			, tipoBusta
	    			, operazione
	    	);
			
			
			
			// from = "bandi"		riepilogo offerta
			// from = "rispondi"	dettaglio comunicazione
			rettifica.setFrom(from);
			if(accettata.getDataScadenza() != null)
				rettifica.setDataScadenza( WizardNuovaComunicazioneHelper.calcolaDataOra(accettata.getDataScadenza(), accettata.getOraScadenza()) );
			
			applicativo = accettata.getApplicativo();
			id = accettata.getId();
    	} else {
    		// NON DOVREBBE SUCCEDERE, MANCA IL TIPO BUSTA!!!
    		throw new ApsException("Errore in creazione del wizard di una rettifica per tipoBusta sconosciuto"); 
    	}
    	return rettifica;
    }
    
}
