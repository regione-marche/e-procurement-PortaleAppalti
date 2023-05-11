package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.esiti;

import it.eldasoft.www.sil.WSGareAppalto.DettaglioEsitoType;
import it.eldasoft.www.sil.WSGareAppalto.DocumentoAllegatoType;
import it.eldasoft.www.sil.WSGareAppalto.LottoEsitoType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IEsitiManager;

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
	 * UID.
	 */
	private static final long serialVersionUID = 596942069064460224L;

	private IEsitiManager esitiManager;
	private IBandiManager bandiManager;	
	private IPageManager pageManager;
	@Validate(EParamValidation.CODICE)
	private String codice;

	private DettaglioEsitoType dettaglioEsito;

	private LottoEsitoType[] lotti;
	private DocumentoAllegatoType[] attiDocumenti;

	/**
	 * true se il men&ugrave; bandi di gara risulta visibile, false altrimenti.
	 * Serve per bloccare il pulsante che da un dettaglio esito affidamento va
	 * al bando di gara.
	 */
	private boolean menuBandiVisibile;

	/**
	 * @param esitiManager
	 *            the esitiManager to set
	 */
	public void setEsitiManager(IEsitiManager esitiManager) {
		this.esitiManager = esitiManager;
	}

	public void setBandiManager(IBandiManager bandiManager) {
		this.bandiManager = bandiManager;
	}

	/**
	 * @param pageManager
	 *            the pageManager to set
	 */
	public void setPageManager(IPageManager pageManager) {
		this.pageManager = pageManager;
	}

	/**
	 * @return the codice
	 */
	public String getCodice() {
		return codice;
	}

	/**
	 * @param codice
	 *            the codice to set
	 */
	public void setCodice(String codice) {
		this.codice = codice;
	}

	/**
	 * @return the dettaglioEsito
	 */
	public DettaglioEsitoType getDettaglioEsito() {
		return dettaglioEsito;
	}

	/**
	 * @param dettaglioEsito
	 *            the dettaglioEsito to set
	 */
	public void setDettaglioEsito(DettaglioEsitoType dettaglioEsito) {
		this.dettaglioEsito = dettaglioEsito;
	}

	/**
	 * @return the lotti
	 */
	public LottoEsitoType[] getLotti() {
		return lotti;
	}

	/**
	 * @param lotti
	 *            the lotti to set
	 */
	public void setLotti(LottoEsitoType[] lotti) {
		this.lotti = lotti;
	}

	/**
	 * @return the attiDocumenti
	 */
	public DocumentoAllegatoType[] getAttiDocumenti() {
		return attiDocumenti;
	}

	/**
	 * @param attiDocumenti the attiDocumenti to set
	 */
	public void setAttiDocumenti(DocumentoAllegatoType[] attiDocumenti) {
		this.attiDocumenti = attiDocumenti;
	}
	
	/**
	 * @return the menuBandiVisibile
	 */
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
		if ("block".equals(this.getTarget()))
			this.setTarget(SUCCESS);
		try {
			DettaglioEsitoType gara = this.esitiManager
					.getDettaglioEsito(this.codice);
			this.setDettaglioEsito(gara);

			IPage page = this.pageManager.getPage("ppgare_bandi_gara");
			this.menuBandiVisibile = page.isShowable();

		} catch (ApsException t) {
			ApsSystemUtils.logThrowable(t, this, "view");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}

	public String viewLotti() {
		try {
			DettaglioEsitoType gara = this.esitiManager
					.getDettaglioEsito(this.codice);
			this.setDettaglioEsito(gara);

			LottoEsitoType[] lottiEsito = this.esitiManager
					.getLottiEsito(this.codice);
			for(int i = 0; i < lottiEsito.length; i++) {
				for(int j = 0; j < gara.getLotto().length; j++) {
					if( gara.getLotto(j).getCodiceLotto().equals(lottiEsito[i].getCodiceLotto()) ) {
						lottiEsito[i].setSoggettiAderenti(gara.getLotto(j).getSoggettiAderenti());
						break;
					}
				}	
			}			
			this.setLotti(lottiEsito);
		} catch (ApsException t) {
			ApsSystemUtils.logThrowable(t, this, "viewLotti");
			ExceptionUtils.manageExceptionError(t, this);
			this.setTarget(CommonSystemConstants.PORTAL_ERROR);
		}
		return this.getTarget();
	}
	
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
