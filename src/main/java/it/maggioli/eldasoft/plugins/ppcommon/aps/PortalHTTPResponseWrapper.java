package it.maggioli.eldasoft.plugins.ppcommon.aps;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Wrapper della response per gestire degli attributi non gestiti di default dai cookie standard.
 */
public class PortalHTTPResponseWrapper extends HttpServletResponseWrapper implements HttpServletResponse {
    /**
     * Constructs a response adaptor wrapping the given response.
     *
     * @param response
     * @throws IllegalArgumentException if the response is null
     */
    public PortalHTTPResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public void addCookie(Cookie cookie) {
        if (cookie instanceof PortalCookie)
            super.addHeader("Set-Cookie", ((PortalCookie) cookie).toHeader());
        else
            super.addCookie(cookie);
    }

}