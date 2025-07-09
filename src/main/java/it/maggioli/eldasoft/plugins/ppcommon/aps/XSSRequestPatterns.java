package it.maggioli.eldasoft.plugins.ppcommon.aps;

import com.agiletec.aps.system.ApsSystemUtils;
import com.opensymphony.xwork2.ActionContext;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.util.regex.Pattern;

/**
 * ... 
 */
public class XSSRequestPatterns {

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
	public static final Pattern[] XXS_PATTERNS = new Pattern[] {
		// Script fragments
	    Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
		Pattern.compile("(?i)(?:\"|')\\s*[^\"']*\\s*=\\s*(?:\"|')\\s*[^\"']+(?:\"|')"),	//Previene l'aggiunta di attributi EX: hxggp " a = " b " fh6vf
		Pattern.compile("(?i)<\\w+\\s+(\\w+\\s*=\\s*\"?.+\"?\\s*)+>"),	//Previene la creazione di tag EX: ph10k'><a b = c d = "f">g50lw ripulisce: <a b = c d = "f">
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
	    
//	    // on...=...
//	    Pattern.compile("on[a-zA-Z]+=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    	
	    // NB: servono ancora gli eventi specifici ???
	    // onload...=...
	    Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // onerror...=...
	    Pattern.compile("onerror(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // onunload...=...
	    Pattern.compile("onunload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // onchange...=...
	    Pattern.compile("onchange(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // onsubmit...=...
	    Pattern.compile("onsubmit(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // onreset...=...
	    Pattern.compile("onreset(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // onselect...=...
	    Pattern.compile("onselect(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // onblur...=...
	    Pattern.compile("onblur(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // onfocus...=...
	    Pattern.compile("onfocus(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // onkeydown...=...
	    Pattern.compile("onkeydown(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // onkeypress...=...
	    Pattern.compile("onkeypress(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // onkeyup...=...
	    Pattern.compile("onkeyup(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // onclick...=...
	    Pattern.compile("onclick(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // ondblclick...=...
	    Pattern.compile("ondblclick(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // onmousedown...=...
	    Pattern.compile("onmousedown(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // onmouseenter...=...
	    Pattern.compile("onmouseenter(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // onmouseleave...=...
	    Pattern.compile("onmouseleave(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // onmousemove...=...
	    Pattern.compile("onmousemove(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // onmouseout...=...
	    Pattern.compile("onmouseout(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // onmouseover...=...
	    Pattern.compile("onmouseover(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    // onmouseup...=...
	    Pattern.compile("onmouseup(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    
	    Pattern.compile("onpointercancel(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    Pattern.compile("onpointerdown(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    Pattern.compile("onpointerenter(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    Pattern.compile("onpointerleave(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    Pattern.compile("onpointermove(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    Pattern.compile("onpointerout(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    Pattern.compile("onpointerover(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    Pattern.compile("onpointerup(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    
	    Pattern.compile("onloadeddata(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    Pattern.compile("onloadedmetadata(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    Pattern.compile("onloadend(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    Pattern.compile("onloadstart(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    Pattern.compile("onlostpointercapture(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    Pattern.compile("onpause(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    Pattern.compile("onplay(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    Pattern.compile("onplaying(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    Pattern.compile("onreset(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    Pattern.compile("onresize(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    Pattern.compile("onscroll(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    Pattern.compile("onsecuritypolicyviolation(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    Pattern.compile("onselect(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    Pattern.compile("onselectionchange(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    Pattern.compile("onselectstart(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    Pattern.compile("onslotchange(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    Pattern.compile("onsubmit(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    Pattern.compile("ontouchcancel(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    Pattern.compile("ontouchstart(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    Pattern.compile("ontransitioncancel(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    Pattern.compile("ontransitionend(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
	    Pattern.compile("onwheel(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
	};
	
	
	/**
	 * filtra il valore di un parametro in base ai filtri XSS    
	 */
	public static String filterXSS(String param, String value) {
		if (value == null) {
			return value;
		}

		// NOTE: It's highly recommended to use the ESAPI library and uncomment the following line to
		// avoid encoded attacks.
		// value = ESAPI.encoder().canonicalize(value);
		//
		// OWASP HTML Sanitizer..
		// NB: attualmente viene commentato e reso dispobilile per 
		//     le versioni future (> 3.10.0)
		//     I jar necessari per utilizzare Owasp html sanitizer sono le seguenti
		//     - owasp-java-html-sanitizer-20200713.1.jar
		//     - guava-27.1-jre.jar
		//value = OWASP_SANITIZER.sanitize(value);

		//Se > 2000, presumibilmente  la stream di un file, questo controllo sar rafforzato non appena verranno fatti
		//dei controlli specifici su ogni action
		//nota  la text area delle note nello step categorie
		//testo  la textarea del testo nell'invio della comunicazione
		//descrizioneAggiuntiva  la textarea descrizione aggiuntiva sullo step prodotti
		if (value.length() <= 2000 || (StringUtils.isNotEmpty(param) && ("testo".equalsIgnoreCase(param) || "descrizioneAggiuntiva".equalsIgnoreCase(param) || "nota".equalsIgnoreCase(param)))) {
			// unescape all chars to get an explicit value...
			value = XSSValidation.unescapeHtml(value);

			// remove all sections that match a pattern...
			for (Pattern scriptPattern : XSSRequestPatterns.XXS_PATTERNS) {
				if (scriptPattern.matcher(value).find()) {
					value = "";    //XSS Found
					break;
				}
			}
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
			//		HttpServletRequest request = ServletActionContext.getRequest();
			//		String baseUrl = request.getRequestURL().toString();
			//		baseUrl = baseUrl.substring(0, baseUrl.length() - request.getRequestURI().length()) + request.getContextPath() + "/";

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
