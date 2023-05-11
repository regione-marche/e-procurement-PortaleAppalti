package com.agiletec.plugins.jpmail.apsadmin.mail;

import java.util.ArrayList;
import java.util.List;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.apsadmin.system.BaseAction;
import com.agiletec.apsadmin.util.SelectItem;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jpmail.aps.services.JpmailSystemConstants;
import com.agiletec.plugins.jpmail.aps.services.mail.IMailManager;
import com.agiletec.plugins.jpmail.aps.services.mail.MailConfig;

public class SmtpConfigAction extends BaseAction implements ISmtpConfigAction {
	
	@Override
	public String edit() {
		try {
			MailConfig config = this.getMailManager().getMailConfig();
			this.populateForm(config);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "edit");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	@Override
	public String save() {
		try {
			MailConfig config = this.prepareConfig();
			this.getMailManager().updateMailConfig(config);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "save");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	/**
	 * Populate the action with the content of the given MailConfig.
	 * @param config The configuration used to populate the action.
	 */
	protected void populateForm(MailConfig config) {
		if (config!=null) {
			this.setDebug(config.isDebug());
			this.setSmtpHost(config.getSmtpHost());
			this.setSmtpPort(config.getSmtpPort());
			this.setSmtpProtocol(config.getSmtpProtocol());
			this.setSmtpTimeout(config.getSmtpTimeout());
			this.setSmtpUserName(config.getSmtpUserName());
			this.setSmtpPassword(config.getSmtpPassword());
		} else {
			config = new MailConfig();
		}
	}
	
	/**
	 * Prepares a MailConfig starting from the action form fields.
	 * @return a MailConfig starting from the action form fields.
	 * @throws ApsSystemException In case of errors.
	 */
	protected MailConfig prepareConfig() throws ApsSystemException {
		MailConfig config = this.getMailManager().getMailConfig();
		config.setDebug(this.isDebug());
		config.setSmtpHost(this.getSmtpHost());
		config.setSmtpPort(this.getSmtpPort());
		config.setSmtpProtocol(this.getSmtpProtocol());
		config.setSmtpTimeout(this.getSmtpTimeout());
		config.setSmtpUserName(this.getSmtpUserName());
		config.setSmtpPassword(this.getSmtpPassword());
		return config;
	}
	
	/**
	 * Returns the list of available protocols defined per sending messages
	 * @return Lista of available protocols.
	 */
	public List<SelectItem> getAvailableProtocols() {
		String[] protocols = JpmailSystemConstants.AVAILABLE_PROTOCOLS;
		List<SelectItem> items = new ArrayList<SelectItem>(protocols.length);
		for (int i = 0; i < protocols.length; i++) {
			SelectItem item = new SelectItem(protocols[i], "name.smtpProtocol." + protocols[i]);
			items.add(item);
		}
		return items;
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
	 * Returns the smtp username.
	 * @return The smtp username.
	 */
	public String getSmtpUserName() {
		return _smtpUserName;
	}
	/**
	 * Sets the smtp username.
	 * @param smtpUserName The smtp username.
	 */
	public void setSmtpUserName(String smtpUserName) {
		this._smtpUserName = smtpUserName;
	}
	
	/**
	 * Returns the smtp password.
	 * @return The smtp password.
	 */
	public String getSmtpPassword() {
		return _smtpPassword;
	}
	/**
	 * Sets the smtp password.
	 * @param smtpPassword The smtp password.
	 */
	public void setSmtpPassword(String smtpPassword) {
		this._smtpPassword = smtpPassword;
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
	
	/**
	 * Returns the IMailManager service.
	 * @return The IMailManager service.
	 */
	public IMailManager getMailManager() {
		return _mailManager;
	}
	/**
	 * Set method for Spring bean injection.<br /> Sets the IMailManager service.
	 * @param mailManager The IMailManager service.
	 */
	public void setMailManager(IMailManager mailManager) {
		this._mailManager = mailManager;
	}
	
	private String _smtpHost;
	private Integer _smtpPort;
	private String _smtpProtocol;
	private Integer _smtpTimeout;
	private String _smtpUserName;
	private String _smtpPassword;
	private boolean _debug;
	
	private IMailManager _mailManager;
	
}