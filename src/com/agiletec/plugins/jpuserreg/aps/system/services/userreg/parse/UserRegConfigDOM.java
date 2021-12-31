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
package com.agiletec.plugins.jpuserreg.aps.system.services.userreg.parse;

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.plugins.jpuserreg.aps.system.services.userreg.model.IUserRegConfig;
import com.agiletec.plugins.jpuserreg.aps.system.services.userreg.model.UserRegConfig;

/*
 * TODO
 * 

<reactivationQuestions>
	<question code="0" >
		<lang code="it" >Qual'è il tuo colore preferito?</lang>
		<lang code="en" >What's your prefered colour?</lang>
	</question>
	<question code="1" >
		<lang code="it" >Qual'è il tuo animale preferito?</lang>
		<lang code="en" >What's your prefered pet?</lang>
	</question>
</reactivationQuestions>

 * 
 * */
/**
 * Container of plugin config 
 * 
 * 
 * 
 
 <regProfileConfig>
	<profileEntity nameAttr="Nome" surnameAttr="Cognome" eMailAttr="eMail" />
	
	<!-- Token validity in minutes - Activation page name -->
	<tokenValidity minutes="60"/>
	
	<!-- Sender code, as in mailConfig -->
	<sender code="CODE1" />
	
	<!-- Authorities to load on user request profile -->
	<userAuthDefaults>
		<role name="editorCoach" />
		<role name="supervisorCustomers" />
		<group name="coach" />
		<group name="customers" />
	</userAuthDefaults>
	
	<!-- Activation page name -->
	<activation pageCode="attivazione">
		<template lang="it">
			<subject>[jAPS] : Attivazione utente</subject>
			<body>
Gentile {name} {surname}, 
grazie per esserti registrato.
Per attivare il tuo account è necessario seguire il seguente link: 
{link}
Cordiali Saluti.
			</body>
		</template>
	</activation>
	
	<!-- Activation page name -->
	<reactivation pageCode="riattivazione">
		<template lang="it">
			<subject>[jAPS] : Riattivazione utente</subject>
			<body>
Gentile {name} {surname}, 
il tuo userName è {userName}.
Per riattivare il tuo account è necessario seguire il seguente link: 
{link}
Cordiali Saluti.
			</body>
		</template>
	</reactivation>
	<reactivationQuestions>
		<question code="0" >
			<lang code="it" >Qual'è il tuo colore preferito?</lang>
			<lang code="en" >What's your prefered colour?</lang>
		</question>
		<question code="1" >
			<lang code="it" >Qual'è il tuo animale preferito?</lang>
			<lang code="en" >What's your prefered pet?</lang>
		</question>
	</reactivationQuestions>
</regProfileConfig>


 * @author S.Puddu
 * @author E.Mezzano
 * @author G.Cocco
 */
public class UserRegConfigDOM {
	
	public UserRegConfigDOM() {
	}
	
	public UserRegConfigDOM(String xml) throws ApsSystemException {
		Document doc = this.decodeDOM(xml);
		Element root = doc.getRootElement();
		IUserRegConfig config = this.getConfig();
		this.extractProfileParamsConfig(root, config);
		this.extractTokenValidityConfig(root, config);
		this.extractMailSenderConfig(root, config);
		this.extractActivationMailConfig(root, config);
		this.extractReactivationMailConfig(root, config);
		this.extractUserAuthDefaults(root, config);
		this.extractActivatedMailConfig(root, config);
	}
	
	protected void extractUserAuthDefaults(Element root, IUserRegConfig config) {
		Element userAuths = root.getChild("userAuthDefaults");
		if (null != userAuths) {
			List<Element> auths = userAuths.getChildren();
			Iterator<Element> it = auths.iterator();
			while (it.hasNext()) {
				Element current = (Element) it.next();
				String name = current.getName();
				if (name.equals("role")) {
					String role = current.getAttributeValue("name");
					config.addRole(role);
				} else if (name.equals("group")) {
					String group = current.getAttributeValue("name");
					config.addGroup(group);
				}
			}
		}
	}
	
	protected void extractProfileParamsConfig(Element root, IUserRegConfig config) {
		Element profileElem = root.getChild("profileEntity");
		config.setProfileNameAttr(profileElem.getAttributeValue("nameAttr"));
		config.setProfileSurnameAttr(profileElem.getAttributeValue("surnameAttr"));
		config.setProfileEMailAttr(profileElem.getAttributeValue("eMailAttr"));
		config.setProfileLangAttr(profileElem.getAttributeValue("langAttr"));
	}
	
	protected void extractTokenValidityConfig(Element root, IUserRegConfig config) {
		Element tokenElem = root.getChild("tokenValidity");
		config.setTokenValidityMinutes(Long.parseLong(tokenElem.getAttributeValue("minutes")));
	}
	
	protected void extractMailSenderConfig(Element root, IUserRegConfig config) {
		Element tokenElem = root.getChild("sender");
		config.setEMailSenderCode(tokenElem.getAttributeValue("code"));
	}
	
	protected void extractActivationMailConfig(Element root, IUserRegConfig config) {
		Element activationElem = root.getChild("activation");
		config.setActivationPageCode(activationElem.getAttributeValue("pageCode"));
		List<Element> templates = activationElem.getChildren("template");
		Iterator<Element> it = templates.iterator();
		while (it.hasNext()) {
			Element template = it.next();
			String lang = template.getAttributeValue("lang");
			String subject = template.getChildText("subject");
			config.addActivationMailSubject(lang, subject);
			String body = template.getChildText("body");
			config.addActivationMailBody(lang, body);
		}
	}
	
	protected void extractReactivationMailConfig(Element root, IUserRegConfig config) {
		Element activationElem = root.getChild("reactivation");
		config.setReactivationPageCode(activationElem.getAttributeValue("pageCode"));
		List<Element> templates = activationElem.getChildren("template");
		Iterator<Element> it = templates.iterator();
		while (it.hasNext()) {
			Element template = it.next();
			String lang = template.getAttributeValue("lang");
			String subject = template.getChildText("subject");
			config.addReactivationMailSubject(lang, subject);
			String body = template.getChildText("body");
			config.addReactivationMailBody(lang, body);
		}
	}
	
	protected void extractActivatedMailConfig(Element root, IUserRegConfig config) {
		Element activationElem = root.getChild("reactivated");
		config.setUserReactivatedPageCode(activationElem.getAttributeValue("pageCode"));
		List<Element> templates = activationElem.getChildren("template");
		Iterator<Element> it = templates.iterator();
		while (it.hasNext()) {
			Element template = it.next();
			String lang = template.getAttributeValue("lang");
			String subject = template.getChildText("subject");
			config.addUserReactivatedMailSubject(lang, subject);
			String body = template.getChildText("body");
			config.addUserReactivatedMailBody(lang, body);
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
	
	public IUserRegConfig getConfig() {
		return _config;
	}
	
	private IUserRegConfig _config = new UserRegConfig();
	
}