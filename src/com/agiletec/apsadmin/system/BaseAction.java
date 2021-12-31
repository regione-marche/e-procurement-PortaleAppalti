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

import it.eldasoft.utils.sign.DigitalSignatureChecker;
import it.eldasoft.utils.sign.DigitalSignatureException;
import it.maggioli.eldasoft.plugins.ppcommon.aps.SAFilter;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.events.Event;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.StringUtilities;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ParameterAware;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.authorization.IAuthorizationManager;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.i18n.II18nManager;
import com.agiletec.aps.system.services.lang.ILangManager;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.system.services.user.UserDetails;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.LocalizedTextUtil;

/**
 * Class beneath all actions.
 *
 * @author E.Santoboni
 */
public class BaseAction extends ActionSupport implements ServletRequestAware, ParameterAware {

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
	 * Check if the current user belongs to the given group. It always returns
	 * true if the user belongs to the Administrators group.
	 *
	 * @param groupName The name of the group to check against the current user.
	 * @return true if the user belongs to the given group, false otherwise.
	 */
	protected boolean isCurrentUserMemberOf(String groupName) {
		UserDetails currentUser = this.getCurrentUser();
		IAuthorizationManager authManager = this.getAuthorizationManager();
		boolean isAuth = authManager.isAuthOnGroup(currentUser, groupName) || authManager.isAuthOnGroup(currentUser, Group.ADMINS_GROUP_NAME);
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

	protected Map<String, String[]> getParameters() {
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
			ApsSystemUtils.getLogger().info("Lingua richiesta ''{}'' non definita nel sistema", langCode);
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
	 * 
	 * @return true se i controlli sono andati a buon fine, false altrimenti
	 */
	protected boolean checkFileFormat(File document, String documentFileName,
			Integer format, Event event, boolean onlyP7m) {
		return checkFileFormat(document, documentFileName, format, new Date(), event, onlyP7m);
	}

	/**
	 * Verifica il formato del file in input.
	 *
	 * @param document file allegato
	 * @param documentFileName nome del file allegato
	 * @param format formato previsto dal servizio di backoffice
	 * @param checkDate data di verifica della validita' della firma 
	 * @param event evento da modificare in caso di errore per la tracciatura eventi
	 * 
	 * @return true se i controlli sono andati a buon fine, false altrimenti
	 */
	protected boolean checkFileFormat(File document, String documentFileName,
					Integer format, Date checkDate, Event event, boolean onlyP7m) {
		
		boolean controlliOk = true;
		if (documentFileName != null && (format == null || format == PortGareSystemConstants.DOCUMENTO_FORMATO_QUALSIASI)) {
			if (documentFileName.toUpperCase().endsWith(".P7M")) {
				format = PortGareSystemConstants.DOCUMENTO_FORMATO_FIRMATO;
			}
		}
		if (documentFileName != null && format != null) {
			switch (format) {
				case PortGareSystemConstants.DOCUMENTO_FORMATO_FIRMATO:
					// come file firmati si considerano .p7m, .tsd, .pdf, .xml
					// NB: per .p7m e .tsd e' possibible verificare la firma digitale
					//     mentre per .pdf, .xml si visualizza a video un messaggio
					//     di notifica per "l'accettazione di un documento" che
					//     non ha firma digitale
					boolean isP7m = documentFileName.toUpperCase().endsWith(".P7M");
					boolean isTsd = documentFileName.toUpperCase().endsWith(".TSD");
					boolean isPdf = documentFileName.toUpperCase().endsWith(".PDF");
					boolean isXml = documentFileName.toUpperCase().endsWith(".XML");
					
					if(onlyP7m && !isP7m ){
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
						if(isP7m) {
							controlliOk = checkDocumentP7m(document, documentFileName, event);
						}												
						if(isTsd) {
							controlliOk = checkDocumentTsd(document, documentFileName, checkDate, event);
						}
						if(isPdf) {														
							controlliOk = checkDocumentPdf(document, documentFileName, event);
							this.addActionMessage(this.getText("Warning.signValidationNotSupported"));
						}
						if(isXml) {
							controlliOk = checkDocumentXml(document, documentFileName, event);
							this.addActionMessage(this.getText("Warning.signValidationNotSupported"));
						}
					}					
					break;
					
				case PortGareSystemConstants.DOCUMENTO_FORMATO_PDF:
					if (!documentFileName.toUpperCase().endsWith(".PDF")) {
						this.addActionError(this.getText("Errors.pdfRequired"));
						if (event != null) {
							event.setLevel(Event.Level.ERROR);
							event.setDetailMessage("File " + documentFileName
									+ " scartato, richiesto file PDF");
						}
						controlliOk = false;
					}
					break;
					
				case PortGareSystemConstants.DOCUMENTO_FORMATO_EXCEL:
					if (!documentFileName.toUpperCase().endsWith(".XLS")
									&& !documentFileName.toUpperCase().endsWith(".XLSX")
									&& !documentFileName.toUpperCase().endsWith(".ODS")) {
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
		return controlliOk;
	}
	
	/**
	 * verifica la validita' di un documento in formato .p7m
	 */
	private boolean checkDocumentP7m(File document, String documentFileName, Event event) {
		boolean controlliOk = true;		
		FileInputStream file = null;
		try {
			DigitalSignatureChecker checker = new DigitalSignatureChecker();
			file = new FileInputStream(document);
			boolean signVerified = checker.verifySignature(IOUtils.toByteArray(file));
			if (!signVerified) {
				this.addActionError(this.getText("Errors.invalidSign"));
				if (event != null) {
					event.setLevel(Event.Level.ERROR);
					event.setDetailMessage("Il file " + documentFileName
							+ " non contiene una firma digitale valida");
				}
				controlliOk = false;
			}
		} catch (FileNotFoundException e) {
			this.addActionError(this.getText("Errors.fileNotSet"));
			if (event != null) {
				event.setError(e);
			}
			controlliOk = false;
		} catch (DigitalSignatureException e) {
			this.addActionError(this.getText("Errors.cannotVerifySign"));
			if (event != null) {
				event.setLevel(Event.Level.ERROR);
				event.setDetailMessage("Impossibile verificare la firma digitale per il file "
						+ documentFileName 
						+ ": " + e.getMessage());
			}
			controlliOk = false;
		} catch (IOException e) {
			this.addActionError(this.getText("Errors.fileNotSet"));
			if (event != null) {
				event.setError(e);
			}
			controlliOk = false;
		} finally {
			if (file != null) {
				try {
					file.close();
				} catch (IOException ex) {
				}
			}
		}
		return controlliOk;
	}
	
	/**
	 * verifica la validita' di un documento in formato .tsd
	 */
	private boolean checkDocumentTsd(File document, String documentFileName, Date checkDate, Event event) {
		boolean controlliOk = true;		
		FileInputStream file = null;
		try {
			DigitalSignatureChecker checker = new DigitalSignatureChecker();
			file = new FileInputStream(document);
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
			byte[] content = checker.getContentTimeStamp(stream);
			if (content != null) {
				byte[] xmlTimeStamps = checker.getXMLTimeStamps(content);
				verificaMarcheTemporaliXML = new String(xmlTimeStamps);
			} else {
				content = stream;
			}
			
			// recupera la firma digitale...
	        byte[] xml = checker.getXMLSignature(content, checkDate);
	        String verificaFirmaDigitaleXML = new String(xml);
	        
	        // verifica la firma digitale...
	        if((verificaFirmaDigitaleXML == null) 
	        	|| (verificaFirmaDigitaleXML != null && verificaFirmaDigitaleXML.isEmpty())	) {
				this.addActionError(this.getText("Errors.invalidSign"));
				if (event != null) {
					event.setLevel(Event.Level.ERROR);
					event.setDetailMessage("Il file " + documentFileName
							+ " non contiene una firma digitale valida");
				}
				controlliOk = false;
	        }
	        
	        // verifica la marcatura temporale...
	        if((verificaMarcheTemporaliXML == null) 
		        || (verificaMarcheTemporaliXML != null && verificaMarcheTemporaliXML.isEmpty())	) {
//	        	this.addActionError(this.getText("Errors.invalid???"));
//				if (event != null) {
//					event.setLevel(Event.Level.ERROR);
//					event.setDetailMessage("Il file " + documentFileName
//							+ " non contiene una marcatura temporale valida");
//				}
	        }	        

		} catch (DigitalSignatureException e) {
			this.addActionError(this.getText("Errors.cannotVerifySign"));
			if (event != null) {
				event.setLevel(Event.Level.ERROR);
				event.setDetailMessage("Impossibile verificare la firma digitale per il file "
						+ documentFileName 
						+ ": " + e.getMessage());
			}
			controlliOk = false;			
		} catch (FileNotFoundException e) {
			this.addActionError(this.getText("Errors.fileNotSet"));
			if (event != null) {
				event.setError(e);
			}
			controlliOk = false;
		} catch (IOException e) {
			this.addActionError(this.getText("Errors.fileNotSet"));
			if (event != null) {
				event.setError(e);
			}
			controlliOk = false;
		} finally {
			if (file != null) {
				try {
					file.close();
				} catch (IOException ex) {
				}
			}
		}
		return controlliOk;
	}

	/**
	 * verifica la validita' di un documento in formato .pdf
	 */
	private boolean checkDocumentPdf(File document, String documentFileName, Event event) {
		boolean controlliOk = true;		
//		FileInputStream file = null;
//		try {
//			// recupera dal file PDF il parametro "Keywords" per verificare 
//			// se esiste un UUID assegnato che sia valido...
////			DigitalSignatureChecker checker = new DigitalSignatureChecker();
////			file = new FileInputStream(document);
////			byte[] contenutoFile = checker.getContent(IOUtils.toByteArray(file));
//			file = new FileInputStream(document);
//			byte[] contenutoFile = IOUtils.toByteArray(file);
//			PdfReader readInputPDF = new PdfReader(contenutoFile);
//			@SuppressWarnings("unchecked")
//			HashMap<String, String> hMap = readInputPDF.getInfo();
//			if (StringUtils.isEmpty(hMap.get("Keywords"))) {
//				this.addActionError(this.getText("Errors.invalidUUID"));
//				if (event != null) {
//					event.setLevel(Event.Level.ERROR);
//					event.setDetailMessage("Il file " + documentFileName
//							+ " non contiene un UUID valido");
//				}
//				controlliOk = false;
//			}
//		} catch (FileNotFoundException e) {
//			this.addActionError(this.getText("Errors.fileNotSet"));
//			if (event != null) {
//				event.setError(e);
//			}
//			controlliOk = false;
////		} catch (DigitalSignatureException e) {
////			this.addActionError(this.getText("Errors.cannotVerifyUUID"));
////			if (event != null) {
////				event.setLevel(Event.Level.ERROR);
////				event.setDetailMessage("Impossibile verificare UUID per il file "
////						+ documentFileName
////						+ ": " + e.getMessage());
////			}
////			controlliOk = false;
//		} catch (IOException e) {
//			this.addActionError(this.getText("Errors.fileNotSet"));
//			if (event != null) {
//				event.setError(e);
//			}
//			controlliOk = false;
//		} finally {
//			if (file != null) {
//				try {
//					file.close();
//				} catch (IOException ex) {
//				}
//			}
//		}
		return controlliOk;
	}

	/**
	 * verifica la validita' di un documento in formato .xml
	 */
	private boolean checkDocumentXml(File document, String documentFileName, Event event) {
		boolean controlliOk = true;		
		
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
	private Object estraiContenutoDocumentoFirmato(
			File document, String documentFileName, boolean contenuto, Event event) {
		byte[] content = null;
		String contentFilename = null;
		FileInputStream file = null;		
		try {			
			DigitalSignatureChecker checker = null;
			byte[] stream = null;
			if(contenuto) {
				checker = new DigitalSignatureChecker();
				file = new FileInputStream(document);
				stream = IOUtils.toByteArray(file);
			}
	
			contentFilename = documentFileName;
			while (contentFilename != null && contentFilename.length() > 0) {
				boolean isP7m = contentFilename.toUpperCase().endsWith(".P7M");
				boolean isTsd = contentFilename.toUpperCase().endsWith(".TSD");
				boolean isPdf = contentFilename.toUpperCase().endsWith(".PDF");
				boolean isXml = contentFilename.toUpperCase().endsWith(".XML");
				
				// recupera il contenuto...
				if(isTsd) {
					if(contenuto && stream != null) { 
						byte[] stm = checker.getContentTimeStamp(stream);
						if (stm != null) {
							stream = stm;
						}
					}
				} else if(isP7m) {
					if(contenuto && stream != null) {
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
	        
		} catch (FileNotFoundException e) {
			this.addActionError(this.getText("Errors.fileNotSet"));
			if (event != null) {
				event.setError(e);
			}
			content = null;
			contentFilename = null;
		} catch (IOException e) {
			this.addActionError(this.getText("Errors.fileNotSet"));
			if (event != null) {
				event.setError(e);
			}
			content = null;
			contentFilename = null;
		} catch (DigitalSignatureException e) {
			this.addActionError(this.getText("Errors.cannotVerifySign"));
			if (event != null) {
				event.setLevel(Event.Level.ERROR);
				event.setDetailMessage("Impossibile verificare la firma digitale per il file "
						+ document.getAbsolutePath() 
						+ ": " + e.getMessage());
			}
			content = null;
			contentFilename = null;
		} finally {
			if (file != null) {
				try {
					file.close();
				} catch (IOException ex) {
				}
			}
		}
		
		// restituisci il contenuto o il nomefile del contenuto... 
		return (contenuto ? content : contentFilename);
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
	 * Verifica la dimensione del file di input.
	 *
	 * @param document file allegato
	 * @param documentFileName nome del file allegato
	 * @param actualTotalSize la dimensione totale dei file finora caricati
	 * @param limiteDimensioneSingoloFile la dimensione massima per singolo file
	 * @param limiteDimensioneTotaliFile la dimensione massima per la busta
	 * @param event evento da modificare in caso di errore per la tracciatura eventi
	 * @return true se i controlli sono andati a buon fine, false altrimenti
	 */
	protected boolean checkFileSize(
			File document, 
			String documentFileName, 
			int actualTotalSize,
			int limiteDimensioneSingoloFile,
			int limiteDimensioneTotaliFile,
			Event event) 
	{
		boolean controlliOk = true;
		if (document == null) {
			this.addActionError(this.getText("Errors.fileNotSet"));
			if (event != null) {
				event.setLevel(Event.Level.ERROR);
				event.setDetailMessage("File da caricare non selezionato");
			}
			controlliOk = false;
		} else {
			if (document.length() == 0) {
				this.addActionError(this.getText("Errors.emptyFile"));
				if (event != null) {
					event.setLevel(Event.Level.ERROR);
					event.setDetailMessage("File " + documentFileName
							+ " vuoto");
				}
				controlliOk = false;
			} else {
				// se e' stato allegato un file si controlla che non superi la dimensione massima
				int dimensioneDocumento = FileUploadUtilities.getFileSize(document);
				//int limiteDimensioneSingoloFile = FileUploadUtilities.getLimiteUploadFile(appParamManager);
				if (dimensioneDocumento > limiteDimensioneSingoloFile) {
					this.addActionError(this.getText("Errors.overflowFileSize"));
					if (event != null) {
						event.setLevel(Event.Level.ERROR);
						event.setDetailMessage("Upload del file " + documentFileName
								+ " bloccato in quanto la sua dimensione ("
								+ dimensioneDocumento
								+ " KB) supera il limite per singolo file di "
								+ limiteDimensioneSingoloFile
								+ " KB definito in configurazione");
					}
					controlliOk = false;
				}
				//int limiteDimensioneTotaliFile = FileUploadUtilities.getLimiteTotaleUploadFile(appParamManager);
				if (controlliOk && (dimensioneDocumento + actualTotalSize > limiteDimensioneTotaliFile)) {
					this.addActionError(this.getText("Errors.overflowTotalFileSize"));
					if (event != null) {
						event.setLevel(Event.Level.ERROR);
						event.setDetailMessage("Upload del file " + documentFileName
								+ " bloccato in quanto la sua dimensione ("
								+ dimensioneDocumento
								+ " KB) sommata ai file caricati in precedenza ("
								+ actualTotalSize 
								+ " KB) supera il limite di "
								+ limiteDimensioneTotaliFile
								+ " KB definito in configurazione");
					}
					controlliOk = false;
				}
			}
		}
		return controlliOk;
	}

	/**
	 * Verifica la dimensione del file di input.
	 *
	 * @param document file allegato
	 * @param documentFileName nome del file allegato
	 * @param actualTotalSize la dimensione totale dei file finora caricati
	 * @param appParamManager l'appParamManager per ricavare le costanti di check
	 * @param event evento da modificare in caso di errore per la tracciatura eventi
	 * @return true se i controlli sono andati a buon fine, false altrimenti
	 */
	protected boolean checkFileSize(
			File document, 
			String documentFileName, 
			int actualTotalSize,
			IAppParamManager appParamManager, 
			Event event) 
	{
		int limiteDimensioneSingoloFile = FileUploadUtilities.getLimiteUploadFile(appParamManager);
		int limiteDimensioneTotaliFile = FileUploadUtilities.getLimiteTotaleUploadFile(appParamManager);
		return this.checkFileSize(document, documentFileName, actualTotalSize, limiteDimensioneSingoloFile, limiteDimensioneTotaliFile, event);
	}
	
	/**
	 * Verifica il nome del file di input.
	 *
	 * @param documentFileName nome del file allegato
	 * @param event evento da modificare in caso di errore per la tracciatura eventi
	 * @return true se i controlli sono andati a buon fine, false altrimenti
	 */
	protected boolean checkFileName(String documentFileName, Event event) {
		boolean controlliOk = true;
		if (documentFileName != null) {
			if (documentFileName.length() > FileUploadUtilities.MAX_LUNGHEZZA_NOME_FILE) {
				this.addActionError(this.getText("Errors.overflowFileNameLength"));
				if (event != null) {
					event.setLevel(Event.Level.ERROR);
					event.setDetailMessage("Il nome file " + documentFileName
							+ " supera il limite di "
							+ FileUploadUtilities.MAX_LUNGHEZZA_NOME_FILE
							+ " caratteri");
				}
				controlliOk = false;
			}
			//matcher to find if there is any special character in string
			Pattern regex = Pattern.compile(FileUploadUtilities.INVALID_FILE_NAME_REGEX);
			Matcher matcher = regex.matcher(documentFileName);
			if (matcher.find()) {
				this.addActionError(this.getText("Errors.invalidFileName"));
				if (event != null) {
					event.setLevel(Event.Level.ERROR);
					event.setDetailMessage("Il nome file " + documentFileName
							+ " contiene caratteri non ammessi");
				}
				controlliOk = false;
			}
		}
		return controlliOk;
	}
	
	/**
	 * Verifica il nome del file di input.
	 *
	 * @param documentDescription descrizione del file allegato
	 * @param event evento da modificare in caso di errore per la tracciatura eventi
	 * @return true se i controlli sono andati a buon fine, false altrimenti
	 */
	protected boolean checkFileDescription(String documentDescription, Event event) {
		boolean controlliOk = true;
		if (StringUtils.isEmpty(documentDescription)) {
			this.addActionError(this.getText("Errors.fileDescriptionMissing"));
			if (event != null) {
				event.setLevel(Event.Level.ERROR);
				event.setDetailMessage("Descrizione obbligatoria mancante per il file caricato");
			}
			controlliOk = false;
		}
		return controlliOk;
	}
	
	/**
	 * Verifica il nome del file di input.
	 *
	 * @param documentFileName nome del file allegato
	 * @param appParamManager l'appParamManager per ricavare le costanti di check
	 * @param estensioniParamName il parametro che indica quali estensioni sono
	 * accettate
	 * @param event evento da modificare in caso di errore per la tracciatura eventi
	 * @return true se i controlli sono andati a buon fine, false altrimenti
	 */
	protected boolean checkFileExtension(String documentFileName, IAppParamManager appParamManager, String estensioniParamName, Event event) {

		boolean controlliOk = true;
		boolean trovato = false;
		String estensioniAmmesse = StringUtils.stripToNull((String) appParamManager.getConfigurationValue(estensioniParamName));
		if (documentFileName != null && estensioniAmmesse != null) {
			String[] estensioni = StringUtils.split(estensioniAmmesse, ',');
			for (String estensione : estensioni) {
				if (documentFileName.toUpperCase().endsWith(estensione.toUpperCase())) {
					trovato = true;
				}
			}
		} else {
			trovato = true;
		}
		if (!trovato) {
			this.addActionError(this.getText("Errors.fileNameExtensionNotSupported", new String[]{estensioniAmmesse}));
			if (event != null) {
				event.setLevel(Event.Level.ERROR);
				event.setDetailMessage("Il file " + documentFileName
						+ " non utilizza una delle estensioni ammesse ("
						+ estensioniAmmesse + ")");
			}
			controlliOk = false;
		}
		return controlliOk;
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
			// allora nel file di resources non Ã¨ presente alcun mapping, si costruisce la chiave secondo una convenzione
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
				// allora nel file di resources non è presente alcun mapping, si costruisce la chiave secondo una convenzione
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
	
	private ILangManager _langManager;

	private IAuthorizationManager _authorizationManager;

	private II18nManager _i18nManager;

	public static final String FAILURE = "failure";

	private HttpServletRequest _request;
	private Map<String, String[]> _params;

	public static final String USER_NOT_ALLOWED = "userNotAllowed";
	
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
		//int i1 = _request.getQueryString().lastIndexOf('/');
		//int i2 = _request.getQueryString().indexOf(".action");
		//String actionPath = _request.getQueryString().substring(0, i1 + 1) + 
		//					  ActionContext.getContext().getActionInvocation().getProxy().getActionName() + 
		//                    _request.getQueryString().substring(i2);
		String url = _request.getAttribute("javax.servlet.forward.request_uri").toString();
		int i = url.indexOf(_request.getContextPath());
		if(i == 0) {
			url = url.substring(i + _request.getContextPath().length());
		}		
		//url = url + "?" + actionPath;
		return url;
	}
		
}
