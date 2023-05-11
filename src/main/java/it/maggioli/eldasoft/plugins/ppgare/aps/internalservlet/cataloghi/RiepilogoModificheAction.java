package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.apsadmin.system.BaseAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.CarrelloProdottiSessione;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.cataloghi.beans.ProdottiCatalogoSessione;
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
 * Action per la gestione del download del riepilogo modifiche prodotti
 *
 * @author Marco.Perazzetta
 */
public class RiepilogoModificheAction extends BaseAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -5865984164066891586L;

	private Map<String, Object> session;
	
	private IEventManager eventManager;

	@Validate(EParamValidation.GENERIC)
	private String catalogo;

	private InputStream inputStream;
	@Validate(EParamValidation.FILE_NAME)
	private String filename;
	@Validate(EParamValidation.CONTENT_TYPE)
	private String contentType;

	private File riepilogo;
	@Validate(EParamValidation.CONTENT_TYPE)
	private String riepilogoContentType;
	@Validate(EParamValidation.FILE_NAME)
	private String riepilogoFileName;

	private boolean deleteRiepilogo;

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
	
	public void setEventManager(IEventManager eventManager) {
		this.eventManager = eventManager;
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

	public File getRiepilogo() {
		return riepilogo;
	}

	public void setRiepilogo(File riepilogo) {
		this.riepilogo = riepilogo;
	}

	public boolean isDeleteRiepilogo() {
		return deleteRiepilogo;
	}

	public void setDeleteRiepilogo(boolean deleteRiepilogo) {
		this.deleteRiepilogo = deleteRiepilogo;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getRiepilogoContentType() {
		return riepilogoContentType;
	}

	public void setRiepilogoContentType(String riepilogoContentType) {
		this.riepilogoContentType = riepilogoContentType;
	}

	public String getRiepilogoFileName() {
		return riepilogoFileName;
	}

	public void setRiepilogoFileName(String riepilogoFileName) {
		this.riepilogoFileName = riepilogoFileName;
	}

	public String getCatalogo() {
		return catalogo;
	}

	public void setCatalogo(String catalogo) {
		this.catalogo = catalogo;
	}

	/**
	 * Restituisce lo stream contenente il riepilogo firmato
	 *
	 * @return il target a cui andare
	 */
	public String downloadRiepilogoFirmato() {
		String target = SUCCESS;
		
		if (null == this.session.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI)) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = ERROR;
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente
			CarrelloProdottiSessione carrelloProdotti = (CarrelloProdottiSessione) this.session
				.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI);
			ProdottiCatalogoSessione prodottiDaInviare = carrelloProdotti.getListaProdottiPerCatalogo().get(carrelloProdotti.getCurrentCatalogo());
			this.setContentType(prodottiDaInviare.getRiepilogoContentType());
			this.setFilename(prodottiDaInviare.getRiepilogoFileName());
			try {
				this.setInputStream(new FileInputStream(prodottiDaInviare.getRiepilogo()));
			} catch (FileNotFoundException e) {
				ApsSystemUtils.logThrowable(e, this, "downloadRiepilogo");
				ExceptionUtils.manageExceptionError(e, this);
				target = ERROR;
			} catch (Throwable e) {
				ApsSystemUtils.logThrowable(e, this, "downloadRiepilogo");
				ExceptionUtils.manageExceptionError(e, this);
				target = ERROR;
			}
		}
		return target;
	}

	/**
	 * Gestisce la richiesta di elimazione del riepilogo firmato
	 *
	 * @return il target a cui andare
	 */
	public String confirmDeleteRiepilogo() {
		String target = SUCCESS;
		
		if (null == this.session.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI)) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = ERROR;
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente
			CarrelloProdottiSessione carrelloProdotti = (CarrelloProdottiSessione) this.session
				.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI);
			ProdottiCatalogoSessione prodottiDaInviare = carrelloProdotti.getListaProdottiPerCatalogo().get(carrelloProdotti.getCurrentCatalogo());
			this.deleteRiepilogo = true;
			this.riepilogo = prodottiDaInviare.getRiepilogo();
		}
		return target;
	}

	/**
	 * Elimina il riepilogo firmato
	 *
	 * @return il target a cui andare
	 */
	public String deleteRiepilogo() {
		Event evento = null;
		
		String target = SUCCESS;
		if (null == this.session.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI)) {
			// la sessione e' scaduta, occorre riconnettersi
			this.addActionError(this.getText("Errors.sessionExpired"));
			target = ERROR;
		} else {
			// la sessione non e' scaduta, per cui proseguo regolarmente
			CarrelloProdottiSessione carrelloProdotti = (CarrelloProdottiSessione) this.session
				.get(PortGareSystemConstants.SESSION_ID_CARRELLO_PRODOTTI);
			ProdottiCatalogoSessione prodottiDaInviare = carrelloProdotti.getListaProdottiPerCatalogo().get(carrelloProdotti.getCurrentCatalogo());
						
			// traccia l'evento di eliminazione di un file...	 
			evento = new Event();
			evento.setUsername(this.getCurrentUser().getUsername());
			evento.setDestination(carrelloProdotti.getCurrentCatalogo());
			evento.setLevel(Event.Level.INFO);
			evento.setEventType(PortGareEventsConstants.DELETE_FILE);
			evento.setIpAddress(this.getCurrentUser().getIpAddress());
			evento.setSessionId(this.getRequest().getSession().getId());
			evento.setMessage("Gestione prodotti: cancellazione riepilogo "
							   + ", file=" + prodottiDaInviare.getRiepilogoFileName()
							   + ", dimensione=" + prodottiDaInviare.getRiepilogoSize() + "KB");

			File file = prodottiDaInviare.getRiepilogo();
			if (file != null && file.exists()) {
				file.delete();
			}
			
			prodottiDaInviare.setRiepilogo(null);
			prodottiDaInviare.setRiepilogoContentType(null);
			prodottiDaInviare.setRiepilogoFileName(null);
			prodottiDaInviare.setRiepilogoSize(null);
		}
		
		if(evento != null) {
			this.eventManager.insertEvent(evento);
		}
		
		return target;
	}

	/**
	 * Gestisce la richiesta di annullamento elimazione del riepilogo firmato
	 *
	 * @return il target a cui andare
	 */
	public String cancelDeleteRiepilogo() {
		return SUCCESS;
	}
	
}
