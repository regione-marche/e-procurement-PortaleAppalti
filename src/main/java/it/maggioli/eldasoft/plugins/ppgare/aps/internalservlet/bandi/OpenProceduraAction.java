package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.bandi;

import org.apache.commons.lang3.StringUtils;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.opensymphony.xwork2.ActionContext;

import it.eldasoft.www.sil.WSGareAppalto.DeliberaType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioAvvisoType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioEsitoType;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioGaraType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.avvisi.AvvisiFinderAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.esiti.EsitiFinderAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IAvvisiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IEsitiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.cataloghi.SearchResult;

/**
 * Action per l'apertura generica di esiti, bandi, delibere e avvisi.
 *
 */
public class OpenProceduraAction extends EncodedDataAction { 
	/**
	 * UID
	 */
	private static final long serialVersionUID = -6413822902770299599L;
	
	private static final String SUCCESS_ESITO 		= "successEsito";
	private static final String SUCCESS_BANDO 		= "successBando";
	private static final String SUCCESS_DELIBERA 	= "successDelibera";
	private static final String SUCCESS_AVVISO 		= "successAvviso";
	
	private IBandiManager bandiManager;
	private IEsitiManager esitiManager;
	private IAvvisiManager avvisiManager;

	@Validate(EParamValidation.CODICE)
	private String codice;
	@Validate(EParamValidation.CIG)
	private String cig;
	
	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public void setEsitiManager(IEsitiManager esitiManager) {
		this.esitiManager = esitiManager;
	}

	public void setAvvisiManager(IAvvisiManager avvisiManager) {
		this.avvisiManager = avvisiManager;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getCodice() {
		return codice;
	}

	public String getCig() {
		return cig;
	}

	public void setCig(String cig) {
		this.cig = cig;
	}

	/**
	 * aprertura unificata per esiti, bandi, determine/delibere  
	 */
	public String openProceduraByCodice() {		
		setTarget(SUCCESS);
		try {
			setTarget( openProcedura(false, codice) );
		} catch (ApsException t) {
			ApsSystemUtils.logThrowable(t, this, "openProceduraByCodice");
			ExceptionUtils.manageExceptionError(t, this);
			setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return getTarget();
	}
	
	/**
	 * aprertura unificata per esiti, bandi, determine/delibere  
	 */
	public String openProceduraByCig() {
		setTarget(SUCCESS);
		try {
			setTarget( openProcedura(true, cig) );
		} catch (ApsException t) {
			ApsSystemUtils.logThrowable(t, this, "openProceduraByCig");
			ExceptionUtils.manageExceptionError(t, this);
			setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return getTarget();
	}

	/**
	 * verifica se aprire esiti, bando di gara, delibera o avviso in base al codice (codice gara o codice cig)
	 */
	private String openProcedura(boolean byCig, String codice) throws ApsException {
		String target = SUCCESS;
		
		getParameters().put("ext", null);
		getRequest().getSession().setAttribute(PortGareSystemConstants.SESSION_ID_FROM_SEARCH, null);

		DettaglioEsitoType esito = (byCig
				? esitiManager.getDettaglioEsitoByCig(codice)
				: esitiManager.getDettaglioEsito(codice)
		);
		if(esito != null && esito.getDatiGenerali() != null) {
			if(byCig)
				ActionContext.getContext().getParameters().put("codice", esito.getDatiGenerali().getCodice());
			setSessionAttributeIfEmpty(PortGareSystemConstants.SESSION_ID_FROM_PAGE, EsitiFinderAction.LIST_ALL_IN_CORSO);
			target = SUCCESS_ESITO;
		} else {
			DettaglioGaraType gara = (byCig
					? bandiManager.getDettaglioGaraByCig(codice)
					: bandiManager.getDettaglioGara(codice)
			);
			boolean accessoConsentito = !BandiAction.isAccessoNonConsentitoDatiProcedura(gara, getCurrentUser());
			if(gara != null && accessoConsentito) {
				setSessionAttributeIfEmpty(PortGareSystemConstants.SESSION_ID_FROM_PAGE, BandiFinderAction.LIST_ALL_IN_CORSO);
				if(byCig)
					ActionContext.getContext().getParameters().put("codice", gara.getDatiGeneraliGara().getCodice());
				target = SUCCESS_BANDO;
			} else {
				SearchResult<DeliberaType> delibere = (byCig
						? bandiManager.getDelibere(null, null, null, codice, null, null, null, null, 0, 1)
						: bandiManager.getDelibere(null, null, null, null, null, null, codice, null, 0, 1)
				);				
				if(delibere != null && delibere.getDati() != null && delibere.getDati().size() > 0) {
					// "codice" non serve perche' il parametro "cig" viene passato direttamente alla OpenSearchDelibereAction
					setSessionAttributeIfEmpty(PortGareSystemConstants.SESSION_ID_FROM_PAGE, OpenSearchDelibereAction.LIST_DELIBERE);
					target = SUCCESS_DELIBERA;
				} else {
					DettaglioAvvisoType avviso = (byCig
							? avvisiManager.getDettaglioAvvisoByCig(codice)
							: avvisiManager.getDettaglioAvviso(codice)
					);
					if(avviso != null && avviso.getDatiGenerali() != null) {
						if(byCig)
							ActionContext.getContext().getParameters().put("codice", avviso.getDatiGenerali().getCodice());
						setSessionAttributeIfEmpty(PortGareSystemConstants.SESSION_ID_FROM_PAGE, AvvisiFinderAction.LIST_ALL_IN_CORSO);
						target = SUCCESS_AVVISO;
					} else {
						// se non si trova esito, gara, delibera o avviso si segnala un errore
						addActionMessage(this.getText("appalto.inDefinizione"));
						target = CommonSystemConstants.PORTAL_ERROR;
					}
				}
			}
		}
		return target; 
	}
	
	/**
	 * ... 
	 */
	private void setSessionAttributeIfEmpty(String key, String defaultValue) {
		if(StringUtils.isEmpty((String)getRequest().getSession().getAttribute(PortGareSystemConstants.SESSION_ID_FROM_PAGE))) 
				getRequest().getSession().setAttribute(PortGareSystemConstants.SESSION_ID_FROM_PAGE, defaultValue);
	}

}
