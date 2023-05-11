package it.maggioli.eldasoft.plugins.ppsmat.aps.internalservlet.documenti;


import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.RequestContext;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.exception.ApsException;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.url.IURLManager;
import com.agiletec.aps.system.services.url.PageURL;
import com.agiletec.apsadmin.system.BaseAction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.maggioli.eldasoft.plugins.ppcommon.aps.IDownloadAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import org.apache.commons.codec.binary.Base64;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DocumentiSmatAction extends BaseAction implements SessionAware,IDownloadAction,ServletResponseAware{
	/**
	 * UID
	 */
	private static final long serialVersionUID = -4471063657228578591L;
	
	private List<DocumentSmat> documenti;
	@Validate(EParamValidation.CODICE)
	private String codice;
	protected InputStream inputStream;
	@Validate(EParamValidation.FILE_NAME)
	protected String filename;
	@Validate(EParamValidation.CONTENT_TYPE)
	protected String contentType;
	@Validate(EParamValidation.DIGIT)
	private String currentFrame;
	private HttpServletResponse response;
	private static final FileNameMap MIMETYPES = URLConnection.getFileNameMap();
	@Validate(EParamValidation.URL)
	private String urlRedirect;
	private IURLManager urlManager;
	private IPageManager pageManager;
	private Map<String, Object> session;
	
	public List<DocumentSmat> getDocumenti() {
		return documenti;
	}

	public void setUrlManager(IURLManager urlManager) {
		this.urlManager = urlManager;
	}
	
	public void setPageManager(IPageManager pageManager) {
		this.pageManager = pageManager;
	}

	public IPageManager getPageManager() {
		return this.pageManager;
	}
	
	public void setDocumenti(List<DocumentSmat> documenti) {
		this.documenti = documenti;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	public String getUrlRedirect() {
		return urlRedirect;
	}

	public void setUrlRedirect(String urlRedirect) {
		this.urlRedirect = urlRedirect;
	}

	public String detail() {
		try {
		  Client client = ClientBuilder.newClient();
		  String url = "http://smatlotus.smatorino.lan/sigi.nsf/get_allegatoSTA";
		  WebTarget webTarget = client.target(url);
		  String authorization = "maggioli:iloiggam2018";
		  String encodedAuthorization = new String (new Base64().encode(authorization.getBytes(Charset.forName("UTF-8")))).trim();
  		  Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON).header("Authorization","Basic "+encodedAuthorization);
		  Response response = invocationBuilder.post( Entity.entity("{\"codice_sta\": \""+codice+"\"}", MediaType.APPLICATION_JSON));
		  int statusCode = response.getStatus();
		  String responseObj;
		  if(statusCode != Response.Status.OK.getStatusCode()){
			  responseObj = (response.readEntity( String.class )).toString();
			  throw new ApsException("Errore nell'invoocazione del dettaglio documento" + codice + " : " + responseObj);
		  } else {
			  responseObj = (response.readEntity( String.class )).toString();
			  ObjectMapper mapper = new ObjectMapper();
			  JsonNode rootNode = mapper.readTree(responseObj);//url = rootNode.get("response_allegato").toString()
			  url = "http:////"+rootNode.get("response_allegato").textValue();;//url = "http://smatlotus.smatorino.lan/sigi.nsf/UNIDfb/8BD62D4454C5A343C1258544006F3AD6/$FILE/DTSTA0023 F.pdf";
			  webTarget = client.target(url);
			  invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON).header("Authorization","Basic "+encodedAuthorization);
			  response = invocationBuilder.get( Response.class );
			  if(statusCode != Response.Status.OK.getStatusCode()){
				  responseObj = (response.readEntity( String.class )).toString();
				  throw new ApsException("Errore nell'invoocazione del dettaglio documento" + codice + " : " + responseObj);
			  } else {
				  byte[] bytes = (response.readEntity( byte[].class ));
				  String[] split = url.split("\\.");
				  this.filename = codice + "." + split[split.length-1];
				  this.contentType = MIMETYPES.getContentTypeFor(this.filename);
				  this.inputStream = new ByteArrayInputStream(bytes);
			  }
		  }
		} catch(Exception ex){
			this.addActionError(this.getText("Errors.documentismat.configuration"));
			this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
			ApsSystemUtils.logThrowable(ex, null, "open");
			this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
			this.urlRedirect = "ciao";
			return INPUT;
		}
		return SUCCESS;
	}
	
	public String view()  {
		List<DocumentSmat> result = new ArrayList<DocumentSmat>(); 
		try {
			  Client client = ClientBuilder.newClient();
			  WebTarget webTarget = client.target("http://smatlotus.smatorino.lan/sigi.nsf/get_elencoDocumenti");
			  String authorization = "maggioli:iloiggam2018";
			  String encodedAuthorization = new String (new Base64().encode(authorization.getBytes(Charset.forName("UTF-8")))).trim();
	  		  Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON).header("Authorization","Basic "+encodedAuthorization);
			  Response response = invocationBuilder.get( Response.class );
			  int statusCode = response.getStatus();
			  
			  if(statusCode != Response.Status.OK.getStatusCode()){
				  String responseObj = (response.readEntity( String.class )).toString();
				  throw new ApsException("Errore nell'invoocazione della lista documenti : " + responseObj);
			  } else {
				  String responseObj = (response.readEntity( String.class )).toString();
				  ObjectMapper mapper = new ObjectMapper();
				  JsonNode rootNode = mapper.readTree(responseObj);
				  List<Map> lista = mapper.readValue(rootNode.get("STA").toString(),List.class);
				  for(Map o : lista){
					  DocumentSmat doc = new DocumentSmat();
					  doc.setCodice_sta(o.get("codice_sta").toString());
					  doc.setDescrizione_sta(o.get("descrizione_sta").toString());
					  result.add(doc);
				  }
				  this.documenti = result;
			  }
		} catch(Exception ex){
			this.addActionError(this.getText("Errors.documentismat.configuration"));
			this.getRequest().getSession().setAttribute("ACTION_OBJECT", this);
			ApsSystemUtils.logThrowable(ex, null, "open");

		}
		return SUCCESS;
	}

	public void setUrlPage(String urlPage) {
	}

	public String getCurrentFrame() {
		return currentFrame;
	}

	public void setCurrentFrame(String currentFrame) {
		this.currentFrame = currentFrame;
	}

	
	public String getUrlErrori() {
		HttpServletRequest request = this.getRequest(); 
		RequestContext reqCtx = new RequestContext();
		reqCtx.setRequest(request);

		reqCtx.setResponse(response);
		Lang currentLang = this.getLangManager().getDefaultLang();
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_LANG, getCurrentLang());
		IPageManager pageManager = this.getPageManager();
		IPage pageDest = pageManager.getPage("portalerror");
		reqCtx.addExtraParam(SystemConstants.EXTRAPAR_CURRENT_PAGE, pageDest);
		request.setAttribute(RequestContext.REQCTX, reqCtx);
		
		PageURL url = this.urlManager.createURL(reqCtx);
	
		url.addParam(RequestContext.PAR_REDIRECT_FLAG, "1");
		return url.getURL();
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

}
