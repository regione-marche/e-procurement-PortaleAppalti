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

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.AbstractService;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import com.agiletec.plugins.jpmail.aps.services.JpmailSystemConstants;
import com.agiletec.plugins.jpmail.aps.services.mail.parse.MailConfigDOM;
import com.sun.mail.smtp.SMTPMessage;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MimetypesFileTypeMap;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * Implementation for the manager providing email sending functions.
 * @version 1.0
 * @author E.Santoboni, E.Mezzano
 */
public class MailManager extends AbstractService implements IMailManager {
	
	/* (non-Javadoc)
	 * @see com.agiletec.aps.system.services.IManager#init()
	 */
	public void init() throws Exception {
		this.loadConfigs();
		ApsSystemUtils.getLogger().debug(
				this.getClass().getName() + ": inizializzato");
	}
	
	/**
	 * Load the XML configuration containing smtp configuration and the sender addresses.
	 * @throws ApsSystemException
	 */
	private void loadConfigs() throws ApsSystemException {
		try {
			ConfigInterface configManager = this.getConfigManager();
			String xml = configManager.getConfigItem(JpmailSystemConstants.MAIL_CONFIG_ITEM);
			if (xml == null) {
				throw new ApsSystemException("Configuration item not present: " + JpmailSystemConstants.MAIL_CONFIG_ITEM);
			}
			MailConfigDOM configDOM = new MailConfigDOM();
			this.setConfig(configDOM.extractConfig(xml));
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "loadConfigs");
			throw new ApsSystemException("Errore in fase di inizializzazione", t);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.agiletec.plugins.jpmail.aps.services.mail.IMailManager#getMailConfig()
	 */
	@Override
	public MailConfig getMailConfig() throws ApsSystemException {
		try {
			return (MailConfig) this._config.clone();
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getMailConfig");
			throw new ApsSystemException("Error loading mail service configuration", t);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.agiletec.plugins.jpmail.aps.services.mail.IMailManager#updateMailConfig(com.agiletec.plugins.jpmail.aps.services.mail.MailConfig)
	 */
	@Override
	public void updateMailConfig(MailConfig config) throws ApsSystemException {
		try {
			String xml = new MailConfigDOM().createConfigXml(config);
			this.getConfigManager().updateConfigItem(JpmailSystemConstants.MAIL_CONFIG_ITEM, xml);
			this.setConfig(config);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "updateMailConfig");
			throw new ApsSystemException("Errore in fase di aggiornamento configurazione mail", t);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.agiletec.plugins.jpmail.aps.services.mail.IMailManager#sendMail(java.lang.String, java.lang.String, java.lang.String[], java.lang.String[], java.lang.String[], java.lang.String)
	 */
	@Override
	public boolean sendMail(String text, String subject, String[] recipientsTo, 
			String[] recipientsCc, String[] recipientsBcc, String senderCode) throws ApsSystemException {
		return this.sendMail(text, subject, CONTENTTYPE_TEXT_PLAIN, (Properties)null, recipientsTo, recipientsCc, recipientsBcc, senderCode);
	}
	
	/* (non-Javadoc)
	 * @see com.agiletec.plugins.jpmail.aps.services.mail.IMailManager#sendMail(java.lang.String, java.lang.String, java.lang.String[], java.lang.String[], java.lang.String[], java.lang.String, java.lang.String)
	 */
	@Override
	public boolean sendMail(String text, String subject, String[] recipientsTo, 
			String[] recipientsCc, String[] recipientsBcc, String senderCode, String contentType) throws ApsSystemException {
		return this.sendMail(text, subject, contentType, (Properties)null, recipientsTo, recipientsCc, recipientsBcc, senderCode);
	}
	
	/* (non-Javadoc)
	 * @see com.agiletec.plugins.jpmail.aps.services.mail.IMailManager#sendMail(java.lang.String, java.lang.String, java.lang.String, java.util.Properties, java.lang.String[], java.lang.String[], java.lang.String[], java.lang.String)
	 */
	@Override
	public boolean sendMail(String text, String subject, String contentType, Properties attachmentFiles, String[] recipientsTo, 
			String[] recipientsCc, String[] recipientsBcc, String senderCode) throws ApsSystemException {
		Transport bus = null;
		ByteArrayOutputStream log =  null;
		try {
			Session session = this.prepareSession();
			if (session.getDebug()) {
				log = new ByteArrayOutputStream();
				session.setDebugOut(new PrintStream(log));
			}
			bus = this.prepareTransport(session);
			MimeMessage msg = this.prepareVoidMimeMessage(session, subject, recipientsTo, recipientsCc, recipientsBcc, senderCode);
			
			if (attachmentFiles==null || attachmentFiles.isEmpty()) {
				msg.setContent(text, contentType+"; charset=utf-8");
			} else {
				Multipart multiPart = new MimeMultipart();
				this.addBodyPart(text, contentType, multiPart);
				this.addAttachments(attachmentFiles, multiPart);
				msg.setContent(multiPart);
			}
			msg.saveChanges();
			
			if (JpmailSystemConstants.PROTOCOL_SMTPS.equals(this._config.getSmtpProtocol()) || 
				JpmailSystemConstants.PROTOCOL_STARTTLS.equals(this._config.getSmtpProtocol())) {
				        SMTPMessage msgWrapped = new SMTPMessage(msg);
				        msgWrapped.setAllow8bitMIME(true);
				        // Send notification on Failure - no way to set envid in Java yet
				        msgWrapped.setNotifyOptions(SMTPMessage.NOTIFY_FAILURE);
				        // Bounce only header
				        msgWrapped.setReturnOption(SMTPMessage.RETURN_HDRS);

				        msgWrapped.saveChanges();
				        bus.sendMessage(msgWrapped, msgWrapped.getAllRecipients());
			} else {
			    bus.sendMessage(msg, msg.getAllRecipients());
			}
		} catch (Throwable t) {
			throw new ApsSystemException("Errore in spedizione mail", t);
		} finally {
			if (log != null) {
				ApsSystemUtils.getLogger().info(
						"JAVAMAIL debug mode ON\n" + log.toString());
				try {
					log.close();
				} catch (IOException e) {
				}
			}
			closeTransport(bus);
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see com.agiletec.plugins.jpmail.aps.services.mail.IMailManager#sendMail(java.lang.String, java.lang.String, java.lang.String, java.util.Properties, java.lang.String[], java.lang.String[], java.lang.String[], java.lang.String)
	 */
	@Override
	public boolean sendMail(String text, String subject, String contentType, Map<String,byte[]> attachmentFiles, String[] recipientsTo, 
			String[] recipientsCc, String[] recipientsBcc, String senderCode) throws ApsSystemException {
		Transport bus = null;
		ByteArrayOutputStream log =  null;
		try {
			Session session = this.prepareSession();
			if (session.getDebug()) {
				log = new ByteArrayOutputStream();
				session.setDebugOut(new PrintStream(log));
			}
			bus = this.prepareTransport(session);
			MimeMessage msg = this.prepareVoidMimeMessage(session, subject, recipientsTo, recipientsCc, recipientsBcc, senderCode);
			
			if (attachmentFiles==null || attachmentFiles.isEmpty()) {
				msg.setContent(text, contentType+"; charset=utf-8");
			} else {
				Multipart multiPart = new MimeMultipart();
				this.addBodyPart(text, contentType, multiPart);
				this.addAttachments(attachmentFiles, multiPart);
				msg.setContent(multiPart);
			}
			msg.saveChanges();
			
			if (JpmailSystemConstants.PROTOCOL_SMTPS.equals(this._config.getSmtpProtocol()) || 
				JpmailSystemConstants.PROTOCOL_STARTTLS.equals(this._config.getSmtpProtocol())) {
				        SMTPMessage msgWrapped = new SMTPMessage(msg);
				        msgWrapped.setAllow8bitMIME(true);
				        // Send notification on Failure - no way to set envid in Java yet
				        msgWrapped.setNotifyOptions(SMTPMessage.NOTIFY_FAILURE);
				        // Bounce only header
				        msgWrapped.setReturnOption(SMTPMessage.RETURN_HDRS);

				        msgWrapped.saveChanges();
				        bus.sendMessage(msgWrapped, msgWrapped.getAllRecipients());
			} else {
			   bus.sendMessage(msg, msg.getAllRecipients());
			}
		} catch (Throwable t) {
			throw new ApsSystemException("Errore in spedizione mail", t);
		} finally {
			if (log != null) {
				ApsSystemUtils.getLogger().info(
						"JAVAMAIL debug mode ON\n" + log.toString());
				try {
					log.close();
				} catch (IOException e) {
				}
			}
			closeTransport(bus);
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see com.agiletec.plugins.jpmail.aps.services.mail.IMailManager#sendMixedMail(java.lang.String, java.lang.String, java.lang.String, java.util.Properties, java.lang.String[], java.lang.String[], java.lang.String[], java.lang.String)
	 */
	@Override
	public boolean sendMixedMail(String simpleText, String htmlText, String subject, Properties attachmentFiles, 
			String[] recipientsTo, String[] recipientsCc, String[] recipientsBcc, String senderCode) throws ApsSystemException {
		Transport bus = null;
		ByteArrayOutputStream log =  null;
		try {
			Session session = this.prepareSession();
			if (session.getDebug()) {
				log = new ByteArrayOutputStream();
				session.setDebugOut(new PrintStream(log));
			}
			bus = this.prepareTransport(session);
			MimeMessage msg = this.prepareVoidMimeMessage(session, subject, recipientsTo, recipientsCc, recipientsBcc, senderCode);
			
			boolean hasAttachments = attachmentFiles!=null && attachmentFiles.size()>0;
			String multipartMimeType = hasAttachments ? "mixed" : "alternative";
			MimeMultipart multiPart = new MimeMultipart(multipartMimeType);
			
			this.addBodyPart(simpleText, CONTENTTYPE_TEXT_PLAIN, multiPart);
			this.addBodyPart(htmlText, CONTENTTYPE_TEXT_HTML, multiPart);
			if (hasAttachments) {
				this.addAttachments(attachmentFiles, multiPart);
			}
			
			msg.setContent(multiPart);
			msg.saveChanges();

			if (JpmailSystemConstants.PROTOCOL_SMTPS.equals(this._config.getSmtpProtocol()) || 
				JpmailSystemConstants.PROTOCOL_STARTTLS.equals(this._config.getSmtpProtocol())) {
				        SMTPMessage msgWrapped = new SMTPMessage(msg);
				        msgWrapped.setAllow8bitMIME(true);
				        // Send notification on Failure - no way to set envid in Java yet
				        msgWrapped.setNotifyOptions(SMTPMessage.NOTIFY_FAILURE);
				        // Bounce only header
				        msgWrapped.setReturnOption(SMTPMessage.RETURN_HDRS);

				        msgWrapped.saveChanges();
				        bus.sendMessage(msgWrapped, msgWrapped.getAllRecipients());
			} else {
			    bus.sendMessage(msg, msg.getAllRecipients());
			}
		} catch (Throwable t) {
			throw new ApsSystemException("Errore in spedizione mail", t);
		} finally {
			if (log != null) {
				ApsSystemUtils.getLogger().info(
						"JAVAMAIL debug mode ON\n" + log.toString());
				try {
					log.close();
				} catch (IOException e) {
				}
			}
			closeTransport(bus);
		}
		return true;
	}
	
	/**
	 * Prepare a Transport object ready for use.
	 * @param session A session object.
	 * @return The Transport object ready for use.
	 * @throws Exception In case of errors opening the Transport object.
	 */
	protected Transport prepareTransport(Session session) throws Exception {
		MailConfig config = this.getConfig();
		Transport bus = session.getTransport("smtp");
		if (config.hasAnonimousAuth()) {
			bus.connect();
		} else {
			Integer port = config.getSmtpPort();
			if (port != null && port.intValue() > 0) {
				bus.connect(config.getSmtpHost(), port.intValue(), config.getSmtpUserName(), config.getSmtpPassword());
			} else {
				bus.connect(config.getSmtpHost(), config.getSmtpUserName(), config.getSmtpPassword());
			}
		}
		return bus;
	}
	
	/**
	 * Prepare a Session object ready for use.
	 * @return The Session object ready for use.
	 */
	protected Session prepareSession() {
		MailConfig config = this.getConfig();
	    Properties props = new Properties();
		if (JpmailSystemConstants.PROTOCOL_SMTPS.equals(config.getSmtpProtocol())
				|| JpmailSystemConstants.PROTOCOL_STARTTLS.equals(config.getSmtpProtocol())) {
			props = System.getProperties();
		}
		props.put("mail.transport.protocol", "smtp");
		// host
		props.put("mail.smtp.host", config.getSmtpHost());
		// Port
		int port = 25;
		Integer portParam = config.getSmtpPort();
		if (null != portParam && portParam.intValue() != 0) {
		    // when port is cleared, in Session object created it remains the old value (not empty)
		    // => it is always important to set the standard value (25) if param is empty
		    // so the Session contains the new (default) value
		    port = portParam;
		}
		props.put("mail.smtp.port", port);
		if (config.hasAnonimousAuth()) {
			props.put("mail.smtp.auth", "false");
		} else {
		    props.put("mail.smtp.auth", "true");
		    // authentication needed: test the protocol and eventually add specific properties
		    if (JpmailSystemConstants.PROTOCOL_SMTPS.equals(config.getSmtpProtocol())) {
				props.put("mail.smtp.ssl.checkserveridentity", true); // Compliant
				props.put("mail.smtp.socketFactory.class",
		              "javax.net.ssl.SSLSocketFactory");
		    } else if (JpmailSystemConstants.PROTOCOL_STARTTLS.equals(config.getSmtpProtocol())) {
				props.put("mail.smtp.ssl.checkserveridentity", true); // Compliant
				props.put("mail.smtp.starttls.enable", "true");
		    }
		}
		// Timeout
		int timeout = DEFAULT_SMTP_TIMEOUT;
		Integer timeoutParam = config.getSmtpTimeout();
		if (null != timeoutParam && timeoutParam.intValue() != 0) {
			timeout = timeoutParam;
		}
		props.put("mail.smtp.connectiontimeout", timeout);
		props.put("mail.smtp.timeout", timeout);
		// Debug
		if (config.isDebug()) {
			props.put("mail.debug", "true");
		}

		return Session.getInstance(props);
	}
	
	/**
	 * Prepare a MimeMessage complete of sender, recipient addresses, subject and current date but lacking in the message text content.
	 * @param session The session object.
	 * @param subject The e-mail subject.
	 * @param recipientsTo The e-mail main destination addresses.
	 * @param recipientsCc The e-mail 'carbon-copy' destination addresses.
	 * @param recipientsBcc The e-mail 'blind carbon-copy' destination addresses.
	 * @param senderCode The sender code, as configured in the service configuration.
	 * @return A mime message without message text content.
	 * @throws AddressException In case of non-valid e-mail addresses.
	 * @throws MessagingException In case of errors preparing the mail message.
	 */
	protected MimeMessage prepareVoidMimeMessage(Session session, String subject, String[] recipientsTo, 
			String[] recipientsCc, String[] recipientsBcc, String senderCode) throws AddressException, MessagingException {
		MimeMessage msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(this.getConfig().getSender(senderCode)));
		msg.setSubject(subject);
		msg.setSentDate(new Date());
		
		this.addRecipients(msg, Message.RecipientType.TO, recipientsTo);
		this.addRecipients(msg, Message.RecipientType.CC, recipientsCc);
		this.addRecipients(msg, Message.RecipientType.BCC, recipientsBcc);
		
		msg.saveChanges();
		return msg;
	}
	
	/**
	 * Add a BodyPart to the Multipart container.
	 * @param text The text content.
	 * @param contentType The text contentType.
	 * @param multiPart The Multipart container.
	 * @throws MessagingException In case of errors adding the body part.
	 */
	protected void addBodyPart(String text, String contentType, Multipart multiPart) throws MessagingException {
		MimeBodyPart textBodyPart = new MimeBodyPart();
		textBodyPart.setContent(text, contentType+"; charset=utf-8");
		multiPart.addBodyPart(textBodyPart);
	}
	
	/**
	 * Add the attachments to the Multipart container.
	 * @param attachmentFiles The attachments mapped as fileName/filePath.
	 * @param multiPart The Multipart container.
	 * @throws MessagingException In case of errors adding the attachments.
	 */
	protected void addAttachments(Properties attachmentFiles, Multipart multiPart) throws MessagingException {
		Iterator filesIter = attachmentFiles.entrySet().iterator();
		while (filesIter.hasNext()) {
			Entry fileEntry = (Entry) filesIter.next();
			MimeBodyPart fileBodyPart = new MimeBodyPart();
			DataSource dataSource = new FileDataSource((String) fileEntry.getValue());
			fileBodyPart.setDataHandler(new DataHandler(dataSource));
			fileBodyPart.setFileName((String) fileEntry.getKey());
			multiPart.addBodyPart(fileBodyPart);
		}
	}
	
	private void addAttachments(Map<String, byte[]> attachmentFiles, Multipart multiPart) throws MessagingException{
		Iterator filesIter = attachmentFiles.entrySet().iterator();
		while (filesIter.hasNext()) {
			Entry fileEntry = (Entry) filesIter.next();
			MimeBodyPart fileBodyPart = new MimeBodyPart();
			fileBodyPart.setFileName((String) fileEntry.getKey());
			DataSource dataSource = new ByteArrayDataSource((byte[]) fileEntry.getValue(), new MimetypesFileTypeMap().getContentType(fileBodyPart.getFileName()));
			fileBodyPart.setDataHandler(new DataHandler(dataSource));
			multiPart.addBodyPart(fileBodyPart);
		}
	}
	
	/**
	 * Aggiunge destinatari alla mail.
	 * @param msg Il messaggio al quale aggiungere i destinatari.
	 * @param recType Il tipo di destinatario.
	 * @param recipients Gli indirizzi mail dei destinatari.
	 */
	/**
	 * Add recipient addresses to the e-mail.
	 * @param msg The mime message to which add the addresses.
	 * @param recType The specific recipient type to which add the addresses.
	 * @param recipients The recipient addresses.
	 */
	protected void addRecipients(MimeMessage msg, RecipientType recType, String[] recipients) {
		if (null != recipients) {
			try {
				Address[] addresses = new Address[recipients.length];
				for (int i=0; i<recipients.length; i++) {
					Address address = new InternetAddress(recipients[i]);
					addresses[i] = address;
				}
				msg.setRecipients(recType, addresses);
			} catch (MessagingException e) {
				throw new RuntimeException("Errore in aggiunta recipients", e);
			}
		}
	}
	
	/**
	 * Close the transport.
	 * @param transport The transport.
	 * @throws ApsSystemException In case of errors closing the transport.
	 */
	protected void closeTransport(Transport transport) throws ApsSystemException {
		if (transport != null) {
			try {
				transport.close();
			} catch (MessagingException e) {
				throw new ApsSystemException("Errore in chiusura connessione", e);
			}
		}
	}
	
	/**
	 * returns the mail service configuration.
	 * @return The mail service configuration.
	 */
	protected MailConfig getConfig() {
		return _config;
	}
	/**
	 * Set the mail service configuration.
	 * @param config The mail service configuration.
	 */
	protected void setConfig(MailConfig config) {
		this._config = config;
	}
	
	/**
	 * Returns the configuration manager.
	 * @return The Configuration manager.
	 */
	protected ConfigInterface getConfigManager() {
		return _configManager;
	}
	/**
	 * Set method for Spring bean injection.<br /> Set the Configuration manager.
	 * @param configManager The Configuration manager.
	 */
	public void setConfigManager(ConfigInterface configManager) {
		this._configManager = configManager;
	}
	
	private MailConfig _config;
	
	private ConfigInterface _configManager;
	
	/*
	 * Default Timeout in milliseconds
	 */
	public static final int DEFAULT_SMTP_TIMEOUT = 5000;
	
}