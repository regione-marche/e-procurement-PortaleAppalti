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
package com.agiletec.apsadmin.system.dispatcher;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.dispatcher.ServletRedirectResult;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.views.util.UrlHelper;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionException;
import com.opensymphony.xwork2.util.reflection.ReflectionExceptionHandler;

/**
 * Redirect Action Result con ancora.
 * Questo resultType è utilizzato per permettere il redirezionamento ad una url che invoca una specifica action 
 * (opzionalmente un namespace) con una specifica ancora.
 *
 * <!-- END SNIPPET: description -->
 *
 * <b>This result type takes the following parameters:</b>
 *
 * <!-- START SNIPPET: params -->
 *
 * <ul>
 *
 * <li><b>actionName (default)</b> - the name of the action that will be redirect to</li>
 *
 * <li><b>namespace</b> - used to determine which namespace the action is in that we're redirecting to . If namespace is
 * null, this defaults to the current namespace</li>
 *
 * <li><b>supressEmptyParameters</b> - optional boolean (defaults to false) that can prevent parameters with no values
 * from being included in the redirect URL.</li>
 *
 * </ul>
 *
 * <!-- END SNIPPET: params -->
 *
 * <b>Example:</b>
 *
 * <pre><!-- START SNIPPET: example -->
 * &lt;package name="public" extends="struts-default"&gt;
 *     &lt;action name="login" class="..."&gt;
 *         &lt;!-- Redirect to another namespace --&gt;
 *         &lt;result type="redirectActionWithAnchor"&gt;
 *             &lt;param name="actionName"&gt;dashboard&lt;/param&gt;
 *             &lt;param name="namespace"&gt;/secure&lt;/param&gt;
 *             &lt;param name="anchorDest"&gt;lang_it/param&gt;
 *         &lt;/result&gt;
 *     &lt;/action&gt;
 * &lt;/package&gt;
 *
 * <!-- END SNIPPET: example --></pre>
 *
 * @see ActionMapper
 */
public class ServletActionRedirectResultWithAnchor extends ServletRedirectResult implements ReflectionExceptionHandler {

    private static final long serialVersionUID = -9042425229314584066L;

    /** The default parameter */
    public static final String DEFAULT_PARAM = "actionName";

    private static final Logger LOG = LoggerFactory.getLogger(ServletActionRedirectResultWithAnchor.class);

    protected String actionName;
    protected String namespace;
    protected String method;
    protected String _anchorDest;
    protected boolean supressEmptyParameters = false;

    private Map<String, String> requestParameters = new LinkedHashMap<String, String>();

    public ServletActionRedirectResultWithAnchor() {
        super();
    }

    public ServletActionRedirectResultWithAnchor(String actionName) {
        this(null, actionName, null);
    }

    public ServletActionRedirectResultWithAnchor(String actionName, String method) {
        this(null, actionName, method);
    }

    public ServletActionRedirectResultWithAnchor(String namespace, String actionName, String method) {
        super(null);
        this.namespace = namespace;
        this.actionName = actionName;
        this.method = method;
    }

    protected List<String> prohibitedResultParam = Arrays.asList(new String[] {
            DEFAULT_PARAM, "namespace", "method", "encode", "parse", "location",
            "prependServletContext", "supressEmptyParameters", "anchorDest" });

    /**
     * @see com.opensymphony.xwork2.Result#execute(com.opensymphony.xwork2.ActionInvocation)
     */
    public void execute(ActionInvocation invocation) throws Exception {
        actionName = conditionalParse(actionName, invocation);
        if (namespace == null) {
            namespace = invocation.getProxy().getNamespace();
        } else {
            namespace = conditionalParse(namespace, invocation);
        }
        if (method == null) {
            method = "";
        }
        else {
            method = conditionalParse(method, invocation);
        }

        String resultCode = invocation.getResultCode();
        if (resultCode != null) {
            ResultConfig resultConfig = invocation.getProxy().getConfig().getResults().get(
                    resultCode);
            Map resultConfigParams = resultConfig.getParams();
            for (Iterator i = resultConfigParams.entrySet().iterator(); i.hasNext(); ) {
                Map.Entry e = (Map.Entry) i.next();
                if (! prohibitedResultParam.contains(e.getKey())) {
                    requestParameters.put(e.getKey().toString(),
                            e.getValue() == null ? "":
                                conditionalParse(e.getValue().toString(), invocation));
                    String potentialValue = e.getValue() == null ? "": conditionalParse(e.getValue().toString(), invocation);
                    if (!supressEmptyParameters || ((potentialValue != null) && (potentialValue.length() > 0))) {
                      requestParameters.put(e.getKey().toString(), potentialValue);
                    }
                }
            }
        }

        StringBuilder tmpLocation = new StringBuilder(actionMapper.getUriFromActionMapping(new ActionMapping(actionName, namespace, method, null)));
        UrlHelper.buildParametersString(requestParameters, tmpLocation, "&");
        
        String anchorDest = this.getAnchorDest();
        if (null != anchorDest) {
        	tmpLocation.append("#").append(anchorDest);
        }
        
        setLocation(tmpLocation.toString());

        super.execute(invocation);
    }

    /**
     * Sets the action name
     *
     * @param actionName The name
     */
    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    /**
     * Sets the namespace
     *
     * @param namespace The namespace
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * Sets the method
     *
     * @param method The method
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * Sets the supressEmptyParameters option
     *
     * @param suppress The new value for this option
     */
    public void setSupressEmptyParameters(boolean supressEmptyParameters) {
        this.supressEmptyParameters = supressEmptyParameters;
    }

    protected String getAnchorDest() {
		return _anchorDest;
	}
    
	/**
     * Sets the anchor destination
     * @param method The anchor destination
     */
    public void setAnchorDest(String anchorDest) {
		this._anchorDest = anchorDest;
	}
    
    /**
     * Adds a request parameter to be added to the redirect url
     *
     * @param key The parameter name
     * @param value The parameter value
     */
    public ServletActionRedirectResultWithAnchor addParameter(String key, Object value) {
        requestParameters.put(key, String.valueOf(value));
        return this;
    }
    
    public void handle(ReflectionException ex) {
        // Only log as debug as they are probably parameters to be appended to the url
        LOG.debug(ex.getMessage(), ex);
    }

}