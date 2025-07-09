package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.areapers;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.apsadmin.system.BaseAction;
import com.opensymphony.xwork2.ModelDriven;
import it.eldasoft.www.WSOperazioniGenerali.DettaglioComunicazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event.Level;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.monitoraggio.FlussiInviatiKO;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.monitoraggio.IMonitoraggioManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.struts2.interceptor.SessionAware;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Action per la gestione della funzionalit&agrave; di estrazione lista dei
 * flussi in stato errato ed aggiornamento.
 * 
 * @author Eleonora.Favaro
 */
public class InvioFlussiConErroriFinderAction extends BaseAction implements
		SessionAware, ModelDriven<InvioFlussiConErroriSearchBean> {

	/**
	 * UID.
	 */
	private static final long serialVersionUID = 3647302868199034204L;
	
	/** Contenitore dei dati di sessione. */
	private Map<String, Object> session;
	
	/** Contenitore dei dati di ricerca. */
	@Validate
	private InvioFlussiConErroriSearchBean model = new InvioFlussiConErroriSearchBean();

	private IMonitoraggioManager monitoraggioManager;
	private IEventManager eventManager;
	
	/** Output: elenco flussi estratti. */
	@Validate
	private List<FlussiInviatiKO> listaFlussiKO = new ArrayList<>();
	/** Input: identificativo comunicazione da rielaborare. */
	@Validate(EParamValidation.ID_COMUNICAZIONE)
	private String idComunicazione;
	/**
	 * Valorizzato a 1 quando si deve poi tornare alla lista utenti ripetendo
	 * l'ultima ricerca effettuata.
	 */
	@Validate(EParamValidation.DIGIT)
	private String last;

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

	@Override
	public InvioFlussiConErroriSearchBean getModel() {
		return this.model;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
	
	public void setMonitoraggioManager(IMonitoraggioManager monitoraggioManager){
		this.monitoraggioManager = monitoraggioManager;
	}
	
	public void setEventManager (IEventManager eventManager){
		this.eventManager = eventManager;
	}

	/**
	 * @return the listaFlussiKO
	 */
	public List<FlussiInviatiKO> getListaFlussiKO() {
		return listaFlussiKO;
	}

	/**
	 * @param listaFlussiKO the listaFlussiKO to set
	 */
	public void setListaFlussiKO(List<FlussiInviatiKO> listaFlussiKO) {
		this.listaFlussiKO = listaFlussiKO;
	}
	
	/**
	 * @return the idComunicazione
	 */
	public String getIdComunicazione() {
		return idComunicazione;
	}

	/**
	 * @param idComunicazione the idComunicazione to set
	 */
	public void setIdComunicazione(String idComunicazione) {
		this.idComunicazione = idComunicazione;
	}
	
	/**
	 * validate 
	 */
	@Override
	public void validate() {
		// funzionalità accessibile sono da amministratore!!!
		if ( !this.isCurrentUserMemberOf(SystemConstants.ADMIN_ROLE) ) {
			redirectToHome();
			return;
		} 
		
		super.validate();
	}

	/**
	 * Predispone l'apertura della pagina con l'elenco dei flussi errati
	 * estrando le comunicazioni di registrazione impresa al portale.
	 * 
	 * @return target struts di esito dell'operazione
	 */
	public String openSearch(){
		String target = SUCCESS;

		// funzionalita' disponibile solo per utenti amministratori loggati
		if ( !this.isCurrentUserMemberOf(SystemConstants.ADMIN_ROLE) ) {
			this.addActionError(this.getText("Errors.function.notEnabled"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			this.model.setTipoComunicazione(PortGareSystemConstants.RICHIESTA_REGISTRAZIONE_PORTALE);
			this.session.put(AreaPersonaleConstants.SESSION_ID_SEARCH_FLUSSI_KO, this.model);
			target = search();
		}
		return target;
	}

	/**
	 * Estrae la lista dei flussi errati sulla base dei criteri di filtro impostati nella form.
	 * 
	 * @return target struts di esito dell'operazione
	 */
	public String search(){
		String target = SUCCESS;

		// funzionalita' disponibile solo per utenti amministratori loggati
		if ( !this.isCurrentUserMemberOf(SystemConstants.ADMIN_ROLE) ) {
			this.addActionError(this.getText("Errors.function.notEnabled"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}else{

			if ("1".equals(this.last)) {
				// se si richiede il rilancio dell'ultima estrazione effettuata,
				// allora si prendono dalla sessione i filtri applicati e si
				// caricano nel presente oggetto
				InvioFlussiConErroriSearchBean finder = (InvioFlussiConErroriSearchBean) this.session
						.get(AreaPersonaleConstants.SESSION_ID_SEARCH_FLUSSI_KO);
				this.model = finder;
			}
			
			try {
				this.listaFlussiKO = this.monitoraggioManager.getInviiFlussiConErrori(model.getUtente(), model.getMittente(), model.getTipoComunicazione().toUpperCase(), model.getRiferimentoProcedura());
				this.session.put(AreaPersonaleConstants.SESSION_ID_SEARCH_FLUSSI_KO, this.model);			
			} catch (ApsException e) {
				this.addActionError(this.getText(
						"Errors.flussiKO.search",
						new String[] {  model.getUtente(), model.getTipoComunicazione().toUpperCase(), model.getRiferimentoProcedura() }));
				ApsSystemUtils.logThrowable(e, this, "search");
			}
		}
		return target;
	}

	/**
	 * Aggiorna la comunicazione selezionata variandone lo stato e riponendola in stato da processare.
	 * 
	 * @return target struts di esito dell'operazione
	 */
	public String update(){
		String target = SUCCESS;
		
		// funzionalita' disponibile solo per utenti amministratori loggati
		if ( !this.isCurrentUserMemberOf(SystemConstants.ADMIN_ROLE) ) {
			this.addActionError(this.getText("Errors.function.notEnabled"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			if (this.idComunicazione != null) {
				Event evento = new Event();
				evento.setUsername(this.getCurrentUser().getUsername());
				evento.setIpAddress(this.getCurrentUser().getIpAddress());
				evento.setSessionId(this.getRequest().getSession().getId());
				evento.setMessage("Aggiornamento comunicazione con id "
						+ this.idComunicazione
						+ " allo stato "
						+ CommonSystemConstants.STATO_COMUNICAZIONE_DA_PROCESSARE);
				evento.setLevel(Level.INFO);
				evento.setEventType(PortGareEventsConstants.CAMBIO_STATO_COMUNICAZIONE);

				String tipoComunicazione = null;
				try {
					// si estrae la comunicazione da aggiornare
					DettaglioComunicazioneType comunicazione = this.monitoraggioManager
							.getFlusso(Long.parseLong(this.idComunicazione));
					if (comunicazione != null) {
						tipoComunicazione = comunicazione.getTipoComunicazione();
						evento.setDestination(comunicazione.getChiave2());
						// si aggiorna lo stato alla comunicazione estratta
						this.monitoraggioManager.rielaboraFlusso(comunicazione);
						// si definisce un esito da inviare alla pagina
						String msgInfo = this.getText("Info.flussiKO.change",
								new String[] { comunicazione.getId().toString() });
						this.addActionMessage(msgInfo);
					} else {
						// errore: nessuna comunicazione estratta a partire dalla chiave
						evento.setLevel(Level.ERROR);
						this.addActionError(this.getText(
								"Errors.flussiKO.updateNoCom", new String[] {this.idComunicazione }));
						target = ERROR;
					}
				} catch (NumberFormatException e) {
					evento.setLevel(Level.ERROR);
					evento.setError(e);
					this.addActionError(this.getText(
							"Errors.flussiKO.updateNoCom", new String[] {this.idComunicazione }));
					ApsSystemUtils.logThrowable(e, this, "update");
					target = ERROR;
				} catch (ApsException e) {
					evento.setLevel(Level.ERROR);
					evento.setError(e);
					this.addActionError(this.getText(
							"Errors.flussiKO.updateError", new String[] {
									tipoComunicazione, this.idComunicazione }));
					ApsSystemUtils.logThrowable(e, this, "update");
					target = ERROR;
				} finally {
					eventManager.insertEvent(evento);
				}
			} else {
				// errore, serve avere id comunicazione impostato
				this.addActionError(this.getText("Errors.flussiKO.noComselected"));
				target = ERROR;
			}
		}
		return target;
	}
}
