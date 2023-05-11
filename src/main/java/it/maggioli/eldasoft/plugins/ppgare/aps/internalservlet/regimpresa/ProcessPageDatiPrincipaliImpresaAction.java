package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.user.IUserManager;
import it.eldasoft.sil.portgare.datatypes.DatiImpresaDocument;
import it.eldasoft.sil.portgare.datatypes.ImpresaAggiornabileType;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityFiscali;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.AccountSSO;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.ntp.INtpManager;
import it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel.SendBusteAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.datiimpresa.DatiImpresaChecker;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.regimpresa.IRegistrazioneImpresaManager;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Action di gestione delle operazioni nella pagina dei dati principali
 * dell'impresa nel wizard di registrazione di un'impresa
 * 
 * @author Stefano.Sabbadin
 * @since 1.2
 */
public class ProcessPageDatiPrincipaliImpresaAction
	extends it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.ProcessPageDatiPrincipaliImpresaAction {
    /**
     * UID
     */
    private static final long serialVersionUID = 5426584774628243103L;

    private IBandiManager bandiManager;
    private IRegistrazioneImpresaManager registrazioneImpresaManager;
    private IUserManager userManager;
	private IEventManager eventManager;
	private ConfigInterface configManager;
	private IAppParamManager appParamManager;
	private INtpManager ntpManager;

	@Validate(EParamValidation.TIPO_IMPORT)
    private String tipoImport;
	@Validate(EParamValidation.TOKEN)
    private String token;
    private boolean checkRecuperaRegistrazione;
    private boolean ssoEnabled;
    
    
    public void setBandiManager(IBandiManager manager) {
    	bandiManager = manager;
    }
    
	public void setRegistrazioneImpresaManager(IRegistrazioneImpresaManager registrazioneImpresaManager) {
		this.registrazioneImpresaManager = registrazioneImpresaManager;
	}
	
    public void setUserManager(IUserManager userManager) {
        this.userManager = userManager;
    }

    public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}

	public void setConfigManager(ConfigInterface configManager) {
		this.configManager = configManager;
	}
	
	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

    public void setNtpManager(INtpManager ntpManager) {
      this.ntpManager = ntpManager;
    }

    public String getTipoImport() {
		return tipoImport;
	}

	public void setTipoImport(String tipoImport) {
		this.tipoImport = tipoImport;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	/**
     * Per sicurezza si valida che gia' in questa fase l'account indicato non
     * esista nel portale prima di inviarlo al backoffice, dove potrebbe
     * comunque risultare duplicato (caso limite, dovuto al fatto che un utente
     * si � registrato 2 volte sul portale in orari ravvicinati, per cui il
     * backoffice non ha ancora processato la prima richiesta e quindi non ha
     * trasferito l'account nel portale ed arriva gia' anche la seconda)
     */
    @Override
	public void validate() {
    	
		//if (this.getFieldErrors().size() > 0) return;
		try {
			// il processo di registrazione viene immediatamente deviato al processo
			// di recover utente se rientra nella casistica di operatore registrato
			// in B.O. ma stranamente non piu' presente nel portale.
	    	if (this.isRecuperaRegistrazione()) {
				return;
			}
			
			super.validate();
			if (this.getFieldErrors().size() > 0) {
				return;
			}
			
			// controllo la partita iva, che puo essere facoltativa per il
			// libero professionista o l'impresa sociale se previsto da
			// configurazione, mentre per tutti gli altri casi risulta
			// sempre obbligatoria
			boolean isLiberoProfessionista = this
					.getCodificheManager()
					.getCodifiche()
					.get(InterceptorEncodedData.LISTA_TIPI_IMPRESA_LIBERO_PROFESSIONISTA)
					.containsKey(this.getTipoImpresa());
			boolean isImpresaSociale = this
					.getCodificheManager()
					.getCodifiche()
					.get(InterceptorEncodedData.LISTA_TIPI_IMPRESA_SOCIALE)
					.containsKey(this.getTipoImpresa());
			boolean isPartitaIVAObbligatoriaLiberoProfessionista = !this
					.getCodificheManager().getCodifiche()
					.get(InterceptorEncodedData.CHECK_PARTITA_IVA_FACOLTATIVA_LIBERO_PROFESSIONISTA)
					.containsValue("1");
			boolean isPartitaIVAObbligatoriaImpresaSociale = !this
					.getCodificheManager().getCodifiche()
					.get(InterceptorEncodedData.CHECK_PARTITA_IVA_FACOLTATIVA_IMPRESA_SOCIALE)
					.containsValue("1");
			boolean isGruppoIVAAbilitato = this
					.getCodificheManager().getCodifiche()
					.get(InterceptorEncodedData.CHECK_GRUPPO_IVA)
					.containsValue("1");

			boolean impresaItaliana = "ITALIA".equalsIgnoreCase(this.getNazioneSedeLegale());
			if(impresaItaliana) {
				this.setAmbitoTerritoriale("1");
			}
			boolean OEItaliano = ("1".equalsIgnoreCase(this.getAmbitoTerritoriale()));
			
			// si predispone la variabile che memorizza, se presente, il campo Gruppo IVA ricevuto da interfaccia utente
			// nota: il gruppo IVA vale solo ed esclusivamente per imprese italiane
			boolean isVatGroup = false;
			
			if (!"".equals(this.getCodiceFiscale())
					&& !((UtilityFiscali.isValidCodiceFiscale(this.getCodiceFiscale(), impresaItaliana)) 
							|| (UtilityFiscali.isValidPartitaIVA(this.getCodiceFiscale(), impresaItaliana)))) {
				if (impresaItaliana) {
					this.addFieldError(
							"codiceFiscale",
							this.getText("Errors.wrongField",
									new String[] { this.getTextFromDB("codiceFiscale") }));
				} else {
					// si vede solo il campo id fiscale estero, pertanto si associa l'errore a tale campo
					this.addFieldError(
							"codiceFiscale",
							this.getText("Errors.wrongForeignFiscalField",
									new String[] { this.getTextFromDB("idFiscaleEstero") }));
				}
			}

			if (!"".equals(this.getCodiceFiscale())
					&& this._datiImpresaChecker.existsCodFiscale(this.getCodiceFiscale())) {
				this.addFieldError("codiceFiscale",
						this.getText("Errors.duplicateCF"));
			}
			
			if (!"".equals(this.getPartitaIVA())
					&& !UtilityFiscali.isValidPartitaIVA(this.getPartitaIVA(), impresaItaliana)) {
				if (impresaItaliana) {
					this.addFieldError(
							"partitaIVA",
							this.getText("Errors.wrongField",
									new String[] { this.getTextFromDB("partitaIVA") }));
				} else {
					// si vede solo il campo id fiscale estero, pertanto si associa l'errore a tale campo
					this.addFieldError(
							"codiceFiscale",
							this.getText("Errors.wrongForeignFiscalField",
									new String[] { this.getTextFromDB("idFiscaleEstero") }));
				}
			}

			if(OEItaliano) {
				// verifica CF e PIVA solo in caso di operatore economico italiano
				isVatGroup = WizardDatiImpresaHelper.isVatGroup(this.getVatGroup());
				
				// verifica se in BO e' stata abilitata la gestione del gruppo iva (SAP)
				if( !isGruppoIVAAbilitato ) {
					isVatGroup = false;
				}
				
				if (this.getPartitaIVA().length() == 0 
						&& ((!isLiberoProfessionista && !isImpresaSociale)
								|| (isLiberoProfessionista && isPartitaIVAObbligatoriaLiberoProfessionista)
								|| (isImpresaSociale && isPartitaIVAObbligatoriaImpresaSociale)
								|| isVatGroup)) {
					this.addFieldError(
							"partitaIVA",
							this.getText("Errors.requiredstring",
									new String[] { this.getTextFromDB("partitaIVA") }));
				}
				
				// verifica se la coppia (CF, P.IVA) e' gia' stata utilizzata...
				// in caso di gruppi IVA e' possibile avere N ditte con la stessa P.IVA ma con CF diversi
				boolean tmpPivaExists = (!"".equals(this.getPartitaIVA()) 
										 ? this._datiImpresaChecker.existsPartitaIVA(this.getPartitaIVA()) 
										 : false);
				
				if( !isVatGroup ) {
					// gruppo IVA = NO
					//
					// controlla se esiste un profilo temporaneo associato alla P.IVA
					if (tmpPivaExists) {
						this.addFieldError("partitaIVA", 
										   this.getText("Errors.duplicatePI"));
					}
					
					if("".equals(this.getPartitaIVA())
					   && ((isLiberoProfessionista && !isPartitaIVAObbligatoriaLiberoProfessionista) 
						    || (isImpresaSociale && !isPartitaIVAObbligatoriaImpresaSociale))) {
						// se siamo nel caso di libero professionista o impresa sociale
						// senza obbligo di indicare la partita IVA, allora controllo
						// solo mediante codice fiscale
						if (this.bandiManager.isImpresaRegistrata(this.getCodiceFiscale(), null, isVatGroup)) {
							this.addFieldError("codiceFiscale",
											   this.getText("Errors.impresaAlreadyPresent"));
						}
					} else if (!"".equals(this.getCodiceFiscale()) && !"".equals(this.getPartitaIVA())
							   && this.bandiManager.isImpresaRegistrata(this.getCodiceFiscale(), this.getPartitaIVA(), isVatGroup)) {
						// altrimenti controllo la presenza in backoffice quando ho entrambe i dati valorizzati
						// (sono comunque obbligatori entrambe)
						this.addFieldError("codiceFiscale",
										   this.getText("Errors.impresaAlreadyPresent"));
					}

				} else {
					// gruppo IVA = SI
					//
					// controlla se esiste un profilo temporaneo associato alla P.IVA
					if (tmpPivaExists) {
						this.addFieldError("partitaIVA", 
										   this.getText("Errors.duplicatePI"));
					}
					
					// per un gruppo IVA anche libero professionista/impresa sociale devono avere CF e P.IVA
					// quindi non c'e' differenza nel controllo sulla presenza di un'impresa registrata
					// in entrambi i casi si fa sempre il controllo con CF+PIVA 
					if( !"".equals(this.getPartitaIVA()) && !"".equals(this.getCodiceFiscale())) {
						if (this.bandiManager.isImpresaRegistrata(this.getCodiceFiscale(), this.getPartitaIVA(), isVatGroup)) {
							this.addFieldError("codiceFiscale",
											   this.getText("Errors.impresaAlreadyPresent"));
						}
					}
				}
				
				// se la gesione dei gruppi iva e' abilitata
				// verifica se la ditta potrebbe far parte di un gruppo IVA 
				// e l'utente potrebbe aver dimenticato di impostare il gruppo iva=si...
				// ...controlla se esiste gia' un'altra ditta con la stessa PIVA...
				if(isGruppoIVAAbilitato) {
					if( !isVatGroup && StringUtils.isNotEmpty(this.getPartitaIVA())) {
						// verifica se c'e' gia' un'altra ditta con la stessa PIVA
				    	boolean exists = this.bandiManager.isImpresaRegistrata(null, this.getPartitaIVA(), isVatGroup);			    	
						if(tmpPivaExists || exists) {
							this.addFieldError("partitaIVA",
											   this.getText("Errors.createGruppoIva"));
						}
					}
				}
			} else {
				// operatore UE o EXTRA EU
				if (this.bandiManager.isImpresaRegistrata(this.getCodiceFiscale(), null, false)) {
					this.addFieldError("idFiscaleEstero",
									   this.getText("Errors.impresaUEAlreadyPresent"));
				}
				
			}

		} catch (Throwable t) {
			throw new RuntimeException(
					"Errore durante la verifica dei dati richiesti per l'impresa "
							+ this.getRagioneSociale(), t);
		}
	}
    	
    
    /**
     * Verifica se esiste gia' una registrazione incompleta per un'impresa
     * (Es: dati presenti in BO ma cancellati lato Portale).
     * 
     * @return true se l'impresa va recuperata, false altrimenti
     * @throws ApsException 
     */
    private boolean isRecuperaRegistrazione() throws ApsException {
    	// se recupero un utente allora l'operatore risulta registrato in b.o.
		String username = this.bandiManager.getImpresaRegistrata(
				this.getCodiceFiscale(),
				this.getPartitaIVA(),
				this.getEmailRecapito(),
				this.getEmailPECRecapito());
		// se recupero la mail di riferimento allora l'operatore e' presente anche a portale
		boolean esisteMail = this._datiImpresaChecker.existsEmail(this.getMail());
		// recupero la registrazione SOLO SE l'operatore c'e' in B.O. e risulta registrato, ma non c'e' su portale
		this.checkRecuperaRegistrazione = (StringUtils.isNotEmpty(username) && !esisteMail);
		return this.checkRecuperaRegistrazione;
    }
    
    /**
     * "Avanti" nella form di compilazione dei dati impresa  
     */
	@Override
	public String next() {
		String target = SUCCESS;
		
		// verifica se recuperare i dati impresa 
		// o continuare con il secondo step della registrazione
		// dati dati impresa...
		if(this.checkRecuperaRegistrazione) {
			// vai al recovery della registrazione precedente
			target = "recover";
		} else {
			// vai al secondo step della registrazione impresa
			target = super.next();
		}
		
		return target;
	}
	
	/**
     * Recover di una registrazione impresa presente in BO come registrata ma sparita da portale (es. token scaduto).
     */
	public String questionRecovery() {
		String target = SUCCESS;
		return target;
	}
	
	/**
     * Annullo il processo di recovery e di registrazione.
     */
	public String cancelRecovery() {
		String target = "cancel";
		this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_REGISTRAZIONE_IMPRESA);
		this.session.remove(PortGareSystemConstants.SESSION_ID_PAGINA);
		return target;
	}
	
    /**
     * Confermo il processo di recovery: se va tutto bene 
     */
	public String confirmRecovery() {
		String target = SUCCESS;
		
		String username = null;
		String userIdSSO = null;
		
		try {
			username = this.bandiManager.getImpresaRegistrata(
					this.getCodiceFiscale(),
					this.getPartitaIVA(),
					this.getEmailRecapito(),
					this.getEmailPECRecapito());
			
			List<String> authentications = this.appParamManager.loadEnabledAuthentications();
			this.ssoEnabled = false;
			// si verifica la presenza dell'autenticazione con single sign on tra quelle visibili
			for(String auth : authentications){
				if(!auth.equals(PortGareSystemConstants.TIPOLOGIA_LOGIN_MAGGIOLI_AUTH_FORM)){
					this.ssoEnabled = true;
					break;
				}
			}
			if (this.ssoEnabled) {
              // nel caso di SSO configurata come disponibile, verifico se effettivamente la registrazione avviene all'interno di una
              // sessione di autenticazione SSO
			  AccountSSO accountSSO = (AccountSSO) this.session.get(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO);
			  if (accountSSO == null) {
			    // e' configurata la sso, ma la registrazione avviene senza esserci passati, pertanto resetto il flag
			    this.ssoEnabled = false;
			  } else {
			    userIdSSO = accountSSO.getLogin();
			  }
			}
			
            if (StringUtils.isEmpty(username)) {
              ApsSystemUtils.getLogger().error("Tentato ripristino senza recupero utenza per CF="
                  + this.getCodiceFiscale()
                  + ", PI="
                  + this.getPartitaIVA()
                  + ", mail="
                  + this.getEmailRecapito()
                  + ", pec="
                  + this.getEmailPECRecapito());
              this.addActionError(this.getText("Errors.recoverRegistrazione.userNotFound"));
              target = CommonSystemConstants.PORTAL_ERROR;
            } else {
              // in caso di registrazione tramite SSO si invia una mail con token all'utente
              // per completare il processo di riattivazione...
              if (this.ssoEnabled) {
                // invia all'impresa una mail con token per completare il processo di registrazione...
                target = this.inviaMailRecoveryRegistrazione(username, userIdSSO);
              } else {
                // recovery della registrazione standard...
                target = this.recoveryRegistrazioneImpresa(username);
              }
            }
		} catch (Throwable e) {
			this.addActionError(this.getText("Errors.recoverRegistrazione.unexpectedError", new String[] {username != null ? username : this.getRagioneSociale()}));
			ApsSystemUtils.logThrowable(e, this, "confirmRecovery");
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		
		return target;
	}
		
    /**
     * Recovery immediato di una registrazione standard NON agganciata a single sign on.
     * 
     * @param username
     *        username utenza da ripristinare
     * @return target struts con esito elaborazione
     */
	private String recoveryRegistrazioneImpresa(String username) {
		String target = SUCCESS;
		
		Event evento = new Event();
		evento.setUsername(username);
		evento.setIpAddress(this.getCurrentUser().getIpAddress());
		evento.setSessionId(this.getRequest().getSession().getId());
		evento.setEventType(PortGareEventsConstants.CREAZIONE_ACCOUNT);
		evento.setLevel(Event.Level.INFO);
		String message = "Ripristino registrazione account per impresa "
				+ "con mail di riferimento " + this.getMail()
				+ ", codice fiscale " + this.getCodiceFiscale()
				+ ", partita IVA " + this.getPartitaIVA();
		evento.setMessage(message);
		
		// reinserisce l'utente
		EsitoOutType inserimento = this.registrazioneImpresaManager.insertImpresa(
				username, 
				this.getEmailRecapito(), 
				this.getEmailPECRecapito(), 
				this.getRagioneSociale(), 
				this.getCodiceFiscale(), 
				this.getPartitaIVA());
		
		if (!inserimento.isEsitoOk()) {
			String msgErrore = this.getText("Errors.recoverRegistrazione.reinsertError", new String[]{username, inserimento.getCodiceErrore()});
			this.addActionError(msgErrore);
			target = CommonSystemConstants.PORTAL_ERROR;
			evento.setLevel(Event.Level.ERROR);
			evento.setDetailMessage(inserimento.getCodiceErrore());
		}
		this.eventManager.insertEvent(evento);
		
		return target;
	}
	
	/**
	 * (SSO) Invia all'impresa una mail con token per completare il processo di registrazione previa conferma via mail.
	 * @throws Exception 
	 */
	private String inviaMailRecoveryRegistrazione(String username, String userIdSSO)  {
      Event evento = new Event();
      evento.setUsername(username);
      evento.setIpAddress(this.getCurrentUser().getIpAddress());
      evento.setSessionId(this.getRequest().getSession().getId());
      evento.setEventType(PortGareEventsConstants.INVIO_MAIL_CONFERMA_RICEVUTA);
      evento.setLevel(Event.Level.INFO);
      evento.setMessage("Invio mail ripristino impresa");

      Date dataAttuale = SendBusteAction.retrieveDataInvio(this.ntpManager, this, "recupero registrazione");
      
      try {
        if (dataAttuale != null) {
          DatiImpresaDocument doc = this.bandiManager.getDatiImpresa(username, null);

          if(doc != null && doc.getDatiImpresa() != null && doc.getDatiImpresa().getImpresa() != null) {
              String data = UtilityDate.convertiData(dataAttuale, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
              
              String codiceFiscale = (StringUtils.isNotEmpty(doc.getDatiImpresa().getImpresa().getCodiceFiscale())
                          ? doc.getDatiImpresa().getImpresa().getCodiceFiscale() 
                          : null);
              String partitaIVA = (StringUtils.isNotEmpty(doc.getDatiImpresa().getImpresa().getPartitaIVA())
                          ? doc.getDatiImpresa().getImpresa().getPartitaIVA() 
                          : null);
              String delegateUser = userIdSSO;
              String destinatario = DatiImpresaChecker.getEmailRiferimento(
                          doc.getDatiImpresa().getImpresa().getRecapiti().getEmail(), 
                          doc.getDatiImpresa().getImpresa().getRecapiti().getPec());
              String token = this.generaToken(username, doc.getDatiImpresa().getImpresa(), delegateUser, dataAttuale);

              String message = "Invio mail ripristino impresa "
                  + "con mail di riferimento " + destinatario
                  + ", codice fiscale " + codiceFiscale
                  + ", partita IVA " + partitaIVA
                  + " da utente delegato " + delegateUser;
              evento.setMessage(message);

              // prepara l'url per la conferma di riattivazione...
              String baseUrl = this.configManager.getParam(SystemConstants.PAR_APPL_BASE_URL);
              StringBuilder url = new StringBuilder (); 
              url.append(baseUrl)
                 .append("it/ppgare_registr.wp?actionPath=/ExtStr2/do/FrontEnd/RegistrImpr/recoverRegistrazione.action")
                 .append("&currentFrame=7")
                 .append("&token=" + token);

              String text = this.formatI18nLabel("MAIL_REGISTRAZIONE_OE_RECUPERA_TESTO",
                      new Object[] { 
                          doc.getDatiImpresa().getImpresa().getRagioneSociale(), 
                          data,
                          baseUrl,
                          url.toString(),
                          delegateUser
                      });
              if( !this.ssoEnabled ) {
                  // rimuovi la riga 
                  // [[La richiesta � stata attivata mediante autenticazione di un soggetto con id {4}.]] 
                
                  // teoricamente questa condizione e' sempre falsa, ma se dovesse servire la funzionalita' anche per il recovery standard
                  // senza sso in questo modo il codice e' gia' compatibile
                  text = text.substring(0, text.indexOf("[[") + 2) + text.substring(text.indexOf("]]")); 
              } 
              text = text.replace("[[", "").replace("]]", "");
              
              String subject = this.formatI18nLabel("MAIL_REGISTRAZIONE_OE_RECUPERA_OGGETTO", 
                      new Object[] { null });
              
              this.getMailManager().sendMail(
                      text, 
                      subject, 
                      new String[] { destinatario },
                      null, 
                      null, 
                      CommonSystemConstants.SENDER_CODE);
              
          } else {
            this.addActionError(this.getText("Errors.recoverRegistrazione.userNotFound"));
            this.setTarget(CommonSystemConstants.PORTAL_ERROR);
            evento.setLevel(Event.Level.ERROR);
            evento.setDetailMessage("Utenza non reperita in appalti");
            // traccia l'errore di impresa non trovata...
            ApsSystemUtils.getLogger().error(
                    "Impresa con utente " + username + " non reperita in Appalti; mail recupero registrazione non inviata.");
          }       
        }
      } catch (Throwable e) {
        this.addActionError(this.getText("Errors.recoverRegistrazione.userNotFound"));
        this.setTarget(CommonSystemConstants.PORTAL_ERROR);
        evento.setError(e);
      } finally {
        this.eventManager.insertEvent(evento);
      }
				
		return this.getTarget();
	}
	
    /**
     * Completa il recupero della registrazione impresa con token inviato via mail. NOTA: attualmente la funzione viene utilizzata solo nel
     * caso di autenticazione SSO seguita dal recovery di un operatore economico.
     */
	public String recoveryRegistrazioneImpresa() {
        Date dataAttuale = SendBusteAction.retrieveDataInvio(this.ntpManager, this, "recupero registrazione");
        if (dataAttuale != null) {
          EsitoOutType esito = null;
          Event evento = new Event();
          evento.setIpAddress(this.getCurrentUser().getIpAddress());
          evento.setSessionId(this.getRequest().getSession().getId());
          evento.setEventType(PortGareEventsConstants.CREAZIONE_ACCOUNT);
          evento.setLevel(Event.Level.INFO);
          
          String username = null;
          
          try {
              String[] p = this.decodeToken(this.token);
              username = p[0];
              this.setEmailRecapito(p[1]);
              this.setEmailPECRecapito(p[2]);
              this.setRagioneSociale(p[3]);
              this.setCodiceFiscale(p[4]);
              this.setPartitaIVA(p[5]);
              String delegateUser = StringUtils.stripToNull(p[6]);
              String strDataOraInvio = p[7];
              
              String mailRiferimento = DatiImpresaChecker.getEmailRiferimento(this.getEmailPECRecapito(), this.getEmailRecapito());

              evento.setUsername(username);
              String message = "Ripristino account per impresa con mail di riferimento "
                      + mailRiferimento
                      + " e ragione sociale " + this.getRagioneSociale();
              evento.setMessage(StringUtils.substring(message, 0, 500));

              // il recovery dell'operatore deve avvenire entro 48h dalla ricezione mail
              Calendar limiteEsecuzioneOperazione = Calendar.getInstance();
              limiteEsecuzioneOperazione.setTime(new Date(Long.parseLong(strDataOraInvio)));
              limiteEsecuzioneOperazione.add(Calendar.HOUR, 48);
              
              if (dataAttuale.compareTo(limiteEsecuzioneOperazione.getTime()) > 0) {
                evento.setLevel(Event.Level.ERROR);
                evento.setDetailMessage("Evento ricevuto fuori limite massimo ("
                    + UtilityDate.convertiData(limiteEsecuzioneOperazione.getTime(), UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS)
                    + ")");
                this.addActionError(this.getText("Errors.userRegistration.unexpectedError", new String[] {"TIMEEXPIRED"}));
                this.setTarget(CommonSystemConstants.PORTAL_ERROR);
              } else {
                // evento entro i limiti di tempo previsti
                
                // si concatenano le operazioni derivate dal flusso di registrazione standard, ovvero l'inserimento dell'impresa nel
                // processo di registrazione e la successiva attivazione in seguito al processing lato backoffice
                
                esito = this.registrazioneImpresaManager.registerImpresa(
                    username, 
                    mailRiferimento,
                    this.getCodiceFiscale(),
                    this.getPartitaIVA(),
                    delegateUser,
                    WizardDatiImpresaHelper.isVatGroup(getVatGroup()));
                
                boolean noErrori = esito.isEsitoOk();
                
                if (noErrori) {
                  // si resetta il campo di accettazione versione al fine di forzare l'accettazione alla prima autenticazione
                  this.userManager.setAcceptanceVersion(username, null);
                }

                if (noErrori) {
                  // impresa inserita con profilo PTRI, ora occorre convertirla in modo da renderla attiva
                  esito = this.registrazioneImpresaManager.activateImpresa(username, this.getRagioneSociale());
                  noErrori = esito.isEsitoOk();
                }
                
                if (noErrori) {
                  if (this.session.get(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO) == null) {
                    // si crea una utenza sso temporanea per gestire i controlli sui messaggi della pagina di esito
                    AccountSSO accountSSO = new AccountSSO();
                    accountSSO.setLogin(username);
                    this.session.put(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO, accountSSO);
                  }
                } else {
                  evento.setLevel(Event.Level.ERROR);
                  evento.setDetailMessage("Codice errore: " + esito.getCodiceErrore());
                  this.addActionError(this.getText("Errors.userRegistration.unexpectedError", new String[] {esito.getCodiceErrore() }));
                  this.setTarget(CommonSystemConstants.PORTAL_ERROR);
                }
              }
              
          } catch(Throwable t) {
            evento.setError(t);
              this.addActionError(this.getText("Errors.recoverRegistrazione.unexpectedError", new String[] {username != null ? username : this.getRagioneSociale()}));
              ApsSystemUtils.logThrowable(t, this, "recoveryRegistrazioneImpresa");
              this.setTarget(CommonSystemConstants.PORTAL_ERROR);
          } finally {
            this.eventManager.insertEvent(evento);
          }
        }
		
		return this.getTarget();
	}

	
	/**
	 * Metodo implementato per ritornare la mail a cui &egrave; stato inviato
	 * con successo il token in caso di recover utente.
	 * 
	 * @return indirizzo email
	 */
	public String getMail() {
		return DatiImpresaChecker.getEmailRiferimento(
				this.getEmailPECRecapito(), this.getEmailRecapito());
	}

    /**
     * Estrae l'helper del wizard dati dell'impresa da utilizzare nei controlli
     * 
     * @return helper contenente i dati dell'impresa
     */
	@Override
    protected WizardDatiImpresaHelper getSessionHelper() {
		WizardDatiImpresaHelper helper = (WizardDatiImpresaHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_REGISTRAZIONE_IMPRESA);
		return helper;
    }

	/**
	 * effettua l'operazione di cifratura/decifratura  
	 * @throws InvalidKeyException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidAlgorithmParameterException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 */
	private static byte[] execCipher(int mode, byte[] value) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		String key = "qwertyuiopasdfghjklzxcvbnm-12345678";
		IvParameterSpec ivSpec = new IvParameterSpec("QWER-975".getBytes());
		DESKeySpec deskey = new DESKeySpec(key.getBytes());
		SecretKeySpec secretKey = new SecretKeySpec(deskey.getKey(), "DES");
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		cipher.init(mode, secretKey, ivSpec);
		return cipher.doFinal(value);
	}

    /**
     * Genera un token per il recupero della registrazione impresa. Il token contiene la data di creazione, pertanto avra' una scadenza.
     * @throws BadPaddingException 
     * @throws IllegalBlockSizeException 
     * @throws InvalidAlgorithmParameterException 
     * @throws NoSuchPaddingException 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeyException 
     */
    private String generaToken(
    		String username, 
    		ImpresaAggiornabileType impresa,
    		String delegateUser,
    		Date dataAttuale) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException 
    {
    	StringBuilder sb = new StringBuilder();
    	sb.append(username).append("|")
    	  .append((StringUtils.isNotEmpty(impresa.getRecapiti().getEmail()) ? impresa.getRecapiti().getEmail() : "")).append("|")
    	  .append((StringUtils.isNotEmpty(impresa.getRecapiti().getPec()) ? impresa.getRecapiti().getPec() : "")).append("|")
    	  .append((StringUtils.isNotEmpty(impresa.getRagioneSociale()) ? impresa.getRagioneSociale() : "")).append("|")
    	  .append((StringUtils.isNotEmpty(impresa.getCodiceFiscale()) ? impresa.getCodiceFiscale() : "")).append("|")
    	  .append((StringUtils.isNotEmpty(impresa.getPartitaIVA()) ? impresa.getPartitaIVA() : "")).append("|")
          .append((StringUtils.isNotEmpty(delegateUser) ? delegateUser : "")).append("|")
          .append(dataAttuale.getTime());
    	byte[] buffer = execCipher(Cipher.ENCRYPT_MODE, sb.toString().getBytes());
    	return Hex.encodeHexString(buffer);
    }
    
	/**
	 * decodifica un token per il recupero della registrazione impresa
	 * @throws DecoderException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws InvalidAlgorithmParameterException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	private String[] decodeToken(String token) throws DecoderException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		byte[] buffer = Hex.decodeHex(token.toCharArray());
		String x = new String( execCipher(Cipher.DECRYPT_MODE, buffer) );
		return x.split("\\|");
	}

}
