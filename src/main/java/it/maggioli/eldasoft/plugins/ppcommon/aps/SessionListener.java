package it.maggioli.eldasoft.plugins.ppcommon.aps;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * ... 
 */
public class SessionListener implements HttpSessionListener {
	
//	private static final Logger LOG = LoggerFactory.getLogger(SessionListener.class);
		
	@Override
	public void sessionCreated(HttpSessionEvent se) {
		BotFilter.doSessionCreated(se.getSession());
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		BotFilter.doSessionDestroyed(se.getSession()); 
	}
	
}
