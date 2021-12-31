package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.agiletec.apsadmin.system.BaseAction;

/**
 * Action di gestione dell'annullamento di una procedura di offerta economica gestita telematicamente.
 * 
 * @author Stefano.Sabbadin
 */
public class CancelOffertaEconomicaAction extends BaseAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7214980651040758457L;

	protected Map<String, Object> session;

	private String codice;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public int getInviaOfferta() {
		return PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA;
	}
	
	/**
	 * Estrae l'helper del wizard dati offerta da utilizzare nei controlli.
	 * 
	 * @return helper contenente i dati dell'impresa
	 */
	protected WizardOffertaEconomicaHelper getSessionHelper() {
		WizardOffertaEconomicaHelper helper = (WizardOffertaEconomicaHelper) session
				.get(PortGareSystemConstants.SESSION_ID_OFFERTA_ECONOMICA);
		return helper;
	}

	public String questionCancel() {
		String target = "confirm";
		WizardOffertaEconomicaHelper helper = this.getSessionHelper();

		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		return target;
	}

	public String cancel() {
		String target = SUCCESS;
		WizardOffertaEconomicaHelper helper = this.getSessionHelper();
		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			WizardPartecipazioneHelper partecipazioneHelper = (WizardPartecipazioneHelper)this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);			
			if(partecipazioneHelper.isPlicoUnicoOfferteDistinte()) {
				target = "openPageListaBusteEconomicheDistinte";
			}
			this.clearSession();
		}

		return target;
	}

	/** Si rimuovono gli oggetti non pi&ugrave; necessari dalla sessione */
	private void clearSession() {
		this.session.remove(PortGareSystemConstants.SESSION_ID_OFFERTA_ECONOMICA);
		this.session.remove(PortGareSystemConstants.SESSION_ID_PAGINA);
	}
	
}