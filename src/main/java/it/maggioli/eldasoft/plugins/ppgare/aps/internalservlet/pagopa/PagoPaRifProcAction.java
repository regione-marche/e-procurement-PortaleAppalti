package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.pagopa;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.user.UserDetails;
import com.opensymphony.xwork2.ModelDriven;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.struts2.interceptor.SessionAware;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Questa classe gestisce le azioni per il pagamento tramite PagoPA
 * @author gabriele.nencini
 *
 */
public class PagoPaRifProcAction extends EncodedDataAction implements SessionAware,ModelDriven<PagoPaRiferimentoFilterModel> {	
	/**
	 * UID
	 */
	private static final long serialVersionUID = 5570536059002396771L;
	
	private final Logger logger = ApsSystemUtils.getLogger();
	private Map<String, Object> session;
	private PagoPaManager pagoPaManager;

	@Validate
	private PagoPaRiferimentoFilterModel model;

	@Validate(EParamValidation.FILE_NAME)
	private String filename;
	private InputStream inputStream;
	private String errorDownload;
	
	/**
	 * @param pagoPaManager the pagoPaManager to set
	 */
	public void setPagoPaManager(PagoPaManager pagoPaManager) {
		this.pagoPaManager = pagoPaManager;
	}

	/**
	 * @return the errorDownload
	 */
	public String getErrorDownload() {
		return errorDownload;
	}

	/**
	 * @param errorDownload the errorDownload to set
	 */
	public void setErrorDownload(String errorDownload) {
		this.errorDownload = errorDownload;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @return the inputStream
	 */
	public InputStream getInputStream() {
		return inputStream;
	}

	public String getRiferimentoProcedura() {
		long start = System.currentTimeMillis();
		try {
			logger.debug("getRiferimentoProcedura");
			UserDetails currentUser = (UserDetails)this.session.get(SystemConstants.SESSIONPARAM_CURRENT_USER);
			logger.debug("getRiferimentoProcedura {}",currentUser);
			if(this.model==null) this.model = new PagoPaRiferimentoFilterModel();
			this.model.setUsernome(currentUser.getUsername());
			this.inputStream = pagoPaManager.getElencoRiferimenti(model);
		} catch (Exception e) {
			logger.error("Errore in getRiferimentoProcedura",e);
			JSONObject data = new JSONObject();
			data.put("data", new JSONArray());
			try {
				this.inputStream = new ByteArrayInputStream(data.toString().getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return FAILURE;
		} finally {
			logger.debug("getRiferimentoProcedura {}",(System.currentTimeMillis()-start));
		}
		return SUCCESS;
	}
	
	@Override
	public PagoPaRiferimentoFilterModel getModel() {
		return model;
	}
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

}
