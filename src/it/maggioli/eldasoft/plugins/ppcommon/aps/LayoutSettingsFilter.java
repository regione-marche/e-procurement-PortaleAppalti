package it.maggioli.eldasoft.plugins.ppcommon.aps;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Filtro per la definizione dei cookies per modificare il layout
 * dell'applicativo
 *
 * @author Stefano.Sabbadin
 */
public class LayoutSettingsFilter implements Filter {

	private static final String REQUEST_PARAM_STAZIONE_APPALTANTE 	= "layout";
	private static final String COOKIE_ID_STAZIONE_APPALTANTE  		= "layoutStyle";	
	
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
					FilterChain chain) throws IOException, ServletException {

		HttpServletResponse response = (HttpServletResponse) resp;
		
		req.setCharacterEncoding("UTF-8");
		// String cookieValueFont = "normal";
		String fontValue = req.getParameter("font");
		if (fontValue != null) {
			// cookieValueFont = fontValue;
			Cookie c = new Cookie("font", fontValue);
			c.setMaxAge(365 * 24 * 60 * 60);
			((HttpServletResponse) resp).addCookie(c);
		}
		// String cookieValueSkin = "normal";
		String skinValue = req.getParameter("skin");
		if (skinValue != null) {
			// cookieValueFont = skinValue;
			Cookie c = new Cookie("skin", skinValue);
			c.setMaxAge(365 * 24 * 60 * 60);
			((HttpServletResponse) resp).addCookie(c);
		}
		
		// verifica se e' stato passato il parametro per la selezione del layout style...
		String layoutValue = req.getParameter("layout");
		if (layoutValue != null) {
			Cookie c = new Cookie("layout", layoutValue);
			c.setMaxAge(365 * 24 * 60 * 60);
			response.addCookie(c);
		}
		
		chain.doFilter(req, resp);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}
	
}
