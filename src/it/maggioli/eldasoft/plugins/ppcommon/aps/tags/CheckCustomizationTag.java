package it.maggioli.eldasoft.plugins.ppcommon.aps.tags;

import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.CustomConfigManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.ICustomConfigManager;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import com.agiletec.aps.util.ApsWebApplicationUtils;

/**
 * Tag per la verifica dell'abilitazione ad una customizzazione applicativa.
 * Salva il valore booleano del test in una variabile definita in base ad uno
 * scope eventualmente impostabile.
 * 
 * @author Stefano.Sabbadin
 * @since 1.2
 */
public class CheckCustomizationTag extends TagSupport {

	/**
	 * UID.
	 */
	private static final long serialVersionUID = 1944598875703938693L;

	/** Variabile in cui inserire il risultato del check. */
	private String _var;
	/** Scope della variabile (request, session, application, page). */
	private String _scope;
	/** identificativo dell'oggetto sul quale effettuare il tes.t */
	private String _objectId;
	/** attributo dell'oggetto sul quale effettuare il test. */
	private String _attribute;
	/**
	 * nome della feature da testare (VIS=visible, MAN=mandatory, ACT=active
	 * function).
	 */
	private String _feature;

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
	 * @return the _objectId
	 */
	public String getObjectId() {
		return _objectId;
	}

	/**
	 * @param id
	 *            the _objectId to set
	 */
	public void setObjectId(String id) {
		_objectId = id;
	}

	/**
	 * @return the _attribute
	 */
	public String getAttribute() {
		return _attribute;
	}

	/**
	 * @param _attribute
	 *            the _attribute to set
	 */
	public void setAttribute(String _attribute) {
		this._attribute = _attribute;
	}

	/**
	 * @return the _feature
	 */
	public String getFeature() {
		return _feature;
	}

	/**
	 * @param _feature
	 *            the _feature to set
	 */
	public void setFeature(String _feature) {
		this._feature = _feature;
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

		String feature = this._feature.toUpperCase();
		if (!CustomConfigManager.FEATURE_VISIBLE.equals(feature)
				&& !CustomConfigManager.FEATURE_MANDATORY.equals(feature)
				&& !CustomConfigManager.FEATURE_ACTIVE_FUNCTION.equals(feature))
			throw new JspException(
					"Attributo 'feature' non valido per il tag checkCustomization; i valori ammessi sono: VIS, MAN, ACT.");

		try {
			ICustomConfigManager customConfigManager = (ICustomConfigManager) ApsWebApplicationUtils
					.getBean(CommonSystemConstants.CUSTOM_CONFIG_MANAGER,
							this.pageContext);
			// si calcola il check
			boolean esitoCheck = false;
			if (CustomConfigManager.FEATURE_VISIBLE.equals(feature)) {
				esitoCheck = customConfigManager.isVisible(this._objectId,
						this._attribute);
			} else if (CustomConfigManager.FEATURE_MANDATORY.equals(feature)) {
				esitoCheck = customConfigManager.isMandatory(this._objectId,
						this._attribute);
			} else if (CustomConfigManager.FEATURE_ACTIVE_FUNCTION
					.equals(feature)) {
				esitoCheck = customConfigManager.isActiveFunction(
						this._objectId, this._attribute);
			}
			// si imposta la variabile con l'esito calcolato
			if ("page".equals(scope))
				this.pageContext.setAttribute(this._var, esitoCheck,
						PageContext.PAGE_SCOPE);
			if ("request".equals(scope))
				this.pageContext.setAttribute(this._var, esitoCheck,
						PageContext.REQUEST_SCOPE);
			if ("session".equals(scope))
				this.pageContext.setAttribute(this._var, esitoCheck,
						PageContext.SESSION_SCOPE);
			if ("application".equals(scope))
				this.pageContext.setAttribute(this._var, esitoCheck,
						PageContext.APPLICATION_SCOPE);
		} catch (Throwable e) {
			throw new JspException(
					"Errore durante il test della personalizzazione cliente", e);
		}

		return super.doStartTag();
	}
}
