package it.maggioli.eldasoft.plugins.ppcommon.aps.internalservlet.qcompiler.inc;

import it.maggioli.eldasoft.plugins.ppcommon.aps.ExceptionUtils;
import java.io.ByteArrayInputStream;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONObject;

import com.agiletec.aps.system.ApsSystemUtils;

/**
 * ...
 * 
 */
public class QuestionariUtils {

	/**
	 * prepara una risposta generica in formato JSON
	 * 
	 * "params" indica un elenco di parametri opzionali 
	 * se valorizzati i parametri sono i seguenti
	 * - target		SUCCESS, ERROR, INPUT, PORTALERROR, ...
	 * - message	messaggio della risposta JSON
	 * - filename	nome del file dell'evetnuale allegato
	 * - filedata	contenuto dell'allegato
	 * - uuid		uuid dell'allegato
	 * - form		json del questionario
	 * - warning	warning della risposta JSON
	 */
	private static String jsonResponse(IProcessPageQuestionario action, Object... params) {
		String target = (String) (params.length > 0 ? params[0] : null);
		String message = (String) (params.length > 1 ? params[1] : null);
		String filename = (String) (params.length > 2 ? params[2] : null);
		String data = (String) (params.length > 3 ? params[3] : null);
		String uuid = (String) (params.length > 4 ? params[4] : null);
		String form = (String) (params.length > 5 ? params[5] : null);
		String warning = (String) (params.length > 6 ? params[6] : null);
		boolean returnCode = com.opensymphony.xwork2.Action.SUCCESS.equals(target);
		try {
			JSONObject response = new JSONObject();
			
			response.put("returnCode", Boolean.toString(returnCode));
			
			// "logLevel" W per warnings, I per info, E per errori
			String msg = message;
			if(!returnCode) {
				response.put("logLevel", "E");
				msg = message;
			} else if(StringUtils.isNotEmpty(warning)) {
				response.put("logLevel", "W");
				msg = warning;
			} else {
				response.put("logLevel", "I");
				msg = "";	//message;	// DA RIVEDERE !!!
			}
			response.put("message", (StringUtils.isNotEmpty(msg) ? msg : ""));
			
			if(filename != null && data != null) {
				response.put("filename", (filename != null ? filename : "")); 
				response.put("filedata", (data != null ? data : ""));
			}
			
			if(uuid != null) {
				response.put("uuid", (uuid != null ? uuid : ""));
			}

			if(form != null) {
				response.put("form", (form != null ? form : ""));
			}
			
			// invia allo stream di risposta della action...
			// NB: angular riceve il json in formato UTF-8 e quando invia delle richieste al portale 
			// se invia il parametro "form" lo invia in UTF8 e lo impacchetta in base64 (quindi base64(utf8)) !!!   
			action.setFilename("response.json");
			action.setContentType("application/json"); 
			action.setInputStream( new ByteArrayInputStream(response.toString().getBytes("UTF-8")) );

			response.clear();
			response = null;
			
			target = com.opensymphony.xwork2.Action.SUCCESS;
		} catch (Exception e) {
			ApsSystemUtils.logThrowable(e, action, "createJsonResponse");
			ExceptionUtils.manageExceptionError(e, null);
			target = com.opensymphony.xwork2.Action.ERROR;
		}
		return target;
	}
	
	public static String jsonResponseDel(IProcessPageQuestionario action, String target, String message) {
		return jsonResponse(action, target, message);
	}
	
	public static String jsonResponseDownload(IProcessPageQuestionario action, String target, String message, String filename, String filedata) {
		return jsonResponse(action, target, message, filename, filedata);
	}

	public static String jsonResponseAddDoc(IProcessPageQuestionario action, String target, String message, String warning, String uuid) {
		return jsonResponse(action, target, message, null, null, uuid, null, warning);
	}
	
	public static String jsonResponseLoad(IProcessPageQuestionario action, String target, String message, String form) {
		return jsonResponse(action, target, message, null, null, null, form);
	}

	public static String jsonResponseSave(IProcessPageQuestionario action, String target, String message) {
		return jsonResponse(action, target, message);
	}

	public static String jsonResponseNewUuid(IProcessPageQuestionario action, String target, String message, String uuid) {
		return jsonResponse(action, target, message, null, null, uuid);
	}
		
	public static String jsonResponseRedirectToDGUE(IProcessPageQuestionario action, String target, String message, String url) {
		return jsonResponse(action, target, message, null, null, null, url);
	}

}
