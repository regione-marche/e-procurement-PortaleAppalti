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
package com.agiletec.apsadmin.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ComponentTagSupport;

import com.agiletec.apsadmin.tags.util.ApsActionParamComponent;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * Tag di utilità per la gestione di submit dell'interfaccia di amministrazione.
 * Il tag ha il compito di costruire il nome dell'action con la sequenza di parametri 
 * (secondo un'ordine prestabilito) che devono essere estratti dall'action. 
 * Per trasportare parametri all'interno delle singole azioni eseguite all'interno 
 * dei form a servizio del cms, i parametri vengono inseriti in sequenza in corrispondenza 
 * del nome della action che si vuole eseguire.
 * Il nome della adione prende la forma:<br />
 * <ACTION_NAME>?<PARAM_NAME_1>=<PARAM_VALUE_1>;<PARAM_NAME_2>=<PARAM_VALUE_2>;....;<PARAM_NAME_N>=<PARAM_VALUE_N>
 * <br />
 * I singoli parametri vengono inseriti dal sottotag ApsActionParamSubTag.
 * @version 1.0
 * @author E.Santoboni
 */
public class ApsActionParamTag extends ComponentTagSupport {
	
	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse resp) {
		return new ApsActionParamComponent(stack, req);
	}
	
    protected void populateParams() {
        super.populateParams();
        ApsActionParamComponent actionParam = (ApsActionParamComponent) component;
        actionParam.setAction(this.getAction());
        actionParam.setVar(this.getVar());
    }
	
	public String getVar() {
		return _var;
	}
	public void setVar(String var) {
		this._var = var;
	}
	
	public String getAction() {
		return _action;
	}
	public void setAction(String action) {
		this._action = action;
	}
	
	protected String _var;
	protected String _action;
	
}