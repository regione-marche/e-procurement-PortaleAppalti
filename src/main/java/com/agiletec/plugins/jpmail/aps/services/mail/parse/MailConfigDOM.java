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
package com.agiletec.plugins.jpmail.aps.services.mail.parse;

import java.io.StringReader;
import java.util.List;
import java.util.Map.Entry;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.util.DefaultApsEncrypter;
import com.agiletec.plugins.jpmail.aps.services.mail.MailConfig;

/*
<mailConfig>
	<senders>
		<sender code="CODE1">EMAIL1@EMAIL.COM</sender>
		<sender code="CODE2">EMAIL2@EMAIL.COM</sender>
	</senders>
	<smtp debug="false" >
		<host>SMTP.EMAIL.COM</host>
		<port>25</port>
		<timeout>10000</timeout>
		<user>USER</user>
		<password>PASSWORD</password>
	</smtp>
</mailConfig>
 */

/**
 * Class that provides read and update operations for the jpmail plugin xml configuration.
 * 
 * @version 1.0
 * @author E.Santoboni, E.Mezzano
 */
public class MailConfigDOM {
	
	/**
	 * Extract the jpmail configuration from an xml.
	 * @param xml The xml containing the configuration.
	 * @return The jpmail configuration.
	 * @throws ApsSystemException In case of parsing errors.
	 */
	public MailConfig extractConfig(String xml) throws ApsSystemException {
		MailConfig config = new MailConfig();
		Element root = this.getRootElement(xml);
		this.extractSenders(root, config);
		this.extractSmtp(root, config);
		return config;
	}
	
	/**
	 * Create an xml containing the jpmail configuration.
	 * @param config The jpmail configuration.
	 * @return The xml containing the configuration.
	 * @throws ApsSystemException In case of errors.
	 */
	public String createConfigXml(MailConfig config) throws ApsSystemException {
		Element root = this.createConfigElement(config);
		Document doc = new Document(root);
		String xml = new XMLOutputter().outputString(doc);
		return xml;
	}
	
	/**
	 * Extract the senders from the xml element and save it into the MailConfig object.
	 * @param root The xml root element containing the senders configuration.
	 * @param config The configuration.
	 */
	private void extractSenders(Element root, MailConfig config) {
		Element sendersRootElem = root.getChild(SENDERS_ELEM);
		if (sendersRootElem!=null) {
			List sendersElem = sendersRootElem.getChildren(SENDER_CHILD);
			for (int i=0; i<sendersElem.size(); i++) {
				Element senderElem = (Element) sendersElem.get(i);
				String code = senderElem.getAttributeValue(SENDER_CODE_ATTR);
				String sender = senderElem.getText();
				config.addSender(code, sender);
			}
		}
	}
	
	/**
	 * Extract the smtp configuration from the xml element and save it into the MailConfig object.
	 * @param root The xml root element containing the smtp configuration.
	 * @param config The configuration.
	 */
	private void extractSmtp(Element root, MailConfig config) {
		Element smtpElem = root.getChild(SMTP_ELEM);
		if (smtpElem!=null) {
			String debug = smtpElem.getAttributeValue(SMTP_DEBUG_ATTR);
			config.setDebug("true".equalsIgnoreCase(debug));
			config.setSmtpHost(smtpElem.getChildText(SMTP_HOST_CHILD));
			String port = smtpElem.getChildText(SMTP_PORT_CHILD);
			if (port != null && port.trim().length()>0) {
				config.setSmtpPort(new Integer(port.trim()));
			}
			config.setSmtpProtocol(smtpElem.getChildText(SMTP_PROTOCOL_CHILD));
			String timeout = smtpElem.getChildText(SMTP_TIMEOUT_CHILD);
			if (timeout != null && timeout.trim().length()>0) {
				config.setSmtpTimeout(new Integer(timeout.trim()));
			}
			config.setSmtpUserName(smtpElem.getChildText(SMTP_USER_CHILD));
			String password = smtpElem.getChildText(SMTP_PASSWORD_CHILD);
			if (password != null && password.length() > 0) {
				try {
					config.setSmtpPassword(DefaultApsEncrypter.decrypt(password));
				} catch (RuntimeException e) {
					// nel caso dia errore la password criptata, si prova a vedere
					// se e' ancora impostata la password in chiaro
					config.setSmtpPassword(smtpElem
							.getChildText(SMTP_PASSWORD_CHILD));
				}
			}
		}
	}
	
	/**
	 * Extract the smtp configuration from the xml element and save it into the MailConfig object.
	 * @param root The xml root element containing the smtp configuration.
	 * @param config The configuration.
	 * @throws ApsSystemException 
	 */
	private Element createConfigElement(MailConfig config) throws ApsSystemException {
		Element configElem = new Element(ROOT);
		Element sendersElem = this.createSendersElement(config);
		configElem.addContent(sendersElem);
		Element smtpElem = this.createSmtpElement(config);
		configElem.addContent(smtpElem);
		return configElem;
	}
	
	/**
	 * Create the senders element starting from the given MailConfig.
	 * @param config The configuration.
	 */
	private Element createSendersElement(MailConfig config) {
		Element sendersElem = new Element(SENDERS_ELEM);
		for (Entry<String, String> senderEntry : config.getSenders().entrySet()) {
			Element senderElement = new Element(SENDER_CHILD);
			senderElement.setAttribute(SENDER_CODE_ATTR, (String) senderEntry.getKey());
			senderElement.addContent((String) senderEntry.getValue());
			sendersElem.addContent(senderElement);
		}
		return sendersElem;
	}
	
	/**
	 * Create the smtp element starting from the given MailConfig.
	 * @param config The configuration.
	 * @throws ApsSystemException 
	 */
	private Element createSmtpElement(MailConfig config) throws ApsSystemException {
		Element smtpElem = new Element(SMTP_ELEM);
		smtpElem.setAttribute(SMTP_DEBUG_ATTR, String.valueOf(config.isDebug()));
		
		Element hostElem = new Element(SMTP_HOST_CHILD);
		hostElem.addContent(config.getSmtpHost());
		smtpElem.addContent(hostElem);
		
		Element protocolElem = new Element(SMTP_PROTOCOL_CHILD);
		protocolElem.addContent(config.getSmtpProtocol());
		smtpElem.addContent(protocolElem);

		Integer port = config.getSmtpPort();
		if (null != port && port.intValue() != 0) {
			Element portElem = new Element(SMTP_PORT_CHILD);
			portElem.addContent(config.getSmtpPort().toString());
			smtpElem.addContent(portElem);
		}
		
		Integer timeout = config.getSmtpTimeout();
		if (null != timeout && timeout.intValue() != 0) {
			Element timeoutElem = new Element(SMTP_TIMEOUT_CHILD);
			timeoutElem.addContent(config.getSmtpTimeout().toString());
			smtpElem.addContent(timeoutElem);
		}
		
		Element userElem = new Element(SMTP_USER_CHILD);
		userElem.addContent(config.getSmtpUserName());
		smtpElem.addContent(userElem);
		
		Element passwordElem = new Element(SMTP_PASSWORD_CHILD);
		if (config.getSmtpPassword() != null && config.getSmtpPassword().length() > 0) {
			passwordElem.addContent(DefaultApsEncrypter.encryptString(config
					.getSmtpPassword()));
			smtpElem.addContent(passwordElem);
		}
		
		return smtpElem;
	}
	
	/**
	 * Returns the Xml element from a given text.
	 * @param xmlText The text containing an Xml.
	 * @return The Xml element from a given text.
	 * @throws ApsSystemException In case of parsing exceptions.
	 */
	private Element getRootElement(String xmlText) throws ApsSystemException {
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(false);
		StringReader reader = new StringReader(xmlText);
		Element root = null;
		try {
			Document doc = builder.build(reader);
			root = doc.getRootElement();
		} catch (Throwable t) {
			ApsSystemUtils.getLogger().error("Error parsing xml: " + t.getMessage());
			throw new ApsSystemException("Error parsing xml", t);
		}
		return root;
	}
	
	private final String ROOT = "mailConfig";
	
	private final String SENDERS_ELEM = "senders";
	private final String SENDER_CHILD = "sender";
	private final String SENDER_CODE_ATTR = "code";
	
	private final String SMTP_ELEM = "smtp";
	private final String SMTP_DEBUG_ATTR = "debug";
	private final String SMTP_HOST_CHILD = "host";
	private final String SMTP_PORT_CHILD = "port";
	private final String SMTP_PROTOCOL_CHILD = "protocol";
	private final String SMTP_TIMEOUT_CHILD = "timeout";
	private final String SMTP_USER_CHILD = "user";
	private final String SMTP_PASSWORD_CHILD = "password";
	//private final String SMTP_PEC_ID_CHILD = "pecId";
	
}