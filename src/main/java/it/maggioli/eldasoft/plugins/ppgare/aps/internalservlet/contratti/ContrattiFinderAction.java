package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.contratti;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.user.UserDetails;
import com.opensymphony.xwork2.ModelDriven;
import it.eldasoft.www.sil.WSGareAppalto.ContrattoType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IContrattiManager;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Classe Action per la gestione della ricerca e visualizzazione lista contratti.
 * 
 * @version 1.9.1
 * @author Stefano.Sabbadin
 */
public class ContrattiFinderAction extends EncodedDataAction implements
	SessionAware, ModelDriven<ContrattiSearchBean> 
{
    /**
	 * UID
	 */
	private static final long serialVersionUID = -3463997215453176670L;

	private IContrattiManager contrattiManager;

    private Map<String, Object> session;

	@Validate
    private ContrattiSearchBean model = new ContrattiSearchBean();

	@Validate(EParamValidation.DIGIT)
    private String last;

    List<ContrattoType> listaContratti = null;
    
    private Date dataUltimoAggiornamento;

	@Validate(EParamValidation.STAZIONE_APPALTANTE)
    private String stazioneAppaltante;
    
	
	public void setContrattiManager(IContrattiManager contrattiManager) {
		this.contrattiManager = contrattiManager;
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	@Override
	public ContrattiSearchBean getModel() {
		return this.model;
	}
	
	public String getLast() {
		return last;
	}

	public void setLast(String last) {
		this.last = last;
	}

	public List<ContrattoType> getListaContratti() {
		return listaContratti;
	}	

	public Date getDataUltimoAggiornamento() {
		return dataUltimoAggiornamento;
	}

	public String getStazioneAppaltante() {
		return stazioneAppaltante;
	}
	
	/**
	 * calcola la MAX data di ultimo aggiormento nell'elenco bandi  
	 */
	private Date getMaxDataUltimoAggiornamento(List<ContrattoType> lista) {
		Date dta = null;
		if(lista != null) {			
			for(int i = 0; i < lista.size(); i++) {
				if( dta == null ) {
					dta = lista.get(i).getDataUltimoAggiornamento();
				} 
				if( lista.get(i).getDataUltimoAggiornamento() != null && 
					lista.get(i).getDataUltimoAggiornamento().compareTo(dta) > 0 ) {
					dta = lista.get(i).getDataUltimoAggiornamento();					
				}				
			}
		}		
		return dta;		
	}

	/**
	 * Restituisce la lista dei contratti stipulati con l'impresa.
	 */
	public String findContratti() {
		
		if ("1".equals(this.last)) {
			// se si richiede il rilancio dell'ultima estrazione effettuata,
			// allora si prendono dalla sessione i filtri applicati e si
			// caricano nel presente oggetto
			ContrattiSearchBean finder = (ContrattiSearchBean) this.session
					.get(PortGareSystemConstants.SESSION_ID_SEARCH_CONTRATTI);
			if(finder != null){
				// Inserito nel caso in cui il percorso seguito fosse 
				// area personale > coumincazione per ODA > vai alla procedura > torna alla lista 
				this.model = finder;
			}
		}
		
		// se è stata impostata una stazione appaltante nei parametri del portale
		// allora i dati vanno sempre filtrati per questa stazione appaltante...
		this.stazioneAppaltante = this.getCodiceStazioneAppaltante(); 
		if(this.stazioneAppaltante != null) {
			this.model.setStazioneAppaltante(this.stazioneAppaltante);
		}

		UserDetails userDetails = this.getCurrentUser();
		if (null != userDetails
			&& !userDetails.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			try {
				// estrazione dell'elenco dei bandi
				this.listaContratti = this.contrattiManager.searchContratti(
						this.model.getStazioneAppaltante(),
						this.model.getOggetto(),
						this.model.getCig(), 
						this.model.getStato(),
						userDetails.getUsername());
				
				this.dataUltimoAggiornamento = this.getMaxDataUltimoAggiornamento(listaContratti);
				
				// salvataggio dei criteri di ricerca in sessione per la
				// prossima riapertura della form
				this.session.put(PortGareSystemConstants.SESSION_ID_SEARCH_CONTRATTI,
							     this.model);
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "findContratti");
				ExceptionUtils.manageExceptionError(t, this);
				this.setTarget(CommonSystemConstants.PORTAL_ERROR);
			}
		} else {
		    this.addActionError(this.getText("Errors.sessionExpired"));
		    this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}

		return this.getTarget();
	}
	
}
