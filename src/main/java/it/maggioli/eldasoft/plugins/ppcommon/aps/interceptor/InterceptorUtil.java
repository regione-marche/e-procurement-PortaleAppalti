package it.maggioli.eldasoft.plugins.ppcommon.aps.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.dispatcher.StrutsRequestWrapper;

import javax.servlet.ServletResponse;

/**
 * Utils per l'interceptor
 */
public class InterceptorUtil {

    /**
     * Ritorna la richiesta a partire dall'ActionInvocation
     * @param invocation
     * @return
     */
    public static StrutsRequestWrapper getRequestFromInterceptor(ActionInvocation invocation) {
        //StrutsRequestWrapper o MultiParRequestWrapper
        Object requestWrapper = invocation.getInvocationContext().get(StrutsStatics.HTTP_REQUEST);
        return requestWrapper instanceof StrutsRequestWrapper
                ? (StrutsRequestWrapper) requestWrapper
                : null;
    }
    /**
     * Ritorna la risposta a partire dall'ActionInvocation
     * @param invocation
     * @return
     */
    public static ServletResponse getResponseFromInterceptor(ActionInvocation invocation) {
        Object response = invocation.getInvocationContext().get(StrutsStatics.HTTP_RESPONSE);
        return response != null ? (ServletResponse) response : null;
    }

    public static boolean isInterceptorPresent(ActionInvocation invocation, String name) {
        return invocation.getProxy()
                .getConfig()
                .getInterceptors()
                .stream()
            .anyMatch(interceptor -> StringUtils.equals(name, interceptor.getName()));
    }

}
