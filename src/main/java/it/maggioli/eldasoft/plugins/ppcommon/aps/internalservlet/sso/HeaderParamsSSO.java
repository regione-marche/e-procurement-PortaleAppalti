package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso;

import java.io.Serializable;
import com.agiletec.aps.system.ApsSystemUtils;
import com.opensymphony.xwork2.ActionContext;

/**
 * ... 
 */
public class HeaderParamsSSO implements /*HttpSessionBindingListener,*/ Serializable {	
	/**
	 * UID
	 */
	private static final long serialVersionUID = -5198413428168371044L;

	private static final String SESSION_OBJECT_SSO_HEADER_PARAMS = "SSOHeaderParameters";
		
	private String nome;
	private String cognome;
	private String azienda;
	private String codiceFiscale;
	private String partitaIVA;
	private String email;
	private String callback;
		
	
	public HeaderParamsSSO(
			String nome, 
			String cognome, 
			String azienda, 
			String codiceFiscale, 
			String partitaIVA, 
			String email, 
			String callback) 
	{
		this.nome = nome;
		this.cognome = cognome;
		this.azienda = azienda;
		this.codiceFiscale = codiceFiscale;
		this.partitaIVA = partitaIVA;
		this.email = email;
		this.callback = callback;
	}	
	
//	@Override
//	public void valueBound(HttpSessionBindingEvent hsbe) {
//	}
//
//	@Override
//	public void valueUnbound(HttpSessionBindingEvent hsbe) {
//	}

	/**
	 * restituisce l'oggetto in sessione 
	 */
	public static HeaderParamsSSO getFromSession() {
		HeaderParamsSSO header = null; 
		try {
			header = (HeaderParamsSSO)ActionContext.getContext().getSession().get(SESSION_OBJECT_SSO_HEADER_PARAMS);
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().warn(t.getMessage());
		}
		return header;
	}

	/**
	 * aggiorna l'oggetto in sessione 
	 */
	public void putToSession() {
		ActionContext.getContext().getSession().put(SESSION_OBJECT_SSO_HEADER_PARAMS, this);
	}
	
	/**
	 * rimuove i parametri dell'header della risposta del sistema di autenticazione remoto dalla sessione   
	 */
	public static void removeFromSession() {
		try {
			ActionContext.getContext().getSession().remove(SESSION_OBJECT_SSO_HEADER_PARAMS);		
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().warn(t.getMessage());
		}
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getAzienda() {
		return azienda;
	}

	public void setAzienda(String azienda) {
		this.azienda = azienda;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public String getPartitaIVA() {
		return partitaIVA;
	}

	public void setPartitaIVA(String partitaIVA) {
		this.partitaIVA = partitaIVA;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


	public String getCallback() {
		return callback;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}

}
