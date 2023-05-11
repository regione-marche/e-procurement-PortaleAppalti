package it.maggioli.eldasoft.plugins.ppcommon.aps.interceptor;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.user.UserDetails;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.struts2.dispatcher.StrutsRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Interceptor che invalida la sessione dell'utente in caso sia stato fatto il login da un'altra sessione.
 *
 *
 * ATTENZIONE: In caso gli ip coincidano invalida anche la nuova sessione (può succedere solo nei test locali)
 */
public class UserClusterInterceptor extends AbstractInterceptor {

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        //Recupero la request dall'actioninvocation di struts
        StrutsRequestWrapper request = InterceptorUtil.getRequestFromInterceptor(invocation);
        // verifica la valida del login (cluster)
        validateCurrentLogin(request);
        return invocation.invoke();
    }

    /**
     * verifica se il login corrente e' ancora valido oppure e' stato invalidato
     */
    private void validateCurrentLogin(HttpServletRequest request) {
        if (request == null)
            return;
        HttpSession session = request.getSession();
        if (session == null)
            return;

        UserDetails currentUser = ((UserDetails) session.getAttribute(SystemConstants.SESSIONPARAM_CURRENT_USER));
        //Controlla se in sessione è memorizzato un utente (quindi se è stato fatto il login)
        if (currentUser != null) {
            boolean isAdmin = (currentUser.getUsername().equals(SystemConstants.ADMIN_USER_NAME) ||
                    currentUser.getUsername().equals(SystemConstants.SERVICE_USER_NAME));
            boolean isGuest = (currentUser.getUsername().equals(SystemConstants.GUEST_USER_NAME));
            if (!isAdmin && !isGuest) {
                String log = "XSSRequestFilter.validateCurrentLogin -> VERIFICA STATO " + currentUser.getUsername();
                //Se nel registro dei login, l'utente ha il logout valorizzato per la sessione corrente, invalida la sessione.
                if (!currentUser.isLoginAlive()) {
                    log = log + " ==> INVALIDATO (sessionId=" + session.getId() + ")";
                    session.invalidate();
                }
                ApsSystemUtils.getLogger().debug(log);
            }
        }
    }

}
