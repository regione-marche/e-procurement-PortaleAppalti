package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.areapers;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event.Level;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.monitoraggio.IMonitoraggioManager;
import it.maggioli.eldasoft.plugins.ppcommon.ws.EsitoOutType;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.regimpresa.IRegistrazioneImpresaManager;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.apsadmin.system.BaseAction;

/**
 * Gestore per forzare l'attivazione di un utente non ancora attivato.
 * 
 * @author Eleonora.Favaro
 * @author stefano.sabbadin
 */
public class AttivazioneOperatoriEconomiciAction extends BaseAction {

	/**
	 * UID.
	 */
	private static final long serialVersionUID = 6234165647148138199L;

	private IMonitoraggioManager monitoraggioManager;
	private IRegistrazioneImpresaManager registrazioneImpresaManager;
	private IEventManager eventManager;
	/** Input: username dell'impresa da attivare. */
	private String username;
	/** Output: email di riferimento dell'impresa. */
	private String emailAttivazione;
	/**
	 * Valorizzato a 1 quando si deve poi tornare alla lista utenti ripetendo
	 * l'ultima ricerca effettuata.
	 */
	private String last;
	

	/**
	 * @param monitoraggioManager the monitoraggioManager to set
	 */
	public void setMonitoraggioManager(IMonitoraggioManager monitoraggioManager) {
		this.monitoraggioManager = monitoraggioManager;
	}

	public void setRegistrazioneImpresaManager(IRegistrazioneImpresaManager registrazioneImpresaManager){
		this.registrazioneImpresaManager = registrazioneImpresaManager;
	}
	
	public void setEventManager(IEventManager eventManager){
		this.eventManager = eventManager;
	}
	
	/**
	 * @return the email
	 */
	public String getEmailAttivazione() {
		return emailAttivazione;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmailAttivazione(String emailAttivazione) {
		this.emailAttivazione = emailAttivazione;
	}
	
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * @return the last
	 */
	public String getLast() {
		return last;
	}

	/**
	 * @param last the last to set
	 */
	public void setLast(String last) {
		this.last = last;
	}

	/**
	 * Apre la pagina per attivare un operatore economico. Verifica se l'utente
	 * &egrave; stato fornito in input e ne estrae la mail di riferimento
	 * memorizzata nel portale in modo da proporla come indirizzo di default a
	 * cui reinviare il token di attivazione.
	 * 
	 * @return target struts di esito dell'operazione
	 */
	@SkipValidation
	public String open(){
		String target = SUCCESS;

		// funzionalita' disponibile solo per utenti amministratori loggati
		if (this.getCurrentUser() == null
				|| !this.isCurrentUserMemberOf(SystemConstants.ADMIN_ROLE)) {
			this.addActionError(this.getText("Errors.function.notEnabled"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			if (!StringUtils.isEmpty(this.getUsername())) {
				this.emailAttivazione = this.monitoraggioManager.getEmailOperatore(this
						.getUsername());
				if (StringUtils.isEmpty(this.emailAttivazione)) {
					this.addActionError(this
							.getText("Errors.opKO.noMailUserSelected"));
					target = ERROR;
				}
			} else {
				this.addActionError(this
						.getText("Errors.opKO.noUserSelected"));
				target = ERROR;
			}
		}

		return target;	
	}

	/**
	 * Effettua l'invio del token di attivazione all'indirizzo editato da
	 * interfaccia per l'utente selezionato in precedenza.
	 * 
	 * @return target struts di esito dell'operazione
	 */
	public String sendMail(){
		String target = SUCCESS;
		
		// funzionalita' disponibile solo per utenti amministratori loggati
		if (this.getCurrentUser() == null
				|| !this.isCurrentUserMemberOf(SystemConstants.ADMIN_ROLE)) {
			this.addActionError(this.getText("Errors.function.notEnabled"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			Event evento = new Event();
			evento.setLevel(Level.INFO);
			evento.setEventType(PortGareEventsConstants.GENERAZIONE_TOKEN_ADMIN);
			evento.setMessage("Generazione del token di attivazione per l'utente " + this.getUsername() + " con email " + this.getEmailAttivazione());
			evento.setUsername(this.getCurrentUser().getUsername());
			evento.setIpAddress(this.getCurrentUser().getIpAddress());
			evento.setSessionId(this.getRequest().getSession().getId());
			
			EsitoOutType invioMail = this.registrazioneImpresaManager.sendActivationMailImpresa(this.getUsername(), this.getEmailAttivazione());
			
			if(invioMail.getCodiceErrore()!= null){
				evento.setLevel(Level.ERROR);
				evento.setDetailMessage(invioMail.getCodiceErrore());
				String msgErrore = this.getText("Errors.opKO.mailError", new String[]{this.getEmailAttivazione()});
				this.addActionError(msgErrore);
				target = INPUT;
			} else {
				// nel caso di esito positivo si torna alla lista utenti con un
				// messaggio informativo di esito in testa alla pagina
				String msgInfo = this.getText("Info.opKO.mailMessage", new String[]{this.getEmailAttivazione()});
				this.addActionMessage(msgInfo);
			}
			
			this.eventManager.insertEvent(evento);
		}
		
		return target;
	}
}
