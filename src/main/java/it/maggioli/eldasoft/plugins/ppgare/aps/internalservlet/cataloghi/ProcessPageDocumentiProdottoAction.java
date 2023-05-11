package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.AbstractProcessPageAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.DocumentiAllegatiFirmaBean;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CarrelloProdottiSessione;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.helpers.WizardDocumentiProdottoHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.helpers.WizardProdottoHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.slf4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.time.Instant;
import java.util.Date;

/**
 * Action di gestione delle operazioni nella pagina dei documenti del wizard
 * d'iscrizione all'albo
 *
 * @author Stefano.Sabbadin
 */
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

	/**
	 * @param customConfigManager the customConfigManager to set
	 */
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

	/**
	 * @return the certRicFileName
	 */
	public String getCertRicFileName() {
		return certRicFileName;
	}

	/**
	 * @param certRicFileName the certRicFileName to set
	 */
	public void setCertRicFileName(String certRicFileName) {
		this.certRicFileName = certRicFileName;
	}

	/**
	 * @return the schedTec
	 */
	public File getSchedTec() {
		return schedTec;
	}

	/**
	 * @param schedTec the schedTec to set
	 */
	public void setSchedTec(File schedTec) {
		this.schedTec = schedTec;
	}

	/**
	 * @return the schedTecContentType
	 */
	public String getSchedTecContentType() {
		return schedTecContentType;
	}

	/**
	 * @param schedTecContentType the schedTecContentType to set
	 */
	public void setSchedTecContentType(String schedTecContentType) {
		this.schedTecContentType = schedTecContentType;
	}

	/**
	 * @return the schedTecFileName
	 */
	public String getSchedTecFileName() {
		return schedTecFileName;
	}

	/**
	 * @param schedTecFileName the schedTecFileName to set
	 */
	public void setSchedTecFileName(String schedTecFileName) {
		this.schedTecFileName = schedTecFileName;
	}

	/**
	 * @return the immagine
	 */
	public File getImmagine() {
		return immagine;
	}

	/**
	 * @param immagine the immagine to set
	 */
	public void setImmagine(File immagine) {
		this.immagine = immagine;
	}

	/**
	 * @return the immagineContentType
	 */
	public String getImmagineContentType() {
		return immagineContentType;
	}

	/**
	 * @param immagineContentType the immagineContentType to set
	 */
	public void setmmagineContentType(String immagineContentType) {
		this.immagineContentType = immagineContentType;
	}

	/**
	 * @return the immagineFileName
	 */
	public String getImmagineFileName() {
		return immagineFileName;
	}

	/**
	 * @param immagineFileName the immagineFileName to set
	 */
	public void setImmagineFileName(String immagineFileName) {
		this.immagineFileName = immagineFileName;
	}

	/**
	 * @return the inputStream
	 */
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
			
			// traccia l'evento di upload di un file...
			Event evento = new Event();
			evento.setUsername(this.getCurrentUser().getUsername());
			evento.setDestination(prodottoHelper.getCodiceCatalogo());
			evento.setLevel(Event.Level.INFO);
			evento.setEventType(PortGareEventsConstants.UPLOAD_FILE);
			evento.setIpAddress(this.getCurrentUser().getIpAddress());
			evento.setSessionId(this.getRequest().getSession().getId());
			evento.setMessage("Gestione prodotti: immagine prodotto "
					+ prodottoHelper.getDettaglioProdotto().getCodiceProdottoFornitore() 
					+ ", file=" + this.immagineFileName 
					+ ", dimensione=" + dimensioneImmagine + "KB");

//			controlliOk = controlliOk && this.checkFileSize(this.immagine, getActualTotalSize(prodottoHelper.getDocumenti()), this.appParamManager);
//			controlliOk = controlliOk && this.checkFileName(this.immagineFileName);
//			controlliOk = controlliOk && this.checkFileExtension(this.immagineFileName, this.appParamManager, AppParamManager.ESTENSIONI_AMMESSE_IMMAGINE);			

			boolean controlliOk =
					checkFileSize(immagine, immagineFileName, getActualTotalSize(prodottoHelper.getDocumenti()), appParamManager, evento)
					&& checkFileName(immagineFileName, evento)
					&& checkFileExtension(immagineFileName, appParamManager, AppParamManager.ESTENSIONI_AMMESSE_IMMAGINE, evento)
					&& checkFileFormat(immagine, immagineFileName, null, evento, false);

			if (controlliOk) {

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
			this.eventManager.insertEvent(evento);
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
			
			// traccia l'evento di upload di un file...
			Event evento = new Event();
			evento.setUsername(this.getCurrentUser().getUsername());
			evento.setDestination(prodottoHelper.getCodiceCatalogo());
			evento.setLevel(Event.Level.INFO);
			evento.setEventType(PortGareEventsConstants.UPLOAD_FILE);
			evento.setIpAddress(this.getCurrentUser().getIpAddress());
			evento.setSessionId(this.getRequest().getSession().getId());
			evento.setMessage("Gestione prodotti: certificazione prodotto "
					+ prodottoHelper.getDettaglioProdotto().getCodiceProdottoFornitore() 
					+ ", file=" + this.certRicFileName 
					+ ", dimensione=" + dimensioneCertificazione + "KB");

			boolean controlliOk =
					checkFileSize(certRic, certRicFileName, getActualTotalSize(prodottoHelper.getDocumenti()), appParamManager, evento)
					&& checkFileName(certRicFileName, evento)
					&& checkFileExtension(certRicFileName, appParamManager, AppParamManager.ESTENSIONI_AMMESSE_DOC, evento)
					&& checkFileFormat(certRic, certRicFileName, null, evento, false);

			if (controlliOk) {
				try {
					Date checkDate = Date.from(Instant.now());
					DocumentiAllegatiFirmaBean checkFirma = this.checkFileSignature(this.certRic, this.certRicFileName, null,checkDate, evento, false, this.appParamManager, this.customConfigManager);
				} catch (ApsSystemException e) {
					ApsSystemUtils.getLogger().error("Errore nella verifica della firma.",e);
					this.addActionError(this.getText("Errors.cannotVerifySign"));
				}
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
			this.eventManager.insertEvent(evento);
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
			
			// traccia l'evento di upload di un file...
			Event evento = new Event();
			evento.setUsername(this.getCurrentUser().getUsername());
			evento.setDestination(prodottoHelper.getCodiceCatalogo());
			evento.setLevel(Event.Level.INFO);
			evento.setEventType(PortGareEventsConstants.UPLOAD_FILE);
			evento.setIpAddress(this.getCurrentUser().getIpAddress());
			evento.setSessionId(this.getRequest().getSession().getId());
			evento.setMessage("Gestione prodotti: scheda tecnica prodotto "
					+ prodottoHelper.getDettaglioProdotto().getCodiceProdottoFornitore() 
					+ ", file=" + this.schedTecFileName 
					+ ", dimensione=" + dimensioneSchedaTecnica + "KB");

			boolean controlliOk =
					checkFileSize(schedTec, schedTecFileName, getActualTotalSize(prodottoHelper.getDocumenti()), appParamManager, evento)
					&& checkFileName(schedTecFileName, evento)
					&& checkFileExtension(schedTecFileName, appParamManager, AppParamManager.ESTENSIONI_AMMESSE_DOC, evento)
					&& checkFileFormat(schedTec, schedTecFileName, null, evento, false);

			DocumentiAllegatiFirmaBean checkFirma = null;
			try {
				Date checkDate = Date.from(Instant.now());
				checkFirma = checkFileSignature(
						schedTec
						, schedTecFileName
						, null
						, checkDate
						, evento
						, Boolean.FALSE
						, appParamManager
						, customConfigManager
				);
			} catch (ApsSystemException e) {
				logger.error("Errore nella verifica della firma digitale.",e);
				this.addActionError(this.getText("Errors.cannotVerifySign"));
			}
						
			if (controlliOk) {
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
			this.eventManager.insertEvent(evento);
		}
		return target;
	}

	/**
	 * Ritorna la dimensione in kilobyte di tutti i file finora uploadati.
	 *
	 * @param helper helper dei documenti
	 *
	 * @return totale in KB dei file caricati
	 */
	private int getActualTotalSize(WizardDocumentiProdottoHelper helper) {
		int total = 0;
		if (helper.getImmagine() != null) {
			total += helper.getImmagineSize();
		}
		for (Integer s : helper.getCertificazioniRichiesteSize()) {
			total += s;
		}
		for (Integer s : helper.getSchedeTecnicheSize()) {
			total += s;
		}
		return total;
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
