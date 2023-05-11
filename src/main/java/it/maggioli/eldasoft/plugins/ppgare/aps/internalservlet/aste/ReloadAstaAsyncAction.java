package it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.aste;

import it.eldasoft.www.sil.WSAste.DettaglioAstaType;
import it.eldasoft.www.sil.WSAste.DettaglioClassificaType;
import it.maggioli.eldasoft.plugins.ppcommon.aps.EncodedDataAction;
import it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.customconfig.IAppParamManager;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.EParamValidation;
import it.maggioli.eldasoft.plugins.ppgare.aps.internalservlet.validation.Validate;
import it.maggioli.eldasoft.plugins.ppgare.aps.system.services.aste.IAsteManager;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.services.user.UserDetails;
import com.opensymphony.xwork2.Action;
//import com.opensymphony.xwork2.ActionSupport;

import net.sf.json.JSONObject;


public class ReloadAstaAsyncAction extends EncodedDataAction {
	/**
	 * UID
	 */
	private static final long serialVersionUID = 9174584071678921983L;
	
	private IAppParamManager appParamManager;
	private IAsteManager asteManager;
	
	@Validate(EParamValidation.CODICE)
	private String codice;	
	@Validate(EParamValidation.CODICE)
	private String codiceLotto;
	private DettaglioAstaType asta;
	private List<DettaglioClassificaType> classifica;
	private InputStream inputStream;		// json response...

	public IAppParamManager getAppParamManager() {
		return appParamManager;
	}

	public void setAppParamManager(IAppParamManager appParamManager) {
		this.appParamManager = appParamManager;
	}

	public IAsteManager getAsteManager() {
		return asteManager;
	}

	public void setAsteManager(IAsteManager asteManager) {
		this.asteManager = asteManager;
	}

	public String getCodice() {
		return codice;
	}
	
	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getCodiceLotto() {
		return codiceLotto;
	}

	public void setCodiceLotto(String codiceLotto) {
		this.codiceLotto = codiceLotto;
	}

	public DettaglioAstaType getAsta() {
		return asta;
	}

	public void setAsta(DettaglioAstaType asta) {
		this.asta = asta;
	}

	public List<DettaglioClassificaType> getClassifica() {
		return classifica;
	}

	public void setClassifica(List<DettaglioClassificaType> classifica) {
		this.classifica = classifica;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}


	/**
	 * ... 
	 */
	public String reload() {
		String target = Action.SUCCESS;
		
		this.asta = null;
		this.classifica = null;
				
		try {				
			UserDetails userDetails = this.getCurrentUser();
			if (null != userDetails
				&& !userDetails.getUsername().equals(SystemConstants.GUEST_USER_NAME)) {
				
				String codiceGara = (this.codiceLotto != null && !this.codiceLotto.isEmpty() 
		 	 			             ? this.codiceLotto : this.codice);

				// rileggi i dati dell'asta e della classifica...
				DettaglioAstaType asta = this.asteManager
					.getDettaglioAsta(codiceGara, userDetails.getUsername());
				
				List<DettaglioClassificaType> classifica = this.asteManager
					.getClassifica(asta.getTipoClassifica(), 
								   codice, 
								   codiceLotto, 
								   userDetails.getUsername(), 
								   asta.getFase().toString());
				
				this.setAsta(asta);
				this.setClassifica(classifica);
				
				// prepara la risposta in formato JSON...
				//
				JSONObject json = new JSONObject();
				json.put("asta", (this.asta != null ? this.asta : ""));
				json.put("classifica", (this.classifica != null ? this.classifica : ""));
	
				// ...ed invia la risposta della action in uno stream...
				this.setInputStream( new ByteArrayInputStream(json.toString().getBytes("UTF-8")) );
				
				json.clear();
				json = null;				
			}
		} catch (Throwable e) {
			ApsSystemUtils.logThrowable(e, this, "reload");
			target = Action.INPUT;
		}					
		return target;
	}

}


