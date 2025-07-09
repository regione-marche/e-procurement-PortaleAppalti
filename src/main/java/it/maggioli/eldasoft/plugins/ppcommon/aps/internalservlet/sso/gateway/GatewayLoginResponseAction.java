package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.gateway;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.apsadmin.system.BaseAction;
import com.opensymphony.xwork2.ActionContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.AccountSSO;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.BaseResponseAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.TrackerSessioniUtenti;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.spid.IAuthServiceSPIDManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.spid.WSAuthServiceSPIDWrapper;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.util.CollectionUtils;

import javax.servlet.ServletContext;
import java.util.Date;

public class GatewayLoginResponseAction extends BaseResponseAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5050639387762534624L;

	private static final String PARAMETER_CONFIGURATION_ERROR = "Errors.sso.parameterConfiguration";

	private String url;
	private String endpoint;
	private String clientId;
	private String authType;
	private String target;
	private String token;
	
	
	public String prepareLogin() {
		target = SUCCESS;
		
		this.urlLogin = prepareCallbackLogin(
				"/do/SSO/GatewayLoginResponse.action",
				this.configManager,
				this.appParamManager,
				this.authServiceSPIDManager,
				this.wsAuthServiceSPID);
		if(this.urlLogin == null) {
			target = INPUT;
		}
		return target;
	}
	
	@Override
	public String login() {
		String target = SUCCESS;

		AccountSSO accountSpid = validate(
				this.appParamManager, 
				this.authServiceSPIDManager, 
				this.wsAuthServiceSPID); 
		
		if(accountSpid == null) {
			target = INPUT;
		} else {
			// dati validi...
			this.getRequest().getSession().removeAttribute("errMsg");

			this.getRequest().setAttribute(
							CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO,
							accountSpid);

			// 31/03/2017: si aggiunge l'utente autenticatosi nella
			// lista contenuta nell'applicativo
			String ipAddress = this.getRequest().getHeader("X-FORWARDED-FOR");
			if (ipAddress == null) {
				ipAddress = this.getRequest().getRemoteAddr();
			}
			accountSpid.setIpAddress(ipAddress);
			
			ServletContext ctx = this.getRequest().getSession().getServletContext();
			TrackerSessioniUtenti.getInstance(ctx)
					.putSessioneUtente(this.getRequest().getSession(),
									   ipAddress,
									   accountSpid.getLogin(),
									   DateFormatUtils.format(new Date(), "dd/MM/yyyy HH:mm:ss"));			
		} 
		
		return target;
	}
	
	/**
	 * ... 
	 */
	
	public AccountSSO validate(
			IAppParamManager appParamManager,
			IAuthServiceSPIDManager authServiceSPIDManager,
			WSAuthServiceSPIDWrapper wsAuthServiceSPID) 
	{
		AccountSSO accountSpid = null;
		
		String target = SUCCESS;

		BaseAction action = (BaseAction)ActionContext.getContext().getActionInvocation().getAction();
		try {
			// recupera le info dell'utente dal servizio SPID tramite il
			// token...
			String jwtToken = this.getRequest().getParameter("jwtToken");
			jwtToken = StringUtils.isEmpty(jwtToken) ? token : jwtToken;
		    final String passphrase = StringUtils.stripToNull(getParamValue((AppParamManager.GATEWAY_PASSPHRASE)));
		    
		    JwtParser parser = Jwts.parser().setSigningKey(passphrase);
			Jws<Claims> jws = parser.parseClaimsJws(jwtToken);
			
			if (MapUtils.isNotEmpty(jws.getBody())) {
				String codiceFiscale = jws.getBody().get("codiceFiscale", String.class);
				String nome = jws.getBody().get("nome", String.class);
				String cognome = jws.getBody().get("cognome", String.class);
				String email = jws.getBody().get("email", String.class);
				
				String login = codiceFiscale;

				if ((StringUtils.isNotEmpty(nome) && StringUtils.isNotEmpty(cognome))
					&& StringUtils.isNotEmpty(login) && login.length() >= 11) {
					
					// AccountSSO ready !!!
					accountSpid = new AccountSSO();
					accountSpid.setNome(nome);
					accountSpid.setCognome(cognome);
					accountSpid.setLogin(login);
					accountSpid.setEmail(email);
					accountSpid.setTipologiaLogin(PortGareSystemConstants.TIPOLOGIA_LOGIN_GATEWAY_SSO);
			
				} else {
					action.addActionError(action.getText(
							"Errors.sso.insufficientData", new String[] {
									"Maggioli SPID", nome, cognome,
									"", codiceFiscale, "",
									email }));
					action.getRequest().getSession().setAttribute("ACTION_OBJECT", action);
					target = INPUT;
				}

			} else {
				action.addActionError(action.getText("Errors.sso.readingData", new String[] { "Gateway" }));
				action.getRequest().getSession().setAttribute("ACTION_OBJECT", action);
				ApsSystemUtils.logThrowable(null, null, "validateLogin");
				target = INPUT;
			}
		} catch (Exception e) {
			action.addActionError(action.getText("Errors.sso.configuration", new String[] { "Gateway" }));
			action.getRequest().getSession().setAttribute("ACTION_OBJECT", action);
			ApsSystemUtils.logThrowable(e, this, "validate");
		}
		
		if(!SUCCESS.equals(target)) {
			accountSpid = null;
		}
			
		return accountSpid;
	}

	private String prepareCallbackLogin(String responseAction, ConfigInterface configManager, IAppParamManager appParamManager,
			IAuthServiceSPIDManager authServiceSPIDManager, WSAuthServiceSPIDWrapper wsAuthServiceSPID) {
		
		String urlLogin = null; 

		BaseAction action = (BaseAction) ActionContext.getContext().getActionInvocation().getAction();
		try {
			loadParams();
			validateParams(action);
			target = CollectionUtils.isEmpty(action.getActionErrors()) ? SUCCESS : INPUT;
			if (SUCCESS.equals(target)) {
				// imposta dinamicamente l'endpoint del proxy prima di
				// utilizzarne i servizi...
//				wsAuthServiceSPID.getProxyWSAuthService().setEndpoint(url);


				// richiedi il token temporaneo al sevizio SPID...
				// e salvalo in sessione per il login...
//				String authId = authServiceSPIDManager.getAuthId();
//
//				action.getRequest().getSession().setAttribute(SESSION_ID_SSO_AUTHID, authId);

				urlLogin = url + endpoint
						+ "/" + clientId;
						//+ "&authenticationType=" + authType;
				getRequest().setAttribute("url", urlLogin);
				
			}
		} catch (ApsException e) {
			action.addActionError(action.getText("Errors.sso.configuration", new String[] { "Maggioli MYID" }));
			action.getRequest().getSession().setAttribute("ACTION_OBJECT", action);
			ApsSystemUtils.logThrowable(e, null, "prepareLogin");
			target = INPUT;
		}
		
		return urlLogin;
	}
	
	private void validateParams(BaseAction action) {
		if (StringUtils.isEmpty(url)) {
			action.addActionError(action.getText(PARAMETER_CONFIGURATION_ERROR, new String[] { "Gateway URL " }));
		}
		if (StringUtils.isEmpty(endpoint)) {
			action.addActionError(action.getText(PARAMETER_CONFIGURATION_ERROR, new String[] { "Gateway endpoint" }));
		}
		if (StringUtils.isEmpty(clientId)) {
			action.addActionError(action.getText(PARAMETER_CONFIGURATION_ERROR, new String[] { "Gateway clientID" }));
		}
//		if (StringUtils.isEmpty(authType)) {
//			action.addActionError(action.getText(PARAMETER_CONFIGURATION_ERROR, new String[] { "Gateway authType" }));
//			action.getRequest().getSession().setAttribute("ACTION_OBJECT", action);
//		}
	}

	private void loadParams() throws ApsSystemException {
		url = getParamValue(AppParamManager.GATEWAY_URL);
		endpoint = getParamValue(AppParamManager.GATEWAY_ENDPOINT);
		clientId = getParamValue(AppParamManager.GATEWAY_CLIENT_ID);
//		authType = getParamValue(AppParamManager.GATEWAY_AUTH_TYPE);
		//String passphrase = getValue(params, AppParamManager.GATEWAY_PASSPHRASE);
	}
	
	private String getParamValue(String key) {
		Object confValue = appParamManager.getConfigurationValue(key);
		return confValue != null ? StringUtils.stripToNull(confValue.toString()) : null;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
