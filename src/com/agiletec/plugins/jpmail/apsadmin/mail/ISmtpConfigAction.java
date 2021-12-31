package com.agiletec.plugins.jpmail.apsadmin.mail;

public interface ISmtpConfigAction {
	
	/**
	 * Execute the action of editing of the IMailManager service configuration.
	 * @return The action result code.
	 */
	public String edit();
	
	/**
	 * Execute the action of saving of the IMailManager service configuration.
	 * @return The action result code.
	 */
	public String save();
	
}