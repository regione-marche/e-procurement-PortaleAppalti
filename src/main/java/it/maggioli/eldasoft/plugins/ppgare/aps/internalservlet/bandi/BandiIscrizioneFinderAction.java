package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.bandi;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import it.eldasoft.www.sil.WSGareAppalto.BandoIscrizioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Classe Action per la gestione della ricerca e visualizzazione lista bandi d'iscrizione.
 * 
 * @version 1.0
 * @author Stefano.Sabbadin
 */
public class BandiIscrizioneFinderAction extends EncodedDataAction implements SessionAware {

	/**
	 * UID
	 */
	private static final long serialVersionUID = -6702048426810895066L;

	private IBandiManager bandiManager;
	private Map<String, Object> session;

	@Validate(EParamValidation.DIGIT)
	private String last;
	private Boolean isAttivo;

	List<BandoIscrizioneType> listaBandi = null;

	private Date dataUltimoAggiornamento;

	public Boolean getIsAttivo() {
		return isAttivo;
	}

	public void setIsAttivo(Boolean attivo) {
		isAttivo = attivo;
	}

	/**
	 * @return the bandi
	 */
	public List<BandoIscrizioneType> getListaBandi() {
		return listaBandi;
	}	
	
	/**
	 * @return the dataUltimoAggiornamento
	 */
	public Date getDataUltimoAggiornamento() {
		return dataUltimoAggiornamento;
	}

	/**
	 * calcola la MAX data di ultimo aggiormento nell'elenco bandi iscrizione  
	 */
	private Date getMaxDataUltimoAggiornamento(List<BandoIscrizioneType> lista) {
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
	 * Restituisce la lista di tutti i bandi d'iscrizione
	 */
	public String listAll() {
		return listAll(true);
	}

	public String listAllArchiviata() {
		return listAll(false);
	}

	private String listAll(boolean isAttivo) {
		try {
			ApsSystemUtils.getLogger().debug("BandiIscrizioneFinderAction.listAll");
			this.isAttivo = isAttivo;
			// estrazione dell'elenco dei bandi in corso
			listaBandi = this.bandiManager.getElencoBandiIscrizione(isAttivo);
			this.dataUltimoAggiornamento = this.getMaxDataUltimoAggiornamento(listaBandi);
			this.session.put(PortGareSystemConstants.SESSION_ID_FROM_SEARCH, false);
		} catch (ApsException t) {
			ApsSystemUtils.logThrowable(t, this, "listAll");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, this, "listAll");
			ExceptionUtils.manageExceptionError(e, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}
	
	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
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
	
}
