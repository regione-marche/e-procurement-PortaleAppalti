/*
 * Created on 04/apr/19
 *
 * Copyright (c) Maggioli S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.maggioli.eldasoft.plugins.ppcommon.aps;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.baseconfig.ConfigInterface;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.AppParamManager;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Filtro HTTP che controlla l'hostname per evitare dns Internal service interaction
 *
 */
public class DNSRequestFilter implements Filter {

  private static final Logger log = LoggerFactory.getLogger(DNSRequestFilter.class);

  private static final Pattern extractUrl = Pattern.compile("http(?:s)?:\\/\\/([\\w\\.\\-\\_]+(?::\\d+)?)(?:/.+)?");
  private static final String DISABLE_FILTER_VALUE = "*";
  private static final String URL_SEPARATOR        = ",";
  private final List<Pattern> LOCAL_ADDRESSES_REGEX = new ArrayList<>(3);

  private ConfigInterface configInterface;
  private IAppParamManager paramManager;

  /**
   * Inizializzo bean (manager) e regex degli url locali
   *
   * @see Filter#init(FilterConfig)
   */
  public void init(FilterConfig arg0) throws ServletException {
    ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
    configInterface = (ConfigInterface) ctx.getBean("BaseConfigManager");
    paramManager = (IAppParamManager) ctx.getBean("AppParamManager");
    initLocalAddressPatterns();
  }

  /**
   * @see Filter#doFilter(ServletRequest,
   *      ServletResponse, FilterChain)
   */
  public void doFilter(ServletRequest req, ServletResponse resp,
      FilterChain chain) throws IOException, ServletException {
    log.debug("START - DNS filter");

    HttpServletRequest request = (HttpServletRequest) req;
    String host = request.getHeader("host");
    String hostsAllowed = (String) paramManager.getConfigurationValue(AppParamManager.HOSTS_ALLOWED);
    if (!StringUtils.equals(hostsAllowed, DISABLE_FILTER_VALUE)
          && !isHostAllowedByProperties(host, hostsAllowed)
          && !isLocalIP(host)
          && !StringUtils.equalsIgnoreCase(host, getApplicationBaseURLHost())) {
        log.error("Bloccata richiesta con header Host valorizzato con {}", host);
        ((HttpServletResponse) resp).sendError(HttpServletResponse.SC_FORBIDDEN);
        return;
    }
    chain.doFilter(req, resp);

    log.debug("END - DNS filter");
  }

  /**
   * @see Filter#destroy()
   */
  public void destroy() { }

  /**
   * Controlla se è stato utilizzato un indirizzo locale
   * @param host
   * @return
   */
  private boolean isLocalIP(String host) {
    return LOCAL_ADDRESSES_REGEX.stream().anyMatch(pattern -> pattern.matcher(host).matches());
  }

  /**
   * Controlla se l'host è nella white list (AppParamManager.HOSTS_ALLOWED)
   * @param hostHeader
   * @param hostAllowedProperty
   * @return
   */
  private boolean isHostAllowedByProperties(String hostHeader, String hostAllowedProperty) {
    return StringUtils.isNotEmpty(hostAllowedProperty)
            && Arrays.stream(hostAllowedProperty.split(URL_SEPARATOR))
                  .map(String::trim)
                .anyMatch(url -> StringUtils.equalsIgnoreCase(hostHeader, url));
  }

  /**
   * Ritorna l'application base url della webapp corrente
   * @return
   */
  private String getApplicationBaseURLHost() {
    String rawUrl = configInterface.getParam(SystemConstants.PAR_APPL_BASE_URL);
    Matcher matcher = extractUrl.matcher(rawUrl);

    return matcher.matches()
            ? matcher.group(1)
            : getHostByReplace(rawUrl);
  }

  /**
   * Calcola l'host dal'url completo
   * @param rawUrl
   * @return
   */
  private String getHostByReplace(String rawUrl) {
    String toReturn = rawUrl.replace("https://", "").replace("http://", "");
    int indexTo = toReturn.indexOf("/");
    return indexTo != -1
            ? toReturn.substring(0, indexTo)
            : toReturn;
  }

  /**
   * Pattern degli indirizzi locali
   */
  private void initLocalAddressPatterns() {
    LOCAL_ADDRESSES_REGEX.add(Pattern.compile("(?i)localhost:\\d+"));
    LOCAL_ADDRESSES_REGEX.add(Pattern.compile("127\\.0\\.0\\.1:\\d+")); //IPv4
    LOCAL_ADDRESSES_REGEX.add(Pattern.compile("\\[::1\\]:\\d+")); //IPv6
  }

}
