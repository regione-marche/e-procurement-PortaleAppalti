package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.botfilter;

import com.agiletec.aps.system.ApsSystemUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestione captcha 
 * 
 * @author ...
 */
public class CaptchaUtils {
		  
	private static final Logger LOG = ApsSystemUtils.getLogger();
	
	private static final String FRIENDLYCAPTCHA_URL_SITEVERIFY 	= "https://apis.maggioli.cloud/rest/captcha/v2/siteverify";
	//private static final String FRIENDLYCAPTCHA_URL_PUZZLE 	= "https://apis.maggioli.cloud/rest/captcha/v2/puzzle";
	//private static final String FRIENDLYCAPTCHA_URL_LIBRARY 	= "https://apis.maggioli.cloud/rest/captcha/v2/widget.module.min.js" 

	// crea l'associazione lista/ricerca con il proprio session id...
	private static final Map<String, String> FRIENDLYCAPTCHA_ERRORS = new HashMap<String, String>() {{
	    put("secret_missing", "400, You forgot to add the secret (=API key) parameter");
	    put("secret_invalid", "401, The API key you provided was invalid");
	    put("solution_missing", "400, You forgot to add the solution parameter");
	    put("bad_request", "400, Something else is wrong with your request, e.g. your request body is empty");
	    put("solution_invalid", "200, The solution you provided was invalid (perhaps the user tried to tamper with the puzzle)");
	    put("solution_timeout_or_duplicate", "200, The puzzle that the solution was for has expired or has already been used");
	}};
	
	private static final String FRIENDLYCAPTCHA_UNAVAILABLE = "404, unavailable";


	/**
	 * valida una request con Captcha
	 */
	public static boolean validate(String solution) {
		boolean success = false; 
		String error = null;
		try {
			String secret = "secret";
			String sitekey = null;
			Map<String, String> recaptcha = friendlyCaptchaSiteVerify(solution, secret, sitekey);
			
	        // {
 			//    "success": true|false,
 			//    "errors": [secret_missing, secret_invalid, ...]
 			// }
			success = (recaptcha != null ? "true".equals(recaptcha.get("success")) : false); 
			error = (recaptcha != null ? recaptcha.get("error") : null);
		} catch (Throwable t) {
			LOG.error("validate", t);
			success = false;
		}
		return success;
	}

	/**
	 * valida una request con Captcha
	 */
	public static boolean validate(HttpServletRequest request) {
		boolean success = false; 
		try {
			success = validate(request.getParameter("frc-captcha-solution"));
		} catch (Throwable t) {
			LOG.error("validate", t);
			success = false;
		}
		return success;
	}

	/**
	 * verifica la validita' di friendly captcha  
	 */
	private static Map<String, String> friendlyCaptchaSiteVerify(
			String solution
			, String secret
			, String sitekey
	) throws Exception {
		ApsSystemUtils.getLogger().debug("friendlyCaptchaSiteVerify BEGIN");
		Boolean success = null;
		List<String> errors = null;
		String status = null;
		try {
			ClientConfig config = new ClientConfig();
	        Client client = ClientBuilder.newClient(config);
	    
	        // JSON body content
	        String body = "{" 
	        		+ "\"solution\": \"" + solution + "\"" 
	        		//+ ",\"secret\": \"" + secret + "\"" 		// non viene considerato
	        		//+ ",\"sitekey\": \"" + sitekey + "\"" 	// opzionale
	        + "}";
	        
	        // POST https://api.friendlycaptcha.com/api/v1/siteverify
	        Response response = client
		    		.target(FRIENDLYCAPTCHA_URL_SITEVERIFY)
		    		.request(MediaType.APPLICATION_JSON)
		    		.accept(MediaType.APPLICATION_JSON)
		    		.post(Entity.entity(body, MediaType.APPLICATION_JSON));
	        
	        // mappa la risposta JSON
	        // {
 			//    "success": true|false,
 			//    "errors": [secret_missing, secret_invalid, ...] // optional
 			// }
			String json = response.readEntity(String.class);
			ObjectMapper om = new ObjectMapper();
			HashMap<String, Object> jsonMap = (HashMap<String, Object>) om.readValue(json, HashMap.class);
			
			if(jsonMap != null) {
				success = (Boolean)(jsonMap.get("success") != null ? jsonMap.get("success") : false);
				errors = (List<String>)(jsonMap.get("error") != null ? jsonMap.get("error") : null);
				LOG.debug("friendlyCaptchaSiteVerify json response = " + json);
	        } 
			
			if( !success ) { 
				if(errors == null) {
					status = "400, Something is wrong with your request";
				} else {
					for(String s : errors) 
						status += FRIENDLYCAPTCHA_ERRORS.get(s) + "\n";
				}
			}
			
		} catch (ProcessingException ex) {
			// NB: 
			// se il sito firendly captcha non risponde per qualche motivo (ad esempio e' giu')
			// secondo le "Verification Best practices" da https://docs.friendlycaptcha.com/#/installation
			// si assume che l'utente sia lecito e si gli si consente di continuare!!!
			if(StringUtils.isNotEmpty(solution)) {
				success = new Boolean(true);
			}
			LOG.warn("friendlyCaptchaSiteVerify() servizio captcha temporaneamente non disponibile." );
		} catch (Exception ex) {
			LOG.error("friendlyCaptchaSiteVerify() " + ex.getMessage());
		}
		
		Map<String, String> result = null;
		if(success != null || errors != null) {
			result = new HashMap<String, String>();
			result.put("success", (success != null && success.booleanValue() ? "true" : "false"));
			if(status != null) {
				result.put("error", status);
			}
		}
		
		LOG.debug("friendlyCaptchaSiteVerify END");
		return result;
	}
	
}
