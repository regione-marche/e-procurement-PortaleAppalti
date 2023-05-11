package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import it.eldasoft.www.sil.WSGareAppalto.DocumentoAllegatoType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CarrelloProdottiSessione;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.helpers.WizardDocumentiProdottoHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.helpers.WizardProdottoHelper;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.struts2.interceptor.SessionAware;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

/**
 * Action per la gestione del download di documenti relativi ad un prodotto di
 * un catalogo
 *
 * @author Marco.Perazzetta
 */
public class AllegatoProdottoAction extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -5865614164066891586L;

	private Map<String, Object> session;

	private IEventManager eventManager;
	
	private int id;
	private Long prodottoId;
	@Validate(EParamValidation.STATO_PRODOTTO)
	private String statoProdotto;
	@Validate(EParamValidation.CODICE)
	private String catalogo;

	private InputStream inputStream;
	@Validate(EParamValidation.FILE_NAME)
	private String filename;
	@Validate(EParamValidation.CONTENT_TYPE)
	private String contentType;

	private DocumentoAllegatoType immagine;
	private DocumentoAllegatoType[] certificazioniRichieste;
	private DocumentoAllegatoType[] schedeTecniche;
	private DocumentoAllegatoType[] facSimileCertificazioni;

	private boolean deleteImmagine;
	private boolean deleteCertificazioneRichiesta;
	private boolean deleteSchedeTecnica;
	private boolean deleteFacSimileCertificazioni;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public String getFilename() {
		return filename;
	}

	public String getContentType() {
		return contentType;
	}

	public DocumentoAllegatoType[] getCertificazioniRichieste() {
		return certificazioniRichieste;
	}

	public void setCertificazioniRichieste(DocumentoAllegatoType[] certificazioniRichieste) {
		this.certificazioniRichieste = certificazioniRichieste;
	}

	public DocumentoAllegatoType[] getSchedeTecniche() {
		return schedeTecniche;
	}

	public void setSchedeTecniche(DocumentoAllegatoType[] schedeTecniche) {
		this.schedeTecniche = schedeTecniche;
	}

	public DocumentoAllegatoType[] getFacSimileCertificazioni() {
		return facSimileCertificazioni;
	}

	public void setFacSimileCertificazioni(DocumentoAllegatoType[] facSimileCertificazioni) {
		this.facSimileCertificazioni = facSimileCertificazioni;
	}

	public boolean isDeleteImmagine() {
		return deleteImmagine;
	}

	public void setDeleteImmagine(boolean deleteImmagine) {
		this.deleteImmagine = deleteImmagine;
	}

	public boolean isDeleteCertificazioneRichiesta() {
		return deleteCertificazioneRichiesta;
	}

	public void setDeleteCertificazioneRichiesta(boolean deleteCertificazioneRichiesta) {
		this.deleteCertificazioneRichiesta = deleteCertificazioneRichiesta;
	}

	public boolean isDeleteSchedeTecnica() {
		return deleteSchedeTecnica;
	}

	public void setDeleteSchedeTecnica(boolean deleteSchedeTecnica) {
		this.deleteSchedeTecnica = deleteSchedeTecnica;
	}

	public boolean isDeleteFacSimileCertificazioni() {
		return deleteFacSimileCertificazioni;
	}

	public void setDeleteFacSimileCertificazioni(boolean deleteFacSimileCertificazioni) {
		this.deleteFacSimileCertificazioni = deleteFacSimileCertificazioni;
	}

	public DocumentoAllegatoType getImmagine() {
		return immagine;
	}

	public Long getProdottoId() {
		return prodottoId;
	}

	public void setProdottoId(Long prodottoId) {
		this.prodottoId = prodottoId;
	}

	public String getStatoProdotto() {
		return statoProdotto;
	}

	public void setStatoProdotto(String statoProdotto) {
		this.statoProdotto = statoProdotto;
	}

	public String getCatalogo() {
		return catalogo;
	}

	public void setCatalogo(String catalogo) {
		this.catalogo = catalogo;
	}

	/**
	 * Restituisce lo stream contenente l'immagine del prodotto
	 *
	 * @return il target a cui andare
	 */
	public String downloadAllegatoImmagine() {
		String target = SUCCESS;
		
		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			WizardDocumentiProdottoHelper documentiHelper = null;
			
			if (this.prodottoId != null && this.statoProdotto != null) {
				CarrelloProdottiSessione carrello = (CarrelloProdottiSessione) this.session.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI);
				documentiHelper = carrello.getDocumentiProdotto(this.statoProdotto, this.prodottoId.intValue());
			} else {
				WizardProdottoHelper prodottoHelper = (WizardProdottoHelper) this.session.get(PortGareSystemConstants.SESSION_ID_DETT_PRODOTTO);
				documentiHelper = prodottoHelper.getDocumenti();
			}
			this.contentType = documentiHelper.getImmagineContentType();
			this.filename = documentiHelper.getImmagineFileName();
			try {
				this.inputStream = new FileInputStream(documentiHelper.getImmagine());
			} catch (FileNotFoundException e) {
				ApsSystemUtils.logThrowable(e, this, "downloadAllegatoCertificazione");
				ExceptionUtils.manageExceptionError(e, this);
				target = ERROR;
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "downloadAllegatoCertificazione");
				ExceptionUtils.manageExceptionError(e, this);
				target = ERROR;
			}
		} else {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = ERROR;
		}
		return target;
	}

	/**
	 * Restituisce lo stream contenente una certificazione richiesta per il
	 * prodotto
	 *
	 * @return il target a cui andare
	 */
	public String downloadAllegatoCertificazione() {
		String target = SUCCESS;
		
		if (null != this.getCurrentUser() 
			&& !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			WizardDocumentiProdottoHelper documentiHelper = null;
			
			if (this.prodottoId != null && this.statoProdotto != null) {
				CarrelloProdottiSessione carrello = (CarrelloProdottiSessione) this.session
					.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI);
				documentiHelper = carrello.getDocumentiProdotto(this.statoProdotto, this.prodottoId.intValue());
			} else {
				WizardProdottoHelper prodottoHelper = (WizardProdottoHelper) this.session
					.get(PortGareSystemConstants.SESSION_ID_DETT_PRODOTTO);
				documentiHelper = prodottoHelper.getDocumenti();
			}
			this.contentType = documentiHelper.getCertificazioniRichiesteContentType().get(this.id);
			this.filename = documentiHelper.getCertificazioniRichiesteFileName().get(this.id);
			try {
				this.inputStream = new FileInputStream(documentiHelper.getCertificazioniRichieste().get(this.id));
			} catch (FileNotFoundException e) {
				ApsSystemUtils.logThrowable(e, this, "downloadAllegatoCertificazione");
				ExceptionUtils.manageExceptionError(e, this);
				target = ERROR;
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "downloadAllegatoCertificazione");
				ExceptionUtils.manageExceptionError(e, this);
				target = ERROR;
			}
		} else {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = ERROR;
		}
		return target;
	}

	/**
	 * Restituisce lo stream contenente una scheda tecnica inserita per il
	 * prodotto
	 *
	 * @return il target a cui andare
	 */
	public String downloadAllegatoSchedaTecnica() {
		String target = SUCCESS;
		
		if (null != this.getCurrentUser() 
		    && !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
			WizardDocumentiProdottoHelper documentiHelper = null;
			
			if (this.prodottoId != null && this.statoProdotto != null) {
				CarrelloProdottiSessione carrello = (CarrelloProdottiSessione) this.session
					.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI);
				documentiHelper = carrello.getDocumentiProdotto(this.statoProdotto, this.prodottoId.intValue());
			} else {
				WizardProdottoHelper prodottoHelper = (WizardProdottoHelper) this.session
					.get(PortGareSystemConstants.SESSION_ID_DETT_PRODOTTO);
				documentiHelper = prodottoHelper.getDocumenti();
			}
			this.contentType = documentiHelper.getSchedeTecnicheContentType().get(this.id);
			this.filename = documentiHelper.getSchedeTecnicheFileName().get(this.id);
			try {
				this.inputStream = new FileInputStream(documentiHelper.getSchedeTecniche().get(this.id));
			} catch (FileNotFoundException e) {
				ApsSystemUtils.logThrowable(e, this, "downloadAllegatoSchedaTecnica");
				ExceptionUtils.manageExceptionError(e, this);
				target = ERROR;
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "downloadAllegatoSchedaTecnica");
				ExceptionUtils.manageExceptionError(e, this);
				target = ERROR;
			}
		} else {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = ERROR;
		}
		return target;
	}

	/**
	 * Gestisce la richiesta di elimazione dell'immagine di un prodotto
	 *
	 * @return il target a cui andare
	 */
	public String confirmDeleteAllegatoImmagine() {
		String target = SUCCESS;
		
		WizardProdottoHelper prodottoHelper = (WizardProdottoHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_PRODOTTO);
		if (prodottoHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			this.deleteImmagine = true;
			this.immagine = prodottoHelper.getDettaglioProdotto().getImmagine();
		}
		return target;
	}

	/**
	 * Gestisce la richiesta di elimazione di una certificazione richiesta
	 *
	 * @return il target a cui andare
	 */
	public String confirmDeleteAllegatoCertificazione() {
		String target = SUCCESS;
		
		WizardProdottoHelper prodottoHelper = (WizardProdottoHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_PRODOTTO);
		if (prodottoHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			this.deleteCertificazioneRichiesta = true;
			this.certificazioniRichieste = prodottoHelper.getDettaglioProdotto().getCertificazioniRichieste();
		}
		return target;
	}

	/**
	 * Gestisce la richiesta di elimazione di una scheda tecnica
	 *
	 * @return il target a cui andare
	 */
	public String confirmDeleteAllegatoSchedaTecnica() {
		String target = SUCCESS;
		
		WizardProdottoHelper prodottoHelper = (WizardProdottoHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_PRODOTTO);
		if (prodottoHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			this.deleteSchedeTecnica = true;
			this.schedeTecniche = prodottoHelper.getDettaglioProdotto().getSchedeTecniche();
		}
		return target;
	}

	/**
	 * Elimina l'immagine del prodotto
	 *
	 * @return il target a cui andare
	 */
	public String deleteAllegatoImmagine() {
		String target = SUCCESS;
		
		WizardProdottoHelper prodottoHelper = (WizardProdottoHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_PRODOTTO);
		if (prodottoHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente
			WizardDocumentiProdottoHelper documentiHelper = prodottoHelper.getDocumenti();
			
			// traccia l'evento di eliminazione di un file...	
			Event evento = new Event();
			evento.setUsername(this.getCurrentUser().getUsername());
			evento.setDestination(prodottoHelper.getCodiceCatalogo());
			evento.setLevel(Event.Level.INFO);
			evento.setEventType(PortGareEventsConstants.DELETE_FILE);
			evento.setIpAddress(this.getCurrentUser().getIpAddress());
			evento.setSessionId(this.getRequest().getSession().getId());
			evento.setMessage(
					"Gestione prodotti: cancellazione immagine prodotto " + prodottoHelper.getDettaglioProdotto().getCodiceProdottoFornitore()
					+ ", file=" + documentiHelper.getImmagineFileName()
					+ ", dimensione=" + documentiHelper.getImmagineSize() + "KB");
			
			try {
				File file = documentiHelper.getImmagine();
				if (file != null && file.exists()) {
					file.delete();
				}
				documentiHelper.setImmagine(null);
				documentiHelper.setImmagineContentType(null);
				documentiHelper.setImmagineFileName(null);
				documentiHelper.setImmagineSize(null);
				prodottoHelper.setAggiornatoDocumenti(true);
			} catch (Throwable ex) {
				//sto ricaricando la pagina con f5, non dovrebbe succedere
			} finally {
				if(evento != null) {
					this.eventManager.insertEvent(evento);
				}
			}						
		}
		return target;
	}

	/**
	 * Elimina una certificazione richiesta
	 *
	 * @return il target a cui andare
	 */
	public String deleteAllegatoCertificazione() {
		String target = SUCCESS;
		
		WizardProdottoHelper prodottoHelper = (WizardProdottoHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_PRODOTTO);
		if (prodottoHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente
			WizardDocumentiProdottoHelper documentiHelper = prodottoHelper.getDocumenti();
			
			// traccia l'evento di eliminazione di un file...	
			Event evento = new Event();
			evento.setUsername(this.getCurrentUser().getUsername());
			evento.setDestination(prodottoHelper.getCodiceCatalogo());
			evento.setLevel(Event.Level.INFO);
			evento.setEventType(PortGareEventsConstants.DELETE_FILE);
			evento.setIpAddress(this.getCurrentUser().getIpAddress());
			evento.setSessionId(this.getRequest().getSession().getId());
			evento.setMessage(
					"Gestione prodotti: cancellazione certificazione prodotto " + prodottoHelper.getDettaglioProdotto().getCodiceProdottoFornitore()
					+ ", file=" + documentiHelper.getCertificazioniRichiesteFileName()
					+ ", dimensione=" + documentiHelper.getCertificazioniRichiesteSize() + "KB");

			try {
				File file = documentiHelper.getCertificazioniRichieste().remove(this.id);
				if (file.exists()) {
					file.delete();
				}
				documentiHelper.getCertificazioniRichiesteContentType().remove(this.id);
				documentiHelper.getCertificazioniRichiesteFileName().remove(this.id);
				documentiHelper.getCertificazioniRichiesteSize().remove(this.id);
				prodottoHelper.setAggiornatoDocumenti(true);
			} catch (Throwable ex) {
				//sto ricaricando la pagina con f5, non dovrebbe succedere
			} finally {
				if(evento != null) {
					this.eventManager.insertEvent(evento);
				}
			}									
		}
		return target;
	}

	/**
	 * Elimina una scheda tecnica
	 *
	 * @return il target a cui andare
	 */
	public String deleteAllegatoSchedaTecnica() {
		String target = SUCCESS;
		
		WizardProdottoHelper prodottoHelper = (WizardProdottoHelper) this.session
			.get(PortGareSystemConstants.SESSION_ID_DETT_PRODOTTO);
		if (prodottoHelper == null) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = CommonSystemConstants.PORTAL_ERROR;
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente
			WizardDocumentiProdottoHelper documentiHelper = prodottoHelper.getDocumenti();
			
			// traccia l'evento di eliminazione di un file...	
			Event evento = new Event();
			evento.setUsername(this.getCurrentUser().getUsername());
			evento.setDestination(prodottoHelper.getCodiceCatalogo());
			evento.setLevel(Event.Level.INFO);
			evento.setEventType(PortGareEventsConstants.DELETE_FILE);
			evento.setIpAddress(this.getCurrentUser().getIpAddress());
			evento.setSessionId(this.getRequest().getSession().getId());
			evento.setMessage(
					"Gestione prodotti: cancellazione scheda tecnica prodotto " + prodottoHelper.getDettaglioProdotto().getCodiceProdottoFornitore()
					+ ", file=" + documentiHelper.getSchedeTecnicheFileName()
					+ ", dimensione=" + documentiHelper.getSchedeTecnicheSize() + "KB");

			try {
				File file = documentiHelper.getSchedeTecniche().remove(this.id);
				if (file.exists()) {
					file.delete();
				}
				documentiHelper.getSchedeTecnicheContentType().remove(this.id);
				documentiHelper.getSchedeTecnicheFileName().remove(this.id);
				documentiHelper.getSchedeTecnicheSize().remove(this.id);
				prodottoHelper.setAggiornatoDocumenti(true);
			} catch (Throwable ex) {
				//sto ricaricando la pagina con f5, non dovrebbe succedere
			} finally {
				if(evento != null) {
					this.eventManager.insertEvent(evento);
				}
			}						
		}
		return target;
	}

	/**
	 * Gestisce la richiesta di annullamento elimazione dell'immagine di un
	 * prodotto
	 *
	 * @return il target a cui andare
	 */
	public String cancelDeleteAllegatoImmagine() {
		return SUCCESS;
	}

	/**
	 * Gestisce la richiesta di annullamento elimazione di una certificazione
	 * richiesta del prodotto
	 *
	 * @return il target a cui andare
	 */
	public String cancelDeleteAllegatoCertificazione() {
		return SUCCESS;
	}

	/**
	 * Gestisce la richiesta di annullamento elimazione di una scheda tecnica del
	 * prodotto
	 *
	 * @return il target a cui andare
	 */
	public String cancelDeleteAllegatoSchedaTecnica() {
		return SUCCESS;
	}
	
}
