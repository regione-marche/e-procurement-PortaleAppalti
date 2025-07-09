package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando;

import com.agiletec.apsadmin.system.BaseAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

/**
 * Action di gestione dell'annullamento di una richiesta di partecipazio
 *
 * @author Stefano.Sabbadin
 */
@FlussiAccessiDistinti({ 
	EFlussiAccessiDistinti.OFFERTA_GARA, 
	EFlussiAccessiDistinti.ISCRIZIONE_ELENCO, EFlussiAccessiDistinti.RINNOVO_ELENCO, 
	EFlussiAccessiDistinti.ISCRIZIONE_CATALOGO, EFlussiAccessiDistinti.RINNOVO_CATALOGO
	})
public class CancelPartecipazioneAction extends BaseAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7527283479159496491L;

	private Map<String, Object> session;

	@Validate(EParamValidation.CODICE)
	private String codice;
	private int operazione;
	@Validate(EParamValidation.DIGIT)
	private String progressivoOfferta;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public String getCodice() {
		return codice;
	}

	public int getOperazione() {
		return operazione;
	}

	public void setOperazione(int operazione) {
		this.operazione = operazione;
	}

	public String getProgressivoOfferta() {
		return progressivoOfferta;
	}

	public void setProgressivoOfferta(String progressivoOfferta) {
		this.progressivoOfferta = progressivoOfferta;
	}

	/**
	 * ... 
	 */
	public String questionCancelPartecipazione() {
		String target = SUCCESS;
		
		WizardPartecipazioneHelper partecipazioneHelper = GestioneBuste.getPartecipazioneFromSession().getHelper();

		if (partecipazioneHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			this.codice = partecipazioneHelper.getIdBando();
			this.operazione = partecipazioneHelper.getTipoEvento();
			this.progressivoOfferta = partecipazioneHelper.getProgressivoOfferta();
			
			if(partecipazioneHelper.getTipoEvento() == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA) {
				//target += ""; 
			}
			if (partecipazioneHelper.getTipoEvento() == PortGareSystemConstants.TIPOLOGIA_EVENTO_INVIA_OFFERTA) {
				target += "InvioOfferta";
			} else if (partecipazioneHelper.getTipoEvento() == PortGareSystemConstants.TIPOLOGIA_EVENTO_COMPROVA_REQUISITI) {
				target += "ComprovaRequisiti";
			}
		}
		return target;
	}

	/**
	 * ... 
	 */
	public String cancelPartecipazione() {
		String target = SUCCESS;
		
		WizardPartecipazioneHelper partecipazioneHelper = GestioneBuste.getPartecipazioneFromSession().getHelper();

		if (partecipazioneHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente
			if (partecipazioneHelper.isGaraTelematica()) {
				target = "openGestioneBuste";
				if(partecipazioneHelper.getTipoEvento() == PortGareSystemConstants.TIPOLOGIA_EVENTO_PARTECIPA_GARA) {
					target = "openGestioneBuste";
				} else if(partecipazioneHelper.isPlicoUnicoOfferteDistinte()) {
					target = "openGestioneBusteDistinte";
				}
			}
//			this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);
			this.session.remove(PortGareSystemConstants.SESSION_ID_PAGINA);
		}
		return target;
	}

}
