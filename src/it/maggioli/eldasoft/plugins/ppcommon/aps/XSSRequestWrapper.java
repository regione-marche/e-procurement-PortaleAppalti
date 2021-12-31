/*
 * Created on 04/apr/2019
 *
 * Copyright (c) Maggioli S.p.A. - Divisione Informatica
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.maggioli.eldasoft.plugins.ppcommon.aps;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

//import org.owasp.html.PolicyFactory;
//import org.owasp.html.Sanitizers;

import com.agiletec.aps.system.ApsSystemUtils;
import com.opensymphony.xwork2.ActionContext;

public class XSSRequestWrapper extends HttpServletRequestWrapper {

	/**
	 * Reflected CrossSite Scripting filter 
	 */
//	private static final PolicyFactory OWASP_SANITIZER = 
//		Sanitizers.FORMATTING 
//		.and(Sanitizers.BLOCKS)
//		.and(Sanitizers.STYLES)
//		.and(Sanitizers.LINKS)
//		.and(Sanitizers.TABLES)
//		.and(Sanitizers.IMAGES);

	/**
	 * elenco di valori html che possono generara attacchi Cross Site Scripting (XSS)
	 */
	private static Pattern[] patterns = new Pattern[] {
		// Script fragments
	    Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
	    // src='...'
	    Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // lonely script tags
	    Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
	    Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // eval(...)
	    Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // expression(...)
	    Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // javascript:...
	    Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
	    // vbscript:...
	    Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
	    // unexpected end of tag ">...
	    Pattern.compile("\">.*", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // on...(...)=...
	    Pattern.compile("on.*?(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),

// NB: servono ancora gli eventi specifici ???
	    // onload(...)=...
	    Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // onerror(...)=...
	    Pattern.compile("onerror(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // onunload(...)=...
	    Pattern.compile("onunload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // onchange(...)=...
	    Pattern.compile("onchange(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // onsubmit(...)=...
	    Pattern.compile("onsubmit(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // onreset(...)=...
	    Pattern.compile("onreset(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // onselect(...)=...
	    Pattern.compile("onselect(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // onblur(...)=...
	    Pattern.compile("onblur(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // onfocus(...)=...
	    Pattern.compile("onfocus(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // onkeydown(...)=...
	    Pattern.compile("onkeydown(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // onkeypress(...)=...
	    Pattern.compile("onkeypress(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // onkeyup(...)=...
	    Pattern.compile("onkeyup(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // onclick(...)=...
	    Pattern.compile("onclick(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // ondblclick(...)=...
	    Pattern.compile("ondblclick(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // onmousedown(...)=...
	    Pattern.compile("onmousedown(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // onmousemove(...)=...
	    Pattern.compile("onmousemove(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // onmouseout(...)=...
	    Pattern.compile("onmouseout(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // onmouseover(...)=...
	    Pattern.compile("onmouseover(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // onmouseup(...)=...
	    Pattern.compile("onmouseup(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
	};
	

  public XSSRequestWrapper(HttpServletRequest servletRequest) {
      super(servletRequest);
  }

  @Override
  public String getHeader(String name) {
      String value = filterXSS(name, super.getHeader(name));
      return value;
  }
  
  @Override
  public Enumeration getHeaders(String name) {
      List<String> newVals = new ArrayList<String>(); 
	  Enumeration values = super.getHeaders(name);	  
	  while (values.hasMoreElements()) {
	  	  String v = (String) values.nextElement();
	  	  newVals.add( filterXSS(name, v) );
//	  	  if(v.contains("alert(")) {
//	  		  System.out.println("XSSRequestWrapper headers=" + name + " value=" + v);
//	  	  }
	  }
	  
	  return Collections.enumeration(newVals);
  }
  
//  @Override
//  public int getIntHeader(String name) {
//	  ...
//  }
  
  @Override
  public String[] getParameterValues(String parameter) {
      String[] values = super.getParameterValues(parameter);

      if (values == null) {
          return null;
      }
      
      if(detectOpenRedirect(parameter, values)) {
    	  return null;
      }

      int count = values.length;
      String[] encodedValues = new String[count];
      for (int i = 0; i < count; i++) {
          encodedValues[i] = filterXSS(parameter, values[i]);
      }

      return encodedValues;
  }

  @Override
  public String getParameter(String parameter) {
      String value = super.getParameter(parameter);
      if(detectOpenRedirect(parameter, value)) {
    	  return null;
      }
      return filterXSS(parameter, value);
  }

  /**
   * ... 
   */
  private String filterXSS(String param, String value) {
      if (value != null) {
          // NOTE: It's highly recommended to use the ESAPI library and uncomment the following line to
          // avoid encoded attacks.
          // value = ESAPI.encoder().canonicalize(value);
    	  String v = value;
    	  
          // Avoid null characters
          value = value.replaceAll("\0", "");

          // Remove all sections that match a pattern
          for (Pattern scriptPattern : patterns){
              value = scriptPattern.matcher(value).replaceAll("");
          }
          
          boolean xssFound = (!v.equals(value));
          if(xssFound) {
        	  // log tentativo di attacco XSS !!!
        	  value = "";
          }
          
          // OWASP HTML Sanitizer..
          // NB: attualmente viene commentato e reso dispobilile per 
          //     le versioni future (> 3.10.0)
          //     I jar necessari per utilizzare Owasp html sanitizer sono le seguenti
          //     - owasp-java-html-sanitizer-20200713.1.jar
          //     - guava-27.1-jre.jar
          //value = OWASP_SANITIZER.sanitize(value);

      }
      return value;
  }
  
  /**
   * VAPT: vulnerabilita' "Open redirect"
   */
  public static boolean detectOpenRedirect(String param, Object value) {
	  String invalidUrl = null;
	  if("urlPage".equalsIgnoreCase(param)) {
		  
		  // recupera base url...
//		  HttpServletRequest request = ServletActionContext.getRequest();
//		  String baseUrl = request.getRequestURL().toString();
//		  baseUrl = baseUrl.substring(0, baseUrl.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
		  
		  // verifica "Open redirect"
		  if(value != null) {
			  if(value.getClass().isArray()) {
				  for(int i = 0; i < Array.getLength(value); i++) {
					  Object v = Array.get(value, i);
					  if( v != null && !isValidUrl(v.toString()) ) {
						  invalidUrl = v.toString();
						  break;
					  }  
				  }
			  } else {
				  if( value != null && !isValidUrl(value.toString()) ) {
					  invalidUrl = value.toString();
				  }
			  }
		  }
	  }
	  if(invalidUrl != null) {
		  ApsSystemUtils.getLogger().warn("Ricevuta url non ammessa: " + invalidUrl);
	  }
	  return (invalidUrl != null);
  }

  private static boolean isValidUrl(String url) {
	  // valid urlPage "/[YY]/[XXXXXXXXXX].wp"
	  boolean valid = false;
	  String lang = "";
	  ActionContext ctx = ActionContext.getContext();
	  if (ctx != null) {
		  lang = ctx.getLocale().getLanguage();
		  valid = url.startsWith("/" + lang + "/") && url.endsWith(".wp");
  	  }
	  return valid;
  }
    
}