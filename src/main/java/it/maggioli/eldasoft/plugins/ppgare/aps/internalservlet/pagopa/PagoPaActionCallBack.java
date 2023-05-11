package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.pagopa;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
import org.slf4j.Logger;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.apsadmin.system.TokenInterceptor;

import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import net.sf.json.JSONObject;

/**
 * Classe di call back per le azioni effettuate dall'utente sulla piattaforma di pagamento
 * @author gabriele.nencini
 *
 */
public class PagoPaActionCallBack extends EncodedDataAction implements SessionAware {
	/**
	 * UID
	 */
	private static final long serialVersionUID = -9182702714450834357L;
	
	private Map<String, Object> session;
	private PagoPaManager pagoPaManager;
	private Long id;
	private final Logger logger = ApsSystemUtils.getLogger();
	private String urlRedirect;
	private InputStream inputStream;
	private ConfigInterface configManager;
	
	/**
	 * @param configManager the configManager to set
	 */
	public void setConfigManager(ConfigInterface configManager) {
		this.configManager = configManager;
	}

	/**
	 * @return the urlRedirect
	 */
	public String getUrlRedirect() {
		return urlRedirect;
	}
	
	public InputStream getInputStream() {
		return this.inputStream;
	}
	
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
	
	
	
	/**
	 * @param pagoPaManager the pagoPaManager to set
	 */
	public void setPagoPaManager(PagoPaManager pagoPaManager) {
		this.pagoPaManager = pagoPaManager;
	}
	
	public String ok() {
		TokenInterceptor.redirectNewToken(getRequest().getSession());
		this.id = Long.valueOf(getParameters().get("id")[0]);
		try {
			pagoPaManager.impostaPagamentoEffettuato(id);
		} catch (Exception e) {
			logger.error("Errore nella impostazione a pagato del pagamento con id {}",this.id,e);
		}
//		this.urlRedirect = "/"+getCurrentLang().getCode()+"/ppgare_pagopa_effettuati.wp";
		StringBuilder url = new StringBuilder ();
		String tokenName = TokenInterceptor.getStrutsTokenName();
		String redirectToken = (String) session.get("redirectToken");
        this.urlRedirect = url.append("/")
        	.append(getCurrentLang().getCode())
           .append("/ppgare_pagopa_effettuati.wp?actionPath=/ExtStr2/do/FrontEnd/PagoPA/dettaglioPagamento.action&currentFrame=7")
           .append("&model.id=").append(this.id)
           .append("&model.pagamentoEffettuato=true")
           .append("&").append(tokenName).append("=").append(redirectToken).toString();
		return SUCCESS;
	}
	
	public String ko() {
		return this.cancel();
	}
	
	public String stos() {
		// recuperare il dato dello id
		// aggiornare a pagamento effettuato ???
		JSONObject data = new JSONObject();
		data.put("response", "OK");
		try {
			this.setInputStream( new ByteArrayInputStream(data.toString().getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return SUCCESS;
	}
	
	public String cancel() {
		// recuperare il dato dello id
		// aggiornare a pagamento non effettuato
		TokenInterceptor.redirectNewToken(getRequest().getSession());
//		String baseUrl = this.configManager.getParam(SystemConstants.PAR_APPL_BASE_URL);
		String tokenName = TokenInterceptor.getStrutsTokenName();
		String redirectToken = (String) session.get("redirectToken");
		this.id = Long.valueOf(getParameters().get("id")[0]);
        StringBuilder url = new StringBuilder (); 
        this.urlRedirect = url.append("/")
        	.append(getCurrentLang().getCode())
           .append("/ppgare_pagopa_daeffettuare.wp?actionPath=/ExtStr2/do/FrontEnd/PagoPA/dettaglioPagamento.action&currentFrame=7")
           .append("&model.id=").append(this.id)
           .append("&").append(tokenName).append("=").append(redirectToken).toString();
		return SUCCESS;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	
}
