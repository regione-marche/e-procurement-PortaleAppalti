package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.datiimpresa;

import com.agiletec.apsadmin.system.BaseAction;
import com.agiletec.apsadmin.system.TokenInterceptor;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.BustaPartecipazione;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.GestioneBuste;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste.helper.WizardOffertaAstaHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo.WizardIscrizioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.iscralbo.WizardRinnovoHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.richpartbando.WizardPartecipazioneHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

/**
 * Action di gestione dell'annullamento di un aggiornamento anagrafica impresa
 * 
 * @author Stefano.Sabbadin
 * @since 1.2
 */
public class CancelAggiornamentoAction extends BaseAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -7527283479159496491L;

	protected Map<String, Object> session;
	@Validate(EParamValidation.URL)
	private String url;
	@Validate(EParamValidation.ACTION)
	private String actionPath;
	@Validate(EParamValidation.DIGIT)
	private String currentFrame;

	/**
	 * Se valorizzato a 1 vuol dire che si arriva da un link esterno alla
	 * funzionalit&agrave; per cui vanno bloccati i link/comandi a partire dalla
	 * presente gestione.
	 */
	@Validate(EParamValidation.GENERIC)
	private String ext;
	

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}


	public String getUrl() {
		return url;
	}

	public String getActionPath() {
		return actionPath;
	}

	public String getCurrentFrame() {
		return currentFrame;
	}

	public String getExt() {
		return ext;
	}
	
	public void setExt(String ext) {
		this.ext = ext;
	}


	/**
	 * Estrae l'helper del wizard dati dell'impresa da utilizzare nei controlli
	 * 
	 * @return helper contenente i dati dell'impresa
	 */
	protected WizardDatiImpresaHelper getSessionHelper() {
		WizardDatiImpresaHelper helper = (WizardDatiImpresaHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA);
		return helper;
	}

	public String questionCancel() {
		String target = SUCCESS;
		WizardDatiImpresaHelper helper = this.getSessionHelper();

		if (helper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		return target;
	}

	public String cancel() {
		// si popolano le informazioni per garantire il return all'azione
		// corretta precedente la chiamata del wizard di aggiornamento
		// anagrafica
		this.url = (String) this.session
			.get(PortGareSystemConstants.SESSION_ID_URL_RITORNO_AGG_ANAGRAFICA_IMPRESA);
		String fromModule = (String) this.session
			.get(PortGareSystemConstants.SESSION_ID_MODULO_RITORNO_AGG_ANAGRAFICA_IMPRESA);
		if (fromModule != null) {
			String fromAction = (String) this.session
				.get(PortGareSystemConstants.SESSION_ID_ACTION_RITORNO_AGG_ANAGRAFICA_IMPRESA);
			if (fromAction == null) {
				// utilizzo l'unica action prevista nel namespace, action dal
				// nome standard
				fromAction = "openPageImpresa";
			}
			this.actionPath = "/ExtStr2/do/FrontEnd/" + fromModule + "/"
							  + fromAction + ".action";
		}
		this.currentFrame = (String) this.session
			.get(PortGareSystemConstants.SESSION_ID_FRAME_RITORNO_AGG_ANAGRAFICA_IMPRESA);
		
		// prepara il token per il redirect
		TokenInterceptor.redirectToken();
		
		this.clearSession();
		return SUCCESS;
	}
	
	/** Si rimuovono gli oggetti non pi&ugrave; necessari dalla sessione */
	private void clearSession() {
		this.session.remove(PortGareSystemConstants.SESSION_ID_PAGINA);	
		this.session.remove(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA);
		this.session.remove(PortGareSystemConstants.SESSION_ID_URL_RITORNO_AGG_ANAGRAFICA_IMPRESA);
		this.session.remove(PortGareSystemConstants.SESSION_ID_MODULO_RITORNO_AGG_ANAGRAFICA_IMPRESA);
		this.session.remove(PortGareSystemConstants.SESSION_ID_ACTION_RITORNO_AGG_ANAGRAFICA_IMPRESA);
		this.session.remove(PortGareSystemConstants.SESSION_ID_FRAME_RITORNO_AGG_ANAGRAFICA_IMPRESA);
	}

	/**
	 * Predispone la url da richiamare per ritornare alla
	 * pagina/funzionalit&agrave; scatenante la richiesta di aggiornamento.<br>
	 * Dove necessario, si aggiorna il wizard di partenza nei soli dati
	 * anagrafici con quelli del wizard appena concluso.
	 */
	public String backAfter() {
		// si popolano le informazioni per garantire il return all'azione
		// corretta precedente la chiamata del wizard di aggiornamento
		// anagrafica, ma consentendo l'aggiornamento dei dati anagrafici nel
		// wizard da cui si e' partiti
		this.url = (String) this.session
			.get(PortGareSystemConstants.SESSION_ID_URL_RITORNO_AGG_ANAGRAFICA_IMPRESA);
		String fromModule = (String) this.session
			.get(PortGareSystemConstants.SESSION_ID_MODULO_RITORNO_AGG_ANAGRAFICA_IMPRESA);
		if (fromModule != null) {
			String fromAction = (String) this.session
				.get(PortGareSystemConstants.SESSION_ID_ACTION_RITORNO_AGG_ANAGRAFICA_IMPRESA);
			if (fromAction == null) {
				// utilizzo l'unica action prevista nel namespace, action dal nome standard 
				fromAction = "openPageImpresa";
			}
			this.actionPath = "/ExtStr2/do/FrontEnd/" + fromModule + "/"
				+ fromAction + ".action";

			WizardDatiImpresaHelper datiImpresaHelper = (WizardDatiImpresaHelper) this.session
				.get(PortGareSystemConstants.SESSION_ID_DETT_ANAGRAFICA_IMPRESA);

			// si aggiorna l'oggetto in sessione presente nel wizard da cui si
			// proviene
			if ("RichPartBando".equals(fromModule)) {
//				WizardPartecipazioneHelper partecipazioneHelper = (WizardPartecipazioneHelper) this.session
//						.get(PortGareSystemConstants.SESSION_ID_DETT_PART_GARA);
				BustaPartecipazione bustaPartecipazione = GestioneBuste.getPartecipazioneFromSession();
				WizardPartecipazioneHelper partecipazioneHelper = bustaPartecipazione.getHelper(); 
				partecipazioneHelper.setImpresa(datiImpresaHelper);
				
			} else if ("IscrAlbo".equals(fromModule)) {
				WizardIscrizioneHelper iscrizioneHelper = null;
				if ("openPageRinnovoImpresa".equals(fromAction)) {
					iscrizioneHelper = (WizardRinnovoHelper) this.session
						.get(PortGareSystemConstants.SESSION_ID_DETT_RINN_ALBO);
				} else {
					iscrizioneHelper = (WizardIscrizioneHelper) this.session
						.get(PortGareSystemConstants.SESSION_ID_DETT_ISCR_ALBO);
				}

				iscrizioneHelper.setImpresa(datiImpresaHelper);
				
			}  else if ("Aste".equals(fromModule)) {
				WizardOffertaAstaHelper offertaHelper = (WizardOffertaAstaHelper) this.session
					.get(PortGareSystemConstants.SESSION_ID_DETT_OFFERTA_ASTA);
				offertaHelper.getOffertaEconomica().setImpresa(datiImpresaHelper);
			}
		}
		this.currentFrame = (String) this.session
			.get(PortGareSystemConstants.SESSION_ID_FRAME_RITORNO_AGG_ANAGRAFICA_IMPRESA);

		// prepara il token per il redirect
		TokenInterceptor.redirectToken();
		
		this.clearSession();
		return SUCCESS;
	}
	
}
