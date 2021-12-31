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
package com.agiletec.aps.system.common.renderer;

/**
 * This class represents a special character to covert in the rendering process.
 * The list of the characters to convert is loaded during the initialization of the 
 * rendering service.
 * @author E.Santoboni
 */
public class HtmlSpecialCharacter {
	
	protected String getCharacter() {
		return _character;
	}
	public void setCharacter(String character) {
		this._character = character;
	}
	
	protected String getHtml() {
		return _html;
	}
	public void setHtml(String html) {
		this._html = html;
	}
	
	private String _character;
	private String _html;
	
}
