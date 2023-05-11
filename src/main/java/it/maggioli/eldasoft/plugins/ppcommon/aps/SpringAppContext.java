/*
 * Created on 13/ott/2010
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.maggioli.eldasoft.plugins.ppcommon.aps;

import javax.servlet.ServletContext;

import org.springframework.web.context.ServletContextAware;

/**
 * Classe definita per ottenere in una web application che si basa su Web
 * Services, i riferimenti ai bean di Spring senza essere in possesso di un
 * riferimento al servlet context.
 *
 * @author Stefano.Sabbadin
 */
public class SpringAppContext implements ServletContextAware {

	private static ServletContext servletContext;

	@Override
	public void setServletContext(ServletContext arg0) {
		servletContext = arg0;
	}

	/**
	 * Permette di ottenere un reference al servlet context in assenza di oggetti
	 * che permettano di raggiungerlo
	 *
	 * @return servlet context
	 */
	public static ServletContext getServletContext() {
		return servletContext;
	}
}
