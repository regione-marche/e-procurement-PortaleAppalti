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
package com.agiletec.apsadmin.tags.form;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.agiletec.apsadmin.tags.util.AutoIndexingTagHelper;
import com.agiletec.apsadmin.tags.util.IAutoIndexingTag;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * This class implements the radio button tag which inherits some of the Struts2 radio functionalities. 
 * @author M Minnai
 */
public class RadioTag extends org.apache.struts2.views.jsp.ui.AbstractUITag implements IAutoIndexingTag {
	
	@Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		String currentCounter = this.getCurrentIndex();
		if (null != currentCounter) {
			this.setTabindex(currentCounter);
		}
		return new com.agiletec.apsadmin.tags.util.RadioMap(stack, req, res, this.getChecked(), this.getCheckedVar(), this.tabindex);
    }
		
	
	public void setChecked(String checked) {
		this._checked = checked;
	}

	public String getChecked() {
		return _checked;
	}
	
	public void setCheckedVar(String checkedVar) {
		this._checkedVar = checkedVar;
	}
	
	public String getCheckedVar() {
		return _checkedVar;
	}

	public String getCurrentIndex() {
		return AutoIndexingTagHelper.getCurrentIndex(this, this.pageContext.getRequest());
	}
	
	public int getStep() {
		return _step;
	}
	public void setStep(int step) {
		this._step = step;
	}
	
	private int _step = 1;

	/**
	 * This toggles the checked condition of the radio button.
	 */
	private String _checked;
	/**
	 * This is the name of the context parameter which holds the value to check against the 'value' attribute.
	 */
	private String _checkedVar;
}
