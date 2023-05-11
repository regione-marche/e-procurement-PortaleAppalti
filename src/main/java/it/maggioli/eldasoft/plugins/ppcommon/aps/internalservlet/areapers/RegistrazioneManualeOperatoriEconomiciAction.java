package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.areapers;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.plugins.jpmail.aps.services.mail.IMailManager;
import it.eldasoft.sil.portgare.datatypes.RegistrazioneImpresaDocument;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event.Level;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.monitoraggio.FlussiInviatiKO;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa.WizardDatiImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.regimpresa.WizardRegistrazioneImpresaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.datiimpresa.DatiImpresaChecker;
import org.apache.commons.codec.binary.Hex;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Registrazione manuale degli operatori economici da verificare
 * 
 * ...
 */
public class RegistrazioneManualeOperatoriEconomiciAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -8360867737551321392L;
	
	private static final String SUCCESS_VIEW 	= "successView";
	private static final String SUCCESS_ACCEPT 	= "successAccept";
	private static final String SUCCESS_REJECT 	= "successReject";
	
	private Map<String, Object> session;
	private IComunicazioniManager comunicazioniManager;
	private IMailManager mailManager;
	private ConfigInterface configManager;
	private IEventManager eventManager;	

	@Validate
	private List<FlussiInviatiKO> listaRegistazioni = new ArrayList<>();
	@Validate(EParamValidation.DIGIT)
	private String idComunicazione;
	@Validate(EParamValidation.URL)
	private String urlPdf;
	private boolean rejectQuestion;
	@Validate(EParamValidation.MOTIVO_RIFIUTO)
	private String motivoRifiuto;
 

	@Override
	public void setSession(Map<String, Object> arg) {
		this.session = arg;	
	}

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}
	
	public void setEventManager(IEventManager eventManager){
		this.eventManager = eventManager;
	}

	public void setMailManager(IMailManager mailManager) {
		this.mailManager = mailManager;
	}
	
	public void setConfigManager(ConfigInterface configManager) {
		this.configManager = configManager;
	}

	public List<FlussiInviatiKO> getListaRegistazioni() {
		return listaRegistazioni;
	}

	public void setListaRegistazioni(List<FlussiInviatiKO> listaRegistazioni) {
		this.listaRegistazioni = listaRegistazioni;
	}

	public String getIdComunicazione() {
		return idComunicazione;
	}

	public void setIdComunicazione(String idComunicazione) {
		this.idComunicazione = idComunicazione;
	}
	
	public boolean isRejectQuestion() {
		return rejectQuestion;
	}

	public void setRejectQuestion(boolean rejectQuestion) {
		this.rejectQuestion = rejectQuestion;
	}

	public String getMotivoRifiuto() {
		return motivoRifiuto;
	}

	public void setMotivoRifiuto(String motivoRifiuto) {
		this.motivoRifiuto = motivoRifiuto;
	}

	public String getUrlPdf() {
		return urlPdf;
	}

	public void setUrlPdf(String urlPdf) {
		this.urlPdf = urlPdf;
	}

	/**
	 * ...
	 */
	@SkipValidation
	public String open() {
		String target = SUCCESS;
		
		this.listaRegistazioni = null;
		this.rejectQuestion = false;
		
		// funzionalita' disponibile solo per utenti amministratori loggati
		if (this.getCurrentUser() == null
			 || !this.isCurrentUserMemberOf(SystemConstants.ADMIN_ROLE)) {
			this.addActionError(this.getText("Errors.function.notEnabled"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			try {
				// trova tutte le comunicazioni in stato ATTESA DI VERIFICA (w_invcom.stato = 18)
				DettaglioComunicazioneType filtri = new DettaglioComunicazioneType();
				filtri.setApplicativo(CommonSystemConstants.ID_APPLICATIVO);
				filtri.setTipoComunicazione(PortGareSystemConstants.RICHIESTA_REGISTRAZIONE_PORTALE);
				filtri.setStato(CommonSystemConstants.STATO_COMUNICAZIONE_ATTESA_VERIFICA);
				List<DettaglioComunicazioneType> comunicazioni = this.comunicazioniManager
							.getElencoComunicazioni(filtri);
				
				if (comunicazioni.size() > 0) {
					this.listaRegistazioni = new ArrayList<FlussiInviatiKO>();
					for (DettaglioComunicazioneType comunicazione : comunicazioni) {
						FlussiInviatiKO registrazione = new FlussiInviatiKO();
						registrazione.setIdComunicazione(comunicazione.getId());
						registrazione.setDataInserimento(comunicazione.getDataInserimento());
						registrazione.setUtente(comunicazione.getChiave1());
						registrazione.setMittente(comunicazione.getMittente());
						registrazione.setTipoComunicazione(comunicazione.getTipoComunicazione());
						this.listaRegistazioni.add(registrazione);
					}
				}
				
			} catch (ApsException e) {
				//String msgErrore = this.getText("Errors.opKO.mailError", new String[]{this.getEmailAttivazione()});
				String msgErrore = e.getMessage(); ///????
				this.addActionError(msgErrore);
				target = ERROR;
			}
		}

		return target;	
	}

	/**
	 * visualizza il pdf dei dati impresa
	 */
	@SkipValidation
	public String view() {
		String target = SUCCESS_VIEW;
		
		// funzionalita' disponibile solo per utenti amministratori loggati
		if (this.getCurrentUser() == null
			|| !this.isCurrentUserMemberOf(SystemConstants.ADMIN_ROLE)) 
		{
			this.addActionError(this.getText("Errors.function.notEnabled"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			if (this.idComunicazione != null) {
				try {
					// recupera la comunicazione FS1 ed estrai l'xml dai dati impresa
					ComunicazioneType comunicazione = this.getComunicazione(this.idComunicazione);
					WizardRegistrazioneImpresaHelper impresa = this.getDatiImpresaFromComunicazione(comunicazione);
					
					// genera il pdf chiamando la action relativa nel package "DatiImpresa"
					this.session.put(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA, impresa);
					this.urlPdf = "/do/FrontEnd/DatiImpr/createPdf.action" +
								  "?urlPage=/it/ppcommon_registra_op_manuale.wp" + 
								  "&amp;currentFrame="; 
								  // + "&amp;" + (String)this.getRequest().getAttribute("tokenHrefParams"); // SERVE ???
					
				} catch (Exception e) {
					this.addActionError(this.getText(
							"Errors.regManuale.updateError",
							new String[] {PortGareSystemConstants.RICHIESTA_REGISTRAZIONE_PORTALE, this.idComunicazione }));
					target = ERROR;
				}
			} else {
				// errore, serve avere id comunicazione impostato
				this.addActionError(this.getText("Errors.regManuale.noComselected"));
				target = ERROR;
			}
		}
		
		return target;
	}
	
	/**
	 * valida la registrazione ed aggiornala la comunicazione FS1
	 */
	@SkipValidation
	public String accept() {
		String target = SUCCESS_ACCEPT;
		
		// funzionalita' disponibile solo per utenti amministratori loggati
		if (this.getCurrentUser() == null
			|| !this.isCurrentUserMemberOf(SystemConstants.ADMIN_ROLE)) 
		{
			this.addActionError(this.getText("Errors.function.notEnabled"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			if (this.idComunicazione != null) {
				Event evento = null;
				try {
					evento = new Event();
					evento.setUsername(this.getCurrentUser().getUsername());
					evento.setIpAddress(this.getCurrentUser().getIpAddress());
					evento.setSessionId(this.getRequest().getSession().getId());
					evento.setMessage("Registrazione approvata con id=" + this.idComunicazione + " allo stato 5");					
					evento.setLevel(Level.INFO);
					evento.setEventType(PortGareEventsConstants.CAMBIO_STATO_COMUNICAZIONE);
					
					// aggiorna la comunicazione FS1 mettendola in stato DA PROCESSARE...
					ComunicazioneType comunicazione = this.getComunicazione(this.idComunicazione);
					if(comunicazione != null) {
						DettaglioComunicazioneType[] comunicazioni = new DettaglioComunicazioneType[1];
						comunicazioni[0] = comunicazione.getDettaglioComunicazione();
						
						this.comunicazioniManager.updateStatoComunicazioni(
								comunicazioni, 
								CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
						
						this.addActionMessage(this.getText("Info.regManuale.approved"));
						
					} else {
						// errore: nessuna comunicazione estratta a partire dalla chiave
						evento.setLevel(Level.ERROR);
						this.addActionError(this.getText(
								"Errors.regManuale.updateError",
								new String[] {PortGareSystemConstants.RICHIESTA_REGISTRAZIONE_PORTALE, this.idComunicazione }));
						target = ERROR;
					}
				} catch (Exception e) {
					evento.setLevel(Level.ERROR);
					evento.setError(e);
					this.addActionError(this.getText(
							"Errors.regManuale.updateError",
							new String[] {PortGareSystemConstants.RICHIESTA_REGISTRAZIONE_PORTALE, this.idComunicazione }));
					ApsSystemUtils.logThrowable(e, this, "accept");
					target = ERROR;
				} finally {
					if(evento != null) {
						this.eventManager.insertEvent(evento);
					}
				}
			} else {
				// errore, serve avere id comunicazione impostato
				this.addActionError(this.getText("Errors.regManuale.noComselected"));
				target = ERROR;
			}
		}
		
		return target;
	}
	
	/**
	 * rifiuta la registrazione ed invia una mail con la motivazione all'operatore
	 */
	@SkipValidation
	public String reject() {
		String target = SUCCESS;
		
		// funzionalita' disponibile solo per utenti amministratori loggati
		if (this.getCurrentUser() == null
			|| !this.isCurrentUserMemberOf(SystemConstants.ADMIN_ROLE)) 
		{
			this.addActionError(this.getText("Errors.function.notEnabled"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			if (this.idComunicazione != null) {
				// apri il forma con la richiesta di motivazione per il "Rifiuto"
				this.setRejectQuestion(true);
			} else {
				// errore, serve avere id comunicazione impostato
				this.addActionError(this.getText("Errors.regManuale.noComselected"));
				target = ERROR;
			}
		}
		
		return target;
	}

	/**
	 * ...
	 */
	@SkipValidation
	public String rejectCancel() {
		String target = SUCCESS_REJECT;
		
		// funzionalita' disponibile solo per utenti amministratori loggati
		if (this.getCurrentUser() == null
			|| !this.isCurrentUserMemberOf(SystemConstants.ADMIN_ROLE)) 
		{
			this.addActionError(this.getText("Errors.function.notEnabled"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			// torna alla lista delle comunicazioni in attesa di validazione
		}
		
		return target;
	}

	/**
	 * ...
	 */
	public String rejectConfirm() {
		String target = SUCCESS_REJECT;
		
		this.setRejectQuestion(true);
		
		// funzionalita' disponibile solo per utenti amministratori loggati
		if (this.getCurrentUser() == null
			|| !this.isCurrentUserMemberOf(SystemConstants.ADMIN_ROLE)) 
		{
			this.addActionError(this.getText("Errors.function.notEnabled"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
		
			if (this.idComunicazione != null) {
				Event evento = null;
				try {
					ComunicazioneType comunicazione = this.getComunicazione(this.idComunicazione);
					
					// genera il token...
					String token = this.generaToken(comunicazione);
					
					evento = new Event();
					evento.setUsername(this.getCurrentUser().getUsername());
					evento.setIpAddress(this.getCurrentUser().getIpAddress());
					evento.setSessionId(this.getRequest().getSession().getId());
					evento.setMessage("Registrazione respinta: id=" + this.idComunicazione + 
									  ", token=" + token + ", motivazione=" + this.motivoRifiuto);
					evento.setLevel(Level.INFO);
					evento.setEventType(PortGareEventsConstants.CAMBIO_STATO_COMUNICAZIONE);
					
					// aggiorna la comunicazione FS1 mettendola in stato BOZZA...
					if(comunicazione != null) {
						DettaglioComunicazioneType[] comunicazioni = new DettaglioComunicazioneType[1];
						comunicazioni[0] = comunicazione.getDettaglioComunicazione();
						
						this.comunicazioniManager.updateStatoComunicazioni(
								comunicazioni, 
								CommonSystemConstants.STATO_COMUNICAZIONE_BOZZA);
						
						// invia una mail all'operatore contenente link e token e la motivazione 
						// per cui e' stata respinta la registrazione
						WizardDatiImpresaHelper impresa = this.getDatiImpresaFromComunicazione(comunicazione);
						
						this.sendMailRifiuto(token, this.motivoRifiuto, impresa);
						
						this.addActionMessage(this.getText("Info.regManuale.rejected"));
						
					} else {
						// errore: nessuna comunicazione estratta a partire dalla chiave
						evento.setLevel(Level.ERROR);
						this.addActionError(this.getText(
								"Errors.regManuale.updateError",
								new String[] {PortGareSystemConstants.RICHIESTA_REGISTRAZIONE_PORTALE, this.idComunicazione }));
						target = INPUT;
					}
					
				} catch (Exception e) {
					evento.setLevel(Level.ERROR);
					evento.setError(e);
					this.addActionError(this.getText(
							"Errors.regManuale.updateError",
							new String[] {PortGareSystemConstants.RICHIESTA_REGISTRAZIONE_PORTALE, this.idComunicazione }));
					ApsSystemUtils.logThrowable(e, this, "accept");
					target = INPUT;
				} finally {
					if(evento != null) {
						this.eventManager.insertEvent(evento);
					}
				}
			} else {
				// errore, serve avere id comunicazione impostato
				this.addActionError(this.getText("Errors.regManuale.noComselected"));
				target = INPUT;
			}
		}
		
		return target;
	}

//	/**
//	 * ... 
//	 */
//	@Override
//	public void validate() {
//		super.validate();
//		if (this.getFieldErrors().size() > 0) {
//			return;
//		}
//	}
//	
	/**
	 * recupera la comunicazione selezionata 
	 */
	private ComunicazioneType getComunicazione(String idComunicazione) throws Exception {
		ComunicazioneType comunicazione = null;
		try {
			comunicazione = this.comunicazioniManager
				.getComunicazione(CommonSystemConstants.ID_APPLICATIVO, Long.parseLong(this.idComunicazione));
		} catch (NumberFormatException e) {
			throw new Exception(e);
		}
		return comunicazione;
	}

	/**
	 * recupera i dati impresa dalla comunicazione 
	 */
	private WizardRegistrazioneImpresaHelper getDatiImpresaFromComunicazione(ComunicazioneType comunicazione) throws Exception {
		WizardRegistrazioneImpresaHelper impresa = null;
	
		RegistrazioneImpresaDocument doc = (RegistrazioneImpresaDocument) WizardRegistrazioneImpresaHelper
			.getXmlDocumentRegistrazione(comunicazione);
		if(doc == null) {
			// non dovrebbe succedere mai...si inserisce questo controllo 
			// per blindare il codice da eventuali comportamenti anomali
			String msg = this.getText("Errors.aggiornamentoAnagrafica.xmlAggiornamentoNotFound");
			this.addActionError(msg);
			throw new Exception(msg);
		} else {
			impresa = new WizardRegistrazioneImpresaHelper(doc.getRegistrazioneImpresa().getDatiImpresa());
		}
		
		return impresa;
	}
	
	/**
	 * effettua l'operazione di cifratura/decifratura  
	 * @throws InvalidKeyException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 */
	private static byte[] execCipher(int mode, byte[] value) throws Exception {
		IvParameterSpec ivSpec = new IvParameterSpec("QWER-975".getBytes());
		DESKeySpec deskey = new DESKeySpec("qwertyuiopasdfghjklzxcvbnm-1234567890".getBytes());
		SecretKeySpec secretKey = new SecretKeySpec(deskey.getKey(), "DES");
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		cipher.init(mode, secretKey, ivSpec);
		return cipher.doFinal(value);
	}
	
	/**
	 * genera un token  
	 * 
	 * @throws InvalidAlgorithmParameterException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchProviderException 
	 * @throws NoSuchAlgorithmException 
	 * @throws UnsupportedEncodingException 
	 * @throws InvalidKeyException 
	 */
	private String generaToken(ComunicazioneType comunicazione) throws Exception {
		String token = "";
		if(comunicazione != null) {
			// Il token dovrà essere codificato base64 come avviene per gli altri token, 
			// non necessariamente deve essere salvato nella tabella di memorizzazione token, 
			// deve contenere all'interno un riferimento all'operatore e all'id comunicazione da riaprire. 
			// E' ipotizzabile inoltre che non abbia scadenza.
			StringBuilder sb = new StringBuilder();
			sb.append(comunicazione.getDettaglioComunicazione().getChiave1()).append("|")	// username
			  .append(comunicazione.getDettaglioComunicazione().getId()).append("|")		// id comunicazione
			  .append("");																	// data scadenza token (default nessuna scadenza)					
			
			byte[] buffer = execCipher(Cipher.ENCRYPT_MODE, sb.toString().getBytes());
			token = Hex.encodeHexString(buffer);
		}
		return token;
	}

	/**
	 * decodifica un token  
	 * 
	 * @throws InvalidAlgorithmParameterException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchProviderException 
	 * @throws NoSuchAlgorithmException 
	 * @throws UnsupportedEncodingException 
	 * @throws InvalidKeyException 
	 */
	public static String[] decodeToken(String token) throws Exception {
		byte[] buffer = Hex.decodeHex(token.toCharArray());
		String x = new String( execCipher(Cipher.DECRYPT_MODE, buffer) );
		return x.split("\\|");
	}
	
	/**
	 * ...
	 * @throws ApsSystemException 
	 */
	private void sendMailRifiuto(
			String token, 
			String motivazione, 
			WizardDatiImpresaHelper impresa) throws ApsSystemException 
	{
		String email = DatiImpresaChecker.getEmailRiferimento(
				impresa.getDatiPrincipaliImpresa().getEmailPECRecapito(),
				impresa.getDatiPrincipaliImpresa().getEmailRecapito());
	
		// recupera il langCode della ditta per localizzare il testo della mail
		String nazione = impresa.getDatiPrincipaliImpresa().getNazioneSedeLegale();
		// l'operatore italiano riceve una mail in italiano, altrimenti la riceve in inglese
		String langCode = this.getDefaultLocale();
		if (!"ITALIA".equalsIgnoreCase(impresa.getDatiPrincipaliImpresa().getNazioneSedeLegale())) {
			langCode = Locale.ENGLISH.getLanguage();
		}
		
		String subject = this.getI18nLabel("MAIL_REG_MANUALE_OP_OGGETTO", langCode);
	
		StringBuilder url = new StringBuilder();
		url.append(this.configManager.getParam(SystemConstants.PAR_APPL_BASE_URL))
		   .append("it/ppgare_registr.wp?actionPath=/ExtStr2/do/FrontEnd/RegistrImpr/init.action")
		   .append("&currentFrame=7")
		   .append("&token=" + token);
		
		String text = MessageFormat.format(this.getI18nLabel("MAIL_REG_MANUALE_OP_TESTO", langCode),
										   new Object[] { motivazione, url.toString() });
		this.mailManager.sendMail(
				text, 
				subject, 
				new String[] { email },
				null, 
				null, 
				CommonSystemConstants.SENDER_CODE);
	}

}
