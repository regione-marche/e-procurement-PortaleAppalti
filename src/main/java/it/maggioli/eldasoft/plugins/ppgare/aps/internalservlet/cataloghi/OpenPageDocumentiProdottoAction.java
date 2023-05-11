package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi;

import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.helpers.WizardDocumentiProdottoHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.helpers.WizardProdottoHelper;
import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.apsadmin.system.BaseAction;
import it.eldasoft.www.sil.WSGareAppalto.DocumentoAllegatoType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.IDownloadAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import java.util.Map;
import org.apache.struts2.interceptor.SessionAware;

/**
 * Action di gestione dell'apertura della pagina dei documenti richiesti del
 * wizard di inserimento prodotto
 *
 * @author Marco.Perazzetta
 */
public class OpenPageDocumentiProdottoAction extends BaseAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -1585508583311924835L;

	private IAppParamManager appParamManager;

	private Map<String, Object> session;

	private int dimensioneAttualeFileCaricati;

	private DocumentoAllegatoType[] certificazioniRichieste;
	private DocumentoAllegatoType[] schedeTecnice;
	private DocumentoAllegatoType[] facSimileCertificazioni;

	private Long certificazioneRichiestaId;
	private Long schedaTecnicaId;
	private Long immagineId;

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public int getDimensioneAttualeFileCaricati() {
		return dimensioneAttualeFileCaricati;
	}

	public DocumentoAllegatoType[] getCertificazioniRichieste() {
		return certificazioniRichieste;
	}

	public void setCertificazioniRichieste(DocumentoAllegatoType[] certificazioniRichieste) {
		this.certificazioniRichieste = certificazioniRichieste;
	}

	public Long getCertificazioneRichiestaId() {
		return certificazioneRichiestaId;
	}

	public void setCertificazioneRichiestaId(Long certificazioneRichiestaId) {
		this.certificazioneRichiestaId = certificazioneRichiestaId;
	}

	public DocumentoAllegatoType[] getSchedeTecnice() {
		return schedeTecnice;
	}

	public void setSchedeTecnice(DocumentoAllegatoType[] schedeTecnice) {
		this.schedeTecnice = schedeTecnice;
	}

	public Long getSchedaTecnicaId() {
		return schedaTecnicaId;
	}

	public void setSchedaTecnicaId(Long schedaTecnicaId) {
		this.schedaTecnicaId = schedaTecnicaId;
	}

	public Long getImmagineId() {
		return immagineId;
	}

	public void setImmagineId(Long immagineId) {
		this.immagineId = immagineId;
	}

	public DocumentoAllegatoType[] getFacSimileCertificazioni() {
		return facSimileCertificazioni;
	}

	public void setFacSimileCertificazioni(DocumentoAllegatoType[] facSimileCertificazioni) {
		this.facSimileCertificazioni = facSimileCertificazioni;
	}

	public Integer getLimiteUploadFile() {
		return FileUploadUtilities.getLimiteUploadFile(this.appParamManager);
	}

	public Integer getLimiteTotaleUploadDocProdotto() {
		return FileUploadUtilities.getLimiteTotaleUploadFile(appParamManager);
	}

	/**
	 * ... 
	 */
	public String openPage() {
		return openPageCommon();
	}

	/**
	 * ... 
	 */
	public String openPageClear() {
		String target = openPageCommon();
		if (SUCCESS.equals(target)) {
			this.certificazioneRichiestaId = null;
			this.schedaTecnicaId = null;
			this.immagineId = null;
		}
		return target;
	}

	/**
	 * ... 
	 */
	private String openPageCommon() {

		String errore = (String) session.remove(IDownloadAction.ERROR_DOWNLOAD);
		if (errore != null) {
			this.addActionError(errore);
		}

		String target = SUCCESS;
		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			try {
				WizardProdottoHelper prodottoHelper = (WizardProdottoHelper) this.session
					.get(PortGareSystemConstants.SESSION_ID_DETT_PRODOTTO);
				
				if (prodottoHelper == null) {
					// la sessione e' scaduta, occorre riconnettersi
					this.addActionError(this.getText("Errors.sessionExpired"));
					target = CommonSystemConstants.PORTAL_ERROR;
				} else {
					// la sessione non e' scaduta, per cui proseguo regolarmente
					this.session.put(PortGareSystemConstants.SESSION_ID_PAGINA, PortGareSystemConstants.WIZARD_ISCRALBO_PAGINA_DOCUMENTI);
					WizardDocumentiProdottoHelper documentiHelper = prodottoHelper.getDocumenti();
					if (documentiHelper != null) {
						if (documentiHelper.getImmagine() != null) {
							this.dimensioneAttualeFileCaricati += documentiHelper.getImmagineSize();
						}
						// si aggiorna il totale dei file finora caricati
						for (Integer s : documentiHelper.getCertificazioniRichiesteSize()) {
							this.dimensioneAttualeFileCaricati += s;
						}
						// si aggiorna il totale dei file finora caricati
						for (Integer s : documentiHelper.getSchedeTecnicheSize()) {
							this.dimensioneAttualeFileCaricati += s;
						}
					}
				}
			} catch (Throwable ex) {
				ApsSystemUtils.logThrowable(ex, this, "next");
				ExceptionUtils.manageExceptionError(ex, this);
				target = CommonSystemConstants.PORTAL_ERROR;
			}
		} else {
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		}
		return target;
	}
	
}
