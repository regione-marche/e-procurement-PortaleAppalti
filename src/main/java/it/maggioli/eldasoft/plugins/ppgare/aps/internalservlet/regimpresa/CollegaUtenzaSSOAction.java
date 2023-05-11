package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.apsadmin.system.BaseAction;
import it.eldasoft.sil.portgare.datatypes.DatiImpresaDocument;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.AccountSSO;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event.Level;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.datiimpresa.DatiImpresaChecker;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.regimpresa.IRegistrazioneImpresaManager;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * ...
 * 
 * @author ...
 */
public class CollegaUtenzaSSOAction extends BaseAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 5814804123524968071L;

	private static final String DES_IV_PARAMETER 	= "QWER-975";
	private static final String DES_KEY 			= "qwertyuiopasdfghjklzxcvbnm-12345678";

    protected Map<String, Object> session;

	private IBandiManager bandiManager;
	private IRegistrazioneImpresaManager registrazioneImpresaManager;
    private IUserManager userManager;
	private IEventManager eventManager;
	private IAppParamManager appParamManager;

	@Validate(EParamValidation.TOKEN)
	private String token;
	@Validate(EParamValidation.USERNAME)
	private String username;
	@Validate(EParamValidation.RAGIONE_SOCIALE)
	private String ragioneSociale;
	@Validate(EParamValidation.CODICE_FISCALE)
	private String codiceFiscale;
	@Validate(EParamValidation.PARTITA_IVA)
	private String partitaIVA;
	@Validate(EParamValidation.EMAIL)
	private String email;
	@Validate(EParamValidation.EMAIL)
	private String pec;
	private boolean collegaUtenza;
	private boolean isVatGroup;

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
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

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRagioneSociale() {
		return ragioneSociale;
	}

	public void setRagioneSociale(String ragioneSociale) {
		this.ragioneSociale = ragioneSociale;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public String getPartitaIVA() {
		return partitaIVA;
	}

	public void setPartitaIVA(String partitaIVA) {
		this.partitaIVA = partitaIVA;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPec() {
		return pec;
	}

	public void setPec(String pec) {
		this.pec = pec;
	}

	public boolean isCollegaUtenza() {
		return collegaUtenza;
	}

	public void setCollegaUtenza(boolean collegaUtenza) {
		this.collegaUtenza = collegaUtenza;
	}

	/**
	 * Apre la pagina riepilogativa con i dati dell'utenza agganciata per il ripristino.
	 */
    public String open() {
      String target = SUCCESS;

      Event evento = new Event();
      evento.setIpAddress(this.getCurrentUser().getIpAddress());
      evento.setSessionId(this.getRequest().getSession().getId());

      target = checkFunzioneDisponibile(target, evento);

      if (!SUCCESS.equals(target)) {
        evento.setEventType(PortGareEventsConstants.ACCESSO_FUNZIONE);
        evento.setMessage("Ripristino utenza con collegamento single sign on ricevuto da import massivo");
        this.eventManager.insertEvent(evento);
      } else {
        // funzione attiva, per cui si prova a recuperare i dati
        this.collegaUtenza = false;
        this.username = null;
        try {
          extractDatiImpresa();
        } catch (Exception e) {
          this.addActionError(this.getText("Errors.unexpected"));
          ApsSystemUtils.logThrowable(e, this, "open");
          target = INPUT;
        }
      }

      return target;
    }

	private void extractDatiImpresa() throws Exception {
		String[] p = CollegaUtenzaSSOAction.decodeToken(this.token);

		this.username = p[0]; 	// username
		//String ... = p[1]; 	// DES Key (vedi in testa alla classe)

		// recupera i dati impresa registrati in BO
		DatiImpresaDocument impresa = this.bandiManager.getDatiImpresa(this.username, null);

		if(impresa != null && impresa.getDatiImpresa() != null && impresa.getDatiImpresa().getImpresa() != null ) {
			this.ragioneSociale = impresa.getDatiImpresa().getImpresa().getRagioneSociale();
			this.codiceFiscale = impresa.getDatiImpresa().getImpresa().getCodiceFiscale();
			this.partitaIVA = impresa.getDatiImpresa().getImpresa().getPartitaIVA();
			if(impresa.getDatiImpresa().getImpresa().getRecapiti() != null) {
				this.email = impresa.getDatiImpresa().getImpresa().getRecapiti().getEmail();
				this.pec = impresa.getDatiImpresa().getImpresa().getRecapiti().getPec();
				this.isVatGroup = WizardDatiImpresaHelper.isVatGroup(impresa.getDatiImpresa().getImpresa().getGruppoIva());
			}
		}
	}

	/**
	 * Esegue il ripristino dell'utenza prepara il login al servizio di autenticazione SPID 
	 */
    public String collegaUtenza() {
      String target = SUCCESS;

      Event evento = new Event();
      evento.setIpAddress(this.getCurrentUser().getIpAddress());
      evento.setSessionId(this.getRequest().getSession().getId());

      try {
        target = checkFunzioneDisponibile(target, evento);

        if (!SUCCESS.equals(target)) {
          evento.setEventType(PortGareEventsConstants.ACCESSO_FUNZIONE);
          evento.setMessage("Ripristino utenza con collegamento single sign on ricevuto da import massivo");
          this.eventManager.insertEvent(evento);
        } else {
          // funzione attiva, per cui si prova a recuperare i dati
          this.collegaUtenza = true;
          if (StringUtils.isNotEmpty(this.token)) {

            extractDatiImpresa();
            
            String mailRiferimento = DatiImpresaChecker.getEmailRiferimento(this.pec, this.email);

            evento.setUsername(this.username);
            evento.setEventType(PortGareEventsConstants.CREAZIONE_ACCOUNT);
            String message = "Ripristino account per impresa con mail di riferimento "
                + mailRiferimento
                + " e ragione sociale " + this.getRagioneSociale();
            evento.setMessage(StringUtils.substring(message, 0, 500));
            evento.setLevel(Event.Level.INFO);

            AccountSSO accountSSO = (AccountSSO) this.session.get(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO);
            
            if (accountSSO == null) {
              // la presente esecuzione avviene esclusivamente entrando previa single sign on
              target = CommonSystemConstants.PORTAL_ERROR;
              evento.setLevel(Event.Level.ERROR);
              evento.setDetailMessage("Azione eseguita senza previa autenticazione SSO del soggetto delegato");
            } else {
              String delegateUser = accountSSO.getLogin();

              // si concatenano le operazioni derivate dal flusso di registrazione standard, ovvero l'inserimento dell'impresa nel
              // processo di registrazione e la successiva attivazione in seguito al processing lato backoffice
              
              EsitoOutType esito = this.registrazioneImpresaManager.registerImpresa(
                  this.username, 
                  mailRiferimento,
                  this.getCodiceFiscale(),
                  this.getPartitaIVA(),
                  delegateUser,
                  isVatGroup);
              
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
              
              if (!noErrori) {
                evento.setLevel(Event.Level.ERROR);
                evento.setDetailMessage("Codice errore: " + esito.getCodiceErrore());
                this.addActionError(this.getText("Errors.userRegistration.unexpectedError", new String[] {esito.getCodiceErrore() }));
                target = CommonSystemConstants.PORTAL_ERROR;
              }
            }
          }
        }
          
      } catch (Throwable t) {
        this.addActionError(this.getText("Errors.unexpected"));
        ApsSystemUtils.logThrowable(t, this, "collegaUtenza");
        target = INPUT;
      } finally {
        this.eventManager.insertEvent(evento);
      }

      return target;
    }

	/**
	 * Verifica se la funzionalita' deve essere operativa oppure no a seconda della data attuale di utilizzo e della data riportata in
	 * configurazione.
	 * 
	 * @param target
	 *        target di origine
	 * @return target eventualmente modificato in caso di blocco
	 */
	private String checkFunzioneDisponibile(String target, Event evento) {
		 String strDataTermine = (String) this.appParamManager.getConfigurationValue(AppParamManager.IMPORT_UTENTI_DATA_TERMINE);
		 Date dataTermine = null;
		 if (StringUtils.isNotEmpty(strDataTermine)) {
			 try {
				 dataTermine = new SimpleDateFormat("dd/MM/yyyy").parse(strDataTermine);
			 } catch (ParseException e) {
				 ApsSystemUtils.getLogger().error("Valore non valido per la configurazione " + AppParamManager.IMPORT_UTENTI_DATA_TERMINE, e);
			 }
		 }

		 if (dataTermine == null) {
			 // blocco l'utilizzo se non e' impostata la configurazione
			 ApsSystemUtils.getLogger().error(this.getTextFromDefaultLocale("Errors.importUserNotSet"));
			 evento.setLevel(Level.ERROR);
			 evento.setDetailMessage(this.getTextFromDefaultLocale("Errors.importUserNotSet"));
			 this.addActionError(this.getText("Errors.importUserNotConfigured"));
			 target = CommonSystemConstants.PORTAL_ERROR;
		 } else {
			 // si calcola la data attuale
			 Calendar calendar = Calendar.getInstance();
			 calendar.set(Calendar.HOUR_OF_DAY, 0);
			 calendar.set(Calendar.MINUTE, 0);
			 calendar.set(Calendar.SECOND, 0);
			 calendar.set(Calendar.MILLISECOND, 0);
			 Date dataAttuale = calendar.getTime();

			 if (dataAttuale.compareTo(dataTermine) > 0) {
				 // blocco l'utilizzo se e' scaduto il termine
				 ApsSystemUtils.getLogger().error(this.getTextFromDefaultLocale("Errors.importUserIsExpired"));
	             evento.setLevel(Level.ERROR);
	             evento.setDetailMessage(this.getTextFromDefaultLocale("Errors.importUserIsExpired"));
				 this.addActionError(this.getText("Errors.importUserNotConfigured"));
				 target = CommonSystemConstants.PORTAL_ERROR;
			 }
		 }
		 return target;
	}		

	/**
	 * Prepara l'algoritmo per la decifratura.
	 */
	private static Cipher getDESCipher(int mode) 
		throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException { 
		IvParameterSpec ivSpec = new IvParameterSpec(DES_IV_PARAMETER.getBytes());
		DESKeySpec deskey = new DESKeySpec(DES_KEY.getBytes());
		SecretKeySpec secretKey = new SecretKeySpec(deskey.getKey(), "DES");
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		cipher.init(mode, secretKey, ivSpec);
		return cipher; 
	}

	/**
	 * Genera un token.
	 * @throws InvalidAlgorithmParameterException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 */
	public static String generaToken(String username) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		// token = {USERNAME}|{DES_KEY}
		String t = "";
        String token = username +"|" + DES_KEY;
        Cipher cipher = getDESCipher(Cipher.ENCRYPT_MODE);
        byte[] buffer = cipher.doFinal( token.getBytes() );
        t = Hex.encodeHexString(buffer);
		return t;
	}

	/**
	 * Decodifica un token.
	 * @throws DecoderException 
	 * @throws InvalidAlgorithmParameterException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 */
	public static String[] decodeToken(String token) throws DecoderException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		// token = {USERNAME}|{DES_KEY}
		String t = "";
        byte[] buffer = Hex.decodeHex(token.toCharArray());
        Cipher cipher = getDESCipher(Cipher.DECRYPT_MODE);
        t = new String( cipher.doFinal(buffer) );
		return t.split("\\|");
	}
	
}
