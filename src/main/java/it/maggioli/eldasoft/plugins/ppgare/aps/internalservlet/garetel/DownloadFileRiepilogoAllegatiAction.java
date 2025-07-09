package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.url.IURLManager;
import com.agiletec.aps.system.services.url.PageURL;
import com.agiletec.apsadmin.system.BaseAction;
import it.eldasoft.www.WSOperazioniGenerali.AllegatoComunicazioneType;
import it.eldasoft.www.WSOperazioniGenerali.ComunicazioneType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.opgen.IComunicazioniManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.ValidationNotRequired;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.struts2.interceptor.ServletResponseAware;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Action per la gestione del download dei documenti di riepilogo gare elenchi e mercati elettronici
 * 
 * @version 1.0
 * @author Michele.DiNapoli
 */
@FlussiAccessiDistinti({ EFlussiAccessiDistinti.OFFERTA_GARA })
public class DownloadFileRiepilogoAllegatiAction extends BaseAction 
	implements  ServletResponseAware
{
	/**
	 * UID
	 */
	private static final long serialVersionUID = -2479103800236560071L;

	private HttpServletResponse response;
	private IComunicazioniManager comunicazioniManager;
	private IPageManager pageManager;
	private IURLManager urlManager;
	
	private InputStream inputStream;
	@Validate(EParamValidation.FILE_NAME)
	private String filename;
	@Validate(EParamValidation.CONTENT_TYPE)
	private String contentType;
	@ValidationNotRequired
	private String urlRedirect;
	private Long id;			// id della comunicazione della busta di riepilogo
	private long idCom;			// id della comunicazione relativa alla gestione per gli allegati della busta
	@Validate(EParamValidation.UUID)
	private String uuid;		// UUID dell'allegato presente nella comunicazione relativa alla busta	

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public void setPageManager(IPageManager pageManager) {
		this.pageManager = pageManager;
	}

	public IPageManager getPageManager() {
		return this.pageManager;
	}

	public IComunicazioniManager getComunicazioniManager() {
		return this.comunicazioniManager;
	}

	public void setComunicazioniManager(IComunicazioniManager comunicazioniManager) {
		this.comunicazioniManager = comunicazioniManager;
	}

	public IURLManager getUrlManager() {
		return urlManager;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public void setUrlManager(IURLManager urlManager) {
		this.urlManager = urlManager;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
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

	public String getUrlRedirect() {
		return urlRedirect;
	}

	public long getIdCom() {
		return idCom;
	}

	public void setIdCom(long idCom) {
		this.idCom = idCom;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * Restituisce lo stream contenente un documento allegato di riepilogo
	 * dei file allegati
	 */
	public String downloadFileRiepilogoAllegati() {
		String target = SUCCESS;
		try {
			ComunicazioneType comunicazioneOffertaCompleta = this.getComunicazioniManager().getComunicazione(
					CommonSystemConstants.ID_APPLICATIVO,
					this.getId());

			boolean marcatura = false; 

			// trova l'allegato del riepilogo con la marcatura temporale
			for(AllegatoComunicazioneType allegato : comunicazioneOffertaCompleta.getAllegato()) {
				String nomeallegato = allegato.getNomeFile();

				if(nomeallegato.equalsIgnoreCase(PortGareSystemConstants.FILENAME_RIEPILOGO_BUSTE) || 
				   nomeallegato.equalsIgnoreCase(PortGareSystemConstants.FILENAME_RIEPILOGO_BUSTE_MARCATURA_TEMPORALE) ||
				   nomeallegato.equalsIgnoreCase(PortGareSystemConstants.FILENAME_RIEPILOGO) ||
				   nomeallegato.equalsIgnoreCase(PortGareSystemConstants.FILENAME_RIEPILOGO_MARCATURA_TEMPORALE))
				{
					marcatura = (nomeallegato.equalsIgnoreCase(PortGareSystemConstants.FILENAME_RIEPILOGO_BUSTE_MARCATURA_TEMPORALE) || 
								 nomeallegato.equalsIgnoreCase(PortGareSystemConstants.FILENAME_RIEPILOGO_MARCATURA_TEMPORALE));
					if(marcatura) {
						// apri la pagina dei dettagli per le marcature temporali e le firme...
						this.idCom = this.id;
						this.uuid = allegato.getNomeFile();
						target = "successFirmato";
					} else {
						// esegui il download dell'allegato...
						if(marcatura) {
							this.contentType = "application/pdf"; 
						} else { 
							this.contentType = "application/timestamp-reply";
						}
						this.filename = nomeallegato;
						this.inputStream = new ByteArrayInputStream(allegato.getFile());
					}
				}
			}
		} catch (Exception e) {
			String codiceMessaggio = "Errors.fileDownload.generic";
			this.addActionError(this.getText(codiceMessaggio, new String[0]));
			ApsSystemUtils.getLogger().error(this.getTextFromDefaultLocale(codiceMessaggio));
			this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
			ApsSystemUtils.logThrowable(e, this, "downloadAllegatoRichiesto");
			ExceptionUtils.manageExceptionError(e, this);
			target = INPUT;
			this.urlRedirect = getPageURL("portalerror");
		} catch (Throwable e) {
			String codiceMessaggio = "Errors.fileDownload.generic";
			this.addActionError(this.getText(codiceMessaggio, new String[0]));
			ApsSystemUtils.getLogger().error(this.getTextFromDefaultLocale(codiceMessaggio));
			this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
			ApsSystemUtils.logThrowable(e, this, "downloadAllegatoRichiesto");
			ExceptionUtils.manageExceptionError(e, this);
			target = INPUT;
			this.urlRedirect = getPageURL("portalerror");
		}

		return target;
	}

	/**
	 * Genera la url per il redirezionamento ad una specifica pagina.
	 * @param page codice pagina
	 * @return url per la redirect alla pagina
	 */
	private String getPageURL(String page) {
		RequestContext reqCtx = new RequestContext();
		Lang currentLang = this.getLangManager().getDefaultLang();
		IPage pageDest = pageManager.getPage(page);
		reqCtx.setRequest(this.getRequest());
		reqCtx.setResponse(this.response);
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG, currentLang);
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE, pageDest);
		PageURL url = this.urlManager.createURL(reqCtx);
		return url.getURL();
	}

}
