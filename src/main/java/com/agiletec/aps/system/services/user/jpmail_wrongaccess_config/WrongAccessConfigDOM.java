/*
 *
 * Copyright 2008 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
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
 * Copyright 2008 AgileTec s.r.l. (http://www.agiletec.it) All rights reserved.
 *
 */
package com.agiletec.aps.system.services.user.jpmail_wrongaccess_config;

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.plugins.jpuserreg.aps.system.services.userreg.model.IUserRegConfig;

/**
 * @author Marco Perazzetta
 */
public class WrongAccessConfigDOM {

	public WrongAccessConfigDOM() {
	}

	public WrongAccessConfigDOM(String xml) throws ApsSystemException {
		Document doc = this.decodeDOM(xml);
		Element root = doc.getRootElement();
		IWrongAccessConfig config = this.getConfig();
		this.extractProfileParamsConfig(root, config);
		this.extractMailSenderConfig(root, config);
		this.extractUserDisabledAdminMailConfig(root, config);
		this.extractUserDisabledUserMailConfig(root, config);
		this.extractIpSuspendedAdminMailConfig(root, config);
	}

	protected void extractMailSenderConfig(Element root, IWrongAccessConfig config) {
		Element tokenElem = root.getChild("sender");
		config.setEMailSenderCode(tokenElem.getAttributeValue("code"));
	}
	
	protected void extractProfileParamsConfig(Element root, IWrongAccessConfig config) {
		Element profileElem = root.getChild("profileEntity");
		config.setProfileNameAttr(profileElem.getAttributeValue("nameAttr"));
		config.setProfileSurnameAttr(profileElem.getAttributeValue("surnameAttr"));
		config.setProfileEMailAttr(profileElem.getAttributeValue("eMailAttr"));
		config.setProfileLangAttr(profileElem.getAttributeValue("langAttr"));
	}

	protected void extractUserDisabledAdminMailConfig(Element root, IWrongAccessConfig config) {
		Element wrongPasswordElem = root.getChild("userDisabledAdminMail");
		config.setUserDisabledAdminPageCode(wrongPasswordElem.getAttributeValue("pageCode"));
		List<Element> templates = wrongPasswordElem.getChildren("template");
		Iterator<Element> it = templates.iterator();
		while (it.hasNext()) {
			Element template = it.next();
			String lang = template.getAttributeValue("lang");
			String subject = template.getChildText("subject");
			config.addUserDisabledAdminMailSubject(lang, subject);
			String body = template.getChildText("body");
			config.addUserDisabledAdminMailBody(lang, body);
		}
	}

	protected void extractUserDisabledUserMailConfig(Element root, IWrongAccessConfig config) {
		Element wrongPasswordElem = root.getChild("userDisabledUserMail");
		config.setUserDisabledUserPageCode(wrongPasswordElem.getAttributeValue("pageCode"));
		List<Element> templates = wrongPasswordElem.getChildren("template");
		Iterator<Element> it = templates.iterator();
		while (it.hasNext()) {
			Element template = it.next();
			String lang = template.getAttributeValue("lang");
			String subject = template.getChildText("subject");
			config.addUserDisabledUserMailSubject(lang, subject);
			String body = template.getChildText("body");
			config.addUserDisabledUserMailBody(lang, body);
		}
	}

	protected void extractIpSuspendedAdminMailConfig(Element root, IWrongAccessConfig config) {
		Element wrongPasswordElem = root.getChild("ipSuspendedAdminMail");
		config.setIpSuspendedAdminPageCode(wrongPasswordElem.getAttributeValue("pageCode"));
		List<Element> templates = wrongPasswordElem.getChildren("template");
		Iterator<Element> it = templates.iterator();
		while (it.hasNext()) {
			Element template = it.next();
			String lang = template.getAttributeValue("lang");
			String subject = template.getChildText("subject");
			config.addIpSuspendedAdminMailSubject(lang, subject);
			String body = template.getChildText("body");
			config.addIpSuspendedAdminMailBody(lang, body);
		}
	}

	protected Document decodeDOM(String xml) throws ApsSystemException {
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		StringReader reader = new StringReader(xml);
		Document doc = null;
		try {
			doc = builder.build(reader);
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().error("Errore nel parsing: " + t.getMessage());
			throw new ApsSystemException("Errore nel parsing della configurazione", t);
		}
		return doc;
	}

	public IWrongAccessConfig getConfig() {
		return _config;
	}

	private IWrongAccessConfig _config = new WrongAccessConfig();

}
