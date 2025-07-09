package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.garetel;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import it.eldasoft.www.WSOperazioniGenerali.FileType;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.xml.security.exceptions.Base64DecodingException;
import org.apache.xml.security.utils.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class DgueRtiAction extends DgueAction {

	private static final Logger log = LoggerFactory.getLogger(DgueRtiAction.class);

	/**
	 * UID
	 */
	private static final long serialVersionUID = 1089863153068777245L;

	public DgueRtiAction() { }

	@Override
	public String execute() {
		long start = System.currentTimeMillis();
		String method = this.getRequest().getMethod();
		logger.debug("DgueRtiAction - request method: {}", method);
		
		try {
			//La prima richiesta inviata dal browser è di tipo OPTION
			//Questa richiesta serve al browser per capire che tipo di richieste sono accettate, in questo caso
			//GET e OPTION (come specificato nell'addResponseHeader)
			super.addResponseHeader();
			if (HttpMethod.OPTIONS.equals(method))
				return SUCCESS;

			Map<String, String> properties = getValidDgueProperties(false);
			if (properties.get(DGUE_SYMKEY) == null || properties.get(DGUE_JWTKEY) == null)
				throw new Exception("Missing params with category " + DGUE_CATEGORY);
			
			JSONObject decodedData = getDecodedData(false);
			log.debug("decodedData: {}", decodedData);
			
			JSONObject infoOE = new JSONObject();
			
			FileType requestFileToSend = getDgueRequestDocument(decodedData);
			infoOE.put(JSON_DGUE_REQUEST, Base64.encode(getUnpackedFileBytes(requestFileToSend)));

			JSONObject data = new JSONObject();
			data.put(JSON_DATA, infoOE);
			json = data.toString();
			log.trace("this.json: {}", json);
		} catch(Base64DecodingException e) {
			log.error("Error on parsing data", e);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} catch(IllegalArgumentException | SignatureException | MalformedJwtException | ExpiredJwtException e) {
			log.error("RequestError", e);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} catch(Exception e) {
			log.error("GeneralError", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} finally {
			if (inputStream == null
					&& StringUtils.isNotEmpty(json))
				inputStream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
			else
				inputStream = new InputStream() {
					@Override
					public int read() {
						return -1;
					}
				};

			log.debug("DgueRtiAction - Time execution: {} ms", (System.currentTimeMillis() - start));
		}

		return SUCCESS;
	}

}
