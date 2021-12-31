package com.agiletec.apsadmin.system;

import it.maggioli.eldasoft.plugins.ppcommon.aps.XSSRequestWrapper;

import java.lang.reflect.Array;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
//import org.owasp.html.HtmlPolicyBuilder;
//import org.owasp.html.HtmlSanitizer;
//import org.owasp.html.PolicyFactory;
//import org.owasp.html.Sanitizers;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.ParametersInterceptor;
import com.opensymphony.xwork2.util.ValueStack;


public class XSSParameterInterceptor extends ParametersInterceptor {
	
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
	private static Pattern[] patterns = new Pattern[]{
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
	      Pattern.compile("on.*(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),

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
		
	@Override
	public String intercept(ActionInvocation invocation) throws Exception {		

//		System.out.println("Pre-Processing");
		String result = this.doIntercept(invocation);
		
//		System.out.println("Post-Processing");
		super.setAcceptParamNames("[a-zA-Z0-9\\.\\]\\[\\(\\)_'\\s]+");
		return result;
	}
	
	@Override
	protected void setParameters(Object action, ValueStack stack, final Map<String, Object> parameters) {
		
		// verifica i valori e bonifica eventuali tentativi di XSS... 
		for(Map.Entry<String, Object> param : parameters.entrySet()) {
			Object value = param.getValue();
			
			boolean skipParam = false;
			
			// VAPT: vulnerabilita' "Open redirect"
			if(XSSRequestWrapper.detectOpenRedirect(param.getKey(), value)) {
				// salta il parametro
				value = null;
			}  
			
			if(value != null) {
				if(value.getClass().isArray()) {
					for(int i = 0; i < Array.getLength(value); i++) {
						Array.set(value, i, filterXSS(param.getKey(), Array.get(value, i)));
				    }
				} else {
					value = filterXSS(param.getKey(), value);
				}
			}
			
			param.setValue(value);
		}
				
		// esegui il codice standard dopo la bonifica dei valori...
		super.setParameters(action, stack, parameters);
	}
	
	@Override
	protected boolean isAccepted(String paramName){
		return Pattern.compile("[a-zA-Z0-9\\.\\]\\[\\(\\)_'\\s]+").matcher(paramName).matches();
	}
	
	
	/**
	 * bonifica il testo sostituendo ai caratteri speciali l'equivalente 
	 * codice di escape 
	 */
    private Object filterXSS(String param, Object value) {
        if (value != null && value instanceof String) {
        	String newValue = (String)value;
        	try {
        		// NOTE: It's highly recommended to use the ESAPI library and uncomment the following line to
                // avoid encoded attacks.
                // value = ESAPI.encoder().canonicalize(value);
        		String v = newValue;
        		
                // Avoid null characters
            	newValue = newValue.replaceAll("\0", "");
            	
                // Remove all sections that match a pattern
                for (Pattern scriptPattern : patterns) {
                	newValue = scriptPattern.matcher(newValue).replaceAll("");
                }
                
                // OWASP HTML Sanitizer..
                // NB: attualmente viene commentato e reso dispobilile per 
                //     le versioni future (> 3.10.0)
                //     I jar necessari per utilizzare Owasp html sanitizer sono le seguenti
                //     - owasp-java-html-sanitizer-20200713.1.jar
                //     - guava-27.1-jre.jar
                //newValue = OWASP_SANITIZER.sanitize(newValue);
                
                boolean xssFound = (!v.equals(newValue));
                if(xssFound) {
                 	// log tentativo di attacco XSS !!!
                	newValue = "";
                }
            } catch (Exception e) {
				System.out.print(e.getMessage());
			}
            
            value = newValue;
        }
        return value;
    }
	
}
