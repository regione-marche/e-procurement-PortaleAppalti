package it.maggioli.eldasoft.plugins.ppcommon.aps.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.I18nInterceptor;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import it.maggioli.eldasoft.plugins.ppcommon.aps.PortalCookie;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.dispatcher.StrutsRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Esteso l'interceptor standard di struts per aggiungere la gestione della lingua nel cookie come backup in caso di
 * cambio lingua. (Copiato ed incollato l'interceptor originale e modificato aggiungendo la gestione dei cookie)
 */
public class I18nPortalInterceptor extends I18nInterceptor {

    private static final int MAX_AGE 		= 365 * 24 * 60 * 60;

    private static final Logger log = LoggerFactory.getLogger(I18nInterceptor.class);

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        log.debug("intercept '{}/{}' { ", invocation.getProxy().getNamespace(), invocation.getProxy().getActionName());

        //get requested locale
        Object localeOnHeader = getLocaleHeader(invocation);

        //save it in session
        Map<String, Object> session = invocation.getInvocationContext().getSession();
        StrutsRequestWrapper request = InterceptorUtil.getRequestFromInterceptor(invocation);

        //Cookie della lingua
        Cookie langCookie = getLangCookie(request);

        if (session != null) {
            synchronized (session) {
                if (localeOnHeader != null) //Aggiornato la sessione ed il cookie
                    updateCurrentLang(invocation, localeOnHeader, session, request);

                //set locale for action
                Object locale = session.get(attributeName);
                if (locale == null && langCookie != null)
                    locale = usePreviousSessionLanguage(session, langCookie);
                if (locale instanceof Locale) {
                    log.debug("apply locale={}", locale);

                    saveLocale(invocation, (Locale)locale);
                }
            }
        }

        log.debug("before Locale={}", invocation.getStack().findValue("locale"));

        final String result = invocation.invoke();
        log.debug("after Locale={}", invocation.getStack().findValue("locale"));

        log.debug("intercept } ");

        return result;
    }

    /**
     * Aggiorna sessione e cookie con il valore della nuova lingua
     * @param invocation
     * @param localeOnHeader
     * @param session
     * @param request
     */
    private void updateCurrentLang(ActionInvocation invocation, Object localeOnHeader, Map<String, Object> session,
                           StrutsRequestWrapper request) {
        Locale locale = (localeOnHeader instanceof Locale)
                        ? (Locale) localeOnHeader
                        : LocalizedTextUtil.localeFromString(localeOnHeader.toString(), null);
        log.debug("store locale={}", locale);

        if (locale != null) {
            session.put(attributeName, locale); //Aggiorno la lingua attuale nella sessione
            ServletResponse response = InterceptorUtil.getResponseFromInterceptor(invocation);
            if (response != null)   //Aggiorno la lingua attuale nel cookie
                addLangCookie(request, (HttpServletResponse) response, locale);
        }
    }

    private Object getLocaleHeader(ActionInvocation invocation) {
        Object localeOnHeader = invocation.getInvocationContext()
                                    .getParameters()
                                .remove(parameterName);
        return localeOnHeader != null && localeOnHeader.getClass().isArray() && ((Object[]) localeOnHeader).length == 1
                ? ((Object[]) localeOnHeader)[0]
                : localeOnHeader;
    }

    private Locale usePreviousSessionLanguage(Map<String, Object> session, Cookie langCookie) {
        Locale previousLocal = LocalizedTextUtil.localeFromString(langCookie.getValue(), null);
        session.put(attributeName, previousLocal);
        return previousLocal;
    }

    /**
     * Ritorna il cookie contenente la lingua
     *
     * @param request
     * @return
     */
    private Cookie getLangCookie(HttpServletRequest request) {
        Cookie toReturn = null;

        if (request != null) {   //StrutsRequestWrapper o MultiParRequestWrapper
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                toReturn = Arrays.stream(cookies)
                        .filter(Objects::nonNull)
                        .filter(curr -> StringUtils.equals(curr.getName(), attributeName))
                    .findFirst()
                .orElse(null);
            }
        }

        return toReturn;
    }

    private void addLangCookie(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        Cookie cookie = new PortalCookie(attributeName, locale.toString(), request);
        cookie.setMaxAge(MAX_AGE);
        response.addCookie(cookie);
    }

}
