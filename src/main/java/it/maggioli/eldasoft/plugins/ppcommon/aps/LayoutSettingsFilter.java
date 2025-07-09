package it.maggioli.eldasoft.plugins.ppcommon.aps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Filtro per la definizione dei cookies per modificare il layout
 * dell'applicativo.
 *
 * Viene anche effettuata la validazione degli header gestiti. (font, skin e layout)
 *
 * @author Stefano.Sabbadin
 */
public class LayoutSettingsFilter implements Filter {

	private static final Logger log = LoggerFactory.getLogger(LayoutSettingsFilter.class);

	private static final String LAYOUT_PARAM_NAME = "layout";
	private static final String SKIN_PARAM_NAME = "skin";
	private static final String FONT_PARAM_NAME = "font";
	private static final List<String> FONT 	= Arrays.asList("normal", "big", "verybig");
	private static final List<String> SKIN 	= Arrays.asList("normal", "text", "highcontrast");
	private static final Pattern LAYOUT 	= Pattern.compile("(?i)[a-zAZ\\d-_]*");
	/**
	 * Età massima coockie in secondi
	 */
	private static final int MAX_AGE 		= 365 * 24 * 60 * 60;

	@Override
	public void init(FilterConfig arg0) throws ServletException { }


	@Override
	public void destroy() {	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
					FilterChain chain) throws IOException, ServletException {
		//Wrapper che esclude dall'header i parametri non validi
		RequestParamRemoverWrapper headerRemover = new RequestParamRemoverWrapper((HttpServletRequest) req);

		PortalHTTPResponseWrapper wrappedResponse = new PortalHTTPResponseWrapper((HttpServletResponse) resp);
		headerRemover.setCharacterEncoding("UTF-8");
		// verifica se e' stato passato il parametro per la selezione del font e se è valido lo inserisco in un coockie...
		addCookieIfValid(headerRemover, wrappedResponse, FONT_PARAM_NAME, FONT);
		// verifica se e' stato passato il parametro per la selezione delo skin e se è valido lo inserisco in un coockie...
		addCookieIfValid(headerRemover, wrappedResponse, SKIN_PARAM_NAME, SKIN);
		// verifica se e' stato passato il parametro per la selezione del layout style e se è valido lo inserisco in un coockie...
		addCookieIfValid(headerRemover, wrappedResponse, LAYOUT_PARAM_NAME, LAYOUT);

		chain.doFilter(headerRemover, wrappedResponse);
	}


	/**
	 * Aggiungo l'header ad un cookie se il valore dell'header è valido.
	 *
	 * @param req Wrapper della request che rimuove i parametri non validi
	 * @param resp
	 * @param paramName	nome dell'header
	 * @param isValidValue	Pattern per validare il valore dell'header
	 */
	private void addCookieIfValid(RequestParamRemoverWrapper req, ServletResponse resp, String paramName, Pattern isValidValue) {
		String value = req.getParameter(paramName);
		if (value != null)
			if (!isValidValue.matcher(value).matches())
				removeInvalidParam(req, paramName, value);
			else
				addCookie(paramName, value, ((HttpServletResponse) resp), req);
	}

	/**
	 * Aggiungo l'header ad un cookie se il valore dell'header è valido.
	 *
	 * @param req Wrapper della request che rimuove i parametri non validi
	 * @param resp
	 * @param paramName nome dell'header
	 * @param validValues Pattern per validare il valore dell'header
	 */
	private void addCookieIfValid(RequestParamRemoverWrapper req, ServletResponse resp, String paramName, List<String> validValues) {
		String value = req.getParameter(paramName);
		if (value != null)
			if (!validValues.contains(value.toLowerCase()))
				removeInvalidParam(req, paramName, value);
			else
				addCookie(paramName, value, ((HttpServletResponse) resp), req);
	}

	/**
	 * Aggiunge al wrapper il nome dell'header corrente, in modo che possa poi essere annullato.
	 *
	 * @param req Wrapper della request che rimuove i parametri non validi
	 * @param paramName nome dell'header
	 * @param value Valore dell'header
	 */
	private void removeInvalidParam(RequestParamRemoverWrapper req, String paramName, String value) {
		log.error("Il valore {} non è un {} consentito", value, paramName);
		req.addNotValidParam(paramName);
	}

	/**
	 * Aggiunge il cookie
	 *
	 * @param paramName nome dell'header
	 * @param value Valore dell'header
	 * @param resp Risposta
	 */
	private static void addCookie(String paramName, String value, HttpServletResponse resp, HttpServletRequest request) {
		Cookie c = new PortalCookie(paramName, value, request);
		c.setMaxAge(MAX_AGE);
		resp.addCookie(c);
	}



}
