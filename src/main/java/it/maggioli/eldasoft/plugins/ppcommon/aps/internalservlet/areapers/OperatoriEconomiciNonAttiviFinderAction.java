package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.areapers;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.apsadmin.system.BaseAction;
import com.opensymphony.xwork2.ModelDriven;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.monitoraggio.IMonitoraggioManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.monitoraggio.OperatoreEconomicoKO;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.locale.LocaleConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Action per la gestione dell'estrazione della lista utenti registrati al
 * portale ma non ancora attivati.
 * 
 * @author Eleonora.Favaro
 */
public class OperatoriEconomiciNonAttiviFinderAction extends BaseAction
		implements SessionAware,
		ModelDriven<OperatoriEconomiciNonAttiviSearchBean> 
{
	/**
	 * UID
	 */
	private static final long serialVersionUID = -2337408061902345976L;

	/** Contenitore dei dati di sessione. */
	private Map<String, Object> session;
	private IMonitoraggioManager monitoraggioManager;
	/** Output: elenco utenti non ancora attivi. */
	@Validate
	private List<OperatoreEconomicoKO> listaOperatoriKO;
	/** Contenitore dei dati di ricerca. */
	@Validate
	private OperatoriEconomiciNonAttiviSearchBean model = new OperatoriEconomiciNonAttiviSearchBean();
	/**
	 * Valorizzato a 1 quando si deve poi tornare alla lista utenti ripetendo
	 * l'ultima ricerca effettuata.
	 */
	@Validate(EParamValidation.DIGIT)
	private String last;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setMonitoraggioManager(IMonitoraggioManager monitoraggioManager){
		this.monitoraggioManager = monitoraggioManager;
	}

	@Override
	public OperatoriEconomiciNonAttiviSearchBean getModel() {
		return this.model;
	}

	/**
	 * @return the last
	 */
	public String getLast() {
		return last;
	}

	/**
	 * @param last
	 *            the last to set
	 */
	public void setLast(String last) {
		this.last = last;
	}


	/**
	 * @return the listaOperatoriKO
	 */
	public List<OperatoreEconomicoKO> getListaOperatoriKO() {
		return listaOperatoriKO;
	}

	/**
	 * @param listaOperatoriKO the listaOperatoriKO to set
	 */
	public void setListaOperatoriKO(List<OperatoreEconomicoKO> listaOperatoriKO) {
		this.listaOperatoriKO = listaOperatoriKO;
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
	 * Predispone l'apertura della pagina con l'elenco degli operatori non
	 * ancora registrati (di default sono estratti tutti quelli non registrati).
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
			// alla prima apertura della pagina in automatico si scatena una ricerca senza filtri
			this.model = new OperatoriEconomiciNonAttiviSearchBean();
			this.session.put(AreaPersonaleConstants.SESSION_ID_SEARCH_OPERATORI_ECONOMICI_KO, this.model);
			target = search();
		}
		return target;
	}

	/**
	 * Estrae la lista degli utenti non attivati sulla base dei criteri di
	 * filtro impostati nella form.
	 * 
	 * @return target struts di esito dell'operazione
	 */
	public String search(){
		String target = SUCCESS;

		boolean paramOK = true;

		// funzionalita' disponibile solo per utenti amministratori loggati
		if ( !this.isCurrentUserMemberOf(SystemConstants.ADMIN_ROLE) ) {
			this.addActionError(this.getText("Errors.function.notEnabled"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}else{
			if ("1".equals(this.last)) {
				// se si richiede il rilancio dell'ultima estrazione effettuata,
				// allora si prendono dalla sessione i filtri applicati e si
				// caricano nel presente oggetto
				OperatoriEconomiciNonAttiviSearchBean finder = (OperatoriEconomiciNonAttiviSearchBean) this.session
						.get(AreaPersonaleConstants.SESSION_ID_SEARCH_OPERATORI_ECONOMICI_KO);
				this.model = finder;
			}

			/* --- CONTROLLO FORMALE DELLE DATE --- */
			Date dtRegistrazioneDa = null;
			if(StringUtils.stripToNull(this.model.getDataRegistrazioneDa()) != null){
				try {
					dtRegistrazioneDa = (Date) LocaleConvertUtils.convert(
							this.model.getDataRegistrazioneDa(), java.sql.Date.class,
							"dd/MM/yyyy");
				} catch (ConversionException e) {
					ApsSystemUtils.logThrowable(e, this, "search");
					this.addActionErrorDateInvalid("LABEL_OP_KO_DATA_REGISTRAZIONE", DA_DATA, this.model.getDataRegistrazioneDa());
					this.model.setDataRegistrazioneDa(null);
					paramOK = false;
				}
			}

			Date dtRegistrazioneA = null;
			if(StringUtils.stripToNull(this.model.getDataRegistrazioneA())!=null){
				try {
					dtRegistrazioneA = (Date) LocaleConvertUtils.convert(
							this.model.getDataRegistrazioneA(), java.sql.Date.class,
							"dd/MM/yyyy");
				} catch (ConversionException e) {
					ApsSystemUtils.logThrowable(e, this, "search");
					this.addActionErrorDateInvalid("LABEL_OP_KO_DATA_REGISTRAZIONE", A_DATA, this.model.getDataRegistrazioneA());
					this.model.setDataRegistrazioneA(null);
					paramOK = false;
				}
			}

			if(paramOK){
				this.setListaOperatoriKO(this.monitoraggioManager.getOperatoriEconomiciKO(model.getUtente(), model.getEmail(), dtRegistrazioneDa, dtRegistrazioneA));
				// salvataggio dei criteri di ricerca in sessione per la
				// prossima riapertura della form
				this.session.put(AreaPersonaleConstants.SESSION_ID_SEARCH_OPERATORI_ECONOMICI_KO, this.model);			
			}

		}

		return target;
	}

}
