package it.maggioli.eldasoft.plugins.ppcommon.apsadmin.customconfig;

import com.opensymphony.xwork2.ModelDriven;
import it.eldasoft.utils.utility.UtilityDate;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.DateValidator;
import org.apache.struts2.interceptor.SessionAware;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

public class SearchEventsAction extends EncodedDataAction implements SessionAware, ModelDriven<SearchEventsBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5034984605091777924L;
	//private final int rowsPerPage = 20;
	private Map<String, Object> session;
	@Validate
	private SearchEventsBean model = new SearchEventsBean();

	/** boolean che mi indica se nascondere (pre-ricerca) o mostrare i risultati (post-ricerca) **/
	private boolean showResult;
	/**Lista dei risultati della ricerca  **/
	private List<Event> risultati;
	/** Numero di pagina **/
	private int pageNum;
	/** Numero di risultati ottenuti dalla ricerca filtrata**/
	private int totEvents;
	/** Lista degli eventi recuperati tramite ricerca **/
	private List<Event> events;
	/** Id dell'evento di cui si desidera vedere il dettaglio **/
	private Long eventId;
	/** Evento selezionato**/
	private Event selectedEvent;
	/** Numero massimo di righe per pagina **/
	private int rowsPerPage = 20;		// 10, 20, 50, 100 dropdown a video
	/** Manager degli eventi **/
	private IEventManager eventManager;


	/**
	 * @return the showResult
	 */
	public boolean isShowResult() {
		return showResult;
	}

	/**
	 * @param showResult the showResult to set
	 */
	public void setShowResult(boolean showResult) {
		this.showResult = showResult;
	}


	/**
	 * @return the risultati
	 */
	public List<Event> getRisultati() {
		return risultati;
	}

	/**
	 * @param list the risultati to set
	 */
	public void setRisultati(List<Event> list) {
		this.risultati = list;
	}

	/**
	 * @return the pageNum
	 */
	public int getPageNum() {
		return pageNum;
	}

	/**
	 * @param pageNum the pageNum to set
	 */
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	/**
	 * @return the totEvents
	 */
	public int getTotEvents() {
		return totEvents;
	}

	/**
	 * @param totEvents the totEvents to set
	 */
	public void setTotEvents(int totEvents) {
		this.totEvents = totEvents;
	}

	/**
	 * @return the events
	 */
	public List<Event> getEvents() {
		return events;
	}

	/**
	 * @param events the events to set
	 */
	public void setEvents(List<Event> events) {
		this.events = events;
	}

	/**
	 * @return the eventId
	 */
	public Long getEventId() {
		return eventId;
	}

	/**
	 * @param eventId the eventId to set
	 */
	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	/**
	 * @return the selectedEvent
	 */
	public Event getSelectedEvent() {
		return selectedEvent;
	}

	/**
	 * @param selectedEvent the selectedEvent to set
	 */
	public void setSelectedEvent(Event selectedEvent) {
		this.selectedEvent = selectedEvent;
	}
	
	public int getRowsPerPage() {
		return rowsPerPage;
	}

	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}

	/**
	 * @return the eventManager
	 */
	public IEventManager getEventManager() {
		return eventManager;
	}

	/**
	 * @param eventManager the eventManager to set
	 */
	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}

	@Override
	public SearchEventsBean getModel() {
		return this.model;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}


	public void validate() {
		super.validate();

		Date d = null;
		
		if(StringUtils.isNotBlank(this.model.getDateFrom())){
			d = DateValidator.getInstance().validate(this.model.getDateFrom(),
					"dd/MM/yyyy");
			if (d == null  ||  this.model.getDateFrom().length() != 10) {
				this.addActionError(this.getText("ppcommon.searchEvents.message.dateFrom.ko"));
			}
		}

		if(StringUtils.isNotBlank(this.model.getDateTo())){
			d = DateValidator.getInstance().validate(this.model.getDateTo(),
					"dd/MM/yyyy");
			if (d == null ||  this.model.getDateTo().length() != 10) {
				this.addActionError(this.getText("ppcommon.searchEvents.message.dateTo.ko"));
			}
		}
	}


	/**
	 * Metodo che effettua la ricerca nella tabella degli eventi 
	 * in base ai criteri inseriti da maschera di input.
	 * @throws SQLException
	 * */

	public String search() {

		this.setTarget(SUCCESS);
		
		boolean continua = true;
		
		if (this.model.isEmpty()) {
			this.addActionError(this.getText("ppcommon.searchEvents.message.filterRequired"));
			continua = false;
		}

		Date dateFrom = UtilityDate.convertiData(this.model.getDateFrom(), UtilityDate.FORMATO_GG_MM_AAAA);
		Date dateTo   = UtilityDate.convertiData(this.model.getDateTo(), UtilityDate.FORMATO_GG_MM_AAAA);

		//controllo sulle date
		if (continua) {
			if(dateFrom != null && dateTo != null && dateFrom.after(dateTo)){
				this.addActionError(this.getText("ppcommon.searchEvents.message.dateInterval.ko"));
				continua = false;
			}
		}

		if (continua) {
			//Si aggiunge un giorno per  recuperare tutti gli eventi di un giorno con orario durante la giornata stessa
			if(dateTo != null){
				GregorianCalendar gc = new GregorianCalendar();
				gc.setTime(dateTo);
				gc.add(Calendar.DAY_OF_MONTH, 1);
				dateTo = gc.getTime();
			}

			this.model.setiDisplayLength(this.rowsPerPage);

			//conta il numero di eventi in base al filtro
			this.setTotEvents(this.eventManager.countEvents(
					dateFrom,
					dateTo,
					this.model.getUser(),
					this.model.getDestination(),
					this.model.getType(),
					this.model.getLevel(),
					this.model.getMessage()
					));

			setRisultatiPaginati(dateFrom,
					dateTo,
					this.model.getUser(),
					this.model.getDestination(),
					this.model.getType(),
					this.model.getLevel(), 
					this.model.getMessage(), this.model.getCurrentPage());

			this.session.put(PortGareSystemConstants.SESSION_ID_LOADED_EVENTS_RESULT, this.getRisultati());
			this.model.processResult(this.getTotEvents(), this.getTotEvents());
			
			// traccia un evento di "CONSULTEVENTS" per ogni consultazione eventi effettuata
			addConsultaEventi();
		}


		return this.getTarget(); 
	}

	/**
	 * Metodo che mi permette di andare ad aprire il dettaglio per l'evento di tipo ERROR selezionato dalla lista 
	 * 
	 *  @return String condizione di filtro costruita per la ricerca
	 **/

	public String openEventDetail() {
		Event evento = this.eventManager.getEvent(this.getEventId());
		if (evento.getDetailMessage() != null) {
			// converto il messaggio in formato HTML, o meglio con dei break di
			// linea e sostituzione delle parentesi angolari, per migliorare la
			// lettura
			evento.setDetailMessage(evento.getDetailMessage().replace("<", "&lt;"));
			evento.setDetailMessage(evento.getDetailMessage().replace(">", "&gt;"));
			evento.setDetailMessage(evento.getDetailMessage().replace("\n", "<br />"));
		}
		this.setSelectedEvent(evento);		
		return SUCCESS;
	}

	/**
	 * Metodo che mi permette di andare visualizzare i risultati appartenenti ad una data pagina

	 * @param int numero della pagina selezionata
	 **/
	private void setRisultatiPaginati(Date dateFrom, Date dateTo, String username, String destination, String type, String level, String message, int pageNum){

		List<Event> lista = null;

		this.setShowResult(true);

		int start = pageNum > 0 ? (pageNum-1)*rowsPerPage : 0;
		lista = this.eventManager.searchEvents(dateFrom, dateTo, username, destination, type, level, message, start, rowsPerPage);

		this.setRisultati(lista);
	}

	//	/**
	//	 * Metodo che mi permette di ritornare alla lista dei risultati della ricerca
	//	 * partendo dalla pagina di dettaglio dell'evento
	//	 * 
	//	 *  @return String target
	//	 **/
	//	public String backFromDetail(){
	//
	//
	//		List<Event> events = (List<Event>)(this.session
	//				.get(PortGareSystemConstants.SESSION_ID_LOADED_EVENTS));
	//
	//		if(events.size()%rowsPerPage==0){
	//			setPages(events.size()/rowsPerPage);
	//		}
	//		else{
	//			setPages((events.size()/rowsPerPage)+1);
	//		}
	//		
	//		this.setTotEvents(events.size());
	//		this.setRisultati((List<Event>)(this.session
	//				.get(PortGareSystemConstants.SESSION_ID_LOADED_EVENTS_RESULT)));
	//		this.setShowResult(true);
	//
	//		this.setCurrPage((Integer)this.session.get(PortGareSystemConstants.SESSION_ID_SEARCH_EVENTS_PAGE_NUMBER));
	//
	//		return SUCCESS;
	//	}
	
	
	/**
	 * traccia un evento di "CONSULTEVENTS" per ogni consultazione eventi effettuata
	 */
	private Event addConsultaEventi() {
		Event evento = null;
		try {
			evento = new Event();
			
			evento.setUsername(this.getCurrentUser().getUsername());
			evento.setDestination("");
			evento.setLevel(Event.Level.INFO);
			evento.setEventType(PortGareEventsConstants.CONSULTA_EVENTI);
			evento.setIpAddress(this.getCurrentUser().getIpAddress());
			evento.setSessionId(this.getRequest().getSession().getId());
			
			// prepare "Event.message" value...
			StringBuilder msg = new StringBuilder("Filtri di ricerca: ");
			boolean almenoUnCriterio = false;
			almenoUnCriterio = addFiltro(msg, "dateFrom", this.model.getDateFrom(), almenoUnCriterio);
			almenoUnCriterio = addFiltro(msg, "dateTo", this.model.getDateTo(), almenoUnCriterio);
			almenoUnCriterio = addFiltro(msg, "user", this.model.getUser(), almenoUnCriterio);
			almenoUnCriterio = addFiltro(msg, "destination", this.model.getDestination(), almenoUnCriterio);
			almenoUnCriterio = addFiltro(msg, "type", this.model.getType(), almenoUnCriterio);
			almenoUnCriterio = addFiltro(msg, "level", this.model.getLevel(), almenoUnCriterio);
			almenoUnCriterio = addFiltro(msg, "message", this.model.getMessage(), almenoUnCriterio);
			msg.append(".");
			
			if(this.getRisultati() != null && this.getRisultati().size() > 0) {
				msg.append(" ");
				msg.append(this.model.getiTotalRecords() + " eventi trovati, ");
				msg.append("pagina=" + this.model.getCurrentPage() + " (" + this.getRisultati().size() + " record), ");
				msg.append("da id=" + this.getRisultati().get(0).getId() + " a id=" + this.getRisultati().get(this.getRisultati().size() - 1).getId());
				msg.append("."); 
			} else {
				msg.append( "Nessun evento trovato.");
			}
			
			evento.setMessage(msg.toString());
		} finally {
			if(evento != null) {
				this.eventManager.insertEvent(evento);
			}
		}
		return evento;
	}

	/**
	 * Aggiunte al messaggio da inviare all'evento il filtro sul campo in input
	 * se valorizzato.
	 * 
	 * @param msg
	 *            messaggio da popolare
	 * @param campoFiltro
	 *            nome del campo di filtro da stampare nel messaggio
	 * @param campoFiltro
	 *            valore del campo di filtro inserito da interfaccia
	 * @param almenoUnCriterio
	 *            true se e' stato impostato almeno alcun criterio, false
	 *            altrimenti
	 * @return true se esiste almeno un criterio di filtro, false altrimenti
	 */
	private boolean addFiltro(StringBuilder msg, String campoFiltro, String valoreFiltro, boolean almenoUnCriterio) {
		if(StringUtils.isNotEmpty(valoreFiltro)) {
			if (almenoUnCriterio) {
				msg.append(", ");
			} else {
				almenoUnCriterio = true;
			}
			msg.append(campoFiltro).append("=").append(valoreFiltro);
		}
		return almenoUnCriterio;
	}

}
