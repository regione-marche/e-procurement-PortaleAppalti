package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi;

import com.agiletec.aps.system.ApsSystemUtils;
import it.eldasoft.www.sil.WSGareAppalto.BandoIscrizioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.ICataloghiManager;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Classe Action per la gestione della ricerca e visualizzazione lista bandi
 * d'iscrizione.
 *
 * @version 1.8.5
 * @author Stefano.Sabbadin
 */
public class CataloghiFinderAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 673040492444579273L;

	private ICataloghiManager cataloghiManager;
	private Map<String, Object> session;

	@Validate(EParamValidation.DIGIT)
	private String last;

	List<BandoIscrizioneType> listaCataloghi = null;
	
	private Date dataUltimoAggiornamento;

	private Boolean isAttivo = true;

	public void setCataloghiManager(ICataloghiManager cataloghiManager) {
		this.cataloghiManager = cataloghiManager;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public String getLast() {
		return last;
	}

	public void setLast(String last) {
		this.last = last;
	}

	public List<BandoIscrizioneType> getListaCataloghi() {
		return listaCataloghi;
	}
	
	public Date getDataUltimoAggiornamento() {
		return dataUltimoAggiornamento;
	}

	public Boolean getIsAttivo() {
		return isAttivo;
	}

	public void setIsAttivo(Boolean attivo) {
		isAttivo = attivo;
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
	 * Restituisce la lista di tutti i cataloghi attivi.
	 *
	 * @return lista di tutti i cataloghi
	 */
	public String listAll() {
		return listAll(true);
	}

	/**
	 * Restituisce la lista di tutti i cataloghi archiviati.
	 *
	 * @return lista di tutti i cataloghi
	 */
	public String listAllArchiviati() {
		return listAll(false);
	}

	private String listAll(boolean isAttivo) {
		try {
			// estrazione dell'elenco dei bandi in corso
			this.isAttivo = isAttivo;
			listaCataloghi = this.cataloghiManager.getElencoCataloghi(isAttivo);
			this.dataUltimoAggiornamento = this.getMaxDataUltimoAggiornamento(listaCataloghi);
			this.session.put(PortGareSystemConstants.SESSION_ID_FROM_SEARCH, false);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "listAll");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}
	
}
