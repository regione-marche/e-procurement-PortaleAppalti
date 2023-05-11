package it.maggioli.eldasoft.plugins.ppcommon.aps.tags;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import com.agiletec.aps.util.ApsWebApplicationUtils;

/**
 * Tag per il recupero di un parametro della configurazione di sistema nella
 * verticalizzazione Eldasoft.
 * 
 * @author Stefano.Sabbadin
 * @since 1.7.2
 */
public class GetApplicationParamTag extends TagSupport {

	/**
	 * UID.
	 */
	private static final long serialVersionUID = 8222768085773530441L;

	/** Variabile in cui inserire il risultato del check. */
	private String _var;
	/** Scope della variabile (request, session, application, page). */
	private String _scope;
	/** nome dell'elemento della configurazione da recuperare. */
	private String _name;

	/**
	 * @return the _var
	 */
	public String getVar() {
		return _var;
	}

	/**
	 * @param _var
	 *            the _var to set
	 */
	public void setVar(String _var) {
		this._var = _var;
	}

	/**
	 * @return the _scope
	 */
	public String getScope() {
		return _scope;
	}

	/**
	 * @param _scope
	 *            the _scope to set
	 */
	public void setScope(String _scope) {
		this._scope = _scope;
	}
	
	/**
	 * @return the _name
	 */
	public String getName() {
		return _name;
	}

	/**
	 * @param _name the _name to set
	 */
	public void setName(String _name) {
		this._name = _name;
	}

	@Override
	public int doStartTag() throws JspException {
		// check dei parametri
		String scope = (this._scope != null ? this._scope.toLowerCase()
				: "page");
		if (!"page".equals(scope) && !"request".equals(scope)
				&& !"session".equals(scope) && !"application".equals(scope))
			throw new JspException(
					"Attributo 'scope' non valido per il tag checkCustomization; i valori ammessi sono: page, request, session, application.");

		try {
			IAppParamManager appParamManager = (IAppParamManager) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.APP_PARAM_MANAGER,
							this.pageContext);
			
			Object obj = appParamManager.getConfigurationValue(this._name);


			// si imposta la variabile con l'esito calcolato
			if ("page".equals(scope))
				this.pageContext.setAttribute(this._var, obj,
						PageContext.PAGE_SCOPE);
			if ("request".equals(scope))
				this.pageContext.setAttribute(this._var, obj,
						PageContext.REQUEST_SCOPE);
			if ("session".equals(scope))
				this.pageContext.setAttribute(this._var, obj,
						PageContext.SESSION_SCOPE);
			if ("application".equals(scope))
				this.pageContext.setAttribute(this._var, obj,
						PageContext.APPLICATION_SCOPE);
		} catch (Throwable e) {
			throw new JspException(
					"Errore durante l'estrazione della configurazione dell'applicativo", e);
		}

		return super.doStartTag();
	}

}
