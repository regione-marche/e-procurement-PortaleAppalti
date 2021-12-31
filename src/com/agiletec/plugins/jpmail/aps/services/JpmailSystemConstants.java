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
package com.agiletec.plugins.jpmail.aps.services;

/**
 * Class containing constants for the jpmail plugin.
 * 
 * @version 1.0
 * @author E.Mezzano
 *
 */
public interface JpmailSystemConstants {
	
	/**
	 * Name of the service delegated to send eMail.
	 */
	public static final String MAIL_MANAGER = "jpmailMailManager";
	
	/**
	 * Name of the sysconfig parameter containing the Plugin configuration
	 */
	public static final String MAIL_CONFIG_ITEM = "jpmail_config";
	
	/**
	 * Code for SMTP protocol 
	 */
	public static final String PROTOCOL_SMTP = "SMTP";
	
	/**
	 * Code for SMTPS protocol (SMTP + SSL)
	 */
	public static final String PROTOCOL_SMTPS = "SMTPS";
	
	/**
	 * Code for STARTTLS protocol (SMTP + TLS)
	 */
	public static final String PROTOCOL_STARTTLS = "STARTTLS";

	/**
	 * Protocol array
	 */
	public static final String[] AVAILABLE_PROTOCOLS = { PROTOCOL_SMTP,
	    PROTOCOL_SMTPS, PROTOCOL_STARTTLS };
}