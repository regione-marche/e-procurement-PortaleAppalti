package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa;

import it.eldasoft.sil.portgare.datatypes.RichiestaGenericaType;
import it.eldasoft.sil.portgare.datatypes.RichiestaVariazioneDocument;
import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.ComunicazioniUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.io.IOException;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.interceptor.validation.SkipValidation;
import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.apsadmin.system.BaseAction;

/**
 * Action di invio della richiesta di variazione dei dati identificativi
 * dell'impresa al backoffice.
 * 
 * @author Stefano.Sabbadin
 * @since 1.2.2
 */
public class RichiediVariazioneDatiImpresaAction extends BaseAction implements
		SessionAware {

	/**
	 * UID
	 */
	private static final long serialVersionUID = 1938253628663867186L;

	private Map<String, Object> session;

	private IComunicazioniManager _comunicazioniManager;
	private IEventManager eventManager;

	private String descrizione;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	/**
	 * @param manager
	 *            the _comunicazioniManager to set
	 */
	public void setComunicazioniManager(IComunicazioniManager manager) {
		_comunicazioniManager = manager;
	}

	/**
	 * @param eventManager the eventManager to set
	 */
	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}
	
	/**
	 * @return the descrizione
	 */
	public String getDescrizione() {
		return descrizione;
	}

	/**
	 * @param descrizione
	 *            the descrizione to set
	 */
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	/**
	 * Invia la richiesta di variazione al backoffice
	 */
	public String send() {
		String target = SUCCESS;
		if (null != this.getCurrentUser().getProfile()
				&& !this.getCurrentUser().getUsername()
						.equals(SystemConstants.GUEST_USER_NAME)) {

			WizardDatiImpresaHelper helper = (WizardDatiImpresaHelper) this.session
					.get(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA);
			if (helper != null) {

				try {
					this.sendComunicazione(helper);
					// si rimuove l'oggetto dalla sessione tanto se si ritorna
					// alla pagina di dettaglio lo si ricarica, e si ha pertanto
					// modo di verificare un tentativo di refresh della pagina
					// con reinvio dei dati
					this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA);
				} catch (ApsException t) {
					ApsSystemUtils.logThrowable(t, this, "send");
					ExceptionUtils.manageExceptionError(t, this);
					target = "errorWS";
				}
			} else {
				this.addActionError(this.getText("Errors.invioComunicazioneCompleted"));
				target = CommonSystemConstants.PORTAL_ERROR;
			}
		} else {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		return target;
	}

	/**
	 * Compone i dati da inviare nella comunicazione ed invia la richiesta al
	 * backoffice
	 * 
	 * @param helper
	 * 
	 * @throws ApsException
	 */
	private void sendComunicazione(WizardDatiImpresaHelper helper)
			throws ApsException {

		ComunicazioneType comunicazione = null;

		// FASE 1: estrazione dei parametri necessari per i dati di testata
		// della comunicazione
		String username = this.getCurrentUser().getUsername();

		Event evento = new Event();
		evento.setUsername(username);
		evento.setIpAddress(this.getCurrentUser().getIpAddress());
		evento.setSessionId(this.getRequest().getSession().getId());
		evento.setEventType(PortGareEventsConstants.INVIO_COMUNICAZIONE_DA_PROCESSARE);
		evento.setLevel(Event.Level.INFO);
		evento.setMessage("Invio comunicazione "
				+ PortGareSystemConstants.RICHIESTA_VARIAZIONE_DATI_ANAGRAFICI
				+ " in stato "
				+ CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);

	
		// Invio della comunicazione
		try {
			// FASE 2: costruzione del contenitore della comunicazione e popolamento
			// della testata
			comunicazione = new ComunicazioneType();

			DettaglioComunicazioneType dettaglioComunicazione = ComunicazioniUtilities
					.createDettaglioComunicazione(
							null,
							username,
							null,
							null,
							helper.getDatiPrincipaliImpresa().getRagioneSociale(),
							CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE,
							this.getTextFromDefaultLocale(
									"label.variazioneAnagrafica.oggetto",
									username),
							this.getTextFromDefaultLocale(
									"label.variazioneAnagrafica.testo",
									username),
							PortGareSystemConstants.RICHIESTA_VARIAZIONE_DATI_ANAGRAFICI,
							null);
			comunicazione.setDettaglioComunicazione(dettaglioComunicazione);

			// FASE 3: creazione e popolamento dell'allegato con l'xml dei dati
			// dainviare
			this.setAllegatoComunicazione(
					comunicazione,
					PortGareSystemConstants.NOME_FILE_DOMANDA_VARIAZIONE,
					this.getTextFromDefaultLocale(
							"label.variazioneAnagrafica.allegato.descrizione"));

			// FASE 4: invio comunicazione
			comunicazione.getDettaglioComunicazione()
					.setId(this._comunicazioniManager
							.sendComunicazione(comunicazione));

			evento.setMessage("Invio comunicazione "
					+ PortGareSystemConstants.RICHIESTA_VARIAZIONE_DATI_ANAGRAFICI
					+ " con id "
					+ comunicazione.getDettaglioComunicazione().getId()
					+ " in stato "
					+ comunicazione.getDettaglioComunicazione().getStato());
			
		} catch (ApsException e) {
			evento.setError(e);
			throw e;
		} catch (IOException e) {
			evento.setError(e);
			throw new ApsException(e.getMessage());
		} finally {
			this.eventManager.insertEvent(evento);
		}
	}

	/**
	 * aggiorna il documento xml della comunicazione e gli eventuali allegati   
	 */
	private void setAllegatoComunicazione(
			ComunicazioneType comunicazione, 
			String nomeFile, 
			String descrizioneFile) throws IOException {
		
		RichiestaVariazioneDocument xmlDocument = RichiestaVariazioneDocument
			.Factory.newInstance();
		RichiestaGenericaType richiesta = xmlDocument.addNewRichiestaVariazione();
		richiesta.setDescrizione(this.descrizione);

		AllegatoComunicazioneType allegatoAggAnagr = ComunicazioniUtilities
			.createAllegatoComunicazione(
					nomeFile,
					descrizioneFile,
					xmlDocument.toString().getBytes());
		comunicazione.setAllegato(new AllegatoComunicazioneType[] { allegatoAggAnagr });
	}

	@SkipValidation
	public String reload() {
		return INPUT;
	}

	public String cancel() {
		return "cancel";
	}

}