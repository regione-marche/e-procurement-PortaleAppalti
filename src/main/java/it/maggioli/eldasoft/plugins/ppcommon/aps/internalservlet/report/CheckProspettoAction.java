package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.report;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import it.eldasoft.report.datatypes.DefinizioneReportType;
import it.eldasoft.www.Report.ValParametroType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.report.IReportManager;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.apsadmin.system.BaseAction;

/**
 * Verifica la presenza di parametri per generare il prospetto di dettaglio del
 * lotto.
 * 
 * @author Stefano.Sabbadin
 */
public class CheckProspettoAction extends BaseAction implements SessionAware {

	/**
	 * UID
	 */
	private static final long serialVersionUID = -6416466117319510884L;

	/**
	 * Verifica la presenza di parametri e decide se caricare la pagina di
	 * inserimento parametri per l'estrazione del report oppure esegue
	 * direttamente il report.
	 * 
	 * @return target di destinazione dell'azione
	 */
	public String check() {
		String target = SUCCESS;
		try {
			DefinizioneReportType def = this._reportManager
					.getDefinizioneReport(this.id);
			this.setDefinizione(def);
			if (def.sizeOfParametroArray() > 0) {
				target = "setParametri";
			} else {
				this.session.put("parametriReport", new ValParametroType[0]);
				this.session.put("titoloReport", def.getNome());
			}
		} catch (ApsException t) {
			ApsSystemUtils.logThrowable(t, this, "check");
			ExceptionUtils.manageExceptionError(t, this);
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		return target;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	/**
	 * @param reportManager
	 *            the reportManager to set
	 */
	public void setReportManager(IReportManager reportManager) {
		this._reportManager = reportManager;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @param definizione
	 *            the definizione to set
	 */
	public void setDefinizione(DefinizioneReportType definizione) {
		this.definizione = definizione;
	}

	/**
	 * @return the definizione
	 */
	public DefinizioneReportType getDefinizione() {
		return definizione;
	}

	/** Sessione HTTP. */
    private Map<String, Object> session;

	/** Riferimento al web service Report */
	private IReportManager _reportManager;

	/** Codice del report. */
	private String id;

	/** Tipo di output del report. */
	private String type;

	/** Definizione del report. */
	private DefinizioneReportType definizione;

}