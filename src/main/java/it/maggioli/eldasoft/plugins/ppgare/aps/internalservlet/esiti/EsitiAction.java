package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.esiti;

import it.eldasoft.www.sil.WSGareAppalto.DettaglioEsitoType;
import it.eldasoft.www.sil.WSGareAppalto.DocumentoAllegatoType;
import it.eldasoft.www.sil.WSGareAppalto.LottoDettaglioGaraType;
import it.eldasoft.www.sil.WSGareAppalto.LottoEsitoType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IEsitiManager;

import org.apache.commons.lang.StringUtils;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;

/**
 * Action per le operazioni sugli esiti di gara. Implementa le operazioni CRUD
 * sulle schede.
 * 
 * @version 1.6
 * @author Stefano.Sabbadin
 */
public class EsitiAction extends EncodedDataAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 596942069064460224L;

	private IEsitiManager esitiManager;
	private IBandiManager bandiManager;	
	private IPageManager pageManager;
	private IAppParamManager appParamManager;
	
	@Validate(EParamValidation.CODICE)
	private String codice;
	private DettaglioEsitoType dettaglioEsito;
	private LottoEsitoType[] lotti;
	@Validate(EParamValidation.GENERIC)
	private String BDNCPUrl;
	private DocumentoAllegatoType[] attiDocumenti;
	
	/**
	 * true se il men&ugrave; bandi di gara risulta visibile, false altrimenti.
	 * Serve per bloccare il pulsante che da un dettaglio esito affidamento va
	 * al bando di gara.
	 */
	private boolean menuBandiVisibile;

	public void setEsitiManager(IEsitiManager esitiManager) {
		this.esitiManager = esitiManager;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	public void setPageManager(IPageManager pageManager) {
		this.pageManager = pageManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public boolean isEsito() {
		// usato nella jsp per definire il contesto della jsp (bando o esito)
		return true;
	}
	
	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public DettaglioEsitoType getDettaglioEsito() {
		return dettaglioEsito;
	}

	public void setDettaglioEsito(DettaglioEsitoType dettaglioEsito) {
		this.dettaglioEsito = dettaglioEsito;
	}

	public LottoEsitoType[] getLotti() {
		return lotti;
	}

	public void setLotti(LottoEsitoType[] lotti) {
		this.lotti = lotti;
	}

	public String getBDNCPUrl() {
		return BDNCPUrl;
	}

	public void setBDNCPUrl(String bDNCPUrl) {
		BDNCPUrl = bDNCPUrl;
	}

	public DocumentoAllegatoType[] getAttiDocumenti() {
		return attiDocumenti;
	}

	public void setAttiDocumenti(DocumentoAllegatoType[] attiDocumenti) {
		this.attiDocumenti = attiDocumenti;
	}
	
	public boolean isMenuBandiVisibile() {
		return menuBandiVisibile;
	}

	/**
	 * Esegue l'operazione di richiesta di visualizzazione di un dettaglio
	 * bando.
	 * 
	 * @return Il codice del risultato dell'azione.
	 */
	public String view() {
		// se si proviene dall'EncodedDataAction di InitIscrizione con un
		// errore, devo resettare il target tanto va riaperta la pagina stessa
		if ("block".equals(this.getTarget()) || "successEsito".equalsIgnoreCase(this.getTarget()))
			this.setTarget(SUCCESS);
		try {
			DettaglioEsitoType gara = this.esitiManager.getDettaglioEsito(this.codice);
			this.setDettaglioEsito(gara);
			
			if(StringUtils.isNotEmpty(this.codice) && gara == null) {
				// se non si trova esito, gara, delibera o avviso si segnala un errore
				addActionMessage(this.getText("appalto.inDefinizione"));
				setTarget(CommonSystemConstants.PORTAL_ERROR);
			} else {
				IPage page = this.pageManager.getPage("ppgare_bandi_gara");
				this.menuBandiVisibile = page.isShowable();
			}
		} catch (ApsException t) {
			ApsSystemUtils.logThrowable(t, this, "view");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}

	/**
	 * ... 
	 */
	public String viewLotti() {
		try {
			DettaglioEsitoType gara = this.esitiManager
					.getDettaglioEsito(this.codice);
			this.setDettaglioEsito(gara);
			this.setLotti( this.getLottiEsito(this.codice, gara.getLotto()) );
		} catch (ApsException t) {
			ApsSystemUtils.logThrowable(t, this, "viewLotti");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}
	
	private LottoEsitoType[] getLottiEsito(String codice, LottoDettaglioGaraType[] lottiGara) throws ApsException {
		LottoEsitoType[] lottiEsito = esitiManager.getLottiEsito(codice);
		for(int i = 0; i < lottiEsito.length; i++) {
			for(int j = 0; j < lottiGara.length; j++) {
				if( lottiGara[j].getCodiceLotto().equals(lottiEsito[i].getCodiceLotto()) ) {
					lottiEsito[i].setSoggettiAderenti(lottiGara[j].getSoggettiAderenti());
					break;
				}
			}
		}
		return lottiEsito;
	}
	
	/**
	 * visualizza schefa ANAC 
	 */
	public String viewBDNCP() {
		try {
			DettaglioEsitoType gara = esitiManager.getDettaglioEsito(this.codice);
			this.setDettaglioEsito(gara);
			this.setLotti( getLottiEsito(codice, gara.getLotto()) );
			this.setBDNCPUrl( (String)appParamManager.getConfigurationValue(AppParamManager.BDNCP_TEMPLATE_URL) );
		} catch (ApsException t) {
			ApsSystemUtils.logThrowable(t, this, "viewBDNCP");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}

	/**
	 * ... 
	 */
	public String viewAttiDocumenti() {
		try {
			DettaglioEsitoType gara = this.esitiManager
					.getDettaglioEsito(this.codice);
			this.setDettaglioEsito(gara);

			DocumentoAllegatoType[] attiDocs = this.bandiManager
					.getAttiDocumentiBandoGara(this.codice);
			this.setAttiDocumenti(attiDocs);
		} catch (ApsException t) {
			ApsSystemUtils.logThrowable(t, this, "viewAttiDocumenti");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}

}
