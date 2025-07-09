package it.maggioli.eldasoft.plugins.ppcommon.aps.interceptor;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.user.DelegateUser;
import com.agiletec.aps.system.services.user.IUserManager;
import com.agiletec.aps.system.services.user.UserDetails;
import com.agiletec.apsadmin.system.BaseAction;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

import it.maggioli.eldasoft.plugins.ppcommon.aps.SpringAppContext;
import it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.sso.AccountSSO;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.CommonSystemConstants;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.TrackerSessioniUtenti;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.StrutsRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Interceptor che invalida la sessione dell'utente in caso sia stato fatto il login da un'altra sessione.
 *
 *
 * ATTENZIONE: In caso gli ip coincidano invalida anche la nuova sessione (può succedere solo nei test locali)
 */
public class UserClusterInterceptor extends AbstractInterceptor {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserClusterInterceptor.class);

	/**
	 * 1. {ActionName}
	 * 2. {ActionMethod}
	 * 3. {username}
	 * 4. {sessionId}
	 * 5. {Request parameters}
	 */
	private static final String LOG_MESSAGE = "START - {}.{} using user [ {}:{} ] and parameters: [ {} ]";

	private boolean forceUnlock;
	
	
    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        // Recupero la request dall'actioninvocation di struts
        StrutsRequestWrapper request = InterceptorUtil.getRequestFromInterceptor(invocation);
        
    	forceUnlock = false;
        
        // verifica la valida del login (cluster)
        validateCurrentLogin(invocation, request);

		logAction(invocation, request);
		
		if(forceUnlock) {
			BaseAction action = (BaseAction)invocation.getAction();
			action.addActionError(action.getText("Errors.accessiDistinti.forcedUnlock"));
			return CommonSystemConstants.PORTAL_ERROR;
		}
        
        return invocation.invoke();
    }

	private void logAction(ActionInvocation invocation, HttpServletRequest request) {
		// Aggiunto perchè se non siamo in debug non vale la pena fare i vari calcoli
		if (LOGGER.isDebugEnabled()) {
			Map<?, ?> parameters = getStrutsParameters(invocation);
			String parametersString = "";
			if (parameters != null)
				parametersString = getStrutsParameters(invocation).entrySet()
						.stream()
							.map(it -> String.format("%s=\"%s\"", it.getKey(), logParameterValue(it.getValue())))
					.collect(Collectors.joining(", "));

			HttpSession session = request.getSession();
			UserDetails currentUser = ((UserDetails) session.getAttribute(SystemConstants.SESSIONPARAM_CURRENT_USER));

			LOGGER.debug(
				LOG_MESSAGE
				, invocation.getProxy().getAction().getClass().getSimpleName()
				, invocation.getProxy().getMethod()
				, currentUser != null ? currentUser.getUsername() : "guest"
				, session.getId()
				, parametersString
			);
		}
	}

	private String logParameterValue(Object value) {
		return value != null
				? value.getClass().isArray()
					? StringUtils.join((Object[]) value)
					: value.toString()
				: "";
	}

	private Map<?, ?> getStrutsParameters(ActionInvocation invocation) {
		Object parameters = invocation.getStack().getContext().get(ActionContext.PARAMETERS);
		if (parameters == null)
			parameters = invocation.getStack().getContext().get("parameters");

		return parameters != null ? (Map) parameters : null;
	}

	/**
     * verifica se il login corrente e' ancora valido oppure e' stato invalidato
     */
    private void validateCurrentLogin(ActionInvocation invocation, HttpServletRequest request) {
        if (request == null)
            return;
        
        HttpSession session = request.getSession();
        if (session == null)
            return;

        UserDetails currentUser = ((UserDetails) session.getAttribute(SystemConstants.SESSIONPARAM_CURRENT_USER));

        // Controlla se in sessione e' memorizzato un utente (quindi se e' stato fatto il login)
        if (currentUser != null) {
        	boolean isAdmin = (currentUser.getUsername().equals(SystemConstants.ADMIN_USER_NAME) ||
         		   			   currentUser.getUsername().equals(SystemConstants.SERVICE_USER_NAME));
        	boolean isGuest = (currentUser.getUsername().equals(SystemConstants.GUEST_USER_NAME));
        	
            if (!isAdmin && !isGuest) {
            	// aggiorna le info del tracker delle sessioni
                updateTrackerSessioniUtenti(request);
                
               	// se necessario rimuovi il lock dell'ultimo flusso nel quale e' entrato il soggetto impresa
                validateAccessiDistintiLock(invocation, request);
                
                // Se nel registro dei login, l'utente ha il logout valorizzato per la sessione corrente, invalida la sessione
                String log = "XSSRequestFilter.validateCurrentLogin -> VERIFICA STATO " + currentUser.getUsername();
                if (!currentUser.isLoginAlive()) {
                    log = log + " ==> INVALIDATO (sessionId=" + session.getId() + ")";
//                    session.removeAttribute(CommonSystemConstants.SESSION_OBJECT_ACCOUNT_SSO); 	// serve???
                    session.invalidate();
                }
                
                ApsSystemUtils.getLogger().debug(log);
            }
        }
    }

    /**
     * aggiorna i dati di sessione dell'utente nel tracker delle sessioni attive
     */    
    private void updateTrackerSessioniUtenti(HttpServletRequest request) {
    	HttpSession session = request.getSession();
    	if(session != null) {
    		UserDetails currentUser = ((UserDetails) session.getAttribute(SystemConstants.SESSIONPARAM_CURRENT_USER));
    		if(currentUser != null) {
    			TrackerSessioniUtenti.updateSessioneUtente(session, currentUser);
    		}
    	}
    }
    
    /**
     * in caso di accesso con soggetto impresa autorizzato (profilo SSO)
     * la verifica del logoin multiplo viene spostata all'apertura di invio dei
     * singoli flussi, quindi e' necessario verificare se un soggetto impresa
     * ha abbandonato un flusso e rimuore il lock della funzione per permettere
     * ad altri utenti di poter accedere al flusso
     */
    private void validateAccessiDistintiLock(ActionInvocation invocation, HttpServletRequest request) {
    	// NB: continua solo se c'e' la gestione di accessi distinti per l'OE
    	if( !AccountSSO.isAccessiDistinti() ) {
    		return;
    	}
    	
    	try {
    		boolean unlock = false;
    		
    		//HttpSession session = request.getSession();
    		//BaseAction action = (BaseAction)ActionContext.getContext().getActionInvocation().getAction();
    		BaseAction action = (BaseAction)invocation.getAction();
	    	AccountSSO accessoSSO = AccountSSO.getFromSession();
	    	DelegateUser soggettoImpresa = accessoSSO.getProfilo();

	    	// solo per le azioni relative agli utenti 
	    	// verifica se il lock corrente e' ancora valido o se e' stato annullato dall'OWNER
	    	if(invocation.getAction() instanceof BaseAction) {
		    	if( action.isLockRevoked(accessoSSO) ) {
		    		// l'OWNER ha annullato il lock del delegato 
	    			unlock = true;
	    			forceUnlock = true;
		    	}
	    	}
	    	
	    	// verifica se c'e' un flusso ancora aperto 
	    	// se il flusso sta per cambiare, chiudi il flusso precedentemente aperto 
	    	// che probabilmente non e' stato chiuso correttamente
	    	if( !action.isFlussoValid(accessoSSO) ) {
    			// e' cambiato il contesto... 
				// probabilmente il soggetto impresa e' uscito dal wizard in cui era entrato
				// esegui l'unlock del flusso in corso, quindi esegui un unlock dei flussi
    			unlock = true;
    		}
	    	
	    	if( !unlock ) {
		    	// verifica se la sessione di lock del soggetto impresa e' stata terminata dall'Owner
		    	// (solo se ho un LOCK attivo leggo la tabella dei lock per verificare se il mio lock e' ancora attivo
		    	//  altrimenti invalido la mia sessione)
	    		boolean locked = StringUtils.isNotEmpty(soggettoImpresa.getFlusso()) && soggettoImpresa.getLoginTime() != null;
	    		if(locked) {
		    		ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
					IUserManager userManager = (IUserManager) ctx.getBean(SystemConstants.USER_MANAGER);
	
					// verifica se il soggetto impresa ha ancora un lock di funzione aperto (ppcommon_delegate_accesses.logouttime = null)
					// NB: attuamente si controlla ad ogni action se il utente/soggetto impresa e' ancora attivo o meno
					//     se l'OWNER chiude una sessione di un soggetto delegato la sessione deve essere invalidata
					//     ma in contesto di cluster un soggetto potrebbe essere su un altro nodo, 
					//     quindi e' necessario rileggere i dati da DB per conoscere lo stato dell'utente/soggetto
					DelegateUser lockAttivo = userManager.loadProfiloSSOAccess(soggettoImpresa.getUsername(), soggettoImpresa.getDelegate());
					if(lockAttivo == null) {
						// NB: il messaggio di notifica non compare a video!!!
						forceUnlock = true;
						unlock = true;
					}
	    		}
    		}
	    
	    	// se necessario esegui l'unlock del soggetto impresa...
	    	if(unlock) {
	    		action.unlockAccessoFunzione();
	    	}
    	} catch (Throwable t) {
    		ApsSystemUtils.logThrowable(t, this, "validateAccessiDistintiLock");
		}
    }

}
