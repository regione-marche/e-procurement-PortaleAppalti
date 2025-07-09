package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi;

import com.agiletec.aps.system.ApsSystemUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CarrelloProdottiSessione;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.helpers.WizardDocumentiProdottoHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.helpers.WizardProdottoHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.slf4j.Logger;

import java.io.File;
import java.io.InputStream;

/**
 * Action di gestione delle operazioni nella pagina dei documenti del wizard
 * d'iscrizione all'albo
 *
 * @author Stefano.Sabbadin
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.PRODOTTI })
public class ProcessPageDocumentiProdottoAction extends AbstractProcessPageAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 647490520354783711L;
	private static final Logger logger = ApsSystemUtils.getLogger();

	private IAppParamManager appParamManager;
	private IEventManager eventManager;
	private ICustomConfigManager customConfigManager;

	private File certRic;
	@Validate(EParamValidation.CONTENT_TYPE)
	private String certRicContentType;
	@Validate(EParamValidation.FILE_NAME)
	private String certRicFileName;

	private File schedTec;
	@Validate(EParamValidation.CONTENT_TYPE)
	private String schedTecContentType;
	@Validate(EParamValidation.FILE_NAME)
	private String schedTecFileName;

	private File immagine;
	@Validate(EParamValidation.CONTENT_TYPE)
	private String immagineContentType;
	@Validate(EParamValidation.FILE_NAME)
	private String immagineFileName;

	private InputStream inputStream;

	
	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}

	public void setCustomConfigManager(ICustomConfigManager customConfigManager) {
		this.customConfigManager = customConfigManager;
	}

	public File getCertRic() {
		return certRic;
	}

	public void setCertRic(File certRic) {
		this.certRic = certRic;
	}

	public String getCertRicContentType() {
		return certRicContentType;
	}

	public void setCertRicContentType(String certRicContentType) {
		this.certRicContentType = certRicContentType;
	}

	public String getCertRicFileName() {
		return certRicFileName;
	}

	public void setCertRicFileName(String certRicFileName) {
		this.certRicFileName = certRicFileName;
	}

	public File getSchedTec() {
		return schedTec;
	}

	public void setSchedTec(File schedTec) {
		this.schedTec = schedTec;
	}

	public String getSchedTecContentType() {
		return schedTecContentType;
	}

	public void setSchedTecContentType(String schedTecContentType) {
		this.schedTecContentType = schedTecContentType;
	}

	public String getSchedTecFileName() {
		return schedTecFileName;
	}

	public void setSchedTecFileName(String schedTecFileName) {
		this.schedTecFileName = schedTecFileName;
	}

	public File getImmagine() {
		return immagine;
	}

	public void setImmagine(File immagine) {
		this.immagine = immagine;
	}

	public String getImmagineContentType() {
		return immagineContentType;
	}

	public void setmmagineContentType(String immagineContentType) {
		this.immagineContentType = immagineContentType;
	}

	public String getImmagineFileName() {
		return immagineFileName;
	}

	public void setImmagineFileName(String immagineFileName) {
		this.immagineFileName = immagineFileName;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	/**
	 * Gestisco la richiesta di allegare un immagine al prodotto
	 *
	 * @return il target a cui andare
	 */
	public String addImmagine() {
		String target = "backToDocumenti";
		
		WizardProdottoHelper prodottoHelper = (WizardProdottoHelper) this.session.get(PortGareSystemConstants.SESSION_ID_DETT_PRODOTTO);
		if (prodottoHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
			// la sessione non e' scaduta, per cui proseguo regolarmente
		} else {
			int dimensioneImmagine = FileUploadUtilities.getFileSize(this.immagine);
			
			// valida l'upload del documento...
			getUploadValidator()
					.setHelper(prodottoHelper)
					.setDocumento(immagine)
					.setDocumentoFileName(immagineFileName)
					.setDocumentoFormato(null)
					.setOnlyP7m(false)
					.setEstensioniAmmesse( (String)appParamManager.getConfigurationValue(AppParamManager.ESTENSIONI_AMMESSE_IMMAGINE) )
					.setEventoDestinazione(prodottoHelper.getCodiceCatalogo())
					.setEventoMessaggio("Gestione prodotti: immagine prodotto "
										+ prodottoHelper.getDettaglioProdotto().getCodiceProdottoFornitore() 
										+ ", file=" + this.immagineFileName 
										+ ", dimensione=" + dimensioneImmagine + "KB");
			
			if ( getUploadValidator().validate() ) {
				// si inseriscono i documenti in sessione
				WizardDocumentiProdottoHelper documentiHelper = prodottoHelper.getDocumenti();

				if (!this.immagineFileName.equals(documentiHelper.getImmagineFileName())) {
					File f = new File(this.immagine.getParent() + File.separatorChar + FileUploadUtilities.generateFileName());
					this.immagine.renameTo(f);
					documentiHelper.setImmagineSize(dimensioneImmagine);
					documentiHelper.setImmagine(f);
					documentiHelper.setImmagineContentType(this.immagineContentType);
					documentiHelper.setImmagineFileName(this.immagineFileName);
					prodottoHelper.setAggiornatoDocumenti(true);
					CarrelloProdottiSessione carrelloProdotti = (CarrelloProdottiSessione) session.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI);
					carrelloProdotti.addFileReference(f);
				}
			} else {
				target = INPUT;
			}
			
			this.eventManager.insertEvent(getUploadValidator().getEvento());
		}
		return target;
	}

	/**
	 * Gestisco la richiesta di allegare una nuova certificazione richiesta al
	 * prodotto
	 *
	 * @return il target a cui andare
	 */
	public String addCertificazioneRichiesta() {
		String target = "backToDocumenti";
		
		WizardProdottoHelper prodottoHelper = (WizardProdottoHelper) this.session.get(PortGareSystemConstants.SESSION_ID_DETT_PRODOTTO);

		if (prodottoHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
			// la sessione non e' scaduta, per cui proseguo regolarmente
		} else {
			int dimensioneCertificazione = FileUploadUtilities.getFileSize(this.certRic);
			
			// valida l'upload del documento...
			getUploadValidator()
					.setHelper(prodottoHelper)
					//.setActualTotalSize(prodottoHelper.getDocumenti().getActualTotalSize())
					.setDocumento(certRic)
					.setDocumentoFileName(certRicFileName)
					.setOnlyP7m(false)
					.setCheckFileSignature(true)
					.setEventoDestinazione(prodottoHelper.getCodiceCatalogo())
					.setEventoMessaggio("Gestione prodotti: certificazione prodotto "
										+ prodottoHelper.getDettaglioProdotto().getCodiceProdottoFornitore() 
										+ ", file=" + this.certRicFileName 
										+ ", dimensione=" + dimensioneCertificazione + "KB");
			
			if ( getUploadValidator().validate() ) {
				// si inseriscono i documenti in sessione
				WizardDocumentiProdottoHelper documentiHelper = prodottoHelper.getDocumenti();

				if (documentiHelper.getCertificazioniRichiesteFileName().contains(this.certRicFileName)) {
					this.addActionError(this.getText("Errors.certificazioneRichiestaPresent"));
				} else {
					File f = new File(this.certRic.getParent() + File.separatorChar + FileUploadUtilities.generateFileName());
					this.certRic.renameTo(f);
					documentiHelper.getCertificazioniRichiesteSize().add(dimensioneCertificazione);
					documentiHelper.getCertificazioniRichieste().add(f);
					documentiHelper.getCertificazioniRichiesteContentType().add(this.certRicContentType);
					documentiHelper.getCertificazioniRichiesteFileName().add(this.certRicFileName);
					prodottoHelper.setAggiornatoDocumenti(true);
					CarrelloProdottiSessione carrelloProdotti = (CarrelloProdottiSessione) session.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI);
					carrelloProdotti.addFileReference(f);
				}
			} else {
				target = INPUT;
			}			
			
			this.eventManager.insertEvent(getUploadValidator().getEvento());
		}
		return target;
	}

	/**
	 * Gestisco la richiesta di allegare una nuova scheda tecnica al prodotto
	 *
	 * @return il target a cui andare
	 */
	public String addSchedaTecnica() {
		String target = "backToDocumenti";
		
		WizardProdottoHelper prodottoHelper = (WizardProdottoHelper) this.session.get(PortGareSystemConstants.SESSION_ID_DETT_PRODOTTO);

		if (prodottoHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
			// la sessione non e' scaduta, per cui proseguo regolarmente
		} else {
			int dimensioneSchedaTecnica = FileUploadUtilities.getFileSize(this.schedTec);
			
			// valida l'upload del documento...
			getUploadValidator()
					.setHelper(prodottoHelper)
					//.setActualTotalSize(prodottoHelper.getDocumenti().getActualTotalSize())
					.setDocumento(schedTec)
					.setDocumentoFileName(schedTecFileName)
					.setOnlyP7m(false)
					.setCheckFileSignature(true)
					.setEventoDestinazione(prodottoHelper.getCodiceCatalogo())
					.setEventoMessaggio("Gestione prodotti: scheda tecnica prodotto "
										+ prodottoHelper.getDettaglioProdotto().getCodiceProdottoFornitore() 
										+ ", file=" + this.schedTecFileName 
										+ ", dimensione=" + dimensioneSchedaTecnica + "KB");

			if ( getUploadValidator().validate() ) {
				// si inseriscono i documenti in sessione
				WizardDocumentiProdottoHelper documentiHelper = prodottoHelper.getDocumenti();

				if (documentiHelper.getSchedeTecnicheFileName().contains(this.schedTecFileName)) {
					this.addActionError(this.getText("Errors.schedaTecnicaPresent"));
				} else {
					File f = new File(this.schedTec.getParent() + File.separatorChar + FileUploadUtilities.generateFileName());
					this.schedTec.renameTo(f);
					documentiHelper.getSchedeTecnicheSize().add(dimensioneSchedaTecnica);
					documentiHelper.getSchedeTecniche().add(f);
					documentiHelper.getSchedeTecnicheContentType().add(this.schedTecContentType);
					documentiHelper.getSchedeTecnicheFileName().add(this.schedTecFileName);
					prodottoHelper.setAggiornatoDocumenti(true);
					CarrelloProdottiSessione carrelloProdotti = (CarrelloProdottiSessione) session.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI);
					carrelloProdotti.addFileReference(f);
				}
			} else {
				target = INPUT;
			}
			
			this.eventManager.insertEvent(getUploadValidator().getEvento());
		}
		return target;
	}

	@Override
	public String next() {
		return SUCCESS;
	}

	@Override
	public String back() {
		String target = "back";
		return target;
	}

}
