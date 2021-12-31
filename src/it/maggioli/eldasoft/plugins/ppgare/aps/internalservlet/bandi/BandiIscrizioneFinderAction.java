package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.bandi;

import it.eldasoft.www.sil.WSGareAppalto.BandoIscrizioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;

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

	private String last;

	List<BandoIscrizioneType> listaBandi = null;

	private Date dataUltimoAggiornamento;
	
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
		try {
			// estrazione dell'elenco dei bandi in corso
			listaBandi = this.bandiManager.getElencoBandiIscrizione();
			this.dataUltimoAggiornamento = this.getMaxDataUltimoAggiornamento(listaBandi);
			this.session.put(PortGareSystemConstants.SESSION_ID_FROM_SEARCH, false);
		} catch (ApsException t) {
			ApsSystemUtils.logThrowable(t, this, "listAll");
			ExceptionUtils.manageExceptionError(t, this);
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
