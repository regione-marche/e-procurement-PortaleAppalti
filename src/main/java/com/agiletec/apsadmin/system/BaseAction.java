/*
 *
 * Copyright 2005 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
 *
 * This file is part of jAPS software.
 * jAPS is a free software; 
 * you can redistribute it and/or modify it
 * under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
 * 
 * See the file License for the specific language governing permissions   
 * and limitations under the License
 * 
 * 
 * 
 * Copyright 2005 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
 *
 */
package com.agiletec.apsadmin.system;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.authorization.IAuthorizationManager;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.i18n.II18nManager;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.system.services.user.DelegateUser;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import it.eldasoft.utils.sign.DigitalSignatureChecker;
import it.eldasoft.utils.sign.DigitalSignatureException;
import it.maggioli.eldasoft.digital.signature.DigitalSignatureCheckClient;
import it.maggioli.eldasoft.digital.signature.ProviderEnum;
import it.maggioli.eldasoft.digital.signature.model.ResponseCheckSignature;
import it.maggioli.eldasoft.digital.signature.providers.Provider;
import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.SAFilter;
import it.maggioli.eldasoft.plugins.ppcommon.aps.SpringAppContext;
import it.maggioli.eldasoft.plugins.ppcommon.aps.UploadValidator;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.docdig.DocumentiAllegatiFirmaBean;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.pdfa.PdfAUtils;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.AccountSSO;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.*;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.IEventManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StringUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.EFlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.flussiAccessiDistinti.FlussiAccessiDistinti;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareEventsConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.keycloakclient.invoker.ApiException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ParameterAware;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Class beneath all actions.
 *
 * @author E.Santoboni
 */
public class BaseAction extends ActionSupport implements ServletRequestAware, ParameterAware {
	/**
	 * UID 
	 */
	private static final long serialVersionUID = -6738957298541768249L;
	
	protected final Logger logger = ApsSystemUtils.getLogger();
	
	private static final SimpleDateFormat YYYYMMDD_HHMMSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/** Limite inferiore e superiore di una data. */
	public static final int DA_DATA 		= 1;
	public static final int A_DATA 			= 2;
	
	/** Limite inferiore e superiore di un importo. */
	public static final int A_PARTIRE_DA	= 1;
	public static final int FINO_A		 	= 2;
	
	/** tipologia di errore da visualizzare. */
	public static final int IS_INVALID 		= 1;
	public static final int IS_REQUIRED 	= 2;

	/**
	 * ...
	 */
	protected void redirectToHome() {
		try {
			ConfigInterface configManager = (ConfigInterface) ApsWebApplicationUtils
					.getBean(SystemConstants.BASE_CONFIG_MANAGER, getRequest());
			
			StringBuffer page = new StringBuffer(configManager.getParam(SystemConstants.PAR_APPL_BASE_URL));
			if (page.charAt(page.length() - 1) != '/')
				page.append('/');
			page.append("index.jsp");
			//page.append("error.jsp");
			
			HttpServletResponse response = ServletActionContext.getResponse();
            response.sendRedirect(page.toString());
            
		} catch (Exception ex) {
			// NON DOVREBBE MAI ACCADERE !!!
			// ??? come trattare l'eccezione e copsa fare in caso di errore ???
			ApsSystemUtils.logThrowable(ex, this, "redirectToHome");
		}
	}
	
	/**
	 * Check if the current user belongs to the given group. It always returns
	 * true if the user belongs to the Administrators group.
	 *
	 * @param groupName The name of the group to check against the current user.
	 * @return true if the user belongs to the given group, false otherwise.
	 */
	protected boolean isCurrentUserMemberOf(String groupName) {
		boolean isAuth = false;
		UserDetails currentUser = this.getCurrentUser();
		if(currentUser != null) {
			IAuthorizationManager authManager = this.getAuthorizationManager();
			isAuth = authManager.isAuthOnGroup(currentUser, groupName) || authManager.isAuthOnGroup(currentUser, Group.ADMINS_GROUP_NAME);
		}
		return isAuth;
	}

	/**
	 * Check if the current user has the given permission granted. It always
	 * returns true if the user has the the "superuser" permission set in some
	 * role.
	 *
	 * @param permissionName The name of the permission to check against the
	 * current user.
	 * @return true if the user has the permission granted, false otherwise.
	 */
	protected boolean hasCurrentUserPermission(String permissionName) {
		UserDetails currentUser = this.getCurrentUser();
		IAuthorizationManager authManager = this.getAuthorizationManager();
		boolean isAuth = authManager.isAuthOnPermission(currentUser, permissionName) || authManager.isAuthOnPermission(currentUser, Permission.SUPERUSER);
		return isAuth;
	}

	public UserDetails getCurrentUser() {
		UserDetails currentUser = null;
		if(this._request != null && this._request.getSession() != null) { 
			currentUser = (UserDetails)this._request.getSession()
				.getAttribute(SystemConstants.SESSIONPARAM_CURRENT_USER);
		}
		return currentUser;
	}

	public boolean isSessionExpired() {
		UserDetails userDetails = this.getCurrentUser();
		if(userDetails == null)
			return true;
		return SystemConstants.GUEST_USER_NAME.equals(userDetails.getUsername());
	}

	@Override
	public void setParameters(Map<String, String[]> params) {
		this._params = params;
	}

	public Map<String, String[]> getParameters() {
		return this._params;
	}

	protected String getParameter(String paramName) {
		Object param = this.getParameters().get(paramName);
		if (param != null && param instanceof String[]) {
			return ((String[]) param)[0];
		} else if (param instanceof String) {
			return (String) param;
		}
		return null;
	}

	/**
	 * Return the current system language used in the back-end interface. If this
	 * language does not belong to those known by the system the default language
	 * is returned. A log line will report the problem.
	 *
	 * @return The current language.
	 */
	public Lang getCurrentLang() {
		Locale locale = this.getLocale();
		String langCode = locale.getLanguage();
		
		// get current selected language (it|en|...) from "currentLang" attribute 
		RequestContext reqCtx = (RequestContext) this._request.getAttribute(RequestContext.REQCTX);
		if(reqCtx != null) {
			Lang lang = (Lang) reqCtx.getExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG);
			if(lang != null) {
				langCode = lang.getCode();
			}
		}
		
		Lang currentLang = this.getLangManager().getLang(langCode);
		if (null != currentLang) {
			return currentLang;
		} else {
			logger.warn("Lingua richiesta ''{}'' non definita nel sistema", langCode);
			return this.getLangManager().getDefaultLang();
		}
	}

	/**
	 * Return a title by current lang.
	 *
	 * @param defaultValue The default value returned in case there is no valid
	 * title in properties.
	 * @param titles The titles.
	 * @return The title.
	 */
	public String getTitle(String defaultValue, Properties titles) {
		if (null == titles) {
			return defaultValue;
		}
		Lang currentLang = this.getCurrentLang();
		String title = titles.getProperty(currentLang.getCode());
		if (null == title) {
			Lang defaultLang = this.getLangManager().getDefaultLang();
			title = titles.getProperty(defaultLang.getCode());
		}
		if (null == title) {
			title = defaultValue;
		}
		return title;
	}	

	/**
	 * Verifica il formato del file in input.
	 *
	 * @param document file allegato
	 * @param documentFileName nome del file allegato
	 * @param format formato previsto dal servizio di backoffice
	 * @param event evento da modificare in caso di errore per la tracciatura eventi
	 * @param onlyP7m ...
	 * 
	 * @return true se i controlli sono andati a buon fine, false altrimenti
	 */
	protected boolean checkFileFormat(
			File document, 
			String documentFileName,
			Integer format, 
			Event event, 
			boolean onlyP7m) {
		return checkFileFormat(document, documentFileName, format, new Date(), event, onlyP7m, false);
	}

	/**
	 * Check if the inputted filename end with the given extension
	 * NB: The check is case-insensitive
	 * @param fileName
	 * @param extension
	 * @return
	 */
	protected boolean checkFileExtension(String fileName, String extension) {
		return StringUtils.endsWithIgnoreCase(fileName, extension);
	}

	/**
	 * Verifica la firma del file se viene richiesto i formato di file {@link PortGareSystemConstants.DOCUMENTO_FORMATO_FIRMATO}
	 *
	 * @param document file allegato
	 * @param documentFileName nome del file allegato
	 * @param format formato previsto dal servizio di backoffice
	 * @param checkDate data di verifica della validita' della firma 
	 * @param event evento da modificare in caso di errore per la tracciatura eventi
	 * @param onlyP7m
	 * @param appParamManager
	 * @return {@link DocumentiAllegatiFirmaBean} se si verifica un file firmato, {@code null} altrimenti
	 * @throws ApsSystemException nel caso ci siano errori
	 */
	protected DocumentiAllegatiFirmaBean checkFileSignature(
			File document,
			String documentFileName,
			Integer format,
			Date checkDate,
			Event event,
			boolean onlyP7m,
			IAppParamManager appParamManager,
			ICustomConfigManager customConfigManager) throws ApsSystemException	
	{
		DocumentiAllegatiFirmaBean controlliOk = null;

		boolean isP7m = checkFileExtension(documentFileName, ".p7m");
		boolean isTsd = checkFileExtension(documentFileName, ".tsd");
		boolean isPdf = checkFileExtension(documentFileName, ".pdf");
		boolean isXml = checkFileExtension(documentFileName, ".xml");
		boolean signed = (format != null && PortGareSystemConstants.DOCUMENTO_FORMATO_FIRMATO == format);
		
		logger.debug("Verifico che il formato del documento risulti un documento firmato per capire se ecluderlo o meno");
		if(signed && !isP7m && !isTsd && !isPdf && !isXml) {
			this.addActionError(this.getText("Errors.p7mRequired"));
			if (event != null) {
				event.setLevel(Event.Level.ERROR);
				event.setDetailMessage("File " + documentFileName + " scartato, richiesto file .p7m, .p7m.tsd, .pdf, .xml");
			}
			throw new ApsSystemException(this.getText("Errors.p7mRequired"));
		}
		
		if (signed && onlyP7m && !isP7m) {
			addActionError(getText("Errors.onlyP7m"));
			if (event != null) {
				event.setLevel(Event.Level.ERROR);
				event.setDetailMessage("File " + documentFileName + " scartato, richiesto file .p7m");
			}
			throw new ApsSystemException(getText("Errors.onlyP7m"));
		}
		
		logger.debug("Verifico la firma");
		// E' inutile fare il controllo visto che se non e' uno dei formati: p7m, tsd, pdf, xml; 
		// viene fatto un throws e si deve fare la verifica della firma su tutti e 4 i formati
		try {
			if (isP7m || isTsd || isXml || isPdf) {
				Integer provider = (Integer) appParamManager.getConfigurationValue("digital-signature-checker-provider");				
				if (provider == null || provider == 0) {
					// verifica della firma con BouncyCastle
					if ((!isXml && !isPdf) || signed)
						controlliOk = standardCheckSignatureMethod(
										document
										, documentFileName
										, checkDate
										, event
										, onlyP7m
										, signed
								);
				} else if (provider == 1 || provider == 2)
					// verifica tramite Maggioli o Infocert
					controlliOk = checkDocumentSignatureWithNewService(
									appParamManager
									, document
									, documentFileName
									, checkDate
									, event
									, provider == 1 ? ProviderEnum.MAGGIOLI : ProviderEnum.INFOCERT
							);
				else
					throw new RuntimeException("The Provider" + provider + " is not supported yet");
				
				// verifica la validita' del formato PDF/A e PDF/UA
				controlliOk = checkPdfACompliance(
						document, 
						documentFileName, 
						checkDate, 
						event, 
						controlliOk);
			}			
		} catch (Exception e) {
			logger.warn("Impossibile verificare la firma del documento.",e);
			throw new ApsSystemException(e.getMessage(), e);
		}

		return controlliOk;
	}
	
	/**
	 * verifica la validita' del formato PDF/A, PDF/UA
	 * @throws Exception 
	 */
	private DocumentiAllegatiFirmaBean checkPdfACompliance(
			File document,
			String documentFileName,
			Date checkDate,
			Event event,
			DocumentiAllegatiFirmaBean controlliOk) throws Exception 
	{
		// verifica solo quando e' attiva la customizzazione UPLOAD|UPLOADPDF-A.ACTIVE = 1
		ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());		
		CustomConfigManager customConfigManager = (CustomConfigManager) ctx.getBean(CommonSystemConstants.CUSTOM_CONFIG_MANAGER);
		
		boolean uploadPdfA = customConfigManager.isActiveFunction("PDF", "UPLOADPDF-A");
		if(uploadPdfA) {
			boolean isPdfA = false;
			
			if (checkFileExtension(documentFileName, ".p7m") || checkFileExtension(documentFileName, ".tsd")) {
				// recupera il contentuto "pdf"
				byte[] pdf = (byte[])estraiContenutoDocumentoFirmato(document, documentFileName, true, event);					
				String pdfFilename = (String)estraiContenutoDocumentoFirmato(document, documentFileName, false, event);		
				while (pdf != null
						&& (checkFileExtension(pdfFilename, "P7M") || checkFileExtension(pdfFilename, "TSD"))
				) {
					pdf = (byte[])estraiContenutoDocumentoFirmato(pdf, pdfFilename, true, event);
					pdfFilename = (String)estraiContenutoDocumentoFirmato(pdf, pdfFilename, false, event);
				}
				if(pdf != null) {
					isPdfA = PdfAUtils.checkIsPdfACompliant(pdf, pdfFilename, event, this);
				}
				pdf = null;
				
			} else if (checkFileExtension(documentFileName, ".pdf")) {
				isPdfA = PdfAUtils.checkIsPdfACompliant(document, documentFileName, event, this);
			}
			
			// marca l'allegato come PDF-A  
			if(controlliOk == null) {
				controlliOk = new DocumentiAllegatiFirmaBean().firmacheckts(checkDate).firmacheck(false);
			}
			controlliOk.setPdfaCompliant(isPdfA);
		}
		
		return controlliOk;
	}

	/**
	 * verifica della firma digitale di un documento per mezzo di servizio esterno 
	 */
	private DocumentiAllegatiFirmaBean checkDocumentSignatureWithNewService(
			IAppParamManager appParamManager
			, File document
			, String documentFileName
			, Date checkDate
			, Event evento
			, ProviderEnum providerEnum
	) {
		logger.debug("Chiamo il servizio esterno con il provider {} per verificare la firma elettronica.", providerEnum.name());

		Date firmacheckts = checkDate != null ? checkDate : Date.from(Instant.now());
		
		DocumentiAllegatiFirmaBean controlliOk = null;
		IEventManager eventManager = null;
		FileInputStream fis = null;
		try {
			Map<String, AppParam> kongParams = appParamManager.getMapAppParamsByCategory("kong-client");
			String authUrl = kongParams.get(AppParamManager.KONG_AUTH_URL).getValue();
			//String authUsername = kongParams.get(AppParamManager.KONG_AUTH_USERNAME).getValue();
			//String authSecret = kongParams.get(AppParamManager.KONG_AUTH_PASSWORD).getValue();
			String authUsername = kongParams.get(AppParamManager.KONG_AUTH_CLIENT_ID).getValue();
			String authSecret = kongParams.get(AppParamManager.KONG_AUTH_CLIENT_SECRET).getValue();
			
			Map<String, AppParam> parameters = appParamManager.getMapAppParamsByCategory("digital-signature-check");
			String providerUrl = parameters.get("digital-signature-check-url").getValue();

			Provider provider =
					DigitalSignatureCheckClient
							.getInstance(providerEnum)
//							.withConfiguration(providerUrl);
							.withConfiguration(authUrl, authUsername, authSecret, providerUrl);

			// crea l'evento per la verifica della firma digitale tramite servizio esterno
			ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
			eventManager = (IEventManager) ctx.getBean(PortGareSystemConstants.EVENTI_MANAGER);
			
			// inserisci l'evento di uscita verso il servizio di verifica della firma...  
			Event eventoCheckFirma = new Event();
			eventoCheckFirma.setUsername(evento.getUsername());
			eventoCheckFirma.setIpAddress(evento.getIpAddress());
			eventoCheckFirma.setSessionId(evento.getSessionId());
			eventoCheckFirma.setDestination(evento.getDestination());
			eventoCheckFirma.setEventType(PortGareEventsConstants.CHECKFIRMA);
			eventoCheckFirma.setLevel(Event.Level.INFO);
			eventoCheckFirma.setMessage("Verifica firma digitale per il file " + documentFileName + 
					  		  			" presso il servizio " + providerUrl + ", " + authUrl + "");
			eventManager.insertEvent(eventoCheckFirma);
			
			logger.debug("verifica firma alla data: {}", YYYYMMDD_HHMMSS.format(firmacheckts));
			fis = new FileInputStream(document);
			ResponseCheckSignature rcs = null;
			if (providerEnum == ProviderEnum.MAGGIOLI)
				rcs = provider.checkDigitalSignature(
						firmacheckts,
						fis,
						documentFileName
				);
			else
				rcs = provider.checkDigitalSignature(
						fis,
						documentFileName
				);

			logger.debug("Ricevuta risposta: {}",rcs.getJsonbody());
			
			controlliOk = new DocumentiAllegatiFirmaBean().firmacheck(rcs.getVerified()).firmacheckts(firmacheckts);
			
		} catch (ApiException e) {
			logger.error("Errore nella verifica della firma digitale.",e);
//				this.addActionError(this.getText("Errors.cannotVerifySign"));
			if (evento != null) {
				evento.setLevel(Event.Level.WARNING);
				evento.setDetailMessage("Impossibile verificare la firma digitale per il file "
						+ documentFileName 
						+ ": " + e.getMessage());
			}
			controlliOk = new DocumentiAllegatiFirmaBean().firmacheck(Boolean.FALSE).firmacheckts(firmacheckts);
		} catch (FileNotFoundException e) {
			logger.error("Errore nella lettura del file per la verifica della firma digitale.",e);
//				this.addActionError(this.getText("Errors.fileNotSet"));
			if (evento != null) {
				evento.setError(e);
			}
			controlliOk = new DocumentiAllegatiFirmaBean().firmacheck(Boolean.FALSE).firmacheckts(firmacheckts);
		} catch (Exception e) {
			final String ERROR_MESSAGE = "Errore nella verifica della firma digitale, controllare che il servizio per il controllo della firma sia operativo e che le configurazioni impostate siano corrette.";
			logger.error(ERROR_MESSAGE);
			if (evento != null) {
				evento.setLevel(Event.Level.ERROR);
				evento.setDetailMessage(ERROR_MESSAGE);
			}
			controlliOk = new DocumentiAllegatiFirmaBean().firmacheck(Boolean.FALSE).firmacheckts(firmacheckts);
		}
				
		// chiudi lo il filestream anche in caso di errori e rilascia il file... 
		if(fis != null) {
			try {
				fis.close();
			} catch (IOException e) {
			}
		}
		
		if(controlliOk != null && Boolean.FALSE.equals(controlliOk.getFirmacheck())) {
			this.addActionMessage(this.getText("Warning.signValidationNotSupported"));
//				this.addActionError(this.getText("Errors.invalidSign"));
			if (evento != null) {
				evento.setLevel(Event.Level.WARNING);
				evento.setDetailMessage("Il file " + documentFileName + " non contiene una firma digitale valida");
			}
		}
		
		return controlliOk;
	}

	/**
	 * Metodo standard per la verifica delle firme
	 *
	 * @param document
	 * @param documentFileName
	 * @param checkDate
	 * @param event
	 * @param onlyP7m
	 * @param b
	 * @return
	 */
	private DocumentiAllegatiFirmaBean standardCheckSignatureMethod(
			File document,
			String documentFileName,
			Date checkDate,
			Event event,
			boolean onlyP7m, 
			boolean signRequested) 
	{
		logger.debug("Verifico la firma con il metodo standard.");
		DocumentiAllegatiFirmaBean controlliOk = null;
		// come file firmati si considerano .p7m, .tsd, .pdf, .xml
		// NB: per .p7m e .tsd e' possibible verificare la firma digitale
		//     mentre per .pdf, .xml si visualizza a video un messaggio
		//     di notifica per "l'accettazione di un documento" che
		//     non ha firma digitale
		boolean isP7m = checkFileExtension(documentFileName, ".p7m");
		boolean isTsd = checkFileExtension(documentFileName, ".tsd");
		boolean isPdf = checkFileExtension(documentFileName, ".pdf");
		boolean isXml = checkFileExtension(documentFileName, ".xml");

		if (document != null) {
			controlliOk = new DocumentiAllegatiFirmaBean().firmacheckts(checkDate);
			if (isP7m)
				controlliOk.firmacheck(checkDocumentP7m(document, documentFileName, event, Date.from(Instant.now())));
			else if (isTsd)
				controlliOk.firmacheck(checkDocumentTsd(document, documentFileName, checkDate, event, signRequested));
			else if (isPdf)
				controlliOk.firmacheck(checkDocumentPdf(document, documentFileName, event, Date.from(Instant.now())));
			else if (isXml)
				controlliOk.firmacheck(checkDocumentXml(document, documentFileName, event));
		}
		if (controlliOk != null && Boolean.FALSE.equals(controlliOk.getFirmacheck())) {
			this.addActionMessage(this.getText("Warning.signValidationNotSupported"));
		}
		return controlliOk;
	}
	
	/**
	 * Verifica il formato del file in input.
	 *
	 * @param document file allegato
	 * @param documentFileName nome del file allegato
	 * @param format formato previsto dal servizio di backoffice
	 * @param checkDate data di verifica della validita' della firma
	 * @param event evento da modificare in caso di errore per la tracciatura eventi
	 * @param onlyP7m
	 * @param signRequested indica se il file prevede una firma digitale da verificare
	 *
	 * @return true se i controlli sono andati a buon fine, false altrimenti
	 */
	protected boolean checkFileFormat(
			File document,
			String documentFileName,
			Integer format,
			Date checkDate,
			Event event,
			boolean onlyP7m,
			boolean signRequested) 
	{
		boolean controlliOk = true;
		if (documentFileName != null
			&& (format == null || format == PortGareSystemConstants.DOCUMENTO_FORMATO_QUALSIASI)
			&& documentFileName.toUpperCase().endsWith(".P7M")) 
		{
			format = PortGareSystemConstants.DOCUMENTO_FORMATO_FIRMATO;
		}
		if (documentFileName != null) {
			boolean doCheckDocument = false;
			boolean isP7m = checkFileExtension(documentFileName, ".P7M");
			boolean isTsd = checkFileExtension(documentFileName, ".TSD");
			boolean isPdf = checkFileExtension(documentFileName, ".PDF");
			boolean isXml = checkFileExtension(documentFileName, ".XML");
			if (format == null) {
				// NB: nel caso non sia specificato un formato si verifica in base all'estensione del file
				//     e forza il controllo solo ai PDF (PORTAPPALT-920) 
				doCheckDocument = true;
				isP7m = false;
				isTsd = false;
				isXml = false;
			} else {
				switch (format) {
					case PortGareSystemConstants.DOCUMENTO_FORMATO_FIRMATO:
	//					// come file firmati si considerano .p7m, .tsd, .pdf, .xml
	//					// NB: per .p7m e .tsd e' possibible verificare la firma digitale
	//					//     mentre per .pdf, .xml si visualizza a video un messaggio
	//					//     di notifica per "l'accettazione di un documento" che
	//					//     non ha firma digitale
						if (onlyP7m && !isP7m) {
							this.addActionError(this.getText("Errors.onlyP7m"));
							if (event != null) {
								event.setLevel(Event.Level.ERROR);
								event.setDetailMessage("File " + documentFileName
															   + " scartato, richiesto file .p7m");
							}
							controlliOk = false;
						} else if (!isP7m && !isTsd && !isPdf && !isXml) {
							this.addActionError(this.getText("Errors.p7mRequired"));
							if (event != null) {
								event.setLevel(Event.Level.ERROR);
								event.setDetailMessage("File " + documentFileName
															   + " scartato, richiesto file .p7m, .p7m.tsd, .pdf, .xml");
							}
							controlliOk = false;
						} else if (document != null) {
							//Causa una duplicazione del controllo firma
							doCheckDocument = true;
						}
						break;
	
					case PortGareSystemConstants.DOCUMENTO_FORMATO_PDF:
						if (!checkFileExtension(documentFileName,".PDF")) {
							this.addActionError(this.getText("Errors.pdfRequired"));
							if (event != null) {
								event.setLevel(Event.Level.ERROR);
								event.setDetailMessage("File " + documentFileName
															   + " scartato, richiesto file PDF");
							}
							controlliOk = false;
						}
						if(!checkDocumentPdf(document, documentFileName, event, null)) {
							controlliOk = false;
						} 
						break;
	
					case PortGareSystemConstants.DOCUMENTO_FORMATO_EXCEL:
						if (!checkFileExtension(documentFileName,".XLS")
								&& !checkFileExtension(documentFileName,".XLSX")
								&& !checkFileExtension(documentFileName,".ODS")) {
							this.addActionError(this.getText("Errors.excelRequired"));
							if (event != null) {
								event.setLevel(Event.Level.ERROR);
								event.setDetailMessage("File " + documentFileName
															   + " scartato, richiesto file in formato foglio elettronico (XLS, XLSX, ODS)");
							}
							controlliOk = false;
						}
						break;
	
					default:
						break;
				}
			}
			if(doCheckDocument) {
				if (isP7m)
					controlliOk = checkDocumentP7m(document, documentFileName, event, Boolean.TRUE, null);
				else if (isTsd)
					controlliOk = checkDocumentTsd(document, documentFileName, checkDate, event, Boolean.TRUE, true);
				else if(isPdf) {
					controlliOk = checkDocumentPdf(document, documentFileName, event, null);
				} 
//				else if(isXml) {
//					controlliOk = checkDocumentXml(document, documentFileName, event);
//				}
			}
		}
		return controlliOk;
	}

	/**
	 * Controllo di validita' della firma di un file utilizzando le bouncy castle (eldasoft-utils)
	 *
	 * @param document Il file
	 * @param documentFileName nome del file
	 * @param event evento da inserire a db
	 * @param showActionError In caso di errore se a true inserisce una action error.
	 * @param currentTime Se valorizzato viene controllato se la firma e' scaduta con la data immessa, se nullo viene saltato il controllo
	 * @return true = firma verificata, false = firma non verificata
	 */
	private boolean checkDocumentSign(
			File document, 
			String documentFileName, 
			Event event, 
			boolean showActionError, 
			Date currentTime) 
	{
		boolean controlliOk = true;

		try (FileInputStream file = new FileInputStream(document)) {
			DigitalSignatureChecker checker = new DigitalSignatureChecker();
			boolean signVerified = checker.verifySignature(IOUtils.toByteArray(file), currentTime);
			if (!signVerified) {
				if(showActionError) this.addActionError(this.getText("Errors.invalidSign"));
				if (event != null) {
					event.setLevel(Event.Level.ERROR);
					event.setDetailMessage("Il file " + documentFileName
							+ " non contiene una firma digitale valida");
				}
				controlliOk = false;
			}
		} catch (IOException e) {
			if(showActionError) this.addActionError(this.getText("Errors.fileNotSet"));
			if (event != null) {
				event.setError(e);
			}
			controlliOk = false;
		} catch (DigitalSignatureException e) {
			if(showActionError) this.addActionError(this.getText("Errors.cannotVerifySign"));
			if (event != null) {
				event.setLevel(Event.Level.ERROR);
				event.setDetailMessage("Impossibile verificare la firma digitale per il file "
						+ documentFileName 
						+ ": " + e.getMessage());
			}
			controlliOk = false;
		}

		return controlliOk;
	}

	/**
	 * Controllo di validita'  utilizzando le bouncy castle (eldasoft-utils)
	 *
	 * @param document Il file
	 * @param documentFileName nome del file
	 * @param event evento da inserire a db
	 * @return true = firma verificata, false = firma non verificata
	 */
	private boolean checkDocumentP7m(
			File document, 
			String documentFileName, 
			Event event) 
	{
		return checkDocumentP7m(document, documentFileName, event, null);
	}
	
	/**
	 * Controllo di validita'  utilizzando le bouncy castle (eldasoft-utils)
	 *
	 * @param document Il file
	 * @param documentFileName nome del file
	 * @param event evento da inserire a db
	 * @param currentTime Se valorizzato viene controllato se la firma e' scaduta con la data immessa, se nullo viene saltato il controllo
	 * @return true = firma verificata, false = firma non verificata
	 */
	private boolean checkDocumentP7m(
			File document, 
			String documentFileName, 
			Event event, 
			Date currentTime) 
	{
		return checkDocumentP7m(document, documentFileName, event, Boolean.FALSE, currentTime);
	}

	/**
	 * Controllo di validita'  utilizzando le bouncy castle (eldasoft-utils)
	 *
	 * @param document Il file
	 * @param documentFileName nome del file
	 * @param event evento da inserire a db
	 * @param showActionError In caso di errore se a true inserisce una action error.
	 * @param currentTime Se valorizzato viene controllato se la firma e' scaduta con la data immessa, se nullo viene saltato il controllo
	 * @return true = firma verificata, false = firma non verificata
	 */
	private boolean checkDocumentP7m(
			File document, 
			String documentFileName, 
			Event event, 
			boolean showActionError, 
			Date currentTime) 
	{
		return checkDocumentSign(
				document, 
				documentFileName, 
				event, 
				showActionError, 
				currentTime);
	}

	/**
	 * verifica la validita' di un documento .tsd 
	 */
	private boolean checkDocumentTsd(
			File document,
			String documentFileName,
			Date checkDate,
			Event event, 
			boolean signRequested) 
	{
		return checkDocumentTsd(
				document, 
				documentFileName, 
				checkDate, 
				event, 
				Boolean.FALSE, 
				signRequested);
	}
	
	/**
	 * verifica la validita' di un documento in formato .tsd
	 */
	private boolean checkDocumentTsd(
			File document, 
			String documentFileName, 
			Date checkDate, 
			Event event, 
			boolean showActionError, 
			boolean signedRequested) 
	{
		logger.debug("checkDocumentTsd({})", documentFileName);
		boolean controlliOk = true;
		try (FileInputStream file = new FileInputStream(document)) {
			DigitalSignatureChecker checker = new DigitalSignatureChecker();
			byte[] stream = IOUtils.toByteArray(file);
			
//			boolean signVerified = checker.verifySignature(stream);
//			if (!signVerified) {
//				this.addActionError(this.getText("Errors.invalidSign"));
//				if (event != null) {
//					event.setLevel(Event.Level.ERROR);
//					event.setDetailMessage("Il file " + documentFileName
//							+ " non contiene una firma digitale valida");
//				}
//				controlliOk = false;
//			}
			
			// recupera la marcatura temporale...
			String verificaMarcheTemporaliXML = null;
			byte[] content = null;
			try {
				content = checker.getContentTimeStamp(stream);
			} catch (Exception e) {
				if (event != null) {
					event.setLevel(Event.Level.WARNING);
					event.setDetailMessage("Impossibile verificare la firma digitale per il file "
												   + documentFileName);
				}
				addActionError(getText("Errors.cannotVerifyTimestamp"));
				controlliOk = false;
			}
			if (controlliOk) {
				if (content != null) {
					byte[] xmlTimeStamps = null;
					try {
						xmlTimeStamps = checker.getXMLTimeStamps(content);
						//content = checker.getContent(content);	// recupera lo stream del file contenuto per estrarre l'estensione del file
					} catch (Exception e) {
						xmlTimeStamps = checker.getXMLTimeStamps(stream);
						//content = checker.getContent(content);	// recupera lo stream del file contenuto per estrarre l'estensione del file
					}
					verificaMarcheTemporaliXML = new String(xmlTimeStamps);
				} else {
					content = stream;
				}

				// recupera l'estensione del file contenuto...
				// (NB: per ora recupera l'estesione dal documentfilename anziche' leggere il contenuto binario)
				String fn = documentFileName.substring(0, documentFileName.length() - 4);
				boolean isP7m = checkFileExtension(fn, ".P7M");
//				boolean isTsd = fn.toUpperCase().endsWith(".TSD"); //UNUSED
				boolean isPdf = checkFileExtension(fn,".PDF");
				boolean isXml = checkFileExtension(fn,".XML");

				// recupera la firma digitale...
				boolean verificaFirma = false;
				String verificaFirmaDigitaleXML = null;
				if (isP7m) {
					byte[] xml = checker.getXMLSignature(content, checkDate);
					verificaFirmaDigitaleXML = new String(xml);
					verificaFirma = true;
				} else if (isPdf || isXml) {
					if (showActionError) this.addActionMessage(this.getText("Warning.signValidationNotSupported"));
					controlliOk = false;
				} else {
					if (signedRequested) {
						if (showActionError) this.addActionMessage(this.getText("Warning.signValidationNotSupported"));
//					if (showActionError) this.addActionError(this.getText("Errors.cannotVerifySign"));
//					if (event != null) {
//						event.setLevel(Event.Level.WARNING);
//						event.setDetailMessage("Impossibile verificare la firma digitale per il file "
//													   + documentFileName);
						controlliOk = false;
					}
				}

				// verifica la firma digitale...
				if (verificaFirma && StringUtils.isEmpty(verificaFirmaDigitaleXML)) {
					if (showActionError) this.addActionError(this.getText("Errors.invalidSign"));
					if (event != null) {
						event.setLevel(Event.Level.WARNING);
						event.setDetailMessage("Il file " + documentFileName
													   + " non contiene una firma digitale valida");
					}
					controlliOk = false;
				}

				// verifica la marcatura temporale...
				if ((verificaMarcheTemporaliXML == null)
						|| (verificaMarcheTemporaliXML != null && verificaMarcheTemporaliXML.isEmpty())) {
//	        	this.addActionError(this.getText("Errors.invalid???"));
//				if (event != null) {
//					event.setLevel(Event.Level.ERROR);
//					event.setDetailMessage("Il file " + documentFileName
//							+ " non contiene una marcatura temporale valida");
//				}
				}
			}

		} catch (DigitalSignatureException e) {
			if(showActionError) this.addActionError(this.getText("Errors.cannotVerifySign"));
			if (event != null) {
				event.setLevel(Event.Level.WARNING);
				event.setDetailMessage("Impossibile verificare la firma digitale per il file "
						+ documentFileName 
						+ ": " + e.getMessage());
			}
			controlliOk = false;
		} catch (IOException e) {
			if (showActionError) this.addActionError(this.getText("Errors.fileNotSet"));
			if (event != null) event.setError(e);
			controlliOk = false;
		}

		return controlliOk;
	}

	/**
	 * verifica la validita' di un documento in formato .pdf ed addizionalmente anche se e' in formato PDF-A
	 * 
	 * @param document Il file
	 * @param documentFileName nome del file
	 * @param event evento da inserire a db
	 * @param currentTime Se valorizzato viene controllato se la firma e' scaduta con la data immessa, se nullo viene saltato il controllo
	 * @return true = firma verificata, false = firma non verificata
	 */
	private boolean checkDocumentPdf(
			File document, 
			String documentFileName,
			Event event,
			Date currentTime)
	{
		logger.debug("checkDocumentPdf({})", documentFileName);
		boolean controlliOk = true;

		// verifica la firma solo se necessario...
		if(currentTime != null) {
			controlliOk = checkDocumentSign(
					document, 
					documentFileName, 
					event, 
					Boolean.TRUE, 
					currentTime); 	
		}
		
		return controlliOk;
	}

	/**
	 * verifica la validita' di un documento in formato .xml
	 */
	private boolean checkDocumentXml(File document, String documentFileName, Event event) {
		logger.debug("checkDocumentXml({})", documentFileName);
		boolean controlliOk = false;
		
		return controlliOk;
	}
	
	/**
	 * Estrai da un documento firmato, il contenuto o il nomefile del contenuto
	 * 
	 * @param document file dal quale estrarre i contenuti
	 * @param documentFileName nomefile del file dal quale estrarre i contenuti
	 * @param contenuto True estrae il contenuto binario dal documento
	 *                  False estrae il nomefile del contenuto nel documento
	 * @param event evento su cui registrare evetnuali tracciature
	 * 
	 */
	public Object estraiContenutoDocumentoFirmato(
			File document, 
			String documentFileName, 
			boolean getContenuto, 
			Event event) 
	{
		logger.debug("estraiContenutoDocumentoFirmato({})", documentFileName);
		Object result = null;
		try {
			byte[] stream = FileUtils.readFileToByteArray(document);
			result = estraiContenutoDocumentoFirmato(stream, documentFileName, getContenuto, event);
		} catch (Throwable t) {
			this.addActionError(this.getText("Errors.fileNotSet"));
			if (event != null) {
				event.setError(t);
			}
		}
		return result;
	}
	
	/**
	 * Estrai da un documento firmato, il contenuto o il nomefile del contenuto
	 * 
	 * @param content file dal quale estrarre i contenuti
	 * @param fileName nomefile del file dal quale estrarre i contenuti
	 * @param getContenuto True estrae il contenuto binario dal documento
	 *                  False estrae il nomefile del contenuto nel documento
	 * @param event evento su cui registrare evetnuali tracciature
	 * 
	 */
	public Object estraiContenutoDocumentoFirmato(
			byte[] content, 
			String fileName, 
			boolean getContenuto, 
			Event event) 
	{
		logger.debug("estraiContenutoDocumentoFirmato({})", fileName);
		String contentFilename = null;
		try {
			DigitalSignatureChecker checker = null;
			byte[] stream = null;
			if(getContenuto) {
				checker = new DigitalSignatureChecker();
				stream = content;
			}
	
			contentFilename = fileName;
			while (contentFilename != null && contentFilename.length() > 0) {
				boolean isP7m = UploadValidator.isP7m(contentFilename);
				boolean isTsd = UploadValidator.isTsd(contentFilename);
				boolean isPdf = UploadValidator.isPdf(contentFilename);
				boolean isXml = UploadValidator.isXml(contentFilename);
				
				// recupera il contenuto...
				if(isTsd) {
					if(getContenuto && stream != null) { 
						byte[] stm = checker.getContentTimeStamp(stream);
						if (stm != null) {
							stream = stm;
						}
					}
				} else if(isP7m) {
					if(getContenuto && stream != null) {
						byte[] stm = checker.getContent(stream);
						if (stm != null) {
							stream = stm;
						}
					}
				} else {
					// fine dell'estrazione del contenuto...
					break;
				}
				
				contentFilename = contentFilename.substring(0, contentFilename.length() - 4);
			}	
			
			content = stream;
	        
//		} catch (FileNotFoundException e) {
//			this.addActionError(this.getText("Errors.fileNotSet"));
//			if (event != null) {
//				event.setError(e);
//			}
//			content = null;
//			contentFilename = null;
//		} catch (IOException e) {
//			this.addActionError(this.getText("Errors.fileNotSet"));
//			if (event != null) {
//				event.setError(e);
//			}
//			content = null;
//			contentFilename = null;
		} catch (DigitalSignatureException e) {
			this.addActionError(this.getText("Errors.cannotVerifySign"));
			if (event != null) {
				event.setLevel(Event.Level.ERROR);
				event.setDetailMessage("Impossibile verificare la firma digitale per il file "
						+ fileName //document.getAbsolutePath() 
						+ ": " + e.getMessage());
			}
			content = null;
			contentFilename = null;
		} 
		
		// restituisci il contenuto o il nomefile del contenuto... 
		return (getContenuto ? content : contentFilename);
	}
	
	/**
	 * Restituisce il contenuto binario del documento digitale (Tsd, P7m, ...)
	 * 
 	 * @param document file dal quale estrarre i contenuti
	 * @param documentFileName nomefile del file dal quale estrarre i contenuti
	 * 
	 * @return un array di byte con il contentuto del documento digitale
	 */
	protected byte[] getContenutoDocumentoFirmato(File document, String documentFileName, Event event) {
		return (byte[]) this.estraiContenutoDocumentoFirmato(document, documentFileName, true, event);
	}

	/**
	 * Restituisce il nomefile del contenuto del documento digitale (Tsd, P7m, ...)
	 * 
 	 * @param document file dal quale estrarre i contenuti
	 * @param documentFileName nomefile del file dal quale estrarre i contenuti
	 * 
	 * @return il nomefile del contenuto del documento digitale
	 */
	protected String getFilenameDocumentoFirmato(File document, String documentFileName, Event event) {
		return (String) this.estraiContenutoDocumentoFirmato(document, documentFileName, false, event);
	}
	
	/**
	 * Ritorna una etichetta in lingua appoggiandosi alla tabella LOCALSTRINGS.
	 * Con il nome del campo in input si cerca nel file di resources la
	 * definizione della chiave in LOCALSTRINGS e si reperisce la relativa
	 * descrizione.
	 * 
	 * @param key
	 *            nome del campo oggetto del messaggio
	 * @return etichetta in lingua relativo al campo
	 */
	public String getTextFromDB(String key) {
		String text = null;

		// si recupera del file di resources la chiave in LOCALSTRINGS per reperire il nome del campo
		String localstringKey = this.getText(key);
		if (key.equals(localstringKey)) {
			// allora nel file di resources non e' presente alcun mapping, si costruisce la chiave secondo una convenzione
			localstringKey = "LABEL_" + StringUtilities.camelToUpperCaseSnake(key);
		}
		// si recupera da LOCALSTRINGS il nome del campo nella lingua in uso
		text = this.getI18nLabel(localstringKey);
		text = StringUtils.replace(text, "'", "''");

		return text;
	}

	/**
	 * Ritorna una etichetta in lingua di default appoggiandosi alla tabella LOCALSTRINGS.
	 * Con il nome del campo in input si cerca nel file di resources la
	 * definizione della chiave in LOCALSTRINGS e si reperisce la relativa
	 * descrizione.
	 * 
	 * @param key
	 *            nome del campo oggetto del messaggio
	 * @return etichetta in lingua di default relativo al campo
	 */
	public String getTextFromDBDefaultLocale(String key) {
		String text = null;
		try {
			// si recupera del file di resources la chiave in LOCALSTRINGS per reperire il nome del campo
			String localstringKey = this.getText(key);
			if (key.equals(localstringKey)) {
				// allora nel file di resources non e' presente alcun mapping, si costruisce la chiave secondo una convenzione
				localstringKey = "LABEL_" + StringUtilities.camelToUpperCaseSnake(key);
			}
			// si recupera da LOCALSTRINGS il nome del campo nella lingua in uso
			text = this.getI18nManager().getLabel(localstringKey, getDefaultLocale());
			text = StringUtils.replace(text, "'", "''");
		} catch (ApsSystemException e) {
			// non si verifichera' mai, e' una eccezione emessa da firma del metodo ma realmente mai emessa
		}
		return text;
	}

	/**
	 * @return il Locale italiano
	 */
	public String getDefaultLocale() {
		return Locale.ITALY.getLanguage();
	}
	
	/**
	 * Ritorna una etichetta in lingua ITALIANA (default).
	 * 
	 * @param bundle
	 *            nome del resource bundle contenente il messaggio
	 * @param key
	 *            chiave del messaggio da reperire
	 * @param params
	 *            elenco dei parametri
	 * @return etichetta in lingua default con le opportune sostituzioni dei
	 *         segnaposto {indice} con i parametri
	 */
	private String getTextFromDefaultLocaleWithBundle(String bundle, String key, Object... params) {
		String text = null;
		ResourceBundle rb = LocalizedTextUtil.findResourceBundle(bundle, Locale.ITALY);
		text = MessageFormat.format(rb.getString(key), params);
		return text;
	}

	/**
	 * Ritorna una etichetta in lingua ITALIANA (default).
	 * 
	 * @param key
	 *            chiave del messaggio da reperire
	 * @param params
	 *            elenco dei parametri
	 * @return etichetta in lingua default con le opportune sostituzioni dei
	 *         segnaposto {indice} con i parametri
	 */
	public String getTextFromDefaultLocale(String key, Object... params) {
		String text = null;
		
		// prima cerca nel package "locale"...
		try {
			text = this.getTextFromDefaultLocaleWithBundle(this.getClass().getPackage().getName() + ".package", key, params);
		} catch (Exception ex) {
			text = null;
		}
		
		if(text == null) {
			// ...poi cerca nel package globale "it.maggioli.eldasoft.plugins.global-messages"...
			try {
				text = this.getTextFromDefaultLocaleWithBundle("it.maggioli.eldasoft.plugins.global-messages", key, params);
			} catch (Exception ex) {
				text = null;
			}
		}
		
		return text;
	}
	
	/**
	 * Ritorna una etichetta in lingua
	 * 
	 * @param key
	 *            nome del campo oggetto del messaggio
	 * @param langCode
	 *            nome del campo oggetto del messaggio
	 *            
	 * @return etichetta in lingua corrente con le opportune sostituzioni dei
	 *         segnaposto {indice} con i parametri
	 */
	public String getI18nLabel(String key, String langCode) {
		String s = key; 
		try {
			s = this.getI18nManager().getLabel(key, langCode);
		} catch (ApsSystemException e) {
			// non si verifichera' mai, e' una eccezione emessa da firma del
			// metodo ma realmente mai emessa
		}
		return s;
	}
	
	/**
	 * Ritorna una etichetta in lingua corrente.
	 * 
	 * @param key
	 *            nome del campo oggetto del messaggio
	 * @return etichetta in lingua corrente con le opportune sostituzioni dei
	 *         segnaposto {indice} con i parametri
	 */
	public String getI18nLabel(String key) {
//		String s = key; 
//		try {
//			s = this.getI18nManager().getLabel(key, this.getCurrentLang().getCode());
//		} catch (ApsSystemException e) {
//			// non si verifichera' mai, e' una eccezione emessa da firma del
//			// metodo ma realmente mai emessa
//		}
//		return s;
		return this.getI18nLabel(key, this.getCurrentLang().getCode());
	}
	
	/**
	 * Ritorna un messaggio formattato di una etichetta in lingua corrente.
	 * 
	 * @param key
	 *            nome del campo oggetto del messaggio
	 * @return etichetta in lingua corrente con le opportune sostituzioni dei
	 *         segnaposto {indice} con i parametri
	 */
	public String formatI18nLabel(String key, Object... params) {
		String s = key; 
		try {
			s = this.getI18nManager().getLabel(key, this.getCurrentLang().getCode());
			if(s != null && params != null) {
				s = MessageFormat.format(s, params);
			}
		} catch (ApsSystemException e) {
			// non si verifichera' mai, e' una eccezione emessa da firma del
			// metodo ma realmente mai emessa
		}
		return (s != null ? s : key);
	}

	/**
	 * Ritorna una etichetta nella lingua di default.
	 * 
	 * @param key
	 *            nome del campo oggetto del messaggio
	 * @return etichetta in lingua corrente con le opportune sostituzioni dei
	 *         segnaposto {indice} con i parametri
	 */
	public String getI18nLabelFromDefaultLocale(String key) {
		String s = key; 
		try {
			s = this.getI18nManager().getLabel(key, this.getDefaultLocale());
		} catch (ApsSystemException e) {
			// non si verifichera' mai, e' una eccezione emessa da firma del
			// metodo ma realmente mai emessa
		}
		return s;
	}

	/**
	 * Ritorna un messaggio formattato di una etichetta nella lingua di default comple.
	 * 
	 * @param key
	 *            nome del campo oggetto del messaggio
	 * @param params
	 * 			  elenco dei parametri
	 * @return etichetta in lingua corrente con le opportune sostituzioni dei
	 *         segnaposto {indice} con i parametri
	 */
	public String formatI18nLabelFromDefaultLocale(String key, Object... params) {
		String s = key; 
		try {
			s = this.getI18nManager().getLabel(key, this.getDefaultLocale());
			if(s != null && params != null) {
				s = MessageFormat.format(s, params);
			}
		} catch (ApsSystemException e) {
			// non si verifichera' mai, e' una eccezione emessa da firma del
			// metodo ma realmente mai emessa
		}
		return (s != null ? s : key);
	}
	
	/**
	 * Aggiunge un messaggio d'errore localizzato in lingua riportante il nome
	 * del campo data non valorizzato e richiesto.
	 * 
	 * @param localstringKey
	 *            etichetta in LOCALSTRINGS che individua il nome del campo data
	 * @param limit
	 *            0=data esatta, 1=dal 2=al
	 */
	public void addActionErrorDateRequired(String localstringKey, int limit) {
		String name = this.getI18nLabel(localstringKey);
		switch (limit) {
		case DA_DATA:
			name += " " + this.getI18nLabel("LABEL_DA_DATA");
			break;
		case A_DATA:
			name += " " + this.getI18nLabel("LABEL_A_DATA");
			break;
		default:
			break;
		}
		this.addActionError(this.getText("Errors.searchForm.date.required",
				new String[] { name }));
	}

	/**
	 * Aggiunge un messaggio d'errore localizzato in lingua riportante il nome
	 * del campo data errato ed il valore.
	 * 
	 * @param localstringKey
	 *            etichetta in LOCALSTRINGS che individua il nome del campo data
	 * @param limit
	 *            0=data esatta, 1=dal 2=al
	 * @param value
	 *            valore errato inserito
	 */
	public void addActionErrorDateInvalid(String localstringKey, int limit, String value) {
		String name = this.getI18nLabel(localstringKey);
		switch (limit) {
		case DA_DATA:
			name += " " + this.getI18nLabel("LABEL_DA_DATA");
			break;
		case A_DATA:
			name += " " + this.getI18nLabel("LABEL_A_DATA");
			break;
		default:
			break;
		}
		this.addActionError(this.getText("Errors.searchForm.date.invalid",
				new String[] { name, value }));
	}

	/**
	 * Aggiunge un messaggio d'errore localizzato in lingua riportante il nome
	 * del campo importo non valorizzato e richiesto.
	 * 
	 * @param localstringKey
	 *            etichetta in LOCALSTRINGS che individua il nome del campo importo
	 * @param limit
	 *            0=data esatta, 1=dal 2=al
	 */
	public void addActionErrorDoubleRequired(String localstringKey, int limit) {
		String name = this.getI18nLabel(localstringKey);
		switch (limit) {
		case A_PARTIRE_DA:
			name += " " + this.getI18nLabel("LABEL_A_PARTIRE_DA");
			break;
		case FINO_A:
			name += " " + this.getI18nLabel("LABEL_FINO_A");
			break;
		default:
			break;
		}
		this.addActionError(this.getText("Errors.field.required",
				new String[] { name }));
	}

	/**
	 * Aggiunge un messaggio d'errore localizzato in lingua riportante il nome
	 * del campo importo errato ed il valore.
	 * 
	 * @param localstringKey
	 *            etichetta in LOCALSTRINGS che individua il nome del campo importo 
	 * @param limit
	 *            0=importo esatto, 1=a partire da 2=fino a
	 * @param value
	 *            valore errato inserito
	 */
	public void addActionErrorDoubleInvalid(String localstringKey, int limit, String value) {
		String name = this.getI18nLabel(localstringKey);
		switch (limit) {
		case A_PARTIRE_DA:
			name += " " + this.getI18nLabel("LABEL_A_PARTIRE_DA");
			break;
		case FINO_A:
			name += " " + this.getI18nLabel("LABEL_FINO_A");
			break;
		default:
			break;
		}
		this.addActionError(this.getText("Errors.field.invalid",
				new String[] { name, value }));
	}

	/**
	 * Aggiunge un messaggio d'errore localizzato in lingua riportante il nome
	 * del campo errato ed il valore.
	 * 
	 * @param localstringKey
	 *            etichetta in LOCALSTRINGS che individua il nome del campo data
	 * @param errorType
	 *            1=IS_INVALID (default), 2=IS_REQUIRED
	 * @param value
	 *            valore errato inserito 
	 */
	public void addActionError(String localstringKey, int errorType, String value) {
		String name = this.getI18nLabel(localstringKey);
		String msg = "";
		switch (errorType) {
		case IS_INVALID: 
			msg = this.getText("Errors.field.invalid", new String[] { name, value });
			break;
			
		case IS_REQUIRED:
			msg = this.getText("Errors.field.required", new String[] { name });
			break;
			
		default:
			msg = this.getText("Errors.field.invalid", new String[] { name, value });
			break;
		}
		this.addActionError(msg);
	}
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this._request = request;
	}

	public HttpServletRequest getRequest() {
		return _request;
	}

	protected ILangManager getLangManager() {
		return _langManager;
	}

	public void setLangManager(ILangManager langManager) {
		this._langManager = langManager;
	}

	@Deprecated
	protected IAuthorizationManager getAuthManager() {
		return _authorizationManager;
	}

	protected IAuthorizationManager getAuthorizationManager() {
		return _authorizationManager;
	}

	public void setAuthorizationManager(IAuthorizationManager authorizationManager) {
		this._authorizationManager = authorizationManager;
	}

	public II18nManager getI18nManager() {
		return _i18nManager;
	}
	
	public void setI18nManager(II18nManager i18nManager) {
		_i18nManager = i18nManager;
	}
	
	private transient ILangManager _langManager;

	private transient IAuthorizationManager _authorizationManager;

	private transient II18nManager _i18nManager;

	private transient HttpServletRequest _request;
	
	private Map<String, String[]> _params;

	public static final String FAILURE 			= "failure";
	
	public static final String USER_NOT_ALLOWED = "userNotAllowed";
	
	/**
	 * In ambiente cluster (Redis) le istanze dei manager non possono essere serializzate/deserializzate 
	 */
	private void readObject(java.io.ObjectInputStream stream) throws ClassNotFoundException, IOException {
		stream.defaultReadObject();
		try {
			ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
			_langManager = (ILangManager) ctx.getBean(SystemConstants.LANGUAGE_MANAGER);
			_authorizationManager = (IAuthorizationManager) ctx.getBean(SystemConstants.AUTHORIZATION_SERVICE);
			_i18nManager = (II18nManager) ctx.getBean(SystemConstants.I18N_MANAGER);
			//_request = ???
		} catch (Exception e) {
		}
	}

	/**
	 * restituisce il valore del codice della stazione appaltante impostata 
	 * come filtro della webapp  
	 */
	public String getCodiceStazioneAppaltante() {
		return StringUtils.stripToNull((String)this._request.getSession()
			.getAttribute(PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE));
	}

	public void setCodiceStazioneAppaltante(String stazioneAppaltante) {
		SAFilter.setStazioneAppaltante(this._request, stazioneAppaltante);
	}

	/**
	 * restituisce il valore del codice fiscale della stazione appaltante impostata 
	 * come filtro della webapp  
	 */
	public String getCodiceFiscaleStazioneAppaltante() {
		return (String)this._request.getSession()
			.getAttribute(PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE_CF);
	}
	
	/**
	 * restituisce il valore della denominazione della stazione appaltante impostata 
	 * come filtro della webapp  
	 */
	public String getDescStazioneAppaltante() {
		return (String)this._request.getSession()
			.getAttribute(PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE_DESC);
	}

	/**
	 * Verifica se il comando di invio &egrave; bloccato in quanto
	 * amministratore loggato come operatore economico, oppure no.
	 * 
	 * @return true se la sessione &egrave; relativa all'utente amministratore
	 *         loggato come operatore economico, false altrimenti
	 */
	public boolean isSendBlocked() {
		boolean blocked = false;
		if (_request.getSession().getAttribute(
				PortGareSystemConstants.SESSION_ID_SENTINELLA_LOGIN_AS) != null) {
			blocked = true;
		}
		return blocked;
	}

	/**
	 * Restituisce l'url della pagina corrente
	 * 
	 * NB: utilizzato ad esempio per il parametro urlPage delle action di generazione dei PDF 
	 */
	public String getCurrentPageUrl() {
		String uri = (String) _request.getAttribute("javax.servlet.forward.request_uri");
		if (uri == null)
			return _request.getRequestURL().toString();
		String scheme = _request.getScheme();
		String serverName = _request.getServerName();
		int serverPort = _request.getServerPort();
		return scheme + "://" + serverName + ":" + serverPort + uri;
	}
		
	/**
	 * Restituisce ... 
	 * NB: Da considerarsi astratto
	 */
	protected Map<String, String> getTextAndElementAndHtmlID() {
		return null;
	}
	
	protected void actionErrorToFieldError() {
		Map<String, String> textAndElementID = getTextAndElementAndHtmlID();	//Errore + IdHtml
		if (MapUtils.isNotEmpty(textAndElementID)) {
			Map<String, List<String>> empty = getActionErrors().stream().collect(Collectors.groupingBy(groupByHtmlId(textAndElementID)));
			// Devo utilizzare perforza questo metodo perche' eliminare i campi a mano della lista non funziona
			clearActionErrors();
			List<String> toKeep = empty.remove("EMPTY");
			// Non faccio un clear perche' non tutti gli errori sono collegati alla compilazione del form
			if (CollectionUtils.isNotEmpty(toKeep))	
				toKeep.forEach(this::addActionError);

			empty.forEach((htmlId, errors) -> errors.forEach(error -> addFieldError(htmlId, error)));
		}
	}

	private Function<String, String> groupByHtmlId(Map<String, String> textAndElementID) {
		return error -> textAndElementID.getOrDefault(error, "EMPTY");
	}

	/**
	 * A causa di problemi di sessione, in caso di maschere di ricerca sulla stessa pagina, ma
	 * diversa showlet, non era possibile cercare utilizzando dei filtri su nessuno dei 2 form.
	 *
	 * Questo metodo inserisce un attributo in sessione, in caso questo non attributo non esista
	 * e se esiste ritorna true
	 *
	 * In caso questa funzione ritorni "true", è necessario non inserire in sessione la pagina
	 * di provenienza:
	 * ES:
	 * 	if (!isMultipleSearchInRequest() {
	 * 		this.session.put(PortGareSystemConstants.SESSION_ID_FROM_SEARCH, fromSearch);
	 * 		this.session.put(PortGareSystemConstants.SESSION_ID_FROM_PAGE, fromPage);
	 * 		this.session.put(PortGareSystemConstants.SESSION_ID_FROM_PAGE_OWNER, FROM_PAGE_OWNER);
	 * 	}
	 *
	 * 	Inoltre bisogna prevenire il form di ricerca dal utilizzare i dati presenti in sessione.
	 * 	ES:
	 * 	if(!isMultipleSearchInRequest() && finder != null && fromPage != null) {
	 * 		String lastFrom = (String)this.session.get(PortGareSystemConstants.SESSION_ID_FROM_PAGE_OWNER) +
	 * 			              (String)this.session.get(PortGareSystemConstants.SESSION_ID_FROM_PAGE);
	 * 		if(!(FROM_PAGE_OWNER + fromPage).equalsIgnoreCase(lastFrom)) {
	 * 			this.model.restoreFrom(finder);
	 *      }
	 *	}
	 *
	 * Questo metodo non risolve completamente i problemi causati da un doppio form di ricerca,
	 * ma almeno permette la ricerca.
	 *
	 * @return true = ci sono più form di ricerca sulla stessa pagina.
	 */
	protected boolean isMultipleSearchInRequest() {
		boolean toReturn = false;

		if (getRequest().getAttribute("COUNT_SEARCH_IN_REQUEST") == null)
			getRequest().setAttribute("COUNT_SEARCH_IN_REQUEST", 1);
		else
			toReturn = true;

		return toReturn;
	}

	/**
	 * effettua il lock per l'accesso ad un determinatp flusso (offerta, iscrizione elenco, ...)
	 */
	protected boolean lockAccessoFunzione(EFlussiAccessiDistinti flusso, String codice) {
		boolean lockOk = false; 
		
		if(AccountSSO.isAccessiDistinti()  
		   && null != this.getCurrentUser() 
		   && !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME))
		{
			ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
			IEventManager eventManager = (IEventManager) ctx.getBean(PortGareSystemConstants.EVENTI_MANAGER);
			IUserManager userManager = (IUserManager) ctx.getBean(SystemConstants.USER_MANAGER);
			
			synchronized (this) {
				// recupera le info del soggetto impresa
				AccountSSO accessoSSO = AccountSSO.getFromSession();
				
				// fai una copia del flusso attuale prima di aggiornarlo... 
				// nel caso il lock non sia possibile, va ripristinato!!!
				String oldFlusso = (accessoSSO != null && accessoSSO.getProfilo() != null ? accessoSSO.getProfilo().getFlusso() : null);
				
				Event evento = null;
				try {
					String delegate = (StringUtils.isNotEmpty(accessoSSO.getProfilo().getDelegate()) 
					   		   		   ? accessoSSO.getProfilo().getDelegate() : "");
					
					String funzione = flusso.toString() + 
									  (StringUtils.isNotEmpty(codice) ? "-" + codice : "");	 // lock a livello di (funzione, gara)
					
					accessoSSO.getProfilo().setFlusso(funzione);
				
					// esegui il LOCK sul flusso corrente se ci sono i permessi necessari...
					boolean tryLock = true;
					List<DelegateUser> lockAttivi = userManager.loadProfiliSSOAccesses(this.getCurrentUser().getUsername());
					if(CollectionUtils.isNotEmpty(lockAttivi)) {
						tryLock = lockAttivi.stream()
								.filter(a -> delegate.equalsIgnoreCase(a.getDelegate()))
								.map(DelegateUser::getFlusso)
								.noneMatch(f -> funzione.equalsIgnoreCase(f));
					}
					
					if( !tryLock ) {
						// esiste gia' un LOCK attivo per questo flusso, quindi non serve rieseguirlo
						lockOk = true;
					} else {
						// non ci sono LOCK per questo flusso...
						if(hasPermessiCompilazioneFlusso() || hasPermessiInvioFlusso()) {
							evento = new Event();
							evento.setUsername(this.getCurrentUser().getUsername());
							evento.setDestination(codice);
							evento.setIpAddress(this.getCurrentUser().getIpAddress());
							evento.setSessionId(this.getCurrentUser().getSessionId());
							evento.setLevel(Event.Level.INFO);
							
							// esegui un tentativo di LOCK sulla funzione
							DelegateUser lock = userManager.lockProfiloSSOAccess(
									this.getCurrentUser(),	
									accessoSSO.getProfilo().getDelegate(),
									funzione);	
							
							if(lock == null) {
								// lock effettuato con successo, ricarica il profilo SSO aggiornato
								lockOk = true;
								accessoSSO.loadProfilo(this.getCurrentUser().getUsername());
								evento.setEventType(PortGareEventsConstants.LOCK);
								evento.setMessage("Lock effettuato per l'accesso a " + flusso.name() + 
												  (StringUtils.isNotEmpty(codice) ? " " + codice : "") +
												  " dall'utente " + this.getCurrentUser().getUsername() + 
												  " con soggetto impresa " + delegate);
								logger.debug("lockAccessoFunzione => {} {} lock succesfull", flusso.name(), delegate);
							} else {
								// un altro utente ha gia' il lock sulla risorsa
								// mostra il messaggio di accesso multiplo 
								evento.setEventType(PortGareEventsConstants.ACCESSO_FUNZIONE);
								evento.setLevel(Event.Level.ERROR);
								evento.setMessage("L'accesso alla funzionalita' " + flusso.name() + 
												  " viene impedito in quanto il soggetto " + lock.getDelegate() + 
												  " sta operando a partire da " + YYYYMMDD_HHMMSS.format(lock.getLoginTime())); 
								this.addActionError(getText("Errors.cannotLockFunction", 
											 					new String[] { flusso.name(), lock.getDelegate(), YYYYMMDD_HHMMSS.format(lock.getLoginTime()) }));
								logger.debug("lockAccessoFunzione => {} {} locked by another sso user", flusso.name(), delegate);
							}
						} else {
							// il profilo del soggetto impresa non ha i permessi per accedere alla funzione
							evento = new Event();
							evento.setUsername(this.getCurrentUser().getUsername());
							evento.setDestination(codice);
							evento.setIpAddress(this.getCurrentUser().getIpAddress());
							evento.setSessionId(this.getCurrentUser().getSessionId());
							evento.setEventType(PortGareEventsConstants.ACCESSO_FUNZIONE);
							evento.setLevel(Event.Level.ERROR);
							evento.setMessage("Accesso alla funzione " + flusso.name() + " non disponibile" +
											  " per mancanza di permessi per l'utente " + this.getCurrentUser().getUsername() + 
											  " e soggetto impresa " + delegate);
							this.addActionError(getText("Errors.noAccessPermissions", 
					 									new String[] { flusso.name(), delegate, this.getCurrentUser().getUsername() }));
							logger.debug("lockAccessoFunzione => {} {} sso user has no permissions", flusso.name(), delegate);
						}
					}
					
				} catch (Throwable t) {
					ApsSystemUtils.logThrowable(t, this, "lockAccessoFunzione");
					ExceptionUtils.manageExceptionError(t, this);
				} finally {
					if (evento != null) {
						eventManager.insertEvent(evento);
					}
				}
				
				// nel caso il lock non sia possibile, ripristina il precedente flusso!!! 
				if( !lockOk ) {
					if(accessoSSO != null && accessoSSO.getProfilo() != null)
						accessoSSO.getProfilo().setFlusso(oldFlusso);
				}
			}
		} else {
			// gestione soggetti impresa con SSO NON ATTIVA!!!
			// il default e' "lock con successo"
			lockOk = true;
		}
		
		return lockOk;
	}
	
	/**
	 * effettua l'unlock per l'accesso alla funzione 
	 */
	public boolean unlockAccessoFunzione() {
		boolean unlocked = false;

		AccountSSO accessoSSO = AccountSSO.getFromSession();
		boolean isProfiloSSO = (accessoSSO != null && accessoSSO.getProfilo() != null); 

		if(isProfiloSSO
		   && null != this.getCurrentUser() 
		   && !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME))
		{ 
			ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
			IEventManager eventManager = (IEventManager) ctx.getBean("EventManager");
			IUserManager userManager = (IUserManager) ctx.getBean("UserManager");

			Event evento = null;
			try {
				String delegate = (StringUtils.isNotEmpty(accessoSSO.getProfilo().getDelegate()) 
		   		   		   		   ? accessoSSO.getProfilo().getDelegate() : "");
				String flusso = AccountSSO.getFlussoAccessiDistinti(accessoSSO);
				String codice = AccountSSO.getCodiceAccessiDistinti(accessoSSO);
				
				// verifica se e' necessatrio un UNLOCK...
				long lockCount = 0;
				List<DelegateUser> lockAttivi = userManager.loadProfiliSSOAccesses(this.getCurrentUser().getUsername());
				if(CollectionUtils.isNotEmpty(lockAttivi)) {
					lockCount = lockAttivi.stream()
							.filter(a -> delegate.equalsIgnoreCase(a.getDelegate()))
							.filter(a -> !EFlussiAccessiDistinti.LOGIN.toString().equalsIgnoreCase(a.getFlusso()))	// tra i lock attivi va ignorata la "funzione" LOGIN !!! 
							.map(DelegateUser::getFlusso)
							.count();
				}

				// esegui l'UNLOCK solo se necessario
				// NB: un soggetto impresa esegue l'unlock dell'unica funziona che puo' aver aperto
				if(lockCount > 0) {
					evento = new Event();
					evento.setUsername(this.getCurrentUser().getUsername());
					evento.setDestination(codice);
					evento.setIpAddress(this.getCurrentUser().getIpAddress());
					evento.setSessionId(this.getCurrentUser().getSessionId());
					evento.setEventType(PortGareEventsConstants.UNLOCK);
					evento.setMessage("Unlock effettuato per l'accesso a " + flusso +
							  		  (StringUtils.isNotEmpty(codice) ? " " + codice : "") +
							  		  " dall'utente " + this.getCurrentUser().getUsername() + 
							  		  " con soggetto impresa " + delegate);
					evento.setLevel(Event.Level.INFO);
	
					unlocked = userManager.unlockProfiloSSOAccess(
							this.getCurrentUser(),	
							accessoSSO.getProfilo().getDelegate()
					);
					
					if(unlocked) {
				    	logger.debug("unlockAccessoFunzione => unlock {} per {} {}", flusso, accessoSSO.getLogin(), delegate);
				    	accessoSSO.getProfilo().setFlusso(null);
					}
				}
			} catch (Throwable t) {
				if(evento != null) {
					evento.setLevel(Event.Level.ERROR);
					evento.setDetailMessage(t);
				}
				ApsSystemUtils.logThrowable(t, this, "unlockAccessoFunzione");
				ExceptionUtils.manageExceptionError(t, this);
			} finally {
				if (evento != null) {
					eventManager.insertEvent(evento);
				}
			}
		} else {
			// quando NON c'e' la gestione dei profili per piu' soggetti impresa con SSO
			// il default e' "unlock con successo" 
			unlocked = true;
		}
		
		return unlocked;
	}
	
	/**
	 * verifica se un utente/soggetto impresa ha i permetti per accedere/inviare 
	 * in base al ruolo definito definito (es: Sola lettura, Istruttore, Completo) 
	 */
	private boolean hasPermessiAccessiDistinti(boolean invio) {
		boolean continua = true;
		if(null != this.getCurrentUser() 
		   && !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			try {
				// la verifica sul profilo viene eseguita solo per l'utente loggato !!!
				continua = false;
				
				AccountSSO sso = AccountSSO.getFromSession();
				
				DelegateUser.Accesso ruolo = AccountSSO.getRuoloAccessiDistinti(sso) ;
				String flusso = AccountSSO.getFlussoAccessiDistinti(sso);
				
				if(StringUtils.isEmpty(flusso)) {
					// se non c'e' flusso, non ci sono restrizioni
					continua = true;
				} else {
					// se c'e' flusso, devono esserci dei permessi
					DelegateUser.Accesso[] accessi = (invio
							? EFlussiAccessiDistinti.PERMESSI_INVIO.get(flusso)
							: EFlussiAccessiDistinti.PERMESSI_COMPILAZIONE.get(flusso)
					);
					
					if(accessi != null)
						continua = Arrays.asList(accessi).contains(ruolo);
				}
			} catch (Throwable t) {
				ApsSystemUtils.logThrowable(t, this, "hasPermessiAccessiDistinti");
				ExceptionUtils.manageExceptionError(t, this);
			}
		}
		return continua;
	}
	
	protected boolean hasPermessiCompilazioneFlusso() {
		return hasPermessiAccessiDistinti(false);
	}
	
	protected boolean hasPermessiInvioFlusso() {
		return hasPermessiAccessiDistinti(true);
	}
	
	protected boolean hasPermessiSolaLettura() {
		boolean readonly = false;
		if(null != this.getCurrentUser() 
		   && !this.getCurrentUser().getUsername().equals(SystemConstants.GUEST_USER_NAME)) 
		{
			if(AccountSSO.isAccessiDistinti()) {
				try {
					 // la verifica sul profilo viene eseguita solo per l'utente loggato !!!
					AccountSSO sso = AccountSSO.getFromSession();
					DelegateUser.Accesso ruolo = AccountSSO.getRuoloAccessiDistinti(sso);
					readonly = (ruolo == DelegateUser.Accesso.READONLY);
				} catch (Throwable t) {
					ApsSystemUtils.logThrowable(t, this, "hasPermessiSolaLettura");
					ExceptionUtils.manageExceptionError(t, this);
				}
			}
		}
		return readonly;
	}

	/**
	 * aggiunge l'errore di mancanza di permessi di accesso per l'utente/soggetto impresa
	 */
	protected void addActionErrorSoggettoImpresaPermessiAccessoInsufficienti() {
		AccountSSO sso = AccountSSO.getFromSession();
		String flusso = (StringUtils.isNotEmpty(sso.getProfilo().getFlusso()) ? sso.getProfilo().getFlusso() : "");
		String delegate = (StringUtils.isNotEmpty(sso.getProfilo().getDelegate()) ? sso.getProfilo().getDelegate() : "");
		addActionError(getText("Errors.noAccessPermissions", new String[] { flusso, delegate, getCurrentUser().getUsername() }));
	}
		
	/**
	 * verifica se il profilo del soggetto impresa ha aperto un flusso e se e' compatibile con quelli previsti  
	 */
	public boolean isFlussoValid(AccountSSO accessoSSO) {
		boolean valid = true;
		
		if(accessoSSO.getProfilo() != null) {
			// estrae dalla action le annotazioni dei i flussi previsti
			List<String> flussiAmmessi = null;
			try {
				Annotation[] classAnnotations = getClass().getAnnotations();
				for (Annotation item : classAnnotations) {
					if (item instanceof FlussiAccessiDistinti) {
						FlussiAccessiDistinti annotation = (FlussiAccessiDistinti)item;
						flussiAmmessi = new ArrayList<String>();
						flussiAmmessi.addAll(Arrays.asList(annotation.value()).stream()
							.map(EFlussiAccessiDistinti::name)
							.collect(Collectors.toList()));
					}
				}
				
				logger.debug("isFlussoValid => " + getClass().toString() + 
							 (flussiAmmessi != null ? "[ " + flussiAmmessi.toString() + " ]" : ""));
			} catch (Exception e) {
				ApsSystemUtils.logThrowable(e, null, "isFlussoValid");
			}

			// estrai il flusso corrente
			String flusso = null;
	    	if(StringUtils.isNotEmpty(accessoSSO.getProfilo().getFlusso())) {
	    		String[] v = accessoSSO.getProfilo().getFlusso().split("-");
	    		flusso = (v != null ? v[0] : accessoSSO.getProfilo().getFlusso());
	    	}
	    	
	    	// verifica se il flusso corrente e' compatibile con i flussi previsti dalla action
	    	// - se non e' attivo un flusso e sono in una action senza flussi previsti => flusso valido
	    	// - se e' attivo un flusso ma sono in una action senza flussi previsti => flusso non valido
	    	// - se non e' attivo un flusso ma sono in una action con flussi previsti => flusso non valido
	    	// - se attivo un flusso e sono in una action con flussi previsti => flusso se e' contenuto nei flussi previsti
			valid = (StringUtils.isEmpty(flusso) && flussiAmmessi == null);
			if(StringUtils.isNotEmpty(flusso) && flussiAmmessi != null) {
		    	valid = flussiAmmessi.contains(flusso);
			}
			
			logger.debug("isFlussoValid => {} IN {} ? ==> {}",  
						 (flusso != null ? flusso : ""), (flussiAmmessi != null ? flussiAmmessi.toString() : "[]"), valid);
		}
		
		return valid;
	}

	/**
	 * verifica se il profilo del soggetto impresa ha ancora il lock attivo su un flusso
	 * oppure l'OWNER lo ha revocato 
	 */
	public synchronized boolean isLockRevoked(AccountSSO accessoSSO) {
		boolean attivo = true;
		if(accessoSSO.getProfilo() != null) {
			// se il soggetto impresa aveva aperto un lock su un flusso...
			// verifica se il lock e' ancora attivo o se e' stato revocato 
			String username = this.getCurrentUser().getUsername();
			String flusso = accessoSSO.getProfilo().getFlusso();
			if(StringUtils.isNotEmpty(flusso))
				if(logger.isDebugEnabled()) {
					//log.debug("intercept '{}/{}' { ", invocation.getProxy().getNamespace(), invocation.getProxy().getActionName());
					logger.debug("isLockRevoked {} {} {} => {} aggiornamento profilo da DB"
								 , (accessoSSO.getLogin() != null ? accessoSSO.getLogin() : "") 
								 , accessoSSO.getProfilo().getDelegate()
								 , accessoSSO.getProfilo().getFlusso()
								 , getClass().getName()
					);
				
				// aggiorna i dati della sessione del soggetto impresa...
				DelegateUser profilo = accessoSSO.loadProfilo(username);
				if(profilo != null) {
					attivo = (StringUtils.isNotEmpty(profilo.getFlusso()) 								// deve esserci un flusso attivo
							  && (profilo.getFlusso().equalsIgnoreCase(flusso))							// il flusso corrente deve essere lo stesso dell'ultima action
							  && (profilo.getLoginTime() != null && profilo.getLogoutTime() == null)	// deve esserci un login al flusso e un logout nullo
					);
				}
			}
		}
		// il lock e' revocato se NON e' piu' attivo !!!
		return !attivo;
	}
	 	
	/**
	 * Metodo di utilità per la pulizia del codice.
	 * @param message
	 */
	public void addErrorAndLog(String message) {
		super.addActionError(message);
		logger.error(message);
	}

	public void addMessageAndLog(String message) {
		super.addActionMessage(message);
		logger.debug(message);
	}
		
	/** ******************************************************************************
	 * validatori per l'upload di un documento (PORTAPPALT-1089 - validazione formati)
	 */
	protected UploadValidator uploadValidator;
	
	protected UploadValidator getUploadValidator() {
		uploadValidator = (uploadValidator != null ? uploadValidator : new UploadValidator(this));
		return uploadValidator;
	}

	public Integer getLimiteUploadFile() {
		return getUploadValidator().getLimiteUploadFile();
	}
	
	public Integer getLimiteTotaleUpload() {
		return getUploadValidator().getLimiteTotaleUploadFile();
	}
	
	public List<String> getInfoEstensioniAmmesse() {
		return getUploadValidator().getInfoEstensioniAmmesse(); 
	}
	
	protected boolean checkFileDescription(String documentDescription, Event event) {
    	return getUploadValidator().checkFileDescription(documentDescription, event);
    }
	
	protected boolean checkFileName(String documentFileName, Event event) {
		return getUploadValidator().checkFileName(documentFileName, event);
	}
	
	protected boolean checkFileExtension(String documentFileName, Event event) {
		return getUploadValidator().checkFileExtension(documentFileName, event);
	}
	
	protected boolean checkFileSize(File document, String documentFileName, int actualTotalSize, Event event) {
		return getUploadValidator().checkFileSize(document, documentFileName, actualTotalSize, event);
	}
	
	protected boolean checkFileSize(int documentSize, String documentFileName, int actualTotalSize, Event event) {
		return getUploadValidator().checkFileSize(documentSize, documentFileName, actualTotalSize, event);
	}
	
	protected boolean checkFileFormat(File document, String documentFileName, Integer format, boolean onlyP7m, Event event) {
		return getUploadValidator().checkFileFormat(document, documentFileName, format, onlyP7m, event);
	}
	
	protected DocumentiAllegatiFirmaBean checkFileSignature(File document, String documentFileName, Integer format, Date checkDate, boolean onlyP7m, Event event) throws ApsSystemException {
		return getUploadValidator().checkFileSignature(document, documentFileName, format, checkDate, onlyP7m, event);
	}

}
