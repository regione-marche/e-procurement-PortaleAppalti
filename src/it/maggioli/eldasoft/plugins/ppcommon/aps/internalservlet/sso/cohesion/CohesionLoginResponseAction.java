package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.cohesion;

import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.AccountSSO;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.TrackerSessioniUtenti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;

import java.io.ByteArrayInputStream;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.w3c.dom.Document;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.url.IURLManager;
import com.agiletec.aps.system.services.url.PageURL;
import com.agiletec.apsadmin.system.BaseAction;

public class CohesionLoginResponseAction extends BaseAction implements ServletResponseAware {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5652091832780950169L;

	private IAppParamManager appParamManager;
	private IURLManager urlManager;
	private IPageManager pageManager;
	
	private HttpServletResponse response;
	
	private boolean formLoginVisible;
	
	/**
	 * @param appParamManager
	 *            the appParamManager to set
	 */
	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}
	

	public void setUrlManager(IURLManager urlManager) {
		this.urlManager = urlManager;
	}
	
	public void setPageManager(IPageManager pageManager) {
		this.pageManager = pageManager;
	}
	
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	/**
	 * @return the formLoginVisible
	 */
	public boolean isFormLoginVisible() {
		return formLoginVisible;
	}

	/**
	 * @param formLoginVisible the formLoginVisible to set
	 */
	public void setFormLoginVisible(boolean formLoginVisible) {
		this.formLoginVisible = formLoginVisible;
	}

	public String login() {
		String target = SUCCESS;
		
		Integer configurazioneSSOProtocollo = (Integer)appParamManager.getConfigurationValue(PortGareSystemConstants.TIPO_PROTOCOLLO_SSO);
		if(PortGareSystemConstants.SSO_PROTOCOLLO_COHESION == configurazioneSSOProtocollo.intValue()) {
			AccountSSO accountCohesion = null;
			String cohesionEncryptionKey = (String)appParamManager.getConfigurationValue("sso.cohesion.encryption.key");
			String token =StringEscapeUtils.unescapeXml(this.getRequest().getParameter("token"));
	
			if(token != null){
				String cohesionResponse = new String(base64Decode(token));
				if (!"".equals(cohesionEncryptionKey)) {
					try {
						cohesionResponse = new String(cipher3DES(false, base64Decode(token), cohesionEncryptionKey.getBytes()));
						Document cohesionResponseXML = getXmlDocFromString(cohesionResponse);
						cohesionResponseXML.getDocumentElement().normalize();
						
						String attributoNome = (String) appParamManager.getConfigurationValue("sso.mapping.nome");
						String attributoCognome = (String) appParamManager.getConfigurationValue("sso.mapping.cognome");
						String attributoLoginCodiceFiscale = (String) appParamManager.getConfigurationValue("sso.mapping.login");
						String attributoEmail = (String) appParamManager.getConfigurationValue("sso.mapping.email");
						String attributoTipoAutenticazione = (String) appParamManager.getConfigurationValue("sso.mapping.tipoAutenticazione");

						String nome = getCohesionElement(cohesionResponseXML, attributoNome);
						String cognome = getCohesionElement(cohesionResponseXML, attributoCognome);
						String login = getCohesionElement(cohesionResponseXML, attributoLoginCodiceFiscale);
						String email = getCohesionElement(cohesionResponseXML, attributoEmail); 
						String tipoAutenticazione = getCohesionElement(cohesionResponseXML, attributoTipoAutenticazione); 
	
						if(nome != null && cognome != null && login != null){
							//WE2040
							login = login.toUpperCase();
							
							accountCohesion = new AccountSSO();
							accountCohesion.setLogin(login);
							accountCohesion.setNome(nome);
							accountCohesion.setCognome(cognome);
							accountCohesion.setEmail(email);
							accountCohesion.setTipoAutenticazione(tipoAutenticazione);		
							this.getRequest().setAttribute(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO, accountCohesion);
							// 31/03/2017: si aggiunge l'utente autenticatosi nella lista contenuta nell'applicativo
							String ipAddress = this.getRequest().getHeader("X-FORWARDED-FOR");
							if (ipAddress == null) {
								ipAddress = this.getRequest().getRemoteAddr();
							}
							accountCohesion.setIpAddress(ipAddress);
							TrackerSessioniUtenti.getInstance(this.getRequest().getSession().getServletContext()).putSessioneUtente(this.getRequest().getSession(), ipAddress, login, DateFormatUtils.format(new Date(), "dd/MM/yyyy HH:mm:ss"));
						}else{
							this.addActionError(this.getText(
									"Errors.sso.insufficientData",
									new String[] { "Cohesion", nome, cognome, null, login, null, email }));
							this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
							target = INPUT;
						}
	
					}catch (Exception ex) {
						ApsSystemUtils.logThrowable(ex, this, "login");
						this.addActionError(this.getText("Errors.sso.dataElaboration", new String[] {"Cohesion"}));
						this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
						target = INPUT;
					}
				}
			}
		}else{
			this.addActionError(this.getText("Errors.sso.IncoerentConfiguration"));
			this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
			target = INPUT;
		}
		return target;
	}

	private byte[] base64Decode(String data) {
		byte[] result = new byte[0];
		try {
			result = DatatypeConverter.parseBase64Binary(data);
		} catch (Exception ex) {
			ApsSystemUtils.logThrowable(ex, this, "base64Decode");
		}
		return result;
	}

	private Document getXmlDocFromString(String xml) {

		Document xmlDoc = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			xmlDoc = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes()));
		} catch (Exception ex) {
			ApsSystemUtils.logThrowable(ex, this, "getXmlDocFromString");
		}
		return xmlDoc;
	}

	private byte[] cipher3DES(boolean encrypt, byte[] message, byte[] key) throws Exception {
		byte[] cipher = new byte[0];
		try {
			if (key.length != 24) {
				throw new Exception("key size must be 24 bytes");
			}
			int cipherMode = Cipher.DECRYPT_MODE;
			if (encrypt) {
				cipherMode = Cipher.ENCRYPT_MODE;
			}
			Cipher sendCipher = Cipher.getInstance("DESede/ECB/NoPadding");
			SecretKey myKey = new SecretKeySpec(key, "DESede");
			sendCipher.init(cipherMode, myKey);
			cipher = sendCipher.doFinal(message);
		} catch (Exception ex) {
			ApsSystemUtils.logThrowable(ex, this, "cipher3DES");
		}
		return cipher;
	}
	
	public String getUrlErrori() {
		HttpServletRequest request = this.getRequest(); 
		RequestContext reqCtx = new RequestContext();
		reqCtx.setRequest(request);

		reqCtx.setResponse(response);
		Lang currentLang = this.getLangManager().getDefaultLang();
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG, currentLang);
		IPage pageDest = pageManager.getPage("portalerror");
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE, pageDest);
		request.setAttribute(RequestContext.REQCTX, reqCtx);
		
		PageURL url = this.urlManager.createURL(reqCtx);
	
		url.addParam(RequestContext.PAR_REDIRECT_FLAG, "1");
		return url.getURL();
	}
	
	private String getCohesionElement(Document doc, String name) {
		String value = null; 
		try {
			value = StringUtils.stripToNull(doc.getElementsByTagName(name).item(0).getTextContent());
		} catch(Exception e) {
			value = null;
		}
		return value;
	}

}
