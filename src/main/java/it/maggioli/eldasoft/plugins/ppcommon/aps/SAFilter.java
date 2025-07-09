package it.maggioli.eldasoft.plugins.ppcommon.aps;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import it.eldasoft.www.sil.WSGareAppalto.DettaglioStazioneAppaltanteType;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.PortGareSystemConstants;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.IBandiManager;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class SAFilter implements Filter {
	
	private static final String REQUEST_PARAM_STAZIONE_APPALTANTE 	= "sa";
	private static final String COOKIE_ID_STAZIONE_APPALTANTE  		= "stazioneAppaltante";	
	
	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}
	
	@Override
	public void destroy() {
	}

	/**
	 * Gestione dei filtri della web application
	 * La web application ha gestisce alcuni filtri che ne partizionano i 
	 * contenuti:
	 *   - stazione appaltante 
	 *   		se presente filtra i dati dell'applicazione per stazione appaltante
	 *            
	 * La politica di gestione dei filtri e' la seguente:
	 * L'applicazione verifica se e' stata eseguita con un parametro 
	 * presente nell'url di esecuzione e ne memorizza il valore in un cookie e 
	 * successivamente nella sessione; in tal modo anche alla scadenza della 
	 * sessione e' possibile recuperare i filtri dell'ultima navigazione.
	 * Se non sono presenti parametri nell'url, l'applicazione visualizza
	 * una pagina nella quale viene richiesto di scegliere quale stazione appaltante 
	 * utilizzare e ne imposta il valore in sessione; se scade la sessione viene 
	 * riproposta la pagina di selezione della stazione appaltante.       
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) 
		throws IOException, ServletException {

		req.setCharacterEncoding("UTF-8");
		
		HttpServletRequest request = (HttpServletRequest) req;
		boolean updateCookie = false;
		String stazioneAppaltanteValue = null;

		// verifica se esiste un cookie per la stazione appaltante...
		// ed estrai i valori dal cookie...
		Cookie cookie = null;
		if(request.getCookies() != null) {
			for(Cookie item : request.getCookies()) {
				if(COOKIE_ID_STAZIONE_APPALTANTE.equals(item.getName())) {
					cookie = item;
					stazioneAppaltanteValue = cookie.getValue();
				}
			}
		}

		// verifica se e' stato passato il parametro della stazione appaltante...
		String sa = req.getParameter(REQUEST_PARAM_STAZIONE_APPALTANTE);
		if (sa != null) {
			stazioneAppaltanteValue = sa;
			updateCookie = true;
		}
		
		// aggiorna i valori dei filtri in sessione... 
		setStazioneAppaltante(request, stazioneAppaltanteValue);

		// aggiorna/crea il cookie...
		if(updateCookie) {
			if(cookie == null)
				cookie = new PortalCookie(COOKIE_ID_STAZIONE_APPALTANTE, stazioneAppaltanteValue, req);
			cookie.setValue(stazioneAppaltanteValue);
			cookie.setMaxAge(365 * 24 * 60 * 60);
			((HttpServletResponse) resp).addCookie(cookie);
		} 
		
		// ...restituisci la gestione all'applicazione...
		chain.doFilter(req, resp);
	} 

	/**
	 * aggiorna il valore del filtro "stazioneAppaltante" della web application
	 */
	public static void setStazioneAppaltante(HttpServletRequest request, String value) {
		// memorizza il filtro in sessione...
		try {
			HttpSession session = request.getSession();

			String stazioneAppaltante = (String)session
				.getAttribute(PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE);
			String stazioneAppaltanteCodiceFiscale = (String)session
				.getAttribute(PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE_CF);
			String stazioneAppaltanteDesc = (String)session
				.getAttribute(PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE_DESC);
			
			if(value != null) {
				if( !value.equals(stazioneAppaltante) ) {
					stazioneAppaltante = value;
					stazioneAppaltanteDesc = null;
					stazioneAppaltanteCodiceFiscale = null;
					
					// recupera le info aggiuntive della stazione appaltante...
					IBandiManager bandiManager = (IBandiManager) ApsWebApplicationUtils
						.getBean(PortGareSystemConstants.BANDI_MANAGER, request);
					
					DettaglioStazioneAppaltanteType sa = bandiManager
						.getDettaglioStazioneAppaltante(stazioneAppaltante);
					if(sa != null) {
						stazioneAppaltanteDesc = sa.getDenominazione();
						stazioneAppaltanteCodiceFiscale = sa.getCodiceFiscale();
					} 
					
					session.setAttribute(
							PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE,
							stazioneAppaltante);
					session.setAttribute(
							PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE_DESC,
							stazioneAppaltanteDesc);
					session.setAttribute(
							PortGareSystemConstants.SESSION_ID_STAZIONE_APPALTANTE_CF,
							stazioneAppaltanteCodiceFiscale);
				}
			}
		} catch (ApsException e) {
			ApsSystemUtils.getLogger().error("Filtro su stazione appaltante con codice " + value + " non trovata");
		}
	}

}
