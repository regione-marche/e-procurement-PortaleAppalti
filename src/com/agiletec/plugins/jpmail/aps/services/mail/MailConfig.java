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
package com.agiletec.plugins.jpmail.aps.services.mail;

import java.util.Map;
import java.util.TreeMap;

/**
 * Bean class containing the basic configuration for the IMailManager service.
 * @version 1.0
 * @author E.Santoboni, E.Mezzano
 */
public class MailConfig implements Cloneable {
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		MailConfig config = new MailConfig();
		config.setDebug(this.isDebug());
		config.setSmtpHost(this.getSmtpHost());
		config.setSmtpPort(this.getSmtpPort());
		config.setSmtpTimeout(this.getSmtpTimeout());
		config.setSmtpUserName(this.getSmtpUserName());
		config.setSmtpPassword(this.getSmtpPassword());
		config.setSmtpProtocol(this.getSmtpProtocol());
		config.setSenders(new TreeMap<String, String>(this.getSenders()));
		return config;
	}
	
	/**
	 * Return true if mail configuration expects an anonymous authentication.
	 * @return
	 */
	public boolean hasAnonimousAuth() {
		return (null == this._smtpUserName || this._smtpUserName.length() == 0);
	}
	
	/**
	 * Returns the smtp host name.
	 * @return The smtp host name.
	 */
	public String getSmtpHost() {
		return _smtpHost;
	}
	/**
	 * Sets the smtp host name.
	 * @param smtpHost The smtp host name.
	 */
	public void setSmtpHost(String smtpHost) {
		this._smtpHost = smtpHost;
	}
	
	/**
	 * Return the smtp port.
	 * @return The smtp port.
	 */
	public Integer getSmtpPort() {
		return _smtpPort;
	}
	/**
	 * Sets the smtp port.
	 * @param port The smtp port.
	 */
	public void setSmtpPort(Integer smtpPort) {
		this._smtpPort = smtpPort;
	}
	
	/**
	 * Return the smtp protocol.
	 * @return The smtp protocol.
	 */
	public String getSmtpProtocol() {
	    return _smtpProtocol;
	}

	/**
	 * Sets the protocol for the smtp access.
	 * @param pecId The protocol for the smtp access.
	 */
	public void setSmtpProtocol(String protocol) {
	    _smtpProtocol = protocol;
	}

	/**
	 * Return the smtp timeout.
	 * @return The smtp timeout.
	 */
	public Integer getSmtpTimeout() {
		return _smtpTimeout;
	}
	/**
	 * Sets the smtp timeout. If 0 or null uses default.
	 * @param port The smtp timeout.
	 */
	public void setSmtpTimeout(Integer smtpTimeout) {
		this._smtpTimeout = smtpTimeout;
	}
	
	/**
	 * Returns the password for the smtp access.
	 * @return The password for the smtp access.
	 */
	public String getSmtpPassword() {
		return _smtpPassword;
	}
	/**
	 * Sets the password for the smtp access.
	 * @param smtpPassword The password for the smtp access.
	 */
	public void setSmtpPassword(String smtpPassword) {
		this._smtpPassword = smtpPassword;
	}
	
	/**
	 * Returns the username for the smtp access.
	 * @return The username for the smtp access.
	 */
	public String getSmtpUserName() {
		return _smtpUserName;
	}
	/**
	 * Sets the username for the smtp access.
	 * @param smtpUserName The username for the smtp access.
	 */
	public void setSmtpUserName(String smtpUserName) {
		this._smtpUserName = smtpUserName;
	}
	
	/**
	 * Returns the senders, mapped as code/address.
	 * @return The senders, mapped as code/address.
	 */
	public Map<String, String> getSenders() {
		return _senders;
	}
	/**
	 * Sets the senders, mapped as code/address.
	 * @param senders The senders, mapped as code/address.
	 */
	public void setSenders(Map<String, String> senders) {
		this._senders = senders;
	}
	/**
	 * Add a sender address.
	 * @param code The code of the sender.
	 * @param sender The address of the sender.
	 */
	public void addSender(String code, String sender) {
		this._senders.put(code, sender);
	}
	/**
	 * The sender address with given code.
	 * @param code the code of the sender.
	 * @return The sender with given code.
	 */
	public String getSender(String code) {
		return (String) this._senders.get(code);
	}
	
	/**
	 * Returns the debug flag, used to trace debug informations.
	 * @return The debug flag.
	 */
	public boolean isDebug() {
		return _debug;
	}
	/**
	 * Sets the debug flag, used to trace debug informations.
	 * @param debug The debug flag.
	 */
	public void setDebug(boolean debug) {
		this._debug = debug;
	}
	
	private Map<String, String> _senders = new TreeMap<String, String>();
	private String _smtpHost;
	private Integer _smtpPort;
	private String _smtpProtocol;
	private Integer _smtpTimeout;
	private String _smtpUserName;
	private String _smtpPassword;
	private boolean _debug;
	
}