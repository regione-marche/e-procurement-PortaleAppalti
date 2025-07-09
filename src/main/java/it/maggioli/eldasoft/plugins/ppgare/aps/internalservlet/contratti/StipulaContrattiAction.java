	package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.contratti;

	import com.agiletec.aps.system.ApsSystemUtils;
	import com.agiletec.aps.system.RequestContext;
	import com.agiletec.aps.system.SystemConstants;
	import com.agiletec.aps.system.exception.ApsException;
	import com.agiletec.aps.system.services.page.IPage;
	import com.agiletec.aps.system.services.page.IPageManager;
	import com.agiletec.aps.system.services.url.IURLManager;
	import com.agiletec.aps.system.services.url.PageURL;
	import com.opensymphony.xwork2.ModelDriven;
	import it.eldasoft.www.sil.WSGareAppalto.StatisticheComunicazioniPersonaliType;
	import it.eldasoft.www.sil.WSStipule.ElencoStipuleContrattiOutType;
	import it.eldasoft.www.sil.WSStipule.StipulaContrattoType;
	import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
	import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.comunicazioni.beans.ComunicazioniConstants;
	import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
	import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
	import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
	import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
	import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IContrattiManager;
	import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IStipuleManager;
	import org.apache.struts2.interceptor.ServletResponseAware;
	import org.apache.struts2.interceptor.SessionAware;

	import javax.servlet.http.HttpServletRequest;
	import javax.servlet.http.HttpServletResponse;
	import java.util.ArrayList;
	import java.util.List;
	import java.util.Map;

/**
 * Azione per la gestione delle operazioni a livello di stipula contratto.
 * 
 * @author michele.dinapoli
 * @since 1.11.5
 */
public class StipulaContrattiAction extends EncodedDataAction 
	implements SessionAware, ModelDriven<StipuleSearchBean>,  ServletResponseAware 
{   
	/**
	 * UID
	 */
	private static final long serialVersionUID = 5477854445026886811L;

	private List<StipulaContrattoType> stipule; 
	@Validate
    private StipuleSearchBean model = new StipuleSearchBean();
    
    private Map<String, Object> session;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;	
	}
	
	/** Manager per l'interrogazione dei contratti. */
	private IContrattiManager contrattiManager;

	/** Manager per l'interrogazione delle comunicazioni. */
	private IBandiManager bandiManager;
	
	private IStipuleManager stipuleManager;
	protected IURLManager urlManager;
	protected IPageManager pageManager;
	private String id;
	
	protected HttpServletResponse response;

	@Validate(EParamValidation.CODICE)
	private String codice;
	
	private StipulaContrattoType dettaglioStipula;
	
	private int numComunicazioniRicevute;
	private int numComunicazioniRicevuteDaLeggere;
	private int numComunicazioniArchiviate;
	private int numComunicazioniArchiviateDaLeggere;
	private int numSoccorsiIstruttori;
	private int numComunicazioniInviate;
	
	private Long idComunicazione;
	private Long idDestinatario;
	
	public int getNumComunicazioniRicevute() {
		return numComunicazioniRicevute;
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	public void setStipuleManager(IStipuleManager stipuleManager) {
		this.stipuleManager = stipuleManager;
	}

	public void setNumComunicazioniRicevute(int numComunicazioniRicevute) {
		this.numComunicazioniRicevute = numComunicazioniRicevute;
	}

	public int getNumComunicazioniRicevuteDaLeggere() {
		return numComunicazioniRicevuteDaLeggere;
	}

	public void setNumComunicazioniRicevuteDaLeggere(
			int numComunicazioniRicevuteDaLeggere) {
		this.numComunicazioniRicevuteDaLeggere = numComunicazioniRicevuteDaLeggere;
	}

	public int getNumComunicazioniArchiviate() {
		return numComunicazioniArchiviate;
	}

	public void setNumComunicazioniArchiviate(int numComunicazioniArchiviate) {
		this.numComunicazioniArchiviate = numComunicazioniArchiviate;
	}

	public int getNumComunicazioniArchiviateDaLeggere() {
		return numComunicazioniArchiviateDaLeggere;
	}

	public void setNumComunicazioniArchiviateDaLeggere(int numComunicazioniArchiviateDaLeggere) {
		this.numComunicazioniArchiviateDaLeggere = numComunicazioniArchiviateDaLeggere;
	}

	public int getNumSoccorsiIstruttori() {
		return numSoccorsiIstruttori;
	}

	public void setNumSoccorsiIstruttori(int numSoccorsiIstruttori) {
		this.numSoccorsiIstruttori = numSoccorsiIstruttori;
	}

	public int getNumComunicazioniInviate() {
		return numComunicazioniInviate;
	}

	public void setNumComunicazioniInviate(int numComunicazioniInviate) {
		this.numComunicazioniInviate = numComunicazioniInviate;
	}

	public StipuleSearchBean getModel() {
		return model;
	}

	public void setModel(StipuleSearchBean model) {
		this.model = model;
	}

	public void setContrattiManager(IContrattiManager contrattiManager) {
		this.contrattiManager = contrattiManager;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public void setUrlManager(IURLManager urlManager) {
		this.urlManager = urlManager;
	}

	public void setPageManager(IPageManager pageManager) {
		this.pageManager = pageManager;
	}
	
	public List<StipulaContrattoType> getStipule() {
		return stipule;
	}

	public void setStipule(List<StipulaContrattoType> stipule) {
		this.stipule = stipule;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public StipulaContrattoType getDettaglioStipula() {
		return dettaglioStipula;
	}

	public void setDettaglioStipula(StipulaContrattoType dettaglioStipula) {
		this.dettaglioStipula = dettaglioStipula;
	}

	public Long getIdComunicazione() {
		return idComunicazione;
	}

	public void setIdComunicazione(Long idComunicazione) {
		this.idComunicazione = idComunicazione;
	}

	public Long getIdDestinatario() {
		return idDestinatario;
	}

	public void setIdDestinatario(Long idDestinatario) {
		this.idDestinatario = idDestinatario;
	}

	/**
	 * ... 
	 */
	public String open(){
		String target = SUCCESS;
		try {
			this.session.remove(PortGareSystemConstants.SESSION_ID_FROM_PAGE);
			this.model = (StipuleSearchBean)this.session.get(PortGareSystemConstants.SESSION_ID_SEARCH_STIPULE);
			if(this.model == null){
				this.model = new StipuleSearchBean();
			}
			String statoValue = this.model.getStato();
			Integer stato = null;
			if(statoValue != null && !"".equals(statoValue)){
				stato = new Integer(statoValue);
			}
			int startIndex = 0;
			if(this.model.getCurrentPage() > 0){
				startIndex = this.model.getiDisplayLength() * (this.model.getCurrentPage() - 1);
			}
			String username = this.getCurrentUser().getUsername();
			ElencoStipuleContrattiOutType risultato = this.stipuleManager.searchStipuleContratti(model.getCodstipula(), model.getOggetto(), stato, model.getStazioneAppaltante(), username, startIndex, this.model.getiDisplayLength());
			StipulaContrattoType[] stipuleArray  = risultato.getElenco();
			ArrayList<StipulaContrattoType> stipuleList = new ArrayList<StipulaContrattoType>();
			if(stipuleArray != null){
				for(int i = 0; i< stipuleArray.length; i++){
					stipuleList.add(stipuleArray[i]);
				}
			}
			this.stipule = stipuleList;
			this.model.processResult(stipuleList.size(), risultato.getNum());
			this.session.put(PortGareSystemConstants.SESSION_ID_SEARCH_STIPULE,
							 this.model);
		} catch (ApsException e) {
			this.addActionError(this.getText("Errors.stipule.configuration", new String[] { "Stipula Contratti" }));
			this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
			ApsSystemUtils.logThrowable(e, null, "open");
		}
		return target;
	}
	
	/**
	 * ...
	 */
	public String search(){
		String target = SUCCESS;
		try {
			
			String statoValue = this.model.getStato();
			Integer stato = null;
			if(statoValue != null && !"".equals(statoValue)){
				stato = new Integer(statoValue);
			}
			int startIndex = 0;
			if(this.model.getCurrentPage() > 0){
				startIndex = this.model.getiDisplayLength() * (this.model.getCurrentPage() - 1);
			}
			String username = this.getCurrentUser().getUsername();
			ElencoStipuleContrattiOutType risultato = this.stipuleManager.searchStipuleContratti(model.getCodstipula(), model.getOggetto(), stato, model.getStazioneAppaltante(), username, startIndex, this.model.getiDisplayLength());
			StipulaContrattoType[] stipuleArray  = risultato.getElenco();
			ArrayList<StipulaContrattoType> stipuleList = new ArrayList<StipulaContrattoType>();
			if(stipuleArray != null){
				for(int i = 0; i< stipuleArray.length; i++){
					stipuleList.add(stipuleArray[i]);
				}
			}
			this.stipule = stipuleList;
			this.model.processResult(stipuleList.size(), risultato.getNum());
			this.session.put(PortGareSystemConstants.SESSION_ID_SEARCH_STIPULE,
							 this.model);
		} catch (ApsException e) {
			this.addActionError(this.getText("Errors.stipule.configuration", new String[] { "Stipula Contratti" }));
			this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
			ApsSystemUtils.logThrowable(e, null, "search");
		}
		return target;
	}
	
	/**
	 * ... 
	 */
	public String detail(){
		String target = SUCCESS;
		try {
			String username = this.getCurrentUser().getUsername();
			this.dettaglioStipula = this.stipuleManager.getDettaglioStipulaContratto(this.codice, username, true);
			StatisticheComunicazioniPersonaliType stat = null;
			stat = this.bandiManager.getStatisticheComunicazioniPersonali(this.getCurrentUser().getUsername(), this.codice,null, null);
			this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_ENTITA_PROCEDURA, "G1STIPULA");
			this.setNumComunicazioniRicevute(stat.getNumComunicazioniRicevute());
			this.setNumComunicazioniRicevuteDaLeggere(stat.getNumComunicazioniRicevuteDaLeggere());
			this.setNumComunicazioniArchiviate(stat.getNumComunicazioniArchiviate());
			this.setNumComunicazioniArchiviateDaLeggere(stat.getNumComunicazioniArchiviateDaLeggere());
			this.setNumSoccorsiIstruttori(stat.getNumSoccorsiIstruttori());
			this.setNumComunicazioniInviate(stat.getNumComunicazioniInviate());
			this.session.put(ComunicazioniConstants.SESSION_ID_COMUNICAZIONI_CODICE_PROCEDURA, this.codice);
		} catch (ApsException e) {
			this.addActionError(this.getText("Errors.stipule.configuration", new String[] { "Stipula Contratti" }));
			this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
			ApsSystemUtils.logThrowable(e, null, "detail");
		}
		return target;
	}
	
	/**
	 * ... 
	 */
	public String getUrlErrori() {
		HttpServletRequest request = this.getRequest();
		RequestContext reqCtx = new RequestContext();
		reqCtx.setRequest(request);
		reqCtx.setResponse(response);
//		Lang currentLang = this.getLangManager().getDefaultLang();
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG, getCurrentLang());
		IPage pageDest = pageManager.getPage("portalerror");
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE, pageDest);
		request.setAttribute(RequestContext.REQCTX, reqCtx);
		PageURL url = this.urlManager.createURL(reqCtx);
		url.addParam(RequestContext.PAR_REDIRECT_FLAG, "1");
		return url.getURL();
	}
	
}
