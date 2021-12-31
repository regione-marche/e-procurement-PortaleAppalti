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
package com.agiletec.aps.system.services;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;

import com.agiletec.aps.system.services.notify.ApsEvent;
import com.agiletec.aps.system.services.notify.INotifyManager;

/**
 * E' alla base dell'implementazione dei Servizi.<br>
 * I servizi sono istanziati e inizializzati all'avvio del sistema o quando 
 * viene richiesto il ri-caricamento di tutto il sistema.
 * @author 
 */
public abstract class AbstractService 
		implements IManager, BeanNameAware, BeanFactoryAware {
	
	/**
	 * Metodo da richiamare nel caso i 
	 * debba fare un refresh del servizio.
	 * @throws Throwable In caso di errori in fase di reinizializzazione.
	 */
	public void refresh() throws Throwable {
		this.release();
		this.init();
	}
	
	/**
	 * Metodo da implementare nel caso si desideri 
	 * rilasciare campi del servizio prima del refresh.
	 * Di default non fa niente.
	 */
	protected void release() {}
	
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this._beanFactory = beanFactory;
	}
	
	/**
	 * Restituisce un servizio in base al nome.
	 * @param name Il nome del servizio da restituire.
	 * @return Il servizio richiesto.
	 * @deprecated It's better to avoid the use of this method: you should use the Spring Injection instead.
	 */
	protected IManager getService(String name) {
		return (IManager) this._beanFactory.getBean(name);
	}
	
	public void setBeanName(String name) {
		this._name = name;
	}
	
	/** 
	 * Restituisce il nome del servizio.
	 * @return Il nome del servizio.
	 */
	public String getName() {
		return _name;
	}
	
	protected INotifyManager getNotifyManager() {
		return _notifyManager;
	}
	public void setNotifyManager(INotifyManager notifyManager) {
		this._notifyManager = notifyManager;
	}
	
	/**
	 * Notifica un evento interno al servizio di notificazione.
	 * @param event L'evento da notificare.
	 */
	protected void notifyEvent(ApsEvent event) {
		this.getNotifyManager().publishEvent(event);
	}
	
	/**
	 * Aggiorna il servizio corrente in funzione dell'evento notificato.
	 * Il metodo è in uso ai servizi atti a ricevere la notificazione 
	 * di un'evento interno.
	 * @param event L'evento notificato.
	 */
	public void update(ApsEvent event) {
		event.notify(this);
	}
	
	private String _name;
	
	private BeanFactory _beanFactory;
	
	private INotifyManager _notifyManager;
	
}
