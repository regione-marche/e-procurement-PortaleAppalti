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
package com.agiletec.aps.system.services.notify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;

import com.agiletec.aps.system.ApsSystemUtils;

/**
 * Servizio notificatore eventi.
 * @author M.Diana - E.Santoboni
 */
public class NotifyManager implements INotifyManager, ApplicationListener, 
		BeanFactoryAware, ApplicationEventPublisherAware {
	
	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ApsEvent) {
			NotifyingEventThread thread = new NotifyingEventThread(this, (ApsEvent) event);
			thread.setName(NOTIFYING_THREAD_NAME);
			thread.start();
			return;
		}
		ApsSystemUtils.getLogger().info("Unhandled generic event detected: "+ event.getClass().getName());
	}
	
	/**
	 * Notifica un evento ai corrispondenti servizi osservatori.
	 * @param event L'evento da notificare.
	 */
	protected void notify(ApsEvent event) {
		Logger log = ApsSystemUtils.getLogger();
		ListableBeanFactory factory = (ListableBeanFactory) this._beanFactory;
		String[] defNames = factory.getBeanNamesForType(event.getObserverInterface());
		for (int i=0; i<defNames.length; i++) {
			Object observer = null;
			try {
				observer = this._beanFactory.getBean(defNames[i]);
			} catch (Throwable t) {
				observer = null;
			}
			if (observer != null) {
				((ObserverService) observer).update(event);
				if (log.isInfoEnabled()) {
					log.info("The event " + event.getClass().getName() + " was notified to the " + observer.getClass().getName()+" service");
				}
			}
		}
		if (log.isInfoEnabled()) {
			log.info("The " + event.getClass().getName()+" has been notified");
		}
	}
	
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this._beanFactory = beanFactory;
	}
	
	/**
	 * Notifica un'evento a tutti i listener definiti nel sistema.
	 * @param event L'evento da notificare.
	 */
	public void publishEvent(ApplicationEvent event) {
		this._eventPublisher.publishEvent(event);
	}
	
	public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
		this._eventPublisher = eventPublisher;
	}
	
	private ApplicationEventPublisher _eventPublisher;
	
	private BeanFactory _beanFactory;
	
	public static final String NOTIFYING_THREAD_NAME = "NotifyingThreadName";
	
}
