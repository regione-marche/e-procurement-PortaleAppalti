package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.assistenza;

import it.maggioli.eldasoft.hda.client.HdaException;
import it.maggioli.eldasoft.hda.client.HdaHelpDesk;
import it.maggioli.eldasoft.hda.client.HdaHelpDeskConfiguration;
import it.maggioli.eldasoft.hda.client.HdaTicket;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.servlet.CaptchaServlet;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.InterceptorEncodedData;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import nl.captcha.Captcha;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.plugins.jpmail.aps.services.mail.IMailManager;

/**
 * Esegue le preoperazioni necessarie all'apertura della pagina dell'area
 * personale.
 *
 * @author Stefano.Sabbadin
 * @since 1.8.6
 */
public class AssistenzaAction extends EncodedDataAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -2541550486948752191L;

	private String ente;
	private String referente;
	private String email;
	private String telefono;
	private String tipoRichiesta;
	private String descrizione;
	private File allegato;
	private String allegatoContentType;
	private String allegatoFileName;
	private String captchaConferma;
	private String infoSystem;
	
	private IAppParamManager appParamManager;
	private IMailManager mailManager;
	private IEventManager eventManager;
	private DataSource dataSource;

	private static final int ASSISTENZA_DISABILITATA = 0;
	private static final int ASSISTENZA_CON_SERVIZIO = 1;
	private static final int ASSISTENZA_VIA_MAIL = 2;

	private static final String NOME_PRODOTTO = "Portale Appalti";
	private static final String VERSION_FILE = "/WEB-INF/PA_VER.TXT";
		
	private String ticketId;

	/**
	 * @param appParamManager the appParamManager to set
	 */
	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	/**
	 * @param mailManager the mailManager to set
	 */
	public void setMailManager(IMailManager mailManager) {
		this.mailManager = mailManager;
	}

	/**
	 * @param eventManager the eventManager to set
	 */
	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}

	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * @return the ente
	 */
	public String getEnte() {
		return ente;
	}

	/**
	 * @param ente the ente to set
	 */
	public void setEnte(String ente) {
		this.ente = ente;
	}

	/**
	 * @return the referente
	 */
	public String getReferente() {
		return referente;
	}

	/**
	 * @param referente the referente to set
	 */
	public void setReferente(String referente) {
		this.referente = referente;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the telefono
	 */
	public String getTelefono() {
		return telefono;
	}

	/**
	 * @param telefono the telefono to set
	 */
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	/**
	 * @return the tipoRichiesta
	 */
	public String getTipoRichiesta() {
		return tipoRichiesta;
	}

	/**
	 * @param tipoRichiesta the tipoRichiesta to set
	 */
	public void setTipoRichiesta(String tipoRichiesta) {
		this.tipoRichiesta = tipoRichiesta;
	}

	/**
	 * @return the descrizione
	 */
	public String getDescrizione() {
		return descrizione;
	}

	/**
	 * @param descrizione the descrizione to set
	 */
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	/**
	 * @return the captchaConferma
	 */
	public String getCaptchaConferma() {
		return captchaConferma;
	}

	/**
	 * @param captchaConferma the captchaConferma to set
	 */
	public void setCaptchaConferma(String captchaConferma) {
		this.captchaConferma = captchaConferma;
	}

	public Integer getLimiteUploadFile() {
		return FileUploadUtilities.getLimiteUploadFile(this.appParamManager);
	}
	
	/**
	 * @return the modoAssistenza
	 */
	public Integer getModoAssistenza() {
		Integer modo = (Integer) this.appParamManager.getConfigurationValue(AppParamManager.MODO_ASSISTENZA);
		if (modo == null || modo < 0) {
			 modo = ASSISTENZA_DISABILITATA;
		}
		return modo;
	}

	/**
	 * @return the mailAssistenza
	 */
	public String getMailAssistenza() {
		return (String) this.appParamManager.getConfigurationValue(AppParamManager.MAIL_ASSISTENZA);
	}

	public String getTelefonoAssistenza() {
		return (String) this.appParamManager.getConfigurationValue(AppParamManager.TELEFONO_ASSISTENZA);
	}

	/**
	 * @return the nomeCliente
	 */
	public String getNomeCliente() {
		return (String) this.appParamManager.getConfigurationValue(AppParamManager.NOME_CLIENTE);
	}

	/**
	 * @return the urlServizio
	 */
	public String getUrlServizio() {
		return (String) this.appParamManager.getConfigurationValue(AppParamManager.URL_SERVIZIO_ASSISTENZA);
	}

	/**
	 * @return the usrServizio
	 */
	public String getUsrServizio() {
		return (String) this.appParamManager.getConfigurationValue(AppParamManager.USER_SERVIZIO_ASSISTENZA);
	}

	/**
	 * @return the pwdServizio
	 */
	public String getPwdServizio() {
		return (String) this.appParamManager.getConfigurationValue(AppParamManager.PASSWORD_SERVIZIO_ASSISTENZA);
	}

	/**
	 * @return the idProdottoServizio
	 */
	public Integer getIdProdottoServizio() {
		return (Integer) this.appParamManager.getConfigurationValue(AppParamManager.IDPRODOTTO_SERVIZIO_ASSISTENZA);
	}
	
	/**
	 * @return the allegato
	 */
	public File getAllegato() {
		return allegato;
	}

	/**
	 * @param allegato the allegato to set
	 */
	public void setAllegato(File allegato) {
		this.allegato = allegato;
	}

	/**
	 * @return the allegatoContentType
	 */
	public String getAllegatoContentType() {
		return allegatoContentType;
	}

	/**
	 * @param allegatoContentType the allegatoContentType to set
	 */
	public void setAllegatoContentType(String allegatoContentType) {
		this.allegatoContentType = allegatoContentType;
	}

	/**
	 * @return the allegatoFileName
	 */
	public String getAllegatoFileName() {
		return allegatoFileName;
	}

	/**
	 * @param allegatoFileName the allegatoFileName to set
	 */
	public void setAllegatoFileName(String allegatoFileName) {
		this.allegatoFileName = allegatoFileName;
	}

	/**
	 * @return the infoSystem
	 */
	public String getInfoSystem() {
		return infoSystem;
	}

	/**
	 * @param infoSystem the infoSystem to set
	 */
	public void setInfoSystem(String infoSystem) {
		this.infoSystem = infoSystem;
	}

	/**
	 * @return the ticketId
	 */
	public String getTicketId() {
		return ticketId;
	}

	/**
	 * @param ticketId the ticketId to set
	 */
	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}

	
	public String view() {
		this.setTarget(SUCCESS);
		switch (getModoAssistenza()) {
		case ASSISTENZA_VIA_MAIL:
			if (StringUtils.isBlank(getMailAssistenza())) {
				ApsSystemUtils.getLogger().error(this.getTextFromDefaultLocale("Errors.emailAssistenzaNotSet"));
				this.addActionError(this.getText("Errors.assistenzaNotConfigured"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
			break;
		case ASSISTENZA_CON_SERVIZIO:
			if (StringUtils.isBlank(this.getUrlServizio())
					|| StringUtils.isBlank(this.getUsrServizio())
					|| StringUtils.isBlank(this.getPwdServizio())
					|| this.getIdProdottoServizio() <= 0) {
				ApsSystemUtils.getLogger().error(this.getTextFromDefaultLocale("Errors.hdaServiceConfigurazioneErrata"));
				this.addActionError(this.getText("Errors.assistenzaNotConfigured"));
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
			break;
		default:
			ApsSystemUtils.getLogger().warn(this.getTextFromDefaultLocale("Warnings.assistenzaNotEnabled"));
			this.addActionMessage(this.getText("Warnings.assistenzaNotEnabled"));
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			break;
		} 

		return this.getTarget();
	}

	public String help() {
		// si effettuano i medesimi test sulla configurazione
		view();

		boolean continua = SUCCESS.equals(this.getTarget());
		
		// in fase di invio della richiesta si traccia anche l'evento
		Event evento = new Event();
		if (!this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			evento.setUsername(this.getCurrentUser().getUsername());
			evento.setIpAddress(this.getCurrentUser().getIpAddress());
		} else {
			evento.setIpAddress(this.getRequest().getRemoteAddr());
		}
		evento.setSessionId(this.getRequest().getSession().getId());
		evento.setEventType(PortGareEventsConstants.HELPDESK);
		evento.setMessage("Inserimento richiesta di assistenza da parte di "
				+ this.ente
				+ " con referente "
				+ this.referente
				+ " (mail " + this.email + ")");
		if (continua) {
			evento.setLevel(Event.Level.INFO);
		} else {
			evento.setLevel(Event.Level.ERROR);
			switch (getModoAssistenza()) {
			case ASSISTENZA_VIA_MAIL:
			case ASSISTENZA_CON_SERVIZIO:
				evento.setDetailMessage(this.getTextFromDefaultLocale("Errors.assistenzaNotConfigured"));
				break;
			default:
				evento.setDetailMessage(this.getTextFromDefaultLocale("Warnings.assistenzaNotEnabled"));
			} 
		}
		
		// si puo' procedere a gestire la richiesta con una gestione errori che
		// riporta alla pagina di compilazione richiesta

		if (continua) {
			// controllo bloccante: verifica del codice di sicurezza (captcha)
			Captcha captcha = (Captcha)this.getRequest().getSession().getAttribute(CaptchaServlet.SESSION_ID_CAPTCHA);		
			if (captcha== null) {
				// non dovrebbe accadere mai, puo' accadere solo se si invia una
				// richiesta senza prima essere passati per la pagina che apre
				// la form
				this.addActionError(this.getText("Errors.noSessionCaptcha"));
				evento.setDetailMessage(this.getTextFromDefaultLocale("Errors.noSessionCaptcha"));
				evento.setLevel(Event.Level.ERROR);
				this.setTarget(INPUT);
				continua = false;
			} else if (!captcha.isCorrect(this.captchaConferma) ) {
				this.addActionError(this.getText("Errors.captcha"));
				evento.setDetailMessage(this.getTextFromDefaultLocale("Errors.captcha"));
				evento.setLevel(Event.Level.ERROR);
				this.setTarget(INPUT);
				continua = false;
			}
		}

		// Verifico la validita' del file allegato
		if (continua && this.getAllegato() != null) {
			continua = this.checkFileSize(this.getAllegato(), this.getAllegatoFileName(), 0, this.appParamManager, evento);
			continua = continua && this.checkFileName(this.getAllegatoFileName(), evento);
			if (!continua) {
				this.setTarget(INPUT);
			}
		}
		
		// qui si svolge il vero inoltro della richiesta
		if (continua) {
			try {
				String tipoAssistenza = this.getMaps().get(InterceptorEncodedData.LISTA_TIPOLOGIE_ASSISTENZA).get(this.getTipoRichiesta());
				
				switch (getModoAssistenza()) {
				case ASSISTENZA_VIA_MAIL:
					evento.setMessage("Inserimento richiesta di assistenza via mail da parte di "
							+ this.ente
							+ " con referente "
							+ this.referente
							+ " (mail " + this.email + ")");
					sendEmail(tipoAssistenza);
					break;
				case ASSISTENZA_CON_SERVIZIO:
					evento.setMessage("Inserimento richiesta di assistenza su HDA da parte di "
							+ this.ente
							+ " con referente "
							+ this.referente
							+ " (mail " + this.email + ")");
					hdaService(tipoAssistenza);		
					evento.setMessage("Inserimento richiesta di assistenza numero "
							+ this.ticketId
							+ " su HDA da parte di "
							+ this.ente
							+ " con referente "
							+ this.referente
							+ " (mail " + this.email + ")");
					break;
				}
			} catch (ApsSystemException ex) {
				ApsSystemUtils.logThrowable(ex, this, "help");
				this.addActionError(this.getText("Errors.requestNotSent"));
				evento.setError(ex);
				this.setTarget(INPUT);
			} catch (RemoteException ex) {
				ApsSystemUtils.logThrowable(ex, this, "help");
				this.addActionError(this.getText("Errors.hdaServiceNotRunning"));
				evento.setError(ex);
				this.setTarget(INPUT);
			} catch (HdaException ex) {				
				ApsSystemUtils.logThrowable(ex, this, "help");
				this.addActionError(this.getText("Errors.hdaServiceConfigurazioneErrata"));
				evento.setError(ex);
				this.setTarget(INPUT);
			} catch (Exception ex) {				
				ApsSystemUtils.logThrowable(ex, this, "help");
				this.addActionError(this.getText("Errors.unexpected"));
				evento.setError(ex);
				this.setTarget(INPUT);
			}
		}
		
		this.eventManager.insertEvent(evento);
		
		return this.getTarget();
	}

	private String getTestoMail(String tipoAssistenza) {
		StringBuilder testo = new StringBuilder(); 
		try {
			testo.append(this.getTextFromDefaultLocale("titoloRiferimenti")).append("\n");
			if(StringUtils.isNotEmpty(this.getDescStazioneAppaltante())) {
				testo.append(this.getI18nManager().getLabel("LABEL_STAZIONE_APPALTANTE", this.getDefaultLocale())).append(": ").append(this.getDescStazioneAppaltante() + " (" + this.getCodiceFiscaleStazioneAppaltante() + ")").append("\n");
			}
			testo.append(this.getTextFromDBDefaultLocale("ente")).append(": ").append(this.getEnte()).append("\n");
			testo.append(this.getTextFromDBDefaultLocale("referente")).append(": ").append(this.getReferente()).append("\n");
			testo.append(this.getTextFromDBDefaultLocale("email")).append(": ").append(this.getEmail()).append("\n");
			if (StringUtils.isNotBlank(this.getTelefono())) {
				testo.append(this.getTextFromDBDefaultLocale("telefono"))
								.append(": ").append(this.getTelefono()).append("\n");
			}
			testo.append("\n");
			testo.append(this.getTextFromDefaultLocale("titoloRichiesta")).append("\n");
			if (StringUtils.isNotBlank(this.getDescrizione())) {
				testo.append(this.getDescrizione()).append("\n");
			} else {
				// se manca il testo nella textarea si rimette l'oggetto della mail
				testo.append(tipoAssistenza).append("\n");
			}
			testo.append("\n");
			testo.append("INFORMAZIONI TECNICHE APPLICATIVO").append("\n");
			testo.append("Prodotto: ").append(NOME_PRODOTTO).append("\n");
			testo.append("Versione: ").append(getVersion()).append("\n");
			testo.append("Registrato da: ").append(getNomeCliente()).append("\n");
			testo.append("Sistema operativo: ").append(System.getProperty("os.name")).append("\n");
			testo.append("Database utilizzato: ").append(this.dataSource.getConnection().getMetaData().getDatabaseProductName()).append("\n");
			testo.append("\n");
			testo.append("CLIENT RICHIEDENTE").append("\n");
			testo.append(this.getInfoSystem());
		} catch(Exception e) {
			// non dovrebbe accadere mai, i problemi nascono dalla lettura della
			// versione da filesystem e dalla lettura del database name dal db.
			//
			// comunque sia si lascia il messaggio con il testo generato
			// parzialmente (cosi' almeno si comprende dove da' il problema)
			// e si traccia nel log il problema
			ApsSystemUtils.logThrowable(e, this, "getTestoMail");
		}
		return testo.toString();
	}
		
	private void sendEmail(String tipoAssistenza) throws ApsSystemException, IOException, SQLException, NullPointerException {
		String[] receiver = {getMailAssistenza()};
		StringBuilder oggetto = new StringBuilder().append(this.getTextFromDefaultLocale("oggettoMail")).append(": ").append(tipoAssistenza);
		String testo = getTestoMail(tipoAssistenza); 

		// a seconda della presenza o meno di un allegato si utilizza il metodo corretto per l'invio mail
		if (this.getAllegato() != null && this.getAllegato().length() != 0) {
			Properties p = new Properties();
			p.put(this.getAllegatoFileName(), this.getAllegato().getAbsolutePath());
			this.mailManager.sendMail(testo, oggetto.toString(), IMailManager.CONTENTTYPE_TEXT_PLAIN, p, receiver, null, null, CommonSystemConstants.SENDER_CODE);
		} else {
			this.mailManager.sendMail(testo, oggetto.toString(), receiver, null, null, CommonSystemConstants.SENDER_CODE);
		}
	}

	private String getVersion() throws IOException {
		ServletContext context = (ServletContext) ServletActionContext.getServletContext();
		InputStream is = new FileInputStream(context.getRealPath(VERSION_FILE));
		return IOUtils.toString(is, "UTF-8");
	}

	/**
	 * Invia la richiesta di assistenza al HDA (helpdesk maggioli)
	 *
	 * @return risultato dell'invio
	 * @throws RemoteException
	 */
	private void hdaService(String tipoAssistenza) throws IOException, HdaException {			
		String testo =
			"[PORTALE] " + 
			"Richiesta di assistenza: " + tipoAssistenza + 
			"\n" + 
			"\n" +
			getTestoMail(tipoAssistenza);
						
		HdaHelpDeskConfiguration hdaConf = new HdaHelpDeskConfiguration();		
		hdaConf.setHostname(getUrlServizio());				
		hdaConf.setUsername(getUsrServizio());
		hdaConf.setPassword(getPwdServizio());
		
		HdaHelpDesk hhd = new HdaHelpDesk(hdaConf, null);		
		HdaTicket ticket = new HdaTicket(new Long(getIdProdottoServizio()), testo.toString(), false);		
		FileInputStream fis = null;
		
		hhd.login();
		try {
			// inserisci l'allegato...
			if(this.getAllegato() != null) {
				fis = new FileInputStream(this.getAllegato());  
				ticket.addFile(fis, this.getAllegatoFileName());
			}
			
			// invia la richiesta...
			this.ticketId = hhd.insertTicket(ticket);					
		} finally {
			try {
				hhd.logout();
			} catch(Exception e) {				
			}
			
			if(fis != null) {
				try {
					fis.close();
				} catch(Exception t) {					
				}
			}
		}
	}	
	
}
